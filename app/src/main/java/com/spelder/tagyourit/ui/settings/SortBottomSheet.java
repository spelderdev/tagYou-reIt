package com.spelder.tagyourit.ui.settings;

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
import com.spelder.tagyourit.networking.api.SortBuilder;
import com.spelder.tagyourit.networking.api.SortBy;

public class SortBottomSheet extends BottomSheetDialogFragment {
  private String id = "";

  public SortBottomSheet() {}

  public SortBottomSheet(String id) {
    this.id = id;
  }

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
    SortBuilder builder = new SortBuilder(view.getContext(), id);

    RadioGroup sortByGroup = view.findViewById(R.id.sort_by_radio);

    SortBy currentSortBy = builder.build();
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

    sortByGroup.setOnCheckedChangeListener((radioGroup, id) -> apply(builder, id));
  }

  private void apply(SortBuilder builder, int selectedId) {
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
    builder.setSortBy(sortBy);
    dismiss();
  }
}
