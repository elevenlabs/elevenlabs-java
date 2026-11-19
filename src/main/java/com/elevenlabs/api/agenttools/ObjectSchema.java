package com.elevenlabs.api.agenttools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** An object with named properties. Created with {@link Schema#object()}. */
public final class ObjectSchema extends Schema {

  ObjectSchema(SchemaState state) {
    super(state);
  }

  @Override
  public ObjectSchema describe(String text) {
    return (ObjectSchema) super.describe(text);
  }

  @Override
  public ObjectSchema fromVariable(String name) {
    return (ObjectSchema) super.fromVariable(name);
  }

  @Override
  public ObjectSchema constant(Object value) {
    return (ObjectSchema) super.constant(value);
  }

  @Override
  public ObjectSchema omit() {
    return (ObjectSchema) super.omit();
  }

  @Override
  public ObjectSchema optional() {
    return (ObjectSchema) super.optional();
  }

  /** Adds the property {@code name}, or replaces it in place if it exists. Required unless the schema is optional. */
  public ObjectSchema property(String name, Schema schema) {
    SchemaState s = state.copy();
    s.properties.put(name, schema);
    return new ObjectSchema(s);
  }

  /** Removes the property {@code name}. */
  public ObjectSchema without(String name) {
    SchemaState s = state.copy();
    s.properties.remove(name);
    return new ObjectSchema(s);
  }

  /** Requires at least one of these properties: appends {@code names} to {@code required_constraints.any_of}. */
  public ObjectSchema requireAnyOf(List<String> names) {
    SchemaState s = state.copy();
    s.anyOf = appended(s.anyOf, names);
    return new ObjectSchema(s);
  }

  /** Requires at least one of these properties. */
  public ObjectSchema requireAnyOf(String... names) {
    return requireAnyOf(Arrays.asList(names));
  }

  /** Requires all of these properties: appends {@code names} to {@code required_constraints.all_of}. */
  public ObjectSchema requireAllOf(List<String> names) {
    SchemaState s = state.copy();
    s.allOf = appended(s.allOf, names);
    return new ObjectSchema(s);
  }

  /** Requires all of these properties. */
  public ObjectSchema requireAllOf(String... names) {
    return requireAllOf(Arrays.asList(names));
  }

  /** Property names, in order. */
  public List<String> getPropertyNames() {
    return Collections.unmodifiableList(new ArrayList<>(state.properties.keySet()));
  }

  /** The schema of property {@code name}, if there is one. */
  public Optional<Schema> propertySchema(String name) {
    return Optional.ofNullable(state.properties.get(name));
  }

  @Override
  Schema create(SchemaState state) {
    return new ObjectSchema(state);
  }

  private static List<JsonNode> appended(List<JsonNode> groups, List<String> names) {
    List<JsonNode> result = groups == null ? new ArrayList<JsonNode>() : new ArrayList<>(groups);
    ObjectNode group = ToolWire.NODES.objectNode();
    group.set("required", ToolWire.toArray(names));
    result.add(group);
    return result;
  }
}
