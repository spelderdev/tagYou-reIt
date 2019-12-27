package com.spelder.tagyourit.ui.lists;

import static com.spelder.tagyourit.ui.FragmentSwitcher.PAR_KEY;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.ListIcon;
import com.spelder.tagyourit.model.ListProperties;

/** Activity used to display and control the music player. */
public class EditListActivity extends AppCompatActivity {
  private static final String TAG = "EditListActivity";
  private static final int UNSELECTED_TINT = Color.GRAY;

  private ListProperties listProperties;
  private Switch downloadSheet;
  private Switch downloadTrack;
  private EditText listName;
  private Button addButton;
  private TextView title;
  private ListIcon selectedIcon = ListIcon.DEFAULT;
  private ImageView selectedIconView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_list);

    ImageView close = findViewById(R.id.add_list_close);
    close.setOnClickListener(view -> onBackPressed());

    downloadSheet = findViewById(R.id.add_list_download_sheet);
    downloadTrack = findViewById(R.id.add_list_download_track);
    title = findViewById(R.id.add_list_title);

    addButton = findViewById(R.id.add_list_create_button);
    addButton.setOnClickListener(
        view -> {
          TagDb db = new TagDb(getApplicationContext());
          db.updateListProperties(createListProperties());
          finish();
        });

    listName = findViewById(R.id.add_list_name);
    listName.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            addButton.setEnabled(s.length() > 0);
            Log.d(TAG, "Length" + s.length());
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });

    setupListProperties();

    LinearLayout iconView = findViewById(R.id.add_list_icons);
    for (ListIcon icon : ListIcon.values()) {
      iconView.addView(createIconView(getApplicationContext(), icon));
    }
  }

  private void setupListProperties() {
    listProperties = getIntent().getParcelableExtra(PAR_KEY);
    if (listProperties == null) {
      listProperties = new ListProperties();
      listProperties.setUserCreated(true);
      addButton.setText(R.string.list_create_button);
      title.setText(R.string.list_new);
    } else {
      listName.setText(listProperties.getName());
      downloadSheet.setChecked(listProperties.isDownloadSheet());
      downloadTrack.setChecked(listProperties.isDownloadTrack());
      addButton.setText(R.string.list_update_button);
      title.setText(R.string.list_update);
    }
    Log.d(TAG, "Length" + listName.getText().length());
    addButton.setEnabled(listName.getText().length() > 0);
  }

  private ListProperties createListProperties() {
    listProperties.setIcon(ListIcon.DEFAULT);
    listProperties.setName(listName.getText().toString());
    listProperties.setDownloadSheet(downloadSheet.isChecked());
    listProperties.setDownloadTrack(downloadTrack.isChecked());
    listProperties.setIcon(selectedIcon);

    return listProperties;
  }

  private View createIconView(Context context, ListIcon icon) {
    ImageView iconView = new ImageView(context);
    iconView.setImageResource(icon.getResourceId());
    iconView.setColorFilter(UNSELECTED_TINT);

    iconView.setOnClickListener(
        view -> {
          selectedIconView.setColorFilter(UNSELECTED_TINT);
          iconView.clearColorFilter();
          selectedIcon = icon;
          selectedIconView = iconView;
        });

    if (icon == listProperties.getIcon()) {
      iconView.clearColorFilter();
      selectedIconView = iconView;
      selectedIcon = icon;
    }

    return iconView;
  }

  @Override
  public void onBackPressed() {
    finish();
    openActivityFromBottom();
  }

  private void openActivityFromBottom() {
    overridePendingTransition(R.anim.stay, R.anim.slide_down);
  }
}
