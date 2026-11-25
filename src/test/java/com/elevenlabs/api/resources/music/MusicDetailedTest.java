package com.elevenlabs.api.resources.music;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elevenlabs.api.ElevenlabsApiClient;
import com.elevenlabs.api.core.ApiConfig;
import com.elevenlabs.api.core.ElevenlabsApiApiException;
import com.elevenlabs.api.core.ElevenlabsApiException;
import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.core.RequestOptions;
import com.elevenlabs.api.errors.UnprocessableEntityError;
import com.elevenlabs.api.resources.music.requests.BodyComposeMusicWithADetailedResponseV1MusicDetailedPost;
import com.elevenlabs.api.resources.music.types.ComposeDetailedMusicRequestOutputFormat;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Coverage for the hand-written {@link MusicDetailed} and {@link MultipartParser}, mirroring
 * {@code tests/test_music_multipart.py} in the Python SDK and {@code MusicDetailedTest} in the .NET
 * SDK. End-to-end cases go through the real client stack to a loopback MockWebServer that answers
 * with a {@code multipart/mixed} body.
 */
class MusicDetailedTest {

  private static final String BOUNDARY = "--boundary123";

  private static final String METADATA =
    "{\"composition_plan\":{\"positive_global_styles\":[\"pop\"],\"negative_global_styles\":[],\"sections\":[]}," +
    "\"song_metadata\":{\"title\":\"Test\",\"description\":\"A test song\",\"genres\":[\"pop\"],\"languages\":[\"en\"],\"is_explicit\":false}," +
    "\"words_timestamps\":null}";

  /** Every byte value, so an off-by-one at either end of the audio part shows up. */
  private static final byte[] AUDIO = audio();

  private MockWebServer server;

  @BeforeEach
  void setUp() throws IOException {
    server = new MockWebServer();
    server.start();
  }

  @AfterEach
  void tearDown() throws IOException {
    server.shutdown();
  }

  private static byte[] audio() {
    byte[] audio = new byte[1024];
    for (int i = 0; i < audio.length; i++) {
      audio[i] = (byte) i;
    }
    return audio;
  }

  private static byte[] bytes(String text) {
    return text.getBytes(StandardCharsets.UTF_8);
  }

  private static byte[] concat(byte[]... chunks) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    for (byte[] chunk : chunks) {
      out.write(chunk, 0, chunk.length);
    }
    return out.toByteArray();
  }

  private static byte[] multipart(String lineBreak, String json, boolean disposition) {
    StringBuilder head = new StringBuilder()
      .append(BOUNDARY)
      .append(lineBreak)
      .append("Content-Type: application/json")
      .append(lineBreak)
      .append(lineBreak)
      .append(json)
      .append(lineBreak)
      .append(BOUNDARY)
      .append(lineBreak)
      .append("Content-Type: audio/mpeg")
      .append(lineBreak);
    if (disposition) {
      head.append("Content-Disposition: attachment; filename=\"test_song.mp3\"").append(lineBreak);
    }
    head.append(lineBreak);
    return concat(bytes(head.toString()), AUDIO, bytes(lineBreak + BOUNDARY + "--" + lineBreak));
  }

  private static byte[] multipart(String lineBreak) {
    return multipart(lineBreak, METADATA, true);
  }

  private static JsonNode metadata() throws IOException {
    return ObjectMappers.JSON_MAPPER.readTree(METADATA);
  }

  private static BodyComposeMusicWithADetailedResponseV1MusicDetailedPost request() {
    return BodyComposeMusicWithADetailedResponseV1MusicDetailedPost.builder()
      .outputFormat(ComposeDetailedMusicRequestOutputFormat.MP_3_44100_128)
      .prompt("Epic orchestral music")
      .musicLengthMs(10000)
      .build();
  }

  private ElevenlabsApiClient client() {
    return ElevenlabsApiClient.builder()
      .apiKey("test-key")
      .url(server.url("/").toString())
      .maxRetries(0)
      .build();
  }

  private MusicDetailed music() {
    return new MusicDetailed(client());
  }

  private void enqueueMultipart(byte[] body) {
    server.enqueue(
      new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "multipart/mixed; boundary=boundary123")
        .setBody(new Buffer().write(body))
    );
  }

  // End to end, through the client.

  @Test
  void parsesMetadataAndExactAudioWithCrlfLineEndings() throws Exception {
    enqueueMultipart(multipart("\r\n"));

    ComposeDetailedResponse song = music().composeDetailed(request());

    assertEquals(metadata(), song.getJson());
    assertArrayEquals(AUDIO, song.getAudio(), "audio must be byte-for-byte, closing delimiter stripped");
    assertEquals("test_song.mp3", song.getFilename());
  }

  @Test
  void sendsTheGeneratedRequest() throws Exception {
    enqueueMultipart(multipart("\r\n"));

    music().composeDetailed(request());

    RecordedRequest seen = server.takeRequest();
    assertEquals("POST", seen.getMethod());
    assertTrue(seen.getPath().startsWith("/v1/music/detailed"), seen.getPath());
    assertEquals("mp3_44100_128", seen.getRequestUrl().queryParameter("output_format"));
    assertEquals("test-key", seen.getHeader("xi-api-key"));
    JsonNode body = ObjectMappers.JSON_MAPPER.readTree(seen.getBody().readUtf8());
    assertEquals("Epic orchestral music", body.get("prompt").asText());
    assertEquals(10000, body.get("music_length_ms").asInt());
    assertFalse(body.has("output_format"), "output_format is a query parameter, not a body field");
  }

  @Test
  void sendsPerCallRequestOptions() throws Exception {
    enqueueMultipart(multipart("\r\n"));

    music()
      .composeDetailed(request(), RequestOptions.builder().addHeader("X-Test", "yes").build());

    assertEquals("yes", server.takeRequest().getHeader("X-Test"));
  }

  @Test
  void returnsTheSongIdHeader() {
    server.enqueue(
      new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "multipart/mixed; boundary=boundary123")
        .setHeader("song-id", "song_abc")
        .setBody(new Buffer().write(multipart("\r\n")))
    );

    ComposeDetailedResponse song = music().composeDetailed(request());

    assertEquals("song_abc", song.getSongId().orElse(null));
  }

  @Test
  void songIdIsEmptyWithoutTheHeader() {
    enqueueMultipart(multipart("\r\n"));

    assertFalse(music().composeDetailed(request()).getSongId().isPresent());
  }

  @Test
  void skipsAPreambleUsingTheContentTypeBoundary() throws Exception {
    enqueueMultipart(concat(bytes("This is a multipart message.\r\n"), multipart("\r\n")));

    ComposeDetailedResponse song = music().composeDetailed(request());

    assertEquals(metadata(), song.getJson());
    assertArrayEquals(AUDIO, song.getAudio());
  }

  @Test
  void throwsWhenTheBodyEndsMidAudio() {
    byte[] full = multipart("\r\n");
    // Cut inside the audio, so the closing delimiter never arrives.
    enqueueMultipart(
      Arrays.copyOf(full, full.length - ("\r\n" + BOUNDARY + "--\r\n").length() - 100)
    );

    ElevenlabsApiException error = assertThrows(ElevenlabsApiException.class, () ->
      music().composeDetailed(request())
    );
    assertTrue(error.getMessage().contains("incomplete"), error.getMessage());
  }

  @Test
  void apiErrorsKeepTheGeneratedErrorHandling() {
    server.enqueue(
      new MockResponse()
        .setResponseCode(422)
        .setHeader("Content-Type", "application/json")
        .setBody("{\"detail\":[{\"loc\":[\"body\"],\"msg\":\"bad\",\"type\":\"value_error\"}]}")
    );

    assertThrows(UnprocessableEntityError.class, () -> music().composeDetailed(request()));
  }

  @Test
  void theTimeoutCoversAStalledBody() {
    server.enqueue(
      new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "multipart/mixed; boundary=boundary123")
        .setBody(new Buffer().write(multipart("\r\n")))
        .setBodyDelay(5, TimeUnit.SECONDS)
    );

    ElevenlabsApiException error = assertThrows(ElevenlabsApiException.class, () ->
      music()
        .composeDetailed(
          request(),
          RequestOptions.builder().timeout(200, TimeUnit.MILLISECONDS).build()
        )
    );
    assertFalse(error instanceof ElevenlabsApiApiException, "a stalled body has no error status");
    assertInstanceOf(IOException.class, error.getCause());
  }

  @Test
  void acceptsTheApiConfigAClientWasBuiltFrom() throws Exception {
    enqueueMultipart(multipart("\r\n"));
    ApiConfig config = ApiConfig.builder().baseUrl(server.url("/").toString()).build();

    ComposeDetailedResponse song = new MusicDetailed(config).composeDetailed(request());

    assertEquals(metadata(), song.getJson());
    assertThrows(IllegalArgumentException.class, () ->
      new MusicDetailed(new ElevenlabsApiClient(config))
    );
  }

  /**
   * Documents why {@link MusicDetailed} exists: the generated method hands back the raw multipart
   * body, unsplit. If this starts failing, the generated method may now parse the parts.
   */
  @Test
  void theGeneratedMethodReturnsTheUnsplitMultipartBody() throws Exception {
    byte[] body = multipart("\r\n");
    enqueueMultipart(body);

    try (InputStream stream = client().music().composeDetailed(request())) {
      assertArrayEquals(body, stream.readAllBytes());
    }
  }

  // The parser on its own.

  @Test
  void parsesLfLineEndings() throws Exception {
    MultipartParser.Parsed parsed = MultipartParser.parse(multipart("\n"), null);

    assertEquals("Test", parsed.json.path("song_metadata").path("title").asText());
    assertArrayEquals(AUDIO, parsed.audio);
  }

  @Test
  void defaultsTheFilenameWithoutContentDisposition() {
    MultipartParser.Parsed parsed = MultipartParser.parse(
      multipart("\r\n", METADATA, false),
      "boundary123"
    );

    assertEquals(MultipartParser.DEFAULT_FILENAME, parsed.filename);
    assertEquals("generated_music.mp3", parsed.filename);
    assertArrayEquals(AUDIO, parsed.audio);
  }

  @Test
  void readsAnUnquotedFilename() {
    assertEquals("song.mp3", MultipartParser.filename("attachment; filename=song.mp3; size=1"));
    assertEquals(MultipartParser.DEFAULT_FILENAME, MultipartParser.filename(null));
  }

  @Test
  void parsesPrettyPrintedJson() throws Exception {
    String pretty = ObjectMappers.JSON_MAPPER.writerWithDefaultPrettyPrinter()
      .writeValueAsString(metadata())
      .replace("\r\n", "\n")
      .replace("\n", "\r\n");

    MultipartParser.Parsed parsed = MultipartParser.parse(multipart("\r\n", pretty, true), null);

    assertEquals(metadata(), parsed.json);
  }

  @Test
  void throwsWithoutAJsonPart() {
    byte[] body = concat(
      bytes(BOUNDARY + "\r\nContent-Type: audio/mpeg\r\n\r\n"),
      AUDIO,
      bytes("\r\n" + BOUNDARY + "--\r\n")
    );

    ElevenlabsApiException error = assertThrows(ElevenlabsApiException.class, () ->
      MultipartParser.parse(body, null)
    );
    assertTrue(error.getMessage().contains("JSON part"), error.getMessage());
  }

  @Test
  void throwsWithoutAnAudioPart() {
    byte[] body = bytes(
      BOUNDARY + "\r\nContent-Type: application/json\r\n\r\n{}\r\n" + BOUNDARY + "--\r\n"
    );

    ElevenlabsApiException error = assertThrows(ElevenlabsApiException.class, () ->
      MultipartParser.parse(body, null)
    );
    assertTrue(error.getMessage().contains("audio part"), error.getMessage());
  }

  @Test
  void throwsWhenTheJsonPartIsNotAnObject() {
    byte[] body = concat(
      bytes(BOUNDARY + "\r\nContent-Type: application/json\r\n\r\n[1]\r\n"),
      bytes(BOUNDARY + "\r\nContent-Type: audio/mpeg\r\n\r\n"),
      AUDIO,
      bytes("\r\n" + BOUNDARY + "--\r\n")
    );

    ElevenlabsApiException error = assertThrows(ElevenlabsApiException.class, () ->
      MultipartParser.parse(body, null)
    );
    assertTrue(error.getMessage().contains("not an object"), error.getMessage());
  }

  @Test
  void throwsOnAPartialClosingBoundary() {
    byte[] full = multipart("\r\n");
    // Ends in "...\r\n--boundary12".
    byte[] cut = Arrays.copyOf(full, full.length - "3--\r\n".length());

    ElevenlabsApiException error = assertThrows(ElevenlabsApiException.class, () ->
      MultipartParser.parse(cut, "boundary123")
    );
    assertTrue(error.getMessage().contains("incomplete"), error.getMessage());
  }

  @Test
  void throwsWhenTheBodyStopsAfterTheLastDelimiterDashes() {
    byte[] body = concat(
      bytes(
        BOUNDARY +
        "\r\nContent-Type: application/json\r\n\r\n{}\r\n" +
        BOUNDARY +
        "\r\nContent-Type: audio/mpeg\r\n\r\n"
      ),
      AUDIO
    );

    ElevenlabsApiException error = assertThrows(ElevenlabsApiException.class, () ->
      MultipartParser.parse(body, "boundary123")
    );
    assertTrue(error.getMessage().contains("incomplete"), error.getMessage());
    assertThrows(ElevenlabsApiException.class, () ->
      MultipartParser.parse(concat(body, bytes("\r\n--")), "boundary123")
    );
  }

  @Test
  void skipsAPreambleWithoutAContentTypeBoundary() {
    byte[] body = concat(
      bytes(
        "preamble\r\n--b\r\nContent-Type: application/json\r\n\r\n{\"a\":1}\r\n--b\r\nContent-Type: audio/mpeg\r\n\r\n"
      ),
      AUDIO,
      bytes("\r\n--b--\r\n")
    );

    MultipartParser.Parsed parsed = MultipartParser.parse(body, "b");

    assertEquals(1, parsed.json.get("a").asInt());
    assertArrayEquals(AUDIO, parsed.audio);
  }

  @Test
  void skipsTransportPaddingAfterEachDelimiter() throws Exception {
    String padded = new String(multipart("\r\n"), StandardCharsets.ISO_8859_1).replace(
      BOUNDARY + "\r\n",
      BOUNDARY + " \t\r\n"
    );

    MultipartParser.Parsed parsed = MultipartParser.parse(
      padded.getBytes(StandardCharsets.ISO_8859_1),
      "boundary123"
    );

    assertEquals(metadata(), parsed.json);
    assertArrayEquals(AUDIO, parsed.audio);
    assertEquals("test_song.mp3", parsed.filename);
  }

  @Test
  void readsAPartWithNoHeaders() {
    byte[] body = concat(
      bytes(
        BOUNDARY + "\r\nContent-Type: application/json\r\n\r\n{\"a\":1}\r\n" + BOUNDARY + "\r\n\r\n"
      ),
      AUDIO,
      bytes("\r\n" + BOUNDARY + "--\r\n")
    );

    MultipartParser.Parsed parsed = MultipartParser.parse(body, null);

    assertEquals(1, parsed.json.get("a").asInt());
    assertArrayEquals(AUDIO, parsed.audio);
    assertEquals(MultipartParser.DEFAULT_FILENAME, parsed.filename);
  }

  @Test
  void findsTheAudioPartBeforeTheJsonPart() {
    byte[] body = concat(
      bytes(BOUNDARY + "\r\nContent-Type: audio/mpeg\r\n\r\n"),
      AUDIO,
      bytes(
        "\r\n" +
        BOUNDARY +
        "\r\nContent-Type: application/json\r\n\r\n{\"a\":1}\r\n" +
        BOUNDARY +
        "--\r\n"
      )
    );

    MultipartParser.Parsed parsed = MultipartParser.parse(body, null);

    assertEquals(1, parsed.json.get("a").asInt());
    assertArrayEquals(AUDIO, parsed.audio);
  }

  @Test
  void readsTheBoundaryParameter() {
    assertEquals("abc", MultipartParser.boundaryFrom("multipart/mixed; boundary=\"abc\""));
    assertEquals(
      "----boundaryf00",
      MultipartParser.boundaryFrom("multipart/mixed; boundary=----boundaryf00")
    );
    assertEquals("abc", MultipartParser.boundaryFrom("multipart/mixed; BOUNDARY=abc; charset=x"));
    assertNull(MultipartParser.boundaryFrom("application/json"));
    assertNull(MultipartParser.boundaryFrom(null));
  }

  @Test
  void rejectsABodyWithoutABoundary() {
    assertThrows(ElevenlabsApiException.class, () ->
      MultipartParser.parse(bytes("not multipart"), null)
    );
  }
}
