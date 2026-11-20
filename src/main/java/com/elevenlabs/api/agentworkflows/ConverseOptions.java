package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.types.ConversationalConfigApiModelWorkflowOverrideInput;
import com.elevenlabs.api.types.EntryBehavior;
import com.elevenlabs.api.types.KnowledgeBaseLocator;
import com.elevenlabs.api.types.PositionInput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Options for a {@code converse()} (subagent) node. Nested values use the SDK's generated types. */
public final class ConverseOptions {

  final OptionMap values = new OptionMap();
  List<String> toolIds;
  List<Tool> tools;

  /** Position in the workflow editor. Nodes without one are laid out automatically. */
  public ConverseOptions position(PositionInput position) {
    values.put("position", position);
    return this;
  }

  /** Name shown in the workflow editor. Defaults to the node id. */
  public ConverseOptions label(String label) {
    values.put("label", label);
    return this;
  }

  /** Tools available to this subagent, on top of the agent's own ({@code additional_tool_ids}), by id. */
  public ConverseOptions toolIds(List<String> toolIds) {
    this.toolIds = toolIds == null ? null : new ArrayList<>(toolIds);
    return this;
  }

  /**
   * Tools available to this subagent as {@link Tool} objects, listed after {@link #toolIds(List)}. Their ids are
   * filled in when the workflow is built with tool ids, as {@code agents().push()} does.
   */
  public ConverseOptions tools(List<? extends Tool> tools) {
    this.tools = tools == null ? null : new ArrayList<Tool>(tools);
    return this;
  }

  /** {@link #tools(List)} for a few tools. */
  public ConverseOptions tools(Tool... tools) {
    return tools(Arrays.asList(tools));
  }

  /** Knowledge base documents available to this subagent, on top of the agent's own ({@code additional_knowledge_base}). */
  public ConverseOptions knowledgeBase(List<KnowledgeBaseLocator> knowledgeBase) {
    values.put("knowledge_base", knowledgeBase);
    return this;
  }

  /** Overrides for the agent's conversation config while this subagent is active. */
  public ConverseOptions conversationConfig(ConversationalConfigApiModelWorkflowOverrideInput conversationConfig) {
    values.put("conversation_config", conversationConfig);
    return this;
  }

  /** When the subagent starts speaking. */
  public ConverseOptions entryBehavior(EntryBehavior entryBehavior) {
    values.put("entry_behavior", entryBehavior);
    return this;
  }
}
