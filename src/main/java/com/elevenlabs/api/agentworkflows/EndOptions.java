package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.types.PositionInput;

/** Options for an {@code end()} node. */
public final class EndOptions {

  final OptionMap values = new OptionMap();
  String id;

  /** The node id. Defaults to {@code "end"}, which every {@code end()} without an id shares. */
  public EndOptions id(String id) {
    this.id = id;
    return this;
  }

  /** Position in the workflow editor. Nodes without one are laid out automatically. */
  public EndOptions position(PositionInput position) {
    values.put("position", position);
    return this;
  }
}
