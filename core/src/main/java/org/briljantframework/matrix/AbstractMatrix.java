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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ToDoubleFunction;

import org.briljantframework.Utils;
import org.briljantframework.exception.NonConformantException;

import com.github.fommil.netlib.BLAS;
import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 20/08/14.
 */
public abstract class AbstractMatrix implements Matrix {

  protected static final BLAS blas = BLAS.getInstance();
  protected int rows;
  protected int cols;

  public AbstractMatrix(int rows, int columns) {
    this.cols = columns;
    this.rows = rows;
  }

  @Override
  public Matrix assign(double value) {
    for (int i = 0; i < size(); i++) {
      put(i, value);
    }
    return this;
  }

  @Override
  public Matrix assign(Matrix matrix) {
    return assign(matrix, DoubleUnaryOperator.identity());
  }

  @Override
  public Matrix assign(Matrix matrix, DoubleUnaryOperator operator) {
    checkArgument(hasEqualShape(matrix), "");
    for (int i = 0; i < size(); i++) {
      put(i, operator.applyAsDouble(matrix.get(i)));
    }
    return this;
  }

  @Override
  public Matrix assign(double[] values) {
    checkArgument(size() == values.length);
    for (int i = 0; i < size(); i++) {
      put(i, values[i]);
    }
    return this;
  }

  @Override
  public double mapReduce(double identity, DoubleBinaryOperator reduce, DoubleUnaryOperator map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.applyAsDouble(identity, map.applyAsDouble(get(i)));
    }
    return identity;
  }

  @Override
  public Matrix reduceColumns(ToDoubleFunction<? super Matrix> reduce) {
    Matrix mat = newEmptyMatrix(1, columns());
    for (int i = 0; i < columns(); i++) {
      mat.put(i, reduce.applyAsDouble(getColumnView(i)));
    }
    return mat;
  }

  @Override
  public Matrix reduceRows(ToDoubleFunction<? super Matrix> reduce) {
    Matrix mat = newEmptyMatrix(rows(), 1);
    for (int i = 0; i < rows(); i++) {
      mat.put(i, reduce.applyAsDouble(getRowView(i)));
    }
    return mat;
  }

  @Override
  public Matrix getRowView(int i) {
    return new MatrixView(this, i, 0, 1, columns());
  }

  public Matrix getColumnView(int index) {
    return new MatrixView(this, 0, index, rows(), 1);
  }

  @Override
  public Diagonal getDiagonalView() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Matrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new MatrixView(this, rowOffset, colOffset, rows, columns);
  }

  @Override
  public Matrix getColumns(int start, int end) {
    throw new UnsupportedOperationException("not implemented");
  }

  public Matrix transpose() {
    Matrix matrix = newEmptyMatrix(this.columns(), this.rows());
    for (int i = 0; i < this.rows(); i++) {
      for (int j = 0; j < this.columns(); j++) {
        matrix.put(j, i, get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public int rows() {
    return rows;
  }

  @Override
  public int columns() {
    return cols;
  }

  @Override
  public Matrix mmul(Matrix other) throws NonConformantException {
    return mmul(1, other, 1);
  }

  @Override
  public Matrix mmul(Diagonal diagonal) {
    if (columns() != diagonal.rows()) {
      throw new NonConformantException(this, diagonal);
    }
    Matrix matrix = newEmptyMatrix(this.rows(), diagonal.columns());
    int rows = this.rows(), columns = diagonal.columns();
    for (int column = 0; column < columns; column++) {
      if (column < this.columns()) {
        for (int row = 0; row < rows; row++) {
          double xv = this.get(row, column);
          double dv = diagonal.get(column);
          matrix.put(row, column, xv * dv);
        }
      } else {
        break;
      }
    }
    return matrix;
  }

  @Override
  public Matrix mul(Matrix other) {
    checkArgument(hasCompatibleShape(other.getShape()));
    return muli(1, other, 1);
  }

  @Override
  public Matrix mul(double scalar) {
    Matrix mat = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.put(i, get(i) * scalar);
    }

    return mat;
  }

  @Override
  public Matrix muli(double scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        put(i, j, get(i, j) * scalar);
      }
    }
    return this;
  }

  @Override
  public Matrix muli(Matrix other) {
    return muli(1.0, other, 1.0);
  }

  @Override
  public Matrix add(Matrix other) {
    return add(1, other, 1);
  }

  @Override
  public Matrix add(double scalar) {
    Matrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, get(i, j) + scalar);
      }
    }
    return matrix;
  }

  @Override
  public Matrix addi(Matrix other) {
    addi(1, other, 1);
    return this;
  }

  @Override
  public Matrix addi(double scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        this.put(i, j, get(i, j) + scalar);
      }
    }
    return this;
  }

  @Override
  public Matrix sub(Matrix other) {
    return sub(1, other, 1);
  }

  @Override
  public Matrix sub(double scalar) {
    return add(-scalar);
  }

  @Override
  public Matrix subi(Matrix other) {
    addi(1, other, -1);
    return this;
  }

  @Override
  public Matrix subi(double scalar) {
    addi(-scalar);
    return this;
  }

  @Override
  public Matrix rsub(double scalar) {
    Matrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, scalar - get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public Matrix rsubi(double scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        put(i, j, scalar - get(i, j));
      }
    }
    return this;
  }

  @Override
  public Matrix div(Matrix other) {
    checkArgument(this.hasEqualShape(other));
    Matrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, get(i, j) / other.get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public Matrix divi(Matrix other) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        this.put(i, j, get(i, j) / other.get(i, j));
      }
    }
    return this;
  }

  @Override
  public Matrix divi(double other) {
    muli(1 / other);
    return this;
  }

  @Override
  public Matrix rdiv(double other) {
    Matrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, other / get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public Matrix rdivi(double other) {
    throw new UnsupportedOperationException();
  }

  /**
   * Subtract dense matrix.
   *
   * @param alpha the alpha
   * @param other the other
   * @param beta the beta
   * @return dense matrix
   */
  @Override
  public Matrix sub(double alpha, Matrix other, double beta) {
    if (!hasEqualShape(other)) {
      throw new NonConformantException(this, other);
    }
    Matrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, alpha * get(i, j) - other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  /**
   * Subtract inplace.
   *
   * @param alpha the alpha
   * @param other the other
   * @param beta the beta
   * @return the matrix
   */
  @Override
  public Matrix subi(double alpha, Matrix other, double beta) {
    addi(alpha, other, -1 * beta);
    return this;
  }

  /**
   * Add dense matrix.
   *
   * @param alpha the alpha
   * @param other the other
   * @param beta the beta
   * @return the dense matrix
   */
  @Override
  public Matrix add(double alpha, Matrix other, double beta) {
    if (!hasEqualShape(other)) {
      throw new NonConformantException(this, other);
    }
    Matrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  /**
   * Add inplace.
   *
   * @param alpha the alpha
   * @param other the other
   * @param beta the beta
   * @return the dense matrix
   */
  @Override
  public Matrix addi(double alpha, Matrix other, double beta) {
    if (!hasEqualShape(other)) {
      throw new NonConformantException(this, other);
    }
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        put(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return this;
  }

  /**
   * Elementwise multiply.
   *
   * @param alpha the alpha
   * @param other the other
   * @param beta the beta
   * @return dense matrix
   */
  @Override
  public Matrix mul(double alpha, Matrix other, double beta) {
    if (!hasEqualShape(other)) {
      throw new NonConformantException(this, other);
    }
    Matrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, alpha * get(i, j) * other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  /**
   * Elementwise multiply.
   *
   * @param alpha the alpha
   * @param other the other
   * @param beta the beta
   * @return dense matrix
   */
  @Override
  public Matrix muli(double alpha, Matrix other, double beta) {
    if (!hasEqualShape(other)) {
      throw new NonConformantException(this, other);
    }
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        put(i, j, alpha * get(i, j) * other.get(i, j) * beta);
      }
    }
    return this;
  }

  @Override
  public Matrix negate() {
    Matrix n = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        n.put(i, j, -get(i, j));
      }
    }
    return n;
  }

  @Override
  public BooleanMatrix lessThan(Matrix other) {
    checkArgument(hasCompatibleShape(other.getShape()), "can't compare a %s matrix to a %s matrix",
        getShape(), other.getShape());

    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < other.rows(); i++) {
      for (int j = 0; j < other.columns(); j++) {
        bm.put(i, j, get(i, j) < other.get(i, j));
      }
    }

    return bm;
  }

  @Override
  public BooleanMatrix lessThan(double value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        bm.put(i, j, get(i, j) < value);
      }
    }
    return bm;
  }

  @Override
  public BooleanMatrix lessThanEqual(Matrix other) {
    checkArgument(hasCompatibleShape(other.getShape()), "can't compare a %s matrix to a %s matrix",
        getShape(), other.getShape());

    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < other.rows(); i++) {
      for (int j = 0; j < other.columns(); j++) {
        bm.put(i, j, get(i, j) <= other.get(i, j));
      }
    }

    return bm;
  }

  /**
   * Less than equal.
   *
   * @param value the value
   * @return the boolean matrix
   */
  @Override
  public BooleanMatrix lessThanEqual(double value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        bm.put(i, j, get(i, j) <= value);
      }
    }
    return bm;
  }

  /**
   * Greater than.
   *
   * @param other the other
   * @return the boolean matrix
   */
  @Override
  public BooleanMatrix greaterThan(Matrix other) {
    checkArgument(hasCompatibleShape(other.getShape()), "can't compare a %s matrix to a %s matrix",
        getShape(), other.getShape());

    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < other.rows(); i++) {
      for (int j = 0; j < other.columns(); j++) {
        bm.put(i, j, get(i, j) > other.get(i, j));
      }
    }

    return bm;
  }

  /**
   * Greater than.
   *
   * @param value the value
   * @return the boolean matrix
   */
  @Override
  public BooleanMatrix greaterThan(double value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        bm.put(i, j, get(i, j) > value);
      }
    }
    return bm;
  }

  /**
   * Greater than equal.
   *
   * @param other the other
   * @return the boolean matrix
   */
  @Override
  public BooleanMatrix greaterThanEquals(Matrix other) {
    checkArgument(hasCompatibleShape(other.getShape()), "can't compare a %s matrix to a %s matrix",
        getShape(), other.getShape());

    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < other.rows(); i++) {
      for (int j = 0; j < other.columns(); j++) {
        bm.put(i, j, get(i, j) >= other.get(i, j));
      }
    }

    return bm;
  }

  /**
   * Greater than equals.
   *
   * @param value the value
   * @return the boolean matrix
   */
  @Override
  public BooleanMatrix greaterThanEquals(double value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        bm.put(i, j, get(i, j) >= value);
      }
    }
    return bm;
  }

  /**
   * Equal to.
   *
   * @param other the other
   * @return the boolean matrix
   */
  @Override
  public BooleanMatrix equalsTo(Matrix other) {
    checkArgument(hasCompatibleShape(other.getShape()), "can't compare a %s matrix to a %s matrix",
        getShape(), other.getShape());

    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < other.rows(); i++) {
      for (int j = 0; j < other.columns(); j++) {
        bm.put(i, j, get(i, j) == other.get(i, j));
      }
    }

    return bm;
  }

  /**
   * Equals to.
   *
   * @param value the value
   * @return the boolean matrix
   */
  @Override
  public BooleanMatrix equalsTo(double value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        bm.put(i, j, get(i, j) == value);
      }
    }
    return bm;
  }

  @Override
  public int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      long bits = Double.doubleToLongBits(get(i));
      result = 31 * result + (int) (bits ^ (bits >>> 32));
    }

    return Objects.hash(rows, cols, result);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Matrix) {
      Matrix mat = (Matrix) obj;
      if (!mat.hasEqualShape(this)) {
        return false;
      }
      for (int i = 0; i < size(); i++) {
        if (get(i) != mat.get(i)) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
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
    StringBuilder out = new StringBuilder();
    Utils.prettyPrintTable(out, builder.build(), 0, 2, false, false);
    out.append("Shape: ").append(getShape());
    return out.toString();
  }

  @Override
  public Iterator<Double> iterator() {
    return new Iterator<Double>() {
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < size();
      }

      @Override
      public Double next() {
        return get(index++);
      }
    };
  }
}
