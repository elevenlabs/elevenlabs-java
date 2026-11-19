package com.elevenlabs.api.speechengine;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

/**
 * The Java-WebSocket server behind {@link SpeechEngineServer}. Kept in its own class so that
 * Java-WebSocket, an optional dependency, is only loaded when a server is actually started.
 *
 * <p>Hand-written (see {@code .fernignore}).
 */
final class JavaWebSocketTransport extends WebSocketServer {

  private static final long START_TIMEOUT_SECONDS = 10;

  private final String path;
  private final String apiKey;
  private final SpeechEngineCallbacks callbacks;
  private final CountDownLatch started = new CountDownLatch(1);
  private volatile Exception startFailure;

  JavaWebSocketTransport(
    InetSocketAddress address,
    String path,
    String apiKey,
    SpeechEngineCallbacks callbacks
  ) {
    super(address);
    this.path = path;
    this.apiKey = apiKey;
    this.callbacks = callbacks;
    setReuseAddr(true);
  }

  void startAndAwait() {
    start();
    boolean ready;
    try {
      ready = started.await(START_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      shutdown();
      throw new IllegalStateException("Interrupted while starting the Speech Engine server", e);
    }
    if (startFailure != null || !ready) {
      shutdown();
      throw new IllegalStateException(
        "Could not start the Speech Engine server on " + getAddress(),
        startFailure
      );
    }
  }

  void shutdown() {
    try {
      stop(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(
    WebSocket conn,
    Draft draft,
    ClientHandshake request
  ) throws InvalidDataException {
    ServerHandshakeBuilder response = super.onWebsocketHandshakeReceivedAsServer(
      conn,
      draft,
      request
    );
    String requestPath = pathOf(request.getResourceDescriptor());
    if (path != null && !path.equals(requestPath)) {
      reject("path mismatch: expected " + path + ", got " + requestPath);
    }
    if (!callbacks.isAuthDisabled()) {
      String header = request.getFieldValue(SpeechEngineJwtVerifier.AUTHORIZATION_HEADER);
      if (header == null || header.isEmpty()) {
        reject("missing " + SpeechEngineJwtVerifier.AUTHORIZATION_HEADER + " header");
      }
      try {
        SpeechEngineJwtVerifier.verify(header, apiKey);
      } catch (RuntimeException e) {
        reject(e.getMessage());
      }
    }
    log("verified connection, creating session");
    return response;
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    SpeechEngineSession session = callbacks.newSession(conn::send, conn::close);
    conn.setAttachment(session);
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    SpeechEngineSession session = conn.getAttachment();
    if (session != null) {
      session.handleRawMessage(message);
    }
  }

  @Override
  public void onMessage(WebSocket conn, ByteBuffer message) {
    // The Speech Engine protocol is text-only.
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    SpeechEngineSession session = conn.getAttachment();
    if (session != null) {
      session.handleDisconnect();
    }
  }

  @Override
  public void onError(WebSocket conn, Exception error) {
    if (conn == null) {
      // A server-level failure, such as the port being in use.
      startFailure = error;
      started.countDown();
      return;
    }
    SpeechEngineSession session = conn.getAttachment();
    if (session != null) {
      session.handleTransportError(error);
    }
  }

  @Override
  public void onStart() {
    started.countDown();
  }

  private void reject(String reason) throws InvalidDataException {
    log("rejected connection: " + reason);
    throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, reason);
  }

  private void log(String message) {
    if (callbacks.isDebug()) {
      System.err.println("[SpeechEngine] " + message);
    }
  }

  private static String pathOf(String resourceDescriptor) {
    if (resourceDescriptor == null || resourceDescriptor.isEmpty()) {
      return "/";
    }
    int query = resourceDescriptor.indexOf('?');
    return query >= 0 ? resourceDescriptor.substring(0, query) : resourceDescriptor;
  }
}
