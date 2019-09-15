package com.spelder.tagyourit.networking.api.filter;

public enum Key {
  ANY("any","ANY"),
  A("A|Aminor|Adorian", "A"),
  B_FLAT("Bflat|Bflatminor|Bflatdorian|Asharp|Asharpminor|Asharpdorian", "Bb", "A#"),
  B("B|Bminor|Bdorian", "B"),
  C("C|Cminor|Cdorian", "C"),
  D_FLAT("Dflat|Dflatminor|Dflatdorian|Csharp|Csharpminor|Csharpdorian", "Db", "C#"),
  D("D|Dminor|Ddorian", "D"),
  E_FLAT("Eflat|Eflatminor|Eflatdorian|Dsharp|Dsharpminor|Dsharpdorian", "Eb", "D#"),
  E("E|Eminor|Edorian", "E"),
  F("F|Fminor|Fdorian", "F"),
  G_FLAT("Gflat|Gflatminor|Gflatdorian|Fsharp|Fsharpminor|Fsharpdorian", "Gb", "F#"),
  G("G|Gminor|Gdorian", "G"),
  A_FLAT("Aflat|Aflatminor|Aflatdorian|Gsharp|Gsharpminor|Gsharpdorian", "Ab", "G#");

  private static final String KEY_LABEL = "WritKey=";

  private final String key;

  private final String[] dbKeys;

  Key(String key, String... dbKeys) {
    this.key = key;
    this.dbKeys = dbKeys;
  }

  public String getFilter() {
    return KEY_LABEL + key;
  }

  public String[] getDbKeys() {
    return dbKeys;
  }
}
