package com.elevenlabs.api.speechengine;

import java.util.Objects;

/**
 * A single message in a Speech Engine conversation transcript.
 *
 * <p>Hand-written (see {@code .fernignore}): the server-side session surface is not part of the
 * generated client. Mirrors {@code ConversationMessage} in the other ElevenLabs SDKs.
 */
public final class ConversationMessage {

  private final String role;
  private final String content;

  public ConversationMessage(String role, String content) {
    this.role = Objects.requireNonNull(role, "role");
    this.content = Objects.requireNonNull(content, "content");
  }

  /** Either {@code "user"} or {@code "agent"}. */
  public String getRole() {
    return role;
  }

  /** The text content of the message. */
  public String getContent() {
    return content;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof ConversationMessage)) return false;
    ConversationMessage that = (ConversationMessage) other;
    return role.equals(that.role) && content.equals(that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(role, content);
  }

  @Override
  public String toString() {
    return "ConversationMessage{role=" + role + ", content=" + content + "}";
  }
}
