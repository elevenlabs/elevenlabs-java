package com.elevenlabs.api.agenttools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** A string, integer, number or boolean. Created with {@link Schema#string()}, {@link Schema#integer()}, ... */
public final class LiteralSchema extends Schema {

  LiteralSchema(SchemaState state) {
    super(state);
  }

  @Override
  public LiteralSchema describe(String text) {
    return (LiteralSchema) super.describe(text);
  }

  @Override
  public LiteralSchema fromVariable(String name) {
    return (LiteralSchema) super.fromVariable(name);
  }

  @Override
  public LiteralSchema constant(Object value) {
    return (LiteralSchema) super.constant(value);
  }

  @Override
  public LiteralSchema omit() {
    return (LiteralSchema) super.omit();
  }

  @Override
  public LiteralSchema optional() {
    return (LiteralSchema) super.optional();
  }

  /** The values the LLM may choose from ({@code enum}). Strings only. Named {@code choices} because {@code enum} is reserved. */
  public LiteralSchema choices(List<String> values) {
    SchemaState s = state.copy();
    s.choices = new ArrayList<>(values);
    return new LiteralSchema(s);
  }

  /** The values the LLM may choose from ({@code enum}). */
  public LiteralSchema choices(String... values) {
    return choices(Arrays.asList(values));
  }

  /** The value is filled in by the system at runtime. */
  public LiteralSchema systemProvided() {
    SchemaState s = state.copy();
    s.systemProvided = true;
    return new LiteralSchema(s);
  }

  /**
   * Rejects an LLM value that isn't in the list held by the dynamic variable {@code variable}. The list isn't shown
   * to the LLM. Needs {@link #describe(String)}.
   */
  public LiteralSchema allowedFrom(String variable) {
    SchemaState s = state.copy();
    s.allowedFrom = variable;
    return new LiteralSchema(s);
  }

  @Override
  Schema create(SchemaState state) {
    return new LiteralSchema(state);
  }
}
