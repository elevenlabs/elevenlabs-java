package com.elevenlabs.api.agenttools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** The value of a webhook request header. Built with the static constructors. */
public final class Header {

  private final JsonNode model;

  private Header(JsonNode model) {
    this.model = model;
  }

  /** A fixed value. */
  public static Header value(String text) {
    return new Header(JsonNodeFactory.instance.textNode(text));
  }

  /** A secret from the workspace's secret store, so the value never appears in the config. */
  public static Header secret(String secretId) {
    return of("secret_id", secretId);
  }

  /** The value of a dynamic variable when the tool runs. */
  public static Header variable(String name) {
    return of("variable_name", name);
  }

  /** A workspace environment variable, by label. */
  public static Header envVar(String label) {
    return of("env_var_label", label);
  }

  /** The header value in API wire format, as a detached copy. */
  public JsonNode toWire() {
    return model.deepCopy();
  }

  private static Header of(String key, String value) {
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put(key, value);
    return new Header(node);
  }
}
