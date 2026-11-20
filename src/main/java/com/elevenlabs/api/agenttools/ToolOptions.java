package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.elevenlabs.api.types.PreToolSpeechMode;
import com.elevenlabs.api.types.ResponseFilter;
import com.elevenlabs.api.types.ToolCallSoundBehavior;
import com.elevenlabs.api.types.ToolCallSoundType;
import com.elevenlabs.api.types.ToolErrorHandlingMode;
import com.elevenlabs.api.types.ToolExecutionMode;
import com.elevenlabs.api.types.ToolInterruptionMode;
import com.elevenlabs.api.types.WebhookToolApiSchemaConfigInputContentType;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Options for a tool, passed to {@code options()}. Every kind takes the common options; the webhook and client
 * options apply to that kind only, and setting one on another kind is reported by {@code build()} as
 * {@code invalid_option}. Values are the generated SDK types, converted to wire format when applied.
 */
public final class ToolOptions {

  final Map<String, Object> values = new LinkedHashMap<>();

  private ToolOptions put(String key, Object value) {
    if (value == null) {
      values.remove(key);
    } else {
      values.put(key, value);
    }
    return this;
  }

  /** What the LLM calls the tool by. */
  public ToolOptions name(String name) {
    return put("name", name);
  }

  /** When the tool should be used and what it does. */
  public ToolOptions description(String description) {
    return put("description", description);
  }

  /** Seconds to wait for the tool: 5-300 for a webhook tool; 1-120, or -1 with {@code expectsResponse}, for a client tool. */
  public ToolOptions responseTimeoutSecs(Integer responseTimeoutSecs) {
    return put("response_timeout_secs", responseTimeoutSecs);
  }

  /** Whether the user can interrupt while the tool runs. */
  public ToolOptions interruptionMode(ToolInterruptionMode interruptionMode) {
    return put("interruption_mode", interruptionMode);
  }

  /** Whether the agent speaks before calling the tool. */
  public ToolOptions preToolSpeech(PreToolSpeechMode preToolSpeech) {
    return put("pre_tool_speech", preToolSpeech);
  }

  /** A sound played while the tool runs. */
  public ToolOptions toolCallSound(ToolCallSoundType toolCallSound) {
    return put("tool_call_sound", toolCallSound);
  }

  /** When the tool call sound plays. */
  public ToolOptions toolCallSoundBehavior(ToolCallSoundBehavior toolCallSoundBehavior) {
    return put("tool_call_sound_behavior", toolCallSoundBehavior);
  }

  /** How tool errors are passed to the LLM. */
  public ToolOptions toolErrorHandlingMode(ToolErrorHandlingMode toolErrorHandlingMode) {
    return put("tool_error_handling_mode", toolErrorHandlingMode);
  }

  /** Webhook and client: placeholder values for the dynamic variables the tool uses ({@code dynamic_variables.dynamic_variable_placeholders}). */
  public ToolOptions dynamicVariablePlaceholders(Map<String, ?> dynamicVariablePlaceholders) {
    return put("dynamic_variable_placeholders", dynamicVariablePlaceholders);
  }

  /** Webhook and client: when the tool runs relative to the agent's speech. */
  public ToolOptions executionMode(ToolExecutionMode executionMode) {
    return put("execution_mode", executionMode);
  }

  /** Webhook: {@code api_schema.content_type}. */
  public ToolOptions contentType(WebhookToolApiSchemaConfigInputContentType contentType) {
    return put("content_type", contentType);
  }

  /** Webhook: {@code api_schema.response_filter}. */
  public ToolOptions responseFilter(ResponseFilter responseFilter) {
    return put("response_filter", responseFilter);
  }

  /** Webhook: follow HTTP redirects. Needs {@link #followRedirectsAllowedDomains(List)}. */
  public ToolOptions followRedirects(Boolean followRedirects) {
    return put("follow_redirects", followRedirects);
  }

  /** Webhook: the domains redirects may go to. */
  public ToolOptions followRedirectsAllowedDomains(List<String> followRedirectsAllowedDomains) {
    return put(
      "follow_redirects_allowed_domains",
      followRedirectsAllowedDomains == null ? null : new ArrayList<>(followRedirectsAllowedDomains)
    );
  }

  /** Client: wait for the client to respond, and pass its response to the LLM. */
  public ToolOptions expectsResponse(Boolean expectsResponse) {
    return put("expects_response", expectsResponse);
  }

  /** The options set, by snake_case name, in wire format, in the order they were set. */
  Map<String, JsonNode> toWire() {
    Map<String, JsonNode> out = new LinkedHashMap<>();
    for (Map.Entry<String, Object> entry : values.entrySet()) {
      out.put(entry.getKey(), CanonicalJson.toTree(entry.getValue()));
    }
    return out;
  }
}
