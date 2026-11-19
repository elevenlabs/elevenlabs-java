package com.elevenlabs.api.speechengine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

/**
 * Fires when the transcript turn it belongs to is interrupted: a newer user transcript arrived, the
 * API sent {@code close}, or the connection dropped. Pass it to (or poll it from) a long-running
 * LLM call so the work stops as soon as the user starts speaking again.
 *
 * <p>Hand-written (see {@code .fernignore}); the Java counterpart of the {@code AbortSignal} handed
 * to {@code onTranscript} in the TypeScript SDK.
 */
public final class AbortSignal {

  private final List<Runnable> listeners = new ArrayList<>();
  private boolean aborted;

  AbortSignal() {}

  /** Whether the turn has been interrupted. */
  public synchronized boolean isAborted() {
    return aborted;
  }

  /**
   * Runs {@code listener} once when the turn is interrupted, on the thread that interrupts it. If the
   * turn is already interrupted the listener runs immediately on the calling thread.
   */
  public void onAbort(Runnable listener) {
    synchronized (this) {
      if (!aborted) {
        listeners.add(listener);
        return;
      }
    }
    listener.run();
  }

  /** Throws {@link CancellationException} if the turn has been interrupted. */
  public void throwIfAborted() {
    if (isAborted()) {
      throw new CancellationException("Speech Engine turn was interrupted");
    }
  }

  void abort() {
    List<Runnable> toRun;
    synchronized (this) {
      if (aborted) {
        return;
      }
      aborted = true;
      toRun = new ArrayList<>(listeners);
      listeners.clear();
    }
    for (Runnable listener : toRun) {
      try {
        listener.run();
      } catch (RuntimeException e) {
        // A failing listener must not stop the others or the turn that is replacing this one.
      }
    }
  }
}
