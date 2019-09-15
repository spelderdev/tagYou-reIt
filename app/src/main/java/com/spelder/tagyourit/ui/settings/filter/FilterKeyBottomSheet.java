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
import com.spelder.tagyourit.networking.api.filter.Key;

public class FilterKeyBottomSheet extends BottomSheetDialogFragment {
  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.filter_key, container, false);

    setupView(view);

    return view;
  }

  private void setupView(View view) {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());

    RadioGroup filterPartsGroup = view.findViewById(R.id.filter_key_radio);

    Button apply = view.findViewById(R.id.filter_key_apply_button);
    apply.setOnClickListener(
        v -> {
          Key keyFilter;
          switch (filterPartsGroup.getCheckedRadioButtonId()) {
            case R.id.filter_key_a:
              keyFilter = Key.A;
              break;
            case R.id.filter_key_ab:
              keyFilter = Key.A_FLAT;
              break;
            case R.id.filter_key_b:
              keyFilter = Key.B;
              break;
            case R.id.filter_key_bb:
              keyFilter = Key.B_FLAT;
              break;
            case R.id.filter_key_c:
              keyFilter = Key.C;
              break;
            case R.id.filter_key_d:
              keyFilter = Key.D;
              break;
            case R.id.filter_key_db:
              keyFilter = Key.D_FLAT;
              break;
            case R.id.filter_key_e:
              keyFilter = Key.E;
              break;
            case R.id.filter_key_eb:
              keyFilter = Key.E_FLAT;
              break;
            case R.id.filter_key_f:
              keyFilter = Key.F;
              break;
            case R.id.filter_key_g:
              keyFilter = Key.G;
              break;
            case R.id.filter_key_gb:
              keyFilter = Key.G_FLAT;
              break;
            case R.id.filter_key_any:
            default:
              keyFilter = Key.ANY;
          }
          settings.edit().putString(getString(R.string.filter_key_key), keyFilter.name()).apply();
          dismiss();
        });

    Key currentSortBy;
    try {
      currentSortBy =
          Key.valueOf(settings.getString(getString(R.string.filter_key_key), Key.ANY.name()));
    } catch (IllegalArgumentException e) {
      currentSortBy = Key.ANY;
    }
    RadioButton currentRadioButton;
    switch (currentSortBy) {
      case A:
        currentRadioButton = view.findViewById(R.id.filter_key_a);
        break;
      case A_FLAT:
        currentRadioButton = view.findViewById(R.id.filter_key_ab);
        break;
      case B:
        currentRadioButton = view.findViewById(R.id.filter_key_b);
        break;
      case B_FLAT:
        currentRadioButton = view.findViewById(R.id.filter_key_bb);
        break;
      case C:
        currentRadioButton = view.findViewById(R.id.filter_key_c);
        break;
      case D:
        currentRadioButton = view.findViewById(R.id.filter_key_d);
        break;
      case D_FLAT:
        currentRadioButton = view.findViewById(R.id.filter_key_db);
        break;
      case E:
        currentRadioButton = view.findViewById(R.id.filter_key_e);
        break;
      case E_FLAT:
        currentRadioButton = view.findViewById(R.id.filter_key_eb);
        break;
      case F:
        currentRadioButton = view.findViewById(R.id.filter_key_f);
        break;
      case G:
        currentRadioButton = view.findViewById(R.id.filter_key_g);
        break;
      case G_FLAT:
        currentRadioButton = view.findViewById(R.id.filter_key_gb);
        break;
      case ANY:
      default:
        currentRadioButton = view.findViewById(R.id.filter_key_any);
    }
    currentRadioButton.setChecked(true);
  }
}
