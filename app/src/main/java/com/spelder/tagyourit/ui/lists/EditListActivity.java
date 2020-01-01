package com.spelder.tagyourit.ui.lists;

import static com.spelder.tagyourit.ui.FragmentSwitcher.PAR_KEY;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
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
import com.spelder.tagyourit.model.ListColor;
import com.spelder.tagyourit.model.ListIcon;
import com.spelder.tagyourit.model.ListProperties;

/** Activity used to edit list properties. */
public class EditListActivity extends AppCompatActivity {
  private static final int UNSELECTED_TINT = Color.LTGRAY;

  private ListProperties listProperties;
  private Switch downloadSheet;
  private Switch downloadTrack;
  private EditText listName;
  private Button addButton;
  private TextView title;
  private ListIcon selectedIcon = ListIcon.DEFAULT;
  private ImageView selectedIconView;
  private ListColor selectedColor = ListColor.DEFAULT;

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
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });

    setupListProperties();

    LinearLayout iconView = findViewById(R.id.add_list_icons);
    for (ListIcon icon : ListIcon.values()) {
      iconView.addView(createIconView(getApplicationContext(), icon));
    }

    LinearLayout colorView = findViewById(R.id.add_list_colors);
    for (ListColor icon : ListColor.values()) {
      colorView.addView(createColorView(getApplicationContext(), icon));
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
      selectedColor = ListColor.fromDbId(listProperties.getColor());
    }
    addButton.setEnabled(listName.getText().length() > 0);
  }

  private ListProperties createListProperties() {
    listProperties.setIcon(ListIcon.DEFAULT);
    listProperties.setName(listName.getText().toString());
    listProperties.setDownloadSheet(downloadSheet.isChecked());
    listProperties.setDownloadTrack(downloadTrack.isChecked());
    listProperties.setIcon(selectedIcon);
    listProperties.setColor(selectedColor.getColorId());

    return listProperties;
  }

  private View createIconView(Context context, ListIcon icon) {
    int size = dpToPixels(48);

    ImageView iconView = new ImageView(context);
    iconView.setImageResource(icon.getResourceId());
    iconView.setColorFilter(UNSELECTED_TINT);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
    iconView.setLayoutParams(layoutParams);

    iconView.setOnClickListener(
        view -> {
          selectedIconView.setColorFilter(UNSELECTED_TINT);
          iconView.setColorFilter(selectedColor.getColorId());
          selectedIcon = icon;
          selectedIconView = iconView;
        });

    if (icon == listProperties.getIcon()) {
      iconView.setColorFilter(selectedColor.getColorId());
      selectedIconView = iconView;
      selectedIcon = icon;
    }

    return iconView;
  }

  private View createColorView(Context context, ListColor color) {
    int size = dpToPixels(36);
    int margin = dpToPixels(6);

    ImageView colorView = new ImageView(context);
    colorView.setImageResource(R.drawable.color_picker);
    colorView.setColorFilter(color.getColorId());
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
    layoutParams.setMargins(margin, margin, margin, margin);
    colorView.setLayoutParams(layoutParams);

    colorView.setOnClickListener(
        view -> {
          selectedColor = color;
          selectedIconView.setColorFilter(selectedColor.getColorId());
        });

    return colorView;
  }

  private int dpToPixels(int dp) {
    Resources r = getResources();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
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
