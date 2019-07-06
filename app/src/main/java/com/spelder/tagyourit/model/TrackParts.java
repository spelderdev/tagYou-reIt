package com.spelder.tagyourit.model;

public enum TrackParts {
  ALL("AllParts"),
  BASS("Bass"),
  BARI("Bari"),
  LEAD("Lead"),
  TENOR("Tenor"),
  OTHER1("Other1"),
  OTHER2("Other2"),
  OTHER3("Other3"),
  OTHER4("Other4");

  private final String key;

  TrackParts(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
