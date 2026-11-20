package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.core.ObjectMappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The canonical JSON format shared by the agents-as-code builders: the bytes every ElevenLabs SDK writes for the
 * same document, matching the reference JavaScript SDK's {@code JSON.stringify(sortKeys(value), null, 2) + "\n"}.
 *
 * <ul>
 *   <li>Object keys are sorted at every level, in the order a JavaScript object keeps them: array-index keys
 *       ({@code "0"}, {@code "12"}) first in numeric order, then the rest by UTF-16 code unit.</li>
 *   <li>Numbers are written as ECMAScript {@code Number.prototype.toString} writes them: {@code 300.0} as
 *       {@code 300}, {@code 1.0E-7} as {@code 1e-7}, {@code 1e21} as {@code 1e+21}. A number a double can't hold
 *       exactly (such as {@code 9007199254740993}) is written exactly as given, so no value changes.</li>
 *   <li>Strings are escaped as {@code JSON.stringify} escapes them; {@code +}, {@code /} and non-ASCII characters
 *       are left as they are.</li>
 *   <li>2-space indentation and a trailing newline.</li>
 * </ul>
 *
 * <p>The tree type is Jackson's {@link JsonNode}, the same library the generated client uses.
 */
public final class CanonicalJson {

  // Decimals may lose trailing zeros (300.0 -> 3E+2); that keeps their value, and only the value is written.
  private static final JsonNodeFactory NODES = JsonNodeFactory.instance;

  // Reads numbers without going through a double, so a stored value is never rounded on load.
  private static final ObjectReader READER = ObjectMappers.JSON_MAPPER
    .reader()
    .with(NODES)
    .with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

  private static final BigInteger MAX_ARRAY_INDEX = BigInteger.valueOf(4294967294L);

  private CanonicalJson() {}

  /**
   * Parses JSON text into a tree, keeping document order and every number's value. A fraction a double holds as
   * written (as JavaScript would read it) becomes a double; one it can't, such as {@code 9007199254740992.5},
   * stays a {@link BigDecimal}. Integers beyond the {@code long} range stay {@link BigInteger}s.
   */
  public static JsonNode parse(String json) {
    try {
      JsonNode node = READER.readTree(json);
      return node == null ? NODES.missingNode() : doublesWhereExact(node);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Invalid JSON: " + e.getOriginalMessage(), e);
    }
  }

  private static JsonNode doublesWhereExact(JsonNode node) {
    if (node.isObject()) {
      ObjectNode object = (ObjectNode) node;
      List<String> keys = new ArrayList<>();
      object.fieldNames().forEachRemaining(keys::add);
      for (String key : keys) {
        object.set(key, doublesWhereExact(object.get(key)));
      }
    } else if (node.isArray()) {
      ArrayNode array = (ArrayNode) node;
      for (int i = 0; i < array.size(); i++) {
        array.set(i, doublesWhereExact(array.get(i)));
      }
    } else if (node.isBigDecimal()) {
      String text = node.decimalValue().toString();
      double value = node.decimalValue().doubleValue();
      if (!Double.isInfinite(value) && JsonNumber.toJavaScript(text).equals(JsonNumber.format(value))) {
        return NODES.numberNode(value);
      }
    }
    return node;
  }

  /** A generated model (or any Jackson-serializable value) as a JSON tree, through the SDK's own object mapper. */
  public static JsonNode toTree(Object value) {
    if (value == null) {
      return NODES.nullNode();
    }
    if (value instanceof JsonNode) {
      return ((JsonNode) value).deepCopy();
    }
    return ObjectMappers.JSON_MAPPER.valueToTree(value);
  }

  /**
   * A fresh tree with object keys in canonical order at every level and numbers normalized to the value
   * JavaScript would write (whole numbers as integers). Arrays keep their order.
   */
  public static JsonNode canonicalize(JsonNode node) {
    if (node == null) {
      return null;
    }
    if (node.isObject()) {
      ObjectNode result = NODES.objectNode();
      for (String key : orderedKeys(node, true)) {
        result.set(key, canonicalize(node.get(key)));
      }
      return result;
    }
    if (node.isArray()) {
      ArrayNode result = NODES.arrayNode();
      for (JsonNode item : node) {
        result.add(canonicalize(item));
      }
      return result;
    }
    if (node.isNumber()) {
      return numberNode(toJavaScript(numberText(node)));
    }
    return node.deepCopy();
  }

  /** The canonical file format: sorted keys, JavaScript number formatting, 2-space indentation, trailing newline. */
  public static String toCanonicalString(JsonNode node) {
    return write(node, true);
  }

  /**
   * {@code JSON.stringify(node, null, 2) + "\n"}. With {@code sortKeys}, object keys are sorted first; without it,
   * they keep document order, except that array-index keys come first as in any JavaScript object.
   */
  public static String write(JsonNode node, boolean sortKeys) {
    return write(node, sortKeys, 2, true);
  }

  /**
   * {@code JSON.stringify(node, null, indent)}, plus a newline if {@code trailingNewline}. The ElevenLabs CLI's
   * project files use 4 spaces and no trailing newline.
   */
  public static String write(JsonNode node, boolean sortKeys, int indent, boolean trailingNewline) {
    StringBuilder out = new StringBuilder();
    writeValue(out, node, sortKeys, 0, indent);
    if (trailingNewline) {
      out.append('\n');
    }
    return out.toString();
  }

  /** Reads a numeric node as a double, whatever its backing type. */
  public static double asDouble(JsonNode node) {
    if (node == null || !node.isNumber()) {
      throw new IllegalArgumentException("Not a number: " + node);
    }
    return node.isBigDecimal() ? node.decimalValue().doubleValue() : node.doubleValue();
  }

  /** Whether a numeric node holds a whole number, judged from every digit rather than through a double. */
  public static boolean isIntegral(JsonNode node) {
    return node != null && node.isNumber() && JsonNumber.isIntegral(numberText(node));
  }

  /**
   * JSON number text as JavaScript would write the same number. A number a double can't hold exactly is kept as
   * written. Text that isn't a finite JSON number is returned unchanged.
   */
  public static String toJavaScript(String numberText) {
    return JsonNumber.toJavaScript(numberText);
  }

  /** Number text to format: exact digits for decimals and integers, the shortest JavaScript form for a double. */
  private static String numberText(JsonNode node) {
    if (node.isBigDecimal()) {
      return node.decimalValue().toString();
    }
    if (node.isDouble() || node.isFloat()) {
      double value = node.doubleValue();
      if (Double.isNaN(value) || Double.isInfinite(value)) {
        // JSON.stringify writes non-finite numbers as null.
        return "null";
      }
      // A float's shortest decimal is what the caller wrote; a double is formatted from its value, since
      // Double.toString isn't the shortest form before JDK 19.
      return node.isFloat() ? Float.toString(node.floatValue()) : JsonNumber.format(value);
    }
    return node.asText();
  }

  /** A double as ECMAScript {@code Number.prototype.toString} writes it: {@code 1e-7}, {@code 1e+21}, {@code 300}. */
  public static String toJavaScript(double value) {
    if (Double.isNaN(value) || Double.isInfinite(value)) {
      return "null";
    }
    return JsonNumber.format(value);
  }

  private static JsonNode numberNode(String text) {
    if ("null".equals(text)) {
      return NODES.nullNode();
    }
    if (text.indexOf('.') < 0 && text.indexOf('e') < 0 && text.indexOf('E') < 0) {
      BigInteger value = new BigInteger(text);
      if (value.bitLength() < 32) {
        return NODES.numberNode(value.intValue());
      }
      if (value.bitLength() < 64) {
        return NODES.numberNode(value.longValue());
      }
      return NODES.numberNode(value);
    }
    double asDouble = Double.parseDouble(text);
    if (text.equals(JsonNumber.format(asDouble))) {
      return NODES.numberNode(asDouble);
    }
    return NODES.numberNode(new BigDecimal(text));
  }

  /** Object keys in JavaScript property order: array indices numerically, then strings (sorted, or as given). */
  static List<String> orderedKeys(JsonNode object, boolean sort) {
    List<String> indices = new ArrayList<>();
    List<String> names = new ArrayList<>();
    Iterator<String> it = object.fieldNames();
    while (it.hasNext()) {
      String key = it.next();
      if (isArrayIndex(key)) {
        indices.add(key);
      } else {
        names.add(key);
      }
    }
    indices.sort((a, b) -> new BigInteger(a).compareTo(new BigInteger(b)));
    if (sort) {
      Collections.sort(names);
    }
    List<String> keys = new ArrayList<>(indices);
    keys.addAll(names);
    return keys;
  }

  private static boolean isArrayIndex(String key) {
    if (key.isEmpty() || key.length() > 10) {
      return false;
    }
    for (int i = 0; i < key.length(); i++) {
      char c = key.charAt(i);
      if (c < '0' || c > '9') {
        return false;
      }
    }
    if (key.length() > 1 && key.charAt(0) == '0') {
      return false;
    }
    return new BigInteger(key).compareTo(MAX_ARRAY_INDEX) <= 0;
  }

  private static void writeValue(StringBuilder out, JsonNode node, boolean sortKeys, int depth, int width) {
    if (node == null || node.isNull() || node.isMissingNode()) {
      out.append("null");
    } else if (node.isObject()) {
      List<String> keys = orderedKeys(node, sortKeys);
      if (keys.isEmpty()) {
        out.append("{}");
        return;
      }
      out.append('{');
      for (int i = 0; i < keys.size(); i++) {
        out.append(i == 0 ? "\n" : ",\n");
        indent(out, depth + 1, width);
        writeString(out, keys.get(i));
        out.append(": ");
        writeValue(out, node.get(keys.get(i)), sortKeys, depth + 1, width);
      }
      out.append('\n');
      indent(out, depth, width);
      out.append('}');
    } else if (node.isArray()) {
      if (node.size() == 0) {
        out.append("[]");
        return;
      }
      out.append('[');
      for (int i = 0; i < node.size(); i++) {
        out.append(i == 0 ? "\n" : ",\n");
        indent(out, depth + 1, width);
        writeValue(out, node.get(i), sortKeys, depth + 1, width);
      }
      out.append('\n');
      indent(out, depth, width);
      out.append(']');
    } else if (node.isNumber()) {
      out.append(toJavaScript(numberText(node)));
    } else if (node.isBoolean()) {
      out.append(node.booleanValue() ? "true" : "false");
    } else if (node.isTextual()) {
      writeString(out, node.textValue());
    } else if (node.isBinary()) {
      writeString(out, node.asText());
    } else {
      // POJO nodes and the like: write them as the SDK's mapper would, then canonically.
      writeValue(out, ObjectMappers.JSON_MAPPER.valueToTree(node), sortKeys, depth, width);
    }
  }

  private static void indent(StringBuilder out, int depth, int width) {
    for (int i = 0; i < depth * width; i++) {
      out.append(' ');
    }
  }

  /** A string literal as {@code JSON.stringify} writes it (ES2019 well-formed: lone surrogates escaped). */
  private static void writeString(StringBuilder out, String value) {
    out.append('"');
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      switch (c) {
        case '"':
          out.append("\\\"");
          break;
        case '\\':
          out.append("\\\\");
          break;
        case '\b':
          out.append("\\b");
          break;
        case '\f':
          out.append("\\f");
          break;
        case '\n':
          out.append("\\n");
          break;
        case '\r':
          out.append("\\r");
          break;
        case '\t':
          out.append("\\t");
          break;
        default:
          if (c < 0x20) {
            unicodeEscape(out, c);
          } else if (Character.isHighSurrogate(c)) {
            if (i + 1 < value.length() && Character.isLowSurrogate(value.charAt(i + 1))) {
              out.append(c).append(value.charAt(i + 1));
              i++;
            } else {
              unicodeEscape(out, c);
            }
          } else if (Character.isLowSurrogate(c)) {
            unicodeEscape(out, c);
          } else {
            out.append(c);
          }
      }
    }
    out.append('"');
  }

  private static void unicodeEscape(StringBuilder out, char c) {
    String hex = Integer.toHexString(c);
    out.append("\\u");
    for (int pad = hex.length(); pad < 4; pad++) {
      out.append('0');
    }
    out.append(hex);
  }

  /**
   * JSON number text, read from its decimal digits rather than through a double, and written as JavaScript
   * writes it (ECMAScript {@code Number::toString}).
   */
  static final class JsonNumber {

    private JsonNumber() {}

    static String toJavaScript(String text) {
      double value;
      try {
        value = Double.parseDouble(text.trim());
      } catch (NumberFormatException e) {
        return text;
      }
      if (Double.isInfinite(value) || Double.isNaN(value) || !Decomposed.valid(text)) {
        return text;
      }
      String js = format(value);
      return sameValue(text, js) ? js : text;
    }

    static boolean isIntegral(String text) {
      Decomposed d = Decomposed.of(text);
      return d != null && (d.digits.isEmpty() || d.point >= d.digits.length());
    }

    /** ECMAScript {@code Number::toString(x)} for a finite double. */
    static String format(double value) {
      if (value == 0) {
        return "0";
      }
      Decomposed d = shortest(Math.abs(value));
      String digits = d.digits;
      int k = digits.length();
      int n = (int) d.point;
      String body;
      if (k <= n && n <= 21) {
        body = digits + zeros(n - k);
      } else if (0 < n && n <= 21) {
        body = digits.substring(0, n) + "." + digits.substring(n);
      } else if (-6 < n && n <= 0) {
        body = "0." + zeros(-n) + digits;
      } else {
        String exponent = (n - 1 >= 0 ? "+" : "-") + Math.abs(n - 1);
        body = k == 1 ? digits + "e" + exponent : digits.charAt(0) + "." + digits.substring(1) + "e" + exponent;
      }
      return value < 0 ? "-" + body : body;
    }

    /**
     * The fewest significant digits that read back as {@code value}, and among those the closest to it, as
     * JavaScript picks them. Computed exactly with {@link BigDecimal}: {@code Double.toString} isn't shortest
     * before JDK 19.
     */
    private static Decomposed shortest(double value) {
      BigDecimal exact = new BigDecimal(value);
      for (int precision = 1; precision <= 17; precision++) {
        BigDecimal candidate = exact.round(new MathContext(precision, RoundingMode.HALF_EVEN));
        if (candidate.doubleValue() == value) {
          return Decomposed.of(candidate.toString());
        }
      }
      return Decomposed.of(exact.round(new MathContext(17, RoundingMode.HALF_EVEN)).toString());
    }

    private static boolean sameValue(String a, String b) {
      Decomposed x = Decomposed.of(a);
      Decomposed y = Decomposed.of(b);
      if (x == null || y == null) {
        return false;
      }
      if (x.digits.isEmpty() || y.digits.isEmpty()) {
        // Zero, whatever its sign: JavaScript writes -0 as 0.
        return x.digits.isEmpty() && y.digits.isEmpty();
      }
      return x.negative == y.negative && x.digits.equals(y.digits) && x.point == y.point;
    }

    private static String zeros(int count) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < count; i++) {
        sb.append('0');
      }
      return sb.toString();
    }
  }

  /**
   * Number text split into its sign, its significant digits (no leading or trailing zeros; empty for zero) and the
   * decimal point position, so the value is {@code 0.digits × 10^point}.
   */
  private static final class Decomposed {

    final boolean negative;
    final String digits;
    final long point;

    private Decomposed(boolean negative, String digits, long point) {
      this.negative = negative;
      this.digits = digits;
      this.point = point;
    }

    static boolean valid(String text) {
      return of(text) != null;
    }

    static Decomposed of(String text) {
      String s = text.trim();
      boolean negative = false;
      if (s.startsWith("-") || s.startsWith("+")) {
        negative = s.charAt(0) == '-';
        s = s.substring(1);
      }
      long exponent = 0;
      int e = Math.max(s.indexOf('e'), s.indexOf('E'));
      if (e >= 0) {
        try {
          String exp = s.substring(e + 1);
          exponent = Long.parseLong(exp.startsWith("+") ? exp.substring(1) : exp);
        } catch (NumberFormatException ex) {
          return null;
        }
        s = s.substring(0, e);
      }
      int dot = s.indexOf('.');
      String whole = dot >= 0 ? s.substring(0, dot) : s;
      String fraction = dot >= 0 ? s.substring(dot + 1) : "";
      String all = whole + fraction;
      if (all.isEmpty()) {
        return null;
      }
      for (int i = 0; i < all.length(); i++) {
        char c = all.charAt(i);
        if (c < '0' || c > '9') {
          return null;
        }
      }
      int leading = 0;
      while (leading < all.length() && all.charAt(leading) == '0') {
        leading++;
      }
      int end = all.length();
      while (end > leading && all.charAt(end - 1) == '0') {
        end--;
      }
      return new Decomposed(negative, all.substring(leading, end), whole.length() - leading + exponent);
    }
  }
}
