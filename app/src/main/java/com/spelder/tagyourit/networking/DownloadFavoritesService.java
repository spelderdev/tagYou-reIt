package com.spelder.tagyourit.networking;

import static com.spelder.tagyourit.ui.FragmentSwitcher.PAR_KEY;

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
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.ListProperties;
import com.spelder.tagyourit.model.Tag;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DownloadFavoritesService extends IntentService {
  private final String TAG = DownloadFavoritesService.class.getName();
  private static final int NOTIFICATION_ID = 29336;
  private static final String CHANNEL_ID = "download_favorites";

  private NotificationManagerCompat notificationManager;
  private NotificationCompat.Builder builder;
  private static boolean shouldCancel = false;

  public DownloadFavoritesService() {
    super("DownloadFavoritesService");
    shouldCancel = false;
  }

  @Override
  protected void onHandleIntent(Intent workIntent) {
    Log.d(TAG, "Handle Intent");

    createNotification();

    ListProperties listProperties = workIntent.getParcelableExtra(PAR_KEY);
    if (listProperties != null && listProperties.getDbId() != null) {
      downloadTags(listProperties.getDbId());
    }

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
        .setContentTitle("Downloading Favorites")
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
    builder.setContentText("Download complete").setProgress(0, 0, false);
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

  private void downloadTags(long dbId) {
    TagDb db = new TagDb(getApplicationContext());
    List<Tag> tags = db.getTagsForList(dbId);

    if (tags == null) {
      return;
    }

    for (int i = 0; i < tags.size(); i++) {
      if (shouldCancel) {
        return;
      }

      Tag tag = tags.get(i);
      updateNotificationProgress(tags.size(), i + 1);

      tag.setDownloaded(true);
      String absoluteFilePath = tag.getSheetMusicPath(getApplicationContext());
      Log.d(TAG, absoluteFilePath);

      File file = new File(absoluteFilePath);
      if (file.exists()) {
        Log.d(TAG, "File exists");
      } else {
        DownloadFileTask task = new DownloadFileTask();
        task.execute(
            tag.getSheetMusicLink(),
            tag.getSheetMusicDirectory(getApplicationContext()),
            tag.getSheetMusicFileName());
        try {
          task.get();
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
        Log.d("DisplayTag", "Downloaded tag");
      }
    }
  }
}
