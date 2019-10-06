package com.spelder.tagyourit.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/** Model for tag. */
public class Tag implements Parcelable {
  public static final Parcelable.Creator<Tag> CREATOR =
      new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {
          Tag tag = new Tag();
          tag.setId(source.readInt());
          tag.setTitle(source.readString());
          tag.setVersion(source.readString());
          tag.setArranger(source.readString());
          tag.setRating(source.readDouble());
          tag.setDownloadCount(source.readInt());
          tag.setClassicTagNumber(source.readInt());
          tag.setPostedDate(new Date(source.readLong()));
          tag.setKey(source.readString());
          tag.setNumberOfParts(source.readInt());
          tag.setLyrics(source.readString());
          tag.setType(source.readString());
          tag.setCollection(source.readString());
          tag.setSheetMusicLink(source.readString());
          tag.setSheetMusicType(source.readString());
          tag.setSheetMusicFile(source.readString());
          tag.setDbId(source.readLong());
          int size = source.readInt();
          for (int i = 0; i < size; i++) {
            String part = source.readString();
            String link = source.readString();
            String type = source.readString();
            tag.addTrack(part, link, type);
          }
          ArrayList<VideoComponents> videoComponents = new ArrayList<>();
          source.readList(videoComponents, VideoComponents.class.getClassLoader());
          tag.setVideos(videoComponents);
          return tag;
        }

        @Override
        public Tag[] newArray(int size) {
          return new Tag[size];
        }
      };

  private static final String SHEET_MUSIC_DIRECTORY = "sheet_music";

  private final TreeMap<String, TrackComponents> tracks;

  private Long dbId = null;

  private int id = 0;

  private String title;

  private String version;

  private String arranger;

  private double rating;

  private int classicTagNumber = -1;

  private Date postedDate;

  private int downloadCount;

  private String key;

  private int numberParts;

  private String lyrics;

  private String type;

  private String collection;

  private String sheetMusicLink;

  private String sheetMusicType;

  private String sheetMusicFile;

  private ArrayList<VideoComponents> videos;

  private boolean isDownloaded = false;

  public Tag() {
    tracks = new TreeMap<>();
    videos = new ArrayList<>();
  }

  public Tag(String id) {
    this(Integer.parseInt(id));
  }

  private Tag(int id) {
    this();
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setId(String id) {
    try {
      this.id = Integer.parseInt(id);
    } catch (Exception e) {
      this.id = 0;
    }
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean isFavorited() {
    return dbId != null;
  }

  public Long getDbId() {
    return dbId;
  }

  public void setDbId(Long dbId) {
    this.dbId = dbId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getArranger() {
    return arranger;
  }

  public void setArranger(String arranger) {
    this.arranger = arranger;
  }

  public String getArrangerDisplay() {
    if (arranger.length() > 0) {
      return arranger;
    }
    return "";
  }

  public double getRating() {
    return rating;
  }

  public void setRating(String rating) {
    if (!rating.isEmpty()) {
      this.rating = Double.parseDouble(rating);
    }
  }

  private void setRating(double rating) {
    this.rating = rating;
  }

  public int getClassicTagNumber() {
    return classicTagNumber;
  }

  public void setClassicTagNumber(int classicTagNumber) {
    this.classicTagNumber = classicTagNumber;
  }

  public Date getPostedDate() {
    return postedDate;
  }

  public void setPostedDate(Date postedDate) {
    this.postedDate = postedDate;
  }

  public void setPostedDate(long postedDate) {
    this.postedDate = new Date(postedDate);
  }

  public int getDownloadCount() {
    return downloadCount;
  }

  public void setDownloadCount(int downloadCount) {
    this.downloadCount = downloadCount;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public int getNumberOfParts() {
    return numberParts;
  }

  public void setNumberOfParts(int numberParts) {
    this.numberParts = numberParts;
  }

  public String getLyrics() {
    return lyrics;
  }

  public void setLyrics(String lyrics) {
    this.lyrics = lyrics;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCollection() {
    return collection;
  }

  public void setCollection(String collection) {
    this.collection = collection;
  }

  public String getSheetMusicLink() {
    return sheetMusicLink;
  }

  public void setSheetMusicLink(String link) {
    this.sheetMusicLink = link;
  }

  public String getSheetMusicType() {
    return sheetMusicType;
  }

  public void setSheetMusicType(String type) {
    this.sheetMusicType = type;
  }

  public String getSheetMusicFile() {
    return sheetMusicFile;
  }

  public void setSheetMusicFile(String file) {
    this.sheetMusicFile = file;
  }

  public boolean hasSheetMusic() {
    return !sheetMusicLink.equals("");
  }

  public String getSheetMusicPath(Context context) {
    if (!isDownloaded) {
      return getSheetMusicDirectory(context) + "/" + getSheetMusicFileName();
    } else {
      return getSheetMusicDirectory(context) + "/" + getSheetMusicFileName();
    }
  }

  public String getSheetMusicDirectory(Context context) {
    if (!isDownloaded) {
      return context.getCacheDir().getAbsolutePath() + "/" + SHEET_MUSIC_DIRECTORY;
    } else {
      return context.getFilesDir().getAbsolutePath();
    }
  }

  public String getSheetMusicFileName() {
    return getId() + "." + getSheetMusicType();
  }

  public void addTrack(String part, String link, String type) {
    TrackComponents value = new TrackComponents();
    value.setLink(link);
    value.setPart(part);
    value.setType(type);
    value.setTagId(getId());
    value.setTagTitle(getTitle());
    tracks.put(part, value);
  }

  public TrackComponents getTrack(String part) {
    return tracks.get(part);
  }

  public Collection<TrackComponents> getTracks() {
    return tracks.values();
  }

  public boolean hasLearningTracks() {
    return !tracks.isEmpty();
  }

  public boolean hasVideos() {
    return !videos.isEmpty();
  }

  public void addVideo(VideoComponents video) {
    video.setTagId(getId());
    video.setTagTitle(getTitle());
    videos.add(video);
  }

  public ArrayList<VideoComponents> getVideos() {
    return new ArrayList<>(videos);
  }

  public void setVideos(ArrayList<VideoComponents> videos) {
    this.videos = new ArrayList<>(videos);
  }

  public void setDownloaded(boolean downloaded) {
    isDownloaded = downloaded;
  }

  public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeInt(id);
    parcel.writeString(title);
    parcel.writeString(version);
    parcel.writeString(arranger);
    parcel.writeDouble(rating);
    parcel.writeInt(downloadCount);
    parcel.writeInt(classicTagNumber);
    parcel.writeLong(postedDate.getTime());
    parcel.writeString(key);
    parcel.writeInt(numberParts);
    parcel.writeString(lyrics);
    parcel.writeString(type);
    parcel.writeString(collection);
    parcel.writeString(sheetMusicLink);
    parcel.writeString(sheetMusicType);
    parcel.writeString(sheetMusicFile);
    parcel.writeLong(dbId);
    parcel.writeInt(tracks.size());
    for (Map.Entry<String, TrackComponents> entry : tracks.entrySet()) {
      parcel.writeString(entry.getKey());
      parcel.writeString(entry.getValue().getLink());
      parcel.writeString(entry.getValue().getType());
    }
    parcel.writeList(videos);
  }

  public int describeContents() {
    return 0;
  }

  @Override
  @NonNull
  public String toString() {
    return "Id: " + id;
  }
}
