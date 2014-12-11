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

import org.briljantframework.DoubleArray;
import org.briljantframework.distance.Distance;

import com.google.common.base.Preconditions;

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
  public double distance(double a, double b) {
    return distance.distance(a, b);
  }

  /**
   * If {@code a} is shorter than {@code b}, then {@code a} is considered a shapelet and slid
   * against {@code b} and wise-versa.
   * <p>
   * The shorter (i.e. the shapelet) is expected to be z-normalized
   *
   * @param a a vector
   * @param b a vector
   * @return the shortest possible distance of a (or b) as it is slid agains b (or a)
   */
  @Override
  public double distance(DoubleArray a, DoubleArray b) {
    double minDistance = Double.POSITIVE_INFINITY;
    boolean earlyStop = false;

    // Assumed to be normalized!
    DoubleArray candidate = a.size() < b.size() ? a : b;
    if (!(candidate instanceof NormalizedShapelet)) {
      throw new IllegalArgumentException("candidate shapelet must be z-normalized");
    }

    int[] order = null;

    // If the candidate is IndexSorted, use this to optimize the search
    if (candidate instanceof IndexSortedNormalizedShapelet) {
      order = ((IndexSortedNormalizedShapelet) candidate).getOrder();
    }

    DoubleArray vector = a.size() >= b.size() ? a : b;
    for (int i = 0; i <= vector.size() - candidate.size(); i++) {
      double sumDistance = 0.0;
      Shapelet subShapelet = NormalizedShapelet.create(i, candidate.size(), vector);

      for (int k = 0; k < candidate.size(); k++) {
        if (order != null) {
          k = order[k];
        }
        double kv = candidate.get(k);
        double iv = subShapelet.get(k);

        sumDistance += distance(kv, iv);
        if (sumDistance > minDistance) {
          earlyStop = true;
          break;
        }
      }
      if (!earlyStop) {
        // System.out.println(i);
        minDistance = sumDistance;
      }

      earlyStop = false;
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
}
