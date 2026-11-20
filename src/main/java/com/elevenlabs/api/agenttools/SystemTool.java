package com.elevenlabs.api.agenttools;

import com.elevenlabs.api.agentworkflows.Destination;
import com.elevenlabs.api.types.SystemToolConfigInput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A built-in tool the agent runs itself, such as ending the call or transferring it. Its name defaults to its
 * {@link #getSystemToolType() system tool type}, and its description is left out so the API uses its own; both can
 * be set with {@link #options(ToolOptions)}. Add it to an agent's {@code prompt.built_in_tools} with
 * {@link #buildBuiltInConfig()}.
 *
 * <pre>{@code
 * SystemTool transfer = SystemTool.transferToNumber()
 *   .transfer("Caller asks for billing", Destination.phone("+15551234567"))
 *   .transfer("Caller asks for a manager", Destination.sipVariable("manager_sip"),
 *     new ToolTransferOptions().transferType(TransferTypeEnum.SIP_REFER));
 * }</pre>
 */
public final class SystemTool extends Tool {

  private static final Set<String> ALLOWED = withCommonOptions();

  private static final List<String> PHONE_TRANSFER_OPTIONS = Arrays.asList(
    "transfer_type",
    "custom_sip_headers",
    "sip_refer_play_dialtone",
    "uui",
    "post_dial_digits"
  );

  private static final List<String> AGENT_TRANSFER_OPTIONS = Arrays.asList(
    "delay_ms",
    "transfer_message",
    "enable_transferred_agent_first_message",
    "preserve_client_tts_overrides",
    "preserve_voice_settings"
  );

  private final String systemToolType;

  /** The {@code params} fields other than {@code system_tool_type}, in wire format. */
  private final ObjectNode params;

  /**
   * Whether the loaded {@code params} were missing, not an object or without a {@code system_tool_type}. They are
   * then kept in the config as loaded, and {@code build()} reports them.
   */
  private final boolean malformedParams;

  private SystemTool(String systemToolType, ObjectNode config, ObjectNode params, boolean malformedParams) {
    super(config);
    this.systemToolType = systemToolType;
    this.params = params;
    this.malformedParams = malformedParams;
  }

  static SystemTool load(ObjectNode config) {
    JsonNode params = config.get("params");
    String type = params != null && params.isObject() ? ToolWire.str(params.get("system_tool_type")) : null;
    if (type == null || type.isEmpty()) {
      // The generated params union's catch-all would accept these, so they're kept as loaded and reported.
      return new SystemTool("", config, ToolWire.NODES.objectNode(), true);
    }
    ObjectNode rest = ((ObjectNode) params).deepCopy();
    rest.remove("system_tool_type");
    config.remove("params");
    return new SystemTool(type, config, rest, false);
  }

  /** Lets the agent end the call. */
  public static SystemTool endCall() {
    return create("end_call", null);
  }

  /** Lets the agent stay quiet when the user asks for a moment. */
  public static SystemTool skipTurn() {
    return skipTurn(new SystemToolOptions.SkipTurn());
  }

  /** {@link #skipTurn()} with params. */
  public static SystemTool skipTurn(SystemToolOptions.SkipTurn options) {
    return create("skip_turn", options);
  }

  /** Lets the agent switch language. */
  public static SystemTool languageDetection() {
    return languageDetection(new SystemToolOptions.LanguageDetection());
  }

  /** {@link #languageDetection()} with params. */
  public static SystemTool languageDetection(SystemToolOptions.LanguageDetection options) {
    return create("language_detection", options);
  }

  /** Lets the agent play keypad tones, for phone menus. */
  public static SystemTool playKeypadTouchTone() {
    return playKeypadTouchTone(new SystemToolOptions.PlayKeypadTouchTone());
  }

  /** {@link #playKeypadTouchTone()} with params. */
  public static SystemTool playKeypadTouchTone(SystemToolOptions.PlayKeypadTouchTone options) {
    return create("play_keypad_touch_tone", options);
  }

  /** Lets the agent detect voicemail and leave a message or hang up. */
  public static SystemTool voicemailDetection() {
    return voicemailDetection(new SystemToolOptions.VoicemailDetection());
  }

  /** {@link #voicemailDetection()} with params. */
  public static SystemTool voicemailDetection(SystemToolOptions.VoicemailDetection options) {
    return create("voicemail_detection", options);
  }

  /** Lets the agent search its knowledge base. */
  public static SystemTool knowledgeBase() {
    return knowledgeBase(new SystemToolOptions.KnowledgeBase());
  }

  /** {@link #knowledgeBase()} with params. */
  public static SystemTool knowledgeBase(SystemToolOptions.KnowledgeBase options) {
    return create("knowledge_base", options);
  }

  /** Lets the agent transfer the call to a phone number or SIP URI. Add destinations with {@code transfer()}. */
  public static SystemTool transferToNumber() {
    return transferToNumber(new SystemToolOptions.TransferToNumber());
  }

  /** {@link #transferToNumber()} with params. */
  public static SystemTool transferToNumber(SystemToolOptions.TransferToNumber options) {
    SystemTool tool = create("transfer_to_number", options);
    tool.params.set("transfers", ToolWire.NODES.arrayNode());
    return tool;
  }

  /** Lets the agent hand the conversation to another agent. Add destinations with {@code transfer()}. */
  public static SystemTool transferToAgent() {
    SystemTool tool = create("transfer_to_agent", null);
    tool.params.set("transfers", ToolWire.NODES.arrayNode());
    return tool;
  }

  @Override
  public ToolKind getKind() {
    return ToolKind.SYSTEM;
  }

  /** The API's {@code system_tool_type}, such as {@code "end_call"}. Empty for loaded params of the wrong shape. */
  public String getSystemToolType() {
    return systemToolType;
  }

  /** {@link #transfer(String, Destination, ToolTransferOptions)} without options. */
  public SystemTool transfer(String condition, Destination destination) {
    return transfer(condition, destination, new ToolTransferOptions());
  }

  /**
   * Adds a destination to a transfer tool. {@code condition} is plain text telling the LLM when to transfer there.
   * {@code transferToNumber()} takes phone and SIP destinations, {@code transferToAgent()} agent destinations. A SIP
   * URI destination (static or from a dynamic variable) needs {@code transferType(TransferTypeEnum.SIP_REFER)}.
   */
  public SystemTool transfer(String condition, Destination destination, ToolTransferOptions options) {
    boolean toNumber = "transfer_to_number".equals(systemToolType);
    if (!toNumber && !"transfer_to_agent".equals(systemToolType)) {
      issues.add(
        ToolIssue.error("invalid_option", "transfer", "transfer() only applies to transfer tools, not " + systemToolType)
      );
      return this;
    }
    if (destination.isPhoneOrSip() != toNumber) {
      issues.add(
        ToolIssue.error(
          "invalid_destination",
          "transfer",
          toNumber
            ? "transferToNumber() needs a phone or SIP destination, not an agent (\"" + condition + "\")"
            : "transferToAgent() needs an agent destination (\"" + condition + "\")"
        )
      );
      return this;
    }
    List<String> allowed = toNumber ? PHONE_TRANSFER_OPTIONS : AGENT_TRANSFER_OPTIONS;
    ObjectNode transfer = destination.toWire();
    transfer.remove("type");
    List<Map.Entry<String, JsonNode>> settings = new ArrayList<>();
    for (Map.Entry<String, JsonNode> entry : (options == null ? new ToolTransferOptions() : options).toWire().entrySet()) {
      if (allowed.contains(entry.getKey())) {
        settings.add(entry);
      } else {
        String name = ToolWire.camelCase(entry.getKey());
        issues.add(
          ToolIssue.error("invalid_option", name, "Option \"" + name + "\" doesn't apply to " + systemToolType)
        );
      }
    }
    // A SIP URI held in a dynamic variable resolves to a SIP URI too, so it also needs SIP REFER.
    String destinationType = ToolWire.str(transfer.path("transfer_destination").get("type"));
    boolean sipUri = "sip_uri".equals(destinationType) || "sip_uri_dynamic_variable".equals(destinationType);
    boolean sipRefer = false;
    for (Map.Entry<String, JsonNode> setting : settings) {
      if ("transfer_type".equals(setting.getKey()) && "sip_refer".equals(ToolWire.str(setting.getValue()))) {
        sipRefer = true;
      }
    }
    if (sipUri && !sipRefer) {
      issues.add(
        ToolIssue.error(
          "invalid_destination",
          "transfer",
          "A SIP URI destination needs transferType(TransferTypeEnum.SIP_REFER) (\"" + condition + "\")"
        )
      );
      return this;
    }
    for (Map.Entry<String, JsonNode> setting : settings) {
      transfer.set(setting.getKey(), setting.getValue());
    }
    transfer.put("condition", condition);
    JsonNode existing = params.get("transfers");
    ArrayNode transfers = existing != null && existing.isArray() ? (ArrayNode) existing : ToolWire.NODES.arrayNode();
    transfers.add(transfer);
    params.set("transfers", transfers);
    return this;
  }

  /** Merges the options every tool accepts. An option that doesn't apply is reported by {@code build()}. */
  @Override
  public SystemTool options(ToolOptions options) {
    applyOptions(options);
    return this;
  }

  /**
   * Validates the tool and returns its entry for the agent's {@code prompt.built_in_tools}, such as
   * {@code BuiltInToolsInput.builder().endCall(SystemTool.endCall().buildBuiltInConfig())}.
   *
   * @throws ToolValidationException listing every problem, if the tool is invalid
   */
  public SystemToolConfigInput buildBuiltInConfig() {
    return buildBuiltInConfig(null);
  }

  /** {@link #buildBuiltInConfig()}, calling {@code onWarning} once per warning. */
  public SystemToolConfigInput buildBuiltInConfig(Consumer<ToolIssue> onWarning) {
    return buildConfig(onWarning)
      .getSystem()
      .orElseThrow(() -> new IllegalStateException("A system tool built a config of another type"));
  }

  @Override
  Collection<String> allowedOptions() {
    return ALLOWED;
  }

  @Override
  List<ToolIssue> validate() {
    List<ToolIssue> issues = new ArrayList<>();
    boolean isTransfer = "transfer_to_number".equals(systemToolType) || "transfer_to_agent".equals(systemToolType);
    if (!isTransfer) {
      return issues;
    }
    // A loaded transfer_to_number tool may use transfer_rules instead of transfers; the API takes exactly one.
    boolean hasTransfers = hasEntries(params.get("transfers"));
    boolean hasRules = hasEntries(params.get("transfer_rules"));
    if (!hasTransfers && !hasRules) {
      issues.add(ToolIssue.error("no_transfers", "transfer", systemToolType + " needs at least one transfer()"));
    } else if (hasTransfers && hasRules) {
      issues.add(
        ToolIssue.error(
          "conflicting_transfers",
          "transfer",
          systemToolType + " has both transfers and transfer_rules; the API takes only one"
        )
      );
    }
    return issues;
  }

  private static boolean hasEntries(JsonNode node) {
    return node != null && node.isArray() && node.size() > 0;
  }

  @Override
  List<ToolIssue> modelIssues() {
    List<ToolIssue> issues = new ArrayList<>();
    if (malformedParams) {
      issues.add(
        ToolIssue.error(
          "invalid_model",
          "params",
          "The loaded params aren't an object with a system_tool_type, so the tool doesn't match the generated tool"
            + " config model. Fix it in the source JSON"
        )
      );
    }
    return issues;
  }

  @Override
  ObjectNode toWire() {
    ObjectNode wire = config.deepCopy();
    wire.put("type", "system");
    if (!malformedParams) {
      ObjectNode systemParams = ToolWire.NODES.objectNode();
      systemParams.put("system_tool_type", systemToolType);
      Iterator<Map.Entry<String, JsonNode>> fields = params.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> field = fields.next();
        systemParams.set(field.getKey(), field.getValue().deepCopy());
      }
      wire.set("params", systemParams);
    }
    return wire;
  }

  private static SystemTool create(String systemToolType, SystemToolOptions.Params options) {
    ObjectNode config = ToolWire.NODES.objectNode();
    config.put("name", systemToolType);
    ObjectNode params = ToolWire.NODES.objectNode();
    if (options != null) {
      for (Map.Entry<String, JsonNode> entry : options.toWire().entrySet()) {
        params.set(entry.getKey(), entry.getValue());
      }
    }
    return new SystemTool(systemToolType, config, params, false);
  }
}
