package com.spelder.tagyourit.ui.tag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.networking.TagQueryTask;
import com.spelder.tagyourit.networking.ResultAction;
import com.spelder.tagyourit.networking.api.SortBy;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import com.spelder.tagyourit.networking.api.filter.FilterBy;
import com.spelder.tagyourit.ui.MainActivity;
import java.util.List;

public class TagListFragment extends ListFragment
    implements AbsListView.OnScrollListener, SharedPreferences.OnSharedPreferenceChangeListener {
  private static final int LOADING_LEFT = 10;

  boolean finished = false;

  boolean created = false;

  private TagListAdapter listAdapter;

  private TagQueryTask currentDownloadTask;

  private SortBy sortBy;

  private boolean loading = false;

  private View footerView;

  private Context context;

  private View noResultsView;

  private View loadingView;

  static TagListFragment newInstance(SortBy sortBy) {
    TagListFragment f = new TagListFragment();
    Bundle args = new Bundle();
    args.putParcelable("sortBy", sortBy);
    f.setArguments(args);
    return f;
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("TagListFragment", "onCreate");
    sortBy = getArguments() != null ? getArguments().getParcelable("sortBy") : SortBy.DOWNLOAD;
    listAdapter = new TagListAdapter(getActivity());
    finished = false;
    PreferenceManager.getDefaultSharedPreferences(context)
        .registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    PreferenceManager.getDefaultSharedPreferences(context)
        .unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    loading = false;
    Log.d("TagListFragment", "onCreateVew");

    View content = inflater.inflate(R.layout.browse_tab_view, container, false);
    noResultsView = content.findViewById(R.id.browse_tab_no_results);
    loadingView = content.findViewById(R.id.browse_tab_loading);

    return content;
  }

  @Override
  public void onResume() {
    Log.d("TagListFragment", "onResume: " + finished);
    super.onResume();

    if (finished) {
      footerView.setVisibility(View.GONE);
      this.getListView().removeFooterView(footerView);
    }
  }

  @Override
  public void onDestroyView() {
    created = false;
    super.onDestroyView();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    footerView = View.inflate(getContext(), R.layout.list_footer_view, null);
    this.getListView().addFooterView(footerView);
    setListAdapter(listAdapter);
    listAdapter.getCount();
    getListView().setOnScrollListener(this);
    created = true;
    setLoading(loading);
  }

  @Override
  public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
    Tag clickedTag = (Tag) listAdapter.getItem(position);
    Log.i(
        "FragmentList",
        "Item clicked: " + clickedTag.getTitle() + " " + clickedTag.getSheetMusicType());
    TagDb db = new TagDb(context);
    clickedTag.setDbId(db.isFavorite(clickedTag));
    ((MainActivity) getActivity()).getManager().displayTag(clickedTag);
  }

  void addTags(List<Tag> result) {
    if (result != null && result.size() == 0 && created) {
      finished = true;
      footerView.setVisibility(View.GONE);
      this.getListView().removeFooterView(footerView);
    } else {
      listAdapter.addTags(result);
      listAdapter.notifyDataSetChanged();
    }
    setLoading(false);
  }

  void clearAndAddTags(List<Tag> result) {
    listAdapter.clearTags();
    listAdapter.addTags(result);
    listAdapter.notifyDataSetChanged();
    setLoading(false);
  }

  private void setLoading(boolean isLoading) {
    loading = isLoading;
    if (!created) {
      return;
    }
    Log.d("TagListFragment", "Set Loading to: " + loading);
    if (isLoading) {
      getListView().setEmptyView(loadingView);
      noResultsView.setVisibility(View.GONE);
    } else {
      getListView().setEmptyView(noResultsView);
      loadingView.setVisibility(View.GONE);
    }
  }

  private boolean isLoadingMore() {
    return loading;
  }

  public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
    if (!finished && !isLoadingMore()) {
      boolean loadMore = firstVisible + visibleCount >= totalCount - LOADING_LEFT;
      if (loadMore) {
        if (footerView.getVisibility() == View.GONE) {
          this.getListView().addFooterView(footerView);
          footerView.setVisibility(View.VISIBLE);
        }
        Log.d("TagListFragment", "loadMore");
        getDownloadTask(false).execute();
      }
    }
  }

  synchronized TagQueryTask getAndCancelDownloadTask(boolean shouldClear) {
    if (isLoadingMore() && currentDownloadTask != null) {
      currentDownloadTask.cancel();
    }
    return getDownloadTask(shouldClear);
  }

  private synchronized TagQueryTask getDownloadTask(boolean shouldClear) {
    setLoading(true);
    if (shouldClear) {
      listAdapter.clearTags();
      listAdapter.notifyDataSetChanged();
    }

    currentDownloadTask =
        new TagQueryTask(
            createResultAction(shouldClear),
            getQuery(),
            sortBy,
            getFilter(),
            listAdapter.getCount());

    return currentDownloadTask;
  }

  String getQuery() {
    return "";
  }

  private FilterBy getFilter() {
    FilterBuilder filterBuilder = new FilterBuilder(getContext());
    return filterBuilder.build();
  }

  ResultAction createResultAction(boolean shouldClear) {
    return this::addTags;
  }

  public void onScrollStateChanged(AbsListView v, int s) {}

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.startsWith("filter_")) {
      finished = false;
      Log.d("TagListFragment", "filterChanged");
      getAndCancelDownloadTask(true).execute();
    }
  }
}
