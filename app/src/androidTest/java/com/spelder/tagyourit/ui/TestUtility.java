package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.ViewInteraction;
import com.spelder.tagyourit.R;

public class TestUtility {
  public static void favoriteTag(String tagTitle) {
    onView(withId(R.id.search_button)).perform(click());

    onView(withId(R.id.search_src_text)).perform(replaceText(tagTitle), closeSoftKeyboard());

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    onView(withId(R.id.action_add_to_default_list)).perform(click());

    onView(allOf(withContentDescription("Navigate up"), isDisplayed())).perform(click());
  }

  public static void unFavoriteTag(String tagTitle) {
    onView(allOf(withId(R.id.tag_list_title), withText(tagTitle)))
        .check(matches(isCompletelyDisplayed()))
        .perform(click());

    onView(withId(R.id.action_delete_from_default_list)).perform(click());

    ViewInteraction navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());
  }
}
