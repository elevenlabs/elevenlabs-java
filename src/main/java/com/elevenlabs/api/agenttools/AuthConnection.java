package com.elevenlabs.api.agenttools;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** The auth connection a webhook tool authenticates with. Built with the static constructors. */
public final class AuthConnection {

  private final ObjectNode model;

  private AuthConnection(ObjectNode model) {
    this.model = model;
  }

  /** An auth connection from the workspace's store, by id. */
  public static AuthConnection id(String authConnectionId) {
    return of("auth_connection_id", authConnectionId);
  }

  /** An environment variable of type auth connection, by label, resolved for the current environment. */
  public static AuthConnection envVar(String label) {
    return of("env_var_label", label);
  }

  /** The auth connection in API wire format, as a detached copy. */
  public ObjectNode toWire() {
    return model.deepCopy();
  }

  private static AuthConnection of(String key, String value) {
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put(key, value);
    return new AuthConnection(node);
  }
}
