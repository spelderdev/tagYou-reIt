package com.spelder.tagyourit.music.model;

public enum Speed {
  SLOWEST(0, 0.25f, "0.25x"),
  SLOWER(1, 0.5f, "0.5x"),
  SLOW(2, 0.75f, "0.75x"),
  NORMAL(3, 1.0f, "1x"),
  FAST(4, 1.25f, "1.25x"),
  FASTER(5, 1.5f, "1.5x"),
  FASTEST(6, 2.0f, "2x");

  private final String display;

  private final float speed;

  private final int id;

  Speed(int id, float speed, String display) {
    this.id = id;
    this.speed = speed;
    this.display = display;
  }

  public static Speed getSpeedFromId(int id) {
    for (Speed e : Speed.values()) {
      if (e.id == id) {
        return e;
      }
    }

    return null;
  }

  public String getDisplay() {
    return display;
  }

  public float getSpeed() {
    return speed;
  }
}
