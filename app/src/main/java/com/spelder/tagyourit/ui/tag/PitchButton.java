package com.spelder.tagyourit.ui.tag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import com.spelder.tagyourit.pitch.Pitch;
import com.spelder.tagyourit.pitch.PitchPlayer;
import java.util.Timer;
import java.util.TimerTask;

/** Button used for the pitch pipe in tag view. */
public class PitchButton extends AppCompatButton {
  private static final int PITCH_DOWN_TIME_MILLIS = 500;
  private static final int PITCH_STOP_TIME = 3000;

  private Pitch pitch;
  private long keyDownTime = 0;
  private boolean pitchPlaying = false;

  public PitchButton(Context context) {
    super(context);
  }

  public PitchButton(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public PitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setPitch(Pitch pitch) {
    this.pitch = pitch;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        setSelected(true);
        setPressed(true);

        if (!pitchPlaying) {
          keyDownTime = System.currentTimeMillis();
          pitchPlaying = true;
          PitchPlayer.playPitch(getContext(), pitch);
        }
        break;
      case MotionEvent.ACTION_UP:
        setSelected(false);
        setPressed(false);
        
        if (System.currentTimeMillis() - keyDownTime < PITCH_DOWN_TIME_MILLIS) {
          Timer stopTimer = new Timer();
          stopTimer.schedule(
              new TimerTask() {
                @Override
                public void run() {
                  pitchPlaying = false;
                  PitchPlayer.stopPitch();
                }
              },
              PITCH_STOP_TIME);
        } else {
          pitchPlaying = false;
          PitchPlayer.stopPitch();
        }
        break;
      case MotionEvent.ACTION_BUTTON_PRESS:
        performClick();
        break;
    }
    return true;
  }

  @Override
  public boolean performClick() {
    super.performClick();
    return true;
  }
}
