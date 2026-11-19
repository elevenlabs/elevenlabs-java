package com.elevenlabs.api.agentworkflows;

import java.util.Optional;

/**
 * A problem found in a workflow. The {@link #getCode() code} is one of the stable codes in the cross-SDK spec, for
 * example {@code "duplicate_node_id"} or {@code "unreachable_node"}.
 */
public final class WorkflowIssue {

  private final String code;
  private final WorkflowSeverity severity;
  private final String message;
  private final String nodeId;

  public WorkflowIssue(String code, WorkflowSeverity severity, String message, String nodeId) {
    this.code = code;
    this.severity = severity;
    this.message = message;
    this.nodeId = nodeId;
  }

  public String getCode() {
    return code;
  }

  public WorkflowSeverity getSeverity() {
    return severity;
  }

  public String getMessage() {
    return message;
  }

  /** The node the problem is about, if there is one. */
  public Optional<String> getNodeId() {
    return Optional.ofNullable(nodeId);
  }

  @Override
  public String toString() {
    return severity.name().toLowerCase() + ": " + code + " (" + message + ")";
  }
}
