package com.spelder.tagyourit.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.networking.DownloadFileTask;
import com.spelder.tagyourit.ui.FragmentSwitcher;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Dialog box for downloading and removing favorites. This displays a progress bar and closes when
 * finished.
 */
public class DownloadFavoritesDialog extends DialogFragment {
  private static final String TAG = "PreferencesFragment";

  private ProgressBar progress;

  private boolean shouldStopThread = false;

  @Override
  @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Bundle bundle = getArguments();
    boolean download = true;
    if (bundle != null) {
      download = bundle.getBoolean(FragmentSwitcher.DOWNLOAD_KEY);
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    View view = View.inflate(getContext(), R.layout.dialog_download_favorites, null);

    final Thread thread;
    if (download) {
      builder.setTitle("Downloading Favorites");
      thread =
          new Thread(
              () -> {
                downloadTags();
                if (!shouldStopThread) {
                  if (getDialog() != null) {
                    getDialog().dismiss();
                  }
                }
              });
    } else {
      builder.setTitle("Removing Favorites Download");
      thread =
          new Thread(
              () -> {
                removeDownloadedTags();
                if (!shouldStopThread) {
                  if (getDialog() != null) {
                    getDialog().dismiss();
                  }
                }
              });
    }

    builder
        .setView(view)
        .setNegativeButton("Cancel", (DialogInterface dialog, int id) -> shouldStopThread = true);

    progress = view.findViewById(R.id.progressBar2);
    this.setCancelable(false);

    thread.start();

    return builder.create();
  }

  private void downloadTags() {
    TagDb db = new TagDb(getActivity());
    List<Tag> favorites = db.getFavorites();

    if (favorites == null) {
      return;
    }

    progress.setMax(favorites.size());

    for (int i = 0; i < favorites.size(); i++) {
      if (shouldStopThread) {
        return;
      }

      Tag tag = favorites.get(i);
      progress.setProgress(i + 1);

      tag.setDownloaded(true);
      String absoluteFilePath = tag.getSheetMusicPath(getActivity());
      Log.d(TAG, absoluteFilePath);

      File file = new File(absoluteFilePath);
      if (file.exists()) {
        Log.d(TAG, "File exists");
      } else {
        DownloadFileTask task = new DownloadFileTask();
        task.execute(
            tag.getSheetMusicLink(),
            tag.getSheetMusicDirectory(getActivity()),
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

  private void removeDownloadedTags() {
    Tag t = new Tag();
    t.setDownloaded(true);
    File dir = new File(t.getSheetMusicDirectory(getActivity()));
    File[] files = dir.listFiles();

    if (files.length == 0) {
      return;
    }

    progress.setMax(files.length);

    Log.d(TAG, "Removing Downloads of Size: " + files.length);
    for (int i = 0; i < files.length; i++) {
      if (shouldStopThread) {
        Log.d(TAG, "Stopping Thread");
        return;
      }

      File tag = files[i];
      progress.setProgress(i + 1);

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
