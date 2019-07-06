package com.spelder.tagyourit.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import com.spelder.tagyourit.R;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

/**
 * Filter side menu used to set the filters for the view. Uses preferences and an additional button.
 */
public class FilterFragment extends PreferenceFragmentCompat {
  private static final String TAG = FilterFragment.class.getName();

  @Override
  public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.filter_drawer_preferences);

    if (getActivity() == null) {
      return;
    }

    Button resetFilterButton = getActivity().findViewById(R.id.filter_reset);
    resetFilterButton.setOnClickListener(
        view -> {
          Log.d(TAG, "Reset Clicked");

          SharedPreferences.Editor preferencesEditor =
              getPreferenceManager().getSharedPreferences().edit();
          preferencesEditor.remove(getString(R.string.filter_part_key));
          preferencesEditor.remove(getString(R.string.filter_sheet_music_key));
          preferencesEditor.remove(getString(R.string.filter_learning_track_key));
          preferencesEditor.remove(getString(R.string.filter_key_key));
          preferencesEditor.remove(getString(R.string.filter_rating_key));
          preferencesEditor.remove(getString(R.string.filter_type_key));
          preferencesEditor.apply();

          setPreferenceScreen(null);
          addPreferencesFromResource(R.xml.filter_drawer_preferences);
        });
  }
}
