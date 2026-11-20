package com.elevenlabs.api.speechengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** End to end over a real socket: the standalone Java-WebSocket server and an OkHttp client. */
class SpeechEngineServerTest {

  private SpeechEngineServer server;

  @AfterEach
  void tearDown() {
    if (server != null) {
      server.stop();
    }
  }

  private String url(String path) {
    return "ws://127.0.0.1:" + server.getPort() + path;
  }

  @Test
  void verifiedConnectionRunsAConversation() throws Exception {
    CompletableFuture<String> initialized = new CompletableFuture<>();
    CompletableFuture<List<ConversationMessage>> heard = new CompletableFuture<>();
    CompletableFuture<Void> closedByApi = new CompletableFuture<>();
    CompletableFuture<Void> disconnected = new CompletableFuture<>();
    SpeechEngineResource engine = new SpeechEngineResource(
      SpeechEngineResourceTest.response("seng_1"),
      TestTokens.API_KEY
    );
    server = engine.start(
      0,
      "/ws",
      new SpeechEngineCallbacks()
        .onInit((id, session) -> initialized.complete(id))
        .onTranscript((transcript, signal, session) -> {
          heard.complete(transcript);
          session.sendResponse("Hi from Java");
        })
        .onClose(session -> closedByApi.complete(null))
        .onDisconnect(session -> disconnected.complete(null))
    );
    assertTrue(server.isRunning());

    WebSocketTestClient api = new WebSocketTestClient(url("/ws"), TestTokens.TOKEN);
    api.opened.get(10, TimeUnit.SECONDS);

    api.send("{\"type\":\"init\",\"conversation_id\":\"conv_42\"}");
    assertEquals("conv_42", initialized.get(10, TimeUnit.SECONDS));

    api.send("{\"type\":\"ping\"}");
    assertEquals("pong", api.next().get("type").asText());

    api.send(
      "{\"type\":\"user_transcript\",\"event_id\":9," +
      "\"user_transcript\":[{\"role\":\"user\",\"content\":\"Hello\"}]}"
    );
    assertEquals("Hello", heard.get(10, TimeUnit.SECONDS).get(0).getContent());
    JsonNode first = api.next();
    assertEquals("agent_response", first.get("type").asText());
    assertEquals("Hi from Java", first.get("content").asText());
    assertEquals(9, first.get("event_id").asLong());
    assertFalse(first.get("is_final").asBoolean());
    JsonNode last = api.next();
    assertEquals("", last.get("content").asText());
    assertTrue(last.get("is_final").asBoolean());

    api.send("{\"type\":\"close\"}");
    closedByApi.get(10, TimeUnit.SECONDS);
    api.close();
    disconnected.get(10, TimeUnit.SECONDS);
  }

  @Test
  void connectionWithoutTokenIsRefusedAtTheHandshake() throws Exception {
    CompletableFuture<Void> opened = new CompletableFuture<>();
    server = new SpeechEngineServer(
      0,
      "/ws",
      TestTokens.API_KEY,
      new SpeechEngineCallbacks().onInit((id, s) -> opened.complete(null))
    ).start();

    assertRefused(new WebSocketTestClient(url("/ws"), null));
    assertRefused(new WebSocketTestClient(url("/ws"), "not-a-jwt"));
    assertFalse(opened.isDone());
  }

  @Test
  void connectionSignedWithAnotherKeyIsRefused() throws Exception {
    server = new SpeechEngineServer(0, null, "sk_someone_else", null).start();
    assertRefused(new WebSocketTestClient(url("/anything"), TestTokens.TOKEN));
  }

  @Test
  void wrongPathIsRefused() throws Exception {
    server = new SpeechEngineServer(0, "/ws", TestTokens.API_KEY, null).start();
    assertRefused(new WebSocketTestClient(url("/other"), TestTokens.TOKEN));
    // The query string is not part of the path.
    WebSocketTestClient ok = new WebSocketTestClient(url("/ws?x=1"), TestTokens.TOKEN);
    ok.opened.get(10, TimeUnit.SECONDS);
    ok.close();
  }

  @Test
  void disableAuthAcceptsUnsignedConnections() throws Exception {
    server = new SpeechEngineServer(
      0,
      "/ws",
      null,
      new SpeechEngineCallbacks().disableAuth(true)
    ).start();
    WebSocketTestClient api = new WebSocketTestClient(url("/ws"), null);
    api.opened.get(10, TimeUnit.SECONDS);
    api.send("{\"type\":\"ping\"}");
    assertEquals("pong", api.next().get("type").asText());
    api.close();
  }

  @Test
  void startWithoutAnApiKeyFails() {
    if (System.getenv("ELEVENLABS_API_KEY") != null) {
      return; // The environment fallback would supply a key.
    }
    assertThrows(IllegalStateException.class, () ->
      new SpeechEngineServer(0, "/ws", null, null).start()
    );
  }

  @Test
  void stopEndsServe() throws Exception {
    server = new SpeechEngineServer(0, "/ws", TestTokens.API_KEY, null);
    Thread serving = new Thread(() -> {
      try {
        server.serve();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });
    serving.start();
    long deadline = System.currentTimeMillis() + 10_000;
    while (!server.isRunning() && System.currentTimeMillis() < deadline) {
      Thread.sleep(20);
    }
    assertTrue(server.isRunning());
    server.stop();
    serving.join(10_000);
    assertFalse(serving.isAlive());
    assertTrue(server.awaitTermination(1, TimeUnit.SECONDS));
  }

  private static void assertRefused(WebSocketTestClient client) throws Exception {
    Response response = client.failed.get(10, TimeUnit.SECONDS);
    assertNotNull(response, "expected an HTTP response refusing the upgrade");
    // Java-WebSocket answers every refused handshake with 404.
    assertEquals(404, response.code());
    assertFalse(client.opened.isDone() && !client.opened.isCompletedExceptionally());
  }
}
