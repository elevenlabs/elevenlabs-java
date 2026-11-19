package com.elevenlabs.api.speechengine;

import com.elevenlabs.api.core.ElevenlabsApiException;
import com.elevenlabs.api.core.ObjectMappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * One Speech Engine conversation over a single WebSocket connection.
 *
 * <p>The session is transport-agnostic: it is given a {@code send} callback that writes a text
 * frame and a {@code close} callback that closes the connection, so the same protocol logic drives
 * the bundled {@link SpeechEngineServer}, the {@link SpeechEngineJakarta} servlet-container
 * integration, and any transport you manage yourself. Feed inbound text frames to
 * {@link #handleRawMessage(String)}, call {@link #handleDisconnect()} when the connection ends, and
 * register handlers with the {@code on*} methods.
 *
 * <p>Transcript handlers run on an executor, never on the thread that called
 * {@code handleRawMessage}, so a blocking LLM call does not stop the session from reading. When a
 * new user transcript arrives, the previous turn's {@link AbortSignal} fires, so a long-running LLM
 * call can be interrupted, matching the Python/TypeScript SDKs.
 *
 * <p>Hand-written (see {@code .fernignore}), mirroring {@code SpeechEngineSession} in the other
 * SDKs.
 */
public final class SpeechEngineSession {

  private static final ObjectMapper MAPPER = ObjectMappers.JSON_MAPPER;

  private final Consumer<String> send;
  private final Runnable closeConnection;
  private final boolean debug;
  private final Executor executor;

  private final List<BiConsumer<String, SpeechEngineSession>> initHandlers =
    new CopyOnWriteArrayList<>();
  private final List<TranscriptHandler> transcriptHandlers = new CopyOnWriteArrayList<>();
  private final List<Consumer<SpeechEngineSession>> closeHandlers = new CopyOnWriteArrayList<>();
  private final List<Consumer<SpeechEngineSession>> disconnectHandlers =
    new CopyOnWriteArrayList<>();
  private final List<BiConsumer<Throwable, SpeechEngineSession>> errorHandlers =
    new CopyOnWriteArrayList<>();

  // Guards the closed-state transition together with replacing currentTurn, so a close cannot race a
  // new turn into starting after it, and two transcripts cannot both think they replaced the same
  // turn. Signals are fired outside the lock.
  private final Object lock = new Object();
  // Serialises outbound frames: several turns may send at once, and not every transport (Jakarta
  // WebSocket's basic remote, for one) tolerates concurrent writes.
  private final Object sendLock = new Object();
  // The turn whose handler is running on this thread, so a reply sent from inside a handler is
  // tagged with that handler's own event ID even after a newer transcript has arrived.
  private final ThreadLocal<Turn> activeTurn = new ThreadLocal<>();
  private final AtomicBoolean disconnectFired = new AtomicBoolean();

  private volatile String conversationId;
  private volatile boolean closed;
  private Turn currentTurn;

  /**
   * @param send writes a text frame to the connection
   */
  public SpeechEngineSession(Consumer<String> send) {
    this(send, null, false, null);
  }

  /**
   * @param send writes a text frame to the connection
   * @param close closes the connection; may be null
   * @param debug log protocol events to standard error
   */
  public SpeechEngineSession(Consumer<String> send, Runnable close, boolean debug) {
    this(send, close, debug, null);
  }

  /**
   * @param send writes a text frame to the connection
   * @param close closes the connection; may be null
   * @param debug log protocol events to standard error
   * @param executor runs transcript handlers; null for a shared pool of daemon threads
   */
  public SpeechEngineSession(
    Consumer<String> send,
    Runnable close,
    boolean debug,
    Executor executor
  ) {
    this.send = Objects.requireNonNull(send, "send");
    this.closeConnection = close != null ? close : () -> {};
    this.debug = debug;
    this.executor = executor != null ? executor : DefaultExecutor.INSTANCE;
  }

  /** The conversation ID assigned by the API, available after the {@code init} message. */
  public Optional<String> getConversationId() {
    return Optional.ofNullable(conversationId);
  }

  /** Whether the session is still open. */
  public boolean isOpen() {
    return !closed;
  }

  /** Registers a handler fired once the session is initialized. */
  public SpeechEngineSession onInit(BiConsumer<String, SpeechEngineSession> handler) {
    initHandlers.add(Objects.requireNonNull(handler, "handler"));
    return this;
  }

  /** Registers a handler fired for each user transcript. */
  public SpeechEngineSession onTranscript(TranscriptHandler handler) {
    transcriptHandlers.add(Objects.requireNonNull(handler, "handler"));
    return this;
  }

  /** Registers a handler fired when the API sends a clean {@code close} message. */
  public SpeechEngineSession onClose(Consumer<SpeechEngineSession> handler) {
    closeHandlers.add(Objects.requireNonNull(handler, "handler"));
    return this;
  }

  /** Registers a handler fired once when the underlying connection ends. */
  public SpeechEngineSession onDisconnect(Consumer<SpeechEngineSession> handler) {
    disconnectHandlers.add(Objects.requireNonNull(handler, "handler"));
    return this;
  }

  /** Registers a handler fired on protocol or transport errors. */
  public SpeechEngineSession onError(BiConsumer<Throwable, SpeechEngineSession> handler) {
    errorHandlers.add(Objects.requireNonNull(handler, "handler"));
    return this;
  }

  /**
   * Parses and dispatches one inbound text frame. A malformed frame is reported to the error
   * handlers rather than thrown, so one bad message never ends the read loop.
   */
  public void handleRawMessage(String raw) {
    JsonNode message;
    try {
      message = MAPPER.readTree(raw);
    } catch (Exception e) {
      emitError(e);
      return;
    }
    if (message == null || !message.isObject()) {
      emitError(new ElevenlabsApiException("Expected a JSON object message"));
      return;
    }
    handleMessage(message);
  }

  /**
   * Sends an LLM response back for TTS synthesis: one non-final {@code agent_response} with the
   * text, followed by a final empty one.
   *
   * <p>Called on a transcript handler's own thread, the response is tagged with that handler's
   * turn. Called from any other thread (an async LLM callback, say), it is tagged with the most
   * recent turn. Called before any transcript has arrived, it logs a warning and sends nothing.
   *
   * @throws IllegalStateException if the session is closed
   */
  public void sendResponse(String response) {
    Turn turn = turnForSend();
    if (turn == null) {
      return;
    }
    log("sending response, event_id=" + turn.eventId);
    sendAgentResponse(response, false, turn.eventId);
    sendAgentResponse("", true, turn.eventId);
  }

  /**
   * Streams an LLM response back for TTS synthesis. Each chunk is a {@code String} or an
   * OpenAI/Anthropic/Gemini stream event (a Jackson {@code JsonNode}, a {@code Map}, or any object
   * Jackson can serialise), whose text is extracted; chunks without text are skipped. A final empty
   * {@code agent_response} is sent when the stream ends.
   *
   * <p>Iteration stops without the final message when the session closes or when the turn is
   * interrupted by a newer transcript, so an interrupted stream stops consuming the LLM.
   *
   * @throws IllegalStateException if the session is closed
   */
  public void sendResponse(Iterable<?> chunks) {
    Turn turn = turnForSend();
    if (turn == null) {
      return;
    }
    int sent = 0;
    Iterator<?> iterator = chunks.iterator();
    while (iterator.hasNext()) {
      Object chunk = iterator.next();
      if (closed || turn.signal.isAborted()) {
        log("stream stopped after " + sent + " chunks, event_id=" + turn.eventId);
        return;
      }
      String text = extractText(chunk);
      if (text != null && !text.isEmpty()) {
        sent++;
        sendAgentResponse(text, false, turn.eventId);
      }
    }
    if (!closed && !turn.signal.isAborted()) {
      log("stream complete: " + sent + " chunks sent, event_id=" + turn.eventId);
      sendAgentResponse("", true, turn.eventId);
    }
  }

  /**
   * Streams an LLM response; see {@link #sendResponse(Iterable)}. The stream is closed when the
   * reply ends, including when a newer transcript interrupts it, so a stream backed by an HTTP body
   * releases its connection.
   */
  public void sendResponse(Stream<?> chunks) {
    try {
      sendIterable(chunks);
    } finally {
      chunks.close();
    }
  }

  private void sendIterable(Stream<?> chunks) {
    Iterable<Object> iterable = () -> {
      Iterator<?> iterator = chunks.iterator();
      return new Iterator<Object>() {
        @Override
        public boolean hasNext() {
          return iterator.hasNext();
        }

        @Override
        public Object next() {
          return iterator.next();
        }
      };
    };
    sendResponse(iterable);
  }

  /**
   * Notifies the session that the connection ended at the transport level. Fires the disconnect
   * handlers once and interrupts the running turn. The bundled transports call this; call it
   * yourself when driving the session over your own transport.
   */
  public void handleDisconnect() {
    if (!disconnectFired.compareAndSet(false, true)) {
      return;
    }
    abort(markClosed());
    log("disconnected");
    for (Consumer<SpeechEngineSession> handler : disconnectHandlers) {
      guarded(() -> handler.accept(this));
    }
  }

  /** Closes the session and its underlying connection. */
  public void close() {
    Turn previous;
    synchronized (lock) {
      if (closed) {
        return;
      }
      closed = true;
      previous = currentTurn;
      currentTurn = null;
    }
    abort(previous);
    try {
      closeConnection.run();
    } catch (RuntimeException e) {
      log("close failed: " + e.getMessage());
    }
  }

  /** Reports a transport-level error to the error handlers. */
  void handleTransportError(Throwable error) {
    emitError(error);
  }

  private void handleMessage(JsonNode message) {
    String type = text(message, "type");
    if (type == null) {
      return;
    }
    switch (type) {
      case "init":
        conversationId = text(message, "conversation_id");
        log("session initialized, conversation_id=" + conversationId);
        String id = conversationId != null ? conversationId : "";
        for (BiConsumer<String, SpeechEngineSession> handler : initHandlers) {
          guarded(() -> handler.accept(id, this));
        }
        break;
      case "user_transcript":
        handleUserTranscript(message);
        break;
      case "ping":
        ObjectNode pong = MAPPER.createObjectNode();
        pong.put("type", "pong");
        sendJson(pong);
        break;
      case "close":
        abort(markClosed());
        log("received close");
        for (Consumer<SpeechEngineSession> handler : closeHandlers) {
          guarded(() -> handler.accept(this));
        }
        break;
      case "error":
        String error = text(message, "message");
        emitError(new ElevenlabsApiException(error != null ? error : "Speech Engine error"));
        break;
      default:
        // Unknown message types are ignored so the SDK stays forward-compatible.
        break;
    }
  }

  private void handleUserTranscript(JsonNode message) {
    JsonNode eventIdNode = message.get("event_id");
    Long eventId = eventIdNode != null && eventIdNode.canConvertToLong() && eventIdNode.isNumber()
      ? eventIdNode.asLong()
      : null;

    Turn previous;
    Turn turn;
    synchronized (lock) {
      // Do not start a turn after the session has closed.
      if (closed) {
        return;
      }
      // A repeat of the transcript already being handled is ignored.
      if (eventId != null && currentTurn != null && eventId.equals(currentTurn.eventId)) {
        log("skipping duplicate transcript, event_id=" + eventId);
        return;
      }
      previous = currentTurn;
      turn = new Turn(eventId);
      currentTurn = turn;
    }
    if (previous != null) {
      log(
        "interrupted: aborting previous turn (event_id=" + previous.eventId +
        ") for new transcript (event_id=" + eventId + ")"
      );
      previous.signal.abort();
    }

    List<ConversationMessage> transcript = new ArrayList<>();
    JsonNode messages = message.get("user_transcript");
    if (messages != null && messages.isArray()) {
      for (JsonNode item : messages) {
        String role = text(item, "role");
        String content = text(item, "content");
        if (role != null && content != null) {
          transcript.add(new ConversationMessage(role, content));
        }
      }
    }
    List<ConversationMessage> view = Collections.unmodifiableList(transcript);
    log("received transcript, event_id=" + eventId + ", messages=" + view.size());

    for (TranscriptHandler handler : transcriptHandlers) {
      try {
        executor.execute(() -> runTurn(handler, view, turn));
      } catch (RejectedExecutionException e) {
        emitError(e);
      }
    }
  }

  private void runTurn(TranscriptHandler handler, List<ConversationMessage> transcript, Turn turn) {
    activeTurn.set(turn);
    try {
      handler.onTranscript(transcript, turn.signal, this);
    } catch (Throwable t) {
      if (isCancellation(t)) {
        log("turn ended by cancellation, event_id=" + turn.eventId);
      } else {
        emitError(t);
      }
    } finally {
      activeTurn.remove();
    }
  }

  private Turn turnForSend() {
    if (closed) {
      throw new IllegalStateException("Cannot send response: session is closed");
    }
    Turn turn = activeTurn.get();
    if (turn != null) {
      return turn;
    }
    synchronized (lock) {
      turn = currentTurn;
    }
    if (turn == null) {
      System.err.println(
        "[SpeechEngine] sendResponse() called before any user transcript. Responses can only be " +
        "sent in reply to a user transcript. To have the agent speak first, set a first message " +
        "in your Speech Engine conversation config."
      );
    }
    return turn;
  }

  /** Marks the session closed and detaches the running turn, returning it so it can be aborted. */
  private Turn markClosed() {
    synchronized (lock) {
      closed = true;
      Turn previous = currentTurn;
      currentTurn = null;
      return previous;
    }
  }

  private static void abort(Turn turn) {
    if (turn != null) {
      turn.signal.abort();
    }
  }

  private void sendAgentResponse(String content, boolean isFinal, Long eventId) {
    ObjectNode message = MAPPER.createObjectNode();
    message.put("type", "agent_response");
    message.put("content", content);
    if (eventId != null) {
      message.put("event_id", eventId);
    }
    message.put("is_final", isFinal);
    sendJson(message);
  }

  private void sendJson(ObjectNode message) {
    if (closed) {
      return;
    }
    try {
      String frame = MAPPER.writeValueAsString(message);
      synchronized (sendLock) {
        send.accept(frame);
      }
    } catch (Exception e) {
      // The connection is gone; the transport reports the disconnect.
      log("send failed: " + e.getMessage());
    }
  }

  private void emitError(Throwable error) {
    if (errorHandlers.isEmpty()) {
      log("unhandled error: " + error);
      return;
    }
    for (BiConsumer<Throwable, SpeechEngineSession> handler : errorHandlers) {
      try {
        handler.accept(error, this);
      } catch (Throwable t) {
        System.err.println("[SpeechEngine] unhandled error in error handler: " + t);
      }
    }
  }

  private void guarded(Runnable action) {
    try {
      action.run();
    } catch (Throwable t) {
      emitError(t);
    }
  }

  private void log(String message) {
    if (debug) {
      System.err.println("[SpeechEngine] " + message);
    }
  }

  private static boolean isCancellation(Throwable t) {
    for (Throwable cause = t; cause != null; cause = cause.getCause()) {
      if (cause instanceof CancellationException || cause instanceof InterruptedException) {
        return true;
      }
      if (cause.getCause() == cause) {
        break;
      }
    }
    return false;
  }

  private static String text(JsonNode node, String key) {
    JsonNode value = node.get(key);
    return value != null && value.isTextual() ? value.asText() : null;
  }

  /**
   * Extracts text from one LLM stream chunk. Handles plain strings and the common streaming
   * shapes: OpenAI Responses ({@code response.output_text.delta}), OpenAI Chat Completions
   * ({@code choices[0].delta.content}), Anthropic ({@code content_block_delta} /
   * {@code text_delta}), and Google Gemini (text parts of {@code candidates[0].content.parts},
   * excluding thoughts).
   */
  static String extractText(Object chunk) {
    if (chunk == null) {
      return null;
    }
    if (chunk instanceof CharSequence) {
      return chunk.toString();
    }
    JsonNode event;
    try {
      event = chunk instanceof JsonNode ? (JsonNode) chunk : MAPPER.valueToTree(chunk);
    } catch (RuntimeException e) {
      return null;
    }
    if (event == null || !event.isObject()) {
      return null;
    }

    // OpenAI Responses API: { type: "response.output_text.delta", delta: "text" }
    if ("response.output_text.delta".equals(text(event, "type"))) {
      String delta = text(event, "delta");
      if (delta != null) {
        return delta;
      }
    }

    // OpenAI Chat Completions API: { choices: [{ delta: { content: "text" } }] }
    JsonNode choices = event.get("choices");
    if (choices != null && choices.isArray() && choices.size() > 0) {
      JsonNode delta = choices.get(0).get("delta");
      if (delta != null && delta.isObject()) {
        String content = text(delta, "content");
        if (content != null) {
          return content;
        }
      }
    }

    // Anthropic Messages API: { type: "content_block_delta", delta: { type: "text_delta", text } }
    if ("content_block_delta".equals(text(event, "type"))) {
      JsonNode delta = event.get("delta");
      if (delta != null && delta.isObject() && "text_delta".equals(text(delta, "type"))) {
        String value = text(delta, "text");
        if (value != null) {
          return value;
        }
      }
    }

    // Google Gemini API: { candidates: [{ content: { parts: [{ text: "text" }] } }] }
    JsonNode candidates = event.get("candidates");
    if (candidates != null && candidates.isArray() && candidates.size() > 0) {
      JsonNode content = candidates.get(0).get("content");
      JsonNode parts = content != null ? content.get("parts") : null;
      if (parts != null && parts.isArray()) {
        StringBuilder joined = new StringBuilder();
        for (JsonNode part : parts) {
          JsonNode thought = part.get("thought");
          if (thought != null && thought.isBoolean() && thought.asBoolean()) {
            continue;
          }
          String value = text(part, "text");
          if (value != null) {
            joined.append(value);
          }
        }
        if (joined.length() > 0) {
          return joined.toString();
        }
      }
    }

    return null;
  }

  private static final class Turn {

    final Long eventId;
    final AbortSignal signal = new AbortSignal();

    Turn(Long eventId) {
      this.eventId = eventId;
    }
  }

  /** Shared pool for transcript handlers: daemon threads, so it never keeps the JVM alive. */
  private static final class DefaultExecutor {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    static final ExecutorService INSTANCE = Executors.newCachedThreadPool(runnable -> {
      Thread thread = new Thread(
        runnable,
        "elevenlabs-speech-engine-" + COUNTER.incrementAndGet()
      );
      thread.setDaemon(true);
      return thread;
    });
  }
}
