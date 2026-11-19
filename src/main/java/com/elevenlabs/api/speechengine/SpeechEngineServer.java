package com.elevenlabs.api.speechengine;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Standalone WebSocket server that produces a {@link SpeechEngineSession} for each inbound
 * connection from the ElevenLabs Speech Engine API.
 *
 * <pre>{@code
 * SpeechEngineServer server = new SpeechEngineServer(3001, "/ws", apiKey, callbacks);
 * server.serve(); // blocks until stop() is called
 * }</pre>
 *
 * <p>Every connection is verified against the API key (the
 * {@code X-ElevenLabs-Speech-Engine-Authorization} JWT) during the WebSocket handshake, before a
 * session exists, unless {@link SpeechEngineCallbacks#disableAuth(boolean)} is set. A refused
 * handshake (wrong path, missing or invalid token) is answered with an HTTP error and never
 * upgraded.
 *
 * <p>Built on <a href="https://github.com/TooTallNate/Java-WebSocket">Java-WebSocket</a>, which is an
 * optional dependency of this SDK: add {@code org.java-websocket:Java-WebSocket} (1.5.x) to your
 * build to use this class. To serve Speech Engine from an existing servlet container instead, use
 * {@link SpeechEngineJakarta}.
 *
 * <p>Hand-written (see {@code .fernignore}), mirroring {@code SpeechEngineServer} in the other SDKs.
 */
public final class SpeechEngineServer implements AutoCloseable {

  private final int port;
  private final String path;
  private final String apiKey;
  private final SpeechEngineCallbacks callbacks;
  private final CountDownLatch stopped = new CountDownLatch(1);

  private volatile JavaWebSocketTransport transport;

  /**
   * @param port the port to listen on; 0 picks a free port (see {@link #getPort()})
   * @param path the path to accept connections on, or null to accept any path
   * @param apiKey the account API key; falls back to the {@code ELEVENLABS_API_KEY} environment
   *     variable when null or empty
   * @param callbacks handlers wired onto each session; may be null
   */
  public SpeechEngineServer(int port, String path, String apiKey, SpeechEngineCallbacks callbacks) {
    this.port = port;
    this.path = path;
    this.apiKey = apiKey;
    this.callbacks = callbacks != null ? callbacks : new SpeechEngineCallbacks();
  }

  /**
   * Starts listening and returns once the server is bound.
   *
   * @return this server
   * @throws IllegalStateException if no API key is available and authentication is not disabled,
   *     if the port cannot be bound, or if Java-WebSocket is not on the classpath
   */
  public synchronized SpeechEngineServer start() {
    if (transport != null) {
      throw new IllegalStateException("SpeechEngineServer is already started");
    }
    String key = resolveApiKey(apiKey);
    if (key == null && !callbacks.isAuthDisabled()) {
      throw new IllegalStateException(
        "SpeechEngineServer requires an API key to verify incoming connections. Pass one, or set " +
        "the ELEVENLABS_API_KEY environment variable. To run without authentication, use " +
        "disableAuth(true), but only behind an IP allowlist."
      );
    }
    if (callbacks.isAuthDisabled()) {
      System.err.println(
        "[SpeechEngine] authentication is disabled: incoming connections will NOT be verified. " +
        "Make sure the server is protected by an IP allowlist restricting traffic to ElevenLabs."
      );
    }

    JavaWebSocketTransport created;
    try {
      created = new JavaWebSocketTransport(new InetSocketAddress(port), path, key, callbacks);
    } catch (NoClassDefFoundError e) {
      throw new IllegalStateException(
        "SpeechEngineServer needs Java-WebSocket on the classpath. Add " +
        "'org.java-websocket:Java-WebSocket:1.5.7' to your build, or serve Speech Engine from an " +
        "existing servlet container with SpeechEngineJakarta.",
        e
      );
    }
    created.startAndAwait();
    transport = created;
    if (callbacks.isDebug()) {
      System.err.println("[SpeechEngine] speech engine server listening on port " + getPort());
    }
    return this;
  }

  /** Starts the server (if it is not running yet) and blocks until {@link #stop()} is called. */
  public void serve() throws InterruptedException {
    synchronized (this) {
      if (transport == null) {
        start();
      }
    }
    stopped.await();
  }

  /**
   * Blocks until the server is stopped or the timeout elapses.
   *
   * @return true if the server stopped
   */
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return stopped.await(timeout, unit);
  }

  /** The port the server is listening on: the actual port when it was created with port 0. */
  public int getPort() {
    JavaWebSocketTransport current = transport;
    return current != null ? current.getPort() : port;
  }

  /** Whether the server is listening. */
  public boolean isRunning() {
    return transport != null && stopped.getCount() > 0;
  }

  /** Stops accepting connections, closes the open ones and shuts the listener down. */
  public synchronized void stop() {
    JavaWebSocketTransport current = transport;
    if (current != null) {
      current.shutdown();
    }
    stopped.countDown();
  }

  @Override
  public void close() {
    stop();
  }

  static String resolveApiKey(String apiKey) {
    if (apiKey != null && !apiKey.trim().isEmpty()) {
      return apiKey;
    }
    String fromEnv = System.getenv("ELEVENLABS_API_KEY");
    return fromEnv != null && !fromEnv.trim().isEmpty() ? fromEnv : null;
  }
}
