package com.elevenlabs.api.agentbuilder;

/**
 * Thrown when a project's files can't be read or written: a missing or malformed file, a path that leads outside the
 * project, or a format the SDK can't read yet (YAML and TOML configs).
 */
public final class AgentProjectException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AgentProjectException(String message) {
    super(message);
  }

  public AgentProjectException(String message, Throwable cause) {
    super(message, cause);
  }
}
