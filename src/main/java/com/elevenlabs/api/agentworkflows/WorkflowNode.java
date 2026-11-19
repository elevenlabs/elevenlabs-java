package com.elevenlabs.api.agentworkflows;

import com.fasterxml.jackson.databind.JsonNode;

/** A handle to one node in a workflow. Handles are cheap: all state lives in the {@link Workflow}. */
public final class WorkflowNode extends WorkflowChain {

  private final String id;
  private final String parentId;

  WorkflowNode(WorkflowGraph graph, String id, String parentId) {
    super(graph);
    this.id = id;
    this.parentId = parentId;
  }

  /** This node's id. */
  public String getId() {
    return id;
  }

  /** The API node type, for example {@code "override_agent"} or {@code "tool"}; {@code null} for an unknown node. */
  public String getType() {
    return graph.containsNode(id) ? WorkflowGraph.typeOf(graph.getNode(id)) : null;
  }

  /** Opens a branch that is followed when {@code condition} holds. */
  public WorkflowBranch when(Condition condition) {
    return branch(graph, id, condition, null);
  }

  /** Opens a branch followed when {@code condition} holds, with a backward condition for the way back. */
  public WorkflowBranch when(Condition condition, Condition back) {
    return branch(graph, id, condition, back);
  }

  /** Opens a branch that is followed when the LLM judges {@code condition} to be true. */
  public WorkflowBranch when(String condition) {
    return when(Condition.llm(condition), null);
  }

  /** {@link #when(String)}, with a backward condition for the way back. */
  public WorkflowBranch when(String condition, Condition back) {
    return when(Condition.llm(condition), back);
  }

  /** Opens a branch that is followed when {@code condition} evaluates to true. */
  public WorkflowBranch when(Expr condition) {
    return when(Condition.expr(condition), null);
  }

  /** {@link #when(Expr)}, with a backward condition for the way back. */
  public WorkflowBranch when(Expr condition, Condition back) {
    return when(Condition.expr(condition), back);
  }

  /** Opens the branch followed when every tool in this tool node succeeded. */
  public WorkflowBranch onSuccess() {
    return onSuccess(null);
  }

  /** {@link #onSuccess()} with an edge label. */
  public WorkflowBranch onSuccess(String label) {
    return branch(graph, id, Condition.success(label), null);
  }

  /** Opens the branch followed when any tool in this tool node failed. */
  public WorkflowBranch onFailure() {
    return onFailure(null);
  }

  /** {@link #onFailure()} with an edge label. */
  public WorkflowBranch onFailure(String label) {
    return branch(graph, id, Condition.failure(label), null);
  }

  /**
   * Routes both outcomes of this tool node in one place. Same as calling {@code onSuccess()} and then
   * {@code onFailure()}, so the success edge always comes first, whichever order the branches were given in.
   * Returns this node.
   */
  public WorkflowNode onResult(ResultBranches branches) {
    if (branches != null && branches.success != null) {
      branches.success.accept(onSuccess());
    }
    if (branches != null && branches.failure != null) {
      branches.failure.accept(onFailure());
    }
    return this;
  }

  /**
   * The node this handle was chained from, so a whole workflow can be written as one chain. For a handle from
   * {@code node(id)}, the source of the first edge into the node other than a self-loop. The start node if there
   * is neither. Adds nothing to the graph.
   */
  public WorkflowNode up() {
    String parent = parentId;
    if (parent == null) {
      for (GraphEdge edge : graph.edges) {
        if (edge.target.equals(id) && !edge.source.equals(id)) {
          parent = edge.source;
          break;
        }
      }
    }
    return new WorkflowNode(graph, parent == null ? graph.startId : parent, null);
  }

  /** Changes this node's options. Only fields that apply to the node's kind may be set; {@code build()} reports the rest. */
  public WorkflowNode update(NodeUpdate update) {
    if (!graph.containsNode(id)) {
      graph.issue("unknown_node", "Can't update unknown node \"" + id + "\"", id);
      return this;
    }
    NodeUpdate u = update == null ? new NodeUpdate() : update;
    Object overrides = u.values.get("schema_overrides");
    JsonNode schemaOverrides = overrides == null ? null : CanonicalJson.toTree(overrides);
    graph.setNode(
      id,
      graph.applyOptions(id, graph.getNode(id), u.destination, u.toolIds, schemaOverrides, u.values.toWire("schema_overrides"))
    );
    return this;
  }

  @Override
  EdgeOrigin origin() {
    return new EdgeOrigin(id, Condition.always().toWire(), null);
  }
}
