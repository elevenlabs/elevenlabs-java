package com.elevenlabs.api.agentworkflows;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The options a caller set, keyed by canonical option name in snake_case ({@code transfer_type}), in the order
 * they were set. Values are generated SDK types (or plain Java values) and are converted to wire format with the
 * SDK's object mapper only when applied, so the builder never depends on their shapes.
 */
final class OptionMap {

  private final Map<String, Object> values = new LinkedHashMap<>();

  void put(String key, Object value) {
    if (value == null) {
      values.remove(key);
    } else {
      values.put(key, value);
    }
  }

  Object get(String key) {
    return values.get(key);
  }

  /** Every option except the ones named, in wire format. */
  Map<String, JsonNode> toWire(String... except) {
    Map<String, JsonNode> out = new LinkedHashMap<>();
    outer:
    for (Map.Entry<String, Object> entry : values.entrySet()) {
      for (String skip : except) {
        if (skip.equals(entry.getKey())) {
          continue outer;
        }
      }
      out.put(entry.getKey(), CanonicalJson.toTree(entry.getValue()));
    }
    return out;
  }
}
