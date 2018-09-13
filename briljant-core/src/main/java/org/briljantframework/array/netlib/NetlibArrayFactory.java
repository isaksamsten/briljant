/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array.netlib;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.briljantframework.Check;
import org.briljantframework.array.*;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
class NetlibArrayFactory implements ArrayFactory {
  private static final ThreadLocal<RealDistribution> normalDistribution =
      ThreadLocal.withInitial(() -> new NormalDistribution(0, 1));

  private static final ThreadLocal<RealDistribution> uniformDistribution =
      ThreadLocal.withInitial(() -> new UniformRealDistribution(0, 1));

  private final ArrayBackend backend;

  NetlibArrayFactory(ArrayBackend backend) {
    this.backend = backend;
  }

  @Override
  public <T> Array<T> newVector(T[] data) {
    return new NetlibArray<>(backend, data);
  }

  @Override
  public <T> Array<T> newMatrix(T[][] data) {
    Check.argument(data.length > 0, "illegal row count");
    Check.argument(data[0].length > 0, "illegal column count");

    int m = data.length;
    int n = data[0].length;
    Array<T> array = newArray(m, n);
    for (int i = 0; i < m; i++) {
      T[] row = data[i];
      Check.argument(row.length == n, "illegal row count");
      for (int j = 0; j < n; j++) {
        array.set(i, j, row[j]);
      }
    }
    return array;
  }

  @Override
  public <T> Array<T> newArray(int... shape) {
    return new NetlibArray<>(backend, shape);
  }

  @Override
  public BooleanArray newBooleanMatrix(boolean[][] data) {
    Check.argument(data.length > 0, "illegal row count");
    Check.argument(data[0].length > 0, "illegal column count");

    int m = data.length;
    int n = data[0].length;
    BooleanArray array = newBooleanArray(m, n);
    for (int i = 0; i < m; i++) {
      boolean[] row = data[i];
      Check.argument(row.length == n, "illegal row count");
      for (int j = 0; j < n; j++) {
        array.set(i, j, row[j]);
      }
    }
    return array;
  }

  @Override
  public BooleanArray newBooleanVector(boolean[] data) {
    return new NetlibBooleanArray(backend, data);
  }

  @Override
  public BooleanArray newBooleanArray(int... shape) {
    return new NetlibBooleanArray(backend, shape);
  }

  @Override
  public IntArray newIntMatrix(int[][] data) {
    Check.argument(data.length > 0, "illegal row count");
    Check.argument(data[0].length > 0, "illegal column count");

    int m = data.length;
    int n = data[0].length;
    IntArray array = newIntArray(m, n);
    for (int i = 0; i < m; i++) {
      int[] row = data[i];
      Check.argument(row.length == n, "illegal row count");
      for (int j = 0; j < n; j++) {
        array.set(i, j, row[j]);
      }
    }
    return array;
  }

  @Override
  public IntArray newIntVector(int[] data) {
    return new NetlibIntArray(backend, true, data);
  }

  @Override
  public IntArray newIntArray(int... shape) {
    return new NetlibIntArray(backend, shape);
  }

  @Override
  public LongArray newLongMatrix(long[][] data) {
    Check.argument(data.length > 0, "illegal row count");
    Check.argument(data[0].length > 0, "illegal column count");

    int m = data.length;
    int n = data[0].length;
    LongArray array = newLongArray(m, n);
    for (int i = 0; i < m; i++) {
      long[] row = data[i];
      Check.argument(row.length == n, "illegal row count");
      for (int j = 0; j < n; j++) {
        array.set(i, j, row[j]);
      }
    }
    return array;
  }

  @Override
  public LongArray newLongVector(long[] data) {
    return new NetlibLongArray(backend, data);
  }

  @Override
  public LongArray newLongArray(int... shape) {
    return new NetlibLongArray(backend, shape);
  }

  @Override
  public DoubleArray newDoubleMatrix(double[][] data) {
    Check.argument(data.length > 0, "illegal row count");
    Check.argument(data[0].length > 0, "illegal column count");

    int m = data.length;
    int n = data[0].length;
    DoubleArray array = newDoubleArray(m, n);
    for (int i = 0; i < m; i++) {
      double[] row = data[i];
      Check.argument(row.length == n, "illegal row count");
      for (int j = 0; j < n; j++) {
        array.set(i, j, row[j]);
      }
    }
    return array;
  }

  @Override
  public ComplexArray newComplexMatrix(Complex[][] data) {
    Check.argument(data.length > 0, "illegal row count");
    Check.argument(data[0].length > 0, "illegal column count");

    int m = data.length;
    int n = data[0].length;
    ComplexArray array = newComplexArray(m, n);
    for (int i = 0; i < m; i++) {
      Complex[] row = data[i];
      Check.argument(row.length == n, "illegal row count");
      for (int j = 0; j < n; j++) {
        array.set(i, j, row[j]);
      }
    }
    return array;
  }

  @Override
  public ComplexArray newComplexVector(Complex[] data) {
    return new NetlibComplexArray(backend, data);
  }

  public ComplexArray newComplexVector(double... data) {
    Complex[] c = new Complex[data.length];
    for (int i = 0; i < data.length; i++) {
      c[i] = Complex.valueOf(data[i]);
    }
    return newComplexVector(c);
  }

  @Override
  public ComplexArray newComplexArray(int... shape) {
    return new NetlibComplexArray(backend, shape);
  }

  @Override
  public DoubleArray randn(int size) {
    RealDistribution distribution = normalDistribution.get();
    DoubleArray array = newDoubleArray(size);
    for (int i = 0; i < size; i++) {
      array.set(i, distribution.sample());
    }
    return array;
  }

  @Override
  public DoubleArray rand(int size) {
    RealDistribution distribution = uniformDistribution.get();
    DoubleArray array = newDoubleArray(size);
    for (int i = 0; i < size; i++) {
      array.set(i, distribution.sample());
    }
    return array;
  }

  @Override
  public DoubleArray ones(int... shape) {
    DoubleArray array = newDoubleArray(shape);
    array.assign(1);
    return array;
  }

  @Override
  public Range range(int start, int end, int step) {
    return new NetlibIntRange(backend, start, end, step);
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
  public DoubleArray linspace(double start, double end, int size) {
    DoubleArray values = newDoubleArray(size);
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
    DoubleArray eye = newDoubleArray(size, size);
    eye.getDiagonal().assign(1);
    return eye;
  }

  @Override
  public DoubleArray newDoubleVector(double[] data) {
    return new NetlibDoubleArray(backend, data);
  }

  @Override
  public DoubleArray newDoubleArray(int... shape) {
    return new NetlibDoubleArray(backend, shape);
  }
}
