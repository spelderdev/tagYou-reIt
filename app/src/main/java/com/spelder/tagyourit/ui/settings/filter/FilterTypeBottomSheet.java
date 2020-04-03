package com.spelder.tagyourit.ui.settings.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import com.spelder.tagyourit.networking.api.filter.Type;

public class FilterTypeBottomSheet extends BottomSheetDialogFragment {
  private String id = "";

  public FilterTypeBottomSheet() {}

  FilterTypeBottomSheet(String id) {
    this.id = id;
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.filter_type, container, false);

    setupView(view);

    return view;
  }

  private void setupView(View view) {
    FilterBuilder filterBuilder = new FilterBuilder(getContext(), id);

    RadioGroup filterPartsGroup = view.findViewById(R.id.filter_type_radio);

    Type currentSortBy = filterBuilder.getType();
    RadioButton currentRadioButton;
    switch (currentSortBy) {
      case BARBERSHOP:
        currentRadioButton = view.findViewById(R.id.filter_type_barbershop);
        break;
      case SWEET_ADELINES:
        currentRadioButton = view.findViewById(R.id.filter_type_sweet_adelines);
        break;
      case SATB:
        currentRadioButton = view.findViewById(R.id.filter_type_satb);
        break;
      case OTHER_MALE:
        currentRadioButton = view.findViewById(R.id.filter_type_other_male);
        break;
      case OTHER_FEMALE:
        currentRadioButton = view.findViewById(R.id.filter_type_other_female);
        break;
      case OTHER_MIXED:
        currentRadioButton = view.findViewById(R.id.filter_type_other_mixed);
        break;
      case ANY:
      default:
        currentRadioButton = view.findViewById(R.id.filter_type_any);
    }
    currentRadioButton.setChecked(true);

    filterPartsGroup.setOnCheckedChangeListener((radioGroup, id) -> apply(filterBuilder, id));
  }

  private void apply(FilterBuilder filterBuilder, int selectedId) {
    Type typeFilter;
    switch (selectedId) {
      case R.id.filter_type_barbershop:
        typeFilter = Type.BARBERSHOP;
        break;
      case R.id.filter_type_sweet_adelines:
        typeFilter = Type.SWEET_ADELINES;
        break;
      case R.id.filter_type_satb:
        typeFilter = Type.SATB;
        break;
      case R.id.filter_type_other_male:
        typeFilter = Type.OTHER_MALE;
        break;
      case R.id.filter_type_other_female:
        typeFilter = Type.OTHER_FEMALE;
        break;
      case R.id.filter_type_other_mixed:
        typeFilter = Type.OTHER_MIXED;
        break;
      case R.id.filter_parts_any:
      default:
        typeFilter = Type.ANY;
    }
    filterBuilder.setType(typeFilter);
    dismiss();
  }
}
