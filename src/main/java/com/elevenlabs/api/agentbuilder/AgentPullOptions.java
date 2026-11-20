package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.core.RequestOptions;

/** Options for {@code agents().pull()}. */
public final class AgentPullOptions {

  AgentProject project;
  String branchId;
  RequestOptions requestOptions;

  /** Writes the agent and its tools into this project, and records their ids. */
  public AgentPullOptions project(AgentProject project) {
    this.project = project;
    return this;
  }

  /** Pull this branch instead of the main one. */
  public AgentPullOptions branchId(String branchId) {
    this.branchId = branchId;
    return this;
  }

  /** Passed to every API call pull makes. */
  public AgentPullOptions requestOptions(RequestOptions requestOptions) {
    this.requestOptions = requestOptions;
    return this;
  }
}
