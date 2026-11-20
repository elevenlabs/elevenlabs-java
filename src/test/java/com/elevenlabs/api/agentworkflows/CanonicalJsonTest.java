package com.elevenlabs.api.agentworkflows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

/** The canonical writer must produce the bytes {@code JSON.stringify(sortKeys(value), null, 2) + "\n"} produces. */
class CanonicalJsonTest {

  private static String number(String text) {
    return CanonicalJson.toJavaScript(text);
  }

  @Test
  void writesNonIntegerDoublesAsJavaScriptDoes() {
    assertEquals("1e-7", number("1E-07"));
    assertEquals("1e-7", CanonicalJson.toJavaScript(1e-7));
    assertEquals("1.5e-7", CanonicalJson.toJavaScript(1.5e-7));
    assertEquals("0.000001", number("1.0E-6"));
    assertEquals("1e+21", CanonicalJson.toJavaScript(1e21));
    assertEquals("123456789012345680000", CanonicalJson.toJavaScript(1.2345678901234568e20));
    assertEquals("0.1", number("0.1"));
    assertEquals("0.30000000000000004", CanonicalJson.toJavaScript(0.1 + 0.2));
    assertEquals("-2.5", number("-2.50"));
    assertEquals("5e-324", CanonicalJson.toJavaScript(Double.MIN_VALUE));
    assertEquals("1.7976931348623157e+308", CanonicalJson.toJavaScript(Double.MAX_VALUE));
    // Formatted from the value, not Double.toString, which isn't always the shortest form before JDK 19.
    assertEquals("2e+23", CanonicalJson.toJavaScript(2e23));
    assertEquals("1.0005e-7", number("1.0005E-7"));
  }

  @Test
  void writesWholeDoublesAsIntegers() {
    assertEquals("300", number("300.0"));
    assertEquals("0", number("0.0"));
    assertEquals("0", number("-0.0"));
    assertEquals("100", number("1E+2"));
  }

  @Test
  void writesIntegersAboveTwoToTheFiftyThirdExactly() {
    assertEquals("9007199254740993", number("9007199254740993"));
    assertEquals("123456789012345678901", number("123456789012345678901"));
    assertEquals("9007199254740992.5", number("9007199254740992.5"));
    assertEquals("9007199254740992", number("9007199254740992"));
  }

  @Test
  void parseKeepsNumbersExactly() {
    JsonNode parsed = CanonicalJson.parse("{\"big\": 123456789012345678901, \"frac\": 9007199254740992.5, \"x\": 300.0}");
    assertEquals(
      "{\n  \"big\": 123456789012345678901,\n  \"frac\": 9007199254740992.5,\n  \"x\": 300\n}\n",
      CanonicalJson.toCanonicalString(parsed)
    );
  }

  @Test
  void sortsKeysAsAJavaScriptObjectOrdersThem() {
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put("b", 1);
    node.put("a", 2);
    node.put("10", 3);
    node.put("2", 4);
    node.put("01", 5);
    node.put("B", 6);
    assertEquals(
      "{\n  \"2\": 4,\n  \"10\": 3,\n  \"01\": 5,\n  \"B\": 6,\n  \"a\": 2,\n  \"b\": 1\n}\n",
      CanonicalJson.toCanonicalString(node)
    );
  }

  @Test
  void escapesStringsAsJsonStringify() {
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put("s", "+1/é\"\\\n\t\u0001 😀\ud800");
    assertEquals(
      "{\n  \"s\": \"+1/é\\\"\\\\\\n\\t\\u0001 😀\\ud800\"\n}\n",
      CanonicalJson.toCanonicalString(node)
    );
  }

  @Test
  void writesEmptyContainersInline() {
    assertEquals("{\n  \"a\": [],\n  \"o\": {}\n}\n", CanonicalJson.toCanonicalString(CanonicalJson.parse("{\"o\":{},\"a\":[]}")));
  }

  @Test
  void canonicalizeNormalizesNumberNodes() {
    JsonNode node = CanonicalJson.canonicalize(CanonicalJson.parse("{\"x\": 300.0, \"y\": 1.5}"));
    assertTrue(node.get("x").isIntegralNumber());
    assertEquals(300, node.get("x").intValue());
    assertTrue(node.get("y").isDouble());
    assertTrue(CanonicalJson.isIntegral(CanonicalJson.parse("4.0")));
    assertFalse(CanonicalJson.isIntegral(CanonicalJson.parse("4.5")));
  }
}
