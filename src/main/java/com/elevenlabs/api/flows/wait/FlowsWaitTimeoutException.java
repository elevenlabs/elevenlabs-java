package com.elevenlabs.api.flows.wait;

import com.elevenlabs.api.core.ElevenlabsApiException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Optional;

// Hand-written (listed in .fernignore). Thrown by the createAndWait helpers on FlowsWaiter.

/**
 * Thrown by {@code createAndWait} when a generation or template run is still unfinished after
 * {@link FlowsWaitOptions#getWaitTimeout()}.
 *
 * <p>The work keeps running on the server. Resume waiting by calling {@code get} with
 * {@link #getId()} (and {@link #getTemplateId()} for a template run), or let a webhook deliver the
 * result.
 */
public final class FlowsWaitTimeoutException extends ElevenlabsApiException {

  private final String id;
  private final String templateId;
  private final String status;
  private final transient Object lastResponse;
  private final Duration waitTimeout;

  FlowsWaitTimeoutException(
    String id,
    String templateId,
    String status,
    Object lastResponse,
    Duration waitTimeout
  ) {
    super(buildMessage(id, templateId, status, waitTimeout));
    this.id = id;
    this.templateId = templateId;
    this.status = status;
    this.lastResponse = lastResponse;
    this.waitTimeout = waitTimeout;
  }

  /** The generation id, or the run id for a template run. */
  public String getId() {
    return id;
  }

  /** The template id for a template run; empty for a generation. */
  public Optional<String> getTemplateId() {
    return Optional.ofNullable(templateId);
  }

  /** The last status seen, such as {@code pending} or {@code generating}. */
  public String getStatus() {
    return status;
  }

  /**
   * The last response seen: the create response ({@code MediaGenerationCreateResponse} or
   * {@code TemplateRunResponse}) if no status check ran, otherwise the last
   * {@code MediaGenerationResponse} or {@code TemplateRunResponse}.
   */
  public Object getLastResponse() {
    return lastResponse;
  }

  /** The wait timeout that ran out. */
  public Duration getWaitTimeout() {
    return waitTimeout;
  }

  private static String buildMessage(
    String id,
    String templateId,
    String status,
    Duration waitTimeout
  ) {
    String after = formatSeconds(waitTimeout);
    if (templateId == null) {
      return (
        "Generation " +
        id +
        " is still " +
        status +
        " after " +
        after +
        "; call get with this id to keep waiting"
      );
    }
    return (
      "Template run " +
      id +
      " of template " +
      templateId +
      " is still " +
      status +
      " after " +
      after +
      "; call templates().runs().get with the template id and run id to keep waiting"
    );
  }

  static String formatSeconds(Duration value) {
    BigDecimal seconds = BigDecimal.valueOf(value.getSeconds())
      .add(BigDecimal.valueOf(value.getNano(), 9))
      .setScale(3, RoundingMode.HALF_UP)
      .stripTrailingZeros();
    return seconds.toPlainString() + "s";
  }
}
