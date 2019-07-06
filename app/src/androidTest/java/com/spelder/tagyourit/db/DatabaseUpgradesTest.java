package com.spelder.tagyourit.db;

import static org.junit.Assert.assertEquals;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DatabaseUpgradesTest {
  private static final String TAG = DatabaseUpgradesTest.class.getCanonicalName();

  @Test
  public void testDatabaseUpgrades() throws IOException {
    TagDbHelper.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());

    for (int i = 3; i < TagDbHelper.DATABASE_VERSION; i++) {
      Log.d(TAG, "Testing upgrade from version:" + i);
      TagDbHelper.clearInstance();
      copyDatabase(i);

      TagDbHelper databaseHelperNew =
          TagDbHelper.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());
      Log.d(TAG, " New Database Version:" + databaseHelperNew.getWritableDatabase().getVersion());
      assertEquals(
          TagDbHelper.DATABASE_VERSION, databaseHelperNew.getWritableDatabase().getVersion());
    }
  }

  private void copyDatabase(int version) throws IOException {
    String dbPath =
        InstrumentationRegistry.getInstrumentation()
            .getTargetContext()
            .getDatabasePath(TagDbHelper.DATABASE_NAME)
            .getAbsolutePath();

    Log.d(TAG, "DB Path: " + dbPath);

    String dbName = String.format("database_v%d.db", version);
    InputStream mInput =
        InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(dbName);

    File db = new File(dbPath);
    boolean results = true;
    if (!db.exists()) {
      if (!db.getParentFile().exists()) {
        results = db.getParentFile().mkdirs();
      }
      if (results) {
        results = db.createNewFile();
      }
    }
    if (results) {
      OutputStream mOutput = new FileOutputStream(dbPath);
      byte[] mBuffer = new byte[1024];
      int mLength;
      while ((mLength = mInput.read(mBuffer)) > 0) {
        mOutput.write(mBuffer, 0, mLength);
      }
      mOutput.flush();
      mOutput.close();
    }
    mInput.close();
  }
}
