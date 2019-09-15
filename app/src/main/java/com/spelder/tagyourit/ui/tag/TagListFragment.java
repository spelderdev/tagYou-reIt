package com.spelder.tagyourit.ui.tag;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.networking.ResultAction;
import com.spelder.tagyourit.networking.TagQueryTask;
import com.spelder.tagyourit.networking.api.SortBy;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import com.spelder.tagyourit.networking.api.filter.FilterBy;
import com.spelder.tagyourit.ui.MainActivity;
import com.spelder.tagyourit.ui.settings.SortBottomSheet;
import com.spelder.tagyourit.ui.settings.filter.FilterBar;
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

  private View browseContent;

  private TextView error_text;

  private View error_layout;

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
    SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
    sortBy =
        SortBy.valueOf(preference.getString(SortBottomSheet.SORT_BY_LABEL, SortBy.TITLE.name()));
    listAdapter = new TagListAdapter(getActivity());
    finished = false;
    preference.registerOnSharedPreferenceChangeListener(this);
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

    View content = inflater.inflate(R.layout.browse_tab, container, false);
    noResultsView = content.findViewById(R.id.browse_tab_no_results);
    loadingView = content.findViewById(R.id.browse_tab_loading);

    error_text = content.findViewById(R.id.browse_tab_view_error_text);
    error_layout = content.findViewById(R.id.browse_tab_view_error);
    browseContent = content.findViewById(R.id.browse_content);

    Button error_button = content.findViewById(R.id.browse_tab_view_refresh_button);
    error_button.setOnClickListener(
        v -> {
          Activity activity = getActivity();
          if (activity == null) {
            return;
          }
          ConnectivityManager connMgr =
              (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
          NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
          if (networkInfo != null && networkInfo.isConnected()) {
            getAndCancelDownloadTask(true);
            unsetNetworkError();
          }
        });

    Activity activity = getActivity();
    if (activity == null) {
      setNetworkError();
      return content;
    }
    ConnectivityManager connMgr =
        (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    if (networkInfo == null || !networkInfo.isConnected()) {
      setNetworkError();
      return content;
    }

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
    getListView().addFooterView(footerView);
    setListAdapter(listAdapter);
    listAdapter.getCount();
    getListView().setOnScrollListener(this);
    created = true;
    setLoading(loading);

    ListView listView = getListView();
    listView.addHeaderView(getLayoutInflater().inflate(R.layout.filter, null));
    listView.setHeaderDividersEnabled(false);

    if (getActivity() != null) {
      FilterBar.setupFilterBar(listView, getActivity().getSupportFragmentManager());
      FilterBar.setupFilterBar(noResultsView, getActivity().getSupportFragmentManager());
    }
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
    if (key.startsWith("filter_") || key.equals(SortBottomSheet.SORT_BY_LABEL)) {
      finished = false;
      Log.d("TagListFragment", "filterChanged");

      SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
      sortBy =
          SortBy.valueOf(preference.getString(SortBottomSheet.SORT_BY_LABEL, SortBy.TITLE.name()));

      getAndCancelDownloadTask(true).execute();
    }
  }

  private void setNetworkError() {
    error_text.setText(R.string.network_error);
    error_layout.setVisibility(View.VISIBLE);
    browseContent.setVisibility(View.GONE);
  }

  private void unsetNetworkError() {
    error_layout.setVisibility(View.GONE);
    browseContent.setVisibility(View.VISIBLE);
  }
}
