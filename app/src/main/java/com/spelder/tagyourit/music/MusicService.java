package com.spelder.tagyourit.music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.media.session.MediaButtonReceiver;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.model.TrackComponents;
import com.spelder.tagyourit.model.TrackParts;
import com.spelder.tagyourit.music.listener.OnCompletionListener;
import com.spelder.tagyourit.music.listener.OnErrorListener;
import com.spelder.tagyourit.music.listener.OnPreparedListener;
import com.spelder.tagyourit.music.model.Speed;
import com.spelder.tagyourit.music.player.AudioPlayer;
import com.spelder.tagyourit.music.processor.BalanceProcessor;
import com.spelder.tagyourit.music.processor.MultiChannelToMono;
import com.spelder.tagyourit.music.processor.PitchShiftProcessor;
import com.spelder.tagyourit.music.utility.MusicServiceNotificationHandler;
import com.spelder.tagyourit.networking.DownloadFileTask;
import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/** Service used for playing music. */
public class MusicService extends Service
    implements OnPreparedListener,
        OnErrorListener,
        OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {
  private static final String TAG = "MusicService";

  private final IBinder musicBind = new MusicBinder();

  private AudioPlayer player;

  private TrackComponents track;

  private Tag tag;

  private List<MusicNotifier> notifierList;

  private boolean isComplete = false;

  private boolean isLoading = false;

  private BalanceProcessor balanceProcessor;

  private PitchShiftProcessor pitchShiftProcessor;

  private MusicServiceNotificationHandler notificationHandler;

  private final BroadcastReceiver noisyReceiver =
      new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          Log.d(TAG, "received noisy");
          pause();
        }
      };

  @Override
  public void onCreate() {
    super.onCreate();
    notifierList = new ArrayList<>();
    notificationHandler = new MusicServiceNotificationHandler(this, getApplicationContext());
    initNoisyReceiver();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    MediaButtonReceiver.handleIntent(notificationHandler.getMediaSession(), intent);
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    audioManager.abandonAudioFocus(this);
    unregisterReceiver(noisyReceiver);
    stop();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return musicBind;
  }

  private void initMusicPlayer() {
    player.setOnPreparedListener(this);
    player.setOnCompletionListener(this);
    player.setOnErrorListener(this);

    balanceProcessor = new BalanceProcessor();
    pitchShiftProcessor = new PitchShiftProcessor(0, player.getChannelSize());
    MultiChannelToMono monoProcessor = new MultiChannelToMono();
    player.addProcessor(balanceProcessor);
    player.addProcessor(monoProcessor);
    player.addProcessor(pitchShiftProcessor);
  }

  public Tag getTag() {
    return tag;
  }

  public void setTag(Tag tag) {
    this.tag = tag;
  }

  public boolean isTrackUnavailable(TrackParts part) {
    return tag.getTrack(part.getKey()) == null;
  }

  @Override
  public void onCompletion() {
    isComplete = true;

    notificationHandler.updateNotificationStopped();

    for (MusicNotifier notifier : notifierList) {
      notifier.done();
    }
  }

  @Override
  public void onError(AudioPlayer mp) {
    Log.v("MUSIC PLAYER", "Playback Error");
    mp.reset();
  }

  @Override
  public void onPrepared(AudioPlayer mp) {
    Log.d(TAG, "onPrepared");
    mp.start();
    isComplete = false;
    isLoading = false;

    notificationHandler.updateNotificationPlay();

    for (MusicNotifier notifier : notifierList) {
      notifier.play(track.getTagTitle(), track.getPart());
    }
  }

  public int getPosition() {
    if (player != null) {
      return player.getCurrentPosition();
    } else {
      return 0;
    }
  }

  public int getDuration() {
    if (player != null && !isComplete) {
      return player.getDurationMillis();
    } else {
      return 0;
    }
  }

  public TrackComponents getTrack() {
    return track;
  }

  public void pause() {
    if (player != null && player.isPlaying()) {
      player.pause();

      notificationHandler.updateNotificationPause();

      for (MusicNotifier notifier : notifierList) {
        notifier.pause();
      }
    }
  }

  public void stop() {
    isComplete = true;
    if (player != null && player.isPlaying()) {
      player.stop();
    }
    player = null;

    for (MusicNotifier notifier : notifierList) {
      notifier.done();
    }

    notificationHandler.clearNotification();
    stopForeground(true);
    stopSelf();
    notificationHandler.resetMediaSession();
  }

  public void seek(long position) {
    if (player != null) {
      player.seekTo(position);
    }
    if (isPlaying()) {
      notificationHandler.updateNotificationPlay();
    } else {
      notificationHandler.updateNotificationPause();
    }
  }

  public void setBalance(float left, float right) {
    balanceProcessor.setBalance(left, right);
  }

  public void setSpeed(Speed speed) {
    boolean applied = false;
    if (player != null) {
      applied = player.setSpeed(speed);
    }

    if (applied) {
      for (MusicNotifier notifier : notifierList) {
        notifier.speedChanged(speed);
      }
    }
  }

  public void setPitch(int semitones) {
    pitchShiftProcessor.setPitch(semitones);

    for (MusicNotifier notifier : notifierList) {
      notifier.pitchChanged(semitones);
    }
  }

  public void play() {
    play(this);
  }

  public void play(Context context) {
    if (!successfullyRetrievedAudioFocus()) {
      return;
    }

    if (!isComplete) {
      isLoading = false;
      player.start();

      notificationHandler.updateNotificationPlay();

      for (MusicNotifier notifier : notifierList) {
        notifier.play(track.getTagTitle(), track.getPart());
      }
    } else {
      playSong(track, context);
    }
  }

  public void playSong(TrackComponents track, Context context) {
    isComplete = true;
    Log.d(TAG, "playSong");
    this.track = track;

    if (notificationHandler.getMediaSession() == null) {
      notificationHandler.initMediaSession();
    }

    if (player == null) {
      player = new AudioPlayer();
      initMusicPlayer();
    }
    player.reset();
    String trackFile = retrieveTrackFile(context, track);
    if (trackFile != null) {
      try {
        File file = new File(trackFile);
        FileInputStream is = new FileInputStream(file);
        player.initialize(is.getFD());
      } catch (Exception e) {
        Log.e("MUSIC SERVICE", "Error setting data source", e);
      }
    }

    notificationHandler.updateNotificationPlay();
  }

  public void playTrack(TrackParts part, Context context) {
    if (tag.getTrack(part.getKey()) != null) {
      playSong(tag.getTrack(part.getKey()), context);
    }
  }

  public boolean isPlaying() {
    return player != null && player.isPlaying();
  }

  public boolean isLoading() {
    return isLoading;
  }

  public void addNotification(MusicNotifier notifier) {
    notifierList.add(notifier);
  }

  private String retrieveTrackFile(Context context, TrackComponents track) {
    String absoluteFilePath = track.getTrackPath(context);
    Log.d(TAG, absoluteFilePath);

    File file = new File(absoluteFilePath);
    if (file.exists()) {
      Log.d(TAG, "File exists");

      return absoluteFilePath;
    } else {
      isLoading = true;
      for (MusicNotifier notifier : notifierList) {
        notifier.loading(track.getTagTitle(), track.getPart());
      }

      DownloadFileTask task = new UpdatedDownloadFileTask(this, absoluteFilePath);
      task.execute(track.getLink(), track.getTrackDirectory(context), track.getTrackFileName());

      return null;
    }
  }

  private boolean successfullyRetrievedAudioFocus() {
    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    int result =
        audioManager.requestAudioFocus(
            this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

    return result == AudioManager.AUDIOFOCUS_GAIN;
  }

  @Override
  public void onAudioFocusChange(int focusChange) {
    switch (focusChange) {
      case AudioManager.AUDIOFOCUS_LOSS:
        {
          stop();
          break;
        }
      case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
        {
          pause();
          break;
        }
      case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
        {
          setBalance(0.3f, 0.3f);
          break;
        }
      case AudioManager.AUDIOFOCUS_GAIN:
        {
          play();
          setBalance(1f, 1f);
          break;
        }
    }
  }

  private void initNoisyReceiver() {
    // Handles headphones coming unplugged.
    IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    registerReceiver(noisyReceiver, filter);
  }

  private static class UpdatedDownloadFileTask extends DownloadFileTask {
    private final WeakReference<MusicService> activityReference;

    private final String absoluteFilePath;

    UpdatedDownloadFileTask(MusicService context, String absoluteFilePath) {
      activityReference = new WeakReference<>(context);
      this.absoluteFilePath = absoluteFilePath;
    }

    @Override
    protected void onPostExecute(Boolean res) {
      MusicService activity = activityReference.get();
      if (activity == null || activity.player == null || activity.player.isPlaying()) {
        return;
      }

      File file = new File(absoluteFilePath);
      Log.d(
          "DisplayTag",
          "Downloaded tag to: " + absoluteFilePath + ". File exists: " + file.exists());
      try {
        FileInputStream is = new FileInputStream(file);
        activity.player.initialize(is.getFD());
      } catch (Exception e) {
        Log.e("MUSIC SERVICE", "Error setting data source", e);
      }
    }
  }

  public class MusicBinder extends Binder {
    public MusicService getService() {
      return MusicService.this;
    }
  }
}
