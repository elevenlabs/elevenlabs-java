package com.elevenlabs.api.agentbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elevenlabs.api.ElevenLabsClient;
import com.elevenlabs.api.agenttools.Schema;
import com.elevenlabs.api.agenttools.SystemTool;
import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.elevenlabs.api.agentworkflows.Workflow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AgentPushTest {

  private final FakeServer server = new FakeServer();
  private final ElevenLabsClient client = server.client();

  @AfterEach
  void stop() throws IOException {
    server.close();
  }

  static Tool getOrder() {
    return getOrder("Look up an order by its number");
  }

  static Tool getOrder(String description) {
    return Tool.webhook("get_order", description)
      .get("https://api.example.com/orders/{order_id}")
      .pathParam("order_id", Schema.string().describe("The order number"));
  }

  static Tool showOrder() {
    return Tool.client("show_order", "Show the order card").param("order_id", Schema.string().fromVariable("order_id"));
  }

  static Agent supportAgent() {
    return supportAgent(getOrder(), showOrder());
  }

  static Agent supportAgent(Tool... tools) {
    Workflow wf = new Workflow();
    wf.converse("triage", "Find out what the caller needs")
      .when("Caller gave an order number")
      .tool("lookup", tools[0])
      .onSuccess()
      .converse("resolve", "Explain the order");
    List<Tool> agentTools = new ArrayList<>();
    agentTools.add(SystemTool.endCall());
    agentTools.addAll(Arrays.asList(tools).subList(1, tools.length));
    return new Agent("Support line", new AgentOptions().prompt("You help with orders.").tools(agentTools).workflow(wf));
  }

  static Map<String, ToolPushResult> results(Object... pairs) {
    Map<String, ToolPushResult> map = new LinkedHashMap<>();
    for (int i = 0; i < pairs.length; i += 3) {
      map.put((String) pairs[i], new ToolPushResult((String) pairs[i + 1], (ToolPushAction) pairs[i + 2]));
    }
    return map;
  }

  static JsonNode json(String text) {
    return CanonicalJson.parse(text);
  }

  static String index(String text) {
    return ProjectFormat.formatIndex(json(text));
  }

  // ---- push, without a project --------------------------------------------------------------------------------

  @Test
  void createsTheToolsThenTheAgentWithTheirIds() {
    AgentPushResult result = client.agents().push(supportAgent());

    assertEquals(Arrays.asList("POST /v1/convai/tools", "POST /v1/convai/tools", "POST /v1/convai/agents/create"), server.writes());
    assertEquals("agent_new_3", result.getAgentId());
    assertEquals(AgentPushAction.CREATED, result.getAction());
    assertEquals(
      results("show_order", "tool_new_1", ToolPushAction.CREATED, "get_order", "tool_new_2", ToolPushAction.CREATED),
      result.getTools()
    );
    JsonNode sent = server.agents.get("agent_new_3");
    assertEquals(json("[\"tool_new_1\"]"), sent.at("/conversation_config/agent/prompt/tool_ids"));
    assertEquals("end_call", sent.at("/conversation_config/agent/prompt/built_in_tools/end_call/name").asText());
    assertEquals(json("[{\"tool_id\":\"tool_new_2\"}]"), sent.at("/workflow/nodes/lookup/tools"));
    // The tool bodies are the tools' wire configs.
    assertEquals(showOrder().toJson(), server.tools.get("tool_new_1").config);
  }

  @Test
  void updatesOnlyWhatChangedAndLeavesAnotherAgentsSameNamedToolAlone() {
    String agentId = client.agents().push(supportAgent()).getAgentId();
    server.addTool("tool_other", getOrder("Someone else's").toJson(), "agent_other");
    server.calls.clear();

    AgentPushResult again = client.agents().push(supportAgent(getOrder("Find an order"), showOrder()), new AgentPushOptions().agentId(agentId));

    assertEquals(Arrays.asList("PATCH /v1/convai/tools/tool_new_2", "PATCH /v1/convai/agents/" + agentId), server.writes());
    Map<String, String> query = new LinkedHashMap<>();
    query.put("used_by_agent_id", agentId);
    query.put("page_size", "100");
    assertEquals(query, server.calls.get(0).query);
    assertEquals(
      results("show_order", "tool_new_1", ToolPushAction.UNCHANGED, "get_order", "tool_new_2", ToolPushAction.UPDATED),
      again.getTools()
    );
    assertEquals(AgentPushAction.UPDATED, again.getAction());
    assertEquals("Someone else's", server.tools.get("tool_other").config.get("description").asText());
  }

  @Test
  void reportsToolsTheAgentStoppedUsingAsDetachedWithoutDeletingThem() {
    String agentId = client.agents().push(supportAgent()).getAgentId();
    Agent agent = supportAgent();
    agent.update(new AgentOptions().tools(Collections.<Tool>emptyList()));
    AgentPushResult result = client.agents().push(agent, new AgentPushOptions().agentId(agentId));
    assertEquals(new ToolPushResult("tool_new_1", ToolPushAction.DETACHED), result.getTools().get("show_order"));
    assertTrue(server.tools.containsKey("tool_new_1"));
    assertFalse(server.writes().contains("DELETE /v1/convai/tools/tool_new_1"));
  }

  @Test
  void matchesABranchPushAgainstTheBranchsToolsNotTheMainBranchs() {
    String agentId = client.agents().push(supportAgent()).getAgentId();
    // The branch uses its own copy of get_order, and no show_order.
    server.addTool("tool_branch_order", getOrder("Branch lookup").toJson());
    ObjectNode branch = server.agents.get(agentId).deepCopy();
    ((ObjectNode) branch.at("/workflow/nodes/lookup")).set("tools", json("[{\"tool_id\":\"tool_branch_order\"}]"));
    ((ObjectNode) branch.at("/conversation_config/agent/prompt")).putArray("tool_ids");
    server.branches.put(agentId + "/agtbrch_1", branch);
    server.calls.clear();

    AgentPushResult result = client.agents().push(
      supportAgent(getOrder("Changed lookup")),
      new AgentPushOptions().agentId(agentId).branchId("agtbrch_1")
    );
    assertEquals(new ToolPushResult("tool_branch_order", ToolPushAction.UPDATED), result.getTools().get("get_order"));
    // A main-branch tool the branch never used isn't "detached".
    assertFalse(result.getTools().containsKey("show_order"));
    assertEquals(Arrays.asList("PATCH /v1/convai/tools/tool_branch_order", "PATCH /v1/convai/agents/" + agentId), server.writes());
    for (FakeServer.Call call : server.calls) {
      assertFalse(call.query.containsKey("used_by_agent_id"));
    }
    assertEquals("Changed lookup", server.tools.get("tool_branch_order").config.get("description").asText());
    assertEquals("Look up an order by its number", server.tools.get("tool_new_2").config.get("description").asText());
  }

  @Test
  void sendsVersionDescriptionAndBranchOnlyWhenGiven() {
    String agentId = client.agents().push(supportAgent()).getAgentId();
    server.calls.clear();
    client.agents().push(supportAgent(), new AgentPushOptions().agentId(agentId));
    FakeServer.Call patch = firstPatchOfAgent();
    assertFalse(patch.body.has("version_description"));
    assertEquals(Collections.emptyMap(), patch.query);

    server.calls.clear();
    server.branches.put(agentId + "/agtbrch_1", server.agents.get(agentId).deepCopy());
    client.agents().push(supportAgent(), new AgentPushOptions().agentId(agentId).branchId("agtbrch_1").versionDescription("v2"));
    patch = firstPatchOfAgent();
    assertEquals("v2", patch.body.get("version_description").asText());
    assertEquals(Collections.singletonMap("branch_id", "agtbrch_1"), patch.query);
  }

  private FakeServer.Call firstPatchOfAgent() {
    for (FakeServer.Call call : server.calls) {
      if ("PATCH".equals(call.method) && call.path.startsWith("/v1/convai/agents/")) {
        return call;
      }
    }
    throw new AssertionError("no agent PATCH");
  }

  @Test
  void validatesBeforeAnyApiCall() {
    Agent agent = new Agent("A", new AgentOptions().prompt("p").tools(Tool.webhook("broken", "No URL")));
    assertThrows(AgentValidationException.class, () -> client.agents().push(agent));
    assertTrue(server.calls.isEmpty());
  }

  @Test
  void reportsWhatItWroteBeforeAFailure() {
    server.failOn = Pattern.compile("^POST /v1/convai/agents/create$");
    AgentPushException e = assertThrows(AgentPushException.class, () -> client.agents().push(supportAgent()));
    assertTrue(e.getMessage().startsWith("Couldn't create agent \"Support line\""), e.getMessage());
    assertNull(e.getResult().getAgentId());
    assertNull(e.getResult().getAction());
    assertEquals(
      results("show_order", "tool_new_1", ToolPushAction.CREATED, "get_order", "tool_new_2", ToolPushAction.CREATED),
      e.getResult().getTools()
    );
    assertTrue(e.getCause() instanceof RuntimeException);
  }

  @Test
  void namesTheToolStepThatFailed() {
    server.failOn = Pattern.compile("^POST /v1/convai/tools$");
    AgentPushException e = assertThrows(AgentPushException.class, () -> client.agents().push(supportAgent()));
    assertTrue(e.getMessage().startsWith("Couldn't create tool \"show_order\""), e.getMessage());
    assertTrue(e.getResult().getTools().isEmpty());
  }

  @Test
  void refusesAgentIdTogetherWithAProject() {
    IllegalArgumentException e = assertThrows(
      IllegalArgumentException.class,
      () -> client.agents().push(supportAgent(), new AgentPushOptions().agentId("agent_1").project(AgentProject.fromFiles(null)))
    );
    assertTrue(e.getMessage().contains("not both"));
  }

  @Test
  void doesNotReportAToolAsDetachedWhileTheWorkflowStillUsesItsId() {
    server.addTool("tool_raw", showOrder().toJson());
    Workflow wf = new Workflow();
    wf.tool("run", Collections.singletonList("tool_raw"));
    Agent agent = new Agent("A", new AgentOptions().prompt("p").workflow(wf));
    String agentId = client.agents().push(agent).getAgentId();
    AgentPushResult again = client.agents().push(agent, new AgentPushOptions().agentId(agentId));
    assertTrue(again.getTools().isEmpty(), again.toString());
  }

  // ---- push, with a project -----------------------------------------------------------------------------------

  @Test
  void recordsIdsAndWritesTheCliLayoutThenUpdatesByRecordedId() {
    AgentProject project = AgentProject.fromFiles(null);
    client.agents().push(supportAgent(), new AgentPushOptions().project(project));
    Map<String, String> files = project.toFiles();
    assertEquals(
      Arrays.asList("agent_configs/Support-line.json", "agents.json", "tool_configs/get_order.json", "tool_configs/show_order.json", "tools.json"),
      sorted(files.keySet())
    );
    assertEquals(
      "{\n    \"agents\": [\n        {\n            \"config\": \"agent_configs/Support-line.json\",\n            \"id\": \"agent_new_3\"\n        }\n    ]\n}",
      files.get("agents.json")
    );
    assertEquals(
      json(
        "{\"tools\":[{\"type\":\"client\",\"config\":\"tool_configs/show_order.json\",\"id\":\"tool_new_1\"}," +
        "{\"type\":\"webhook\",\"config\":\"tool_configs/get_order.json\",\"id\":\"tool_new_2\"}]}"
      ),
      json(files.get("tools.json"))
    );
    assertEquals(
      json("[\"tool_new_1\"]"),
      json(files.get("agent_configs/Support-line.json")).at("/conversation_config/agent/prompt/tool_ids")
    );

    // A fresh run: new objects, matched to the project's entries by name.
    server.calls.clear();
    AgentProject reloaded = AgentProject.fromFiles(files);
    AgentPushResult result = client.agents().push(supportAgent(), new AgentPushOptions().project(reloaded));
    assertEquals(Collections.singletonList("PATCH /v1/convai/agents/agent_new_3"), server.writes());
    assertEquals(ToolPushAction.UNCHANGED, result.getTools().get("get_order").getAction());
    // Only the version the update reported is new, recorded like the CLI does.
    assertEquals(Collections.singletonList("agents.json"), reloaded.getChangedFiles());
    assertEquals("ver_1", json(reloaded.toFiles().get("agents.json")).at("/agents/0/version_id").asText());
  }

  @Test
  void pushesALoadedAgentsSettingsTheSdkDoesNotModel() {
    server.addTool("tool_lookup", json("{\"type\":\"client\",\"name\":\"lookup\",\"description\":\"old\"}"));
    Map<String, String> files = new LinkedHashMap<>();
    files.put("agents.json", "{\"agents\":[{\"config\":\"agent_configs/a.json\",\"id\":\"agent_1\"}]}");
    files.put("tools.json", "{\"tools\":[{\"type\":\"client\",\"config\":\"tool_configs/t.json\",\"id\":\"tool_lookup\"}]}");
    files.put(
      "agent_configs/a.json",
      "{\"name\":\"A\",\"conversation_config\":{\"agent\":{\"prompt\":{\"prompt\":\"p\",\"tool_ids\":[\"tool_lookup\"]}}," +
      "\"future_setting\":{\"on\":true}},\"platform_settings\":{},\"tags\":[]}"
    );
    files.put("tool_configs/t.json", "{\"type\":\"client\",\"name\":\"lookup\",\"description\":\"d\",\"future_field\":1}");
    AgentProject project = AgentProject.fromFiles(files);
    server.addAgent("agent_1", (ObjectNode) json("{}"));

    Agent agent = project.agent("A");
    agent.update(new AgentOptions().firstMessage("Hello"));
    client.agents().push(agent, new AgentPushOptions().project(project));

    FakeServer.Call toolPatch = null;
    FakeServer.Call agentPatch = null;
    for (FakeServer.Call call : server.calls) {
      if ("PATCH".equals(call.method) && call.path.contains("/tools/")) {
        toolPatch = call;
      } else if ("PATCH".equals(call.method) && call.path.contains("/agents/")) {
        agentPatch = call;
      }
    }
    assertEquals(1, toolPatch.body.at("/tool_config/future_field").asInt());
    assertEquals(json("{\"on\":true}"), agentPatch.body.at("/conversation_config/future_setting"));
    assertEquals("Hello", agentPatch.body.at("/conversation_config/agent/first_message").asText());
    assertEquals(
      Arrays.asList("agent_configs/a.json", "agents.json", "tool_configs/t.json", "tools.json"),
      sorted(project.getChangedFiles())
    );
  }

  @Test
  void updatesAProjectToolWhoseOnlyChangeIsAFieldTheSdkDoesNotModel() {
    server.addTool("tool_1", json("{\"type\":\"client\",\"name\":\"ping\",\"description\":\"d\",\"future_field\":1}"), "agent_1");
    server.addAgent("agent_1", (ObjectNode) json("{}"));
    Map<String, String> files = new LinkedHashMap<>();
    files.put("agents.json", "{\"agents\":[{\"config\":\"agent_configs/a.json\",\"id\":\"agent_1\"}]}");
    files.put("tools.json", "{\"tools\":[{\"type\":\"client\",\"config\":\"tool_configs/t.json\",\"id\":\"tool_1\"}]}");
    files.put("agent_configs/a.json", "{\"name\":\"A\",\"conversation_config\":{\"agent\":{\"prompt\":{\"prompt\":\"p\",\"tool_ids\":[\"tool_1\"]}}}}");
    files.put("tool_configs/t.json", "{\"type\":\"client\",\"name\":\"ping\",\"description\":\"d\",\"future_field\":2}");
    AgentProject project = AgentProject.fromFiles(files);
    AgentPushResult result = client.agents().push(project.agent("A"), new AgentPushOptions().project(project));
    assertEquals(ToolPushAction.UPDATED, result.getTools().get("ping").getAction());
    assertEquals(2, server.tools.get("tool_1").config.get("future_field").asInt());
  }

  @Test
  void bringsTheProjectUpToDateWhenTheServerAlreadyMatchesANewDefinition() {
    AgentProject project = AgentProject.fromFiles(null);
    client.agents().push(supportAgent(getOrder("v2"), showOrder()), new AgentPushOptions().project(project));
    Map<String, String> files = new LinkedHashMap<>(project.toFiles());
    files.put("tool_configs/get_order.json", ProjectFormat.formatConfig(getOrder("v1").toJson()));
    AgentProject stale = AgentProject.fromFiles(files);
    AgentPushResult result = client.agents().push(supportAgent(getOrder("v2"), showOrder()), new AgentPushOptions().project(stale));
    assertEquals(ToolPushAction.UNCHANGED, result.getTools().get("get_order").getAction());
    assertEquals("v2", json(stale.toFiles().get("tool_configs/get_order.json")).get("description").asText());
  }

  @Test
  void keepsAProjectToolsUnmodeledFieldsWhenTheSameToolIsRebuiltInCode() {
    AgentProject project = AgentProject.fromFiles(null);
    client.agents().push(supportAgent(), new AgentPushOptions().project(project));
    Map<String, String> files = new LinkedHashMap<>(project.toFiles());
    ObjectNode withExtra = (ObjectNode) json(files.get("tool_configs/get_order.json"));
    withExtra.put("future_field", 1);
    files.put("tool_configs/get_order.json", ProjectFormat.formatConfig(withExtra));
    server.tools.get("tool_new_2").config = withExtra;

    AgentProject reloaded = AgentProject.fromFiles(files);
    AgentPushResult result = client.agents().push(supportAgent(), new AgentPushOptions().project(reloaded));
    assertEquals(ToolPushAction.UNCHANGED, result.getTools().get("get_order").getAction());
    assertEquals(1, json(reloaded.toFiles().get("tool_configs/get_order.json")).get("future_field").asInt());
  }

  @Test
  void matchesToolsByTheProjectsRecordedIdNotByNameOnTheServer() {
    // The project says get_order is tool_mine; a same-named tool the agent doesn't use is never touched.
    server.addTool("tool_mine", getOrder("Old").toJson());
    server.addTool("tool_theirs", getOrder("Theirs").toJson(), "agent_other");
    Map<String, String> files = new LinkedHashMap<>();
    files.put("tools.json", "{\"tools\":[{\"type\":\"webhook\",\"config\":\"tool_configs/get_order.json\",\"id\":\"tool_mine\"}]}");
    files.put("tool_configs/get_order.json", ProjectFormat.formatConfig(getOrder("Old").toJson()));
    AgentProject project = AgentProject.fromFiles(files);
    Agent agent = new Agent("A", new AgentOptions().prompt("p").tools(getOrder("New")));
    AgentPushResult result = client.agents().push(agent, new AgentPushOptions().project(project));
    assertEquals(new ToolPushResult("tool_mine", ToolPushAction.UPDATED), result.getTools().get("get_order"));
    assertEquals("Theirs", server.tools.get("tool_theirs").config.get("description").asText());
    assertEquals("New", server.tools.get("tool_mine").config.get("description").asText());
  }

  @Test
  void rejectsTwoProjectEntriesWithTheToolsName() {
    Map<String, String> files = new LinkedHashMap<>();
    files.put(
      "tools.json",
      "{\"tools\":[{\"type\":\"client\",\"config\":\"tool_configs/a.json\",\"id\":\"t1\"},{\"type\":\"client\",\"config\":\"tool_configs/b.json\",\"id\":\"t2\"}]}"
    );
    files.put("tool_configs/a.json", "{\"type\":\"client\",\"name\":\"ping\",\"description\":\"d\"}");
    files.put("tool_configs/b.json", "{\"type\":\"client\",\"name\":\"ping\",\"description\":\"d\"}");
    AgentProject project = AgentProject.fromFiles(files);
    Agent agent = new Agent("A", new AgentOptions().prompt("p").tools(Tool.client("ping", "d")));
    AgentPushException e = assertThrows(AgentPushException.class, () -> client.agents().push(agent, new AgentPushOptions().project(project)));
    assertTrue(e.getCause() instanceof AgentProjectException, String.valueOf(e.getCause()));
    assertTrue(e.getMessage().contains("are named \"ping\""), e.getMessage());
  }

  @Test
  void savesWhatItWroteToAProjectOnDiskWhenARequestFailsPartway(@TempDir Path dir) throws IOException {
    AgentProject project = AgentProject.open(dir);
    server.failOn = Pattern.compile("^POST /v1/convai/agents/create$");
    AgentPushException e = assertThrows(AgentPushException.class, () -> client.agents().push(supportAgent(), new AgentPushOptions().project(project)));
    assertEquals(2, e.getResult().getTools().size());
    // The tools it created are recorded, so the next push updates them instead of making copies.
    JsonNode tools = json(TestFiles.read(dir.resolve("tools.json")));
    assertEquals("tool_new_1", tools.at("/tools/0/id").asText());
    assertEquals("tool_new_2", tools.at("/tools/1/id").asText());
    assertFalse(Files.exists(dir.resolve("agents.json")) && json(TestFiles.read(dir.resolve("agents.json"))).at("/agents/0/id").isTextual());

    server.failOn = null;
    server.calls.clear();
    AgentProject reopened = AgentProject.open(dir);
    AgentPushResult result = client.agents().push(supportAgent(), new AgentPushOptions().project(reopened));
    assertEquals(Collections.singletonList("POST /v1/convai/agents/create"), server.writes());
    assertEquals(ToolPushAction.UNCHANGED, result.getTools().get("get_order").getAction());
  }

  // ---- pull ---------------------------------------------------------------------------------------------------

  @Test
  void pullLoadsAnAgentWithItsToolsAsToolObjectsAndWritesThemIntoAProject() {
    String agentId = client.agents().push(supportAgent()).getAgentId();

    AgentProject project = AgentProject.fromFiles(null);
    Agent agent = client.agents().pull(agentId, new AgentPullOptions().project(project));
    assertEquals("Support line", agent.getName());
    List<String> names = new ArrayList<>();
    for (Tool tool : agent.tools()) {
      names.add(tool.getName());
    }
    assertEquals(Arrays.asList("show_order", "get_order"), names);
    assertEquals(agentId, project.agentId(agent).get());

    Map<String, String> files = project.toFiles();
    assertEquals(
      Arrays.asList("agent_configs/Support-line.json", "agents.json", "tool_configs/get_order.json", "tool_configs/show_order.json", "tools.json"),
      sorted(files.keySet())
    );
    List<String> keys = new ArrayList<>();
    json(files.get("agent_configs/Support-line.json")).fieldNames().forEachRemaining(keys::add);
    assertEquals(Arrays.asList("conversation_config", "name", "platform_settings", "tags", "workflow"), keys);

    // Pushing the pulled agent back changes no tools.
    server.calls.clear();
    AgentPushResult result = client.agents().push(agent, new AgentPushOptions().project(project));
    assertEquals(Collections.singletonList("PATCH /v1/convai/agents/" + agentId), server.writes());
    List<ToolPushAction> actions = new ArrayList<>();
    for (ToolPushResult tool : result.getTools().values()) {
      actions.add(tool.getAction());
    }
    assertEquals(Arrays.asList(ToolPushAction.UNCHANGED, ToolPushAction.UNCHANGED), actions);
  }

  @Test
  void pullWithoutAProjectKeepsOtherToolKindsAttachedById() {
    server.addTool("tool_mcp", json("{\"type\":\"mcp\",\"name\":\"search\",\"description\":\"d\",\"mcp_server_id\":\"m1\"}"));
    server.addAgent(
      "agent_1",
      (ObjectNode) json("{\"name\":\"A\",\"conversation_config\":{\"agent\":{\"prompt\":{\"prompt\":\"p\",\"tool_ids\":[\"tool_mcp\"]}}}}")
    );
    Agent agent = client.agents().pull("agent_1");
    assertTrue(agent.tools().isEmpty());
    assertEquals(Collections.singletonList("tool_mcp"), agent.getToolIds());
  }

  @Test
  void pushLeavesAnOtherKindOfToolAttachedByIdWithoutCreatingOrUpdatingIt() {
    JsonNode mcpConfig = json("{\"type\":\"mcp\",\"name\":\"docs_search\",\"description\":\"Search the docs\",\"mcp_server_id\":\"mcp_1\"}");
    server.addTool("tool_mcp", mcpConfig, "agent_1");
    ObjectNode config = (ObjectNode) json(
      "{\"name\":\"A\",\"conversation_config\":{\"agent\":{\"prompt\":{\"prompt\":\"p\",\"tool_ids\":[\"tool_mcp\"]}}}}"
    );
    server.addAgent("agent_1", config);
    Agent agent = Agent.fromJson(config, Collections.singletonMap("tool_mcp", Tool.fromJson(mcpConfig)));
    agent.update(new AgentOptions().firstMessage("Hi"));

    AgentPushResult result = client.agents().push(agent, new AgentPushOptions().agentId("agent_1"));

    assertEquals(Collections.singletonList("PATCH /v1/convai/agents/agent_1"), server.writes());
    assertTrue(result.getTools().isEmpty(), result.getTools().toString());
    assertEquals(json("[\"tool_mcp\"]"), server.agents.get("agent_1").at("/conversation_config/agent/prompt/tool_ids"));
    assertEquals(mcpConfig, server.tools.get("tool_mcp").config);
  }

  @Test
  void pullWritesTheServersWorkflowAsItIsWithANodeTypeTheSdkDoesNotKnow() {
    String workflow =
      "{\"edges\":{\"e1\":{\"forward_condition\":{\"type\":\"unconditional\"},\"reroutes\":[],\"source\":\"start_node\",\"target\":\"survey\"}}," +
      "\"nodes\":{\"start_node\":{\"edge_order\":[\"e1\"],\"type\":\"start\"}," +
      "\"survey\":{\"edge_order\":[],\"label\":\"Survey\",\"questions\":[{\"text\":\"How was it?\"}],\"type\":\"future_survey\"}}," +
      "\"prevent_subagent_loops\":false}";
    String config =
      "{\"conversation_config\":{\"agent\":{\"prompt\":{\"prompt\":\"p\"}}},\"name\":\"Survey line\",\"platform_settings\":{}," +
      "\"tags\":[],\"workflow\":" + workflow + "}";
    server.addAgent("agent_wf", (ObjectNode) json(config));

    AgentProject project = AgentProject.fromFiles(null);
    client.agents().pull("agent_wf", new AgentPullOptions().project(project));

    assertEquals(ProjectFormat.formatConfig(json(config)), project.toFiles().get("agent_configs/Survey-line.json"));
  }

  private static List<String> sorted(java.util.Collection<String> values) {
    List<String> list = new ArrayList<>(values);
    Collections.sort(list);
    return list;
  }
}
