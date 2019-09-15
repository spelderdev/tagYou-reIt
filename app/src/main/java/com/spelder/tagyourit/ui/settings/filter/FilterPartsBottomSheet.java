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
import com.spelder.tagyourit.networking.api.filter.PartFilter;

public class FilterPartsBottomSheet extends BottomSheetDialogFragment {
  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.filter_parts, container, false);

    setupView(view);

    return view;
  }

  private void setupView(View view) {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());

    RadioGroup filterPartsGroup = view.findViewById(R.id.filter_parts_radio);

    Button apply = view.findViewById(R.id.filter_parts_apply_button);
    apply.setOnClickListener(
        v -> {
          PartFilter partFilter;
          switch (filterPartsGroup.getCheckedRadioButtonId()) {
            case R.id.filter_parts_four:
              partFilter = PartFilter.FOUR;
              break;
            case R.id.filter_parts_five:
              partFilter = PartFilter.FIVE;
              break;
            case R.id.filter_parts_six:
              partFilter = PartFilter.SIX;
              break;
            case R.id.filter_parts_seven:
              partFilter = PartFilter.SEVEN;
              break;
            case R.id.filter_parts_eight:
              partFilter = PartFilter.EIGHT;
              break;
            case R.id.filter_parts_any:
            default:
              partFilter = PartFilter.ANY;
          }
          settings.edit().putString(getString(R.string.filter_part_key), partFilter.name()).apply();
          dismiss();
        });

    PartFilter currentSortBy;
    try {
      currentSortBy =
          PartFilter.valueOf(
              settings.getString(getString(R.string.filter_part_key), PartFilter.ANY.name()));
    } catch (IllegalArgumentException e) {
      currentSortBy = PartFilter.ANY;
    }
    RadioButton currentRadioButton;
    switch (currentSortBy) {
      case FOUR:
        currentRadioButton = view.findViewById(R.id.filter_parts_four);
        break;
      case FIVE:
        currentRadioButton = view.findViewById(R.id.filter_parts_five);
        break;
      case SIX:
        currentRadioButton = view.findViewById(R.id.filter_parts_six);
        break;
      case SEVEN:
        currentRadioButton = view.findViewById(R.id.filter_parts_seven);
        break;
      case EIGHT:
        currentRadioButton = view.findViewById(R.id.filter_parts_eight);
        break;
      case ANY:
      default:
        currentRadioButton = view.findViewById(R.id.filter_parts_any);
    }
    currentRadioButton.setChecked(true);
  }
}
