package com.elevenlabs.api.agentworkflows;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * An edge in the workflow graph. Compared by identity: two edges with the same fields are still two different
 * edges, so {@code equals} is deliberately not overridden.
 */
final class GraphEdge {

  String source;
  String target;
  ObjectNode forwardCondition;
  ObjectNode backwardCondition;

  /** Set for edges loaded from an existing workflow; generated at build time otherwise. */
  String id;

  /** Other fields of a loaded edge, such as {@code reroutes} (editor bend points), written back as they were. */
  ObjectNode extra = JsonNodeFactory.instance.objectNode();

  GraphEdge(String source, String target) {
    this.source = source;
    this.target = target;
  }
}
