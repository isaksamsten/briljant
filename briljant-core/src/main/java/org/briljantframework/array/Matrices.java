/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
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
package org.briljantframework.array;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.AbstractFieldMatrix;
import org.apache.commons.math3.linear.AbstractRealMatrix;
import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DefaultFieldMatrixPreservingVisitor;
import org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.briljantframework.Check;

/**
 * Utilities for handling matrices.
 * 
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class Matrices {

  public static final String CAN_ONLY_VIEW_2D_ARRAYS = "Can only view 2d-arrays.";
  public static final String CAN_ONLY_VIEW_1D_ARRAYS = "Can only view 1d-arrays.";
  private static final String REQUIRE_2D = "Require a 2d-array.";

  private Matrices() {}

  /**
   * Convert the field matrix to an array.
   * 
   * @param matrix the matrix
   * @return a new array
   */
  public static ComplexArray toArray(FieldMatrix<Complex> matrix) {
    ComplexArray array =
        Arrays.complexArray(matrix.getRowDimension(), matrix.getColumnDimension());
    matrix.walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor<Complex>(Complex.ZERO) {
      @Override
      public void visit(int row, int column, Complex value) {
        array.set(row, column, value);
      }
    });
    return array;
  }

  /**
   * Convert the real matrix to a double array.
   * 
   * @param matrix the matrix
   * @return a new array
   */
  public static DoubleArray toArray(RealMatrix matrix) {
    DoubleArray array =
        Arrays.doubleArray(matrix.getRowDimension(), matrix.getColumnDimension());
    matrix.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
      @Override
      public void visit(int row, int column, double value) {
        array.set(row, column, value);
      }
    });
    return array;
  }

  /**
   * Create a new field vector from the given array. If the array is conti
   *
   * @param array a new field vector
   * @param <T> the type of array
   * @return a
   */
  public static <T extends FieldElement<T>> FieldVector<T> asFieldVector(Array<T> array) {
    if (array.isView() || !array.isContiguous()) {
      array = array.copy();
    }
    return new ArrayFieldVector<>(array.data(), false);
  }

  /**
   * View the array as a {@link FieldMatrix}.
   *
   * @param array the array
   * @param <T> the field
   * @return a field matrix view
   */
  public static <T extends FieldElement<T>> FieldMatrix<T> asFieldMatrix(Array<T> array) {
    Check.argument(array.isMatrix(), CAN_ONLY_VIEW_2D_ARRAYS);
    return new AbstractFieldMatrix<T>() {
      @Override
      public FieldMatrix<T> createMatrix(int rowDimension, int columnDimension)
          throws NotStrictlyPositiveException {
        return asFieldMatrix(array.newEmptyArray(rowDimension, columnDimension));
      }

      @Override
      public FieldMatrix<T> copy() {
        return asFieldMatrix(array.copy());
      }

      @Override
      public T getEntry(int row, int column) throws OutOfRangeException {
        return array.get(row, column);
      }

      @Override
      public void setEntry(int row, int column, T value) throws OutOfRangeException {
        array.set(row, column, value);
      }

      @Override
      public void addToEntry(int row, int column, T increment) throws OutOfRangeException {
        array.set(row, column, array.get(row, column).add(increment));
      }

      @Override
      public void multiplyEntry(int row, int column, T factor) throws OutOfRangeException {
        array.set(row, column, array.get(row, column).multiply(factor));
      }

      @Override
      public int getRowDimension() {
        return array.rows();
      }

      @Override
      public int getColumnDimension() {
        return array.columns();
      }
    };
  }

  /**
   * View the complex array as a {@link FieldMatrix}.
   *
   * @param array the array
   * @return a field matrix view
   */
  public static FieldMatrix<Complex> asFieldMatrix(ComplexArray array) {
    return asFieldMatrix(array.boxed());
  }

  /**
   * View the double array as a {@link RealMatrix}.
   *
   * @param array the array (must be 2d)
   * @return a real matrix view
   */
  public static RealMatrix asRealMatrix(DoubleArray array) {
    Check.argument(array.isMatrix(), CAN_ONLY_VIEW_2D_ARRAYS);
    return new AbstractRealMatrix() {
      @Override
      public int getRowDimension() {
        return array.rows();
      }

      @Override
      public int getColumnDimension() {
        return array.columns();
      }

      @Override
      public RealMatrix createMatrix(int rowDimension, int columnDimension)
          throws NotStrictlyPositiveException {
        return asRealMatrix(array.newEmptyArray(rowDimension, columnDimension));
      }

      @Override
      public RealMatrix copy() {
        return asRealMatrix(array.copy());
      }

      @Override
      public double getEntry(int row, int column) throws OutOfRangeException {
        return array.get(row, column);
      }

      @Override
      public void setEntry(int row, int column, double value) throws OutOfRangeException {
        array.set(row, column, value);
      }
    };
  }

  /**
   * View the double array as a {@link RealVector}.
   *
   * @param array the array
   * @return a real vector view
   */
  public static RealVector asRealVector(DoubleArray array) {
    Check.argument(array.isVector(), CAN_ONLY_VIEW_1D_ARRAYS);
    return new RealVector() {
      @Override
      public int getDimension() {
        return array.size();
      }

      @Override
      public double getEntry(int index) throws OutOfRangeException {
        return array.get(index);
      }

      @Override
      public void setEntry(int index, double value) throws OutOfRangeException {
        array.set(index, value);
      }

      @Override
      public RealVector append(RealVector v) {
        ArrayRealVector vector = new ArrayRealVector(v.getDimension() + array.size());
        copyFromArray(vector);
        for (int i = 0; i < v.getDimension(); i++) {
          vector.setEntry(i, v.getEntry(i));
        }
        return vector;
      }

      private void copyFromArray(ArrayRealVector vector) {
        for (int i = 0; i < array.size(); i++) {
          vector.setEntry(i, array.get(i));
        }
      }

      @Override
      public RealVector append(double d) {
        ArrayRealVector vector = new ArrayRealVector(array.size() + 1);
        copyFromArray(vector);
        vector.setEntry(array.size(), d);
        return vector;
      }

      @Override
      public RealVector getSubVector(int index, int n) throws NotPositiveException,
          OutOfRangeException {
        return asRealVector(array.get(Range.of(index, index + n)));
      }

      @Override
      public void setSubVector(int index, RealVector v) throws OutOfRangeException {
        for (int i = 0; i < array.size(); i++) {
          array.set(i + index, v.getEntry(i));
        }
      }

      @Override
      public boolean isNaN() {
        return Arrays.any(array, Double::isNaN);
      }

      @Override
      public boolean isInfinite() {
        return Arrays.any(array, Double::isInfinite);
      }

      @Override
      public RealVector copy() {
        return asRealVector(array.copy());
      }

      @Override
      @Deprecated
      public RealVector ebeDivide(RealVector v) throws DimensionMismatchException {
        if (v.getDimension() != array.size()) {
          throw new DimensionMismatchException(v.getDimension(), array.size());
        }
        RealVector a = new ArrayRealVector(array.size());
        for (int i = 0; i < array.size(); i++) {
          a.setEntry(i, array.get(i) / v.getEntry(i));
        }
        return a;
      }

      @Override
      @Deprecated
      public RealVector ebeMultiply(RealVector v) throws DimensionMismatchException {
        if (v.getDimension() != array.size()) {
          throw new DimensionMismatchException(v.getDimension(), array.size());
        }
        RealVector a = new ArrayRealVector(array.size());
        for (int i = 0; i < array.size(); i++) {
          a.setEntry(i, array.get(i) * v.getEntry(i));
        }
        return a;
      }
    };
  }

  /**
   * Repeats copies of a matrix.
   *
   * <p/>
   * Example
   *
   * @param x the matrix
   * @param n the number of rows and columns to repeat
   * @param <T> the type of array
   * @return a new array
   */
  public static <T extends BaseArray<T>> T repmat(T x, int n) {
    return repmat(x, n, n);
  }

  public static <T extends BaseArray<T>> T repmat(T x, int r, int c) {
    Check.argument(x.isMatrix(), REQUIRE_2D);
    final int m = x.rows();
    final int n = x.columns();
    T y = x.newEmptyArray(m * r, n * c);
    for (int cc = 0; cc < c; cc++) {
      for (int j = 0; j < n; j++) {
        int jj = j + (cc * n);
        for (int rc = 0; rc < r; rc++) {
          for (int i = 0; i < m; i++) {
            y.set(i + (rc * m), jj, x, i, j);
          }
        }
      }
    }
    return y;
  }
}
