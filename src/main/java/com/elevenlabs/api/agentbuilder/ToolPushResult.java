package com.elevenlabs.api.agentbuilder;

import java.util.Objects;

/** One tool's id and what {@code agents().push()} did to it. */
public final class ToolPushResult {

  private final String id;
  private final ToolPushAction action;

  public ToolPushResult(String id, ToolPushAction action) {
    this.id = id;
    this.action = action;
  }

  public String getId() {
    return id;
  }

  public ToolPushAction getAction() {
    return action;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof ToolPushResult)) {
      return false;
    }
    ToolPushResult that = (ToolPushResult) other;
    return Objects.equals(id, that.id) && action == that.action;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, action);
  }

  @Override
  public String toString() {
    return "{id=" + id + ", action=" + action.wireName() + "}";
  }
}
