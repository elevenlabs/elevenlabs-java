package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.types.ToolRequestModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A loaded tool of a type the builder has no constructor for, such as {@code mcp}. Kept exactly as loaded;
 * {@code build()} warns with {@code unknown_tool_type}.
 */
final class OtherTool extends Tool {

  OtherTool(ObjectNode raw) {
    super(raw);
  }

  @Override
  public ToolKind getKind() {
    return ToolKind.OTHER;
  }

  @Override
  Collection<String> allowedOptions() {
    return Collections.emptyList();
  }

  @Override
  List<ToolIssue> validate() {
    JsonNode type = config.get("type");
    List<ToolIssue> issues = new ArrayList<>();
    issues.add(
      new ToolIssue(
        "unknown_tool_type",
        ToolSeverity.WARNING,
        "Tool \"" + getName() + "\" has type " + (type == null ? "none" : type.toString()) + ", which this SDK can't"
          + " edit. It round-trips through toJson(), but build()'s output may be rejected by"
          + " agents().tools().create() and agents().tools().update()",
        null
      )
    );
    return issues;
  }

  @Override
  ObjectNode toWire() {
    return config.deepCopy();
  }

  /** The loaded config as the generated tool config: a member the generated types model, or the unknown member. */
  @Override
  ToolRequestModel toModel(ObjectNode wire) {
    ObjectNode root = ToolWire.NODES.objectNode();
    root.set("tool_config", wire);
    try {
      return ObjectMappers.JSON_MAPPER.treeToValue(root, ToolRequestModel.class);
    } catch (Exception e) {
      throw new ToolValidationException(
        getName(),
        Collections.singletonList(
          ToolIssue.error(
            "invalid_model",
            null,
            "The loaded tool doesn't fit the generated tool config model (" + e.getMessage() + "). Send the toJson()"
              + " output directly"
          )
        )
      );
    }
  }
}
