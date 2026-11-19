package com.elevenlabs.api.resources.music;

import com.elevenlabs.api.ElevenlabsApiClient;
import com.elevenlabs.api.core.ApiConfig;
import com.elevenlabs.api.core.BaseService;
import com.elevenlabs.api.core.ClientOptions;
import com.elevenlabs.api.core.DefaultHeadersInterceptor;
import com.elevenlabs.api.core.ElevenlabsApiException;
import com.elevenlabs.api.core.Environment;
import com.elevenlabs.api.core.HttpMethod;
import com.elevenlabs.api.core.Logger;
import com.elevenlabs.api.core.LoggingInterceptor;
import com.elevenlabs.api.core.RequestBuilder;
import com.elevenlabs.api.core.RequestConfig;
import com.elevenlabs.api.core.RequestOptions;
import com.elevenlabs.api.core.RetryInterceptor;
import com.elevenlabs.api.errors.UnprocessableEntityError;
import com.elevenlabs.api.resources.music.requests.BodyComposeMusicWithADetailedResponseV1MusicDetailedPost;
import com.elevenlabs.api.types.HttpValidationError;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Compose a song and get its metadata and audio file as separate fields.
 *
 * <pre>{@code
 * MusicDetailed music = new MusicDetailed(client);
 * ComposeDetailedResponse song = music.composeDetailed(
 *   BodyComposeMusicWithADetailedResponseV1MusicDetailedPost.builder()
 *     .prompt("Epic orchestral music with dramatic strings")
 *     .musicLengthMs(60000)
 *     .build()
 * );
 * System.out.println(song.getJson().path("song_metadata").path("title").asText());
 * Files.write(Paths.get(song.getFilename()), song.getAudio());
 * }</pre>
 *
 * <p>The API answers {@code POST /v1/music/detailed} with a {@code multipart/mixed} body (a JSON part,
 * then the audio). The generated {@code client.music().composeDetailed} decodes that body as JSON,
 * which fails: it logs the parse error at {@code SEVERE} and returns {@code null}. This sends the same request, built exactly as the generated method builds it, through
 * the same HTTP stack (base URL, credentials, retries, timeout and error handling) and splits the
 * parts.
 *
 * <p>Hand-written (see {@code .fernignore}). The generated method can't be changed to return a
 * different type without editing generated code, so this is a separate entry point rather than a
 * method on {@code client.music()}. Per-call {@link RequestOptions} apply; configuration set with
 * {@code client.music().setRequestOptions} or {@code setComposeDetailedConfig} does not. Mirrors
 * {@code music.compose_detailed} in the Python SDK.
 */
public class MusicDetailed {

  private static final String PATH = "v1/music/detailed";

  private final BaseService baseService;

  /**
   * Uses the root client's configuration (base URL, credentials, retries, timeout) and its OkHttp
   * client, as {@code client.music()} does.
   *
   * @param client A root client built with {@link ElevenlabsApiClient#builder()} or from {@link
   *     ClientOptions}. For one constructed from an {@link ApiConfig}, use {@link
   *     #MusicDetailed(ApiConfig)} with that config.
   */
  public MusicDetailed(ElevenlabsApiClient client) {
    this(configOf(client));
  }

  /** For a root client constructed with {@code new ElevenlabsApiClient(config)}: pass the same config. */
  public MusicDetailed(ApiConfig config) {
    Objects.requireNonNull(config, "config");
    this.baseService = new BaseService(httpClientFor(config), config);
    this.baseService.addErrorMapping(
        422,
        HttpValidationError.class,
        (message, code, body, rawResponse) ->
          new UnprocessableEntityError((HttpValidationError) body, rawResponse)
      );
  }

  /**
   * Compose a song from a prompt or a composition plan, and return its metadata and audio.
   *
   * @throws com.elevenlabs.api.core.ElevenlabsApiApiException The API returned an error status (the
   *     same exceptions as the generated method, e.g. {@link UnprocessableEntityError} for a 422).
   * @throws ElevenlabsApiException The request or the response body failed in transit, or the body
   *     isn't a complete multipart body with a JSON part and an audio part.
   */
  public ComposeDetailedResponse composeDetailed(
    BodyComposeMusicWithADetailedResponseV1MusicDetailedPost requestParameters
  ) {
    return composeDetailed(requestParameters, null);
  }

  /**
   * Compose a song from a prompt or a composition plan, and return its metadata and audio.
   *
   * @throws com.elevenlabs.api.core.ElevenlabsApiApiException The API returned an error status (the
   *     same exceptions as the generated method, e.g. {@link UnprocessableEntityError} for a 422).
   * @throws ElevenlabsApiException The request or the response body failed in transit, or the body
   *     isn't a complete multipart body with a JSON part and an audio part.
   */
  public ComposeDetailedResponse composeDetailed(
    BodyComposeMusicWithADetailedResponseV1MusicDetailedPost requestParameters,
    RequestOptions requestOptions
  ) {
    Objects.requireNonNull(requestParameters, "requestParameters");
    RequestConfig resolvedConfig = baseService.getResolvedConfig(
      null,
      requestOptions == null ? null : requestOptions.toRequestConfig()
    );
    Request request = buildRequest(requestParameters, resolvedConfig);
    // Where the generated method decodes JSON: buffer the bytes and split the parts instead. The
    // body is read on the same per-request OkHttp client, so its timeout covers a stalled body too.
    byte[] body;
    String contentType;
    Optional<String> songId;
    try (Response response = baseService.execute(request, resolvedConfig)) {
      contentType = response.header("Content-Type");
      songId = Optional.ofNullable(response.header("song-id"));
      body = response.body() == null ? new byte[0] : response.body().bytes();
    } catch (IOException e) {
      throw new ElevenlabsApiException(
        "Failed to read the music response body: " + e.getMessage(),
        e
      );
    }
    MultipartParser.Parsed parsed = MultipartParser.parse(
      body,
      MultipartParser.boundaryFrom(contentType)
    );
    return new ComposeDetailedResponse(parsed.json, parsed.audio, parsed.filename, songId);
  }

  /** Built exactly as the generated {@code MusicClient.buildComposeDetailedRequest} builds it. */
  private Request buildRequest(
    BodyComposeMusicWithADetailedResponseV1MusicDetailedPost requestParameters,
    RequestConfig resolvedConfig
  ) {
    RequestBuilder builder = new RequestBuilder(
      HttpMethod.POST,
      baseService.resolveBaseUrl(resolvedConfig, Environment.DEFAULT),
      PATH
    )
      .applyRequestConfig(resolvedConfig)
      .setOptionalQueryParameter(
        "enable_logging",
        requestParameters.getEnableLogging().orElse(null)
      )
      .setJsonContent(requestParameters);
    if (requestParameters.getOutputFormat().orElse(null) != null) {
      builder.setOptionalQueryParameter(
        "output_format",
        requestParameters.getOutputFormat().orElse(null).toString()
      );
    }
    return builder.build();
  }

  private static ApiConfig configOf(ElevenlabsApiClient client) {
    Objects.requireNonNull(client, "client");
    // The generated sub-clients keep the root's ClientOptions; it is null only for a root built
    // directly from an ApiConfig, whose config isn't reachable from here.
    ClientOptions options = client.music().clientOptions;
    if (options == null) {
      throw new IllegalArgumentException(
        "This client was constructed from an ApiConfig; use new MusicDetailed(config) with that config"
      );
    }
    return options.toSdkConfig();
  }

  /**
   * The OkHttp client the root client gives every generated sub-client: the configured one (sharing
   * its connection pool and dispatcher), with the SDK's header, retry and logging interceptors.
   * Mirrors the {@code ElevenlabsApiClient} constructor, which doesn't expose the client it builds.
   */
  private static OkHttpClient httpClientFor(ApiConfig config) {
    OkHttpClient customHttpClient = config.getHttpClient();
    OkHttpClient.Builder httpClientBuilder = (customHttpClient != null
        ? customHttpClient.newBuilder()
        : new OkHttpClient.Builder()).addInterceptor(new DefaultHeadersInterceptor(config))
      .addInterceptor(new RetryInterceptor(config.getRetryConfig()))
      .addInterceptor(new LoggingInterceptor(Logger.from(config.getLogConfig())));
    if (customHttpClient == null) {
      httpClientBuilder.readTimeout(config.getTimeout(), TimeUnit.MILLISECONDS);
    }
    return httpClientBuilder.build();
  }
}
