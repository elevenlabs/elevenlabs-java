package com.elevenlabs.api.speechengine;

import com.elevenlabs.api.core.ObjectMappers;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Shared Speech Engine JWT fixtures. */
final class TestTokens {

  /**
   * Shared cross-SDK golden vector (see the PHP/.NET/Rust verifier tests). {@code exp} is far in the
   * future so the token stays valid; every ElevenLabs SDK must accept this exact (apiKey, token)
   * pair. Generated with secret = sha256(apiKey), HS256. The key is a placeholder, not a credential.
   */
  static final String API_KEY = "sk_0123456789abcdef0123456789abcdef";

  static final String TOKEN =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
    ".eyJpc3MiOiJodHRwczovL2FwaS5lbGV2ZW5sYWJzLmlvL2NvbnZhaS9zcGVlY2gtZW5naW5lIiwic3ViIjoiY29udmFpX3NwZWVjaF9lbmdpbmVfdXBzdHJlYW0iLCJpYXQiOjEyMzQ1Njc4OTAsImV4cCI6NDEwMjQ0NDgwMH0" +
    ".UObM4hx1W8NvGViU3aqPv1-IEXhmXgWDgoS9RZZWILk";

  static final String ISSUER = "https://api.elevenlabs.io/convai/speech-engine";
  static final String SUBJECT = "convai_speech_engine_upstream";

  private TestTokens() {}

  static Map<String, Object> claims(String iss, String sub, long iat, long exp) {
    Map<String, Object> claims = new LinkedHashMap<>();
    claims.put("iss", iss);
    claims.put("sub", sub);
    claims.put("iat", iat);
    claims.put("exp", exp);
    return claims;
  }

  /** Signs claims into an HS256 JWT with the test key, the way the API signs. */
  static String sign(Map<String, Object> claims) throws Exception {
    Base64.Encoder b64 = Base64.getUrlEncoder().withoutPadding();
    String header = b64.encodeToString(
      "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8)
    );
    String payload = b64.encodeToString(ObjectMappers.JSON_MAPPER.writeValueAsBytes(claims));
    byte[] secret = MessageDigest.getInstance("SHA-256").digest(
      API_KEY.getBytes(StandardCharsets.UTF_8)
    );
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(secret, "HmacSHA256"));
    String signature = b64.encodeToString(
      mac.doFinal((header + "." + payload).getBytes(StandardCharsets.UTF_8))
    );
    return header + "." + payload + "." + signature;
  }

  static long now() {
    return System.currentTimeMillis() / 1000L;
  }
}
