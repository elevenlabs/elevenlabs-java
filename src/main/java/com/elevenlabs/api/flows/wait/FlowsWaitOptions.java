package com.elevenlabs.api.flows.wait;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

// Hand-written (listed in .fernignore). Options for the createAndWait helpers on FlowsWaiter.

/**
 * Options for the {@code createAndWait} helpers on {@link FlowsWaiter}.
 *
 * <pre>{@code
 * FlowsWaitOptions options = FlowsWaitOptions.builder()
 *   .waitTimeout(Duration.ofMinutes(10))
 *   .minPollInterval(Duration.ofSeconds(5))
 *   .build();
 * }</pre>
 */
public final class FlowsWaitOptions {

  /**
   * The shortest wait between status checks, and the wait used when the server sends no
   * {@code Retry-After} header. Matches the server's shortest {@code Retry-After} (3 seconds for
   * image, audio and text), so the SDK polls no faster than the server would pace it.
   */
  public static final Duration DEFAULT_MIN_POLL_INTERVAL = Duration.ofSeconds(3);

  /**
   * The smallest {@link #getMinPollInterval()} accepted. Below this, a missing server hint would
   * turn the wait into a tight loop of status checks.
   */
  public static final Duration SMALLEST_MIN_POLL_INTERVAL = Duration.ofMillis(500);

  private static final FlowsWaitOptions DEFAULTS = builder().build();

  private final Duration waitTimeout;
  private final Duration minPollInterval;

  private FlowsWaitOptions(Duration waitTimeout, Duration minPollInterval) {
    this.waitTimeout = waitTimeout;
    this.minPollInterval = minPollInterval;
  }

  /** No wait timeout and a 3 second minimum poll interval. */
  public static FlowsWaitOptions defaults() {
    return DEFAULTS;
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * How long to wait in total before throwing {@link FlowsWaitTimeoutException}. Empty (the
   * default) waits indefinitely.
   */
  public Optional<Duration> getWaitTimeout() {
    return Optional.ofNullable(waitTimeout);
  }

  /** The shortest wait between status checks, and the wait used when the server suggests none. */
  public Duration getMinPollInterval() {
    return minPollInterval;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof FlowsWaitOptions)) {
      return false;
    }
    FlowsWaitOptions that = (FlowsWaitOptions) other;
    return (
      Objects.equals(waitTimeout, that.waitTimeout) && minPollInterval.equals(that.minPollInterval)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(waitTimeout, minPollInterval);
  }

  @Override
  public String toString() {
    return (
      "FlowsWaitOptions{waitTimeout=" + waitTimeout + ", minPollInterval=" + minPollInterval + "}"
    );
  }

  public static final class Builder {

    private Duration waitTimeout;
    private Duration minPollInterval = DEFAULT_MIN_POLL_INTERVAL;

    private Builder() {}

    /**
     * How long to wait in total before throwing {@link FlowsWaitTimeoutException}, which carries
     * the id so you can resume with {@code get}. {@code null} (the default) waits indefinitely;
     * otherwise it must be at least zero, and zero times out straight after the create call
     * unless that call already returned a finished template run. Not to be confused with the
     * {@code timeout} in {@link com.elevenlabs.api.core.RequestOptions}, which limits each HTTP
     * request.
     */
    public Builder waitTimeout(Duration waitTimeout) {
      this.waitTimeout = waitTimeout;
      return this;
    }

    /**
     * The shortest wait between status checks, and the wait used when the server suggests none.
     * Must be at least {@link #SMALLEST_MIN_POLL_INTERVAL} (0.5 seconds). Defaults to
     * {@link #DEFAULT_MIN_POLL_INTERVAL} (3 seconds).
     */
    public Builder minPollInterval(Duration minPollInterval) {
      this.minPollInterval = minPollInterval;
      return this;
    }

    /**
     * @throws IllegalArgumentException if the wait timeout is negative, or the minimum poll
     *     interval is missing or below 0.5 seconds
     */
    public FlowsWaitOptions build() {
      if (minPollInterval == null || minPollInterval.compareTo(SMALLEST_MIN_POLL_INTERVAL) < 0) {
        throw new IllegalArgumentException(
          "minPollInterval must be at least 0.5s, got " + minPollInterval
        );
      }
      if (waitTimeout != null && waitTimeout.isNegative()) {
        throw new IllegalArgumentException(
          "waitTimeout must be at least zero, or null to wait indefinitely, got " + waitTimeout
        );
      }
      return new FlowsWaitOptions(waitTimeout, minPollInterval);
    }
  }
}
