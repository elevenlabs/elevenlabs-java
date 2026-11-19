package com.elevenlabs.api;

import com.elevenlabs.api.core.ApiConfig;
import com.elevenlabs.api.core.ClientOptions;
import com.elevenlabs.api.core.DefaultHeadersInterceptor;
import com.elevenlabs.api.core.Logger;
import com.elevenlabs.api.core.LoggingInterceptor;
import com.elevenlabs.api.core.RetryInterceptor;
import com.elevenlabs.api.speechengine.SpeechEngineClientWrapper;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

/**
 * The ElevenLabs client, with the hand-written helpers wired in. Everything on the generated
 * {@link ElevenlabsApiClient} works the same; {@link #speechEngine()} additionally returns a
 * {@link SpeechEngineClientWrapper}, whose {@code create}/{@code get}/{@code update}/{@code duplicate}
 * return a {@link com.elevenlabs.api.speechengine.SpeechEngineResource} that can host the engine's
 * WebSocket server.
 *
 * <pre>{@code
 * ElevenLabsClient client = ElevenLabsClient.builder().build(); // reads ELEVENLABS_API_KEY
 * SpeechEngineResource engine = client.speechEngine().get("seng_123");
 * }</pre>
 *
 * <p>Hand-written (see {@code .fernignore}): the generator emits only {@link ElevenlabsApiClient}.
 * Mirrors {@code ElevenLabsClient} in the TypeScript SDK, which wraps the generated client the same
 * way.
 */
public class ElevenLabsClient extends ElevenlabsApiClient {

  private final ApiConfig speechEngineConfig;
  private final SpeechEngineClientWrapper speechEngineClient;

  /** Constructs the client from client options; most callers use {@link #builder()}. */
  public ElevenLabsClient(ClientOptions clientOptions) {
    super(clientOptions);
    this.speechEngineConfig = clientOptions.toSdkConfig();
    this.speechEngineClient = new SpeechEngineClientWrapper(
      sdkHttpClient(speechEngineConfig),
      speechEngineConfig,
      clientOptions,
      clientOptions.headers(null).get("xi-api-key")
    );
  }

  /** Returns a builder for {@link ElevenLabsClient}. */
  public static ElevenLabsClientBuilder builder() {
    return new ElevenLabsClientBuilder();
  }

  /** Returns the Speech Engine client, whose create/get/update/duplicate return a resource. */
  @Override
  public SpeechEngineClientWrapper speechEngine() {
    return this.speechEngineClient;
  }

  @Override
  public void setBaseUrl(String baseUrl) {
    super.setBaseUrl(baseUrl);
    // The wrapper has its own config (see the constructor), so it must follow the change too.
    this.speechEngineConfig.setBaseUrl(baseUrl);
  }

  /**
   * Builds the OkHttp client the same way the generated root constructor does: the caller's
   * client (if any) is copied, and the SDK's header, retry and logging interceptors are layered on.
   * The generated constructor keeps its client in a local variable, so the wrapper cannot reuse
   * that instance; {@code newBuilder()} still shares the connection pool and dispatcher.
   */
  private static OkHttpClient sdkHttpClient(ApiConfig config) {
    OkHttpClient custom = config.getHttpClient();
    OkHttpClient.Builder builder = (custom != null ? custom.newBuilder() : new OkHttpClient.Builder())
      .addInterceptor(new DefaultHeadersInterceptor(config))
      .addInterceptor(new RetryInterceptor(config.getRetryConfig()))
      .addInterceptor(new LoggingInterceptor(Logger.from(config.getLogConfig())));
    if (custom == null) {
      builder.readTimeout(config.getTimeout(), TimeUnit.MILLISECONDS);
    }
    return builder.build();
  }
}
