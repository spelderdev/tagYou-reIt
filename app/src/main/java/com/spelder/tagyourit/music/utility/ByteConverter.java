package com.spelder.tagyourit.music.utility;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Converts between byte array and float array. */
public class ByteConverter {
  public static byte[] convertToByteArray(float[] floatArray) {
    byte[] byteArray = new byte[floatArray.length * 2];
    for (int i = 0; i < floatArray.length; i++) {
      short j = (short) (floatArray[i] * 32768);
      byte[] tmp = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(j).array();
      byteArray[i * 2] = tmp[0];
      byteArray[i * 2 + 1] = tmp[1];
    }

    return byteArray;
  }

  public static float[] convertToFloatArray(byte[] byteArray) {
    float[] floatArray = new float[byteArray.length / 2];
    for (int i = 0; i < byteArray.length / 2; i++) {
      float val =
          ((float) ByteBuffer.wrap(byteArray, i * 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort())
              / (float) 32768;

      if (val > 1) {
        val = 1;
      }
      if (val < -1) {
        val = -1;
      }
      floatArray[i] = val;
    }

    return floatArray;
  }
}
