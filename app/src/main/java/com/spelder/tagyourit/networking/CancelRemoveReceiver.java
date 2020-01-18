package com.spelder.tagyourit.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CancelRemoveReceiver extends BroadcastReceiver {
  private static final String TAG = CancelRemoveReceiver.class.getName();

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Cancelling remove download");
    RemoveTagDownloadService.cancel();
  }
}
