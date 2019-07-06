package com.spelder.tagyourit.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.networking.RateTag;
import com.spelder.tagyourit.ui.FragmentSwitcher;

/** Dialog box for rating tags. */
public class RateTagDialog extends DialogFragment {
  @Override
  @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Bundle bundle = getArguments();
    final Tag tag = bundle.getParcelable(FragmentSwitcher.TAG_KEY);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    View view = View.inflate(getContext(), R.layout.rating_dialog, null);
    final RatingBar rating = view.findViewById(R.id.dialog_rating_bar);

    final TagDb db = new TagDb(getActivity());
    float rating_num = (float) db.getUserRating(tag.getId());
    if (rating_num >= 0) {
      rating.setRating(rating_num);
    }

    builder.setTitle("Rate " + tag.getTitle());
    builder
        .setView(view)
        .setPositiveButton(
            "Rate",
            (DialogInterface dialog, int id) -> {
              db.insertUserRating(tag.getId(), rating.getRating());
              RateTag.rateTag(tag.getId(), rating.getRating());
            })
        .setNegativeButton(
            "Cancel", (DialogInterface dialog, int id) -> RateTagDialog.this.getDialog().cancel());
    return builder.create();
  }
}
