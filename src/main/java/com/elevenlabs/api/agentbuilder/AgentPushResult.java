package com.elevenlabs.api.agentbuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * What {@code agents().push()} did: the agent's id, whether it was created or updated, and each tool it touched, by
 * name. In an {@link AgentPushException}'s partial result, {@link #getAgentId()} and {@link #getAction()} are
 * {@code null} if the failure came before the agent was written.
 */
public final class AgentPushResult {

  String agentId;
  AgentPushAction action;
  final Map<String, ToolPushResult> tools = new LinkedHashMap<>();

  AgentPushResult() {}

  /** The agent's id; {@code null} in a partial result from before the agent was written. */
  public String getAgentId() {
    return agentId;
  }

  /** {@code CREATED} or {@code UPDATED}; {@code null} in a partial result from before the agent was written. */
  public AgentPushAction getAction() {
    return action;
  }

  /** Each tool push touched, by name, in the order it touched them. */
  public Map<String, ToolPushResult> getTools() {
    return Collections.unmodifiableMap(tools);
  }

  @Override
  public String toString() {
    return "{agentId=" + agentId + ", action=" + (action == null ? null : action.wireName()) + ", tools=" + tools + "}";
  }
}
