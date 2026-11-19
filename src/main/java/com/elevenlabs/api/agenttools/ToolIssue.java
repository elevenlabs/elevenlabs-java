package com.elevenlabs.api.agenttools;

import java.util.Optional;

/**
 * A problem found in a tool. The {@link #getCode() code} is one of the stable codes in the cross-SDK spec, for
 * example {@code "missing_url"} or {@code "missing_value_source"}.
 */
public final class ToolIssue {

  private final String code;
  private final ToolSeverity severity;
  private final String message;
  private final String path;

  public ToolIssue(String code, ToolSeverity severity, String message, String path) {
    this.code = code;
    this.severity = severity;
    this.message = message;
    this.path = path;
  }

  static ToolIssue error(String code, String path, String message) {
    return new ToolIssue(code, ToolSeverity.ERROR, message, path);
  }

  public String getCode() {
    return code;
  }

  public ToolSeverity getSeverity() {
    return severity;
  }

  public String getMessage() {
    return message;
  }

  /** Where the problem is, such as {@code "body.reason"} or {@code "pathParams.order_id"}, if it has a place. */
  public Optional<String> getPath() {
    return Optional.ofNullable(path);
  }

  @Override
  public String toString() {
    return severity.name().toLowerCase(java.util.Locale.ROOT) + ": " + code + " (" + message + ")";
  }
}
