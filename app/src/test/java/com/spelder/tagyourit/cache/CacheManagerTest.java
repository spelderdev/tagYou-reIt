package com.spelder.tagyourit.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.junit.After;
import org.junit.Test;

public class CacheManagerTest {
  private static final String TEST_DIR = "src/test/resources/testDir/";

  @After
  public void after() {
    File file = new File(TEST_DIR);
    assertTrue(deleteDirectory(file));
  }

  @Test
  public void checkCacheAndClear_SingleFileDelete() throws IOException {
    // Given files written
    File file = createFile(TEST_DIR + "file1.txt", 6000000);

    // When cache checked
    CacheManager.checkCacheAndClear(file.getParentFile(), "");

    // Then
    assertFalse(file.exists());
  }

  @Test
  public void checkCacheAndClear_SingleFileLeave() throws IOException {
    // Given files written
    File file = createFile(TEST_DIR + "file2.txt", 60000);

    // When cache checked
    CacheManager.checkCacheAndClear(file.getParentFile(), "");

    // Then
    assertTrue(file.exists());
  }

  @Test
  public void checkCacheAndClear_MultipleFiles() throws IOException {
    // Given files written
    File file1 = createFile(TEST_DIR + "file3.txt", 6000000);
    File file2 = createFile(TEST_DIR + "file4.txt", 60000);

    // When cache checked
    CacheManager.checkCacheAndClear(file1.getParentFile(), "");

    // Then
    assertFalse(file1.exists());
    assertTrue(file2.exists());
  }

  private File createFile(final String filename, final long sizeInBytes) throws IOException {
    File file = new File(filename);
    assertNotNull(file.getParentFile());
    assertTrue(file.getParentFile().exists() || file.getParentFile().mkdirs());
    assertTrue(file.createNewFile());

    try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
      raf.setLength(sizeInBytes);
    }

    return file;
  }

  private boolean deleteDirectory(File directoryToBeDeleted) {
    File[] allContents = directoryToBeDeleted.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        deleteDirectory(file);
      }
    }
    return directoryToBeDeleted.delete();
  }
}
