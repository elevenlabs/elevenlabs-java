package com.elevenlabs.api.agenttools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Everything a schema holds. Schemas never change a state they hold: every method copies it with the change. The
 * JSON trees inside are never mutated either, only copied out.
 */
final class SchemaState {

  SchemaKind kind;

  /** A loaded literal's {@code type}, kept as is (the API also allows a list such as {@code ["integer", "null"]}). */
  JsonNode literalType;

  /** Whether the loaded schema declared a {@code type} (otherwise the builder writes one). */
  boolean typeLoaded;

  String description;
  String dynamicVariable;
  boolean hasConstant;

  /** The constant, when {@link #hasConstant}; a {@code NullNode} for a nullable literal's null. */
  JsonNode constant;

  boolean systemProvided;
  boolean omitted;
  boolean optional;
  List<String> choices;
  String allowedFrom;

  /** A loaded literal's {@code allowed_values} object, so fields other than the variable survive edits. */
  ObjectNode allowedValues;

  Schema items;

  /** Properties in order. Replacing one with {@code put} keeps its position. */
  LinkedHashMap<String, Schema> properties = new LinkedHashMap<>();

  /** A loaded object's {@code required}, so its order survives edits. */
  List<String> loadedRequired;

  /** A loaded object's {@code required_constraints} fields other than {@code any_of} / {@code all_of}, if it had one. */
  ObjectNode constraintsExtra;

  /** The {@code any_of} groups, each a wire object with {@code required} (and any loaded extras); null when absent. */
  List<JsonNode> anyOf;

  /** The {@code all_of} groups, as {@link #anyOf}. */
  List<JsonNode> allOf;

  /** Loaded fields the builder doesn't model, and loaded API defaults, written back unchanged. */
  ObjectNode extra = ToolWire.NODES.objectNode();

  /**
   * Loaded modeled fields of the wrong shape, kept in {@link #extra} as loaded. The generated request model would
   * reject or silently change them, so {@code build()} reports each one ({@code invalid_model}).
   */
  List<String> malformed = new ArrayList<>();

  SchemaState(SchemaKind kind) {
    this.kind = kind;
  }

  SchemaState copy() {
    SchemaState s = new SchemaState(kind);
    s.literalType = literalType;
    s.typeLoaded = typeLoaded;
    s.description = description;
    s.dynamicVariable = dynamicVariable;
    s.hasConstant = hasConstant;
    s.constant = constant;
    s.systemProvided = systemProvided;
    s.omitted = omitted;
    s.optional = optional;
    s.choices = choices;
    s.allowedFrom = allowedFrom;
    s.allowedValues = allowedValues;
    s.items = items;
    s.properties = new LinkedHashMap<>(properties);
    s.loadedRequired = loadedRequired;
    s.constraintsExtra = constraintsExtra;
    s.anyOf = anyOf;
    s.allOf = allOf;
    s.extra = extra;
    s.malformed = malformed;
    return s;
  }
}
