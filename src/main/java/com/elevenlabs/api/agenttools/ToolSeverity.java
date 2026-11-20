package com.elevenlabs.api.agenttools;

/** Severity of a {@link ToolIssue}. Errors stop {@code build()}; warnings are passed to its callback. */
public enum ToolSeverity {
  ERROR,
  WARNING,
}
