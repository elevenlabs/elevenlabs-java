package com.elevenlabs.api.speechengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elevenlabs.api.core.ElevenlabsApiException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SpeechEngineJwtVerifierTest {

  @Test
  void goldenVectorVerifiesAndDecodes() {
    Map<String, Object> claims = SpeechEngineJwtVerifier.verify(
      TestTokens.TOKEN,
      TestTokens.API_KEY
    );
    assertEquals(TestTokens.ISSUER, claims.get("iss"));
    assertEquals(TestTokens.SUBJECT, claims.get("sub"));
    assertEquals(4102444800L, ((Number) claims.get("exp")).longValue());
    assertEquals(1234567890L, ((Number) claims.get("iat")).longValue());
  }

  @Test
  void bearerPrefixIsAccepted() {
    Map<String, Object> claims = SpeechEngineJwtVerifier.verify(
      "Bearer " + TestTokens.TOKEN,
      TestTokens.API_KEY
    );
    assertEquals(TestTokens.SUBJECT, claims.get("sub"));
  }

  @Test
  void residencyKeyNormalizesToBaseKey() {
    // A residency key must verify the same token: the suffix is stripped before hashing.
    Map<String, Object> claims = SpeechEngineJwtVerifier.verify(
      TestTokens.TOKEN,
      TestTokens.API_KEY + "_residency_in"
    );
    assertEquals(TestTokens.SUBJECT, claims.get("sub"));
  }

  @Test
  void wrongApiKeyIsRejected() {
    assertRejected(TestTokens.TOKEN, "sk_wrongwrongwrongwrongwrongwrongwrong", "signature mismatch");
  }

  @Test
  void tamperedPayloadIsRejected() {
    String[] parts = TestTokens.TOKEN.split("\\.");
    // Swap in a payload with a different subject but keep the original signature.
    String forged = Base64.getUrlEncoder()
      .withoutPadding()
      .encodeToString(
        ("{\"iss\":\"" + TestTokens.ISSUER + "\",\"sub\":\"evil\",\"iat\":1234567890," +
          "\"exp\":4102444800}").getBytes(StandardCharsets.UTF_8)
      );
    assertRejected(
      parts[0] + "." + forged + "." + parts[2],
      TestTokens.API_KEY,
      "signature mismatch"
    );
  }

  @Test
  void expiredTokenIsRejected() throws Exception {
    long now = TestTokens.now();
    String token = TestTokens.sign(
      TestTokens.claims(TestTokens.ISSUER, TestTokens.SUBJECT, now - 3600, now - 120)
    );
    assertRejected(token, TestTokens.API_KEY, "expired");
  }

  @Test
  void expiryWithinLeewayIsAccepted() throws Exception {
    long now = TestTokens.now();
    String token = TestTokens.sign(
      TestTokens.claims(TestTokens.ISSUER, TestTokens.SUBJECT, now - 3600, now - 30)
    );
    assertEquals(TestTokens.SUBJECT, SpeechEngineJwtVerifier.verify(token, TestTokens.API_KEY).get("sub"));
  }

  @Test
  void futureIssuedAtIsRejected() throws Exception {
    long now = TestTokens.now();
    String token = TestTokens.sign(
      TestTokens.claims(TestTokens.ISSUER, TestTokens.SUBJECT, now + 3600, now + 7200)
    );
    assertRejected(token, TestTokens.API_KEY, "iat is in the future");
  }

  @Test
  void wrongIssuerIsRejected() throws Exception {
    String token = TestTokens.sign(
      TestTokens.claims("https://evil.example.com", TestTokens.SUBJECT, 1234567890L, 4102444800L)
    );
    assertRejected(token, TestTokens.API_KEY, "unexpected issuer");
  }

  @Test
  void wrongSubjectIsRejected() throws Exception {
    String token = TestTokens.sign(
      TestTokens.claims(TestTokens.ISSUER, "someone_else", 1234567890L, 4102444800L)
    );
    assertRejected(token, TestTokens.API_KEY, "unexpected subject");
  }

  @Test
  void malformedTokenIsRejected() {
    assertRejected("not-a-jwt", TestTokens.API_KEY, "expected 3 parts");
  }

  @Test
  void missingApiKeyIsRejected() {
    assertRejected(TestTokens.TOKEN, "", "No API key configured");
  }

  @Test
  void isValidNeverThrows() {
    assertTrue(SpeechEngineJwtVerifier.isValid(TestTokens.TOKEN, TestTokens.API_KEY));
    assertFalse(SpeechEngineJwtVerifier.isValid("garbage", TestTokens.API_KEY));
    assertFalse(SpeechEngineJwtVerifier.isValid(null, TestTokens.API_KEY));
    assertFalse(SpeechEngineJwtVerifier.isValid(TestTokens.TOKEN, null));
  }

  private static void assertRejected(String token, String apiKey, String messagePart) {
    ElevenlabsApiException error = assertThrows(ElevenlabsApiException.class, () ->
      SpeechEngineJwtVerifier.verify(token, apiKey)
    );
    assertTrue(
      error.getMessage().contains(messagePart),
      () -> "expected '" + messagePart + "' in: " + error.getMessage()
    );
  }
}
