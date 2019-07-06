package com.spelder.tagyourit.music.player;

import android.util.Log;
import com.spelder.tagyourit.music.model.AudioFormat;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Common class for extracting audio from a source to the raw audio buffer needed for processing.
 */
abstract class AudioExtractor implements Runnable {
  private static final String TAG = TrackWriter.class.getName();

  final PipedOutputStream outputStream;

  final PipedInputStream inputStream;

  final EventHandler eventHandler;

  final PlayerState state;

  protected AudioFormat format;

  long presentationTime = 0;

  private TrackWriter trackWriter;

  AudioExtractor(
      PipedOutputStream outputStream,
      PipedInputStream inputStream,
      EventHandler eventHandler,
      PlayerState state) {
    this.outputStream = outputStream;
    this.inputStream = inputStream;
    this.eventHandler = eventHandler;
    this.state = state;
  }

  abstract AudioFormat initialize();

  @Override
  public abstract void run();

  void setTrackWriter(TrackWriter trackWriter) {
    this.trackWriter = trackWriter;
  }

  int getCurrentPosition() {
    try {
      int r = (int) (presentationTime / 1000);
      int s = (int) ((double) (inputStream.available()) * 800 / format.getBitrate());

      if (r - s < 0) {
        return 1;
      }
      return r - s;
    } catch (IOException | ArithmeticException | NullPointerException e) {
      return (int) (presentationTime / 1000);
    }
  }

  synchronized void seekTo(long position) {
    if (!state.isRunning()) {
      return;
    }

    Log.d(TAG, "Seek to: " + position);

    presentationTime = position * 1000;
    performSeek(presentationTime);

    boolean playAfter = true;
    if (!state.isPlaying()) {
      playAfter = false;
    }
    state.pause();
    clearInputStream();
    if (playAfter) {
      state.start();
    }

    Log.d(TAG, "Exiting seek to");
  }

  protected abstract void performSeek(long timeMillis);

  void clearInputStream() {
    if (inputStream == null) {
      return;
    }

    try {
      long skippedBytes = inputStream.skip(inputStream.available());
      Log.d(TAG, "Skipped bytes: " + skippedBytes);
    } catch (IOException e) {
      Log.d(TAG, "Cannot skip", e);
    }

    if (trackWriter != null) {
      trackWriter.clear();
    }
  }

  protected void cleanup() {
    presentationTime = 0;
    format = null;
  }

  boolean isRunning() {
    return !state.isUninitialized();
  }
}
