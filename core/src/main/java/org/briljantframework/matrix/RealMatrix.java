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

import org.briljantframework.matrix.slice.Slicer;

/**
 * 
 * Created by Isak Karlsson on 28/08/14.
 */
public interface RealMatrix extends RealMatrixLike, Iterable<Double> {

  /**
   * Get vector at row
   *
   * @param i row
   * @return a vector
   */
  RealMatrix getRow(int i);

  /**
   *
   * @param i
   * @return
   */
  RealMatrix dropRow(int i);

  /**
   * Get rows from <code>start</code> to <code>end</code>
   *
   * @param start index
   * @param end index
   * @return a new matrix containing rows [start, end]
   */
  RealMatrix getRows(int start, int end);

  /**
   * Gets rows.
   *
   * @param slicer the slicer
   * @return rows rows
   */
  RealMatrix getRows(Slicer slicer);

  /**
   * Gets column.
   *
   * @param index the index
   * @return the column
   */
  RealMatrix getColumn(int index);

  /**
   * Gets columns.
   *
   * @param start the start
   * @param end the end
   * @return columns columns
   */
  RealMatrix getColumns(int start, int end);

  /**
   * Gets columns.
   *
   * @param slicer the slicer
   * @return columns columns
   */
  RealMatrix getColumns(Slicer slicer);

  RealMatrix dropColumn(int index);

  /**
   * Slice matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return matrix matrix
   */
  RealMatrix slice(Slicer rows, Slicer cols);

  /**
   * Create a copy of this matrix. This contract stipulates that modifications of the copy does not
   * affect the original.
   *
   * @return the copy
   */
  RealMatrix copy();

  /**
   * Transpose matrix like.
   *
   * @return the matrix like
   */
  RealMatrix transpose();

  // Arithmetical operations ///////////

  /**
   * Multiply r.
   *
   * @param other the other
   * @return r r
   */
  RealMatrix mmul(RealMatrix other);

  /**
   * Multiply by diagonal.
   *
   * @param diagonal the diagonal
   * @return matrix matrix
   */
  RealMatrix mmuld(RealDiagonal diagonal);

  /**
   * @param other
   * @return
   */
  RealMatrix mul(RealMatrix other);

  /**
   * Multiply inplace.
   *
   * @param scalar the scalar
   * @return this (modified)
   */
  RealMatrix muli(double scalar);

  /**
   * Elementwise multiply.
   *
   * @param other the other
   * @return r r
   */
  RealMatrix muli(RealMatrix other);

  /**
   * Add r.
   *
   * @param other the other
   * @return r r
   */
  RealMatrix add(RealMatrix other);

  /**
   * Add r.
   *
   * @param scalar the scalar
   * @return r r
   */
  RealMatrix add(double scalar);

  /**
   * Add inplace.
   *
   * @param other the other
   * @return r r
   */
  RealMatrix addi(RealMatrix other);

  /**
   * Add inplace.
   *
   * @param scalar the scalar
   * @return r r
   */
  RealMatrix addi(double scalar);

  /**
   * this - other
   *
   * @param other the other
   * @return r r
   */
  RealMatrix sub(RealMatrix other);

  /**
   * this - other
   *
   * @param scalar the scalar
   * @return r r
   */
  RealMatrix sub(double scalar);

  /**
   * this <- this - other
   *
   * @param other the other
   * @return r r
   */
  RealMatrix subi(RealMatrix other);

  /**
   * this <- this - other
   *
   * @param scalar the scalar
   * @return r r
   */
  RealMatrix subi(double scalar);

  /**
   * other - this
   *
   * @param scalar the scalar
   * @return r r
   */
  RealMatrix rsub(double scalar);


  /**
   * this <- other - this
   *
   * @param scalar the scalar
   * @return r r
   */
  RealMatrix rsubi(double scalar);

  /**
   * this / other
   *
   * @param other the other
   * @return r r
   */
  RealMatrix div(RealMatrix other);

  /**
   * this / other
   *
   * @param other the scalar
   * @return r r
   */
  default RealMatrix div(double other) {
    return mul(1.0 / other);
  }

  /**
   * Multiply r.
   *
   * @param scalar the scalar
   * @return r r
   */
  RealMatrix mul(double scalar);

  /**
   * this <- this / other
   *
   * @param other the other
   * @return r r
   */
  RealMatrix divi(RealMatrix other);

  /**
   * this <- this / other
   *
   * @param other the scalar
   * @return r r
   */
  RealMatrix divi(double other);

  /**
   * other / this
   *
   * @param other the denominator
   * @return the r
   */
  RealMatrix rdiv(double other);

  /**
   * this <- other / this
   *
   * @param other the nominator
   * @return the r
   */
  RealMatrix rdivi(double other);

  RealMatrix sub(double alpha, RealMatrix other, double beta);

  RealMatrix subi(double alpha, RealMatrix other, double beta);

  RealMatrix add(double alpha, RealMatrix other, double beta);

  RealMatrix addi(double alpha, RealMatrix other, double beta);

  RealMatrix mul(double alpha, RealMatrix other, double beta);

  RealMatrix muli(double alpha, RealMatrix other, double beta);

  RealMatrix mmul(double alpha, RealMatrix other, double beta);

  /**
   * new matrix with elements negated
   *
   * @return the r
   */
  RealMatrix negate();

  /**
   * Set value at row i and column j to value
   *
   * @param i row
   * @param j column
   * @param value value
   */
  void put(int i, int j, double value);

  /**
   * Puts <code>value</code> at the linearized position <code>index</code>.
   *
   * @param index the index
   * @param value the value
   * @see #get(int)
   */
  void put(int index, double value);

  BooleanMatrix lessThan(RealMatrix other);

  BooleanMatrix lessThan(double value);

  BooleanMatrix lessThanEqual(RealMatrix other);

  BooleanMatrix lessThanEqual(double value);

  BooleanMatrix greaterThan(RealMatrix other);

  BooleanMatrix greaterThan(double value);

  BooleanMatrix greaterThanEquals(RealMatrix other);

  BooleanMatrix greaterThanEquals(double value);

  BooleanMatrix equalsTo(RealMatrix other);

  BooleanMatrix equalsTo(double value);

  RealMatrix find(BooleanMatrix matrix);

  /**
   * Raw view of the column-major underlying array. In some instances it might be possible to mutate
   * this (e.g., if the implementation provides a direct reference. However, there are no such
   * guarantees).
   *
   * @return the underlying array. Touch with caution.
   */
  double[] asDoubleArray();

  /**
   * Created by Isak Karlsson on 04/09/14.
   *
   * @param <T> the type parameter
   */
  @FunctionalInterface
  interface Copy<T extends RealMatrix> {

    /**
     * Copy the tensor while retaining the shape
     *
     * @param matrix a tensorLike
     * @return a copy of tensorLike
     */
    default T copyMatrix(RealMatrixLike matrix) {
      return copyMatrix(matrix.getShape(), matrix);
    }

    /**
     * Copy the tensor and perhaps change the shape
     *
     * @param shape the new shape
     * @param matrix the matrix
     * @return a copy of matrix
     */
    T copyMatrix(Shape shape, RealMatrixLike matrix);
  }

  /**
   * Created by Isak Karlsson on 03/09/14.
   *
   * @param <T> the type parameter
   */
  @FunctionalInterface
  interface New<T extends RealMatrix> {

    /**
     * New tensor.
     *
     * @param rows the rows
     * @param cols the cols
     * @return the t
     */
    default T newMatrix(int rows, int cols) {
      return newMatrix(Shape.of(rows, cols));
    }

    /**
     * New tensor.
     *
     * @param shape the shape
     * @return the t
     */
    default T newMatrix(Shape shape) {
      return newMatrix(shape, shape.getArrayOfShape());
    }

    /**
     * Construct a new tensor with the same shape
     *
     * @param tensor the tensor
     * @param values the values
     * @return t t
     */
    T newMatrix(Shape tensor, double[] values);

    /**
     * New matrix.
     *
     * @param rows the rows
     * @param cols the cols
     * @param array the array
     * @return the t
     */
    default T newMatrix(int rows, int cols, double[] array) {
      Shape shape = Shape.of(rows, cols);
      checkArgument(shape.size() == array.length, "shape and value array does not match");
      return newMatrix(shape, array);
    }

    /**
     * New vector.
     *
     * @param size the size
     * @param array the array
     * @return the t
     */
    default T newVector(int size, double[] array) {
      Shape shape = Shape.of(size, 1);
      checkArgument(shape.size() == array.length, "shape and value array does not match");
      return newMatrix(shape, array);
    }
  }
}
