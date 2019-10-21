package com.spelder.tagyourit.networking.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SortBuilder {
  private static final String SORT_BY_LABEL = "SORT_BY";
  private final SharedPreferences preferences;

  public SortBuilder(Context context) {
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public SortBy build() {
    return SortBy.valueOf(preferences.getString(SORT_BY_LABEL, SortBy.TITLE.name()));
  }

  public void setSortBy(SortBy sort) {
    preferences.edit().putString(SORT_BY_LABEL, sort.name()).apply();
  }

  public static boolean isSortKey(String key) {
    return key.equals(SortBuilder.SORT_BY_LABEL);
  }
}
