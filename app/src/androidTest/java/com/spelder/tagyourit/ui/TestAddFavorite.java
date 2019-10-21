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
import static org.junit.Assert.assertEquals;

import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.networking.api.SortBuilder;
import com.spelder.tagyourit.networking.api.SortBy;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import java.io.File;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestAddFavorite {
  @Rule
  public final ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup
            && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }

  @Before
  public void init() {
    TagDb db = new TagDb(mActivityRule.getActivity());
    db.deleteAllFavorites();

    FilterBuilder filter = new FilterBuilder(mActivityRule.getActivity());
    filter.applyDefaultFilter();

    SortBuilder sort = new SortBuilder(mActivityRule.getActivity());
    sort.setSortBy(SortBy.TITLE);
  }

  @Test
  public void addFavoriteBrowse() {
    String tag_title = "'Less You Listen";

    // Start the screen of your activity.
    onView(withId(R.id.nav_browse)).perform(click());

    onView(allOf(withId(R.id.tag_list_title), withText(tag_title))).perform(click());

    onView(withId(R.id.action_favorite)).perform(click());

    onView(withId(R.id.nav_favorite)).perform(click());

    unFavoriteTag(tag_title);
  }

  @Test
  public void addFavoritesSearch() {
    String tagTitle1 = "Lost";
    String tagTitle2 = "Love Me and the World is Mine";
    String tagTitle3 = "Tonight, Tonight";

    favoriteTag(tagTitle1 + " Soren");
    favoriteTag(tagTitle2 + " David");
    favoriteTag(tagTitle3 + " Island Boys");

    onView(withId(R.id.nav_favorite)).perform(click());

    unFavoriteTag(tagTitle1);
    unFavoriteTag(tagTitle2);
    unFavoriteTag(tagTitle3);
  }

  @Test
  public void testDownloadFavorite() {
    String tagTitle1 = "Lost";
    String tagTitle2 = "Like Leaves will Fall";

    favoriteTag(tagTitle1 + " Soren");
    favoriteTag(tagTitle2 + " Tickner");

    onView(withId(R.id.nav_settings)).perform(click());

    ViewInteraction downloadToggle =
        onView(
            childAtPosition(
                allOf(
                    withId(R.id.recycler_view),
                    childAtPosition(withId(android.R.id.list_container), 0)),
                3));
    downloadToggle.perform(click());

    SystemClock.sleep(5000);

    Tag t = new Tag();
    t.setDownloaded(true);
    File dir = new File(t.getSheetMusicDirectory(mActivityRule.getActivity()));
    assertEquals(0, dir.listFiles().length);

    downloadToggle.perform(click());
    SystemClock.sleep(8000);

    assertEquals(2, dir.listFiles().length);

    onView(withId(R.id.nav_favorite)).perform(click());

    unFavoriteTag(tagTitle1);
    unFavoriteTag(tagTitle2);

    ViewInteraction progressBar = onView(allOf(withId(android.R.id.empty), isDisplayed()));
    progressBar.check(matches(isDisplayed()));
  }

  private void favoriteTag(String tagTitle) {
    onView(withId(R.id.search_button)).perform(click());

    onView(withId(R.id.search_src_text)).perform(replaceText(tagTitle), closeSoftKeyboard());

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    onView(withId(R.id.action_favorite)).perform(click());

    onView(allOf(withContentDescription("Navigate up"), isDisplayed())).perform(click());
  }

  private void unFavoriteTag(String tagTitle) {
    onView(allOf(withId(R.id.tag_list_title), withText(tagTitle)))
        .check(matches(isCompletelyDisplayed()))
        .perform(click());

    onView(withId(R.id.action_unfavorite)).perform(click());

    ViewInteraction navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());
  }
}
