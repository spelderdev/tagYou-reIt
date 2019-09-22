package com.spelder.tagyourit.networking.api.filter;

public enum Key {
  ANY("Key", "any", "ANY"),
  A("A", "A|Aminor|Adorian", "A"),
  B_FLAT("Bb", "Bflat|Bflatminor|Bflatdorian|Asharp|Asharpminor|Asharpdorian", "Bb", "A#"),
  B("B", "B|Bminor|Bdorian", "B"),
  C("C", "C|Cminor|Cdorian", "C"),
  D_FLAT("Db", "Dflat|Dflatminor|Dflatdorian|Csharp|Csharpminor|Csharpdorian", "Db", "C#"),
  D("D", "D|Dminor|Ddorian", "D"),
  E_FLAT("Eb", "Eflat|Eflatminor|Eflatdorian|Dsharp|Dsharpminor|Dsharpdorian", "Eb", "D#"),
  E("E", "E|Eminor|Edorian", "E"),
  F("F", "F|Fminor|Fdorian", "F"),
  G_FLAT("Gb", "Gflat|Gflatminor|Gflatdorian|Fsharp|Fsharpminor|Fsharpdorian", "Gb", "F#"),
  G("G", "G|Gminor|Gdorian", "G"),
  A_FLAT("Ab", "Aflat|Aflatminor|Aflatdorian|Gsharp|Gsharpminor|Gsharpdorian", "Ab", "G#");

  private static final String KEY_LABEL = "WritKey=";

  private final String key;
  private final String[] dbKeys;
  private final String displayName;

  Key(String displayName, String key, String... dbKeys) {
    this.key = key;
    this.dbKeys = dbKeys;
    this.displayName = displayName;
  }

  public String getFilter() {
    return KEY_LABEL + key;
  }

  public String[] getDbKeys() {
    return dbKeys;
  }

  public String getDisplayName() {
    return displayName;
  }
}
