package com.elevenlabs.api.agentworkflows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.elevenlabs.api.types.AgentWorkflowRequestModel;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/**
 * The shared cross-SDK golden fixtures, vendored from elevenlabs-js {@code docs/agents-as-code/fixtures} into
 * {@code src/test/resources/agentworkflows/fixtures}. Each file is run through the Java builder; its
 * {@code toJsonString()} must equal {@code JSON.stringify(expected, null, 2) + "\n"} byte for byte, or
 * {@code build()} must report exactly the listed error codes.
 */
class WorkflowFixtureTest {

  // Bump when vendoring new fixtures, so a missing file fails loudly instead of being skipped.
  private static final int FIXTURE_COUNT = 10;

  @TestFactory
  Stream<DynamicTest> fixtures() throws IOException, URISyntaxException {
    return fixtureFiles().stream().map(path -> DynamicTest.dynamicTest(path.getFileName().toString(), () -> check(path)));
  }

  @Test
  void allFixturesArePresent() throws IOException, URISyntaxException {
    assertEquals(FIXTURE_COUNT, fixtureFiles().size());
  }

  private static void check(Path path) throws IOException {
    JsonNode fixture = CanonicalJson.parse(new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
    WorkflowFixtureRunner.Result result = WorkflowFixtureRunner.run(fixture);

    if (fixture.has("errors")) {
      List<String> expected = new ArrayList<>();
      fixture.get("errors").forEach(code -> expected.add(code.asText()));
      Collections.sort(expected);
      List<String> actual = result.errors == null ? new ArrayList<>() : new ArrayList<>(result.errors);
      Collections.sort(actual);
      assertEquals(expected, actual);
      return;
    }
    assertNull(result.errors, () -> "build() reported errors for a fixture expecting output: " + result.errors);
    // JSON.stringify(expected, null, 2): document order, JavaScript number formatting.
    assertEquals(CanonicalJson.write(fixture.get("expected"), false), result.json);

    // build()'s generated request model carries the same workflow: loading it back writes the same bytes. A node
    // type the model doesn't know can't survive that, so build() rejects it with invalid_model instead.
    if (hasUnmodelledNodeType(fixture.get("expected"))) {
      WorkflowValidationException e = assertThrows(WorkflowValidationException.class, () -> result.workflow.build());
      for (WorkflowIssue issue : e.getIssues()) {
        assertEquals("invalid_model", issue.getCode());
      }
      return;
    }
    AgentWorkflowRequestModel model = result.workflow.build();
    assertEquals(result.json, Workflow.fromAgent(model).toJsonString());
  }

  private static boolean hasUnmodelledNodeType(JsonNode workflow) {
    for (JsonNode node : workflow.path("nodes")) {
      if (!NodeTypes.modelKnows(node.path("type").asText(null))) {
        return true;
      }
    }
    return false;
  }

  static List<Path> fixtureFiles() throws IOException, URISyntaxException {
    URL dir = WorkflowFixtureTest.class.getResource("/agentworkflows/fixtures");
    if (dir == null) {
      return new ArrayList<>();
    }
    try (Stream<Path> files = Files.list(Paths.get(dir.toURI()))) {
      return files.filter(p -> p.toString().endsWith(".json")).sorted().collect(Collectors.toList());
    }
  }
}
