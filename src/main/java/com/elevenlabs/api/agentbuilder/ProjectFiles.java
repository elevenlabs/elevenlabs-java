package com.elevenlabs.api.agentbuilder;

import com.elevenlabs.api.agentworkflows.CanonicalJson;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** File access for {@link AgentProject}, through {@code java.nio.file}. */
final class ProjectFiles {

  private static final Set<PosixFilePermission> OWNER_ONLY = EnumSet.of(
    PosixFilePermission.OWNER_READ,
    PosixFilePermission.OWNER_WRITE
  );

  /** What {@link #read} found: the index files and the configs they list, and the files in the config directories. */
  static final class Contents {

    final Map<String, String> files = new LinkedHashMap<>();
    final List<String> taken = new ArrayList<>();
  }

  private ProjectFiles() {}

  /** Reads the index files and every config they list. {@code taken} lists the files already in the config directories. */
  static Contents read(Path dir) {
    Path root = realPath(dir);
    Contents contents = new Contents();
    for (String index : new String[] { AgentProject.AGENTS_FILE, AgentProject.TOOLS_FILE }) {
      String text = readIfExists(resolveInProject(root, index));
      if (text == null) {
        continue;
      }
      contents.files.put(index, text);
      JsonNode parsed;
      try {
        parsed = CanonicalJson.parse(text);
      } catch (IllegalArgumentException e) {
        continue; // AgentProject reports it, with the parse error.
      }
      List<JsonNode> configs = new ArrayList<>();
      for (JsonNode entry : entries(parsed, "agents")) {
        configs.add(entry.get("config"));
        JsonNode branches = entry.get("branches");
        if (branches != null && branches.isObject()) {
          for (JsonNode branch : branches) {
            configs.add(branch.isObject() ? branch.get("config") : null);
          }
        }
      }
      for (JsonNode entry : entries(parsed, "tools")) {
        configs.add(entry.get("config"));
      }
      for (JsonNode config : configs) {
        if (config == null || !config.isTextual()) {
          throw new AgentProjectException(index + " has an entry without a \"config\" path");
        }
        String content = readIfExists(resolveInProject(root, config.textValue()));
        if (content != null) {
          contents.files.put(ProjectFormat.normalizePath(config.textValue()), content);
        }
      }
    }
    for (String sub : new String[] { AgentProject.AGENT_CONFIGS_DIR, AgentProject.TOOL_CONFIGS_DIR }) {
      Path subdir = root.resolve(sub);
      if (!Files.isDirectory(subdir)) {
        continue;
      }
      try (DirectoryStream<Path> names = Files.newDirectoryStream(subdir)) {
        for (Path name : names) {
          contents.taken.add(sub + "/" + name.getFileName().toString());
        }
      } catch (NotDirectoryException e) {
        // No directory yet.
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
    return contents;
  }

  /** Writes each file owner-only, creating directories as needed, never through a symlink or outside the project. */
  static void write(Path dir, List<Map.Entry<String, String>> files) {
    Path root = realPath(dir);
    boolean posix = root.getFileSystem().supportedFileAttributeViews().contains("posix");
    for (Map.Entry<String, String> file : files) {
      String path = file.getKey();
      Path target = resolveInProject(root, path);
      try {
        Files.createDirectories(target.getParent());
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
      if (Files.isSymbolicLink(target)) {
        throw new AgentProjectException("Refusing to write " + path + ": it's a symlink");
      }
      Set<OpenOption> options = new java.util.HashSet<>();
      options.add(StandardOpenOption.WRITE);
      options.add(StandardOpenOption.CREATE);
      options.add(StandardOpenOption.TRUNCATE_EXISTING);
      options.add(LinkOption.NOFOLLOW_LINKS);
      FileAttribute<?>[] attributes = posix
        ? new FileAttribute<?>[] { PosixFilePermissions.asFileAttribute(OWNER_ONLY) }
        : new FileAttribute<?>[0];
      byte[] bytes = file.getValue().getBytes(StandardCharsets.UTF_8);
      try (SeekableByteChannel channel = Files.newByteChannel(target, options, attributes)) {
        if (posix) {
          // An existing file keeps its mode on open; make it owner-only too.
          Files.getFileAttributeView(target, PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).setPermissions(OWNER_ONLY);
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        while (buffer.hasRemaining()) {
          channel.write(buffer);
        }
      } catch (FileSystemException e) {
        if (Files.isSymbolicLink(target)) {
          throw new AgentProjectException("Refusing to write " + path + ": it's a symlink", e);
        }
        throw new UncheckedIOException(e);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  /**
   * The CLI's {@code resolve_in_project}: the absolute path for a project-relative one, refusing anything that escapes
   * the project, including through a symlinked directory.
   */
  static Path resolveInProject(Path root, String path) {
    if (ProjectFormat.trim(path).isEmpty() || hasControlCharacter(path)) {
      throw new AgentProjectException("Invalid config path " + CanonicalJson.write(Wire.NODES.textNode(path), false).trim());
    }
    Path candidate;
    try {
      candidate = root.resolve(path).normalize();
    } catch (java.nio.file.InvalidPathException e) {
      throw new AgentProjectException("Invalid config path " + path, e);
    }
    if (!candidate.startsWith(root)) {
      throw new AgentProjectException("Config path " + path + " is outside the project");
    }
    // The deepest part of the path that exists, with symlinks resolved, must still be inside.
    Path ancestor = candidate;
    while (true) {
      try {
        Path real = ancestor.toRealPath();
        if (!real.startsWith(root)) {
          throw new AgentProjectException("Config path " + path + " leads outside the project through a symlink");
        }
        return candidate;
      } catch (NoSuchFileException e) {
        Path parent = ancestor.getParent();
        if (parent == null) {
          throw new AgentProjectException("Config path " + path + " is outside the project");
        }
        ancestor = parent;
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  private static boolean hasControlCharacter(String path) {
    for (int i = 0; i < path.length(); i++) {
      if (Character.getType(path.charAt(i)) == Character.CONTROL) {
        return true;
      }
    }
    return false;
  }

  private static Path realPath(Path dir) {
    try {
      return dir.toRealPath();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /** The file's text, line endings and all, or {@code null} if there's no such file. */
  private static String readIfExists(Path path) {
    if (!Files.isRegularFile(path)) {
      return null;
    }
    try (InputStream in = Files.newInputStream(path)) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] chunk = new byte[8192];
      int n;
      while ((n = in.read(chunk)) != -1) {
        out.write(chunk, 0, n);
      }
      return new String(out.toByteArray(), StandardCharsets.UTF_8);
    } catch (NoSuchFileException e) {
      return null;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static List<JsonNode> entries(JsonNode index, String key) {
    List<JsonNode> result = new ArrayList<>();
    JsonNode entries = index.isObject() ? index.get(key) : null;
    if (entries != null && entries.isArray()) {
      for (JsonNode entry : entries) {
        if (entry.isObject()) {
          result.add(entry);
        }
      }
    }
    return result;
  }
}
