package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.types.PositionInput;
import com.elevenlabs.api.types.TransferTypeEnum;
import com.elevenlabs.api.types.UuiTransferConfig;
import com.elevenlabs.api.types.WorkflowPhoneNumberNodeModelInputCustomSipHeadersItem;
import com.elevenlabs.api.types.WorkflowPhoneNumberNodeModelInputPostDialDigits;
import java.util.List;

/**
 * Options for a {@code transfer()} node. The phone/SIP options apply to phone and SIP destinations, the rest to
 * agent destinations; setting one that doesn't apply is reported by {@code build()} as {@code invalid_option}.
 */
public final class TransferOptions {

  final OptionMap values = new OptionMap();

  /** Position in the workflow editor. Nodes without one are laid out automatically. */
  public TransferOptions position(PositionInput position) {
    values.put("position", position);
    return this;
  }

  /** Phone/SIP: how the call is transferred. */
  public TransferOptions transferType(TransferTypeEnum transferType) {
    values.put("transfer_type", transferType);
    return this;
  }

  /** Phone/SIP: SIP headers sent with the transfer. */
  public TransferOptions customSipHeaders(List<WorkflowPhoneNumberNodeModelInputCustomSipHeadersItem> customSipHeaders) {
    values.put("custom_sip_headers", customSipHeaders);
    return this;
  }

  /** Phone/SIP: play a dial tone during a SIP REFER transfer. */
  public TransferOptions sipReferPlayDialtone(Boolean sipReferPlayDialtone) {
    values.put("sip_refer_play_dialtone", sipReferPlayDialtone);
    return this;
  }

  /** Phone/SIP: User-to-User Information sent with the transfer. */
  public TransferOptions uui(UuiTransferConfig uui) {
    values.put("uui", uui);
    return this;
  }

  /** Phone/SIP: digits dialled once the call connects. */
  public TransferOptions postDialDigits(WorkflowPhoneNumberNodeModelInputPostDialDigits postDialDigits) {
    values.put("post_dial_digits", postDialDigits);
    return this;
  }

  /** Agent: delay before the transfer, in milliseconds. */
  public TransferOptions delayMs(Integer delayMs) {
    values.put("delay_ms", delayMs);
    return this;
  }

  /** Agent: message spoken before the transfer. */
  public TransferOptions transferMessage(String transferMessage) {
    values.put("transfer_message", transferMessage);
    return this;
  }

  /** Agent: whether the receiving agent speaks its first message. */
  public TransferOptions enableTransferredAgentFirstMessage(Boolean enableTransferredAgentFirstMessage) {
    values.put("enable_transferred_agent_first_message", enableTransferredAgentFirstMessage);
    return this;
  }

  /** Agent: keep the client's TTS overrides after the transfer. */
  public TransferOptions preserveClientTtsOverrides(Boolean preserveClientTtsOverrides) {
    values.put("preserve_client_tts_overrides", preserveClientTtsOverrides);
    return this;
  }

  /** Agent: keep the current voice settings after the transfer. */
  public TransferOptions preserveVoiceSettings(Boolean preserveVoiceSettings) {
    values.put("preserve_voice_settings", preserveVoiceSettings);
    return this;
  }
}
