/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.array.base;

import org.briljantframework.array.Array;
import org.briljantframework.array.BaseArray;
import org.briljantframework.array.BitArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.LongArray;
import org.briljantframework.array.Range;
import org.briljantframework.array.api.ArrayFactory;
import org.apache.commons.math3.complex.Complex;

/**
 * @author Isak Karlsson
 */
public class BaseArrayFactory implements ArrayFactory {

  protected BaseArrayFactory() {

  }

  @Override
  public <T> Array<T> array(T[] data) {
    return new BaseReferenceArray<T>(this, data);
  }

  @Override
  public <T> Array<T> array(T[][] data) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IntArray array(int[][] data) {
    int rows = data.length;
    int columns = data[0].length;
    IntArray x = intArray(rows, columns);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        x.set(i, j, data[i][j]);
      }
    }
    return x;
  }

  @Override
  public IntArray array(int[] data) {
    return new BaseIntArray(this, true, data);
  }

  @Override
  public LongArray array(long[][] data) {
    return null;
  }

  @Override
  public LongArray array(long[] data) {
    return new BaseLongArray(this, data);
  }

  @Override
  public DoubleArray array(double[][] data) {
    int rows = data.length;
    int cols = data[0].length;
    DoubleArray x = doubleArray(rows, cols);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        x.set(i, j, data[i][j]);
      }
    }
    return x;
  }

  @Override
  public DoubleArray array(double[] data) {
    return new BaseDoubleArray(this, data);
  }

  @Override
  public <T extends BaseArray<T>> T diag(T data) {
    if (data.isVector()) {
      int n = data.size();
      T arr = data.newEmptyArray(n, n);
      arr.getDiagonal().assign(data);
      return arr;
    } else if (data.isMatrix()) {
      return data.getDiagonal();
    } else {
      throw new IllegalArgumentException("Input must be 1- or 2-d");
    }
  }

  @Override
  public ComplexArray array(Complex[][] data) {
    return null;
  }

  @Override
  public ComplexArray array(Complex[] data) {
    return new BaseComplexArray(this, data);
  }

  @Override
  public ComplexArray complexArray(double... data) {
    Complex[] c = new Complex[data.length];
    for (int i = 0; i < data.length; i++) {
      c[i] = Complex.valueOf(data[i]);
    }
    return array(c);
  }

  @Override
  public BitArray array(boolean[][] data) {
    return null;
  }

  @Override
  public BitArray array(boolean[] data) {
    return new BaseBitArray(this, data);
  }

  @Override
  public IntArray intArray(int... shape) {
    return new BaseIntArray(this, shape);
  }

  @Override
  public LongArray longArray(int... shape) {
    return new BaseLongArray(this, shape);
  }

  @Override
  public DoubleArray doubleArray(int... shape) {
    return new BaseDoubleArray(this, shape);
  }

  @Override
  public ComplexArray complexArray(int... shape) {
    return new BaseComplexArray(this, shape);
  }

  @Override
  public BitArray booleanArray(int... shape) {
    return new BaseBitArray(this, shape);
  }

  @Override
  public <T> Array<T> referenceArray(int... shape) {
    return new BaseReferenceArray<T>(this, shape);
  }

  @Override
  public DoubleArray ones(int... shape) {
    return doubleArray(shape).assign(1);
  }

  @Override
  public DoubleArray zero(int... shape) {
    return doubleArray(shape);
  }

  @Override
  public Range range(int start, int end, int step) {
    return new BaseRange(this, start, end, step);
  }

  @Override
  public Range range(int start, int end) {
    return range(start, end, 1);
  }

  @Override
  public Range range(int end) {
    return range(0, end);
  }

  @Override
  public Range range() {
    return range(0, -1, -1);
  }

  @Override
  public DoubleArray linspace(double start, double end, int size) {
    DoubleArray values = doubleArray(size);
    double step = (end - start) / (size - 1);
    double value = start;
    for (int index = 0; index < size; index++) {
      values.set(index, value);
      value += step;
    }
    return values;
  }

  @Override
  public DoubleArray eye(int size) {
    DoubleArray eye = doubleArray(size, size);
    eye.getDiagonal().assign(1);
    return eye;
  }
}
