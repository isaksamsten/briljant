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

package org.briljantframework.matrix.base;

import org.briljantframework.matrix.AbstractDoubleArray;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.matrix.api.ArrayFactory;

/**
 * Implementation of {@link org.briljantframework.matrix.DoubleArray} using a single {@code
 * double}
 * array. Indexing is
 * calculated in column-major order, hence varying column faster than row is preferred when
 * iterating.
 *
 * @author Isak Karlsson
 */
class BaseDoubleArray extends AbstractDoubleArray {

  private double[] values;

  BaseDoubleArray(ArrayFactory bj, int... shape) {
    super(bj, shape);
    this.values = new double[size()];
  }

  BaseDoubleArray(ArrayFactory bj, double[] values) {
    super(bj, values.length);
    this.values = values;
  }

  private BaseDoubleArray(ArrayFactory bj, int offset,
                          int[] shape,
                          int[] stride,
                          double[] values) {
    super(bj, offset, shape, stride);
    this.values = values;
  }

  @Override
  protected DoubleArray makeView(int offset, int[] shape, int[] stride) {
    return new BaseDoubleArray(getMatrixFactory(), offset, shape, stride, values);
  }

  @Override
  public DoubleArray newEmptyArray(int... shape) {
    return new BaseDoubleArray(getMatrixFactory(), shape);
  }

  @Override
  protected double getElement(int i) {
    return values[i];
  }

  @Override
  protected void setElement(int i, double value) {
    values[i] = value;
  }
}
