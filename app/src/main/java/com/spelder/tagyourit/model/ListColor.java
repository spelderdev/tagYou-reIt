package com.spelder.tagyourit.model;

import android.graphics.Color;

public enum ListColor {
  DEFAULT(Color.GRAY),
  ORANGE(0xffe48126),
  CYAN(0xff009688),
  BLUE(0xff3f51b5),
  GREEN(0xff8bc34a),
  LIGHT_BLUE(0xff03a9f4),
  YELLOW(0xfffbc02d),
  PINK(0xffd81b60);

  private final int colorId;

  ListColor(int colorId) {
    this.colorId = colorId;
  }

  public int getColorId() {
    return colorId;
  }

  public static ListColor fromDbId(int colorId) {
    for (ListColor icon : ListColor.values()) {
      if (icon.getColorId() == colorId) {
        return icon;
      }
    }

    return DEFAULT;
  }
}
