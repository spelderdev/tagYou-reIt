package com.spelder.tagyourit.music.player;

import android.os.Handler;
import com.spelder.tagyourit.music.listener.OnCompletionListener;
import com.spelder.tagyourit.music.listener.OnErrorListener;
import com.spelder.tagyourit.music.listener.OnPreparedListener;

/** Sends out events to subscribed threads. */
class EventHandler {
  private final Handler handler = new Handler();
  private final AudioPlayer player;
  private OnPreparedListener preparedListener;
  private OnCompletionListener completionListener;
  private OnErrorListener errorListener;

  EventHandler(AudioPlayer player) {
    this.player = player;
  }

  void setOnPreparedListener(OnPreparedListener listener) {
    preparedListener = listener;
  }

  void setOnCompletionListener(OnCompletionListener listener) {
    completionListener = listener;
  }

  void setOnErrorListener(OnErrorListener listener) {
    errorListener = listener;
  }

  void sendOnPreparedEvent() {
    handler.post(
        () -> {
          if (preparedListener != null) {
            preparedListener.onPrepared(player);
          }
        });
  }

  void sendOnErrorEvent() {
    handler.post(() -> errorListener.onError(player));
  }

  void sendOnCompletionEvent() {
    handler.post(() -> completionListener.onCompletion());
  }
}
