package com.elevenlabs.api.agentworkflows;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * When an edge may be followed. A plain {@code String} passed to {@code when()} is shorthand for {@link #llm}, and
 * an {@link Expr} is shorthand for {@link #expr}. {@code label} is shown on the edge in the workflow editor.
 */
public final class Condition {

  private final ObjectNode model;

  private Condition(ObjectNode model) {
    this.model = model;
  }

  /** Followed when the LLM judges {@code condition} to be true. */
  public static Condition llm(String condition) {
    return llm(condition, null);
  }

  /** Followed when the LLM judges {@code condition} to be true, with an edge label. */
  public static Condition llm(String condition, String label) {
    ObjectNode node = object("llm");
    node.put("condition", condition);
    return labelled(node, label);
  }

  /** Followed when {@code expression} evaluates to true. */
  public static Condition expr(Expr expression) {
    return expr(expression, null);
  }

  /** Followed when {@code expression} evaluates to true, with an edge label. */
  public static Condition expr(Expr expression, String label) {
    ObjectNode node = object("expression");
    node.set("expression", expression.toWire());
    return labelled(node, label);
  }

  /** Always followed. */
  public static Condition always() {
    return always(null);
  }

  /** Always followed, with an edge label. */
  public static Condition always(String label) {
    return labelled(object("unconditional"), label);
  }

  /** Followed when every tool in a tool node succeeded. */
  public static Condition success() {
    return success(null);
  }

  /** Followed when every tool in a tool node succeeded, with an edge label. */
  public static Condition success(String label) {
    ObjectNode node = object("result");
    node.put("successful", true);
    return labelled(node, label);
  }

  /** Followed when any tool in a tool node failed. */
  public static Condition failure() {
    return failure(null);
  }

  /** Followed when any tool in a tool node failed, with an edge label. */
  public static Condition failure(String label) {
    ObjectNode node = object("result");
    node.put("successful", false);
    return labelled(node, label);
  }

  /** The condition in API wire format, as a detached copy. */
  public ObjectNode toWire() {
    return model.deepCopy();
  }

  private static ObjectNode object(String type) {
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put("type", type);
    return node;
  }

  private static Condition labelled(ObjectNode node, String label) {
    if (label != null) {
      node.put("label", label);
    }
    return new Condition(node);
  }
}
