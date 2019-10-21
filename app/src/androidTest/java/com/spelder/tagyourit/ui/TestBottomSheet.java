package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import com.spelder.tagyourit.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestBottomSheet {
  @Rule
  public ActivityTestRule<MainActivity> mActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  @Test
  public void bottomSheetComponents() {
    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("Like Leaves will Fall Tickner"), closeSoftKeyboard());

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    onView(withId(R.id.pdfView)).check(matches(isDisplayed()));

    onView(allOf(withId(R.id.action_menu), withContentDescription("Menu"))).perform(click());

    onView(withId(R.id.tag_detail_title)).check(matches(withText("Like Leaves will Fall")));
    onView(withId(R.id.tag_detail_arranger)).check(matches(withText("Jake Tickner")));
    onView(withId(R.id.tag_detail_rating)).check(matches(isDisplayed()));
    onView(withId(R.id.tag_detail_key_button)).check(matches(withText("Major:Ab")));
    onView(withId(R.id.tag_detail_track_all)).check(matches(withText("All Parts")));
    onView(allOf(withId(R.id.tag_detail_rate_button), withText("Rate")))
        .check(matches(withText("Rate")));
    onView(allOf(withId(R.id.tag_detail_share_button), withText("Share")))
        .check(matches(withText("Share")));
  }

  @Test
  public void imageTest() {
    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("Smile Bobby Gray"), closeSoftKeyboard());

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    onView(withId(R.id.image)).check(matches(isDisplayed()));

    onView(allOf(withId(R.id.action_menu), withContentDescription("Menu"))).perform(click());

    onView(withId(R.id.tag_detail_title)).check(matches(withText("Smile")));
    onView(withId(R.id.tag_detail_rating)).check(matches(isDisplayed()));
    onView(withId(R.id.tag_detail_arranger)).check(matches(withText("Bobby Gray, Jr")));
    onView(allOf(withId(R.id.tag_detail_key_button), withText("Major:G")))
        .check(matches(withText("Major:G")));
    onView(allOf(withId(R.id.tag_detail_track_all), withText("All Parts")))
        .check(matches(withText("All Parts")));
    onView(allOf(withId(R.id.tag_detail_track_tenor), withText("Tenor")))
        .check(matches(withText("Tenor")));
    onView(allOf(withId(R.id.tag_detail_track_lead), withText("Lead")))
        .check(matches(withText("Lead")));
    onView(allOf(withId(R.id.tag_detail_track_bass), withText("Bass")))
        .check(matches(withText("Bass")));
    onView(allOf(withId(R.id.tag_detail_track_bari), withText("Bari")))
        .check(matches(withText("Bari")));
    onView(allOf(withId(R.id.tag_detail_rate_button), withText("Rate")))
        .check(matches(withText("Rate")));
  }
}
