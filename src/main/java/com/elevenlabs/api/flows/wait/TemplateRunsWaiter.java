package com.elevenlabs.api.flows.wait;

import com.elevenlabs.api.core.ElevenlabsApiHttpResponse;
import com.elevenlabs.api.core.RequestOptions;
import com.elevenlabs.api.resources.flows.templates.runs.RunsClient;
import com.elevenlabs.api.resources.flows.templates.runs.requests.TemplateRunCreateRequest;
import com.elevenlabs.api.types.TemplateRunResponse;

// Hand-written (listed in .fernignore). createAndWait for client.flows().templates().runs(),
// matching flows.templates.runs.create_and_wait in the Python SDK.

/** {@code createAndWait} for {@code client.flows().templates().runs()}. Get one from {@link FlowsWaiter}. */
public final class TemplateRunsWaiter {

  private final RunsClient runs;
  private final FlowsPoller poller;

  TemplateRunsWaiter(RunsClient runs, FlowsPoller poller) {
    this.runs = runs;
    this.poller = poller;
  }

  /**
   * Start a template run and wait until it is {@code completed} or {@code failed}, with the
   * default {@link FlowsWaitOptions}: no wait timeout, polling no faster than every 3 seconds.
   *
   * @see #createAndWait(String, TemplateRunCreateRequest, FlowsWaitOptions, RequestOptions)
   */
  public TemplateRunResponse createAndWait(String templateId, TemplateRunCreateRequest request) {
    return createAndWait(templateId, request, null, null);
  }

  /**
   * Start a template run and wait until it is {@code completed} or {@code failed}.
   *
   * @see #createAndWait(String, TemplateRunCreateRequest, FlowsWaitOptions, RequestOptions)
   */
  public TemplateRunResponse createAndWait(
    String templateId,
    TemplateRunCreateRequest request,
    FlowsWaitOptions waitOptions
  ) {
    return createAndWait(templateId, request, waitOptions, null);
  }

  /**
   * Start a run of a flows template and wait until its status is {@code completed} or
   * {@code failed}, then return the final run.
   *
   * <p>Between status checks this waits as long as the server's {@code Retry-After} header
   * suggests, never less than {@link FlowsWaitOptions#getMinPollInterval()}, plus up to 20%
   * jitter. This is meant for scripts and quickstarts: in production, set {@code webhook} on the
   * request and handle the {@code flows_template_run} webhook instead of holding a thread open.
   *
   * <p>A {@code failed} run is returned, not thrown; check {@code getStatus()} on the result and
   * on each output. A run that is already finished when created is returned without polling. The
   * calling thread blocks while it waits; interrupting it stops the wait with an
   * {@code ElevenlabsApiException} and leaves the interrupt flag set.
   *
   * @param templateId The ID of the template.
   * @param request The same request the generated {@code create} takes.
   * @param waitOptions The wait timeout and minimum poll interval, or null for the defaults.
   * @param requestOptions Applied to the create call and to every status check. The status checks
   *     are GET requests, which never carry {@code bodyProperties}. {@code timeout} and
   *     {@code maxRetries} apply to each request on its own.
   * @return The run in its terminal state.
   * @throws FlowsWaitTimeoutException if the wait timeout runs out first; it carries the run id
   *     and the template id.
   */
  public TemplateRunResponse createAndWait(
    String templateId,
    TemplateRunCreateRequest request,
    FlowsWaitOptions waitOptions,
    RequestOptions requestOptions
  ) {
    FlowsWaitOptions options = waitOptions == null ? FlowsWaitOptions.defaults() : waitOptions;
    long startedAt = poller.nanoTime();
    ElevenlabsApiHttpResponse<TemplateRunResponse> created = runs
      .withRawResponse()
      .create(templateId, request, requestOptions);
    String runId = created.getData().getId();
    return poller.waitFor(
      runId,
      templateId,
      created.getData(),
      created.getData().getStatus().toString(),
      created.headers(),
      TemplateRunResponse.class,
      () -> runs.withRawResponse().get(templateId, runId, requestOptions),
      response -> response.getStatus().toString(),
      startedAt,
      options
    );
  }
}
