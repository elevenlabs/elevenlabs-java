package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.agenttools.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Mutable state shared by a {@link Workflow} and every handle created from it. Nodes are kept in wire format, in
 * insertion order.
 */
final class WorkflowGraph {

  private static final JsonNodeFactory NODES = JsonNodeFactory.instance;

  // Canonical option name -> API field, per node type.
  private static final Map<String, Map<String, String>> FIELDS = new HashMap<>();
  private static final Map<String, String> NODE_KIND = new HashMap<>();
  private static final Map<String, String> POSITION_ONLY = fields("position", "position");

  static {
    FIELDS.put("start", POSITION_ONLY);
    FIELDS.put("end", POSITION_ONLY);
    FIELDS.put("tool", POSITION_ONLY);
    FIELDS.put(
      "override_agent",
      fields(
        "position", "position",
        "prompt", "additional_prompt",
        "label", "label",
        "tool_ids", "additional_tool_ids",
        "knowledge_base", "additional_knowledge_base",
        "conversation_config", "conversation_config",
        "entry_behavior", "entry_behavior"
      )
    );
    FIELDS.put(
      "phone_number",
      fields(
        "position", "position",
        "transfer_type", "transfer_type",
        "custom_sip_headers", "custom_sip_headers",
        "sip_refer_play_dialtone", "sip_refer_play_dialtone",
        "uui", "uui",
        "post_dial_digits", "post_dial_digits"
      )
    );
    FIELDS.put(
      "standalone_agent",
      fields(
        "position", "position",
        "delay_ms", "delay_ms",
        "transfer_message", "transfer_message",
        "enable_transferred_agent_first_message", "enable_transferred_agent_first_message",
        "preserve_client_tts_overrides", "preserve_client_tts_overrides",
        "preserve_voice_settings", "preserve_voice_settings"
      )
    );
    NODE_KIND.put("start", "start");
    NODE_KIND.put("end", "end");
    NODE_KIND.put("override_agent", "subagent");
    NODE_KIND.put("tool", "tool");
    NODE_KIND.put("phone_number", "phone/SIP transfer");
    NODE_KIND.put("standalone_agent", "agent transfer");
  }

  private final Map<String, ObjectNode> nodes = new LinkedHashMap<>();

  List<GraphEdge> edges = new ArrayList<>();
  final List<WorkflowIssue> issues = new ArrayList<>();
  String startId = "start_node";
  Boolean preventSubagentLoops;

  /** Each loaded node's {@code edge_order}, as edges. {@code build()} keeps this order and appends edges added since. */
  final Map<String, List<GraphEdge>> loadedOrder = new HashMap<>();

  /** {@link Tool} objects referenced by nodes, by placeholder, in the order they were first used. */
  final Map<String, Tool> toolRefs = new LinkedHashMap<>();

  /** The placeholder a {@link Tool} object stands under in the graph until {@code build()} swaps in its id. */
  static String toolPlaceholder(String name) {
    return "<tool:" + name + ">";
  }

  /** A tool id as shown in messages: the tool's name for a {@link Tool} object's placeholder. */
  static String displayTool(String id) {
    if (id.startsWith("<tool:") && id.endsWith(">")) {
      return id.substring("<tool:".length(), id.length() - 1);
    }
    return id;
  }

  /**
   * Swaps {@link Tool} objects for placeholders, remembering each one. A different tool of the same name conflicts if
   * this node or another node still uses that name; one left behind by a removed or edited node doesn't.
   */
  List<String> refTools(String nodeId, List<Tool> tools) {
    List<String> keys = new ArrayList<>();
    Map<String, Tool> inThisNode = new HashMap<>();
    for (Tool tool : tools) {
      String key = toolPlaceholder(tool.getName());
      Tool known = toolRefs.get(key);
      Tool clash = inThisNode.get(key);
      if (clash == null && known != null && usesPlaceholder(key, nodeId)) {
        clash = known;
      }
      inThisNode.put(key, tool);
      if (clash != null && clash != tool) {
        issue("duplicate_tool_name", "Node \"" + nodeId + "\" uses a different tool also named \"" + tool.getName() + "\"", nodeId);
      }
      toolRefs.put(key, tool);
      keys.add(key);
    }
    return keys;
  }

  private boolean usesPlaceholder(String key, String exceptNodeId) {
    for (Map.Entry<String, ObjectNode> entry : nodes.entrySet()) {
      if (!entry.getKey().equals(exceptNodeId) && nodeToolIds(entry.getValue()).contains(key)) {
        return true;
      }
    }
    return false;
  }

  /** The tool ids (or placeholders) a node uses: a tool node's tools, or a subagent's extra tools. */
  static List<String> nodeToolIds(JsonNode model) {
    List<String> ids = new ArrayList<>();
    JsonNode tools = model.get("tools");
    if (tools != null && tools.isArray()) {
      for (JsonNode tool : tools) {
        JsonNode id = tool.get("tool_id");
        if (id != null && id.isTextual()) {
          ids.add(id.textValue());
        }
      }
    }
    JsonNode extra = model.get("additional_tool_ids");
    if (extra != null && extra.isArray()) {
      for (JsonNode id : extra) {
        if (id.isTextual()) {
          ids.add(id.textValue());
        }
      }
    }
    return ids;
  }

  /** A copy of a node with each tool id (a tool node's tools, a subagent's extra tools) replaced through {@code map}. */
  static ObjectNode mapNodeToolIds(ObjectNode model, Function<String, String> map) {
    ObjectNode node = model.deepCopy();
    String type = typeOf(node);
    JsonNode tools = node.get("tools");
    if ("tool".equals(type) && tools != null && tools.isArray()) {
      for (JsonNode tool : tools) {
        JsonNode id = tool.get("tool_id");
        if (tool.isObject() && id != null && id.isTextual()) {
          ((ObjectNode) tool).put("tool_id", map.apply(id.textValue()));
        }
      }
    }
    JsonNode extra = node.get("additional_tool_ids");
    if ("override_agent".equals(type) && extra != null && extra.isArray()) {
      ArrayNode mapped = NODES.arrayNode();
      for (JsonNode id : extra) {
        mapped.add(id.isTextual() ? NODES.textNode(map.apply(id.textValue())) : id);
      }
      node.set("additional_tool_ids", mapped);
    }
    return node;
  }

  List<String> nodeOrder() {
    return Collections.unmodifiableList(new ArrayList<>(nodes.keySet()));
  }

  boolean containsNode(String id) {
    return nodes.containsKey(id);
  }

  ObjectNode getNode(String id) {
    return nodes.get(id);
  }

  void setNode(String id, ObjectNode model) {
    nodes.put(id, model);
  }

  void removeNode(String id) {
    nodes.remove(id);
  }

  void issue(String code, String message, String nodeId) {
    issues.add(new WorkflowIssue(code, WorkflowSeverity.ERROR, message, nodeId));
  }

  void addNode(String id, ObjectNode model) {
    ObjectNode existing = nodes.get(id);
    if (existing == null) {
      nodes.put(id, model);
    } else if (!("end".equals(typeOf(existing)) && "end".equals(typeOf(model)))) {
      issue("duplicate_node_id", "Node id \"" + id + "\" is used by more than one node", id);
    }
  }

  /**
   * Applies canonical options to a node model, returning a new model. {@code destination}, {@code toolIds},
   * {@code tools} and {@code schemaOverrides} are handled specially; {@code rest} maps through the node kind's field
   * map. {@code tools} are {@link Tool} objects, which stand under placeholders after {@code toolIds}; giving either
   * replaces the node's tools.
   */
  ObjectNode applyOptions(
    String id,
    ObjectNode model,
    Destination destination,
    List<String> toolIds,
    List<Tool> tools,
    JsonNode schemaOverrides,
    Map<String, JsonNode> rest
  ) {
    ObjectNode node = model.deepCopy();
    if (tools != null) {
      List<String> combined = toolIds == null ? new ArrayList<>() : new ArrayList<>(toolIds);
      combined.addAll(refTools(id, tools));
      toolIds = combined;
    }
    if (schemaOverrides != null && schemaOverrides.isObject() && !toolRefs.isEmpty()) {
      // A Tool object's overrides may be keyed by its name.
      ObjectNode byPlaceholder = NODES.objectNode();
      Iterator<Map.Entry<String, JsonNode>> entries = schemaOverrides.fields();
      while (entries.hasNext()) {
        Map.Entry<String, JsonNode> entry = entries.next();
        String placeholder = toolPlaceholder(entry.getKey());
        byPlaceholder.set(toolRefs.containsKey(placeholder) ? placeholder : entry.getKey(), entry.getValue());
      }
      schemaOverrides = byPlaceholder;
    }

    if (destination != null) {
      String type = typeOf(node);
      if ("phone_number".equals(type) || "standalone_agent".equals(type)) {
        ObjectNode dest = destination.toWire();
        if (!type.equals(typeOf(dest))) {
          JsonNode position = node.get("position");
          node = dest;
          if (position != null) {
            node.set("position", position.deepCopy());
          }
        } else {
          if ("standalone_agent".equals(type)) {
            // A new agent destination starts at that agent's default entry point, not the old one's.
            node.remove("agent_id");
            node.remove("node_id");
            node.remove("branch_id");
          }
          node.setAll(dest);
        }
      } else {
        invalidOption(id, "destination", type);
      }
    }

    Map<String, JsonNode> fieldsInput = new LinkedHashMap<>(rest);
    String nodeType = typeOf(node);
    if ("tool".equals(nodeType)) {
      JsonNode existing = node.get("tools");
      List<String> ids = new ArrayList<>();
      if (toolIds != null) {
        ids.addAll(toolIds);
      } else if (existing != null && existing.isArray()) {
        for (JsonNode tool : existing) {
          ids.add(tool.path("tool_id").asText());
        }
      }
      if (schemaOverrides != null) {
        Iterator<String> named = schemaOverrides.fieldNames();
        while (named.hasNext()) {
          String toolId = named.next();
          if (!ids.contains(toolId)) {
            issue(
              "invalid_option",
              "Node \"" + id + "\" has schema overrides for tool \"" + displayTool(toolId) + "\", which it doesn't run",
              id
            );
          }
        }
      }
      ArrayNode locators = NODES.arrayNode();
      for (String toolId : ids) {
        JsonNode overrides = null;
        if (schemaOverrides != null && schemaOverrides.has(toolId)) {
          overrides = schemaOverrides.get(toolId).deepCopy();
        } else if (existing != null && existing.isArray()) {
          for (JsonNode tool : existing) {
            if (toolId.equals(tool.path("tool_id").asText(null))) {
              JsonNode kept = tool.get("schema_overrides");
              overrides = kept == null || kept.isNull() ? null : kept.deepCopy();
              break;
            }
          }
        }
        ObjectNode tool = NODES.objectNode();
        tool.put("tool_id", toolId);
        if (overrides != null) {
          tool.set("schema_overrides", overrides);
        }
        locators.add(tool);
      }
      node.set("tools", locators);
    } else {
      if (schemaOverrides != null) {
        invalidOption(id, "schema_overrides", nodeType);
      }
      if (toolIds != null) {
        ArrayNode arr = NODES.arrayNode();
        for (String toolId : toolIds) {
          arr.add(toolId);
        }
        fieldsInput.put("tool_ids", arr);
      }
    }

    // Node kinds without a dedicated field map (such as generated say/update_state, or newer API node types)
    // still accept position, which every node kind has.
    Map<String, String> fields = nodeType != null && FIELDS.containsKey(nodeType) ? FIELDS.get(nodeType) : POSITION_ONLY;
    for (Map.Entry<String, JsonNode> entry : fieldsInput.entrySet()) {
      if (entry.getValue() == null || entry.getValue().isNull()) {
        continue;
      }
      String field = fields.get(entry.getKey());
      if (field == null) {
        invalidOption(id, entry.getKey(), nodeType);
      } else {
        node.set(field, entry.getValue().deepCopy());
      }
    }
    return node;
  }

  /** Replaces the graph's contents with an existing wire-format workflow, keeping ids, positions and edge order. */
  void load(JsonNode workflow) {
    nodes.clear();
    edges = new ArrayList<>();
    issues.clear();
    loadedOrder.clear();
    toolRefs.clear();
    JsonNode psl = workflow.get("prevent_subagent_loops");
    preventSubagentLoops = psl != null && psl.isBoolean() ? psl.booleanValue() : null;

    JsonNode rawNodes = workflow.path("nodes");
    JsonNode rawEdges = workflow.path("edges");
    List<String> nodeIds = new ArrayList<>();
    if (rawNodes.isObject()) {
      Iterator<Map.Entry<String, JsonNode>> it = rawNodes.fields();
      while (it.hasNext()) {
        Map.Entry<String, JsonNode> entry = it.next();
        if (!entry.getValue().isObject()) {
          continue;
        }
        ObjectNode model = entry.getValue().deepCopy();
        model.remove("edge_order");
        nodes.put(entry.getKey(), model);
        nodeIds.add(entry.getKey());
      }
    }

    startId = "start_node";
    for (String id : nodeIds) {
      if ("start".equals(typeOf(nodes.get(id)))) {
        startId = id;
        break;
      }
    }
    if (!nodes.containsKey(startId)) {
      ObjectNode start = NODES.objectNode();
      start.put("type", "start");
      nodes.put(startId, start);
    }

    // Rebuild the edge list in each node's edge_order, so that build() reproduces it.
    Map<String, GraphEdge> placed = new HashMap<>();
    for (String id : nodeIds) {
      JsonNode order = rawNodes.get(id).get("edge_order");
      List<GraphEdge> loaded = new ArrayList<>();
      if (order != null && order.isArray()) {
        for (JsonNode edgeIdNode : order) {
          if (!edgeIdNode.isTextual()) {
            continue;
          }
          String edgeId = edgeIdNode.textValue();
          addLoadedEdge(rawEdges, edgeId, placed);
          GraphEdge edge = placed.get(edgeId);
          if (edge != null) {
            loaded.add(edge);
          }
        }
      }
      loadedOrder.put(id, loaded);
    }
    if (rawEdges.isObject()) {
      Iterator<String> it = rawEdges.fieldNames();
      while (it.hasNext()) {
        addLoadedEdge(rawEdges, it.next(), placed);
      }
    }
  }

  private void addLoadedEdge(JsonNode rawEdges, String edgeId, Map<String, GraphEdge> placed) {
    JsonNode raw = rawEdges.get(edgeId);
    if (placed.containsKey(edgeId) || raw == null || !raw.isObject()) {
      return;
    }
    GraphEdge edge = new GraphEdge(raw.path("source").asText(""), raw.path("target").asText(""));
    edge.id = edgeId;
    JsonNode forward = raw.get("forward_condition");
    JsonNode backward = raw.get("backward_condition");
    edge.forwardCondition = forward != null && forward.isObject() ? (ObjectNode) forward.deepCopy() : null;
    edge.backwardCondition = backward != null && backward.isObject() ? (ObjectNode) backward.deepCopy() : null;
    Iterator<Map.Entry<String, JsonNode>> fields = raw.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      String key = field.getKey();
      if (
        key.equals("source") ||
        key.equals("target") ||
        key.equals("forward_condition") ||
        key.equals("backward_condition")
      ) {
        continue;
      }
      edge.extra.set(key, field.getValue().deepCopy());
    }
    placed.put(edgeId, edge);
    edges.add(edge);
  }

  private void invalidOption(String id, String option, String nodeType) {
    String kind = nodeType != null && NODE_KIND.containsKey(nodeType) ? NODE_KIND.get(nodeType) : "\"" + nodeType + "\"";
    issue("invalid_option", "Option \"" + option + "\" doesn't apply to " + kind + " node \"" + id + "\"", id);
  }

  static String typeOf(JsonNode model) {
    JsonNode type = model == null ? null : model.get("type");
    return type != null && type.isTextual() ? type.textValue() : null;
  }

  private static Map<String, String> fields(String... pairs) {
    Map<String, String> map = new HashMap<>();
    for (int i = 0; i < pairs.length; i += 2) {
      map.put(pairs[i], pairs[i + 1]);
    }
    return map;
  }
}
