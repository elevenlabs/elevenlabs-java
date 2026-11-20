package com.elevenlabs.api.agenttools;

import java.util.Collections;
import java.util.List;

/**
 * Thrown by a tool's {@code build()}, {@code buildConfig()}, {@code toJson()} and {@code toJsonString()} when the
 * tool is invalid. Lists every problem found.
 */
public final class ToolValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String toolName;
  private final List<ToolIssue> issues;

  public ToolValidationException(String toolName, List<ToolIssue> issues) {
    super(message(toolName, issues));
    this.toolName = toolName;
    this.issues = Collections.unmodifiableList(issues);
  }

  /** The name of the tool that failed. */
  public String getToolName() {
    return toolName;
  }

  /** Every error found, in the order it was found. */
  public List<ToolIssue> getIssues() {
    return issues;
  }

  private static String message(String toolName, List<ToolIssue> issues) {
    StringBuilder sb = new StringBuilder("Invalid tool \"").append(toolName).append("\":");
    for (ToolIssue issue : issues) {
      sb.append("\n  - ").append(issue.getMessage());
    }
    return sb.toString();
  }
}
