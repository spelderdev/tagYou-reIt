package com.spelder.tagyourit.networking;

import android.util.Log;
import com.spelder.tagyourit.model.Tag;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.HttpsURLConnection;

/** Perform action of rating the tag to the tag API. */
public class RateTag {
  private static final String BASE_URL = "https://www.barbershoptags.com/api.php?action=rate";

  private static final String TAG_ID = "id=";

  private static final String RATING_ID = "rating=";

  private static final String TAG = "RateTagDialog";

  private static final ExecutorService executor =
      Executors.newCachedThreadPool(Executors.defaultThreadFactory());

  public static void rateTag(int tagId, float rating) {
    int ratingInt = (int) rating;
    if (ratingInt < 1) {
      ratingInt = 1;
    }

    final String html = BASE_URL + "&" + TAG_ID + tagId + "&" + RATING_ID + ratingInt;
    Log.d(TAG, html);

    Future<List<Tag>> future =
        executor.submit(
            () -> {
              URL url = new URL(html);
              HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
              try {
                conn.setReadTimeout(50000 /* milliseconds */);
                conn.setConnectTimeout(50000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("TagListRetriever", "The response is: " + response);
              } finally {
                conn.disconnect();
              }

              return null;
            });

    try {
      future.get(50, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      e.printStackTrace();
    }
  }
}
