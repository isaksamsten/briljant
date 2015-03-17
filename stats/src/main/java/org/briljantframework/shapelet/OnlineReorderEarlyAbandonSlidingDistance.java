package org.briljantframework.shapelet;

import org.briljantframework.distance.Distance;
import org.briljantframework.distance.Euclidean;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * TODO(isak): make this work! Created by Isak Karlsson on 27/10/14.
 */
public class OnlineReorderEarlyAbandonSlidingDistance extends EarlyAbandonSlidingDistance {
  /**
   * Instantiates a new Online reorder early abandon sliding distance.
   *
   * @param distance the distance
   */
  public OnlineReorderEarlyAbandonSlidingDistance(Distance distance) {
    super(distance);
  }

  /**
   * Create online reorder early abandon sliding distance.
   *
   * @return the online reorder early abandon sliding distance
   */
  public static OnlineReorderEarlyAbandonSlidingDistance create() {
    return new OnlineReorderEarlyAbandonSlidingDistance(Euclidean.getInstance());
  }


  @Override
  public double compute(Vector a, Vector b) {
    // Candidate is normalized
    Vector candidate = a.size() < b.size() ? a : b;
    int l = candidate.size();

    int[] order;
    if (candidate instanceof IndexSortedNormalizedShapelet) {
      order = ((IndexSortedNormalizedShapelet) candidate).getSortOrder();
    } else {
      order =
          Vectors.indexSort(
              candidate,
              (i, j) -> Double.compare(Math.abs(candidate.getAsDouble(j)),
                                       Math.abs(candidate.getAsDouble(i))));
    }

    Vector vector = a.size() >= b.size() ? a : b;
    int m = vector.size();

    double ex = 0, ex2 = 0; // running sum and square sum
    double best = Double.POSITIVE_INFINITY;

    // todo: get this temporary array out of the way
    double[] T = new double[l * 2];
    int loc = 0;

    for (int i = 0; i < m; i++) {
      double ti = vector.getAsDouble(i);
      T[i % l] = ti;
      T[(i % l) + l] = ti;

      ex += ti;
      ex2 += ti * ti;

      if (i >= l - 1) {
        double mean = ex / l;
        double std = Math.sqrt(ex2 / l - (mean * mean));
        int index = (i + 1) % l;

        int j = 0;
        double d = 0;
        while (j < l && d < best) {
          double sum = candidate.getAsDouble(order[j]) - (T[order[j]] - mean) / std;
          d += sum * sum;
          j++;
        }

        if (j == l && d < best) {
          loc = i - l + 1;
          best = d;
        }
        double v = T[index];
        ex -= v;
        ex2 -= v * v;
      }

    }
    // System.out.println(loc);

    return best;
  }
}
