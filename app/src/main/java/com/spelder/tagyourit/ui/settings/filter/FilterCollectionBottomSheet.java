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
import com.spelder.tagyourit.networking.api.filter.Collection;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;

public class FilterCollectionBottomSheet extends BottomSheetDialogFragment {
  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.filter_collection, container, false);

    setupView(view);

    return view;
  }

  private void setupView(View view) {
    FilterBuilder filterBuilder = new FilterBuilder(getContext());

    RadioGroup filterCollectionGroup = view.findViewById(R.id.filter_collection_radio);

    Collection currentSortBy = filterBuilder.getCollection();
    RadioButton currentRadioButton;
    switch (currentSortBy) {
      case CLASSIC:
        currentRadioButton = view.findViewById(R.id.filter_collection_classic);
        break;
      case EASY:
        currentRadioButton = view.findViewById(R.id.filter_collection_easy);
        break;
      case ONE_HUNDRED:
        currentRadioButton = view.findViewById(R.id.filter_collection_100);
        break;
      case ANY:
      default:
        currentRadioButton = view.findViewById(R.id.filter_collection_any);
    }
    currentRadioButton.setChecked(true);

    filterCollectionGroup.setOnCheckedChangeListener((radioGroup, id) -> apply(filterBuilder, id));
  }

  private void apply(FilterBuilder filterBuilder, int selectedId) {
    Collection collectionFilter;
    switch (selectedId) {
      case R.id.filter_collection_classic:
        collectionFilter = Collection.CLASSIC;
        break;
      case R.id.filter_collection_easy:
        collectionFilter = Collection.EASY;
        break;
      case R.id.filter_collection_100:
        collectionFilter = Collection.ONE_HUNDRED;
        break;
      case R.id.filter_parts_any:
      default:
        collectionFilter = Collection.ANY;
    }
    filterBuilder.setCollection(collectionFilter);
    dismiss();
  }
}
