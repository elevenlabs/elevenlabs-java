package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.agentworkflows.Workflow;
import com.elevenlabs.api.types.AgentPlatformSettingsRequestModel;
import com.elevenlabs.api.types.ConversationalConfig;
import com.elevenlabs.api.types.Llm;
import com.elevenlabs.api.types.TtsConversationalModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The settings an {@link Agent} can be given in code, for its constructor and {@link Agent#update(AgentOptions)}.
 * Every setting is optional; one left unset (or set to {@code null}) leaves the agent's value as it is.
 *
 * <pre>{@code
 * new AgentOptions()
 *     .prompt("You are a support agent for Acme.")
 *     .firstMessage("Hi, how can I help?")
 *     .tools(SystemTool.endCall(), showOrder)
 *     .workflow(wf);
 * }</pre>
 */
public final class AgentOptions {

  String name;
  String prompt;
  String firstMessage;
  String language;
  Llm llm;
  Double temperature;
  String voiceId;
  TtsConversationalModel ttsModel;
  List<String> tags;
  List<Tool> tools;
  List<String> toolIds;
  Workflow workflow;
  ConversationalConfig conversationConfig;
  AgentPlatformSettingsRequestModel platformSettings;

  /** Renames the agent. Only {@link Agent#update(AgentOptions)} reads it; the constructor takes the name itself. */
  public AgentOptions name(String name) {
    this.name = name;
    return this;
  }

  /** The agent's instructions: {@code conversation_config.agent.prompt.prompt}. */
  public AgentOptions prompt(String prompt) {
    this.prompt = prompt;
    return this;
  }

  /** What the agent says first: {@code conversation_config.agent.first_message}. */
  public AgentOptions firstMessage(String firstMessage) {
    this.firstMessage = firstMessage;
    return this;
  }

  /** {@code conversation_config.agent.language}, for example {@code "en"}. */
  public AgentOptions language(String language) {
    this.language = language;
    return this;
  }

  /** {@code conversation_config.agent.prompt.llm}. */
  public AgentOptions llm(Llm llm) {
    this.llm = llm;
    return this;
  }

  /** {@code conversation_config.agent.prompt.temperature}. */
  public AgentOptions temperature(Double temperature) {
    this.temperature = temperature;
    return this;
  }

  /** {@code conversation_config.tts.voice_id}. */
  public AgentOptions voiceId(String voiceId) {
    this.voiceId = voiceId;
    return this;
  }

  /** {@code conversation_config.tts.model_id}. */
  public AgentOptions ttsModel(TtsConversationalModel ttsModel) {
    this.ttsModel = ttsModel;
    return this;
  }

  /** The agent's tags. */
  public AgentOptions tags(List<String> tags) {
    this.tags = tags == null ? null : new ArrayList<>(tags);
    return this;
  }

  /**
   * Tools the agent can use anywhere. Webhook and client tools are created or updated by {@code agents().push()} and
   * attached by id; system tools go into {@code prompt.built_in_tools}. Replaces the agent's tools on {@code update()}.
   */
  public AgentOptions tools(List<? extends Tool> tools) {
    this.tools = tools == null ? null : new ArrayList<Tool>(tools);
    return this;
  }

  /** {@link #tools(List)} for a few tools. */
  public AgentOptions tools(Tool... tools) {
    return tools(Arrays.asList(tools));
  }

  /** Ids of existing tools to attach as they are. {@code agents().push()} doesn't create or change them. */
  public AgentOptions toolIds(List<String> toolIds) {
    this.toolIds = toolIds == null ? null : new ArrayList<>(toolIds);
    return this;
  }

  /** The agent's workflow. Its tool nodes can take {@link Tool} objects, which {@code agents().push()} manages too. */
  public AgentOptions workflow(Workflow workflow) {
    this.workflow = workflow;
    return this;
  }

  /**
   * Any other conversation settings, merged under the typed options above. Setting a field here that a typed option
   * also sets is reported by {@code validate()} as {@code option_conflict}. Each call to {@code update()} merges on
   * top of the last.
   */
  public AgentOptions conversationConfig(ConversationalConfig conversationConfig) {
    this.conversationConfig = conversationConfig;
    return this;
  }

  /** Platform settings (privacy, evaluation, widget...), merged the same way. */
  public AgentOptions platformSettings(AgentPlatformSettingsRequestModel platformSettings) {
    this.platformSettings = platformSettings;
    return this;
  }
}
