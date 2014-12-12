package org.briljantframework.dataseries;

import static com.google.common.base.Preconditions.checkArgument;

import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 12/12/14.
 */
public class LinearResampler implements DataSeriesResampler {

  private final int targetSize;

  public LinearResampler(int targetSize) {
    this.targetSize = targetSize;
  }

  @Override
  public Vector resample(Vector in) {
    checkArgument(in.size() > targetSize);

    Vector.Builder out = in.newBuilder();
    int bin = in.size() / targetSize;
    int pad = in.size() % targetSize;

    int currentIndex = 0;
    int toPad = 0;
    while (currentIndex < in.size()) {
      int inc = 0;
      if (toPad++ < pad) {
        inc = 1;
      }
      int binInc = bin + inc;
      int start = currentIndex;
      int end = currentIndex + binInc - 1;
      double w = start / end;
      out.add(lerp(in.getAsDouble(start), in.getAsDouble(end), w));


      currentIndex += binInc;
      // double sum = 0;
      // for (int j = 0; j < binInc; j++) {
      // sum += in.getAsDouble(currentIndex++);
      // }
      // out.add(sum / binInc);
    }
    return out.build();
  }

  private double lerp(double a, double b, double w) {
    return ((1 - w) * a) + (w * b);
  }
}
