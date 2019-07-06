package com.spelder.tagyourit.music.player;

import com.spelder.tagyourit.music.model.PlayerStates;

class PlayerState {
  private PlayerStates state = PlayerStates.UNINITIALIZED;

  private synchronized void notifyPause() {
    notifyAll();
  }

  public void start() {
    if (state == PlayerStates.PAUSED) {
      state = PlayerStates.PLAYING;
      notifyPause();
    }
    state = PlayerStates.PLAYING;
  }

  void pause() {
    state = PlayerStates.PAUSED;
  }

  void stop() {
    if (state == PlayerStates.PLAYING) {
      state = PlayerStates.STOPPED;
    }
    if (state == PlayerStates.PAUSED) {
      state = PlayerStates.STOPPED;
      notifyPause();
    }
  }

  void uninitialize() {
    state = PlayerStates.UNINITIALIZED;
  }

  boolean isStopped() {
    return state == PlayerStates.STOPPED;
  }

  public boolean isPlaying() {
    return state == PlayerStates.PLAYING;
  }

  boolean isUninitialized() {
    return state == PlayerStates.UNINITIALIZED;
  }

  boolean isRunning() {
    return state == PlayerStates.PLAYING || state == PlayerStates.PAUSED;
  }

  synchronized void waitPlay() {
    while (state == PlayerStates.PAUSED) {
      try {
        wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
