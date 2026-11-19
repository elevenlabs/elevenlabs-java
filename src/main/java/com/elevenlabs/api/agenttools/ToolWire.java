package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Helpers for the tool builder's wire representation. Like the workflow builder, it keeps its state in API wire
 * format (snake_case keys) as Jackson trees; canonical output goes through the workflow builder's
 * {@link com.elevenlabs.api.agentworkflows.CanonicalJson}.
 */
final class ToolWire {

  static final JsonNodeFactory NODES = JsonNodeFactory.instance;

  private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
  private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

  private ToolWire() {}

  /**
   * A detached deep copy with every {@code null}-valued property removed, since the API sends {@code null} for some
   * unset fields and the spec reads them as absent. {@code constant_value: null} is kept for the schema loader to
   * judge, and the contents of a constant or a placeholder map are free-form JSON, copied unchanged. The values of
   * name maps ({@code properties}, {@code path_params_schema}, {@code request_headers}) are walked as values, so a
   * param named {@code constant_value} is no exception.
   */
  static JsonNode stripNulls(JsonNode node) {
    if (node.isObject()) {
      ObjectNode result = NODES.objectNode();
      Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> field = fields.next();
        String key = field.getKey();
        JsonNode value = field.getValue();
        if (value.isNull()) {
          // A null constant is a value for a literal whose type list includes "null"; the schema decides.
          if ("constant_value".equals(key)) {
            result.set(key, NODES.nullNode());
          }
          continue;
        }
        if ("constant_value".equals(key) || "dynamic_variable_placeholders".equals(key)) {
          result.set(key, value.deepCopy());
        } else if (isNameMap(key) && value.isObject()) {
          ObjectNode names = NODES.objectNode();
          Iterator<Map.Entry<String, JsonNode>> entries = value.fields();
          while (entries.hasNext()) {
            Map.Entry<String, JsonNode> entry = entries.next();
            if (!entry.getValue().isNull()) {
              names.set(entry.getKey(), stripNulls(entry.getValue()));
            }
          }
          result.set(key, names);
        } else {
          result.set(key, stripNulls(value));
        }
      }
      return result;
    }
    if (node.isArray()) {
      ArrayNode result = NODES.arrayNode();
      for (JsonNode item : node) {
        result.add(item.isNull() ? NODES.nullNode() : stripNulls(item));
      }
      return result;
    }
    return node.deepCopy();
  }

  /** Keys whose value maps names (of params, properties or headers) to values, rather than fields to values. */
  static boolean isNameMap(String key) {
    return "properties".equals(key) || "path_params_schema".equals(key) || "request_headers".equals(key);
  }

  /** The string value of {@code node}, or {@code null} if it isn't a JSON string. */
  static String str(JsonNode node) {
    return node != null && node.isTextual() ? node.textValue() : null;
  }

  /** The elements of a JSON array of strings, or {@code null} if it isn't an array or has a non-string element. */
  static List<String> strings(JsonNode node) {
    if (node == null || !node.isArray()) {
      return null;
    }
    List<String> result = new ArrayList<>();
    for (JsonNode item : node) {
      if (!item.isTextual()) {
        return null;
      }
      result.add(item.textValue());
    }
    return result;
  }

  static ArrayNode toArray(Iterable<String> values) {
    ArrayNode array = NODES.arrayNode();
    for (String value : values) {
      array.add(value);
    }
    return array;
  }

  static boolean isTrue(JsonNode node) {
    return node != null && node.isBoolean() && node.booleanValue();
  }

  static boolean isFalse(JsonNode node) {
    return node != null && node.isBoolean() && !node.booleanValue();
  }

  static boolean isEmptyString(JsonNode node) {
    return node != null && node.isTextual() && node.textValue().isEmpty();
  }

  /** Whether a numeric node is NaN or infinite. JSON can't hold those, and JavaScript would write them as null. */
  static boolean isNonFinite(JsonNode node) {
    if (node.isDouble() || node.isFloat()) {
      double value = node.doubleValue();
      return Double.isNaN(value) || Double.isInfinite(value);
    }
    return false;
  }

  /** Whether {@code node} or anything inside it is a non-finite number. */
  static boolean containsNonFinite(JsonNode node) {
    if (node.isNumber()) {
      return isNonFinite(node);
    }
    for (JsonNode child : node) {
      if (containsNonFinite(child)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether a number node holds an exact integer (an integer node, or a decimal with no fraction) outside the
   * {@code long} range that no double holds as JavaScript writes it. A double beyond the range is a double, and is
   * sent as one; so is an integer written as JavaScript writes a double, such as {@code 123456789012345680000}.
   */
  static boolean isExactIntegerBeyondLong(JsonNode node) {
    BigInteger value;
    if (node.isBigInteger()) {
      value = node.bigIntegerValue();
    } else if (node.isBigDecimal()) {
      BigDecimal decimal = node.decimalValue();
      if (decimal.signum() != 0 && decimal.stripTrailingZeros().scale() > 0) {
        return false;
      }
      value = decimal.toBigInteger();
    } else {
      return false;
    }
    return (value.compareTo(LONG_MIN) < 0 || value.compareTo(LONG_MAX) > 0) && !isDoubleText(value);
  }

  /** Whether JavaScript writes the double nearest to {@code value} as {@code value}'s own digits. */
  static boolean isDoubleText(BigInteger value) {
    double nearest = value.doubleValue();
    return !Double.isInfinite(nearest) && CanonicalJson.toJavaScript(nearest).equals(value.toString());
  }

  /** {@code "response_timeout_secs"} -> {@code "responseTimeoutSecs"}, the option's name in Java. */
  static String camelCase(String snake) {
    StringBuilder out = new StringBuilder();
    boolean upper = false;
    for (int i = 0; i < snake.length(); i++) {
      char c = snake.charAt(i);
      if (c == '_') {
        upper = true;
      } else {
        out.append(upper ? Character.toUpperCase(c) : c);
        upper = false;
      }
    }
    return out.toString();
  }
}
