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
import org.briljantframework.vector.VectorLike;

import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 20/08/14.
 */
public abstract class AbstractMatrix implements Matrix {

  protected static final String ARG_DIFF_SIZE = "Arguments imply different size.";
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
  public Matrix assign(VectorLike vector, Axis axis) {
    return assign(vector, (a, b) -> b, axis);
  }

  @Override
  public Matrix assign(VectorLike other, DoubleBinaryOperator operator, Axis axis) {
    /*
     * Due to cache-locality, put(i, ) is for most (at least array based) matrices a _big_ win.
     * Therefore, the straightforward implementation using two for-loops is not used below.
     * This is a big win since this.size() >= other.size().
     */
    Matrix mat = newEmptyMatrix(rows(), columns());
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        mat.put(i, operator.applyAsDouble(get(i), other.get(i % rows())));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        mat.put(i, operator.applyAsDouble(get(i), other.get(i / rows())));
      }
    }
    return mat;
  }

  @Override
  public Matrix assign(Matrix matrix) {
    return assign(matrix, DoubleUnaryOperator.identity());
  }

  @Override
  public Matrix assign(Matrix matrix, DoubleUnaryOperator operator) {
    assertEqualSize(matrix);
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
  public Matrix map(DoubleUnaryOperator operator) {
    Matrix mat = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.put(i, operator.applyAsDouble(get(i)));
    }
    return mat;
  }

  @Override
  public double reduce(double identity, DoubleBinaryOperator reduce, DoubleUnaryOperator map) {
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

  public Matrix transpose() {
    Matrix matrix = newEmptyMatrix(this.columns(), this.rows());
    for (int i = 0; i < size(); i++) {
      matrix.put(i, get(i));
    }
    return matrix;
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
  public Matrix mmul(double alpha, Matrix other, double beta) {
    if (columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }

    Matrix result = newEmptyMatrix(rows(), other.columns());
    for (int row = 0; row < rows(); row++) {
      for (int col = 0; col < other.columns(); col++) {
        double sum = 0.0;
        for (int k = 0; k < columns(); k++) {
          sum += get(row, k) * other.get(k, col);
        }
        result.put(row, col, sum);
      }
    }
    return result;
  }

  @Override
  public Matrix mul(Matrix other) {
    return mul(1, other, 1);
  }

  @Override
  public Matrix mul(double alpha, Matrix other, double beta) {
    return copy().muli(alpha, other, beta);
  }

  @Override
  public Matrix mul(VectorLike other, Axis axis) {
    return mul(1, other, 1, axis);
  }

  @Override
  public Matrix mul(double alpha, VectorLike other, double beta, Axis axis) {
    return copy().muli(alpha, other, beta, axis);
  }

  @Override
  public Matrix mul(double scalar) {
    return copy().muli(scalar);
  }

  @Override
  public Matrix muli(Matrix other) {
    return muli(1.0, other, 1.0);
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
  public Matrix muli(double alpha, Matrix other, double beta) {
    assertEqualSize(other);
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        put(i, j, alpha * get(i, j) * other.get(i, j) * beta);
      }
    }
    return this;
  }

  @Override
  public Matrix muli(VectorLike other, Axis axis) {
    return muli(1, other, 1, axis);
  }

  @Override
  public Matrix muli(double alpha, VectorLike other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.put(i, (alpha * get(i)) * (other.get(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.put(i, (alpha * get(i)) * (other.get(i / rows()) * beta));
      }
    }
    return this;
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
  public Matrix add(VectorLike other, Axis axis) {
    return add(1, other, 1, axis);
  }

  @Override
  public Matrix add(double alpha, VectorLike other, double beta, Axis axis) {
    return copy().addi(alpha, other, beta, axis);
  }

  @Override
  public Matrix add(double alpha, Matrix other, double beta) {
    assertEqualSize(other);
    Matrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, alpha * get(i, j) + other.get(i, j) * beta);
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
  public Matrix addi(VectorLike other, Axis axis) {
    return addi(1, other, 1, axis);
  }

  @Override
  public Matrix addi(double alpha, VectorLike other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.put(i, (alpha * get(i)) + (other.get(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.put(i, (alpha * get(i)) + (other.get(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public Matrix addi(double alpha, Matrix other, double beta) {
    assertEqualSize(other);
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        put(i, j, alpha * get(i, j) + other.get(i, j) * beta);
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
  public Matrix sub(VectorLike other, Axis axis) {
    return sub(1, other, 1, axis);
  }

  @Override
  public Matrix sub(double alpha, VectorLike other, double beta, Axis axis) {
    return copy().subi(alpha, other, beta, axis);
  }

  @Override
  public Matrix sub(double alpha, Matrix other, double beta) {
    assertEqualSize(other);
    Matrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, alpha * get(i, j) - other.get(i, j) * beta);
      }
    }
    return matrix;
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
  public Matrix subi(VectorLike other, Axis axis) {
    return subi(1, other, 1, axis);
  }

  @Override
  public Matrix subi(double alpha, VectorLike other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.put(i, (alpha * get(i)) - (other.get(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.put(i, (alpha * get(i)) - (other.get(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public Matrix subi(double alpha, Matrix other, double beta) {
    addi(alpha, other, -1 * beta);
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
  public Matrix rsub(VectorLike other, Axis axis) {
    return rsub(1, other, 1, axis);
  }

  @Override
  public Matrix rsub(double alpha, VectorLike other, double beta, Axis axis) {
    return copy().rsubi(alpha, other, beta, axis);
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
  public Matrix rsubi(VectorLike other, Axis axis) {
    return rsubi(1, other, 1, axis);
  }

  @Override
  public Matrix rsubi(double alpha, VectorLike other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.put(i, (other.get(i % rows()) * beta) - (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.put(i, (other.get(i / rows()) * beta) - (alpha * get(i)));
      }
    }
    return this;
  }

  @Override
  public Matrix div(Matrix other) {
    assertEqualSize(other);
    Matrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, get(i, j) / other.get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public Matrix div(double other) {
    return mul(1.0 / other);
  }

  @Override
  public Matrix div(VectorLike other, Axis axis) {
    return div(1, other, 1, axis);
  }

  @Override
  public Matrix div(double alpha, VectorLike other, double beta, Axis axis) {
    return copy().divi(alpha, other, beta, axis);
  }

  @Override
  public Matrix divi(Matrix other) {
    assertEqualSize(other);
    for (int i = 0; i < size(); i++) {
      put(i, get(i) / other.get(i));
    }
    return this;
  }

  @Override
  public Matrix divi(double other) {
    return muli(1 / other);
  }

  @Override
  public Matrix divi(VectorLike other, Axis axis) {
    return divi(1, other, 1, axis);
  }

  @Override
  public Matrix divi(double alpha, VectorLike other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.put(i, (alpha * get(i)) / (other.get(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.put(i, (alpha * get(i)) / (other.get(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public Matrix rdiv(double other) {
    Matrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.put(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public Matrix rdiv(VectorLike other, Axis axis) {
    return rdiv(1, other, 1, axis);
  }

  @Override
  public Matrix rdiv(double alpha, VectorLike other, double beta, Axis axis) {
    return copy().rdivi(alpha, other, beta, axis);
  }

  @Override
  public Matrix rdivi(double other) {
    for (int i = 0; i < size(); i++) {
      put(i, other / get(i));
    }
    return this;
  }

  @Override
  public Matrix rdivi(VectorLike other, Axis axis) {
    return rdivi(1, other, 1, axis);
  }

  @Override
  public Matrix rdivi(double alpha, VectorLike other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows());
      for (int i = 0; i < size(); i++) {
        this.put(i, (other.get(i % rows()) * beta) / (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns());
      for (int i = 0; i < size(); i++) {
        this.put(i, (other.get(i / rows()) * beta) / (alpha * get(i)));
      }
    }
    return this;
  }

  @Override
  public Matrix negate() {
    Matrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.put(i, -get(i));
    }
    return n;
  }

  @Override
  public BooleanMatrix lessThan(Matrix other) {
    assertEqualSize(other);
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < other.size(); i++) {
      bm.put(i, get(i) < other.get(i));
    }

    return bm;
  }

  @Override
  public BooleanMatrix lessThan(double value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.put(i, get(i) < value);
    }
    return bm;
  }

  @Override
  public BooleanMatrix lessThanEqual(Matrix other) {
    assertEqualSize(other);

    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < other.rows(); i++) {
      bm.put(i, get(i) <= other.get(i));
    }
    return bm;
  }

  @Override
  public BooleanMatrix lessThanEqual(double value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.put(i, get(i) <= value);
    }
    return bm;
  }

  @Override
  public BooleanMatrix greaterThan(Matrix other) {
    assertEqualSize(other);
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < other.rows(); i++) {
      bm.put(i, get(i) > other.get(i));
    }
    return bm;
  }

  @Override
  public BooleanMatrix greaterThan(double value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < rows(); i++) {
      bm.put(i, get(i) > value);
    }
    return bm;
  }

  @Override
  public BooleanMatrix greaterThanEquals(Matrix other) {
    assertEqualSize(other);
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < other.rows(); i++) {
      bm.put(i, get(i) >= other.get(i));
    }

    return bm;
  }

  @Override
  public BooleanMatrix greaterThanEquals(double value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < rows(); i++) {
      bm.put(i, get(i) >= value);
    }
    return bm;
  }

  @Override
  public BooleanMatrix equalsTo(Matrix other) {
    assertEqualSize(other);

    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < other.rows(); i++) {
      bm.put(i, get(i) == other.get(i));
    }

    return bm;
  }

  @Override
  public BooleanMatrix equalsTo(double value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < rows(); i++) {
      bm.put(i, get(i) == value);
    }
    return bm;
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
  public double[] asDoubleArray() {
    double[] array = new double[size()];
    for (int i = 0; i < size(); i++) {
      array[i] = get(i);
    }
    return array;
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

  protected void assertEqualSize(Matrix other) {
    if (this.rows() != other.rows() || this.columns() != other.columns()) {
      throw new IllegalArgumentException(String.format(
          "nonconformant arguments (op1 is %s, op2 is %s)", this.getShape(), other.getShape()));

    }
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
