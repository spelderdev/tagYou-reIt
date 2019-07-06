package com.spelder.tagyourit.ui.settings;

import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.drive.FavoritesBackup;
import com.spelder.tagyourit.ui.FragmentSwitcher;
import com.spelder.tagyourit.ui.MainActivity;
import com.spelder.tagyourit.ui.dialog.DownloadFavoritesDialog;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

/** Edits preferences for the system. */
public class PreferencesFragment extends PreferenceFragmentCompat {
  private FavoritesBackup back;

  @Override
  public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.preferences);

    back = new FavoritesBackup(getActivity());

    Preference backupPref = findPreference("pref_key_backup");
    backupPref.setOnPreferenceClickListener(
        preference -> {
          back.backup();
          return true;
        });

    Preference restorePref = findPreference("pref_key_restore");
    restorePref.setOnPreferenceClickListener(
        preference -> {
          back.restore();
          return true;
        });

    SwitchPreference download = (SwitchPreference) findPreference("pref_favorites_save");
    download.setOnPreferenceChangeListener(
        (preference, newValue) -> {
          if (newValue instanceof Boolean) {
            DownloadFavoritesDialog dialog = new DownloadFavoritesDialog();

            Boolean boolVal = (Boolean) newValue;

            Bundle bundle = new Bundle();
            bundle.putBoolean(FragmentSwitcher.DOWNLOAD_KEY, boolVal);
            dialog.setArguments(bundle);
            dialog.show(getActivity().getSupportFragmentManager(), "download_favorites");
          }
          return true;
        });

    Preference privacyPref = findPreference("pref_key_privacy");
    privacyPref.setOnPreferenceClickListener(
        preference -> {
          ((MainActivity) getActivity()).getManager().displayPrivacyPolicy();
          return true;
        });
  }

  public void initializeDriveClient(GoogleSignInAccount signInAccount) {
    if (back != null) {
      back.initializeDriveClient(signInAccount);
    }
  }
}
