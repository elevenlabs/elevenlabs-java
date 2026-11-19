package com.elevenlabs.api.agentworkflows;

import com.elevenlabs.api.core.ObjectMappers;
import com.elevenlabs.api.types.AstNodeInput;
import com.elevenlabs.api.types.ConversationalConfigApiModelWorkflowOverrideInput;
import com.elevenlabs.api.types.EntryBehavior;
import com.elevenlabs.api.types.KnowledgeBaseLocator;
import com.elevenlabs.api.types.PositionInput;
import com.elevenlabs.api.types.TransferTypeEnum;
import com.elevenlabs.api.types.UuiTransferConfig;
import com.elevenlabs.api.types.WorkflowPhoneNumberNodeModelInputCustomSipHeadersItem;
import com.elevenlabs.api.types.WorkflowPhoneNumberNodeModelInputPostDialDigits;
import com.elevenlabs.api.types.WorkflowToolLocatorSchemaOverridesValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Interprets the language-neutral fixtures in elevenlabs-js {@code docs/agents-as-code/fixtures} through the Java
 * builder. See {@code workflow-spec.md}, "Fixtures". Mirrors the reference runner ({@code fixtureRunner.ts}):
 * option keys are canonical camelCase names and nested values are wire format, parsed into the generated types.
 */
final class WorkflowFixtureRunner {

  static final class Result {

    final Workflow workflow;
    final String json;
    final List<String> errors;

    Result(Workflow workflow, String json, List<String> errors) {
      this.workflow = workflow;
      this.json = json;
      this.errors = errors;
    }
  }

  private WorkflowFixtureRunner() {}

  static Result run(JsonNode fixture) {
    Workflow wf;
    if (fixture.has("from")) {
      wf = Workflow.fromJson(fixture.get("from"));
    } else {
      JsonNode psl = fixture.path("workflow").get("preventSubagentLoops");
      wf = new Workflow(new WorkflowOptions().preventSubagentLoops(psl == null ? null : psl.booleanValue()));
    }
    runOps(wf, fixture.path("ops"), wf);
    try {
      return new Result(wf, wf.toJsonString(), null);
    } catch (WorkflowValidationException e) {
      List<String> codes = new ArrayList<>();
      for (WorkflowIssue issue : e.getIssues()) {
        codes.add(issue.getCode());
      }
      return new Result(wf, null, codes);
    }
  }

  private static WorkflowNode runOps(Workflow wf, JsonNode ops, WorkflowChain first) {
    WorkflowChain previous = first;
    WorkflowNode result = null;
    for (JsonNode op : ops) {
      String kind = op.get("op").asText();
      switch (kind) {
        case "update":
          wf.node(op.get("node").asText()).update(parseUpdate(op.get("options")));
          continue;
        case "remove":
          wf.remove(op.get("node").asText());
          continue;
        case "onResult": {
          WorkflowNode node = op.has("at") ? wf.node(op.get("at").asText()) : (WorkflowNode) previous;
          ResultBranches branches = new ResultBranches();
          // Keep the fixture's key order, so a failure-first fixture reaches the builder failure-first.
          Iterator<String> keys = op.fieldNames();
          while (keys.hasNext()) {
            String key = keys.next();
            JsonNode branchOps = op.get(key);
            if (key.equals("success")) {
              branches.success(branch -> runOps(wf, branchOps, branch));
            } else if (key.equals("failure")) {
              branches.failure(branch -> runOps(wf, branchOps, branch));
            }
          }
          node.onResult(branches);
          continue;
        }
        case "insertBetween": {
          JsonNode inner = op.path("ops");
          wf.insertBetween(op.get("source").asText(), op.get("target").asText(), from -> runOps(wf, inner, from));
          continue;
        }
        default:
          break;
      }

      WorkflowChain at = !op.has("at")
        ? previous
        : "$start".equals(op.get("at").asText()) ? wf : wf.node(op.get("at").asText());

      if (op.has("when")) {
        Condition back = op.has("back") ? condition(op.get("back")) : null;
        Condition when = condition(op.get("when"));
        at = at instanceof Workflow ? ((Workflow) at).when(when, back) : ((WorkflowNode) at).when(when, back);
      } else if (op.path("onSuccess").asBoolean(false)) {
        at = ((WorkflowNode) at).onSuccess();
      } else if (op.path("onFailure").asBoolean(false)) {
        at = ((WorkflowNode) at).onFailure();
      }

      JsonNode options = op.get("options");
      switch (kind) {
        case "converse":
          result = at.converse(op.get("id").asText(), op.get("prompt").asText(), parseConverse(options));
          break;
        case "tool":
          result = at.tool(op.get("id").asText(), strings(op.get("toolIds")), parseTool(options));
          break;
        case "transfer":
          result = at.transfer(op.get("id").asText(), destination(op.get("destination")), parseTransfer(options));
          break;
        case "end":
          result = at.end(parseEnd(options));
          break;
        case "goTo":
          result = at.goTo(op.get("target").asText(), op.has("back") ? condition(op.get("back")) : null);
          break;
        default:
          throw new IllegalArgumentException("Unknown fixture op " + kind);
      }
      previous = result;
    }
    return result;
  }

  private static Condition condition(JsonNode raw) {
    if (raw.isTextual()) {
      return Condition.llm(raw.textValue());
    }
    String label = raw.has("label") ? raw.get("label").asText() : null;
    if (raw.has("llm")) {
      return Condition.llm(raw.get("llm").asText(), label);
    }
    // Through the generated AST model, as the reference runner does.
    return Condition.expr(Expr.fromModel(typed(raw.get("expr"), AstNodeInput.class)), label);
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

  private static ConverseOptions parseConverse(JsonNode o) {
    ConverseOptions opt = new ConverseOptions();
    if (o == null) {
      return opt;
    }
    opt.position(typed(o.get("position"), PositionInput.class));
    opt.label(text(o.get("label")));
    opt.toolIds(strings(o.get("toolIds")));
    opt.knowledgeBase(typed(o.get("knowledgeBase"), new TypeReference<List<KnowledgeBaseLocator>>() {}));
    opt.conversationConfig(typed(o.get("conversationConfig"), ConversationalConfigApiModelWorkflowOverrideInput.class));
    opt.entryBehavior(o.has("entryBehavior") ? EntryBehavior.valueOf(o.get("entryBehavior").asText()) : null);
    return opt;
  }

  private static ToolOptions parseTool(JsonNode o) {
    ToolOptions opt = new ToolOptions();
    if (o == null) {
      return opt;
    }
    opt.position(typed(o.get("position"), PositionInput.class));
    opt.schemaOverrides(schemaOverrides(o.get("schemaOverrides")));
    return opt;
  }

  private static TransferOptions parseTransfer(JsonNode o) {
    TransferOptions opt = new TransferOptions();
    if (o == null) {
      return opt;
    }
    opt.position(typed(o.get("position"), PositionInput.class));
    opt.transferType(o.has("transferType") ? TransferTypeEnum.valueOf(o.get("transferType").asText()) : null);
    opt.customSipHeaders(
      typed(o.get("customSipHeaders"), new TypeReference<List<WorkflowPhoneNumberNodeModelInputCustomSipHeadersItem>>() {})
    );
    opt.sipReferPlayDialtone(bool(o.get("sipReferPlayDialtone")));
    opt.uui(typed(o.get("uui"), UuiTransferConfig.class));
    opt.postDialDigits(typed(o.get("postDialDigits"), WorkflowPhoneNumberNodeModelInputPostDialDigits.class));
    opt.delayMs(o.has("delayMs") ? o.get("delayMs").intValue() : null);
    opt.transferMessage(text(o.get("transferMessage")));
    opt.enableTransferredAgentFirstMessage(bool(o.get("enableTransferredAgentFirstMessage")));
    opt.preserveClientTtsOverrides(bool(o.get("preserveClientTtsOverrides")));
    opt.preserveVoiceSettings(bool(o.get("preserveVoiceSettings")));
    return opt;
  }

  private static EndOptions parseEnd(JsonNode o) {
    EndOptions opt = new EndOptions();
    if (o == null) {
      return opt;
    }
    opt.position(typed(o.get("position"), PositionInput.class));
    opt.id(text(o.get("id")));
    return opt;
  }

  private static NodeUpdate parseUpdate(JsonNode o) {
    NodeUpdate opt = new NodeUpdate();
    if (o == null) {
      return opt;
    }
    opt.prompt(text(o.get("prompt")));
    opt.toolIds(strings(o.get("toolIds")));
    opt.destination(o.has("destination") ? destination(o.get("destination")) : null);
    opt.position(typed(o.get("position"), PositionInput.class));
    opt.label(text(o.get("label")));
    opt.knowledgeBase(typed(o.get("knowledgeBase"), new TypeReference<List<KnowledgeBaseLocator>>() {}));
    opt.conversationConfig(typed(o.get("conversationConfig"), ConversationalConfigApiModelWorkflowOverrideInput.class));
    opt.entryBehavior(o.has("entryBehavior") ? EntryBehavior.valueOf(o.get("entryBehavior").asText()) : null);
    opt.schemaOverrides(schemaOverrides(o.get("schemaOverrides")));
    opt.transferType(o.has("transferType") ? TransferTypeEnum.valueOf(o.get("transferType").asText()) : null);
    opt.customSipHeaders(
      typed(o.get("customSipHeaders"), new TypeReference<List<WorkflowPhoneNumberNodeModelInputCustomSipHeadersItem>>() {})
    );
    opt.sipReferPlayDialtone(bool(o.get("sipReferPlayDialtone")));
    opt.uui(typed(o.get("uui"), UuiTransferConfig.class));
    opt.postDialDigits(typed(o.get("postDialDigits"), WorkflowPhoneNumberNodeModelInputPostDialDigits.class));
    opt.delayMs(o.has("delayMs") ? o.get("delayMs").intValue() : null);
    opt.transferMessage(text(o.get("transferMessage")));
    opt.enableTransferredAgentFirstMessage(bool(o.get("enableTransferredAgentFirstMessage")));
    opt.preserveClientTtsOverrides(bool(o.get("preserveClientTtsOverrides")));
    opt.preserveVoiceSettings(bool(o.get("preserveVoiceSettings")));
    return opt;
  }

  private static Map<String, Map<String, WorkflowToolLocatorSchemaOverridesValue>> schemaOverrides(JsonNode raw) {
    if (raw == null) {
      return null;
    }
    Map<String, Map<String, WorkflowToolLocatorSchemaOverridesValue>> out = new LinkedHashMap<>();
    Iterator<Map.Entry<String, JsonNode>> tools = raw.fields();
    while (tools.hasNext()) {
      Map.Entry<String, JsonNode> tool = tools.next();
      Map<String, WorkflowToolLocatorSchemaOverridesValue> paths = new LinkedHashMap<>();
      Iterator<Map.Entry<String, JsonNode>> it = tool.getValue().fields();
      while (it.hasNext()) {
        Map.Entry<String, JsonNode> path = it.next();
        paths.put(path.getKey(), typed(path.getValue(), WorkflowToolLocatorSchemaOverridesValue.class));
      }
      out.put(tool.getKey(), paths);
    }
    return out;
  }

  private static <T> T typed(JsonNode raw, Class<T> type) {
    if (raw == null) {
      return null;
    }
    try {
      return ObjectMappers.JSON_MAPPER.treeToValue(raw, type);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static <T> T typed(JsonNode raw, TypeReference<T> type) {
    if (raw == null) {
      return null;
    }
    return ObjectMappers.JSON_MAPPER.convertValue(raw, type);
  }

  private static List<String> strings(JsonNode raw) {
    if (raw == null) {
      return null;
    }
    List<String> out = new ArrayList<>();
    for (JsonNode item : raw) {
      out.add(item.asText());
    }
    return out;
  }

  private static String text(JsonNode raw) {
    return raw == null ? null : raw.asText();
  }

  private static Boolean bool(JsonNode raw) {
    return raw == null ? null : raw.booleanValue();
  }
}
