package com.elevenlabs.api.speechengine;

import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Handlers and options wired onto each {@link SpeechEngineSession} produced by
 * {@link SpeechEngineServer}, {@link SpeechEngineJakarta} or {@link SpeechEngineResource}.
 *
 * <pre>{@code
 * SpeechEngineCallbacks callbacks = new SpeechEngineCallbacks()
 *   .onTranscript((transcript, signal, session) -> session.sendResponse(llm.reply(transcript)))
 *   .onError((error, session) -> error.printStackTrace());
 * }</pre>
 *
 * <p>Hand-written (see {@code .fernignore}), mirroring the {@code SpeechEngineCallbacks} option
 * object in the Python/TypeScript SDKs.
 */
public final class SpeechEngineCallbacks {

  private boolean debug;
  private boolean disableAuth;
  private Executor executor;
  private BiConsumer<String, SpeechEngineSession> onInit;
  private TranscriptHandler onTranscript;
  private Consumer<SpeechEngineSession> onClose;
  private Consumer<SpeechEngineSession> onDisconnect;
  private BiConsumer<Throwable, SpeechEngineSession> onError;

  /** Enable debug logging to standard error. */
  public SpeechEngineCallbacks debug(boolean debug) {
    this.debug = debug;
    return this;
  }

  /**
   * Disable verification of the {@code X-ElevenLabs-Speech-Engine-Authorization} JWT on incoming
   * connections. When {@code true}, any client that can reach the server is accepted.
   *
   * <p><b>Insecure.</b> Only enable this when the server is protected another way, typically an IP
   * allowlist restricting traffic to ElevenLabs' egress ranges. Without that, anyone on the internet
   * can open a session and consume your compute and downstream LLM quota.
   */
  public SpeechEngineCallbacks disableAuth(boolean disableAuth) {
    this.disableAuth = disableAuth;
    return this;
  }

  /**
   * The executor transcript handlers run on. Defaults to a shared pool of daemon threads, one per
   * running turn.
   */
  public SpeechEngineCallbacks executor(Executor executor) {
    this.executor = executor;
    return this;
  }

  /** Fired once when the session is initialized with a conversation ID. */
  public SpeechEngineCallbacks onInit(BiConsumer<String, SpeechEngineSession> handler) {
    this.onInit = handler;
    return this;
  }

  /** Fired each time the Speech Engine API sends a user transcript. */
  public SpeechEngineCallbacks onTranscript(TranscriptHandler handler) {
    this.onTranscript = handler;
    return this;
  }

  /** Fired when ElevenLabs sends a clean {@code close} message ending the conversation. */
  public SpeechEngineCallbacks onClose(Consumer<SpeechEngineSession> handler) {
    this.onClose = handler;
    return this;
  }

  /** Fired once when the underlying WebSocket connection ends. */
  public SpeechEngineCallbacks onDisconnect(Consumer<SpeechEngineSession> handler) {
    this.onDisconnect = handler;
    return this;
  }

  /** Fired on protocol-level or WebSocket-level errors. */
  public SpeechEngineCallbacks onError(BiConsumer<Throwable, SpeechEngineSession> handler) {
    this.onError = handler;
    return this;
  }

  public boolean isDebug() {
    return debug;
  }

  public boolean isAuthDisabled() {
    return disableAuth;
  }

  /** Creates a session over a transport and registers these handlers on it. */
  SpeechEngineSession newSession(java.util.function.Consumer<String> send, Runnable close) {
    SpeechEngineSession session = new SpeechEngineSession(send, close, debug, executor);
    if (onInit != null) session.onInit(onInit);
    if (onTranscript != null) session.onTranscript(onTranscript);
    if (onClose != null) session.onClose(onClose);
    if (onDisconnect != null) session.onDisconnect(onDisconnect);
    if (onError != null) session.onError(onError);
    return session;
  }
}
