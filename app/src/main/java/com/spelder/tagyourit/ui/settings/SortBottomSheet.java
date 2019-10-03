package com.spelder.tagyourit.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.networking.api.SortBy;

public class SortBottomSheet extends BottomSheetDialogFragment {
  public static final String SORT_BY_LABEL = "SORT_BY";

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.sort, container, false);

    setupView(view);

    return view;
  }

  private void setupView(View view) {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());

    RadioGroup sortByGroup = view.findViewById(R.id.sort_by_radio);

    SortBy currentSortBy = SortBy.valueOf(settings.getString(SORT_BY_LABEL, SortBy.TITLE.name()));
    RadioButton currentRadioButton;
    switch (currentSortBy) {
      case DOWNLOAD:
        currentRadioButton = view.findViewById(R.id.sort_downloads);
        break;
      case LATEST:
        currentRadioButton = view.findViewById(R.id.sort_newest_posted);
        break;
      case RATING:
        currentRadioButton = view.findViewById(R.id.sort_rating);
        break;
      case TITLE:
      default:
        currentRadioButton = view.findViewById(R.id.sort_title);
    }
    currentRadioButton.setChecked(true);

    sortByGroup.setOnCheckedChangeListener((radioGroup, id) -> apply(settings, id));
  }

  private void apply(SharedPreferences settings, int selectedId) {
    SortBy sortBy;
    switch (selectedId) {
      case R.id.sort_downloads:
        sortBy = SortBy.DOWNLOAD;
        break;
      case R.id.sort_newest_posted:
        sortBy = SortBy.LATEST;
        break;
      case R.id.sort_rating:
        sortBy = SortBy.RATING;
        break;
      case R.id.sort_title:
      default:
        sortBy = SortBy.TITLE;
    }
    settings.edit().putString(SORT_BY_LABEL, sortBy.name()).apply();
    dismiss();
  }
}
