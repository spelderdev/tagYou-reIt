package com.spelder.tagyourit.pitch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.spelder.tagyourit.music.player.AudioPlayer;
import com.spelder.tagyourit.music.synthesis.Instrument;

public class PitchPlayer {
  private static AudioPlayer player;

  private static Pitch lastPlayedPitch = Pitch.A;

  private PitchPlayer() {
    // Do not instantiate
  }

  public static void stopPitch() {
    if (player != null && player.isNotStopped()) {
      player.stop();
      player = null;
    }
  }

  public static boolean isPlaying() {
    return player != null && player.isNotStopped();
  }

  public static Pitch getPlayingPitch() {
    if (player != null && player.isNotStopped()) {
      return lastPlayedPitch;
    }

    return null;
  }

  public static void playPitch(Context context, String key) {
    playPitch(context, Pitch.determinePitch(key));
  }

  public static void togglePitch(Context context) {
    if (player != null && player.isNotStopped()) {
      stopPitch();
    } else {
      playPitch(context, lastPlayedPitch);
    }
  }

  private static void createPitchPlayer(final Context context, final Pitch pitch) {
    player = new AudioPlayer();

    try {
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
      double value = Double.valueOf(preferences.getString("tuning", "440.0"));
      double pitchDifference = value - 440;
      player.initialize(pitch.getFrequency() + pitchDifference, Instrument.PITCH_PIPE);
    } catch (Exception e) {
      Log.e("MUSIC SERVICE", "Error setting data source", e);
    }
  }

  public static synchronized void playPitch(final Context context, final Pitch pitch) {
    lastPlayedPitch = pitch;

    if (player != null && player.isNotStopped()) {
      player.stop();
      player = null;
    }

    try {
      createPitchPlayer(context, pitch);
      player.start();
    } catch (Exception e) {
      Log.e("PitchPlayer", "exception", e);
    }
  }
}
