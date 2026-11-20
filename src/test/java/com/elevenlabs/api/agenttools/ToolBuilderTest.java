package com.elevenlabs.api.agenttools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.elevenlabs.api.agentworkflows.Destination;
import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.types.PreToolSpeechMode;
import com.elevenlabs.api.types.SystemToolConfigInput;
import com.elevenlabs.api.types.ToolRequestModel;
import com.elevenlabs.api.types.ToolResponseModel;
import com.elevenlabs.api.types.TransferTypeEnum;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class ToolBuilderTest {

  private static WebhookTool getOrder() {
    return Tool.webhook("get_order", "Look up an order")
      .get("https://api.example.com/orders/{order_id}")
      .pathParam("order_id", Schema.string().describe("The order number"));
  }

  private static JsonNode json(String text) {
    return CanonicalJson.parse(text);
  }

  private static List<String> codes(Supplier<?> call) {
    ToolValidationException e = assertThrows(ToolValidationException.class, call::get);
    List<String> codes = new ArrayList<>();
    for (ToolIssue issue : e.getIssues()) {
      codes.add(issue.getCode());
    }
    return codes;
  }

  private static List<String> paths(Supplier<?> call) {
    ToolValidationException e = assertThrows(ToolValidationException.class, call::get);
    List<String> paths = new ArrayList<>();
    for (ToolIssue issue : e.getIssues()) {
      paths.add(issue.getCode() + "@" + issue.getPath().orElse(""));
    }
    return paths;
  }

  /** The request model as JSON, keys sorted. */
  private static JsonNode sent(Tool tool) {
    return CanonicalJson.canonicalize(CanonicalJson.toTree(tool.build()));
  }

  // ---- Schema ------------------------------------------------------------------------------------------------

  @Test
  void aSchemaIsAValueAndEditingACopyLeavesTheOthersAlone() {
    LiteralSchema base = Schema.string().describe("Base");
    LiteralSchema optional = base.optional();
    ObjectSchema body = Schema.object().property("a", base);
    ObjectSchema more = body.property("b", optional);
    assertFalse(base.isOptional());
    assertTrue(optional.isOptional());
    assertEquals(Collections.singletonList("a"), body.getPropertyNames());
    assertEquals(Arrays.asList("a", "b"), more.getPropertyNames());
    assertEquals(json("{\"properties\":{\"a\":{\"description\":\"Base\",\"type\":\"string\"}},\"required\":[\"a\"],\"type\":\"object\"}"), body.toJson());
  }

  @Test
  void replacingAPropertyKeepsItsPosition() {
    ObjectSchema body = Schema.object()
      .property("a", Schema.string().describe("A"))
      .property("b", Schema.string().describe("B"))
      .property("a", Schema.integer().describe("A again"));
    assertEquals(Arrays.asList("a", "b"), body.getPropertyNames());
    assertEquals(SchemaKind.INTEGER, body.propertySchema("a").get().getKind());
  }

  @Test
  void loadsPropertiesNotInRequiredAsOptional() {
    ObjectSchema schema = (ObjectSchema) Schema.fromJson(
      "{\"type\":\"object\",\"properties\":{\"a\":{\"type\":\"string\",\"description\":\"A\"},\"b\":{\"type\":\"string\",\"description\":\"B\"}},\"required\":[\"b\"]}"
    );
    assertTrue(schema.propertySchema("a").get().isOptional());
    assertFalse(schema.propertySchema("b").get().isOptional());
  }

  // ---- Webhook tools -----------------------------------------------------------------------------------------

  @Test
  void buildsTheGeneratedRequestModel() {
    ToolRequestModel model = getOrder()
      .header("Authorization", Header.secret("sec_orders"))
      .options(new ToolOptions().preToolSpeech(PreToolSpeechMode.FORCE).responseTimeoutSecs(10))
      .build();
    assertTrue(model.getToolConfig().isWebhook());
    assertEquals("get_order", model.getToolConfig().getWebhook().get().getName());
    assertEquals(
      "https://api.example.com/orders/{order_id}",
      model.getToolConfig().getWebhook().get().getApiSchema().getUrl()
    );
  }

  @Test
  void reportsEveryProblemAtOnceWithPathsAndTheToolName() {
    WebhookTool tool = Tool.webhook("broken", "Broken")
      .post("https://api.example.com/{a}")
      .queryParam("q", Schema.string());
    ToolValidationException e = assertThrows(ToolValidationException.class, tool::build);
    assertEquals("broken", e.getToolName());
    assertTrue(e.getMessage().startsWith("Invalid tool \"broken\":"));
    assertEquals(
      Arrays.asList("missing_path_param@pathParams.a", "missing_value_source@query.q", "missing_body@body"),
      paths(tool::build)
    );
  }

  @Test
  void removingWhatDoesNotExistDoesNothing() {
    String before = getOrder().assign("a", "a").toJsonString();
    String after = getOrder()
      .assign("a", "a")
      .removePathParam("missing")
      .removeQueryParam("missing")
      .removeHeader("missing")
      .removeAssignment("missing")
      .toJsonString();
    assertEquals(before, after);
  }

  @Test
  void removingTheLastAssignmentLeavesAssignmentsOut() {
    WebhookTool tool = getOrder().assign("a", "a").removeAssignment("a");
    assertFalse(tool.toJson().has("assignments"));
    assertEquals(getOrder().toJsonString(), tool.toJsonString());
  }

  @Test
  void aLoadedEmptyAssignmentsListRoundTripsUnlessEdited() {
    ObjectNode raw = getOrder().toJson();
    raw.set("assignments", ToolWire.NODES.arrayNode());
    WebhookTool tool = Tool.fromJson(raw).asWebhook().get();
    assertEquals(raw, tool.toJson());
    assertEquals(raw, tool.removeAssignment("missing").toJson());
  }

  @Test
  void removingTheLastQueryParamLeavesTheQuerySchemaOut() {
    WebhookTool built = getOrder().queryParam("q", Schema.string().describe("Q")).removeQueryParam("q");
    assertFalse(built.toJson().get("api_schema").has("query_params_schema"));
    // A `type` left from the loaded schema isn't content either.
    WebhookTool loaded = Tool.fromJson(
      "{\"type\":\"webhook\",\"name\":\"t\",\"description\":\"T\",\"api_schema\":{\"method\":\"GET\",\"url\":\"https://x.test/\","
        + "\"query_params_schema\":{\"type\":\"object\",\"properties\":{\"q\":{\"type\":\"string\",\"description\":\"Q\"}},\"required\":[\"q\"]}}}"
    ).asWebhook().get();
    assertTrue(loaded.toJson().get("api_schema").get("query_params_schema").has("type"));
    assertFalse(loaded.removeQueryParam("q").toJson().get("api_schema").has("query_params_schema"));
  }

  @Test
  void aLoadedQuerySchemaKeepsEveryLoadedField() {
    JsonNode raw = json(
      "{\"api_schema\":{\"method\":\"GET\",\"query_params_schema\":{\"description\":\"\",\"properties\":{\"q\":{\"description\":\"Query\",\"type\":\"string\"}},"
        + "\"required\":[\"q\"],\"x_custom\":{\"kept\":true}},\"url\":\"https://api.example.com/search\"},\"description\":\"Search\",\"name\":\"search\",\"type\":\"webhook\"}"
    );
    WebhookTool tool = Tool.fromJson(raw).asWebhook().get();
    assertEquals(raw, tool.toJson());
    JsonNode written = tool.queryParam("limit", Schema.integer().constant(10)).toJson().get("api_schema").get("query_params_schema");
    assertEquals(json("{\"kept\":true}"), written.get("x_custom"));
    assertFalse(written.has("type"));
    assertEquals(json("[\"q\",\"limit\"]"), written.get("required"));
    for (String query : new String[] { "{\"required\":[],\"x_custom\":1}", "{\"properties\":{},\"type\":\"object\"}", "{}" }) {
      JsonNode empty = json(
        "{\"api_schema\":{\"method\":\"GET\",\"query_params_schema\":" + query + ",\"url\":\"https://x.test/\"},\"description\":\"T\",\"name\":\"t\",\"type\":\"webhook\"}"
      );
      assertEquals(CanonicalJson.toCanonicalString(empty), Tool.fromJson(empty).toJsonString(), query);
    }
  }

  @Test
  void anEmptyUrlIsMissing() {
    assertEquals(Collections.singletonList("missing_url"), codes(() -> Tool.webhook("t", "T").get("").build()));
    Tool loaded = Tool.fromJson("{\"type\":\"webhook\",\"name\":\"t\",\"description\":\"T\",\"api_schema\":{\"method\":\"GET\",\"url\":\"\"}}");
    assertEquals(Collections.singletonList("missing_url"), codes(loaded::build));
  }

  @Test
  void loadedContainersOfAnotherTypeAreKeptAndReported() {
    String notObject = "{\"description\":\"X\",\"type\":\"string\"}";
    WebhookTool tool = Tool.fromJson(
      "{\"type\":\"webhook\",\"name\":\"t\",\"description\":\"T\",\"api_schema\":{\"method\":\"POST\",\"url\":\"https://x.test/\","
        + "\"request_body_schema\":" + notObject + ",\"response_body_schema\":" + notObject + ",\"query_params_schema\":" + notObject + "}}"
    ).asWebhook().get();
    assertEquals(SchemaKind.STRING, tool.getBodySchema().get().getKind());
    assertEquals(json(notObject), tool.getBodySchema().get().toJson());
    assertEquals(
      Arrays.asList("body_not_object@query", "body_not_object@body", "body_not_object@response"),
      paths(tool::build)
    );
    ClientTool client = Tool.fromJson("{\"type\":\"client\",\"name\":\"t\",\"description\":\"T\",\"parameters\":" + notObject + "}")
      .asClient()
      .get()
      .param("y", Schema.string().describe("Y"));
    assertFalse(client.getParametersSchema().isPresent());
    assertEquals(Collections.singletonList("body_not_object@params"), paths(client::build));
  }

  @Test
  void aLoadedEmptySchemaIsAValidEmptyObject() {
    Tool tool = Tool.fromJson(
      "{\"type\":\"webhook\",\"name\":\"t\",\"description\":\"T\",\"api_schema\":{\"method\":\"POST\",\"url\":\"https://x.test/\","
        + "\"request_body_schema\":{},\"response_body_schema\":{},\"query_params_schema\":{}}}"
    );
    tool.build();
    JsonNode api = tool.toJson().get("api_schema");
    assertEquals(json("{\"type\":\"object\"}"), api.get("request_body_schema"));
    assertEquals(json("{\"type\":\"object\"}"), api.get("response_body_schema"));
    assertEquals(json("{}"), api.get("query_params_schema"));
  }

  @Test
  void aPathParamIsRequiredEvenWhenMarkedOptional() {
    WebhookTool omitted = Tool.webhook("t", "T").get("https://x.test/{id}").pathParam("id", Schema.string().omit().optional());
    assertEquals(Collections.singletonList("omitted_required@pathParams.id"), paths(omitted::build));
    WebhookTool optional = Tool.webhook("t", "T").get("https://x.test/{id}").pathParam("id", Schema.string().describe("Id").optional());
    optional.build();
    Tool reloaded = Tool.fromJson(optional.toJson());
    reloaded.build();
    assertEquals(optional.toJsonString(), reloaded.toJsonString());
  }

  @Test
  void checksTimeoutsRedirectsAndOptionsOfAnotherKind() {
    assertEquals(
      Collections.singletonList("invalid_option@responseTimeoutSecs"),
      paths(() -> getOrder().options(new ToolOptions().responseTimeoutSecs(4)).build())
    );
    getOrder().options(new ToolOptions().responseTimeoutSecs(300)).build();
    assertEquals(
      Collections.singletonList("invalid_option@followRedirects"),
      paths(() -> getOrder().options(new ToolOptions().followRedirects(true)).build())
    );
    getOrder().options(new ToolOptions().followRedirects(true).followRedirectsAllowedDomains(Arrays.asList("x.test"))).build();
    assertEquals(
      Collections.singletonList("invalid_option@expectsResponse"),
      paths(() -> getOrder().options(new ToolOptions().expectsResponse(true)).build())
    );
  }

  @Test
  void exposesTheBodySchemaForEditing() {
    WebhookTool tool = Tool.webhook("t", "T")
      .post("https://x.test/")
      .body(Schema.object().property("a", Schema.string().describe("A")));
    ObjectSchema body = (ObjectSchema) tool.getBodySchema().get();
    tool.body(body.property("b", Schema.string().describe("B").optional()));
    assertEquals(json("[\"a\"]"), tool.toJson().get("api_schema").get("request_body_schema").get("required"));
    assertEquals(2, tool.toJson().get("api_schema").get("request_body_schema").get("properties").size());
  }

  // ---- Client tools ------------------------------------------------------------------------------------------

  @Test
  void removingTheLastClientParamLeavesParametersOut() {
    ClientTool tool = Tool.client("show", "Show").param("a", Schema.string().describe("A")).removeParam("a");
    assertFalse(tool.getParametersSchema().isPresent());
    assertEquals(json("{\"description\":\"Show\",\"name\":\"show\",\"type\":\"client\"}"), tool.toJson());
  }

  @Test
  void loadedEmptyParametersRoundTripUnlessEdited() {
    JsonNode raw = json("{\"description\":\"T\",\"name\":\"t\",\"parameters\":{\"type\":\"object\"},\"type\":\"client\"}");
    ClientTool tool = Tool.fromJson(raw).asClient().get();
    assertEquals(raw, tool.toJson());
    assertEquals(raw, tool.removeParam("missing").toJson());
  }

  @Test
  void clientTimeoutsAreOneToOneTwentyOrMinusOneWithExpectsResponse() {
    ClientTool tool = Tool.client("t", "T");
    assertEquals(
      Collections.singletonList("invalid_option"),
      codes(() -> Tool.client("t", "T").options(new ToolOptions().responseTimeoutSecs(121)).build())
    );
    assertEquals(
      Collections.singletonList("invalid_option"),
      codes(() -> Tool.client("t", "T").options(new ToolOptions().responseTimeoutSecs(-1)).build())
    );
    tool.options(new ToolOptions().responseTimeoutSecs(-1).expectsResponse(true)).build();
  }

  @Test
  void checksRequireAnyOfAgainstTheParamsItHas() {
    Tool tool = Tool.fromJson(
      "{\"type\":\"client\",\"name\":\"t\",\"description\":\"T\",\"parameters\":{\"type\":\"object\",\"properties\":{\"a\":{\"type\":\"string\",\"description\":\"A\"}},"
        + "\"required_constraints\":{\"any_of\":[{\"required\":[\"a\",\"b\"]}]}}}"
    );
    assertEquals(Collections.singletonList("unknown_required_property@params"), paths(tool::build));
  }

  // ---- System tools ------------------------------------------------------------------------------------------

  @Test
  void buildsAnEntryForBuiltInTools() {
    SystemToolConfigInput config = SystemTool.endCall().buildBuiltInConfig();
    assertEquals("end_call", config.getName());
    JsonNode wire = SystemTool.endCall().toJson();
    assertEquals(json("{\"name\":\"end_call\",\"params\":{\"system_tool_type\":\"end_call\"},\"type\":\"system\"}"), wire);
  }

  @Test
  void transfersOnlyOnTransferToolsAndSipOnlyWithSipRefer() {
    assertEquals(
      Collections.singletonList("invalid_option@transfer"),
      paths(() -> SystemTool.endCall().transfer("x", Destination.phone("+15551234567")).build())
    );
    assertEquals(
      Arrays.asList("invalid_destination", "no_transfers"),
      codes(() -> SystemTool.transferToNumber().transfer("x", Destination.sipVariable("sip")).build())
    );
    SystemTool ok = SystemTool.transferToNumber()
      .transfer("x", Destination.sip("sip:a@b.test"), new ToolTransferOptions().transferType(TransferTypeEnum.SIP_REFER));
    ok.build();
    assertEquals(
      Arrays.asList("invalid_option@delayMs"),
      paths(() ->
        SystemTool.transferToNumber()
          .transfer("x", Destination.phone("+15551234567"), new ToolTransferOptions().delayMs(5))
          .build()
      )
    );
  }

  @Test
  void loadedSystemParamsWithoutASystemToolTypeAreKeptAndReported() {
    for (String params : new String[] { "[]", "42", "{}", "{\"system_tool_type\":5}", "{\"system_tool_type\":\"\"}", null }) {
      String raw = "{\"name\":\"end_call\"," + (params == null ? "" : "\"params\":" + params + ",") + "\"type\":\"system\"}";
      Tool tool = Tool.fromJson(raw);
      assertEquals(Collections.singletonList("invalid_model@params"), paths(tool::build), raw);
      assertEquals(json(raw), tool.toJson(), raw);
    }
    // A system_tool_type the builder has no constructor for is still fine.
    Tool.fromJson("{\"name\":\"x\",\"params\":{\"system_tool_type\":\"end_procedure\"},\"type\":\"system\"}").build();
  }

  // ---- Loading -----------------------------------------------------------------------------------------------

  @Test
  void loadsARequestOrResponseWrapper() {
    String config = getOrder().toJsonString();
    assertEquals(config, Tool.fromJson("{\"tool_config\":" + config + "}").toJsonString());
    assertEquals(config, Tool.fromJson(CanonicalJson.toTree(getOrder().build())).toJsonString());
  }

  @Test
  void loadsTheResultOfToolsGet() throws Exception {
    ToolResponseModel response = ObjectMappers.JSON_MAPPER.readValue(
      "{\"id\":\"tool_1\",\"tool_config\":{\"type\":\"client\",\"name\":\"t\",\"description\":\"T\",\"parameters\":{\"type\":\"object\","
        + "\"properties\":{\"x\":{\"type\":\"string\",\"description\":\"X\",\"dynamic_variable\":\"\",\"constant_value\":\"\",\"is_system_provided\":false}},"
        + "\"required\":[\"x\"]}},\"access_info\":{\"is_creator\":true,\"creator_name\":\"a\",\"creator_email\":\"a@b.test\",\"role\":\"admin\"},"
        + "\"usage_stats\":{\"avg_latency_secs\":1}}",
      ToolResponseModel.class
    );
    Tool tool = Tool.fromResponse(response);
    assertEquals(ToolKind.CLIENT, tool.getKind());
    tool.build();
    // The API's defaults for unset fields are written back as loaded.
    assertEquals(json("\"\""), tool.toJson().get("parameters").get("properties").get("x").get("constant_value"));
  }

  @Test
  void readsNullAsAbsentWhateverThePropertyIsNamed() {
    for (String name : new String[] { "x", "constant_value", "dynamic_variable_placeholders" }) {
      Tool tool = Tool.fromJson(
        "{\"name\":\"t\",\"description\":\"T\",\"type\":\"client\",\"response_timeout_secs\":null,\"parameters\":{\"type\":\"object\",\"description\":null,"
          + "\"properties\":{\"a\":{\"type\":\"string\",\"description\":\"A\",\"dynamic_variable\":null},\"" + name + "\":null}}}"
      );
      tool.build();
      assertEquals(
        json("{\"description\":\"T\",\"name\":\"t\",\"parameters\":{\"properties\":{\"a\":{\"description\":\"A\",\"type\":\"string\"}},\"type\":\"object\"},\"type\":\"client\"}"),
        tool.toJson(),
        name
      );
    }
  }

  @Test
  void keepsAToolOfAnUnknownTypeAndWarnsWhenBuilt() {
    JsonNode raw = json("{\"description\":\"d\",\"mcp_server_id\":\"m\",\"name\":\"search\",\"type\":\"mcp\",\"x\":null}");
    Tool tool = Tool.fromJson(raw);
    assertEquals(ToolKind.OTHER, tool.getKind());
    assertFalse(tool.asWebhook().isPresent());
    List<String> warnings = new ArrayList<>();
    tool.build(issue -> warnings.add(issue.getCode()));
    assertEquals(Collections.singletonList("unknown_tool_type"), warnings);
    assertEquals(CanonicalJson.toCanonicalString(raw), tool.toJsonString());
    assertEquals(
      Collections.singletonList("invalid_option"),
      codes(() -> tool.options(new ToolOptions().name("other")).toJson())
    );
  }

  @Test
  void aModeledSchemaFieldOfTheWrongShapeIsKeptAndReported() {
    String[] parameters = {
      "{\"properties\":{\"x\":{\"description\":\"X\",\"is_system_provided\":\"yes\",\"type\":\"string\"}},\"type\":\"object\"}",
      "{\"properties\":{\"x\":{\"description\":\"X\",\"is_omitted\":\"yes\",\"type\":\"string\"}},\"type\":\"object\"}",
      "{\"properties\":{\"x\":{\"description\":\"X\",\"dynamic_variable\":5,\"type\":\"string\"}},\"type\":\"object\"}",
      "{\"properties\":{\"x\":{\"description\":\"X\",\"enum\":[1,2],\"type\":\"string\"}},\"type\":\"object\"}",
      "{\"properties\":{\"x\":{\"description\":\"X\",\"type\":\"string\"}},\"required\":\"x\",\"type\":\"object\"}",
      "{\"properties\":{\"x\":{\"description\":\"X\",\"type\":\"string\"}},\"required\":[\"x\",1],\"type\":\"object\"}",
      "{\"properties\":\"x\",\"type\":\"object\"}",
      "{\"properties\":{\"x\":5},\"required\":[\"x\"],\"type\":\"object\"}",
      "{\"properties\":{\"x\":{\"description\":\"X\",\"type\":\"string\"}},\"required_constraints\":{\"any_of\":\"x\"},\"type\":\"object\"}",
      "{\"properties\":{\"x\":{\"items\":\"x\",\"type\":\"array\"}},\"type\":\"object\"}",
      "{\"properties\":{\"x\":{\"allowed_values\":\"x\",\"description\":\"X\",\"type\":\"string\"}},\"type\":\"object\"}",
    };
    for (String params : parameters) {
      String raw = "{\"description\":\"T\",\"name\":\"t\",\"parameters\":" + params + ",\"type\":\"client\"}";
      Tool tool = Tool.fromJson(raw);
      assertEquals(Collections.singletonList("invalid_model"), codes(tool::build), raw);
      // toJson() writes the input back as loaded.
      assertEquals(json(raw), tool.toJson(), raw);
    }
  }

  @Test
  void loadedClientParametersThatAreNotAnObjectAreKeptAndReported() {
    for (String params : new String[] { "42", "\"x\"", "[]" }) {
      String raw = "{\"description\":\"T\",\"name\":\"t\",\"parameters\":" + params + ",\"type\":\"client\"}";
      ClientTool tool = Tool.fromJson(raw).asClient().get();
      assertFalse(tool.getParametersSchema().isPresent());
      assertEquals(Collections.singletonList("invalid_model@params"), paths(tool::build));
      assertEquals(json(raw), tool.toJson());
      // Adding a param doesn't replace them.
      assertEquals(json(raw), tool.param("y", Schema.string().describe("Y")).toJson());
    }
  }

  @Test
  void loadedAllowedValuesAndRequiredConstraintsKeepTheirOtherFields() {
    Tool tool = Tool.fromJson(
      "{\"type\":\"client\",\"name\":\"t\",\"description\":\"T\",\"parameters\":{\"type\":\"object\",\"properties\":{"
        + "\"a\":{\"type\":\"string\",\"description\":\"A\",\"allowed_values\":{\"dynamic_variable\":\"ids\",\"x_extra\":1}},"
        + "\"b\":{\"type\":\"string\",\"description\":\"B\"}},"
        + "\"required_constraints\":{\"any_of\":[{\"required\":[\"a\"],\"x_group\":true}],\"x_top\":\"kept\"}}}"
    );
    ClientTool client = tool.asClient().get();
    LiteralSchema a = (LiteralSchema) client.getParametersSchema().get().propertySchema("a").get();
    ObjectSchema edited = client.getParametersSchema().get().property("a", a.allowedFrom("other_ids")).requireAllOf("b");
    JsonNode params = Tool.client("t", "T").param("p", edited).toJson().get("parameters").get("properties").get("p");
    assertEquals(json("{\"dynamic_variable\":\"other_ids\",\"x_extra\":1}"), params.get("properties").get("a").get("allowed_values"));
    assertEquals(
      json("{\"all_of\":[{\"required\":[\"b\"]}],\"any_of\":[{\"required\":[\"a\"],\"x_group\":true}],\"x_top\":\"kept\"}"),
      params.get("required_constraints")
    );
    tool.build();
  }

  // ---- Constants ---------------------------------------------------------------------------------------------

  @Test
  void aNullConstantOfANullableLiteralSurvivesLoadingButBuildReportsIt() {
    for (String name : new String[] { "x", "constant_value", "dynamic_variable_placeholders", "properties" }) {
      Supplier<Schema> nullable = () -> Schema.fromJson("{\"type\":[\"string\",\"null\"]}").constant(null);
      ClientTool tool = Tool.client("t", "T").param(name, nullable.get());
      assertEquals(Collections.singletonList("constant_type_mismatch@params." + name), paths(tool::build), name);
      JsonNode written = tool.toJson();
      assertEquals(
        json("{\"constant_value\":null,\"type\":[\"string\",\"null\"]}"),
        written.get("parameters").get("properties").get(name)
      );
      assertEquals(written, Tool.fromJson(written).toJson());
      WebhookTool webhook = Tool.webhook("t", "T")
        .post("https://x.test/{" + name + "}")
        .pathParam(name, nullable.get())
        .body(Schema.object().property("b", Schema.object().property(name, nullable.get())));
      assertEquals(
        Arrays.asList("constant_type_mismatch@pathParams." + name, "constant_type_mismatch@body.b." + name),
        paths(webhook::build),
        name
      );
    }
  }

  @Test
  void aLoadedNullConstantOfANonNullableLiteralIsAbsent() {
    Tool tool = Tool.fromJson(
      "{\"type\":\"client\",\"name\":\"t\",\"description\":\"T\",\"parameters\":{\"type\":\"object\",\"properties\":{\"x\":{\"type\":\"string\",\"description\":\"X\",\"constant_value\":null}}}}"
    );
    tool.build();
    assertFalse(tool.toJson().get("parameters").get("properties").get("x").has("constant_value"));
    // A user's null on a non-nullable literal is a mismatch.
    assertEquals(
      Collections.singletonList("constant_type_mismatch"),
      codes(() -> Tool.client("t", "T").param("x", Schema.string().constant(null)).toJson())
    );
  }

  @Test
  void rejectsConstantsTheGeneratedModelWouldChange() {
    // Beyond the long range: rejected by toJson() and build(), as in every SDK.
    BigInteger big = new BigInteger("9223372036854775809");
    assertEquals(
      Collections.singletonList("constant_type_mismatch@params.id"),
      paths(() -> Tool.client("t", "T").param("id", Schema.integer().constant(big)).toJson())
    );
    Tool loaded = Tool.fromJson(
      "{\"name\":\"t\",\"description\":\"T\",\"type\":\"client\",\"parameters\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"constant_value\":9223372036854775809}}}}"
    );
    assertEquals(Collections.singletonList("constant_type_mismatch"), codes(loaded::build));
    // Inside an array or object constant the value is free-form JSON; the generated model can't hold it either.
    ClientTool tags = Tool.client("t", "T").param("tags", Schema.array(Schema.integer().describe("Tag")).constant(Arrays.asList(big)));
    assertTrue(tags.toJsonString().contains("9223372036854775809"));
    assertEquals(Collections.singletonList("constant_type_mismatch@params.tags"), paths(tags::build));
    // The generated literal constant is an int: a long beyond it would be truncated, so build() reports it.
    ClientTool wide = Tool.client("t", "T").param("n", Schema.integer().constant(3000000000L));
    assertTrue(wide.toJsonString().contains("\"constant_value\": 3000000000"));
    assertEquals(Collections.singletonList("constant_type_mismatch@params.n"), paths(wide::build));
    // A decimal no double holds exactly.
    Tool decimal = Tool.fromJson(
      "{\"name\":\"t\",\"description\":\"T\",\"type\":\"client\",\"parameters\":{\"properties\":{\"n\":{\"type\":\"number\",\"constant_value\":9007199254740992.5}}}}"
    );
    assertTrue(decimal.toJsonString().contains("9007199254740992.5"));
    assertEquals(Collections.singletonList("constant_type_mismatch@params.n"), paths(decimal::build));
    // Non-finite numbers, anywhere in a constant.
    assertEquals(
      Collections.singletonList("constant_type_mismatch"),
      codes(() -> Tool.client("t", "T").param("n", Schema.number().constant(Double.NaN)).toJson())
    );
    Map<String, Object> withInfinity = new LinkedHashMap<>();
    withInfinity.put("a", Double.POSITIVE_INFINITY);
    assertEquals(
      Collections.singletonList("constant_type_mismatch"),
      codes(() -> Tool.client("t", "T").param("o", Schema.object().constant(withInfinity)).toJson())
    );
  }

  @Test
  void integerConstantsAreJudgedFromTheirDigits() {
    for (String constant : new String[] { "1.5", "15e-1", "9007199254740992.5", "1e-1" }) {
      Tool tool = Tool.fromJson(
        "{\"name\":\"t\",\"description\":\"T\",\"type\":\"client\",\"parameters\":{\"properties\":{\"n\":{\"type\":\"integer\",\"constant_value\":" + constant + "}}}}"
      );
      assertEquals(Collections.singletonList("constant_type_mismatch"), codes(tool::toJson), constant);
    }
    for (String constant : new String[] { "3", "3.0", "1e2", "-7" }) {
      Tool tool = Tool.fromJson(
        "{\"name\":\"t\",\"description\":\"T\",\"type\":\"client\",\"parameters\":{\"properties\":{\"n\":{\"type\":\"integer\",\"constant_value\":" + constant + "}}}}"
      );
      tool.build();
    }
    Tool.client("t", "T").param("n", Schema.integer().constant(3.0)).build();
  }

  @Test
  void largeIntegersAndEmptyObjectsInConstantsAreSentExactly() {
    Map<String, Object> constant = new LinkedHashMap<>();
    constant.put("big", 9007199254740993L);
    constant.put("empty", new LinkedHashMap<String, Object>());
    constant.put("list", new ArrayList<Object>());
    ClientTool tool = Tool.client("t", "T").param("o", Schema.object().constant(constant));
    String out = tool.toJsonString();
    assertTrue(out.contains("\"big\": 9007199254740993"), out);
    assertTrue(out.contains("\"empty\": {}"), out);
    JsonNode sent = sent(tool).get("tool_config").get("parameters").get("properties").get("o").get("constant_value");
    assertEquals(CanonicalJson.toCanonicalString(CanonicalJson.parse("{\"big\":9007199254740993,\"empty\":{},\"list\":[]}")), CanonicalJson.toCanonicalString(sent));
  }

  @Test
  void numericPropertyNamesStayAnObject() {
    String raw =
      "{\"type\":\"client\",\"name\":\"t\",\"description\":\"T\",\"parameters\":{\"type\":\"object\",\"properties\":{"
        + "\"b\":{\"type\":\"string\",\"description\":\"B\"},\"1\":{\"type\":\"string\",\"description\":\"One\"},\"0\":{\"type\":\"integer\",\"constant_value\":5}},"
        + "\"required\":[\"1\",\"b\",\"0\"]}}";
    Tool tool = Tool.fromJson(raw);
    JsonNode properties = tool.toJson().get("parameters").get("properties");
    assertTrue(properties.isObject());
    List<String> names = new ArrayList<>();
    properties.fieldNames().forEachRemaining(names::add);
    // As a JavaScript object orders them: array indices first.
    assertEquals(Arrays.asList("0", "1", "b"), names);
    assertEquals(json("[\"1\",\"b\",\"0\"]"), tool.toJson().get("parameters").get("required"));
    assertEquals(tool.toJsonString(), Tool.fromJson(CanonicalJson.toTree(tool.build())).toJsonString());
    // Built in code, in any insertion order.
    ClientTool built = Tool.client("t", "T").param("1", Schema.string().describe("One")).param("0", Schema.string().describe("Zero"));
    assertTrue(built.toJson().get("parameters").get("properties").isObject());
    built.build();
  }

  @Test
  void writesNumbersAsTheJavaScriptSdkDoes() {
    ClientTool tool = Tool.client("t", "T")
      .param("a", Schema.number().constant(1e-7))
      .param("b", Schema.number().constant(1.5e-7))
      .param("c", Schema.number().constant(1e21))
      .param("d", Schema.number().constant(123456789012345680000.0))
      .param("e", Schema.number().constant(0.000001))
      .param("f", Schema.number().constant(300.0));
    String out = tool.toJsonString();
    String[][] expected = { { "a", "1e-7" }, { "b", "1.5e-7" }, { "c", "1e+21" }, { "d", "123456789012345680000" }, { "e", "0.000001" }, { "f", "300" } };
    for (String[] pair : expected) {
      String line = "\"" + pair[0] + "\": {\n        \"constant_value\": " + pair[1] + ",\n        \"type\": \"number\"\n      }";
      assertTrue(out.contains(line), pair[0] + ": " + out);
    }
    assertEquals(out, Tool.fromJson(out).toJsonString());
    tool.build();
  }

  @Test
  void asKindReturnsTheSameTool() {
    WebhookTool tool = getOrder();
    Tool any = tool;
    assertSame(tool, any.asWebhook().get());
    assertFalse(any.asClient().isPresent());
    assertFalse(any.asSystem().isPresent());
  }
}
