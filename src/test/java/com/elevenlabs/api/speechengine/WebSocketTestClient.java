package com.elevenlabs.api.speechengine;

import com.elevenlabs.api.core.ObjectMappers;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/** Plays the ElevenLabs API side of a Speech Engine connection: it dials in and sends frames. */
final class WebSocketTestClient {

  private static final OkHttpClient HTTP = new OkHttpClient();

  final CompletableFuture<Void> opened = new CompletableFuture<>();
  /** Completes with the HTTP response of a refused handshake (null if there was none). */
  final CompletableFuture<Response> failed = new CompletableFuture<>();
  final CompletableFuture<Integer> closed = new CompletableFuture<>();
  private final BlockingQueue<String> received = new LinkedBlockingQueue<>();
  private final WebSocket socket;

  WebSocketTestClient(String url, String token) {
    Request.Builder request = new Request.Builder().url(url);
    if (token != null) {
      request.header(SpeechEngineJwtVerifier.AUTHORIZATION_HEADER, token);
    }
    socket = HTTP.newWebSocket(
      request.build(),
      new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
          opened.complete(null);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
          received.add(text);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
          webSocket.close(1000, null);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
          closed.complete(code);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
          failed.complete(response);
          opened.completeExceptionally(t);
        }
      }
    );
  }

  void send(String frame) {
    socket.send(frame);
  }

  JsonNode next() throws Exception {
    String frame = received.poll(10, TimeUnit.SECONDS);
    if (frame == null) {
      throw new AssertionError("no frame received within 10s");
    }
    return ObjectMappers.JSON_MAPPER.readTree(frame);
  }

  void close() {
    socket.close(1000, null);
  }
}
