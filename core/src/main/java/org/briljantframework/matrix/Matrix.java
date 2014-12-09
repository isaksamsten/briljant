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

import java.util.function.*;

/**
 * 
 * Created by Isak Karlsson on 28/08/14.
 */
public interface Matrix extends VectorLike, Iterable<Double> {

  /**
   * Assign {@code value} to {@code this}
   * 
   * @param value the value to assign
   * @return a modified matrix
   */
  Matrix assign(double value);

  /**
   * Assign {@code matrix} to {@code this}. Requires {@code matrix.getShape()} to equal
   * {@code this.getShape()}.
   * 
   * @param matrix the matrix
   * @return a modified matrix
   */
  Matrix assign(Matrix matrix);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value. Compare:
   * 
   * <pre>
   * Matrix original = ArrayMatrix.filledWith(10, 10, 2);
   * Matrix other = ArrayMatrix.filledWith(10, 10, 3);
   * for (int i = 0; i &lt; matrix.size(); i++) {
   *   original.put(i, other.get(i) * 3);
   * }
   * </pre>
   * 
   * and {@code original.assign(other, x -> * 3)} or {@code original.add(1, other, 3)}
   * 
   * 
   * 
   * @param matrix the matrix
   * @param operator the operator
   * @return receiver modified
   */
  Matrix assign(Matrix matrix, DoubleUnaryOperator operator);

  /**
   * Assign the values in {@code values} to this matrix. The {@code length} of {@code value} must
   * equal {@code this.size()}. The array is assumed to be in column major order, hence
   * {@code [1,2,3,4]} assigned to a matrix will result in {@code [1 3; 2 4]} and not
   * {@code [1,2; 3,4]}, similar to R.
   * 
   * @param values the column major array
   * @return a modified matrix
   */
  Matrix assign(double[] values);

  /**
   * Reduces {@code this} into a real value. For example summing can be implemented as
   * {@code matrix.mapReduce((a,b) -> a + b, x -> x)}
   * 
   * 
   * @param identity the initial value
   * @param reduce takes two values and reduces them to one
   * @param map takes a value and possibly transforms it
   * @return the result
   */
  double mapReduce(double identity, DoubleBinaryOperator reduce, DoubleUnaryOperator map);

  /**
   * Reduces each column. Column wise summing can be implemented as
   * 
   * <pre>
   * matrix.reduceColumns(col -&gt; col.reduce(0, (a, b) -&gt; a + b, x -&gt; x));
   * </pre>
   * 
   * @param reduce takes a {@code Matrix} and returns {@code double}
   * @return a new column vector with the reduced value
   */
  Matrix reduceColumns(ToDoubleFunction<? super Matrix> reduce);

  /**
   * Reduces each rows. Row wise summing can be implemented as
   *
   * <pre>
   * matrix.reduceRows(row -&gt; row.reduce(0, (a, b) -&gt; a + b, x -&gt; x));
   * </pre>
   *
   * @param reduce takes a {@code Matrix} and returns {@code double}
   * @return a new column vector with the reduced value
   */
  Matrix reduceRows(ToDoubleFunction<? super Matrix> reduce);

  /**
   * Get row vector at {@code i}. Modifications will change to original matrix.
   *
   * @param i row
   * @return a vector
   */
  Matrix getRowView(int i);

  /**
   * Gets vector at {@code index}. Modifications will change the original matrix.
   *
   * @param index the index
   * @return the column
   */
  Matrix getColumnView(int index);

  /**
   * Gets a view of the diagonal. Modifications will change the original matrix.
   * 
   * @return a diagonal view
   */
  Diagonal getDiagonalView();

  /**
   * Get a view of row starting at {@code rowOffset} until {@code rowOffset + rows} and columns
   * starting at {@code colOffset} until {@code colOffset + columns}.
   * 
   * For example,
   * 
   * <pre>
   *   1 2 3
   *   4 5 6
   *   7 8 9
   * </pre>
   * 
   * and {@code matrix.getView(1, 1, 2, 2)} produces
   * 
   * <pre>
   *   5 6
   *   8 9
   * </pre>
   * 
   * @param rowOffset
   * @param colOffset
   * @param rows
   * @param columns
   * @return
   */
  Matrix getView(int rowOffset, int colOffset, int rows, int columns);

  /**
   * Gets columns.
   *
   * @param start the start
   * @param end the end
   * @return columns columns
   */
  Matrix getColumns(int start, int end);

  /**
   * Create a copy of this matrix.
   *
   * @return the copy
   */
  Matrix copy();

  /**
   * Transpose matrix like.
   *
   * @return the matrix like
   */
  Matrix transpose();

  /**
   * The number of rows.
   *
   * @return number or rows
   */
  int rows();

  /**
   * The number of columns.
   *
   * @return number of columns
   */
  int columns();


  // Arithmetical operations ///////////

  /**
   * <u>m</u>atrix<u>m</u>ultiplication
   *
   * @param other the other
   * @return r r
   */
  Matrix mmul(Matrix other);

  /**
   * <u>m</u>atrix<u>d</u>iagonal multiplication
   *
   * @param diagonal the diagonal
   * @return matrix matrix
   */
  Matrix mmul(Diagonal diagonal);

  /**
   * Element wise <u>m</u>ultiplication
   * 
   * @param other the matrix
   * @return a new matrix
   */
  Matrix mul(Matrix other);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  Matrix mul(double scalar);

  /**
   * In place element wise <u>m</u>ultiplication.
   *
   * @param scalar the scalar
   * @return receiver multiplied
   */
  Matrix muli(double scalar);

  /**
   * In place element wise <u>m</u>ultiplication.
   *
   * @param other the other
   * @return receiver modified
   */
  Matrix muli(Matrix other);

  /**
   * Element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  Matrix add(Matrix other);

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  Matrix add(double scalar);

  /**
   * In place element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  Matrix addi(Matrix other);

  /**
   * In place element wise addition.
   *
   * @param scalar the scalar
   * @return receiver modified
   */
  Matrix addi(double scalar);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  Matrix sub(Matrix other);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param scalar the scalar
   * @return r r
   */
  Matrix sub(double scalar);

  /**
   * In place element wise subtraction.
   *
   * @param other the other matrix
   * @return receiver modified
   */
  Matrix subi(Matrix other);

  /**
   * In place element wise subtraction.
   *
   * @param scalar the scalar
   * @return receiver modified
   */
  Matrix subi(double scalar);

  /**
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  Matrix rsub(double scalar);


  /**
   * In place <u>r</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return r r
   */
  Matrix rsubi(double scalar);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the other
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  Matrix div(Matrix other);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  default Matrix div(double other) {
    return mul(1.0 / other);
  }

  /**
   * In place element wise division.
   *
   * @param other the other matrix
   * @return receiver modified
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  Matrix divi(Matrix other);

  /**
   * In place element wise division.
   * 
   * @param other the other
   * @return receiver modified
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  Matrix divi(double other);

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  Matrix rdiv(double other);

  /**
   * In place element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  Matrix rdivi(double other);

  /**
   * Element wise subtraction. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).sub(other.mul(beta))}, but in one pass.
   * 
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  Matrix sub(double alpha, Matrix other, double beta);

  /**
   * In place Element wise subtraction.
   * 
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @see #sub(double, Matrix, double)
   * @return a new matrix
   */
  Matrix subi(double alpha, Matrix other, double beta);

  /**
   * Element wise addition. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).add(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  Matrix add(double alpha, Matrix other, double beta);

  /**
   * In place Element wise subtraction.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @see #add(double, Matrix, double)
   * @return a new matrix
   */
  Matrix addi(double alpha, Matrix other, double beta);

  /**
   * Element wise multiplication. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).mul(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  Matrix mul(double alpha, Matrix other, double beta);

  /**
   * In place Element wise subtraction.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @see #mul(double, Matrix, double)
   * @return a new matrix
   */
  Matrix muli(double alpha, Matrix other, double beta);

  /**
   * <u>M</u>atrix <u>M</u>atrix <u>M</u>ultiplication. Scaling {@code this} with {@code alpha} and
   * {@code other} with {@code beta}. Hence, it computes
   * {@code this.mul(alpha).mul(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  Matrix mmul(double alpha, Matrix other, double beta);

  /**
   * Perform possibly <i>unsafe</i> operation on the, in some cases, underlying representation.
   * Expected to return a new matrix.
   * <p>
   * 
   * <pre>
   *  Matrix a = ...;
   *  Matrix b = ...;
   *  multiplied = a.unsafe( x -> {
   *    b.unsafe( y -> {
   *      double[] tmp = new double[a.rows() * b.columns()];
   *      BLAS.getInstance().dgemm("n", "n", a.rows(), b.columns(),
   *          b.rows(), alpha, a.values, a.rows(),
   *          b.values, b.rows(), beta, tmp, a.rows());
   *      return new ArrayMatrix(b.columns(), tmp);
   *    })
   *  });
   * </pre>
   *
   * @param op unsafe operation
   * @return a new matrix
   */
  Matrix unsafeTransform(Function<double[], Matrix> op);

  void unsafe(Consumer<double[]> consumer);

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  Matrix negate();

  /**
   * Set value at row {@code i} and column {@code j} to value
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

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) < other.get(i, j)}.
   * 
   * @param other the matrix
   * @return a boolean matrix
   */
  BooleanMatrix lessThan(Matrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) < value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BooleanMatrix lessThan(double value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) <= other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BooleanMatrix lessThanEqual(Matrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) <= value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BooleanMatrix lessThanEqual(double value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) > other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BooleanMatrix greaterThan(Matrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) > value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BooleanMatrix greaterThan(double value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) >= other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BooleanMatrix greaterThanEquals(Matrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) >= value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BooleanMatrix greaterThanEquals(double value);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if
   * {@code get(i, j) == other.get(i, j)}.
   *
   * @param other the matrix
   * @return a boolean matrix
   */
  BooleanMatrix equalsTo(Matrix other);

  /**
   * Return a boolean matrix with element {@code i, j} set to true if {@code get(i, j) == value}.
   *
   * @param value the matrix
   * @return a boolean matrix
   */
  BooleanMatrix equalsTo(double value);

  /**
   * Is square.
   *
   * @return true if rows() == columns()
   */
  default boolean isSquare() {
    return rows() == columns();
  }

  /**
   * The shape of the current matrix.
   *
   * @return the shape
   */
  default Shape getShape() {
    return Shape.of(rows(), columns());
  }

  /**
   * Returns true if {@link org.briljantframework.matrix.Shape#size()} == {@link #size()}
   *
   * @param shape the shape
   * @return the boolean
   */
  default boolean hasCompatibleShape(Shape shape) {
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
  default boolean hasCompatibleShape(int rows, int cols) {
    return Math.multiplyExact(rows, cols) == rows() * columns();
  }

  /**
   * Equal shape (i.e.
   *
   * @param other the other
   * @return the boolean
   */
  default boolean hasEqualShape(Matrix other) {
    return rows() == other.rows() && columns() == other.columns();
  }

  Matrix newEmptyMatrix(int rows, int columns);
}
