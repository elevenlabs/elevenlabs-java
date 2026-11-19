package com.elevenlabs.api.webhooks;

import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.webhooks.WebhookVerificationException.Reason;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Verifies the signature on webhooks that ElevenLabs sends to your server.
 *
 * <p>ElevenLabs signs each webhook with an HMAC-SHA256 digest, sent in the {@code
 * ElevenLabs-Signature} header as a compound value of the form {@code
 * t=<unix_timestamp>,v0=<hex_digest>}. The signed message is the timestamp and the raw request body
 * joined with a period ({@code <timestamp>.<raw_body>}).
 *
 * <p>This is a hand-written helper rather than generated code (listed in {@code .fernignore}): the
 * Fern generator's built-in verifier cannot parse a compound header that carries both the timestamp
 * and the signature. It mirrors {@code construct_event} in the Python SDK so every SDK accepts the
 * same signatures.
 *
 * <pre>{@code
 * JsonNode event = WebhookVerifier.constructEvent(rawBody, signatureHeader, secret);
 * if ("post_call_transcription".equals(event.path("type").asText())) {
 *   String agentId = event.path("data").path("agent_id").asText();
 * }
 * }</pre>
 */
public final class WebhookVerifier {

  /**
   * How far in the past, in seconds, a webhook timestamp may be before it is rejected as a possible
   * replay. Timestamps in the future are accepted, as in every other ElevenLabs SDK.
   */
  public static final long TOLERANCE_SECONDS = 30 * 60;

  private static final String SCHEME = "v0=";
  private static final char[] HEX = "0123456789abcdef".toCharArray();

  private WebhookVerifier() {}

  /**
   * Verify a webhook signature and return the decoded event payload.
   *
   * @param rawBody the raw request body, exactly as received. Pass the unparsed string:
   *     re-serializing a deserialized body changes it and breaks verification.
   * @param sigHeader the value of the {@code ElevenLabs-Signature} header
   * @param secret your webhook signing secret
   * @return the decoded webhook event
   * @throws WebhookVerificationException (status code 400) if the header or secret is missing, the
   *     header has no {@code t=}/{@code v0=} pair, the timestamp is more than {@link
   *     #TOLERANCE_SECONDS} old, the signature does not match, or the body is not valid JSON
   */
  public static JsonNode constructEvent(String rawBody, String sigHeader, String secret) {
    return constructEvent(utf8(rawBody), sigHeader, secret);
  }

  /**
   * Verify a webhook signature over the raw body bytes and return the decoded event payload. Use
   * this overload when your framework hands you the body as bytes, so no charset conversion happens
   * before verification.
   *
   * @see #constructEvent(String, String, String)
   */
  public static JsonNode constructEvent(byte[] rawBody, String sigHeader, String secret) {
    return constructEvent(rawBody, sigHeader, secret, System.currentTimeMillis() / 1000);
  }

  /** {@link #constructEvent(byte[], String, String)} with the current time injected, for tests. */
  static JsonNode constructEvent(byte[] rawBody, String sigHeader, String secret, long nowSeconds) {
    if (sigHeader == null || sigHeader.isEmpty()) {
      throw new WebhookVerificationException(Reason.MISSING_SIGNATURE_HEADER);
    }
    if (secret == null || secret.isEmpty()) {
      throw new WebhookVerificationException(Reason.MISSING_SECRET);
    }
    byte[] body = rawBody == null ? new byte[0] : rawBody;

    String timestamp = null;
    String signature = null;
    for (String part : sigHeader.split(",", -1)) {
      if (part.startsWith("t=")) {
        timestamp = part.substring(2);
      } else if (part.startsWith(SCHEME)) {
        signature = part;
      }
    }
    if (timestamp == null || timestamp.isEmpty() || signature == null) {
      throw new WebhookVerificationException(Reason.MISSING_SIGNATURE_SCHEME);
    }

    // A non-numeric timestamp can never be inside the window, so it is rejected the same way as a
    // stale one rather than surfacing a NumberFormatException.
    long requestTimestamp;
    try {
      requestTimestamp = Long.parseLong(timestamp);
    } catch (NumberFormatException e) {
      requestTimestamp = 0;
    }
    if (requestTimestamp < nowSeconds - TOLERANCE_SECONDS) {
      throw new WebhookVerificationException(Reason.TIMESTAMP_OUTSIDE_TOLERANCE);
    }

    byte[] mac = hmacSha256(utf8(secret), utf8(timestamp + "."), body);
    byte[] expected = utf8(SCHEME + toLowerHex(mac));
    // MessageDigest.isEqual compares in constant time.
    if (!MessageDigest.isEqual(expected, utf8(signature))) {
      throw new WebhookVerificationException(Reason.SIGNATURE_MISMATCH);
    }

    JsonNode event;
    try {
      // Trailing content after the JSON value is rejected, as Python's json.loads rejects it.
      event = ObjectMappers.JSON_MAPPER.reader()
        .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
        .readTree(body);
    } catch (IOException e) {
      throw new WebhookVerificationException(Reason.INVALID_PAYLOAD, e);
    }
    // Jackson returns a MissingNode (or null on older versions) for an empty body.
    if (event == null || event.isMissingNode()) {
      throw new WebhookVerificationException(Reason.INVALID_PAYLOAD);
    }
    return event;
  }

  private static byte[] hmacSha256(byte[] key, byte[] prefix, byte[] body) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(key, "HmacSHA256"));
      mac.update(prefix);
      mac.update(body);
      return mac.doFinal();
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      // Every Java platform is required to provide HmacSHA256, and it accepts any non-empty key.
      throw new IllegalStateException("HmacSHA256 is unavailable", e);
    }
  }

  private static String toLowerHex(byte[] bytes) {
    char[] out = new char[bytes.length * 2];
    for (int i = 0; i < bytes.length; i++) {
      out[i * 2] = HEX[(bytes[i] >> 4) & 0x0f];
      out[i * 2 + 1] = HEX[bytes[i] & 0x0f];
    }
    return new String(out);
  }

  private static byte[] utf8(String value) {
    return value == null ? new byte[0] : value.getBytes(StandardCharsets.UTF_8);
  }
}
