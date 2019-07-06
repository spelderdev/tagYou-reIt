package com.spelder.tagyourit.networking;

import android.util.Log;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.networking.api.SortBy;
import com.spelder.tagyourit.networking.api.filter.FilterBy;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;

/** Queries the barbershop tag API and converts it into the internal model. */
public class TagListRetriever {
  private static final String ID_LABEL = "id=";

  private static final String BASE_URL = "https://www.barbershoptags.com/api.php?";

  private static final String DOWNLOAD_NUMBER_URL = "n=20";

  private static final String START_NUMBER_URL = "start=";

  private static final String SEARCH_URL = "q=";

  private final ExecutorService executor =
      Executors.newCachedThreadPool(Executors.defaultThreadFactory());

  private final String downloadUrl;

  private Future<List<Tag>> future;

  public TagListRetriever(int tagId) {
    downloadUrl = BASE_URL + ID_LABEL + tagId;
    Log.d("TagListRetriever", "" + downloadUrl);
  }

  TagListRetriever(String search, SortBy sortBy, FilterBy filterBy, int startNum) {
    String sortByString = sortBy.getSortBy();
    String filterByString = filterBy.getFilter();
    search = search.replaceAll("\\s+", "%20");
    downloadUrl =
        BASE_URL
            + SEARCH_URL
            + search
            + "&"
            + sortByString
            + "&"
            + DOWNLOAD_NUMBER_URL
            + "&"
            + START_NUMBER_URL
            + (startNum + 1)
            + filterByString;

    Log.d("TagListRetriever", "" + downloadUrl);
  }

  public List<Tag> downloadUrl() throws Exception {
    future =
        executor.submit(
            () -> {
              URL url = new URL(downloadUrl);
              HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
              ArrayList<Tag> tagList = new ArrayList<>();
              try {
                conn.setReadTimeout(50000 /* milliseconds */);
                conn.setConnectTimeout(50000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("TagListRetriever", "The response is: " + response);
                try (InputStream is = conn.getInputStream()) {
                  tagList = parseXml(is);
                }

                return tagList;
              } catch (Exception e) {
                Log.e("TagListRetriever", "Exception: ", e);
                return tagList;
              } finally {
                conn.disconnect();
              }
            });
    return future.get(50, TimeUnit.SECONDS);
  }

  private ArrayList<Tag> parseXml(InputStream stream) {
    TagXmlParser parser = new TagXmlParser();
    ArrayList<Tag> tags = null;
    try {
      tags = parser.parse(stream);
    } catch (Exception e) {
      Log.e("TagListRetriever", "Cannot parse data", e);
    }
    return tags;
  }

  void cancel() {
    Log.d("TagListRetriever", "Cancelling current download");
    if (future != null) {
      Log.d("TagListRetriever", "Cancel retriever");
      future.cancel(true);
    }
  }
}
