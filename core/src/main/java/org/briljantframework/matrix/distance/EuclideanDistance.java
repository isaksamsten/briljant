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

package org.briljantframework.matrix.distance;


import org.briljantframework.matrix.MatrixLike;

/**
 * EuclideanDistance between two Points (either Vectors or scalars)
 * <p>
 * Created by Isak Karlsson on 01/09/14.
 */
public class EuclideanDistance implements Distance {

  /**
   * The constant INSTANCE.
   */
  public static final EuclideanDistance INSTANCE = new EuclideanDistance();

  private EuclideanDistance() {

  }

  @Override
  public double distance(double a, double b) {
    double r = a - b;
    return r * r;
  }

  @Override
  public double distance(MatrixLike a, MatrixLike b) {
    int size = Math.min(a.size(), b.size());

    double residual = 0.0;
    for (int i = 0; i < size; i++) {
      residual += distance(a.get(i), b.get(i));
    }

    return Math.sqrt(residual);
  }

  @Override
  public double max() {
    return Double.POSITIVE_INFINITY;
  }

  @Override
  public double min() {
    return 0;
  }

  @Override
  public String toString() {
    return "EuclideanDistance";
  }
}
