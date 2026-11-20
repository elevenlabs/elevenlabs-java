package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.agenttools.ToolKind;
import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.elevenlabs.api.core.RequestOptions;
import com.elevenlabs.api.resources.agents.AgentsClient;
import com.elevenlabs.api.resources.agents.requests.BodyCreateAgentV1ConvaiAgentsCreatePost;
import com.elevenlabs.api.resources.agents.requests.GetAgentsRequest;
import com.elevenlabs.api.resources.agents.requests.UpdateAgentRequest;
import com.elevenlabs.api.resources.agents.tools.ToolsClient;
import com.elevenlabs.api.resources.agents.tools.requests.ListToolsRequest;
import com.elevenlabs.api.types.ConversationalConfig;
import com.elevenlabs.api.types.GetAgentResponseModel;
import com.elevenlabs.api.types.ToolRequestModel;
import com.elevenlabs.api.types.ToolResponseModel;
import com.elevenlabs.api.types.ToolResponseModelToolConfig;
import com.elevenlabs.api.types.ToolsResponseModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/** The steps of {@code agents().push()} and {@code agents().pull()}, on the generated agents and tools clients. */
final class AgentPush {

  private final AgentsClient agents;
  private final ToolsClient tools;

  AgentPush(AgentsClient agents, ToolsClient tools) {
    this.agents = agents;
    this.tools = tools;
  }

  AgentPushResult push(Agent agent, AgentPushOptions options) {
    AgentPushOptions opts = options == null ? new AgentPushOptions() : options;
    AgentProject project = opts.project;
    String branchId = opts.branchId;
    RequestOptions requestOptions = opts.requestOptions;
    if (project != null && opts.agentId != null) {
      throw new IllegalArgumentException(
        "Pass agentId or project to agents().push(), not both: the project records the agent's id"
      );
    }
    agent.validate();
    if (project != null) {
      project.checkBranch(agent, branchId);
    }

    List<Tool> managed = agent.tools();
    String agentId = project != null ? project.agentId(agent).orElse(null) : opts.agentId;
    AgentPushResult result = new AgentPushResult();
    Map<String, String> ids = new LinkedHashMap<>();
    String step = "Couldn't read the agent's tools";
    try {
      // The tools the agent already uses. With a project they're matched by recorded id; without one, by name
      // among the tools this agent uses, so a same-named tool another agent uses is never touched.
      List<ToolResponseModel> inUse = new ArrayList<>();
      if (agentId != null && branchId != null) {
        inUse = toolsOnBranch(agentId, branchId, requestOptions);
      } else if (agentId != null) {
        inUse = toolsUsedBy(agentId, requestOptions);
      }
      Map<String, ToolResponseModel> usedByName = new LinkedHashMap<>();
      for (ToolResponseModel remote : inUse) {
        usedByName.put(toolName(remote), remote);
      }

      for (Tool tool : managed) {
        String knownId;
        if (project != null) {
          knownId = project.toolId(tool).orElse(null);
        } else {
          ToolResponseModel named = usedByName.get(tool.getName());
          knownId = named == null ? null : named.getId();
        }
        ToolResponseModel remote = null;
        if (knownId != null) {
          step = "Couldn't read tool \"" + tool.getName() + "\" (" + knownId + ")";
          for (ToolResponseModel candidate : inUse) {
            if (knownId.equals(candidate.getId())) {
              remote = candidate;
              break;
            }
          }
          if (remote == null) {
            remote = tools.get(knownId, requestOptions);
          }
        }
        JsonNode unchanged = project == null ? null : project.unchangedRaw(tool);
        JsonNode config = unchanged != null ? unchanged : tool.toJson();
        if (remote != null && sameTool(remote, tool, config)) {
          ids.put(tool.getName(), remote.getId());
          result.tools.put(tool.getName(), new ToolPushResult(remote.getId(), ToolPushAction.UNCHANGED));
          // The server already matches, but the project may hold an older definition: bring it up to date.
          if (project != null) {
            project.recordTool(tool, remote.getId(), config, false);
          }
          continue;
        }
        // The generated model drops nothing, but the wire config is the exact body: it replaces tool_config.
        ToolRequestModel typed = tool.build();
        ObjectNode body = Wire.NODES.objectNode();
        body.set("tool_config", config);
        if (remote != null) {
          step = "Couldn't update tool \"" + tool.getName() + "\" (" + remote.getId() + ")";
          tools.update(remote.getId(), typed, withBody(requestOptions, body));
          ids.put(tool.getName(), remote.getId());
          result.tools.put(tool.getName(), new ToolPushResult(remote.getId(), ToolPushAction.UPDATED));
        } else {
          step = "Couldn't create tool \"" + tool.getName() + "\"";
          ToolResponseModel created = tools.create(typed, withBody(requestOptions, body));
          ids.put(tool.getName(), created.getId());
          result.tools.put(tool.getName(), new ToolPushResult(created.getId(), ToolPushAction.CREATED));
        }
        if (project != null) {
          project.recordTool(tool, ids.get(tool.getName()), config, false);
        }
      }

      ObjectNode config = agent.toJson(ids);

      // Every id the new config still uses, wherever it is: the agent's tool_ids and the workflow's nodes.
      Set<String> kept = new HashSet<>(Wire.toolIdsIn(config));
      for (ToolResponseModel remote : inUse) {
        String name = toolName(remote);
        if (!kept.contains(remote.getId()) && !result.tools.containsKey(name)) {
          result.tools.put(name, new ToolPushResult(remote.getId(), ToolPushAction.DETACHED));
        }
      }

      // The whole API-format config is the body: as extra body properties it replaces the typed request's top-level
      // fields, so settings this SDK doesn't model, kept from a loaded config, reach the API too.
      RequestOptions body = withBody(requestOptions, config);
      String id;
      String versionId = null;
      if (agentId == null) {
        step = "Couldn't create agent \"" + agent.getName() + "\"";
        BodyCreateAgentV1ConvaiAgentsCreatePost typed = BodyCreateAgentV1ConvaiAgentsCreatePost.builder()
          .conversationConfig(ConversationalConfig.builder().build())
          .build();
        id = agents.create(typed, body).getAgentId();
        result.action = AgentPushAction.CREATED;
      } else {
        step = "Couldn't update agent \"" + agent.getName() + "\" (" + agentId + ")";
        UpdateAgentRequest typed = UpdateAgentRequest.builder()
          .branchId(Optional.ofNullable(branchId))
          .versionDescription(Optional.ofNullable(opts.versionDescription))
          .build();
        GetAgentResponseModel updated = agents.update(agentId, typed, body);
        versionId = updated.getVersionId().orElse(null);
        id = agentId;
        result.action = AgentPushAction.UPDATED;
      }
      result.agentId = id;
      if (project != null) {
        project.recordAgent(agent, id, config, branchId, versionId, false);
      }
    } catch (RuntimeException e) {
      saveQuietly(project);
      throw new AgentPushException(step, result, e);
    }
    if (project != null && project.getDir().isPresent()) {
      project.save();
    }
    return result;
  }

  Agent pull(String agentId, AgentPullOptions options) {
    AgentPullOptions opts = options == null ? new AgentPullOptions() : options;
    AgentProject project = opts.project;
    String branchId = opts.branchId;
    RequestOptions requestOptions = opts.requestOptions;
    if (project != null) {
      project.checkBranchById(agentId, branchId);
    }
    GetAgentResponseModel response = getAgent(agentId, branchId, requestOptions);
    JsonNode live = wire(response);
    // The CLI's pulled-config shape: these keys only, so ids and server metadata stay out of the file.
    ObjectNode config = Wire.NODES.objectNode();
    config.set("name", orElse(live.get("name"), Wire.NODES.nullNode()));
    config.set("conversation_config", orElse(live.get("conversation_config"), Wire.NODES.objectNode()));
    config.set("platform_settings", orElse(live.get("platform_settings"), Wire.NODES.objectNode()));
    config.set("tags", orElse(live.get("tags"), Wire.NODES.arrayNode()));
    JsonNode workflow = live.get("workflow");
    if (workflow != null && workflow.isObject()) {
      config.set("workflow", workflow);
    }

    Map<String, Tool> toolsById = new LinkedHashMap<>();
    Map<String, JsonNode> toolConfigs = new LinkedHashMap<>();
    for (String id : Wire.toolIdsIn(config)) {
      JsonNode wire = toolWire(tools.get(id, requestOptions));
      Tool tool = Tool.fromJson(wire);
      // tools.json lists webhook and client tools; others (MCP...) stay attached by id.
      if (tool.getKind() == ToolKind.WEBHOOK || tool.getKind() == ToolKind.CLIENT) {
        toolsById.put(id, tool);
        toolConfigs.put(id, CanonicalJson.canonicalize(wire));
      }
    }

    Agent agent = Agent.fromJson(config, toolsById);
    if (project != null) {
      for (Map.Entry<String, Tool> entry : toolsById.entrySet()) {
        project.recordTool(entry.getValue(), entry.getKey(), toolConfigs.get(entry.getKey()), true);
      }
      project.recordAgent(agent, agentId, config, branchId, response.getVersionId().orElse(null), true);
      if (project.getDir().isPresent()) {
        project.save();
      }
    }
    return agent;
  }

  /**
   * The tools a branch of the agent uses. The tools API's {@code used_by_agent_id} covers the main branch only, so
   * they come from the branch's config, by id.
   */
  private List<ToolResponseModel> toolsOnBranch(String agentId, String branchId, RequestOptions requestOptions) {
    JsonNode branch = wire(getAgent(agentId, branchId, requestOptions));
    List<ToolResponseModel> result = new ArrayList<>();
    for (String id : Wire.toolIdsIn(branch)) {
      result.add(tools.get(id, requestOptions));
    }
    return result;
  }

  private List<ToolResponseModel> toolsUsedBy(String agentId, RequestOptions requestOptions) {
    List<ToolResponseModel> all = new ArrayList<>();
    String cursor = null;
    do {
      ToolsResponseModel page = tools.list(
        ListToolsRequest.builder().usedByAgentId(agentId).pageSize(100).cursor(Optional.ofNullable(cursor)).build(),
        requestOptions
      );
      all.addAll(page.getTools());
      cursor = page.getHasMore() ? page.getNextCursor().orElse(null) : null;
    } while (cursor != null);
    return all;
  }

  private GetAgentResponseModel getAgent(String agentId, String branchId, RequestOptions requestOptions) {
    return agents.get(agentId, GetAgentsRequest.builder().branchId(Optional.ofNullable(branchId)).build(), requestOptions);
  }

  /**
   * Whether the server already has this tool as defined. Modeled fields are compared through the builder, so both
   * sides normalize alike; the config to send must also be contained in the server's, so a change to a field the
   * builder doesn't model still counts.
   */
  private static boolean sameTool(ToolResponseModel remote, Tool tool, JsonNode config) {
    try {
      String local = ToolDefinition.of(tool);
      return local != null && local.equals(ToolDefinition.of(Tool.fromResponse(remote))) && Wire.isSubset(config, toolWire(remote));
    } catch (RuntimeException e) {
      return false;
    }
  }

  /** A tool response's config in API format, with its {@code type}. */
  private static JsonNode toolWire(ToolResponseModel remote) {
    ToolResponseModelToolConfig toolConfig = remote.getToolConfig();
    JsonNode tree = wire(toolConfig);
    if (tree.isObject() && !tree.has("type")) {
      String type = toolConfig.isWebhook() ? "webhook" : toolConfig.isClient() ? "client" : toolConfig.isSystem() ? "system" : toolConfig.isMcp() ? "mcp" : null;
      if (type != null) {
        ((ObjectNode) tree).put("type", type);
      }
    }
    return tree;
  }

  private static String toolName(ToolResponseModel remote) {
    JsonNode name = toolWire(remote).get("name");
    return name != null && name.isTextual() ? name.textValue() : remote.getId();
  }

  /** A generated response model in API format, without the {@code null}s it writes for fields it didn't get. */
  private static JsonNode wire(Object model) {
    return Wire.withoutNulls(CanonicalJson.toTree(model));
  }

  private static JsonNode orElse(JsonNode value, JsonNode fallback) {
    return value == null || value.isNull() ? fallback : value;
  }

  /** The caller's request options, plus {@code body}'s top-level fields as extra body properties. */
  private static RequestOptions withBody(RequestOptions base, ObjectNode body) {
    RequestOptions.Builder builder = RequestOptions.builder();
    if (base != null) {
      if (base.getTimeout().isPresent()) {
        builder.timeout(base.getTimeout().get(), base.getTimeoutTimeUnit());
      }
      if (base.getMaxRetries().isPresent()) {
        builder.maxRetries(base.getMaxRetries().get());
      }
      Map<String, Supplier<String>> headerSuppliers = base.getHeaderSuppliers();
      for (Map.Entry<String, String> header : base.getHeaders().entrySet()) {
        if (!headerSuppliers.containsKey(header.getKey())) {
          builder.addHeader(header.getKey(), header.getValue());
        }
      }
      for (Map.Entry<String, Supplier<String>> header : headerSuppliers.entrySet()) {
        builder.addHeader(header.getKey(), header.getValue());
      }
      Map<String, Supplier<String>> querySuppliers = base.getQueryParameterSuppliers();
      for (Map.Entry<String, String> query : base.getQueryParameters().entrySet()) {
        if (!querySuppliers.containsKey(query.getKey())) {
          builder.addQueryParameter(query.getKey(), query.getValue());
        }
      }
      for (Map.Entry<String, Supplier<String>> query : querySuppliers.entrySet()) {
        builder.addQueryParameter(query.getKey(), query.getValue());
      }
      builder.bodyProperties(base.getBodyProperties());
    }
    Iterator<Map.Entry<String, JsonNode>> fields = body.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      builder.addBodyProperty(field.getKey(), field.getValue());
    }
    return builder.build();
  }

  private static void saveQuietly(AgentProject project) {
    if (project == null || !project.getDir().isPresent()) {
      return;
    }
    try {
      project.save();
    } catch (RuntimeException e) {
      // The push error is the one to report; the ids it carries are in the thrown result.
    }
  }
}
