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

package org.briljantframework.matrix;

import java.util.function.DoubleUnaryOperator;

import org.briljantframework.Utils;

import com.google.common.collect.ImmutableTable;

/**
 * Implementation of a sparse diagonal matrix
 * <p>
 * Created by isak on 27/06/14.
 */
public class RealDiagonal extends AbstractRealMatrix implements RealMatrixLike {

  private final int size;
  private final double[] values;

  private RealDiagonal(int rows, int cols, double[] values) {
    super(rows, cols);
    this.values = values;
    this.size = values.length;
    this.rows = rows;
  }

  /**
   * Empty diagonal.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the diagonal
   */
  public static RealDiagonal empty(int rows, int cols) {
    return new RealDiagonal(rows, cols, new double[Math.max(rows, cols)]);
  }

  /**
   * Of diagonal.
   *
   * @param rows the rows
   * @param cols the cols
   * @param values the values
   * @return the diagonal
   */
  public static RealDiagonal of(int rows, int cols, double... values) {
    return new RealDiagonal(rows, cols, values);
  }

  public void apply(DoubleUnaryOperator operator) {
    for (int i = 0; i < values.length; i++) {
      values[i] = operator.applyAsDouble(values[i]);
    }
  }

  /**
   * Reshape matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the matrix
   */
  public RealMatrix reshape(int rows, int cols) {
    RealArrayMatrix ret =
        RealArrayMatrix.fromColumnOrder(this.rows(), this.columns(), asDoubleArray());
    ret.reshapei(rows, cols);
    return ret;
  }

  /**
   * Create a copy of this matrix. This contract stipulates that modifications of the copy does not
   * affect the original.
   *
   * @return the copy
   */
  public RealDiagonal copy() {
    double[] values = new double[this.values.length];
    System.arraycopy(this.values, 0, values, 0, this.values.length);
    return new RealDiagonal(this.rows(), this.columns(), values);
  }

  /**
   * Set value at row i and column j to value
   *
   * @param i row
   * @param j column
   * @param value value
   */
  public void put(int i, int j, double value) {
    if (i == j) {
      put(i, value);
    } else {
      throw new IllegalStateException("Illegal to touch non-diagonal entries");
    }
  }

  /**
   * Puts <code>value</code> at the linearized position <code>index</code>.
   *
   * @param index the index
   * @param value the value
   * @see #get(int)
   */
  public void put(int index, double value) {
    if (index > size && index < 0) {
      throw new IllegalArgumentException("index out of bounds");
    } else {
      values[index] = value;
    }
  }

  /**
   * Raw view of the column-major underlying array. In some instances it might be possible to mutate
   * this (e.g., if the implementation provides a direct reference. However, there are nos such
   * guarantees).
   *
   * @return the underlying array. Touch with caution.
   */
  public double[] asDoubleArray() {
    int rows = rows(), cols = columns();
    double[] dense = new double[rows * cols];
    int n = Math.min(cols, rows);
    for (int j = 0; j < n; j++) {
      dense[j * rows + j] = values[j];
    }
    return dense;
  }

  /**
   * Get double.
   *
   * @param i the i
   * @param j the j
   * @return double
   */
  public double get(int i, int j) {
    if (i == j) {
      return get(i);
    } else {
      return 0;
    }
  }

  /**
   * Get double.
   *
   * @param index the index
   * @return the double
   */
  public double get(int index) {
    if (index > size && index < 0) {
      throw new IllegalArgumentException("index > size");
    } else {
      return index < values.length ? values[index] : 0;
    }
  }

  /**
   * Size int.
   *
   * @return the int
   */
  public int size() {
    return size;
  }

  /**
   * Map diagonal.
   *
   * @param operator the operator
   * @return the diagonal
   */
  public RealDiagonal map(DoubleUnaryOperator operator) {
    double[] diagonal = new double[this.size];
    for (int i = 0; i < diagonal.length; i++) {
      diagonal[i] = operator.applyAsDouble(get(i));
    }
    return new RealDiagonal(this.rows(), this.columns(), diagonal);
  }

  /**
   * Transpose matrix like.
   *
   * @return the matrix like
   */
  public RealDiagonal transpose() {
    double[] values = new double[this.values.length];
    System.arraycopy(this.values, 0, values, 0, values.length);
    return new RealDiagonal(this.columns(), this.rows(), values);
  }

  /**
   * Multiplying a square symmetric diagonal matrix (i.e. a vector of diagonal entries) d and X,
   * storing the result in Y
   * <p>
   * 
   * <pre>
   * Y &lt; -dX
   * </pre>
   *
   * @param x a square matrix with x.rows = d.size
   * @return a matrix
   */
  public RealMatrix mmul(RealMatrix x) {
    return RealMatrices.mdmul(RealArrayMatrix::new, x, this);
  }

  /**
   * Multiply diagonal.
   *
   * @param scalar the scalar
   * @return the diagonal
   */
  public RealDiagonal mul(double scalar) {
    double[] out = new double[size];
    for (int i = 0; i < size; i++) {
      out[i] = values[i] * scalar;
    }

    return new RealDiagonal(this.rows(), this.columns(), out);
  }

  @Override
  public String toString() {
    ImmutableTable.Builder<Integer, Integer, String> builder = ImmutableTable.builder();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        if (get(i, j) < 0) {
          builder.put(i, j, String.format("%1.4f", get(i, j)));
        } else {
          builder.put(i, j, String.format(" %1.4f", get(i, j)));
        }
      }
    }
    StringBuilder out = new StringBuilder("Diagonal\n");
    Utils.prettyPrintTable(out, builder.build(), 0, 2, false, false);
    out.append("Shape: ").append(getShape());
    return out.toString();
  }

  @Override
  protected RealMatrix newMatrix(Shape shape, double[] array) {
    return new RealArrayMatrix(shape, array);
  }

  @Override
  protected RealMatrix newEmptyMatrix(int rows, int columns) {
    return new RealArrayMatrix(rows, columns);
  }

  public RealDiagonal transposei() {
    int tmp = rows;
    rows = cols;
    cols = tmp;

    return this;
  }
}
