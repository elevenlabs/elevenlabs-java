package com.elevenlabs.api.agentworkflows;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Where a transfer hands the conversation off to. Built with the static constructors. Used by workflow
 * {@code transfer()} nodes; {@link #toWire()} is public so other agents-as-code builders can reuse it.
 */
public final class Destination {

  private final ObjectNode model;

  private Destination(ObjectNode model) {
    this.model = model;
  }

  /** A phone number in E.164 format. */
  public static Destination phone(String phoneNumber) {
    return phoneNumber("phone", "phone_number", phoneNumber);
  }

  /** A phone number read from a dynamic variable at transfer time. */
  public static Destination phoneVariable(String variableName) {
    return phoneNumber("phone_dynamic_variable", "phone_number", variableName);
  }

  /** A SIP URI. */
  public static Destination sip(String sipUri) {
    return phoneNumber("sip_uri", "sip_uri", sipUri);
  }

  /** A SIP URI read from a dynamic variable at transfer time. */
  public static Destination sipVariable(String variableName) {
    return phoneNumber("sip_uri_dynamic_variable", "sip_uri", variableName);
  }

  /** Another ElevenLabs agent, starting at its default entry point. */
  public static Destination agent(String agentId) {
    return agent(agentId, null);
  }

  /** Another ElevenLabs agent. {@code nodeId} is the node in its workflow to start from. */
  public static Destination agent(String agentId, String nodeId) {
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put("type", "standalone_agent");
    node.put("agent_id", agentId);
    if (nodeId != null) {
      node.put("node_id", nodeId);
    }
    return new Destination(node);
  }

  /** Whether this is a phone number or SIP destination (a {@code phone_number} node), not an agent. */
  public boolean isPhoneOrSip() {
    return "phone_number".equals(model.path("type").asText());
  }

  /**
   * The node fields this destination sets, in API wire format, as a detached copy: {@code type} plus
   * {@code transfer_destination} for phone and SIP, or {@code agent_id} / {@code node_id} for an agent.
   */
  public ObjectNode toWire() {
    return model.deepCopy();
  }

  private static Destination phoneNumber(String destinationType, String field, String value) {
    ObjectNode destination = JsonNodeFactory.instance.objectNode();
    destination.put("type", destinationType);
    destination.put(field, value);
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put("type", "phone_number");
    node.set("transfer_destination", destination);
    return new Destination(node);
  }
}
