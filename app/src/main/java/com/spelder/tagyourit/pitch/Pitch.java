package com.spelder.tagyourit.pitch;

public enum Pitch {
  A_FLAT(415.3, "Ab"),
  A(440.0, "A"),
  A_SHARP(466.16, "A#"),
  B_FLAT(466.16, "Bb"),
  B(493.88, "B"),
  C(523.25, "C"),
  C_SHARP(554.37, "C#"),
  D_FLAT(554.37, "Db"),
  D(587.33, "D"),
  D_SHARP(622.25, "D#"),
  E_FLAT(622.25, "Eb"),
  E(659.25, "E"),
  F(349.23, "F"),
  F_SHARP(369.99, "F#"),
  G_FLAT(369.99, "Gb"),
  G(392.00, "G"),
  G_SHARP(415.30, "G#");

  private final double frequency;

  private final String display;

  Pitch(double frequency, String display) {
    this.frequency = frequency;
    this.display = display;
  }

  public static Pitch determinePitch(String key) {
    if (isAFlat(key)) {
      return Pitch.A_FLAT;
    } else if (isASharp(key)) {
      return Pitch.A_SHARP;
    } else if (isA(key)) {
      return Pitch.A;
    } else if (isBFlat(key)) {
      return Pitch.B_FLAT;
    } else if (isB(key)) {
      return Pitch.B;
    } else if (isCSharp(key)) {
      return Pitch.C_SHARP;
    } else if (isC(key)) {
      return Pitch.C;
    } else if (isDFlat(key)) {
      return Pitch.D_FLAT;
    } else if (isDSharp(key)) {
      return Pitch.D_SHARP;
    } else if (isD(key)) {
      return Pitch.D;
    } else if (isEFlat(key)) {
      return Pitch.E_FLAT;
    } else if (isE(key)) {
      return Pitch.E;
    } else if (isFSharp(key)) {
      return Pitch.F_SHARP;
    } else if (isF(key)) {
      return Pitch.F;
    } else if (isGFlat(key)) {
      return Pitch.G_FLAT;
    } else if (isGSharp(key)) {
      return Pitch.G_SHARP;
    } else if (isG(key)) {
      return Pitch.G;
    }

    return null;
  }

  private static boolean isAFlat(String key) {
    return key.contains("Ab");
  }

  private static boolean isA(String key) {
    return key.contains("A") && !key.contains("Ab") && !key.contains("A#");
  }

  private static boolean isASharp(String key) {
    return key.contains("A#");
  }

  private static boolean isBFlat(String key) {
    return key.contains("Bb");
  }

  private static boolean isB(String key) {
    return key.contains("B") && !key.contains("Bb");
  }

  private static boolean isC(String key) {
    return key.contains("C") && !key.contains("C#");
  }

  private static boolean isCSharp(String key) {
    return key.contains("C#");
  }

  private static boolean isDFlat(String key) {
    return key.contains("Db");
  }

  private static boolean isD(String key) {
    return key.contains("D") && !key.contains("Db") && !key.contains("D#");
  }

  private static boolean isDSharp(String key) {
    return key.contains("D#");
  }

  private static boolean isEFlat(String key) {
    return key.contains("Eb");
  }

  private static boolean isE(String key) {
    return key.contains("E") && !key.contains("Eb");
  }

  private static boolean isF(String key) {
    return key.contains("F") && !key.contains("F#");
  }

  private static boolean isFSharp(String key) {
    return key.contains("F#");
  }

  private static boolean isGFlat(String key) {
    return key.contains("Gb");
  }

  private static boolean isG(String key) {
    return key.contains("G") && !key.contains("Gb") && !key.contains("G#");
  }

  private static boolean isGSharp(String key) {
    return key.contains("G#");
  }

  public double getFrequency() {
    return frequency;
  }

  public String getDisplay() {
    return display;
  }
}
