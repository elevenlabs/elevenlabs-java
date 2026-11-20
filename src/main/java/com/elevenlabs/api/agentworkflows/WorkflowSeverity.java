package com.elevenlabs.api.agentworkflows;

/** Severity of a {@link WorkflowIssue}. Errors stop {@code build()}; warnings are passed to its callback. */
public enum WorkflowSeverity {
  ERROR,
  WARNING,
}
