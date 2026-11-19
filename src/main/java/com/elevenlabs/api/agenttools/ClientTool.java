package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A tool that sends an event to the user's client.
 *
 * <pre>{@code
 * ClientTool showMap = Tool.client("show_store_map", "Show the caller a map of the nearest store")
 *   .param("store_id", Schema.string().describe("The store to show"))
 *   .options(new ToolOptions().expectsResponse(false));
 * }</pre>
 */
public final class ClientTool extends Tool {

  private static final Set<String> ALLOWED = withCommonOptions(
    "dynamic_variable_placeholders",
    "execution_mode",
    "expects_response"
  );

  /**
   * The params schema. An object unless a loaded config declared another type, which {@code build()} reports.
   * Loaded {@code parameters} that aren't a JSON object at all stay in the config as loaded.
   */
  private Schema parameters;

  ClientTool(ObjectNode config, Schema parameters) {
    super(config);
    this.parameters = parameters;
  }

  @Override
  public ToolKind getKind() {
    return ToolKind.CLIENT;
  }

  /**
   * The params, as an object schema, for reading. Empty if the tool has none, or if loaded params aren't an object.
   */
  public Optional<ObjectSchema> getParametersSchema() {
    return parameters instanceof ObjectSchema ? Optional.of((ObjectSchema) parameters) : Optional.<ObjectSchema>empty();
  }

  /**
   * Sets the param {@code name}, or replaces it in place. Required unless the schema is {@code optional()}. Any
   * schema kind. Loaded params that aren't an object are left as loaded, and {@code build()} reports them.
   */
  public ClientTool param(String name, Schema schema) {
    if (parameters == null && !config.has("parameters")) {
      parameters = Schema.object().property(name, schema);
    } else if (parameters instanceof ObjectSchema) {
      parameters = ((ObjectSchema) parameters).property(name, schema);
    }
    return this;
  }

  /** Removes the param {@code name}. Does nothing if there is none. Removing the last one leaves {@code parameters} out. */
  public ClientTool removeParam(String name) {
    if (parameters instanceof ObjectSchema && ((ObjectSchema) parameters).propertySchema(name).isPresent()) {
      ObjectSchema remaining = ((ObjectSchema) parameters).without(name);
      parameters = remaining.getPropertyNames().isEmpty() ? null : remaining;
    }
    return this;
  }

  /** Merges options for client tools. An option that doesn't apply is reported by {@code build()}. */
  @Override
  public ClientTool options(ToolOptions options) {
    applyOptions(options);
    return this;
  }

  @Override
  Collection<String> allowedOptions() {
    return ALLOWED;
  }

  @Override
  List<ToolIssue> validate() {
    List<ToolIssue> issues = new ArrayList<>();
    if (parameters != null) {
      if (parameters.getKind() != SchemaKind.OBJECT) {
        issues.add(ToolIssue.error("body_not_object", "params", "The params schema must be an object"));
      }
      // Checks the params object's own requireAnyOf()/requireAllOf() and every param under it.
      ToolValidation.checkSchema(parameters, "params", false, issues);
    }
    JsonNode timeout = config.get("response_timeout_secs");
    if (timeout != null && timeout.isNumber()) {
      double secs = CanonicalJson.asDouble(timeout);
      if (secs != -1 && (secs < 1 || secs > 120)) {
        issues.add(
          ToolIssue.error("invalid_option", "responseTimeoutSecs", "A client tool's responseTimeoutSecs must be 1-120, or -1")
        );
      }
      if (secs == -1 && !ToolWire.isTrue(config.get("expects_response"))) {
        issues.add(
          ToolIssue.error("invalid_option", "responseTimeoutSecs", "responseTimeoutSecs(-1) needs expectsResponse(true)")
        );
      }
    }
    return issues;
  }

  @Override
  List<ToolIssue> modelIssues() {
    List<ToolIssue> issues = new ArrayList<>();
    if (parameters == null && config.has("parameters")) {
      issues.add(
        ToolIssue.error(
          "invalid_model",
          "params",
          "The loaded parameters aren't a JSON object, so the tool doesn't match the generated tool config model."
            + " Fix it in the source JSON"
        )
      );
    }
    if (parameters != null) {
      ToolValidation.checkModelShape(parameters, "params", issues);
    }
    return issues;
  }

  @Override
  ObjectNode toWire() {
    ObjectNode wire = config.deepCopy();
    wire.put("type", "client");
    if (parameters != null) {
      wire.set("parameters", parameters.toWire());
    }
    return wire;
  }
}
