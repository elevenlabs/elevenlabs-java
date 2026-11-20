package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.elevenlabs.api.types.SearchStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code params} a system tool constructor takes, such as {@code SystemTool.skipTurn(new
 * SystemToolOptions.SkipTurn().waitTimeoutSecs(15.0))}. One nested class per system tool type that has params.
 */
public final class SystemToolOptions {

  private SystemToolOptions() {}

  /** Shared storage: params by wire name, in the order they were set. */
  abstract static class Params {

    final Map<String, Object> values = new LinkedHashMap<>();

    void put(String key, Object value) {
      if (value == null) {
        values.remove(key);
      } else {
        values.put(key, value);
      }
    }

    Map<String, JsonNode> toWire() {
      Map<String, JsonNode> out = new LinkedHashMap<>();
      for (Map.Entry<String, Object> entry : values.entrySet()) {
        out.put(entry.getKey(), CanonicalJson.toTree(entry.getValue()));
      }
      return out;
    }
  }

  /** Params for {@link SystemTool#skipTurn(SkipTurn)}. */
  public static final class SkipTurn extends Params {

    /** Seconds to wait before the agent checks in. -1 waits for the user. */
    public SkipTurn waitTimeoutSecs(Double waitTimeoutSecs) {
      put("wait_timeout_secs", waitTimeoutSecs);
      return this;
    }
  }

  /** Params for {@link SystemTool#languageDetection(LanguageDetection)}. */
  public static final class LanguageDetection extends Params {

    /** Only switch language during the first two user turns. */
    public LanguageDetection onlyAtConversationStart(Boolean onlyAtConversationStart) {
      put("only_at_conversation_start", onlyAtConversationStart);
      return this;
    }
  }

  /** Params for {@link SystemTool#playKeypadTouchTone(PlayKeypadTouchTone)}. */
  public static final class PlayKeypadTouchTone extends Params {

    /** Send tones as RTP events instead of in-band audio. SIP trunk numbers only. */
    public PlayKeypadTouchTone useOutOfBandDtmf(Boolean useOutOfBandDtmf) {
      put("use_out_of_band_dtmf", useOutOfBandDtmf);
      return this;
    }

    /** Don't speak after playing tones, so speech doesn't confuse an IVR. */
    public PlayKeypadTouchTone suppressTurnAfterDtmf(Boolean suppressTurnAfterDtmf) {
      put("suppress_turn_after_dtmf", suppressTurnAfterDtmf);
      return this;
    }
  }

  /** Params for {@link SystemTool#voicemailDetection(VoicemailDetection)}. */
  public static final class VoicemailDetection extends Params {

    /** Message to leave on voicemail. Without one, the call ends. */
    public VoicemailDetection voicemailMessage(String voicemailMessage) {
      put("voicemail_message", voicemailMessage);
      return this;
    }
  }

  /** Params for {@link SystemTool#knowledgeBase(KnowledgeBase)}. */
  public static final class KnowledgeBase extends Params {

    /** The search strategies the agent may use. */
    public KnowledgeBase enabledStrategies(List<SearchStrategy> enabledStrategies) {
      put("enabled_strategies", enabledStrategies == null ? null : new ArrayList<>(enabledStrategies));
      return this;
    }
  }

  /** Params for {@link SystemTool#transferToNumber(TransferToNumber)}. */
  public static final class TransferToNumber extends Params {

    /** Play a message to the caller while they wait for the transfer. */
    public TransferToNumber enableClientMessage(Boolean enableClientMessage) {
      put("enable_client_message", enableClientMessage);
      return this;
    }
  }
}
