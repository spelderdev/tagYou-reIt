package com.spelder.tagyourit.ui.tag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.model.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** List adapter for the tags. This sets up the actual tag list holder. */
class TagListAdapter extends BaseAdapter {
  private final LayoutInflater mInflater;

  private final List<Tag> tags;

  TagListAdapter(Context context) {
    mInflater = LayoutInflater.from(context);
    tags = new ArrayList<>();
  }

  @Override
  public int getCount() {
    return tags.size();
  }

  @Override
  public Object getItem(int position) {
    return tags.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  void addTags(List<Tag> toAdd) {
    if (toAdd != null) {
      tags.addAll(toAdd);
    }
  }

  void clearTags() {
    tags.clear();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view;
    ViewHolder holder;
    if (convertView == null) {
      view = mInflater.inflate(R.layout.tag_list_view, parent, false);
      holder = new ViewHolder();
      holder.title = view.findViewById(R.id.tag_list_title);
      holder.arranger = view.findViewById(R.id.tag_list_arranger);
      holder.version = view.findViewById(R.id.tag_list_version);
      holder.tagRatingText = view.findViewById(R.id.tag_list_ratingText);
      holder.sheet = view.findViewById(R.id.tag_list_sheet_music);
      holder.track = view.findViewById(R.id.tag_list_track);
      holder.video = view.findViewById(R.id.tag_list_video);
      holder.separator = view.findViewById(R.id.tag_list_line_sep);
      view.setTag(holder);
    } else {
      view = convertView;
      holder = (ViewHolder) view.getTag();
    }
    Tag tag = tags.get(position);
    holder.title.setText(tag.getTitle());
    holder.arranger.setText(tag.getArrangerDisplay());
    if (tag.getArranger().length() > 0) {
      holder.arranger.setVisibility(View.VISIBLE);
    } else {
      holder.arranger.setVisibility(View.GONE);
    }
    holder.version.setText(tag.getVersion());
    if (tag.getVersion().length() > 0) {
      holder.version.setVisibility(View.VISIBLE);
    } else {
      holder.version.setVisibility(View.GONE);
    }
    if (tag.getArranger().length() > 0 && tag.getVersion().length() > 0) {
      holder.separator.setVisibility(View.VISIBLE);
    } else {
      holder.separator.setVisibility(View.GONE);
    }
    holder.tagRatingText.setText(String.format(Locale.ENGLISH, "%.1f", tag.getRating()));
    if (tag.hasSheetMusic()) {
      holder.sheet.setVisibility(View.VISIBLE);
    } else {
      holder.sheet.setVisibility(View.INVISIBLE);
    }
    if (tag.hasLearningTracks()) {
      holder.track.setVisibility(View.VISIBLE);
    } else {
      holder.track.setVisibility(View.INVISIBLE);
    }
    if (tag.hasVideos()) {
      holder.video.setVisibility(View.VISIBLE);
    } else {
      holder.video.setVisibility(View.INVISIBLE);
    }
    return view;
  }

  private class ViewHolder {
    ImageView sheet, track, video;
    TextView title, arranger, version, tagRatingText, separator;
  }
}
