package com.spelder.tagyourit.drive;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.spelder.tagyourit.db.TagDbHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

/** Backs up and restores the tag database to the user's Google Drive account. */
@SuppressWarnings("WeakerAccess")
public class FavoritesBackup {
  public static final int REQUEST_CODE_SIGN_IN = 0;

  private static final String TAG = FavoritesBackup.class.getName();

  private static final String FILE_NAME = TagDbHelper.DATABASE_NAME;

  private static String dbFilePath;
  private final Activity activity;
  private DriveResourceClient mDriveResourceClient;
  private Action action;

  private final OnSuccessListener<MetadataBuffer> successListener =
      new OnSuccessListener<MetadataBuffer>() {
        @Override
        public void onSuccess(MetadataBuffer metadata) {
          switch (action) {
            case BACKUP:
              new BackupAsyncTask(mDriveResourceClient, FavoritesBackup.this).execute(metadata);
              break;
            case RESTORE:
              new RestoreAsyncTask(mDriveResourceClient, FavoritesBackup.this).execute(metadata);
              break;
          }
        }
      };

  public FavoritesBackup(Activity activity) {
    dbFilePath = activity.getDatabasePath(TagDbHelper.DATABASE_NAME).getAbsolutePath();
    this.activity = activity;
  }

  private static void showMessage(Activity activity, String message) {
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
  }

  /** Starts the sign-in process and initializes the Drive client. */
  private void signIn() {
    Set<Scope> requiredScopes = new HashSet<>(2);
    requiredScopes.add(Drive.SCOPE_FILE);
    requiredScopes.add(Drive.SCOPE_APPFOLDER);
    GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
    if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
      initializeDriveClient(signInAccount);
    } else {
      GoogleSignInOptions signInOptions =
          new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestScopes(Drive.SCOPE_FILE)
              .requestScopes(Drive.SCOPE_APPFOLDER)
              .build();
      GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity, signInOptions);
      activity.startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }
  }

  /**
   * Continues the sign-in process, initializing the Drive clients with the current user's account.
   */
  public void initializeDriveClient(GoogleSignInAccount signInAccount) {
    mDriveResourceClient = Drive.getDriveResourceClient(activity, signInAccount);
    onDriveClientReady();
  }

  private void onDriveClientReady() {
    performAction();
  }

  /**
   * Retrieves results for the next page. For the first run, it retrieves results for the first
   * page.
   */
  private void performAction() {
    Query query =
        new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, FILE_NAME)).build();
    Task<MetadataBuffer> queryTask = mDriveResourceClient.query(query);
    queryTask
        .addOnSuccessListener(activity, successListener)
        .addOnFailureListener(
            activity,
            e -> {
              Log.e(TAG, "Error retrieving files", e);
              showMessage(activity, "");
            });
  }

  private Activity getActivity() {
    return activity;
  }

  public void backup() {
    action = Action.BACKUP;
    signIn();
  }

  public void restore() {
    action = Action.RESTORE;
    signIn();
  }

  private enum Action {
    BACKUP,
    RESTORE
  }

  private static final class BackupAsyncTask extends AsyncTask<MetadataBuffer, Void, Boolean> {
    private final DriveResourceClient mDriveResourceClient;

    private final FavoritesBackup favoritesBackup;

    BackupAsyncTask(DriveResourceClient mDriveResourceClient, FavoritesBackup favoritesBackup) {
      this.mDriveResourceClient = mDriveResourceClient;
      this.favoritesBackup = favoritesBackup;
    }

    @Override
    protected Boolean doInBackground(MetadataBuffer... params) {
      if (params[0].getCount() > 0) {
        Log.i(TAG, "" + params[0].get(0).getDriveId());
        Log.d(TAG, "Modified date: " + params[0].get(0).getModifiedDate());
        DriveFile file = params[0].get(0).getDriveId().asDriveFile();
        editFile(file);

        return true;
      } else {
        createFile();

        return true;
      }
    }

    private void editFile(DriveFile file) {
      Task<DriveContents> openTask = mDriveResourceClient.openFile(file, DriveFile.MODE_WRITE_ONLY);
      openTask
          .continueWithTask(
              task -> {
                DriveContents contents = task.getResult();

                if (contents == null) {
                  return null;
                }

                try (OutputStream out = contents.getOutputStream()) {
                  copyFile(out);
                }

                return mDriveResourceClient.commitContents(contents, null);
              })
          .addOnSuccessListener(
              favoritesBackup.getActivity(),
              driveFile ->
                  showMessage(favoritesBackup.getActivity(), "Successfully backed up contents"))
          .addOnFailureListener(
              favoritesBackup.getActivity(),
              e -> {
                Log.e(TAG, "Unable to create file", e);
                showMessage(favoritesBackup.getActivity(), "Error while backing up contents");
              });
    }

    private void copyFile(OutputStream outputStream) {
      Log.d(TAG, "Copying file to " + dbFilePath);

      File dbFile = new File(dbFilePath);
      FileInputStream fis = null;
      try {
        fis = new FileInputStream(dbFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
          outputStream.write(buffer, 0, length);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }

    private void createFile() {
      Log.d(TAG, "Creating new file");

      final Task<DriveFolder> appFolderTask = mDriveResourceClient.getAppFolder();
      final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();

      Tasks.whenAll(appFolderTask, createContentsTask)
          .continueWithTask(
              task -> {
                DriveFolder parent = appFolderTask.getResult();
                DriveContents contents = createContentsTask.getResult();

                if (contents == null || parent == null) {
                  return null;
                }

                OutputStream outputStream = contents.getOutputStream();
                copyFile(outputStream);

                MetadataChangeSet changeSet =
                    new MetadataChangeSet.Builder()
                        .setTitle(FILE_NAME)
                        .setMimeType("text/plain")
                        .build();

                return mDriveResourceClient.createFile(parent, changeSet, contents);
              })
          .addOnSuccessListener(
              favoritesBackup.getActivity(),
              driveFile ->
                  showMessage(favoritesBackup.getActivity(), "Successfully backed up contents"))
          .addOnFailureListener(
              favoritesBackup.getActivity(),
              e -> {
                Log.e(TAG, "Unable to create file", e);
                showMessage(favoritesBackup.getActivity(), "Error while backing up contents");
              });
    }
  }

  private static final class RestoreAsyncTask extends AsyncTask<MetadataBuffer, Boolean, Boolean> {
    private final DriveResourceClient mDriveResourceClient;

    private final FavoritesBackup favoritesBackup;

    RestoreAsyncTask(DriveResourceClient mDriveResourceClient, FavoritesBackup favoritesBackup) {
      this.mDriveResourceClient = mDriveResourceClient;
      this.favoritesBackup = favoritesBackup;
    }

    @Override
    protected Boolean doInBackground(MetadataBuffer... params) {
      if (params[0].getCount() > 0) {
        DriveFile file = params[0].get(0).getDriveId().asDriveFile();
        Log.d(TAG, "" + file.getDriveId());
        Log.d(TAG, "Modified date: " + params[0].get(0).getModifiedDate());

        retrieveContents(file);
      } else {
        Log.d(TAG, "File not found");
      }

      return true;
    }

    private void copyFile(InputStream inputStream) {
      Log.d(TAG, "Copying file to " + dbFilePath);

      OutputStream output = null;
      int length;
      try {
        output = new FileOutputStream(dbFilePath);
        byte[] buffer = new byte[1024];
        while ((length = inputStream.read(buffer)) > 0) {
          output.write(buffer, 0, length);
        }
      } catch (IOException e) {
        Log.e(TAG, "IOException while reading from the stream", e);
      } finally {
        if (output != null) {
          try {
            output.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }

    private void retrieveContents(DriveFile file) {
      TagDbHelper.clearInstance();

      Task<DriveContents> openFileTask =
          mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
      openFileTask
          .continueWithTask(
              task -> {
                DriveContents contents = task.getResult();

                if (contents == null) {
                  return null;
                }

                copyFile(contents.getInputStream());
                return mDriveResourceClient.discardContents(contents);
              })
          .addOnSuccessListener(
              favoritesBackup.getActivity(),
              driveFile ->
                  showMessage(favoritesBackup.getActivity(), "Successfully restored backup"))
          .addOnFailureListener(
              favoritesBackup.getActivity(),
              e -> {
                Log.e(TAG, "Unable to create file", e);
                showMessage(favoritesBackup.getActivity(), "Error while restoring backup");
              });
    }
  }
}
