package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.Tag;
import java.io.File;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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

  @Test
  public void addFavoriteBrowse() {
    TagDb db = new TagDb(mActivityRule.getActivity());
    db.deleteAllFavorites();

    String tag_title = "'Less You Listen";

    // Open Drawer to click on navigation.
    onView(ViewMatchers.withId(R.id.drawer_layout))
        .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
        .perform(DrawerActions.open()); // Open Drawer

    // Start the screen of your activity.
    onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_browse));
    SystemClock.sleep(1000);

    onView(withId(R.id.pager)).perform(swipeLeft());
    SystemClock.sleep(200);

    onView(withId(R.id.pager)).perform(swipeLeft());
    SystemClock.sleep(200);

    onView(withId(R.id.pager)).perform(swipeLeft());
    SystemClock.sleep(200);

    onView(allOf(withId(R.id.tag_list_title), withText(tag_title))).perform(click());

    onView(withId(R.id.action_favorite)).perform(click());

    onView(withId(R.id.drawer_layout))
        .check(matches(isClosed(Gravity.LEFT)))
        .perform(DrawerActions.open());

    onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_favorite));
    SystemClock.sleep(1000);

    onView(allOf(withId(R.id.tag_list_title), withText(tag_title)))
        .check(matches(isCompletelyDisplayed()))
        .perform(click());

    onView(withId(R.id.action_unfavorite)).perform(click());
  }

  @Test
  public void addFavoriteSearch() {
    TagDb db = new TagDb(mActivityRule.getActivity());
    db.deleteAllFavorites();

    ViewInteraction appCompatImageView =
        onView(
            allOf(
                withId(R.id.search_button),
                withContentDescription("Search"),
                childAtPosition(
                    allOf(withId(R.id.search_bar), childAtPosition(withId(R.id.action_search), 0)),
                    1),
                isDisplayed()));
    appCompatImageView.perform(click());

    ViewInteraction searchAutoComplete =
        onView(
            allOf(
                withId(R.id.search_src_text),
                childAtPosition(
                    allOf(
                        withId(R.id.search_plate),
                        childAtPosition(withId(R.id.search_edit_frame), 1)),
                    0),
                isDisplayed()));
    searchAutoComplete.perform(replaceText("love me and the world"), closeSoftKeyboard());

    DataInteraction linearLayout =
        onData(anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)))
            .atPosition(0);
    linearLayout.perform(click());

    ViewInteraction imageView =
        onView(
            allOf(
                withId(R.id.image),
                childAtPosition(
                    allOf(withId(R.id.sheet_view), childAtPosition(withId(R.id.bottom_sheet), 0)),
                    0),
                isDisplayed()));
    imageView.check(matches(isDisplayed()));

    ViewInteraction actionMenuItemView =
        onView(
            allOf(
                withId(R.id.action_favorite),
                withContentDescription("Favorite"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0),
                isDisplayed()));
    actionMenuItemView.perform(click());

    ViewInteraction navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());
    navUp.perform(click());

    ViewInteraction navDrawer =
        onView(allOf(withContentDescription("Open navigation drawer"), isDisplayed()));
    navDrawer.perform(click());

    ViewInteraction navigationMenuItemView =
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(withId(R.id.nav_view), 0)),
                    2),
                isDisplayed()));
    navigationMenuItemView.perform(click());

    ViewInteraction textView2 =
        onView(
            allOf(
                withId(R.id.tag_list_title),
                withText("Love Me and the World is Mine"),
                isDisplayed()));
    textView2.check(matches(withText("Love Me and the World is Mine")));
    textView2.perform(click());

    ViewInteraction actionMenuItemView2 =
        onView(
            allOf(
                withId(R.id.action_unfavorite),
                withContentDescription("UnFavorite"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0),
                isDisplayed()));
    actionMenuItemView2.perform(click());
  }

  @Test
  public void addMultipleFavorites() {
    TagDb db = new TagDb(mActivityRule.getActivity());
    db.deleteAllFavorites();

    ViewInteraction appCompatImageView =
        onView(
            allOf(
                withId(R.id.search_button),
                withContentDescription("Search"),
                childAtPosition(
                    allOf(withId(R.id.search_bar), childAtPosition(withId(R.id.action_search), 0)),
                    1),
                isDisplayed()));
    appCompatImageView.perform(click());

    ViewInteraction searchAutoComplete =
        onView(
            allOf(
                withId(R.id.search_src_text),
                childAtPosition(
                    allOf(
                        withId(R.id.search_plate),
                        childAtPosition(withId(R.id.search_edit_frame), 1)),
                    0),
                isDisplayed()));
    searchAutoComplete.perform(replaceText("love me and the world"), closeSoftKeyboard());

    DataInteraction linearLayout =
        onData(anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)))
            .atPosition(0);
    linearLayout.perform(click());

    ViewInteraction actionMenuItemView =
        onView(
            allOf(
                withId(R.id.action_favorite),
                withContentDescription("Favorite"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0),
                isDisplayed()));
    actionMenuItemView.perform(click());

    ViewInteraction navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());

    ViewInteraction appCompatImageView2 =
        onView(
            allOf(
                withId(R.id.search_button),
                withContentDescription("Search"),
                childAtPosition(
                    allOf(withId(R.id.search_bar), childAtPosition(withId(R.id.action_search), 0)),
                    1),
                isDisplayed()));
    appCompatImageView2.perform(click());

    ViewInteraction searchAutoComplete2 =
        onView(
            allOf(
                withId(R.id.search_src_text),
                childAtPosition(
                    allOf(
                        withId(R.id.search_plate),
                        childAtPosition(withId(R.id.search_edit_frame), 1)),
                    0),
                isDisplayed()));
    searchAutoComplete2.perform(replaceText("lost"), closeSoftKeyboard());

    DataInteraction linearLayout2 =
        onData(anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)))
            .atPosition(0);
    linearLayout2.perform(click());

    ViewInteraction actionMenuItemView2 =
        onView(
            allOf(
                withId(R.id.action_favorite),
                withContentDescription("Favorite"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0),
                isDisplayed()));
    actionMenuItemView2.perform(click());

    navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());

    ViewInteraction appCompatImageView3 =
        onView(
            allOf(
                withId(R.id.search_button),
                withContentDescription("Search"),
                childAtPosition(
                    allOf(withId(R.id.search_bar), childAtPosition(withId(R.id.action_search), 0)),
                    1),
                isDisplayed()));
    appCompatImageView3.perform(click());

    ViewInteraction searchAutoComplete3 =
        onView(
            allOf(
                withId(R.id.search_src_text),
                childAtPosition(
                    allOf(
                        withId(R.id.search_plate),
                        childAtPosition(withId(R.id.search_edit_frame), 1)),
                    0),
                isDisplayed()));
    searchAutoComplete3.perform(replaceText("tonight"), closeSoftKeyboard());

    DataInteraction linearLayout3 =
        onData(anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)))
            .atPosition(1);
    linearLayout3.perform(click());

    ViewInteraction actionMenuItemView3 =
        onView(
            allOf(
                withId(R.id.action_favorite),
                withContentDescription("Favorite"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0),
                isDisplayed()));
    actionMenuItemView3.perform(click());

    navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());
    navUp.perform(click());

    ViewInteraction navDrawer =
        onView(allOf(withContentDescription("Open navigation drawer"), isDisplayed()));
    navDrawer.perform(click());

    ViewInteraction navigationMenuItemView =
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(withId(R.id.nav_view), 0)),
                    2),
                isDisplayed()));
    navigationMenuItemView.perform(click());

    ViewInteraction textView =
        onView(allOf(withId(R.id.tag_list_title), withText("Lost"), isDisplayed()));
    textView.check(matches(withText("Lost")));

    ViewInteraction textView2 =
        onView(
            allOf(
                withId(R.id.tag_list_title),
                withText("Love Me and the World is Mine"),
                isDisplayed()));
    textView2.check(matches(withText("Love Me and the World is Mine")));

    ViewInteraction textView3 =
        onView(allOf(withId(R.id.tag_list_title), withText("Tonight, Tonight"), isDisplayed()));
    textView3.check(matches(withText("Tonight, Tonight")));

    DataInteraction linearLayout4 =
        onData(anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)))
            .atPosition(1);
    linearLayout4.perform(click());

    ViewInteraction actionMenuItemView4 =
        onView(
            allOf(
                withId(R.id.action_unfavorite),
                withContentDescription("UnFavorite"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0),
                isDisplayed()));
    actionMenuItemView4.perform(click());

    navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());

    DataInteraction linearLayout5 =
        onData(anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)))
            .atPosition(1);
    linearLayout5.perform(click());

    ViewInteraction actionMenuItemView5 =
        onView(
            allOf(
                withId(R.id.action_unfavorite),
                withContentDescription("UnFavorite"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0),
                isDisplayed()));
    actionMenuItemView5.perform(click());

    navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());

    DataInteraction linearLayout6 =
        onData(anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)))
            .atPosition(0);
    linearLayout6.perform(click());

    ViewInteraction actionMenuItemView6 =
        onView(
            allOf(
                withId(R.id.action_unfavorite),
                withContentDescription("UnFavorite"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0),
                isDisplayed()));
    actionMenuItemView6.perform(click());

    navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());
  }

  @Test
  public void testDownloadFavorite() {
    TagDb db = new TagDb(mActivityRule.getActivity());
    db.deleteAllFavorites();

    ViewInteraction search =
        onView(
            allOf(
                withId(R.id.search_button),
                withContentDescription("Search"),
                childAtPosition(
                    allOf(withId(R.id.search_bar), childAtPosition(withId(R.id.action_search), 0)),
                    1),
                isDisplayed()));
    search.perform(click());

    ViewInteraction searchAutoComplete =
        onView(
            allOf(
                withId(R.id.search_src_text),
                childAtPosition(
                    allOf(
                        withId(R.id.search_plate),
                        childAtPosition(withId(R.id.search_edit_frame), 1)),
                    0),
                isDisplayed()));
    searchAutoComplete.perform(replaceText("leaves"), closeSoftKeyboard());

    DataInteraction linearLayout =
        onData(anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)))
            .atPosition(1);
    linearLayout.perform(click());

    ViewInteraction favorite =
        onView(
            allOf(
                withId(R.id.action_favorite),
                withContentDescription("Favorite"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 1), 0),
                isDisplayed()));
    favorite.perform(click());

    ViewInteraction navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());

    search =
        onView(
            allOf(
                withId(R.id.search_button),
                withContentDescription("Search"),
                childAtPosition(
                    allOf(withId(R.id.search_bar), childAtPosition(withId(R.id.action_search), 0)),
                    1),
                isDisplayed()));
    search.perform(click());

    searchAutoComplete =
        onView(
            allOf(
                withId(R.id.search_src_text),
                childAtPosition(
                    allOf(
                        withId(R.id.search_plate),
                        childAtPosition(withId(R.id.search_edit_frame), 1)),
                    0),
                isDisplayed()));
    searchAutoComplete.perform(replaceText("lost"), closeSoftKeyboard());

    linearLayout =
        onData(anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)))
            .atPosition(0);
    linearLayout.perform(click());

    favorite =
        onView(
            allOf(withId(R.id.action_favorite), withContentDescription("Favorite"), isDisplayed()));
    favorite.perform(click());

    navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());
    navUp.perform(click());

    ViewInteraction navDrawer =
        onView(allOf(withContentDescription("Open navigation drawer"), isDisplayed()));
    navDrawer.perform(click());

    ViewInteraction navigationMenuItemView =
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(withId(R.id.nav_view), 0)),
                    4),
                isDisplayed()));
    navigationMenuItemView.perform(click());

    ViewInteraction downloadToggle =
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.recycler_view),
                        childAtPosition(withId(android.R.id.list_container), 0)),
                    0),
                isDisplayed()));
    downloadToggle.perform(click());

    SystemClock.sleep(5000);

    Tag t = new Tag();
    t.setDownloaded(true);
    File dir = new File(t.getSheetMusicDirectory(mActivityRule.getActivity()));
    assertEquals(0, dir.listFiles().length);

    downloadToggle.perform(click());
    SystemClock.sleep(8000);

    assertEquals(2, dir.listFiles().length);

    navDrawer = onView(allOf(withContentDescription("Open navigation drawer"), isDisplayed()));
    navDrawer.perform(click());

    navigationMenuItemView =
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(withId(R.id.nav_view), 0)),
                    2),
                isDisplayed()));
    navigationMenuItemView.perform(click());

    linearLayout =
        onData(anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)))
            .atPosition(0);
    linearLayout.perform(click());

    ViewInteraction unFavorite =
        onView(
            allOf(
                withId(R.id.action_unfavorite),
                withContentDescription("UnFavorite"),
                isDisplayed()));
    unFavorite.perform(click());

    navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());

    linearLayout =
        onData(anything())
            .inAdapterView(
                allOf(
                    withId(android.R.id.list),
                    childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)))
            .atPosition(0);
    linearLayout.perform(click());

    unFavorite =
        onView(
            allOf(
                withId(R.id.action_unfavorite),
                withContentDescription("UnFavorite"),
                isDisplayed()));
    unFavorite.perform(click());

    navUp = onView(allOf(withContentDescription("Navigate up"), isDisplayed()));
    navUp.perform(click());

    ViewInteraction progressBar = onView(allOf(withId(android.R.id.empty), isDisplayed()));
    progressBar.check(matches(isDisplayed()));
  }
}
