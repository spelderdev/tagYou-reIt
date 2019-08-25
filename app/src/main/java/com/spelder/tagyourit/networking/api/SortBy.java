package com.spelder.tagyourit.networking.api;

import android.os.Parcel;
import android.os.Parcelable;

public enum SortBy implements Parcelable {
  LATEST("Posted", SortOrder.DESCENDING),
  RATING("Rating", SortOrder.DESCENDING),
  DOWNLOAD("Downloaded", SortOrder.DESCENDING),
  TITLE("Title", SortOrder.ASCENDING);

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
  private final SortOrder order;

  SortBy(String label, SortOrder order) {
    this.label = label;
    this.order = order;
  }

  public String getSortBy() {
    return SORT_BY_LABEL + label;
  }

  public SortOrder getOrder() {
    return order;
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
