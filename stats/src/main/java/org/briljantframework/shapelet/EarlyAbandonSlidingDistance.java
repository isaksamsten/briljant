/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.shapelet;

import com.google.common.base.Preconditions;

import org.briljantframework.distance.Distance;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 23/09/14.
 */
public class EarlyAbandonSlidingDistance implements Distance {

  protected final Distance distance;

  /**
   * Instantiates a new Early abandon sliding distance.
   *
   * @param distance the distance
   */
  public EarlyAbandonSlidingDistance(Distance distance) {
    this.distance = Preconditions.checkNotNull(distance, "Requires a distance measure");
  }

  /**
   * Create early abandon sliding distance.
   *
   * @param distance the distance
   * @return the early abandon sliding distance
   */
  public static EarlyAbandonSlidingDistance create(Distance distance) {
    return new EarlyAbandonSlidingDistance(distance);
  }

  @Override
  public double compute(double a, double b) {
    return distance.compute(a, b);
  }

  /**
   * If {@code a} is shorter than {@code b}, then {@code a} is considered a shapelet and slid
   * against {@code b} and wise-versa.
   *
   * <p> The shorter vector (i.e. the shapelet) is expected to be z-normalized
   *
   * @param a a vector
   * @param b a vector
   * @return the shortest possible distance of a (or b) as it is slid against b (or a)
   */
  @Override
  public double compute(Vector a, Vector b) {
    double minDistance = Double.POSITIVE_INFINITY;
    Vector candidate = a.size() < b.size() ? a : b;
    if (!(candidate instanceof NormalizedShapelet)) {
      throw new IllegalArgumentException("Candidate shapelet must be z-normalized");
    }

    int[] order = null;
    // If the candidate is IndexSorted, use this to optimize the search
    if (candidate instanceof IndexSortedNormalizedShapelet) {
      order = ((IndexSortedNormalizedShapelet) candidate).getSortOrder();
    }

    Vector vector = a.size() >= b.size() ? a : b;
    int seriesSize = vector.size();
    int m = candidate.size();
    double[] t = new double[m * 2];

    double ex = 0;
    double ex2 = 0;
    for (int i = 0; i < seriesSize; i++) {
      double d = vector.getAsDouble(i);
      ex += d;
      ex2 += d * d;
      t[i % m] = d;
      t[(i % m) + m] = d;

      if (i >= m - 1) {
        int j = (i + 1) % m;
        double mean = ex / m;
        double sigma = StrictMath.sqrt(ex2 / m - mean * mean);
        double dist = distance(candidate, t, j, m, order, mean, sigma, minDistance);
        if (dist < minDistance) {
          minDistance = dist;
        }

        ex -= t[j];
        ex2 -= t[j] * t[j];
      }
    }
    return Math.sqrt(minDistance / candidate.size());
  }

  @Override
  public double max() {
    return distance.max();
  }

  @Override
  public double min() {
    return distance.min();
  }

  double distance(Vector c, double[] t, int j, int m, int[] order, double mean, double std,
                  double bsf) {
    double sum = 0;
    for (int i = 0; i < m && sum < bsf; i++) {
      if (order != null) {
        i = order[i];
      }
      double x = ((t[i + j] - mean) / std) - c.getAsDouble(i);
      sum += x * x;
    }
    return sum;
  }
}
