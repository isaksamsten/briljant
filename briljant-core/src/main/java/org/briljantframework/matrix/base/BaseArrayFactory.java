package org.briljantframework.matrix.base;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.BitArray;
import org.briljantframework.matrix.ComplexArray;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.matrix.IntArray;
import org.briljantframework.matrix.LongArray;
import org.briljantframework.matrix.Range;
import org.briljantframework.matrix.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
public class BaseArrayFactory implements ArrayFactory {

  protected BaseArrayFactory() {

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
    return new BaseIntArray(this, data);
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
  public DoubleArray diag(DoubleArray data) {
    if (data.isVector()) {
      int n = data.size();
      DoubleArray arr = doubleArray(n, n);
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
