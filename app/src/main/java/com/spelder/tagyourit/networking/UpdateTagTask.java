package com.spelder.tagyourit.networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.Tag;
import java.util.List;

/**
 * Checks to see if the update frequency of the tag has been reached and updates the tag's
 * information if it has.
 */
public class UpdateTagTask implements Runnable {
  private static final String TAG = UpdateTagTask.class.getName();

  private final TagDb db;

  private final SharedPreferences preferences;

  private TagListRetriever ret;

  private boolean cancel;

  public UpdateTagTask(Context context) {
    cancel = false;
    db = new TagDb(context);
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  private int getUpdateFrequencyInDays() {
    String freq = preferences.getString("pref_key_update_frequency", "14");
    if (freq != null) {
      return Integer.valueOf(freq);
    }

    return 14;
  }

  @Override
  public void run() {
    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

    int daysToUpdate = getUpdateFrequencyInDays();
    List<Integer> tagIdList = db.getOutdatedTagIdList(daysToUpdate);

    Log.d(TAG, "Updating tags: " + tagIdList.size() + " in last " + daysToUpdate + " days");
    for (int i = 0; i < tagIdList.size(); i++) {
      if (cancel) {
        Log.d(TAG, "Canceling tag update");
        break;
      }

      try {
        int tagId = tagIdList.get(i);
        Log.d(TAG, "Updating tag with Id: " + tagId);
        ret = new TagListRetriever(tagId);
        List<Tag> returnedTags = ret.downloadUrl();
        db.updateTags(returnedTags);
      } catch (Exception e) {
        if (e.getMessage() != null) {
          Log.e(TAG, "Error updating tags", e);
        } else {
          Log.e(TAG, "Error updating tags");
        }
      }
    }
  }

  public void cancel() {
    cancel = true;
    if (ret != null) {
      ret.cancel();
    }
  }
}
