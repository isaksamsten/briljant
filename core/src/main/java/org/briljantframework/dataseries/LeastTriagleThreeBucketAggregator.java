package org.briljantframework.dataseries;

import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Type;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * Implements the Least Triangle ... ... Data Series resampler found in [cite the thesis].
 * 
 * The implementation assumes that the values are ordered in increasing order.
 * 
 * @author Isak Karlsson
 */
public class LeastTriagleThreeBucketAggregator implements Aggregator {

  private final int threshold;

  public LeastTriagleThreeBucketAggregator(int threshold) {
    this.threshold = threshold;
  }

  @Override
  public Vector.Builder partialAggregate(Vector in) {
    Preconditions.checkNotNull(in);
    if (in.size() < threshold || threshold == 0) {
      return in.newCopyBuilder();
    }
    Vector.Builder sampled = in.newBuilder();

    // Bucket size
    double every = (double) (in.size() - 2) / (threshold - 2);

    int a = 0;
    int nextA = 0;

    int[] indexes = new int[in.size()];
    for (int i = 0; i < in.size(); i++) {
      indexes[i] = i;
    }
    sampled.add(in, a);
    for (int i = 0; i < threshold - 2; i++) {
      int avgX = 0;
      double avgY = 0;
      int avgRangeStart = (int) Math.floor((i + 1) * every) + 1;
      int avgRangeEnd = Math.min((int) Math.floor((i + 2) * every) + 1, in.size());
      int rangeLength = avgRangeEnd - avgRangeStart;

      for (; avgRangeStart < avgRangeEnd; avgRangeStart++) {
        avgX += indexes[avgRangeStart];
        avgY += in.getAsDouble(avgRangeStart);
      }
      avgX /= rangeLength;
      avgY /= rangeLength;

      int rangeOffset = (int) Math.floor(i * every) + 1;
      int rangeTo = (int) Math.floor((i + 1) * every) + 1;

      int pointX = indexes[a];
      double pointY = in.getAsDouble(a);
      double maxArea = Double.NEGATIVE_INFINITY;
      for (; rangeOffset < rangeTo; rangeOffset++) {
        double xDiff = pointX - avgX * (in.getAsDouble(rangeOffset) - pointY);
        double yDiff = pointY - avgY * (pointX - indexes[rangeOffset]);
        double area = Math.abs(xDiff - yDiff) * 0.5;
        if (area > maxArea) {
          maxArea = area;
          nextA = rangeOffset;
        }
      }
      sampled.add(in, nextA);
      a = nextA;
    }

    return sampled.add(in, in.size() - 1);
  }

  @Override
  public Type getAggregatedType() {
    return DoubleVector.TYPE;
  }
}
