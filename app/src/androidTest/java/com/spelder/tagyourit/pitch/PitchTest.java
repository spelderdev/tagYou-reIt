package com.spelder.tagyourit.pitch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PitchTest {
  @Test
  public void pitch_determinePitch() {
    assertEquals(Pitch.determinePitch("Ab"), Pitch.A_FLAT);
    assertEquals(Pitch.determinePitch("A#"), Pitch.A_SHARP);
    assertEquals(Pitch.determinePitch("A"), Pitch.A);
    assertEquals(Pitch.determinePitch("Bb"), Pitch.B_FLAT);
    assertEquals(Pitch.determinePitch("B"), Pitch.B);
    assertEquals(Pitch.determinePitch("C#"), Pitch.C_SHARP);
    assertEquals(Pitch.determinePitch("C"), Pitch.C);
    assertEquals(Pitch.determinePitch("Db"), Pitch.D_FLAT);
    assertEquals(Pitch.determinePitch("D#"), Pitch.D_SHARP);
    assertEquals(Pitch.determinePitch("D"), Pitch.D);
    assertEquals(Pitch.determinePitch("Eb"), Pitch.E_FLAT);
    assertEquals(Pitch.determinePitch("E"), Pitch.E);
    assertEquals(Pitch.determinePitch("F#"), Pitch.F_SHARP);
    assertEquals(Pitch.determinePitch("F"), Pitch.F);
    assertEquals(Pitch.determinePitch("Gb"), Pitch.G_FLAT);
    assertEquals(Pitch.determinePitch("G#"), Pitch.G_SHARP);
    assertEquals(Pitch.determinePitch("G"), Pitch.G);
  }
}
