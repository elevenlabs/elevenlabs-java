package com.elevenlabs.api.speechengine;

import com.elevenlabs.api.core.ElevenlabsApiException;
import com.elevenlabs.api.core.ObjectMappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Verifies the JWT that the ElevenLabs Speech Engine API presents when it opens an inbound
 * WebSocket connection to a developer-owned Speech Engine server.
 *
 * <p>The token arrives in the {@code X-ElevenLabs-Speech-Engine-Authorization} header and is an
 * HS256 JWT whose HMAC secret is the SHA-256 hash of the account's API key. Data-residency keys
 * ({@code ..._residency_<region>}) are signed with the base key, so the suffix is stripped before
 * hashing.
 *
 * <p>Hand-written (see {@code .fernignore}): the generator has no concept of the Speech Engine
 * upstream handshake. It mirrors {@code verifySpeechEngineJwt} in the TypeScript and Python SDKs,
 * so every SDK accepts the same tokens.
 */
public final class SpeechEngineJwtVerifier {

  /** The header the Speech Engine API puts the token in. Header names are case-insensitive. */
  public static final String AUTHORIZATION_HEADER = "X-ElevenLabs-Speech-Engine-Authorization";

  private static final String ISSUER = "https://api.elevenlabs.io/convai/speech-engine";
  private static final String SUBJECT = "convai_speech_engine_upstream";

  /** Clock skew, in seconds, allowed on the {@code exp} and {@code iat} claims. */
  private static final long LEEWAY_SECONDS = 60;

  private static final Pattern RESIDENCY_KEY_SUFFIX = Pattern.compile("_residency_[a-z0-9]+$");

  private SpeechEngineJwtVerifier() {}

  /**
   * Verifies an inbound Speech Engine connection token and returns its decoded claims.
   *
   * @param token the {@code X-ElevenLabs-Speech-Engine-Authorization} header value, with or without
   *     a {@code Bearer } prefix
   * @param apiKey the account API key the engine was created with
   * @return the decoded JWT payload (claims)
   * @throws ElevenlabsApiException if the API key is missing, the token is malformed, the signature
   *     does not match, or a claim (issuer, subject, exp, iat) is invalid
   */
  public static Map<String, Object> verify(String token, String apiKey) {
    if (apiKey == null || apiKey.trim().isEmpty()) {
      throw new ElevenlabsApiException("No API key configured for Speech Engine verification");
    }

    String value = token == null ? "" : token.trim();
    if (value.regionMatches(true, 0, "bearer ", 0, 7)) {
      value = value.substring(7).trim();
    }

    String[] parts = value.split("\\.", -1);
    if (parts.length != 3) {
      throw new ElevenlabsApiException("Invalid JWT: expected 3 parts");
    }
    String headerB64 = parts[0];
    String payloadB64 = parts[1];
    String signatureB64 = parts[2];

    JsonNode payload;
    try {
      payload = ObjectMappers.JSON_MAPPER.readTree(base64UrlDecode(payloadB64));
    } catch (Exception e) {
      throw new ElevenlabsApiException("Invalid JWT: failed to decode payload");
    }
    if (payload == null || !payload.isObject()) {
      throw new ElevenlabsApiException("Invalid JWT: payload is not an object");
    }

    byte[] expected = sign(headerB64 + "." + payloadB64, apiKey);
    byte[] actual;
    try {
      actual = base64UrlDecode(signatureB64);
    } catch (IllegalArgumentException e) {
      actual = new byte[0];
    }
    if (!MessageDigest.isEqual(expected, actual)) {
      throw new ElevenlabsApiException(
        "Invalid JWT: signature mismatch — make sure the API key used by your Speech Engine " +
        "server matches the one used to create the engine."
      );
    }

    if (!ISSUER.equals(text(payload, "iss"))) {
      throw new ElevenlabsApiException("Invalid JWT: unexpected issuer");
    }
    if (!SUBJECT.equals(text(payload, "sub"))) {
      throw new ElevenlabsApiException("Invalid JWT: unexpected subject");
    }

    long now = System.currentTimeMillis() / 1000L;
    Long exp = number(payload, "exp");
    if (exp == null) {
      throw new ElevenlabsApiException("Invalid JWT: missing exp claim");
    }
    Long iat = number(payload, "iat");
    if (iat == null) {
      throw new ElevenlabsApiException("Invalid JWT: missing iat claim");
    }
    if (exp + LEEWAY_SECONDS < now) {
      throw new ElevenlabsApiException("Invalid JWT: token has expired");
    }
    if (iat - LEEWAY_SECONDS > now) {
      throw new ElevenlabsApiException("Invalid JWT: iat is in the future");
    }

    return ObjectMappers.JSON_MAPPER.convertValue(
      payload,
      new TypeReference<Map<String, Object>>() {}
    );
  }

  /**
   * Like {@link #verify(String, String)}, but returns {@code false} instead of throwing when the key
   * is missing, the token is absent, or the token is invalid.
   */
  public static boolean isValid(String token, String apiKey) {
    if (token == null || token.isEmpty() || apiKey == null || apiKey.isEmpty()) {
      return false;
    }
    try {
      verify(token, apiKey);
      return true;
    } catch (RuntimeException e) {
      return false;
    }
  }

  /** The key the API signs with: the residency suffix is stripped before hashing. */
  static String normalizeApiKeyForSigning(String apiKey) {
    return RESIDENCY_KEY_SUFFIX.matcher(apiKey.trim()).replaceAll("");
  }

  private static byte[] sign(String signingInput, String apiKey) {
    try {
      byte[] secret = MessageDigest.getInstance("SHA-256").digest(
        normalizeApiKeyForSigning(apiKey).getBytes(StandardCharsets.UTF_8)
      );
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret, "HmacSHA256"));
      return mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
    } catch (GeneralSecurityException e) {
      // SHA-256 and HmacSHA256 are mandatory on every Java platform.
      throw new IllegalStateException("HmacSHA256 is unavailable", e);
    }
  }

  /** Decodes base64url (RFC 7515); padding is optional. */
  private static byte[] base64UrlDecode(String input) {
    return Base64.getUrlDecoder().decode(input);
  }

  private static String text(JsonNode payload, String key) {
    JsonNode node = payload.get(key);
    return node != null && node.isTextual() ? node.asText() : null;
  }

  private static Long number(JsonNode payload, String key) {
    JsonNode node = payload.get(key);
    return node != null && node.isNumber() && node.canConvertToLong() ? node.asLong() : null;
  }
}
