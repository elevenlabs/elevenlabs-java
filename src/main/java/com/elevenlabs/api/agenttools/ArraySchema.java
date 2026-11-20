package com.elevenlabs.api.agenttools;

import java.util.Optional;

/** A list whose elements match one schema. Created with {@link Schema#array(Schema)}. */
public final class ArraySchema extends Schema {

  ArraySchema(SchemaState state) {
    super(state);
  }

  @Override
  public ArraySchema describe(String text) {
    return (ArraySchema) super.describe(text);
  }

  @Override
  public ArraySchema fromVariable(String name) {
    return (ArraySchema) super.fromVariable(name);
  }

  @Override
  public ArraySchema constant(Object value) {
    return (ArraySchema) super.constant(value);
  }

  @Override
  public ArraySchema omit() {
    return (ArraySchema) super.omit();
  }

  @Override
  public ArraySchema optional() {
    return (ArraySchema) super.optional();
  }

  /** The element schema. Empty only for a loaded array without one, which {@code build()} rejects. */
  public Optional<Schema> getItems() {
    return Optional.ofNullable(state.items);
  }

  @Override
  Schema create(SchemaState state) {
    return new ArraySchema(state);
  }
}
