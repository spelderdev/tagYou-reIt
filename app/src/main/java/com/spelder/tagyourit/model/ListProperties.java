package com.spelder.tagyourit.model;

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
  private ListIcon icon;
  private boolean downloadSheet;
  private boolean downloadTrack;
  private int listSize;

  public Long getDbId() {
    return dbId;
  }

  public void setDbId(Long dbId) {
    this.dbId = dbId;
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
    parcel.writeLong(dbId);
    parcel.writeString(name);
    parcel.writeInt(userCreated ? 1 : 0);
    parcel.writeInt(icon.getDbId());
    parcel.writeInt(downloadSheet ? 1 : 0);
    parcel.writeInt(downloadTrack ? 1 : 0);
    parcel.writeInt(listSize);
  }
}
