package com.elevenlabs.api.agentbuilder;

/**
 * Thrown by {@code agents().push()} when an API call fails after it started writing. {@link #getResult()} lists what
 * was written before the failure, so nothing created goes unreported; with a project, those ids are saved to it too.
 * The failed call's exception is the {@link #getCause() cause}.
 */
public final class AgentPushException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final transient AgentPushResult result;

  public AgentPushException(String message, AgentPushResult result, Throwable cause) {
    super(message + ": " + (cause == null ? "unknown error" : cause.getMessage()), cause);
    this.result = result;
  }

  /** What push wrote before the failure. */
  public AgentPushResult getResult() {
    return result;
  }
}
