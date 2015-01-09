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

import org.briljantframework.exceptions.NonConformantException;

import com.google.common.base.Preconditions;

/**
 * Implementation of a sparse diagonal matrix
 * <p>
 * Created by isak on 27/06/14.
 */
public class Diagonal extends AbstractDoubleMatrix {

  private final int size;
  private final double[] values;

  private Diagonal(int rows, int cols, double[] values) {
    super(rows, cols);
    this.values = values;
    this.size = values.length;
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

  @Deprecated
  public void apply(DoubleUnaryOperator operator) {
    mapi(operator);
  }

  /**
   * Set value at row i and column j to value
   *
   * @param i row
   * @param j column
   * @param value value
   */
  public void set(int i, int j, double value) {
    if (i == j) {
      set(i, value);
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
  public void set(int index, double value) {
    if (index > size && index < 0) {
      throw new IllegalArgumentException("index out of bounds");
    } else {
      values[index] = value;
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
   * Transpose matrix like.
   *
   * @return the matrix like
   */
  public Diagonal transpose() {
    double[] values = new double[this.values.length];
    return new Diagonal(this.columns(), this.rows(), values);
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
   * @param other a square matrix with x.rows = d.size
   * @return a matrix
   */
  @Override
  public DoubleMatrix mmul(DoubleMatrix other) {
    if (this.columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }

    DoubleMatrix mat = new ArrayDoubleMatrix(this.rows(), other.columns());
    int rows = this.rows(), columns = other.columns();
    for (int row = 0; row < rows; row++) {
      if (row < other.rows()) {
        for (int column = 0; column < columns; column++) {
          mat.set(row, column, other.get(row, column) * this.get(row));
        }
      } else {
        break;
      }
    }

    return mat;
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

  @Override
  public Diagonal reshape(int rows, int columns) {
    Preconditions.checkArgument(rows * columns == size(),
        "Total size of new matrix must be unchanged.");
    return new Diagonal(rows, columns, values);
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

  @Override
  public boolean isArrayBased() {
    return false;
  }

  @Override
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayDoubleMatrix(rows, columns);
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
}
