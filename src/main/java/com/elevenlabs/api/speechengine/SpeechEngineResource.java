package com.elevenlabs.api.speechengine;

import com.elevenlabs.api.types.SpeechEngineResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A Speech Engine returned by {@code client.speechEngine().create/get/update/duplicate} on
 * {@link com.elevenlabs.api.ElevenLabsClient}. It is the API response (every getter of
 * {@link SpeechEngineResponse} works) plus the means to host the engine's WebSocket server, with the
 * client's API key carried along for verifying inbound connections.
 *
 * <pre>{@code
 * SpeechEngineResource engine = client.speechEngine().create(request);
 * engine.serve(3001, "/ws", new SpeechEngineCallbacks()
 *   .onTranscript((transcript, signal, session) -> session.sendResponse(llm.reply(transcript))));
 * }</pre>
 *
 * <p>Use {@link #serve} or {@link #start} to run a standalone server, {@link SpeechEngineJakarta}
 * to attach to an existing servlet container, or {@link #verifyRequest} plus
 * {@link #createSession} to drive a connection you manage yourself.
 *
 * <p>Hand-written (see {@code .fernignore}), mirroring {@code SpeechEngineResource} in the other
 * SDKs.
 */
public class SpeechEngineResource extends SpeechEngineResponse {

  private final String apiKey;

  /**
   * @param response the API response describing the engine
   * @param apiKey the account API key used to verify inbound connections; null falls back to the
   *     {@code ELEVENLABS_API_KEY} environment variable when a server starts
   */
  public SpeechEngineResource(SpeechEngineResponse response, String apiKey) {
    // The generated from() skips additionalProperties, so copy them too: unknown fields in the API
    // response stay on the resource.
    super(
        SpeechEngineResponse.builder()
            .from(response)
            .additionalProperties(response.getAdditionalProperties()));
    this.apiKey = apiKey;
  }

  /** The speech engine ID; the same as {@link #getSpeechEngineId()}. */
  @JsonIgnore
  public String getEngineId() {
    return getSpeechEngineId();
  }

  /**
   * Verifies that an incoming request carries a valid Speech Engine authorization JWT. Header names
   * are matched case-insensitively; a value may be a {@code String} or a collection of strings (the
   * first is used). Returns false, never throws, when the key is missing, the header is absent, or
   * the token is invalid. Only needed when you handle the WebSocket upgrade yourself.
   */
  public boolean verifyRequest(Map<String, ?> headers) {
    if (headers == null) {
      return false;
    }
    for (Map.Entry<String, ?> entry : headers.entrySet()) {
      if (SpeechEngineJwtVerifier.AUTHORIZATION_HEADER.equalsIgnoreCase(entry.getKey())) {
        Object value = entry.getValue();
        if (value instanceof Collection) {
          Collection<?> values = (Collection<?>) value;
          value = values.isEmpty() ? null : values.iterator().next();
        }
        return value instanceof String && verifyRequestHeader((String) value);
      }
    }
    return false;
  }

  /**
   * Verifies an {@code X-ElevenLabs-Speech-Engine-Authorization} header value. Returns false, never
   * throws, when the key is missing, the value is empty, or the token is invalid.
   */
  public boolean verifyRequestHeader(String headerValue) {
    return SpeechEngineJwtVerifier.isValid(headerValue, SpeechEngineServer.resolveApiKey(apiKey));
  }

  /**
   * Creates a session over a connection you manage yourself (any transport).
   *
   * @param send writes a text frame to the connection
   * @param close closes the connection; may be null
   */
  public SpeechEngineSession createSession(Consumer<String> send, Runnable close) {
    return new SpeechEngineSession(send, close, false);
  }

  /** Like {@link #createSession(Consumer, Runnable)}, with the callbacks registered on it. */
  public SpeechEngineSession createSession(
    Consumer<String> send,
    Runnable close,
    SpeechEngineCallbacks callbacks
  ) {
    return (callbacks != null ? callbacks : new SpeechEngineCallbacks()).newSession(send, close);
  }

  /** A standalone server for this engine, not yet started. */
  public SpeechEngineServer server(int port, String path, SpeechEngineCallbacks callbacks) {
    return new SpeechEngineServer(port, path, apiKey, callbacks);
  }

  /** Starts a standalone server for this engine and returns it once it is listening. */
  public SpeechEngineServer start(int port, String path, SpeechEngineCallbacks callbacks) {
    return server(port, path, callbacks).start();
  }

  /** Runs a standalone server for this engine, blocking until it is stopped. */
  public void serve(int port, String path, SpeechEngineCallbacks callbacks)
    throws InterruptedException {
    server(port, path, callbacks).serve();
  }

  String apiKey() {
    return apiKey;
  }
}
