package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.Destination;
import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.types.PhoneNumberTransferCustomSipHeadersItem;
import com.elevenlabs.api.types.PhoneNumberTransferPostDialDigits;
import com.elevenlabs.api.types.PreToolSpeechMode;
import com.elevenlabs.api.types.ResponseFilter;
import com.elevenlabs.api.types.SearchStrategy;
import com.elevenlabs.api.types.ToolCallSoundBehavior;
import com.elevenlabs.api.types.ToolCallSoundType;
import com.elevenlabs.api.types.ToolErrorHandlingMode;
import com.elevenlabs.api.types.ToolExecutionMode;
import com.elevenlabs.api.types.ToolInterruptionMode;
import com.elevenlabs.api.types.TransferTypeEnum;
import com.elevenlabs.api.types.UuiTransferConfig;
import com.elevenlabs.api.types.WebhookToolApiSchemaConfigInputContentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Interprets the language-neutral fixtures in elevenlabs-js {@code docs/agents-as-code/fixtures/tools} through the
 * Java builder. See {@code tool-spec.md}, "Fixtures". Mirrors the reference runner ({@code fixtureRunner.ts}): option
 * keys are canonical camelCase names and values are wire format, parsed into the generated types.
 */
final class ToolFixtureRunner {

  static final class Result {

    final Tool tool;
    final String json;
    final List<String> errors;

    Result(Tool tool, String json, List<String> errors) {
      this.tool = tool;
      this.json = json;
      this.errors = errors;
    }
  }

  private ToolFixtureRunner() {}

  static Result run(JsonNode fixture) {
    if (fixture.has("schema")) {
      return new Result(null, com.elevenlabs.api.agentworkflows.CanonicalJson.toCanonicalString(buildSchema(fixture.get("schema")).toJson()), null);
    }
    JsonNode spec = fixture.path("tool");
    Tool tool;
    if (fixture.has("from")) {
      tool = Tool.fromJson(fixture.get("from"));
    } else if (spec.has("webhook")) {
      tool = Tool.webhook(spec.get("webhook").get(0).asText(), spec.get("webhook").get(1).asText());
    } else if (spec.has("client")) {
      tool = Tool.client(spec.get("client").get(0).asText(), spec.get("client").get(1).asText());
    } else {
      tool = createSystem(spec.get("system").asText(), spec.path("systemOptions"));
    }
    for (JsonNode op : fixture.path("ops")) {
      call(tool, op.get("op").asText(), op.path("args"));
    }
    try {
      return new Result(tool, tool.toJsonString(), null);
    } catch (ToolValidationException e) {
      List<String> codes = new ArrayList<>();
      for (ToolIssue issue : e.getIssues()) {
        codes.add(issue.getCode());
      }
      return new Result(tool, null, codes);
    }
  }

  private static SystemTool createSystem(String type, JsonNode options) {
    switch (type) {
      case "end_call":
        return SystemTool.endCall();
      case "skip_turn": {
        SystemToolOptions.SkipTurn o = new SystemToolOptions.SkipTurn();
        if (options.has("waitTimeoutSecs")) {
          o.waitTimeoutSecs(options.get("waitTimeoutSecs").doubleValue());
        }
        return SystemTool.skipTurn(o);
      }
      case "language_detection": {
        SystemToolOptions.LanguageDetection o = new SystemToolOptions.LanguageDetection();
        if (options.has("onlyAtConversationStart")) {
          o.onlyAtConversationStart(options.get("onlyAtConversationStart").booleanValue());
        }
        return SystemTool.languageDetection(o);
      }
      case "play_keypad_touch_tone": {
        SystemToolOptions.PlayKeypadTouchTone o = new SystemToolOptions.PlayKeypadTouchTone();
        if (options.has("useOutOfBandDtmf")) {
          o.useOutOfBandDtmf(options.get("useOutOfBandDtmf").booleanValue());
        }
        if (options.has("suppressTurnAfterDtmf")) {
          o.suppressTurnAfterDtmf(options.get("suppressTurnAfterDtmf").booleanValue());
        }
        return SystemTool.playKeypadTouchTone(o);
      }
      case "voicemail_detection": {
        SystemToolOptions.VoicemailDetection o = new SystemToolOptions.VoicemailDetection();
        if (options.has("voicemailMessage")) {
          o.voicemailMessage(options.get("voicemailMessage").asText());
        }
        return SystemTool.voicemailDetection(o);
      }
      case "knowledge_base": {
        SystemToolOptions.KnowledgeBase o = new SystemToolOptions.KnowledgeBase();
        if (options.has("enabledStrategies")) {
          o.enabledStrategies(parse(options.get("enabledStrategies"), new TypeReference<List<SearchStrategy>>() {}));
        }
        return SystemTool.knowledgeBase(o);
      }
      case "transfer_to_number": {
        SystemToolOptions.TransferToNumber o = new SystemToolOptions.TransferToNumber();
        if (options.has("enableClientMessage")) {
          o.enableClientMessage(options.get("enableClientMessage").booleanValue());
        }
        return SystemTool.transferToNumber(o);
      }
      case "transfer_to_agent":
        return SystemTool.transferToAgent();
      default:
        throw new IllegalArgumentException("Unknown system tool \"" + type + "\"");
    }
  }

  private static void call(Tool tool, String op, JsonNode args) {
    switch (op) {
      case "get":
        webhook(tool, op).get(args.get(0).asText());
        break;
      case "post":
        webhook(tool, op).post(args.get(0).asText());
        break;
      case "put":
        webhook(tool, op).put(args.get(0).asText());
        break;
      case "patch":
        webhook(tool, op).patch(args.get(0).asText());
        break;
      case "delete":
        webhook(tool, op).delete(args.get(0).asText());
        break;
      case "pathParam":
        webhook(tool, op).pathParam(args.get(0).asText(), schemaArg(args.get(1)));
        break;
      case "queryParam":
        webhook(tool, op).queryParam(args.get(0).asText(), schemaArg(args.get(1)));
        break;
      case "body":
        webhook(tool, op).body(schemaArg(args.get(0)));
        break;
      case "response":
        webhook(tool, op).response(schemaArg(args.get(0)));
        break;
      case "header":
        webhook(tool, op).header(args.get(0).asText(), header(args.get(1)));
        break;
      case "auth": {
        JsonNode raw = args.get(0);
        webhook(tool, op).auth(raw.has("id") ? AuthConnection.id(raw.get("id").asText()) : AuthConnection.envVar(raw.get("envVar").asText()));
        break;
      }
      case "assign": {
        AssignOptions options = new AssignOptions();
        JsonNode raw = args.path(2);
        if (raw.has("sanitize")) {
          options.sanitize(raw.get("sanitize").booleanValue());
        }
        if (raw.has("preserveNativeType")) {
          options.preserveNativeType(raw.get("preserveNativeType").booleanValue());
        }
        webhook(tool, op).assign(args.get(0).asText(), args.get(1).asText(), options);
        break;
      }
      case "removePathParam":
        webhook(tool, op).removePathParam(args.get(0).asText());
        break;
      case "removeQueryParam":
        webhook(tool, op).removeQueryParam(args.get(0).asText());
        break;
      case "removeHeader":
        webhook(tool, op).removeHeader(args.get(0).asText());
        break;
      case "removeAssignment":
        webhook(tool, op).removeAssignment(args.get(0).asText());
        break;
      case "param":
        client(tool, op).param(args.get(0).asText(), schemaArg(args.get(1)));
        break;
      case "removeParam":
        client(tool, op).removeParam(args.get(0).asText());
        break;
      case "transfer":
        system(tool, op).transfer(args.get(0).asText(), destination(args.get(1)), transferOptions(args.path(2)));
        break;
      case "options":
        tool.options(toolOptions(args.get(0)));
        break;
      default:
        throw new IllegalArgumentException("Unknown fixture op \"" + op + "\"");
    }
  }

  private static WebhookTool webhook(Tool tool, String op) {
    return tool.asWebhook().orElseThrow(() -> new IllegalArgumentException("\"" + op + "\" needs a webhook tool"));
  }

  private static ClientTool client(Tool tool, String op) {
    return tool.asClient().orElseThrow(() -> new IllegalArgumentException("\"" + op + "\" needs a client tool"));
  }

  private static SystemTool system(Tool tool, String op) {
    return tool.asSystem().orElseThrow(() -> new IllegalArgumentException("\"" + op + "\" needs a system tool"));
  }

  private static ToolOptions toolOptions(JsonNode raw) {
    ToolOptions options = new ToolOptions();
    Iterator<Map.Entry<String, JsonNode>> fields = raw.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      JsonNode v = field.getValue();
      switch (field.getKey()) {
        case "name":
          options.name(v.asText());
          break;
        case "description":
          options.description(v.asText());
          break;
        case "responseTimeoutSecs":
          options.responseTimeoutSecs(v.intValue());
          break;
        case "interruptionMode":
          options.interruptionMode(parse(v, ToolInterruptionMode.class));
          break;
        case "preToolSpeech":
          options.preToolSpeech(parse(v, PreToolSpeechMode.class));
          break;
        case "toolCallSound":
          options.toolCallSound(parse(v, ToolCallSoundType.class));
          break;
        case "toolCallSoundBehavior":
          options.toolCallSoundBehavior(parse(v, ToolCallSoundBehavior.class));
          break;
        case "toolErrorHandlingMode":
          options.toolErrorHandlingMode(parse(v, ToolErrorHandlingMode.class));
          break;
        case "dynamicVariablePlaceholders":
          options.dynamicVariablePlaceholders(parse(v, new TypeReference<Map<String, Object>>() {}));
          break;
        case "executionMode":
          options.executionMode(parse(v, ToolExecutionMode.class));
          break;
        case "contentType":
          options.contentType(parse(v, WebhookToolApiSchemaConfigInputContentType.class));
          break;
        case "responseFilter":
          options.responseFilter(parse(v, ResponseFilter.class));
          break;
        case "followRedirects":
          options.followRedirects(v.booleanValue());
          break;
        case "followRedirectsAllowedDomains":
          options.followRedirectsAllowedDomains(parse(v, new TypeReference<List<String>>() {}));
          break;
        case "expectsResponse":
          options.expectsResponse(v.booleanValue());
          break;
        default:
          throw new IllegalArgumentException("Unknown option \"" + field.getKey() + "\"");
      }
    }
    return options;
  }

  private static ToolTransferOptions transferOptions(JsonNode raw) {
    ToolTransferOptions options = new ToolTransferOptions();
    Iterator<Map.Entry<String, JsonNode>> fields = raw.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      JsonNode v = field.getValue();
      switch (field.getKey()) {
        case "transferType":
          options.transferType(parse(v, TransferTypeEnum.class));
          break;
        case "customSipHeaders":
          options.customSipHeaders(parse(v, new TypeReference<List<PhoneNumberTransferCustomSipHeadersItem>>() {}));
          break;
        case "sipReferPlayDialtone":
          options.sipReferPlayDialtone(v.booleanValue());
          break;
        case "uui":
          options.uui(parse(v, UuiTransferConfig.class));
          break;
        case "postDialDigits":
          options.postDialDigits(parse(v, PhoneNumberTransferPostDialDigits.class));
          break;
        case "delayMs":
          options.delayMs(v.intValue());
          break;
        case "transferMessage":
          options.transferMessage(v.asText());
          break;
        case "enableTransferredAgentFirstMessage":
          options.enableTransferredAgentFirstMessage(v.booleanValue());
          break;
        case "preserveClientTtsOverrides":
          options.preserveClientTtsOverrides(v.booleanValue());
          break;
        case "preserveVoiceSettings":
          options.preserveVoiceSettings(v.booleanValue());
          break;
        default:
          throw new IllegalArgumentException("Unknown transfer option \"" + field.getKey() + "\"");
      }
    }
    return options;
  }

  // A schema argument is wire-format JSON, or {"build": [ops]} for one built with Schema methods (e.g. optional()).
  private static Schema schemaArg(JsonNode raw) {
    return raw.has("build") ? buildSchema(raw.get("build")) : Schema.fromJson(raw);
  }

  private static Schema buildSchema(JsonNode ops) {
    JsonNode first = ops.get(0);
    JsonNode firstArgs = first.path("args");
    Schema schema;
    switch (first.get("op").asText()) {
      case "string":
        schema = Schema.string();
        break;
      case "integer":
        schema = Schema.integer();
        break;
      case "number":
        schema = Schema.number();
        break;
      case "bool":
        schema = Schema.bool();
        break;
      case "object":
        schema = Schema.object();
        break;
      case "array":
        schema = Schema.array(schemaArg(firstArgs.get(0)));
        break;
      case "fromJSON":
        schema = Schema.fromJson(firstArgs.get(0));
        break;
      default:
        throw new IllegalArgumentException("Unknown schema constructor \"" + first.get("op").asText() + "\"");
    }
    for (int i = 1; i < ops.size(); i++) {
      JsonNode op = ops.get(i);
      JsonNode args = op.path("args");
      switch (op.get("op").asText()) {
        case "describe":
          schema = schema.describe(args.get(0).asText());
          break;
        case "fromVariable":
          schema = schema.fromVariable(args.get(0).asText());
          break;
        case "constant":
          schema = schema.constant(args.get(0));
          break;
        case "omit":
          schema = schema.omit();
          break;
        case "optional":
          schema = schema.optional();
          break;
        case "systemProvided":
          schema = ((LiteralSchema) schema).systemProvided();
          break;
        case "choices":
          schema = ((LiteralSchema) schema).choices(strings(args.get(0)));
          break;
        case "allowedFrom":
          schema = ((LiteralSchema) schema).allowedFrom(args.get(0).asText());
          break;
        case "property":
          schema = ((ObjectSchema) schema).property(args.get(0).asText(), schemaArg(args.get(1)));
          break;
        case "without":
          schema = ((ObjectSchema) schema).without(args.get(0).asText());
          break;
        case "requireAnyOf":
          schema = ((ObjectSchema) schema).requireAnyOf(strings(args.get(0)));
          break;
        case "requireAllOf":
          schema = ((ObjectSchema) schema).requireAllOf(strings(args.get(0)));
          break;
        default:
          throw new IllegalArgumentException("Unknown schema op \"" + op.get("op").asText() + "\"");
      }
    }
    return schema;
  }

  private static Header header(JsonNode raw) {
    if (raw.has("value")) {
      return Header.value(raw.get("value").asText());
    }
    if (raw.has("secret")) {
      return Header.secret(raw.get("secret").asText());
    }
    if (raw.has("variable")) {
      return Header.variable(raw.get("variable").asText());
    }
    return Header.envVar(raw.get("envVar").asText());
  }

  private static Destination destination(JsonNode raw) {
    if (raw.has("phone")) {
      return Destination.phone(raw.get("phone").asText());
    }
    if (raw.has("phoneVariable")) {
      return Destination.phoneVariable(raw.get("phoneVariable").asText());
    }
    if (raw.has("sip")) {
      return Destination.sip(raw.get("sip").asText());
    }
    if (raw.has("sipVariable")) {
      return Destination.sipVariable(raw.get("sipVariable").asText());
    }
    return Destination.agent(raw.get("agent").asText(), raw.has("nodeId") ? raw.get("nodeId").asText() : null);
  }

  private static List<String> strings(JsonNode raw) {
    List<String> out = new ArrayList<>();
    for (JsonNode item : raw) {
      out.add(item.asText());
    }
    return out;
  }

  private static <T> T parse(JsonNode value, Class<T> type) {
    try {
      return ObjectMappers.JSON_MAPPER.treeToValue(value, type);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static <T> T parse(JsonNode value, TypeReference<T> type) {
    return ObjectMappers.JSON_MAPPER.convertValue(value, type);
  }
}
