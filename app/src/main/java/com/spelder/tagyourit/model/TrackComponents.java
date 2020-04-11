package com.spelder.tagyourit.model;

import android.content.Context;

/** Model for track. */
public class TrackComponents {
  private static final String TRACK_DIRECTORY = "tracks";

  private String link;

  private String type;

  private String part;

  private int tagId;

  private String tagTitle;

  private boolean isDownloaded = false;

  public static String getTrackCacheDirectory(Context context) {
    return context.getCacheDir().getAbsolutePath() + "/" + TRACK_DIRECTORY;
  }

  public String getTrackDirectory(Context context) {
    if (!isDownloaded) {
      return context.getCacheDir().getAbsolutePath() + "/" + TRACK_DIRECTORY;
    } else {
      return context.getFilesDir().getAbsolutePath() + "/" + TRACK_DIRECTORY;
    }
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPart() {
    return part;
  }

  public void setPart(String part) {
    this.part = part;
  }

  private int getTagId() {
    return tagId;
  }

  void setTagId(int tagId) {
    this.tagId = tagId;
  }

  public String getTagTitle() {
    return tagTitle;
  }

  void setTagTitle(String tagTitle) {
    this.tagTitle = tagTitle;
  }

  public String getTrackFileName() {
    return getTagId() + "-" + getPart() + "." + getType();
  }

  public String getTrackPath(Context context) {
    return getTrackDirectory(context) + "/" + getTrackFileName();
  }

  public void setDownloaded(boolean downloaded) {
    isDownloaded = downloaded;
  }

  public boolean isDownloaded() {
    return isDownloaded;
  }
}
