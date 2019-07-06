package com.spelder.tagyourit.music.player;

import static com.spelder.tagyourit.music.player.AudioPlayer.BUFFER_SIZE;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import com.spelder.tagyourit.music.model.AudioFormat;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;

/** Extracts audio from a file to the raw audio buffer needed for processing. */
class FileAudioExtractor extends AudioExtractor {
  private static final String TAG = TrackWriter.class.getName();

  private static final int NO_OUTPUT_COUNTER_LIMIT = 10;

  private static final long CODEC_TIMEOUT = 1000;

  private MediaCodec codec;

  private MediaExtractor extractor;

  private FileDescriptor audioFile;

  FileAudioExtractor(
      PipedOutputStream outputStream,
      PipedInputStream inputStream,
      EventHandler eventHandler,
      PlayerState state) {
    super(outputStream, inputStream, eventHandler, state);
  }

  void setAudioFile(FileDescriptor audioFile) {
    this.audioFile = audioFile;
  }

  @Override
  AudioFormat initialize() {
    extractor = new MediaExtractor();
    format = new AudioFormat();
    try {
      extractor.setDataSource(audioFile);

      MediaFormat mediaFormat = extractor.getTrackFormat(0);
      format.setAudioParameters(mediaFormat, audioFile);

      codec = MediaCodec.createDecoderByType(format.getMime());
      codec.configure(mediaFormat, null, null, 0);
    } catch (IOException e) {
      Log.e(TAG, "Failed to set data source", e);
      eventHandler.sendOnErrorEvent();
    }

    Log.d(
        TAG,
        "Track info: mime:"
            + format.getMime()
            + " sampleRate:"
            + format.getSampleRate()
            + " channels:"
            + format.getChannels()
            + " bitrate:"
            + format.getBitrate()
            + " durationMillis:"
            + format.getDurationMillis());

    // check we have audio content we know
    if (format == null || !format.getMime().startsWith("audio/")) {
      eventHandler.sendOnErrorEvent();
    }

    return format;
  }

  @Override
  public void run() {
    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

    codec.start();
    extractor.selectTrack(0);

    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
    boolean sawInputEndOfStream = false;
    boolean sawOutputEndOfStream = false;
    int noOutputCounter = 0;

    while (!sawOutputEndOfStream
        && noOutputCounter < NO_OUTPUT_COUNTER_LIMIT
        && state.isRunning()) {
      state.waitPlay();
      if (!state.isRunning()) {
        break;
      }

      noOutputCounter++;
      if (!sawInputEndOfStream) {
        sawInputEndOfStream = extractInput();
      }

      sawOutputEndOfStream = writeOutput(info);
      if (info.size > 0) {
        noOutputCounter = 0;
      }
    }

    try {
      while (inputStream.available() >= BUFFER_SIZE) {
        Thread.sleep(1);
      }
    } catch (IOException | InterruptedException e) {
      Log.e(TAG, "Cannot wait to finish", e);
    }
    cleanup();

    if (noOutputCounter >= NO_OUTPUT_COUNTER_LIMIT) {
      eventHandler.sendOnErrorEvent();
    } else if (!state.isStopped()) {
      eventHandler.sendOnCompletionEvent();
    }

    state.uninitialize();
  }

  private boolean extractInput() {
    boolean sawInputEndOfStream = false;
    int inputBufferIndex = codec.dequeueInputBuffer(CODEC_TIMEOUT);
    if (inputBufferIndex >= 0) {
      ByteBuffer destinationBuffer = codec.getInputBuffer(inputBufferIndex);
      int sampleSize = 0;
      if (destinationBuffer != null) {
        sampleSize = extractor.readSampleData(destinationBuffer, 0);
      }
      if (sampleSize < 0) {
        Log.d(TAG, "saw input end of stream. Stopping playback");
        sawInputEndOfStream = true;
        sampleSize = 0;
      } else {
        presentationTime = extractor.getSampleTime();
      }

      codec.queueInputBuffer(
          inputBufferIndex,
          0,
          sampleSize,
          presentationTime,
          sawInputEndOfStream ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

      if (!sawInputEndOfStream) {
        extractor.advance();
      }
    } else {
      Log.e(TAG, "inputBufferIndex " + inputBufferIndex);
    }

    return sawInputEndOfStream;
  }

  private boolean writeOutput(MediaCodec.BufferInfo info) {
    int bufferIndex = codec.dequeueOutputBuffer(info, CODEC_TIMEOUT);
    if (bufferIndex >= 0) {
      ByteBuffer buffer = codec.getOutputBuffer(bufferIndex);
      byte[] tmp = new byte[info.size];
      if (buffer != null) {
        buffer.get(tmp);
        buffer.clear();
      }

      try {
        outputStream.write(tmp);
        outputStream.flush();
      } catch (IOException e) {
        Log.e(TAG, "Cannot write: ", e);
      }

      codec.releaseOutputBuffer(bufferIndex, false);
      if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
        Log.d(TAG, "saw output EOS.");
        return true;
      }
    } else if (bufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
      MediaFormat outputFormat = codec.getOutputFormat();
      Log.d(TAG, "output format has changed to " + outputFormat);
    } else {
      Log.d(TAG, "dequeueOutputBuffer returned " + bufferIndex);
    }

    return false;
  }

  protected void performSeek(long timeMillis) {
    extractor.seekTo(timeMillis, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
  }

  @Override
  protected void cleanup() {
    if (codec != null) {
      codec.stop();
      codec.release();
      codec = null;
    }

    super.cleanup();
  }
}
