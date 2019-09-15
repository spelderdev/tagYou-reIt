package com.spelder.tagyourit.ui.tag;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.networking.api.SortBy;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import com.spelder.tagyourit.ui.MainActivity;
import com.spelder.tagyourit.ui.settings.SortBottomSheet;
import com.spelder.tagyourit.ui.settings.filter.FilterBar;
import java.util.List;

/** The favorites tag list. */
public class FavoritesFragment extends ListFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener {
  private TagListAdapter listAdapter;
  private FilterBar filterBar;
  private FilterBar filterBarEmpty;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    listAdapter = new TagListAdapter(getActivity());
    PreferenceManager.getDefaultSharedPreferences(getActivity())
        .registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    PreferenceManager.getDefaultSharedPreferences(getActivity())
        .unregisterOnSharedPreferenceChangeListener(this);
    if (filterBar != null) {
      PreferenceManager.getDefaultSharedPreferences(getActivity())
          .unregisterOnSharedPreferenceChangeListener(filterBar);
    }
    if (filterBarEmpty != null) {
      PreferenceManager.getDefaultSharedPreferences(getActivity())
          .unregisterOnSharedPreferenceChangeListener(filterBarEmpty);
    }
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    TagDb db = new TagDb(getActivity());
    SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getContext());
    SortBy sortBy =
        SortBy.valueOf(preference.getString(SortBottomSheet.SORT_BY_LABEL, SortBy.TITLE.name()));
    List<Tag> tags = db.getFavorites(new FilterBuilder(getActivity()).build(), sortBy);
    listAdapter.clearTags();
    listAdapter.addTags(tags);
    listAdapter.notifyDataSetChanged();

    return inflater.inflate(R.layout.favorites_view, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    setListAdapter(listAdapter);

    ListView listView = getListView();
    listView.addHeaderView(getLayoutInflater().inflate(R.layout.filter, null));
    listView.setHeaderDividersEnabled(false);

    if (getActivity() != null) {
      filterBar = new FilterBar(getActivity());
      PreferenceManager.getDefaultSharedPreferences(getActivity())
          .registerOnSharedPreferenceChangeListener(filterBar);
      filterBar.setupFilterBar(listView, getActivity().getSupportFragmentManager());

      filterBarEmpty = new FilterBar(getActivity());
      PreferenceManager.getDefaultSharedPreferences(getActivity())
          .registerOnSharedPreferenceChangeListener(filterBarEmpty);
      filterBarEmpty.setupFilterBar(listView.getEmptyView(), getActivity().getSupportFragmentManager());
    }

    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
    Tag clickedTag = (Tag) getListView().getItemAtPosition(position);
    Log.i(
        "FragmentList",
        "Item clicked: " + clickedTag.getTitle() + " " + clickedTag.getSheetMusicType());
    MainActivity activity = (MainActivity) getActivity();
    if (activity == null) {
      return;
    }
    activity.getManager().displayTag(clickedTag);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.startsWith("filter_") || key.equals(SortBottomSheet.SORT_BY_LABEL)) {
      Log.d("TagListFragment", "filterChanged");
      TagDb db = new TagDb(getActivity());
      SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getContext());
      SortBy sortBy =
          SortBy.valueOf(preference.getString(SortBottomSheet.SORT_BY_LABEL, SortBy.TITLE.name()));
      List<Tag> tags = db.getFavorites(new FilterBuilder(getActivity()).build(), sortBy);
      listAdapter.clearTags();
      listAdapter.addTags(tags);
      listAdapter.notifyDataSetChanged();
    }
  }
}
