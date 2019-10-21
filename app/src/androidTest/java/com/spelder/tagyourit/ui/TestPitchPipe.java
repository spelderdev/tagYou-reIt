package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.os.SystemClock;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.pitch.Pitch;
import com.spelder.tagyourit.pitch.PitchPlayer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestPitchPipe {
  @Rule
  public ActivityTestRule<MainActivity> mActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  private ViewInteraction centerButton;

  @Test
  public void pitchPipeView() {
    onView(withId(R.id.nav_pitch_pipe)).perform(click());

    centerButton = onView(allOf(withId(R.id.pitch_pipe_center), isDisplayed()));
    assertFalse(PitchPlayer.isPlaying());
    assertNull(PitchPlayer.getPlayingPitch());
    centerButton.perform(click());
    assertTrue(PitchPlayer.isPlaying());
    assertEquals(PitchPlayer.getPlayingPitch(), Pitch.A);
    centerButton.perform(click());
    assertFalse(PitchPlayer.isPlaying());

    centerButton.check(matches(withText("A")));

    testPitch(R.id.pitch_pipe_c, Pitch.C);
    testPitch(R.id.pitch_pipe_db, Pitch.D_FLAT);
    testPitch(R.id.pitch_pipe_d, Pitch.D);
    testPitch(R.id.pitch_pipe_eb, Pitch.E_FLAT);
    testPitch(R.id.pitch_pipe_e, Pitch.E);
    testPitch(R.id.pitch_pipe_f, Pitch.F);
    testPitch(R.id.pitch_pipe_gb, Pitch.G_FLAT);
    testPitch(R.id.pitch_pipe_g, Pitch.G);
    testPitch(R.id.pitch_pipe_ab, Pitch.A_FLAT);
    testPitch(R.id.pitch_pipe_a, Pitch.A);
    testPitchLongClick(R.id.pitch_pipe_bb, Pitch.B_FLAT);
    testPitchLongClick(R.id.pitch_pipe_b, Pitch.B);
  }

  private void testPitch(int id, Pitch pitch) {
    onView(allOf(withId(id), isDisplayed())).perform(click());

    assertFalse(PitchPlayer.isPlaying());
    centerButton.perform(click());
    assertTrue(PitchPlayer.isPlaying());
    assertEquals(PitchPlayer.getPlayingPitch(), pitch);
    centerButton.perform(click());
    assertFalse(PitchPlayer.isPlaying());

    centerButton.check(matches(withText(pitch.getDisplay())));
  }

  private void testPitchLongClick(int id, Pitch pitch) {
    onView(allOf(withId(id), isDisplayed())).perform(click());

    assertFalse(PitchPlayer.isPlaying());
    centerButton.perform(click());
    assertTrue(PitchPlayer.isPlaying());
    assertEquals(PitchPlayer.getPlayingPitch(), pitch);

    SystemClock.sleep(10000);
    assertTrue(PitchPlayer.isPlaying());

    centerButton.perform(click());
    assertFalse(PitchPlayer.isPlaying());

    centerButton.check(matches(withText(pitch.getDisplay())));
  }

  @Test
  public void pitchPipeTag() {
    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("like leaves tickner"));

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    onView(withId(R.id.action_menu)).perform(click());

    assertFalse(PitchPlayer.isPlaying());

    onView(allOf(withId(R.id.tag_detail_key_button), withText("Major:Ab"))).perform(click());

    SystemClock.sleep(2000);

    assertTrue(PitchPlayer.isPlaying());
    assertNotNull(PitchPlayer.getPlayingPitch());
  }
}
