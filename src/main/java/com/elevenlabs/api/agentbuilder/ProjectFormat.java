package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** The ElevenLabs CLI's project file format and file naming. */
final class ProjectFormat {

  private static final int MAX_NAME_LENGTH = 100;

  private ProjectFormat() {}

  /** Index files: 4-space indent, no trailing newline (serde_json's pretty printer), fields in the order given. */
  static String formatIndex(JsonNode value) {
    return CanonicalJson.write(value, false, 4, false);
  }

  /** Config files: like {@link #formatIndex}, with keys sorted at every level, as the CLI writes them. */
  static String formatConfig(JsonNode value) {
    return CanonicalJson.write(value, true, 4, false);
  }

  /** The CLI's {@code sanitize_filename}: a filesystem-safe file stem for an agent or tool name. */
  static String sanitizeFilename(String name) {
    String trimmed = trim(name);
    if (trimmed.isEmpty()) {
      return "unnamed";
    }
    StringBuilder replaced = new StringBuilder();
    for (int i = 0; i < trimmed.length(); ) {
      int c = trimmed.codePointAt(i);
      if ("/\\:*?\"<>|".indexOf(c) >= 0 || isSpace(c)) {
        replaced.append('-');
      } else {
        replaced.appendCodePoint(c);
      }
      i += Character.charCount(c);
    }
    String sanitized = replaced.toString();
    while (sanitized.contains("--")) {
      sanitized = sanitized.replace("--", "-");
    }
    String stripped = sanitized.replaceAll("^[.-]+|[.-]+$", "");
    if (stripped.isEmpty()) {
      return "unnamed";
    }
    String result = trimmed.startsWith(".") ? "_" + stripped : stripped;
    if (result.codePointCount(0, result.length()) > MAX_NAME_LENGTH) {
      result = result.substring(0, result.offsetByCodePoints(0, MAX_NAME_LENGTH));
    }
    return result;
  }

  /** The CLI's {@code generate_unique_filename}: {@code <dir>/<name>.json}, then {@code -1}, {@code -2}... while taken. */
  static String uniquePath(String dir, String name, Set<String> taken) {
    String stem = sanitizeFilename(name);
    String candidate = dir + "/" + stem + ".json";
    for (int n = 1; taken.contains(candidate); n++) {
      candidate = dir + "/" + stem + "-" + n + ".json";
    }
    return candidate;
  }

  /** A project-relative path in one canonical spelling: forward slashes, no {@code ./} or empty segments. */
  static String normalizePath(String path) {
    List<String> parts = new ArrayList<>();
    for (String part : path.replace('\\', '/').split("/", -1)) {
      if (!part.isEmpty() && !part.equals(".")) {
        parts.add(part);
      }
    }
    return String.join("/", parts);
  }

  /** JavaScript's {@code String.prototype.trim}: strips Unicode whitespace, not just ASCII. */
  static String trim(String value) {
    int start = 0;
    int end = value.length();
    while (start < end && isSpace(value.charAt(start))) {
      start++;
    }
    while (end > start && isSpace(value.charAt(end - 1))) {
      end--;
    }
    return value.substring(start, end);
  }

  private static boolean isSpace(int c) {
    return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '﻿';
  }
}
