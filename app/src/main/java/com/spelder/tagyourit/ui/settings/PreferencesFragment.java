package com.spelder.tagyourit.ui.settings;

import android.app.Activity;
import android.os.Bundle;
import androidx.preference.Preference;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.drive.FavoritesBackup;
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

    String accountName = back.getSignedInEmail();

    Preference accountPref = findPreference("pref_key_account");
    if (accountName != null) {
      accountPref.setTitle("Sign out");
      accountPref.setSummary(accountName);
    } else {
      accountPref.setTitle("Sign in");
      accountPref.setSummary("");
    }
    accountPref.setOnPreferenceClickListener(
        preference -> {
          if (back.getSignedInEmail() != null) {
            back.signOut(
                () -> {
                  accountPref.setTitle("Sign in");
                  accountPref.setSummary("");
                });
          } else {
            back.signInCustomAction(
                () -> {
                  accountPref.setTitle("Sign out");
                  accountPref.setSummary(back.getSignedInEmail());
                });
          }
          return true;
        });

    Preference backupPref = findPreference("pref_key_backup");
    backupPref.setOnPreferenceClickListener(
        preference -> {
          back.backup(
              () -> {
                accountPref.setTitle("Sign out");
                accountPref.setSummary(back.getSignedInEmail());
              });
          return true;
        });

    Preference restorePref = findPreference("pref_key_restore");
    restorePref.setOnPreferenceClickListener(
        preference -> {
          back.restore(
              () -> {
                accountPref.setTitle("Sign out");
                accountPref.setSummary(back.getSignedInEmail());
              });
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
