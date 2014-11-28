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

package org.briljantframework.learning.time;

import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.MatrixLike;

/**
 * A z-normalized sub sequence view of another MatrixLike
 * <p>
 * Created by Isak Karlsson on 28/09/14.
 */
public class NormalizedShapelet extends Shapelet {

  private final double sigma;
  private final double mean;

  public NormalizedShapelet(int start, int length, MatrixLike vector) {
    super(start, length, vector);
    Shapelet shapelet = Shapelet.create(start, length, vector);
    this.mean = Matrices.mean(shapelet);
    if (length == 1) {
      this.sigma = 0.0;
    } else {
      this.sigma = Matrices.std(shapelet, mean);
    }
  }

  /**
   * Create normalized shapelet.
   *
   * @param start the start
   * @param length the length
   * @param vectorLike the vector like
   * @return the normalized shapelet
   */
  public static NormalizedShapelet create(int start, int length, MatrixLike vectorLike) {
    return new NormalizedShapelet(start, length, vectorLike);
  }

  @Override
  public double get(int index) {
    if (sigma == 0) {
      return 0;
    } else {
      return (super.get(index) - mean) / sigma;
    }
  }
}
