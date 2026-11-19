package com.elevenlabs.api.webhooks;

import com.elevenlabs.api.core.ElevenlabsApiApiException;
import java.util.Collections;

/**
 * Thrown by {@link WebhookVerifier#constructEvent} when a webhook fails verification.
 *
 * <p>Hand-written (listed in {@code .fernignore}). Verification happens locally and never talks to
 * the API, but the exception carries status code 400 and an {@code {"error": "<message>"}} body, as
 * the {@code BadRequestError} that the Python SDK raises does, so a webhook handler can answer the
 * request with {@link #statusCode()} directly. {@link #reason()} says which check failed; the
 * messages match the other ElevenLabs SDKs.
 */
public final class WebhookVerificationException extends ElevenlabsApiApiException {

  /** Which verification check failed. */
  public enum Reason {
    /** The {@code ElevenLabs-Signature} header was empty or not present. */
    MISSING_SIGNATURE_HEADER("Missing signature header"),

    /** The webhook signing secret was empty or not configured. */
    MISSING_SECRET("Webhook secret not configured"),

    /** The header did not contain both a {@code t=} timestamp and a {@code v0=} signature. */
    MISSING_SIGNATURE_SCHEME("No signature hash found with expected scheme v0"),

    /**
     * The timestamp is more than {@link WebhookVerifier#TOLERANCE_SECONDS} in the past (or is not
     * a number), so the request is rejected as a possible replay.
     */
    TIMESTAMP_OUTSIDE_TOLERANCE("Timestamp outside the tolerance zone"),

    /** The {@code v0} signature does not match the HMAC of the timestamp and raw body. */
    SIGNATURE_MISMATCH("Signature hash does not match the expected signature hash for payload"),

    /** The signature was valid but the body is not valid JSON. */
    INVALID_PAYLOAD("Webhook payload is not valid JSON");

    private final String message;

    Reason(String message) {
      this.message = message;
    }

    /** The error message reported for this reason. */
    public String message() {
      return message;
    }
  }

  private final Reason reason;

  WebhookVerificationException(Reason reason) {
    this(reason, null);
  }

  WebhookVerificationException(Reason reason, Exception cause) {
    super(reason.message(), 400, Collections.singletonMap("error", reason.message()));
    this.reason = reason;
    if (cause != null) {
      initCause(cause);
    }
  }

  /** Which verification check failed. */
  public Reason reason() {
    return reason;
  }
}
