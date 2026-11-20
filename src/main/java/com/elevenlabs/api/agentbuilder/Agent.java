package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.agenttools.SystemTool;
import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.agenttools.ToolKind;
import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.elevenlabs.api.agentworkflows.Workflow;
import com.elevenlabs.api.agentworkflows.WorkflowValidationException;
import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.resources.agents.requests.BodyCreateAgentV1ConvaiAgentsCreatePost;
import com.elevenlabs.api.types.AgentConfig;
import com.elevenlabs.api.types.AgentPlatformSettingsRequestModel;
import com.elevenlabs.api.types.ConversationalConfig;
import com.elevenlabs.api.types.PromptAgentApiModelInput;
import com.elevenlabs.api.types.TtsConversationalConfigInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * An agent, written as code: its settings, its tools and its workflow in one definition. Push it with
 * {@code client.agents().push(agent)}, which creates or updates the tools and the agent together.
 *
 * <pre>{@code
 * Agent agent = new Agent("Support line", new AgentOptions()
 *     .prompt("You are a support agent for Acme.")
 *     .firstMessage("Hi, how can I help?")
 *     .tools(SystemTool.endCall(), getOrder)
 *     .workflow(wf));
 * AgentPushResult result = client.agents().push(agent);
 * }</pre>
 *
 * <p>An agent holds no client: it can be validated without an API key and pushed to several workspaces.
 */
public final class Agent {

  /** A typed option's API-format path and value, derived from the generated config type. */
  private static final class TypedValue {

    final List<String> path;
    final JsonNode wire;

    TypedValue(List<String> path, JsonNode wire) {
      this.path = path;
      this.wire = wire;
    }
  }

  /** A loaded system tool with its config as loaded, sent as it is while the tool is unchanged. */
  private static final class LoadedSystem {

    final JsonNode raw;
    final String baseline;

    LoadedSystem(JsonNode raw, String baseline) {
      this.raw = raw;
      this.baseline = baseline;
    }
  }

  private static final List<String> PROMPT_PATH = typed(
    ConversationalConfig.builder()
      .agent(AgentConfig.builder().prompt(PromptAgentApiModelInput.builder().prompt("").build()).build())
      .build()
  ).path;

  private String name;
  private final Map<String, TypedValue> typed = new LinkedHashMap<>();
  private JsonNode tags;
  private List<Tool> tools = new ArrayList<>();
  private List<String> toolIds = new ArrayList<>();
  private Workflow workflow;
  /** A loaded workflow nobody has edited yet, kept in API format so it round-trips exactly. */
  private ObjectNode rawWorkflow;
  private Map<String, Tool> toolsById = new LinkedHashMap<>();
  /** The loaded {@code tool_ids}, so a loaded agent keeps its tool order. */
  private List<String> loadedToolIds = new ArrayList<>();
  /** Whether {@code tools} or {@code toolIds} was given (even empty), so the agent sets {@code tool_ids}. */
  private boolean toolListGiven;
  private final Map<Tool, LoadedSystem> loadedSystem = new IdentityHashMap<>();
  /** Settings loaded from a config, in API format. Typed options and raw options are applied on top. */
  private ObjectNode base = Wire.NODES.objectNode();
  private final List<ConversationalConfig> rawConversationConfig = new ArrayList<>();
  private final List<AgentPlatformSettingsRequestModel> rawPlatformSettings = new ArrayList<>();

  /** An agent with just a name. */
  public Agent(String name) {
    this(name, null);
  }

  /** An agent with options. */
  public Agent(String name, AgentOptions options) {
    this.name = name == null ? "" : name;
    if (options != null) {
      update(options);
    }
  }

  /**
   * Loads an agent config in API format (snake_case): a CLI {@code agent_configs/*.json} file, or the
   * {@link #toJson(Map)} of an agent. Fields this SDK doesn't model are kept, so an unedited agent round-trips
   * exactly. Numbers keep their exact value.
   */
  public static Agent fromJson(String json) {
    return fromJson(CanonicalJson.parse(json), null);
  }

  /**
   * {@link #fromJson(String)}, with each id in {@code tool_ids} (and in the workflow's nodes) found in
   * {@code toolsById} as a webhook or client tool becoming that {@link Tool} object, which the agent then manages; the
   * rest, other kinds such as MCP tools included, stay as {@link #getToolIds() tool ids}.
   */
  public static Agent fromJson(String json, Map<String, ? extends Tool> toolsById) {
    return fromJson(CanonicalJson.parse(json), toolsById);
  }

  /** {@link #fromJson(String)} for a parsed config. */
  public static Agent fromJson(JsonNode json) {
    return fromJson(json, null);
  }

  /** {@link #fromJson(String, Map)} for a parsed config. */
  public static Agent fromJson(JsonNode json, Map<String, ? extends Tool> toolsById) {
    if (json == null || !json.isObject()) {
      throw new IllegalArgumentException("An agent config must be a JSON object");
    }
    ObjectNode base = (ObjectNode) json.deepCopy();
    JsonNode name = base.remove("name");
    JsonNode tags = base.remove("tags");
    JsonNode workflow = base.remove("workflow");
    Agent agent = new Agent(name != null && name.isTextual() ? name.textValue() : "");
    agent.base = base;
    agent.tags = tags != null && tags.isArray() ? tags : null;
    if (toolsById != null) {
      agent.toolsById = new LinkedHashMap<String, Tool>(toolsById);
    }
    JsonNode ids = Wire.getPath(base, Wire.TOOL_IDS_PATH);
    if (ids != null && ids.isArray()) {
      Wire.setPath(base, Wire.TOOL_IDS_PATH, null);
      agent.loadedToolIds = Wire.strings(ids);
      for (String id : agent.loadedToolIds) {
        Tool tool = agent.toolsById.get(id);
        // Like pull: a kind push doesn't manage stays attached by its id, so replacing `tools` keeps it.
        if (tool == null || !isManaged(tool)) {
          agent.toolIds.add(id);
        } else {
          agent.tools.add(tool);
        }
      }
      agent.toolListGiven = true;
    }
    // System tools become SystemTool objects, so replacing `tools` replaces them like on an agent built in code.
    JsonNode builtIn = Wire.getPath(base, Wire.BUILT_IN_TOOLS_PATH);
    if (builtIn != null && builtIn.isObject()) {
      List<String> keys = new ArrayList<>();
      builtIn.fieldNames().forEachRemaining(keys::add);
      for (String key : keys) {
        JsonNode raw = builtIn.get(key);
        if (!raw.isObject()) {
          continue;
        }
        try {
          ObjectNode config = Wire.NODES.objectNode();
          config.put("type", "system");
          config.setAll((ObjectNode) raw.deepCopy());
          config.put("type", "system");
          Tool tool = Tool.fromJson(config);
          Optional<SystemTool> system = tool.asSystem();
          if (!system.isPresent() || !key.equals(system.get().getSystemToolType())) {
            continue;
          }
          agent.tools.add(tool);
          agent.loadedSystem.put(tool, new LoadedSystem(raw, safeJson(tool)));
          ((ObjectNode) builtIn).remove(key);
        } catch (RuntimeException e) {
          // Kept in the config as it is.
        }
      }
      if (builtIn.size() == 0) {
        Wire.setPath(base, Wire.BUILT_IN_TOOLS_PATH, null);
      }
    }
    if (workflow != null && workflow.isObject()) {
      agent.rawWorkflow = (ObjectNode) workflow;
    }
    return agent;
  }

  /** The agent's name. */
  public String getName() {
    return name;
  }

  /**
   * The agent's workflow, if it has one. A loaded workflow is turned into a {@link Workflow} the first time it's read
   * here, with its tool ids swapped for the {@link Tool} objects the agent knows, so it can be edited.
   */
  public Optional<Workflow> getWorkflow() {
    if (workflow == null && rawWorkflow != null) {
      workflow = Workflow.fromJson(rawWorkflow, toolsById);
      rawWorkflow = null;
    }
    return Optional.ofNullable(workflow);
  }

  /**
   * Changes the agent. Typed options, {@code name}, {@code tags}, {@code tools}, {@code toolIds} and
   * {@code workflow} replace what was there; {@code conversationConfig} and {@code platformSettings} merge.
   */
  public Agent update(AgentOptions options) {
    if (options == null) {
      return this;
    }
    if (options.name != null) {
      name = options.name;
    }
    if (options.prompt != null) {
      setTyped("prompt", prompt(PromptAgentApiModelInput.builder().prompt(options.prompt).build()));
    }
    if (options.firstMessage != null) {
      setTyped("firstMessage", agentConfig(AgentConfig.builder().firstMessage(options.firstMessage).build()));
    }
    if (options.language != null) {
      setTyped("language", agentConfig(AgentConfig.builder().language(options.language).build()));
    }
    if (options.llm != null) {
      setTyped("llm", prompt(PromptAgentApiModelInput.builder().llm(options.llm).build()));
    }
    if (options.temperature != null) {
      setTyped("temperature", prompt(PromptAgentApiModelInput.builder().temperature(options.temperature).build()));
    }
    if (options.voiceId != null) {
      setTyped(
        "voiceId",
        ConversationalConfig.builder().tts(TtsConversationalConfigInput.builder().voiceId(options.voiceId).build()).build()
      );
    }
    if (options.ttsModel != null) {
      setTyped(
        "ttsModel",
        ConversationalConfig.builder().tts(TtsConversationalConfigInput.builder().modelId(options.ttsModel).build()).build()
      );
    }
    if (options.tags != null) {
      tags = Wire.array(options.tags);
    }
    if (options.tools != null) {
      tools = new ArrayList<>(options.tools);
      toolListGiven = true;
    }
    if (options.toolIds != null) {
      toolIds = new ArrayList<>(options.toolIds);
      toolListGiven = true;
    }
    if (options.workflow != null) {
      workflow = options.workflow;
      rawWorkflow = null;
    }
    if (options.conversationConfig != null) {
      rawConversationConfig.add(options.conversationConfig);
    }
    if (options.platformSettings != null) {
      rawPlatformSettings.add(options.platformSettings);
    }
    return this;
  }

  /** Adds a tool the agent can use anywhere. */
  public Agent addTool(Tool tool) {
    tools.add(tool);
    toolListGiven = true;
    return this;
  }

  /**
   * Every webhook and client tool the agent and its workflow use, each once, in first-use order: the tools
   * {@code agents().push()} creates or updates. System tools aren't included; they live inside the agent's config. Nor
   * are other kinds, such as MCP tools: they stay attached by the id the agent was loaded with.
   */
  public List<Tool> tools() {
    Set<Tool> seen = Collections.newSetFromMap(new IdentityHashMap<Tool, Boolean>());
    List<Tool> result = new ArrayList<>();
    List<Tool> every = new ArrayList<>(tools);
    every.addAll(workflowTools());
    for (Tool tool : every) {
      if (isManaged(tool) && seen.add(tool)) {
        result.add(tool);
      }
    }
    return result;
  }

  /** The ids of existing tools attached as they are, which {@code agents().push()} doesn't manage. */
  public List<String> getToolIds() {
    return new ArrayList<>(toolIds);
  }

  /**
   * Checks everything that doesn't need tool ids: the tools, the workflow, the settings, and that no two tools share
   * a name. CI can run this without an API key.
   *
   * @throws AgentValidationException listing every problem, if the agent is invalid
   */
  public void validate() {
    validate(null);
  }

  /** {@link #validate()}, calling {@code onWarning} for each non-fatal problem, such as a missing prompt. */
  public void validate(Consumer<AgentIssue> onWarning) {
    assemble(null, onWarning);
  }

  /**
   * The agent config in API format (snake_case keys, sorted at every level): the shape of a CLI
   * {@code agent_configs/*.json} file. {@code toolIds} maps each managed tool's name to its id; every webhook and
   * client tool needs one.
   *
   * @throws AgentValidationException listing every problem, if the agent is invalid or a tool has no id
   */
  public ObjectNode toJson(Map<String, String> toolIds) {
    return toJson(toolIds, null);
  }

  /** {@link #toJson(Map)} for an agent without managed tools. */
  public ObjectNode toJson() {
    return toJson(Collections.<String, String>emptyMap(), null);
  }

  /** {@link #toJson(Map)}, calling {@code onWarning} for each non-fatal problem. */
  public ObjectNode toJson(Map<String, String> toolIds, Consumer<AgentIssue> onWarning) {
    return assemble(toolIds == null ? Collections.<String, String>emptyMap() : toolIds, onWarning);
  }

  /** {@link #build(Map)} for an agent without managed tools. */
  public BodyCreateAgentV1ConvaiAgentsCreatePost build() {
    return build(Collections.<String, String>emptyMap());
  }

  /**
   * Validates the agent and returns {@link #toJson(Map)} as the generated create-agent request, the shape
   * {@code agents().create()} accepts. Fields this SDK doesn't model ride along as the model's additional properties.
   *
   * @throws AgentValidationException listing every problem, if the agent is invalid or a tool has no id
   */
  public BodyCreateAgentV1ConvaiAgentsCreatePost build(Map<String, String> toolIds) {
    ObjectNode config = toJson(toolIds);
    try {
      return ObjectMappers.JSON_MAPPER.treeToValue(config, BodyCreateAgentV1ConvaiAgentsCreatePost.class);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(
        "The agent doesn't fit the generated create-agent request: " + e.getOriginalMessage(),
        e
      );
    }
  }

  /** The managed tools the workflow uses: from the edited workflow, or the loaded one's ids the agent knows. */
  private List<Tool> workflowTools() {
    if (workflow != null) {
      return workflow.tools();
    }
    List<Tool> result = new ArrayList<>();
    for (String id : Wire.rawWorkflowToolIds(rawWorkflow)) {
      Tool tool = toolsById.get(id);
      if (tool != null) {
        result.add(tool);
      }
    }
    return result;
  }

  private void setTyped(String key, ConversationalConfig piece) {
    typed.put(key, typed(piece));
  }

  private static ConversationalConfig prompt(PromptAgentApiModelInput prompt) {
    return agentConfig(AgentConfig.builder().prompt(prompt).build());
  }

  private static ConversationalConfig agentConfig(AgentConfig agent) {
    return ConversationalConfig.builder().agent(agent).build();
  }

  /**
   * The API-format path a typed option sets, and its value there, from the piece of the generated config type it
   * fills in. Written against the generated types, so a renamed field fails to compile rather than going astray.
   */
  private static TypedValue typed(ConversationalConfig piece) {
    JsonNode wire = CanonicalJson.toTree(piece);
    List<String> path = Wire.path("conversation_config");
    while (wire.isObject() && wire.size() == 1) {
      String field = wire.fieldNames().next();
      path.add(field);
      wire = wire.get(field);
    }
    return new TypedValue(path, wire);
  }

  /**
   * Builds the API-format config. Without {@code toolIds} (validation only), tools stand under placeholder ids and
   * missing ids aren't reported.
   */
  private ObjectNode assemble(final Map<String, String> ids, Consumer<AgentIssue> onWarning) {
    List<AgentIssue> issues = new ArrayList<>();
    ObjectNode config = base.deepCopy();

    if (name.trim().isEmpty()) {
      issues.add(AgentIssue.error("missing_name", "The agent needs a name", null));
    }

    // Raw options first, so the typed ones win and can be checked against them.
    ObjectNode raw = Wire.NODES.objectNode();
    for (ConversationalConfig settings : rawConversationConfig) {
      try {
        ObjectNode wrapped = Wire.NODES.objectNode();
        wrapped.set("conversation_config", CanonicalJson.toTree(settings));
        Wire.deepMerge(raw, wrapped);
      } catch (RuntimeException e) {
        issues.add(invalidConfig("conversationConfig", e));
      }
    }
    for (AgentPlatformSettingsRequestModel settings : rawPlatformSettings) {
      try {
        ObjectNode wrapped = Wire.NODES.objectNode();
        wrapped.set("platform_settings", CanonicalJson.toTree(settings));
        Wire.deepMerge(raw, wrapped);
      } catch (RuntimeException e) {
        issues.add(invalidConfig("platformSettings", e));
      }
    }
    Wire.deepMerge(config, raw);

    for (Map.Entry<String, TypedValue> entry : typed.entrySet()) {
      TypedValue value = entry.getValue();
      if (Wire.getPath(raw, value.path) != null) {
        issues.add(conflict(entry.getKey(), value.path));
      }
      Wire.setPath(config, value.path, value.wire.deepCopy());
    }

    // Tools: every one validates, and no two different tools share a name.
    Map<String, Tool> byName = new LinkedHashMap<>();
    List<Tool> everyTool = new ArrayList<>(tools);
    everyTool.addAll(workflowTools());
    for (Tool tool : everyTool) {
      Tool other = byName.get(tool.getName());
      if (other != null && other != tool) {
        issues.add(
          AgentIssue.error(
            "duplicate_tool_name",
            "Two different tools are named \"" + tool.getName() + "\"",
            "tools." + tool.getName()
          )
        );
      }
      if (other == null) {
        byName.put(tool.getName(), tool);
        try {
          tool.buildConfig();
        } catch (RuntimeException e) {
          issues.add(AgentIssue.error("invalid_tool", e.getMessage(), "tools." + tool.getName()));
        }
      }
      if (!isManaged(tool) && tool.getKind() != ToolKind.SYSTEM && knownId(tool) == null) {
        issues.add(
          AgentIssue.error(
            "invalid_tool",
            "Tool \"" + tool.getName() + "\" has type \"" + toolType(tool) + "\", which push doesn't create or update:"
              + " load it with its id or attach it with toolIds",
            "tools." + tool.getName()
          )
        );
      }
    }

    List<String> managedIds = new ArrayList<>(toolIds);
    List<Tool> system = new ArrayList<>();
    for (Tool tool : tools) {
      if (tool.getKind() == ToolKind.SYSTEM) {
        system.add(tool);
      } else {
        managedIds.add(idOf(tool, ids));
      }
    }
    if (toolListGiven) {
      if (Wire.getPath(raw, Wire.TOOL_IDS_PATH) != null) {
        issues.add(conflict("tools", Wire.TOOL_IDS_PATH));
      }
      Wire.setPath(config, Wire.TOOL_IDS_PATH, Wire.array(keepOrder(managedIds, loadedToolIds)));
    }
    for (Tool tool : system) {
      List<String> path = new ArrayList<>(Wire.BUILT_IN_TOOLS_PATH);
      path.add(((SystemTool) tool).getSystemToolType());
      if (Wire.getPath(raw, path) != null) {
        issues.add(conflict("tools", path));
      }
      LoadedSystem loaded = loadedSystem.get(tool);
      JsonNode wire = null;
      if (loaded != null && loaded.baseline != null && loaded.baseline.equals(safeJson(tool))) {
        wire = loaded.raw.deepCopy();
      } else {
        try {
          wire = tool.toJson();
        } catch (RuntimeException e) {
          // Reported above as invalid_tool.
        }
      }
      if (wire != null) {
        Wire.setPath(config, path, wire);
      }
    }

    // Workflow: its own checks, plus tool nodes can only run webhook and client tools.
    if (workflow != null) {
      Map<String, String> workflowIds = new LinkedHashMap<>();
      for (Tool tool : workflow.tools()) {
        if (tool.getKind() == ToolKind.SYSTEM) {
          issues.add(
            AgentIssue.error(
              "system_tool_in_workflow",
              "System tool \"" + tool.getName() + "\" can't be used in a workflow node; add it to the agent's tools",
              "workflow.tools." + tool.getName()
            )
          );
        }
        workflowIds.put(tool.getName(), idOf(tool, ids));
      }
      try {
        config.set("workflow", workflow.toJson(workflowIds));
      } catch (WorkflowValidationException e) {
        issues.add(AgentIssue.error("invalid_workflow", e.getMessage(), "workflow"));
      }
    } else if (rawWorkflow != null) {
      // Check the loaded graph without replacing the raw copy, which keeps fields this SDK doesn't model.
      try {
        Workflow.fromJson(rawWorkflow).toJson();
      } catch (RuntimeException e) {
        issues.add(AgentIssue.error("invalid_workflow", e.getMessage(), "workflow"));
      }
      // Its managed tools may have new ids, for example when pushed as a new agent.
      config.set(
        "workflow",
        Wire.mapRawWorkflowToolIds(rawWorkflow.deepCopy(), id -> {
          Tool tool = toolsById.get(id);
          String mapped = tool == null || ids == null || !isManaged(tool) ? null : ids.get(tool.getName());
          return mapped == null ? id : mapped;
        })
      );
    }

    if (ids != null) {
      for (Tool tool : tools()) {
        if (ids.get(tool.getName()) == null) {
          issues.add(
            AgentIssue.error(
              "unresolved_tool",
              "Tool \"" + tool.getName() + "\" has no id yet. Push the agent with agents().push(), or pass toolIds",
              "tools." + tool.getName()
            )
          );
        }
      }
    }

    JsonNode prompt = Wire.getPath(config, PROMPT_PATH);
    if (prompt == null || !prompt.isTextual()) {
      issues.add(
        new AgentIssue(
          "missing_prompt",
          AgentSeverity.WARNING,
          "The agent needs a prompt",
          "conversation_config.agent.prompt.prompt"
        )
      );
    }

    List<AgentIssue> unique = dedupe(issues);
    List<AgentIssue> errors = new ArrayList<>();
    for (AgentIssue issue : unique) {
      if (issue.getSeverity() == AgentSeverity.ERROR) {
        errors.add(issue);
      }
    }
    if (!errors.isEmpty()) {
      throw new AgentValidationException(name, errors);
    }
    if (onWarning != null) {
      for (AgentIssue issue : unique) {
        if (issue.getSeverity() == AgentSeverity.WARNING) {
          onWarning.accept(issue);
        }
      }
    }

    config.put("name", name);
    if (config.get("conversation_config") == null || config.get("conversation_config").isNull()) {
      config.set("conversation_config", Wire.NODES.objectNode());
    }
    if (config.get("platform_settings") == null || config.get("platform_settings").isNull()) {
      config.set("platform_settings", Wire.NODES.objectNode());
    }
    config.set("tags", tags == null ? Wire.NODES.arrayNode() : tags.deepCopy());
    return (ObjectNode) CanonicalJson.canonicalize(config);
  }

  /**
   * The id a tool goes under: a managed tool's from {@code ids}, else a placeholder; any other kind's, the id the agent
   * was loaded with.
   */
  private String idOf(Tool tool, Map<String, String> ids) {
    String id = isManaged(tool) ? (ids == null ? null : ids.get(tool.getName())) : knownId(tool);
    return id == null ? "<tool:" + tool.getName() + ">" : id;
  }

  /** Webhook and client tools: the kinds {@code agents().push()} creates and updates. */
  private static boolean isManaged(Tool tool) {
    return tool.getKind() == ToolKind.WEBHOOK || tool.getKind() == ToolKind.CLIENT;
  }

  /** The id this tool object was loaded under, through {@code fromJson}'s {@code toolsById}. */
  private String knownId(Tool tool) {
    for (Map.Entry<String, Tool> entry : toolsById.entrySet()) {
      if (entry.getValue() == tool) {
        return entry.getKey();
      }
    }
    return null;
  }

  private static String toolType(Tool tool) {
    try {
      JsonNode type = tool.toJson().get("type");
      return type == null || !type.isTextual() ? "unknown" : type.asText();
    } catch (RuntimeException e) {
      return "unknown";
    }
  }

  /** {@code ids} with the ones in {@code loaded} first, in that order, then the rest as they are. */
  private static List<String> keepOrder(List<String> ids, final List<String> loaded) {
    List<Integer> order = new ArrayList<>();
    for (int i = 0; i < ids.size(); i++) {
      order.add(i);
    }
    final List<String> values = ids;
    Collections.sort(order, (a, b) -> {
      int byRank = Integer.compare(rank(values.get(a), loaded), rank(values.get(b), loaded));
      return byRank != 0 ? byRank : Integer.compare(a, b);
    });
    List<String> result = new ArrayList<>();
    for (int i : order) {
      result.add(ids.get(i));
    }
    return result;
  }

  private static int rank(String id, List<String> loaded) {
    int i = loaded.indexOf(id);
    return i == -1 ? loaded.size() : i;
  }

  static String safeJson(Tool tool) {
    try {
      return tool.toJsonString();
    } catch (RuntimeException e) {
      return null;
    }
  }

  private static AgentIssue invalidConfig(String option, RuntimeException e) {
    return AgentIssue.error("invalid_config", "Invalid " + option + ": " + e.getMessage(), option);
  }

  private static AgentIssue conflict(String option, List<String> path) {
    String dotted = String.join(".", path);
    return AgentIssue.error(
      "option_conflict",
      "\"" + option + "\" and the raw config both set " + dotted + "; set it in one place",
      dotted
    );
  }

  private static List<AgentIssue> dedupe(List<AgentIssue> issues) {
    Set<String> seen = new HashSet<>();
    List<AgentIssue> result = new ArrayList<>();
    for (AgentIssue issue : issues) {
      if (seen.add(issue.getCode() + "|" + issue.getPath().orElse("") + "|" + issue.getMessage())) {
        result.add(issue);
      }
    }
    return result;
  }
}
