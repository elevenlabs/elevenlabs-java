package com.elevenlabs.api.resources.music;

import com.elevenlabs.api.core.ElevenlabsApiException;
import com.elevenlabs.api.core.ObjectMappers;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits the {@code multipart/mixed} body of {@code POST /v1/music/detailed} into its JSON metadata
 * part and its audio part. The generator models this response as JSON, so the generated
 * {@code MusicClient.composeDetailed} can't decode it.
 *
 * <p>Works on bytes throughout, so the audio is returned exactly as sent: the line break before each
 * delimiter and the closing {@code --boundary--} are stripped, never left on the end of the audio.
 *
 * <p>Hand-written (see {@code .fernignore}), mirroring {@code parseMultipart} in the TypeScript SDK and
 * the parsers in the other ElevenLabs SDKs.
 */
final class MultipartParser {

  /** The file name used when the audio part doesn't suggest one. */
  static final String DEFAULT_FILENAME = "generated_music.mp3";

  private static final byte[] CRLF = { '\r', '\n' };
  private static final byte[] LF = { '\n' };
  private static final byte[] DASH_DASH = { '-', '-' };
  private static final Pattern BOUNDARY_PATTERN = Pattern.compile(
    "boundary=(?:\"([^\"]*)\"|([^;\\s]*))",
    Pattern.CASE_INSENSITIVE
  );
  private static final Pattern FILENAME_PATTERN = Pattern.compile(
    "filename=\"([^\"]+)\"|filename=([^;\\s]+)",
    Pattern.CASE_INSENSITIVE
  );

  private MultipartParser() {}

  static final class Parsed {

    final JsonNode json;
    final byte[] audio;
    final String filename;

    Parsed(JsonNode json, byte[] audio, String filename) {
      this.json = json;
      this.audio = audio;
      this.filename = filename;
    }
  }

  private static final class Part {

    final Map<String, String> headers;
    final byte[] body;
    /** Whether the part had the blank line that separates its headers from its body. */
    final boolean separated;

    Part(Map<String, String> headers, byte[] body, boolean separated) {
      this.headers = headers;
      this.body = body;
      this.separated = separated;
    }

    String contentType() {
      String type = headers.get("content-type");
      return type == null ? "" : type;
    }
  }

  /** The {@code boundary} parameter of a {@code Content-Type} header, unquoted, or null. */
  static String boundaryFrom(String contentType) {
    if (contentType == null) {
      return null;
    }
    Matcher match = BOUNDARY_PATTERN.matcher(contentType);
    if (!match.find()) {
      return null;
    }
    String value = match.group(1) != null ? match.group(1) : match.group(2);
    return value == null || value.isEmpty() ? null : value;
  }

  /**
   * @param body The response body.
   * @param boundary The {@code boundary} parameter of the response's {@code Content-Type}, or null.
   *     Without it the delimiter is read from the body's first line.
   * @throws ElevenlabsApiException The body isn't a complete multipart body with a JSON part and an
   *     audio part, including when it ends before the closing {@code --boundary--} (truncated).
   */
  static Parsed parse(byte[] body, String boundary) {
    byte[] delimiter = delimiterFor(body, boundary);

    // The first delimiter starts the body or a line: anything before it is a preamble, which is ignored.
    int pos;
    if (startsWithAt(body, 0, delimiter)) {
      pos = 0;
    } else {
      int found = indexOf(body, concat(LF, delimiter), 0);
      if (found < 0) {
        throw new ElevenlabsApiException(
          "Could not find the multipart boundary in the music response"
        );
      }
      pos = found + 1;
    }
    // The body uses one line ending throughout; read it from the end of the first delimiter line,
    // past any transport padding (spaces or tabs, which MIME allows after a delimiter).
    byte[] lineBreak = startsWithAt(body, skipPadding(body, pos + delimiter.length), CRLF)
      ? CRLF
      : LF;
    byte[] breakThenDelimiter = concat(lineBreak, delimiter);

    List<Part> parts = new ArrayList<>();
    while (true) {
      int after = pos + delimiter.length;
      if (startsWithAt(body, after, DASH_DASH)) {
        break; // Closing delimiter.
      }
      after = skipPadding(body, after);
      int start = startsWithAt(body, after, lineBreak) ? after + lineBreak.length : after;
      int next = indexOf(body, breakThenDelimiter, start);
      // Every part ends at a delimiter, and the body at `--boundary--`; the API always sends it. A
      // body that stops first was cut off, so its last part (the audio, usually) is incomplete.
      if (next < 0) {
        throw new ElevenlabsApiException(
          "The music response ended before its closing multipart boundary; it is incomplete"
        );
      }
      parts.add(splitPart(body, start, next, lineBreak));
      pos = next + lineBreak.length;
    }

    Part jsonPart = null;
    Part audioPart = null;
    for (Part part : parts) {
      boolean isJson = part.contentType().toLowerCase(Locale.ROOT).startsWith("application/json");
      if (jsonPart == null && isJson) {
        jsonPart = part;
      } else if (audioPart == null && !isJson) {
        audioPart = part;
      }
    }

    if (jsonPart == null) {
      throw new ElevenlabsApiException("Could not find the JSON part in the music response");
    }
    JsonNode json;
    try {
      int[] range = trimAscii(jsonPart.body);
      json = ObjectMappers.JSON_MAPPER.readTree(
        Arrays.copyOfRange(jsonPart.body, range[0], range[1])
      );
    } catch (IOException e) {
      throw new ElevenlabsApiException(
        "Could not parse the JSON part of the music response: " + e.getMessage(),
        e
      );
    }
    if (json == null || !json.isObject()) {
      throw new ElevenlabsApiException("The JSON part of the music response is not an object");
    }
    if (audioPart == null || !audioPart.separated) {
      throw new ElevenlabsApiException("Could not find the audio part in the music response");
    }

    return new Parsed(json, audioPart.body, filename(audioPart.headers.get("content-disposition")));
  }

  private static byte[] delimiterFor(byte[] body, String boundary) {
    if (boundary != null && !boundary.isEmpty()) {
      return ("--" + boundary).getBytes(StandardCharsets.US_ASCII);
    }
    int firstBreak = indexOf(body, LF, 0);
    int[] range = trimAscii(Arrays.copyOfRange(body, 0, firstBreak < 0 ? body.length : firstBreak));
    byte[] delimiter = Arrays.copyOfRange(body, range[0], range[1]);
    if (!startsWithAt(delimiter, 0, DASH_DASH) || delimiter.length < 3) {
      throw new ElevenlabsApiException(
        "Could not find the multipart boundary in the music response"
      );
    }
    return delimiter;
  }

  /** Spaces and tabs after a delimiter are MIME transport padding; returns the offset past them. */
  private static int skipPadding(byte[] body, int offset) {
    while (offset < body.length && (body[offset] == ' ' || body[offset] == '\t')) {
      offset++;
    }
    return offset;
  }

  /** Splits {@code body[start..end)} into headers and body, copying only the part's body. */
  private static Part splitPart(byte[] body, int start, int end, byte[] lineBreak) {
    // A part with no headers starts with the blank line that ends them.
    if (startsWithAt(body, start, lineBreak) && start + lineBreak.length <= end) {
      return new Part(
        new HashMap<String, String>(),
        Arrays.copyOfRange(body, start + lineBreak.length, end),
        true
      );
    }
    byte[] part = Arrays.copyOfRange(body, start, end);
    int separator = indexOf(part, concat(lineBreak, lineBreak), 0);
    if (separator < 0) {
      return new Part(headers(part, lineBreak), new byte[0], false);
    }
    return new Part(
      headers(Arrays.copyOfRange(part, 0, separator), lineBreak),
      Arrays.copyOfRange(part, separator + 2 * lineBreak.length, part.length),
      true
    );
  }

  private static Map<String, String> headers(byte[] block, byte[] lineBreak) {
    Map<String, String> headers = new HashMap<>();
    String text = new String(block, StandardCharsets.UTF_8);
    String separator = new String(lineBreak, StandardCharsets.US_ASCII);
    for (String line : text.split(Pattern.quote(separator), -1)) {
      int colon = line.indexOf(':');
      if (colon > 0) {
        headers.put(
          line.substring(0, colon).trim().toLowerCase(Locale.ROOT),
          line.substring(colon + 1).trim()
        );
      }
    }
    return headers;
  }

  static String filename(String disposition) {
    if (disposition != null) {
      Matcher match = FILENAME_PATTERN.matcher(disposition);
      if (match.find()) {
        String name = match.group(1) != null ? match.group(1) : match.group(2);
        if (name != null && !name.isEmpty()) {
          return name;
        }
      }
    }
    return DEFAULT_FILENAME;
  }

  private static int indexOf(byte[] haystack, byte[] needle, int start) {
    if (needle.length == 0 || start > haystack.length) {
      return -1;
    }
    outer:
    for (int i = start; i <= haystack.length - needle.length; i++) {
      for (int j = 0; j < needle.length; j++) {
        if (haystack[i + j] != needle[j]) {
          continue outer;
        }
      }
      return i;
    }
    return -1;
  }

  private static boolean startsWithAt(byte[] haystack, int offset, byte[] prefix) {
    if (offset < 0 || offset + prefix.length > haystack.length) {
      return false;
    }
    for (int i = 0; i < prefix.length; i++) {
      if (haystack[offset + i] != prefix[i]) {
        return false;
      }
    }
    return true;
  }

  /** The {@code [start, end)} range of {@code bytes} without leading and trailing ASCII whitespace. */
  private static int[] trimAscii(byte[] bytes) {
    int start = 0;
    int end = bytes.length;
    while (start < end && isAsciiWhitespace(bytes[start])) {
      start++;
    }
    while (end > start && isAsciiWhitespace(bytes[end - 1])) {
      end--;
    }
    return new int[] { start, end };
  }

  private static boolean isAsciiWhitespace(byte b) {
    return b == ' ' || b == '\t' || b == '\n' || b == '\r' || b == '\f';
  }

  private static byte[] concat(byte[] first, byte[] second) {
    byte[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }
}
