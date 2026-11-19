package com.elevenlabs.api.speechengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.websocket.CloseReason;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

/** The Jakarta endpoint's own joining of partial messages, driven through a stub session. */
class SpeechEngineJakartaPartialTest {

  private final List<String> sent = new ArrayList<>();
  private final List<CloseReason> closes = new ArrayList<>();
  private final AtomicReference<MessageHandler.Partial<String>> handler = new AtomicReference<>();

  @SuppressWarnings("unchecked")
  private void open() {
    RemoteEndpoint.Basic remote = (RemoteEndpoint.Basic) Proxy.newProxyInstance(
      getClass().getClassLoader(),
      new Class<?>[] { RemoteEndpoint.Basic.class },
      (proxy, method, args) -> {
        if (method.getName().equals("sendText") && args.length == 1) {
          sent.add((String) args[0]);
        }
        return null;
      }
    );
    Session socket = (Session) Proxy.newProxyInstance(
      getClass().getClassLoader(),
      new Class<?>[] { Session.class },
      (proxy, method, args) -> {
        switch (method.getName()) {
          case "addMessageHandler":
            handler.set((MessageHandler.Partial<String>) args[args.length - 1]);
            return null;
          case "getBasicRemote":
            return remote;
          case "close":
            closes.add(args != null && args.length == 1 ? (CloseReason) args[0] : null);
            return null;
          default:
            return null;
        }
      }
    );
    new SpeechEngineJakarta.SpeechEngineEndpoint(new SpeechEngineCallbacks()).onOpen(socket, null);
    assertNotNull(handler.get());
  }

  @Test
  void piecesAreJoinedIntoOneMessage() {
    open();
    handler.get().onMessage("{\"type\":", false);
    handler.get().onMessage("\"ping\"}", true);
    assertEquals(1, sent.size());
    assertTrue(sent.get(0).contains("pong"));
  }

  @Test
  void theRestOfAnOversizedMessageIsDropped() {
    open();
    char[] big = new char[SpeechEngineJakarta.MAX_MESSAGE_CHARS + 1];
    Arrays.fill(big, 'x');
    handler.get().onMessage(new String(big), false);
    // The tail of the oversized message must not be parsed as a message of its own.
    handler.get().onMessage("{\"type\":\"ping\"}", true);
    assertEquals(0, sent.size());
    assertEquals(1, closes.size());
    assertEquals(CloseReason.CloseCodes.TOO_BIG, closes.get(0).getCloseCode());
  }
}
