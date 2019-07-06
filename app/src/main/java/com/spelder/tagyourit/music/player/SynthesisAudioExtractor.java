package com.spelder.tagyourit.music.player;

import static com.spelder.tagyourit.music.player.AudioPlayer.BUFFER_SIZE;

import android.util.Log;
import com.spelder.tagyourit.music.model.AudioFormat;
import com.spelder.tagyourit.music.synthesis.AdvancedHarmonicOscillator;
import com.spelder.tagyourit.music.synthesis.Instrument;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/** Extracts audio from the synthesizer to the raw audio buffer needed for processing. */
public class SynthesisAudioExtractor extends AudioExtractor {
  private static final String TAG = SynthesisAudioExtractor.class.getName();

  private AdvancedHarmonicOscillator extractor;

  private Instrument instrument;

  private double frequency;

  SynthesisAudioExtractor(
      PipedOutputStream outputStream,
      PipedInputStream inputStream,
      EventHandler eventHandler,
      PlayerState state) {
    super(outputStream, inputStream, eventHandler, state);
  }

  void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  void setFrequency(double frequency) {
    this.frequency = frequency;
  }

  @Override
  AudioFormat initialize() {
    format = new AudioFormat();
    extractor = new AdvancedHarmonicOscillator(format);
    extractor.setFrequency(frequency);
    extractor.setHarmonicMagnitudes(instrument.getHarmonicMagnitudes());
    extractor.setLfoFrequency(instrument.getLfoFrequency(frequency));
    extractor.setModulationType(instrument.getModType());
    extractor.setModulationDepth(instrument.getModulationDepth());
    extractor.setGainMultiplier(instrument.getGainMultiplier());
    return format;
  }

  @Override
  public void run() {
    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

    byte[] buffer = new byte[BUFFER_SIZE / 4];
    while (state.isRunning()) {
      state.waitPlay();
      if (!state.isRunning()) {
        break;
      }

      try {
        outputStream.write(extractor.getSamples(buffer));
        outputStream.flush();
      } catch (IOException e) {
        Log.e(TAG, "Cannot write: ", e);
      }
    }

    try {
      while (inputStream.available() >= BUFFER_SIZE) {
        Thread.sleep(1);
      }
    } catch (IOException | InterruptedException e) {
      Log.e(TAG, "Cannot wait to finish", e);
    }
    cleanup();

    if (!state.isStopped()) {
      eventHandler.sendOnCompletionEvent();
    }

    state.uninitialize();
  }

  @Override
  protected void performSeek(long timeMillis) {
    // Do not seek
  }
}
