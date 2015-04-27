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

import org.briljantframework.vector.Vector;

/**
 * A z-normalized sub sequence view of another MatrixLike
 * <p>
 * Created by Isak Karlsson on 28/09/14.
 */
public class NormalizedShapelet extends Shapelet {

  private final double sigma;
  private final double mean;

  public NormalizedShapelet(int start, int length, Vector vector) {
    super(start, length, vector);
    double ex = 0;
    double ex2 = 0;
    int size = start + length;
    for (int i = start; i < size; i++) {
      double v = vector.getAsDouble(i);
      ex += v;
      ex2 += v * v;
    }
    this.mean = ex / length;
    if (length == 1) {
      this.sigma = 0;
    } else {
      this.sigma = Math.sqrt(ex2 / length - mean * mean);
    }
  }

  /**
   * Create normalized shapelet.
   *
   * @param start      the start
   * @param length     the length
   * @param vectorLike the vector like
   * @return the normalized shapelet
   */
  public static NormalizedShapelet create(int start, int length, Vector vectorLike) {
    return new NormalizedShapelet(start, length, vectorLike);
  }

  @Override
  public double getAsDouble(int index) {
    if (sigma == 0) {
      return 0;
    } else {
      return (super.getAsDouble(index) - mean) / sigma;
    }
  }
}
