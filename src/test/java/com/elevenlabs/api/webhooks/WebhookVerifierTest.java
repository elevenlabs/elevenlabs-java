package com.elevenlabs.api.webhooks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elevenlabs.api.core.ElevenlabsApiApiException;
import com.elevenlabs.api.webhooks.WebhookVerificationException.Reason;
import com.fasterxml.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

/**
 * Coverage for {@link WebhookVerifier}, mirroring the shared cross-SDK suite (see the .NET and Rust
 * ports), including the shared golden vector.
 */
public class WebhookVerifierTest {

  // Shared cross-SDK golden vector. The timestamp is far in the future so it stays inside the
  // (past-only) tolerance window forever; every ElevenLabs SDK must accept this exact
  // (secret, header, body) triple.
  private static final String SECRET = "wsec_0123456789abcdef0123456789abcdef";
  private static final String BODY =
    "{\"type\":\"post_call_transcription\",\"event_timestamp\":1234567890," +
    "\"data\":{\"agent_id\":\"abc123\"}}";
  private static final String HEADER =
    "t=4102444800,v0=01fc9faf23a5c155fbab279c4e0acb2ae059a567b14c10a19bd9f99c88315e99";

  private static final String PING = "{\"type\":\"ping\"}";

  private static long now() {
    return System.currentTimeMillis() / 1000;
  }

  private static String sign(String timestamp, String body) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    byte[] digest = mac.doFinal((timestamp + "." + body).getBytes(StandardCharsets.UTF_8));
    StringBuilder hex = new StringBuilder();
    for (byte b : digest) {
      hex.append(String.format("%02x", b));
    }
    return "t=" + timestamp + ",v0=" + hex;
  }

  private static WebhookVerificationException assertRejected(
    String body,
    String header,
    String secret,
    Reason reason
  ) {
    WebhookVerificationException e = assertThrows(WebhookVerificationException.class, () ->
      WebhookVerifier.constructEvent(body, header, secret)
    );
    assertEquals(reason, e.reason());
    assertEquals(reason.message(), e.getMessage());
    assertEquals(400, e.statusCode());
    return e;
  }

  private static String zeros(int n) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++) {
      sb.append('0');
    }
    return sb.toString();
  }

  @Test
  public void goldenVectorVerifiesAndDecodes() {
    JsonNode event = WebhookVerifier.constructEvent(BODY, HEADER, SECRET);

    assertEquals("post_call_transcription", event.get("type").asText());
    assertEquals(1234567890L, event.get("event_timestamp").asLong());
    assertEquals("abc123", event.get("data").get("agent_id").asText());
  }

  @Test
  public void goldenVectorVerifiesFromRawBytes() {
    JsonNode event = WebhookVerifier.constructEvent(
      BODY.getBytes(StandardCharsets.UTF_8),
      HEADER,
      SECRET
    );

    assertEquals("post_call_transcription", event.get("type").asText());
  }

  @Test
  public void currentTimestampVerifies() throws Exception {
    String timestamp = Long.toString(now());

    JsonNode event = WebhookVerifier.constructEvent(PING, sign(timestamp, PING), SECRET);

    assertEquals("ping", event.get("type").asText());
  }

  @Test
  public void nonAsciiBodyVerifies() throws Exception {
    String timestamp = Long.toString(now());
    String body = "{\"type\":\"ping\",\"text\":\"héllo 日本\"}";

    JsonNode event = WebhookVerifier.constructEvent(body, sign(timestamp, body), SECRET);

    assertEquals("héllo 日本", event.get("text").asText());
  }

  @Test
  public void tamperedSignatureIsRejected() {
    assertRejected(BODY, "t=4102444800,v0=" + zeros(64), SECRET, Reason.SIGNATURE_MISMATCH);
  }

  @Test
  public void tamperedBodyIsRejected() {
    assertRejected(BODY + " ", HEADER, SECRET, Reason.SIGNATURE_MISMATCH);
  }

  @Test
  public void wrongSecretIsRejected() {
    assertRejected(BODY, HEADER, SECRET + "x", Reason.SIGNATURE_MISMATCH);
  }

  @Test
  public void uppercaseSignatureIsRejected() {
    String upper = "t=4102444800,v0=" + HEADER.substring("t=4102444800,v0=".length()).toUpperCase();
    assertRejected(BODY, upper, SECRET, Reason.SIGNATURE_MISMATCH);
  }

  @Test
  public void oldTimestampIsRejected() throws Exception {
    String timestamp = Long.toString(now() - 31 * 60);

    assertRejected(PING, sign(timestamp, PING), SECRET, Reason.TIMESTAMP_OUTSIDE_TOLERANCE);
  }

  @Test
  public void toleranceBoundaryIsInclusive() throws Exception {
    long now = 1_700_000_000L;
    byte[] body = PING.getBytes(StandardCharsets.UTF_8);

    String atEdge = Long.toString(now - WebhookVerifier.TOLERANCE_SECONDS);
    JsonNode event = WebhookVerifier.constructEvent(body, sign(atEdge, PING), SECRET, now);
    assertEquals("ping", event.get("type").asText());

    String pastEdge = Long.toString(now - WebhookVerifier.TOLERANCE_SECONDS - 1);
    WebhookVerificationException e = assertThrows(WebhookVerificationException.class, () ->
      WebhookVerifier.constructEvent(body, sign(pastEdge, PING), SECRET, now)
    );
    assertEquals(Reason.TIMESTAMP_OUTSIDE_TOLERANCE, e.reason());
  }

  @Test
  public void nonNumericTimestampIsRejected() throws Exception {
    assertRejected(PING, sign("abc", PING), SECRET, Reason.TIMESTAMP_OUTSIDE_TOLERANCE);
  }

  @Test
  public void missingHeaderIsRejected() {
    assertRejected(BODY, "", SECRET, Reason.MISSING_SIGNATURE_HEADER);
    assertRejected(BODY, null, SECRET, Reason.MISSING_SIGNATURE_HEADER);
  }

  @Test
  public void missingSecretIsRejected() {
    assertRejected(BODY, HEADER, "", Reason.MISSING_SECRET);
    assertRejected(BODY, HEADER, null, Reason.MISSING_SECRET);
  }

  @Test
  public void headerWithoutV0SchemeIsRejected() {
    assertRejected(BODY, "t=4102444800", SECRET, Reason.MISSING_SIGNATURE_SCHEME);
  }

  @Test
  public void headerWithoutTimestampIsRejected() {
    assertRejected(
      BODY,
      "v0=01fc9faf23a5c155fbab279c4e0acb2ae059a567b14c10a19bd9f99c88315e99",
      SECRET,
      Reason.MISSING_SIGNATURE_SCHEME
    );
  }

  @Test
  public void validSignatureOverNonJsonBodyIsAnInvalidPayload() throws Exception {
    String timestamp = Long.toString(now());

    assertRejected("not json", sign(timestamp, "not json"), SECRET, Reason.INVALID_PAYLOAD);
    assertRejected("", sign(timestamp, ""), SECRET, Reason.INVALID_PAYLOAD);
    assertRejected("{} {}", sign(timestamp, "{} {}"), SECRET, Reason.INVALID_PAYLOAD);
  }

  @Test
  public void exceptionIsAnApiExceptionWithAnErrorBody() {
    ElevenlabsApiApiException e = assertThrows(ElevenlabsApiApiException.class, () ->
      WebhookVerifier.constructEvent(BODY, "", SECRET)
    );

    assertEquals(400, e.statusCode());
    assertTrue(e.body() instanceof Map);
    assertEquals("Missing signature header", ((Map<?, ?>) e.body()).get("error"));
  }
}
