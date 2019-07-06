package com.spelder.tagyourit.music.processor;

import com.spelder.tagyourit.music.model.AudioEvent;

public interface AudioProcessor {
  void process(AudioEvent event);
}
