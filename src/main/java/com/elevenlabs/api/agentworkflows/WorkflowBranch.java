package com.elevenlabs.api.agentworkflows;

/**
 * A pending edge out of a node, opened by {@code when()}, {@code onSuccess()} or {@code onFailure()}. It has the
 * node verbs and {@code goTo()}; the edge is added when one of them is called.
 */
public final class WorkflowBranch extends WorkflowChain {

  private final EdgeOrigin from;

  WorkflowBranch(WorkflowGraph graph, EdgeOrigin from) {
    super(graph);
    this.from = from;
  }

  @Override
  EdgeOrigin origin() {
    return from;
  }
}
