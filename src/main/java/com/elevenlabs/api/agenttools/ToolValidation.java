package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Schema checks shared by every tool kind. */
final class ToolValidation {

  private ToolValidation() {}

  /**
   * Checks a schema and everything under it. {@code path} names it in messages, such as {@code "body.reason"}.
   * {@code isProperty}: the schema is a property (or a query or client param), so {@code omit()} needs it optional.
   */
  static void checkSchema(Schema schema, String path, boolean isProperty, List<ToolIssue> issues) {
    SchemaState s = schema.state;
    // The API reads "" as unset, so an empty description or variable name isn't a value source.
    boolean described = s.description != null && !s.description.isEmpty();
    int sources = 0;
    for (boolean set : new boolean[] {
      described,
      s.dynamicVariable != null && !s.dynamicVariable.isEmpty(),
      s.hasConstant,
      s.systemProvided,
      s.omitted,
    }) {
      if (set) {
        sources++;
      }
    }
    if (sources > 1) {
      issues.add(
        ToolIssue.error(
          "conflicting_value_source",
          path,
          "\"" + path + "\" sets more than one of describe(), fromVariable(), constant(), systemProvided() and omit()"
        )
      );
    }
    if (s.omitted && isProperty && !s.optional) {
      issues.add(ToolIssue.error("omitted_required", path, "\"" + path + "\" uses omit(), so it must be optional()"));
    }
    if (s.choices != null && s.kind != SchemaKind.STRING) {
      issues.add(ToolIssue.error("invalid_choices", path, "\"" + path + "\" uses choices(), which only applies to strings"));
    }
    if (s.allowedFrom != null && !described) {
      issues.add(
        ToolIssue.error(
          "allowed_values_needs_description",
          path,
          "\"" + path + "\" uses allowedFrom(), which needs describe() so the LLM provides the value"
        )
      );
    }
    if (s.hasConstant) {
      checkConstant(s, path, issues);
    }
    // The API rejects a literal with no value source wherever it appears, response schemas included.
    if (sources == 0 && s.kind.isLiteral()) {
      issues.add(
        ToolIssue.error(
          "missing_value_source",
          path,
          "\"" + path + "\" needs one of describe(), fromVariable(), constant(), systemProvided() or omit()"
        )
      );
    }

    if (s.kind == SchemaKind.OBJECT) {
      List<JsonNode> groups = new ArrayList<>();
      if (s.anyOf != null) {
        groups.addAll(s.anyOf);
      }
      if (s.allOf != null) {
        groups.addAll(s.allOf);
      }
      for (JsonNode group : groups) {
        for (String name : Schema.groupNames(group)) {
          if (!s.properties.containsKey(name)) {
            issues.add(
              ToolIssue.error(
                "unknown_required_property",
                path,
                "\"" + path + "\" requires \"" + name + "\" in requireAnyOf()/requireAllOf(), but has no such property"
              )
            );
          }
        }
      }
      for (Map.Entry<String, Schema> prop : s.properties.entrySet()) {
        checkSchema(prop.getValue(), path + "." + prop.getKey(), true, issues);
      }
    }
    if (s.kind == SchemaKind.ARRAY) {
      if (s.items != null) {
        checkSchema(s.items, path + "[]", false, issues);
      } else if (!s.malformed.contains("items")) {
        // Loaded items of the wrong shape are reported by build() as invalid_model instead.
        issues.add(ToolIssue.error("missing_array_items", path, "Array \"" + path + "\" has no items schema"));
      }
    }
  }

  private static void checkConstant(SchemaState s, String path, List<ToolIssue> issues) {
    JsonNode value = s.constant;
    if (!matchesKind(s.kind, value, Schema.isNullableType(s.literalType))) {
      issues.add(
        ToolIssue.error("constant_type_mismatch", path, "\"" + path + "\" has a constant that isn't a " + s.kind.wireName())
      );
    } else if (ToolWire.isEmptyString(value)) {
      issues.add(
        ToolIssue.error(
          "constant_type_mismatch",
          path,
          "\"" + path + "\" can't have \"\" as a constant: the API reads it as unset"
        )
      );
    } else if (ToolWire.containsNonFinite(value)) {
      // JSON has no NaN or Infinity; JavaScript would write it as null, so the value would change.
      issues.add(
        ToolIssue.error("constant_type_mismatch", path, "\"" + path + "\" has a constant that isn't a finite number")
      );
    } else if (s.kind.isLiteral() && value.isNumber() && ToolWire.isExactIntegerBeyondLong(value)) {
      // The generated request model can't hold an integer beyond the long range, so it would change the value.
      issues.add(
        ToolIssue.error(
          "constant_type_mismatch",
          path,
          "\"" + path + "\" has the constant " + value + ", which is outside the long range and can't be sent exactly"
        )
      );
    }
  }

  private static boolean matchesKind(SchemaKind kind, JsonNode value, boolean nullable) {
    if (value == null || value.isNull()) {
      return nullable;
    }
    switch (kind) {
      case STRING:
        return value.isTextual();
      case INTEGER:
        // Judged from every digit as written, not through a double: 9007199254740992.5 rounds to a whole double.
        return value.isNumber() && !ToolWire.isNonFinite(value) && CanonicalJson.isIntegral(value);
      case NUMBER:
        return value.isNumber();
      case BOOLEAN:
        return value.isBoolean();
      case OBJECT:
        return value.isObject();
      default:
        return value.isArray();
    }
  }

  /**
   * Reports each loaded field under a schema that has the wrong shape for the generated request model (kept as
   * loaded, so {@code toJson()} writes it back), as {@code invalid_model}.
   */
  static void checkModelShape(Schema schema, String path, List<ToolIssue> issues) {
    SchemaState s = schema.state;
    for (String key : s.malformed) {
      issues.add(
        ToolIssue.error(
          "invalid_model",
          path,
          "\"" + path + "\" has a loaded " + key + " of the wrong shape, which the generated tool config model can't"
            + " take. Fix it in the source JSON"
        )
      );
    }
    for (Map.Entry<String, Schema> prop : s.properties.entrySet()) {
      checkModelShape(prop.getValue(), path + "." + prop.getKey(), issues);
    }
    if (s.items != null) {
      checkModelShape(s.items, path + "[]", issues);
    }
  }
}
