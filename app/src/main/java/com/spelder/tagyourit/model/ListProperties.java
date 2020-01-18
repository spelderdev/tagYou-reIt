package com.spelder.tagyourit.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

public class ListProperties implements Parcelable {
  public static final Parcelable.Creator<ListProperties> CREATOR =
      new Creator<ListProperties>() {
        @Override
        public ListProperties createFromParcel(Parcel source) {
          ListProperties listProperties = new ListProperties();
          listProperties.setDbId(source.readLong());
          listProperties.setName(source.readString());
          listProperties.setUserCreated(source.readInt() == 1);
          listProperties.setIcon(ListIcon.fromDbId(source.readInt()));
          listProperties.setDownloadSheet(source.readInt() == 1);
          listProperties.setDownloadTrack(source.readInt() == 1);
          listProperties.setListSize(source.readInt());
          listProperties.setColor(source.readInt());
          listProperties.setDefaultList(source.readInt() == 1);
          return listProperties;
        }

        @Override
        public ListProperties[] newArray(int size) {
          return new ListProperties[size];
        }
      };

  private Long dbId;
  private String name;
  private boolean userCreated;
  private ListIcon icon = ListIcon.DEFAULT;
  private int color = Color.GRAY;
  private boolean downloadSheet;
  private boolean downloadTrack;
  private boolean defaultList;
  private int listSize;

  public Long getDbId() {
    return dbId;
  }

  public void setDbId(Long dbId) {
    if (dbId != null && dbId == -1) {
      this.dbId = null;
    } else {
      this.dbId = dbId;
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isUserCreated() {
    return userCreated;
  }

  public void setUserCreated(boolean userCreated) {
    this.userCreated = userCreated;
  }

  public ListIcon getIcon() {
    return icon;
  }

  public void setIcon(ListIcon icon) {
    this.icon = icon;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public boolean isDownloadSheet() {
    return downloadSheet;
  }

  public void setDownloadSheet(boolean downloadSheet) {
    this.downloadSheet = downloadSheet;
  }

  public boolean isDownloadTrack() {
    return downloadTrack;
  }

  public void setDownloadTrack(boolean downloadTrack) {
    this.downloadTrack = downloadTrack;
  }

  public boolean isDefaultList() {
    return defaultList;
  }

  public void setDefaultList(boolean defaultList) {
    this.defaultList = defaultList;
  }

  public int getListSize() {
    return listSize;
  }

  public void setListSize(int listSize) {
    this.listSize = listSize;
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel parcel, int flags) {
    if (dbId != null) {
      parcel.writeLong(dbId);
    } else {
      parcel.writeLong(-1);
    }
    parcel.writeString(name);
    parcel.writeInt(userCreated ? 1 : 0);
    parcel.writeInt(icon.getDbId());
    parcel.writeInt(downloadSheet ? 1 : 0);
    parcel.writeInt(downloadTrack ? 1 : 0);
    parcel.writeInt(listSize);
    parcel.writeInt(color);
    parcel.writeInt(defaultList ? 1 : 0);
  }
}
