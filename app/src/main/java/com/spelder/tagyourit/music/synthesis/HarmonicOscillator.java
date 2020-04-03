package com.spelder.tagyourit.music.synthesis;

import com.spelder.tagyourit.music.model.AudioFormat;

/**
 * Creates a sound using additive synthesis and the harmonic scale. Have the ability to the
 * frequency of the pitch and the magnitudes of the pitches on the harmonic scale.
 */
public class HarmonicOscillator {
  private double[] magnitudes = {1};

  private double[] phase = new double[magnitudes.length];

  private double frequency;

  private double multiplier = 1;

  private double gainMultiplier = 1;

  private final int sampleRate;

  HarmonicOscillator(AudioFormat audioFormat) {
    setFrequency(440);
    this.sampleRate = audioFormat.getSampleRate();
  }

  /**
   * Set frequency of the oscillator in Hz.
   *
   * @param frequency Frequency in Hz for this oscillator
   */
  public void setFrequency(double frequency) {

    this.frequency = frequency;
  }

  public void setGainMultiplier(double gainMultiplier) {
    this.gainMultiplier = gainMultiplier;
  }

  public void setHarmonicMagnitudes(double[] magnitudes) {
    this.magnitudes = magnitudes;
    phase = new double[magnitudes.length];

    double sum = 0;
    for (double magnitude : magnitudes) {
      sum += magnitude;
    }
    multiplier = 1 / sum;
  }

  protected double getSample() {
    double sample = 0;
    for (int k = 1; k <= magnitudes.length; k++) {
      int id = k - 1;
      double phase_inc = 2 * Math.PI * frequency * k / sampleRate;
      sample += gainMultiplier * multiplier * magnitudes[id] * Math.cos(phase[id]);
      phase[id] += phase_inc;
      if (phase[id] >= 2 * Math.PI) {
        phase[id] -= 2 * Math.PI;
      }
    }

    return sample;
  }

  /**
   * Get a buffer of oscillator samples
   *
   * @param buffer Array to fill with samples
   * @return Count of bytes produced.
   */
  public byte[] getSamples(byte[] buffer) {
    int index = 0;
    for (int i = 0; i < buffer.length / 2; i++) {
      double ds = getSample() * Short.MAX_VALUE;
      short ss = (short) Math.round(ds);
      buffer[index++] = (byte) (ss & 0xff);
      buffer[index++] = (byte) ((ss >> 8) & 0xff);
    }
    return buffer;
  }
}
