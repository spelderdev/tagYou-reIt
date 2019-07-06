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
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import com.spelder.tagyourit.ui.MainActivity;
import java.util.List;

/** The favorites tag list. */
public class FavoritesFragment extends ListFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener {
  private TagListAdapter listAdapter;

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
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    TagDb db = new TagDb(getActivity());
    List<Tag> tags = db.getFavorites(new FilterBuilder(getActivity()).build());
    listAdapter.clearTags();
    listAdapter.addTags(tags);
    listAdapter.notifyDataSetChanged();

    return inflater.inflate(R.layout.favorites_view, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    setListAdapter(listAdapter);

    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
    Tag clickedTag = (Tag) listAdapter.getItem(position);
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
    if (key.startsWith("filter_")) {
      Log.d("TagListFragment", "filterChanged");
      TagDb db = new TagDb(getActivity());
      List<Tag> tags = db.getFavorites(new FilterBuilder(getActivity()).build());
      listAdapter.clearTags();
      listAdapter.addTags(tags);
      listAdapter.notifyDataSetChanged();
    }
  }
}
