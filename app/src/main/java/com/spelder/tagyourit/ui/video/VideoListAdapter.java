package com.spelder.tagyourit.ui.video;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.spelder.tagyourit.BuildConfig;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.model.VideoComponents;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** List adapter for displaying the video list. */
class VideoListAdapter extends BaseAdapter {
  private final LayoutInflater mInflater;

  private final List<VideoComponents> videos;

  private final SparseArray<View> videoViews;

  private VideoComponents currentVideo;

  VideoListAdapter(Context context) {
    mInflater = LayoutInflater.from(context);
    videos = new ArrayList<>();
    videoViews = new SparseArray<>();
  }

  @Override
  public int getCount() {
    return videos.size();
  }

  @Override
  public Object getItem(int position) {
    return videos.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  void addVideos(List<VideoComponents> toAdd) {
    if (toAdd != null) {
      videos.addAll(toAdd);
      videoViews.clear();
    }
  }

  void setCurrentVideo(VideoComponents currentVideo) {
    this.currentVideo = currentVideo;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = videoViews.get(position);
    VideoComponents video = videos.get(position);

    if (view == null) {
      view = mInflater.inflate(R.layout.video_list_view, parent, false);
      VideoListAdapter.ViewHolder holder = new VideoListAdapter.ViewHolder();
      TextView title = view.findViewById(R.id.video_list_title);
      TextView postedBy = view.findViewById(R.id.video_list_posted_by);
      TextView key = view.findViewById(R.id.video_list_key);
      TextView views = view.findViewById(R.id.video_list_views);
      View postedByLayout = view.findViewById(R.id.video_list_posted_by_layout);
      View keyLayout = view.findViewById(R.id.video_list_key_layout);
      View viewLayout = view.findViewById(R.id.video_list_views_layout);

      if (video.getVideoTitle() != null && !video.getVideoTitle().isEmpty()) {
        title.setText(video.getVideoTitle());
        title.setVisibility(View.VISIBLE);
      } else {
        title.setVisibility(View.GONE);
      }
      if (video.getSungBy() != null && !video.getSungBy().isEmpty()) {
        postedBy.setText(video.getSungBy());
        postedByLayout.setVisibility(View.VISIBLE);
      } else {
        postedByLayout.setVisibility(View.GONE);
      }
      if (video.getSungKey() != null) {
        key.setText(video.getSungKey().getDisplay());
        keyLayout.setVisibility(View.VISIBLE);
      } else {
        keyLayout.setVisibility(View.GONE);
      }
      if (video.getViewCount() != null && video.getViewCount().compareTo(BigInteger.ZERO) != 0) {
        views.setText(String.format(Locale.US, "%,d", video.getViewCount()));
        viewLayout.setVisibility(View.VISIBLE);
      } else {
        viewLayout.setVisibility(View.GONE);
      }

      YouTubeThumbnailView thumbnail = view.findViewById(R.id.video_list_thumbnail);
      thumbnail.setTag(video.getVideoCode());
      thumbnail.initialize(BuildConfig.YOUTUBE_API_KEY, holder);

      videoViews.put(position, view);
    }

    TextView title = view.findViewById(R.id.video_list_title);
    if (currentVideo != null && currentVideo.getId() == video.getId()) {
      title.setTextColor(Color.parseColor("#e48126"));
    } else {
      title.setTextColor(Color.WHITE);
    }

    return view;
  }

  private class ViewHolder implements YouTubeThumbnailView.OnInitializedListener {
    @Override
    public void onInitializationSuccess(
        YouTubeThumbnailView thumbnailView, YouTubeThumbnailLoader thumbnailLoader) {
      thumbnailLoader.setVideo(thumbnailView.getTag().toString());
      thumbnailLoader.setOnThumbnailLoadedListener(
          new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
              thumbnailLoader.release();
            }

            @Override
            public void onThumbnailError(
                YouTubeThumbnailView youTubeThumbnailView,
                YouTubeThumbnailLoader.ErrorReason errorReason) {}
          });
    }

    public void onInitializationFailure(
        YouTubeThumbnailView var1, YouTubeInitializationResult var2) {}
  }
}
