package org.briljantframework.dataseries;

import static com.google.common.base.Preconditions.checkArgument;

import org.briljantframework.vector.Vector;
import org.briljantframework.vector.transform.Transformation;

/**
 * Created by Isak Karlsson on 12/12/14.
 */
public class LinearResampler implements Transformation {

  private final int targetSize;

  public LinearResampler(int targetSize) {
    this.targetSize = targetSize;
  }

  @Override
  public Vector transform(Vector in) {
    checkArgument(in.size() > targetSize, "Can't linearly oversample data series.");

    Vector.Builder out = in.newBuilder();
    int bin = in.size() / targetSize;
    int pad = in.size() % targetSize;

    System.out.println(bin);
    int currentIndex = 0;
    int toPad = 0;
    while (currentIndex < in.size()) {
      int inc = 0;
      // In some cases in.size() / targetSize result in a reminder,
      // distribute this reminder equally over all bins
      if (toPad++ < pad) {
        inc = 1;
      }
      int binInc = bin + inc;
      int start = currentIndex;
      int end = currentIndex + binInc - 1;
      double w = (double) 1 / 5;
      out.add(lerp(in.getAsDouble(start), in.getAsDouble(end), w));


      currentIndex += binInc;
    }
    return out.build();
  }

  private double lerp(double a, double b, double w) {
    return ((1 - w) * a) + (w * b);
  }
}
