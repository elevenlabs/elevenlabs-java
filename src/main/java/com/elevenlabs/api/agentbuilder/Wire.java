package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Helpers for agent configs in API wire format (snake_case JSON trees). */
final class Wire {

  static final JsonNodeFactory NODES = JsonNodeFactory.instance;

  static final List<String> TOOL_IDS_PATH = path("conversation_config", "agent", "prompt", "tool_ids");
  static final List<String> BUILT_IN_TOOLS_PATH = path("conversation_config", "agent", "prompt", "built_in_tools");

  private Wire() {}

  static List<String> path(String... keys) {
    List<String> path = new ArrayList<>();
    for (String key : keys) {
      path.add(key);
    }
    return path;
  }

  /** The value at {@code path}, or {@code null} if any step is missing. A JSON {@code null} there is returned as is. */
  static JsonNode getPath(JsonNode target, List<String> path) {
    JsonNode current = target;
    for (String key : path) {
      if (current == null || !current.isObject()) {
        return null;
      }
      current = current.get(key);
    }
    return current;
  }

  /** Sets the value at {@code path}, creating objects along the way. {@code null} deletes the key. */
  static void setPath(ObjectNode target, List<String> path, JsonNode value) {
    ObjectNode current = target;
    for (String key : path.subList(0, path.size() - 1)) {
      JsonNode next = current.get(key);
      if (next == null || !next.isObject()) {
        if (value == null) {
          return;
        }
        next = NODES.objectNode();
        current.set(key, next);
      }
      current = (ObjectNode) next;
    }
    String last = path.get(path.size() - 1);
    if (value == null) {
      current.remove(last);
    } else {
      current.set(last, value);
    }
  }

  /** Merges {@code source} into {@code target}: objects merge key by key, anything else (arrays included) replaces. */
  static ObjectNode deepMerge(ObjectNode target, JsonNode source) {
    if (source == null || !source.isObject()) {
      return target;
    }
    Iterator<Map.Entry<String, JsonNode>> fields = source.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      JsonNode existing = target.get(field.getKey());
      if (field.getValue().isObject() && existing != null && existing.isObject()) {
        deepMerge((ObjectNode) existing, field.getValue());
      } else {
        target.set(field.getKey(), field.getValue().deepCopy());
      }
    }
    return target;
  }

  /**
   * Whether every value in {@code local} is in {@code remote} at the same place. Extra keys in {@code remote} (server
   * defaults) are fine, a {@code null} in {@code local} matches anything, and numbers compare by value.
   */
  static boolean isSubset(JsonNode local, JsonNode remote) {
    if (local == null || local.isNull()) {
      return true;
    }
    if (remote == null) {
      return false;
    }
    if (local.isArray()) {
      if (!remote.isArray() || remote.size() != local.size()) {
        return false;
      }
      for (int i = 0; i < local.size(); i++) {
        if (!isSubset(local.get(i), remote.get(i))) {
          return false;
        }
      }
      return true;
    }
    if (local.isObject()) {
      if (!remote.isObject()) {
        return false;
      }
      Iterator<Map.Entry<String, JsonNode>> fields = local.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> field = fields.next();
        if (!field.getValue().isNull() && !isSubset(field.getValue(), remote.get(field.getKey()))) {
          return false;
        }
      }
      return true;
    }
    if (local.isNumber() && remote.isNumber()) {
      // JSON can't tell 1 from 1.0, and a parsed response may hold either.
      return decimal(local).compareTo(decimal(remote)) == 0;
    }
    return local.equals(remote);
  }

  private static BigDecimal decimal(JsonNode number) {
    if (number.isBigDecimal() || number.isBigInteger() || number.isIntegralNumber()) {
      return number.decimalValue();
    }
    return new BigDecimal(CanonicalJson.toJavaScript(number.doubleValue()));
  }

  /** A copy without {@code null} values: a generated model writes a required field it doesn't have as {@code null}. */
  static JsonNode withoutNulls(JsonNode node) {
    if (node == null) {
      return null;
    }
    JsonNode copy = node.deepCopy();
    stripNulls(copy);
    return copy;
  }

  private static void stripNulls(JsonNode node) {
    if (node.isObject()) {
      ObjectNode object = (ObjectNode) node;
      List<String> keys = new ArrayList<>();
      object.fieldNames().forEachRemaining(keys::add);
      for (String key : keys) {
        if (object.get(key).isNull()) {
          object.remove(key);
        } else {
          stripNulls(object.get(key));
        }
      }
    } else if (node.isArray()) {
      for (JsonNode item : node) {
        stripNulls(item);
      }
    }
  }

  /** The strings in an array node, skipping anything else. */
  static List<String> strings(JsonNode array) {
    List<String> values = new ArrayList<>();
    if (array != null && array.isArray()) {
      for (JsonNode item : array) {
        if (item.isTextual()) {
          values.add(item.textValue());
        }
      }
    }
    return values;
  }

  static ArrayNode array(Iterable<String> values) {
    ArrayNode array = NODES.arrayNode();
    for (String value : values) {
      array.add(value);
    }
    return array;
  }

  /** The tool ids an API-format workflow's nodes use: tool nodes' tools, then subagents' extra tools, per node. */
  static List<String> rawWorkflowToolIds(JsonNode workflow) {
    List<String> ids = new ArrayList<>();
    JsonNode nodes = workflow == null ? null : workflow.get("nodes");
    if (nodes == null || !nodes.isObject()) {
      return ids;
    }
    for (JsonNode node : nodes) {
      if (!node.isObject()) {
        continue;
      }
      JsonNode tools = node.get("tools");
      if (tools != null && tools.isArray()) {
        for (JsonNode tool : tools) {
          JsonNode id = tool.get("tool_id");
          if (tool.isObject() && id != null && id.isTextual()) {
            ids.add(id.textValue());
          }
        }
      }
      ids.addAll(strings(node.get("additional_tool_ids")));
    }
    return ids;
  }

  /** Replaces the tool ids in an API-format workflow's tool nodes and subagents, in place. */
  static ObjectNode mapRawWorkflowToolIds(ObjectNode workflow, java.util.function.Function<String, String> map) {
    JsonNode nodes = workflow.get("nodes");
    if (nodes == null || !nodes.isObject()) {
      return workflow;
    }
    for (JsonNode node : nodes) {
      if (!node.isObject()) {
        continue;
      }
      JsonNode tools = node.get("tools");
      if (tools != null && tools.isArray()) {
        for (JsonNode tool : tools) {
          JsonNode id = tool.get("tool_id");
          if (tool.isObject() && id != null && id.isTextual()) {
            ((ObjectNode) tool).put("tool_id", map.apply(id.textValue()));
          }
        }
      }
      JsonNode extra = node.get("additional_tool_ids");
      if (extra != null && extra.isArray()) {
        ArrayNode mapped = NODES.arrayNode();
        for (JsonNode id : extra) {
          mapped.add(id.isTextual() ? NODES.textNode(map.apply(id.textValue())) : id);
        }
        ((ObjectNode) node).set("additional_tool_ids", mapped);
      }
    }
    return workflow;
  }

  /** Every tool id an agent config uses, in its {@code tool_ids} and its workflow's nodes, once each, in order. */
  static List<String> toolIdsIn(JsonNode config) {
    Set<String> ids = new LinkedHashSet<>(strings(getPath(config, TOOL_IDS_PATH)));
    JsonNode workflow = config.get("workflow");
    ids.addAll(rawWorkflowToolIds(workflow != null && workflow.isObject() ? workflow : null));
    return new ArrayList<>(ids);
  }
}
