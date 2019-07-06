package com.spelder.tagyourit.music.processor;

import com.spelder.tagyourit.music.model.AudioEvent;

/**
 * Changes the balance of the incoming buffer to the set values. This currently only works for audio
 * that is in stereo format. Any other non-stereo formats will be skipped.
 */
public class BalanceProcessor implements AudioProcessor {
  private float leftBalance;

  private float rightBalance;

  public BalanceProcessor() {
    leftBalance = 1.0f;
    rightBalance = 1.0f;
  }

  public void setBalance(float left, float right) {
    leftBalance = left;
    rightBalance = right;
  }

  @Override
  public void process(AudioEvent event) {
    if (event.getChannelNumber() != 2) {
      return;
    }

    float balance;
    for (int channel = 1; channel <= event.getChannelNumber(); channel++) {
      float[] channelBuffer = event.getFloatBuffer(channel);
      if (channel % 2 == 0) {
        balance = rightBalance;
      } else {
        balance = leftBalance;
      }

      for (int i = 0; i < channelBuffer.length; i++) {
        channelBuffer[i] = channelBuffer[i] * balance;

        if (channelBuffer[i] > 1.0) {
          channelBuffer[i] = 1.0f;
        } else if (channelBuffer[i] < -1.0) {
          channelBuffer[i] = -1.0f;
        }
      }

      event.setFloatBuffer(channelBuffer, channel);
    }
  }
}
