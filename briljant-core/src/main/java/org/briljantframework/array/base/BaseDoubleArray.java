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

package org.briljantframework.array.base;

import org.briljantframework.array.AbstractDoubleArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
class BaseDoubleArray extends AbstractDoubleArray {

  private double[] data;

  BaseDoubleArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
    this.data = new double[size()];
  }

  BaseDoubleArray(ArrayFactory bj, double[] data) {
    super(bj, new int[]{data.length});
    this.data = data;
  }

  private BaseDoubleArray(ArrayFactory bj, int offset,
                          int[] shape,
                          int[] stride,
                          int majorStride,
                          double[] data) {
    super(bj, offset, shape, stride, majorStride);
    this.data = data;
  }

  @Override
  public DoubleArray asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new BaseDoubleArray(getArrayFactory(), offset, shape, stride, majorStride, data);
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public DoubleArray newEmptyArray(int... shape) {
    return new BaseDoubleArray(getArrayFactory(), shape);
  }

  @Override
  protected double getElement(int i) {
    return data[i];
  }

  @Override
  protected void setElement(int i, double value) {
    data[i] = value;
  }

  @Override
  public double[] data() {
    return data;
  }
}
