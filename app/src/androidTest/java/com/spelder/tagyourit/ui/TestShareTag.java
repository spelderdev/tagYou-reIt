package com.spelder.tagyourit.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.spelder.tagyourit.R;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestShareTag {
  @Rule public IntentsTestRule<MainActivity> intentRule = new IntentsTestRule<>(MainActivity.class);

  public static Matcher<Intent> chooser(Matcher<Intent> matcher) {
    return allOf(hasAction(Intent.ACTION_CHOOSER), hasExtra(is(Intent.EXTRA_INTENT), matcher));
  }

  @Test
  public void openShareIntent() {
    onView(ViewMatchers.withId(R.id.action_search)).perform(click());

    onView(withId(R.id.search_src_text)).perform(replaceText("like leaves tickner"), closeSoftKeyboard());

    onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(1).perform(click());

    onView(withId(R.id.action_menu)).perform(click());

    Instrumentation.ActivityResult intentResult =
        new Instrumentation.ActivityResult(Activity.RESULT_OK, new Intent());

    intending(anyIntent()).respondWith(intentResult);

    onView(allOf(withId(R.id.tag_detail_share_button), withText("Share"))).perform(click());

    intended(
        chooser(
            allOf(
                hasAction(Intent.ACTION_SEND),
                hasExtra(
                    Intent.EXTRA_TEXT,
                    "http://www.barbershoptags.com/tag-2275-Like-Leaves-will-Fall"))));
  }
}
