package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.elevenlabs.api.types.ToolRequestModel;
import com.elevenlabs.api.types.ToolRequestModelToolConfig;
import com.elevenlabs.api.types.ToolResponseModel;
import com.elevenlabs.api.types.ToolResponseModelToolConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * An agent tool, written as code. Create one with {@link #webhook(String, String)}, {@link #client(String, String)}
 * or a {@link SystemTool} constructor, or load one with {@link #fromJson(String)} / {@link #fromResponse}.
 * {@link #build()} returns the request model {@code agents().tools().create()} and {@code update()} accept;
 * {@link #toJsonString()} the canonical file format the ElevenLabs CLI keeps in {@code tool_configs/*.json}.
 *
 * <p>Building never throws: problems are recorded and reported together by {@code build()}, {@code buildConfig()},
 * {@code toJson()} and {@code toJsonString()} as a {@link ToolValidationException}.
 */
public abstract class Tool {

  static final List<String> COMMON_OPTIONS = Arrays.asList(
    "name",
    "description",
    "response_timeout_secs",
    "interruption_mode",
    "pre_tool_speech",
    "tool_call_sound",
    "tool_call_sound_behavior",
    "tool_error_handling_mode"
  );

  /** The tool config in wire format, except the parts subclasses keep in their own fields, and {@code type}. */
  final ObjectNode config;

  /** Problems found while building, reported by {@code build()}. */
  final List<ToolIssue> issues = new ArrayList<>();

  Tool(ObjectNode config) {
    this.config = config;
  }

  /** A tool the server runs by calling an HTTP endpoint. Set the method and URL with {@code get()}, {@code post()}, ... */
  public static WebhookTool webhook(String name, String description) {
    return new WebhookTool(nameAndDescription(name, description), ToolWire.NODES.objectNode());
  }

  /** A tool that sends an event to the user's client. */
  public static ClientTool client(String name, String description) {
    return new ClientTool(nameAndDescription(name, description), null);
  }

  /**
   * Loads a tool stored in API format (snake_case), for example from {@link #toJsonString()} or a CLI
   * {@code tool_configs/*.json} file: a tool config, or a request or response with a {@code tool_config} key. Numbers
   * keep their exact value.
   */
  public static Tool fromJson(String json) {
    return fromJson(CanonicalJson.parse(json));
  }

  /**
   * Loads a tool from parsed API-format JSON: a tool config, or a request or response with a {@code tool_config} key.
   * Returns a tool of the matching kind; a type the builder has no constructor for loads as {@link ToolKind#OTHER}
   * and is kept exactly as loaded.
   */
  public static Tool fromJson(JsonNode json) {
    if (json == null || !json.isObject()) {
      throw new IllegalArgumentException("A tool must be a JSON object");
    }
    JsonNode raw = json.get("tool_config") != null && json.get("tool_config").isObject() ? json.get("tool_config") : json;
    String type = ToolWire.str(raw.get("type"));
    if (!"webhook".equals(type) && !"client".equals(type) && !"system".equals(type)) {
      return new OtherTool((ObjectNode) raw.deepCopy());
    }
    ObjectNode config = (ObjectNode) ToolWire.stripNulls(raw);
    config.remove("type");
    if ("webhook".equals(type)) {
      return WebhookTool.load(config);
    }
    if ("client".equals(type)) {
      // Params that aren't an object stay in `config`, written back as loaded; build() reports them.
      JsonNode parameters = config.get("parameters");
      Schema schema = null;
      if (parameters != null && parameters.isObject()) {
        schema = Schema.fromModel((ObjectNode) parameters, false, Schema.containerKind((ObjectNode) parameters));
        config.remove("parameters");
      }
      return new ClientTool(config, schema);
    }
    return SystemTool.load(config);
  }

  /** Loads the result of {@code agents().tools().get()}. */
  public static Tool fromResponse(ToolResponseModel response) {
    ToolResponseModelToolConfig toolConfig = response.getToolConfig();
    JsonNode tree = CanonicalJson.toTree(toolConfig);
    if (tree.isObject() && !tree.has("type")) {
      // A config built in code rather than read from the API has no `type` of its own: the union member says it.
      String type = toolConfig.isWebhook() ? "webhook" : toolConfig.isClient() ? "client" : toolConfig.isSystem() ? "system" : toolConfig.isMcp() ? "mcp" : null;
      if (type != null) {
        ((ObjectNode) tree).put("type", type);
      }
    }
    return fromJson(tree);
  }

  /** {@code WEBHOOK}, {@code CLIENT}, {@code SYSTEM}, or {@code OTHER} for a loaded tool of a type this SDK can't edit. */
  public abstract ToolKind getKind();

  /** The tool's name, as the LLM sees it. */
  public String getName() {
    String name = ToolWire.str(config.get("name"));
    return name == null ? "" : name;
  }

  /** Merges options for this kind of tool. An option that doesn't apply is reported by {@code build()}. */
  public Tool options(ToolOptions options) {
    applyOptions(options);
    return this;
  }

  /** This tool as a {@link WebhookTool}, or empty if it is another kind. */
  public Optional<WebhookTool> asWebhook() {
    return this instanceof WebhookTool ? Optional.of((WebhookTool) this) : Optional.<WebhookTool>empty();
  }

  /** This tool as a {@link ClientTool}, or empty if it is another kind. */
  public Optional<ClientTool> asClient() {
    return this instanceof ClientTool ? Optional.of((ClientTool) this) : Optional.<ClientTool>empty();
  }

  /** This tool as a {@link SystemTool}, or empty if it is another kind. */
  public Optional<SystemTool> asSystem() {
    return this instanceof SystemTool ? Optional.of((SystemTool) this) : Optional.<SystemTool>empty();
  }

  /**
   * Validates the tool and returns it in the shape {@code agents().tools().create()} and {@code update()}
   * accept.
   *
   * @throws ToolValidationException listing every problem, if the tool is invalid
   */
  public ToolRequestModel build() {
    return build(null);
  }

  /**
   * {@link #build()}, calling {@code onWarning} once per warning (such as {@code unknown_tool_type}).
   *
   * @throws ToolValidationException listing every problem, if the tool is invalid
   */
  public ToolRequestModel build(Consumer<ToolIssue> onWarning) {
    ObjectNode wire = checkedWire(onWarning);
    List<ToolIssue> modelIssues = modelIssues();
    if (!modelIssues.isEmpty()) {
      throw new ToolValidationException(getName(), modelIssues);
    }
    return toModel(wire);
  }

  /**
   * Validates the tool and returns only its config. Create webhook and client tools with
   * {@code agents().tools().create()} and attach them to an agent by id in {@code prompt.tool_ids}; for a
   * system tool, {@link SystemTool#buildBuiltInConfig()} returns its {@code prompt.built_in_tools} entry.
   *
   * @throws ToolValidationException listing every problem, if the tool is invalid
   */
  public ToolRequestModelToolConfig buildConfig() {
    return build(null).getToolConfig();
  }

  /** {@link #buildConfig()}, calling {@code onWarning} once per warning. */
  public ToolRequestModelToolConfig buildConfig(Consumer<ToolIssue> onWarning) {
    return build(onWarning).getToolConfig();
  }

  /**
   * The tool config in API format (snake_case keys, sorted at every level), the shape of a CLI
   * {@code tool_configs/*.json} file. Validates like {@link #build()}.
   *
   * @throws ToolValidationException listing every problem, if the tool is invalid
   */
  public ObjectNode toJson() {
    return (ObjectNode) CanonicalJson.canonicalize(checkedWire(null));
  }

  /**
   * {@link #toJson()} as a string with 2-space indentation and a trailing newline: the canonical file format,
   * byte-identical to the other ElevenLabs SDKs.
   *
   * @throws ToolValidationException listing every problem, if the tool is invalid
   */
  public String toJsonString() {
    return CanonicalJson.toCanonicalString(checkedWire(null));
  }

  /** The option names (snake_case) this kind accepts. */
  abstract Collection<String> allowedOptions();

  /** Problems that need the finished tool. Reported by {@code toJson()} and {@code build()}. */
  abstract List<ToolIssue> validate();

  /**
   * Loaded input that {@code toJson()} writes back as loaded but the generated request model can't take, so only
   * {@code build()} reports it ({@code invalid_model}).
   */
  List<ToolIssue> modelIssues() {
    return new ArrayList<>();
  }

  /** The finished tool config in wire format, with its {@code type}. */
  abstract ObjectNode toWire();

  /** The generated request model for a finished, validated wire-format config. */
  ToolRequestModel toModel(ObjectNode wire) {
    return ToolModel.convert(getName(), wire);
  }

  void applyOptions(ToolOptions options) {
    Collection<String> allowed = allowedOptions();
    for (Map.Entry<String, JsonNode> entry : options.toWire().entrySet()) {
      if (allowed.contains(entry.getKey())) {
        setOption(entry.getKey(), entry.getValue());
      } else {
        String name = ToolWire.camelCase(entry.getKey());
        issues.add(
          ToolIssue.error(
            "invalid_option",
            name,
            "Option \"" + name + "\" doesn't apply to " + getKind().wireName() + " tools"
          )
        );
      }
    }
  }

  void setOption(String key, JsonNode value) {
    if ("dynamic_variable_placeholders".equals(key)) {
      JsonNode existing = config.get("dynamic_variables");
      ObjectNode variables = existing != null && existing.isObject() ? (ObjectNode) existing : ToolWire.NODES.objectNode();
      variables.set("dynamic_variable_placeholders", value);
      config.set("dynamic_variables", variables);
    } else {
      config.set(key, value);
    }
  }

  /** Validates, reports warnings, and returns the wire-format tool config. */
  ObjectNode checkedWire(Consumer<ToolIssue> onWarning) {
    List<ToolIssue> all = new ArrayList<>(issues);
    all.addAll(validate());
    List<ToolIssue> errors = new ArrayList<>();
    for (ToolIssue issue : all) {
      if (issue.getSeverity() == ToolSeverity.ERROR) {
        errors.add(issue);
      }
    }
    if (!errors.isEmpty()) {
      throw new ToolValidationException(getName(), errors);
    }
    if (onWarning != null) {
      for (ToolIssue issue : all) {
        if (issue.getSeverity() == ToolSeverity.WARNING) {
          onWarning.accept(issue);
        }
      }
    }
    return toWire();
  }

  static Set<String> withCommonOptions(String... names) {
    Set<String> result = new HashSet<>(COMMON_OPTIONS);
    result.addAll(Arrays.asList(names));
    return result;
  }

  private static ObjectNode nameAndDescription(String name, String description) {
    ObjectNode config = ToolWire.NODES.objectNode();
    config.put("name", name);
    config.put("description", description);
    return config;
  }
}
