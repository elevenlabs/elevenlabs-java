package com.elevenlabs.api.agenttools;

import java.util.Locale;

/** The kind of a {@link Schema}: its wire {@code type}. */
public enum SchemaKind {
  STRING,
  INTEGER,
  NUMBER,
  BOOLEAN,
  OBJECT,
  ARRAY;

  /** The wire {@code type}: {@code "string"}, {@code "integer"}, {@code "number"}, {@code "boolean"}, ... */
  public String wireName() {
    return name().toLowerCase(Locale.ROOT);
  }

  /** Whether this is a string, integer, number or boolean. */
  public boolean isLiteral() {
    return this != OBJECT && this != ARRAY;
  }
}
