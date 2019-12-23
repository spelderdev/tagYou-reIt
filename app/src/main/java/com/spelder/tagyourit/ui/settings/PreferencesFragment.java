package com.spelder.tagyourit.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.drive.FavoritesBackup;
import com.spelder.tagyourit.networking.DownloadFavoritesService;
import com.spelder.tagyourit.networking.RemoveFavoritesDownloadService;
import com.spelder.tagyourit.ui.MainActivity;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

/** Edits preferences for the system. */
public class PreferencesFragment extends PreferenceFragmentCompat {
  private FavoritesBackup back;

  @Override
  public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.preferences);

    Activity activity = getActivity();
    if (activity == null) {
      return;
    }
    back = new FavoritesBackup(activity);

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
            if ((Boolean) newValue) {
              RemoveFavoritesDownloadService.cancel();
              Intent intent = new Intent(getActivity(), DownloadFavoritesService.class);
              getActivity().startService(intent);
            } else {
              DownloadFavoritesService.cancel();
              Intent intent = new Intent(getActivity(), RemoveFavoritesDownloadService.class);
              getActivity().startService(intent);
            }
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
