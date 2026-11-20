package com.elevenlabs.api.agentbuilder;

import java.util.Optional;

/**
 * A problem found in an agent. The {@link #getCode() code} is one of the stable codes in the cross-SDK spec, for
 * example {@code "missing_name"}, {@code "duplicate_tool_name"} or {@code "unresolved_tool"}.
 */
public final class AgentIssue {

  private final String code;
  private final AgentSeverity severity;
  private final String message;
  private final String path;

  public AgentIssue(String code, AgentSeverity severity, String message, String path) {
    this.code = code;
    this.severity = severity;
    this.message = message;
    this.path = path;
  }

  static AgentIssue error(String code, String message, String path) {
    return new AgentIssue(code, AgentSeverity.ERROR, message, path);
  }

  public String getCode() {
    return code;
  }

  public AgentSeverity getSeverity() {
    return severity;
  }

  public String getMessage() {
    return message;
  }

  /** Where the problem is, for example {@code "tools.get_order"} or {@code "workflow"}, if it has a place. */
  public Optional<String> getPath() {
    return Optional.ofNullable(path);
  }

  @Override
  public String toString() {
    return severity.name().toLowerCase(java.util.Locale.ROOT) + ": " + code + " (" + message + ")";
  }
}
