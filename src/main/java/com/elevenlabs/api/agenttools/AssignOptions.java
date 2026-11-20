package com.elevenlabs.api.agenttools;

/** Options for {@link WebhookTool#assign(String, String, AssignOptions)}. */
public final class AssignOptions {

  Boolean sanitize;
  Boolean preserveNativeType;

  /** Remove the value from the tool response before the LLM and the transcript see it. */
  public AssignOptions sanitize(Boolean sanitize) {
    this.sanitize = sanitize;
    return this;
  }

  /** Store lists and objects as they are instead of as JSON strings. */
  public AssignOptions preserveNativeType(Boolean preserveNativeType) {
    this.preserveNativeType = preserveNativeType;
    return this;
  }
}
