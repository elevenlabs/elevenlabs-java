package com.elevenlabs.api.agentworkflows;

/** Options for a new {@link Workflow}. */
public final class WorkflowOptions {

  Boolean preventSubagentLoops;

  /** Stops the conversation from re-entering a subagent it already visited. */
  public WorkflowOptions preventSubagentLoops(Boolean preventSubagentLoops) {
    this.preventSubagentLoops = preventSubagentLoops;
    return this;
  }
}
