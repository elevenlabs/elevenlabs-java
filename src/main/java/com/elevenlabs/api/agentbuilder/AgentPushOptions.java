package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.core.RequestOptions;

/** Options for {@code agents().push()}. */
public final class AgentPushOptions {

  String agentId;
  AgentProject project;
  String branchId;
  String versionDescription;
  RequestOptions requestOptions;

  /**
   * The agent to update. Without it (and without a project that knows the agent), push creates a new agent, so keep
   * the returned id. Not allowed with {@link #project(AgentProject)}, which records the id itself.
   */
  public AgentPushOptions agentId(String agentId) {
    this.agentId = agentId;
    return this;
  }

  /**
   * An agents-as-code project in the CLI's layout. Its {@code agents.json} and {@code tools.json} say which agent and
   * tools on the server this definition is; push records new ids there and writes the configs it sent.
   */
  public AgentPushOptions project(AgentProject project) {
    this.project = project;
    return this;
  }

  /** Update this branch of the agent instead of its main branch. */
  public AgentPushOptions branchId(String branchId) {
    this.branchId = branchId;
    return this;
  }

  /** Describes the new version, for agents with versioning. */
  public AgentPushOptions versionDescription(String versionDescription) {
    this.versionDescription = versionDescription;
    return this;
  }

  /** Passed to every API call push makes. */
  public AgentPushOptions requestOptions(RequestOptions requestOptions) {
    this.requestOptions = requestOptions;
    return this;
  }
}
