package com.spelder.tagyourit.music.processor;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import android.util.Log;
import com.spelder.tagyourit.music.model.AudioEvent;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/** Pitch shifts the audio buffer using Fourier Transform. */
public class PitchShiftProcessor implements AudioProcessor {
  private static final String TAG = PitchShiftProcessor.class.getName();

  private static final int O_SAMPLE = 8;

  private final int size;

  private final float[] gInFIFO;

  private final float[] gOutFIFO;

  private final double[] gLastPhase;

  private final float[] gSumPhase;

  private final float[] gOutputAccumulator;

  private final double[] gAnaFreq;

  private final double[] gAnaMagnitude;

  private final FastFourierTransformer fft;

  private double pitchShift;

  private Complex[] gFftWorkspace;

  private int gRover = 0;

  private PitchShiftProcessor(int size) {
    this.size = size;
    gInFIFO = new float[size];
    gOutFIFO = new float[size];
    gFftWorkspace = new Complex[size];
    gLastPhase = new double[size / 2 + 1];
    gSumPhase = new float[size / 2 + 1];
    gOutputAccumulator = new float[2 * size];
    gAnaFreq = new double[size];
    gAnaMagnitude = new double[size];
    fft = new FastFourierTransformer(DftNormalization.STANDARD);
  }

  public PitchShiftProcessor(int semitones, int size) {
    this(size);
    setPitch(semitones);
    Log.d(TAG, "Pitch shift factor: " + pitchShift);
  }

  public void setPitch(int semitones) {
    pitchShift = Math.pow(2, (double) semitones / 12);
  }

  public void setPitch(double cents) {
    pitchShift = Math.pow(2, cents / 1200);
  }

  @Override
  public void process(AudioEvent audioEvent) {
    if (pitchShift > 0.999 && pitchShift < 1.00001) {
      return;
    }

    for (int channel = 1; channel <= audioEvent.getChannelNumber(); channel++) {
      float[] floatBuffer = audioEvent.getFloatBuffer(channel);
      float[] outBuffer = new float[floatBuffer.length];
      pitchShift(size, (float) audioEvent.getSampleRate(), floatBuffer, outBuffer);
      audioEvent.setFloatBuffer(outBuffer, channel);
    }
  }

  private void pitchShift(int fftFrameSize, float sampleRate, float[] inData, float[] outData) {
    double magnitude, phase, tmp, window, real, imaginary;
    double freqPerBin, expected;
    int qpd, index, inFifoLatency, stepSize, fftFrameSize2;

    /* set up some handy variables */
    fftFrameSize2 = fftFrameSize / 2;
    stepSize = fftFrameSize / O_SAMPLE;
    freqPerBin = sampleRate / (double) fftFrameSize;
    expected = 2 * PI * (double) stepSize / (double) fftFrameSize;
    inFifoLatency = fftFrameSize - stepSize;
    if (gRover == 0) {
      gRover = inFifoLatency;
    }

    /* main processing loop */
    for (int i = 0; i < fftFrameSize; i++) {
      /* As long as we have not yet collected enough data just read in */
      gInFIFO[gRover] = inData[i];
      outData[i] = gOutFIFO[gRover - inFifoLatency];
      gRover++;

      /* now we have enough data for processing */
      if (gRover >= fftFrameSize) {
        gRover = inFifoLatency;

        /* do windowing and re,im interleave */
        for (int k = 0; k < fftFrameSize; k++) {
          window = -.5 * cos(2 * PI * k / (double) fftFrameSize) + .5;
          gFftWorkspace[k] = new Complex(gInFIFO[k] * window, 0);
        }

        /* ***************** ANALYSIS ******************* */
        /* do transform */
        gFftWorkspace = fft.transform(gFftWorkspace, TransformType.FORWARD);

        /* this is the analysis step */
        for (int k = 0; k < fftFrameSize2; k++) {
          /* de-interlace FFT buffer */
          real = gFftWorkspace[k].getReal();
          imaginary = gFftWorkspace[k].getImaginary();

          /* compute magnitude and phase */
          magnitude = 2 * sqrt(real * real + imaginary * imaginary);
          phase = atan2(imaginary, real);

          /* compute phase difference */
          tmp = phase - gLastPhase[k];
          gLastPhase[k] = phase;

          /* subtract expected phase difference */
          tmp -= k * expected;

          /* map delta phase into +/- Pi interval */
          qpd = (int) (tmp / PI);
          if (qpd >= 0) {
            qpd += qpd & 1;
          } else {
            qpd -= qpd & 1;
          }
          tmp -= PI * qpd;

          /* get deviation from bin frequency from the +/- Pi interval */
          tmp = O_SAMPLE * tmp / (2. * PI);

          /* compute the k-th partials' true frequency */
          tmp = k * freqPerBin + tmp * freqPerBin;

          /* store magnitude and true frequency in analysis arrays */
          gAnaMagnitude[k] = magnitude;
          gAnaFreq[k] = tmp;
        }

        /* ***************** PROCESSING ******************* */
        /* this does the actual pitch shifting */
        double[] gSynMagnitude = new double[fftFrameSize2];
        double[] gSynFreq = new double[fftFrameSize2];

        for (int k = 0; k < fftFrameSize2; k++) {
          index = (int) (k * pitchShift);
          if (index < fftFrameSize2) {
            gSynMagnitude[index] += gAnaMagnitude[k];
            gSynFreq[index] = gAnaFreq[k] * pitchShift;
          }
        }

        /* ***************** SYNTHESIS ******************* */
        /* this is the synthesis step */
        for (int k = 0; k < fftFrameSize2; k++) {
          /* get magnitude and true frequency from synthesis arrays */
          magnitude = gSynMagnitude[k];
          tmp = gSynFreq[k];

          /* subtract bin mid frequency */
          tmp -= k * freqPerBin;

          /* get bin deviation from freq deviation */
          tmp /= freqPerBin;

          /* take outSample into account */
          tmp = 2 * PI * tmp / O_SAMPLE;

          /* add the overlap phase advance back in */
          tmp += k * expected;

          /* accumulate delta phase to get bin phase */
          gSumPhase[k] += tmp;
          phase = gSumPhase[k];

          /* get real and imaginary part and re-interleave */
          gFftWorkspace[k] = new Complex(magnitude * cos(phase), magnitude * sin(phase));
        }

        /* zero negative frequencies */
        for (int k = fftFrameSize2 + 1; k < fftFrameSize; k++) {
          gFftWorkspace[k] = new Complex(0, 0);
        }

        /* do inverse transform */
        gFftWorkspace = fft.transform(gFftWorkspace, TransformType.INVERSE);

        /* do windowing and add to output accumulator */
        for (int k = 0; k < fftFrameSize; k++) {
          window = -.5 * cos(2. * PI * k / fftFrameSize) + .5;
          gOutputAccumulator[k] += 2 * window * gFftWorkspace[k].getReal() / O_SAMPLE;
        }
        System.arraycopy(gOutputAccumulator, 0, gOutFIFO, 0, stepSize);

        /* shift accumulator */
        System.arraycopy(gOutputAccumulator, stepSize, gOutputAccumulator, 0, fftFrameSize);

        /* move input FIFO */
        System.arraycopy(gInFIFO, stepSize, gInFIFO, 0, inFifoLatency);
      }
    }
  }
}
