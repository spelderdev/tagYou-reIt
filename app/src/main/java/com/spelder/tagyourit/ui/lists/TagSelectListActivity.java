package com.spelder.tagyourit.ui.lists;

import static com.spelder.tagyourit.ui.FragmentSwitcher.PAR_KEY;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.ListProperties;
import com.spelder.tagyourit.model.Tag;
import java.util.List;

/** The list of lists. */
public class TagSelectListActivity extends ListActivity {
  private ListSelectionListAdapter listAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tag_select_lists);
    listAdapter = new ListSelectionListAdapter(this);
    setListAdapter(listAdapter);

    ImageView close = findViewById(R.id.tag_select_close);
    close.setOnClickListener(view -> onBackPressed());

    Tag tag = getIntent().getParcelableExtra(PAR_KEY);

    Button apply = findViewById(R.id.tag_select_apply_button);
    apply.setOnClickListener(
        view -> {
          List<Long> selectedDbIds = listAdapter.getSelectedListIds();

          TagDb db = new TagDb(this);
          db.updateTagList(tag, selectedDbIds);

          finish();
        });

    if (tag == null) {
      return;
    }

    TagDb db = new TagDb(this);
    List<ListProperties> properties = db.getListProperties();
    listAdapter.clearProperties();
    listAdapter.addProperties(properties);
    listAdapter.setSelectedDbIds(db.getListsForTag(tag));
    listAdapter.notifyDataSetChanged();
  }

  @Override
  public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
    ListProperties clickedList = (ListProperties) getListView().getItemAtPosition(position);
    Log.i("FragmentList", "Item clicked: " + clickedList.getName());
  }
}
