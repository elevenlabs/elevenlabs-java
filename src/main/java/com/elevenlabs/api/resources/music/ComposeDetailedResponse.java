package com.elevenlabs.api.resources.music;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;
import java.util.Optional;

/**
 * A composed song from {@link MusicDetailed#composeDetailed}: the metadata the API returns with it,
 * the audio file, and its name.
 *
 * <p>Hand-written (see {@code .fernignore}).
 */
public final class ComposeDetailedResponse {

  private final JsonNode json;
  private final byte[] audio;
  private final String filename;
  private final Optional<String> songId;

  public ComposeDetailedResponse(
    JsonNode json,
    byte[] audio,
    String filename,
    Optional<String> songId
  ) {
    this.json = Objects.requireNonNull(json, "json");
    this.audio = Objects.requireNonNull(audio, "audio");
    this.filename = Objects.requireNonNull(filename, "filename");
    this.songId = Objects.requireNonNull(songId, "songId");
  }

  /**
   * The JSON metadata part as the API sent it (snake_case keys, always an object):
   * {@code composition_plan}, {@code song_metadata} (title, description, genres, languages,
   * is_explicit), {@code words_timestamps} (null unless {@code with_timestamps} is set) and, with
   * {@code with_waveform_visual}, {@code waveform_visual}.
   */
  public JsonNode getJson() {
    return json;
  }

  /** The audio file's bytes, in the requested output format. The array is not copied. */
  public byte[] getAudio() {
    return audio;
  }

  /** The file name the API suggests for the audio, or {@code generated_music.mp3}. */
  public String getFilename() {
    return filename;
  }

  /** The {@code song-id} response header, sent when {@code store_for_inpainting} is set. */
  public Optional<String> getSongId() {
    return songId;
  }

  @java.lang.Override
  public String toString() {
    return (
      "ComposeDetailedResponse{json=" +
      json +
      ", audio=" +
      audio.length +
      " bytes, filename=" +
      filename +
      ", songId=" +
      songId.orElse(null) +
      "}"
    );
  }
}
