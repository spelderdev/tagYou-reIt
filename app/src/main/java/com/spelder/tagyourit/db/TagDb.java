package com.spelder.tagyourit.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.spelder.tagyourit.db.TagContract.LearningTracksEntry;
import com.spelder.tagyourit.db.TagContract.ListEntriesEntry;
import com.spelder.tagyourit.db.TagContract.ListPropertiesEntry;
import com.spelder.tagyourit.db.TagContract.RatingEntry;
import com.spelder.tagyourit.db.TagContract.TagEntry;
import com.spelder.tagyourit.db.TagContract.VideoEntry;
import com.spelder.tagyourit.model.ListIcon;
import com.spelder.tagyourit.model.ListProperties;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.model.TrackComponents;
import com.spelder.tagyourit.model.VideoComponents;
import com.spelder.tagyourit.networking.api.SortBy;
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

  public void updateTagList(Tag tag, List<Long> selectedListIds) {
    Log.d("TagDb", selectedListIds.toString());
    if (selectedListIds.isEmpty()) {
      long tagDbId = getDbIdFromTagId(tag.getId());
      if (tagDbId != -1) {
        deleteListEntriesByTagId(tagDbId);
        deleteTag(tag);
      }
    } else {
      long tagDbId = getDbIdFromTagId(tag.getId());
      if (tagDbId == -1) {
        tagDbId = insertTag(tag);
      }
      deleteListEntriesByTagId(tagDbId);
      for (Long listId : selectedListIds) {
        insertInList(tagDbId, listId);
      }
    }
  }

  public long insertDefaultList(Tag tag) {
    long tagDbId = getDbIdFromTagId(tag.getId());
    if (tagDbId == -1) {
      tagDbId = insertTag(tag);
    }
    insertDefaultList(tagDbId);
    return tagDbId;
  }

  private long insertTag(Tag tag) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(TagEntry.COLUMN_NAME_ID, tag.getId());
    values.put(TagEntry.COLUMN_NAME_TITLE, tag.getTitle());
    values.put(TagEntry.COLUMN_NAME_VERSION, tag.getVersion());
    values.put(TagEntry.COLUMN_NAME_ARRANGER, tag.getArranger());
    values.put(TagEntry.COLUMN_NAME_RATING, tag.getRating());
    values.put(TagEntry.COLUMN_NAME_DOWNLOAD, tag.getDownloadCount());
    values.put(TagEntry.COLUMN_NAME_POSTED, tag.getPostedDate().getTime());
    values.put(TagEntry.COLUMN_NAME_CLASSIC_TAG_NUMBER, tag.getClassicTagNumber());
    values.put(TagEntry.COLUMN_NAME_KEY, tag.getKey());
    values.put(TagEntry.COLUMN_NAME_PARTS_NUMBER, tag.getNumberOfParts());
    values.put(TagEntry.COLUMN_NAME_LYRICS, tag.getLyrics());
    values.put(TagEntry.COLUMN_NAME_TYPE, tag.getType());
    values.put(TagEntry.COLUMN_NAME_COLLECTION, tag.getCollection());
    values.put(TagEntry.COLUMN_NAME_SHEET_MUSIC_TYPE, tag.getSheetMusicType());
    values.put(TagEntry.COLUMN_NAME_SHEET_MUSIC_LINK, tag.getSheetMusicLink());
    values.put(TagEntry.COLUMN_NAME_SHEET_MUSIC_FILE, tag.getSheetMusicFile());
    values.put(TagEntry.COLUMN_NAME_LAST_MODIFIED_DATE, sdf.format(new Date()));

    long newRowId = db.insert(TagEntry.TABLE_NAME, null, values);
    Log.d("TagDb,", "Inserted tagId: " + tag.getId());

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
  }

  public void updateVideos(List<VideoComponents> videos) {
    if (videos.isEmpty()) {
      return;
    }

    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    long dbId = getDbIdFromTagId(videos.get(0).getTagId());
    if (dbId > -1) {
      deleteVideos(dbId, db);

      insertVideos(videos, dbId);
    }
  }

  private void updateTag(Tag tag, SQLiteDatabase db) {
    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put(TagEntry.COLUMN_NAME_TITLE, tag.getTitle());
    values.put(TagEntry.COLUMN_NAME_VERSION, tag.getVersion());
    values.put(TagEntry.COLUMN_NAME_ARRANGER, tag.getArranger());
    values.put(TagEntry.COLUMN_NAME_RATING, tag.getRating());
    values.put(TagEntry.COLUMN_NAME_DOWNLOAD, tag.getDownloadCount());
    values.put(TagEntry.COLUMN_NAME_POSTED, tag.getPostedDate().getTime());
    values.put(TagEntry.COLUMN_NAME_CLASSIC_TAG_NUMBER, tag.getClassicTagNumber());
    values.put(TagEntry.COLUMN_NAME_KEY, tag.getKey());
    values.put(TagEntry.COLUMN_NAME_PARTS_NUMBER, tag.getNumberOfParts());
    values.put(TagEntry.COLUMN_NAME_LYRICS, tag.getLyrics());
    values.put(TagEntry.COLUMN_NAME_TYPE, tag.getType());
    values.put(TagEntry.COLUMN_NAME_COLLECTION, tag.getCollection());
    values.put(TagEntry.COLUMN_NAME_SHEET_MUSIC_TYPE, tag.getSheetMusicType());
    values.put(TagEntry.COLUMN_NAME_SHEET_MUSIC_LINK, tag.getSheetMusicLink());
    values.put(TagEntry.COLUMN_NAME_SHEET_MUSIC_FILE, tag.getSheetMusicFile());
    values.put(TagEntry.COLUMN_NAME_LAST_MODIFIED_DATE, sdf.format(new Date()));
    // Insert the new row, returning the primary key value of the new row
    String strFilter = TagEntry.COLUMN_NAME_ID + "=" + tag.getId();
    db.update(TagEntry.TABLE_NAME, values, strFilter, null);
    Log.d("TagDb,", "Updated tagId: " + tag.getId());

    long dbId = getDbIdFromTagId(tag.getId());
    if (dbId > -1) {
      deleteTracks(dbId, db);
      deleteVideos(dbId, db);

      insertTrack(tag.getTracks(), dbId);
      insertVideos(tag.getVideos(), dbId);
    }
  }

  private void deleteVideos(long dbId, SQLiteDatabase db) {
    // Define 'where' part of query.
    String selection = VideoEntry.COLUMN_NAME_TAG_ID + " LIKE ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs4 = {"" + dbId};
    // Issue SQL statement.
    int deletedRows = db.delete(VideoEntry.TABLE_NAME, selection, selectionArgs4);
    Log.d("VideoDB", "Number of Deleted rows: " + deletedRows);
  }

  private void deleteTracks(long dbId, SQLiteDatabase db) {
    // Define 'where' part of query.
    String selection = LearningTracksEntry.COLUMN_NAME_TAG_ID + " LIKE ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs3 = {"" + dbId};
    // Issue SQL statement.
    int deletedRows = db.delete(LearningTracksEntry.TABLE_NAME, selection, selectionArgs3);
    Log.d("LearningTrackDB", "Number of Deleted rows: " + deletedRows);
  }

  private long getDbIdFromTagId(int tagId) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    String sql =
        "SELECT "
            + TagEntry._ID
            + " FROM "
            + TagEntry.TABLE_NAME
            + " WHERE "
            + TagEntry.COLUMN_NAME_ID
            + " = "
            + tagId;
    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      Log.d("TagDb", "Tag id not found: " + tagId);
      return -1;
    }
    c.moveToFirst();

    long dbId;
    do {
      dbId = c.getLong(c.getColumnIndex(TagEntry._ID));
      Log.d("TagDb", "Found tag id of " + dbId);
    } while (c.moveToNext());
    c.close();
    return dbId;
  }

  public List<Integer> getOutdatedTagIdList(int updatedDays) {
    ArrayList<Integer> outdatedTagIdList = new ArrayList<>();
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    String sql =
        "SELECT "
            + TagEntry.COLUMN_NAME_ID
            + " FROM "
            + TagEntry.TABLE_NAME
            + " WHERE "
            + TagEntry.COLUMN_NAME_LAST_MODIFIED_DATE
            + " IS NULL OR "
            + TagEntry.COLUMN_NAME_LAST_MODIFIED_DATE
            + " <= date('now','-"
            + updatedDays
            + " day')";
    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return outdatedTagIdList;
    }
    c.moveToFirst();

    do {
      outdatedTagIdList.add(c.getInt(c.getColumnIndex(TagEntry.COLUMN_NAME_ID)));
      Log.d("TagDb", "" + c.getInt(c.getColumnIndex(TagEntry.COLUMN_NAME_ID)));
    } while (c.moveToNext());
    c.close();
    return outdatedTagIdList;
  }

  private void insertTrack(Collection<TrackComponents> tracks, long tagId) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    for (TrackComponents track : tracks) {
      ContentValues values = new ContentValues();
      values.put(LearningTracksEntry.COLUMN_NAME_TAG_ID, tagId);
      values.put(LearningTracksEntry.COLUMN_NAME_PART, track.getPart());
      values.put(LearningTracksEntry.COLUMN_NAME_LINK, track.getLink());
      values.put(LearningTracksEntry.COLUMN_NAME_FILE_TYPE, track.getType());
      values.put(LearningTracksEntry.COLUMN_NAME_FILE, "");
      long newRowId = db.insert(LearningTracksEntry.TABLE_NAME, null, values);
      Log.d("TrackDb,", "Inserted trackId: " + newRowId);
    }
  }

  private void insertVideos(Collection<VideoComponents> videos, long tagId) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    for (VideoComponents video : videos) {
      ContentValues values = new ContentValues();
      values.put(VideoEntry.COLUMN_NAME_TAG_ID, tagId);
      values.put(VideoEntry.COLUMN_NAME_TITLE, video.getVideoTitle());
      values.put(VideoEntry.COLUMN_NAME_DESCRIPTION, video.getDescription());
      if (video.getSungKey() != null) {
        values.put(VideoEntry.COLUMN_NAME_KEY, video.getSungKey().name());
      }
      values.put(VideoEntry.COLUMN_NAME_MULTITRACK, video.isMultitrack() ? 1 : 0);
      values.put(VideoEntry.COLUMN_NAME_VIDEO_ID, video.getId());
      values.put(VideoEntry.COLUMN_NAME_VIDEO_CODE, video.getVideoCode());
      values.put(VideoEntry.COLUMN_NAME_SUNG_BY, video.getSungBy());
      values.put(VideoEntry.COLUMN_NAME_SUNG_WEBSITE, video.getSungWebsite());
      values.put(VideoEntry.COLUMN_NAME_POSTED_DATE, video.getPostedDate());
      values.put(
          VideoEntry.COLUMN_NAME_VIEW_COUNT,
          video.getViewCount() != null ? video.getViewCount().toString() : "0");
      values.put(
          VideoEntry.COLUMN_NAME_LIKE_COUNT,
          video.getLikeCount() != null ? video.getLikeCount().toString() : "0");
      values.put(
          VideoEntry.COLUMN_NAME_DISLIKE_COUNT,
          video.getDislikeCount() != null ? video.getDislikeCount().toString() : "0");
      values.put(
          VideoEntry.COLUMN_NAME_FAVORITE_COUNT,
          video.getFavoriteCount() != null ? video.getFavoriteCount().toString() : "0");
      values.put(
          VideoEntry.COLUMN_NAME_COMMENT_COUNT,
          video.getCommentCount() != null ? video.getCommentCount().toString() : "0");
      long newRowId = db.insert(VideoEntry.TABLE_NAME, null, values);
      Log.d("VideoDb,", "Inserted videoId: " + newRowId);
    }
  }

  private void insertDefaultList(long tagDbId) {
    ListProperties defaultList = getDefaultList();
    if (defaultList == null) {
      return;
    }

    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(ListEntriesEntry.COLUMN_NAME_TAG_ID, tagDbId);
    values.put(ListEntriesEntry.COLUMN_NAME_LIST_ID, defaultList.getDbId());
    db.insert(ListEntriesEntry.TABLE_NAME, null, values);
    Log.d("ListEntryDb,", "Inserted in default list tagDbId: " + tagDbId);
  }

  private void insertInList(long tagDbId, long listId) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(ListEntriesEntry.COLUMN_NAME_TAG_ID, tagDbId);
    values.put(ListEntriesEntry.COLUMN_NAME_LIST_ID, listId);
    db.insert(ListEntriesEntry.TABLE_NAME, null, values);
    Log.d("ListEntryDb,", "Inserted tagDbId: " + tagDbId + " into list: " + listId);
  }

  private Long getFavoritesListId() {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    String sql =
        "SELECT "
            + ListPropertiesEntry._ID
            + " FROM "
            + ListPropertiesEntry.TABLE_NAME
            + " WHERE "
            + ListPropertiesEntry.COLUMN_NAME_NAME
            + " = '"
            + ListPropertiesEntry.FAVORITE_NAME
            + "'";

    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return null;
    }
    c.moveToFirst();
    Long id = c.getLong(c.getColumnIndex(ListPropertiesEntry._ID));
    c.close();

    return id;
  }

  public void updateListProperties(ListProperties properties) {
    if (properties.isDefaultList()) {
      setAllListsNotDefault();
    }

    if (properties.getDbId() == null) {
      addListProperties(properties);
      return;
    }

    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    String strFilter = ListPropertiesEntry._ID + "=" + properties.getDbId();
    db.update(ListPropertiesEntry.TABLE_NAME, convertToContentValues(properties), strFilter, null);
    Log.d("TagDb", "Updated listId: " + properties.getDbId());

    setFavoriteListAsDefaultIfNeeded();
  }

  private void addListProperties(ListProperties properties) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    long newRowId =
        db.insert(ListPropertiesEntry.TABLE_NAME, null, convertToContentValues(properties));
    Log.d("TagDb,", "Inserted listId: " + newRowId);
  }

  private void setAllListsNotDefault() {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(ListPropertiesEntry.COLUMN_NAME_DEFAULT_LIST, 0);

    String strFilter = ListPropertiesEntry.COLUMN_NAME_DEFAULT_LIST + "=?";
    db.update(ListPropertiesEntry.TABLE_NAME, values, strFilter, new String[] {"1"});
  }

  private void setFavoriteListAsDefaultIfNeeded() {
    if (getDefaultList() != null) {
      return;
    }

    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(ListPropertiesEntry.COLUMN_NAME_DEFAULT_LIST, 1);

    Long id = getFavoritesListId();
    if (id != null) {
      String strFilter = ListPropertiesEntry._ID + "=?";
      db.update(
          ListPropertiesEntry.TABLE_NAME, values, strFilter, new String[] {Long.toString(id)});
    }
  }

  private ContentValues convertToContentValues(ListProperties properties) {
    ContentValues values = new ContentValues();
    values.put(ListPropertiesEntry.COLUMN_NAME_NAME, properties.getName());
    values.put(ListPropertiesEntry.COLUMN_NAME_USER_CREATED, properties.isUserCreated() ? 1 : 0);
    values.put(
        ListPropertiesEntry.COLUMN_NAME_DOWNLOAD_SHEET, properties.isDownloadSheet() ? 1 : 0);
    values.put(
        ListPropertiesEntry.COLUMN_NAME_DOWNLOAD_TRACK, properties.isDownloadTrack() ? 1 : 0);
    values.put(ListPropertiesEntry.COLUMN_NAME_DEFAULT_LIST, properties.isDefaultList() ? 1 : 0);
    values.put(ListPropertiesEntry.COLUMN_NAME_ICON, properties.getIcon().getDbId());
    values.put(ListPropertiesEntry.COLUMN_NAME_COLOR, properties.getColor());

    return values;
  }

  public List<ListProperties> getListProperties() {
    ArrayList<ListProperties> listProperties = new ArrayList<>();
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    String sql = "SELECT * FROM " + ListPropertiesEntry.TABLE_NAME;

    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return listProperties;
    }
    c.moveToFirst();

    do {
      ListProperties properties = new ListProperties();
      properties.setDbId(c.getLong(c.getColumnIndex(ListPropertiesEntry._ID)));
      properties.setName(c.getString(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_NAME)));
      properties.setUserCreated(
          c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_USER_CREATED)) == 1);
      properties.setDownloadSheet(
          c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_DOWNLOAD_SHEET)) == 1);
      properties.setDownloadTrack(
          c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_DOWNLOAD_TRACK)) == 1);
      properties.setDefaultList(
          c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_DEFAULT_LIST)) == 1);
      properties.setIcon(
          ListIcon.fromDbId(c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_ICON))));
      properties.setColor(c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_COLOR)));
      properties.setListSize(getListSize(properties.getDbId()));

      listProperties.add(properties);
      Log.d("TagDb", "" + c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_NAME)));
    } while (c.moveToNext());

    c.close();
    return listProperties;
  }

  public ListProperties getListProperties(long dbId) {
    String sql =
        "SELECT * FROM "
            + ListPropertiesEntry.TABLE_NAME
            + " WHERE "
            + ListPropertiesEntry._ID
            + "="
            + dbId;

    return getListProperties(sql);
  }

  private ListProperties getListProperties(String sql) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return null;
    }
    c.moveToFirst();

    ListProperties properties = new ListProperties();
    properties.setDbId(c.getLong(c.getColumnIndex(ListPropertiesEntry._ID)));
    properties.setName(c.getString(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_NAME)));
    properties.setUserCreated(
        c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_USER_CREATED)) == 1);
    properties.setDownloadSheet(
        c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_DOWNLOAD_SHEET)) == 1);
    properties.setDownloadTrack(
        c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_DOWNLOAD_TRACK)) == 1);
    properties.setDefaultList(
        c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_DEFAULT_LIST)) == 1);
    properties.setIcon(
        ListIcon.fromDbId(c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_ICON))));
    properties.setColor(c.getInt(c.getColumnIndex(ListPropertiesEntry.COLUMN_NAME_COLOR)));
    properties.setListSize(getListSize(properties.getDbId()));

    c.close();
    return properties;
  }

  private int getListSize(long listId) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    String sql =
        "SELECT COUNT(*) AS tagCount FROM "
            + ListEntriesEntry.TABLE_NAME
            + " WHERE "
            + ListEntriesEntry.COLUMN_NAME_LIST_ID
            + " = "
            + listId;
    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return 0;
    }
    c.moveToFirst();
    int i = c.getInt(0);
    c.close();

    return i;
  }

  public void deleteList(ListProperties listProperties) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    deleteListEntriesByListId(listProperties.getDbId(), db);

    // Define 'where' part of query.
    String selection = ListPropertiesEntry._ID + " LIKE ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs4 = {"" + listProperties.getDbId()};
    // Issue SQL statement.
    int deletedRows = db.delete(ListPropertiesEntry.TABLE_NAME, selection, selectionArgs4);
    Log.d("ListPropertiesDB", "Number of Deleted rows: " + deletedRows);

    setFavoriteListAsDefaultIfNeeded();
  }

  private void deleteListEntriesByListId(long listDbId, SQLiteDatabase db) {
    // Define 'where' part of query.
    String selection = ListEntriesEntry.COLUMN_NAME_LIST_ID + " LIKE ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs4 = {"" + listDbId};
    // Issue SQL statement.
    int deletedRows = db.delete(ListEntriesEntry.TABLE_NAME, selection, selectionArgs4);
    Log.d("ListEntriesDB", "Number of Deleted rows: " + deletedRows);
  }

  private void deleteListEntriesByTagId(long tagDbId) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    // Define 'where' part of query.
    String selection = ListEntriesEntry.COLUMN_NAME_TAG_ID + " LIKE ?";
    // Specify arguments in placeholder order.
    String[] selectionArgs4 = {"" + tagDbId};
    // Issue SQL statement.
    int deletedRows = db.delete(ListEntriesEntry.TABLE_NAME, selection, selectionArgs4);
    Log.d("ListEntriesDB", "Number of Deleted rows: " + deletedRows);
  }

  public void deleteFromDefaultList(Tag tag) {
    Log.d(
        "ListEntryDB",
        "Deleting from default list with title: "
            + tag.getTitle()
            + " and DB ID: "
            + tag.getDbId());

    ListProperties defaultList = getDefaultList();
    if (defaultList == null) {
      return;
    }

    // Gets the data repository in write mode
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    // Define 'where' part of query.
    String selection =
        ListEntriesEntry.COLUMN_NAME_TAG_ID
            + " LIKE ? AND "
            + ListEntriesEntry.COLUMN_NAME_LIST_ID
            + " = "
            + defaultList.getDbId();
    // Specify arguments in placeholder order.
    String[] selectionArgs = {"" + tag.getDbId()};
    // Issue SQL statement.
    int deletedRows = db.delete(ListEntriesEntry.TABLE_NAME, selection, selectionArgs);
    Log.d("ListEntryDB", "Number of Deleted rows: " + deletedRows);

    if (getListsForTag(tag).isEmpty()) {
      deleteTag(tag);
    }
  }

  private void deleteTag(Tag tag) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();

    deleteTracks(tag.getDbId(), db);
    deleteVideos(tag.getDbId(), db);

    String selection = TagEntry._ID + " LIKE ?";
    String[] selectionArgs = {tag.getDbId().toString()};
    int deletedRows = db.delete(TagEntry.TABLE_NAME, selection, selectionArgs);
    Log.d("TagDB", "Number of Deleted rows: " + deletedRows);
  }

  public List<Long> getListsForTag(Tag tag) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    String sql =
        "SELECT "
            + ListEntriesEntry.COLUMN_NAME_LIST_ID
            + " FROM "
            + TagEntry.TABLE_NAME
            + ", "
            + ListEntriesEntry.TABLE_NAME
            + " WHERE "
            + TagEntry.TABLE_NAME
            + "."
            + TagEntry._ID
            + " = "
            + ListEntriesEntry.TABLE_NAME
            + "."
            + ListEntriesEntry.COLUMN_NAME_TAG_ID
            + " AND "
            + TagEntry.TABLE_NAME
            + "."
            + TagEntry.COLUMN_NAME_ID
            + " = "
            + tag.getId();

    Cursor c = db.rawQuery(sql, new String[] {});

    List<Long> dbIds = new ArrayList<>();
    if (c.getCount() == 0) {
      return dbIds;
    }

    c.moveToFirst();
    do {
      dbIds.add(c.getLong(c.getColumnIndex(ListEntriesEntry.COLUMN_NAME_LIST_ID)));
    } while (c.moveToNext());

    c.close();
    return dbIds;
  }

  public Long isInDefaultList(Tag tag) {
    ListProperties defaultList = getDefaultList();
    if (defaultList == null) {
      return null;
    }

    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    String sql =
        "SELECT * FROM "
            + TagEntry.TABLE_NAME
            + ", "
            + ListEntriesEntry.TABLE_NAME
            + " WHERE "
            + TagEntry.TABLE_NAME
            + "."
            + TagEntry._ID
            + " = "
            + ListEntriesEntry.TABLE_NAME
            + "."
            + ListEntriesEntry.COLUMN_NAME_TAG_ID
            + " AND "
            + TagEntry.TABLE_NAME
            + "."
            + TagEntry.COLUMN_NAME_ID
            + " = "
            + tag.getId()
            + " AND "
            + ListEntriesEntry.COLUMN_NAME_LIST_ID
            + " = "
            + defaultList.getDbId();
    Cursor c = db.rawQuery(sql, new String[] {});
    Long dbId = null;
    if (c.getCount() > 0) {
      c.moveToFirst();
      dbId = c.getLong(c.getColumnIndex(TagEntry._ID));
    }

    c.close();
    return dbId;
  }

  public void deleteAllFavorites() {
    List<Tag> tags = getTagsForList(getFavoritesListId());
    for (Tag tag : tags) {
      deleteFromDefaultList(tag);
    }
  }

  public List<Tag> getTagsForList(Long listDbId) {
    String sql =
        "SELECT * FROM "
            + TagEntry.TABLE_NAME
            + ", "
            + ListEntriesEntry.TABLE_NAME
            + " WHERE "
            + TagEntry.TABLE_NAME
            + "."
            + TagEntry._ID
            + " = "
            + ListEntriesEntry.TABLE_NAME
            + "."
            + ListEntriesEntry.COLUMN_NAME_TAG_ID
            + " AND "
            + ListEntriesEntry.COLUMN_NAME_LIST_ID
            + " = "
            + listDbId
            + " ORDER BY "
            + TagEntry.COLUMN_NAME_TITLE;
    return getTagsForList(sql);
  }

  public List<Tag> getTagsForList(FilterBy filter, SortBy sortBy, Long listDbId) {
    Log.d("TagDb", filter.getDbFilter());

    String sql =
        "SELECT * FROM "
            + TagEntry.TABLE_NAME
            + ", "
            + ListEntriesEntry.TABLE_NAME
            + " WHERE "
            + TagEntry.TABLE_NAME
            + "."
            + TagEntry._ID
            + " = "
            + ListEntriesEntry.TABLE_NAME
            + "."
            + ListEntriesEntry.COLUMN_NAME_TAG_ID
            + " AND "
            + ListEntriesEntry.COLUMN_NAME_LIST_ID
            + " = "
            + listDbId
            + filter.getDbFilter()
            + " ORDER BY "
            + getSortColumnName(sortBy);

    return getTagsForList(sql);
  }

  private List<Tag> getTagsForList(String sql) {
    ArrayList<Tag> tags = new ArrayList<>();
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return tags;
    }
    c.moveToFirst();

    do {
      Tag tag = new Tag();
      tag.setDbId(c.getLong(c.getColumnIndex(TagEntry._ID)));
      tag.setId(c.getInt(c.getColumnIndex(TagEntry.COLUMN_NAME_ID)));
      tag.setTitle(c.getString(c.getColumnIndex(TagEntry.COLUMN_NAME_TITLE)));
      tag.setVersion(c.getString(c.getColumnIndex(TagEntry.COLUMN_NAME_VERSION)));
      tag.setArranger(c.getString(c.getColumnIndex(TagEntry.COLUMN_NAME_ARRANGER)));
      tag.setRating(c.getString(c.getColumnIndex(TagEntry.COLUMN_NAME_RATING)));
      tag.setClassicTagNumber(c.getInt(c.getColumnIndex(TagEntry.COLUMN_NAME_CLASSIC_TAG_NUMBER)));
      tag.setDownloadCount(c.getInt(c.getColumnIndex(TagEntry.COLUMN_NAME_DOWNLOAD)));
      tag.setPostedDate(c.getLong(c.getColumnIndex(TagEntry.COLUMN_NAME_POSTED)));
      tag.setKey(c.getString(c.getColumnIndex(TagEntry.COLUMN_NAME_KEY)));
      tag.setNumberOfParts(c.getInt(c.getColumnIndex(TagEntry.COLUMN_NAME_PARTS_NUMBER)));
      tag.setLyrics(c.getString(c.getColumnIndex(TagEntry.COLUMN_NAME_LYRICS)));
      tag.setType(c.getString(c.getColumnIndex(TagEntry.COLUMN_NAME_TYPE)));
      tag.setCollection(c.getString(c.getColumnIndex(TagEntry.COLUMN_NAME_COLLECTION)));
      tag.setSheetMusicType(c.getString(c.getColumnIndex(TagEntry.COLUMN_NAME_SHEET_MUSIC_TYPE)));
      tag.setSheetMusicLink(c.getString(c.getColumnIndex(TagEntry.COLUMN_NAME_SHEET_MUSIC_LINK)));
      tag.setSheetMusicFile(c.getString(c.getColumnIndex(TagEntry.COLUMN_NAME_SHEET_MUSIC_FILE)));

      ListProperties list =
          getListProperties(c.getInt(c.getColumnIndex(ListEntriesEntry.COLUMN_NAME_LIST_ID)));
      tag.setDownloaded(list.isDownloadSheet());

      addTracks(tag, db, list.isDownloadTrack());
      addVideos(tag, db);

      tags.add(tag);
      Log.d("TagDb", "Got tag from list: " + tag.getId());
    } while (c.moveToNext());

    c.close();
    return tags;
  }

  public ListProperties getDefaultList() {
    String sql =
        "SELECT * FROM "
            + ListPropertiesEntry.TABLE_NAME
            + " WHERE "
            + ListPropertiesEntry.COLUMN_NAME_DEFAULT_LIST
            + "=1";

    return getListProperties(sql);
  }

  private String getSortColumnName(SortBy sortBy) {
    switch (sortBy) {
      case RATING:
        return TagEntry.COLUMN_NAME_RATING + " " + sortBy.getOrder().getLabel();
      case DOWNLOAD:
        return TagEntry.COLUMN_NAME_DOWNLOAD + " " + sortBy.getOrder().getLabel();
      case LATEST:
        return TagEntry.COLUMN_NAME_POSTED + " " + sortBy.getOrder().getLabel();
      case TITLE:
      default:
        return TagEntry.COLUMN_NAME_TITLE + " " + sortBy.getOrder().getLabel();
    }
  }

  private void addTracks(Tag tag, SQLiteDatabase db, boolean isDownloaded) {
    String sql =
        "SELECT * FROM "
            + LearningTracksEntry.TABLE_NAME
            + " WHERE "
            + LearningTracksEntry.TABLE_NAME
            + "."
            + LearningTracksEntry.COLUMN_NAME_TAG_ID
            + " = "
            + tag.getDbId();
    Cursor c = db.rawQuery(sql, new String[] {});
    if (c.getCount() == 0) {
      return;
    }
    c.moveToFirst();
    do {
      String type = c.getString(c.getColumnIndex(LearningTracksEntry.COLUMN_NAME_FILE_TYPE));
      String part = c.getString(c.getColumnIndex(LearningTracksEntry.COLUMN_NAME_PART));
      String link = c.getString(c.getColumnIndex(LearningTracksEntry.COLUMN_NAME_LINK));
      tag.addTrack(part, link, type, isDownloaded);
    } while (c.moveToNext());

    c.close();
  }

  private void addVideos(Tag tag, SQLiteDatabase db) {
    String sql =
        "SELECT * FROM "
            + VideoEntry.TABLE_NAME
            + " WHERE "
            + VideoEntry.TABLE_NAME
            + "."
            + VideoEntry.COLUMN_NAME_TAG_ID
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
      video.setId(c.getInt(c.getColumnIndex(VideoEntry.COLUMN_NAME_VIDEO_ID)));
      video.setMultitrack(c.getInt(c.getColumnIndex(VideoEntry.COLUMN_NAME_MULTITRACK)) != 0);
      String sungKey = c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_KEY));
      if (sungKey != null && !sungKey.isEmpty()) {
        video.setSungKey(Pitch.valueOf(sungKey));
      }
      video.setSungWebsite(c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_SUNG_WEBSITE)));
      video.setPostedDate(c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_POSTED_DATE)));
      video.setDescription(c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_DESCRIPTION)));
      video.setVideoTitle(c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_TITLE)));
      video.setVideoCode(c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_VIDEO_CODE)));
      video.setSungBy(c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_SUNG_BY)));
      video.setViewCount(
          new BigInteger(c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_VIEW_COUNT))));
      video.setLikeCount(
          new BigInteger(c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_LIKE_COUNT))));
      video.setDislikeCount(
          new BigInteger(c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_DISLIKE_COUNT))));
      video.setFavoriteCount(
          new BigInteger(c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_FAVORITE_COUNT))));
      video.setCommentCount(
          new BigInteger(c.getString(c.getColumnIndex(VideoEntry.COLUMN_NAME_COMMENT_COUNT))));

      tag.addVideo(video);
    } while (c.moveToNext());

    c.close();
  }

  public boolean hasEntriesInDefaultList() {
    ListProperties defaultList = getDefaultList();
    if (defaultList == null) {
      return false;
    }

    return getListSize(defaultList.getDbId()) > 0;
  }

  public void insertUserRating(long tagId, double rating) {
    // Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();
    values.put(RatingEntry.COLUMN_NAME_TAG_ID, tagId);
    values.put(RatingEntry.COLUMN_NAME_TAG_RATING, rating);

    if (getUserRating(tagId) < 0) {
      SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
      // Insert the new row, returning the primary key value of the new row
      long newRowId = db.insert(RatingEntry.TABLE_NAME, null, values);
      Log.d("TagDb,", "Inserted ratingId: " + newRowId);
    } else {
      SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
      String[] args = new String[] {"" + tagId};
      long newRowId =
          db.update(RatingEntry.TABLE_NAME, values, RatingEntry.COLUMN_NAME_TAG_ID + "=?", args);
      Log.d("TagDb,", "Updated ratingId: " + newRowId);
    }
  }

  public double getUserRating(long tagId) {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    String sql =
        "SELECT * FROM "
            + RatingEntry.TABLE_NAME
            + " WHERE "
            + RatingEntry.TABLE_NAME
            + "."
            + RatingEntry.COLUMN_NAME_TAG_ID
            + " = "
            + tagId;
    Cursor c = db.rawQuery(sql, new String[] {});
    double rating = -1.0;
    if (c.getCount() > 0) {
      c.moveToFirst();
      rating =
          Double.parseDouble(c.getString(c.getColumnIndex(RatingEntry.COLUMN_NAME_TAG_RATING)));
    }
    c.close();
    return rating;
  }

  public void close() {
    SQLiteDatabase db = TagDbHelper.getInstance(context).getWritableDatabase();
    db.close();
  }
}
