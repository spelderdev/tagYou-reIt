package com.spelder.tagyourit.ui.settings.filter;

import android.os.Bundle;
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
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import com.spelder.tagyourit.networking.api.filter.Part;

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
    FilterBuilder filterBuilder = new FilterBuilder(getContext());

    RadioGroup filterPartsGroup = view.findViewById(R.id.filter_parts_radio);

    Button apply = view.findViewById(R.id.filter_parts_apply_button);
    apply.setOnClickListener(
        v -> {
          Part partFilter;
          switch (filterPartsGroup.getCheckedRadioButtonId()) {
            case R.id.filter_parts_four:
              partFilter = Part.FOUR;
              break;
            case R.id.filter_parts_five:
              partFilter = Part.FIVE;
              break;
            case R.id.filter_parts_six:
              partFilter = Part.SIX;
              break;
            case R.id.filter_parts_seven:
              partFilter = Part.SEVEN;
              break;
            case R.id.filter_parts_eight:
              partFilter = Part.EIGHT;
              break;
            case R.id.filter_parts_any:
            default:
              partFilter = Part.ANY;
          }
          filterBuilder.setPart(partFilter);
          dismiss();
        });

    Part currentSortBy = filterBuilder.getPart();
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
