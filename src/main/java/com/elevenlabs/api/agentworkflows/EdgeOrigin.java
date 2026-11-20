package com.elevenlabs.api.agentworkflows;

import com.fasterxml.jackson.databind.node.ObjectNode;

/** Where the next edge starts and which conditions it carries. */
final class EdgeOrigin {

  final String source;
  final ObjectNode forwardCondition;
  final ObjectNode backwardCondition;

  EdgeOrigin(String source, ObjectNode forwardCondition, ObjectNode backwardCondition) {
    this.source = source;
    this.forwardCondition = forwardCondition;
    this.backwardCondition = backwardCondition;
  }
}
