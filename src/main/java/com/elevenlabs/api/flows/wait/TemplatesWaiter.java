package com.elevenlabs.api.flows.wait;

// Hand-written (listed in .fernignore). Mirrors client.flows().templates() so template runs are
// reached as flows.templates().runs().createAndWait(...), like the generated client.

/** Holds {@link #runs()}. Get one from {@link FlowsWaiter#templates()}. */
public final class TemplatesWaiter {

  private final TemplateRunsWaiter runs;

  TemplatesWaiter(TemplateRunsWaiter runs) {
    this.runs = runs;
  }

  /** {@code createAndWait} for {@code client.flows().templates().runs()}. */
  public TemplateRunsWaiter runs() {
    return runs;
  }
}
