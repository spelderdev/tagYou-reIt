package com.spelder.tagyourit.ui.tag;

import static com.spelder.tagyourit.ui.FragmentSwitcher.PAR_KEY;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.ortiz.touchview.TouchImageView;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.networking.DownloadFileTask;
import com.spelder.tagyourit.networking.videos.YouTubeVideoInformation;
import com.spelder.tagyourit.ui.MainActivity;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;

/** The tag display view. This contains handling of the sheet music and the bottom sheet view. */
public class DisplayTag extends Fragment {
  private static final String TAG = "DisplayTag";

  private static final int REQ_WIDTH = 1000;

  private static final int REQ_HEIGHT = 1000;

  private static final String TOUCH_ACTION_KEY_FULLSCREEN = "fullscreen";

  private static final String TOUCH_ACTION_KEY_MENU = "menu";

  private SharedPreferences preferences;

  private TouchImageView mImageView;

  private PDFView mPdfView;

  private Tag tag;

  private String absoluteFilePath;

  private Context context;

  private TextView error_text;

  private LinearLayout no_pdf_text;

  private View error_layout;

  private View loading_layout;

  private TagDetailsBottomSheet bottomSheet;

  private boolean fullscreenState = false;

  public DisplayTag() {}

  private static int calculateInSampleSize(BitmapFactory.Options options) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > REQ_HEIGHT || width > REQ_WIDTH) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) >= REQ_HEIGHT && (halfWidth / inSampleSize) >= REQ_WIDTH) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    return inflater.inflate(R.layout.sheet_music_viewer, container, false);
  }

  @Override
  public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    view.setOnSystemUiVisibilityChangeListener(
        visibility -> {
          MainActivity activity = (MainActivity) getActivity();
          if (activity == null) {
            return;
          }
          if (visibility == 0) {
            showSystemUI();
            if (activity.getSupportActionBar() != null) {
              activity.getSupportActionBar().show();
            }
            Log.d(TAG, "Vis=0: " + fullscreenState);
            fullscreenState = false;
          } else {
            Log.d(TAG, "Vis=1: " + fullscreenState);
            if (activity.getSupportActionBar() != null) {
              activity.getSupportActionBar().hide();
            }
            fullscreenState = true;
          }
        });

    bottomSheet = new TagDetailsBottomSheet();

    mImageView = view.findViewById(R.id.image);
    mImageView.setOnClickListener(v -> handleTouchView());

    mPdfView = view.findViewById(R.id.pdfView);
    mPdfView.setOnClickListener(v -> handleTouchView());

    no_pdf_text = view.findViewById(R.id.no_pdf);

    error_text = view.findViewById(R.id.browse_tab_view_error_text);
    Button error_button = view.findViewById(R.id.browse_tab_view_refresh_button);
    error_layout = view.findViewById(R.id.pdf_renderer_error);

    error_button.setOnClickListener(
        v -> {
          Activity activity = getActivity();
          if (activity != null) {
            ConnectivityManager connMgr =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr != null) {
              NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
              if (networkInfo != null && networkInfo.isConnected()) {
                retrieveTagInfo(context);
                unsetNetworkError();
                showTag();
              }
            }
          }
        });

    loading_layout = view.findViewById(R.id.pdfLoading);

    mPdfView.useBestQuality(true);

    retrieveTagInfo(context);
  }

  private void handleTouchView() {
    String touchAction = preferences.getString("pref_key_touch_action", "fullscreen");
    if (touchAction.equals(TOUCH_ACTION_KEY_FULLSCREEN)) {
      handleFullscreen();
    } else if (touchAction.equals(TOUCH_ACTION_KEY_MENU)) {
      showBottomMenu();
    }
  }

  private void handleFullscreen() {
    if (!fullscreenState) {
      hideSystemUI();
    } else {
      showSystemUI();
    }
    fullscreenState = !fullscreenState;
  }

  @Override
  public void onConfigurationChanged(@NonNull Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    Log.d(TAG, "config");

    if (mPdfView.getVisibility() == View.VISIBLE
        && (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
            || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)) {
      Log.d(TAG, "fitToWidth");
      showPdf();
    }
  }

  public void showBottomMenu() {
    if (!bottomSheet.isVisible()) {
      FragmentActivity activity = getActivity();
      if (activity != null) {
        bottomSheet.show(activity.getSupportFragmentManager(), "Bottom sheet");
      }
    } else {
      bottomSheet.dismiss();
    }
  }

  private void showTag() {
    Log.d(TAG, "Tag type: " + tag.getNumberOfParts());

    if (tag.getSheetMusicType() == null) {
      no_pdf_text.setVisibility(View.VISIBLE);
      mPdfView.setVisibility(View.GONE);
      mImageView.setVisibility(View.GONE);
      loading_layout.setVisibility(View.GONE);
      return;
    }

    if (tag.getSheetMusicType().equals("pdf")) {
      mPdfView.setVisibility(View.VISIBLE);
      mImageView.setVisibility(View.GONE);
      no_pdf_text.setVisibility(View.GONE);
      loading_layout.setVisibility(View.GONE);
      showPdf();
    } else {
      mPdfView.setVisibility(View.GONE);
      mImageView.setVisibility(View.VISIBLE);
      no_pdf_text.setVisibility(View.GONE);
      loading_layout.setVisibility(View.GONE);
      showImage();
    }
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);

    if (context instanceof Activity) {
      this.context = context;
      preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
  }

  private void retrieveTagInfo(Context context) {
    Bundle bundle = getArguments();
    if (bundle == null) {
      return;
    }

    tag = bundle.getParcelable(PAR_KEY);
    if (tag == null) {
      return;
    }

    bottomSheet.setArguments(bundle);

    Log.d(TAG, tag.getTitle());
    absoluteFilePath = tag.getSheetMusicPath(context);
    Log.d(TAG, absoluteFilePath);

    File file = new File(absoluteFilePath);
    if (file.exists()) {
      Log.d(TAG, "File exists");
      showTag();

      if (isOutdated(file)) {
        Log.d(TAG, "Updating sheet music for outdated tag: " + tag.getTitle());
        downloadTask();
      }
    } else {
      downloadTask();
    }

    // The title is set from the YouTube Information
    if (tag.getVideos() != null
        && !tag.getVideos().isEmpty()
        && tag.getVideos().get(0).getVideoTitle().isEmpty()) {
      new Thread(
              () -> {
                YouTubeVideoInformation info = new YouTubeVideoInformation(context);
                tag.setVideos(info.getVideoInfoAndUpdateDb(tag.getVideos()));
              })
          .start();
    }
  }

  private boolean isOutdated(File file) {
    String updateDaysString = preferences.getString("pref_key_update_frequency", "14");
    int updatedDays = Integer.valueOf(updateDaysString);
    Calendar baseDay = Calendar.getInstance();
    baseDay.add(Calendar.DAY_OF_YEAR, -updatedDays);
    return new Date(file.lastModified()).before(baseDay.getTime());
  }

  private void downloadTask() {
    Log.d(TAG, tag.getSheetMusicDirectory(context));
    Log.d(TAG, tag.getSheetMusicFileName());
    DownloadFileTask task = new UpdatedDownloadFileTask(this);
    task.execute(
        tag.getSheetMusicLink(), tag.getSheetMusicDirectory(context), tag.getSheetMusicFileName());
  }

  /** Shows the specified page of PDF to the screen. */
  private void showPdf() {
    File f = new File(absoluteFilePath);

    if (f.exists()) {
      mPdfView
          .fromFile(f)
          .onRender((int nbPages, float pageWidth, float pageHeight) -> mPdfView.fitToWidth())
          .enableSwipe(true)
          .swipeHorizontal(false)
          .enableDoubletap(true)
          .defaultPage(0)
          .enableAnnotationRendering(false)
          .password(null)
          .scrollHandle(new DefaultScrollHandle(context))
          .load();
    } else {
      setNetworkError();
    }
  }

  private void showImage() {
    File imgFile = new File(absoluteFilePath);

    if (imgFile.exists()) {
      final BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;

      // Calculate inSampleSize
      options.inSampleSize = calculateInSampleSize(options);

      // Decode bitmap with inSampleSize set
      options.inJustDecodeBounds = false;
      mImageView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options));
    } else {
      setNetworkError();
    }
  }

  public Tag getDisplayedTag() {
    return tag;
  }

  private void setNetworkError() {
    error_text.setText(R.string.network_error);
    error_layout.setVisibility(View.VISIBLE);
    mPdfView.setVisibility(View.GONE);
    mImageView.setVisibility(View.GONE);
  }

  private void unsetNetworkError() {
    error_layout.setVisibility(View.GONE);
    mPdfView.setVisibility(View.GONE);
    mImageView.setVisibility(View.GONE);
  }

  private void hideSystemUI() {
    if (getActivity() != null) {
      getActivity()
          .getWindow()
          .getDecorView()
          .setSystemUiVisibility(
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_IMMERSIVE);
      getActivity()
          .getWindow()
          .setFlags(
              WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
              WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

      getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
    }
  }

  private void showSystemUI() {
    if (getActivity() != null) {
      getActivity()
          .getWindow()
          .getDecorView()
          .setSystemUiVisibility(
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
      getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

      getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }
  }

  private static class UpdatedDownloadFileTask extends DownloadFileTask {
    private final WeakReference<DisplayTag> activityReference;

    UpdatedDownloadFileTask(DisplayTag context) {
      activityReference = new WeakReference<>(context);
    }

    @Override
    protected void onPostExecute(Boolean res) {
      DisplayTag activity = activityReference.get();
      if (activity == null || activity.isDetached()) {
        return;
      }

      Log.d("DisplayTag", "Downloaded tag");
      activity.showTag();
    }
  }
}
