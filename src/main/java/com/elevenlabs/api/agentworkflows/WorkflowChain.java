package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.agenttools.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The verbs that add a node and connect it to the current point in the workflow. Each returns the new node, so
 * linear flows chain: {@code wf.converse(...).when("...").tool(...)}. Implemented by {@link Workflow} (which
 * branches from the start node), {@link WorkflowNode} and {@link WorkflowBranch}.
 */
public abstract class WorkflowChain {

  final WorkflowGraph graph;

  WorkflowChain(WorkflowGraph graph) {
    this.graph = graph;
  }

  /** Where an edge added from here starts, and which conditions it carries. */
  abstract EdgeOrigin origin();

  /** A subagent node: the agent holds a conversation with {@code prompt} added to its instructions until an outgoing condition holds. */
  public WorkflowNode converse(String id, String prompt) {
    return converse(id, prompt, null);
  }

  /** A subagent node, with options. */
  public WorkflowNode converse(String id, String prompt, ConverseOptions options) {
    ConverseOptions opts = options == null ? new ConverseOptions() : options;
    ObjectNode model = JsonNodeFactory.instance.objectNode();
    model.put("type", "override_agent");
    model.put("label", id);
    model.put("additional_prompt", prompt);
    graph.addNode(id, graph.applyOptions(id, model, null, opts.toolIds, opts.tools, null, opts.values.toWire()));
    return connect(id, null);
  }

  /** Runs the tools in parallel. Branch on the result with {@code onResult()}, or {@code onSuccess()} and {@code onFailure()}. */
  public WorkflowNode tool(String id, List<String> toolIds) {
    return tool(id, toolIds, null);
  }

  /** A tool node, with options such as schema overrides. */
  public WorkflowNode tool(String id, List<String> toolIds, ToolOptions options) {
    ToolOptions opts = options == null ? new ToolOptions() : options;
    ObjectNode model = JsonNodeFactory.instance.objectNode();
    model.put("type", "tool");
    Object overrides = opts.values.get("schema_overrides");
    JsonNode schemaOverrides = overrides == null ? null : CanonicalJson.toTree(overrides);
    List<String> ids = toolIds == null ? new ArrayList<>() : new ArrayList<>(toolIds);
    graph.addNode(
      id,
      graph.applyOptions(id, model, null, ids, opts.tools, schemaOverrides, opts.values.toWire("schema_overrides"))
    );
    return connect(id, null);
  }

  /**
   * A tool node that runs {@link Tool} objects. Their ids are filled in when the workflow is built with tool ids, as
   * {@code agents().push()} does, so a workflow can use tools that don't exist yet.
   */
  public WorkflowNode tool(String id, Tool... tools) {
    return tool(id, new ToolOptions().tools(Arrays.asList(tools)));
  }

  /**
   * A tool node whose tools are all in {@code options}: {@link ToolOptions#tools(List) Tool objects}, plus options
   * such as schema overrides, keyed by tool name.
   */
  public WorkflowNode tool(String id, ToolOptions options) {
    return tool(id, new ArrayList<String>(), options);
  }

  /** Transfers the conversation to a phone number, SIP URI or another agent. */
  public WorkflowNode transfer(String id, Destination destination) {
    return transfer(id, destination, null);
  }

  /** A transfer node, with options for the destination's kind. */
  public WorkflowNode transfer(String id, Destination destination, TransferOptions options) {
    TransferOptions opts = options == null ? new TransferOptions() : options;
    graph.addNode(id, graph.applyOptions(id, destination.toWire(), null, null, null, null, opts.values.toWire()));
    return connect(id, null);
  }

  /** Ends the conversation. Every {@code end()} without an id shares one end node, {@code "end"}. */
  public WorkflowNode end() {
    return end(null);
  }

  /** Ends the conversation at the end node {@code options.id}, reusing it if it already exists. */
  public WorkflowNode end(EndOptions options) {
    EndOptions opts = options == null ? new EndOptions() : options;
    String id = opts.id == null ? "end" : opts.id;
    ObjectNode model = JsonNodeFactory.instance.objectNode();
    model.put("type", "end");
    graph.addNode(id, graph.applyOptions(id, model, null, null, null, null, opts.values.toWire()));
    return connect(id, null);
  }

  /**
   * Moves the conversation to a node that already exists (or will by {@code build()}), for merges and loops. When
   * {@code target} already has an edge to this node, the move is sent as that edge's backward condition.
   */
  public WorkflowNode goTo(String target) {
    return connect(target, null);
  }

  /** {@link #goTo(String)}, with a backward condition for the way back. */
  public WorkflowNode goTo(String target, Condition back) {
    return connect(target, back);
  }

  /** {@link #goTo(String)} for a node handle. */
  public WorkflowNode goTo(WorkflowNode target) {
    return connect(target.getId(), null);
  }

  /** {@link #goTo(String, Condition)} for a node handle. */
  public WorkflowNode goTo(WorkflowNode target, Condition back) {
    return connect(target.getId(), back);
  }

  private WorkflowNode connect(String target, Condition back) {
    EdgeOrigin origin = origin();
    GraphEdge edge = new GraphEdge(origin.source, target);
    edge.forwardCondition = origin.forwardCondition == null ? null : origin.forwardCondition.deepCopy();
    if (back != null) {
      edge.backwardCondition = back.toWire();
    } else if (origin.backwardCondition != null) {
      edge.backwardCondition = origin.backwardCondition.deepCopy();
    }
    graph.edges.add(edge);
    return new WorkflowNode(graph, target, origin.source);
  }

  static WorkflowBranch branch(WorkflowGraph graph, String source, Condition condition, Condition back) {
    return new WorkflowBranch(graph, new EdgeOrigin(source, condition.toWire(), back == null ? null : back.toWire()));
  }
}
