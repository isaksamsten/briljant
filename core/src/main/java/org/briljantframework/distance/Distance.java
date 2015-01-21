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

package org.briljantframework.distance;

import org.briljantframework.vector.Vector;

/**
 * In mathematics, a metric or distance function is a function that defines a distance between
 * elements of a set. A set with a metric is called a metric space. A metric induces a topology on a
 * set but not all topologies can be generated by a metric. A topological space whose topology can
 * be described by a metric is called metrizable. A metric on a set X is a function (called the
 * distance function or simply distance)
 * <p>
 * d : X × X → R
 * <p>
 * (where R is the set of real numbers). For all x, y, z in X, this function is required to satisfy
 * the following conditions:
 * <p>
 * 
 * <pre>
 *        d(x, y) ≥ 0                     (non-negativity, or separation axiom)
 *        d(x, y) = 0                     if and only if   x = y     (identity of indiscernibles, or coincidence axiom)
 *        d(x, y) = d(y, x)               (symmetry)
 *        d(x, z) ≤ d(x, y) + d(y, z)     (subadditivity / triangle inequality).
 * </pre>
 * <p>
 * Users of this class should not assume that all conditions are fulfilled. In some cases only the
 * relative order is important.
 * <p>
 * Created by Isak Karlsson on 01/09/14.
 */
public interface Distance {

  /**
   * Calculate the distance between two scalar
   *
   * @param a scalar
   * @param b scalar
   * @return distance double
   */
  double distance(double a, double b);

  /**
   * Compute the distance between two vectors
   *
   * @param a a vector
   * @param b a vector
   * @return the distance
   */
  double distance(Vector a, Vector b);

  /**
   * Max double.
   *
   * @return the maximum possible distance
   */
  double max();

  /**
   * Min double.
   *
   * @return the minimum possible distance
   */
  double min();
}
