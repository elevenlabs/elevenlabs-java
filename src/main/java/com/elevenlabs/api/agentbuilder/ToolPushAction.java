package com.elevenlabs.api.agentbuilder;

import java.util.Locale;

/** What {@code agents().push()} did to one tool. */
public enum ToolPushAction {
  /** The tool was new, so it was created. */
  CREATED,
  /** The tool existed and differed, so it was updated in place. */
  UPDATED,
  /** The server already had the tool as defined; no request was made. */
  UNCHANGED,
  /** The agent used the tool before and no longer does. It isn't deleted. */
  DETACHED;

  /** {@code "created"}, {@code "updated"}, {@code "unchanged"} or {@code "detached"}, as the spec spells it. */
  public String wireName() {
    return name().toLowerCase(Locale.ROOT);
  }
}
