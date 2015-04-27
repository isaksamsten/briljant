package org.briljantframework.shapelet;

import org.briljantframework.distance.Distance;
import org.briljantframework.distance.EditDistance;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 24/03/15.
 */
public class SlidingDistance implements Distance {

  private final Distance measure = new EditDistance();

  @Override
  public double compute(double a, double b) {
    return 0;
  }

  @Override
  public double compute(Vector a, Vector b) {
    double minDistance = Double.POSITIVE_INFINITY;

    // Assumed to be normalized!
    Vector candidate = a.size() < b.size() ? a : b;
    Vector vector = a.size() >= b.size() ? a : b;
    for (int i = 0; i <= vector.size() - candidate.size(); i++) {
      Shapelet subShapelet = Shapelet.create(i, candidate.size(), vector);
      double sumDistance = measure.compute(candidate, subShapelet);
      if (sumDistance < minDistance) {
        minDistance = sumDistance;
      }
    }
    return Math.sqrt(minDistance / candidate.size());
  }

  @Override
  public double max() {
    return 0;
  }

  @Override
  public double min() {
    return 0;
  }
}
