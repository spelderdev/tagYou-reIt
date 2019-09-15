package com.spelder.tagyourit.ui.settings.filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.FragmentManager;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.networking.api.filter.Key;
import com.spelder.tagyourit.networking.api.filter.PartFilter;
import com.spelder.tagyourit.networking.api.filter.Type;

public class FilterBar implements SharedPreferences.OnSharedPreferenceChangeListener {
  private final String SHEET_MUSIC_KEY;
  private final String LEARNING_TRACK_KEY;
  private final String RATING_KEY;
  private final String TYPE_KEY;
  private final String KEY_KEY;
  private final String PARTS_NUMBER_KEY;

  private Button partsButton;
  private Button keyButton;
  private Button typeButton;
  private Button ratingButton;
  private Button learningTracksButton;
  private Button sheetMusicButton;
  private Context context;

  public FilterBar(Context context) {
    SHEET_MUSIC_KEY = context.getResources().getString(R.string.filter_sheet_music_key);
    LEARNING_TRACK_KEY = context.getResources().getString(R.string.filter_learning_track_key);
    RATING_KEY = context.getResources().getString(R.string.filter_rating_key);
    TYPE_KEY = context.getResources().getString(R.string.filter_type_key);
    KEY_KEY = context.getResources().getString(R.string.filter_key_key);
    PARTS_NUMBER_KEY = context.getResources().getString(R.string.filter_part_key);
  }

  public void setupFilterBar(View view, FragmentManager fragmentManager) {
    context = view.getContext();

    partsButton = view.findViewById(R.id.filter_parts);
    partsButton.setOnClickListener(
        v -> {
          FilterPartsBottomSheet bottomSheet = new FilterPartsBottomSheet();
          bottomSheet.show(fragmentManager, "Filter Parts Bottom Sheet");
        });
    keyButton = view.findViewById(R.id.filter_key);
    keyButton.setOnClickListener(
        v -> {
          FilterKeyBottomSheet bottomSheet = new FilterKeyBottomSheet();
          bottomSheet.show(fragmentManager, "Filter Key Bottom Sheet");
        });
    typeButton = view.findViewById(R.id.filter_type);
    typeButton.setOnClickListener(
        v -> {
          FilterTypeBottomSheet bottomSheet = new FilterTypeBottomSheet();
          bottomSheet.show(fragmentManager, "Filter Parts Bottom Sheet");
        });
    ratingButton = view.findViewById(R.id.filter_rating);
    ratingButton.setOnClickListener(
        v -> {
          FilterRatingBottomSheet bottomSheet = new FilterRatingBottomSheet();
          bottomSheet.show(fragmentManager, "Filter Parts Bottom Sheet");
        });
    learningTracksButton = view.findViewById(R.id.filter_learning_tracks);
    learningTracksButton.setOnClickListener(
        v -> {
          SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
          settings
              .edit()
              .putBoolean(LEARNING_TRACK_KEY, !settings.getBoolean(LEARNING_TRACK_KEY, false))
              .apply();
        });
    sheetMusicButton = view.findViewById(R.id.filter_sheet_music);
    sheetMusicButton.setOnClickListener(
        v -> {
          SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
          settings
              .edit()
              .putBoolean(SHEET_MUSIC_KEY, !settings.getBoolean(SHEET_MUSIC_KEY, false))
              .apply();
        });

    applyFilterStyle();
  }

  private void applyFilterStyle() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    boolean sheetMusicApplied = preferences.getBoolean(SHEET_MUSIC_KEY, false);
    boolean learningTrackApplied = preferences.getBoolean(LEARNING_TRACK_KEY, false);
    boolean ratingApplied;
    try {
      ratingApplied = Double.parseDouble(preferences.getString(RATING_KEY, "-1")) >= 0;
    } catch (NumberFormatException e) {
      ratingApplied = false;
    }
    boolean partApplied =
        !preferences
            .getString(PARTS_NUMBER_KEY, PartFilter.ANY.name())
            .equals(PartFilter.ANY.name());
    boolean typeApplied = !preferences.getString(TYPE_KEY, Type.ANY.name()).equals(Type.ANY.name());
    boolean keyApplied = !preferences.getString(KEY_KEY, Key.ANY.name()).equals(Key.ANY.name());

    setButtonStyle(sheetMusicButton, sheetMusicApplied, context);
    setButtonStyle(learningTracksButton, learningTrackApplied, context);
    setButtonStyle(ratingButton, ratingApplied, context);
    setButtonStyle(partsButton, partApplied, context);
    setButtonStyle(typeButton, typeApplied, context);
    setButtonStyle(keyButton, keyApplied, context);
  }

  private void setButtonStyle(Button button, boolean filterApplied, Context context) {
    if (filterApplied) {
      button.setBackgroundResource(R.drawable.rounded_button_filled);
      button.setTextColor(context.getResources().getColor(R.color.card_view_background));
    } else {
      button.setBackgroundResource(R.drawable.rounded_button);
      button.setTextColor(context.getResources().getColor(R.color.darkSecondaryTitle));
    }
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.startsWith("filter_")) {
      applyFilterStyle();
    }
  }
}
