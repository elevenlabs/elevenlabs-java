package com.elevenlabs.api.flows.wait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elevenlabs.api.ElevenlabsApiClient;
import com.elevenlabs.api.core.ElevenlabsApiException;
import com.elevenlabs.api.core.RequestOptions;
import com.elevenlabs.api.errors.NotFoundError;
import com.elevenlabs.api.resources.flows.templates.runs.requests.TemplateRunCreateRequest;
import com.elevenlabs.api.types.AudioReference;
import com.elevenlabs.api.types.CreatifyAuroraRequest;
import com.elevenlabs.api.types.ElevenFlashV25Request;
import com.elevenlabs.api.types.GenerationReference;
import com.elevenlabs.api.types.ImageReference;
import com.elevenlabs.api.types.GptImage1Request;
import com.elevenlabs.api.types.ImageGenerationRequest;
import com.elevenlabs.api.types.MediaGenerationCreateResponse;
import com.elevenlabs.api.types.MediaGenerationResponse;
import com.elevenlabs.api.types.TemplateRunResponse;
import com.elevenlabs.api.types.TemplateRunStatus;
import com.elevenlabs.api.types.TextToSpeechGenerationRequest;
import com.elevenlabs.api.types.VideoGenerationRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Coverage for the hand-written Flows {@code createAndWait} helpers, mirroring
 * {@code FlowsCreateAndWaitTest} in the .NET SDK and {@code tests/test_flows_create_and_wait.py} in
 * the Python SDK. Requests go through the real generated client stack to a MockWebServer; a fake
 * {@link FlowsPoller} records the waits instead of sleeping and advances a fake clock by each one.
 */
class FlowsCreateAndWaitTest {

  /** Records each wait and advances its clocks by it instead of sleeping. */
  private static final class FakePoller extends FlowsPoller {

    static final Instant EPOCH = Instant.parse("2001-09-09T01:46:40Z");

    final List<Duration> delays = new ArrayList<>();
    double jitter;
    boolean interruptOnSleep;
    private long elapsedNanos;

    @Override
    void sleep(Duration delay) throws InterruptedException {
      delays.add(delay);
      elapsedNanos += delay.toNanos();
      if (interruptOnSleep) {
        throw new InterruptedException();
      }
    }

    @Override
    long nanoTime() {
      return elapsedNanos;
    }

    @Override
    Instant now() {
      return EPOCH.plusNanos(elapsedNanos);
    }

    @Override
    double nextJitter() {
      return jitter;
    }

    List<Double> delaySeconds() {
      List<Double> seconds = new ArrayList<>();
      for (Duration delay : delays) {
        seconds.add(delay.toNanos() / 1e9);
      }
      return seconds;
    }
  }

  private MockWebServer server;
  private ElevenlabsApiClient client;
  private FakePoller poller;
  private FlowsWaiter flows;

  @BeforeEach
  void setUp() throws Exception {
    server = new MockWebServer();
    server.start();
    client = ElevenlabsApiClient.builder()
      .apiKey("test-key")
      .url(server.url("/").toString())
      .maxRetries(0)
      .build();
    poller = new FakePoller();
    flows = new FlowsWaiter(client.flows(), poller);
  }

  @AfterEach
  void tearDown() throws Exception {
    server.shutdown();
  }

  private void reply(String body, String retryAfter) {
    MockResponse response = new MockResponse()
      .setResponseCode(200)
      .setBody(body)
      .addHeader("Content-Type", "application/json");
    if (retryAfter != null) {
      response.addHeader("Retry-After", retryAfter);
    }
    server.enqueue(response);
  }

  private void created(String retryAfter) {
    reply("{\"id\":\"gen_1\",\"status\":\"pending\"}", retryAfter);
  }

  private void inProgress(String status, String retryAfter) {
    reply("{\"id\":\"gen_1\",\"status\":\"" + status + "\"}", retryAfter);
  }

  private void completed() {
    reply(
      "{\"id\":\"gen_1\",\"status\":\"completed\",\"content_url\":\"https://example.com/out.png\",\"content_mime_type\":\"image/png\"}",
      null
    );
  }

  private void failed() {
    reply(
      "{\"id\":\"gen_1\",\"status\":\"failed\",\"failure_reason\":\"model_error\",\"error_message\":\"boom\"}",
      null
    );
  }

  private void run(String status, String retryAfter) {
    reply(
      "{\"id\":\"run_1\",\"template_id\":\"tpl_1\",\"version_id\":\"ver_1\",\"status\":\"" +
      status +
      "\",\"outputs\":{}}",
      retryAfter
    );
  }

  private static ImageGenerationRequest imageRequest() {
    return ImageGenerationRequest.gptImage1(
      GptImage1Request.builder().prompt("A corgi on a beach").build()
    );
  }

  private static TextToSpeechGenerationRequest textToSpeechRequest() {
    return TextToSpeechGenerationRequest.elevenFlashV2_5(
      ElevenFlashV25Request.builder().text("Hello").voice("voice_1").build()
    );
  }

  private static VideoGenerationRequest videoRequest() {
    GenerationReference source = GenerationReference.builder().generationId("gen_0").build();
    return VideoGenerationRequest.creatifyAurora(
      CreatifyAuroraRequest.builder()
        .image(ImageReference.generation(source))
        .audio(AudioReference.generation(source))
        .build()
    );
  }

  private static TemplateRunCreateRequest runRequest() {
    return TemplateRunCreateRequest.builder().build();
  }

  private List<String> takeRequests() throws InterruptedException {
    List<String> requests = new ArrayList<>();
    RecordedRequest request;
    while ((request = server.takeRequest(0, TimeUnit.MILLISECONDS)) != null) {
      requests.add(request.getMethod() + " " + request.getPath());
    }
    return requests;
  }

  private static FlowsWaitOptions timeout(Duration waitTimeout) {
    return FlowsWaitOptions.builder().waitTimeout(waitTimeout).build();
  }

  @Test
  void imagePollsUntilCompletedHonouringRetryAfterSeconds() throws Exception {
    created("3");
    inProgress("generating", "7");
    completed();

    MediaGenerationResponse result = flows.image().createAndWait(imageRequest());

    assertTrue(result.isCompleted());
    assertEquals("gen_1", result.getCompleted().get().getId());
    assertEquals("https://example.com/out.png", result.getCompleted().get().getContentUrl());
    assertEquals(Arrays.asList(3.0, 7.0), poller.delaySeconds());
    assertEquals(
      Arrays.asList("POST /v1/flows/image", "GET /v1/flows/image/gen_1", "GET /v1/flows/image/gen_1"),
      takeRequests()
    );
  }

  @Test
  void failedGenerationIsReturnedNotThrown() throws Exception {
    created(null);
    failed();

    MediaGenerationResponse result = flows.video().createAndWait(videoRequest());

    assertTrue(result.isFailed());
    assertEquals("boom", result.getFailed().get().getErrorMessage());
    assertEquals(Collections.singletonList(3.0), poller.delaySeconds());
    assertEquals(
      Arrays.asList("POST /v1/flows/video", "GET /v1/flows/video/gen_1"),
      takeRequests()
    );
  }

  @Test
  void textToSpeechPollsUntilCompleted() throws Exception {
    created("4");
    completed();

    MediaGenerationResponse result = flows.textToSpeech().createAndWait(textToSpeechRequest());

    assertTrue(result.isCompleted());
    assertEquals(Collections.singletonList(4.0), poller.delaySeconds());
    assertEquals(
      Arrays.asList("POST /v1/flows/text-to-speech", "GET /v1/flows/text-to-speech/gen_1"),
      takeRequests()
    );
  }

  @Test
  void unknownStatusKeepsPolling() {
    created(null);
    inProgress("queued", null);
    completed();

    MediaGenerationResponse result = flows.image().createAndWait(imageRequest());

    assertTrue(result.isCompleted());
    assertEquals(Arrays.asList(3.0, 3.0), poller.delaySeconds());
  }

  @Test
  void retryAfterAcceptsAnHttpDate() {
    // The fake clock reads 01:46:40 GMT, so this date is 10 seconds away.
    created("Sun, 09 Sep 2001 01:46:50 GMT");
    completed();

    flows.image().createAndWait(imageRequest());

    assertEquals(Collections.singletonList(10.0), poller.delaySeconds());
  }

  @Test
  void minPollIntervalIsAFloorUnderRetryAfter() {
    // A past date, a zero, a nonsense value and a short hint all fall back to the 5s floor.
    created("Sun, 09 Sep 2001 01:46:30 GMT");
    inProgress("generating", "0");
    inProgress("generating", "not-a-date");
    inProgress("generating", "2");
    inProgress("generating", "8");
    completed();

    flows
      .image()
      .createAndWait(
        imageRequest(),
        FlowsWaitOptions.builder().minPollInterval(Duration.ofSeconds(5)).build()
      );

    assertEquals(Arrays.asList(5.0, 5.0, 5.0, 5.0, 8.0), poller.delaySeconds());
  }

  @Test
  void jitterAddsUpToTwentyPercent() {
    poller.jitter = 0.5;
    created("5");
    completed();

    flows.image().createAndWait(imageRequest());

    assertEquals(1, poller.delays.size());
    assertEquals(5.5, poller.delaySeconds().get(0), 1e-9);
  }

  @Test
  void timeoutThrowsWithTheGenerationId() throws Exception {
    created("30");
    inProgress("generating", "30");

    FlowsWaitTimeoutException error = assertThrows(FlowsWaitTimeoutException.class, () ->
      flows.image().createAndWait(imageRequest(), timeout(Duration.ofSeconds(10)))
    );

    // The 30s hint is cut to the 10s left; the next pass finds no time left and throws.
    assertEquals(Collections.singletonList(10.0), poller.delaySeconds());
    assertEquals("gen_1", error.getId());
    assertFalse(error.getTemplateId().isPresent());
    assertEquals("generating", error.getStatus());
    assertEquals(Duration.ofSeconds(10), error.getWaitTimeout());
    assertInstanceOf(MediaGenerationResponse.class, error.getLastResponse());
    assertTrue(error.getMessage().contains("Generation gen_1 is still generating after 10s"));
    assertTrue(error.getMessage().contains("call get with this id to keep waiting"));
    assertInstanceOf(ElevenlabsApiException.class, error);
    assertEquals(2, takeRequests().size());
  }

  @Test
  void zeroTimeoutThrowsStraightAfterTheCreate() throws Exception {
    created(null);

    FlowsWaitTimeoutException error = assertThrows(FlowsWaitTimeoutException.class, () ->
      flows.image().createAndWait(imageRequest(), timeout(Duration.ZERO))
    );

    assertEquals("pending", error.getStatus());
    assertInstanceOf(MediaGenerationCreateResponse.class, error.getLastResponse());
    assertTrue(error.getMessage().contains("after 0s"));
    assertTrue(poller.delays.isEmpty());
    assertEquals(1, takeRequests().size());
  }

  @Test
  void invalidOptionsAreRejected() {
    assertThrows(IllegalArgumentException.class, () ->
      FlowsWaitOptions.builder().minPollInterval(Duration.ofMillis(499)).build()
    );
    assertThrows(IllegalArgumentException.class, () ->
      FlowsWaitOptions.builder().minPollInterval(null).build()
    );
    assertThrows(IllegalArgumentException.class, () ->
      FlowsWaitOptions.builder().waitTimeout(Duration.ofSeconds(-1)).build()
    );
    FlowsWaitOptions defaults = FlowsWaitOptions.defaults();
    assertEquals(Duration.ofSeconds(3), defaults.getMinPollInterval());
    assertFalse(defaults.getWaitTimeout().isPresent());
    assertEquals(
      Duration.ofMillis(500),
      FlowsWaitOptions.builder().minPollInterval(Duration.ofMillis(500)).build().getMinPollInterval()
    );
  }

  @Test
  void templateRunPollsUntilCompleted() throws Exception {
    run("pending", "2");
    run("completed", null);

    TemplateRunResponse result = flows
      .templates()
      .runs()
      .createAndWait(
        "tpl_1",
        runRequest(),
        FlowsWaitOptions.builder().minPollInterval(Duration.ofSeconds(1)).build()
      );

    assertEquals(TemplateRunStatus.COMPLETED, result.getStatus());
    assertEquals(Collections.singletonList(2.0), poller.delaySeconds());
    assertEquals(
      Arrays.asList("POST /v1/flows/templates/tpl_1/runs", "GET /v1/flows/templates/tpl_1/runs/run_1"),
      takeRequests()
    );
  }

  @Test
  void failedTemplateRunIsReturnedNotThrown() {
    run("generating", null);
    run("failed", null);

    TemplateRunResponse result = flows.templates().runs().createAndWait("tpl_1", runRequest());

    assertEquals(TemplateRunStatus.FAILED, result.getStatus());
  }

  @Test
  void templateRunAlreadyTerminalOnCreateSkipsPolling() throws Exception {
    run("completed", null);

    TemplateRunResponse result = flows.templates().runs().createAndWait("tpl_1", runRequest());

    assertEquals(TemplateRunStatus.COMPLETED, result.getStatus());
    assertTrue(poller.delays.isEmpty());
    assertEquals(1, takeRequests().size());
  }

  @Test
  void templateRunTimeoutCarriesTheTemplateId() {
    run("pending", null);

    FlowsWaitTimeoutException error = assertThrows(FlowsWaitTimeoutException.class, () ->
      flows.templates().runs().createAndWait("tpl_1", runRequest(), timeout(Duration.ZERO))
    );

    assertEquals("run_1", error.getId());
    assertEquals("tpl_1", error.getTemplateId().get());
    assertInstanceOf(TemplateRunResponse.class, error.getLastResponse());
    assertTrue(error.getMessage().contains("Template run run_1 of template tpl_1"));
    assertTrue(
      error
        .getMessage()
        .contains("call templates().runs().get with the template id and run id to keep waiting")
    );
  }

  @Test
  void requestOptionsReachEveryRequestAndBodyPropertiesOnlyTheCreate() throws Exception {
    created(null);
    completed();

    flows
      .image()
      .createAndWait(
        imageRequest(),
        null,
        RequestOptions.builder().addHeader("x-test", "1").addBodyProperty("extra", 1).build()
      );

    RecordedRequest create = server.takeRequest();
    RecordedRequest poll = server.takeRequest();
    assertTrue(create.getBody().readUtf8().contains("\"extra\""));
    assertEquals(0, poll.getBodySize());
    assertEquals("1", create.getHeader("x-test"));
    assertEquals("1", poll.getHeader("x-test"));
    assertEquals("test-key", poll.getHeader("xi-api-key"));
  }

  @Test
  void statusCheckErrorsPropagate() {
    created(null);
    server.enqueue(
      new MockResponse()
        .setResponseCode(404)
        .setBody("{\"detail\":\"not found\"}")
        .addHeader("Content-Type", "application/json")
    );

    assertThrows(NotFoundError.class, () -> flows.image().createAndWait(imageRequest()));
  }

  @Test
  void interruptStopsTheWait() throws Exception {
    poller.interruptOnSleep = true;
    created(null);

    try {
      ElevenlabsApiException error = assertThrows(ElevenlabsApiException.class, () ->
        flows.image().createAndWait(imageRequest())
      );
      assertInstanceOf(InterruptedException.class, error.getCause());
      assertTrue(Thread.currentThread().isInterrupted());
    } finally {
      // Clear the flag so it does not leak into other tests on this thread.
      Thread.interrupted();
    }
    assertEquals(1, takeRequests().size());
  }

  @Test
  void parsesRetryAfterForms() {
    FlowsPoller parser = new FakePoller();
    assertEquals(Duration.ofSeconds(12), parser.parseRetryAfter(headers(" 12 ")));
    assertEquals(Duration.ofSeconds(10), parser.parseRetryAfter(headers("Sunday, 09-Sep-01 01:46:50 GMT")));
    assertEquals(Duration.ofSeconds(10), parser.parseRetryAfter(headers("Sun Sep  9 01:46:50 2001")));
    assertEquals(Duration.ZERO, parser.parseRetryAfter(headers("Sun, 09 Sep 2001 01:46:00 GMT")));
    assertEquals(FlowsPoller.LONGEST_DELAY, parser.parseRetryAfter(headers("99999999999999999999")));
    assertEquals(null, parser.parseRetryAfter(headers("1.5")));
    assertEquals(null, parser.parseRetryAfter(headers("")));
    assertEquals(null, parser.parseRetryAfter(Collections.emptyMap()));
  }

  private static java.util.Map<String, List<String>> headers(String retryAfter) {
    return Collections.singletonMap("retry-after", Collections.singletonList(retryAfter));
  }

  @Test
  void realPollerSleepsBetweenChecks() {
    // The public constructor uses the real clock: one check after the 0.5s floor plus jitter.
    FlowsWaiter real = new FlowsWaiter(client);
    created(null);
    completed();

    long started = System.nanoTime();
    MediaGenerationResponse result = real
      .image()
      .createAndWait(
        imageRequest(),
        FlowsWaitOptions.builder().minPollInterval(Duration.ofMillis(500)).build()
      );
    long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);

    assertTrue(result.isCompleted());
    assertTrue(elapsedMillis >= 500, "slept " + elapsedMillis + "ms");
  }
}
