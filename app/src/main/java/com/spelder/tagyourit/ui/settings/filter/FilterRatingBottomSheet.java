package com.spelder.tagyourit.ui.settings.filter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.spelder.tagyourit.R;

public class FilterRatingBottomSheet extends BottomSheetDialogFragment {
  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.filter_rating, container, false);

    setupView(view);

    return view;
  }

  private void setupView(View view) {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());

    RadioGroup filterPartsGroup = view.findViewById(R.id.filter_rating_radio);

    Button apply = view.findViewById(R.id.filter_rating_apply_button);
    apply.setOnClickListener(
        v -> {
          double ratingFilter;
          switch (filterPartsGroup.getCheckedRadioButtonId()) {
            case R.id.filter_rating_0:
              ratingFilter = 0;
              break;
            case R.id.filter_rating_0_5:
              ratingFilter = 0.5;
              break;
            case R.id.filter_rating_1:
              ratingFilter = 1;
              break;
            case R.id.filter_rating_1_5:
              ratingFilter = 1.5;
              break;
            case R.id.filter_rating_2:
              ratingFilter = 2;
              break;
            case R.id.filter_rating_2_5:
              ratingFilter = 2.5;
              break;
            case R.id.filter_rating_3:
              ratingFilter = 3;
              break;
            case R.id.filter_rating_3_5:
              ratingFilter = 3.5;
              break;
            case R.id.filter_rating_4:
              ratingFilter = 4;
              break;
            case R.id.filter_rating_4_5:
              ratingFilter = 4.5;
              break;
            case R.id.filter_parts_any:
            default:
              ratingFilter = -1;
          }
          settings
              .edit()
              .putString(getString(R.string.filter_rating_key), "" + ratingFilter)
              .apply();
          dismiss();
        });

    double currentSortBy;
    try {
      currentSortBy =
          Double.parseDouble(settings.getString(getString(R.string.filter_rating_key), "-1"));
    } catch (IllegalArgumentException e) {
      currentSortBy = -1;
    }
    RadioButton currentRadioButton;
    if (currentSortBy < 0) {
      currentRadioButton = view.findViewById(R.id.filter_rating_any);
    } else if (currentSortBy < 0.5) {
      currentRadioButton = view.findViewById(R.id.filter_rating_0);
    } else if (currentSortBy < 1) {
      currentRadioButton = view.findViewById(R.id.filter_rating_0_5);
    } else if (currentSortBy < 1.5) {
      currentRadioButton = view.findViewById(R.id.filter_rating_1);
    } else if (currentSortBy < 2) {
      currentRadioButton = view.findViewById(R.id.filter_rating_1_5);
    } else if (currentSortBy < 2.5) {
      currentRadioButton = view.findViewById(R.id.filter_rating_2);
    } else if (currentSortBy < 3) {
      currentRadioButton = view.findViewById(R.id.filter_rating_2_5);
    } else if (currentSortBy < 3.5) {
      currentRadioButton = view.findViewById(R.id.filter_rating_3);
    } else if (currentSortBy < 4) {
      currentRadioButton = view.findViewById(R.id.filter_rating_3_5);
    } else if (currentSortBy < 4.5) {
      currentRadioButton = view.findViewById(R.id.filter_rating_4);
    } else if (currentSortBy < 5) {
      currentRadioButton = view.findViewById(R.id.filter_rating_4_5);
    } else {
      currentRadioButton = view.findViewById(R.id.filter_rating_any);
    }
    currentRadioButton.setChecked(true);
  }
}
