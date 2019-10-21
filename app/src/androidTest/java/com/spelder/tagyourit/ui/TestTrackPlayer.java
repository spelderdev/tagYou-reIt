package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.actionWithAssertions;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.spelder.tagyourit.action.OrientationChangeAction.orientationLandscape;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.widget.SeekBar;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.cache.CacheManager;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.model.TrackComponents;
import com.spelder.tagyourit.music.MusicService;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestTrackPlayer {
  @Rule
  public final ActivityTestRule<MainActivity> mActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  public static ViewAction scrubSeekBarAction(int progress) {
    return actionWithAssertions(
        new GeneralSwipeAction(
            Swipe.SLOW,
            new SeekBarThumbCoordinatesProvider(0),
            new SeekBarThumbCoordinatesProvider(progress),
            Press.PINPOINT));
  }

  private void clearCache(Context context) {
    File musicDir = new File(TrackComponents.getTrackDirectory(context));
    CacheManager.cleanDir(musicDir, CacheManager.getDirSize(musicDir), "");

    File sheetDir = new File(new Tag().getSheetMusicDirectory(context));
    CacheManager.cleanDir(sheetDir, CacheManager.getDirSize(sheetDir), "");
  }

  @Test
  public void testMediaPlayerPortrait() {
    clearCache(mActivityTestRule.getActivity());

    MusicService musicService = mActivityTestRule.getActivity().getMusicSrv();

    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("Smile Bobby Gray"));

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    onView(allOf(withId(R.id.action_menu), withContentDescription("Menu"))).perform(click());

    onView(withId(R.id.tag_detail_track_all)).perform(click());

    onView(allOf(withId(R.id.track_toolbar), isDisplayed())).perform(click());

    assertTrue(musicService.isPlaying());

    onView(withId(R.id.music_player_play_pause)).perform(click());

    assertFalse(musicService.isPlaying());

    onView(withId(R.id.music_player_title)).check(matches(withText("Smile")));
    assertEquals(musicService.getTrack().getTagTitle(), "Smile");

    ViewInteraction part = onView(withId(R.id.music_player_part));
    part.check(matches(withText("AllParts")));
    assertEquals(musicService.getTrack().getPart(), "AllParts");

    ViewInteraction startTime =
        onView(allOf(withId(R.id.music_player_seek_bar_start), isDisplayed()));
    startTime.check(matches(withText(containsString("00:0"))));

    ViewInteraction endTime = onView(allOf(withId(R.id.music_player_seek_bar_end), isDisplayed()));
    endTime.check(matches(withText(containsString("-00:"))));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      ViewInteraction speed = onView(allOf(withId(R.id.music_player_speed_value), isDisplayed()));
      speed.check(matches(withText("1x")));
    }

    onView(withId(R.id.music_player_loading)).check(matches(not(isDisplayed())));

    onView(allOf(withId(R.id.music_player_play_pause), isDisplayed())).perform(click());
    assertTrue(musicService.isPlaying());

    ViewInteraction lead = onView(allOf(withId(R.id.music_player_play_lead), isDisplayed()));
    lead.perform(click());
    part.check(matches(withText("Lead")));
    assertEquals(musicService.getTrack().getPart(), "Lead");
    assertTrue(musicService.isPlaying());

    ViewInteraction bass = onView(allOf(withId(R.id.music_player_play_bass), isDisplayed()));
    bass.perform(click());
    part.check(matches(withText("Bass")));
    assertEquals(musicService.getTrack().getPart(), "Bass");
    assertTrue(musicService.isPlaying());

    ViewInteraction tenor = onView(allOf(withId(R.id.music_player_play_tenor), isDisplayed()));
    tenor.perform(click());
    part.check(matches(withText("Tenor")));
    assertEquals(musicService.getTrack().getPart(), "Tenor");
    assertTrue(musicService.isPlaying());

    ViewInteraction bari = onView(allOf(withId(R.id.music_player_play_bari), isDisplayed()));
    bari.perform(click());
    part.check(matches(withText("Bari")));
    assertEquals(musicService.getTrack().getPart(), "Bari");
    assertTrue(musicService.isPlaying());

    ViewInteraction musicPlayerClose =
        onView(allOf(withId(R.id.music_player_close), isDisplayed()));
    musicPlayerClose.perform(click());

    ViewInteraction toolbarControl = onView(allOf(withId(R.id.player_control), isDisplayed()));
    toolbarControl.perform(click());
    assertFalse(musicService.isPlaying());
    toolbarControl.perform(click());
    assertTrue(musicService.isPlaying());

    ViewInteraction toolbarClose = onView(allOf(withId(R.id.player_close), isDisplayed()));
    toolbarClose.perform(click());
    assertFalse(musicService.isPlaying());
    assertEquals(musicService.getDuration(), 0);
    assertEquals(musicService.getPosition(), 0);
    assertFalse(musicService.isLoading());
  }

  @Test
  public void testMediaPlayerLandscape() {
    mActivityTestRule
        .getActivity()
        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    MusicService musicService = mActivityTestRule.getActivity().getMusicSrv();

    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("Smile Bobby Gray"), closeSoftKeyboard());

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    onView(allOf(withId(R.id.action_menu), withContentDescription("Menu"))).perform(click());

    SystemClock.sleep(500);

    onView(withId(R.id.tag_detail_title)).perform(swipeUp());

    SystemClock.sleep(500);

    onView(withId(R.id.tag_detail_track_all)).perform(click());

    onView(allOf(withId(R.id.track_toolbar), isDisplayed())).perform(click());

    assertTrue(musicService.isPlaying());

    onView(
            allOf(
                withId(R.id.music_player_play_pause),
                withContentDescription("Playback Control"),
                isDisplayed()))
        .perform(click());

    onView(isRoot()).perform(orientationLandscape());

    assertFalse(musicService.isPlaying());

    onView(withId(R.id.music_player_title)).check(matches(withText("Smile")));
    assertEquals(musicService.getTrack().getTagTitle(), "Smile");

    ViewInteraction part = onView(allOf(withId(R.id.music_player_part), isDisplayed()));
    part.check(matches(withText("AllParts")));
    assertEquals(musicService.getTrack().getPart(), "AllParts");

    ViewInteraction startTime =
        onView(allOf(withId(R.id.music_player_seek_bar_start), isDisplayed()));
    startTime.check(matches(withText(containsString("00:0"))));

    ViewInteraction endTime = onView(allOf(withId(R.id.music_player_seek_bar_end), isDisplayed()));
    endTime.check(matches(withText(containsString("-00:"))));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      ViewInteraction speed = onView(allOf(withId(R.id.music_player_speed_value), isDisplayed()));
      speed.check(matches(withText("1x")));
    }

    ViewInteraction loading = onView(withId(R.id.music_player_loading));
    loading.check(matches(not(isDisplayed())));

    ViewInteraction playPause = onView(allOf(withId(R.id.music_player_play_pause), isDisplayed()));
    playPause.perform(click());
    assertTrue(musicService.isPlaying());

    ViewInteraction lead = onView(allOf(withId(R.id.music_player_play_lead), isDisplayed()));
    lead.perform(click());
    part.check(matches(withText("Lead")));
    assertEquals(musicService.getTrack().getPart(), "Lead");
    assertTrue(musicService.isPlaying());

    ViewInteraction bass = onView(allOf(withId(R.id.music_player_play_bass), isDisplayed()));
    bass.perform(click());
    part.check(matches(withText("Bass")));
    assertEquals(musicService.getTrack().getPart(), "Bass");
    assertTrue(musicService.isPlaying());

    ViewInteraction tenor = onView(allOf(withId(R.id.music_player_play_tenor), isDisplayed()));
    tenor.perform(click());
    part.check(matches(withText("Tenor")));
    assertEquals(musicService.getTrack().getPart(), "Tenor");
    assertTrue(musicService.isPlaying());

    ViewInteraction bari = onView(allOf(withId(R.id.music_player_play_bari), isDisplayed()));
    bari.perform(click());
    part.check(matches(withText("Bari")));
    assertEquals(musicService.getTrack().getPart(), "Bari");
    assertTrue(musicService.isPlaying());

    ViewInteraction musicPlayerClose =
        onView(allOf(withId(R.id.music_player_close), isDisplayed()));
    musicPlayerClose.perform(click());

    ViewInteraction toolbarControl = onView(allOf(withId(R.id.player_control), isDisplayed()));
    toolbarControl.perform(click());
    assertFalse(musicService.isPlaying());
    toolbarControl.perform(click());
    assertTrue(musicService.isPlaying());

    ViewInteraction toolbarClose = onView(allOf(withId(R.id.player_close), isDisplayed()));
    toolbarClose.perform(click());
    assertFalse(musicService.isPlaying());
    assertEquals(musicService.getDuration(), 0);
    assertEquals(musicService.getPosition(), 0);
    assertFalse(musicService.isLoading());
  }

  @Test
  public void testMediaPlayerLimitedTracks() {
    MusicService musicService = mActivityTestRule.getActivity().getMusicSrv();

    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("Through the Eyes of Love"), closeSoftKeyboard());

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    onView(allOf(withId(R.id.action_menu), withContentDescription("Menu"))).perform(click());

    SystemClock.sleep(500);

    onView(withId(R.id.tag_detail_track_all)).perform(click());

    SystemClock.sleep(1000);

    onView(withId(R.id.track_toolbar)).perform(click());

    assertTrue(musicService.isPlaying());

    onView(allOf(withId(R.id.music_player_play_pause), withContentDescription("Playback Control")))
        .perform(click());

    assertFalse(musicService.isPlaying());

    ViewInteraction title = onView(allOf(withId(R.id.music_player_title), isDisplayed()));
    title.check(matches(withText("Through the Eyes of Love")));
    assertEquals(musicService.getTrack().getTagTitle(), "Through the Eyes of Love");

    ViewInteraction part = onView(allOf(withId(R.id.music_player_part), isDisplayed()));
    part.check(matches(withText("AllParts")));
    assertEquals(musicService.getTrack().getPart(), "AllParts");

    ViewInteraction startTime =
        onView(allOf(withId(R.id.music_player_seek_bar_start), isDisplayed()));
    startTime.check(matches(withText(containsString("00:0"))));

    ViewInteraction endTime = onView(allOf(withId(R.id.music_player_seek_bar_end), isDisplayed()));
    endTime.check(matches(withText(containsString("-00:"))));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      ViewInteraction speed = onView(allOf(withId(R.id.music_player_speed_value), isDisplayed()));
      speed.check(matches(withText("1x")));
    }

    ViewInteraction loading = onView(withId(R.id.music_player_loading));
    loading.check(matches(not(isDisplayed())));

    ViewInteraction playPause = onView(allOf(withId(R.id.music_player_play_pause), isDisplayed()));
    playPause.perform(click());
    assertTrue(musicService.isPlaying());

    ViewInteraction lead = onView(withId(R.id.music_player_play_lead));
    lead.check(matches(not(isDisplayed())));

    ViewInteraction bass = onView(withId(R.id.music_player_play_bass));
    bass.check(matches(not(isDisplayed())));

    ViewInteraction tenor = onView(withId(R.id.music_player_play_tenor));
    tenor.check(matches(not(isDisplayed())));

    ViewInteraction bari = onView(withId(R.id.music_player_play_bari));
    bari.check(matches(not(isDisplayed())));

    musicService.seek(0);
    SystemClock.sleep(1000);
    startTime.check(matches(withSubstring("00:0")));
    assertTrue(musicService.isPlaying());

    musicService.seek(musicService.getDuration());
    SystemClock.sleep(1000);
    endTime.check(matches(withText("-00:00")));
    assertFalse(musicService.isPlaying());

    ViewInteraction all = onView(withId(R.id.music_player_play_all));
    all.perform(click());

    musicService.setBalance(0f, 1f);
    assertTrue(musicService.isPlaying());

    ViewInteraction balanceBar = onView(withId(R.id.music_player_balance));
    balanceBar.perform(scrubSeekBarAction(75));
    assertTrue(musicService.isPlaying());

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      ViewInteraction speedBar = onView(withId(R.id.music_player_speed));
      speedBar.perform(scrubSeekBarAction(1));
      assertTrue(musicService.isPlaying());

      ViewInteraction speedText = onView(withId(R.id.music_player_speed_value));
      speedText.check(matches(withText("0.5x")));
    }

    playPause.perform(click());

    musicService.seek(0);
    SystemClock.sleep(1000);
    startTime.check(matches(withText("00:00")));
    assertFalse(musicService.isPlaying());

    musicService.seek(3);
    SystemClock.sleep(1000);
    assertFalse(musicService.isPlaying());

    musicService.seek(musicService.getDuration());
    SystemClock.sleep(1000);
    endTime.check(matches(withText("-00:00")));
    assertFalse(musicService.isPlaying());

    all.perform(click());

    ViewInteraction musicPlayerClose =
        onView(allOf(withId(R.id.music_player_close), isDisplayed()));
    musicPlayerClose.perform(click());

    ViewInteraction toolbarControl = onView(allOf(withId(R.id.player_control), isDisplayed()));
    toolbarControl.perform(click());
    assertFalse(musicService.isPlaying());
    toolbarControl.perform(click());
    assertTrue(musicService.isPlaying());

    ViewInteraction toolbarClose = onView(allOf(withId(R.id.player_close), isDisplayed()));
    toolbarClose.perform(click());
    assertFalse(musicService.isPlaying());
    assertEquals(musicService.getDuration(), 0);
    assertEquals(musicService.getPosition(), 0);
    assertFalse(musicService.isLoading());
  }

  @Test
  public void testTrackMenu() {
    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("ebb tide"), closeSoftKeyboard());

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    selectTrackAtPosition(R.id.tag_detail_track_all, "AllParts");
    selectTrackAtPosition(R.id.tag_detail_track_tenor, "Tenor");
    selectTrackAtPosition(R.id.tag_detail_track_lead, "Lead");
    selectTrackAtPosition(R.id.tag_detail_track_bass, "Bass");
    selectTrackAtPosition(R.id.tag_detail_track_bari, "Bari");
    selectTrackAtPosition(R.id.tag_detail_track_other1, "Other1");

    onView(allOf(withId(R.id.player_close), withContentDescription("Close"))).perform(click());
  }

  private void selectTrackAtPosition(int id, String trackName) {
    onView(allOf(withId(R.id.action_menu), withContentDescription("Menu"))).perform(click());

    onView(withId(R.id.tag_detail_title)).perform(swipeUp());
    SystemClock.sleep(1000);

    onView(withId(id)).perform(click());

    SystemClock.sleep(1000);

    onView(allOf(withId(R.id.selected_track_part), withText(trackName)))
        .check(matches(withText(trackName)));
  }

  private static class SeekBarThumbCoordinatesProvider implements CoordinatesProvider {
    final int mProgress;

    SeekBarThumbCoordinatesProvider(int progress) {
      mProgress = progress;
    }

    private static float[] getVisibleLeftTop(View view) {
      final int[] xy = new int[2];
      view.getLocationOnScreen(xy);
      return new float[] {(float) xy[0], (float) xy[1]};
    }

    @Override
    public float[] calculateCoordinates(View view) {
      if (!(view instanceof SeekBar)) {
        throw new PerformException.Builder()
            .withViewDescription(HumanReadables.describe(view))
            .withCause(new RuntimeException("SeekBar expected"))
            .build();
      }
      SeekBar seekBar = (SeekBar) view;
      int width = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
      double progress = mProgress == 0 ? seekBar.getProgress() : mProgress;
      int xPosition = (int) (seekBar.getPaddingLeft() + width * progress / seekBar.getMax());
      float[] xy = getVisibleLeftTop(seekBar);
      return new float[] {xy[0] + xPosition, xy[1] + 10};
    }
  }
}
