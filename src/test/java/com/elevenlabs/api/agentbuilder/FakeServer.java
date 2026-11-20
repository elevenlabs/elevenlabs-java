package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.ElevenLabsClient;
import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * An in-memory stand-in for the agents and tools endpoints push and pull use, on a local MockWebServer. Modelled on
 * the JS SDK's {@code fakeServer.ts}.
 */
final class FakeServer implements AutoCloseable {

  private static final JsonNodeFactory NODES = JsonNodeFactory.instance;

  static final class Call {

    final String method;
    final String path;
    final Map<String, String> query;
    final JsonNode body;

    Call(String method, String path, Map<String, String> query, JsonNode body) {
      this.method = method;
      this.path = path;
      this.query = query;
      this.body = body;
    }
  }

  static final class StoredTool {

    JsonNode config;
    List<String> usedBy;

    StoredTool(JsonNode config, List<String> usedBy) {
      this.config = config;
      this.usedBy = usedBy;
    }
  }

  final Map<String, StoredTool> tools = new LinkedHashMap<>();
  final Map<String, ObjectNode> agents = new LinkedHashMap<>();
  /** Branch configs by {@code agentId/branchId}. Only the main branch counts for {@code used_by_agent_id}, as in the API. */
  final Map<String, ObjectNode> branches = new LinkedHashMap<>();
  final List<Call> calls = Collections.synchronizedList(new ArrayList<Call>());
  /** Fails every request whose {@code METHOD path} matches. */
  volatile Pattern failOn;
  private int next = 1;
  private final MockWebServer server = new MockWebServer();

  FakeServer() {
    server.setDispatcher(
      new Dispatcher() {
        @Override
        public MockResponse dispatch(RecordedRequest request) {
          synchronized (FakeServer.this) {
            return handle(request);
          }
        }
      }
    );
    try {
      server.start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void addTool(String id, JsonNode config, String... usedBy) {
    List<String> users = new ArrayList<>();
    Collections.addAll(users, usedBy);
    tools.put(id, new StoredTool(config.deepCopy(), users));
  }

  void addAgent(String id, ObjectNode config) {
    agents.put(id, config.deepCopy());
  }

  ElevenLabsClient client() {
    return ElevenLabsClient.builder().apiKey("test").url(server.url("/").toString()).maxRetries(0).build();
  }

  /** The calls that change something, as {@code METHOD path}. */
  List<String> writes() {
    List<String> result = new ArrayList<>();
    synchronized (calls) {
      for (Call call : calls) {
        if (!"GET".equals(call.method)) {
          result.add(call.method + " " + call.path);
        }
      }
    }
    return result;
  }

  @Override
  public void close() throws IOException {
    server.shutdown();
  }

  private MockResponse handle(RecordedRequest request) {
    HttpUrl url = request.getRequestUrl();
    String method = request.getMethod();
    String path = url.encodedPath();
    Map<String, String> query = new LinkedHashMap<>();
    for (String name : url.queryParameterNames()) {
      query.put(name, url.queryParameter(name));
    }
    String text = request.getBody().readUtf8();
    JsonNode body = text.isEmpty() ? null : CanonicalJson.parse(text);
    calls.add(new Call(method, path, query, body));
    if (failOn != null && failOn.matcher(method + " " + path).find()) {
      return json(NODES.objectNode().put("detail", "boom"), 500);
    }
    String[] parts = path.split("/"); // /v1/convai/<kind>/<id>
    String kind = parts.length > 3 ? parts[3] : null;
    String id = parts.length > 4 ? parts[4] : null;
    if ("tools".equals(kind)) {
      if ("GET".equals(method) && id == null) {
        String agentId = url.queryParameter("used_by_agent_id");
        ArrayNode list = NODES.arrayNode();
        for (Map.Entry<String, StoredTool> tool : tools.entrySet()) {
          if (agentId == null || tool.getValue().usedBy.contains(agentId)) {
            list.add(toolResponse(tool.getKey(), tool.getValue().config));
          }
        }
        ObjectNode page = NODES.objectNode();
        page.set("tools", list);
        page.put("has_more", false);
        return json(page, 200);
      }
      if ("POST".equals(method)) {
        String toolId = "tool_new_" + next++;
        tools.put(toolId, new StoredTool(body.get("tool_config"), new ArrayList<String>()));
        return json(toolResponse(toolId, body.get("tool_config")), 200);
      }
      StoredTool tool = tools.get(id);
      if (tool == null) {
        return json(NODES.objectNode().put("detail", "not found"), 404);
      }
      if ("PATCH".equals(method)) {
        tool.config = body.get("tool_config");
      }
      return json(toolResponse(id, tool.config), 200);
    }
    if ("agents".equals(kind)) {
      if ("POST".equals(method) && "create".equals(id)) {
        String agentId = "agent_new_" + next++;
        storeAgent(agentId, (ObjectNode) body);
        return json(NODES.objectNode().put("agent_id", agentId), 200);
      }
      String branchId = url.queryParameter("branch_id");
      if (branchId != null) {
        String key = id + "/" + branchId;
        if ("PATCH".equals(method)) {
          ObjectNode merged = branches.containsKey(key) ? branches.get(key).deepCopy() : NODES.objectNode();
          merged.setAll((ObjectNode) body);
          branches.put(key, merged);
        }
        ObjectNode branch = branches.get(key);
        return branch == null ? json(NODES.objectNode().put("detail", "not found"), 404) : json(agentResponse(id, branch), 200);
      }
      if ("PATCH".equals(method)) {
        ObjectNode merged = agents.containsKey(id) ? agents.get(id).deepCopy() : NODES.objectNode();
        merged.setAll((ObjectNode) body);
        storeAgent(id, merged);
        return json(agentResponse(id, agents.get(id)), 200);
      }
      ObjectNode agent = agents.get(id);
      return agent == null ? json(NODES.objectNode().put("detail", "not found"), 404) : json(agentResponse(id, agent), 200);
    }
    throw new IllegalStateException("unexpected request " + method + " " + path);
  }

  private void storeAgent(String agentId, ObjectNode config) {
    agents.put(agentId, config);
    List<String> ids = new ArrayList<>();
    JsonNode toolIds = config.path("conversation_config").path("agent").path("prompt").path("tool_ids");
    for (JsonNode toolId : toolIds) {
      ids.add(toolId.asText());
    }
    for (JsonNode node : config.path("workflow").path("nodes")) {
      for (JsonNode tool : node.path("tools")) {
        ids.add(tool.path("tool_id").asText());
      }
      for (JsonNode extra : node.path("additional_tool_ids")) {
        ids.add(extra.asText());
      }
    }
    for (Map.Entry<String, StoredTool> tool : tools.entrySet()) {
      tool.getValue().usedBy.remove(agentId);
      if (ids.contains(tool.getKey())) {
        tool.getValue().usedBy.add(agentId);
      }
    }
  }

  private static ObjectNode toolResponse(String id, JsonNode config) {
    ObjectNode response = NODES.objectNode();
    response.put("id", id);
    response.set("tool_config", config.deepCopy());
    ObjectNode access = response.putObject("access_info");
    access.put("is_creator", true);
    access.put("creator_name", "a");
    access.put("creator_email", "a@example.com");
    access.put("role", "admin");
    response.putObject("usage_stats").put("avg_latency_secs", 0.1);
    return response;
  }

  /** The defaults the API fills into a stored workflow, which the response model requires. */
  private static JsonNode withServerDefaults(JsonNode workflow) {
    ObjectNode result = NODES.objectNode();
    result.put("prevent_subagent_loops", false);
    result.setAll((ObjectNode) workflow.deepCopy());
    ObjectNode nodes = NODES.objectNode();
    Iterator<Map.Entry<String, JsonNode>> it = workflow.path("nodes").fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> entry = it.next();
      JsonNode node = entry.getValue();
      if ("override_agent".equals(node.path("type").asText())) {
        ObjectNode filled = NODES.objectNode();
        filled.putObject("conversation_config");
        filled.putArray("additional_knowledge_base");
        filled.putArray("additional_tool_ids");
        filled.put("entry_behavior", "generate_immediately");
        filled.put("allow_interruptions_on_entry", true);
        filled.setAll((ObjectNode) node.deepCopy());
        nodes.set(entry.getKey(), filled);
      } else {
        nodes.set(entry.getKey(), node.deepCopy());
      }
    }
    ObjectNode edges = NODES.objectNode();
    Iterator<Map.Entry<String, JsonNode>> edgeIt = workflow.path("edges").fields();
    while (edgeIt.hasNext()) {
      Map.Entry<String, JsonNode> entry = edgeIt.next();
      ObjectNode filled = NODES.objectNode();
      filled.putArray("reroutes");
      filled.setAll((ObjectNode) entry.getValue().deepCopy());
      edges.set(entry.getKey(), filled);
    }
    result.set("nodes", nodes);
    result.set("edges", edges);
    return result;
  }

  private static ObjectNode agentResponse(String id, ObjectNode config) {
    ObjectNode response = NODES.objectNode();
    response.put("agent_id", id);
    ObjectNode metadata = response.putObject("metadata");
    metadata.put("created_at_unix_secs", 1);
    metadata.put("updated_at_unix_secs", 1);
    response.put("created_at_unix_secs", 1);
    response.put("updated_at_unix_secs", 1);
    response.put("version_id", "ver_1");
    response.setAll(config.deepCopy());
    if (config.has("workflow")) {
      response.set("workflow", withServerDefaults(config.get("workflow")));
    }
    return response;
  }

  private static MockResponse json(JsonNode body, int status) {
    return new MockResponse()
      .setResponseCode(status)
      .setHeader("Content-Type", "application/json")
      .setBody(CanonicalJson.write(body, false));
  }
}
