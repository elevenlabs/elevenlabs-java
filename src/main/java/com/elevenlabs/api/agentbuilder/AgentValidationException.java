package com.elevenlabs.api.agentbuilder;

import java.util.Collections;
import java.util.List;

/**
 * Thrown by {@link Agent#validate()}, {@link Agent#toJson} and {@link Agent#build} when the agent is invalid, and by
 * {@code agents().push()} before any request. Lists every problem found.
 */
public final class AgentValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String agentName;
  private final List<AgentIssue> issues;

  public AgentValidationException(String agentName, List<AgentIssue> issues) {
    super(message(agentName, issues));
    this.agentName = agentName;
    this.issues = Collections.unmodifiableList(issues);
  }

  /** The name of the agent that failed. */
  public String getAgentName() {
    return agentName;
  }

  /** Every error found, in the order it was found. */
  public List<AgentIssue> getIssues() {
    return issues;
  }

  private static String message(String agentName, List<AgentIssue> issues) {
    StringBuilder sb = new StringBuilder("Invalid agent \"").append(agentName).append("\":");
    for (AgentIssue issue : issues) {
      sb.append("\n  - ").append(issue.getMessage());
    }
    return sb.toString();
  }
}
