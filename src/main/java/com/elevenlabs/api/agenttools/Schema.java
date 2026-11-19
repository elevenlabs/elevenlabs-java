package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A tool param's JSON schema, and where its value comes from. Schemas are values: every method returns a new schema
 * and never changes the one it is called on, so one schema can be reused across tools and properties.
 *
 * <p>{@link #string()}, {@link #integer()}, {@link #number()} and {@link #bool()} return a {@link LiteralSchema},
 * {@link #object()} an {@link ObjectSchema} and {@link #array(Schema)} an {@link ArraySchema}. Methods that apply to
 * one kind only live on that subtype, and every method returns the subtype it is called on.
 *
 * <pre>{@code
 * Schema.object()
 *   .property("order_id", Schema.string().fromVariable("order_id"))
 *   .property("reason", Schema.string().describe("Why the caller wants a refund").choices("damaged", "late"))
 *   .property("note", Schema.string().describe("Anything else the caller said").optional());
 * }</pre>
 */
public abstract class Schema {

  // Fields the builder models for every kind. Any other loaded field is kept as loaded.
  private static final Set<String> COMMON_KEYS = new HashSet<>(
    Arrays.asList("type", "description", "dynamic_variable", "constant_value", "is_omitted")
  );
  private static final Set<String> OBJECT_KEYS = new HashSet<>(
    Arrays.asList("properties", "required", "required_constraints")
  );
  private static final Set<String> LITERAL_KEYS = new HashSet<>(
    Arrays.asList("is_system_provided", "enum", "allowed_values")
  );

  final SchemaState state;

  Schema(SchemaState state) {
    this.state = state;
  }

  /** A string. */
  public static LiteralSchema string() {
    return new LiteralSchema(new SchemaState(SchemaKind.STRING));
  }

  /** An integer. */
  public static LiteralSchema integer() {
    return new LiteralSchema(new SchemaState(SchemaKind.INTEGER));
  }

  /** A number. */
  public static LiteralSchema number() {
    return new LiteralSchema(new SchemaState(SchemaKind.NUMBER));
  }

  /** A boolean. Named {@code bool} as in every SDK, because {@code boolean} is reserved in Java. */
  public static LiteralSchema bool() {
    return new LiteralSchema(new SchemaState(SchemaKind.BOOLEAN));
  }

  /** An object. Add properties with {@link ObjectSchema#property(String, Schema)}. */
  public static ObjectSchema object() {
    return new ObjectSchema(new SchemaState(SchemaKind.OBJECT));
  }

  /** An array whose elements match {@code items}. */
  public static ArraySchema array(Schema items) {
    SchemaState s = new SchemaState(SchemaKind.ARRAY);
    s.items = items;
    return new ArraySchema(s);
  }

  /** Loads a schema in API format (snake_case), given as a JSON string. */
  public static Schema fromJson(String json) {
    return fromJson(CanonicalJson.parse(json));
  }

  /** Loads a schema in API format (snake_case), given as a parsed JSON object. */
  public static Schema fromJson(JsonNode json) {
    if (json == null || !json.isObject()) {
      throw new IllegalArgumentException("A schema must be a JSON object");
    }
    ObjectNode model = (ObjectNode) ToolWire.stripNulls(json);
    return fromModel(model, false, kindOf(model));
  }

  /** {@code STRING}, {@code INTEGER}, {@code NUMBER}, {@code BOOLEAN}, {@code OBJECT} or {@code ARRAY}. */
  public SchemaKind getKind() {
    return state.kind;
  }

  /** Whether the parent leaves this property out of {@code required}. */
  public boolean isOptional() {
    return state.optional;
  }

  /** The LLM provides the value, guided by {@code text}. On an object or array, documents the container. */
  public Schema describe(String text) {
    SchemaState s = state.copy();
    s.description = text;
    return create(s);
  }

  /** The value is read from the dynamic variable {@code name} when the tool runs. */
  public Schema fromVariable(String name) {
    SchemaState s = state.copy();
    s.dynamicVariable = name;
    return create(s);
  }

  /**
   * The value is always {@code value}: a JSON value matching the schema's type, which {@code build()} checks. Takes
   * a {@link JsonNode}, a {@code String}, {@code Number} or {@code Boolean}, a {@code Map} or {@code List} of those,
   * or {@code null} (only valid for a loaded literal whose type list includes {@code "null"}).
   */
  public Schema constant(Object value) {
    SchemaState s = state.copy();
    s.hasConstant = true;
    s.constant = CanonicalJson.toTree(value);
    return create(s);
  }

  /** The param is left out of the request. Only allowed on an optional property. */
  public Schema omit() {
    SchemaState s = state.copy();
    s.omitted = true;
    return create(s);
  }

  /** Leaves this property out of its parent's {@code required}. Properties are required unless marked optional. */
  public Schema optional() {
    SchemaState s = state.copy();
    s.optional = true;
    return create(s);
  }

  /** The schema in API format (snake_case keys, sorted at every level). Doesn't validate. */
  public ObjectNode toJson() {
    return (ObjectNode) CanonicalJson.canonicalize(toWire());
  }

  abstract Schema create(SchemaState state);

  /** The schema in API wire format, unsorted. Modeled fields that are unset leave the matching extra field in place. */
  ObjectNode toWire() {
    SchemaState s = state;
    ObjectNode o = s.extra.deepCopy();
    if (s.description != null) {
      o.put("description", s.description);
    }
    if (s.dynamicVariable != null) {
      o.put("dynamic_variable", s.dynamicVariable);
    }
    if (s.omitted) {
      o.put("is_omitted", true);
    }
    if (s.hasConstant) {
      o.set("constant_value", s.constant.deepCopy());
    }
    if (s.kind == SchemaKind.OBJECT) {
      o.put("type", "object");
      if (!s.properties.isEmpty()) {
        ObjectNode props = ToolWire.NODES.objectNode();
        for (Map.Entry<String, Schema> prop : s.properties.entrySet()) {
          props.set(prop.getKey(), prop.getValue().toWire());
        }
        o.set("properties", props);
      }
      List<String> required = requiredOf(s);
      if (!required.isEmpty()) {
        o.set("required", ToolWire.toArray(required));
      }
      // A loaded wrapper is kept with its own fields, and a loaded (even empty) group list with it.
      if (s.constraintsExtra != null || s.anyOf != null || s.allOf != null) {
        ObjectNode constraints = s.constraintsExtra == null ? ToolWire.NODES.objectNode() : s.constraintsExtra.deepCopy();
        if (s.anyOf != null) {
          constraints.set("any_of", groupsToWire(s.anyOf));
        }
        if (s.allOf != null) {
          constraints.set("all_of", groupsToWire(s.allOf));
        }
        o.set("required_constraints", constraints);
      }
      return o;
    }
    if (s.kind == SchemaKind.ARRAY) {
      o.put("type", "array");
      if (s.items != null) {
        o.set("items", s.items.toWire());
      }
      return o;
    }
    o.set("type", s.literalType != null ? s.literalType.deepCopy() : ToolWire.NODES.textNode(s.kind.wireName()));
    if (s.systemProvided) {
      o.put("is_system_provided", true);
    }
    if (s.choices != null) {
      o.set("enum", ToolWire.toArray(s.choices));
    }
    if (s.allowedValues != null || s.allowedFrom != null) {
      // The loaded object keeps its other fields; the variable name is overlaid.
      ObjectNode allowed = s.allowedValues == null ? ToolWire.NODES.objectNode() : s.allowedValues.deepCopy();
      if (s.allowedFrom != null) {
        allowed.put("dynamic_variable", s.allowedFrom);
      }
      o.set("allowed_values", allowed);
    }
    return o;
  }

  /**
   * Loads a wire-format schema whose nulls are already removed (except a constant's). {@code optional} is whether
   * its parent leaves it out of {@code required}; {@code kind} is the schema's kind, which callers infer with
   * {@link #kindOf} or {@link #containerKind}.
   */
  static Schema fromModel(ObjectNode m, boolean optional, SchemaKind kind) {
    SchemaState s = new SchemaState(kind);
    s.optional = optional;
    // The API fills unset fields with defaults ("", false, [], {}). They aren't value sources, but are kept as
    // extra fields so a pulled config writes back unchanged.
    ObjectNode extra = ToolWire.NODES.objectNode();
    Iterator<Map.Entry<String, JsonNode>> fields = m.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      if (!field.getValue().isNull() && (!isModeled(kind, field.getKey()) || isUnset(field.getKey(), field.getValue(), kind))) {
        extra.set(field.getKey(), field.getValue().deepCopy());
      }
    }
    s.extra = extra;
    Loader load = new Loader(m, kind, s);

    JsonNode description = load.set("description");
    if (description != null && description.isTextual()) {
      s.description = description.textValue();
    } else if (description != null) {
      load.keep("description", true);
    }
    JsonNode variable = load.set("dynamic_variable");
    if (variable != null && variable.isTextual()) {
      s.dynamicVariable = variable.textValue();
    } else if (variable != null) {
      load.keep("dynamic_variable", true);
    }
    // null is absent, except as the constant of a literal whose type list includes "null".
    JsonNode loadedConstant = m.get("constant_value");
    if (kind.isLiteral() && loadedConstant != null && loadedConstant.isNull() && isNullableType(m.get("type"))) {
      s.hasConstant = true;
      s.constant = ToolWire.NODES.nullNode();
    } else if (load.set("constant_value") != null) {
      s.hasConstant = true;
      s.constant = load.set("constant_value").deepCopy();
    }
    s.typeLoaded = m.hasNonNull("type");
    JsonNode omitted = load.set("is_omitted");
    if (ToolWire.isTrue(omitted)) {
      s.omitted = true;
    } else if (omitted != null) {
      load.keep("is_omitted", true);
    }

    if (kind == SchemaKind.OBJECT) {
      loadObject(load, s);
      return new ObjectSchema(s);
    }
    if (kind == SchemaKind.ARRAY) {
      JsonNode items = load.set("items");
      if (items != null && items.isObject()) {
        s.items = fromModel((ObjectNode) items, true, kindOf((ObjectNode) items));
      } else if (items != null) {
        load.keep("items", true);
      }
      return new ArraySchema(s);
    }
    s.literalType = m.hasNonNull("type") ? m.get("type").deepCopy() : null;
    JsonNode systemProvided = load.set("is_system_provided");
    if (ToolWire.isTrue(systemProvided)) {
      s.systemProvided = true;
    } else if (systemProvided != null) {
      load.keep("is_system_provided", true);
    }
    JsonNode choices = load.set("enum");
    if (choices != null && ToolWire.strings(choices) != null) {
      s.choices = ToolWire.strings(choices);
    } else if (choices != null) {
      load.keep("enum", true);
    }
    JsonNode allowed = load.set("allowed_values");
    if (allowed != null && allowed.isObject() && (!allowed.has("dynamic_variable") || allowed.get("dynamic_variable").isTextual())) {
      s.allowedValues = (ObjectNode) allowed.deepCopy();
      s.allowedFrom = ToolWire.str(allowed.get("dynamic_variable"));
    } else if (allowed != null) {
      load.keep("allowed_values", true);
    }
    return new LiteralSchema(s);
  }

  private static void loadObject(Loader load, SchemaState s) {
    JsonNode props = load.set("properties");
    JsonNode required = load.set("required");
    boolean badProps = props != null && !allObjects(props);
    boolean badRequired = required != null && ToolWire.strings(required) == null;
    if (badProps || badRequired) {
      // Each needs the other: a property's optional() comes from `required`. Both are kept as loaded.
      if (props != null) {
        load.keep("properties", badProps);
      }
      if (required != null) {
        load.keep("required", badRequired);
      }
    } else {
      List<String> names = required == null ? null : ToolWire.strings(required);
      if (props != null) {
        Iterator<Map.Entry<String, JsonNode>> it = props.fields();
        while (it.hasNext()) {
          Map.Entry<String, JsonNode> prop = it.next();
          ObjectNode child = (ObjectNode) prop.getValue();
          boolean optional = names == null || !names.contains(prop.getKey());
          s.properties.put(prop.getKey(), fromModel(child, optional, kindOf(child)));
        }
      }
      s.loadedRequired = names;
    }
    JsonNode constraints = load.set("required_constraints");
    if (constraints != null) {
      List<JsonNode> anyOf = constraintGroups(constraints, "any_of");
      List<JsonNode> allOf = constraintGroups(constraints, "all_of");
      if (!constraints.isObject() || anyOf == MALFORMED || allOf == MALFORMED) {
        load.keep("required_constraints", true);
      } else {
        // Fields other than the two group lists, and fields inside each group, are kept as loaded.
        ObjectNode rest = ((ObjectNode) constraints).deepCopy();
        rest.remove("any_of");
        rest.remove("all_of");
        s.constraintsExtra = rest;
        s.anyOf = anyOf;
        s.allOf = allOf;
      }
    }
  }

  private static final List<JsonNode> MALFORMED = new ArrayList<>();

  /** A loaded group list: null when absent, {@link #MALFORMED} if it isn't a list of {@code {required: [names]}}. */
  private static List<JsonNode> constraintGroups(JsonNode constraints, String key) {
    JsonNode groups = constraints.get(key);
    if (groups == null || groups.isNull()) {
      return null;
    }
    if (!groups.isArray()) {
      return MALFORMED;
    }
    List<JsonNode> result = new ArrayList<>();
    for (JsonNode group : groups) {
      if (!group.isObject() || ToolWire.strings(group.get("required")) == null) {
        return MALFORMED;
      }
      result.add(group.deepCopy());
    }
    return result;
  }

  private static boolean allObjects(JsonNode props) {
    if (!props.isObject()) {
      return false;
    }
    for (JsonNode value : props) {
      if (!value.isObject()) {
        return false;
      }
    }
    return true;
  }

  /** Reads a loaded schema's modeled fields, and keeps the ones of the wrong shape as loaded. */
  private static final class Loader {

    final ObjectNode model;
    final SchemaKind kind;
    final SchemaState state;

    Loader(ObjectNode model, SchemaKind kind, SchemaState state) {
      this.model = model;
      this.kind = kind;
      this.state = state;
    }

    /** The loaded value of {@code key}, or null if it is absent, null or an API default for an unset field. */
    JsonNode set(String key) {
      JsonNode value = model.get(key);
      return value == null || value.isNull() || isUnset(key, value, kind) ? null : value;
    }

    /** Keeps {@code key} in the extra fields as loaded; {@code malformed} if the generated model can't take it. */
    void keep(String key, boolean malformed) {
      JsonNode value = set(key);
      if (value != null) {
        state.extra.set(key, value.deepCopy());
        if (malformed) {
          state.malformed.add(key);
        }
      }
    }
  }

  /** Whether the builder models {@code key} for a schema of {@code kind}. Anything else is kept as loaded. */
  private static boolean isModeled(SchemaKind kind, String key) {
    if (COMMON_KEYS.contains(key)) {
      return true;
    }
    if (kind == SchemaKind.OBJECT) {
      return OBJECT_KEYS.contains(key);
    }
    if (kind == SchemaKind.ARRAY) {
      return "items".equals(key);
    }
    return LITERAL_KEYS.contains(key);
  }

  /**
   * Whether {@code value} is what the API returns for an unset field. A literal's unset constant is {@code ""}, so
   * {@code ""} can't be a literal's constant.
   */
  private static boolean isUnset(String key, JsonNode value, SchemaKind kind) {
    switch (key) {
      case "description":
      case "dynamic_variable":
        return ToolWire.isEmptyString(value);
      case "constant_value":
        return kind.isLiteral() && ToolWire.isEmptyString(value);
      case "is_system_provided":
      case "is_omitted":
        return ToolWire.isFalse(value);
      case "required":
        return value.isArray() && value.size() == 0;
      case "properties":
        return value.isObject() && value.size() == 0;
      default:
        return false;
    }
  }

  /** The kind of a wire-format schema. A literal's type may be a list such as {@code ["integer", "null"]}. */
  static SchemaKind kindOf(ObjectNode m) {
    JsonNode type = m.get("type");
    boolean untyped = type == null || type.isNull();
    String name = ToolWire.str(type);
    if ("object".equals(name) || (untyped && m.has("properties"))) {
      return SchemaKind.OBJECT;
    }
    if ("array".equals(name) || (untyped && m.has("items"))) {
      return SchemaKind.ARRAY;
    }
    if (type != null && type.isArray()) {
      name = null;
      for (JsonNode member : type) {
        if (member.isTextual() && !"null".equals(member.textValue())) {
          name = member.textValue();
          break;
        }
      }
    }
    if ("integer".equals(name)) {
      return SchemaKind.INTEGER;
    }
    if ("number".equals(name)) {
      return SchemaKind.NUMBER;
    }
    if ("boolean".equals(name)) {
      return SchemaKind.BOOLEAN;
    }
    return SchemaKind.STRING;
  }

  /**
   * The kind of a schema the API only allows to be an object (a tool's params, query, body or response). One with no
   * {@code type} is an object, even when empty; a declared {@code type} is kept, so validation can report a schema
   * that isn't an object instead of it being rewritten.
   */
  static SchemaKind containerKind(ObjectNode m) {
    return m.hasNonNull("type") ? kindOf(m) : SchemaKind.OBJECT;
  }

  /** Whether a literal's {@code type} is a list that includes {@code "null"}, e.g. {@code ["integer", "null"]}. */
  static boolean isNullableType(JsonNode type) {
    if (type == null || !type.isArray()) {
      return false;
    }
    for (JsonNode member : type) {
      if ("null".equals(member.textValue())) {
        return true;
      }
    }
    return false;
  }

  /** The names a {@code required_constraints} group requires. */
  static List<String> groupNames(JsonNode group) {
    List<String> names = ToolWire.strings(group.get("required"));
    return names == null ? new ArrayList<String>() : names;
  }

  private static ArrayNode groupsToWire(List<JsonNode> groups) {
    ArrayNode array = ToolWire.NODES.arrayNode();
    for (JsonNode group : groups) {
      array.add(group.deepCopy());
    }
    return array;
  }

  /** A loaded object's {@code required} order for the required properties it still has, then new ones in order. */
  private static List<String> requiredOf(SchemaState s) {
    List<String> required = new ArrayList<>();
    for (Map.Entry<String, Schema> prop : s.properties.entrySet()) {
      if (!prop.getValue().isOptional()) {
        required.add(prop.getKey());
      }
    }
    List<String> result = new ArrayList<>();
    if (s.loadedRequired != null) {
      for (String name : s.loadedRequired) {
        if (required.contains(name) && !result.contains(name)) {
          result.add(name);
        }
      }
    }
    for (String name : required) {
      if (!result.contains(name)) {
        result.add(name);
      }
    }
    return result;
  }
}
