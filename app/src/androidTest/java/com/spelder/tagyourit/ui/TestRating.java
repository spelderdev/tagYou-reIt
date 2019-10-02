package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import android.os.SystemClock;
import android.view.View;
import android.widget.RatingBar;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import com.spelder.tagyourit.R;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestRating {
  @Rule
  public ActivityTestRule<MainActivity> mActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  @Test
  public void rateTag() {
    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("like leaves"));

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(click());

    checkRating(0);
    checkRating(4);
    checkRating(5);
  }

  private void checkRating(int stars) {
    onView(allOf(withId(R.id.action_menu), withContentDescription("Menu"))).perform(click());

    SystemClock.sleep(500);
    onView(withId(R.id.bottom_sheet)).perform(swipeUp());

    onData(anything()).inAdapterView(withId(R.id.list)).atPosition(13).perform(click());

    onView(withId(R.id.alertTitle)).check(matches(withText("Rate Like Leaves will Fall")));

    onView(withId(R.id.dialog_rating_bar)).perform(new SetRating(stars));

    onView(allOf(withId(android.R.id.button1), withText("Rate"))).perform(scrollTo(), click());

    onView(allOf(withId(R.id.action_menu), withContentDescription("Menu"))).perform(click());

    SystemClock.sleep(500);
    onView(withId(R.id.bottom_sheet)).perform(swipeUp());

    onData(anything()).inAdapterView(withId(R.id.list)).atPosition(13).perform(click());

    onView(withId(R.id.dialog_rating_bar)).check(new CheckRating(stars));

    onView(allOf(withId(android.R.id.button2), withText("Cancel"))).perform(scrollTo(), click());
  }

  private final class CheckRating implements ViewAssertion {
    private final int starCount;

    CheckRating(int starCount) {
      this.starCount = starCount;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
      RatingBar ratingBar = (RatingBar) view;
      ViewMatchers.assertThat(
          "RatingBar star count", (int) ratingBar.getRating(), CoreMatchers.equalTo(starCount));
    }
  }

  private final class SetRating implements ViewAction {
    private final int stars;

    SetRating(int stars) {
      this.stars = stars;
    }

    @Override
    public Matcher<View> getConstraints() {
      return ViewMatchers.isAssignableFrom(RatingBar.class);
    }

    @Override
    public String getDescription() {
      return "Custom view action to set rating.";
    }

    @Override
    public void perform(UiController uiController, View view) {
      RatingBar ratingBar = (RatingBar) view;
      ratingBar.setRating(stars);
    }
  }
}
