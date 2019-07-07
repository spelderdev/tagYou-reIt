package com.spelder.tagyourit.ui.tag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.ortiz.touchview.TouchImageView;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.model.TrackComponents;
import com.spelder.tagyourit.model.TrackParts;
import com.spelder.tagyourit.model.VideoComponents;
import com.spelder.tagyourit.networking.DownloadFileTask;
import com.spelder.tagyourit.networking.videos.YouTubeVideoInformation;
import com.spelder.tagyourit.pitch.PitchPlayer;
import com.spelder.tagyourit.ui.FragmentSwitcher;
import com.spelder.tagyourit.ui.MainActivity;
import com.spelder.tagyourit.ui.dialog.RateTagDialog;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** The tag display view. This contains handling of the sheet music and the bottom sheet view. */
public class DisplayTag extends Fragment {
  private static final String TAG = "DisplayTag";

  private static final int REQ_WIDTH = 1000;

  private static final int REQ_HEIGHT = 1000;

  private static final int PITCH_DOWN_TIME_MILLIS = 500;

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

  private BottomSheetLayout bottomSheet;

  private boolean fullscreenState = false;

  private long keyDownTime = 0;

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
    return inflater.inflate(R.layout.fragment_pdf_renderer_basic, container, false);
  }

  @Override
  public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    view.setOnSystemUiVisibilityChangeListener(
        visibility -> {
          if (visibility == 0) {
            showSystemUI();
            ((MainActivity) getActivity()).getSupportActionBar().show();
            Log.d(TAG, "Vis=0: " + fullscreenState);
            fullscreenState = false;
          } else {
            Log.d(TAG, "Vis=1: " + fullscreenState);
            ((MainActivity) getActivity()).getSupportActionBar().hide();
            fullscreenState = true;
          }
        });

    bottomSheet = view.findViewById(R.id.bottom_sheet);

    mImageView = view.findViewById(R.id.image);
    mImageView.setOnClickListener(
        v -> {
          handleTouchView();
        });

    mPdfView = view.findViewById(R.id.pdfView);
    mPdfView.setOnClickListener(
        v -> {
          handleTouchView();
        });

    no_pdf_text = view.findViewById(R.id.no_pdf);

    error_text = view.findViewById(R.id.browse_tab_view_error_text);
    Button error_button = view.findViewById(R.id.browse_tab_view_refresh_button);
    error_layout = view.findViewById(R.id.pdf_renderer_error);

    error_button.setOnClickListener(
        v -> {
          ConnectivityManager connMgr =
              (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
          NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
          if (networkInfo != null && networkInfo.isConnected()) {
            retrieveTagInfo(context);
            unsetNetworkError();
            showTag();
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
    if (!bottomSheet.isSheetShowing()) {
      showMenuSheet();
    } else {
      bottomSheet.dismissSheet();
    }
  }

  private void showMenuSheet() {
    MenuSheetView menuSheetView =
        new MenuSheetView(
            getActivity(),
            MenuSheetView.MenuType.LIST,
            tag.getTitle(),
            item -> {
              TrackComponents track = null;
              int videoId = -2;
              if (item.getItemId() == R.id.menu_track_all) {
                track = tag.getTrack(TrackParts.ALL.getKey());
              } else if (item.getItemId() == R.id.menu_track_bass) {
                track = tag.getTrack(TrackParts.BASS.getKey());
              } else if (item.getItemId() == R.id.menu_track_bari) {
                track = tag.getTrack(TrackParts.BARI.getKey());
              } else if (item.getItemId() == R.id.menu_track_lead) {
                track = tag.getTrack(TrackParts.LEAD.getKey());
              } else if (item.getItemId() == R.id.menu_track_tenor) {
                track = tag.getTrack(TrackParts.TENOR.getKey());
              } else if (item.getItemId() == R.id.menu_track_other1) {
                track = tag.getTrack(TrackParts.OTHER1.getKey());
              } else if (item.getItemId() == R.id.menu_track_other2) {
                track = tag.getTrack(TrackParts.OTHER2.getKey());
              } else if (item.getItemId() == R.id.menu_track_other3) {
                track = tag.getTrack(TrackParts.OTHER3.getKey());
              } else if (item.getItemId() == R.id.menu_track_other4) {
                track = tag.getTrack(TrackParts.OTHER4.getKey());
              }

              if (item.getItemId() == R.id.menu_video_1) {
                videoId = 0;
              } else if (item.getItemId() == R.id.menu_video_2) {
                videoId = 1;
              } else if (item.getItemId() == R.id.menu_video_3) {
                videoId = 2;
              } else if (item.getItemId() == R.id.menu_video_4) {
                videoId = 3;
              } else if (item.getItemId() == R.id.menu_video_more) {
                videoId = -1;
              }

              if (track != null) {
                if (getActivity() instanceof MainActivity) {
                  MainActivity activity = (MainActivity) getActivity();
                  activity.playTrack(track, tag);
                }
              } else if (videoId != -2) {
                if (getActivity() instanceof MainActivity) {
                  MainActivity activity = (MainActivity) getActivity();
                  activity.openVideoPlayer(tag.getVideos(), videoId);
                }
              } else if (item.getItemId() == R.id.menu_share) {
                String shareLink =
                    "http://www.barbershoptags.com/tag-"
                        + tag.getId()
                        + "-"
                        + tag.getTitle().replaceAll(" ", "-");
                if (!tag.getVersion().isEmpty()) {
                  shareLink += "-(" + tag.getVersion().replaceAll(" ", "-") + ")";
                }

                Log.d(TAG, shareLink);

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, shareLink);
                startActivity(Intent.createChooser(i, "Share URL"));
              } else if (item.getItemId() == R.id.menu_rate) {
                createRatingDialog(tag);
              }

              if (bottomSheet.isSheetShowing()) {
                bottomSheet.dismissSheet();
              }
              return true;
            });
    menuSheetView.inflateMenu(R.menu.tag_menu);
    TextView arranger = menuSheetView.findViewById(R.id.tag_list_arranger);
    if (tag.getArranger() != null && !tag.getArranger().isEmpty()) {
      arranger.setText(tag.getArrangerDisplay());
    } else {
      arranger.setText("");
    }
    View keyView = menuSheetView.findViewById(R.id.tag_key_view);
    TextView key = menuSheetView.findViewById(R.id.tag_list_key);
    if (tag.getKey() == null || tag.getKey().isEmpty()) {
      keyView.setVisibility(View.GONE);
    } else {
      key.setText(tag.getKey());
    }

    RatingBar rating = menuSheetView.findViewById(R.id.tag_list_ratingBar);
    rating.setRating((float) tag.getRating());
    TextView ratingText = menuSheetView.findViewById(R.id.tag_list_ratingText);
    ratingText.setText(String.format(Locale.ENGLISH, "%.1f", tag.getRating()));
    TextView version = menuSheetView.findViewById(R.id.tag_list_version);
    version.setText(tag.getVersion());
    TextView sep = menuSheetView.findViewById(R.id.tag_list_line_sep);
    keyView.setOnTouchListener(
        (View v, MotionEvent event) -> {
          switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
              keyDownTime = System.currentTimeMillis();
              keyView.setSelected(true);
              PitchPlayer.playPitch(getContext(), tag.getKey());
              break;
            case MotionEvent.ACTION_UP:
              keyView.setSelected(false);
              if (System.currentTimeMillis() - keyDownTime < PITCH_DOWN_TIME_MILLIS) {
                // PitchPlayer.setLoop( false );
                PitchPlayer.stopPitch();
              } else {
                PitchPlayer.stopPitch();
              }
              break;
            case MotionEvent.ACTION_BUTTON_PRESS:
              v.performClick();
              break;
          }
          return true;
        });
    if (tag.getArranger().length() > 0 && tag.getVersion().length() > 0) {
      sep.setVisibility(View.VISIBLE);
    } else {
      sep.setVisibility(View.GONE);
    }

    if (tag.getTrack(TrackParts.ALL.getKey()) == null) {
      menuSheetView.getMenu().findItem(R.id.menu_track_all).setVisible(false);
    }
    if (tag.getTrack(TrackParts.BASS.getKey()) == null) {
      menuSheetView.getMenu().findItem(R.id.menu_track_bass).setVisible(false);
    }
    if (tag.getTrack(TrackParts.BARI.getKey()) == null) {
      menuSheetView.getMenu().findItem(R.id.menu_track_bari).setVisible(false);
    }
    if (tag.getTrack(TrackParts.LEAD.getKey()) == null) {
      menuSheetView.getMenu().findItem(R.id.menu_track_lead).setVisible(false);
    }
    if (tag.getTrack(TrackParts.TENOR.getKey()) == null) {
      menuSheetView.getMenu().findItem(R.id.menu_track_tenor).setVisible(false);
    }
    if (tag.getTrack(TrackParts.OTHER1.getKey()) == null) {
      menuSheetView.getMenu().findItem(R.id.menu_track_other1).setVisible(false);
    }
    if (tag.getTrack(TrackParts.OTHER2.getKey()) == null) {
      menuSheetView.getMenu().findItem(R.id.menu_track_other2).setVisible(false);
    }
    if (tag.getTrack(TrackParts.OTHER3.getKey()) == null) {
      menuSheetView.getMenu().findItem(R.id.menu_track_other3).setVisible(false);
    }
    if (tag.getTrack(TrackParts.OTHER4.getKey()) == null) {
      menuSheetView.getMenu().findItem(R.id.menu_track_other4).setVisible(false);
    }

    ArrayList<VideoComponents> videos = tag.getVideos();
    if (videos.size() >= 1) {
      menuSheetView.getMenu().findItem(R.id.menu_video_1).setTitle(getVideoTitle(videos.get(0)));
    } else {
      menuSheetView.getMenu().findItem(R.id.menu_video_1).setVisible(false);
      menuSheetView.getMenu().findItem(R.id.menu_videos).setVisible(false);
    }
    if (videos.size() >= 2) {
      menuSheetView.getMenu().findItem(R.id.menu_video_2).setTitle(getVideoTitle(videos.get(1)));
    } else {
      menuSheetView.getMenu().findItem(R.id.menu_video_2).setVisible(false);
    }
    if (videos.size() >= 3) {
      menuSheetView.getMenu().findItem(R.id.menu_video_3).setTitle(getVideoTitle(videos.get(2)));
    } else {
      menuSheetView.getMenu().findItem(R.id.menu_video_3).setVisible(false);
    }
    if (videos.size() >= 4) {
      menuSheetView.getMenu().findItem(R.id.menu_video_4).setTitle(getVideoTitle(videos.get(3)));
    } else {
      menuSheetView.getMenu().findItem(R.id.menu_video_4).setVisible(false);
    }
    if (videos.size() <= 4) {
      menuSheetView.getMenu().findItem(R.id.menu_video_more).setVisible(false);
    }

    menuSheetView.updateMenu();

    bottomSheet.showWithSheetView(menuSheetView);
  }

  private String getVideoTitle(VideoComponents video) {
    if (video.getVideoTitle() != null && !video.getVideoTitle().replaceAll("\\s", "").isEmpty()) {
      return video.getVideoTitle();
    } else if (video.getDescription() != null
        && !video.getDescription().replaceAll("\\s", "").isEmpty()) {
      return video.getDescription();
    } else if (video.getSungBy() != null && !video.getSungBy().replaceAll("\\s", "").isEmpty()) {
      return video.getSungBy();
    }
    return video.getTagTitle();
  }

  private void showTag() {
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

    tag = bundle.getParcelable(FragmentSwitcher.PAR_KEY);
    if (tag == null) {
      return;
    }

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
    if (updateDaysString == null) {
      return false;
    }
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

  private void createRatingDialog(Tag tag) {
    if (getActivity() != null) {
      RateTagDialog dialog = new RateTagDialog();
      Bundle bundle = new Bundle();
      bundle.putParcelable(FragmentSwitcher.TAG_KEY, tag);
      dialog.setArguments(bundle);
      dialog.show(getActivity().getSupportFragmentManager(), "rate");
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
