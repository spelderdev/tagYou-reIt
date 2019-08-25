package com.spelder.tagyourit.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Methods to handle the overall state of the database including creating, upgrading and downgrading
 * versions.
 */
public class TagDbHelper extends SQLiteOpenHelper {
  public static final String DATABASE_NAME = "Tag.db";

  // If you change the database schema, you must increment the database version.
  static final int DATABASE_VERSION = 6;

  private static final String TAG = TagDbHelper.class.getName();

  private static TagDbHelper mInstance = null;

  private final AssetManager assetManager;

  private TagDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    assetManager = context.getAssets();
  }

  static synchronized TagDbHelper getInstance(Context ctx) {
    if (mInstance == null) {
      mInstance = new TagDbHelper(ctx.getApplicationContext());
    }
    return mInstance;
  }

  // @VisibleForTesting
  public static void clearInstance() {
    mInstance = null;
  }

  public void onCreate(SQLiteDatabase db) {
    db.execSQL(TagContract.TagEntry.SQL_CREATE_ENTRIES);
    db.execSQL(TagContract.LearningTracksEntry.SQL_CREATE_ENTRIES);
    db.execSQL(TagContract.FavoritesEntry.SQL_CREATE_ENTRIES);
    db.execSQL(TagContract.RatingEntry.SQL_CREATE_ENTRIES);
    db.execSQL(TagContract.VideoEntry.SQL_CREATE_ENTRIES);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (DATABASE_VERSION <= 3) {
      db.execSQL(TagContract.TagEntry.SQL_DELETE_ENTRIES);
      db.execSQL(TagContract.LearningTracksEntry.SQL_DELETE_ENTRIES);
      db.execSQL(TagContract.FavoritesEntry.SQL_DELETE_ENTRIES);
      db.execSQL(TagContract.RatingEntry.SQL_DELETE_ENTRIES);
      db.execSQL(TagContract.VideoEntry.SQL_DELETE_ENTRIES);
      onCreate(db);
    } else {
      Log.e(TAG, "Updating table from " + oldVersion + " to " + newVersion);
      for (int i = oldVersion; i < newVersion; ++i) {
        String migrationName = String.format(Locale.US, "from_%d_to_%d.sql", i, (i + 1));
        Log.d(TAG, "Looking for migration file: " + migrationName);
        readAndExecuteSQLScript(db, migrationName);
      }
    }
  }

  @Override
  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    onUpgrade(db, oldVersion, newVersion);
  }

  private void readAndExecuteSQLScript(SQLiteDatabase db, String fileName) {
    if (TextUtils.isEmpty(fileName)) {
      Log.d(TAG, "SQL script file name is empty");
      return;
    }

    Log.d(TAG, "Script found. Executing...");
    BufferedReader reader = null;

    try {
      InputStream is = assetManager.open(fileName);
      InputStreamReader isr = new InputStreamReader(is);
      reader = new BufferedReader(isr);
      executeSQLScript(db, reader);
    } catch (IOException e) {
      Log.e(TAG, "IOException:", e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          Log.e(TAG, "IOException:", e);
        }
      }
    }
  }

  private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
    String line;
    StringBuilder statement = new StringBuilder();
    while ((line = reader.readLine()) != null) {
      statement.append(line);
      statement.append("\n");
      if (line.endsWith(";")) {
        db.execSQL(statement.toString());
        statement = new StringBuilder();
      }
    }
  }
}
