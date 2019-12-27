package com.spelder.tagyourit.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.ListProperties;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.ui.pitch.PitchPipeFragment;
import com.spelder.tagyourit.ui.settings.PreferencesFragment;
import com.spelder.tagyourit.ui.settings.PrivacyPolicyFragment;
import com.spelder.tagyourit.ui.tag.DisplayTag;
import com.spelder.tagyourit.ui.lists.CustomListFragment;
import com.spelder.tagyourit.ui.lists.ListFragment;
import com.spelder.tagyourit.ui.tag.SearchListFragment;
import com.spelder.tagyourit.ui.tag.TagListFragment;

/**
 * Helper class for handling the fragments of the main activity. This helps with switching to the
 * correct fragments.
 */
public class FragmentSwitcher {
  public static final String TAG_KEY = "com.spelder.tagyourit.ui.par.tag";
  public static final String PAR_KEY = "com.spelder.tagyourit.ui.par";
  private static final int FRAGMENT_SETTINGS = 3;
  private static final int FRAGMENT_BROWSE = 0;
  private static final int FRAGMENT_LIST = 1;
  private static final int FRAGMENT_DISPLAY_TAG = 4;
  private static final int FRAGMENT_SEARCH = 5;
  private static final int FRAGMENT_PITCH_PIPE = 6;
  private static final int FRAGMENT_PRIVACY_POLICY = 7;
  private static final int FRAGMENT_TAG_LIST = 8;

  private final FragmentActivity activity;
  private Fragment fragmentBrowse;
  private Fragment fragmentList;
  private Fragment fragmentPitchPipe;
  private PreferencesFragment fragmentSettings;
  private Fragment fragmentDisplay;
  private Fragment fragmentSearch;
  private Fragment fragmentPrivacyPolicy;
  private Fragment fragmentTagList;
  private int currentFragment;
  private int baseFragment = FRAGMENT_BROWSE;

  FragmentSwitcher(FragmentActivity activity) {
    this.activity = activity;
  }

  private int getFragmentCount() {
    return activity.getSupportFragmentManager().getBackStackEntryCount();
  }

  private Fragment getFragmentAt(int index) {
    return getFragmentCount() > 0
        ? activity.getSupportFragmentManager().findFragmentByTag(Integer.toString(index))
        : null;
  }

  private Fragment getFragment(int id) {
    Fragment frag = fragmentBrowse;
    switch (id) {
      case FRAGMENT_BROWSE:
        if (fragmentBrowse == null) {
          fragmentBrowse = new TagListFragment();
        }
        frag = fragmentBrowse;
        break;
      case FRAGMENT_LIST:
        if (fragmentList == null) {
          fragmentList = new ListFragment();
        }
        frag = fragmentList;
        break;
      case FRAGMENT_PITCH_PIPE:
        if (fragmentPitchPipe == null) {
          fragmentPitchPipe = new PitchPipeFragment();
        }
        frag = fragmentPitchPipe;
        break;
      case FRAGMENT_SETTINGS:
        if (fragmentSettings == null) {
          fragmentSettings = new PreferencesFragment();
        }
        frag = fragmentSettings;
        break;
      case FRAGMENT_SEARCH:
        if (fragmentSearch == null) {
          fragmentSearch = SearchListFragment.newInstance();
        }
        frag = fragmentSearch;
        break;
      case FRAGMENT_PRIVACY_POLICY:
        if (fragmentPrivacyPolicy == null) {
          fragmentPrivacyPolicy = new PrivacyPolicyFragment();
        }
        frag = fragmentPrivacyPolicy;
        break;
      case FRAGMENT_DISPLAY_TAG:
        fragmentDisplay = new DisplayTag();
        frag = fragmentDisplay;
        break;
      case FRAGMENT_TAG_LIST:
        fragmentTagList = new CustomListFragment();
        frag = fragmentTagList;
        break;
    }
    return frag;
  }

  void displayNavigationSelectedFragment(int id) {
    if (id == R.id.nav_lists) {
      baseFragment = FRAGMENT_LIST;
      displayFragment(FRAGMENT_LIST);
    } else if (id == R.id.nav_settings) {
      baseFragment = FRAGMENT_SETTINGS;
      displayFragment(FRAGMENT_SETTINGS);
    } else if (id == R.id.nav_browse) {
      baseFragment = FRAGMENT_BROWSE;
      displayFragment(FRAGMENT_BROWSE);
    } else if (id == R.id.nav_pitch_pipe) {
      baseFragment = FRAGMENT_PITCH_PIPE;
      displayFragment(FRAGMENT_PITCH_PIPE);
    }

    Log.d("FragmentSwitcher", "Set baseFragment: " + baseFragment);
  }

  void setBaseFragment(int base) {
    baseFragment = base;
  }

  void onBackPressed() {
    int backCount = getFragmentCount();
    if (backCount > -1) {
      Fragment backStackFragment = getFragmentAt(getFragmentCount() - 1);
      currentFragment = getCurrentIdFromFragment(backStackFragment);

      Log.d("FragmentSwitcher", "currentFragment: " + currentFragment);
      if (backStackFragment instanceof DisplayTag) {
        fragmentDisplay = backStackFragment;
      } else if (backStackFragment instanceof TagListFragment) {
        fragmentSearch = backStackFragment;
      }
    }
  }

  void hideKeyboard() {
    InputMethodManager imm =
        (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    // Find the currently focused view, so we can grab the correct window token from it.
    View view = activity.getCurrentFocus();
    // If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
      view = new View(activity);
    }
    if (imm != null) {
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  private int getCurrentIdFromFragment(Fragment fragment) {
    if (fragment instanceof DisplayTag) {
      return FRAGMENT_DISPLAY_TAG;
    } else if (fragment instanceof SearchListFragment) {
      return FRAGMENT_SEARCH;
    } else if (fragment instanceof TagListFragment) {
      return FRAGMENT_BROWSE;
    } else if (fragment instanceof ListFragment) {
      return FRAGMENT_LIST;
    } else if (fragment instanceof PitchPipeFragment) {
      return FRAGMENT_PITCH_PIPE;
    } else if (fragment instanceof PrivacyPolicyFragment) {
      return FRAGMENT_PRIVACY_POLICY;
    } else if (fragment instanceof CustomListFragment) {
      return FRAGMENT_TAG_LIST;
    }
    return baseFragment;
  }

  void loadSearch(String query) {
    if (isNotFragmentSearch()) {
      displayFragmentBackStack(FRAGMENT_SEARCH);
    }
    ((SearchListFragment) getFragment(FRAGMENT_SEARCH)).loadSearch(query);
  }

  public void displayPrivacyPolicy() {
    displayFragmentBackStack(FRAGMENT_PRIVACY_POLICY);
  }

  public void displayList(ListProperties clickedList) {
    Bundle bundles = new Bundle();
    if (clickedList != null) {
      bundles.putParcelable(PAR_KEY, clickedList);
    }
    // add the bundle as an argument
    Fragment displayListFragment = getFragment(FRAGMENT_TAG_LIST);
    displayListFragment.setArguments(bundles);
    currentFragment = FRAGMENT_TAG_LIST;
    activity
        .getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, displayListFragment, Integer.toString(getFragmentCount()))
        .addToBackStack("" + currentFragment)
        .commit();
  }

  public void displayTag(Tag clickedTag) {
    MainActivity.currentQuery = "";
    fragmentSearch = null;
    Bundle bundles = new Bundle();
    if (clickedTag != null) {
      bundles.putParcelable(PAR_KEY, clickedTag);
    }
    // add the bundle as an argument
    Fragment displayTagFragment = getFragment(FRAGMENT_DISPLAY_TAG);
    displayTagFragment.setArguments(bundles);
    currentFragment = FRAGMENT_DISPLAY_TAG;
    activity
        .getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, displayTagFragment, Integer.toString(getFragmentCount()))
        .addToBackStack("" + currentFragment)
        .commit();
    hideKeyboard();
  }

  private boolean isNotFragmentSearch() {
    return currentFragment != FRAGMENT_SEARCH;
  }

  boolean isFavoriteVisible() {
    return currentFragment == FRAGMENT_DISPLAY_TAG
        && !((DisplayTag) fragmentDisplay).getDisplayedTag().isFavorited();
  }

  boolean isUnFavoriteVisible() {
    return currentFragment == FRAGMENT_DISPLAY_TAG
        && ((DisplayTag) fragmentDisplay).getDisplayedTag().isFavorited();
  }

  boolean isMenuVisible() {
    return currentFragment == FRAGMENT_DISPLAY_TAG;
  }

  boolean isSearchVisible() {
    return currentFragment != FRAGMENT_DISPLAY_TAG
        && currentFragment != FRAGMENT_SETTINGS
        && currentFragment != FRAGMENT_PRIVACY_POLICY;
  }

  boolean isSortVisible() {
    return currentFragment == FRAGMENT_BROWSE
        || currentFragment == FRAGMENT_SEARCH
        || currentFragment == FRAGMENT_TAG_LIST;
  }

  boolean isAddListVisible() {
    return currentFragment == FRAGMENT_LIST;
  }

  boolean isDeleteListVisible() {
    return currentFragment == FRAGMENT_TAG_LIST && getDisplayedList().isUserCreated();
  }

  boolean isListOptionsVisible() {
    return currentFragment == FRAGMENT_TAG_LIST;
  }

  int getCurrentNavigationId() {
    return getNavigationId(currentFragment);
  }

  private int getNavigationId(int fragmentId) {
    switch (fragmentId) {
      case FRAGMENT_BROWSE:
        return R.id.nav_browse;
      case FRAGMENT_LIST:
        return R.id.nav_lists;
      case FRAGMENT_SETTINGS:
        return R.id.nav_settings;
      case FRAGMENT_PITCH_PIPE:
        return R.id.nav_pitch_pipe;
    }
    return 0;
  }

  String getCurrentMenuTitle() {
    return getMenuTitle(currentFragment);
  }

  private String getMenuTitle(int id) {
    Log.d("Title", "" + id);
    if (id == FRAGMENT_BROWSE) {
      return "Discover";
    } else if (id == FRAGMENT_LIST) {
      return "Lists";
    } else if (id == FRAGMENT_PITCH_PIPE) {
      return "Pitch Pipe";
    } else if (id == FRAGMENT_PRIVACY_POLICY) {
      return "Privacy Policy";
    } else if (id == FRAGMENT_SEARCH) {
      return "Search";
    } else if (id == FRAGMENT_SETTINGS) {
      return "Settings";
    } else if (id == FRAGMENT_DISPLAY_TAG) {
      return getDisplayedTag().getTitle();
    } else if (id == FRAGMENT_TAG_LIST) {
      return getDisplayedList().getName();
    }
    return "";
  }

  ListProperties getDisplayedList() {
    return ((CustomListFragment) fragmentTagList).getListProperties();
  }

  Tag getDisplayedTag() {
    return ((DisplayTag) fragmentDisplay).getDisplayedTag();
  }

  void showBottomMenu() {
    ((DisplayTag) fragmentDisplay).showBottomMenu();
  }

  int getBaseFragmentId() {
    TagDb db = new TagDb(activity);
    if (db.hasFavorites()) {
      return FRAGMENT_LIST;
    }

    return FRAGMENT_BROWSE;
  }

  void displayFragment(int id) {
    Log.d("MainActivity", "" + id);
    if (id != FRAGMENT_SEARCH) {
      MainActivity.currentQuery = "";
      fragmentSearch = null;
    }
    currentFragment = id;
    activity
        .getSupportFragmentManager()
        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    activity
        .getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, getFragment(id), getFragment(id).getClass().getName())
        .commit();
  }

  private void displayFragmentBackStack(int id) {
    if (id != FRAGMENT_SEARCH) {
      MainActivity.currentQuery = "";
      fragmentSearch = null;
    }
    if (currentFragment != id) {
      activity
          .getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.fragment_container, getFragment(id), Integer.toString(getFragmentCount()))
          .addToBackStack("" + currentFragment)
          .commit();
    }
    currentFragment = id;
  }

  void initializeDriveClient(GoogleSignInAccount signInAccount) {
    if (fragmentSettings != null) {
      fragmentSettings.initializeDriveClient(signInAccount);
    }
  }
}
