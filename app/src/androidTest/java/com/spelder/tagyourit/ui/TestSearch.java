package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import android.os.SystemClock;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import com.spelder.tagyourit.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestSearch {
  @Rule
  public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

  @Test
  public void search() {
    final String title = "Like Leaves will Fall";

    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(typeText("like leaves"));

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(click());

    onView(withId(R.id.action_menu)).perform(click());

    onView(withId(R.id.title)).check(matches(withText(title)));

    SystemClock.sleep(500);

    onView(allOf(withId(R.id.label), withText("All Parts"))).perform(click());

    SystemClock.sleep(2000);

    onView(withId(R.id.player_control)).perform(click());
    onView(withId(R.id.player_close)).perform(click());
  }
}
