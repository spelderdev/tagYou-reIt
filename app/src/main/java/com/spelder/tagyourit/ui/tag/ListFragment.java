package com.spelder.tagyourit.ui.tag;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.ListProperties;
import com.spelder.tagyourit.ui.MainActivity;
import java.util.List;

/** The list of lists. */
public class ListFragment extends androidx.fragment.app.ListFragment {
  private ListPropertiesListAdapter listAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    listAdapter = new ListPropertiesListAdapter(getActivity());
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    TagDb db = new TagDb(getActivity());
    List<ListProperties> properties = db.getListProperties();
    listAdapter.clearProperties();
    listAdapter.addProperties(properties);
    listAdapter.notifyDataSetChanged();

    return inflater.inflate(R.layout.list_view, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    setListAdapter(listAdapter);

    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
    ListProperties clickedList = (ListProperties) getListView().getItemAtPosition(position);
    Log.i("FragmentList", "Item clicked: " + clickedList.getName());
    MainActivity activity = (MainActivity) getActivity();
    if (activity == null) {
      return;
    }
    activity.getManager().displayList(clickedList);
  }
}
