package com.spelder.tagyourit.networking.api;

public enum SortOrder {
  ASCENDING("ASC"),
  DESCENDING("DESC");
  private final String label;

  SortOrder(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
