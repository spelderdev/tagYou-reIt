package com.spelder.tagyourit.ui.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.model.ListProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** List adapter for the list properties. This sets up the actual list holder. */
class ListPropertiesListAdapter extends BaseAdapter {
  private final LayoutInflater mInflater;
  private final List<ListProperties> listProperties;

  ListPropertiesListAdapter(Context context) {
    mInflater = LayoutInflater.from(context);
    listProperties = new ArrayList<>();
  }

  @Override
  public int getCount() {
    return listProperties.size();
  }

  @Override
  public Object getItem(int position) {
    return listProperties.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  void addProperties(List<ListProperties> toAdd) {
    if (toAdd != null) {
      listProperties.addAll(toAdd);
    }
  }

  void clearProperties() {
    listProperties.clear();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view;
    ViewHolder holder;
    if (convertView == null) {
      view = mInflater.inflate(R.layout.list_entry, parent, false);
      holder = new ViewHolder();
      holder.name = view.findViewById(R.id.list_entry_name);
      holder.icon = view.findViewById(R.id.list_entry_icon);
      holder.count = view.findViewById(R.id.list_entry_count);
      view.setTag(holder);
    } else {
      view = convertView;
      holder = (ViewHolder) view.getTag();
    }
    ListProperties properties = listProperties.get(position);
    holder.name.setText(properties.getName());
    holder.count.setText(String.format(Locale.ENGLISH, "%d", properties.getListSize()));
    holder.icon.setImageDrawable(
        mInflater.getContext().getResources().getDrawable(properties.getIcon().getResourceId()));
    holder.icon.setColorFilter(properties.getColor());
    return view;
  }

  private class ViewHolder {
    ImageView icon;
    TextView name, count;
  }
}
