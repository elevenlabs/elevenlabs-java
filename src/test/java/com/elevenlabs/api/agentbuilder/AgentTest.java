package com.elevenlabs.api.agentbuilder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elevenlabs.api.agenttools.SystemTool;
import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.elevenlabs.api.agentworkflows.ToolOptions;
import com.elevenlabs.api.agentworkflows.ConverseOptions;
import com.elevenlabs.api.agentworkflows.NodeUpdate;
import com.elevenlabs.api.agentworkflows.Workflow;
import com.elevenlabs.api.resources.agents.requests.BodyCreateAgentV1ConvaiAgentsCreatePost;
import com.elevenlabs.api.types.AgentConfig;
import com.elevenlabs.api.types.AgentPlatformSettingsRequestModel;
import com.elevenlabs.api.types.ConversationalConfig;
import com.elevenlabs.api.types.Llm;
import com.elevenlabs.api.types.PrivacyConfigInput;
import com.elevenlabs.api.types.PromptAgentApiModelInput;
import com.elevenlabs.api.types.TtsConversationalModel;
import com.elevenlabs.api.types.TurnConfig;
import com.elevenlabs.api.types.WorkflowToolLocatorSchemaOverridesValue;
import com.elevenlabs.api.core.ObjectMappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AgentTest {

  static JsonNode json(String text) {
    return CanonicalJson.parse(text);
  }

  static JsonNode fixture(String path) throws IOException {
    return json(TestFiles.read(TestFiles.CLI_PROJECT.resolve(path)));
  }

  static Map<String, String> ids(String... pairs) {
    Map<String, String> map = new LinkedHashMap<>();
    for (int i = 0; i < pairs.length; i += 2) {
      map.put(pairs[i], pairs[i + 1]);
    }
    return map;
  }

  /** Each issue as {@code code@path}. */
  static List<String> issuesOf(Runnable run) {
    AgentValidationException e = assertThrows(AgentValidationException.class, run::run);
    List<String> issues = new ArrayList<>();
    for (AgentIssue issue : e.getIssues()) {
      issues.add(issue.getCode() + "@" + issue.getPath().orElse(""));
    }
    return issues;
  }

  static List<String> names(List<Tool> tools) {
    List<String> names = new ArrayList<>();
    for (Tool tool : tools) {
      names.add(tool.getName());
    }
    return names;
  }

  // ---- toJson / build -----------------------------------------------------------------------------------------

  @Test
  void mapsTypedOptionsToolsAndSystemToolsIntoTheApiConfig() {
    Agent agent = new Agent(
      "Support line",
      new AgentOptions()
        .prompt("You are a support agent.")
        .firstMessage("Hi!")
        .language("en")
        .llm(Llm.GEMINI_2_0_FLASH)
        .temperature(0.5)
        .voiceId("voice_1")
        .ttsModel(TtsConversationalModel.ELEVEN_FLASH_V_2_5)
        .tags(Collections.singletonList("support"))
        .tools(SystemTool.endCall(), AgentPushTest.getOrder())
        .toolIds(Collections.singletonList("tool_existing"))
    );
    ObjectNode config = agent.toJson(ids("get_order", "tool_1"));
    assertEquals(
      json(
        "{\"conversation_config\":{\"agent\":{\"first_message\":\"Hi!\",\"language\":\"en\",\"prompt\":{" +
        "\"built_in_tools\":{\"end_call\":{\"name\":\"end_call\",\"params\":{\"system_tool_type\":\"end_call\"},\"type\":\"system\"}}," +
        "\"llm\":\"gemini-2.0-flash\",\"prompt\":\"You are a support agent.\",\"temperature\":0.5," +
        "\"tool_ids\":[\"tool_existing\",\"tool_1\"]}},\"tts\":{\"model_id\":\"eleven_flash_v2_5\",\"voice_id\":\"voice_1\"}}," +
        "\"name\":\"Support line\",\"platform_settings\":{},\"tags\":[\"support\"]}"
      ),
      config
    );
    // Keys sorted at every level.
    assertEquals(CanonicalJson.toCanonicalString(config), CanonicalJson.write(config, false));

    BodyCreateAgentV1ConvaiAgentsCreatePost body = agent.build(ids("get_order", "tool_1"));
    assertEquals("Support line", body.getName().get());
    assertEquals(Arrays.asList("tool_existing", "tool_1"), body.getConversationConfig().getAgent().get().getPrompt().get().getToolIds().get());
    assertTrue(body.getConversationConfig().getAgent().get().getPrompt().get().getBuiltInTools().get().getEndCall().isPresent());
  }

  @Test
  void typedOptionsTakeTheirPathsFromTheGeneratedModels() {
    Agent agent = new Agent("A", new AgentOptions().prompt("p").voiceId("v").ttsModel(TtsConversationalModel.ELEVEN_FLASH_V_2_5));
    ObjectNode config = agent.toJson();
    assertEquals("p", config.at("/conversation_config/agent/prompt/prompt").asText());
    assertEquals("v", config.at("/conversation_config/tts/voice_id").asText());
    assertEquals("eleven_flash_v2_5", config.at("/conversation_config/tts/model_id").asText());
  }

  @Test
  void mergesTheRawConfigUnderTheTypedOptions() {
    Agent agent = new Agent(
      "A",
      new AgentOptions()
        .prompt("p")
        .conversationConfig(
          ConversationalConfig.builder()
            .turn(TurnConfig.builder().turnTimeout(7.0).build())
            .agent(AgentConfig.builder().disableFirstMessageInterruptions(true).build())
            .build()
        )
        .platformSettings(AgentPlatformSettingsRequestModel.builder().privacy(PrivacyConfigInput.builder().recordVoice(false).build()).build())
    );
    agent.update(new AgentOptions().conversationConfig(ConversationalConfig.builder().turn(TurnConfig.builder().silenceEndCallTimeout(20.0).build()).build()));
    ObjectNode config = agent.toJson();
    assertEquals(
      json(
        "{\"agent\":{\"disable_first_message_interruptions\":true,\"prompt\":{\"prompt\":\"p\"}}," +
        "\"turn\":{\"silence_end_call_timeout\":20,\"turn_timeout\":7}}"
      ),
      config.get("conversation_config")
    );
    assertEquals(json("{\"privacy\":{\"record_voice\":false}}"), config.get("platform_settings"));
  }

  @Test
  void fillsInTheIdsOfToolObjectsUsedByWorkflowNodes() {
    Tool lookup = AgentPushTest.getOrder();
    Tool show = AgentPushTest.showOrder();
    Map<String, Map<String, WorkflowToolLocatorSchemaOverridesValue>> overrides = new LinkedHashMap<>();
    overrides.put(
      "get_order",
      Collections.singletonMap(
        "path_params.order_id",
        ObjectMappers.JSON_MAPPER.convertValue(json("{\"source\":\"constant\",\"constant_value\":\"1\"}"), WorkflowToolLocatorSchemaOverridesValue.class)
      )
    );
    Workflow wf = new Workflow();
    wf.converse("triage", "Find out what the caller needs")
      .when("Caller gave an order number")
      .tool("lookup", new ToolOptions().tools(lookup).schemaOverrides(overrides))
      .onSuccess()
      .converse("resolve", "Explain the order", new ConverseOptions().tools(show).toolIds(Collections.singletonList("tool_raw")));
    Agent agent = new Agent("A", new AgentOptions().prompt("p").workflow(wf));

    List<Tool> tools = agent.tools();
    assertEquals(2, tools.size());
    assertSame(lookup, tools.get(0));
    assertSame(show, tools.get(1));
    JsonNode nodes = agent.toJson(ids("get_order", "tool_g", "show_order", "tool_s")).at("/workflow/nodes");
    assertEquals(
      json("[{\"schema_overrides\":{\"path_params.order_id\":{\"constant_value\":\"1\",\"source\":\"constant\"}},\"tool_id\":\"tool_g\"}]"),
      nodes.at("/lookup/tools")
    );
    // Ids first, then Tool objects.
    assertEquals(json("[\"tool_raw\",\"tool_s\"]"), nodes.at("/resolve/additional_tool_ids"));
    assertEquals(
      Collections.singletonList("unresolved_tool@tools.show_order"),
      issuesOf(() -> agent.toJson(ids("get_order", "tool_g")))
    );
  }

  @Test
  void validatesWithoutIdsSoCiNeedsNoApiKey() {
    assertDoesNotThrow(() -> new Agent("A", new AgentOptions().prompt("p").tools(AgentPushTest.getOrder())).validate());
  }

  @Test
  void reportsEveryProblemAtOnce() {
    Workflow wf = new Workflow();
    wf.tool("hang_up", SystemTool.endCall());
    Agent agent = new Agent(
      "",
      new AgentOptions()
        .prompt("p")
        .tools(AgentPushTest.getOrder(), AgentPushTest.getOrder(), Tool.webhook("broken", "No URL"))
        .workflow(wf)
        .conversationConfig(
          ConversationalConfig.builder()
            .agent(AgentConfig.builder().prompt(PromptAgentApiModelInput.builder().prompt("raw prompt").build()).build())
            .build()
        )
    );
    assertEquals(
      Arrays.asList(
        "missing_name@",
        "option_conflict@conversation_config.agent.prompt.prompt",
        "duplicate_tool_name@tools.get_order",
        "invalid_tool@tools.broken",
        "system_tool_in_workflow@workflow.tools.end_call"
      ),
      issuesOf(agent::validate)
    );
  }

  @Test
  void warnsWithoutFailingWhenThereIsNoPrompt() {
    final List<String> warnings = new ArrayList<>();
    new Agent("A").validate(issue -> warnings.add(issue.getCode()));
    assertEquals(Collections.singletonList("missing_prompt"), warnings);
  }

  @Test
  void reportsAWorkflowThatDoesNotBuild() {
    Workflow wf = new Workflow();
    wf.goTo("nowhere");
    assertEquals(
      Collections.singletonList("invalid_workflow@workflow"),
      issuesOf(new Agent("A", new AgentOptions().prompt("p").workflow(wf))::validate)
    );
  }

  static Tool mcpTool() {
    return Tool.fromJson(json("{\"type\":\"mcp\",\"name\":\"docs_search\",\"description\":\"Search the docs\",\"mcp_server_id\":\"mcp_1\"}"));
  }

  @Test
  void keepsOtherToolKindsAttachedByTheIdTheyWereLoadedWith() {
    Tool mcp = mcpTool();
    Agent agent = Agent.fromJson(
      json("{\"name\":\"A\",\"conversation_config\":{\"agent\":{\"prompt\":{\"prompt\":\"p\",\"tool_ids\":[\"tool_mcp\"]}}}}"),
      Collections.singletonMap("tool_mcp", mcp)
    );
    agent.addTool(AgentPushTest.getOrder());
    // Push manages only webhook and client tools.
    assertEquals(Collections.singletonList("get_order"), names(agent.tools()));
    assertEquals(
      json("[\"tool_mcp\",\"tool_g\"]"),
      agent.toJson(ids("get_order", "tool_g")).at("/conversation_config/agent/prompt/tool_ids")
    );
  }

  @Test
  void rejectsAnOtherKindOfToolWithoutAnId() {
    Agent agent = new Agent("A", new AgentOptions().prompt("p")).addTool(mcpTool());
    assertTrue(agent.tools().isEmpty());
    AgentValidationException e = assertThrows(AgentValidationException.class, agent::validate);
    assertEquals(1, e.getIssues().size());
    AgentIssue issue = e.getIssues().get(0);
    assertEquals("invalid_tool", issue.getCode());
    assertEquals("tools.docs_search", issue.getPath().orElse(""));
    assertEquals(
      "Tool \"docs_search\" has type \"mcp\", which push doesn't create or update: load it with its id or attach it with toolIds",
      issue.getMessage()
    );

    Workflow wf = new Workflow();
    wf.tool("search", mcpTool());
    assertEquals(
      Collections.singletonList("invalid_tool@tools.docs_search"),
      issuesOf(new Agent("B", new AgentOptions().prompt("p").workflow(wf))::validate)
    );
  }

  @Test
  void attachesAToolAddedWithAddTool() {
    Agent agent = new Agent("A", new AgentOptions().prompt("p")).addTool(AgentPushTest.getOrder());
    assertEquals(json("[\"tool_1\"]"), agent.toJson(ids("get_order", "tool_1")).at("/conversation_config/agent/prompt/tool_ids"));
  }

  @Test
  void treatsAnExplicitlyEmptyToolListAsSet() {
    Agent agent = new Agent(
      "A",
      new AgentOptions()
        .prompt("p")
        .toolIds(Collections.<String>emptyList())
        .conversationConfig(
          ConversationalConfig.builder()
            .agent(AgentConfig.builder().prompt(PromptAgentApiModelInput.builder().toolIds(Collections.singletonList("tool_existing")).build()).build())
            .build()
        )
    );
    assertEquals(Collections.singletonList("option_conflict@conversation_config.agent.prompt.tool_ids"), issuesOf(agent::validate));
  }

  @Test
  void updateReplacesTypedOptionsAndRenames() {
    Agent agent = new Agent("A", new AgentOptions().prompt("one").tags(Collections.singletonList("x")));
    agent.update(new AgentOptions().name("B").prompt("two"));
    ObjectNode config = agent.toJson();
    assertEquals("B", agent.getName());
    assertEquals("two", config.at("/conversation_config/agent/prompt/prompt").asText());
    assertEquals(json("[\"x\"]"), config.get("tags"));
  }

  // ---- fromJson -----------------------------------------------------------------------------------------------

  @Test
  void roundTripsACliAgentConfigKeepingUnmodeledFieldsAndTheToolOrder() throws IOException {
    JsonNode raw = fixture("agent_configs/support.json");
    Tool lookup = Tool.fromJson(fixture("tool_configs/lookup_order.json"));
    Agent agent = Agent.fromJson(raw, Collections.singletonMap("tool_lookup", lookup));

    assertEquals("Support", agent.getName());
    assertEquals(Collections.singletonList(lookup), agent.tools());
    assertEquals(Collections.singletonList("tool_from_dashboard"), agent.getToolIds());
    assertEquals(CanonicalJson.canonicalize(raw), agent.toJson(ids("lookup_order", "tool_lookup")));
    assertEquals(
      CanonicalJson.toCanonicalString(raw),
      CanonicalJson.toCanonicalString(agent.toJson(ids("lookup_order", "tool_lookup")))
    );
  }

  @Test
  void acceptsAJsonString() throws IOException {
    Agent agent = Agent.fromJson(TestFiles.read(TestFiles.CLI_PROJECT.resolve("agent_configs/billing.json")));
    assertEquals("Billing", agent.getName());
    assertEquals(Collections.singletonList("tool_refund"), agent.getToolIds());
  }

  @Test
  void turnsALoadedWorkflowIntoAWorkflowOnFirstUseWithToolObjects() throws IOException {
    JsonNode raw = fixture("agent_configs/support.beta.json");
    Tool refund = Tool.fromJson(fixture("tool_configs/issue_refund.json"));
    Agent agent = Agent.fromJson(raw, Collections.singletonMap("tool_refund", refund));

    // Read from the raw workflow, before it's materialized.
    assertEquals(Collections.singletonList(refund), agent.tools());
    ObjectNode unedited = agent.toJson(ids("issue_refund", "tool_refund"));
    assertEquals(CanonicalJson.canonicalize(raw.get("workflow")), unedited.get("workflow"));

    agent.getWorkflow().get().node("refunds").update(new NodeUpdate().prompt("Handle refunds"));
    JsonNode nodes = agent.toJson(ids("issue_refund", "tool_new")).at("/workflow/nodes");
    assertEquals(json("[\"tool_new\"]"), nodes.at("/refunds/additional_tool_ids"));
    assertEquals("Handle refunds", nodes.at("/refunds/additional_prompt").asText());
    assertEquals(Collections.singletonList(refund), agent.tools());
  }

  @Test
  void writesNewToolIdsIntoALoadedWorkflowItHasNotMaterialized() throws IOException {
    JsonNode raw = fixture("agent_configs/support.beta.json");
    Tool refund = Tool.fromJson(fixture("tool_configs/issue_refund.json"));
    Agent agent = Agent.fromJson(raw, Collections.singletonMap("tool_refund", refund));
    assertEquals(json("[\"tool_clone\"]"), agent.toJson(ids("issue_refund", "tool_clone")).at("/workflow/nodes/refunds/additional_tool_ids"));
  }

  @Test
  void validatesALoadedWorkflowWithoutMaterializingIt() throws IOException {
    ObjectNode raw = (ObjectNode) fixture("agent_configs/support.beta.json");
    ((ObjectNode) raw.at("/workflow/edges")).set("broken", json("{\"source\":\"start_node\",\"target\":\"nowhere\"}"));
    Agent agent = Agent.fromJson(raw);
    assertEquals(Collections.singletonList("invalid_workflow@workflow"), issuesOf(agent::validate));
  }

  @Test
  void replacesALoadedAgentsSystemToolsAlongWithItsOtherTools() throws IOException {
    Agent agent = Agent.fromJson(fixture("agent_configs/support.json"));
    // System tools aren't managed by push.
    assertTrue(agent.tools().isEmpty());
    agent.update(new AgentOptions().tools(Collections.<Tool>emptyList()));
    JsonNode prompt = agent.toJson().at("/conversation_config/agent/prompt");
    assertFalse(prompt.has("built_in_tools"));
    // Ids it doesn't manage stay.
    assertEquals(json("[\"tool_lookup\",\"tool_from_dashboard\"]"), prompt.get("tool_ids"));
  }

  @Test
  void sendsAnEditedLoadedSystemToolAsBuilt() throws IOException {
    Agent agent = Agent.fromJson(fixture("agent_configs/support.json"));
    SystemTool endCall = SystemTool.endCall();
    agent.addTool(endCall);
    JsonNode builtIn = agent.toJson().at("/conversation_config/agent/prompt/built_in_tools");
    assertEquals(Arrays.asList("end_call", "transfer_to_agent"), fieldNames(builtIn));
    assertEquals(endCall.toJson(), builtIn.get("end_call"));
  }

  @Test
  void deepMergeCopiesWhatItMerges() {
    ObjectNode target = (ObjectNode) json("{\"turn\":{\"a\":1}}");
    ObjectNode source = (ObjectNode) json("{\"turn\":{\"b\":{\"c\":2}},\"list\":[1]}");
    Wire.deepMerge(target, source);
    ((ObjectNode) source.at("/turn/b")).put("c", 3);
    assertEquals(json("{\"turn\":{\"a\":1,\"b\":{\"c\":2}},\"list\":[1]}"), target);
  }

  @Test
  void rejectsAConfigThatIsNotAnObject() {
    assertThrows(IllegalArgumentException.class, () -> Agent.fromJson("[]"));
    assertNull(Wire.getPath(json("{\"a\":1}"), Wire.path("a", "b")));
  }

  private static List<String> fieldNames(JsonNode node) {
    List<String> names = new ArrayList<>();
    node.fieldNames().forEachRemaining(names::add);
    return names;
  }
}
