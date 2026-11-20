package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.types.AgentWorkflowRequestModel;
import com.elevenlabs.api.types.AgentWorkflowResponseModel;
import com.elevenlabs.api.types.GetAgentResponseModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An agent workflow, written as code. The workflow itself is the start node: calling a verb on it branches from
 * the start. Build it with the verbs and routing methods, then {@link #build()} it into the request model that
 * {@code agents().create()} / {@code agents().update()} accept, or {@link #toJsonString()} it to store as config.
 *
 * <pre>{@code
 * Workflow wf = new Workflow();
 * WorkflowNode lookup = wf.converse("triage", "Find out what the caller needs")
 *     .when("Caller gave an order number").tool("lookup_order", Arrays.asList("tool_get_order"));
 * lookup.onResult(new ResultBranches()
 *     .success(ok -> ok.converse("resolve", "Explain the order status").end())
 *     .failure(failed -> failed.transfer("human", Destination.phone("+15551234567"))));
 * AgentWorkflowRequestModel model = wf.build();
 * }</pre>
 *
 * <p>Chaining never throws: every problem is recorded and reported together by {@code build()}.
 */
public final class Workflow extends WorkflowChain {

  /** An empty workflow: just the start node, {@code "start_node"}. */
  public Workflow() {
    this(null);
  }

  /** An empty workflow with options. */
  public Workflow(WorkflowOptions options) {
    super(new WorkflowGraph());
    ObjectNode start = JsonNodeFactory.instance.objectNode();
    start.put("type", "start");
    graph.addNode(graph.startId, start);
    graph.preventSubagentLoops = options == null ? null : options.preventSubagentLoops;
  }

  /** Loads the workflow of an existing agent (the result of {@code agents().get()}) so it can be edited. */
  public static Workflow fromAgent(GetAgentResponseModel agent) {
    return fromWire(agent == null ? null : agent.getWorkflow().map(Workflow::modelTree).orElse(null));
  }

  /** Loads a workflow from a get-agent response's {@code workflow} field. */
  public static Workflow fromAgent(AgentWorkflowResponseModel workflow) {
    return fromWire(workflow == null ? null : modelTree(workflow));
  }

  /** Loads a workflow request model so it can be edited. */
  public static Workflow fromAgent(AgentWorkflowRequestModel workflow) {
    return fromWire(workflow == null ? null : modelTree(workflow));
  }

  /**
   * A generated model in wire format. A generated model writes a required field it doesn't have as {@code null}
   * (so {@code label} on every condition of a response), which only means "absent"; those are dropped.
   */
  private static JsonNode modelTree(Object model) {
    return withoutNulls(CanonicalJson.toTree(model));
  }

  private static JsonNode withoutNulls(JsonNode node) {
    if (node.isObject()) {
      ObjectNode object = (ObjectNode) node;
      List<String> keys = new ArrayList<>();
      object.fieldNames().forEachRemaining(keys::add);
      for (String key : keys) {
        if (object.get(key).isNull()) {
          object.remove(key);
        } else {
          withoutNulls(object.get(key));
        }
      }
    } else if (node.isArray()) {
      for (JsonNode item : node) {
        withoutNulls(item);
      }
    }
    return node;
  }

  /**
   * Loads a workflow stored as API-format JSON (snake_case), for example from {@link #toJsonString()} or a CLI
   * {@code agent_configs/*.json} file. The input is either a workflow or a whole agent config with a
   * {@code workflow} key. Numbers are read exactly and document order is kept.
   */
  public static Workflow fromJson(String json) {
    return fromJson(CanonicalJson.parse(json));
  }

  /** Loads a workflow from a parsed JSON tree (a workflow, or an agent config with a {@code workflow} key). */
  public static Workflow fromJson(JsonNode parsed) {
    return fromJson(parsed, null);
  }

  /**
   * {@link #fromJson(String)}, with each tool id found in {@code toolsById} swapped for that {@link Tool} object, so
   * the loaded workflow refers to it as a workflow built in code would. Other ids stay as they are.
   */
  public static Workflow fromJson(String json, Map<String, ? extends Tool> toolsById) {
    return fromJson(CanonicalJson.parse(json), toolsById);
  }

  /** {@link #fromJson(JsonNode)}, with tool ids found in {@code toolsById} swapped for those {@link Tool} objects. */
  public static Workflow fromJson(JsonNode parsed, Map<String, ? extends Tool> toolsById) {
    JsonNode raw = parsed != null && parsed.isObject() && parsed.has("workflow") ? parsed.get("workflow") : parsed;
    Workflow wf = fromWire(raw);
    if (toolsById != null && !toolsById.isEmpty()) {
      wf.bindTools(toolsById);
    }
    return wf;
  }

  private void bindTools(final Map<String, ? extends Tool> byId) {
    for (String id : graph.nodeOrder()) {
      graph.setNode(
        id,
        WorkflowGraph.mapNodeToolIds(graph.getNode(id), toolId -> {
          Tool tool = byId.get(toolId);
          if (tool == null) {
            return toolId;
          }
          String key = WorkflowGraph.toolPlaceholder(tool.getName());
          graph.toolRefs.put(key, tool);
          return key;
        })
      );
    }
  }

  /** The {@link Tool} objects the workflow's nodes use, each once, in the order they were first used. */
  public List<Tool> tools() {
    Set<String> used = new LinkedHashSet<>();
    for (String id : graph.nodeOrder()) {
      used.addAll(WorkflowGraph.nodeToolIds(graph.getNode(id)));
    }
    List<Tool> tools = new ArrayList<>();
    for (Map.Entry<String, Tool> entry : graph.toolRefs.entrySet()) {
      if (used.contains(entry.getKey())) {
        tools.add(entry.getValue());
      }
    }
    return tools;
  }

  private static Workflow fromWire(JsonNode raw) {
    Workflow wf = new Workflow();
    if (raw != null && raw.isObject()) {
      wf.graph.load(raw);
    }
    return wf;
  }

  /** The id of the start node: {@code "start_node"}, the id the API requires, or whatever a loaded workflow uses. */
  public String getStartId() {
    return graph.startId;
  }

  /** Ids of every node, in the order they were added. */
  public List<String> getNodeIds() {
    return graph.nodeOrder();
  }

  /** Opens a branch from the start node that is followed when {@code condition} holds. */
  public WorkflowBranch when(Condition condition) {
    return branch(graph, graph.startId, condition, null);
  }

  /** {@link #when(Condition)}, with a backward condition for the way back. */
  public WorkflowBranch when(Condition condition, Condition back) {
    return branch(graph, graph.startId, condition, back);
  }

  /** Opens a branch from the start node, followed when the LLM judges {@code condition} to be true. */
  public WorkflowBranch when(String condition) {
    return when(Condition.llm(condition), null);
  }

  /** {@link #when(String)}, with a backward condition for the way back. */
  public WorkflowBranch when(String condition, Condition back) {
    return when(Condition.llm(condition), back);
  }

  /** Opens a branch from the start node, followed when {@code condition} evaluates to true. */
  public WorkflowBranch when(Expr condition) {
    return when(Condition.expr(condition), null);
  }

  /** {@link #when(Expr)}, with a backward condition for the way back. */
  public WorkflowBranch when(Expr condition, Condition back) {
    return when(Condition.expr(condition), back);
  }

  /** A handle to an existing node, for editing it or going to it. An unknown id is reported by {@code build()}. */
  public WorkflowNode node(String id) {
    if (!graph.containsNode(id)) {
      graph.issue("unknown_node", "There is no node \"" + id + "\"", id);
    }
    return new WorkflowNode(graph, id, null);
  }

  /** Removes a node and every edge to or from it. */
  public Workflow remove(String id) {
    if (graph.startId.equals(id)) {
      graph.issue("cannot_remove_start", "The start node can't be removed", id);
      return this;
    }
    graph.removeNode(id);
    List<GraphEdge> kept = new ArrayList<>();
    for (GraphEdge edge : graph.edges) {
      if (!edge.source.equals(id) && !edge.target.equals(id)) {
        kept.add(edge);
      }
    }
    graph.edges = kept;
    return this;
  }

  /**
   * Splices new nodes into the edge from {@code sourceId} to {@code targetId}. {@code insert} receives a branch
   * from the source carrying that edge's forward condition and returns the last new node, which is then connected
   * to {@code targetId} ({@code onSuccess()} if it's a tool node). Returns that node, or {@code null} if there is no
   * such edge, the edge is bidirectional, or the callback returned nothing; {@code build()} reports why.
   */
  public WorkflowNode insertBetween(String sourceId, String targetId, Function<WorkflowBranch, WorkflowNode> insert) {
    int index = -1;
    for (int i = 0; i < graph.edges.size(); i++) {
      GraphEdge edge = graph.edges.get(i);
      if (edge.source.equals(sourceId) && edge.target.equals(targetId)) {
        index = i;
        break;
      }
    }
    if (index == -1) {
      graph.issue(
        "unknown_edge",
        "There is no edge from \"" + sourceId + "\" to \"" + targetId + "\" to insert into",
        sourceId
      );
      return null;
    }
    GraphEdge original = graph.edges.get(index);
    // Splitting a bidirectional edge can't preserve the target's reverse route, so reject it rather than silently
    // drop or misattach that route. The caller removes the backward condition first.
    if (original.backwardCondition != null) {
      graph.issue(
        "cannot_insert_bidirectional",
        "Edge \"" + sourceId + "\" → \"" + targetId + "\" has a backward condition; inserting into a " +
        "bidirectional edge would drop its reverse route. Remove the backward condition first",
        sourceId
      );
      return null;
    }
    graph.edges.remove(index);
    int before = graph.edges.size();
    ObjectNode forward = original.forwardCondition != null ? original.forwardCondition : Condition.always().toWire();
    WorkflowNode last = insert.apply(new WorkflowBranch(graph, new EdgeOrigin(sourceId, forward, null)));

    // Put the first new edge back where the original was, so the source's edge order is unchanged.
    int firstHop = -1;
    for (int i = before; i < graph.edges.size(); i++) {
      if (graph.edges.get(i).source.equals(sourceId)) {
        firstHop = i;
        break;
      }
    }
    if (firstHop != -1) {
      GraphEdge edge = graph.edges.remove(firstHop);
      graph.edges.add(index, edge);
      List<GraphEdge> loaded = graph.loadedOrder.get(sourceId);
      if (loaded != null) {
        for (int slot = 0; slot < loaded.size(); slot++) {
          if (loaded.get(slot) == original) {
            loaded.set(slot, edge);
            break;
          }
        }
      }
    }

    if (last == null) {
      graph.issue(
        "missing_insert_result",
        "insertBetween(\"" + sourceId + "\", \"" + targetId + "\") must return the last inserted node",
        sourceId
      );
      return null;
    }
    if ("tool".equals(last.getType())) {
      last.onSuccess().goTo(targetId);
    } else {
      last.goTo(targetId);
    }
    return last;
  }

  /**
   * Validates the workflow and returns it in the shape {@code agents().create()} and {@code agents().update()}
   * accept.
   *
   * @throws WorkflowValidationException listing every problem, if the workflow is invalid
   */
  public AgentWorkflowRequestModel build() {
    return build(null, null);
  }

  /**
   * {@link #build()}, calling {@code onWarning} once per warning (such as {@code unreachable_node}).
   *
   * <p>A node type this SDK version's generated request model doesn't have a member for can't be carried by the
   * model (the generated union would read it as a different node type), so {@code build()} reports it as an
   * {@code invalid_model} error. {@link #toJson()} keeps such nodes as given.
   *
   * @throws WorkflowValidationException listing every problem, if the workflow is invalid
   */
  public AgentWorkflowRequestModel build(Consumer<WorkflowIssue> onWarning) {
    return build(null, onWarning);
  }

  /**
   * {@link #build()} for a workflow whose nodes use {@link Tool} objects: {@code toolIds} maps each one's name to its
   * id. {@code agents().push()} passes these after creating the tools. A {@code Tool} object without an id is an
   * {@code unresolved_tool} error.
   *
   * @throws WorkflowValidationException listing every problem, if the workflow is invalid
   */
  public AgentWorkflowRequestModel build(Map<String, String> toolIds) {
    return build(toolIds, null);
  }

  /** {@link #build(Map)}, calling {@code onWarning} once per warning. */
  public AgentWorkflowRequestModel build(Map<String, String> toolIds, Consumer<WorkflowIssue> onWarning) {
    ObjectNode wire = toWireObject(onWarning, true, toolIds);
    widenNumberLiterals(wire);
    try {
      return ObjectMappers.JSON_MAPPER.treeToValue(wire, AgentWorkflowRequestModel.class);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("The workflow doesn't fit the generated request model: " + e.getOriginalMessage(), e);
    }
  }

  /**
   * The generated number-literal value holds an {@code int} or a {@code double}, and its deserializer
   * narrows any integer with {@code intValue()}, so a literal outside the {@code int} range (a
   * millisecond timestamp, say) would silently wrap. Such literals are passed as doubles instead,
   * which is how JavaScript sends every number: exact up to 2^53.
   */
  private static void widenNumberLiterals(JsonNode node) {
    if (node.isObject()) {
      JsonNode value = node.get("value");
      if ("number_literal".equals(node.path("type").asText(null))
          && value != null
          && value.isIntegralNumber()
          && !value.canConvertToInt()) {
        ((ObjectNode) node).put("value", value.doubleValue());
      }
    }
    if (node.isContainerNode()) {
      for (JsonNode child : node) {
        widenNumberLiterals(child);
      }
    }
  }

  /**
   * The workflow in API format (snake_case keys, sorted at every level), for storing as config. Validates like
   * {@link #build()}.
   */
  public ObjectNode toJson() {
    return toJson((Map<String, String>) null);
  }

  /** {@link #toJson()} with each {@link Tool} object's id, by tool name, as {@link #build(Map)} takes them. */
  public ObjectNode toJson(Map<String, String> toolIds) {
    return (ObjectNode) CanonicalJson.canonicalize(toWireObject(null, false, toolIds));
  }

  /** {@link #toJson()} as a string with 2-space indentation and a trailing newline: the canonical file format. */
  public String toJsonString() {
    return toJsonString((Map<String, String>) null);
  }

  /** {@link #toJsonString()} with each {@link Tool} object's id, by tool name. */
  public String toJsonString(Map<String, String> toolIds) {
    return CanonicalJson.toCanonicalString(toWireObject(null, false, toolIds));
  }

  @Override
  EdgeOrigin origin() {
    return new EdgeOrigin(graph.startId, Condition.always().toWire(), null);
  }

  /**
   * Validates, merges reverse edges, lays out positions and assembles the wire-format workflow object. With
   * {@code forModel}, the output is bound for the generated request model, so a node type it doesn't model is an
   * {@code invalid_model} error instead of an {@code unknown_node_type} warning.
   */
  ObjectNode toWireObject(Consumer<WorkflowIssue> onWarning, boolean forModel, Map<String, String> toolIdsOrNull) {
    final Map<String, String> toolIds = toolIdsOrNull == null ? Collections.<String, String>emptyMap() : toolIdsOrNull;
    List<WorkflowIssue> issues = new ArrayList<>();
    for (WorkflowIssue issue : Validate.run(graph)) {
      if (forModel && "unknown_node_type".equals(issue.getCode())) {
        String id = issue.getNodeId().orElse(null);
        issues.add(
          new WorkflowIssue(
            "invalid_model",
            WorkflowSeverity.ERROR,
            "Node \"" + id + "\" has type \"" + WorkflowGraph.typeOf(graph.getNode(id)) + "\", which this SDK " +
            "version's request model can't represent, so build() can't return it. Upgrade the SDK, or send toJson() " +
            "as the request's workflow through additional body properties.",
            id
          )
        );
      } else {
        issues.add(issue);
      }
    }
    issues.addAll(unresolvedTools(toolIds));
    List<WorkflowIssue> errors = new ArrayList<>();
    for (WorkflowIssue issue : issues) {
      if (issue.getSeverity() == WorkflowSeverity.ERROR) {
        errors.add(issue);
      }
    }
    if (!errors.isEmpty()) {
      throw new WorkflowValidationException(errors);
    }
    if (onWarning != null) {
      for (WorkflowIssue issue : issues) {
        if (issue.getSeverity() == WorkflowSeverity.WARNING) {
          onWarning.accept(issue);
        }
      }
    }

    // An edge that goes back the way another one came is sent as that edge's backward condition.
    Map<Integer, Integer> merges = Validate.reverseEdgeMerges(graph);
    List<GraphEdge> kept = new ArrayList<>();
    for (int i = 0; i < graph.edges.size(); i++) {
      if (!merges.containsKey(i)) {
        kept.add(graph.edges.get(i));
      }
    }
    List<String> ids = assignEdgeIds(kept);
    Map<GraphEdge, String> idOf = new IdentityHashMap<>();
    for (int k = 0; k < kept.size(); k++) {
      idOf.put(kept.get(k), ids.get(k));
    }

    Map<String, JsonNode> positions = Layout.run(graph);
    Map<String, List<GraphEdge>> outgoing = new HashMap<>();
    ObjectNode edgesOut = JsonNodeFactory.instance.objectNode();

    for (int i = 0; i < graph.edges.size(); i++) {
      GraphEdge edge = graph.edges.get(i);
      Integer into = merges.get(i);
      if (into != null) {
        GraphEdge reverse = graph.edges.get(into);
        addToOrder(outgoing, edge.source, reverse);
        ((ObjectNode) edgesOut.get(idOf.get(reverse))).set("backward_condition", edge.forwardCondition.deepCopy());
        continue;
      }
      // An edge belongs in a node's edge_order only for a transition that leaves it: the source when it has a
      // forward condition, the target when it has a backward condition. A loaded edge may carry only one.
      if (edge.forwardCondition != null) {
        addToOrder(outgoing, edge.source, edge);
      }
      if (edge.backwardCondition != null) {
        addToOrder(outgoing, edge.target, edge);
      }
      ObjectNode wire = edge.extra.deepCopy();
      wire.put("source", edge.source);
      wire.put("target", edge.target);
      if (edge.forwardCondition != null) {
        wire.set("forward_condition", edge.forwardCondition.deepCopy());
      }
      if (edge.backwardCondition != null) {
        wire.set("backward_condition", edge.backwardCondition.deepCopy());
      }
      edgesOut.set(idOf.get(edge), wire);
    }

    ObjectNode nodesOut = JsonNodeFactory.instance.objectNode();
    Function<String, String> resolve = toolId -> {
      Tool tool = graph.toolRefs.get(toolId);
      return tool == null ? toolId : toolIds.get(tool.getName());
    };
    for (String id : graph.nodeOrder()) {
      ObjectNode out = graph.toolRefs.isEmpty() ? graph.getNode(id).deepCopy() : WorkflowGraph.mapNodeToolIds(graph.getNode(id), resolve);
      out.set("position", positions.get(id).deepCopy());
      List<GraphEdge> loaded = graph.loadedOrder.containsKey(id) ? graph.loadedOrder.get(id) : new ArrayList<>();
      List<GraphEdge> current = outgoing.containsKey(id) ? outgoing.get(id) : new ArrayList<>();
      ArrayNode order = out.putArray("edge_order");
      for (GraphEdge edge : edgeOrder(loaded, current)) {
        order.add(idOf.get(edge));
      }
      nodesOut.set(id, out);
    }

    ObjectNode workflow = JsonNodeFactory.instance.objectNode();
    workflow.set("nodes", nodesOut);
    workflow.set("edges", edgesOut);
    if (graph.preventSubagentLoops != null) {
      workflow.put("prevent_subagent_loops", graph.preventSubagentLoops.booleanValue());
    }
    return workflow;
  }

  /** A {@code unresolved_tool} error for each {@link Tool} object a node uses that has no id in {@code toolIds}. */
  private List<WorkflowIssue> unresolvedTools(Map<String, String> toolIds) {
    List<WorkflowIssue> issues = new ArrayList<>();
    for (String nodeId : graph.nodeOrder()) {
      for (String id : WorkflowGraph.nodeToolIds(graph.getNode(nodeId))) {
        Tool tool = graph.toolRefs.get(id);
        if (tool != null && toolIds.get(tool.getName()) == null) {
          issues.add(
            new WorkflowIssue(
              "unresolved_tool",
              WorkflowSeverity.ERROR,
              "Node \"" + nodeId + "\" uses tool \"" + tool.getName() + "\", which has no id yet. Push the agent with " +
              "agents().push(), or pass build(toolIds)",
              nodeId
            )
          );
        }
      }
    }
    return issues;
  }

  private static void addToOrder(Map<String, List<GraphEdge>> outgoing, String nodeId, GraphEdge edge) {
    outgoing.computeIfAbsent(nodeId, key -> new ArrayList<>()).add(edge);
  }

  /** A loaded node's order for the edges it still has, then any others in edge-list order. */
  private static List<GraphEdge> edgeOrder(List<GraphEdge> loaded, List<GraphEdge> current) {
    Set<GraphEdge> currentSet = identitySet(current);
    List<GraphEdge> result = new ArrayList<>();
    for (GraphEdge edge : loaded) {
      if (currentSet.contains(edge)) {
        result.add(edge);
      }
    }
    Set<GraphEdge> keptSet = identitySet(result);
    for (GraphEdge edge : current) {
      if (!keptSet.contains(edge)) {
        result.add(edge);
      }
    }
    return result;
  }

  private static Set<GraphEdge> identitySet(List<GraphEdge> edges) {
    Set<GraphEdge> set = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
    set.addAll(edges);
    return set;
  }

  /** Loaded edges keep their ids. New ones get {@code source__target}, with {@code __2}, {@code __3}... on collision. */
  private static List<String> assignEdgeIds(List<GraphEdge> edges) {
    Set<String> taken = new HashSet<>();
    for (GraphEdge edge : edges) {
      if (edge.id != null) {
        taken.add(edge.id);
      }
    }
    List<String> ids = new ArrayList<>();
    for (GraphEdge edge : edges) {
      if (edge.id != null) {
        ids.add(edge.id);
        continue;
      }
      String base = edge.source + "__" + edge.target;
      String id = base;
      for (int n = 2; taken.contains(id); n++) {
        id = base + "__" + n;
      }
      taken.add(id);
      ids.add(id);
    }
    return ids;
  }
}
