package com.spelder.tagyourit.networking.api.filter;

public enum Rating {
  ANY(-1, "Rating"),
  ZERO(0, "Rating: 0"),
  ZERO_5(0.5, "Rating: 0.5"),
  ONE(1, "Rating: 1"),
  ONE_5(1.5, "Rating: 1.5"),
  TWO(2, "Rating: 2"),
  TWO_5(2.5, "Rating: 2.5"),
  THREE(3, "Rating: 3"),
  THREE_5(3.5, "Rating: 3.5"),
  FOUR(4, "Rating: 4"),
  FOUR_5(4.5, "Rating: 4.5");

  private static final String RATING_LABEL = "MinRating=";
  private final double rating;
  private final String displayName;

  Rating(double rating, String displayName) {
    this.rating = rating;
    this.displayName = displayName;
  }

  public double getRating() {
    return rating;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getFilter() {
    return RATING_LABEL + getRating();
  }
}
