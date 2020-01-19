package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.spelder.tagyourit.action.OrientationChangeAction.orientationLandscape;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

import android.os.Build;
import android.os.SystemClock;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.networking.api.SortBuilder;
import com.spelder.tagyourit.networking.api.SortBy;
import com.spelder.tagyourit.networking.api.filter.FilterBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestSheetMusic {
  @Rule
  public final ActivityTestRule<MainActivity> mActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  @Before
  public void init() {
    TagDb db = new TagDb(mActivityTestRule.getActivity());
    db.deleteAllFavorites();

    FilterBuilder filter = new FilterBuilder(mActivityTestRule.getActivity());
    filter.applyDefaultFilter();

    SortBuilder sort = new SortBuilder(mActivityTestRule.getActivity());
    sort.setSortBy(SortBy.TITLE);
  }

  @Test
  public void pdfViewTest() {
    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("like leaves tickner"));

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    ViewInteraction actionMenuItemView =
        onView(allOf(withId(R.id.action_menu), withContentDescription("Menu")));

    ViewInteraction pdfView = onView(allOf(withId(R.id.pdfView), isDisplayed()));
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
      pdfView.perform(click());
      SystemClock.sleep(2000);

      actionMenuItemView.check(matches(not(isDisplayed())));

      pdfView.perform(click());
      SystemClock.sleep(1000);
    }

    pdfView.perform(doubleClick());
    SystemClock.sleep(100);
    pdfView.perform(swipeLeft());
    pdfView.perform(swipeRight());
    SystemClock.sleep(100);
    pdfView.perform(doubleClick());
    SystemClock.sleep(100);

    onView(isRoot()).perform(orientationLandscape());
    SystemClock.sleep(100);

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
      pdfView.perform(click());
      SystemClock.sleep(2000);

      actionMenuItemView.check(matches(not(isDisplayed())));

      pdfView.perform(click());
      SystemClock.sleep(1000);
    }

    pdfView.perform(doubleClick());
    SystemClock.sleep(100);
    pdfView.perform(swipeLeft());
    pdfView.perform(swipeRight());
    SystemClock.sleep(100);
    pdfView.perform(doubleClick());
  }

  @Test
  public void imageViewTest() {
    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("Smile Bobby Gray"));

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    ViewInteraction actionMenuItemView =
        onView(allOf(withId(R.id.action_menu), withContentDescription("Menu")));

    ViewInteraction imageView = onView(allOf(withId(R.id.image), isDisplayed()));
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
      imageView.perform(click());
      SystemClock.sleep(2000);

      actionMenuItemView.check(matches(not(isDisplayed())));

      imageView.perform(click());
      SystemClock.sleep(1000);
    }

    imageView.perform(doubleClick());
    SystemClock.sleep(100);
    imageView.perform(swipeLeft());
    imageView.perform(swipeRight());
    SystemClock.sleep(100);
    imageView.perform(doubleClick());
    SystemClock.sleep(100);

    onView(isRoot()).perform(orientationLandscape());
    SystemClock.sleep(100);

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
      imageView.perform(click());
      SystemClock.sleep(2000);

      actionMenuItemView.check(matches(not(isDisplayed())));

      imageView.perform(click());
      SystemClock.sleep(1000);
    }

    imageView.perform(doubleClick());
    SystemClock.sleep(100);
    imageView.perform(swipeLeft());
    imageView.perform(swipeRight());
    SystemClock.sleep(100);
    imageView.perform(doubleClick());
  }

  @Test
  public void testNoSheetMusic() {
    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(com.google.android.material.R.id.search_src_text))
        .perform(replaceText("ricola!"));

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    ViewInteraction textView = onView(allOf(withText("No Sheet Music Available"), isDisplayed()));
    textView.check(matches(withText("No Sheet Music Available")));
  }
}
