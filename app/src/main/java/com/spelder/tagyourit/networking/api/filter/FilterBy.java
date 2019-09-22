package com.spelder.tagyourit.networking.api.filter;

import androidx.annotation.NonNull;
import com.spelder.tagyourit.db.TagContract;

public class FilterBy {
  private static final String SHEET_MUSIC_LABEL = "SheetMusic=";
  private static final String LEARNING_TRACK_LABEL = "Learning=";

  private boolean hasSheetMusic = false;
  private boolean hasLearningTrack = false;
  private Part numberOfParts = Part.ANY;
  private Rating minimumRating = Rating.ANY;
  private Type type = Type.ANY;
  private Key key = Key.ANY;

  void setHasSheetMusic(boolean hasSheetMusic) {
    this.hasSheetMusic = hasSheetMusic;
  }

  public boolean isSheetMusicApplied() {
    return hasSheetMusic;
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

  public boolean isLearningTrackApplied() {
    return hasLearningTrack;
  }

  void setNumberOfParts(Part numberOfParts) {
    if (numberOfParts != null) {
      this.numberOfParts = numberOfParts;
    }
  }

  public String getNumberOfPartsDisplayName() {
    return numberOfParts.getDisplayName();
  }

  public boolean isNumberOfPartsApplied() {
    return numberOfParts != Part.ANY;
  }

  private String getNumberOfPartsFilter() {
    if (numberOfParts != Part.ANY) {
      return "&" + numberOfParts.getFilter();
    }
    return "";
  }

  private String getNumberOfPartsDbFilter() {
    if (numberOfParts != Part.ANY) {
      return " AND "
          + TagContract.TagEntry.TABLE_NAME
          + "."
          + TagContract.TagEntry.COLUMN_NAME_PARTS_NUMBER
          + " = "
          + numberOfParts.getNumberParts();
    }
    return "";
  }

  void setMinimumRating(Rating minimumRating) {
    if (minimumRating != null) {
      this.minimumRating = minimumRating;
    }
  }

  public String getMinimumRatingDisplayName() {
    return minimumRating.getDisplayName();
  }

  public boolean isMinimumRatingApplied() {
    return minimumRating != Rating.ANY;
  }

  private String getMinimumRatingFilter() {
    if (minimumRating != Rating.ANY) {
      return "&" + minimumRating.getFilter();
    }
    return "";
  }

  private String getMinimumRatingDbFilter() {
    if (minimumRating != Rating.ANY) {
      return " AND "
          + TagContract.TagEntry.TABLE_NAME
          + "."
          + TagContract.TagEntry.COLUMN_NAME_RATING
          + " > "
          + minimumRating.getRating();
    }
    return "";
  }

  void setType(Type type) {
    if (type != null) {
      this.type = type;
    }
  }

  public String getTypeDisplayName() {
    return type.getDisplayName();
  }

  public boolean isTypeApplied() {
    return type != Type.ANY;
  }

  private String getTypeFilter() {
    if (type != Type.ANY) {
      return "&" + type.getFilter();
    }
    return "";
  }

  private String getTypeDbFilter() {
    if (type != Type.ANY) {
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
    if (key != null) {
      this.key = key;
    }
  }

  public String getKeyDisplayName() {
    return key.getDisplayName();
  }

  public boolean isKeyApplied() {
    return key != Key.ANY;
  }

  private String getKeyFilter() {
    if (key != Key.ANY) {
      return "&" + key.getFilter();
    }
    return "";
  }

  private String getKeyDbFilter() {
    if (key != Key.ANY) {
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
