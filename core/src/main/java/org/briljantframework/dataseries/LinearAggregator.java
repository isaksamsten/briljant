package org.briljantframework.dataseries;

import static com.google.common.base.Preconditions.checkArgument;

import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Type;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 12/12/14.
 */
public class LinearAggregator implements Aggregator {

  private final int targetSize;

  public LinearAggregator(int targetSize) {
    this.targetSize = targetSize;
  }

  @Override
  public Vector.Builder partialAggregate(Vector in) {
    checkArgument(in.size() > targetSize, "Can't linearly oversample data series.");

    DoubleVector.Builder builder = new DoubleVector.Builder(0, targetSize);
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
      builder.add(lerp(in.getAsDouble(start), in.getAsDouble(end), w));
      currentIndex += binInc;
    }
    return builder;
  }

  @Override
  public Type getAggregatedType() {
    return DoubleVector.TYPE;
  }

  private double lerp(double a, double b, double w) {
    return ((1 - w) * a) + (w * b);
  }
}
