package com.spelder.tagyourit.music.synthesis;

import com.spelder.tagyourit.music.model.AudioFormat;

/**
 * Uses the harmonic oscillator and a low frequency oscillator to modify the sound. HAve the ability
 * to set the type of modification.
 */
public class AdvancedHarmonicOscillator extends HarmonicOscillator {
  private static final double MOD_DEPTH_MIN = 0.0;

  private static final double MOD_DEPTH_MAX = 1.0;

  private double frequency;

  private HarmonicOscillator lfo;

  private MOD_TYPE modulationType;

  private double modulationDepth;

  private double rangeMultiplier;

  private double detuneMultiplier;

  public AdvancedHarmonicOscillator(AudioFormat audioFormat) {
    super(audioFormat);
    lfo = new HarmonicOscillator(audioFormat);
    rangeMultiplier = 1.0;
    detuneMultiplier = 1.0;
    modulationType = MOD_TYPE.NONE;
  }

  /**
   * Set the frequency of the oscillator in Hz.
   *
   * @param frequency Frequency in Hz for this oscillator
   */
  public void setFrequency(double frequency) {

    this.frequency = frequency;
  }

  /**
   * Set modulation type for oscillator
   *
   * @param modulationType Determines the type of modulation for this oscillator
   */
  public void setModulationType(MOD_TYPE modulationType) {

    this.modulationType = modulationType;
  }

  /**
   * Set modulation depth
   *
   * <p>Controls the depth of the modulation supplied by the LFO to the main oscillator
   *
   * <p>depth must be between MOD_DEPTH_MIN and MOD_DEPTH_MAX.
   *
   * @param modulationDepth The depth of the modulation
   */
  public void setModulationDepth(double modulationDepth) {

    modulationDepth = (modulationDepth < MOD_DEPTH_MIN) ? MOD_DEPTH_MIN : modulationDepth;
    modulationDepth = (modulationDepth > MOD_DEPTH_MAX) ? MOD_DEPTH_MAX : modulationDepth;

    this.modulationDepth = modulationDepth;
  }

  /**
   * Set the frequency of the LFO.
   *
   * @param frequency Frequency in Hz for the LFO oscillator
   */
  public void setLfoFrequency(double frequency) {

    lfo.setFrequency(frequency);
  }

  /**
   * Return the next sample of the oscillator's waveform
   *
   * @return Next oscillator sample
   */
  protected double getSample() {

    double freq = frequency;

    // Are we frequency modulating
    if ((modulationType == MOD_TYPE.FM) && (modulationDepth != 0.0)) {
      double lfoValue = lfo.getSample() * modulationDepth;
      freq *= Math.pow(2.0, lfoValue);
    }
    // Apply frequency multiplier
    freq *= rangeMultiplier;

    // Apply detuning multiplier
    freq *= detuneMultiplier;

    // Set frequency of osc
    super.setFrequency(freq);

    // Get an osc sample
    double sample = super.getSample();

    // Are we amplitude modulating
    if (modulationType == MOD_TYPE.AM) {
      double lfoOffset = (lfo.getSample() + 1.0) / 2.0;
      double m = 1.0 - (modulationDepth * lfoOffset);
      sample *= m;
    }
    // Return the osc sample
    return sample;
  }

  /** Modulation type enumeration */
  public enum MOD_TYPE {
    NONE,
    AM,
    FM
  }
}
