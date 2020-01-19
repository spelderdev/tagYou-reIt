package com.spelder.tagyourit.networking.api.filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.spelder.tagyourit.R;

public class FilterBuilder {
  private static final String TAG = FilterBuilder.class.getName();
  private final String SHEET_MUSIC_KEY;
  private final String LEARNING_TRACK_KEY;
  private final String RATING_KEY;
  private final String TYPE_KEY;
  private final String KEY_KEY;
  private final String PARTS_NUMBER_KEY;
  private final String COLLECTION_KEY;
  private final SharedPreferences preferences;

  public FilterBuilder(Context context) {
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
    SHEET_MUSIC_KEY = context.getResources().getString(R.string.filter_sheet_music_key);
    LEARNING_TRACK_KEY = context.getResources().getString(R.string.filter_learning_track_key);
    RATING_KEY = context.getResources().getString(R.string.filter_rating_key);
    TYPE_KEY = context.getResources().getString(R.string.filter_type_key);
    KEY_KEY = context.getResources().getString(R.string.filter_key_key);
    PARTS_NUMBER_KEY = context.getResources().getString(R.string.filter_part_key);
    COLLECTION_KEY = context.getResources().getString(R.string.filter_collection_key);
  }

  public FilterBuilder(Context context, String id) {
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
    SHEET_MUSIC_KEY = context.getResources().getString(R.string.filter_sheet_music_key) + id;
    LEARNING_TRACK_KEY = context.getResources().getString(R.string.filter_learning_track_key) + id;
    RATING_KEY = context.getResources().getString(R.string.filter_rating_key) + id;
    TYPE_KEY = context.getResources().getString(R.string.filter_type_key) + id;
    KEY_KEY = context.getResources().getString(R.string.filter_key_key) + id;
    PARTS_NUMBER_KEY = context.getResources().getString(R.string.filter_part_key) + id;
    COLLECTION_KEY = context.getResources().getString(R.string.filter_collection_key) + id;
  }

  public FilterBy build() {
    FilterBy filter = new FilterBy();
    filter.setHasSheetMusic(getSheetMusic());
    filter.setHasLearningTrack(getLearningTrack());
    filter.setMinimumRating(getRating());
    filter.setNumberOfParts(getPart());
    filter.setType(getType());
    filter.setKey(getKey());
    filter.setCollection(getCollection());

    Log.d(TAG, "Filter: " + filter);

    return filter;
  }

  public boolean getSheetMusic() {
    return preferences.getBoolean(SHEET_MUSIC_KEY, false);
  }

  public void setSheetMusic(boolean sheetMusic) {
    preferences.edit().putBoolean(SHEET_MUSIC_KEY, sheetMusic).apply();
  }

  public boolean getLearningTrack() {
    return preferences.getBoolean(LEARNING_TRACK_KEY, false);
  }

  public void setLearningTrack(boolean learningTrack) {
    preferences.edit().putBoolean(LEARNING_TRACK_KEY, learningTrack).apply();
  }

  public Rating getRating() {
    try {
      String ratingString = preferences.getString(RATING_KEY, Rating.ANY.name());
      Log.d(TAG, "Get rating: " + ratingString);
      return Rating.valueOf(ratingString);
    } catch (IllegalArgumentException e) {
      return Rating.ANY;
    }
  }

  public void setRating(Rating rating) {
    preferences.edit().putString(RATING_KEY, rating.name()).apply();
  }

  public Part getPart() {
    try {
      String partString = preferences.getString(PARTS_NUMBER_KEY, Part.ANY.name());
      Log.d(TAG, "Get part: " + partString);
      return Part.valueOf(partString);
    } catch (IllegalArgumentException e) {
      return Part.ANY;
    }
  }

  public void setPart(Part part) {
    preferences.edit().putString(PARTS_NUMBER_KEY, part.name()).apply();
  }

  public Type getType() {
    try {
      String typeString = preferences.getString(TYPE_KEY, Type.ANY.name());
      Log.d(TAG, "Get type: " + typeString);
      return Type.valueOf(typeString);
    } catch (IllegalArgumentException e) {
      return Type.ANY;
    }
  }

  public void setType(Type type) {
    preferences.edit().putString(TYPE_KEY, type.name()).apply();
  }

  public Key getKey() {
    try {
      String keyString = preferences.getString(KEY_KEY, Key.ANY.name());
      Log.d(TAG, "Get key: " + keyString);
      return Key.valueOf(keyString);
    } catch (IllegalArgumentException e) {
      return Key.ANY;
    }
  }

  public void setKey(Key key) {
    preferences.edit().putString(KEY_KEY, key.name()).apply();
  }

  public Collection getCollection() {
    try {
      String collectionString = preferences.getString(COLLECTION_KEY, Collection.ANY.name());
      Log.d(TAG, "Get collection: " + collectionString);
      return Collection.valueOf(collectionString);
    } catch (IllegalArgumentException e) {
      return Collection.ANY;
    }
  }

  public void setCollection(Collection collection) {
    preferences.edit().putString(COLLECTION_KEY, collection.name()).apply();
  }

  public void applyDefaultFilter() {
    setCollection(Collection.ANY);
    setLearningTrack(false);
    setSheetMusic(false);
    setType(Type.ANY);
    setPart(Part.ANY);
    setKey(Key.ANY);
    setRating(Rating.ANY);
  }
}
