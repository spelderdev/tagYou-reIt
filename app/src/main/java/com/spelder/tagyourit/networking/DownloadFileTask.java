package com.spelder.tagyourit.networking;

import android.os.AsyncTask;
import android.util.Log;
import com.spelder.tagyourit.cache.CacheManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/** AsyncTask used to download files from a URL. */
public class DownloadFileTask extends AsyncTask<String, Void, Boolean> {
  private final String TAG = DownloadFileTask.class.getName();

  public DownloadFileTask() {}

  @Override
  protected Boolean doInBackground(String... context) {
    Log.d(TAG, "Link: " + context[0] + ", folder: " + context[1] + ", file: " + context[2]);

    final File f = new File(context[1], context[2]);

    int numRetry = 5;
    while (numRetry > 0 && !f.exists()) {
      try {
        download(context[0], f);
      } catch (IOException e) {
        Log.d(TAG, "Exception during download, retry=" + numRetry, e);
        numRetry--;
      }
    }

    new Thread(() -> CacheManager.checkCacheAndClear(f.getParentFile(), f.getAbsolutePath()))
        .start();

    Log.d(TAG, "Return " + f.exists());
    return f.exists();
  }

  private void download(String link, File file) throws IOException {
    DataOutputStream fos = null;
    try {
      URL u = new URL(link);
      URLConnection conn = u.openConnection();

      // Without this cannot get the content length
      conn.setRequestProperty("Accept-Encoding", "identity");

      int contentLength = conn.getContentLength();
      DataInputStream stream = new DataInputStream(u.openStream());
      byte[] buffer = new byte[contentLength];
      stream.readFully(buffer);
      stream.close();
      if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
        fos = new DataOutputStream(new FileOutputStream(file, false));
        fos.write(buffer);
        fos.flush();
      }
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          Log.d(TAG, "Exception closing", e);
        }
      }
    }
  }
}
