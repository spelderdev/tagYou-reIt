package com.spelder.tagyourit.music.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.music.MusicService;
import com.spelder.tagyourit.music.model.PlayerStates;
import com.spelder.tagyourit.ui.music.MusicPlayerActivity;

/** Handles the notifications for the music service. */
public class MusicServiceNotificationHandler {
  private static final String CHANNEL_ID = "media_playback";
  private static final String TAG = MusicServiceNotificationHandler.class.getName();
  private static final int NOTIFICATION_ID = 29334;
  private MediaSessionCompat mediaSession;
  private final Context context;
  private final MusicService service;

  private final MediaSessionCompat.Callback mMediaSessionCallback =
      new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
          super.onPlay();
          if (!service.isLoading()) {
            service.play();
          }
        }

        @Override
        public void onPause() {
          super.onPause();
          if (!service.isLoading()) {
            service.pause();
          }
        }

        @Override
        public void onStop() {
          super.onStop();
          service.stop();
        }

        @Override
        public void onSeekTo(long pos) {
          super.onSeekTo(pos);
          service.seek(pos);
        }
      };

  public MusicServiceNotificationHandler(MusicService service, Context context) {
    this.context = context;
    this.service = service;
  }

  public void initMediaSession() {
    ComponentName mediaButtonReceiver = new ComponentName(context, MediaButtonReceiver.class);
    mediaSession = new MediaSessionCompat(context, "Tag", mediaButtonReceiver, null);

    mediaSession.setCallback(mMediaSessionCallback);
    mediaSession.setFlags(
        MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
            | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

    Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
    mediaButtonIntent.setClass(context, MediaButtonReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mediaButtonIntent, 0);
    mediaSession.setMediaButtonReceiver(pendingIntent);
  }

  public void resetMediaSession() {
    mediaSession = null;
  }

  public MediaSessionCompat getMediaSession() {
    return mediaSession;
  }

  private void updateNotification(PlayerStates state) {
    NotificationManagerCompat.from(context)
        .notify(NOTIFICATION_ID, buildForegroundNotification(state));
  }

  public void clearNotification() {
    Log.d(TAG, "Clearing notification");
    if (mediaSession != null) {
      mediaSession.release();
    }
    NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
  }

  public void updateNotificationPlay() {
    mediaSession.setActive(true);
    setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
    service.startForeground(NOTIFICATION_ID, buildForegroundNotification(PlayerStates.PLAYING));
  }

  public void updateNotificationPause() {
    setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
    updateNotification(PlayerStates.PAUSED);
  }

  public void updateNotificationStopped() {
    setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
    updateNotification(PlayerStates.STOPPED);
  }

  private void setMediaPlaybackState(int state) {
    PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder();
    long commonCapabilities =
        PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SEEK_TO
            | PlaybackStateCompat.ACTION_STOP;
    if (state == PlaybackStateCompat.STATE_PLAYING) {
      playbackStateBuilder.setActions(commonCapabilities | PlaybackStateCompat.ACTION_PAUSE);
    } else {
      playbackStateBuilder.setActions(commonCapabilities | PlaybackStateCompat.ACTION_PLAY);
    }

    if (!service.isLoading()) {
      playbackStateBuilder.setState(state, service.getPosition(), 1.0f);
    } else {
      playbackStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, service.getPosition(), 1.0f);
    }

    mediaSession.setMetadata(buildMetadata());
    mediaSession.setPlaybackState(playbackStateBuilder.build());
  }

  private MediaMetadataCompat buildMetadata() {
    MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
    if (service.getTrack() != null) {
      metadataBuilder
          .putString(MediaMetadataCompat.METADATA_KEY_TITLE, service.getTrack().getTagTitle())
          .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, service.getTrack().getPart());
    }
    if (service.getTag() != null) {
      metadataBuilder
          .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, service.getTag().getArranger())
          .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, service.getTag().getNumberOfParts())
          .putRating(
              MediaMetadataCompat.METADATA_KEY_RATING,
              RatingCompat.newStarRating(
                  RatingCompat.RATING_5_STARS, (float) service.getTag().getRating()));
    }

    int duration = service.getDuration();
    if (duration > 0.001) {
      metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration);
    }

    return metadataBuilder.build();
  }

  private Notification buildForegroundNotification(PlayerStates state) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      initChannels(context);
    }

    int icon;
    if (state == PlayerStates.PLAYING) {
      icon = R.drawable.pause;
    } else {
      icon = R.drawable.play_arrow;
    }

    String title = "title";
    if (service.getTrack() != null) {
      title = service.getTrack().getTagTitle();
    }

    String part = "part";
    if (service.getTrack() != null) {
      part = service.getTrack().getPart();
    }

    Intent musicPlayerActivityIntent = new Intent(context, MusicPlayerActivity.class);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(
            context, 1, musicPlayerActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID);
    b.setStyle(
            new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0)
                .setMediaSession(mediaSession.getSessionToken())
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_STOP)))
        .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        .setSmallIcon(R.drawable.hand_music)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .addAction(
            new NotificationCompat.Action(
                icon,
                "Play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context, PlaybackStateCompat.ACTION_PLAY_PAUSE)))
        .addAction(
            new NotificationCompat.Action(
                R.drawable.close,
                "Stop",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context, PlaybackStateCompat.ACTION_STOP)))
        .setContentIntent(pendingIntent)
        .setContentTitle(title)
        .setContentText(part)
        .setOnlyAlertOnce(true)
        .setDeleteIntent(
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                context, PlaybackStateCompat.ACTION_STOP));

    return (b.build());
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private void initChannels(Context context) {
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationChannel channel =
        new NotificationChannel(CHANNEL_ID, "Track playback", NotificationManager.IMPORTANCE_LOW);
    channel.setDescription("Learning track playback controls");
    channel.setShowBadge(false);
    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
    notificationManager.createNotificationChannel(channel);
  }
}
