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
public class Diagonal extends AbstractDenseMatrix implements MatrixLike {

  private final int size;

  private Diagonal(int rows, int cols, double[] values) {
    super(rows, cols, values);
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
  public static Diagonal empty(int rows, int cols) {
    return new Diagonal(rows, cols, new double[Math.max(rows, cols)]);
  }

  /**
   * Of diagonal.
   *
   * @param rows the rows
   * @param cols the cols
   * @param values the values
   * @return the diagonal
   */
  public static Diagonal of(int rows, int cols, double... values) {
    return new Diagonal(rows, cols, values);
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
  public Matrix mmul(Matrix x) {
    return Matrices.mdmul(DenseMatrix::new, x, this);
  }

  /**
   * Reshape matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the matrix
   */
  public Matrix reshape(int rows, int cols) {
    DenseMatrix ret = DenseMatrix.fromColumnOrder(this.rows(), this.columns(), asDoubleArray());
    ret.reshapei(rows, cols);
    return ret;
  }

  /**
   * Create a copy of this matrix. This contract stipulates that modifications of the copy does not
   * affect the original.
   *
   * @return the copy
   */
  public Diagonal copy() {
    double[] values = new double[this.values.length];
    System.arraycopy(this.values, 0, values, 0, this.values.length);
    return new Diagonal(this.rows(), this.columns(), values);
  }

  /**
   * Is square.
   *
   * @return true if rows() == columns()
   */
  public boolean isSquare() {
    return rows() == columns();
  }

  /**
   * The shape of the current matrix.
   *
   * @return the shape
   */
  public Shape getShape() {
    return Shape.of(rows(), columns());
  }

  /**
   * Returns true if {@link org.briljantframework.matrix.Shape#size()} == {@link #size()}
   *
   * @param shape the shape
   * @return the boolean
   */
  public boolean hasCompatibleShape(Shape shape) {
    return hasCompatibleShape(shape.rows, shape.columns);
  }

  /**
   * Has compatible shape.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the boolean
   * @throws ArithmeticException
   */
  public boolean hasCompatibleShape(int rows, int cols) {
    return Math.multiplyExact(rows, cols) == rows() * columns();
  }

  /**
   * Equal shape (i.e.
   *
   * @param other the other
   * @return the boolean
   */
  public boolean hasEqualShape(MatrixLike other) {
    return rows() == other.rows() && columns() == other.columns();
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
  public Diagonal map(DoubleUnaryOperator operator) {
    double[] diagonal = new double[this.size];
    for (int i = 0; i < diagonal.length; i++) {
      diagonal[i] = operator.applyAsDouble(get(i));
    }
    return new Diagonal(this.rows(), this.columns(), diagonal);
  }

  /**
   * Multiply diagonal.
   *
   * @param scalar the scalar
   * @return the diagonal
   */
  public Diagonal mul(double scalar) {
    double[] out = new double[size];
    for (int i = 0; i < size; i++) {
      out[i] = values[i] * scalar;
    }

    return new Diagonal(this.rows(), this.columns(), out);
  }

  /**
   * Transpose matrix like.
   *
   * @return the matrix like
   */
  public Diagonal transpose() {
    double[] values = new double[this.values.length];
    System.arraycopy(this.values, 0, values, 0, values.length);
    return new Diagonal(this.columns(), this.rows(), values);
  }

  public Diagonal transposei() {
    int tmp = rows;
    rows = cols;
    cols = tmp;

    return this;
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
}
