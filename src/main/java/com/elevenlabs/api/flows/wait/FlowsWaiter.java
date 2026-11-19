package com.elevenlabs.api.flows.wait;

import com.elevenlabs.api.ElevenlabsApiClient;
import com.elevenlabs.api.resources.flows.FlowsClient;
import com.elevenlabs.api.resources.flows.image.ImageClient;
import com.elevenlabs.api.resources.flows.texttospeech.TextToSpeechClient;
import com.elevenlabs.api.resources.flows.video.VideoClient;
import com.elevenlabs.api.types.ImageGenerationRequest;
import com.elevenlabs.api.types.TextToSpeechGenerationRequest;
import com.elevenlabs.api.types.VideoGenerationRequest;
import java.util.Objects;

// Hand-written (listed in .fernignore). The generated Flows clients are plain classes that Fern
// rewrites on every regeneration, and Java has no partial classes or extension methods, so
// createAndWait cannot be added to them without editing generated files. This is a separate entry
// point instead, like FlowsWaiter in the PHP SDK.

/**
 * Adds {@code createAndWait} to the Flows clients: start a generation or template run and poll
 * until it is {@code completed} or {@code failed}.
 *
 * <pre>{@code
 * FlowsWaiter flows = new FlowsWaiter(client);
 * MediaGenerationResponse generation = flows.image().createAndWait(request);
 * TemplateRunResponse run = flows.templates().runs().createAndWait(templateId, runRequest);
 * }</pre>
 *
 * <p>Each waiter sends its requests through the matching generated client's
 * {@code withRawResponse()} (for example {@code client.flows().image()}), so the base URL,
 * credentials, retries and per-method config set on those clients apply. The raw response is
 * what exposes the server's {@code Retry-After} header between polls.
 *
 * <p>Thread-safe: a waiter holds no per-call state.
 */
public final class FlowsWaiter {

  private final GenerationWaiter<ImageGenerationRequest> image;
  private final GenerationWaiter<VideoGenerationRequest> video;
  private final GenerationWaiter<TextToSpeechGenerationRequest> textToSpeech;
  private final TemplatesWaiter templates;

  /** @param client The root client. */
  public FlowsWaiter(ElevenlabsApiClient client) {
    this(Objects.requireNonNull(client, "client").flows());
  }

  /** @param flows The root client's {@code flows()}. */
  public FlowsWaiter(FlowsClient flows) {
    this(flows, new FlowsPoller());
  }

  FlowsWaiter(FlowsClient flows, FlowsPoller poller) {
    Objects.requireNonNull(flows, "flows");
    ImageClient imageClient = flows.image();
    VideoClient videoClient = flows.video();
    TextToSpeechClient textToSpeechClient = flows.textToSpeech();
    this.image = new GenerationWaiter<>(
      (request, options) -> imageClient.withRawResponse().create(request, options),
      (id, options) -> imageClient.withRawResponse().get(id, options),
      poller
    );
    this.video = new GenerationWaiter<>(
      (request, options) -> videoClient.withRawResponse().create(request, options),
      (id, options) -> videoClient.withRawResponse().get(id, options),
      poller
    );
    this.textToSpeech = new GenerationWaiter<>(
      (request, options) -> textToSpeechClient.withRawResponse().create(request, options),
      (id, options) -> textToSpeechClient.withRawResponse().get(id, options),
      poller
    );
    this.templates = new TemplatesWaiter(new TemplateRunsWaiter(flows.templates().runs(), poller));
  }

  /** {@code createAndWait} for {@code client.flows().image()}. */
  public GenerationWaiter<ImageGenerationRequest> image() {
    return image;
  }

  /** {@code createAndWait} for {@code client.flows().video()}. */
  public GenerationWaiter<VideoGenerationRequest> video() {
    return video;
  }

  /** {@code createAndWait} for {@code client.flows().textToSpeech()}. */
  public GenerationWaiter<TextToSpeechGenerationRequest> textToSpeech() {
    return textToSpeech;
  }

  /** Holds {@link TemplatesWaiter#runs()}, for {@code client.flows().templates().runs()}. */
  public TemplatesWaiter templates() {
    return templates;
  }
}
