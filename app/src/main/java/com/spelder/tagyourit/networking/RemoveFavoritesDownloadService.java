package com.spelder.tagyourit.networking;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.model.Tag;
import java.io.File;

public class RemoveFavoritesDownloadService extends IntentService {
  private final String TAG = RemoveFavoritesDownloadService.class.getName();
  private static final int NOTIFICATION_ID = 29335;
  private static final String CHANNEL_ID = "download_favorites";

  private NotificationManagerCompat notificationManager;
  private NotificationCompat.Builder builder;
  private static boolean shouldCancel = false;

  public RemoveFavoritesDownloadService() {
    super("RemoveFavoritesDownloadService");
    shouldCancel = false;
  }

  @Override
  protected void onHandleIntent(Intent workIntent) {
    createNotification();
    removeDownloadedTags();
    finishNotification();
  }

  public static void cancel() {
    shouldCancel = true;
  }

  private void createNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      initChannels(getApplicationContext());
    }

    notificationManager = NotificationManagerCompat.from(this);
    builder = new NotificationCompat.Builder(this, CHANNEL_ID);

    Intent cancelIntent = new Intent(getApplicationContext(), CancelDownloadReceiver.class);
    cancelIntent.setAction("notification_cancelled");
    PendingIntent cancelPendingIntent =
        PendingIntent.getBroadcast(
            getApplicationContext(), 0, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);

    builder
        .setContentTitle("Removing Favorites Downloads")
        .setSmallIcon(R.drawable.hand_music)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .addAction(
            new NotificationCompat.Action(R.drawable.hand_music, "Cancel", cancelPendingIntent));
  }

  private void updateNotificationProgress(int maxProgress, int currentProgress) {
    builder.setProgress(maxProgress, currentProgress, false);
    notificationManager.notify(NOTIFICATION_ID, builder.build());
  }

  private void finishNotification() {
    builder.setContentText("Removing complete").setProgress(0, 0, false);
    notificationManager.notify(NOTIFICATION_ID, builder.build());
    notificationManager.cancel(NOTIFICATION_ID);
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private void initChannels(Context context) {
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationChannel channel =
        new NotificationChannel(CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_LOW);
    channel.setDescription("Downloading tag data");
    channel.setShowBadge(false);
    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
    if (notificationManager != null) {
      notificationManager.createNotificationChannel(channel);
    }
  }

  private void removeDownloadedTags() {
    Tag t = new Tag();
    t.setDownloaded(true);
    File dir = new File(t.getSheetMusicDirectory(getApplicationContext()));
    File[] files = dir.listFiles();

    if (files == null || files.length == 0) {
      return;
    }

    Log.d(TAG, "Removing Downloads of Size: " + files.length);
    for (int i = 0; i < files.length; i++) {
      if (shouldCancel) {
        return;
      }

      File tag = files[i];
      updateNotificationProgress(files.length, i + 1);

      String absoluteFilePath = tag.getAbsolutePath();
      Log.d(TAG, tag.getAbsolutePath());
      Log.d(TAG, "" + tag.exists());
      if (tag.exists()) {
        Log.d(TAG, "Removing file: " + absoluteFilePath);
        Log.d(TAG, "" + tag.delete());
      }
    }
  }
}
