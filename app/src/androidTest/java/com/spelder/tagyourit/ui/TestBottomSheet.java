package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import android.os.SystemClock;
import androidx.test.espresso.ViewInteraction;
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
        .perform(replaceText("like leaves"), closeSoftKeyboard());

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(click());

    ViewInteraction actionMenuItemView =
        onView(allOf(withId(R.id.action_menu), withContentDescription("Menu")));
    actionMenuItemView.perform(click());

    onView(withId(R.id.title)).check(matches(withText("Like Leaves will Fall")));

    onView(withId(R.id.tag_list_arranger)).check(matches(withText("Jake Tickner")));

    onView(withId(R.id.tag_list_ratingBar)).check(matches(isDisplayed()));

    onView(allOf(withId(R.id.label), withText("Major:Ab"))).check(matches(withText("Major:Ab")));

    onView(allOf(withId(R.id.label), withText("All Parts"))).check(matches(withText("All Parts")));

    SystemClock.sleep(500);

    onView(withId(R.id.bottom_sheet)).perform(swipeUp());

    onView(allOf(withId(R.id.label), withText("Rate"))).check(matches(withText("Rate")));

    onView(allOf(withId(R.id.label), withText("Share"))).check(matches(withText("Share")));

    actionMenuItemView.perform(click());

    onView(withId(R.id.pdfView)).check(matches(isDisplayed()));

    actionMenuItemView.perform(click());

    onView(withId(R.id.bottom_sheet)).check(matches(isDisplayed()));
  }

  @Test
  public void imageTest() {
    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("smile"), closeSoftKeyboard());

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(click());

    onView(withId(R.id.image)).check(matches(isDisplayed()));

    onView(allOf(withId(R.id.action_menu), withContentDescription("Menu"))).perform(click());

    onView(withId(R.id.title)).check(matches(withText("Smile")));

    onView(withId(R.id.tag_list_ratingBar)).check(matches(isDisplayed()));

    onView(withId(R.id.tag_list_arranger)).check(matches(withText("Bobby Gray, Jr")));

    onView(allOf(withId(R.id.label), withText("Major:G"))).check(matches(withText("Major:G")));

    onView(allOf(withId(R.id.label), withText("All Parts"))).check(matches(withText("All Parts")));

    onView(allOf(withId(R.id.label), withText("Tenor"))).check(matches(withText("Tenor")));

    onView(allOf(withId(R.id.label), withText("Lead"))).check(matches(withText("Lead")));

    onView(allOf(withId(R.id.label), withText("Bass"))).check(matches(withText("Bass")));

    onView(allOf(withId(R.id.label), withText("Bari"))).check(matches(withText("Bari")));

    onView(withId(R.id.bottom_sheet)).perform(swipeUp());

    onView(allOf(withId(R.id.label), withText("Rate"))).check(matches(withText("Rate")));
  }
}
