package com.spelder.tagyourit.model;

import com.spelder.tagyourit.R;

public enum ListIcon {
  DEFAULT(R.drawable.list_transparent, 0),
  FAVORITE(R.drawable.favorite_transparent, 1),
  STAR(R.drawable.star_transparent, 2),
  KEY(R.drawable.key_transparent, 3),
  RECENT(R.drawable.clock_transparent, 4),
  DOWNLOAD(R.drawable.download_transparent, 5),
  PLAY(R.drawable.play_transparent, 6),
  NOTE(R.drawable.note_transparent, 7),
  TEACH(R.drawable.teach_transparent, 8);

  private int dbId;
  private int resourceId;

  private ListIcon(int resourceId, int dbId) {
    this.dbId = dbId;
    this.resourceId = resourceId;
  }

  public int getDbId() {
    return dbId;
  }

  public int getResourceId() {
    return resourceId;
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
