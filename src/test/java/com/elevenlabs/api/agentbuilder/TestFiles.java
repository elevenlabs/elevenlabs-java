package com.elevenlabs.api.agentbuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/** File helpers for the agent builder tests (Java 8 APIs only). */
final class TestFiles {

  static final Path CLI_PROJECT = Paths.get("src/test/resources/agentbuilder/fixtures/project/cli-graph");

  private TestFiles() {}

  static String read(Path path) throws IOException {
    try (InputStream in = Files.newInputStream(path)) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] chunk = new byte[8192];
      int n;
      while ((n = in.read(chunk)) != -1) {
        out.write(chunk, 0, n);
      }
      return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
  }

  static void write(Path path, String text) throws IOException {
    Files.createDirectories(path.toAbsolutePath().getParent());
    Files.write(path, text.getBytes(StandardCharsets.UTF_8));
  }

  /** Every file under {@code dir}, by forward-slash path relative to it. */
  static Map<String, String> readTree(final Path dir) throws IOException {
    final Map<String, String> files = new TreeMap<>();
    try (Stream<Path> paths = Files.walk(dir)) {
      for (Path path : (Iterable<Path>) paths::iterator) {
        if (Files.isRegularFile(path)) {
          files.put(dir.relativize(path).toString().replace('\\', '/'), read(path));
        }
      }
    }
    return files;
  }
}
