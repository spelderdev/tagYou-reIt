package com.spelder.tagyourit.networking.api.filter;

public enum Part {
  ANY(-1, "Part"),
  FOUR(4, "Part: 4"),
  FIVE(5, "Part: 5"),
  SIX(6, "Part: 6"),
  SEVEN(7, "Part: 7"),
  EIGHT(8, "Part: 8");

  private static final String PARTS_LABEL = "Parts=";
  private final int numberParts;
  private final String displayName;

  Part(int numberParts, String displayName) {
    this.numberParts = numberParts;
    this.displayName = displayName;
  }

  public int getNumberParts() {
    return numberParts;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getFilter() {
    return PARTS_LABEL + getNumberParts();
  }
}
