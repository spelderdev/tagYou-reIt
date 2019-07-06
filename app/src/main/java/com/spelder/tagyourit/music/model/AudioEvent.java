package com.spelder.tagyourit.music.model;

import com.spelder.tagyourit.music.utility.ByteConverter;

/**
 * A single audio event containing the audio buffer and methods for converting the buffer based on
 * the processors usage.
 */
public class AudioEvent {
  private int channelNumber;

  private double sampleRate;

  private float[][] channelBuffer;

  public AudioEvent(int channelNumber) {
    setChannelNumber(channelNumber);
  }

  public int getChannelNumber() {
    return channelNumber;
  }

  public void setChannelNumber(int channelNumber) {
    this.channelNumber = channelNumber;
    channelBuffer = new float[channelNumber][];
  }

  public byte[] getByteBuffer() {
    return ByteConverter.convertToByteArray(getFloatBuffer());
  }

  public void setByteBuffer(byte[] byteBuffer) {
    setChannelBuffer(ByteConverter.convertToFloatArray(byteBuffer));
  }

  private float[] getFloatBuffer() {
    float[] floatBuffer = new float[channelBuffer[0].length * channelNumber];
    for (int i = 0; i < channelBuffer.length; i++) {
      for (int j = 0; j < channelBuffer[i].length; j++) {
        floatBuffer[(j * channelNumber) + i] = channelBuffer[i][j];
      }
    }

    return floatBuffer;
  }

  public void setFloatBuffer(float[] floatBuffer) {
    setChannelBuffer(floatBuffer);
  }

  public double getSampleRate() {
    return sampleRate;
  }

  public void setSampleRate(double sampleRate) {
    this.sampleRate = sampleRate;
  }

  public float[] getFloatBuffer(int channel) {
    return channelBuffer[channel - 1];
  }

  public float[][] getChannelBuffer() {
    return channelBuffer;
  }

  private void setChannelBuffer(float[] floatBuffer) {
    for (int i = 0; i < channelBuffer.length; i++) {
      channelBuffer[i] = new float[floatBuffer.length / channelNumber];
    }
    for (int i = 0; i < floatBuffer.length; i++) {
      channelBuffer[i % channelNumber][(int) Math.floor((double) i / channelNumber)] =
          floatBuffer[i];
    }
  }

  public void setFloatBuffer(float[] floatBuffer, int channel) {
    channelBuffer[channel - 1] = floatBuffer;
  }
}
