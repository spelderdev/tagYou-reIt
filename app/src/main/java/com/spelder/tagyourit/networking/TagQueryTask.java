package com.spelder.tagyourit.networking;

import android.os.AsyncTask;
import android.util.Log;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.networking.api.SortBy;
import com.spelder.tagyourit.networking.api.filter.FilterBy;
import java.util.List;

/** AsyncTask for querying the tag API to retrieve tag info. */
public class TagQueryTask extends AsyncTask<Object, Void, List<Tag>> {
  private static final String TAG = TagQueryTask.class.getName();
  private final ResultAction resultAction;
  private final String query;
  private final SortBy sortBy;
  private final FilterBy filterBy;
  private final int startNum;
  private TagListRetriever ret;
  private boolean cancelled = false;

  public TagQueryTask(
      ResultAction resultAction, String query, SortBy sortBy, FilterBy filterBy, int startNum) {
    this.resultAction = resultAction;
    this.query = query;
    this.sortBy = sortBy;
    this.filterBy = filterBy;
    this.startNum = startNum;
  }

  @Override
  protected List<Tag> doInBackground(Object... num) {
    try {
      ret = new TagListRetriever(query, sortBy, filterBy, startNum);
      if (!cancelled) {
        return ret.downloadUrl();
      } else {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
  }

  public void cancel() {
    Log.d(TAG, "RET: " + (ret != null));
    cancelled = true;
    if (ret != null) {
      ret.cancel();
    }
  }

  @Override
  protected void onPostExecute(List<Tag> result) {
    if (!cancelled) {
      resultAction.run(result);
    }
  }
}
