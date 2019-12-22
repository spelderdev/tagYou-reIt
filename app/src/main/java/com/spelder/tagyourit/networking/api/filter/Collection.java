package com.spelder.tagyourit.networking.api.filter;

public enum Collection {
  ANY("any", "Any", "Collection"),
  CLASSIC("classic", "classic", "Classic"),
  EASY("easytags", "easytags", "Easy"),
  ONE_HUNDRED("100", "100", "100 Days");

  private static final String PARTS_LABEL = "Collection=";

  private final String key;
  private final String dbKey;
  private final String displayName;

  Collection(String key, String dbKey, String displayName) {
    this.key = key;
    this.dbKey = dbKey;
    this.displayName = displayName;
  }

  public String getFilter() {
    return PARTS_LABEL + key;
  }

  public String getDbFilter() {
    return dbKey;
  }

  public String getDisplayName() {
    return displayName;
  }
}
