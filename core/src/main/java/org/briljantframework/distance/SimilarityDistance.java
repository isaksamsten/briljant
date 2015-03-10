package org.briljantframework.distance;

import org.briljantframework.similiarity.Similarity;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 10/03/15.
 */
public class SimilarityDistance implements Distance {
  private final Similarity similarity;

  public SimilarityDistance(Similarity similarity) {
    this.similarity = similarity;
  }

  @Override
  public double compute(double a, double b) {
    throw new UnsupportedOperationException();
  }

  @Override
  public double compute(Vector a, Vector b) {
    return -similarity.compute(a, b);
  }

  @Override
  public double max() {
    return Double.POSITIVE_INFINITY;
  }

  @Override
  public double min() {
    return Double.NEGATIVE_INFINITY;
  }
}
