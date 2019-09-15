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
import com.spelder.tagyourit.networking.api.filter.Type;

public class FilterTypeBottomSheet extends BottomSheetDialogFragment {
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
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());

    RadioGroup filterPartsGroup = view.findViewById(R.id.filter_type_radio);

    Button apply = view.findViewById(R.id.filter_type_apply_button);
    apply.setOnClickListener(
        v -> {
          Type typeFilter;
          switch (filterPartsGroup.getCheckedRadioButtonId()) {
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
          settings.edit().putString(getString(R.string.filter_type_key), typeFilter.name()).apply();
          dismiss();
        });

    Type currentSortBy;
    try {
      currentSortBy =
          Type.valueOf(settings.getString(getString(R.string.filter_type_key), Type.ANY.name()));
    } catch (IllegalArgumentException e) {
      currentSortBy = Type.ANY;
    }
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
  }
}
