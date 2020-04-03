package com.spelder.tagyourit.ui.settings.filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.FragmentManager;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import com.spelder.tagyourit.networking.api.filter.FilterBy;

public class FilterBar implements SharedPreferences.OnSharedPreferenceChangeListener {
  private Button partsButton;
  private Button keyButton;
  private Button typeButton;
  private Button ratingButton;
  private Button collectionButton;
  private Button learningTracksButton;
  private Button sheetMusicButton;
  private Context context;
  private final FilterBuilder filterBuilder;
  private String id = "";

  public FilterBar(Context context) {
    filterBuilder = new FilterBuilder(context);
  }

  public FilterBar(Context context, long listId) {
    id = Long.toString(listId);
    filterBuilder = new FilterBuilder(context, id);
  }

  public void setupFilterBar(View view, FragmentManager fragmentManager) {
    context = view.getContext();

    partsButton = view.findViewById(R.id.filter_parts);
    partsButton.setOnClickListener(
        v -> {
          FilterPartsBottomSheet bottomSheet = new FilterPartsBottomSheet(id);
          bottomSheet.show(fragmentManager, "Filter Parts Bottom Sheet");
        });

    keyButton = view.findViewById(R.id.filter_key);
    keyButton.setOnClickListener(
        v -> {
          FilterKeyBottomSheet bottomSheet = new FilterKeyBottomSheet(id);
          bottomSheet.show(fragmentManager, "Filter Key Bottom Sheet");
        });

    typeButton = view.findViewById(R.id.filter_type);
    typeButton.setOnClickListener(
        v -> {
          FilterTypeBottomSheet bottomSheet = new FilterTypeBottomSheet(id);
          bottomSheet.show(fragmentManager, "Filter Parts Bottom Sheet");
        });

    ratingButton = view.findViewById(R.id.filter_rating);
    ratingButton.setOnClickListener(
        v -> {
          FilterRatingBottomSheet bottomSheet = new FilterRatingBottomSheet(id);
          bottomSheet.show(fragmentManager, "Filter Parts Bottom Sheet");
        });

    collectionButton = view.findViewById(R.id.filter_collection);
    collectionButton.setOnClickListener(
        v -> {
          FilterCollectionBottomSheet bottomSheet = new FilterCollectionBottomSheet(id);
          bottomSheet.show(fragmentManager, "Filter Collection Bottom Sheet");
        });

    learningTracksButton = view.findViewById(R.id.filter_learning_tracks);
    learningTracksButton.setOnClickListener(
        v -> filterBuilder.setLearningTrack(!filterBuilder.getLearningTrack()));

    sheetMusicButton = view.findViewById(R.id.filter_sheet_music);
    sheetMusicButton.setOnClickListener(
        v -> filterBuilder.setSheetMusic(!filterBuilder.getSheetMusic()));

    applyFilterStyle();
  }

  private void applyFilterStyle() {
    FilterBy filter = filterBuilder.build();

    setButtonStyle(sheetMusicButton, filter.isSheetMusicApplied(), context);
    setButtonStyle(learningTracksButton, filter.isLearningTrackApplied(), context);
    setButtonStyle(ratingButton, filter.isMinimumRatingApplied(), context);
    setButtonStyle(partsButton, filter.isNumberOfPartsApplied(), context);
    setButtonStyle(typeButton, filter.isTypeApplied(), context);
    setButtonStyle(keyButton, filter.isKeyApplied(), context);
    setButtonStyle(collectionButton, filter.isCollectionApplied(), context);

    ratingButton.setText(filter.getMinimumRatingDisplayName());
    partsButton.setText(filter.getNumberOfPartsDisplayName());
    typeButton.setText(filter.getTypeDisplayName());
    keyButton.setText(filter.getKeyDisplayName());
    collectionButton.setText(filter.getCollectionDisplayName());
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
