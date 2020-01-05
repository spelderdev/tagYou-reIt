package com.spelder.tagyourit.ui.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.spelder.tagyourit.R;
import com.spelder.tagyourit.model.ListProperties;
import java.util.ArrayList;
import java.util.List;

/** List adapter for the list properties. This sets up the actual list holder. */
class ListSelectionListAdapter extends BaseAdapter {
  private final LayoutInflater mInflater;
  private final List<ListProperties> listProperties;
  private final List<Long> selectedDbIds;

  ListSelectionListAdapter(Context context) {
    mInflater = LayoutInflater.from(context);
    listProperties = new ArrayList<>();
    selectedDbIds = new ArrayList<>();
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

  void setSelectedDbIds(List<Long> selectedDbIds) {
    this.selectedDbIds.clear();
    this.selectedDbIds.addAll(selectedDbIds);
  }

  List<Long> getSelectedListIds() {
    return selectedDbIds;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view;
    ViewHolder holder;
    if (convertView == null) {
      view = mInflater.inflate(R.layout.list_select, parent, false);
      holder = new ViewHolder();
      holder.name = view.findViewById(R.id.list_select_name);
      holder.icon = view.findViewById(R.id.list_select_icon);
      holder.selected = view.findViewById(R.id.list_select_selected);
      view.setTag(holder);
    } else {
      view = convertView;
      holder = (ViewHolder) view.getTag();
    }
    ListProperties properties = listProperties.get(position);
    holder.name.setText(properties.getName());
    holder.icon.setImageDrawable(
        mInflater.getContext().getResources().getDrawable(properties.getIcon().getResourceId()));
    holder.icon.setColorFilter(properties.getColor());
    holder.selected.setChecked(selectedDbIds.contains(properties.getDbId()));
    holder.selected.setOnCheckedChangeListener(
        (CompoundButton buttonView, boolean isChecked) -> {
          if (isChecked) {
            selectedDbIds.add(properties.getDbId());
          } else {
            selectedDbIds.remove(properties.getDbId());
          }
        });
    return view;
  }

  private class ViewHolder {
    ImageView icon;
    TextView name;
    CheckBox selected;
  }
}
