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

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.exceptions.SizeMismatchException;
import org.briljantframework.matrix.AbstractDoubleMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Diagonal;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.LongMatrix;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.storage.DoubleStorage;
import org.briljantframework.matrix.storage.Storage;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.ToDoubleFunction;

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

  @Override
  public Diagonal assign(double value) {
    for (int i = 0; i < diagonalSize(); i++) {
      setDiagonal(i, value);
    }
    return this;
  }

  @Override
  public Diagonal assign(double[] array) {
    Check.size(array.length, this);
    for (int i = 0; i < diagonalSize(); i++) {
      setDiagonal(i, array[i]);
    }
    return this;
  }

  @Override
  public Diagonal assign(DoubleMatrix o) {
    if (o.isVector()) {
      if (o.size() < diagonalSize()) {
        throw new SizeMismatchException(diagonalSize(), o.size());
      }
      for (int i = 0; i < diagonalSize(); i++) {
        setDiagonal(i, o.get(i));
      }
    } else {
      Check.equalShape(this, o);
      for (int i = 0; i < rows(); i++) {
        setDiagonal(i, o.get(i, i));
      }
    }
    return this;

  }

  @Override
  public Diagonal assign(DoubleSupplier supplier) {
    for (int i = 0; i < diagonalSize(); i++) {
      setDiagonal(i, supplier.getAsDouble());
    }
    return this;
  }

  @Override
  public Diagonal assign(DoubleMatrix matrix, DoubleUnaryOperator operator) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Diagonal assign(DoubleMatrix matrix, DoubleBinaryOperator combine) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Diagonal assign(IntMatrix matrix, IntToDoubleFunction function) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DoubleMatrix assign(LongMatrix matrix, LongToDoubleFunction function) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Diagonal assign(ComplexMatrix matrix, ToDoubleFunction<? super Complex> function) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Diagonal update(DoubleUnaryOperator operator) {
    for (int i = 0; i < diagonalSize(); i++) {
      setDiagonal(i, operator.applyAsDouble(getDiagonal(i)));
    }
    return this;
  }

  public void set(int i, int j, double value) {
    if (i == j) {
      values.setDouble(i, value);
    } else {
      throw new IllegalStateException("Can't to touch non-diagonal entries");
    }
  }

  public void set(int index, double value) {
    int row = index % rows();
    int col = index / rows();
    set(row, col, value);
  }

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

  public double get(int index) {
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

  public BaseDiagonal map(DoubleUnaryOperator operator) {
    double[] diagonal = new double[this.values.size()];
    for (int i = 0; i < diagonal.length; i++) {
      diagonal[i] = operator.applyAsDouble(getDiagonal(i));
    }
    return new BaseDiagonal(getMatrixFactory(), diagonal, this.rows(), this.columns());
  }

  public Diagonal transpose() {
    return new BaseDiagonal(getMatrixFactory(),
                            values.doubleArray().clone(),
                            this.columns(),
                            this.rows());
  }

  public Diagonal copy() {
    return new BaseDiagonal(getMatrixFactory(),
                            values.doubleArray().clone(),
                            this.rows(),
                            this.columns());
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
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
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
