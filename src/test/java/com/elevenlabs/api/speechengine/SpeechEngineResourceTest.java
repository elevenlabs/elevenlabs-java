package com.elevenlabs.api.speechengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elevenlabs.api.ElevenLabsClient;
import com.elevenlabs.api.ElevenlabsApiClient;
import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.resources.speechengine.SpeechEngineClient;
import com.elevenlabs.api.resources.speechengine.requests.CreateSpeechEngineRequest;
import com.elevenlabs.api.resources.speechengine.requests.UpdateSpeechEngineRequest;
import com.elevenlabs.api.types.AgentCallLimits;
import com.elevenlabs.api.types.AgentMetadataDbModel;
import com.elevenlabs.api.types.AsrConversationalConfig;
import com.elevenlabs.api.types.BaseTurnConfig;
import com.elevenlabs.api.types.ConversationConfigOutput;
import com.elevenlabs.api.types.PrivacyConfigOutput;
import com.elevenlabs.api.types.SpeechEngineAuthConfig;
import com.elevenlabs.api.types.SpeechEngineConfig;
import com.elevenlabs.api.types.SpeechEngineConversationInitiationClientDataConfig;
import com.elevenlabs.api.types.SpeechEngineResponse;
import com.elevenlabs.api.types.TtsConversationalConfigOutput;
import com.elevenlabs.api.types.VadConfig;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The wiring: ElevenLabsClient.speechEngine() returns resources built from the real API path. */
class SpeechEngineResourceTest {

  private MockWebServer server;
  private ElevenLabsClient client;

  @BeforeEach
  void setUp() throws Exception {
    server = new MockWebServer();
    server.start();
    client = ElevenLabsClient.builder()
      .apiKey(TestTokens.API_KEY)
      .url(server.url("/").toString())
      .maxRetries(0)
      .build();
  }

  @AfterEach
  void tearDown() throws Exception {
    client.close();
    server.shutdown();
  }

  static SpeechEngineResponse response(String id) {
    return SpeechEngineResponse.builder()
      .speechEngineId(id)
      .name("My engine")
      .speechEngine(SpeechEngineConfig.builder().wsUrl("wss://example.com/ws").build())
      .asr(AsrConversationalConfig.builder().build())
      .tts(TtsConversationalConfigOutput.builder().build())
      .turn(BaseTurnConfig.builder().build())
      .vad(VadConfig.builder().build())
      .conversation(ConversationConfigOutput.builder().build())
      .privacy(PrivacyConfigOutput.builder().build())
      .callLimits(AgentCallLimits.builder().build())
      .auth(SpeechEngineAuthConfig.builder().build())
      .language("en")
      .cascadeTimeoutSeconds(5d)
      .overrides(SpeechEngineConversationInitiationClientDataConfig.builder().build())
      .metadata(AgentMetadataDbModel.builder().createdAtUnixSecs(1).updatedAtUnixSecs(1).build())
      .build();
  }

  private void enqueue(String id) throws Exception {
    server.enqueue(
      new MockResponse()
        .setResponseCode(200)
        .setBody(ObjectMappers.JSON_MAPPER.writeValueAsString(response(id)))
        .addHeader("Content-Type", "application/json")
    );
  }

  @Test
  void builderReturnsTheWiredClient() {
    ElevenLabsClient built = ElevenLabsClient.builder().apiKey("x").timeout(30).build();
    assertTrue(built.speechEngine() instanceof SpeechEngineClientWrapper);
    // Still usable everywhere the generated types are expected.
    ElevenlabsApiClient generated = built;
    SpeechEngineClient generatedSpeechEngine = generated.speechEngine();
    assertSame(built.speechEngine(), generatedSpeechEngine);
    built.close();
  }

  @Test
  void createReturnsAResourceCarryingTheApiKey() throws Exception {
    enqueue("seng_created");

    SpeechEngineResource engine = client
      .speechEngine()
      .create(
        CreateSpeechEngineRequest.builder()
          .speechEngine(SpeechEngineConfig.builder().wsUrl("wss://example.com/ws").build())
          .name("My engine")
          .build()
      );

    RecordedRequest request = server.takeRequest();
    assertEquals("POST", request.getMethod());
    assertEquals("/v1/speech-engine", request.getPath());
    assertEquals(TestTokens.API_KEY, request.getHeader("xi-api-key"));

    assertEquals("seng_created", engine.getEngineId());
    assertEquals("seng_created", engine.getSpeechEngineId());
    assertEquals("My engine", engine.getName());
    assertTrue(engine.verifyRequestHeader(TestTokens.TOKEN));
  }

  @Test
  void getUpdateAndDuplicateReturnResources() throws Exception {
    enqueue("seng_1");
    enqueue("seng_1");
    enqueue("seng_copy");

    SpeechEngineResource fetched = client.speechEngine().get("seng_1");
    SpeechEngineResource updated = client
      .speechEngine()
      .update("seng_1", UpdateSpeechEngineRequest.builder().name("Renamed").build());
    SpeechEngineResource copy = client.speechEngine().duplicate("seng_1");

    assertEquals("/v1/speech-engine/seng_1", server.takeRequest().getPath());
    RecordedRequest update = server.takeRequest();
    assertEquals("PATCH", update.getMethod());
    assertEquals("Renamed", ObjectMappers.JSON_MAPPER.readTree(update.getBody().readUtf8()).get("name").asText());
    assertEquals("/v1/speech-engine/seng_1/duplicate", server.takeRequest().getPath());

    assertEquals("seng_1", fetched.getEngineId());
    assertEquals("seng_1", updated.getEngineId());
    assertEquals("seng_copy", copy.getEngineId());
    assertTrue(copy.verifyRequestHeader(TestTokens.TOKEN));
  }

  @Test
  void codeWrittenAgainstTheGeneratedClientStillCompilesAndWorks() throws Exception {
    enqueue("seng_1");
    SpeechEngineClient generated = client.speechEngine();
    SpeechEngineResponse response = generated.get("seng_1");
    // The runtime value is the resource, so the server helpers are one cast away.
    assertTrue(response instanceof SpeechEngineResource);
  }

  @Test
  void setBaseUrlReachesTheSpeechEngineClient() throws Exception {
    MockWebServer other = new MockWebServer();
    other.start();
    try {
      other.enqueue(
        new MockResponse()
          .setBody(ObjectMappers.JSON_MAPPER.writeValueAsString(response("seng_moved")))
          .addHeader("Content-Type", "application/json")
      );
      client.setBaseUrl(other.url("/").toString());
      assertEquals("seng_moved", client.speechEngine().get("seng_moved").getEngineId());
      assertEquals(1, other.getRequestCount());
      assertEquals(0, server.getRequestCount());
    } finally {
      other.shutdown();
    }
  }

  @Test
  void resourceSerialisesLikeTheResponse() throws Exception {
    SpeechEngineResource engine = new SpeechEngineResource(response("seng_1"), TestTokens.API_KEY);
    JsonNode json = ObjectMappers.JSON_MAPPER.readTree(engine.toString());
    assertEquals("seng_1", json.get("speech_engine_id").asText());
    assertFalse(json.has("engine_id"));
    assertFalse(json.has("engineId"));
    assertEquals(response("seng_1"), engine);
  }

  @Test
  void resourceKeepsUnknownResponseFields() throws Exception {
    SpeechEngineResponse response = SpeechEngineResponse.builder()
      .from(response("seng_1"))
      .additionalProperty("future_field", "kept")
      .build();
    SpeechEngineResource engine = new SpeechEngineResource(response, TestTokens.API_KEY);
    assertEquals("kept", engine.getAdditionalProperties().get("future_field"));
    JsonNode json = ObjectMappers.JSON_MAPPER.readTree(engine.toString());
    assertEquals("kept", json.get("future_field").asText());
  }

  @Test
  void verifyRequestMatchesTheHeaderCaseInsensitively() {
    SpeechEngineResource engine = new SpeechEngineResource(response("seng_1"), TestTokens.API_KEY);

    Map<String, Object> headers = new HashMap<>();
    headers.put("x-elevenlabs-speech-engine-authorization", TestTokens.TOKEN);
    assertTrue(engine.verifyRequest(headers));

    Map<String, List<String>> listHeaders = new HashMap<>();
    listHeaders.put(
      "X-ElevenLabs-Speech-Engine-Authorization",
      Collections.singletonList("Bearer " + TestTokens.TOKEN)
    );
    assertTrue(engine.verifyRequest(listHeaders));

    assertFalse(engine.verifyRequest(new HashMap<String, String>()));
    headers.put("x-elevenlabs-speech-engine-authorization", "invalid");
    assertFalse(engine.verifyRequest(headers));

    SpeechEngineResource wrongKey = new SpeechEngineResource(response("seng_1"), "sk_other");
    assertFalse(wrongKey.verifyRequestHeader(TestTokens.TOKEN));
  }

  @Test
  void createSessionDrivesACustomTransport() throws Exception {
    SpeechEngineResource engine = new SpeechEngineResource(response("seng_1"), TestTokens.API_KEY);
    List<String> sent = new ArrayList<>();
    SpeechEngineSession session = engine.createSession(sent::add, null);
    session.handleRawMessage("{\"type\":\"ping\"}");
    assertEquals("pong", ObjectMappers.JSON_MAPPER.readTree(sent.get(0)).get("type").asText());
    assertNotNull(engine.server(0, "/ws", null));
  }
}
