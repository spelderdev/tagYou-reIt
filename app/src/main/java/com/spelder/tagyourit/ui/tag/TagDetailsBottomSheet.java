package com.spelder.tagyourit.ui.tag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.db.TagDb;
import com.spelder.tagyourit.model.Tag;
import com.spelder.tagyourit.model.TrackComponents;
import com.spelder.tagyourit.model.TrackParts;
import com.spelder.tagyourit.model.VideoComponents;
import com.spelder.tagyourit.pitch.Pitch;
import com.spelder.tagyourit.ui.FragmentSwitcher;
import com.spelder.tagyourit.ui.MainActivity;
import com.spelder.tagyourit.ui.dialog.RateTagDialog;
import java.util.ArrayList;
import java.util.Locale;

public class TagDetailsBottomSheet extends BottomSheetDialogFragment {
  private static final String TAG = TagDetailsBottomSheet.class.getName();

  private Tag tag;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.tag_details, container, false);

    retrieveTagInfo();

    setupDetails(view);
    setupButtons(view);
    setupTracks(view);
    setupVideos(view);

    return view;
  }

  private void retrieveTagInfo() {
    Bundle bundle = getArguments();
    if (bundle == null) {
      return;
    }

    tag = bundle.getParcelable(FragmentSwitcher.PAR_KEY);
  }

  private void setupDetails(View view) {
    TextView title = view.findViewById(R.id.tag_detail_title);
    title.setText(tag.getTitle());

    TextView arranger = view.findViewById(R.id.tag_detail_arranger);
    if (tag.getArranger() != null && !tag.getArranger().isEmpty()) {
      arranger.setText(tag.getArrangerDisplay());
    } else {
      arranger.setText("");
    }

    TextView ratingText = view.findViewById(R.id.tag_detail_ratingText);
    ratingText.setText(String.format(Locale.ENGLISH, "%.1f", tag.getRating()));

    TextView version = view.findViewById(R.id.tag_detail_version);
    version.setText(tag.getVersion());

    TextView sep = view.findViewById(R.id.tag_detail_line_sep);
    if (tag.getArranger().length() > 0 && tag.getVersion().length() > 0) {
      sep.setVisibility(View.VISIBLE);
    } else {
      sep.setVisibility(View.GONE);
    }
  }

  private void setupButtons(View view) {
    PitchButton key = view.findViewById(R.id.tag_detail_key_button);
    key.setPitch(Pitch.determinePitch(tag.getKey()));
    if (tag.getKey() == null || tag.getKey().isEmpty()) {
      key.setVisibility(View.GONE);
    } else {
      key.setText(tag.getKey());
    }

    Button share = view.findViewById(R.id.tag_detail_share_button);
    share.setOnClickListener(
        v -> {
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
        });

    Button rateButton = view.findViewById(R.id.tag_detail_rate_button);
    rateButton.setOnClickListener(v -> createRatingDialog(tag));

    Button favoriteButton = view.findViewById(R.id.tag_detail_favorite_button);
    int favoriteResource = tag.isFavorited() ? R.drawable.favorite : R.drawable.favorite_outline;
    favoriteButton.setCompoundDrawablesWithIntrinsicBounds(
        null, getResources().getDrawable(favoriteResource), null, null);
    favoriteButton.setOnClickListener(
        v -> {
          TagDb db = new TagDb(getActivity());
          if (!tag.isFavorited()) {
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(
                null, getResources().getDrawable(R.drawable.favorite), null, null);

            long newRow = db.insertFavorite(tag);
            tag.setDbId(newRow);
          } else {
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(
                null, getResources().getDrawable(R.drawable.favorite_outline), null, null);

            db.deleteFavorite(tag);
            tag.setDbId(null);
          }
          Activity activity = getActivity();
          if (activity != null) {
            activity.invalidateOptionsMenu();
          }
        });
  }

  private void setupTracks(View view) {
    Button trackAll = view.findViewById(R.id.tag_detail_track_all);
    if (tag.getTrack(TrackParts.ALL.getKey()) == null) {
      trackAll.setVisibility(View.GONE);
    } else {
      trackAll.setOnClickListener(v -> playTrack(tag.getTrack(TrackParts.ALL.getKey())));
    }

    Button trackBass = view.findViewById(R.id.tag_detail_track_bass);
    if (tag.getTrack(TrackParts.BASS.getKey()) == null) {
      trackBass.setVisibility(View.GONE);
    } else {
      trackBass.setOnClickListener(v -> playTrack(tag.getTrack(TrackParts.BASS.getKey())));
    }

    Button trackBari = view.findViewById(R.id.tag_detail_track_bari);
    if (tag.getTrack(TrackParts.BARI.getKey()) == null) {
      trackBari.setVisibility(View.GONE);
    } else {
      trackBari.setOnClickListener(v -> playTrack(tag.getTrack(TrackParts.BARI.getKey())));
    }

    Button trackLead = view.findViewById(R.id.tag_detail_track_lead);
    if (tag.getTrack(TrackParts.LEAD.getKey()) == null) {
      trackLead.setVisibility(View.GONE);
    } else {
      trackLead.setOnClickListener(v -> playTrack(tag.getTrack(TrackParts.LEAD.getKey())));
    }

    Button trackTenor = view.findViewById(R.id.tag_detail_track_tenor);
    if (tag.getTrack(TrackParts.TENOR.getKey()) == null) {
      trackTenor.setVisibility(View.GONE);
    } else {
      trackTenor.setOnClickListener(v -> playTrack(tag.getTrack(TrackParts.TENOR.getKey())));
    }

    Button trackOther1 = view.findViewById(R.id.tag_detail_track_other1);
    if (tag.getTrack(TrackParts.OTHER1.getKey()) == null) {
      trackOther1.setVisibility(View.GONE);
    } else {
      trackOther1.setOnClickListener(v -> playTrack(tag.getTrack(TrackParts.OTHER1.getKey())));
    }

    Button trackOther2 = view.findViewById(R.id.tag_detail_track_other2);
    if (tag.getTrack(TrackParts.OTHER2.getKey()) == null) {
      trackOther2.setVisibility(View.GONE);
    } else {
      trackOther2.setOnClickListener(v -> playTrack(tag.getTrack(TrackParts.OTHER2.getKey())));
    }

    Button trackOther3 = view.findViewById(R.id.tag_detail_track_other3);
    if (tag.getTrack(TrackParts.OTHER3.getKey()) == null) {
      trackOther3.setVisibility(View.GONE);
    } else {
      trackOther3.setOnClickListener(v -> playTrack(tag.getTrack(TrackParts.OTHER3.getKey())));
    }

    Button trackOther4 = view.findViewById(R.id.tag_detail_track_other4);
    if (tag.getTrack(TrackParts.OTHER4.getKey()) == null) {
      trackOther4.setVisibility(View.GONE);
    } else {
      trackOther4.setOnClickListener(v -> playTrack(tag.getTrack(TrackParts.OTHER4.getKey())));
    }
  }

  private void setupVideos(View view) {
    ArrayList<VideoComponents> videos = tag.getVideos();
    Button video1 = view.findViewById(R.id.tag_detail_video_1);
    Button video2 = view.findViewById(R.id.tag_detail_video_2);
    Button video3 = view.findViewById(R.id.tag_detail_video_3);
    Button video4 = view.findViewById(R.id.tag_detail_video_4);
    Button videoMore = view.findViewById(R.id.tag_detail_video_more);
    View videoSection = view.findViewById(R.id.tag_detail_videos);

    if (videos.size() >= 1) {
      video1.setText(getVideoTitle(videos.get(0)));
      video1.setOnClickListener(v -> playVideo(0));
    } else {
      video1.setVisibility(View.GONE);
      videoSection.setVisibility(View.GONE);
    }

    if (videos.size() >= 2) {
      video2.setText(getVideoTitle(videos.get(1)));
      video2.setOnClickListener(v -> playVideo(1));
    } else {
      video2.setVisibility(View.GONE);
    }

    if (videos.size() >= 3) {
      video3.setText(getVideoTitle(videos.get(2)));
      video3.setOnClickListener(v -> playVideo(2));
    } else {
      video3.setVisibility(View.GONE);
    }

    if (videos.size() >= 4) {
      video4.setText(getVideoTitle(videos.get(3)));
      video4.setOnClickListener(v -> playVideo(3));
    } else {
      video4.setVisibility(View.GONE);
    }

    if (videos.size() <= 4) {
      videoMore.setVisibility(View.GONE);
    } else {
      videoMore.setOnClickListener(v -> playVideo(-1));
    }
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

  private void playTrack(TrackComponents track) {
    dismiss();
    MainActivity activity = (MainActivity) getActivity();
    if (activity != null) {
      activity.playTrack(track, tag);
    }
  }

  private void playVideo(int videoId) {
    dismiss();
    MainActivity activity = (MainActivity) getActivity();
    if (activity != null) {
      activity.openVideoPlayer(tag.getVideos(), videoId);
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
}
