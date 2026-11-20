package com.elevenlabs.api.agentworkflows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Positions for the workflow editor. Nodes that already have a position keep it. The rest go in column =
 * breadth-first depth from the start node, row = order of discovery within that column, skipping rows where a
 * positioned node already sits. Unreachable nodes go in the column after the deepest one.
 */
final class Layout {

  static final int COLUMN_WIDTH = 300;
  static final int ROW_HEIGHT = 200;

  private Layout() {}

  static Map<String, JsonNode> run(WorkflowGraph graph) {
    Validate.Depths depths = Validate.depthsFrom(graph);
    int lastColumn = (depths.map.isEmpty() ? 0 : Collections.max(depths.map.values())) + 1;

    List<String> order = depths.order();
    for (String id : graph.nodeOrder()) {
      if (!depths.has(id)) {
        order.add(id);
      }
    }

    Map<Integer, Integer> rows = new HashMap<>();
    Map<String, JsonNode> positions = new HashMap<>();
    Set<String> occupied = new HashSet<>();
    for (String id : graph.nodeOrder()) {
      JsonNode position = graph.getNode(id).get("position");
      if (position != null && position.isObject()) {
        occupied.add(cell(coordinate(position.get("x")), coordinate(position.get("y"))));
      }
    }

    for (String id : order) {
      JsonNode existing = graph.getNode(id).get("position");
      if (existing != null && existing.isObject()) {
        positions.put(id, existing);
        continue;
      }
      int column = depths.has(id) ? depths.map.get(id) : lastColumn;
      int row = rows.containsKey(column) ? rows.get(column) : 0;
      while (occupied.contains(cell(column * COLUMN_WIDTH, row * ROW_HEIGHT))) {
        row++;
      }
      rows.put(column, row + 1);
      occupied.add(cell(column * COLUMN_WIDTH, row * ROW_HEIGHT));
      ObjectNode position = JsonNodeFactory.instance.objectNode();
      position.put("x", column * COLUMN_WIDTH);
      position.put("y", row * ROW_HEIGHT);
      positions.put(id, position);
    }
    return positions;
  }

  private static double coordinate(JsonNode value) {
    return value != null && value.isNumber() ? CanonicalJson.asDouble(value) : Double.NaN;
  }

  private static String cell(double x, double y) {
    return x + "," + y;
  }
}
