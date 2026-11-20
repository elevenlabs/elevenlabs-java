package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * An agents-as-code project in the ElevenLabs CLI's layout: {@code agents.json} and {@code tools.json} list the agents
 * and tools with their ids, and each one's config sits in {@code agent_configs/} or {@code tool_configs/} in API
 * format. A project made by {@code elevenlabs agents init} / {@code pull} works here, and one written here works with
 * the CLI.
 *
 * <p>Pass it to {@code agents().push(agent, new AgentPushOptions().project(project))} and
 * {@code agents().pull(agentId, new AgentPullOptions().project(project))}: the project records which agent and tools
 * on the server each definition is, so pushing again updates them instead of making copies.
 *
 * <pre>{@code
 * AgentProject project = AgentProject.open(Paths.get("."));
 * Agent agent = project.agent("Support line");
 * agent.update(new AgentOptions().prompt("..."));
 * client.agents().push(agent, new AgentPushOptions().project(project));
 * }</pre>
 *
 * <p>Files are written only by push and pull, once they know every id. A file nothing changed keeps its exact text.
 * Not supported yet: YAML and TOML configs, {@code tests.json}, and creating branches.
 */
public final class AgentProject {

  static final String AGENTS_FILE = "agents.json";
  static final String TOOLS_FILE = "tools.json";
  static final String AGENT_CONFIGS_DIR = "agent_configs";
  static final String TOOL_CONFIGS_DIR = "tool_configs";

  /** One entry in {@code agents.json}. {@code branches} is kept as loaded. */
  static final class AgentEntry {

    String config;
    String id;
    String branchId;
    String versionId;
    ObjectNode branches;
  }

  /** One entry in {@code tools.json}. */
  static final class ToolEntry {

    String type;
    String config;
    String id;
  }

  static final class AgentSlot {

    final AgentEntry entry;
    Agent agent;

    AgentSlot(AgentEntry entry, Agent agent) {
      this.entry = entry;
      this.agent = agent;
    }
  }

  static final class ToolSlot {

    final ToolEntry entry;
    Tool tool;
    /** The config as loaded or last sent, in API format, sent as it is while the tool is unchanged. */
    JsonNode raw;
    /** The tool's definition when {@code raw} was recorded, to tell whether it was edited since. */
    String baseline;

    ToolSlot(ToolEntry entry, Tool tool) {
      this.entry = entry;
      this.tool = tool;
    }
  }

  private final Path dir;
  private final Map<String, String> files = new LinkedHashMap<>();
  private final List<AgentSlot> agents = new ArrayList<>();
  private final List<ToolSlot> tools = new ArrayList<>();
  /** Paths written since loading or saving, with their new contents. */
  private final Map<String, String> changed = new LinkedHashMap<>();
  /** Paths in use, on disk or in the project, so new files don't overwrite one the project doesn't list. */
  private final Set<String> taken = new HashSet<>();

  private AgentProject(Map<String, String> files, Path dir, List<String> taken) {
    this.dir = dir;
    for (Map.Entry<String, String> file : files.entrySet()) {
      this.files.put(ProjectFormat.normalizePath(file.getKey()), file.getValue());
    }
    this.taken.addAll(this.files.keySet());
    for (String path : taken) {
      this.taken.add(ProjectFormat.normalizePath(path));
    }
    load();
  }

  /**
   * A project from file contents, keyed by path relative to the project root. No I/O: only {@code agents.json},
   * {@code tools.json} and the config files they list are read. Missing index files mean an empty project.
   *
   * @throws AgentProjectException if a file is missing, malformed or in a format the SDK can't read yet
   */
  public static AgentProject fromFiles(Map<String, String> files) {
    return new AgentProject(files == null ? Collections.<String, String>emptyMap() : files, null, Collections.<String>emptyList());
  }

  /**
   * Reads the project in {@code dir}. A directory without {@code agents.json} and {@code tools.json} opens as an
   * empty project, and {@link #save()} creates them.
   *
   * @throws AgentProjectException if a file is malformed, or a config path leads outside the project
   * @throws java.io.UncheckedIOException if a file can't be read
   */
  public static AgentProject open(Path dir) {
    ProjectFiles.Contents contents = ProjectFiles.read(dir);
    return new AgentProject(contents.files, dir, contents.taken);
  }

  /** {@link #open(Path)} for a path given as a string. */
  public static AgentProject open(String dir) {
    return open(Paths.get(dir));
  }

  /** The directory the project was opened from, if any. {@link #save()} writes there. */
  public Optional<Path> getDir() {
    return Optional.ofNullable(dir);
  }

  /** Every agent in the project, in {@code agents.json} order. */
  public List<Agent> agents() {
    List<Agent> result = new ArrayList<>();
    for (AgentSlot slot : agents) {
      result.add(slot.agent);
    }
    return result;
  }

  /**
   * The agent named {@code name}. The same object every time, so edits to it are what {@code agents().push()} with
   * this project sends.
   *
   * @throws AgentProjectException if no agent, or more than one, has that name
   */
  public Agent agent(String name) {
    List<AgentSlot> matches = new ArrayList<>();
    for (AgentSlot slot : agents) {
      if (slot.agent.getName().equals(name)) {
        matches.add(slot);
      }
    }
    if (matches.size() != 1) {
      throw new AgentProjectException(
        matches.isEmpty()
          ? "No agent named \"" + name + "\" in " + AGENTS_FILE
          : matches.size() + " agents in " + AGENTS_FILE + " are named \"" + name + "\""
      );
    }
    return matches.get(0).agent;
  }

  /** Every tool in {@code tools.json}, in order. */
  public List<Tool> tools() {
    List<Tool> result = new ArrayList<>();
    for (ToolSlot slot : tools) {
      result.add(slot.tool);
    }
    return result;
  }

  /**
   * The id {@code agents.json} records for this agent, if it was pushed or pulled: the entry the agent was loaded
   * from, or else the only entry with its name.
   *
   * @throws AgentProjectException if several entries have the agent's name
   */
  public Optional<String> agentId(Agent agent) {
    AgentSlot slot = agentSlot(agent);
    return slot == null ? Optional.<String>empty() : Optional.ofNullable(slot.entry.id);
  }

  /**
   * The id {@code tools.json} records for this tool, if it was pushed or pulled: the entry the tool was loaded from,
   * or else the only entry with its name.
   *
   * @throws AgentProjectException if several entries have the tool's name
   */
  public Optional<String> toolId(Tool tool) {
    ToolSlot slot = toolSlot(tool);
    return slot == null ? Optional.<String>empty() : Optional.ofNullable(slot.entry.id);
  }

  /**
   * The project's files, keyed by path: the index files and every config they list (branch configs included), with
   * what push and pull wrote since loading. Files nothing changed keep their exact text.
   */
  public Map<String, String> toFiles() {
    Map<String, String> result = new LinkedHashMap<>();
    result.put(AGENTS_FILE, current(AGENTS_FILE, agentsIndex()));
    result.put(TOOLS_FILE, current(TOOLS_FILE, toolsIndex()));
    List<String> paths = new ArrayList<>();
    for (AgentSlot slot : agents) {
      paths.add(slot.entry.config);
    }
    for (AgentSlot slot : agents) {
      if (slot.entry.branches != null) {
        for (JsonNode branch : slot.entry.branches) {
          JsonNode config = branch.get("config");
          if (config != null && config.isTextual()) {
            paths.add(config.textValue());
          }
        }
      }
    }
    for (ToolSlot slot : tools) {
      paths.add(slot.entry.config);
    }
    for (String path : paths) {
      String text = current(ProjectFormat.normalizePath(path), null);
      if (text != null) {
        result.put(path, text);
      }
    }
    return result;
  }

  /** The paths push and pull changed since the project was loaded or last saved. */
  public List<String> getChangedFiles() {
    return new ArrayList<>(changed.keySet());
  }

  /**
   * Writes the files push and pull changed to the project's directory, owner-only ({@code 0600}), never through a
   * symlink and never outside the project. {@code agents().push()} and {@code pull()} call this themselves for a
   * project opened with {@link #open(Path)}.
   *
   * @return the paths written
   * @throws AgentProjectException if the project wasn't opened from a directory, or a path is unsafe to write
   * @throws java.io.UncheckedIOException if a file can't be written
   */
  public List<String> save() {
    if (dir == null) {
      throw new AgentProjectException("This project wasn't opened from a directory; use toFiles() instead");
    }
    List<Map.Entry<String, String>> written = new ArrayList<>();
    for (Map.Entry<String, String> file : changed.entrySet()) {
      written.add(new AbstractMap.SimpleImmutableEntry<>(file.getKey(), file.getValue()));
    }
    if (written.isEmpty()) {
      return new ArrayList<>();
    }
    ProjectFiles.write(dir, written);
    List<String> paths = new ArrayList<>();
    for (Map.Entry<String, String> file : written) {
      files.put(file.getKey(), file.getValue());
      paths.add(file.getKey());
    }
    changed.clear();
    return paths;
  }

  // ---- Used by push and pull -------------------------------------------------------------------------------------

  /** The entry for an agent: the one it was loaded from, else the only one with its name. */
  AgentSlot agentSlot(Agent agent) {
    List<AgentSlot> named = new ArrayList<>();
    for (AgentSlot slot : agents) {
      if (slot.agent == agent) {
        return slot;
      }
      if (slot.agent.getName().equals(agent.getName())) {
        named.add(slot);
      }
    }
    return single(named, AGENTS_FILE, agent.getName());
  }

  /** The entry for a tool: the one it was loaded from, else the only one with its name. */
  ToolSlot toolSlot(Tool tool) {
    List<ToolSlot> named = new ArrayList<>();
    for (ToolSlot slot : tools) {
      if (slot.tool == tool) {
        return slot;
      }
      if (slot.tool.getName().equals(tool.getName())) {
        named.add(slot);
      }
    }
    return single(named, TOOLS_FILE, tool.getName());
  }

  /**
   * The recorded config to send for a tool whose definition matches its project entry (the loaded object, or one
   * rebuilt in code with the same name), so fields this SDK doesn't model survive.
   */
  JsonNode unchangedRaw(Tool tool) {
    ToolSlot slot = toolSlot(tool);
    if (slot == null || slot.raw == null || slot.baseline == null) {
      return null;
    }
    return slot.baseline.equals(ToolDefinition.of(tool)) ? slot.raw : null;
  }

  /**
   * Records a tool's id and the config now on the server, adding an entry if it's new. Push finds the entry by
   * definition (object, then name); pull, by id.
   */
  void recordTool(Tool tool, String id, JsonNode config, boolean matchById) {
    ToolSlot slot = null;
    if (matchById) {
      for (ToolSlot candidate : tools) {
        if (id.equals(candidate.entry.id)) {
          slot = candidate;
          break;
        }
      }
    } else {
      slot = toolSlot(tool);
    }
    if (slot == null) {
      String path = ProjectFormat.uniquePath(TOOL_CONFIGS_DIR, tool.getName(), taken);
      taken.add(path);
      ToolEntry entry = new ToolEntry();
      entry.config = path;
      slot = new ToolSlot(entry, tool);
      tools.add(slot);
    }
    slot.tool = tool;
    slot.entry.id = id;
    slot.entry.type = tool.getKind().wireName();
    slot.raw = config;
    slot.baseline = ToolDefinition.of(tool);
    write(slot.entry.config, ProjectFormat.formatConfig(config));
    write(TOOLS_FILE, toolsIndex());
  }

  /** Records an agent's id and its config, adding an entry if it's new. Matches like {@link #recordTool}. */
  void recordAgent(Agent agent, String id, JsonNode config, String branchId, String versionId, boolean matchById) {
    AgentSlot slot = null;
    if (matchById) {
      for (AgentSlot candidate : agents) {
        if (id.equals(candidate.entry.id)) {
          slot = candidate;
          break;
        }
      }
    } else {
      slot = agentSlot(agent);
    }
    if (slot == null) {
      String path = ProjectFormat.uniquePath(AGENT_CONFIGS_DIR, agent.getName(), taken);
      taken.add(path);
      AgentEntry entry = new AgentEntry();
      entry.config = path;
      slot = new AgentSlot(entry, agent);
      agents.add(slot);
    }
    slot.entry.id = id;
    ObjectNode branch = branch(slot, branchId);
    // A branch has its own file; the slot's agent is the main branch's, which a branch must not replace.
    if (branch == null) {
      slot.agent = agent;
    }
    // Like the CLI: the version the server reports, on the branch entry it belongs to.
    if (versionId != null) {
      if (branch != null) {
        branch.put("version_id", versionId);
      } else {
        slot.entry.versionId = versionId;
      }
    }
    String path = slot.entry.config;
    if (branch != null && branch.get("config") != null && branch.get("config").isTextual()) {
      path = branch.get("config").textValue();
    }
    write(path, ProjectFormat.formatConfig(config));
    write(AGENTS_FILE, agentsIndex());
  }

  /**
   * Rejects a branch the project has no file for, before anything is written: its settings would otherwise land in
   * the main branch's file, and a later CLI push would send them to the main branch.
   */
  void checkBranch(Agent agent, String branchId) {
    checkBranchOf(agentSlot(agent), agent.getName(), branchId);
  }

  /** {@link #checkBranch} for pull, which finds the agent by id. */
  void checkBranchById(String agentId, String branchId) {
    AgentSlot found = null;
    for (AgentSlot slot : agents) {
      if (agentId.equals(slot.entry.id)) {
        found = slot;
        break;
      }
    }
    checkBranchOf(found, agentId, branchId);
  }

  private void checkBranchOf(AgentSlot slot, String label, String branchId) {
    if (branchId == null || (slot != null && branchId.equals(slot.entry.branchId))) {
      return;
    }
    if (slot == null || branch(slot, branchId) == null) {
      throw new AgentProjectException(
        "Branch " + branchId + " of \"" + label + "\" has no config file in " + AGENTS_FILE + ". Add it under " +
        "\"branches\" (the CLI's `agents pull --all-branches` does), or push the main branch"
      );
    }
  }

  private static ObjectNode branch(AgentSlot slot, String branchId) {
    if (branchId == null || slot.entry.branches == null) {
      return null;
    }
    for (JsonNode branch : slot.entry.branches) {
      JsonNode id = branch.get("branch_id");
      if (branch.isObject() && id != null && branchId.equals(id.asText(null))) {
        return (ObjectNode) branch;
      }
    }
    return null;
  }

  private String current(String path, String fallback) {
    if (changed.containsKey(path)) {
      return changed.get(path);
    }
    if (files.containsKey(path)) {
      return files.get(path);
    }
    return fallback;
  }

  private void write(String path, String text) {
    String key = ProjectFormat.normalizePath(path);
    if (text.equals(files.get(key))) {
      changed.remove(key);
    } else {
      changed.put(key, text);
    }
  }

  private void load() {
    JsonNode agentsIndex = readIndex(AGENTS_FILE).get("agents");
    JsonNode toolsIndex = readIndex(TOOLS_FILE).get("tools");

    Map<String, Tool> byId = new LinkedHashMap<>();
    for (JsonNode raw : entries(toolsIndex)) {
      ToolEntry entry = new ToolEntry();
      entry.type = text(raw.get("type"));
      entry.config = configPath(raw, TOOLS_FILE);
      entry.id = text(raw.get("id"));
      JsonNode config = readConfig(entry.config, TOOLS_FILE);
      Tool tool;
      try {
        tool = Tool.fromJson(config);
      } catch (RuntimeException e) {
        throw new AgentProjectException(entry.config + " isn't a valid tool config: " + e.getMessage(), e);
      }
      ToolSlot slot = new ToolSlot(entry, tool);
      slot.raw = config;
      slot.baseline = ToolDefinition.of(tool);
      tools.add(slot);
      if (entry.id != null) {
        byId.put(entry.id, tool);
      }
    }
    for (JsonNode raw : entries(agentsIndex)) {
      AgentEntry entry = new AgentEntry();
      entry.config = configPath(raw, AGENTS_FILE);
      entry.id = text(raw.get("id"));
      entry.branchId = text(raw.get("branch_id"));
      entry.versionId = text(raw.get("version_id"));
      JsonNode branches = raw.get("branches");
      entry.branches = branches != null && branches.isObject() ? (ObjectNode) branches.deepCopy() : null;
      JsonNode config = readConfig(entry.config, AGENTS_FILE);
      Agent agent;
      try {
        agent = Agent.fromJson(config, byId);
      } catch (RuntimeException e) {
        throw new AgentProjectException(entry.config + " isn't a valid agent config: " + e.getMessage(), e);
      }
      agents.add(new AgentSlot(entry, agent));
    }
  }

  private JsonNode readIndex(String path) {
    String text = files.get(path);
    if (text == null) {
      return Wire.NODES.objectNode();
    }
    JsonNode parsed;
    try {
      parsed = CanonicalJson.parse(text);
    } catch (IllegalArgumentException e) {
      throw new AgentProjectException(path + " isn't valid JSON: " + e.getMessage(), e);
    }
    if (!parsed.isObject()) {
      throw new AgentProjectException(path + " isn't valid: it must hold a JSON object");
    }
    return parsed;
  }

  private JsonNode readConfig(String path, String index) {
    if (!path.toLowerCase(Locale.ROOT).endsWith(".json")) {
      throw new AgentProjectException(
        index + " points at " + path + ": only JSON config files are supported yet (YAML and TOML aren't)"
      );
    }
    String text = files.get(ProjectFormat.normalizePath(path));
    if (text == null) {
      throw new AgentProjectException(index + " points at " + path + ", which doesn't exist");
    }
    try {
      return CanonicalJson.parse(text);
    } catch (IllegalArgumentException e) {
      throw new AgentProjectException(path + " isn't valid JSON: " + e.getMessage(), e);
    }
  }

  private String agentsIndex() {
    ArrayNode list = Wire.NODES.arrayNode();
    for (AgentSlot slot : agents) {
      // The CLI writes fields in this order and leaves unset ones out.
      ObjectNode entry = list.addObject();
      entry.put("config", slot.entry.config);
      putIfSet(entry, "id", slot.entry.id);
      putIfSet(entry, "branch_id", slot.entry.branchId);
      putIfSet(entry, "version_id", slot.entry.versionId);
      if (slot.entry.branches != null) {
        entry.set("branches", slot.entry.branches.deepCopy());
      }
    }
    ObjectNode index = Wire.NODES.objectNode();
    index.set("agents", list);
    return ProjectFormat.formatIndex(index);
  }

  private String toolsIndex() {
    ArrayNode list = Wire.NODES.arrayNode();
    for (ToolSlot slot : tools) {
      ObjectNode entry = list.addObject();
      putIfSet(entry, "type", slot.entry.type);
      entry.put("config", slot.entry.config);
      putIfSet(entry, "id", slot.entry.id);
    }
    ObjectNode index = Wire.NODES.objectNode();
    index.set("tools", list);
    return ProjectFormat.formatIndex(index);
  }

  private static void putIfSet(ObjectNode entry, String key, String value) {
    if (value != null) {
      entry.put(key, value);
    }
  }

  private static String configPath(JsonNode entry, String index) {
    JsonNode config = entry.get("config");
    if (config == null || !config.isTextual()) {
      throw new AgentProjectException(index + " has an entry without a \"config\" path");
    }
    return config.textValue();
  }

  private static String text(JsonNode node) {
    return node != null && node.isTextual() ? node.textValue() : null;
  }

  private static List<JsonNode> entries(JsonNode list) {
    List<JsonNode> result = new ArrayList<>();
    if (list != null && list.isArray()) {
      for (JsonNode entry : list) {
        if (entry.isObject()) {
          result.add(entry);
        }
      }
    }
    return result;
  }

  private static <T> T single(List<T> matches, String index, String name) {
    if (matches.size() > 1) {
      throw new AgentProjectException(
        matches.size() + " entries in " + index + " are named \"" + name + "\"; push the loaded one"
      );
    }
    return matches.isEmpty() ? null : matches.get(0);
  }
}
