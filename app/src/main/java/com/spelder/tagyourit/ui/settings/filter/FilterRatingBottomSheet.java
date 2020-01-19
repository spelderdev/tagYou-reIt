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
import com.spelder.tagyourit.networking.api.filter.Rating;

public class FilterRatingBottomSheet extends BottomSheetDialogFragment {
  private String id = "";

  public FilterRatingBottomSheet() {}

  public FilterRatingBottomSheet(String id) {
    this.id = id;
  }

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
    FilterBuilder filterBuilder = new FilterBuilder(getContext(), id);

    RadioGroup filterPartsGroup = view.findViewById(R.id.filter_rating_radio);

    Rating currentRating = filterBuilder.getRating();
    RadioButton currentRadioButton;
    switch (currentRating) {
      case ZERO:
        currentRadioButton = view.findViewById(R.id.filter_rating_0);
        break;
      case ZERO_5:
        currentRadioButton = view.findViewById(R.id.filter_rating_0_5);
        break;
      case ONE:
        currentRadioButton = view.findViewById(R.id.filter_rating_1);
        break;
      case ONE_5:
        currentRadioButton = view.findViewById(R.id.filter_rating_1_5);
        break;
      case TWO:
        currentRadioButton = view.findViewById(R.id.filter_rating_2);
        break;
      case TWO_5:
        currentRadioButton = view.findViewById(R.id.filter_rating_2_5);
        break;
      case THREE:
        currentRadioButton = view.findViewById(R.id.filter_rating_3);
        break;
      case THREE_5:
        currentRadioButton = view.findViewById(R.id.filter_rating_3_5);
        break;
      case FOUR:
        currentRadioButton = view.findViewById(R.id.filter_rating_4);
        break;
      case FOUR_5:
        currentRadioButton = view.findViewById(R.id.filter_rating_4_5);
        break;
      case ANY:
      default:
        currentRadioButton = view.findViewById(R.id.filter_rating_any);
    }
    currentRadioButton.setChecked(true);

    filterPartsGroup.setOnCheckedChangeListener((radioGroup, id) -> apply(filterBuilder, id));
  }

  private void apply(FilterBuilder filterBuilder, int selectedId) {
    Rating ratingFilter;
    switch (selectedId) {
      case R.id.filter_rating_0:
        ratingFilter = Rating.ZERO;
        break;
      case R.id.filter_rating_0_5:
        ratingFilter = Rating.ZERO_5;
        break;
      case R.id.filter_rating_1:
        ratingFilter = Rating.ONE;
        break;
      case R.id.filter_rating_1_5:
        ratingFilter = Rating.ONE_5;
        break;
      case R.id.filter_rating_2:
        ratingFilter = Rating.TWO;
        break;
      case R.id.filter_rating_2_5:
        ratingFilter = Rating.TWO_5;
        break;
      case R.id.filter_rating_3:
        ratingFilter = Rating.THREE;
        break;
      case R.id.filter_rating_3_5:
        ratingFilter = Rating.THREE_5;
        break;
      case R.id.filter_rating_4:
        ratingFilter = Rating.FOUR;
        break;
      case R.id.filter_rating_4_5:
        ratingFilter = Rating.FOUR_5;
        break;
      case R.id.filter_parts_any:
      default:
        ratingFilter = Rating.ANY;
    }
    filterBuilder.setRating(ratingFilter);
    dismiss();
  }
}
