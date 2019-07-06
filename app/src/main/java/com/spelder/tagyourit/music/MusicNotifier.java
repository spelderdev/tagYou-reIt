package com.spelder.tagyourit.music;

import com.spelder.tagyourit.music.model.Speed;

public interface MusicNotifier {
  void done();

  void play(String title, String part);

  void pause();

  void speedChanged(Speed speed);

  void pitchChanged(int semitones);

  void loading(String title, String part);
}
