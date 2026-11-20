package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.types.AgentWorkflowRequestModelNodesValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Whether this SDK version's generated request model has a member for a node type. Asked of the generated
 * union's own discriminator rather than listed here, so a regeneration that adds a node type is picked up without
 * a change to the builder.
 */
final class NodeTypes {

  private static final Map<String, Boolean> KNOWN = new ConcurrentHashMap<>();
  private static final Method DISCRIMINATOR = discriminator();

  private NodeTypes() {}

  static boolean modelKnows(String nodeType) {
    if (nodeType == null) {
      return false;
    }
    return KNOWN.computeIfAbsent(nodeType, NodeTypes::probe);
  }

  private static boolean probe(String nodeType) {
    if (DISCRIMINATOR == null) {
      // The generated union no longer exposes its discriminator; don't warn about every node.
      return true;
    }
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put("type", nodeType);
    try {
      return ((Integer) DISCRIMINATOR.invoke(null, node)) >= 0;
    } catch (ReflectiveOperationException | RuntimeException e) {
      return true;
    }
  }

  private static Method discriminator() {
    try {
      Method method = AgentWorkflowRequestModelNodesValue.class.getDeclaredMethod("getDiscriminatorIndex", JsonNode.class);
      method.setAccessible(true);
      return method;
    } catch (ReflectiveOperationException | RuntimeException e) {
      return null;
    }
  }
}
