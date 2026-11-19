package com.elevenlabs.api.speechengine;

import java.util.List;

/**
 * Handles one user transcript. Runs on the session's executor, never on the transport's read
 * thread, so it may block on an LLM call. Reply with {@link SpeechEngineSession#sendResponse}.
 *
 * <p>Hand-written (see {@code .fernignore}).
 */
@FunctionalInterface
public interface TranscriptHandler {
  /**
   * @param transcript the conversation so far, oldest first
   * @param signal fires when a newer transcript interrupts this turn
   * @param session the session to reply on
   * @throws Exception any failure is reported to the session's error handlers, except a
   *     cancellation ({@link java.util.concurrent.CancellationException} or
   *     {@link InterruptedException}), which is the expected way for an interrupted turn to end
   */
  void onTranscript(
    List<ConversationMessage> transcript,
    AbortSignal signal,
    SpeechEngineSession session
  ) throws Exception;
}
