package com.spelder.tagyourit.ui.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.model.TrackParts;
import com.spelder.tagyourit.music.MusicNotifier;
import com.spelder.tagyourit.music.MusicService;
import com.spelder.tagyourit.music.model.Speed;
import java.util.Locale;

/** Activity used to display and control the music player. */
public class MusicPlayerActivity extends AppCompatActivity {
  private static final String TAG = "MusicPlayerActivity";

  private TextView trackTitle;

  private TextView trackPart;

  private ImageView trackPlayPause;

  private View trackLoading;

  private ImageView playAllTrack;

  private ImageView playBariTrack;

  private ImageView playBassTrack;

  private ImageView playLeadTrack;

  private ImageView playTenorTrack;

  private SeekBar seekBar;

  private TextView playbackStart;

  private TextView playbackEnd;

  private TextView speedText;

  private TextView pitchText;

  private MusicService musicSrv;

  private SeekBar speedSeekBar;

  private SeekBar pitchSeekBar;

  private SeekBar balanceSeekBar;

  private boolean musicBound = false;

  private final ServiceConnection musicConnection =
      new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
          musicSrv = binder.getService();

          if (musicSrv.getTrack() == null) {
            Log.i(TAG, "Closing music player since there is no track");
            musicSrv.stop();
            onBackPressed();
            return;
          }

          musicSrv.addNotification(
              new MusicNotifier() {
                @Override
                public void done() {
                  trackPlayPause.setImageResource(R.drawable.play_circle_filled_white);
                  seekBar.setProgress(0);
                }

                @Override
                public void play(String title, String part) {
                  trackTitle.setText(title);
                  trackPart.setText(part);
                  pitchSeekBar.setEnabled(true);
                  speedSeekBar.setEnabled(true);
                  balanceSeekBar.setEnabled(true);

                  trackLoading.setVisibility(View.GONE);
                  trackPlayPause.setVisibility(View.VISIBLE);

                  trackPlayPause.setImageResource(R.drawable.pause_circle_filled);
                }

                @Override
                public void pause() {
                  trackLoading.setVisibility(View.GONE);
                  trackPlayPause.setVisibility(View.VISIBLE);
                  pitchSeekBar.setEnabled(true);
                  speedSeekBar.setEnabled(true);
                  balanceSeekBar.setEnabled(true);

                  trackPlayPause.setImageResource(R.drawable.play_circle_filled_white);
                }

                @Override
                public void speedChanged(Speed speed) {
                  speedText.setText(speed.getDisplay());
                }

                @Override
                public void pitchChanged(int semitones) {
                  pitchText.setText(String.valueOf(semitones));
                }

                @Override
                public void loading(String title, String part) {
                  trackTitle.setText(title);
                  trackPart.setText(part);
                  pitchSeekBar.setEnabled(false);
                  speedSeekBar.setEnabled(false);
                  balanceSeekBar.setEnabled(false);

                  trackPlayPause.setVisibility(View.GONE);
                  trackLoading.setVisibility(View.VISIBLE);
                }
              });
          musicBound = true;
          if (musicSrv.isLoading()) {
            trackPlayPause.setVisibility(View.GONE);
            trackLoading.setVisibility(View.VISIBLE);
          } else if (musicSrv.isPlaying()) {
            trackLoading.setVisibility(View.GONE);
            trackPlayPause.setVisibility(View.VISIBLE);
            trackPlayPause.setImageResource(R.drawable.pause_circle_filled);
          } else {
            trackLoading.setVisibility(View.GONE);
            trackPlayPause.setVisibility(View.VISIBLE);
            trackPlayPause.setImageResource(R.drawable.play_circle_filled_white);
          }

          Log.d(TAG, "musicSrv: " + (musicSrv == null));
          Log.d(TAG, "musicSrv.getTrack(): " + (musicSrv.getTrack() == null));

          trackTitle.setText(musicSrv.getTrack().getTagTitle());
          trackPart.setText(musicSrv.getTrack().getPart());

          if (musicSrv.isTrackUnavailable(TrackParts.ALL)) {
            playAllTrack.setVisibility(View.GONE);
          }
          if (musicSrv.isTrackUnavailable(TrackParts.BARI)) {
            playBariTrack.setVisibility(View.GONE);
          }
          if (musicSrv.isTrackUnavailable(TrackParts.BASS)) {
            playBassTrack.setVisibility(View.GONE);
          }
          if (musicSrv.isTrackUnavailable(TrackParts.LEAD)) {
            playLeadTrack.setVisibility(View.GONE);
          }
          if (musicSrv.isTrackUnavailable(TrackParts.TENOR)) {
            playTenorTrack.setVisibility(View.GONE);
          }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          musicBound = false;
        }
      };

  private Thread t;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.music_player);
    ImageView close = findViewById(R.id.music_player_close);
    close.setOnClickListener(view -> onBackPressed());

    trackTitle = findViewById(R.id.music_player_title);
    trackPart = findViewById(R.id.music_player_part);
    trackPlayPause = findViewById(R.id.music_player_play_pause);
    trackPlayPause.setOnClickListener(view -> togglePlayPause());
    trackLoading = findViewById(R.id.music_player_loading);

    seekBar = findViewById(R.id.music_player_seek_bar);
    seekBar.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
              int time = (int) ((i / 100.0) * musicSrv.getDuration());
              musicSrv.seek(time);
            }
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    playbackStart = findViewById(R.id.music_player_seek_bar_start);
    playbackEnd = findViewById(R.id.music_player_seek_bar_end);

    final Context context = this;

    playAllTrack = findViewById(R.id.music_player_play_all);
    playAllTrack.setOnClickListener(view -> musicSrv.playTrack(TrackParts.ALL, context));

    playBariTrack = findViewById(R.id.music_player_play_bari);
    playBariTrack.setOnClickListener(view -> musicSrv.playTrack(TrackParts.BARI, context));

    playBassTrack = findViewById(R.id.music_player_play_bass);
    playBassTrack.setOnClickListener(view -> musicSrv.playTrack(TrackParts.BASS, context));

    playLeadTrack = findViewById(R.id.music_player_play_lead);
    playLeadTrack.setOnClickListener(view -> musicSrv.playTrack(TrackParts.LEAD, context));

    playTenorTrack = findViewById(R.id.music_player_play_tenor);
    playTenorTrack.setOnClickListener(view -> musicSrv.playTrack(TrackParts.TENOR, context));

    balanceSeekBar = findViewById(R.id.music_player_balance);
    balanceSeekBar.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
              float left = 1f;
              float right = 1f;
              if (i < 50) {
                right = (float) i / 50;
              }
              if (i > 50) {
                left = (50 - ((float) i - 50)) / 50;
              }
              musicSrv.setBalance(left, right);
            }
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    speedText = findViewById(R.id.music_player_speed_value);
    TextView speedT = findViewById(R.id.music_player_speed_text);

    speedSeekBar = findViewById(R.id.music_player_speed);
    speedSeekBar.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
              musicSrv.setSpeed(Speed.getSpeedFromId(i));
            }
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    pitchText = findViewById(R.id.music_player_pitch_value);

    pitchSeekBar = findViewById(R.id.music_player_pitch);
    pitchSeekBar.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
              musicSrv.setPitch(i - 6);
            }
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      speedText.setVisibility(View.GONE);
      speedSeekBar.setVisibility(View.GONE);
      speedT.setVisibility(View.GONE);
      findViewById(R.id.music_player_speed_layout).setVisibility(View.GONE);
      int orientation = getResources().getConfiguration().orientation;
      if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        LinearLayout pitchLayout = findViewById(R.id.music_player_pitch_layout);
        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        pitchLayout.setLayoutParams(params);
      }
    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    Intent playIntent = new Intent(this, MusicService.class);
    getApplicationContext().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
    startService(playIntent);

    createUpdatePlaybackTimer();
  }

  @Override
  protected void onStop() {
    super.onStop();

    if (t != null) {
      t.interrupt();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    if (musicBound && !isChangingConfigurations()) {
      getApplicationContext().unbindService(musicConnection);
    }

    musicSrv = null;
  }

  @Override
  public void onBackPressed() {
    finish();
    openActivityFromBottom();
  }

  private void openActivityFromBottom() {
    overridePendingTransition(R.anim.stay, R.anim.slide_down);
  }

  private void togglePlayPause() {
    if (musicSrv.isPlaying()) {
      musicSrv.pause();
    } else {
      musicSrv.play(this);
    }
  }

  private void setPlaybackStart(String start) {
    playbackStart.setText(start);
  }

  private void setPlaybackEnd(String end) {
    playbackEnd.setText(end);
  }

  private void createUpdatePlaybackTimer() {
    t =
        new Thread() {
          @Override
          public void run() {
            try {
              while (!isInterrupted()) {
                runOnUiThread(
                    () -> {
                      if (musicSrv == null || musicSrv.getDuration() == 0) {
                        seekBar.setProgress(0);
                        setPlaybackStart(
                            String.format(Locale.US, "%02d", 0)
                                + ":"
                                + String.format(Locale.US, "%02d", 0));
                        setPlaybackEnd(
                            "-"
                                + String.format(Locale.US, "%02d", 0)
                                + ":"
                                + String.format(Locale.US, "%02d", 0));
                        return;
                      }

                      int time = musicSrv.getPosition();
                      int timeRemaining = musicSrv.getDuration() - time;
                      double percentageComplete = (double) time / musicSrv.getDuration();

                      int timeMinutes = time / 60000;
                      int timeSeconds = (time / 1000) - (timeMinutes * 60);
                      setPlaybackStart(
                          String.format(Locale.US, "%02d", timeMinutes)
                              + ":"
                              + String.format(Locale.US, "%02d", timeSeconds));

                      int timeRemainingMinutes = timeRemaining / 60000;
                      int timeRemainingSeconds =
                          (timeRemaining / 1000) - (timeRemainingMinutes * 60);
                      setPlaybackEnd(
                          "-"
                              + String.format(Locale.US, "%02d", timeRemainingMinutes)
                              + ":"
                              + String.format(Locale.US, "%02d", timeRemainingSeconds));

                      seekBar.setProgress((int) (percentageComplete * 100));
                    });

                Thread.sleep(1000);
              }
            } catch (InterruptedException e) {
              Log.d(TAG, "Exception", e);
            }
          }
        };

    t.start();
  }
}
