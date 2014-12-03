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

import org.briljantframework.Utils;
import org.briljantframework.exception.MismatchException;
import org.briljantframework.exception.NonConformantException;
import org.briljantframework.matrix.slice.Range;
import org.briljantframework.matrix.slice.Slice;
import org.briljantframework.matrix.slice.Slicer;

import com.carrotsearch.hppc.DoubleArrayList;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 20/08/14.
 */
public abstract class AbstractRealMatrix implements RealMatrix {

  protected int rows;
  protected int cols;

  /**
   * Instantiates a new Abstract tensor.
   *
   * @param rows the rows
   * @param columns the columns
   */
  public AbstractRealMatrix(int rows, int columns) {
    this.cols = columns;
    this.rows = rows;
  }

  /**
   * Rows int.
   *
   * @return number or rows
   */
  public int rows() {
    return rows;
  }

  /**
   * Columns int.
   *
   * @return number of columns
   */
  public int columns() {
    return cols;
  }

  @Override
  public RealMatrix getRow(int i) {
    RealMatrix row = newEmptyMatrix(1, columns());
    for (int j = 0; j < columns(); j++) {
      row.put(0, j, get(i, j));
    }
    return row;
  }

  @Override
  public RealMatrix dropRow(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RealMatrix getRows(int start, int end) {
    return getRows(Range.exclusive(start, end));
  }

  @Override
  public RealMatrix getRows(Slicer slicer) {
    if (slicer.length() > this.rows()) {
      throw new MismatchException("slicer", "longer than number of rows");
    }
    Slice slice = slicer.getSlice();
    RealArrayMatrix m = new RealArrayMatrix(slicer.length(), this.columns());
    int newI = 0, rows = this.rows;
    while (slice.hasNext(rows)) {
      int i = slice.next();
      for (int j = 0; j < this.columns(); j++) {
        m.put(newI, j, get(i, j));
      }
      newI += 1;
    }
    return m;
  }

  public RealArrayMatrix getColumn(int index) {
    if (index > columns()) {
      throw new IllegalArgumentException("index > headers()");
    }
    double[] col = new double[this.rows()];
    for (int i = 0; i < this.rows(); i++) {
      col[i] = get(i, index);
    }

    return new RealArrayMatrix(rows(), 1, col);
  }

  @Override
  public RealMatrix getColumns(int start, int end) {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public RealArrayMatrix getColumns(Slicer slicer) {
    if (slicer.length() > this.columns()) {
      throw new MismatchException("column", "slice longer than number of columns");
    }
    RealArrayMatrix m = new RealArrayMatrix(this.rows(), slicer.length());
    Slice slice = slicer.getSlice();

    slice.rewind();
    for (int i = 0; i < this.rows(); i++) {
      int newJ = 0;
      while (slice.hasNext(this.columns())) {
        int j = slice.next();
        m.put(i, newJ++, get(i, j));
      }
      slice.rewind();
    }
    return m;
  }

  /**
   * Drop column.
   *
   * @param col the col
   * @return matrix matrix
   */
  public RealMatrix dropColumn(int col) {
    checkArgument(col > 0 && col < columns());

    RealArrayMatrix m = new RealArrayMatrix(rows(), columns() - 1);
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < m.columns(); j++) {
        if (j != col) {
          m.put(i, j - 1, this.get(i, j));
        }
      }
    }
    return m;
  }

  @Override
  public RealArrayMatrix slice(Slicer rows, Slicer cols) {
    if (rows.length() <= 0 || rows.length() > this.rows()) {
      throw new IllegalArgumentException("cannot slice more rows than there are rows");
    }

    if (cols.length() <= 0 || cols.length() > this.columns()) {
      throw new IllegalArgumentException("cannot slice more columns than there are colums");
    }

    RealArrayMatrix result = new RealArrayMatrix(rows.length(), cols.length());
    Slice colSlice = cols.getSlice();
    Slice rowSlice = rows.getSlice();

    int newI = 0;
    while (rowSlice.hasNext(this.rows())) {
      int i = rowSlice.next();
      int newJ = 0;
      while (colSlice.hasNext(this.columns())) {
        int j = colSlice.next();
        result.put(newI, newJ++, get(i, j));
      }
      newI++;
      colSlice.rewind();
    }
    return result;
  }

  /**
   * @return the transpose of this matrix
   */
  public RealMatrix transpose() {
    RealMatrix matrix = newEmptyMatrix(this.columns(), this.rows());
    for (int i = 0; i < this.rows(); i++) {
      for (int j = 0; j < this.columns(); j++) {
        matrix.put(j, i, get(i, j));
      }
    }
    return matrix;
  }

  /**
   * Multiply this matrix with other
   *
   * @param other matrix
   * @return a new matrix
   * @throws org.briljantframework.exception.NonConformantException
   */
  @Override
  public RealMatrix mmul(RealMatrix other) throws NonConformantException {
    return RealMatrices.mmul(this::newMatrix, this, other);
  }

  /**
   * @param diagonal the diagonal
   * @return the result
   */
  @Override
  public RealMatrix mmuld(RealDiagonal diagonal) {
    return RealMatrices.mdmul(this::newMatrix, this, diagonal);
  }

  @Override
  public RealMatrix mul(RealMatrix other) {
    Preconditions.checkArgument(hasCompatibleShape(other.getShape()));
    return muli(1, other, 1);
  }

  @Override
  public RealMatrix muli(double scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        put(i, j, get(i, j) * scalar);
      }
    }
    return this;
  }

  @Override
  public RealMatrix muli(RealMatrix other) {
    return muli(1.0, other, 1.0);
  }

  @Override
  public RealMatrix add(RealMatrix other) {
    return add(1, other, 1);
  }

  @Override
  public RealMatrix add(double scalar) {
    RealMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, get(i, j) + scalar);
      }
    }
    return matrix;
  }

  @Override
  public RealMatrix addi(RealMatrix other) {
    addi(1, other, 1);
    return this;
  }

  @Override
  public RealMatrix addi(double scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        this.put(i, j, get(i, j) + scalar);
      }
    }
    return this;
  }

  @Override
  public RealMatrix sub(RealMatrix other) {
    return sub(1, other, 1);
  }

  @Override
  public RealMatrix sub(double scalar) {
    return add(-scalar);
  }

  @Override
  public RealMatrix subi(RealMatrix other) {
    addi(1, other, -1);
    return this;
  }

  @Override
  public RealMatrix subi(double scalar) {
    addi(-scalar);
    return this;
  }

  @Override
  public RealMatrix rsub(double scalar) {
    RealMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, scalar - get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public RealMatrix rsubi(double scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        put(i, j, scalar - get(i, j));
      }
    }
    return this;
  }

  @Override
  public RealMatrix div(RealMatrix other) {
    checkArgument(this.hasEqualShape(other));
    RealMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, get(i, j) / other.get(i, j));
      }
    }
    return matrix;
  }

  /**
   * Multiply this matrix with a scalar
   *
   * @param scalar to multiply
   * @return a new matrix with the values multiplied
   */
  @Override
  public RealMatrix mul(double scalar) {
    return RealMatrices.mul(this::newMatrix, this, scalar);
  }

  @Override
  public RealMatrix divi(RealMatrix other) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        this.put(i, j, get(i, j) / other.get(i, j));
      }
    }
    return this;
  }

  @Override
  public RealMatrix divi(double other) {
    muli(1 / other);
    return this;
  }

  @Override
  public RealMatrix rdiv(double other) {
    return RealMatrices.div(this::newMatrix, other, this);
  }

  @Override
  public RealMatrix rdivi(double other) {
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
  public RealMatrix sub(double alpha, RealMatrix other, double beta) {
    if (!hasEqualShape(other)) {
      throw new NonConformantException(this, other);
    }
    RealMatrix matrix = newEmptyMatrix(rows(), columns());
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
  public RealMatrix subi(double alpha, RealMatrix other, double beta) {
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
  public RealMatrix add(double alpha, RealMatrix other, double beta) {
    if (!hasEqualShape(other)) {
      throw new NonConformantException(this, other);
    }
    RealMatrix matrix = newEmptyMatrix(rows(), columns());
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
  public RealMatrix addi(double alpha, RealMatrix other, double beta) {
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
  public RealMatrix mul(double alpha, RealMatrix other, double beta) {
    if (!hasEqualShape(other)) {
      throw new NonConformantException(this, other);
    }
    RealMatrix matrix = newEmptyMatrix(rows(), columns());
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
  public RealMatrix muli(double alpha, RealMatrix other, double beta) {
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

  /**
   * Multiply this matrix with <code>other</code> scaling <code>this</code> with <code>alpha</code>
   * and other with <code>beta</code>
   *
   * @param alpha scaling factor for this
   * @param other matrix
   * @param beta scaling factor for other
   * @return a new Matrix
   */
  @Override
  public RealMatrix mmul(double alpha, RealMatrix other, double beta) {
    if (this.columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }
    return RealMatrices.mmul(this::newMatrix, this, alpha, other, beta);
  }

  @Override
  public RealMatrix negate() {
    RealMatrix n = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        n.put(i, j, -get(i, j));
      }
    }
    return n;
  }

  /**
   * Less than.
   *
   * @param other the other
   * @return the boolean matrix
   */
  @Override
  public BooleanMatrix lessThan(RealMatrix other) {
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

  /**
   * Less than.
   *
   * @param value the value
   * @return the boolean matrix
   */
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

  /**
   * Less than equal.
   *
   * @param other the other
   * @return the boolean matrix
   */
  @Override
  public BooleanMatrix lessThanEqual(RealMatrix other) {
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
  public BooleanMatrix greaterThan(RealMatrix other) {
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
  public BooleanMatrix greaterThanEquals(RealMatrix other) {
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
  public BooleanMatrix equalsTo(RealMatrix other) {
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

  /**
   * Find vector.
   *
   * @param matrix the matrix
   * @return the vector
   */
  @Override
  public RealMatrix find(BooleanMatrix matrix) {
    checkArgument(hasCompatibleShape(matrix.getShape()));
    DoubleArrayList list = new DoubleArrayList();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        if (matrix.has(i, j)) {
          list.add(get(i, j));
        }
      }
    }
    return newMatrix(getShape(), list.toArray());
  }

  @Override
  public boolean equals(Object obj) {
    return false;
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
    StringBuilder out = new StringBuilder("DenseMatrix\n");
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

  protected abstract RealMatrix newMatrix(Shape shape, double[] array);

  protected abstract RealMatrix newEmptyMatrix(int rows, int columns);
}
