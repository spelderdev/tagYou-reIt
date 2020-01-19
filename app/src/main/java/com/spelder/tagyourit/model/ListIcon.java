package com.spelder.tagyourit.model;

import com.spelder.tagyourit.R;

public enum ListIcon {
  DEFAULT(0, R.drawable.list_transparent, R.drawable.list_filled, R.drawable.list_outline),
  FAVORITE(
      1, R.drawable.favorite_transparent, R.drawable.favorite_filled, R.drawable.favorite_outline),
  STAR(2, R.drawable.star_transparent, R.drawable.star_filled, R.drawable.star_outline),
  KEY(3, R.drawable.key_transparent, R.drawable.key_filled, R.drawable.key_outline),
  RECENT(4, R.drawable.clock_transparent, R.drawable.clock_filled, R.drawable.clock_outline),
  DOWNLOAD(
      5, R.drawable.download_transparent, R.drawable.download_filled, R.drawable.download_outline),
  PLAY(6, R.drawable.play_transparent, R.drawable.play_filled, R.drawable.play_outline),
  NOTE(7, R.drawable.note_transparent, R.drawable.note_filled, R.drawable.note_outline),
  TEACH(8, R.drawable.school_transparent, R.drawable.school_filled, R.drawable.school_outline);

  private final int dbId;
  private final int mainResourceId;
  private final int filledResourceId;
  private final int outlineResourceId;

  ListIcon(int dbId, int mainResourceId, int filledResourceId, int outlineResourceId) {
    this.dbId = dbId;
    this.mainResourceId = mainResourceId;
    this.filledResourceId = filledResourceId;
    this.outlineResourceId = outlineResourceId;
  }

  public int getDbId() {
    return dbId;
  }

  public int getMainResourceId() {
    return mainResourceId;
  }

  public int getFilledResourceId() {
    return filledResourceId;
  }

  public int getOutlineResourceId() {
    return outlineResourceId;
  }

  public static ListIcon fromDbId(int dbId) {
    for (ListIcon icon : ListIcon.values()) {
      if (icon.getDbId() == dbId) {
        return icon;
      }
    }

    return DEFAULT;
  }
}
