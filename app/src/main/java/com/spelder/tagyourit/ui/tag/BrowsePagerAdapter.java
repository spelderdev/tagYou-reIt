package com.spelder.tagyourit.ui.tag;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.spelder.tagyourit.networking.api.SortBy;
import java.util.TreeMap;

/** Adapter for the Browse fragment. */
class BrowsePagerAdapter extends FragmentPagerAdapter {
  private static final String[] tabTitles =
      new String[] {"LATEST", "RATING", "DOWNLOAD", "TITLE", "CLASSIC"};

  private static final SortBy[] tabSorting =
      new SortBy[] {SortBy.LATEST, SortBy.RATING, SortBy.DOWNLOAD, SortBy.TITLE, SortBy.CLASSIC};

  private static TreeMap<Integer, TagListFragment> fragmentTreeMap;

  BrowsePagerAdapter(FragmentManager fm) {
    super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    Log.d("BrowsePagerAdapter", "BrowsePagerAdapter");
    if (fragmentTreeMap == null) {
      fragmentTreeMap = new TreeMap<>();
    }
  }

  @Override
  public int getCount() {
    return tabTitles.length;
  }

  @Override
  @NonNull
  public Fragment getItem(int position) {
    Log.d("BrowsePagerAdapter", "Position: " + position);
    Fragment fragment = fragmentTreeMap.get(position);
    if (fragment == null) {
      TagListFragment newFragment = TagListFragment.newInstance(tabSorting[position]);
      fragmentTreeMap.put(position, newFragment);
      return newFragment;
    } else {
      return fragment;
    }
  }

  @Override
  public CharSequence getPageTitle(int position) {
    // Generate title based on item position
    return tabTitles[position];
  }
}
