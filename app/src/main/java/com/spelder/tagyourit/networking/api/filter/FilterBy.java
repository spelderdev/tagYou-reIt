package com.spelder.tagyourit.networking.api.filter;

import androidx.annotation.NonNull;
import com.spelder.tagyourit.db.TagContract;

public class FilterBy {
  private static final String SHEET_MUSIC_LABEL = "SheetMusic=";

  private static final String LEARNING_TRACK_LABEL = "Learning=";

  private static final String PARTS_LABEL = "Parts=";

  private static final String RATING_LABEL = "MinRating=";

  private boolean hasSheetMusic;

  private boolean hasLearningTrack;

  private Integer numberOfParts = null;

  private Double minimumRating = null;

  private Type type = null;

  private Key key = null;

  void setHasSheetMusic(boolean hasSheetMusic) {
    this.hasSheetMusic = hasSheetMusic;
  }

  private String getSheetMusicFilter() {
    if (hasSheetMusic) {
      return "&" + SHEET_MUSIC_LABEL + "Yes";
    }
    return "";
  }

  private String getSheetMusicDbFilter() {
    if (hasSheetMusic) {
      return " AND "
          + TagContract.TagEntry.TABLE_NAME
          + "."
          + TagContract.TagEntry.COLUMN_NAME_SHEET_MUSIC_LINK
          + " != ''";
    }
    return "";
  }

  private String getLearningTrackFilter() {
    if (hasLearningTrack) {
      return "&" + LEARNING_TRACK_LABEL + "Yes";
    }
    return "";
  }

  private String getLearningTrackDbFilter() {
    if (hasLearningTrack) {
      return " AND "
          + TagContract.TagEntry.TABLE_NAME
          + "."
          + TagContract.TagEntry._ID
          + " IN (SELECT "
          + TagContract.LearningTracksEntry.TABLE_NAME
          + "."
          + TagContract.LearningTracksEntry.COLUMN_NAME_TAG_ID
          + " FROM "
          + TagContract.LearningTracksEntry.TABLE_NAME
          + ")";
    }
    return "";
  }

  void setHasLearningTrack(boolean hasLearningTrack) {
    this.hasLearningTrack = hasLearningTrack;
  }

  void setNumberOfParts(Integer numberOfParts) {
    this.numberOfParts = numberOfParts;
  }

  private String getNumberOfPartsFilter() {
    if (numberOfParts != null) {
      return "&" + PARTS_LABEL + numberOfParts;
    }
    return "";
  }

  private String getNumberOfPartsDbFilter() {
    if (numberOfParts != null) {
      return " AND "
          + TagContract.TagEntry.TABLE_NAME
          + "."
          + TagContract.TagEntry.COLUMN_NAME_PARTS_NUMBER
          + " = "
          + numberOfParts;
    }
    return "";
  }

  void setMinimumRating(Double minimumRating) {
    this.minimumRating = minimumRating;
  }

  private String getMinimumRatingFilter() {
    if (minimumRating != null) {
      return "&" + RATING_LABEL + minimumRating;
    }
    return "";
  }

  private String getMinimumRatingDbFilter() {
    if (minimumRating != null) {
      return " AND "
          + TagContract.TagEntry.TABLE_NAME
          + "."
          + TagContract.TagEntry.COLUMN_NAME_RATING
          + " > "
          + minimumRating;
    }
    return "";
  }

  void setType(Type type) {
    this.type = type;
  }

  private String getTypeFilter() {
    if (type != null) {
      return "&" + type.getFilter();
    }
    return "";
  }

  private String getTypeDbFilter() {
    if (type != null) {
      return " AND "
          + TagContract.TagEntry.TABLE_NAME
          + "."
          + TagContract.TagEntry.COLUMN_NAME_TYPE
          + " LIKE '"
          + type.getDbFilter()
          + "'";
    }
    return "";
  }

  void setKey(Key key) {
    this.key = key;
  }

  private String getKeyFilter() {
    if (key != null) {
      return "&" + key.getFilter();
    }
    return "";
  }

  private String getKeyDbFilter() {
    if (key != null) {
      StringBuilder filter = new StringBuilder(" AND ( ");
      String[] dbKeys = key.getDbKeys();
      for (int i = 0; i < dbKeys.length; i++) {
        if (i != 0) {
          filter.append(" OR ");
        }
        filter.append(TagContract.TagEntry.TABLE_NAME);
        filter.append(".");
        filter.append(TagContract.TagEntry.COLUMN_NAME_KEY);
        filter.append(" LIKE '%");
        filter.append(dbKeys[i]);
        filter.append("'");
      }
      filter.append(")");

      return filter.toString();
    }
    return "";
  }

  public String getFilter() {
    return getSheetMusicFilter()
        + getLearningTrackFilter()
        + getNumberOfPartsFilter()
        + getMinimumRatingFilter()
        + getTypeFilter()
        + getKeyFilter();
  }

  public String getDbFilter() {
    return getNumberOfPartsDbFilter()
        + getMinimumRatingDbFilter()
        + getSheetMusicDbFilter()
        + getKeyDbFilter()
        + getLearningTrackDbFilter()
        + getTypeDbFilter();
  }

  @Override
  @NonNull
  public String toString() {
    return "FilterBy{"
        + "hasSheetMusic="
        + hasSheetMusic
        + ", hasLearningTrack="
        + hasLearningTrack
        + ", numberOfParts="
        + numberOfParts
        + ", minimumRating="
        + minimumRating
        + ", type="
        + type
        + ", key="
        + key
        + '}';
  }
}
