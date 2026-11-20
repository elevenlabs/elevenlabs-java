package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.types.AstNodeInput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.List;

/**
 * A workflow expression, compiled to the API's expression AST. Built with the static constructors and the
 * instance operators; kept in wire format.
 *
 * <pre>{@code
 * Expr.anyOf(Expr.variable("tier").eq("gold"), Expr.variable("orders").gte(10));
 * }</pre>
 */
public final class Expr {

  private static final JsonNodeFactory NODES = JsonNodeFactory.instance;

  private final ObjectNode node;

  private Expr(ObjectNode node) {
    this.node = node;
  }

  /** The value of a dynamic variable. */
  public static Expr variable(String name) {
    ObjectNode n = typed("dynamic_variable");
    n.put("name", name);
    return new Expr(n);
  }

  /** A string literal. */
  public static Expr literal(String value) {
    ObjectNode n = typed("string_literal");
    n.put("value", value);
    return new Expr(n);
  }

  /** A boolean literal. */
  public static Expr literal(boolean value) {
    ObjectNode n = typed("boolean_literal");
    n.put("value", value);
    return new Expr(n);
  }

  /** An integer literal. */
  public static Expr literal(long value) {
    ObjectNode n = typed("number_literal");
    n.put("value", value);
    return new Expr(n);
  }

  /** A number literal. */
  public static Expr literal(double value) {
    ObjectNode n = typed("number_literal");
    n.put("value", value);
    return new Expr(n);
  }

  /** A null literal. */
  public static Expr literalNull() {
    return new Expr(typed("null_literal"));
  }

  /** A boolean the LLM extracts from the conversation, described by {@code description}. */
  public static Expr llm(String description) {
    return llm(description, "boolean", null);
  }

  /** A value of JSON-schema {@code type} the LLM extracts from the conversation. */
  public static Expr llm(String description, String type) {
    return llm(description, type, null);
  }

  /** A value the LLM extracts from the conversation; {@code enumValues} lists the allowed values of a string. */
  public static Expr llm(String description, String type, List<String> enumValues) {
    ObjectNode schema = NODES.objectNode();
    schema.put("type", type == null ? "boolean" : type);
    schema.put("description", description);
    if (enumValues != null) {
      ArrayNode values = schema.putArray("enum");
      for (String value : enumValues) {
        values.add(value);
      }
    }
    ObjectNode n = typed("llm");
    n.set("value_schema", schema);
    return new Expr(n);
  }

  /** True when every expression is true. */
  public static Expr allOf(Expr... exprs) {
    return children("and_operator", Arrays.asList(exprs));
  }

  /** True when at least one expression is true. */
  public static Expr anyOf(Expr... exprs) {
    return children("or_operator", Arrays.asList(exprs));
  }

  /** {@code whenTrue} if {@code condition} is true, otherwise {@code whenFalse}. */
  public static Expr ifElse(Expr condition, Expr whenTrue, Expr whenFalse) {
    ObjectNode n = typed("conditional_operator");
    n.set("condition", condition.toWire());
    // The API's AST uses these camelCase keys; carried through verbatim.
    n.set("trueExpression", whenTrue.toWire());
    n.set("falseExpression", whenFalse.toWire());
    return new Expr(n);
  }

  /** Wraps an existing AST node, given as a generated {@code AstNodeInput} model. */
  public static Expr fromModel(AstNodeInput ast) {
    return fromModel(CanonicalJson.toTree(ast));
  }

  /** Wraps an existing AST node, given in API wire format. */
  public static Expr fromModel(JsonNode ast) {
    if (ast == null || !ast.isObject()) {
      throw new IllegalArgumentException("An expression AST node must be a JSON object");
    }
    return new Expr((ObjectNode) ast.deepCopy());
  }

  public Expr eq(Expr other) {
    return binary("eq_operator", other);
  }

  public Expr eq(String other) {
    return binary("eq_operator", literal(other));
  }

  public Expr eq(long other) {
    return binary("eq_operator", literal(other));
  }

  public Expr eq(double other) {
    return binary("eq_operator", literal(other));
  }

  public Expr eq(boolean other) {
    return binary("eq_operator", literal(other));
  }

  public Expr neq(Expr other) {
    return binary("neq_operator", other);
  }

  public Expr neq(String other) {
    return binary("neq_operator", literal(other));
  }

  public Expr neq(long other) {
    return binary("neq_operator", literal(other));
  }

  public Expr neq(double other) {
    return binary("neq_operator", literal(other));
  }

  public Expr neq(boolean other) {
    return binary("neq_operator", literal(other));
  }

  public Expr gt(Expr other) {
    return binary("gt_operator", other);
  }

  public Expr gt(long other) {
    return binary("gt_operator", literal(other));
  }

  public Expr gt(double other) {
    return binary("gt_operator", literal(other));
  }

  public Expr gte(Expr other) {
    return binary("gte_operator", other);
  }

  public Expr gte(long other) {
    return binary("gte_operator", literal(other));
  }

  public Expr gte(double other) {
    return binary("gte_operator", literal(other));
  }

  public Expr lt(Expr other) {
    return binary("lt_operator", other);
  }

  public Expr lt(long other) {
    return binary("lt_operator", literal(other));
  }

  public Expr lt(double other) {
    return binary("lt_operator", literal(other));
  }

  public Expr lte(Expr other) {
    return binary("lte_operator", other);
  }

  public Expr lte(long other) {
    return binary("lte_operator", literal(other));
  }

  public Expr lte(double other) {
    return binary("lte_operator", literal(other));
  }

  public Expr add(Expr other) {
    return binary("add_operator", other);
  }

  public Expr add(long other) {
    return binary("add_operator", literal(other));
  }

  public Expr add(double other) {
    return binary("add_operator", literal(other));
  }

  public Expr sub(Expr other) {
    return binary("sub_operator", other);
  }

  public Expr sub(long other) {
    return binary("sub_operator", literal(other));
  }

  public Expr sub(double other) {
    return binary("sub_operator", literal(other));
  }

  public Expr mul(Expr other) {
    return binary("mul_operator", other);
  }

  public Expr mul(long other) {
    return binary("mul_operator", literal(other));
  }

  public Expr mul(double other) {
    return binary("mul_operator", literal(other));
  }

  public Expr div(Expr other) {
    return binary("div_operator", other);
  }

  public Expr div(long other) {
    return binary("div_operator", literal(other));
  }

  public Expr div(double other) {
    return binary("div_operator", literal(other));
  }

  /** The expression in API wire format, as a detached copy. */
  public ObjectNode toWire() {
    return node.deepCopy();
  }

  private Expr binary(String type, Expr other) {
    ObjectNode n = typed(type);
    n.set("left", toWire());
    n.set("right", other.toWire());
    return new Expr(n);
  }

  private static Expr children(String type, List<Expr> exprs) {
    ObjectNode n = typed(type);
    ArrayNode list = n.putArray("children");
    for (Expr expr : exprs) {
      list.add(expr.toWire());
    }
    return new Expr(n);
  }

  private static ObjectNode typed(String type) {
    ObjectNode n = NODES.objectNode();
    n.put("type", type);
    return n;
  }
}
