package com.elevenlabs.api.speechengine;

import com.elevenlabs.api.core.ApiConfig;
import com.elevenlabs.api.core.ClientOptions;
import com.elevenlabs.api.core.RequestOptions;
import com.elevenlabs.api.resources.speechengine.SpeechEngineClient;
import com.elevenlabs.api.resources.speechengine.requests.CreateSpeechEngineRequest;
import com.elevenlabs.api.resources.speechengine.requests.GetSpeechEngineRequest;
import com.elevenlabs.api.resources.speechengine.requests.UpdateSpeechEngineRequest;
import com.elevenlabs.api.types.DuplicateSpeechEngineRequest;
import com.elevenlabs.api.types.SpeechEngineResponse;
import okhttp3.OkHttpClient;

/**
 * The Speech Engine client returned by {@link com.elevenlabs.api.ElevenLabsClient#speechEngine()}.
 *
 * <p>Extends the generated {@link SpeechEngineClient}: {@code list}, {@code delete},
 * {@code withRawResponse} and the upstream WebSocket are inherited unchanged, while
 * {@code create}, {@code get}, {@code update} and {@code duplicate} are overridden to return a
 * {@link SpeechEngineResource}, which is still a {@link SpeechEngineResponse}, so code written
 * against the generated client keeps compiling.
 *
 * <p>Hand-written (see {@code .fernignore}), mirroring {@code SpeechEngineClientWrapper} in the
 * TypeScript SDK.
 */
public class SpeechEngineClientWrapper extends SpeechEngineClient {

  private final String apiKey;

  public SpeechEngineClientWrapper(
    OkHttpClient httpClient,
    ApiConfig config,
    ClientOptions clientOptions,
    String apiKey
  ) {
    super(httpClient, config, clientOptions);
    this.apiKey = apiKey;
  }

  // --- create ---------------------------------------------------------------------------------

  @Override
  public SpeechEngineResource create(CreateSpeechEngineRequest request) {
    return create(request, null);
  }

  @Override
  public SpeechEngineResource create(
    CreateSpeechEngineRequest request,
    RequestOptions requestOptions
  ) {
    return wrap(super.create(request, requestOptions));
  }

  // --- get ------------------------------------------------------------------------------------

  @Override
  public SpeechEngineResource get(String speechEngineId) {
    return get(speechEngineId, GetSpeechEngineRequest.builder().build(), null);
  }

  @Override
  public SpeechEngineResource get(String speechEngineId, RequestOptions requestOptions) {
    return get(speechEngineId, GetSpeechEngineRequest.builder().build(), requestOptions);
  }

  @Override
  public SpeechEngineResource get(String speechEngineId, GetSpeechEngineRequest request) {
    return get(speechEngineId, request, null);
  }

  @Override
  public SpeechEngineResource get(
    String speechEngineId,
    GetSpeechEngineRequest request,
    RequestOptions requestOptions
  ) {
    return wrap(super.get(speechEngineId, request, requestOptions));
  }

  // --- update ---------------------------------------------------------------------------------

  @Override
  public SpeechEngineResource update(String speechEngineId) {
    return update(speechEngineId, UpdateSpeechEngineRequest.builder().build(), null);
  }

  @Override
  public SpeechEngineResource update(String speechEngineId, RequestOptions requestOptions) {
    return update(speechEngineId, UpdateSpeechEngineRequest.builder().build(), requestOptions);
  }

  @Override
  public SpeechEngineResource update(String speechEngineId, UpdateSpeechEngineRequest request) {
    return update(speechEngineId, request, null);
  }

  @Override
  public SpeechEngineResource update(
    String speechEngineId,
    UpdateSpeechEngineRequest request,
    RequestOptions requestOptions
  ) {
    return wrap(super.update(speechEngineId, request, requestOptions));
  }

  // --- duplicate ------------------------------------------------------------------------------

  @Override
  public SpeechEngineResource duplicate(String speechEngineId) {
    return duplicate(speechEngineId, DuplicateSpeechEngineRequest.builder().build(), null);
  }

  @Override
  public SpeechEngineResource duplicate(String speechEngineId, RequestOptions requestOptions) {
    return duplicate(
      speechEngineId,
      DuplicateSpeechEngineRequest.builder().build(),
      requestOptions
    );
  }

  @Override
  public SpeechEngineResource duplicate(
    String speechEngineId,
    DuplicateSpeechEngineRequest request
  ) {
    return duplicate(speechEngineId, request, null);
  }

  @Override
  public SpeechEngineResource duplicate(
    String speechEngineId,
    DuplicateSpeechEngineRequest request,
    RequestOptions requestOptions
  ) {
    return wrap(super.duplicate(speechEngineId, request, requestOptions));
  }

  // --- server shortcuts (no API call) ---------------------------------------------------------

  /**
   * A standalone Speech Engine server verified with this client's API key, not yet started. No API
   * call is made; use it when the engine already exists.
   */
  public SpeechEngineServer server(int port, String path, SpeechEngineCallbacks callbacks) {
    return new SpeechEngineServer(port, path, apiKey, callbacks);
  }

  /**
   * Verifies an {@code X-ElevenLabs-Speech-Engine-Authorization} header value against this client's
   * API key. Returns false, never throws.
   */
  public boolean verifyRequestHeader(String headerValue) {
    return SpeechEngineJwtVerifier.isValid(headerValue, SpeechEngineServer.resolveApiKey(apiKey));
  }

  String apiKey() {
    return apiKey;
  }

  private SpeechEngineResource wrap(SpeechEngineResponse response) {
    return new SpeechEngineResource(response, apiKey);
  }
}
