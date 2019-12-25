package com.spelder.tagyourit.model;

import com.spelder.tagyourit.R;

public enum ListIcon {
  DEFAULT(R.drawable.clock, 0),
  FAVORITE(R.drawable.favorite, 1);

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
