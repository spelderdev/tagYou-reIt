package com.spelder.tagyourit.db;

import android.provider.BaseColumns;

/** Defines the structure of the tag databases. */
public class TagContract {
  private static final String TEXT_TYPE = " TEXT";

  private static final String INT_TYPE = " INT";

  private static final String COMMA_SEP = ",";

  private static final String FOREIGN_KEY_OPEN = "FOREIGN KEY(";

  private static final String FOREIGN_KEY_CLOSE = ") REFERENCES ";

  private TagContract() {
    // Do not instantiate
  }

  /* Inner class that defines the table contents */
  public static class TagEntry implements BaseColumns {
    public static final String TABLE_NAME = "tag";

    public static final String COLUMN_NAME_RATING = "rating";

    public static final String COLUMN_NAME_KEY = "key";

    public static final String COLUMN_NAME_PARTS_NUMBER = "parts";

    public static final String COLUMN_NAME_SHEET_MUSIC_LINK = "sheet_music_link";

    public static final String COLUMN_NAME_TYPE = "type";

    static final String COLUMN_NAME_ID = "tag_id";

    static final String COLUMN_NAME_TITLE = "title";

    static final String COLUMN_NAME_VERSION = "version";

    static final String COLUMN_NAME_ARRANGER = "arranger";

    static final String COLUMN_NAME_LYRICS = "lyrics";

    static final String COLUMN_NAME_SHEET_MUSIC_TYPE = "sheet_music_type";

    static final String COLUMN_NAME_SHEET_MUSIC_FILE = "sheet_music_file";

    static final String COLUMN_NAME_LAST_MODIFIED_DATE = "last_modified_date";

    static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + _ID
            + " INTEGER PRIMARY KEY,"
            + COLUMN_NAME_ID
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_TITLE
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_VERSION
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_ARRANGER
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_RATING
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_KEY
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_PARTS_NUMBER
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_LYRICS
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_SHEET_MUSIC_TYPE
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_SHEET_MUSIC_LINK
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_SHEET_MUSIC_FILE
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_LAST_MODIFIED_DATE
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_TYPE
            + TEXT_TYPE
            + " )";

    static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
  }

  public static class LearningTracksEntry implements BaseColumns {
    public static final String TABLE_NAME = "learning_tracks";

    public static final String COLUMN_NAME_TAG_ID = "learning_tag_id";

    static final String COLUMN_NAME_PART = "part";

    static final String COLUMN_NAME_FILE_TYPE = "file_type";

    static final String COLUMN_NAME_LINK = "link";

    static final String COLUMN_NAME_FILE = "file";

    static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + _ID
            + " INTEGER PRIMARY KEY,"
            + COLUMN_NAME_TAG_ID
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_PART
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_FILE_TYPE
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_LINK
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_FILE
            + TEXT_TYPE
            + COMMA_SEP
            + FOREIGN_KEY_OPEN
            + COLUMN_NAME_TAG_ID
            + FOREIGN_KEY_CLOSE
            + TagEntry.TABLE_NAME
            + "("
            + TagEntry._ID
            + ")"
            + " )";

    static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
  }

  static class VideoEntry implements BaseColumns {
    static final String TABLE_NAME = "video";

    static final String COLUMN_NAME_VIDEO_ID = "video_id";

    static final String COLUMN_NAME_TITLE = "title";

    static final String COLUMN_NAME_DESCRIPTION = "description";

    static final String COLUMN_NAME_KEY = "key";

    static final String COLUMN_NAME_VIDEO_CODE = "video_code";

    static final String COLUMN_NAME_SUNG_BY = "sung_by";

    static final String COLUMN_NAME_SUNG_WEBSITE = "sung_website";

    static final String COLUMN_NAME_POSTED_DATE = "posted_date";

    static final String COLUMN_NAME_MULTITRACK = "multitrack";

    static final String COLUMN_NAME_VIEW_COUNT = "view_count";

    static final String COLUMN_NAME_LIKE_COUNT = "like_count";

    static final String COLUMN_NAME_DISLIKE_COUNT = "dislike_count";

    static final String COLUMN_NAME_FAVORITE_COUNT = "favorite_count";

    static final String COLUMN_NAME_COMMENT_COUNT = "comment_count";

    static final String COLUMN_NAME_TAG_ID = "video_tag_id";

    static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + _ID
            + " INTEGER PRIMARY KEY,"
            + COLUMN_NAME_VIDEO_ID
            + INT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_TITLE
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_DESCRIPTION
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_KEY
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_VIDEO_CODE
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_SUNG_BY
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_SUNG_WEBSITE
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_POSTED_DATE
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_TAG_ID
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_MULTITRACK
            + INT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_VIEW_COUNT
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_LIKE_COUNT
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_DISLIKE_COUNT
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_FAVORITE_COUNT
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_COMMENT_COUNT
            + TEXT_TYPE
            + COMMA_SEP
            + FOREIGN_KEY_OPEN
            + COLUMN_NAME_TAG_ID
            + FOREIGN_KEY_CLOSE
            + TagEntry.TABLE_NAME
            + "("
            + TagEntry._ID
            + ")"
            + " )";

    static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
  }

  static class FavoritesEntry implements BaseColumns {
    static final String TABLE_NAME = "favorites";

    static final String COLUMN_NAME_TAG_ID = "favorite_tag_id";

    static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + _ID
            + " INTEGER PRIMARY KEY,"
            + COLUMN_NAME_TAG_ID
            + TEXT_TYPE
            + COMMA_SEP
            + FOREIGN_KEY_OPEN
            + COLUMN_NAME_TAG_ID
            + FOREIGN_KEY_CLOSE
            + TagEntry.TABLE_NAME
            + "("
            + TagEntry._ID
            + ")"
            + " )";

    static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
  }

  static class RatingEntry implements BaseColumns {
    static final String TABLE_NAME = "rating";

    static final String COLUMN_NAME_TAG_ID = "rating_tag_id";

    static final String COLUMN_NAME_TAG_RATING = "rating_tag_rating";

    static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + _ID
            + " INTEGER PRIMARY KEY,"
            + COLUMN_NAME_TAG_ID
            + TEXT_TYPE
            + COMMA_SEP
            + COLUMN_NAME_TAG_RATING
            + TEXT_TYPE
            + " )";

    static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
  }
}
