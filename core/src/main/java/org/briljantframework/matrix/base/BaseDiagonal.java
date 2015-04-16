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

import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.AbstractDoubleMatrix;
import org.briljantframework.matrix.Diagonal;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.storage.DoubleStorage;
import org.briljantframework.matrix.storage.Storage;
import org.briljantframework.matrix.api.MatrixFactory;

import java.util.function.DoubleUnaryOperator;

/**
 * Implementation of a sparse diagonal matrix
 * <p>
 * Created by isak on 27/06/14.
 */
class BaseDiagonal extends AbstractDoubleMatrix implements Diagonal {

  private final Storage values;

  public BaseDiagonal(MatrixFactory bj, double[] values, int rows, int cols) {
    super(bj, rows, cols);
    this.values = new DoubleStorage(values);
  }

  public BaseDiagonal(MatrixFactory bj, Storage values, int rows, int columns) {
    super(bj, rows, columns);
    this.values = values;
  }

  @Deprecated
  public void apply(DoubleUnaryOperator operator) {
    update(operator);
  }

  /**
   * Set value at row i and column j to value
   *
   * @param i     row
   * @param j     column
   * @param value value
   */
  public void set(int i, int j, double value) {
    if (i == j) {
      values.setDouble(i, value);
    } else {
      throw new IllegalStateException("Can't to touch non-diagonal entries");
    }
  }

  /**
   * Puts <code>value</code> at the linearized position <code>index</code>.
   *
   * @param index the index
   * @param value the value
   * @see org.briljantframework.matrix.DoubleMatrix#get(int)
   */
  public void set(int index, double value) {
    int row = index % rows();
    int col = index / rows();
    set(row, col, value);
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
      return values.getDouble(i);
    } else {
      if (i > rows() || j > columns()) {
        throw new IndexOutOfBoundsException();
      }
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
//    if (index > size && index < 0) {
//      throw new IllegalArgumentException("index > size");
//    } else {
//      return index < values.length ? values[index] : 0;
//    }
    int col = index / rows();
    int row = index % rows();
    return get(row, col);
  }

  /**
   * Returns the number of diagonal entries. Equal to {@code Math.min(rows(), columns())}.
   *
   * @returns the diagonal size
   */
  @Override
  public int diagonalSize() {
    return values.size();
  }

  /**
   * Returns the diagonal element at {@code i, i}
   *
   * @param i the index
   * @return the value
   */
  public double getDiagonal(int i) {
    return get(i, i);
  }

  public void setDiagonal(int i, double value) {
    set(i, i, value);
  }

  /**
   * Map diagonal.
   *
   * @param operator the operator
   * @return the diagonal
   */
  public BaseDiagonal map(DoubleUnaryOperator operator) {
    double[] diagonal = new double[this.values.size()];
    for (int i = 0; i < diagonal.length; i++) {
      diagonal[i] = operator.applyAsDouble(getDiagonal(i));
    }
    return new BaseDiagonal(getMatrixFactory(), diagonal, this.rows(), this.columns());
  }

  /**
   * Transpose matrix like.
   *
   * @return the matrix like
   */
  public BaseDiagonal transpose() {
    return new BaseDiagonal(getMatrixFactory(), getStorage().copy(), this.columns(), this.rows());
  }

  /**
   * Create a copy of this matrix. This contract stipulates that modifications of the copy does not
   * affect the original.
   *
   * @return the copy
   */
  public BaseDiagonal copy() {
    return new BaseDiagonal(getMatrixFactory(), getStorage().copy(), this.rows(), this.columns());
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

    DoubleMatrix mat = bj.doubleMatrix(this.rows(), other.columns());
    long rows = this.rows(), columns = other.columns();
    for (int row = 0; row < rows; row++) {
      if (row < other.rows()) {
        for (int column = 0; column < columns; column++) {
          mat.set(row, column, other.get(row, column) * this.getDiagonal(row));
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
  @Override
  public BaseDiagonal mul(double scalar) {
    double[] out = new double[values.size()];
    for (int i = 0; i < values.size(); i++) {
      out[i] = values.getDouble(i) * scalar;
    }

    return new BaseDiagonal(getMatrixFactory(), out, this.rows(), this.columns());
  }

  @Override
  public BaseDiagonal reshape(int rows, int columns) {
    return new BaseDiagonal(getMatrixFactory(), values, rows, columns);
  }

  @Override
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return bj.doubleMatrix(rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public Storage getStorage() {
    return values;
  }
}
