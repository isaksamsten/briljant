package org.briljantframework.dataseries;

import static com.google.common.base.Preconditions.checkArgument;

import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Type;
import org.briljantframework.vector.Vector;

/**
 * The MeanResampler implements the perhaps simplest resampling (approximation) method for data
 * series. Divide the data series into bins, and take the mean of each bin as the new data series.
 * 
 * This is know under the fancy name Piecewise Aggregate Approximation
 * 
 * @author Isak Karlsson
 */
public class MeanAggregator implements Aggregator {
  private final int targetSize;

  public MeanAggregator(int targetSize) {
    this.targetSize = targetSize;
  }

  @Override
  public Vector.Builder partialAggregate(Vector in) {
    checkArgument(in.size() >= targetSize, "Input size must be smaller than target size.");
    if (in.size() == targetSize) {
      return in.newCopyBuilder();
    }
    DoubleVector.Builder out = new DoubleVector.Builder(0, targetSize);
    int bin = in.size() / targetSize;
    int pad = in.size() % targetSize;

    int currentIndex = 0;
    int toPad = 0;
    while (currentIndex < in.size()) {
      int inc = 0;
      if (toPad++ < pad) {
        inc = 1;
      }
      double sum = 0;
      int binInc = bin + inc;
      for (int j = 0; j < binInc; j++) {
        sum += in.getAsDouble(currentIndex++);
      }
      out.add(sum / binInc);
    }
    return out;
  }

  @Override
  public Type getAggregatedType() {
    return DoubleVector.TYPE;
  }
}
