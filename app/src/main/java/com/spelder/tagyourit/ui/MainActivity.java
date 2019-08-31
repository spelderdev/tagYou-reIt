package com.spelder.tagyourit.ui;

import static com.spelder.tagyourit.ui.video.VideoPlayerActivity.VIDEOS_KEY;
import static com.spelder.tagyourit.ui.video.VideoPlayerActivity.VIDEO_ID_KEY;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.drive.FavoritesBackup;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.model.TrackComponents;
import com.spelder.tagyourit.model.VideoComponents;
import com.spelder.tagyourit.music.MusicNotifier;
import com.spelder.tagyourit.music.MusicService;
import com.spelder.tagyourit.music.model.Speed;
import com.spelder.tagyourit.networking.TagListRetriever;
import com.spelder.tagyourit.networking.UpdateTagTask;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import com.spelder.tagyourit.ui.music.MusicPlayerActivity;
import com.spelder.tagyourit.ui.settings.SortBottomSheet;
import com.spelder.tagyourit.ui.video.VideoPlayerActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Application's main entry point. The application contains multiple fragments that run from this
 * activity. This displays the top level functions of the app like the navigation drawer, filter
 * menu, favorites button, and search.
 */
public class MainActivity extends AppCompatActivity
    implements BottomNavigationView.OnNavigationItemSelectedListener,
        OnQueryTextListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
  private static final String TAG = MainActivity.class.getName();

  public static String currentQuery = "";

  private FragmentSwitcher manager;

  private FilterBuilder filterBuilder;

  private Activity actionBar;

  private SearchView searchView;

  private View trackToolbar;

  private TextView trackTitle;

  private TextView trackPart;

  private ImageView trackPlayPause;

  private View trackLoading;

  private MusicService musicSrv;

  private Intent playIntent;

  private boolean musicBound = false;

  private SortBottomSheet sortBottomSheet;

  private final ServiceConnection musicConnection =
      new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
          musicSrv = binder.getService();
          musicSrv.addNotification(
              new MusicNotifier() {
                @Override
                public void done() {
                  trackToolbar.setVisibility(View.GONE);
                }

                @Override
                public void play(String title, String part) {
                  trackTitle.setText(title);
                  trackPart.setText(part);

                  trackLoading.setVisibility(View.GONE);
                  trackPlayPause.setVisibility(View.VISIBLE);
                  trackToolbar.setVisibility(View.VISIBLE);
                  trackPlayPause.setImageResource(R.drawable.ic_pause_white_24dp);
                }

                @Override
                public void pause() {
                  trackLoading.setVisibility(View.GONE);
                  trackPlayPause.setVisibility(View.VISIBLE);
                  trackToolbar.setVisibility(View.VISIBLE);
                  trackPlayPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                }

                @Override
                public void speedChanged(Speed speed) {}

                @Override
                public void pitchChanged(int semitones) {}

                @Override
                public void loading(String title, String part) {
                  trackTitle.setText(title);
                  trackPart.setText(part);

                  trackPlayPause.setVisibility(View.GONE);
                  trackLoading.setVisibility(View.VISIBLE);
                  trackToolbar.setVisibility(View.VISIBLE);
                }
              });
          musicBound = true;
          if (musicSrv.isPlaying()) {
            trackPlayPause.setImageResource(R.drawable.ic_pause_white_24dp);
          } else {
            trackPlayPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
          }

          if (musicSrv.getTrack() != null) {
            trackTitle.setText(musicSrv.getTrack().getTagTitle());
            trackPart.setText(musicSrv.getTrack().getPart());
          }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          musicBound = false;
        }
      };

  private UpdateTagTask updateTagTask;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(null);
    actionBar = this;
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    if (manager == null) {
      manager = new FragmentSwitcher(this);
    }

    if (filterBuilder == null) {
      filterBuilder = new FilterBuilder(this);
    }

    BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
    bottomNavigationView.setOnNavigationItemSelectedListener(this);

    int baseId = manager.getBaseFragmentId();
    manager.setBaseFragment(baseId);
    manager.displayFragment(baseId);
    bottomNavigationView.setSelectedItemId(manager.getCurrentNavigationId());

    updateMenu();

    getSupportFragmentManager()
        .addOnBackStackChangedListener(
            () -> {
              ActionBar ab = getSupportActionBar();
              int backCount = getSupportFragmentManager().getBackStackEntryCount();
              if (backCount == 0) {
                if (!searchView.isIconified()) {
                  searchView.setIconified(true);
                }

                manager.getCurrentNavigationId();

                if (ab != null) {
                  ab.setDisplayHomeAsUpEnabled(false);
                }
              } else {
                manager.getCurrentNavigationId();

                // Displays back button
                if (ab != null) {
                  ab.setDisplayHomeAsUpEnabled(true);
                }
              }

              updateMenu();
            });

    trackToolbar = findViewById(R.id.track_toolbar);
    trackTitle = findViewById(R.id.selected_track_title);
    trackPart = findViewById(R.id.selected_track_part);
    trackPlayPause = findViewById(R.id.player_control);
    trackPlayPause.setOnClickListener(v -> togglePlayPause());
    trackLoading = findViewById(R.id.player_loading);
    ImageView trackClose = findViewById(R.id.player_close);
    trackClose.setOnClickListener(
        v -> {
          musicSrv.stop();

          stopService(playIntent);

          trackToolbar.setVisibility(View.GONE);
        });

    trackToolbar.setOnClickListener(view -> openMusicPlayer());

    handleIntent();

    sortBottomSheet = new SortBottomSheet();

    PreferenceManager.getDefaultSharedPreferences(this)
        .registerOnSharedPreferenceChangeListener(this);
  }

  private void handleIntent() {
    Intent intent = getIntent();
    if (intent != null && intent.getData() != null) {
      Uri data = intent.getData();
      try {
        Log.d(TAG, data.toString());
        String tagId = data.getLastPathSegment();
        if (tagId == null) {
          return;
        }
        tagId = tagId.replaceFirst("tag-", "");
        tagId = tagId.substring(0, tagId.indexOf('-'));
        Log.d(TAG, "Opening TagId: " + tagId);
        int tagIdInt = Integer.parseInt(tagId);
        TagListRetriever ret = new TagListRetriever(tagIdInt);
        List<Tag> returnedTags = ret.downloadUrl();
        if (returnedTags.size() >= 1) {
          manager.displayTag(returnedTags.get(0));
        }
      } catch (Exception e) {
        Log.e(TAG, "Could not open intent", e);
        Toast.makeText(this, "Could not open tag", Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onStart() {
    super.onStart();

    playIntent = new Intent(this, MusicService.class);
    getApplicationContext().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);

    updateTagTask = new UpdateTagTask(getApplicationContext());
    new Thread(updateTagTask).start();
  }

  @Override
  protected void onStop() {
    super.onStop();

    if (musicBound) {
      getApplicationContext().unbindService(musicConnection);
    }

    if (updateTagTask != null) {
      updateTagTask.cancel();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    manager = null;
    filterBuilder = null;

    stopService(playIntent);
    musicSrv = null;

    PreferenceManager.getDefaultSharedPreferences(this)
        .unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public boolean onQueryTextChange(String query) {
    currentQuery = query;
    if (!query.isEmpty()) {
      Log.d("MainActivity", "QueryTextChanged: " + query);

      manager.loadSearch(query);
    }

    return true;
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    currentQuery = query;
    Log.d("MainActivity", "QueryTextSubmit: " + query);
    if (!searchView.isIconified()) {
      searchView.setIconified(true);
    }
    searchView.setIconified(true);
    manager.hideKeyboard();

    return true;
  }

  @Override
  public void onBackPressed() {
    if (!searchView.isIconified()) {
      searchView.setIconified(true);
    }

    super.onBackPressed();

    manager.onBackPressed();

    updateMenu();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);

    menu.findItem(R.id.action_favorite).setVisible(manager.isFavoriteVisible());
    menu.findItem(R.id.action_unfavorite).setVisible(manager.isUnFavoriteVisible());
    menu.findItem(R.id.action_menu).setVisible(manager.isMenuVisible());
    menu.findItem(R.id.action_search).setVisible(manager.isSearchVisible());
    menu.findItem(R.id.action_sort).setVisible(manager.isSortVisible());

    searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    searchView.setOnQueryTextListener(this);

    if (!currentQuery.isEmpty()) {
      searchView.setIconified(false);
      searchView.setQuery(currentQuery, false);
    }

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    Log.d(TAG, "Option selected");
    // Handle presses on the action bar items
    switch (item.getItemId()) {
      case R.id.action_favorite:
        Tag tag = manager.getDisplayedTag();

        TagDb db = new TagDb(this);
        long newRow = db.insertFavorite(tag);

        tag.setDbId(newRow);

        updateMenu();
        return true;

      case R.id.action_unfavorite:
        Log.d("MainActivity", "UnFavorites");
        Tag unTag = manager.getDisplayedTag();

        TagDb unDb = new TagDb(this);
        unDb.deleteFavorite(unTag);

        unTag.setDbId(null);

        updateMenu();
        return true;

      case R.id.action_menu:
        Log.d("MainActivity", "Menu");
        manager.showBottomMenu();
        return true;

      case R.id.action_sort:
        toggleSort();
        return true;

      case android.R.id.home:
        onBackPressed();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void toggleSort() {
    if (!sortBottomSheet.isVisible()) {
      sortBottomSheet.show(getSupportFragmentManager(), "Bottom sheet");
    } else {
      sortBottomSheet.dismiss();
    }
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    manager.displayNavigationSelectedFragment(id);

    updateMenu();

    return true;
  }

  private void updateMenu() {
    invalidateOptionsMenu();
    setCurrentMenuTitle();
  }

  private void setCurrentMenuTitle() {
    actionBar.setTitle(manager.getCurrentMenuTitle());
  }

  public FragmentSwitcher getManager() {
    return manager;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == FavoritesBackup.REQUEST_CODE_SIGN_IN) {
      if (resultCode != RESULT_OK) {
        // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
        // required and is fatal. For apps where sign-in is optional, handle
        // appropriately
        Log.e(TAG, "Sign-in failed. Result code: " + resultCode);
        return;
      }

      Task<GoogleSignInAccount> getAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
      if (getAccountTask.isSuccessful()) {
        manager.initializeDriveClient(getAccountTask.getResult());
      } else {
        Log.e(TAG, "Sign-in failed. Could not get account info");
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  public void playTrack(TrackComponents track, Tag tag) {
    Log.d(TAG, "Start music service");
    musicSrv.setTag(tag);
    ContextCompat.startForegroundService(this, playIntent);
    musicSrv.playSong(track, this);
  }

  private void togglePlayPause() {
    if (musicSrv.isPlaying()) {
      musicSrv.pause();
    } else {
      musicSrv.play(this);
    }
  }

  private void openMusicPlayer() {
    openActivityFromBottom();
    Intent intent = new Intent(this, MusicPlayerActivity.class);
    startActivity(intent);
  }

  public void openVideoPlayer(ArrayList<VideoComponents> videos, int currentVideoId) {
    openActivityFromBottom();
    Intent intent = new Intent(this, VideoPlayerActivity.class);
    intent.putExtra(VIDEO_ID_KEY, currentVideoId);
    intent.putParcelableArrayListExtra(VIDEOS_KEY, videos);
    startActivity(intent);
  }

  private void openActivityFromBottom() {
    overridePendingTransition(R.anim.slide_up, R.anim.stay);
  }

  public MusicService getMusicSrv() {
    return musicSrv;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.startsWith("filter_")) {
      updateMenu();
    }
  }
}
