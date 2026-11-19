package com.elevenlabs.api.flows.wait;

import com.elevenlabs.api.core.ElevenlabsApiHttpResponse;
import com.elevenlabs.api.core.RequestOptions;
import com.elevenlabs.api.types.MediaGenerationCreateResponse;
import com.elevenlabs.api.types.MediaGenerationCompletedResponse;
import com.elevenlabs.api.types.MediaGenerationFailedResponse;
import com.elevenlabs.api.types.MediaGenerationInProgressResponse;
import com.elevenlabs.api.types.MediaGenerationResponse;
import java.util.Map;
import java.util.function.BiFunction;

// Hand-written (listed in .fernignore). createAndWait for the image, video and text-to-speech
// Flows clients, matching create_and_wait in the Python SDK.

/**
 * {@code createAndWait} for one of {@code client.flows().image()}, {@code .video()} or
 * {@code .textToSpeech()}. Get one from {@link FlowsWaiter}.
 *
 * @param <R> The request body the generated {@code create} takes: {@code ImageGenerationRequest},
 *     {@code VideoGenerationRequest} or {@code TextToSpeechGenerationRequest}.
 */
public final class GenerationWaiter<R> {

  private final BiFunction<R, RequestOptions, ElevenlabsApiHttpResponse<MediaGenerationCreateResponse>> create;
  private final BiFunction<String, RequestOptions, ElevenlabsApiHttpResponse<MediaGenerationResponse>> get;
  private final FlowsPoller poller;

  GenerationWaiter(
    BiFunction<R, RequestOptions, ElevenlabsApiHttpResponse<MediaGenerationCreateResponse>> create,
    BiFunction<String, RequestOptions, ElevenlabsApiHttpResponse<MediaGenerationResponse>> get,
    FlowsPoller poller
  ) {
    this.create = create;
    this.get = get;
    this.poller = poller;
  }

  /**
   * Start a generation and wait until it is {@code completed} or {@code failed}, with the default
   * {@link FlowsWaitOptions}: no wait timeout, polling no faster than every 3 seconds.
   *
   * @see #createAndWait(Object, FlowsWaitOptions, RequestOptions)
   */
  public MediaGenerationResponse createAndWait(R request) {
    return createAndWait(request, null, null);
  }

  /**
   * Start a generation and wait until it is {@code completed} or {@code failed}.
   *
   * @see #createAndWait(Object, FlowsWaitOptions, RequestOptions)
   */
  public MediaGenerationResponse createAndWait(R request, FlowsWaitOptions waitOptions) {
    return createAndWait(request, waitOptions, null);
  }

  /**
   * Start a generation and wait until it is {@code completed} or {@code failed}, then return the
   * final generation.
   *
   * <p>Between status checks this waits as long as the server's {@code Retry-After} header
   * suggests, never less than {@link FlowsWaitOptions#getMinPollInterval()}, plus up to 20%
   * jitter. This is meant for scripts and quickstarts: in production, set {@code webhook} on the
   * request and handle the webhook event instead of holding a thread open.
   *
   * <p>A {@code failed} generation is returned, not thrown; check {@code isCompleted()} /
   * {@code isFailed()} on the result. The calling thread blocks while it waits; interrupting it
   * stops the wait with an {@code ElevenlabsApiException} and leaves the interrupt flag set.
   *
   * @param request The same request the generated {@code create} takes.
   * @param waitOptions The wait timeout and minimum poll interval, or null for the defaults.
   * @param requestOptions Applied to the create call and to every status check. The status checks
   *     are GET requests, which never carry {@code bodyProperties}. {@code timeout} and
   *     {@code maxRetries} apply to each request on its own.
   * @return The generation in its terminal state: completed or failed.
   * @throws FlowsWaitTimeoutException if the wait timeout runs out first.
   */
  public MediaGenerationResponse createAndWait(
    R request,
    FlowsWaitOptions waitOptions,
    RequestOptions requestOptions
  ) {
    FlowsWaitOptions options = waitOptions == null ? FlowsWaitOptions.defaults() : waitOptions;
    long startedAt = poller.nanoTime();
    ElevenlabsApiHttpResponse<MediaGenerationCreateResponse> created = create.apply(
      request,
      requestOptions
    );
    String generationId = created.getData().getId();
    return poller.waitFor(
      generationId,
      null,
      created.getData(),
      created.getData().getStatus(),
      created.headers(),
      MediaGenerationResponse.class,
      () -> get.apply(generationId, requestOptions),
      GenerationWaiter::statusOf,
      startedAt,
      options
    );
  }

  static String statusOf(MediaGenerationResponse response) {
    return response.visit(
      new MediaGenerationResponse.Visitor<String>() {
        @Override
        public String visitPending(MediaGenerationInProgressResponse pending) {
          return pending.getStatus().toString();
        }

        @Override
        public String visitCompleted(MediaGenerationCompletedResponse completed) {
          return "completed";
        }

        @Override
        public String visitFailed(MediaGenerationFailedResponse failed) {
          return "failed";
        }

        @Override
        public String _visitUnknown(Object unknownType) {
          // A status this SDK version doesn't know yet is not terminal, so the wait goes on.
          if (unknownType instanceof Map) {
            Object status = ((Map<?, ?>) unknownType).get("status");
            if (status != null) {
              return status.toString();
            }
          }
          return "unknown";
        }
      }
    );
  }
}
