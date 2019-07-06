package com.spelder.tagyourit.cache;

import android.util.Log;
import java.io.File;

/** Methods used for managing the cached files stored on the device. */
public class CacheManager {
  private static final long MAX_SIZE = 5242880L; // 5MB

  private static final String TAG = CacheManager.class.getName();

  private CacheManager() {
    // Do not instantiate
  }

  public static void checkCacheAndClear(File cacheDir, String path) {
    long size = getDirSize(cacheDir);
    Log.i(TAG, "Cache Directory Size: " + size);

    if (size > MAX_SIZE) {
      cleanDir(cacheDir, size - MAX_SIZE, path);
    }
  }

  public static void cleanDir(File dir, long bytes, String path) {
    Log.i(TAG, "Cleaning Cache Directory");

    long bytesDeleted = 0;
    File[] files = dir.listFiles();

    if (files == null) {
      return;
    }

    for (File file : files) {
      if (!file.getAbsolutePath().equals(path)) {
        bytesDeleted += file.length();
        Log.d(TAG, "Deleting file: " + file.getAbsolutePath() + ", deleted = " + file.delete());
      }

      if (bytesDeleted >= bytes) {
        break;
      }
    }
  }

  public static long getDirSize(File dir) {
    long size = 0;
    File[] files = dir.listFiles();

    if (files == null) {
      return size;
    }

    for (File file : files) {
      if (file.isFile()) {
        size += file.length();
      }
    }

    return size;
  }
}
