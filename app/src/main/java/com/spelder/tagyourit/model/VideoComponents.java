package com.spelder.tagyourit.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.spelder.tagyourit.pitch.Pitch;
import java.math.BigInteger;

/** Model for videos. */
public class VideoComponents implements Parcelable {
  public static final Parcelable.Creator<VideoComponents> CREATOR =
      new Creator<VideoComponents>() {
        @Override
        public VideoComponents createFromParcel(Parcel source) {
          VideoComponents video = new VideoComponents();
          video.setId(source.readInt());
          video.setDescription(source.readString());
          String sungKeyString = source.readString();
          if (sungKeyString != null) {
            video.setSungKey(Pitch.valueOf(sungKeyString));
          }
          video.setMultitrack(source.readByte() != 0);
          video.setVideoCode(source.readString());
          video.setSungBy(source.readString());
          video.setSungWebsite(source.readString());
          video.setPostedDate(source.readString());
          video.setTagId(source.readInt());
          video.setTagTitle(source.readString());
          video.setVideoTitle(source.readString());
          video.setViewCount(new BigInteger(source.readString()));
          video.setFavoriteCount(new BigInteger(source.readString()));
          video.setLikeCount(new BigInteger(source.readString()));
          video.setDislikeCount(new BigInteger(source.readString()));
          video.setCommentCount(new BigInteger(source.readString()));

          return video;
        }

        @Override
        public VideoComponents[] newArray(int size) {
          return new VideoComponents[size];
        }
      };

  private int id;

  private String description;

  private Pitch sungKey;

  private boolean multitrack = false;

  private String videoCode;

  private String sungBy;

  private String sungWebsite;

  private String postedDate;

  private int tagId;

  private String tagTitle;

  private String videoTitle;

  private BigInteger viewCount;

  private BigInteger likeCount;

  private BigInteger favoriteCount;

  private BigInteger dislikeCount;

  private BigInteger commentCount;

  public VideoComponents() {
    videoTitle = "";
    viewCount = BigInteger.valueOf(0);
    likeCount = BigInteger.valueOf(0);
    favoriteCount = BigInteger.valueOf(0);
    dislikeCount = BigInteger.valueOf(0);
    commentCount = BigInteger.valueOf(0);
  }

  public VideoComponents(VideoComponents video) {
    setId(video.getId());
    setDescription(video.getDescription());
    setSungKey(video.getSungKey());
    setMultitrack(video.isMultitrack());
    setVideoCode(video.getVideoCode());
    setSungBy(video.getSungBy());
    setSungWebsite(video.getSungWebsite());
    setPostedDate(video.getPostedDate());
    setTagId(video.getTagId());
    setTagTitle(video.getTagTitle());
    setVideoTitle(video.getVideoTitle());
    setViewCount(video.getViewCount());
    setFavoriteCount(video.getFavoriteCount());
    setLikeCount(video.getLikeCount());
    setDislikeCount(video.getDislikeCount());
    setCommentCount(video.getCommentCount());
  }

  public String getVideoTitle() {
    return videoTitle;
  }

  public void setVideoTitle(String videoTitle) {
    this.videoTitle = videoTitle;
  }

  public BigInteger getViewCount() {
    return viewCount;
  }

  public void setViewCount(BigInteger viewCount) {
    this.viewCount = viewCount;
  }

  public BigInteger getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(BigInteger likeCount) {
    this.likeCount = likeCount;
  }

  public BigInteger getFavoriteCount() {
    return favoriteCount;
  }

  public void setFavoriteCount(BigInteger favoriteCount) {
    this.favoriteCount = favoriteCount;
  }

  public BigInteger getDislikeCount() {
    return dislikeCount;
  }

  public void setDislikeCount(BigInteger dislikeCount) {
    this.dislikeCount = dislikeCount;
  }

  public BigInteger getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(BigInteger commentCount) {
    this.commentCount = commentCount;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Pitch getSungKey() {
    return sungKey;
  }

  public void setSungKey(Pitch sungKey) {
    this.sungKey = sungKey;
  }

  public boolean isMultitrack() {
    return multitrack;
  }

  public void setMultitrack(boolean multitrack) {
    this.multitrack = multitrack;
  }

  public String getVideoCode() {
    return videoCode;
  }

  public void setVideoCode(String videoCode) {
    this.videoCode = videoCode;
  }

  public String getSungBy() {
    return sungBy;
  }

  public void setSungBy(String sungBy) {
    this.sungBy = sungBy;
  }

  public String getSungWebsite() {
    return sungWebsite;
  }

  public void setSungWebsite(String sungWebsite) {
    this.sungWebsite = sungWebsite;
  }

  public String getPostedDate() {
    return postedDate;
  }

  public void setPostedDate(String postedDate) {
    this.postedDate = postedDate;
  }

  public int getTagId() {
    return tagId;
  }

  public void setTagId(int tagId) {
    this.tagId = tagId;
  }

  public String getTagTitle() {
    return tagTitle;
  }

  public void setTagTitle(String tagTitle) {
    this.tagTitle = tagTitle;
  }

  public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeInt(id);
    parcel.writeString(description);
    if (sungKey == null) {
      parcel.writeString(null);
    } else {
      parcel.writeString(sungKey.name());
    }
    parcel.writeByte((byte) (multitrack ? 1 : 0));
    parcel.writeString(videoCode);
    parcel.writeString(sungBy);
    parcel.writeString(sungWebsite);
    parcel.writeString(postedDate);
    parcel.writeInt(tagId);
    parcel.writeString(tagTitle);
    parcel.writeString(videoTitle);
    if (viewCount != null) {
      parcel.writeString(viewCount.toString());
    } else {
      parcel.writeString("0");
    }
    if (favoriteCount != null) {
      parcel.writeString(favoriteCount.toString());
    } else {
      parcel.writeString("0");
    }
    if (likeCount != null) {
      parcel.writeString(likeCount.toString());
    } else {
      parcel.writeString("0");
    }
    if (dislikeCount != null) {
      parcel.writeString(dislikeCount.toString());
    } else {
      parcel.writeString("0");
    }
    if (commentCount != null) {
      parcel.writeString(commentCount.toString());
    } else {
      parcel.writeString("0");
    }
  }

  public int describeContents() {
    return 0;
  }
}
