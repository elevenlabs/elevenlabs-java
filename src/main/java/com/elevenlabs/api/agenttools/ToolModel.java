package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.types.ToolRequestModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns a finished wire-format tool config into the generated {@link ToolRequestModel}, and makes sure the model
 * sends every schema constant exactly as {@code toJson()} writes it. The generated literal schema holds its constant
 * as a {@code String}, {@code int}, {@code double} or {@code boolean} and reads {@code null} as unset, so some
 * constants (an integer beyond the {@code int} range, a decimal no double holds, a nullable literal's {@code null})
 * would change or vanish. Rather than send something else, {@code build()} reports each one as
 * {@code constant_type_mismatch}; {@code toJson()} still carries it exactly.
 */
final class ToolModel {

  private static final BigDecimal LONG_MIN = BigDecimal.valueOf(Long.MIN_VALUE);
  private static final BigDecimal LONG_MAX = BigDecimal.valueOf(Long.MAX_VALUE);

  private ToolModel() {}

  static ToolRequestModel convert(String toolName, ObjectNode wire) {
    // Constants the generated model fails on rather than changes: report them, not the failure they cause.
    List<ToolIssue> unrepresentable = new ArrayList<>();
    for (Map.Entry<List<String>, JsonNode> entry : constants(wire).entrySet()) {
      String at = display(entry.getKey());
      JsonNode value = entry.getValue();
      if (containsIntegerBeyondLong(value)) {
        unrepresentable.add(
          ToolIssue.error(
            "constant_type_mismatch",
            at,
            "\"" + at + "\" has a constant holding an integer outside the long range, which this SDK's generated"
              + " request model can't send. Send the toJson() output directly, or use another constant"
          )
        );
      } else if (value.isBigDecimal() && !CanonicalJson.isIntegral(value)) {
        unrepresentable.add(
          ToolIssue.error(
            "constant_type_mismatch",
            at,
            "\"" + at + "\" has the constant " + CanonicalJson.toCanonicalString(value).trim() + ", which no double"
              + " holds exactly, so this SDK's generated request model can't send it. Use a constant it can represent"
              + " exactly"
          )
        );
      }
    }
    if (!unrepresentable.isEmpty()) {
      throw new ToolValidationException(toolName, unrepresentable);
    }
    ObjectNode root = ToolWire.NODES.objectNode();
    root.set("tool_config", forModel(wire));
    ToolRequestModel model;
    try {
      model = ObjectMappers.JSON_MAPPER.treeToValue(root, ToolRequestModel.class);
    } catch (Exception e) {
      // Validation covers the builder's rules, not every loaded field's shape.
      throw new ToolValidationException(
        toolName,
        Collections.singletonList(
          ToolIssue.error(
            "invalid_model",
            null,
            "The tool doesn't match the generated tool config model (" + e.getMessage() + "). A loaded field likely"
              + " has the wrong shape; fix it in the source JSON"
          )
        )
      );
    }
    List<ToolIssue> issues = compareConstants(wire, ObjectMappers.JSON_MAPPER.valueToTree(model.getToolConfig()));
    if (!issues.isEmpty()) {
      throw new ToolValidationException(toolName, issues);
    }
    return model;
  }

  /**
   * A copy for the generated model: whole doubles in the {@code long} range become integers, as JavaScript writes
   * them ({@code 10.0} is sent as {@code 10}), and integers beyond it that JavaScript would read and write as a
   * double ({@code 123456789012345680000}) become that double. Every other number is left as it is.
   */
  private static JsonNode forModel(JsonNode node) {
    if (node.isObject()) {
      ObjectNode result = ToolWire.NODES.objectNode();
      Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> field = fields.next();
        result.set(field.getKey(), forModel(field.getValue()));
      }
      return result;
    }
    if (node.isArray()) {
      ArrayNode result = ToolWire.NODES.arrayNode();
      for (JsonNode item : node) {
        result.add(forModel(item));
      }
      return result;
    }
    if (node.isNumber() && !node.isIntegralNumber() && !ToolWire.isNonFinite(node) && CanonicalJson.isIntegral(node)) {
      BigDecimal value = node.isBigDecimal() ? node.decimalValue() : new BigDecimal(node.doubleValue());
      if (value.compareTo(LONG_MIN) >= 0 && value.compareTo(LONG_MAX) <= 0) {
        long whole = value.longValueExact();
        return whole == (int) whole ? ToolWire.NODES.numberNode((int) whole) : ToolWire.NODES.numberNode(whole);
      }
      if (node.isBigDecimal() && ToolWire.isDoubleText(value.toBigInteger())) {
        return ToolWire.NODES.numberNode(value.doubleValue());
      }
      return node;
    }
    if (node.isBigInteger() && ToolWire.isDoubleText(node.bigIntegerValue())) {
      // Beyond the long range, but written as JavaScript writes a double: sent as that double.
      return ToolWire.NODES.numberNode(node.bigIntegerValue().doubleValue());
    }
    return node.deepCopy();
  }

  private static boolean containsIntegerBeyondLong(JsonNode node) {
    if (node.isNumber()) {
      return ToolWire.isExactIntegerBeyondLong(node);
    }
    for (JsonNode child : node) {
      if (containsIntegerBeyondLong(child)) {
        return true;
      }
    }
    return false;
  }

  /** An error for each schema constant in {@code wire} that {@code sent} (the serialized model) changed or dropped. */
  private static List<ToolIssue> compareConstants(JsonNode wire, JsonNode sent) {
    Map<List<String>, JsonNode> wanted = constants(wire);
    Map<List<String>, JsonNode> actual = constants(sent);
    List<ToolIssue> issues = new ArrayList<>();
    for (Map.Entry<List<String>, JsonNode> entry : wanted.entrySet()) {
      String at = display(entry.getKey());
      JsonNode value = entry.getValue();
      JsonNode got = actual.get(entry.getKey());
      if (got != null && CanonicalJson.toCanonicalString(got).equals(CanonicalJson.toCanonicalString(value))) {
        continue;
      }
      String text = CanonicalJson.toCanonicalString(value).trim();
      String message;
      if (value.isNull()) {
        message =
          "\"" + at + "\" has the constant null, which this SDK's generated request model can't send (it reads null"
            + " as unset). Send the toJson() output directly, or use another constant";
      } else if (got != null) {
        message =
          "\"" + at + "\" has the constant " + text + ", which this SDK's generated request model would send as "
            + CanonicalJson.toCanonicalString(got).trim() + ". Use a constant it can represent exactly";
      } else {
        message =
          "\"" + at + "\" has the constant " + text + ", which this SDK's generated request model would drop. Send"
            + " the toJson() output directly, or use another constant";
      }
      issues.add(ToolIssue.error("constant_type_mismatch", at, message));
    }
    return issues;
  }

  // Path steps: a field ("K"), a property or param name ("N", never a schema field even when spelled like one) or an
  // array index ("I").
  private static final String KEY = "K";
  private static final String NAME = "N";
  private static final String INDEX = "I";

  /**
   * Every schema constant in a tool config (wire format, or the generated model serialized), by where it is. Free-form
   * JSON values (constants, dynamic variable placeholders, headers) aren't schemas, so they aren't searched; property
   * and path param names are walked as names, so a param named {@code constant_value} isn't read as a constant.
   */
  private static Map<List<String>, JsonNode> constants(JsonNode config) {
    Map<List<String>, JsonNode> out = new LinkedHashMap<>();
    walk(config, new ArrayList<String>(), out);
    return out;
  }

  private static void walk(JsonNode node, List<String> path, Map<List<String>, JsonNode> out) {
    if (node.isObject()) {
      Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> field = fields.next();
        String key = field.getKey();
        JsonNode value = field.getValue();
        if ("constant_value".equals(key)) {
          out.put(new ArrayList<>(path), value);
        } else if ("dynamic_variable_placeholders".equals(key) || "request_headers".equals(key)) {
          continue;
        } else if ("properties".equals(key) || "path_params_schema".equals(key)) {
          path.add(KEY + key);
          Iterator<Map.Entry<String, JsonNode>> names = value.fields();
          while (names.hasNext()) {
            Map.Entry<String, JsonNode> name = names.next();
            path.add(NAME + name.getKey());
            walk(name.getValue(), path, out);
            path.remove(path.size() - 1);
          }
          path.remove(path.size() - 1);
        } else {
          path.add(KEY + key);
          walk(value, path, out);
          path.remove(path.size() - 1);
        }
      }
    } else if (node.isArray()) {
      for (int i = 0; i < node.size(); i++) {
        path.add(INDEX + i);
        walk(node.get(i), path, out);
        path.remove(path.size() - 1);
      }
    }
  }

  /** A constant's location as validation names it, such as {@code "body.order.id"} or {@code "params.tags[]"}. */
  private static String display(List<String> path) {
    StringBuilder out = new StringBuilder();
    for (String step : path) {
      String kind = step.substring(0, 1);
      String text = step.substring(1);
      String label;
      if (INDEX.equals(kind)) {
        out.append('[').append(text).append(']');
        continue;
      } else if (NAME.equals(kind)) {
        label = text;
      } else if ("api_schema".equals(text) || "properties".equals(text)) {
        continue;
      } else if ("items".equals(text)) {
        out.append("[]");
        continue;
      } else {
        label = label(text);
      }
      if (out.length() > 0) {
        out.append('.');
      }
      out.append(label);
    }
    return out.toString();
  }

  /** A config or schema field's name in a validation path. */
  private static String label(String key) {
    switch (key) {
      case "path_params_schema":
        return "pathParams";
      case "query_params_schema":
        return "query";
      case "request_body_schema":
        return "body";
      case "response_body_schema":
        return "response";
      case "parameters":
        return "params";
      default:
        return key;
    }
  }
}
