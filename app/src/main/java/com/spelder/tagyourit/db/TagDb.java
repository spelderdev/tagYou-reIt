package com.spelder.tagyourit.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.model.TrackComponents;
import com.spelder.tagyourit.model.VideoComponents;
import com.spelder.tagyourit.networking.api.filter.FilterBy;
import com.spelder.tagyourit.pitch.Pitch;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Used to manage the database for tags. */
public class TagDb {
  private final Context context;

  private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

  public TagDb(Context c) {
    context = c;
  }

  public long insertFavorite(Tag tag) {
    long tagId = insertTag(tag);
    insertFavorite(tagId);
    return tagId;
  }

  private long insertTag(Tag tag) {
    // Gets the data repository in write mode
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put(TagContract.TagEntry.COLUMN_NAME_ID, tag.getId());
    values.put(TagContract.TagEntry.COLUMN_NAME_TITLE, tag.getTitle());
    values.put(TagContract.TagEntry.COLUMN_NAME_VERSION, tag.getVersion());
    values.put(TagContract.TagEntry.COLUMN_NAME_ARRANGER, tag.getArranger());
    values.put(TagContract.TagEntry.COLUMN_NAME_RATING, tag.getRating());
    values.put(TagContract.TagEntry.COLUMN_NAME_KEY, tag.getKey());
    values.put(TagContract.TagEntry.COLUMN_NAME_PARTS_NUMBER, tag.getNumberOfParts());
    values.put(TagContract.TagEntry.COLUMN_NAME_LYRICS, tag.getLyrics());
    values.put(TagContract.TagEntry.COLUMN_NAME_TYPE, tag.getType());
    values.put(TagContract.TagEntry.COLUMN_NAME_SHEET_MUSIC_TYPE, tag.getSheetMusicType());
    values.put(TagContract.TagEntry.COLUMN_NAME_SHEET_MUSIC_LINK, tag.getSheetMusicLink());
    values.put(TagContract.TagEntry.COLUMN_NAME_SHEET_MUSIC_FILE, tag.getSheetMusicFile());
    values.put(TagContract.TagEntry.COLUMN_NAME_LAST_MODIFIED_DATE, sdf.format(new Date()));
    values.put(TagContract.TagEntry.COLUMN_NAME_TYPE, sdf.format(new Date()));
    // Insert the new row, returning the primary key value of the new row
    long newRowId = db.insert(TagContract.TagEntry.TABLE_NAME, null, values);
    Log.d("TagDb,", "tagId: " + tag.getId());
    db.close();

    insertTrack(tag.getTracks(), newRowId);
    insertVideos(tag.getVideos(), newRowId);

    return newRowId;
  }

  public void updateTags(List<Tag> tags) {
    // Gets the data repository in write mode
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    for (Tag tag : tags) {
      updateTag(tag, db);
    }

    db.close();
  }

  public void updateVideos(List<VideoComponents> videos) {
    if (videos.isEmpty()) {
      return;
    }

    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    long dbId = getDbIdFromTagId(videos.get(0).getTagId(), db);
    if (dbId > -1) {
      deleteVideos(dbId, db);

      insertVideos(videos, dbId);
    }

    db.close();
  }

  private void updateTag(Tag tag, SQLiteDatabase db) {
    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put(TagContract.TagEntry.COLUMN_NAME_TITLE, tag.getTitle());
    values.put(TagContract.TagEntry.COLUMN_NAME_VERSION, tag.getVersion());
    values.put(TagContract.TagEntry.COLUMN_NAME_ARRANGER, tag.getArranger());
    values.put(TagContract.TagEntry.COLUMN_NAME_RATING, tag.getRating());
    values.put(TagContract.TagEntry.COLUMN_NAME_KEY, tag.getKey());
    values.put(TagContract.TagEntry.COLUMN_NAME_PARTS_NUMBER, tag.getNumberOfParts());
    values.put(TagContract.TagEntry.COLUMN_NAME_LYRICS, tag.getLyrics());
    values.put(TagContract.TagEntry.COLUMN_NAME_TYPE, tag.getType());
    values.put(TagContract.TagEntry.COLUMN_NAME_SHEET_MUSIC_TYPE, tag.getSheetMusicType());
    values.put(TagContract.TagEntry.COLUMN_NAME_SHEET_MUSIC_LINK, tag.getSheetMusicLink());
    values.put(TagContract.TagEntry.COLUMN_NAME_SHEET_MUSIC_FILE, tag.getSheetMusicFile());
    values.put(TagContract.TagEntry.COLUMN_NAME_LAST_MODIFIED_DATE, sdf.format(new Date()));
    // Insert the new row, returning the primary key value of the new row
    String strFilter = TagContract.TagEntry.COLUMN_NAME_ID + "=" + tag.getId();
    db.update(TagContract.TagEntry.TABLE_NAME, values, strFilter, null);
    Log.d("TagDb,", "Updated tagId: " + tag.getId());

    long dbId = getDbIdFromTagId(tag.getId(), db);
    if (dbId > -1) {
      deleteTracks(dbId, db);
      deleteVideos(dbId, db);

      insertTrack(tag.getTracks(), dbId);
      insertVideos(tag.getVideos(), dbId);
    }
  }

  private void deleteVideos(long dbId, SQLiteDatabase db) {
    // Define 'where' part of query.
    String selection = TagContract.VideoEntry.COLUMN_NAME_TAG_ID + " LIKE ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs4 = {"" + dbId};
    // Issue SQL statement.
    int deletedRows = db.delete(TagContract.VideoEntry.TABLE_NAME, selection, selectionArgs4);
    Log.d("VideoDB", "Number of Deleted rows: " + deletedRows);
  }

  private void deleteTracks(long dbId, SQLiteDatabase db) {
    // Define 'where' part of query.
    String selection = TagContract.LearningTracksEntry.COLUMN_NAME_TAG_ID + " LIKE ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs3 = {"" + dbId};
    // Issue SQL statement.
    int deletedRows =
        db.delete(TagContract.LearningTracksEntry.TABLE_NAME, selection, selectionArgs3);
    Log.d("LearningTrackDB", "Number of Deleted rows: " + deletedRows);
  }

  private long getDbIdFromTagId(int tagId, SQLiteDatabase db) {
    String sql =
        "SELECT "
            + TagContract.TagEntry._ID
            + " FROM "
            + TagContract.TagEntry.TABLE_NAME
            + " WHERE "
            + TagContract.TagEntry.COLUMN_NAME_ID
            + " = "
            + tagId;
    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return -1;
    }
    c.moveToFirst();

    long dbId;
    do {
      dbId = c.getLong(c.getColumnIndex(TagContract.TagEntry._ID));
    } while (c.moveToNext());
    c.close();
    return dbId;
  }

  public List<Integer> getOutdatedTagIdList(int updatedDays) {
    ArrayList<Integer> outdatedTagIdList = new ArrayList<>();
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    String sql =
        "SELECT "
            + TagContract.TagEntry.COLUMN_NAME_ID
            + " FROM "
            + TagContract.TagEntry.TABLE_NAME
            + " WHERE "
            + TagContract.TagEntry.COLUMN_NAME_LAST_MODIFIED_DATE
            + " IS NULL OR "
            + TagContract.TagEntry.COLUMN_NAME_LAST_MODIFIED_DATE
            + " <= date('now','-"
            + updatedDays
            + " day')";
    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return outdatedTagIdList;
    }
    c.moveToFirst();

    do {
      outdatedTagIdList.add(c.getInt(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_ID)));
      Log.d("TagDb", "" + c.getInt(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_ID)));
    } while (c.moveToNext());
    c.close();
    db.close();
    return outdatedTagIdList;
  }

  private void insertTrack(Collection<TrackComponents> tracks, long tagId) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    for (TrackComponents track : tracks) {
      ContentValues values = new ContentValues();
      values.put(TagContract.LearningTracksEntry.COLUMN_NAME_TAG_ID, tagId);
      values.put(TagContract.LearningTracksEntry.COLUMN_NAME_PART, track.getPart());
      values.put(TagContract.LearningTracksEntry.COLUMN_NAME_LINK, track.getLink());
      values.put(TagContract.LearningTracksEntry.COLUMN_NAME_FILE_TYPE, track.getType());
      values.put(TagContract.LearningTracksEntry.COLUMN_NAME_FILE, "");
      long newRowId = db.insert(TagContract.LearningTracksEntry.TABLE_NAME, null, values);
      Log.d("TrackDb,", "trackId: " + newRowId);
    }
    db.close();
  }

  private void insertVideos(Collection<VideoComponents> videos, long tagId) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    for (VideoComponents video : videos) {
      ContentValues values = new ContentValues();
      values.put(TagContract.VideoEntry.COLUMN_NAME_TAG_ID, tagId);
      values.put(TagContract.VideoEntry.COLUMN_NAME_TITLE, video.getVideoTitle());
      values.put(TagContract.VideoEntry.COLUMN_NAME_DESCRIPTION, video.getDescription());
      if (video.getSungKey() != null) {
        values.put(TagContract.VideoEntry.COLUMN_NAME_KEY, video.getSungKey().name());
      }
      values.put(TagContract.VideoEntry.COLUMN_NAME_MULTITRACK, video.isMultitrack() ? 1 : 0);
      values.put(TagContract.VideoEntry.COLUMN_NAME_VIDEO_ID, video.getId());
      values.put(TagContract.VideoEntry.COLUMN_NAME_VIDEO_CODE, video.getVideoCode());
      values.put(TagContract.VideoEntry.COLUMN_NAME_SUNG_BY, video.getSungBy());
      values.put(TagContract.VideoEntry.COLUMN_NAME_SUNG_WEBSITE, video.getSungWebsite());
      values.put(TagContract.VideoEntry.COLUMN_NAME_POSTED_DATE, video.getPostedDate());
      values.put(
          TagContract.VideoEntry.COLUMN_NAME_VIEW_COUNT,
          video.getViewCount() != null ? video.getViewCount().toString() : "0");
      values.put(
          TagContract.VideoEntry.COLUMN_NAME_LIKE_COUNT,
          video.getLikeCount() != null ? video.getLikeCount().toString() : "0");
      values.put(
          TagContract.VideoEntry.COLUMN_NAME_DISLIKE_COUNT,
          video.getDislikeCount() != null ? video.getDislikeCount().toString() : "0");
      values.put(
          TagContract.VideoEntry.COLUMN_NAME_FAVORITE_COUNT,
          video.getFavoriteCount() != null ? video.getFavoriteCount().toString() : "0");
      values.put(
          TagContract.VideoEntry.COLUMN_NAME_COMMENT_COUNT,
          video.getCommentCount() != null ? video.getCommentCount().toString() : "0");
      long newRowId = db.insert(TagContract.VideoEntry.TABLE_NAME, null, values);
      Log.d("VideoDb,", "videoId: " + newRowId);
    }
    db.close();
  }

  private void insertFavorite(long tagId) {
    // Gets the data repository in write mode
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put(TagContract.FavoritesEntry.COLUMN_NAME_TAG_ID, tagId);
    // Insert the new row, returning the primary key value of the new row
    long newRowId = db.insert(TagContract.FavoritesEntry.TABLE_NAME, null, values);
    Log.d("FavoriteDb,", "tagId: " + newRowId);
    db.close();
  }

  public void deleteFavorite(Tag tag) {
    Log.d(
        "FavoriteDB",
        "Deleting favorite with title: " + tag.getTitle() + " and DB ID: " + tag.getDbId());

    // Gets the data repository in write mode
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    // Define 'where' part of query.
    String selection = TagContract.FavoritesEntry.COLUMN_NAME_TAG_ID + " LIKE ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs = {"" + tag.getDbId()};
    // Issue SQL statement.
    int deletedRows = db.delete(TagContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs);
    Log.d("FavoriteDB", "Number of Deleted rows: " + deletedRows);

    // Define 'where' part of query.
    selection = TagContract.LearningTracksEntry.COLUMN_NAME_TAG_ID + " LIKE ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs3 = {"" + tag.getDbId()};
    // Issue SQL statement.
    deletedRows = db.delete(TagContract.LearningTracksEntry.TABLE_NAME, selection, selectionArgs3);
    Log.d("LearningTrackDB", "Number of Deleted rows: " + deletedRows);

    // Define 'where' part of query.
    selection = TagContract.VideoEntry.COLUMN_NAME_TAG_ID + " LIKE ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs4 = {"" + tag.getDbId()};
    // Issue SQL statement.
    deletedRows = db.delete(TagContract.VideoEntry.TABLE_NAME, selection, selectionArgs4);
    Log.d("VideoDB", "Number of Deleted rows: " + deletedRows);

    // Define 'where' part of query.
    selection = TagContract.TagEntry._ID + " LIKE ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs2 = {"" + tag.getDbId()};
    // Issue SQL statement.
    deletedRows = db.delete(TagContract.TagEntry.TABLE_NAME, selection, selectionArgs2);
    Log.d("TagDB", "Number of Deleted rows: " + deletedRows);

    db.close();
  }

  public Long isFavorite(Tag tag) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    String sql =
        "SELECT * FROM "
            + TagContract.TagEntry.TABLE_NAME
            + ", "
            + TagContract.FavoritesEntry.TABLE_NAME
            + " WHERE "
            + TagContract.TagEntry.TABLE_NAME
            + "."
            + TagContract.TagEntry._ID
            + " = "
            + TagContract.FavoritesEntry.TABLE_NAME
            + "."
            + TagContract.FavoritesEntry.COLUMN_NAME_TAG_ID
            + " AND "
            + TagContract.TagEntry.TABLE_NAME
            + "."
            + TagContract.TagEntry.COLUMN_NAME_ID
            + " = "
            + tag.getId();
    Cursor c = db.rawQuery(sql, new String[] {});
    Long dbId = null;
    if (c.getCount() > 0) {
      c.moveToFirst();
      dbId = c.getLong(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_ID));
    }
    c.close();
    db.close();
    return dbId;
  }

  public void deleteAllFavorites() {
    List<Tag> tags = getFavorites();
    for (Tag tag : tags) {
      deleteFavorite(tag);
    }
  }

  public List<Tag> getFavorites(FilterBy filter) {
    String sql =
        "SELECT * FROM "
            + TagContract.TagEntry.TABLE_NAME
            + ", "
            + TagContract.FavoritesEntry.TABLE_NAME
            + " WHERE "
            + TagContract.TagEntry.TABLE_NAME
            + "."
            + TagContract.TagEntry._ID
            + " = "
            + TagContract.FavoritesEntry.TABLE_NAME
            + "."
            + TagContract.FavoritesEntry.COLUMN_NAME_TAG_ID
            + filter.getDbFilter()
            + " ORDER BY "
            + TagContract.TagEntry.COLUMN_NAME_TITLE;

    return getFavorites(sql);
  }

  private List<Tag> getFavorites(String sql) {
    ArrayList<Tag> tags = new ArrayList<>();
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return tags;
    }
    c.moveToFirst();

    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    boolean saveFavorites = sharedPref.getBoolean("pref_favorites_save", false);

    do {
      Tag tag = new Tag();
      tag.setDbId(c.getLong(c.getColumnIndex(TagContract.TagEntry._ID)));
      tag.setId(c.getInt(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_ID)));
      tag.setTitle(c.getString(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_TITLE)));
      tag.setVersion(c.getString(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_VERSION)));
      tag.setArranger(c.getString(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_ARRANGER)));
      tag.setRating(c.getString(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_RATING)));
      tag.setKey(c.getString(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_KEY)));
      tag.setNumberOfParts(
          c.getInt(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_PARTS_NUMBER)));
      tag.setLyrics(c.getString(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_LYRICS)));
      tag.setType(c.getString(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_TYPE)));
      tag.setSheetMusicType(
          c.getString(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_SHEET_MUSIC_TYPE)));
      tag.setSheetMusicLink(
          c.getString(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_SHEET_MUSIC_LINK)));
      tag.setSheetMusicFile(
          c.getString(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_SHEET_MUSIC_FILE)));
      tag.setDownloaded(saveFavorites);
      addTracks(tag, db);
      addVideos(tag, db);
      tags.add(tag);
      Log.d("TagDb", "" + c.getInt(c.getColumnIndex(TagContract.TagEntry.COLUMN_NAME_ID)));
    } while (c.moveToNext());
    c.close();
    db.close();
    return tags;
  }

  public List<Tag> getFavorites() {
    String sql =
        "SELECT * FROM "
            + TagContract.TagEntry.TABLE_NAME
            + ", "
            + TagContract.FavoritesEntry.TABLE_NAME
            + " WHERE "
            + TagContract.TagEntry.TABLE_NAME
            + "."
            + TagContract.TagEntry._ID
            + " = "
            + TagContract.FavoritesEntry.TABLE_NAME
            + "."
            + TagContract.FavoritesEntry.COLUMN_NAME_TAG_ID
            + " ORDER BY "
            + TagContract.TagEntry.COLUMN_NAME_TITLE;
    return getFavorites(sql);
  }

  private void addTracks(Tag tag, SQLiteDatabase db) {
    String sql =
        "SELECT * FROM "
            + TagContract.LearningTracksEntry.TABLE_NAME
            + " WHERE "
            + TagContract.LearningTracksEntry.TABLE_NAME
            + "."
            + TagContract.LearningTracksEntry.COLUMN_NAME_TAG_ID
            + " = "
            + tag.getDbId();
    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return;
    }
    c.moveToFirst();
    do {
      String type =
          c.getString(c.getColumnIndex(TagContract.LearningTracksEntry.COLUMN_NAME_FILE_TYPE));
      String part = c.getString(c.getColumnIndex(TagContract.LearningTracksEntry.COLUMN_NAME_PART));
      String link = c.getString(c.getColumnIndex(TagContract.LearningTracksEntry.COLUMN_NAME_LINK));
      tag.addTrack(part, link, type);
    } while (c.moveToNext());

    c.close();
  }

  private void addVideos(Tag tag, SQLiteDatabase db) {
    String sql =
        "SELECT * FROM "
            + TagContract.VideoEntry.TABLE_NAME
            + " WHERE "
            + TagContract.VideoEntry.TABLE_NAME
            + "."
            + TagContract.VideoEntry.COLUMN_NAME_TAG_ID
            + " = "
            + tag.getDbId();
    Cursor c = db.rawQuery(sql, new String[] {});
    Log.d("VideoDb", "Number of Videos: " + c.getCount());
    if (c.getCount() == 0) {
      return;
    }
    c.moveToFirst();
    do {
      VideoComponents video = new VideoComponents();
      video.setId(c.getInt(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_VIDEO_ID)));
      video.setMultitrack(
          c.getInt(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_MULTITRACK)) != 0);
      String sungKey = c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_KEY));
      if (sungKey != null && !sungKey.isEmpty()) {
        video.setSungKey(Pitch.valueOf(sungKey));
      }
      video.setSungWebsite(
          c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_SUNG_WEBSITE)));
      video.setPostedDate(
          c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_POSTED_DATE)));
      video.setDescription(
          c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_DESCRIPTION)));
      video.setVideoTitle(c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_TITLE)));
      video.setVideoCode(
          c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_VIDEO_CODE)));
      video.setSungBy(c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_SUNG_BY)));
      video.setViewCount(
          new BigInteger(
              c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_VIEW_COUNT))));
      video.setLikeCount(
          new BigInteger(
              c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_LIKE_COUNT))));
      video.setDislikeCount(
          new BigInteger(
              c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_DISLIKE_COUNT))));
      video.setFavoriteCount(
          new BigInteger(
              c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_FAVORITE_COUNT))));
      video.setCommentCount(
          new BigInteger(
              c.getString(c.getColumnIndex(TagContract.VideoEntry.COLUMN_NAME_COMMENT_COUNT))));

      tag.addVideo(video);
    } while (c.moveToNext());

    c.close();
  }

  public boolean hasFavorites() {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    // SELECT COUNT(column_name) FROM table_name;
    String sql = "SELECT COUNT(*) AS favCount FROM " + TagContract.FavoritesEntry.TABLE_NAME;
    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return false;
    }
    c.moveToFirst();
    int i = c.getInt(0);
    c.close();
    db.close();
    return i > 0;
  }

  public void insertUserRating(long tagId, double rating) {
    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put(TagContract.RatingEntry.COLUMN_NAME_TAG_ID, tagId);
    values.put(TagContract.RatingEntry.COLUMN_NAME_TAG_RATING, rating);

    if (getUserRating(tagId) < 0) {
      SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
      // Insert the new row, returning the primary key value of the new row
      long newRowId = db.insert(TagContract.RatingEntry.TABLE_NAME, null, values);
      Log.d("TagDb,", "Inserted ratingId: " + newRowId);
      db.close();
    } else {
      SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
      String[] args = new String[] {"" + tagId};
      long newRowId =
          db.update(
              TagContract.RatingEntry.TABLE_NAME,
              values,
              TagContract.RatingEntry.COLUMN_NAME_TAG_ID + "=?",
              args);
      Log.d("TagDb,", "Updated ratingId: " + newRowId);
      db.close();
    }
  }

  public double getUserRating(long tagId) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    String sql =
        "SELECT * FROM "
            + TagContract.RatingEntry.TABLE_NAME
            + " WHERE "
            + TagContract.RatingEntry.TABLE_NAME
            + "."
            + TagContract.RatingEntry.COLUMN_NAME_TAG_ID
            + " = "
            + tagId;
    Cursor c = db.rawQuery(sql, new String[] {});
    double rating = -1.0;
    if (c.getCount() > 0) {
      c.moveToFirst();
      rating =
          Double.parseDouble(
              c.getString(c.getColumnIndex(TagContract.RatingEntry.COLUMN_NAME_TAG_RATING)));
    }
    c.close();
    db.close();
    return rating;
  }
}
