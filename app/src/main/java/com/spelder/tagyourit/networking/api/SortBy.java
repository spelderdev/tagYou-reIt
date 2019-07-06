package com.spelder.tagyourit.networking.api;

import android.os.Parcel;
import android.os.Parcelable;

public enum SortBy implements Parcelable {
  LATEST("Posted"),
  RATING("Rating"),
  DOWNLOAD("Downloaded"),
  TITLE("Title"),
  CLASSIC("Collection=classic&Sortby=Classic"),
  EASY("Collection=easytags");

  public static final Creator<SortBy> CREATOR =
      new Creator<SortBy>() {
        @Override
        public SortBy createFromParcel(Parcel in) {
          return SortBy.values()[in.readInt()];
        }

        @Override
        public SortBy[] newArray(int size) {
          return new SortBy[size];
        }
      };

  private static final String SORT_BY_LABEL = "Sortby=";

  private final String label;

  SortBy(String label) {
    this.label = label;
  }

  public String getSortBy() {
    return SORT_BY_LABEL + label;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(ordinal());
  }

  @Override
  public int describeContents() {
    return 0;
  }
}
