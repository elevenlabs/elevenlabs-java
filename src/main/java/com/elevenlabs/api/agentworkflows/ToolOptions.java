package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.types.PositionInput;
import com.elevenlabs.api.types.WorkflowToolLocatorSchemaOverridesValue;
import java.util.LinkedHashMap;
import java.util.Map;

/** Options for a {@code tool()} node. */
public final class ToolOptions {

  final OptionMap values = new OptionMap();

  /** Position in the workflow editor. Nodes without one are laid out automatically. */
  public ToolOptions position(PositionInput position) {
    values.put("position", position);
    return this;
  }

  /** Parameter overrides per tool id, keyed by dotted parameter path ({@code "path_params.order_id"}). */
  public ToolOptions schemaOverrides(Map<String, Map<String, WorkflowToolLocatorSchemaOverridesValue>> schemaOverrides) {
    values.put("schema_overrides", schemaOverrides == null ? null : new LinkedHashMap<>(schemaOverrides));
    return this;
  }
}
