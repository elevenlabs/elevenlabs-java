package com.elevenlabs.api.agentworkflows;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Checks that need the finished graph, reverse-edge merging and the breadth-first walk the layout shares. */
final class Validate {

  private Validate() {}

  /** Breadth-first depths from the start node, keeping visiting order. */
  static final class Depths {

    final Map<String, Integer> map = new LinkedHashMap<>();

    void set(String id, int depth) {
      map.put(id, depth);
    }

    boolean has(String id) {
      return map.containsKey(id);
    }

    List<String> order() {
      return new ArrayList<>(map.keySet());
    }
  }

  /** Every issue: those recorded while chaining, then those found in the finished graph. */
  static List<WorkflowIssue> run(WorkflowGraph graph) {
    List<WorkflowIssue> issues = new ArrayList<>(graph.issues);
    Set<String> endsWithEdges = new HashSet<>();

    Map<Integer, Integer> merged = reverseEdgeMerges(graph);
    Set<String> connected = new HashSet<>();
    for (int i = 0; i < graph.edges.size(); i++) {
      if (merged.containsKey(i)) {
        continue;
      }
      GraphEdge edge = graph.edges.get(i);
      // The API allows one edge per pair of nodes, in either direction.
      String pair = edge.source.compareTo(edge.target) <= 0
        ? edge.source + "\u0000" + edge.target
        : edge.target + "\u0000" + edge.source;
      if (connected.contains(pair)) {
        error(
          issues,
          "duplicate_edge",
          "Edge " + name(edge.source, edge.target) + " connects two nodes that already have an edge between them, " +
          "and the API allows only one. Give the existing edge a different condition, or remove one",
          edge.source
        );
      }
      connected.add(pair);
    }

    for (GraphEdge edge : graph.edges) {
      String name = name(edge.source, edge.target);
      if (edge.source.equals(edge.target)) {
        error(issues, "self_loop", "Edge " + name + " connects a node to itself, which the API doesn't allow", edge.source);
      }
      for (String id : new String[] { edge.source, edge.target }) {
        if (!graph.containsNode(id)) {
          error(issues, "unknown_node", "Edge " + name + " refers to unknown node \"" + id + "\"", id);
        }
      }
      // Forward routing rules apply only when there is a forward transition. A loaded edge can be backward-only,
      // and then source -> target doesn't exist.
      if (edge.forwardCondition != null) {
        routingRules(graph, issues, endsWithEdges, edge.source, edge.target, edge.forwardCondition);
      }
      // A backward condition is a second transition the other way, target -> source, that the API stores on this
      // same edge. It obeys the same routing rules as any edge leaving the target.
      if (edge.backwardCondition != null) {
        routingRules(graph, issues, endsWithEdges, edge.target, edge.source, edge.backwardCondition);
      }
    }

    for (String id : graph.nodeOrder()) {
      String type = WorkflowGraph.typeOf(graph.getNode(id));
      if (!NodeTypes.modelKnows(type)) {
        issues.add(
          new WorkflowIssue(
            "unknown_node_type",
            WorkflowSeverity.WARNING,
            "Node \"" + id + "\" has type \"" + type + "\", which this SDK version doesn't model. build() keeps it " +
            "as given, but agents create/update may reject it. Upgrade the SDK, or send toJson() as the request's " +
            "workflow through additional body properties.",
            id
          )
        );
      }
    }

    Depths reachable = depthsFrom(graph);
    for (String id : graph.nodeOrder()) {
      if (!reachable.has(id)) {
        issues.add(
          new WorkflowIssue(
            "unreachable_node",
            WorkflowSeverity.WARNING,
            "Node \"" + id + "\" can't be reached from the start node",
            id
          )
        );
      }
    }
    return issues;
  }

  /** The rules for one transition {@code from -> to}: a forward condition, or a backward one read the other way. */
  private static void routingRules(
    WorkflowGraph graph,
    List<WorkflowIssue> issues,
    Set<String> endsWithEdges,
    String from,
    String to,
    JsonNode condition
  ) {
    String name = name(from, to);
    if (to.equals(graph.startId)) {
      error(issues, "edge_into_start", "Edge " + name + " points into the start node; go to the first node after it instead", from);
    }
    String sourceType = graph.containsNode(from) ? WorkflowGraph.typeOf(graph.getNode(from)) : null;
    String conditionType = WorkflowGraph.typeOf(condition);
    if ("end".equals(sourceType) && endsWithEdges.add(from)) {
      error(issues, "end_has_outgoing_edges", "End node \"" + from + "\" can't have outgoing edges", from);
    }
    if ("tool".equals(sourceType) && !"result".equals(conditionType)) {
      error(issues, "tool_edge_needs_result", "Edge " + name + " leaves a tool node, so it must use onSuccess() or onFailure()", from);
    }
    if (sourceType != null && !"tool".equals(sourceType) && "result".equals(conditionType)) {
      error(issues, "result_edge_needs_tool", "Edge " + name + " uses onSuccess()/onFailure(), which only applies to tool nodes", from);
    }
  }

  /**
   * Edges the API stores as the backward condition of another edge: index of the later edge to index of the
   * earlier one. The later edge goes the opposite way between the same two nodes, and neither has a backward
   * condition of its own.
   */
  static Map<Integer, Integer> reverseEdgeMerges(WorkflowGraph graph) {
    Map<Integer, Integer> merges = new HashMap<>();
    Set<Integer> taken = new HashSet<>();
    for (int j = 0; j < graph.edges.size(); j++) {
      GraphEdge later = graph.edges.get(j);
      if (later.source.equals(later.target) || later.backwardCondition != null || later.forwardCondition == null) {
        continue;
      }
      for (int k = 0; k < j; k++) {
        GraphEdge earlier = graph.edges.get(k);
        if (
          !taken.contains(k) &&
          !merges.containsKey(k) &&
          earlier.source.equals(later.target) &&
          earlier.target.equals(later.source) &&
          earlier.backwardCondition == null
        ) {
          merges.put(j, k);
          taken.add(k);
          break;
        }
      }
    }
    return merges;
  }

  /**
   * Breadth-first depth of every node reachable from the start node, in visiting order. Edges with a forward
   * condition are followed forward first; only when that runs out does a backward condition lead on, from an
   * edge's target to a source not yet visited.
   */
  static Depths depthsFrom(WorkflowGraph graph) {
    Depths depths = new Depths();
    visit(graph, depths, graph.startId, 0);
    while (true) {
      GraphEdge back = null;
      for (GraphEdge edge : graph.edges) {
        if (
          edge.backwardCondition != null &&
          depths.has(edge.target) &&
          !depths.has(edge.source) &&
          graph.containsNode(edge.source)
        ) {
          back = edge;
          break;
        }
      }
      if (back == null) {
        return depths;
      }
      visit(graph, depths, back.source, depths.map.get(back.target) + 1);
    }
  }

  private static void visit(WorkflowGraph graph, Depths depths, String id, int depth) {
    depths.set(id, depth);
    ArrayDeque<String> queue = new ArrayDeque<>();
    queue.add(id);
    while (!queue.isEmpty()) {
      String current = queue.poll();
      for (GraphEdge edge : graph.edges) {
        if (
          edge.source.equals(current) &&
          edge.forwardCondition != null &&
          !depths.has(edge.target) &&
          graph.containsNode(edge.target)
        ) {
          depths.set(edge.target, depths.map.get(current) + 1);
          queue.add(edge.target);
        }
      }
    }
  }

  private static String name(String from, String to) {
    return "\"" + from + "\" → \"" + to + "\"";
  }

  private static void error(List<WorkflowIssue> issues, String code, String message, String nodeId) {
    issues.add(new WorkflowIssue(code, WorkflowSeverity.ERROR, message, nodeId));
  }
}
