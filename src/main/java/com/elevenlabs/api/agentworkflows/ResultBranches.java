package com.elevenlabs.api.agentworkflows;

import java.util.function.Consumer;

/**
 * The two outcomes of a tool node, for {@link WorkflowNode#onResult(ResultBranches)}. Named rather than
 * positional, so the branches can't be swapped by mistake. Either may be left out.
 */
public final class ResultBranches {

  Consumer<WorkflowBranch> success;
  Consumer<WorkflowBranch> failure;

  /** Routes the branch followed when every tool succeeded. */
  public ResultBranches success(Consumer<WorkflowBranch> success) {
    this.success = success;
    return this;
  }

  /** Routes the branch followed when any tool failed. */
  public ResultBranches failure(Consumer<WorkflowBranch> failure) {
    this.failure = failure;
    return this;
  }
}
