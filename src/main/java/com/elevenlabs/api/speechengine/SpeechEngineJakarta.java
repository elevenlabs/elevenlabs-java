package com.elevenlabs.api.speechengine;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.CloseReason;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Serves Speech Engine from an existing Jakarta servlet container: Tomcat 10+, Jetty 11+, Undertow,
 * or Spring Boot 3's embedded server.
 *
 * <pre>{@code
 * // In a ServletContextListener, a ServletContainerInitializer, or a Spring Boot
 * // ServletContextInitializer bean:
 * SpeechEngineResource engine = client.speechEngine().get("seng_123");
 * SpeechEngineJakarta.attach(servletContext, engine, "/speech-engine/ws", callbacks);
 * }</pre>
 *
 * <p>{@code attach} registers two things on the path: a servlet filter that verifies the
 * {@code X-ElevenLabs-Speech-Engine-Authorization} JWT and answers {@code 401} before the request
 * is upgraded, and a Jakarta WebSocket endpoint that drives a {@link SpeechEngineSession} per
 * connection. The filter is registered ahead of the container's own WebSocket upgrade filter, so an
 * unverified request never reaches the endpoint. Both must be registered while the servlet context
 * is starting.
 *
 * <p>Uses the Jakarta Servlet (5.0+) and Jakarta WebSocket (2.0+) APIs, which the container
 * provides; this SDK does not ship them. Kept apart from {@link SpeechEngineResource} so the
 * resource never references these optional types.
 *
 * <p>Hand-written (see {@code .fernignore}), the Java counterpart of {@code attach()} in the
 * TypeScript SDK and the ASP.NET Core attach helper in the .NET SDK.
 */
public final class SpeechEngineJakarta {

  /** The servlet context attribute under which containers publish the WebSocket container. */
  static final String SERVER_CONTAINER_ATTRIBUTE = "jakarta.websocket.server.ServerContainer";

  /** The largest inbound message accepted (16 Mi characters), as in the Go SDK. */
  static final int MAX_MESSAGE_CHARS = 16 * 1024 * 1024;

  private SpeechEngineJakarta() {}

  /** Serves the engine on {@code path}, verifying connections with the engine's API key. */
  public static void attach(
    ServletContext servletContext,
    SpeechEngineResource engine,
    String path,
    SpeechEngineCallbacks callbacks
  ) {
    attach(servletContext, Objects.requireNonNull(engine, "engine").apiKey(), path, callbacks);
  }

  /**
   * Serves Speech Engine on {@code path} without fetching an engine first, verifying connections
   * with the client's API key.
   */
  public static void attach(
    ServletContext servletContext,
    SpeechEngineClientWrapper speechEngine,
    String path,
    SpeechEngineCallbacks callbacks
  ) {
    attach(
      servletContext,
      Objects.requireNonNull(speechEngine, "speechEngine").apiKey(),
      path,
      callbacks
    );
  }

  /**
   * Serves Speech Engine on {@code path}, verifying connections with {@code apiKey} (falling back to
   * the {@code ELEVENLABS_API_KEY} environment variable).
   *
   * @throws IllegalStateException if no API key is available and authentication is not disabled,
   *     if the context has no WebSocket container, or if the context has already started
   */
  public static void attach(
    ServletContext servletContext,
    String apiKey,
    String path,
    SpeechEngineCallbacks callbacks
  ) {
    Objects.requireNonNull(servletContext, "servletContext");
    Objects.requireNonNull(path, "path");
    SpeechEngineCallbacks resolved = callbacks != null ? callbacks : new SpeechEngineCallbacks();
    String key = SpeechEngineServer.resolveApiKey(apiKey);

    if (resolved.isAuthDisabled()) {
      System.err.println(
        "[SpeechEngine] authentication is disabled: incoming connections will NOT be verified. " +
        "Make sure the server is protected by an IP allowlist restricting traffic to ElevenLabs."
      );
    } else {
      if (key == null) {
        throw new IllegalStateException(
          "SpeechEngineJakarta.attach requires an API key to verify incoming connections. Pass " +
          "one, or set the ELEVENLABS_API_KEY environment variable. To run without " +
          "authentication, use disableAuth(true), but only behind an IP allowlist."
        );
      }
      FilterRegistration.Dynamic filter = servletContext.addFilter(
        "elevenlabs-speech-engine-auth:" + path,
        new AuthFilter(key, resolved.isDebug())
      );
      if (filter == null) {
        throw new IllegalStateException("A Speech Engine endpoint is already attached at " + path);
      }
      // isMatchAfter = false: run before the container's own upgrade filter.
      filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, path);
    }

    Object container = servletContext.getAttribute(SERVER_CONTAINER_ATTRIBUTE);
    if (!(container instanceof ServerContainer)) {
      throw new IllegalStateException(
        "No Jakarta WebSocket container found in the servlet context. Add the container's " +
        "WebSocket module (for example tomcat-embed-websocket) to the classpath."
      );
    }
    ServerEndpointConfig config = ServerEndpointConfig.Builder.create(
      SpeechEngineEndpoint.class,
      path
    )
      .configurator(new EndpointConfigurator(resolved))
      .build();
    try {
      ((ServerContainer) container).addEndpoint(config);
    } catch (DeploymentException e) {
      throw new IllegalStateException("Could not register the Speech Engine endpoint at " + path, e);
    }
  }

  /** Answers 401 for requests without a valid Speech Engine JWT, before any upgrade. */
  static final class AuthFilter implements Filter {

    private final String apiKey;
    private final boolean debug;

    AuthFilter(String apiKey, boolean debug) {
      this.apiKey = apiKey;
      this.debug = debug;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
      if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
        chain.doFilter(request, response);
        return;
      }
      String header = ((HttpServletRequest) request).getHeader(
          SpeechEngineJwtVerifier.AUTHORIZATION_HEADER
        );
      if (header == null || header.isEmpty()) {
        reject((HttpServletResponse) response, "missing authorization header");
        return;
      }
      try {
        SpeechEngineJwtVerifier.verify(header, apiKey);
      } catch (RuntimeException e) {
        reject((HttpServletResponse) response, e.getMessage());
        return;
      }
      chain.doFilter(request, response);
    }

    private void reject(HttpServletResponse response, String reason) throws IOException {
      if (debug) {
        System.err.println("[SpeechEngine] rejected connection: " + reason);
      }
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
  }

  /** Hands the container a fresh endpoint per connection, carrying the callbacks. */
  static final class EndpointConfigurator extends ServerEndpointConfig.Configurator {

    private final SpeechEngineCallbacks callbacks;

    EndpointConfigurator(SpeechEngineCallbacks callbacks) {
      this.callbacks = callbacks;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) {
      return endpointClass.cast(new SpeechEngineEndpoint(callbacks));
    }
  }

  /**
   * One connection's endpoint: drives a {@link SpeechEngineSession}. Public only because containers
   * require endpoint classes to be; construct it through {@link #attach}.
   */
  public static final class SpeechEngineEndpoint extends Endpoint {

    private final SpeechEngineCallbacks callbacks;
    private volatile SpeechEngineSession session;

    SpeechEngineEndpoint(SpeechEngineCallbacks callbacks) {
      this.callbacks = callbacks;
    }

    @Override
    public void onOpen(Session socket, EndpointConfig config) {
      SpeechEngineSession created = callbacks.newSession(
        text -> {
          try {
            socket.getBasicRemote().sendText(text);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        },
        () -> {
          try {
            socket.close();
          } catch (IOException e) {
            // Already gone.
          }
        }
      );
      session = created;
      // Every user_transcript carries the whole conversation, so messages outgrow container text
      // buffers (8 KB in Tomcat, 64 KB in Jetty) within a few turns, and a Whole handler would be
      // closed with 1009. A Partial handler receives each message in buffer-sized pieces whatever
      // the container's limit, and the pieces are joined here, up to MAX_MESSAGE_CHARS.
      socket.addMessageHandler(
        String.class,
        new MessageHandler.Partial<String>() {
          private final StringBuilder pending = new StringBuilder();

          @Override
          public void onMessage(String part, boolean last) {
            if (pending.length() + part.length() > MAX_MESSAGE_CHARS) {
              pending.setLength(0);
              try {
                socket.close(
                  new CloseReason(CloseReason.CloseCodes.TOO_BIG, "Speech Engine message too large")
                );
              } catch (IOException e) {
                // Already gone.
              }
              return;
            }
            if (last && pending.length() == 0) {
              created.handleRawMessage(part);
              return;
            }
            pending.append(part);
            if (last) {
              String message = pending.toString();
              pending.setLength(0);
              created.handleRawMessage(message);
            }
          }
        }
      );
    }

    @Override
    public void onClose(Session socket, CloseReason reason) {
      SpeechEngineSession current = session;
      if (current != null) {
        current.handleDisconnect();
      }
    }

    @Override
    public void onError(Session socket, Throwable error) {
      SpeechEngineSession current = session;
      if (current != null) {
        current.handleTransportError(error);
      }
    }
  }
}
