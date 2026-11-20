package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.types.PositionInput;
import com.elevenlabs.api.types.WorkflowToolLocatorSchemaOverridesValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Options for a {@code tool()} node. */
public final class ToolOptions {

  final OptionMap values = new OptionMap();
  List<Tool> tools;

  /**
   * {@link Tool} objects to run, after the tool ids. Their ids are filled in when the workflow is built with tool
   * ids, as {@code agents().push()} does.
   */
  public ToolOptions tools(List<? extends Tool> tools) {
    this.tools = tools == null ? null : new ArrayList<Tool>(tools);
    return this;
  }

  /** {@link #tools(List)} for a few tools. */
  public ToolOptions tools(Tool... tools) {
    return tools(Arrays.asList(tools));
  }

  /** Position in the workflow editor. Nodes without one are laid out automatically. */
  public ToolOptions position(PositionInput position) {
    values.put("position", position);
    return this;
  }

  /**
   * Parameter overrides per tool id (or tool name, for a {@link Tool} object), keyed by dotted parameter path
   * ({@code "path_params.order_id"}).
   */
  public ToolOptions schemaOverrides(Map<String, Map<String, WorkflowToolLocatorSchemaOverridesValue>> schemaOverrides) {
    values.put("schema_overrides", schemaOverrides == null ? null : new LinkedHashMap<>(schemaOverrides));
    return this;
  }
}
