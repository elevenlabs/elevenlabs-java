package com.elevenlabs.api.flows.wait;

import com.elevenlabs.api.core.ElevenlabsApiException;
import com.elevenlabs.api.core.ElevenlabsApiHttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import okhttp3.Headers;

// Hand-written (listed in .fernignore). The polling loop behind the createAndWait helpers on
// FlowsWaiter, matching create_and_wait in the Python SDK and CreateAndWaitAsync in the .NET SDK.

/**
 * The polling loop, plus its timing seams: how to sleep, read a monotonic clock, read the wall
 * clock (for HTTP-date {@code Retry-After} values) and draw jitter. Tests subclass it so nothing
 * actually sleeps.
 */
class FlowsPoller {

  /** Spreads out clients that started together so their status checks don't arrive in lockstep. */
  static final double POLL_JITTER = 0.2;

  /** The longest single sleep; keeps every delay convertible to nanoseconds. */
  static final Duration LONGEST_DELAY = Duration.ofNanos(Long.MAX_VALUE);

  void sleep(Duration delay) throws InterruptedException {
    TimeUnit.NANOSECONDS.sleep(delay.toNanos());
  }

  /** A monotonic clock reading, in nanoseconds. */
  long nanoTime() {
    return System.nanoTime();
  }

  /** The wall clock, for {@code Retry-After} values given as an HTTP date. */
  Instant now() {
    return Instant.now();
  }

  /** A number in [0, 1). */
  double nextJitter() {
    return ThreadLocalRandom.current().nextDouble();
  }

  /**
   * The server's {@code Retry-After}, given as whole seconds or an HTTP date (RFC 9110), or
   * {@code null} if absent or unparseable. A date in the past reads as zero. Kept here rather
   * than reusing the generated retry interceptor's parser, which can change on any regeneration.
   */
  Duration parseRetryAfter(Map<String, List<String>> headers) {
    String raw = header(headers, "Retry-After");
    if (raw == null) {
      return null;
    }
    String value = raw.trim();
    if (value.isEmpty()) {
      return null;
    }
    if (isDigits(value)) {
      // Anything longer than 18 digits would overflow a long; it is far past LONGEST_DELAY anyway.
      return value.length() > 18 ? LONGEST_DELAY : Duration.ofSeconds(Long.parseLong(value));
    }
    Date date;
    try {
      // OkHttp's HTTP-date parser accepts the IMF-fixdate, RFC 850 and asctime forms RFC 9110 allows.
      date = new Headers.Builder().add("Retry-After", value).build().getDate("Retry-After");
    } catch (IllegalArgumentException e) {
      return null;
    }
    if (date == null) {
      return null;
    }
    Duration remaining = Duration.between(now(), date.toInstant());
    return remaining.isNegative() ? Duration.ZERO : remaining;
  }

  /**
   * How long to wait before the next status check: the server's {@code Retry-After}, never below
   * {@code minInterval}, plus 0-20% jitter.
   */
  Duration pollDelay(Map<String, List<String>> headers, Duration minInterval) {
    Duration retryAfter = parseRetryAfter(headers);
    Duration delay = retryAfter != null && retryAfter.compareTo(minInterval) > 0
      ? retryAfter
      : minInterval;
    double nanos = (delay.getSeconds() * 1e9 + delay.getNano()) * (1 + nextJitter() * POLL_JITTER);
    return nanos >= Long.MAX_VALUE ? LONGEST_DELAY : Duration.ofNanos((long) nanos);
  }

  static boolean isTerminal(String status) {
    return "completed".equals(status) || "failed".equals(status);
  }

  /**
   * Polls {@code get} until the status is {@code completed} or {@code failed} and returns that
   * response.
   *
   * @param id The generation or run id, for the timeout error.
   * @param templateId The template id for a run (for the timeout error), or null.
   * @param firstResponse The create response.
   * @param firstStatus The create response's status.
   * @param firstHeaders The create response's headers.
   * @param resultType The status check's response type.
   * @param get One status check.
   * @param statusOf Reads the status of a status-check response.
   * @param startedAt {@link #nanoTime()} before the create call.
   * @param options The wait options.
   */
  <T> T waitFor(
    String id,
    String templateId,
    Object firstResponse,
    String firstStatus,
    Map<String, List<String>> firstHeaders,
    Class<T> resultType,
    Supplier<ElevenlabsApiHttpResponse<T>> get,
    Function<T, String> statusOf,
    long startedAt,
    FlowsWaitOptions options
  ) {
    Duration waitTimeout = options.getWaitTimeout().orElse(null);
    Object current = firstResponse;
    String status = firstStatus;
    Map<String, List<String>> headers = firstHeaders;
    // `resultType.isInstance(current)` is false only for a generation's create response, whose
    // type differs from the status check's; a create that is already terminal then takes one
    // immediate check.
    while (!(isTerminal(status) && resultType.isInstance(current))) {
      Duration delay = Duration.ZERO;
      if (!isTerminal(status)) {
        delay = pollDelay(headers, options.getMinPollInterval());
        if (waitTimeout != null) {
          // The last sleep ends on the deadline; the check after it takes time, so the next pass
          // throws.
          Duration remaining = waitTimeout.minus(Duration.ofNanos(nanoTime() - startedAt));
          if (remaining.isNegative() || remaining.isZero()) {
            throw new FlowsWaitTimeoutException(id, templateId, status, current, waitTimeout);
          }
          if (delay.compareTo(remaining) > 0) {
            delay = remaining;
          }
        }
      }
      if (!delay.isZero()) {
        try {
          sleep(delay);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new ElevenlabsApiException(
            (templateId == null ? "Generation " : "Template run ") +
            id +
            " wait was interrupted while still " +
            status,
            e
          );
        }
      }
      ElevenlabsApiHttpResponse<T> next = get.get();
      current = next.getData();
      status = statusOf.apply(next.getData());
      headers = next.headers();
    }
    return resultType.cast(current);
  }

  private static String header(Map<String, List<String>> headers, String name) {
    if (headers == null) {
      return null;
    }
    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
      if (name.equalsIgnoreCase(entry.getKey()) && entry.getValue() != null) {
        for (String value : entry.getValue()) {
          if (value != null) {
            return value;
          }
        }
      }
    }
    return null;
  }

  private static boolean isDigits(String value) {
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c < '0' || c > '9') {
        return false;
      }
    }
    return true;
  }
}
