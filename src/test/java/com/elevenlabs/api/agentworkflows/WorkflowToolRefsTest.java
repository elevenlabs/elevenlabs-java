package com.elevenlabs.api.agentworkflows;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.elevenlabs.api.agenttools.Schema;
import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.types.AgentWorkflowRequestModel;
import com.elevenlabs.api.types.WorkflowToolLocatorSchemaOverridesValue;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Tool nodes and subagents that take {@link Tool} objects, which stand under placeholders until build. */
class WorkflowToolRefsTest {

  private static Tool getOrder() {
    return Tool.webhook("get_order", "Look up an order")
      .get("https://api.example.com/orders/{order_id}")
      .pathParam("order_id", Schema.string().describe("The order number"));
  }

  private static Map<String, String> ids(String name, String id) {
    return Collections.singletonMap(name, id);
  }

  private static List<String> codes(Runnable run) {
    WorkflowValidationException e = assertThrows(WorkflowValidationException.class, run::run);
    List<String> codes = new ArrayList<>();
    for (WorkflowIssue issue : e.getIssues()) {
      codes.add(issue.getCode());
    }
    return codes;
  }

  @Test
  void swapsInEachToolsIdOnBuild() {
    Workflow wf = new Workflow();
    wf.tool("lookup", getOrder()).onSuccess().end();
    JsonNode json = wf.toJson(ids("get_order", "tool_1"));
    assertEquals(CanonicalJson.parse("[{\"tool_id\":\"tool_1\"}]"), json.at("/nodes/lookup/tools"));
    assertEquals(json, CanonicalJson.parse(wf.toJsonString(ids("get_order", "tool_1"))));
    AgentWorkflowRequestModel model = wf.build(ids("get_order", "tool_1"));
    assertEquals(json.at("/nodes/lookup/tools"), CanonicalJson.toTree(model).at("/nodes/lookup/tools"));
  }

  @Test
  void reportsAToolObjectWithoutAnId() {
    Workflow wf = new Workflow();
    wf.tool("lookup", getOrder()).onSuccess().end();
    assertEquals(Collections.singletonList("unresolved_tool"), codes(wf::build));
    assertEquals(Collections.singletonList("unresolved_tool"), codes(wf::toJson));
    assertEquals(Collections.singletonList("unresolved_tool"), codes(() -> wf.toJsonString(ids("other", "tool_1"))));
  }

  @Test
  void listsTheToolObjectsTheNodesUseInFirstUseOrder() {
    Tool lookup = getOrder();
    Tool show = Tool.client("show_order", "Show the order");
    Workflow wf = new Workflow();
    wf.converse("triage", "Triage", new ConverseOptions().tools(show)).when("Has a number").tool("lookup", lookup, show);
    List<Tool> tools = wf.tools();
    assertEquals(2, tools.size());
    assertSame(show, tools.get(0));
    assertSame(lookup, tools.get(1));
    // Still in the order each was first referenced.
    wf.remove("triage");
    assertEquals(Arrays.asList(show, lookup), wf.tools());
    wf.node("lookup").update(new NodeUpdate().toolIds(Collections.singletonList("tool_raw")));
    assertEquals(Collections.<Tool>emptyList(), wf.tools());
  }

  @Test
  void letsARemovedNodesToolNameBeReusedByANewToolObject() {
    Workflow wf = new Workflow();
    wf.tool("a", getOrder());
    wf.remove("a");
    wf.tool("b", getOrder());
    assertDoesNotThrow(() -> wf.build(ids("get_order", "tool_1")));
  }

  @Test
  void reportsTwoDifferentToolsWithOneName() {
    Workflow wf = new Workflow();
    wf.tool("lookup", getOrder(), getOrder());
    assertEquals(Collections.singletonList("duplicate_tool_name"), codes(() -> wf.build(ids("get_order", "tool_1"))));

    Workflow across = new Workflow();
    across.tool("a", getOrder());
    across.tool("b", getOrder());
    assertEquals(Collections.singletonList("duplicate_tool_name"), codes(() -> across.build(ids("get_order", "tool_1"))));

    Tool lookup = getOrder();
    Workflow same = new Workflow();
    same.tool("lookup", lookup, lookup);
    assertDoesNotThrow(() -> same.build(ids("get_order", "tool_1")));
  }

  @Test
  void keysSchemaOverridesForAToolObjectByItsName() {
    Map<String, Map<String, WorkflowToolLocatorSchemaOverridesValue>> overrides = new LinkedHashMap<>();
    overrides.put(
      "get_order",
      Collections.singletonMap(
        "path_params.order_id",
        ObjectMappers.JSON_MAPPER.convertValue(
          CanonicalJson.parse("{\"source\":\"constant\",\"constant_value\":\"1\"}"),
          WorkflowToolLocatorSchemaOverridesValue.class
        )
      )
    );
    Workflow wf = new Workflow();
    wf.tool("lookup", new ToolOptions().tools(getOrder()).schemaOverrides(overrides));
    assertEquals(
      CanonicalJson.parse("{\"path_params.order_id\":{\"constant_value\":\"1\",\"source\":\"constant\"}}"),
      wf.toJson(ids("get_order", "tool_1")).at("/nodes/lookup/tools/0/schema_overrides")
    );

    Workflow typo = new Workflow();
    typo.tool("lookup", new ToolOptions().tools(getOrder()).schemaOverrides(Collections.singletonMap("get_ordr", overrides.get("get_order"))));
    assertEquals(Collections.singletonList("invalid_option"), codes(() -> typo.build(ids("get_order", "tool_1"))));
  }

  @Test
  void loadsAWorkflowWithToolIdsBoundToToolObjects() {
    Tool lookup = getOrder();
    String raw = "{\"nodes\":{\"start_node\":{\"type\":\"start\"},\"lookup\":{\"type\":\"tool\",\"tools\":[{\"tool_id\":\"tool_get\"},{\"tool_id\":\"tool_other\"}]}," +
      "\"sub\":{\"type\":\"override_agent\",\"label\":\"Sub\",\"additional_tool_ids\":[\"tool_get\"]}}," +
      "\"edges\":{\"e1\":{\"source\":\"start_node\",\"target\":\"sub\",\"forward_condition\":{\"type\":\"unconditional\"}}," +
      "\"e2\":{\"source\":\"sub\",\"target\":\"lookup\",\"forward_condition\":{\"type\":\"llm\",\"condition\":\"Go\"}}}}";
    Workflow wf = Workflow.fromJson(raw, Collections.singletonMap("tool_get", lookup));
    assertEquals(Collections.singletonList(lookup), wf.tools());
    JsonNode json = wf.toJson(ids("get_order", "tool_new"));
    assertEquals(CanonicalJson.parse("[{\"tool_id\":\"tool_new\"},{\"tool_id\":\"tool_other\"}]"), json.at("/nodes/lookup/tools"));
    assertEquals(CanonicalJson.parse("[\"tool_new\"]"), json.at("/nodes/sub/additional_tool_ids"));
    // Without the map, ids load as plain strings.
    assertEquals(Collections.<Tool>emptyList(), Workflow.fromJson(raw).tools());
  }
}
