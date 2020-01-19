package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.os.SystemClock;
import android.view.View;
import android.widget.ListView;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.networking.api.SortBuilder;
import com.spelder.tagyourit.networking.api.SortBy;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestFilter {
  @Rule
  public final ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  @Before
  public void init() {
    FilterBuilder filter = new FilterBuilder(mActivityRule.getActivity());
    filter.applyDefaultFilter();

    SortBuilder sort = new SortBuilder(mActivityRule.getActivity());
    sort.setSortBy(SortBy.DOWNLOAD);

    TagDb db = new TagDb(mActivityRule.getActivity());
    db.deleteAllFavorites();
  }

  private void resetFilter() {
    FilterBuilder filter = new FilterBuilder(mActivityRule.getActivity());
    filter.applyDefaultFilter();
  }

  @Test
  public void filterTest() {
    String tagTitle1 = "Only Love Could Hurt Like This";
    String tagTitle2 = "Ebb Tide";
    String tagTitle3 = "Ricola!";

    TestUtility.favoriteTag(tagTitle1 + " Terry");
    TestUtility.favoriteTag(tagTitle2);
    TestUtility.favoriteTag(tagTitle3 + " Ethan");

    onView(withId(R.id.nav_lists)).perform(click());

    onView(allOf(withId(R.id.filter_parts), isDisplayed())).perform(scrollTo(), click());

    onView(withId(R.id.filter_parts_five)).perform(click());

    onView(matcherAtCount(withId(R.id.tag_list_title), 1)).check(matches(withText(tagTitle2)));

    resetFilter();

    onView(allOf(withId(R.id.filter_sheet_music), isDisplayed())).perform(scrollTo(), click());

    onView(matcherAtCount(withId(R.id.tag_list_title), 1)).check(matches(withText(tagTitle2)));

    onView(matcherAtCount(withId(R.id.tag_list_title), 2)).check(matches(withText(tagTitle1)));

    onView(withId(android.R.id.list)).check(ViewAssertions.matches(withListSize(2)));

    resetFilter();

    onView(allOf(withId(R.id.filter_learning_tracks), isDisplayed())).perform(scrollTo(), click());

    onView(withId(android.R.id.list)).check(ViewAssertions.matches(withListSize(3)));

    resetFilter();

    onView(allOf(withId(R.id.filter_sheet_music), isDisplayed())).perform(swipeLeft());
    SystemClock.sleep(1000);

    onView(allOf(withId(R.id.filter_key), isDisplayed())).perform(scrollTo(), click());

    onView(allOf(withId(R.id.filter_key_any), isDisplayed())).perform(swipeUp());
    SystemClock.sleep(1000);

    onView(allOf(withId(R.id.filter_key_d), isDisplayed())).perform(swipeUp());
    SystemClock.sleep(1000);

    onView(allOf(withId(R.id.filter_key_e), isDisplayed())).perform(swipeUp());
    SystemClock.sleep(1000);

    onView(allOf(withId(R.id.filter_key_f), isDisplayed())).perform(swipeUp());
    SystemClock.sleep(1000);

    onView(allOf(withId(R.id.filter_key_gb), isDisplayed())).perform(swipeUp());
    SystemClock.sleep(1000);

    onView(withId(R.id.filter_key_g)).perform(click());

    onView(matcherAtCount(withId(R.id.tag_list_title), 1)).check(matches(withText(tagTitle3)));

    onView(withId(android.R.id.list)).check(ViewAssertions.matches(withListSize(1)));

    resetFilter();

    onView(allOf(withId(R.id.filter_learning_tracks), isDisplayed())).perform(swipeLeft());

    onView(allOf(withId(R.id.filter_type), isDisplayed())).perform(scrollTo(), click());

    onView(withId(R.id.filter_type_other_male)).perform(click());

    onView(matcherAtCount(withId(R.id.tag_list_title), 1)).check(matches(withText(tagTitle2)));

    onView(withId(android.R.id.list)).check(ViewAssertions.matches(withListSize(1)));

    resetFilter();

    onView(allOf(withId(R.id.filter_rating), isDisplayed())).perform(scrollTo(), click());

    onView(allOf(withId(R.id.filter_rating_any), isDisplayed())).perform(swipeUp());
    SystemClock.sleep(1000);

    onView(allOf(withId(R.id.filter_rating_3), isDisplayed())).perform(swipeUp());
    SystemClock.sleep(1000);

    onView(allOf(withId(R.id.filter_rating_3_5), isDisplayed())).perform(swipeUp());
    SystemClock.sleep(1000);

    onView(withId(R.id.filter_rating_4)).perform(click());

    onView(withId(android.R.id.empty)).check(matches(isDisplayed()));

    resetFilter();

    onView(allOf(withId(R.id.filter_rating), isDisplayed())).perform(swipeLeft());
    SystemClock.sleep(1000);

    onView(allOf(withId(R.id.filter_collection), isDisplayed())).perform(scrollTo(), click());

    onView(withId(R.id.filter_collection_classic)).perform(click());

    onView(withId(android.R.id.empty)).check(matches(isDisplayed()));

    resetFilter();

    TestUtility.unFavoriteTag(tagTitle1);
    TestUtility.unFavoriteTag(tagTitle2);
    TestUtility.unFavoriteTag(tagTitle3);
  }

  public Matcher<View> withListSize(final int size) {
    return new TypeSafeMatcher<View>() {
      @Override
      public boolean matchesSafely(final View view) {
        return ((ListView) view).getCount() == size + 1;
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("ListView should have " + size + " items");
      }
    };
  }

  private <T> Matcher<T> matcherAtCount(final Matcher<T> matcher, int id) {
    return new BaseMatcher<T>() {
      int count = 1;

      @Override
      public boolean matches(final Object item) {
        if (matcher.matches(item)) {
          if (count == id) {
            count++;
            return true;
          }
          count++;
        }

        return false;
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("should return first matching item");
      }
    };
  }
}
