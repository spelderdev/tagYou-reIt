package com.spelder.tagyourit.ui.lists;

import static com.spelder.tagyourit.ui.FragmentSwitcher.PAR_KEY;

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
import com.spelder.tagyourit.model.ListProperties;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.networking.api.SortBuilder;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import com.spelder.tagyourit.ui.MainActivity;
import com.spelder.tagyourit.ui.settings.filter.FilterBar;
import com.spelder.tagyourit.ui.tag.TagListAdapter;
import java.util.List;

/** The favorites tag list. */
public class CustomListFragment extends ListFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener {
  private TagListAdapter listAdapter;
  private FilterBar filterBar;
  private FilterBar filterBarEmpty;
  private ListProperties listProperties;

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
    listProperties = retrieveListProperties();
    return inflater.inflate(R.layout.favorites_view, container, false);
  }

  @Override
  public void onResume() {
    super.onResume();

    TagDb db = new TagDb(getActivity());
    listProperties = db.getListProperties(listProperties.getDbId());
    SortBuilder sort = new SortBuilder(getContext());
    List<Tag> tags =
        db.getTagsForList(
            new FilterBuilder(getActivity()).build(), sort.build(), listProperties.getDbId());
    listAdapter.clearTags();
    listAdapter.addTags(tags);
    listAdapter.notifyDataSetChanged();
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
      filterBarEmpty.setupFilterBar(
          listView.getEmptyView(), getActivity().getSupportFragmentManager());
    }

    super.onActivityCreated(savedInstanceState);
  }

  private ListProperties retrieveListProperties() {
    Bundle bundle = getArguments();
    if (bundle == null) {
      return null;
    }

    return bundle.getParcelable(PAR_KEY);
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
    if (key.startsWith("filter_") || SortBuilder.isSortKey(key)) {
      Log.d("TagListFragment", "filterChanged");
      TagDb db = new TagDb(getActivity());
      SortBuilder sort = new SortBuilder(getContext());
      List<Tag> tags =
          db.getTagsForList(
              new FilterBuilder(getActivity()).build(), sort.build(), listProperties.getDbId());
      listAdapter.clearTags();
      listAdapter.addTags(tags);
      listAdapter.notifyDataSetChanged();
    }
  }

  public ListProperties getListProperties() {
    return listProperties;
  }
}
