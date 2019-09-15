package com.spelder.tagyourit.networking.api.filter;

public enum PartFilter {
  ANY(-1),
  FOUR(4),
  FIVE(5),
  SIX(6),
  SEVEN(7),
  EIGHT(8);

  private int numberParts;

  private PartFilter(int numberParts) {
    this.numberParts = numberParts;
  }

  public int getNumberParts() {
    return numberParts;
  }
}
