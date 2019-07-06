package com.spelder.tagyourit.music.player;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;
import com.spelder.tagyourit.music.model.AudioEvent;
import com.spelder.tagyourit.music.model.AudioFormat;
import com.spelder.tagyourit.music.model.Speed;
import com.spelder.tagyourit.music.processor.AudioProcessor;
import java.io.IOException;
import java.io.PipedInputStream;
import java.util.List;

/** Writes the raw audio buffer to the AudioTrack, which actually plays the audio. */
class TrackWriter implements Runnable {
  private static final String TAG = TrackWriter.class.getName();
  private final PipedInputStream inputStream;
  private final AudioFormat format;
  private final PlayerState state;
  private List<AudioProcessor> processors;
  private AudioTrack audioTrack;
  private boolean running = false;

  TrackWriter(PipedInputStream inputStream, AudioFormat format, PlayerState state) {
    this.inputStream = inputStream;
    this.format = format;
    this.state = state;

    initializeAudioTrack();
  }

  private void initializeAudioTrack() {
    int channelConfiguration =
        format.getChannels() == 1
            ? android.media.AudioFormat.CHANNEL_OUT_MONO
            : android.media.AudioFormat.CHANNEL_OUT_STEREO;
    int minSize =
        AudioTrack.getMinBufferSize(
                format.getSampleRate(),
                channelConfiguration,
                android.media.AudioFormat.ENCODING_PCM_16BIT)
            * 2;
    audioTrack =
        new AudioTrack(
            AudioManager.STREAM_MUSIC,
            format.getSampleRate(),
            android.media.AudioFormat.CHANNEL_OUT_MONO,
            android.media.AudioFormat.ENCODING_PCM_16BIT,
            minSize,
            AudioTrack.MODE_STREAM);
  }

  @Override
  public void run() {
    running = true;
    try {
      android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

      audioTrack.play();

      byte[] chunk = new byte[AudioPlayer.BUFFER_SIZE];
      while (state.isRunning()) {
        state.waitPlay();
        if (inputStream != null
            && inputStream.available() >= AudioPlayer.BUFFER_SIZE
            && inputStream.read(chunk) != -1) {
          AudioEvent event = new AudioEvent(format.getChannels());
          event.setByteBuffer(chunk);
          event.setSampleRate(format.getSampleRate());

          for (AudioProcessor processor : processors) {
            processor.process(event);
          }

          byte[] byteBuffer = event.getByteBuffer();
          if (audioTrack != null) {
            audioTrack.write(byteBuffer, 0, byteBuffer.length);
          }
        } else {
          Thread.sleep(1);
        }
      }
    } catch (IOException | InterruptedException | IllegalStateException e) {
      Log.e(TAG, "Stopped processing: ", e);
    }
    running = false;
  }

  void setProcessor(List<AudioProcessor> processors) {
    this.processors = processors;
  }

  boolean setSpeed(Speed speed) {
    // this checks on API 23 and up
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (state.isPlaying()) {
        audioTrack.setPlaybackParams(audioTrack.getPlaybackParams().setSpeed(speed.getSpeed()));
      } else {
        audioTrack.setPlaybackParams(audioTrack.getPlaybackParams().setSpeed(speed.getSpeed()));
        state.pause();
      }

      return true;
    }

    return false;
  }

  void clear() {
    if (audioTrack != null) {
      audioTrack.flush();
    }
  }

  void destroy() {
    if (audioTrack != null) {
      audioTrack.flush();
      audioTrack.release();
      audioTrack = null;
    }
  }

  boolean isRunning() {
    return running;
  }
}
