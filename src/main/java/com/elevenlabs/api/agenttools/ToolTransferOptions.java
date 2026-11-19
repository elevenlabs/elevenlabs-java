package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.elevenlabs.api.types.PhoneNumberTransferCustomSipHeadersItem;
import com.elevenlabs.api.types.PhoneNumberTransferPostDialDigits;
import com.elevenlabs.api.types.TransferTypeEnum;
import com.elevenlabs.api.types.UuiTransferConfig;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Options for one {@link SystemTool#transfer(String, com.elevenlabs.api.agentworkflows.Destination,
 * ToolTransferOptions)} entry, named as the workflow builder's {@code TransferOptions}. The phone/SIP options apply
 * to {@code transferToNumber()}, the agent options to {@code transferToAgent()}; setting one on the other kind is
 * reported by {@code build()} as {@code invalid_option}.
 */
public final class ToolTransferOptions {

  final Map<String, Object> values = new LinkedHashMap<>();

  private ToolTransferOptions put(String key, Object value) {
    if (value == null) {
      values.remove(key);
    } else {
      values.put(key, value);
    }
    return this;
  }

  /** Phone/SIP: how the call is transferred. A SIP URI destination needs {@code TransferTypeEnum.SIP_REFER}. */
  public ToolTransferOptions transferType(TransferTypeEnum transferType) {
    return put("transfer_type", transferType);
  }

  /** Phone/SIP: SIP headers sent with the transfer. */
  public ToolTransferOptions customSipHeaders(List<PhoneNumberTransferCustomSipHeadersItem> customSipHeaders) {
    return put("custom_sip_headers", customSipHeaders == null ? null : new ArrayList<>(customSipHeaders));
  }

  /** Phone/SIP: play a dial tone during a SIP REFER transfer. */
  public ToolTransferOptions sipReferPlayDialtone(Boolean sipReferPlayDialtone) {
    return put("sip_refer_play_dialtone", sipReferPlayDialtone);
  }

  /** Phone/SIP: User-to-User Information sent with the transfer. */
  public ToolTransferOptions uui(UuiTransferConfig uui) {
    return put("uui", uui);
  }

  /** Phone/SIP: digits dialled once the call connects. */
  public ToolTransferOptions postDialDigits(PhoneNumberTransferPostDialDigits postDialDigits) {
    return put("post_dial_digits", postDialDigits);
  }

  /** Agent: delay before the transfer, in milliseconds. */
  public ToolTransferOptions delayMs(Integer delayMs) {
    return put("delay_ms", delayMs);
  }

  /** Agent: message spoken before the transfer. */
  public ToolTransferOptions transferMessage(String transferMessage) {
    return put("transfer_message", transferMessage);
  }

  /** Agent: whether the receiving agent speaks its first message. */
  public ToolTransferOptions enableTransferredAgentFirstMessage(Boolean enableTransferredAgentFirstMessage) {
    return put("enable_transferred_agent_first_message", enableTransferredAgentFirstMessage);
  }

  /** Agent: keep the client's TTS overrides after the transfer. */
  public ToolTransferOptions preserveClientTtsOverrides(Boolean preserveClientTtsOverrides) {
    return put("preserve_client_tts_overrides", preserveClientTtsOverrides);
  }

  /** Agent: keep the current voice settings after the transfer. */
  public ToolTransferOptions preserveVoiceSettings(Boolean preserveVoiceSettings) {
    return put("preserve_voice_settings", preserveVoiceSettings);
  }

  Map<String, JsonNode> toWire() {
    Map<String, JsonNode> out = new LinkedHashMap<>();
    for (Map.Entry<String, Object> entry : values.entrySet()) {
      out.put(entry.getKey(), CanonicalJson.toTree(entry.getValue()));
    }
    return out;
  }
}
