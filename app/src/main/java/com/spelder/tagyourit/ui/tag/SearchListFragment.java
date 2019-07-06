package com.spelder.tagyourit.ui.tag;

import android.os.Bundle;
import com.spelder.tagyourit.networking.ResultAction;
import com.spelder.tagyourit.networking.api.SortBy;

/** Instance of tag list with modifications for search. */
public class SearchListFragment extends TagListFragment {
  private String query;

  public static SearchListFragment newInstance() {
    SearchListFragment f = new SearchListFragment();
    Bundle args = new Bundle();
    args.putParcelable("sortBy", SortBy.DOWNLOAD);
    f.setArguments(args);
    return f;
  }

  public void loadSearch(String query) {
    this.query = query;
    finished = false;
    if (created) {
      getListView().setSelection(0);
      getAndCancelDownloadTask(true).execute();
    }
  }

  @Override
  String getQuery() {
    return query;
  }

  @Override
  ResultAction createResultAction(boolean shouldClear) {
    if (shouldClear) {
      return this::clearAndAddTags;
    } else {
      return this::addTags;
    }
  }
}
