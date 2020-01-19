package com.spelder.tagyourit.drive;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDbHelper;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Backs up and restores the tag database to the user's Google Drive account. */
public class DatabaseBackup {
  public static final int REQUEST_CODE_SIGN_IN = 0;

  private static final String TAG = DatabaseBackup.class.getName();

  private static final String FILE_NAME = TagDbHelper.DATABASE_NAME;

  private static String dbFilePath;
  private final Activity activity;
  private DriveServiceHelper mDriveServiceHelper;
  private Action action;
  private SignInResult signInResult;

  private enum Action {
    BACKUP,
    RESTORE,
    SIGN_IN
  }

  public DatabaseBackup(Activity activity) {
    dbFilePath = activity.getDatabasePath(TagDbHelper.DATABASE_NAME).getAbsolutePath();
    this.activity = activity;
  }

  private static void showMessage(Activity activity, String message) {
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
  }

  /** Starts the sign-in process and initializes the Drive client. */
  private void signIn() {
    Set<Scope> requiredScopes = new HashSet<>(1);
    requiredScopes.add(new Scope(DriveScopes.DRIVE_FILE));
    GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
    if (signInAccount != null
        && signInAccount.getAccount() != null
        && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
      initializeDriveClient(signInAccount);
    } else {
      GoogleSignInOptions signInOptions =
          new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestEmail()
              .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
              .build();
      GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity, signInOptions);
      activity.startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }
  }

  public String getSignedInEmail() {
    GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
    if (signInAccount != null && signInAccount.getAccount() != null) {
      return signInAccount.getEmail();
    }

    return null;
  }

  public void signOut(SignInResult result) {
    GoogleSignInOptions signInOptions =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
            .build();
    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity, signInOptions);
    googleSignInClient
        .signOut()
        .addOnCompleteListener(
            activity,
            task -> {
              showMessage(activity, "Sign out successful");
              result.finished();
            });
  }

  /**
   * Continues the sign-in process, initializing the Drive clients with the current user's account.
   */
  public void initializeDriveClient(GoogleSignInAccount signInAccount) {
    Log.d(TAG, "Signed in as " + signInAccount.getEmail());

    // Use the authenticated account to sign in to the Drive service.
    GoogleAccountCredential credential =
        GoogleAccountCredential.usingOAuth2(
            activity, Collections.singleton(DriveScopes.DRIVE_FILE));
    credential.setSelectedAccount(signInAccount.getAccount());
    Drive googleDriveService =
        new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
            .setApplicationName(activity.getResources().getString(R.string.app_name))
            .build();

    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
    // Its instantiation is required before handling any onClick actions.
    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);

    performAction();
  }

  private void performAction() {
    switch (action) {
      case BACKUP:
        backupAction();
        break;
      case RESTORE:
        restoreAction();
        break;
      case SIGN_IN:
        showMessage(activity, "Sign in successful");
        signInResult.finished();
        break;
    }
  }

  private void backupAction() {
    mDriveServiceHelper
        .queryFiles(FILE_NAME)
        .addOnCompleteListener(
            task -> {
              Log.d(TAG, "Backing up file from " + dbFilePath);
              TagDbHelper.clearInstance();
              File dbFile = new File(dbFilePath);
              Task<com.google.api.services.drive.model.File> fileTask;
              String f = task.getResult();
              if (task.getResult() != null && task.getResult().isEmpty()) {
                fileTask = mDriveServiceHelper.createFile(FILE_NAME, dbFile);
              } else {
                fileTask = mDriveServiceHelper.updateFile(task.getResult(), FILE_NAME, dbFile);
              }

              checkTaskComplete(fileTask);
            })
        .addOnFailureListener(
            exception -> {
              Log.e(TAG, "Error while backing up contents", exception);
              showMessage(activity, "Error while backing up contents");
            });
  }

  private void checkTaskComplete(Task<?> task) {
    task.addOnCompleteListener(
            checkTask -> {
              if (checkTask.isSuccessful()) {
                switch (action) {
                  case BACKUP:
                    signInResult.finished();
                    showMessage(activity, "Successfully backed up contents");
                    break;
                  case RESTORE:
                    signInResult.finished();
                    showMessage(activity, "Successfully restored contents");
                    break;
                }
              }
            })
        .addOnFailureListener(
            exception -> {
              switch (action) {
                case BACKUP:
                  Log.e(TAG, "Error while backing up contents", exception);
                  showMessage(activity, "Error while backing up contents");
                  break;
                case RESTORE:
                  Log.e(TAG, "Error while restoring contents", exception);
                  showMessage(activity, "Error while restoring contents");
                  break;
              }
            });
  }

  private void restoreAction() {

    mDriveServiceHelper
        .queryFiles(FILE_NAME)
        .addOnCompleteListener(
            task -> {
              Log.d(TAG, "Restoring file to " + dbFilePath);
              TagDbHelper.clearInstance();
              File dbFile = new File(dbFilePath);
              if (task.getResult() != null) {
                Task<?> fileTask = mDriveServiceHelper.downloadFile(task.getResult(), dbFile);

                checkTaskComplete(fileTask);
              } else {
                Log.d(TAG, "File not found");
              }
            })
        .addOnFailureListener(task -> showMessage(activity, "Error restoring..."));
  }

  public void signInCustomAction(SignInResult result) {
    signInResult = result;
    action = Action.SIGN_IN;
    signIn();
  }

  public void backup(SignInResult result) {
    signInResult = result;
    action = Action.BACKUP;
    signIn();
  }

  public void restore(SignInResult result) {
    signInResult = result;
    action = Action.RESTORE;
    signIn();
  }
}
