package com.spelder.tagyourit.networking.api.filter;

public enum Type {
  ANY("any", "Any"),
  BARBERSHOP("bbs", "Barbershop"),
  SWEET_ADELINES("sai", "Female Barbershop"),
  SATB("satb", "SATB"),
  OTHER_MALE("male", "Other male"),
  OTHER_FEMALE("female", "Other female"),
  OTHER_MIXED("mixed", "Other mixed");

  private static final String TYPE_LABEL = "Type=";

  private final String key;

  private final String dbKey;

  Type(String key, String dbKey) {
    this.key = key;
    this.dbKey = dbKey;
  }

  public String getFilter() {
    return TYPE_LABEL + key;
  }

  public String getDbFilter() {
    return dbKey;
  }
}
