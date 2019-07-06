package com.spelder.tagyourit.music.player;

import android.util.Log;
import com.spelder.tagyourit.music.listener.OnCompletionListener;
import com.spelder.tagyourit.music.listener.OnErrorListener;
import com.spelder.tagyourit.music.listener.OnPreparedListener;
import com.spelder.tagyourit.music.model.AudioFormat;
import com.spelder.tagyourit.music.model.Speed;
import com.spelder.tagyourit.music.processor.AudioProcessor;
import com.spelder.tagyourit.music.synthesis.Instrument;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AudioPlayer {
  static final int BUFFER_SIZE = 4096 * 2;

  private static final int THREAD_WAIT_TIMEOUT = 200;

  private static final String TAG = "AudioPlayer";

  private final List<AudioProcessor> processors = new ArrayList<>();

  private final EventHandler eventHandler;

  private final PlayerState state;

  private PipedOutputStream outputStream;

  private PipedInputStream inputStream;

  private AudioFormat format;

  private TrackWriter trackWriter;

  private Thread trackWriterThread;

  private AudioExtractor audioExtractor;

  private Thread audioExtractorThread;

  public AudioPlayer() {
    eventHandler = new EventHandler(this);
    state = new PlayerState();
  }

  public void initialize(double frequency, Instrument instrument) {
    try {
      initialize();
      if (audioExtractor == null) {
        audioExtractor =
            new SynthesisAudioExtractor(outputStream, inputStream, eventHandler, state);
        ((SynthesisAudioExtractor) audioExtractor).setFrequency(frequency);
        ((SynthesisAudioExtractor) audioExtractor).setInstrument(instrument);
        format = audioExtractor.initialize();
      }
      if (trackWriter == null) {
        trackWriter = new TrackWriter(inputStream, format, state);
        trackWriter.setProcessor(processors);
        audioExtractor.setTrackWriter(trackWriter);
      }

      eventHandler.sendOnPreparedEvent();
    } catch (IOException e) {
      Log.e(TAG, "Cannot initialize:", e);
    }
  }

  public void initialize(FileDescriptor audioFile) {
    try {
      initialize();
      if (audioExtractor == null) {
        audioExtractor = new FileAudioExtractor(outputStream, inputStream, eventHandler, state);
        ((FileAudioExtractor) audioExtractor).setAudioFile(audioFile);
        format = audioExtractor.initialize();
      }
      if (trackWriter == null) {
        trackWriter = new TrackWriter(inputStream, format, state);
        trackWriter.setProcessor(processors);
        audioExtractor.setTrackWriter(trackWriter);
      }

      eventHandler.sendOnPreparedEvent();
    } catch (IOException e) {
      Log.e(TAG, "Cannot initialize:", e);
    }
  }

  private void initialize() throws IOException {
    if (outputStream == null) {
      outputStream = new PipedOutputStream();
    }
    if (inputStream == null) {
      inputStream = new PipedInputStream(outputStream, BUFFER_SIZE * 100);
    }
  }

  public void start() {
    Log.d(TAG, "Start");

    if (state.isUninitialized()) {
      state.start();

      Log.d(TAG, "Creating new processing threads");
      audioExtractorThread = new Thread(audioExtractor);
      audioExtractorThread.start();
      trackWriterThread = new Thread(trackWriter);
      trackWriterThread.start();
    } else {
      state.start();
    }
  }

  public void pause() {
    state.pause();
  }

  public void stop() {
    state.stop();
  }

  public boolean isNotStopped() {
    return !state.isStopped();
  }

  public boolean isPlaying() {
    return state.isPlaying();
  }

  public void reset() {
    stop();
    waitForStop();
    destroy();
  }

  private void waitForStop() {
    try {
      int waitTime = 0;
      while (audioExtractor != null
          && audioExtractor.isRunning()
          && waitTime < THREAD_WAIT_TIMEOUT) {
        Log.d(TAG, "Waiting for audioExtractor to stop");
        Thread.sleep(5);
        waitTime += 5;
      }

      if (waitTime >= THREAD_WAIT_TIMEOUT && audioExtractorThread != null) {
        audioExtractorThread.interrupt();
      }
    } catch (InterruptedException e) {
      Log.e(TAG, "Interrupted while waiting", e);
    }

    try {
      int waitTime = 0;
      while (trackWriter != null && trackWriter.isRunning() && waitTime < THREAD_WAIT_TIMEOUT) {
        Log.d(TAG, "Waiting for trackWriter to stop");
        Thread.sleep(5);
        waitTime += 5;
      }

      if (waitTime >= THREAD_WAIT_TIMEOUT && trackWriterThread != null) {
        trackWriterThread.interrupt();
      }
    } catch (InterruptedException e) {
      Log.e(TAG, "Interrupted while waiting", e);
    }
  }

  private void destroy() {
    if (audioExtractor != null) {
      audioExtractor.clearInputStream();
      audioExtractor = null;
      audioExtractorThread = null;
    }

    if (trackWriter != null) {
      trackWriter.destroy();
      trackWriter = null;
      trackWriterThread = null;
    }

    if (outputStream != null) {
      try {
        outputStream.flush();
        outputStream.close();
      } catch (IOException e) {
        Log.e(TAG, "Failed to close", e);
      }
      outputStream = null;
    }

    if (inputStream != null) {
      try {
        inputStream.close();
      } catch (IOException e) {
        Log.e(TAG, "Failed to close", e);
      }
      inputStream = null;
    }
  }

  public int getCurrentPosition() {
    if (audioExtractor != null) {
      return audioExtractor.getCurrentPosition();
    }
    return 0;
  }

  public int getDurationMillis() {
    return (int) format.getDurationMillis();
  }

  public void seekTo(long position) {
    audioExtractor.seekTo(position);
  }

  public void setOnPreparedListener(OnPreparedListener listener) {
    eventHandler.setOnPreparedListener(listener);
  }

  public void setOnCompletionListener(OnCompletionListener listener) {
    eventHandler.setOnCompletionListener(listener);
  }

  public void setOnErrorListener(OnErrorListener listener) {
    eventHandler.setOnErrorListener(listener);
  }

  public void addProcessor(AudioProcessor processor) {
    processors.add(processor);
  }

  public boolean setSpeed(Speed speed) {
    return trackWriter.setSpeed(speed);
  }

  public int getChannelSize() {
    return BUFFER_SIZE / 4;
  }
}
