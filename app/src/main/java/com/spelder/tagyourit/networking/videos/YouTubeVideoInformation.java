package com.spelder.tagyourit.networking.videos;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Log;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.io.BaseEncoding;
import com.spelder.tagyourit.BuildConfig;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.VideoComponents;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Retrieves video information from YouTube and converts it into internal model. */
public class YouTubeVideoInformation {
  private static final String TAG = YouTubeVideoInformation.class.getName();

  private static final String APPLICATION_NAME = "Tag You're It";

  private static final String VIDEO_FIELDS =
      "items(id,snippet(title,description),statistics(viewCount,likeCount,favoriteCount,dislikeCount,commentCount))";

  private static final String VIDEO_PARTS = "snippet,statistics";

  private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private final Context context;

  private HttpTransport HTTP_TRANSPORT;

  public YouTubeVideoInformation(Context context) {
    this.context = context;

    try {
      HTTP_TRANSPORT = new NetHttpTransport();
    } catch (Throwable t) {
      Log.e(TAG, "Could not get transport", t);
    }
  }

  private YouTube getYouTubeService() {
    return new YouTube.Builder(
            HTTP_TRANSPORT,
            JSON_FACTORY,
            request -> {
              String packageName = context.getPackageName();
              String SHA1 = getSHA1(packageName);

              request.getHeaders().set("X-Android-Package", packageName);
              request.getHeaders().set("X-Android-Cert", SHA1);
            })
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  public ArrayList<VideoComponents> getVideoInfoAndUpdateDb(List<VideoComponents> videos) {
    ArrayList<VideoComponents> updatedVideoList = getVideoInfo(videos);

    TagDb db = new TagDb(context);
    db.updateVideos(updatedVideoList);

    return updatedVideoList;
  }

  private ArrayList<VideoComponents> getVideoInfo(List<VideoComponents> videos) {
    ArrayList<VideoComponents> updatedVideoList = new ArrayList<>();

    for (VideoComponents video : videos) {
      VideoComponents updatedVideo = getVideoInfo(video);
      if (updatedVideo != null) {
        updatedVideoList.add(updatedVideo);
      }
    }
    Collections.sort(updatedVideoList, new SortByViews());
    return updatedVideoList;
  }

  private VideoComponents getVideoInfo(VideoComponents video) {
    YouTube youtube = getYouTubeService();
    VideoComponents updatedVideo = new VideoComponents(video);

    try {
      YouTube.Videos.List videosListByIdRequest =
          youtube
              .videos()
              .list(VIDEO_PARTS)
              .setFields(VIDEO_FIELDS)
              .setKey(BuildConfig.YOUTUBE_API_KEY)
              .setId(video.getVideoCode());

      VideoListResponse response = videosListByIdRequest.execute();
      Log.d(TAG, "Video Response: " + response);

      if (!response.getItems().isEmpty()) {
        Video responseVideo = response.getItems().get(0);
        updatedVideo.setVideoTitle(responseVideo.getSnippet().getTitle());
        updatedVideo.setViewCount(responseVideo.getStatistics().getViewCount());
        updatedVideo.setLikeCount(responseVideo.getStatistics().getLikeCount());
        updatedVideo.setDislikeCount(responseVideo.getStatistics().getDislikeCount());
        updatedVideo.setFavoriteCount(responseVideo.getStatistics().getFavoriteCount());
        updatedVideo.setCommentCount(responseVideo.getStatistics().getCommentCount());
        return updatedVideo;
      }
    } catch (Throwable t) {
      Log.e(TAG, "There was a service error", t);
    }

    return null;
  }

  private String getSHA1(String packageName) {
    try {
      Signature[] signatures;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        signatures =
            context
                .getPackageManager()
                .getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                .signingInfo
                .getApkContentsSigners();
      } else {
        signatures =
            context
                .getPackageManager()
                .getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                .signatures;
      }

      if (signatures.length > 0) {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        md.update(signatures[0].toByteArray());
        return BaseEncoding.base16().encode(md.digest());
      }
    } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
      Log.e(TAG, "Error retrieving Signature", e);
    }
    return null;
  }

  class SortByViews implements Comparator<VideoComponents> {
    public int compare(VideoComponents a, VideoComponents b) {
      return b.getViewCount().compareTo(a.getViewCount());
    }
  }
}
