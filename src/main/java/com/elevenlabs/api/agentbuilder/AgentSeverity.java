package com.elevenlabs.api.agentbuilder;

/** How serious an {@link AgentIssue} is: an error stops {@code validate()}, a warning doesn't. */
public enum AgentSeverity {
  ERROR,
  WARNING,
}
