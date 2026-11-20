package com.elevenlabs.api.agentworkflows;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.types.AgentWorkflowRequestModel;
import com.elevenlabs.api.types.AgentWorkflowRequestModelNodesValue;
import com.elevenlabs.api.types.PositionInput;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Coverage for the public builder surface beyond the shared fixtures: {@code up()} chaining, {@code build()}
 * returning the generated request model, loading from models, sugar, and the backward-condition rules.
 */
class WorkflowBuilderTest {

  private static List<String> errorCodes(Workflow wf) {
    WorkflowValidationException e = assertThrows(WorkflowValidationException.class, wf::build);
    List<String> codes = new ArrayList<>();
    for (WorkflowIssue issue : e.getIssues()) {
      codes.add(issue.getCode());
    }
    return codes;
  }

  @Test
  void buildReturnsGeneratedRequestModel() {
    Workflow wf = new Workflow();
    wf.converse("support_agent", "Help the user").when("Resolved").end();

    AgentWorkflowRequestModel model = wf.build();

    Map<String, AgentWorkflowRequestModelNodesValue> nodes = model.getNodes().get();
    assertTrue(nodes.get("support_agent").isOverrideAgent());
    assertTrue(nodes.get("end").isEnd());
    assertTrue(model.getEdges().get().containsKey("start_node__support_agent"));
  }

  @Test
  void chainsWithUp() {
    Workflow wf = new Workflow();
    wf
      .converse("triage", "Find out what the caller needs")
      .when("Caller gave an order number")
      .tool("lookup_order", Arrays.asList("tool_get_order"))
      .onResult(
        new ResultBranches()
          .success(ok -> ok.converse("resolve", "Explain the order status").end())
          .failure(failed -> failed.goTo("triage"))
      )
      .up()
      .when("Caller wants a human")
      .transfer("human", Destination.phone("+15551234567"));

    assertEquals(Arrays.asList("start_node", "triage", "lookup_order", "resolve", "end", "human"), wf.getNodeIds());
    JsonNode json = wf.toJson();
    assertTrue(json.get("edges").has("triage__human"));
    // lookup_order's failure goes back along triage -> lookup_order as its backward condition.
    assertEquals("result", json.get("edges").get("triage__lookup_order").get("backward_condition").get("type").asText());
    assertTrue(wf.toJsonString().contains("\"+15551234567\""));
  }

  @Test
  void buildKeepsIntegerLiteralsBeyondTheIntRange() throws Exception {
    Workflow wf = new Workflow();
    wf.converse("triage", "Triage").when(Expr.variable("ts").gt(1_700_000_000_000L)).end();

    assertTrue(wf.toJsonString().contains("\"value\": 1700000000000\n"));
    // The generated number union narrows integers to int; build() must not wrap this one.
    JsonNode model = ObjectMappers.JSON_MAPPER.valueToTree(wf.build());
    assertTrue(CanonicalJson.toCanonicalString(model).contains("\"value\": 1700000000000\n"));
  }

  @Test
  void stringAndExprConditionsAreSugar() {
    Workflow wf = new Workflow();
    WorkflowNode triage = wf.converse("triage", "Triage");
    triage.when("plain string").end();
    triage.when(Expr.anyOf(Expr.variable("tier").eq("gold"), Expr.variable("orders").gte(10))).end(new EndOptions().id("vip_end"));

    String json = wf.toJsonString();
    assertTrue(json.contains("\"type\": \"llm\""));
    assertTrue(json.contains("\"type\": \"expression\""));
    assertTrue(json.contains("\"value\": 10\n"));
  }

  @Test
  void roundTripsThroughFromJson() {
    Workflow wf = new Workflow(new WorkflowOptions().preventSubagentLoops(true));
    wf.converse("a", "A", new ConverseOptions().position(PositionInput.builder().x(12.5).y(1e-7).build())).when("done").end();
    String once = wf.toJsonString();
    assertTrue(once.contains("\"x\": 12.5"));
    assertTrue(once.contains("\"y\": 1e-7"));
    assertEquals(once, Workflow.fromJson(once).toJsonString());
  }

  @Test
  void loadsFromGeneratedModels() throws Exception {
    Workflow wf = new Workflow();
    wf.converse("a", "A").when("done").end();
    String once = wf.toJsonString();

    AgentWorkflowRequestModel request = wf.build();
    assertEquals(once, Workflow.fromAgent(request).toJsonString());

    // A response carries the API's defaults (empty reroutes, entry settings), which are kept; ids, positions and
    // edge order come back unchanged.
    com.elevenlabs.api.types.AgentWorkflowResponseModel response = ObjectMappers.JSON_MAPPER.readValue(
      once,
      com.elevenlabs.api.types.AgentWorkflowResponseModel.class
    );
    Workflow loaded = Workflow.fromAgent(response);
    // Nodes come back in document order, which in the canonical file is sorted.
    assertEquals(Arrays.asList("a", "end", "start_node"), loaded.getNodeIds());
    JsonNode json = loaded.toJson();
    assertFalse(json.toString().contains("null"), "unset model fields must not come back as nulls");
    assertEquals(300, json.get("nodes").get("a").get("position").get("x").intValue());
    assertEquals("[\"a__end\"]", json.get("nodes").get("a").get("edge_order").toString());
    assertDoesNotThrow(() -> loaded.build());
  }

  @Test
  void chainingNeverThrowsErrorsSurfaceAtBuild() {
    Workflow wf = new Workflow();
    wf.converse("a", "A");
    wf.node("does_not_exist").update(new NodeUpdate().label("x"));
    assertTrue(errorCodes(wf).contains("unknown_node"));
  }

  @Test
  void warningsGoToTheCallback() {
    Workflow wf = new Workflow();
    wf.converse("a", "A");
    wf.node("a").when("never").end();
    wf.remove("a");
    List<String> warnings = new ArrayList<>();
    wf.build(issue -> warnings.add(issue.getCode()));
    assertEquals(Arrays.asList("unreachable_node"), warnings);
  }

  @Test
  void backwardConditionIntoStartIsRejected() {
    Workflow wf = new Workflow();
    wf.when("Begin", Condition.llm("Restart")).converse("greet", "Hello");
    assertTrue(errorCodes(wf).contains("edge_into_start"));
  }

  @Test
  void backwardConditionOutOfToolNeedsResult() {
    Workflow wf = new Workflow();
    wf.converse("a", "A").when("Look up", Condition.llm("Back")).tool("lookup", Arrays.asList("tool_1"));
    assertTrue(errorCodes(wf).contains("tool_edge_needs_result"));
  }

  @Test
  void backwardLoopBetweenConverseNodesIsAllowed() {
    Workflow wf = new Workflow();
    wf.converse("a", "A").when("To B", Condition.llm("Back to A")).converse("b", "B").end();
    assertDoesNotThrow(() -> wf.build());
  }

  @Test
  void insertBetweenBidirectionalEdgeIsRejected() {
    Workflow wf = new Workflow();
    wf.converse("a", "A").when("to b", Condition.llm("back to a")).converse("b", "B");
    assertNull(wf.insertBetween("a", "b", br -> br.converse("mid", "M")));
    assertTrue(errorCodes(wf).contains("cannot_insert_bidirectional"));
  }

  @Test
  void positionAcceptedForNodeTypesWithoutAFieldMap() {
    Workflow wf = Workflow.fromJson(
      "{\"nodes\": {\"start_node\": {\"type\": \"start\"}, \"announce\": {\"type\": \"say\", " +
      "\"message\": {\"type\": \"literal\", \"text\": \"hello\"}}}, \"edges\": {\"e1\": {\"source\": \"start_node\", " +
      "\"target\": \"announce\", \"forward_condition\": {\"type\": \"unconditional\"}}}}"
    );
    wf.node("announce").update(new NodeUpdate().position(PositionInput.builder().x(10.0).y(20.0).build()));
    assertDoesNotThrow(() -> wf.build());
    assertEquals(10, wf.toJson().get("nodes").get("announce").get("position").get("x").intValue());
  }

  @Test
  void loadedBackwardOnlyEdgeHasNoForwardTransition() {
    // x -> start_node carrying only a backward condition is the transition start_node -> x. There is no forward
    // x -> start_node, so it isn't an edge into the start and x's edge_order doesn't list it.
    Workflow wf = Workflow.fromJson(
      "{\"nodes\": {\"start_node\": {\"type\": \"start\"}, " +
      "\"hub\": {\"type\": \"override_agent\", \"label\": \"hub\", \"additional_prompt\": \"H\"}, " +
      "\"x\": {\"type\": \"override_agent\", \"label\": \"x\", \"additional_prompt\": \"X\"}, " +
      "\"end\": {\"type\": \"end\"}}, " +
      "\"edges\": {" +
      "\"e1\": {\"source\": \"start_node\", \"target\": \"hub\", \"forward_condition\": {\"type\": \"llm\", \"condition\": \"go\"}}, " +
      "\"e2\": {\"source\": \"hub\", \"target\": \"x\", \"forward_condition\": {\"type\": \"llm\", \"condition\": \"go\"}}, " +
      "\"e3\": {\"source\": \"hub\", \"target\": \"end\", \"forward_condition\": {\"type\": \"llm\", \"condition\": \"go\"}}, " +
      "\"e4\": {\"source\": \"x\", \"target\": \"start_node\", \"backward_condition\": {\"type\": \"llm\", \"condition\": \"restart\"}}}}"
    );
    JsonNode nodes = wf.toJson().get("nodes");
    assertFalse(nodes.get("x").get("edge_order").toString().contains("e4"));
    assertTrue(nodes.get("start_node").get("edge_order").toString().contains("e4"));
  }

  @Test
  void unknownNodeTypeIsKeptByToJsonAndRejectedByBuild() {
    // The generated union would read "future_survey" as another member (override_agent), so build() can't return
    // it; toJson() keeps it as given.
    Workflow wf = Workflow.fromJson(
      "{\"nodes\": {\"start_node\": {\"type\": \"start\"}, \"survey\": {\"type\": \"future_survey\", \"label\": \"Survey\", " +
      "\"survey_id\": \"s\"}}, " +
      "\"edges\": {\"e1\": {\"source\": \"start_node\", \"target\": \"survey\", \"forward_condition\": {\"type\": \"unconditional\"}}}}"
    );
    List<String> warnings = new ArrayList<>();
    WorkflowValidationException e = assertThrows(
      WorkflowValidationException.class,
      () -> wf.build(issue -> warnings.add(issue.getCode()))
    );
    assertEquals(1, e.getIssues().size());
    assertEquals("invalid_model", e.getIssues().get(0).getCode());
    assertEquals("survey", e.getIssues().get(0).getNodeId().get());
    assertTrue(warnings.isEmpty());
    assertEquals("future_survey", wf.toJson().get("nodes").get("survey").get("type").asText());
    assertTrue(NodeTypes.modelKnows("override_agent"));
    assertTrue(NodeTypes.modelKnows("say"));
    assertFalse(NodeTypes.modelKnows("future_survey"));
  }

  @Test
  void updateChangesTransferKindAndResetsAgentEntryPoint() {
    Workflow wf = new Workflow();
    wf.converse("a", "A").when("sales").transfer("t", Destination.agent("agent_1", "node_9"));
    wf.node("t").update(new NodeUpdate().destination(Destination.agent("agent_2")).delayMs(100));
    JsonNode t = wf.toJson().get("nodes").get("t");
    assertEquals("agent_2", t.get("agent_id").asText());
    assertFalse(t.has("node_id"));
    assertEquals(100, t.get("delay_ms").intValue());

    wf.node("t").update(new NodeUpdate().destination(Destination.sip("sip:x@example.com")));
    t = wf.toJson().get("nodes").get("t");
    assertEquals("phone_number", t.get("type").asText());
    assertFalse(t.has("delay_ms"));
  }
}
