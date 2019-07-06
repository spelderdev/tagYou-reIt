package com.spelder.tagyourit.ui.tag;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.spelder.tagyourit.R;

/** Displays the browse view. This sets the tabs which contains the tag lists. */
public class BrowseFragment extends Fragment {
  private ViewPager mPager;

  private TextView error_text;

  private View error_layout;

  private TabLayout tabLayout;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d("BrowseFragment", "onCreateView");
    View view = inflater.inflate(R.layout.browse_tab, container, false);
    error_text = view.findViewById(R.id.browse_tab_view_error_text);
    Button error_button = view.findViewById(R.id.browse_tab_view_refresh_button);
    error_layout = view.findViewById(R.id.browse_tab_view_error);
    mPager = view.findViewById(R.id.pager);
    error_button.setOnClickListener(
        v -> {
          Activity activity = getActivity();
          if (activity == null) {
            return;
          }
          ConnectivityManager connMgr =
              (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
          NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
          if (networkInfo != null && networkInfo.isConnected()) {
            startUp();
            unsetNetworkError();
          }
        });
    Activity activity = getActivity();
    if (activity == null) {
      setNetworkError();
      return view;
    }
    ConnectivityManager connMgr =
        (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    if (networkInfo == null || !networkInfo.isConnected()) {
      setNetworkError();
      return view;
    }
    startUp();
    return view;
  }

  private void startUp() {
    BrowsePagerAdapter mAdapter = new BrowsePagerAdapter(getChildFragmentManager());
    mPager.setAdapter(mAdapter);
    Activity activity = getActivity();
    if (activity == null) {
      return;
    }
    tabLayout = activity.findViewById(R.id.tabs);
    tabLayout.setVisibility(View.VISIBLE);
    tabLayout.setupWithViewPager(mPager);
  }

  @Override
  public void onDestroyView() {
    tabLayout.setVisibility(View.GONE);
    super.onDestroyView();
  }

  private void setNetworkError() {
    error_text.setText(R.string.network_error);
    error_layout.setVisibility(View.VISIBLE);
    mPager.setVisibility(View.GONE);
  }

  private void unsetNetworkError() {
    error_layout.setVisibility(View.GONE);
    mPager.setVisibility(View.VISIBLE);
  }
}
