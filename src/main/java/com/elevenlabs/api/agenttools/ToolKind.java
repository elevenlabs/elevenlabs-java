package com.elevenlabs.api.agenttools;

/** The kind of a {@link Tool}. */
public enum ToolKind {
  /** The server calls an HTTP endpoint. */
  WEBHOOK,
  /** The agent sends an event to the user's client. */
  CLIENT,
  /** A built-in tool the agent runs itself, such as ending or transferring the call. */
  SYSTEM,
  /** A loaded tool of a type this SDK has no constructor for, such as {@code mcp}. It round-trips but can't be edited. */
  OTHER;

  /** {@code "webhook"}, {@code "client"}, {@code "system"} or {@code "other"}, as the spec and messages spell it. */
  public String wireName() {
    return name().toLowerCase(java.util.Locale.ROOT);
  }
}
