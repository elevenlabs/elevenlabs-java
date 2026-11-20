package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.types.ConversationalConfigApiModelWorkflowOverrideInput;
import com.elevenlabs.api.types.EntryBehavior;
import com.elevenlabs.api.types.KnowledgeBaseLocator;
import com.elevenlabs.api.types.PositionInput;
import com.elevenlabs.api.types.TransferTypeEnum;
import com.elevenlabs.api.types.UuiTransferConfig;
import com.elevenlabs.api.types.WorkflowPhoneNumberNodeModelInputCustomSipHeadersItem;
import com.elevenlabs.api.types.WorkflowPhoneNumberNodeModelInputPostDialDigits;
import com.elevenlabs.api.types.WorkflowToolLocatorSchemaOverridesValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Changes for {@link WorkflowNode#update(NodeUpdate)}: the options of the node's own kind, plus {@code prompt}
 * (converse), {@code toolIds} (converse or tool) and {@code destination} (transfer). Setting a field that doesn't
 * apply to the node's kind is reported by {@code build()} as {@code invalid_option}.
 */
public final class NodeUpdate {

  final OptionMap values = new OptionMap();
  Destination destination;
  List<String> toolIds;
  List<Tool> tools;

  /** Converse: the instructions added to the agent's prompt. */
  public NodeUpdate prompt(String prompt) {
    values.put("prompt", prompt);
    return this;
  }

  /** Converse: extra tools. Tool: the tools to run. By id; with {@link #tools(List)}, the ids come first. */
  public NodeUpdate toolIds(List<String> toolIds) {
    this.toolIds = toolIds == null ? null : new ArrayList<>(toolIds);
    return this;
  }

  /**
   * Converse: extra tools. Tool: the tools to run. As {@link Tool} objects, after {@link #toolIds(List)}; setting
   * either replaces the node's tools.
   */
  public NodeUpdate tools(List<? extends Tool> tools) {
    this.tools = tools == null ? null : new ArrayList<Tool>(tools);
    return this;
  }

  /** {@link #tools(List)} for a few tools. */
  public NodeUpdate tools(Tool... tools) {
    return tools(Arrays.asList(tools));
  }

  /**
   * Transfer: where to transfer to. A destination of the other transfer kind changes the node's type and keeps only
   * its position. A new agent destination starts at that agent's default entry point unless it names a node.
   */
  public NodeUpdate destination(Destination destination) {
    this.destination = destination;
    return this;
  }

  public NodeUpdate position(PositionInput position) {
    values.put("position", position);
    return this;
  }

  public NodeUpdate label(String label) {
    values.put("label", label);
    return this;
  }

  public NodeUpdate knowledgeBase(List<KnowledgeBaseLocator> knowledgeBase) {
    values.put("knowledge_base", knowledgeBase);
    return this;
  }

  public NodeUpdate conversationConfig(ConversationalConfigApiModelWorkflowOverrideInput conversationConfig) {
    values.put("conversation_config", conversationConfig);
    return this;
  }

  public NodeUpdate entryBehavior(EntryBehavior entryBehavior) {
    values.put("entry_behavior", entryBehavior);
    return this;
  }

  /** Tool: parameter overrides per tool id (or tool name, for a {@link Tool} object). */
  public NodeUpdate schemaOverrides(Map<String, Map<String, WorkflowToolLocatorSchemaOverridesValue>> schemaOverrides) {
    values.put("schema_overrides", schemaOverrides == null ? null : new LinkedHashMap<>(schemaOverrides));
    return this;
  }

  public NodeUpdate transferType(TransferTypeEnum transferType) {
    values.put("transfer_type", transferType);
    return this;
  }

  public NodeUpdate customSipHeaders(List<WorkflowPhoneNumberNodeModelInputCustomSipHeadersItem> customSipHeaders) {
    values.put("custom_sip_headers", customSipHeaders);
    return this;
  }

  public NodeUpdate sipReferPlayDialtone(Boolean sipReferPlayDialtone) {
    values.put("sip_refer_play_dialtone", sipReferPlayDialtone);
    return this;
  }

  public NodeUpdate uui(UuiTransferConfig uui) {
    values.put("uui", uui);
    return this;
  }

  public NodeUpdate postDialDigits(WorkflowPhoneNumberNodeModelInputPostDialDigits postDialDigits) {
    values.put("post_dial_digits", postDialDigits);
    return this;
  }

  public NodeUpdate delayMs(Integer delayMs) {
    values.put("delay_ms", delayMs);
    return this;
  }

  public NodeUpdate transferMessage(String transferMessage) {
    values.put("transfer_message", transferMessage);
    return this;
  }

  public NodeUpdate enableTransferredAgentFirstMessage(Boolean enableTransferredAgentFirstMessage) {
    values.put("enable_transferred_agent_first_message", enableTransferredAgentFirstMessage);
    return this;
  }

  public NodeUpdate preserveClientTtsOverrides(Boolean preserveClientTtsOverrides) {
    values.put("preserve_client_tts_overrides", preserveClientTtsOverrides);
    return this;
  }

  public NodeUpdate preserveVoiceSettings(Boolean preserveVoiceSettings) {
    values.put("preserve_voice_settings", preserveVoiceSettings);
    return this;
  }
}
