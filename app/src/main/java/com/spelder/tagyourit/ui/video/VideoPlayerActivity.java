package com.spelder.tagyourit.ui.video;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.model.VideoComponents;
import java.util.List;

/**
 * Video player activity that plays and controls the video as well as displays the list beneath the
 * video.
 */
public class VideoPlayerActivity extends AppCompatActivity {
  public static final String VIDEO_ID_KEY = "video.player.currentVideoId";

  public static final String VIDEOS_KEY = "video.player.videos";

  private static final String VIDEO_CURRENT_SECOND_KEY = "video.player.currentSecond";

  private static final int RECOVERY_REQUEST = 1;

  private final Handler setUnspecifiedOrientationHandler = new Handler();

  private final Runnable setUnspecifiedOrientationRunnable =
      () -> setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

  private YouTubePlayerView youTubeView;

  private VideoComponents currentVideo;

  private int currentVideoId;

  private YouTubePlayer player;

  private ActionBar actionBar;

  private float currentSecond = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.video_player);

    actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    List<VideoComponents> videos = loadFromSavedOrIntent(savedInstanceState);
    initializeVideoList(videos);
    initYouTubePlayerView();
    loadVideoInfo();
    handleOrientation(getResources().getConfiguration());
  }

  private void initializeVideoList(List<VideoComponents> videos) {
    final VideoListAdapter listAdapter = new VideoListAdapter(this);
    ListView videoList = findViewById(R.id.video_player_list);
    videoList.setAdapter(listAdapter);
    videoList.setOnItemClickListener(
        (AdapterView<?> arg0, View arg1, int position, long arg3) -> {
          currentSecond = 0;
          currentVideoId = position;
          currentVideo = (VideoComponents) listAdapter.getItem(position);
          loadVideoInfo();
          loadVideo(player, currentVideo.getVideoCode());
          listAdapter.setCurrentVideo(currentVideo);
          listAdapter.notifyDataSetChanged();
        });
    listAdapter.addVideos(videos);
    listAdapter.setCurrentVideo(currentVideo);
    listAdapter.notifyDataSetChanged();
  }

  private List<VideoComponents> loadFromSavedOrIntent(Bundle savedInstanceState) {
    List<VideoComponents> videos = getIntent().getParcelableArrayListExtra(VIDEOS_KEY);
    if (savedInstanceState != null) {
      currentVideoId = savedInstanceState.getInt(VIDEO_ID_KEY);
      currentSecond = savedInstanceState.getFloat(VIDEO_CURRENT_SECOND_KEY);
    } else {
      currentVideoId = getIntent().getIntExtra(VIDEO_ID_KEY, -1);
    }
    if (currentVideoId != -1) {
      currentVideo = videos.get(currentVideoId);
    } else {
      currentVideo = null;
    }

    return videos;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    if (youTubeView.isFullScreen()) {
      setTemporaryPortrait();
    } else {
      super.onBackPressed();
    }
  }

  private void initYouTubePlayerView() {
    youTubeView = findViewById(R.id.youtube_view);
    getLifecycle().addObserver(youTubeView);

    youTubeView.setEnableAutomaticInitialization(false);
    youTubeView.initialize(
        new AbstractYouTubePlayerListener() {
          @Override
          public void onReady(@NonNull YouTubePlayer youTubePlayer) {
            player = youTubePlayer;
            if (currentVideo != null) {
              loadVideo(youTubePlayer, currentVideo.getVideoCode());
            }
          }

          @Override
          public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {
            currentSecond = second;
          }
        },
        true);

    youTubeView
        .getPlayerUiController()
        .setFullScreenButtonClickListener(
            view -> {
              if (!youTubeView.isFullScreen()) {
                setPermanentLandscape();
              } else {
                setTemporaryPortrait();
              }
            });
  }

  private void setPermanentLandscape() {
    setUnspecifiedOrientationHandler.removeCallbacks(setUnspecifiedOrientationRunnable);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  }

  private void setTemporaryPortrait() {
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setUnspecifiedOrientationHandler.postDelayed(setUnspecifiedOrientationRunnable, 4000);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle savedState) {
    super.onSaveInstanceState(savedState);
    savedState.putInt(VIDEO_ID_KEY, currentVideoId);
    savedState.putFloat(VIDEO_CURRENT_SECOND_KEY, currentSecond);
  }

  private void loadVideoInfo() {
    if (currentVideo == null) {
      youTubeView.setVisibility(GONE);
      actionBar.setTitle(getResources().getString(R.string.video_all));
    } else {
      youTubeView.setVisibility(VISIBLE);
      actionBar.setTitle(currentVideo.getVideoTitle());
    }
  }

  private void loadVideo(YouTubePlayer youTubePlayer, String videoId) {
    if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
      youTubePlayer.loadVideo(videoId, currentSecond);
    } else {
      youTubePlayer.cueVideo(videoId, currentSecond);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RECOVERY_REQUEST) {
      initYouTubePlayerView();
    }
  }

  @Override
  public void onConfigurationChanged(@NonNull Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    handleOrientation(newConfig);
  }

  private void handleOrientation(Configuration configuration) {
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      youTubeView.enterFullScreen();
      hideSystemUI();
    } else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
      youTubeView.exitFullScreen();
      showSystemUI();
    }
  }

  private void hideSystemUI() {
    actionBar.hide();
    getWindow()
        .getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    getWindow()
        .setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
  }

  private void showSystemUI() {
    actionBar.show();
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
  }
}
