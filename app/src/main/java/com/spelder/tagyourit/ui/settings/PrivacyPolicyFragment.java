package com.spelder.tagyourit.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.spelder.tagyourit.R;

/** Displays the privacy policy. */
public class PrivacyPolicyFragment extends Fragment {
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.privacy_policy, container, false);
  }

  @Override
  public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    WebView myWebView = view.findViewById(R.id.privacy_policy);
    myWebView.loadUrl("file:///android_asset/PrivacyPolicy.html");
  }
}
