package com.spelder.tagyourit.networking.api.filter;

public enum Type {
  ANY("any", "Any", "Type"),
  BARBERSHOP("bbs", "Barbershop", "Barbershop"),
  SWEET_ADELINES("sai", "Female Barbershop", "Sweet Adelines"),
  SATB("satb", "SATB", "SATB"),
  OTHER_MALE("male", "Other male", "Other male"),
  OTHER_FEMALE("female", "Other female", "Other female"),
  OTHER_MIXED("mixed", "Other mixed", "Other mixed");

  private static final String TYPE_LABEL = "Type=";

  private final String key;
  private final String dbKey;
  private final String displayName;

  Type(String key, String dbKey, String displayName) {
    this.key = key;
    this.dbKey = dbKey;
    this.displayName = displayName;
  }

  public String getFilter() {
    return TYPE_LABEL + key;
  }

  public String getDbFilter() {
    return dbKey;
  }

  public String getDisplayName() {
    return displayName;
  }
}
