package com.spelder.tagyourit.drive;

import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a file
 * picker UI via Storage Access Framework.
 */
class DriveServiceHelper {
  private static final String TYPE_UNKNOWN = "application/vnd.google-apps.unknown";
  private final Executor mExecutor = Executors.newSingleThreadExecutor();
  private final Drive mDriveService;

  DriveServiceHelper(Drive driveService) {
    mDriveService = driveService;
  }

  /** Downloads the file identified by {@code fileId} to {@code localFile}. */
  Task<Void> downloadFile(String fileId, java.io.File localFile) {
    return Tasks.call(
        mExecutor,
        () -> {
          OutputStream outputStream = new FileOutputStream(localFile);
          mDriveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
          return null;
        });
  }

  /**
   * Updates the file identified by {@code fileId} with the given {@code name} from {@code
   * localFile}.
   */
  Task<File> updateFile(String fileId, String name, java.io.File localFile) {
    return Tasks.call(
        mExecutor,
        () -> {
          File metadata =
              new File()
                  .setName(name)
                  .setMimeType(TYPE_UNKNOWN)
                  .setParents(Collections.singletonList("appDataFolder"));

          FileContent fileContent = new FileContent(TYPE_UNKNOWN, localFile);

          return mDriveService.files().update(fileId, metadata, fileContent).execute();
        });
  }

  /** Create the file with the given {@code name} from {@code localFile}. */
  Task<File> createFile(String name, java.io.File localFile) {
    return Tasks.call(
        mExecutor,
        () -> {
          File metadata =
              new File()
                  .setName(name)
                  .setMimeType(TYPE_UNKNOWN)
                  .setParents(Collections.singletonList("appDataFolder"));

          FileContent fileContent = new FileContent(TYPE_UNKNOWN, localFile);

          return mDriveService.files().create(metadata, fileContent).execute();
        });
  }

  Task<String> queryFiles(String filename) {
    return Tasks.call(
        mExecutor,
        () -> {
          FileList result =
              mDriveService
                  .files()
                  .list()
                  .setQ("name = '" + filename + "'")
                  .setSpaces("appDataFolder")
                  .setFields("files(id, name, mimeType)")
                  .execute();

          String id = "";
          if (result.getFiles().size() > 0) {
            id = result.getFiles().get(0).getId();
            Log.d("DriveServiceHelper", result.getFiles().get(0).getMimeType());
          }

          return id;
        });
  }
}
