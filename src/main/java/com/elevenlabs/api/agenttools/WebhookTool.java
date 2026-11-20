package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A tool the server runs by calling an HTTP endpoint. Every method returns the tool, so a tool is written as one
 * chain.
 *
 * <pre>{@code
 * WebhookTool getOrder = Tool.webhook("get_order", "Look up an order by the number the caller gives")
 *   .get("https://api.example.com/orders/{order_id}")
 *   .pathParam("order_id", Schema.string().describe("The order number, e.g. A-1042"))
 *   .header("Authorization", Header.secret("sec_orders_api"))
 *   .assign("order_status", "order.status");
 * }</pre>
 */
public final class WebhookTool extends Tool {

  private static final Set<String> ALLOWED = withCommonOptions(
    "dynamic_variable_placeholders",
    "execution_mode",
    "content_type",
    "response_filter",
    "follow_redirects",
    "follow_redirects_allowed_domains"
  );

  // As the API matches them: env var templates aren't placeholders.
  private static final Pattern ENV_VAR_TEMPLATE = Pattern.compile("\\{\\{system__env_[a-z][a-z0-9_]*\\}\\}");
  private static final Pattern PLACEHOLDER = Pattern.compile("\\{([^{}]+)\\}");

  /** The {@code api_schema} fields not kept below: url, method, auth, content type and so on. */
  private final ObjectNode api;

  private final LinkedHashMap<String, Schema> pathParams = new LinkedHashMap<>();
  private Schema query;

  // Whether the loaded query schema declared its own `type` (otherwise the builder's is synthetic and not written),
  // and whether it is still exactly as loaded (so an empty one round-trips).
  private boolean queryTypeLoaded;
  private boolean queryUntouched;
  private Schema body;
  private Schema response;
  private final LinkedHashMap<String, JsonNode> headers = new LinkedHashMap<>();

  /** Loaded {@code api_schema} fields of the wrong shape, kept in {@link #api} as loaded; {@code build()} reports them. */
  private final Set<String> malformed = new LinkedHashSet<>();

  WebhookTool(ObjectNode config, ObjectNode api) {
    super(config);
    this.api = api;
  }

  static WebhookTool load(ObjectNode config) {
    JsonNode apiNode = config.get("api_schema");
    ObjectNode api;
    boolean apiMalformed = false;
    if (apiNode != null && apiNode.isObject()) {
      api = (ObjectNode) apiNode;
      config.remove("api_schema");
    } else {
      // Missing, or of the wrong shape: a wrong one stays in the config as loaded (and has no URL).
      api = ToolWire.NODES.objectNode();
      apiMalformed = apiNode != null;
    }
    WebhookTool tool = new WebhookTool(config, api);
    if (apiMalformed) {
      tool.malformed.add("api_schema");
    }
    JsonNode pathParams = api.get("path_params_schema");
    if (pathParams != null) {
      if (allObjects(pathParams)) {
        Iterator<Map.Entry<String, JsonNode>> it = pathParams.fields();
        while (it.hasNext()) {
          Map.Entry<String, JsonNode> param = it.next();
          ObjectNode model = (ObjectNode) param.getValue();
          tool.pathParams.put(param.getKey(), Schema.fromModel(model, false, Schema.kindOf(model)));
        }
        api.remove("path_params_schema");
      } else {
        tool.malformed.add("path_params_schema");
      }
    }
    JsonNode query = api.get("query_params_schema");
    if (query != null && query.isObject()) {
      ObjectNode model = (ObjectNode) query;
      tool.query = Schema.fromModel(model, false, Schema.containerKind(model));
      tool.queryTypeLoaded = model.hasNonNull("type");
      tool.queryUntouched = true;
      api.remove("query_params_schema");
    } else if (query != null) {
      tool.malformed.add("query_params_schema");
    }
    tool.body = loadContainer(api, "request_body_schema", tool.malformed);
    tool.response = loadContainer(api, "response_body_schema", tool.malformed);
    JsonNode headers = api.get("request_headers");
    if (headers != null && headers.isObject()) {
      Iterator<Map.Entry<String, JsonNode>> it = headers.fields();
      while (it.hasNext()) {
        Map.Entry<String, JsonNode> header = it.next();
        tool.headers.put(header.getKey(), header.getValue().deepCopy());
      }
      api.remove("request_headers");
    } else if (headers != null) {
      tool.malformed.add("request_headers");
    }
    return tool;
  }

  @Override
  public ToolKind getKind() {
    return ToolKind.WEBHOOK;
  }

  /** The request body schema, for editing and setting again with {@link #body(Schema)}. */
  public Optional<Schema> getBodySchema() {
    return Optional.ofNullable(body);
  }

  /** The response body schema, for editing and setting again with {@link #response(Schema)}. */
  public Optional<Schema> getResponseSchema() {
    return Optional.ofNullable(response);
  }

  /** Sends a {@code GET} to {@code url}. {@code {name}} placeholders in the URL are filled from path params. */
  public WebhookTool get(String url) {
    return request("GET", url);
  }

  /** Sends a {@code POST} to {@code url}. */
  public WebhookTool post(String url) {
    return request("POST", url);
  }

  /** Sends a {@code PUT} to {@code url}. */
  public WebhookTool put(String url) {
    return request("PUT", url);
  }

  /** Sends a {@code PATCH} to {@code url}. */
  public WebhookTool patch(String url) {
    return request("PATCH", url);
  }

  /** Sends a {@code DELETE} to {@code url}. */
  public WebhookTool delete(String url) {
    return request("DELETE", url);
  }

  /**
   * Sets the path param {@code name}, filling the {@code {name}} placeholder in the URL. Literal schemas only. A path
   * param is always required, whatever {@code optional()} says. A param that exists is replaced in place.
   */
  public WebhookTool pathParam(String name, Schema schema) {
    if (!malformed.contains("path_params_schema")) {
      pathParams.put(name, schema);
    }
    return this;
  }

  /**
   * Sets the query param {@code name}. Required unless the schema is {@code optional()}. Literal schemas only. A
   * loaded query schema that isn't an object is left as loaded, and {@code build()} reports it.
   */
  public WebhookTool queryParam(String name, Schema schema) {
    if (query == null && !malformed.contains("query_params_schema")) {
      query = Schema.object().property(name, schema);
      queryUntouched = false;
    } else if (query instanceof ObjectSchema) {
      query = ((ObjectSchema) query).property(name, schema);
      queryUntouched = false;
    }
    return this;
  }

  /** Sets the request body schema. Must be an object. */
  public WebhookTool body(Schema schema) {
    body = schema;
    malformed.remove("request_body_schema");
    api.remove("request_body_schema");
    return this;
  }

  /** Sets the response body schema. Must be an object. */
  public WebhookTool response(Schema schema) {
    response = schema;
    malformed.remove("response_body_schema");
    api.remove("response_body_schema");
    return this;
  }

  /** Sets the request header {@code name}. A header that exists is replaced in place. */
  public WebhookTool header(String name, Header value) {
    if (!malformed.contains("request_headers")) {
      headers.put(name, value.toWire());
    }
    return this;
  }

  /** Authenticates requests with an auth connection. */
  public WebhookTool auth(AuthConnection connection) {
    api.set("auth_connection", connection.toWire());
    return this;
  }

  /**
   * Stores the value at {@code valuePath} in the response (dot notation, such as {@code "order.status"}) in the
   * dynamic variable {@code variable}.
   */
  public WebhookTool assign(String variable, String valuePath) {
    return assign(variable, valuePath, new AssignOptions());
  }

  /** {@link #assign(String, String)} with options. */
  public WebhookTool assign(String variable, String valuePath, AssignOptions options) {
    ObjectNode assignment = ToolWire.NODES.objectNode();
    assignment.put("source", "response");
    assignment.put("dynamic_variable", variable);
    assignment.put("value_path", valuePath);
    if (options != null && options.sanitize != null) {
      assignment.put("sanitize", options.sanitize);
    }
    if (options != null && options.preserveNativeType != null) {
      assignment.put("preserve_native_type", options.preserveNativeType);
    }
    JsonNode existing = config.get("assignments");
    ArrayNode assignments = existing != null && existing.isArray() ? (ArrayNode) existing : ToolWire.NODES.arrayNode();
    assignments.add(assignment);
    config.set("assignments", assignments);
    return this;
  }

  /** Removes the path param {@code name}. Does nothing if there is none. */
  public WebhookTool removePathParam(String name) {
    pathParams.remove(name);
    return this;
  }

  /**
   * Removes the query param {@code name}. Does nothing if there is none. Removing the last one leaves
   * {@code query_params_schema} out, unless the loaded schema has other fields.
   */
  public WebhookTool removeQueryParam(String name) {
    if (query instanceof ObjectSchema && ((ObjectSchema) query).propertySchema(name).isPresent()) {
      query = ((ObjectSchema) query).without(name);
      queryUntouched = false;
    }
    return this;
  }

  /** Removes the request header {@code name}. Does nothing if there is none. */
  public WebhookTool removeHeader(String name) {
    headers.remove(name);
    return this;
  }

  /**
   * Removes every assignment to {@code variable}. Does nothing if there is none. Removing the last one leaves
   * {@code assignments} out.
   */
  public WebhookTool removeAssignment(String variable) {
    JsonNode existing = config.get("assignments");
    if (existing != null && existing.isArray()) {
      ArrayNode kept = ToolWire.NODES.arrayNode();
      for (JsonNode assignment : existing) {
        if (!variable.equals(ToolWire.str(assignment.get("dynamic_variable")))) {
          kept.add(assignment.deepCopy());
        }
      }
      if (kept.size() == 0 && existing.size() > 0) {
        config.remove("assignments");
      } else if (kept.size() != existing.size()) {
        config.set("assignments", kept);
      }
    }
    return this;
  }

  /** Merges options for webhook tools. An option that doesn't apply is reported by {@code build()}. */
  @Override
  public WebhookTool options(ToolOptions options) {
    applyOptions(options);
    return this;
  }

  @Override
  Collection<String> allowedOptions() {
    return ALLOWED;
  }

  @Override
  void setOption(String key, JsonNode value) {
    if ("content_type".equals(key) || "response_filter".equals(key)) {
      api.set(key, value);
    } else {
      super.setOption(key, value);
    }
  }

  @Override
  List<ToolIssue> validate() {
    List<ToolIssue> issues = new ArrayList<>();
    // An empty URL is as good as none: the API rejects it.
    String url = ToolWire.str(api.get("url"));
    if (url == null || url.isEmpty()) {
      url = null;
      issues.add(ToolIssue.error("missing_url", null, "Set the method and URL with get(), post(), put(), patch() or delete()"));
    }
    // As the API does: env var templates aren't placeholders, and the auth connection fills auth_resolved_params.
    List<String> placeholders = new ArrayList<>();
    Matcher matcher = PLACEHOLDER.matcher(ENV_VAR_TEMPLATE.matcher(url == null ? "" : url).replaceAll("env"));
    while (matcher.find()) {
      placeholders.add(matcher.group(1));
    }
    List<String> authResolved = ToolWire.strings(api.get("auth_resolved_params"));
    if (authResolved == null) {
      authResolved = new ArrayList<>();
    }
    for (String name : placeholders) {
      if (!pathParams.containsKey(name) && !authResolved.contains(name)) {
        issues.add(
          ToolIssue.error(
            "missing_path_param",
            "pathParams." + name,
            "The URL has {" + name + "} but no pathParam(\"" + name + "\")"
          )
        );
      }
    }
    for (Map.Entry<String, Schema> param : pathParams.entrySet()) {
      String name = param.getKey();
      Schema schema = param.getValue();
      String path = "pathParams." + name;
      if (url != null && !placeholders.contains(name)) {
        issues.add(ToolIssue.error("unknown_path_param", path, "pathParam(\"" + name + "\") has no {" + name + "} in the URL"));
      } else if (authResolved.contains(name)) {
        issues.add(
          ToolIssue.error("unknown_path_param", path, "{" + name + "} is filled by the auth connection, not a pathParam")
        );
      }
      if (!schema.getKind().isLiteral()) {
        issues.add(
          ToolIssue.error("param_not_literal", path, "Path param \"" + name + "\" must be a string, integer, number or bool")
        );
      }
      // A path param is always required (the URL needs it), whatever optional() says: the API schema has no
      // `required` list for path params to leave it out of.
      if (schema.state.omitted) {
        issues.add(
          ToolIssue.error("omitted_required", path, "Path param \"" + name + "\" can't use omit(): the URL always needs it")
        );
      }
      ToolValidation.checkSchema(schema, path, false, issues);
    }
    if (query != null && !(query instanceof ObjectSchema)) {
      issues.add(ToolIssue.error("body_not_object", "query", "The query params schema must be an object"));
    }
    if (query instanceof ObjectSchema) {
      for (Map.Entry<String, Schema> prop : query.state.properties.entrySet()) {
        String path = "query." + prop.getKey();
        if (!prop.getValue().getKind().isLiteral()) {
          issues.add(
            ToolIssue.error(
              "param_not_literal",
              path,
              "Query param \"" + prop.getKey() + "\" must be a string, integer, number or bool"
            )
          );
        }
        ToolValidation.checkSchema(prop.getValue(), path, true, issues);
      }
    }
    checkBody("body", body, issues);
    checkBody("response", response, issues);
    String method = ToolWire.str(api.get("method"));
    if ("GET".equals(method) && body != null) {
      issues.add(ToolIssue.error("body_not_allowed", "body", "A GET request can't have a body"));
    }
    if (("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) && body == null) {
      issues.add(ToolIssue.error("missing_body", "body", "A " + method + " request needs a body"));
    }

    Map<String, String> where = new HashMap<>();
    checkUnique("pathParams", new ArrayList<>(pathParams.keySet()), where, issues);
    checkUnique("query", propertyNames(query), where, issues);
    checkUnique("body", propertyNames(body), where, issues);

    Map<String, String> seen = new HashMap<>();
    for (String name : headers.keySet()) {
      String lower = name.toLowerCase(Locale.ROOT);
      if (seen.containsKey(lower)) {
        issues.add(
          ToolIssue.error(
            "duplicate_header",
            "headers." + name,
            "Headers \"" + seen.get(lower) + "\" and \"" + name + "\" differ only in case"
          )
        );
      }
      seen.put(lower, name);
    }

    JsonNode timeout = config.get("response_timeout_secs");
    if (timeout != null && timeout.isNumber()) {
      double secs = CanonicalJson.asDouble(timeout);
      if (secs < 5 || secs > 300) {
        issues.add(
          ToolIssue.error("invalid_option", "responseTimeoutSecs", "A webhook tool's responseTimeoutSecs must be 5-300")
        );
      }
    }
    JsonNode domains = config.get("follow_redirects_allowed_domains");
    if (ToolWire.isTrue(config.get("follow_redirects")) && (domains == null || !domains.isArray() || domains.size() == 0)) {
      issues.add(
        ToolIssue.error("invalid_option", "followRedirects", "followRedirects needs followRedirectsAllowedDomains")
      );
    }
    return issues;
  }

  @Override
  List<ToolIssue> modelIssues() {
    List<ToolIssue> issues = new ArrayList<>();
    for (String key : malformed) {
      issues.add(
        ToolIssue.error(
          "invalid_model",
          key,
          "The loaded " + key + " has the wrong shape, which the generated tool config model can't take. Fix it in"
            + " the source JSON"
        )
      );
    }
    for (Map.Entry<String, Schema> param : pathParams.entrySet()) {
      ToolValidation.checkModelShape(param.getValue(), "pathParams." + param.getKey(), issues);
    }
    if (query != null) {
      ToolValidation.checkModelShape(query, "query", issues);
    }
    if (body != null) {
      ToolValidation.checkModelShape(body, "body", issues);
    }
    if (response != null) {
      ToolValidation.checkModelShape(response, "response", issues);
    }
    return issues;
  }

  @Override
  ObjectNode toWire() {
    ObjectNode apiSchema = api.deepCopy();
    if (!pathParams.isEmpty()) {
      ObjectNode params = ToolWire.NODES.objectNode();
      for (Map.Entry<String, Schema> param : pathParams.entrySet()) {
        params.set(param.getKey(), param.getValue().toWire());
      }
      apiSchema.set("path_params_schema", params);
    }
    // The query params schema keeps every loaded field. Its wire form has no `type`, so the one the builder adds is
    // dropped unless the loaded schema declared it. Emptied by edits, it is left out: a `type` kept from the loaded
    // schema isn't content.
    if (query != null) {
      ObjectNode model = query.toWire();
      if (!queryTypeLoaded) {
        model.remove("type");
      }
      boolean content = false;
      Iterator<String> names = model.fieldNames();
      while (names.hasNext()) {
        if (!"type".equals(names.next())) {
          content = true;
        }
      }
      if (content || queryUntouched) {
        apiSchema.set("query_params_schema", model);
      }
    }
    if (body != null) {
      apiSchema.set("request_body_schema", body.toWire());
    }
    if (response != null) {
      apiSchema.set("response_body_schema", response.toWire());
    }
    if (!headers.isEmpty()) {
      ObjectNode headerNode = ToolWire.NODES.objectNode();
      for (Map.Entry<String, JsonNode> header : headers.entrySet()) {
        headerNode.set(header.getKey(), header.getValue().deepCopy());
      }
      apiSchema.set("request_headers", headerNode);
    }
    ObjectNode wire = config.deepCopy();
    wire.put("type", "webhook");
    if (!malformed.contains("api_schema")) {
      wire.set("api_schema", apiSchema);
    }
    return wire;
  }

  private static Schema loadContainer(ObjectNode api, String key, Set<String> malformed) {
    JsonNode node = api.get(key);
    if (node == null) {
      return null;
    }
    if (!node.isObject()) {
      malformed.add(key);
      return null;
    }
    api.remove(key);
    return Schema.fromModel((ObjectNode) node, false, Schema.containerKind((ObjectNode) node));
  }

  private static boolean allObjects(JsonNode node) {
    if (!node.isObject()) {
      return false;
    }
    for (JsonNode value : node) {
      if (!value.isObject()) {
        return false;
      }
    }
    return true;
  }

  private static List<String> propertyNames(Schema schema) {
    return schema instanceof ObjectSchema ? ((ObjectSchema) schema).getPropertyNames() : new ArrayList<String>();
  }

  private static void checkUnique(String location, List<String> names, Map<String, String> where, List<ToolIssue> issues) {
    for (String name : names) {
      if (where.containsKey(name)) {
        issues.add(
          ToolIssue.error(
            "duplicate_param",
            location + "." + name,
            "\"" + name + "\" is in both " + where.get(name) + " and " + location
              + "; param names must be unique across path, query and body"
          )
        );
      } else {
        where.put(name, location);
      }
    }
  }

  private static void checkBody(String label, Schema schema, List<ToolIssue> issues) {
    if (schema == null) {
      return;
    }
    if (schema.getKind() != SchemaKind.OBJECT) {
      issues.add(ToolIssue.error("body_not_object", label, "The " + label + " schema must be an object"));
    }
    ToolValidation.checkSchema(schema, label, false, issues);
  }

  private WebhookTool request(String method, String url) {
    api.put("method", method);
    api.put("url", url);
    return this;
  }
}
