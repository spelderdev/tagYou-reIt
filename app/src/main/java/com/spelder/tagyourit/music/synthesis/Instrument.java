package com.spelder.tagyourit.music.synthesis;

import static com.spelder.tagyourit.music.synthesis.HarmonicMagnitudeConstants.CLARINET_MAGNITUDES;
import static com.spelder.tagyourit.music.synthesis.HarmonicMagnitudeConstants.FLUTE_MAGNITUDES;
import static com.spelder.tagyourit.music.synthesis.HarmonicMagnitudeConstants.OBOE_MAGNITUDES;
import static com.spelder.tagyourit.music.synthesis.HarmonicMagnitudeConstants.PITCH_PIPE_MAGNITUDES;
import static com.spelder.tagyourit.music.synthesis.HarmonicMagnitudeConstants.PURE_MAGNITUDES;

/** Different instruments used for synthesis. It contains the settings for synthesizer. */
public enum Instrument {
  PITCH_PIPE(PITCH_PIPE_MAGNITUDES, .1, 20, AdvancedHarmonicOscillator.MOD_TYPE.FM, .2),
  FLUTE(FLUTE_MAGNITUDES, 0, 0, AdvancedHarmonicOscillator.MOD_TYPE.FM, .2),
  CLARINET(CLARINET_MAGNITUDES, 0, 0, AdvancedHarmonicOscillator.MOD_TYPE.FM, .2),
  OBOE(OBOE_MAGNITUDES, 0, 0, AdvancedHarmonicOscillator.MOD_TYPE.FM, .2),
  PURE(PURE_MAGNITUDES, 0, 0, AdvancedHarmonicOscillator.MOD_TYPE.FM, .3);

  private final double modulationDepth;
  private final double lfoFrequencySemitoneRatio;
  private final AdvancedHarmonicOscillator.MOD_TYPE modType;
  private final double[] harmonicMagnitudes;
  private final double gainMultiplier;

  Instrument(
      double[] harmonicMagnitudes,
      double modulationDepth,
      double lfoFrequencySemitoneRatio,
      AdvancedHarmonicOscillator.MOD_TYPE modType,
      double gainMultiplier) {
    this.harmonicMagnitudes = harmonicMagnitudes;
    this.modulationDepth = modulationDepth;
    this.lfoFrequencySemitoneRatio = lfoFrequencySemitoneRatio;
    this.modType = modType;
    this.gainMultiplier = gainMultiplier;
  }

  public double getModulationDepth() {
    return modulationDepth;
  }

  public double[] getHarmonicMagnitudes() {
    return harmonicMagnitudes;
  }

  public AdvancedHarmonicOscillator.MOD_TYPE getModType() {
    return modType;
  }

  public double getLfoFrequency(double frequency) {
    return Math.pow(2, lfoFrequencySemitoneRatio / 12) * frequency;
  }

  public double getGainMultiplier() {
    return gainMultiplier;
  }
}
