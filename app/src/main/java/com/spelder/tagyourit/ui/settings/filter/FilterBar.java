package com.spelder.tagyourit.ui.settings.filter;

import android.view.View;
import android.widget.Button;
import androidx.fragment.app.FragmentManager;
import com.spelder.tagyourit.R;

public class FilterBar {
  public static void setupFilterBar(View view, FragmentManager fragmentManager) {
    Button partsButton = view.findViewById(R.id.filter_parts);
    partsButton.setOnClickListener(
        v -> {
          FilterPartsBottomSheet bottomSheet = new FilterPartsBottomSheet();
          bottomSheet.show(fragmentManager, "Filter Parts Bottom Sheet");
        });
    Button keyButton = view.findViewById(R.id.filter_key);
    keyButton.setOnClickListener(
        v -> {
          FilterKeyBottomSheet bottomSheet = new FilterKeyBottomSheet();
          bottomSheet.show(fragmentManager, "Filter Key Bottom Sheet");
        });
    Button typeButton = view.findViewById(R.id.filter_type);
    typeButton.setOnClickListener(
        v -> {
          FilterTypeBottomSheet bottomSheet = new FilterTypeBottomSheet();
          bottomSheet.show(fragmentManager, "Filter Parts Bottom Sheet");
        });
    Button ratingButton = view.findViewById(R.id.filter_rating);
    ratingButton.setOnClickListener(
        v -> {
          FilterRatingBottomSheet bottomSheet = new FilterRatingBottomSheet();
          bottomSheet.show(fragmentManager, "Filter Parts Bottom Sheet");
        });
  }
}
