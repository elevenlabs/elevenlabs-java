package com.elevenlabs.api.speechengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elevenlabs.api.core.ObjectMappers;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SpeechEngineSessionTest {

  private final List<String> sent = new CopyOnWriteArrayList<>();
  private final AtomicInteger closes = new AtomicInteger();
  private final ExecutorService pool = Executors.newCachedThreadPool();

  @AfterEach
  void tearDown() {
    pool.shutdownNow();
  }

  /** A session whose transcript handlers run inline, so assertions follow handleRawMessage. */
  private SpeechEngineSession inlineSession() {
    return new SpeechEngineSession(sent::add, closes::incrementAndGet, false, Runnable::run);
  }

  private List<JsonNode> frames() throws Exception {
    List<JsonNode> frames = new ArrayList<>();
    for (String frame : sent) {
      frames.add(ObjectMappers.JSON_MAPPER.readTree(frame));
    }
    return frames;
  }

  private static String transcript(long eventId, String text) {
    return "{\"type\":\"user_transcript\",\"event_id\":" + eventId +
      ",\"user_transcript\":[{\"role\":\"user\",\"content\":\"" + text + "\"}]}";
  }

  @Test
  void initSetsConversationIdAndFiresHandler() {
    SpeechEngineSession session = inlineSession();
    AtomicReference<String> seen = new AtomicReference<>();
    session.onInit((id, s) -> seen.set(id));

    assertFalse(session.getConversationId().isPresent());
    session.handleRawMessage("{\"type\":\"init\",\"conversation_id\":\"conv_1\"}");

    assertEquals("conv_1", seen.get());
    assertEquals("conv_1", session.getConversationId().get());
  }

  @Test
  void pingReceivesPong() throws Exception {
    inlineSession().handleRawMessage("{\"type\":\"ping\"}");
    assertEquals(1, sent.size());
    assertEquals("pong", frames().get(0).get("type").asText());
  }

  @Test
  void transcriptDeliversMessagesAndStringResponse() throws Exception {
    SpeechEngineSession session = inlineSession();
    AtomicReference<List<ConversationMessage>> seen = new AtomicReference<>();
    session.onTranscript((transcript, signal, s) -> {
      seen.set(transcript);
      s.sendResponse("Hello there");
    });

    session.handleRawMessage(
      "{\"type\":\"user_transcript\",\"event_id\":7,\"user_transcript\":[" +
      "{\"role\":\"user\",\"content\":\"Hi\"},{\"role\":\"agent\",\"content\":\"Hey\"}," +
      "{\"role\":\"user\",\"content\":\"How are you?\"}]}"
    );

    assertEquals(
      Arrays.asList(
        new ConversationMessage("user", "Hi"),
        new ConversationMessage("agent", "Hey"),
        new ConversationMessage("user", "How are you?")
      ),
      seen.get()
    );
    List<JsonNode> frames = frames();
    assertEquals(2, frames.size());
    assertAgentResponse(frames.get(0), "Hello there", 7, false);
    assertAgentResponse(frames.get(1), "", 7, true);
  }

  @Test
  void transcriptWithoutEventIdOmitsIt() throws Exception {
    SpeechEngineSession session = inlineSession();
    session.onTranscript((transcript, signal, s) -> s.sendResponse("ok"));
    session.handleRawMessage("{\"type\":\"user_transcript\",\"user_transcript\":[]}");

    JsonNode first = frames().get(0);
    assertEquals("ok", first.get("content").asText());
    assertFalse(first.has("event_id"));
  }

  @Test
  void streamedResponseSendsChunksThenFinal() throws Exception {
    SpeechEngineSession session = inlineSession();
    session.onTranscript((transcript, signal, s) -> s.sendResponse(Arrays.asList("Hel", "", "lo"))
    );
    session.handleRawMessage(transcript(3, "hi"));

    List<JsonNode> frames = frames();
    assertEquals(3, frames.size());
    assertAgentResponse(frames.get(0), "Hel", 3, false);
    assertAgentResponse(frames.get(1), "lo", 3, false);
    assertAgentResponse(frames.get(2), "", 3, true);
  }

  @Test
  void streamedResponseAcceptsAJavaStream() throws Exception {
    SpeechEngineSession session = inlineSession();
    session.onTranscript((transcript, signal, s) -> s.sendResponse(Stream.of("a", "b")));
    session.handleRawMessage(transcript(1, "hi"));
    assertEquals(3, frames().size());
  }

  @Test
  void streamedResponseExtractsLlmStreamEvents() throws Exception {
    SpeechEngineSession session = inlineSession();
    List<Object> chunks = new ArrayList<>();
    // OpenAI Chat Completions, as a Map.
    Map<String, Object> delta = new HashMap<>();
    delta.put("content", "one ");
    Map<String, Object> choice = new HashMap<>();
    choice.put("delta", delta);
    Map<String, Object> chat = new HashMap<>();
    chat.put("choices", Collections.singletonList(choice));
    chunks.add(chat);
    // OpenAI Responses, Anthropic and Gemini, as JsonNodes.
    chunks.add(json("{\"type\":\"response.output_text.delta\",\"delta\":\"two \"}"));
    chunks.add(
      json("{\"type\":\"content_block_delta\",\"delta\":{\"type\":\"text_delta\",\"text\":\"three \"}}")
    );
    chunks.add(
      json(
        "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"hmm\",\"thought\":true}," +
        "{\"text\":\"four\"}]}}]}"
      )
    );
    // Events without text are skipped.
    chunks.add(json("{\"type\":\"response.created\"}"));
    session.onTranscript((transcript, signal, s) -> s.sendResponse(chunks));
    session.handleRawMessage(transcript(5, "count"));

    List<JsonNode> frames = frames();
    assertEquals(5, frames.size());
    assertEquals("one ", frames.get(0).get("content").asText());
    assertEquals("two ", frames.get(1).get("content").asText());
    assertEquals("three ", frames.get(2).get("content").asText());
    assertEquals("four", frames.get(3).get("content").asText());
    assertAgentResponse(frames.get(4), "", 5, true);
  }

  @Test
  void sendResponseBeforeAnyTranscriptIsSuppressed() {
    SpeechEngineSession session = inlineSession();
    session.sendResponse("too early");
    assertTrue(sent.isEmpty());
  }

  @Test
  void sendResponseOffTheHandlerThreadUsesTheLatestTurn() throws Exception {
    SpeechEngineSession session = inlineSession();
    session.onTranscript((transcript, signal, s) -> {});
    session.handleRawMessage(transcript(11, "hi"));

    Thread other = new Thread(() -> session.sendResponse("later"));
    other.start();
    other.join();

    assertAgentResponse(frames().get(0), "later", 11, false);
  }

  @Test
  void newerTranscriptAbortsThePreviousTurnWhichKeepsItsOwnEventId() throws Exception {
    SpeechEngineSession session = new SpeechEngineSession(sent::add, null, false, pool);
    CountDownLatch firstStarted = new CountDownLatch(1);
    CountDownLatch firstAborted = new CountDownLatch(1);
    CountDownLatch bothDone = new CountDownLatch(2);
    session.onTranscript((transcript, signal, s) -> {
      try {
        if ("first".equals(transcript.get(0).getContent())) {
          signal.onAbort(firstAborted::countDown);
          firstStarted.countDown();
          // A slow LLM call: wait for the interruption, then reply anyway.
          assertTrue(firstAborted.await(5, TimeUnit.SECONDS));
          s.sendResponse("stale");
        } else {
          s.sendResponse("fresh");
        }
      } finally {
        bothDone.countDown();
      }
    });

    session.handleRawMessage(transcript(1, "first"));
    assertTrue(firstStarted.await(5, TimeUnit.SECONDS));
    session.handleRawMessage(transcript(2, "second"));
    assertTrue(bothDone.await(5, TimeUnit.SECONDS));

    Map<String, Long> eventIdByContent = new HashMap<>();
    for (JsonNode frame : frames()) {
      if (!frame.get("content").asText().isEmpty()) {
        eventIdByContent.put(frame.get("content").asText(), frame.get("event_id").asLong());
      }
    }
    assertEquals(Long.valueOf(1), eventIdByContent.get("stale"));
    assertEquals(Long.valueOf(2), eventIdByContent.get("fresh"));
  }

  @Test
  void interruptedStreamStopsWithoutAFinalMessage() throws Exception {
    SpeechEngineSession session = new SpeechEngineSession(sent::add, null, false, pool);
    CountDownLatch firstChunkSent = new CountDownLatch(1);
    CountDownLatch interrupted = new CountDownLatch(1);
    CountDownLatch streamDone = new CountDownLatch(1);
    session.onTranscript((transcript, signal, s) -> {
      if (!"first".equals(transcript.get(0).getContent())) {
        return;
      }
      Iterable<String> llm = () -> new java.util.Iterator<String>() {
        int index;

        @Override
        public boolean hasNext() {
          return index < 3;
        }

        @Override
        public String next() {
          if (index == 1) {
            firstChunkSent.countDown();
            try {
              interrupted.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          }
          return "chunk" + index++;
        }
      };
      try {
        s.sendResponse(llm);
      } finally {
        streamDone.countDown();
      }
    });

    session.handleRawMessage(transcript(1, "first"));
    assertTrue(firstChunkSent.await(5, TimeUnit.SECONDS));
    session.handleRawMessage(transcript(2, "second"));
    interrupted.countDown();
    assertTrue(streamDone.await(5, TimeUnit.SECONDS));

    List<JsonNode> frames = frames();
    assertEquals(1, frames.size(), "only the chunk sent before the interruption");
    assertAgentResponse(frames.get(0), "chunk0", 1, false);
  }

  @Test
  void duplicateTranscriptIsSkipped() {
    SpeechEngineSession session = inlineSession();
    AtomicInteger calls = new AtomicInteger();
    session.onTranscript((transcript, signal, s) -> calls.incrementAndGet());
    session.handleRawMessage(transcript(4, "hi"));
    session.handleRawMessage(transcript(4, "hi"));
    assertEquals(1, calls.get());
  }

  @Test
  void transcriptAfterCloseStartsNoHandler() {
    SpeechEngineSession session = inlineSession();
    AtomicInteger calls = new AtomicInteger();
    session.onTranscript((transcript, signal, s) -> calls.incrementAndGet());
    session.handleRawMessage("{\"type\":\"close\"}");
    session.handleRawMessage(transcript(1, "hi"));
    assertEquals(0, calls.get());
  }

  @Test
  void closeMessageFiresCloseAbortsTheTurnAndClosesTheSession() {
    SpeechEngineSession session = inlineSession();
    AtomicReference<AbortSignal> turnSignal = new AtomicReference<>();
    AtomicInteger closeEvents = new AtomicInteger();
    session.onTranscript((transcript, signal, s) -> turnSignal.set(signal));
    session.onClose(s -> closeEvents.incrementAndGet());

    session.handleRawMessage(transcript(1, "hi"));
    session.handleRawMessage("{\"type\":\"close\"}");

    assertEquals(1, closeEvents.get());
    assertTrue(turnSignal.get().isAborted());
    assertFalse(session.isOpen());
    assertThrows(IllegalStateException.class, () -> session.sendResponse("late"));
  }

  @Test
  void errorMessageFiresError() {
    SpeechEngineSession session = inlineSession();
    AtomicReference<Throwable> seen = new AtomicReference<>();
    session.onError((error, s) -> seen.set(error));
    session.handleRawMessage("{\"type\":\"error\",\"message\":\"boom\"}");
    assertEquals("boom", seen.get().getMessage());
  }

  @Test
  void malformedJsonFiresErrorAndKeepsTheSessionOpen() {
    SpeechEngineSession session = inlineSession();
    AtomicInteger errors = new AtomicInteger();
    session.onError((error, s) -> errors.incrementAndGet());
    session.handleRawMessage("{not json");
    session.handleRawMessage("[1,2]");
    assertEquals(2, errors.get());
    assertTrue(session.isOpen());
  }

  @Test
  void unknownMessageTypesAreIgnored() {
    SpeechEngineSession session = inlineSession();
    AtomicInteger errors = new AtomicInteger();
    session.onError((error, s) -> errors.incrementAndGet());
    session.handleRawMessage("{\"type\":\"something_new\"}");
    assertEquals(0, errors.get());
    assertTrue(sent.isEmpty());
  }

  @Test
  void handlerExceptionsAreReportedButCancellationIsNot() {
    SpeechEngineSession session = inlineSession();
    List<Throwable> errors = new CopyOnWriteArrayList<>();
    session.onError((error, s) -> errors.add(error));
    session.onTranscript((transcript, signal, s) -> {
      if ("cancel".equals(transcript.get(0).getContent())) {
        throw new CancellationException("interrupted");
      }
      throw new IllegalArgumentException("handler failed");
    });

    session.handleRawMessage(transcript(1, "fail"));
    session.handleRawMessage(transcript(2, "cancel"));

    assertEquals(1, errors.size());
    assertEquals("handler failed", errors.get(0).getMessage());
  }

  @Test
  void closeInvokesTheCloseCallbackOnce() {
    SpeechEngineSession session = inlineSession();
    session.close();
    session.close();
    assertEquals(1, closes.get());
    assertFalse(session.isOpen());
  }

  @Test
  void transportDisconnectFiresOnceAndAbortsTheTurn() {
    SpeechEngineSession session = inlineSession();
    AtomicInteger disconnects = new AtomicInteger();
    AtomicReference<AbortSignal> turnSignal = new AtomicReference<>();
    session.onDisconnect(s -> disconnects.incrementAndGet());
    session.onTranscript((transcript, signal, s) -> turnSignal.set(signal));

    session.handleRawMessage(transcript(1, "hi"));
    // A clean close message first must not stop the disconnect event from firing.
    session.handleRawMessage("{\"type\":\"close\"}");
    session.handleDisconnect();
    session.handleDisconnect();

    assertEquals(1, disconnects.get());
    assertTrue(turnSignal.get().isAborted());
  }

  @Test
  void abortSignalRunsLateListenersImmediately() {
    AbortSignal signal = new AbortSignal();
    AtomicInteger runs = new AtomicInteger();
    signal.onAbort(runs::incrementAndGet);
    signal.abort();
    signal.abort();
    signal.onAbort(runs::incrementAndGet);
    assertEquals(2, runs.get());
    assertThrows(CancellationException.class, signal::throwIfAborted);
  }

  @Test
  void extractTextReturnsNullForUnknownShapes() {
    assertNull(SpeechEngineSession.extractText(null));
    assertNull(SpeechEngineSession.extractText(42));
    assertNull(SpeechEngineSession.extractText(json("{\"foo\":\"bar\"}")));
    assertEquals("plain", SpeechEngineSession.extractText(new StringBuilder("plain")));
  }

  private static JsonNode json(String raw) {
    try {
      return ObjectMappers.JSON_MAPPER.readTree(raw);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static void assertAgentResponse(
    JsonNode frame,
    String content,
    long eventId,
    boolean isFinal
  ) {
    assertEquals("agent_response", frame.get("type").asText());
    assertEquals(content, frame.get("content").asText());
    assertEquals(eventId, frame.get("event_id").asLong());
    assertEquals(isFinal, frame.get("is_final").asBoolean());
  }
}
