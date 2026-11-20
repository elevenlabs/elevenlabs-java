package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.core.ApiConfig;
import com.elevenlabs.api.core.ClientOptions;
import com.elevenlabs.api.resources.agents.AgentsClient;
import okhttp3.OkHttpClient;

/**
 * {@code client.agents()}: the generated {@link AgentsClient}, with {@link #push} and {@link #pull} for agents written
 * as code. {@link com.elevenlabs.api.ElevenLabsClient#agents()} returns this; everything else on the generated client
 * works the same.
 *
 * <p>Hand-written (see {@code .fernignore}).
 */
public class AgentsClientWrapper extends AgentsClient {

  public AgentsClientWrapper(OkHttpClient httpClient, ApiConfig config, ClientOptions clientOptions) {
    super(httpClient, config, clientOptions);
  }

  /** {@link #push(Agent, AgentPushOptions)} creating a new agent. */
  public AgentPushResult push(Agent agent) {
    return push(agent, null);
  }

  /**
   * Creates or updates an agent and every webhook and client tool it uses, in one call.
   *
   * <ol>
   *   <li>Validates the agent, so an invalid one writes nothing.</li>
   *   <li>Creates each tool that's new and updates each one that changed. Which tools already exist comes from the
   *       project's {@code tools.json}, or, without a project, from the tools the agent ({@code agentId}) already
   *       uses, by name: on its main branch, or on {@code branchId} when given. Tools other agents use are never
   *       touched.</li>
   *   <li>Creates the agent, or updates it, with the tools' ids filled in.</li>
   * </ol>
   *
   * <p>Settings the SDK doesn't model, kept from a loaded config, are sent too. Tools the agent stops using are
   * reported as {@code DETACHED}, not deleted.
   *
   * <pre>{@code
   * String agentId = client.agents().push(agent).getAgentId();
   * client.agents().push(agent, new AgentPushOptions().agentId(agentId));   // later: update it
   * client.agents().push(agent, new AgentPushOptions().project(project));   // or let a project track the ids
   * }</pre>
   *
   * @throws AgentValidationException if the agent is invalid, before any API call
   * @throws AgentProjectException if the project has no file for {@code branchId}, before any API call
   * @throws AgentPushException if an API call fails, with what was already written
   */
  public AgentPushResult push(Agent agent, AgentPushOptions options) {
    return new AgentPush(this, tools()).push(agent, options);
  }

  /** {@link #pull(String, AgentPullOptions)} without a project. */
  public Agent pull(String agentId) {
    return pull(agentId, null);
  }

  /**
   * Loads an existing agent as an {@link Agent}, with its webhook and client tools as {@code Tool} objects. With a
   * project, also writes the agent and its tools there, in the CLI's layout, and records their ids.
   *
   * <pre>{@code
   * Agent agent = client.agents().pull(agentId, new AgentPullOptions().project(project));
   * agent.update(new AgentOptions().firstMessage("Hello!"));
   * client.agents().push(agent, new AgentPushOptions().project(project));
   * }</pre>
   */
  public Agent pull(String agentId, AgentPullOptions options) {
    return new AgentPush(this, tools()).pull(agentId, options);
  }
}
