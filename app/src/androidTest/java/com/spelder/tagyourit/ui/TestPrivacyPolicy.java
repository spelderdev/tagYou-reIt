package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import com.spelder.tagyourit.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestPrivacyPolicy {
  @Rule
  public ActivityTestRule<MainActivity> mActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  @Test
  public void testPrivacyPolicy() {
    onView(withContentDescription("Open navigation drawer")).perform(click());

    onView(withText("Settings")).perform(click());

    onView(withId(R.id.recycler_view)).perform(swipeUp());

    onView(withText("Privacy Policy")).perform(click());

    onView(withText("Privacy Policy")).check(matches(isDisplayed()));

    onView(withId(R.id.privacy_policy)).check(matches(isDisplayed()));

    onView(withContentDescription("Navigate up")).perform(click());

    onView(withText("Privacy Policy")).check(matches(isDisplayed()));
  }
}
