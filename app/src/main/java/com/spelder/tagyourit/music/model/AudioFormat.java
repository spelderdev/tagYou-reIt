package com.spelder.tagyourit.music.model;

import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import java.io.FileDescriptor;

/** Model for the format of the audio. */
public class AudioFormat {
  private int sampleRate = 44100, channels = 1, bitrate = 5644800;

  private String mime = null;

  private long durationMillis = 0;

  public int getSampleRate() {
    return sampleRate;
  }

  public int getChannels() {
    return channels;
  }

  public int getBitrate() {
    return bitrate;
  }

  public String getMime() {
    return mime;
  }

  public long getDurationMillis() {
    return durationMillis;
  }

  public void setAudioParameters(MediaFormat format, FileDescriptor audioFile) {
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    mmr.setDataSource(audioFile);

    mime = format.getString(MediaFormat.KEY_MIME);
    sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
    channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

    if (format.containsKey(MediaFormat.KEY_DURATION)) {
      durationMillis = format.getLong(MediaFormat.KEY_DURATION) / 1000;
    } else {
      String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
      durationMillis = Integer.parseInt(durationStr);
    }

    if (format.containsKey(MediaFormat.KEY_BIT_RATE)) {
      bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);
    } else {
      String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
      bitrate = Integer.parseInt(durationStr);
    }
  }
}
