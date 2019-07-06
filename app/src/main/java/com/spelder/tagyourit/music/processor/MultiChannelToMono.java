package com.spelder.tagyourit.music.processor;

import com.spelder.tagyourit.music.model.AudioEvent;

/** Converts the incoming audio stream to mono format. */
public class MultiChannelToMono implements AudioProcessor {
  @Override
  public void process(AudioEvent audioEvent) {
    float[][] channelBuffer = audioEvent.getChannelBuffer();
    int channelNumber = audioEvent.getChannelNumber();
    float[] monoBuffer = new float[channelBuffer[0].length];

    for (int i = 0; i < channelBuffer[0].length; i++) {
      float sum = 0;
      for (int channel = 0; channel < channelNumber; channel++) {
        sum += channelBuffer[channel][i];
      }
      monoBuffer[i] = sum / channelNumber;
    }

    audioEvent.setChannelNumber(1);
    audioEvent.setFloatBuffer(monoBuffer);
  }
}
