package com.spelder.tagyourit.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CancelDownloadReceiver extends BroadcastReceiver {
  private static final String TAG = CancelDownloadReceiver.class.getName();

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Cancelling downloading");
    DownloadTagService.cancel();
  }
}
