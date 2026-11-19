package com.elevenlabs.api;

import com.elevenlabs.api.core.Environment;
import com.elevenlabs.api.core.LogConfig;
import okhttp3.OkHttpClient;

/**
 * Builder for {@link ElevenLabsClient}. Takes the same options as {@link ElevenlabsApiClientBuilder};
 * the setters are overridden only so that a chain ending in {@link #build()} keeps the
 * {@link ElevenLabsClient} type.
 *
 * <p>Hand-written (see {@code .fernignore}).
 */
public class ElevenLabsClientBuilder extends ElevenlabsApiClientBuilder {

  @Override
  public ElevenLabsClientBuilder apiKey(String apiKey) {
    super.apiKey(apiKey);
    return this;
  }

  @Override
  public ElevenLabsClientBuilder environment(Environment environment) {
    super.environment(environment);
    return this;
  }

  @Override
  public ElevenLabsClientBuilder url(String url) {
    super.url(url);
    return this;
  }

  @Override
  public ElevenLabsClientBuilder timeout(int timeout) {
    super.timeout(timeout);
    return this;
  }

  @Override
  public ElevenLabsClientBuilder maxRetries(int maxRetries) {
    super.maxRetries(maxRetries);
    return this;
  }

  @Override
  public ElevenLabsClientBuilder initialRetryDelayMillis(long initialRetryDelayMillis) {
    super.initialRetryDelayMillis(initialRetryDelayMillis);
    return this;
  }

  @Override
  public ElevenLabsClientBuilder maxRetryDelayMillis(long maxRetryDelayMillis) {
    super.maxRetryDelayMillis(maxRetryDelayMillis);
    return this;
  }

  @Override
  public ElevenLabsClientBuilder retryJitterFactor(double retryJitterFactor) {
    super.retryJitterFactor(retryJitterFactor);
    return this;
  }

  @Override
  public ElevenLabsClientBuilder httpClient(OkHttpClient httpClient) {
    super.httpClient(httpClient);
    return this;
  }

  @Override
  public ElevenLabsClientBuilder logging(LogConfig logging) {
    super.logging(logging);
    return this;
  }

  @Override
  public ElevenLabsClientBuilder addHeader(String name, String value) {
    super.addHeader(name, value);
    return this;
  }

  @Override
  public ElevenLabsClient build() {
    validateConfiguration();
    return new ElevenLabsClient(buildClientOptions());
  }
}
