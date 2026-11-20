package com.elevenlabs.api.agentbuilder;

import java.util.Locale;

/** What {@code agents().push()} did to the agent: created it, or updated it in place. */
public enum AgentPushAction {
  CREATED,
  UPDATED;

  /** {@code "created"} or {@code "updated"}, as the spec spells it. */
  public String wireName() {
    return name().toLowerCase(Locale.ROOT);
  }
}
