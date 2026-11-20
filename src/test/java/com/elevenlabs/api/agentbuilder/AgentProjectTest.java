package com.elevenlabs.api.agentbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.elevenlabs.api.ElevenLabsClient;
import com.elevenlabs.api.agenttools.Tool;
import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AgentProjectTest {

  static JsonNode json(String text) {
    return CanonicalJson.parse(text);
  }

  static Map<String, String> cliProject() throws IOException {
    return TestFiles.readTree(TestFiles.CLI_PROJECT);
  }

  static List<String> names(List<?> items) {
    List<String> names = new ArrayList<>();
    for (Object item : items) {
      names.add(item instanceof Agent ? ((Agent) item).getName() : ((Tool) item).getName());
    }
    return names;
  }

  static boolean posix() {
    return FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
  }

  // ---- The shared fixture's three checks ----------------------------------------------------------------------

  @Test
  void loadsACliProjectToolsByIdAgentsWithToolObjectsUnknownIdsKept() throws IOException {
    AgentProject project = AgentProject.fromFiles(cliProject());
    assertEquals(Arrays.asList("Support", "Billing"), names(project.agents()));
    assertEquals(Arrays.asList("lookup_order", "issue_refund"), names(project.tools()));

    Agent support = project.agent("Support");
    assertEquals(Collections.singletonList("lookup_order"), names(support.tools()));
    assertSame(project.tools().get(0), support.tools().get(0));
    assertEquals(Collections.singletonList("tool_from_dashboard"), support.getToolIds());
    assertEquals("agent_support", project.agentId(support).get());
    assertEquals("tool_lookup", project.toolId(support.tools().get(0)).get());

    Agent billing = project.agent("Billing");
    assertEquals(Collections.singletonList("issue_refund"), names(billing.tools()));
    assertTrue(billing.getToolIds().isEmpty());
    assertEquals("agent_billing", project.agentId(billing).get());
    assertEquals("tool_refund", project.toolId(project.tools().get(1)).get());
  }

  @Test
  void givesBackEveryFileUnchangedWhenNothingWasPushedBranchConfigsIncluded() throws IOException {
    Map<String, String> files = cliProject();
    Map<String, String> back = AgentProject.fromFiles(files).toFiles();
    assertEquals(files, new java.util.TreeMap<>(back));
  }

  @Test
  void roundTripsTheCliAgentConfigToItsSortedForm() throws IOException {
    AgentProject project = AgentProject.fromFiles(cliProject());
    JsonNode raw = json(cliProject().get("agent_configs/support.json"));
    Map<String, String> ids = Collections.singletonMap("lookup_order", "tool_lookup");
    assertEquals(CanonicalJson.toCanonicalString(raw), CanonicalJson.toCanonicalString(project.agent("Support").toJson(ids)));
  }

  @Test
  void opensTheFixtureFromDiskTheSameWay() {
    AgentProject project = AgentProject.open(TestFiles.CLI_PROJECT);
    assertEquals(Arrays.asList("Support", "Billing"), names(project.agents()));
    assertEquals("agent_support", project.agentId(project.agent("Support")).get());
    assertTrue(project.getChangedFiles().isEmpty());
  }

  // ---- Errors and naming --------------------------------------------------------------------------------------

  @Test
  void reportsMissingMalformedAndNotYetSupportedFilesClearly() {
    assertMessage("only JSON config files are supported yet", Collections.singletonMap("agents.json", agentsIndex("agent_configs/a.yaml")));
    assertMessage("only JSON config files are supported yet", Collections.singletonMap("agents.json", agentsIndex("agent_configs/a.toml")));
    assertMessage("points at agent_configs/a.json, which doesn't exist", Collections.singletonMap("agents.json", agentsIndex("agent_configs/a.json")));
    assertMessage("agents.json isn't valid JSON", Collections.singletonMap("agents.json", "{"));
    Map<String, String> badTool = new LinkedHashMap<>();
    badTool.put("tools.json", "{\"tools\":[{\"type\":\"webhook\",\"config\":\"tool_configs/t.json\"}]}");
    badTool.put("tool_configs/t.json", "[]");
    assertMessage("tool_configs/t.json isn't a valid tool config", badTool);
    AgentProjectException e = assertThrows(AgentProjectException.class, () -> AgentProject.fromFiles(null).agent("Nope"));
    assertTrue(e.getMessage().contains("No agent named \"Nope\""));
  }

  private static String agentsIndex(String config) {
    return "{\"agents\":[{\"config\":\"" + config + "\"}]}";
  }

  private static void assertMessage(String expected, Map<String, String> files) {
    AgentProjectException e = assertThrows(AgentProjectException.class, () -> AgentProject.fromFiles(files));
    assertTrue(e.getMessage().contains(expected), e.getMessage());
  }

  @Test
  void namesNewFilesLikeTheCli() {
    assertEquals("Support-line-EU", ProjectFormat.sanitizeFilename("  Support line / EU "));
    assertEquals("_hidden", ProjectFormat.sanitizeFilename(".hidden"));
    assertEquals("unnamed", ProjectFormat.sanitizeFilename("..."));
    assertEquals("unnamed", ProjectFormat.sanitizeFilename("   "));
    assertEquals("a-b-c", ProjectFormat.sanitizeFilename("a:*b?\"<>|c"));
    StringBuilder longName = new StringBuilder();
    for (int i = 0; i < 120; i++) {
      longName.append('x');
    }
    assertEquals(100, ProjectFormat.sanitizeFilename(longName.toString()).length());
    Set<String> taken = new HashSet<>(Arrays.asList("agent_configs/A.json", "agent_configs/A-1.json"));
    assertEquals("agent_configs/A-2.json", ProjectFormat.uniquePath("agent_configs", "A", taken));
    assertEquals("a/b.json", ProjectFormat.normalizePath("./a\\\\b.json"));
  }

  @Test
  void writesTheCliFormat() {
    assertEquals("{\n    \"b\": [\n        1\n    ],\n    \"a\": {}\n}", ProjectFormat.formatIndex(json("{\"b\":[1],\"a\":{}}")));
    assertEquals("{\n    \"a\": {},\n    \"b\": [\n        1\n    ]\n}", ProjectFormat.formatConfig(json("{\"b\":[1],\"a\":{}}")));
  }

  @Test
  void saveNeedsADirectory() {
    AgentProjectException e = assertThrows(AgentProjectException.class, () -> AgentProject.fromFiles(null).save());
    assertTrue(e.getMessage().contains("toFiles()"));
  }

  // ---- On disk --------------------------------------------------------------------------------------------------

  @Test
  void opensAnEmptyDirectoryAndPushWritesTheCliLayoutOwnerOnly(@TempDir Path dir) throws IOException {
    // A file the project doesn't list.
    TestFiles.write(dir.resolve("agent_configs/A.json"), "not ours");
    AgentProject project = AgentProject.open(dir);
    Agent agent = new Agent("A", new AgentOptions().prompt("p").tools(Tool.client("ping", "Ping the app")));
    try (FakeServer server = new FakeServer()) {
      ElevenLabsClient client = server.client();
      client.agents().push(agent, new AgentPushOptions().project(project));
    }

    Map<String, String> files = TestFiles.readTree(dir);
    assertEquals(
      Arrays.asList("agent_configs/A-1.json", "agent_configs/A.json", "agents.json", "tool_configs/ping.json", "tools.json"),
      new ArrayList<>(files.keySet())
    );
    assertEquals("not ours", files.get("agent_configs/A.json"));
    assertEquals("{\n    \"description\": \"Ping the app\",\n    \"name\": \"ping\",\n    \"type\": \"client\"\n}", files.get("tool_configs/ping.json"));
    if (posix()) {
      for (String path : Arrays.asList("agents.json", "tools.json", "agent_configs/A-1.json", "tool_configs/ping.json")) {
        assertEquals("rw-------", PosixFilePermissions.toString(Files.getPosixFilePermissions(dir.resolve(path))), path);
      }
    }
    assertTrue(project.getChangedFiles().isEmpty());

    AgentProject reopened = AgentProject.open(dir);
    assertEquals(project.agentId(agent), reopened.agentId(reopened.agent("A")));
  }

  @Test
  void makesAnExistingFileOwnerOnlyWhenItRewritesIt(@TempDir Path dir) throws IOException {
    assumeTrue(posix());
    TestFiles.write(dir.resolve("agents.json"), "{\"agents\":[]}");
    Files.setPosixFilePermissions(dir.resolve("agents.json"), PosixFilePermissions.fromString("rw-r--r--"));
    AgentProject project = AgentProject.open(dir);
    Agent agent = new Agent("A", new AgentOptions().prompt("p"));
    project.recordAgent(agent, "agent_1", agent.toJson(), null, null, false);
    project.save();
    Set<PosixFilePermission> mode = Files.getPosixFilePermissions(dir.resolve("agents.json"));
    assertEquals("rw-------", PosixFilePermissions.toString(mode));
  }

  @Test
  void keepsLineEndingsOfFilesItDidNotChange(@TempDir Path dir) throws IOException {
    String text = "{\r\n    \"agents\": []\r\n}";
    TestFiles.write(dir.resolve("agents.json"), text);
    assertEquals(text, AgentProject.open(dir).toFiles().get("agents.json"));
  }

  @Test
  void refusesConfigPathsOutsideTheProjectDirectlyOrThroughASymlink(@TempDir Path tmp) throws IOException {
    Path outside = tmp.resolve("outside");
    TestFiles.write(outside.resolve("a.json"), "{}");

    Path escaping = tmp.resolve("escaping");
    TestFiles.write(escaping.resolve("agents.json"), agentsIndex("../outside/a.json"));
    AgentProjectException e = assertThrows(AgentProjectException.class, () -> AgentProject.open(escaping));
    assertTrue(e.getMessage().contains("outside the project"), e.getMessage());

    Path linked = tmp.resolve("linked");
    Files.createDirectories(linked);
    Files.createSymbolicLink(linked.resolve("agent_configs"), outside);
    TestFiles.write(linked.resolve("agents.json"), agentsIndex("agent_configs/a.json"));
    e = assertThrows(AgentProjectException.class, () -> AgentProject.open(linked));
    assertTrue(e.getMessage().contains("through a symlink"), e.getMessage());
  }

  @Test
  void neverWritesThroughASymlinkedFile(@TempDir Path tmp) throws IOException {
    Path target = tmp.resolve("elsewhere.json");
    TestFiles.write(target, "keep");
    Path dir = tmp.resolve("project");
    Files.createDirectories(dir);
    AgentProject project = AgentProject.open(dir);
    Files.createSymbolicLink(dir.resolve("agents.json"), target);
    Agent agent = new Agent("A", new AgentOptions().prompt("p"));
    project.recordAgent(agent, "agent_1", agent.toJson(), null, null, false);
    assertThrows(AgentProjectException.class, project::save);
    assertEquals("keep", TestFiles.read(target));
  }

  @Test
  void neverWritesThroughASymlinkInsideTheProject(@TempDir Path tmp) throws IOException {
    // A symlink that stays inside the project is still refused: files are written, never followed.
    Path dir = tmp.resolve("project");
    TestFiles.write(dir.resolve("real.json"), "keep");
    AgentProject project = AgentProject.open(dir);
    Files.createSymbolicLink(dir.resolve("agents.json"), dir.resolve("real.json"));
    Agent agent = new Agent("A", new AgentOptions().prompt("p"));
    project.recordAgent(agent, "agent_1", agent.toJson(), null, null, false);
    AgentProjectException e = assertThrows(AgentProjectException.class, project::save);
    assertTrue(e.getMessage().contains("symlink"), e.getMessage());
    assertEquals("keep", TestFiles.read(dir.resolve("real.json")));
  }

  // ---- Recording ----------------------------------------------------------------------------------------------

  @Test
  void rejectsABranchTheProjectHasNoFileForBeforeAnyRequest() throws IOException {
    AgentProject project = AgentProject.fromFiles(cliProject());
    try (FakeServer server = new FakeServer()) {
      ElevenLabsClient client = server.client();
      AgentProjectException push = assertThrows(
        AgentProjectException.class,
        () -> client.agents().push(project.agent("Billing"), new AgentPushOptions().project(project).branchId("agtbrch_x"))
      );
      assertTrue(push.getMessage().contains("has no config file"));
      AgentProjectException pull = assertThrows(
        AgentProjectException.class,
        () -> client.agents().pull("agent_support", new AgentPullOptions().project(project).branchId("agtbrch_x"))
      );
      assertTrue(pull.getMessage().contains("has no config file"));
      assertTrue(server.calls.isEmpty());
    }
  }

  @Test
  void keepsTheMainAgentWhenABranchIsRecordedAndTheToolTypeInStep() throws IOException {
    AgentProject project = AgentProject.fromFiles(cliProject());
    Agent main = project.agent("Support");
    Agent beta = Agent.fromJson(cliProject().get("agent_configs/support.beta.json"));
    project.recordAgent(beta, "agent_support", beta.toJson(), "agtbrch_beta", "ver_9", true);
    assertSame(main, project.agent("Support"));
    assertTrue(project.getChangedFiles().contains("agent_configs/support.beta.json"));
    assertFalse(project.getChangedFiles().contains("agent_configs/support.json"));
    JsonNode index = json(project.toFiles().get("agents.json"));
    assertEquals("ver_9", index.at("/agents/0/branches/beta/version_id").asText());
    assertFalse(index.at("/agents/0").has("version_id"));

    Tool lookup = project.tools().get(0);
    Tool client = Tool.client("lookup_order", "Now a client tool");
    project.recordTool(client, project.toolId(lookup).get(), client.toJson(), true);
    assertEquals("client", json(project.toFiles().get("tools.json")).at("/tools/0/type").asText());
  }

  @Test
  void dropsAPendingWriteOnceAFileIsBackToWhatWasLoaded() {
    ObjectNode loaded = (ObjectNode) json("{\"name\":\"A\",\"conversation_config\":{},\"platform_settings\":{},\"tags\":[]}");
    Map<String, String> files = new LinkedHashMap<>();
    files.put("agents.json", ProjectFormat.formatIndex(json("{\"agents\":[{\"config\":\"agent_configs/a.json\",\"id\":\"agent_1\"}]}")));
    files.put("agent_configs/a.json", ProjectFormat.formatConfig(loaded));
    AgentProject project = AgentProject.fromFiles(files);
    Agent agent = project.agent("A");
    ObjectNode tagged = loaded.deepCopy();
    tagged.set("tags", json("[\"x\"]"));
    project.recordAgent(agent, "agent_1", tagged, null, null, false);
    assertEquals(Collections.singletonList("agent_configs/a.json"), project.getChangedFiles());
    project.recordAgent(agent, "agent_1", loaded, null, null, false);
    assertTrue(project.getChangedFiles().isEmpty());
    Map<String, String> expected = new LinkedHashMap<>(files);
    expected.put("tools.json", "{\n    \"tools\": []\n}");
    assertEquals(expected, project.toFiles());
  }

  @Test
  void matchesARebuiltAgentByNameAndRejectsAmbiguousNames() throws IOException {
    AgentProject project = AgentProject.fromFiles(cliProject());
    assertEquals("agent_billing", project.agentId(new Agent("Billing")).get());
    assertFalse(project.agentId(new Agent("Nobody")).isPresent());
    Map<String, String> files = new LinkedHashMap<>();
    files.put("agents.json", ProjectFormat.formatIndex(json("{\"agents\":[{\"config\":\"agent_configs/a.json\"},{\"config\":\"agent_configs/b.json\"}]}")));
    files.put("agent_configs/a.json", "{\"name\":\"A\"}");
    files.put("agent_configs/b.json", "{\"name\":\"A\"}");
    AgentProject duplicate = AgentProject.fromFiles(files);
    AgentProjectException e = assertThrows(AgentProjectException.class, () -> duplicate.agentId(new Agent("A")));
    assertTrue(e.getMessage().contains("are named"));
    // The loaded objects still match their own entries.
    assertFalse(duplicate.agentId(duplicate.agents().get(0)).isPresent());
  }
}
