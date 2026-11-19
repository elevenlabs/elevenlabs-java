package com.elevenlabs.api.agentworkflows;

import java.util.Collections;
import java.util.List;

/** Thrown by {@code build()}, {@code toJson()} and {@code toJsonString()} when the workflow is invalid. Lists every problem found. */
public final class WorkflowValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final List<WorkflowIssue> issues;

  public WorkflowValidationException(List<WorkflowIssue> issues) {
    super(message(issues));
    this.issues = Collections.unmodifiableList(issues);
  }

  /** Every error found, in the order it was found. */
  public List<WorkflowIssue> getIssues() {
    return issues;
  }

  private static String message(List<WorkflowIssue> issues) {
    StringBuilder sb = new StringBuilder("Invalid workflow:");
    for (WorkflowIssue issue : issues) {
      sb.append("\n  - ").append(issue.getMessage());
    }
    return sb.toString();
  }
}
