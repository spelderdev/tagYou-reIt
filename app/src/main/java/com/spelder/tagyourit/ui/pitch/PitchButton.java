package com.spelder.tagyourit.ui.pitch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.pitch.Pitch;
import com.spelder.tagyourit.pitch.PitchPlayer;

/** Button used for the pitch pipe. */
public class PitchButton extends AppCompatButton {
  private Button centerPitch;

  private Pitch pitch;

  private String centerPitchLabel;

  private int centerPitchIcon;

  public PitchButton(Context context) {
    super(context);
  }

  public PitchButton(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public PitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setCenterPitchButton(Button centerPitch) {
    this.centerPitch = centerPitch;
  }

  public void setPitch(Pitch pitch) {
    this.pitch = pitch;
    switch (pitch) {
      case A_FLAT:
        centerPitchLabel = getResources().getString(R.string.pitch_ab);
        centerPitchIcon = R.drawable.pitch_circle_8_center;
        break;
      case A:
        centerPitchLabel = getResources().getString(R.string.pitch_a);
        centerPitchIcon = R.drawable.pitch_circle_9_center;
        break;
      case A_SHARP:
        centerPitchLabel = getResources().getString(R.string.pitch_bb);
        centerPitchIcon = R.drawable.pitch_circle_10_center;
        break;
      case B_FLAT:
        centerPitchLabel = getResources().getString(R.string.pitch_bb);
        centerPitchIcon = R.drawable.pitch_circle_10_center;
        break;
      case B:
        centerPitchLabel = getResources().getString(R.string.pitch_b);
        centerPitchIcon = R.drawable.pitch_circle_11_center;
        break;
      case C:
        centerPitchLabel = getResources().getString(R.string.pitch_c);
        centerPitchIcon = R.drawable.pitch_circle_12_center;
        break;
      case C_SHARP:
        centerPitchLabel = getResources().getString(R.string.pitch_db);
        centerPitchIcon = R.drawable.pitch_circle_1_center;
        break;
      case D_FLAT:
        centerPitchLabel = getResources().getString(R.string.pitch_db);
        centerPitchIcon = R.drawable.pitch_circle_1_center;
        break;
      case D:
        centerPitchLabel = getResources().getString(R.string.pitch_d);
        centerPitchIcon = R.drawable.pitch_circle_2_center;
        break;
      case D_SHARP:
        centerPitchLabel = getResources().getString(R.string.pitch_eb);
        centerPitchIcon = R.drawable.pitch_circle_3_center;
        break;
      case E_FLAT:
        centerPitchLabel = getResources().getString(R.string.pitch_eb);
        centerPitchIcon = R.drawable.pitch_circle_3_center;
        break;
      case E:
        centerPitchLabel = getResources().getString(R.string.pitch_e);
        centerPitchIcon = R.drawable.pitch_circle_4_center;
        break;
      case F:
        centerPitchLabel = getResources().getString(R.string.pitch_f);
        centerPitchIcon = R.drawable.pitch_circle_5_center;
        break;
      case F_SHARP:
        centerPitchLabel = getResources().getString(R.string.pitch_gb);
        centerPitchIcon = R.drawable.pitch_circle_6_center;
        break;
      case G_SHARP:
        centerPitchLabel = getResources().getString(R.string.pitch_ab);
        centerPitchIcon = R.drawable.pitch_circle_8_center;
        break;
      case G:
        centerPitchLabel = getResources().getString(R.string.pitch_g);
        centerPitchIcon = R.drawable.pitch_circle_7_center;
        break;
      case G_FLAT:
        centerPitchLabel = getResources().getString(R.string.pitch_gb);
        centerPitchIcon = R.drawable.pitch_circle_6_center;
        break;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    performClick();
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        PitchPlayer.playPitch(getContext(), pitch);
        break;
      case MotionEvent.ACTION_UP:
        PitchPlayer.stopPitch();
        break;
    }
    return true;
  }

  @Override
  public boolean performClick() {
    super.performClick();
    centerPitch.setBackgroundResource(centerPitchIcon);
    centerPitch.setText(centerPitchLabel);
    return true;
  }
}
