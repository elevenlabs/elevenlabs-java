package com.elevenlabs.api.speechengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.Response;
import org.apache.catalina.Context;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.websocket.server.WsSci;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** End to end inside a real servlet container: embedded Tomcat with the Jakarta integration. */
class SpeechEngineJakartaTest {

  private Tomcat tomcat;

  @AfterEach
  void tearDown() throws Exception {
    if (tomcat != null) {
      tomcat.stop();
      tomcat.destroy();
    }
  }

  private int startTomcat(String apiKey, SpeechEngineCallbacks callbacks) throws Exception {
    File base = Files.createTempDirectory("speech-engine-tomcat").toFile();
    tomcat = new Tomcat();
    tomcat.setBaseDir(base.getAbsolutePath());
    tomcat.setPort(0);
    tomcat.getConnector();
    Context context = tomcat.addContext("", base.getAbsolutePath());
    // Stands in for the application's own servlet (Spring's DispatcherServlet, say).
    Tomcat.addServlet(context, "default", new DefaultServlet());
    context.addServletMappingDecoded("/", "default");
    context.addServletContainerInitializer(new WsSci(), null);
    context.addServletContainerInitializer(
      (classes, servletContext) ->
        SpeechEngineJakarta.attach(servletContext, apiKey, "/speech-engine/ws", callbacks),
      null
    );
    tomcat.start();
    return tomcat.getConnector().getLocalPort();
  }

  @Test
  void verifiedConnectionRunsAConversation() throws Exception {
    CompletableFuture<String> initialized = new CompletableFuture<>();
    CompletableFuture<Void> disconnected = new CompletableFuture<>();
    int port = startTomcat(
      TestTokens.API_KEY,
      new SpeechEngineCallbacks()
        .onInit((id, session) -> initialized.complete(id))
        .onTranscript((transcript, signal, session) ->
          session.sendResponse(java.util.Arrays.asList("Hello ", "from ", "Tomcat"))
        )
        .onDisconnect(session -> disconnected.complete(null))
    );

    WebSocketTestClient api = new WebSocketTestClient(
      "ws://127.0.0.1:" + port + "/speech-engine/ws",
      TestTokens.TOKEN
    );
    api.opened.get(10, TimeUnit.SECONDS);

    api.send("{\"type\":\"init\",\"conversation_id\":\"conv_7\"}");
    assertEquals("conv_7", initialized.get(10, TimeUnit.SECONDS));

    api.send("{\"type\":\"ping\"}");
    assertEquals("pong", api.next().get("type").asText());

    api.send(
      "{\"type\":\"user_transcript\",\"event_id\":3," +
      "\"user_transcript\":[{\"role\":\"user\",\"content\":\"Hi\"}]}"
    );
    StringBuilder spoken = new StringBuilder();
    JsonNode frame;
    do {
      frame = api.next();
      assertEquals(3, frame.get("event_id").asLong());
      spoken.append(frame.get("content").asText());
    } while (!frame.get("is_final").asBoolean());
    assertEquals("Hello from Tomcat", spoken.toString());

    api.close();
    disconnected.get(10, TimeUnit.SECONDS);
  }

  @Test
  void aTranscriptLargerThanTomcatsTextBufferIsReceived() throws Exception {
    // Tomcat's default text buffer is 8 KB; a long conversation is far bigger.
    StringBuilder content = new StringBuilder();
    for (int i = 0; i < 20000; i++) {
      content.append("word ");
    }
    CompletableFuture<String> received = new CompletableFuture<>();
    int port = startTomcat(
      TestTokens.API_KEY,
      new SpeechEngineCallbacks()
        .onTranscript((transcript, signal, session) -> {
          received.complete(transcript.get(0).getContent());
          session.sendResponse("ok");
        })
    );

    WebSocketTestClient api = new WebSocketTestClient(
      "ws://127.0.0.1:" + port + "/speech-engine/ws",
      TestTokens.TOKEN
    );
    api.opened.get(10, TimeUnit.SECONDS);
    api.send(
      "{\"type\":\"user_transcript\",\"event_id\":1," +
      "\"user_transcript\":[{\"role\":\"user\",\"content\":\"" + content + "\"}]}"
    );
    assertEquals(content.toString(), received.get(10, TimeUnit.SECONDS));
    assertEquals("ok", api.next().get("content").asText());
    api.close();
  }

  @Test
  void unverifiedRequestIsAnswered401BeforeTheUpgrade() throws Exception {
    CompletableFuture<Void> opened = new CompletableFuture<>();
    int port = startTomcat(
      TestTokens.API_KEY,
      new SpeechEngineCallbacks().onInit((id, s) -> opened.complete(null))
    );
    String url = "ws://127.0.0.1:" + port + "/speech-engine/ws";

    for (String token : new String[] { null, "not-a-jwt", "Bearer " + TestTokens.TOKEN + "x" }) {
      WebSocketTestClient api = new WebSocketTestClient(url, token);
      Response response = api.failed.get(10, TimeUnit.SECONDS);
      assertNotNull(response);
      assertEquals(401, response.code());
    }
    assertTrue(!opened.isDone());
  }

  @Test
  void bearerPrefixedTokenIsAccepted() throws Exception {
    int port = startTomcat(TestTokens.API_KEY, null);
    WebSocketTestClient api = new WebSocketTestClient(
      "ws://127.0.0.1:" + port + "/speech-engine/ws",
      "Bearer " + TestTokens.TOKEN
    );
    api.opened.get(10, TimeUnit.SECONDS);
    api.close();
  }
}
