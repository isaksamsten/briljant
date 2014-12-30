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

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ToDoubleFunction;

import org.briljantframework.vector.VectorLike;

/**
 * A matrix is a 2-dimensional array.
 * 
 * <p>
 * Every implementation have to ensure that {@link #put(int, double)}, {@link #get(int)} and
 * {@link #asDoubleArray()} work in <b>column-major</b> order as fortran and not in <b>row-major</b>
 * order as in e.g., c.
 * 
 * More specifically this means that given the matrix {@code m}
 * 
 * <pre>
 *     1  2  3
 *     4  5  6
 *     7  8  9
 * </pre>
 * 
 * The following must hold:
 * <ul>
 * <li>{@code m.asDoubleArray()} returns {@code [1,4,7,2,5,8,3,6,9]}</li>
 * <li>{@code for(int i = 0; i < m.size(); i++) System.out.print(m.get(i))} produces
 * {@code 147258369}</li>
 * <li>{@code for(int i = 0; i < m.size(); i++) m.put(i, m.get(i) * 2)} changes {@code m} to
 * 
 * <pre>
 *     1   4   6
 *     8   10  12
 *     14  16  18
 * </pre>
 * 
 * </li>
 * </ul>
 * <p>
 * Due to the order in which values are stored and implications such as cache-locality, different
 * implementations might have varying performance characteristics. For example, for element wise
 * operations one should prefer {@link #get(int)} and {@link #put(int, double)} to
 * {@link #get(int, int)} and {@link #put(int, int, double)}.
 *
 * <pre>
 * // Option 1:
 * for (int i = 0; i < m.rows(); i++) {
 *   for (int j = 0; j < m.columns() ; j++) {
 *     m.put(i, j, m.get(i, j) * 2);
 *   }
 * }
 * 
 * // Option 2
 * for (int i = 0; i < m.size(); i++) {
 *   m.put(i, m.get(i) * 2)
 * }
 * 
 * // Option 3
 * for (int j = 0; j < m.columns(); j++) {
 *   for (int i = 0; i < m.rows() ; i++) {
 *     m.put(i, j, m.get(i, j) * 2);
 *   }
 * }
 * </pre>
 * 
 * In the example above, prefer <b>Option 2</b> (or simply {@code m.addi(2)}). <b>Option 3</b> can
 * also be an alternative option, that for many implementations preserve cache locality and might be
 * more readable in some cases.
 * 
 * @author Isak Karlsson
 */
public interface Matrix extends Iterable<Double> {

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.DoubleSupplier#getAsDouble()}
   * 
   * @param supplier the supplier
   * @return receiver modified
   */
  Matrix assign(DoubleSupplier supplier);

  /**
   * Assign {@code value} to {@code this}
   * 
   * @param value the value to assign
   * @return receiver modified
   */
  Matrix assign(double value);

  /**
   * Assign {@code vector} extending row or column wise
   * 
   * Note: {@code vector.size()} must equal {@code matrix.rows()} or {@code matrix.columns()}
   * 
   * @param vector the vector
   * @param axis the extending direction
   * @return receiver modified
   */
  Matrix assign(VectorLike vector, Axis axis);

  /**
   * Assign {@code vector} and apply operator to every element extending row or column wise
   * 
   * @param vector the vector
   * @param operator the operator
   * @param axis the extending direction
   * @return receiver modified
   */
  Matrix assign(VectorLike vector, DoubleBinaryOperator operator, Axis axis);

  /**
   * Assign {@code matrix} to {@code this}. Requires {@code matrix.getShape()} to equal
   * {@code this.getShape()}.
   * 
   * @param matrix the matrix
   * @return receiver modified
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
   * @return receiver modified
   */
  Matrix assign(double[] values);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * For example, {@code m.map(Math::sqrt)} is equal to
   *
   * <pre>
   *     Matrix n = m.copy();
   *     for(int i = 0; i < n.size(); i++)
   *        n.put(i, Math.sqrt(n.get(i));
   * </pre>
   *
   * To perform the operation in place, modifying {@code m}, use {@code m.assign(m, Math::sqrt)} or
   * more verbosely
   *
   * <pre>
   *     for(int i = 0; i < m.size(); i++)
   *       m.put(i, Math.sqrt(m.get(i));
   * </pre>
   *
   * @param operator the operator to apply to each element
   * @return a new matrix
   */
  Matrix map(DoubleUnaryOperator operator);

  /**
   * Reduces {@code this} into a real value. For example, summing can be implemented as
   * {@code matrix.reduce(0, (a,b) -> a + b, x -> x)}
   * 
   * 
   * @param identity the initial value
   * @param reduce takes two values and reduces them to one
   * @param map takes a value and possibly transforms it
   * @return the result
   */
  double reduce(double identity, DoubleBinaryOperator reduce, DoubleUnaryOperator map);

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
   * Please note that modifications of the view, mutates the original.
   * 
   * @param rowOffset the row offset
   * @param colOffset the column offset
   * @param rows number of rows after row offset
   * @param columns number of columns after column offset
   * @return the matrix view
   */
  Matrix getView(int rowOffset, int colOffset, int rows, int columns);

  /**
   * Transpose matrix like.
   *
   * @return the matrix like
   */
  Matrix transpose();

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
   * Element wise <u>m</u>ultiplication
   *
   * @param other the matrix
   * @return a new matrix
   */
  Matrix mul(Matrix other);

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
   * Element wise multiplication, extending {@code other} row or column wise (determined by
   * {@code axis})
   *
   * @param other the vector
   * @param axis the extending direction
   * @return a new matrix
   * @see #mul(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix mul(VectorLike other, Axis axis);

  /**
   * Element wise multiplication, extending {@code other} row or column wise
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]},
   * {@code x.mul(1, y, 1, Axis.COLUMN)} extends column-wise, resulting in {@code [1,2,3;2,4,6]}.
   * Instead, using {@code y=[0, 2, 2]} and {@code x.mul(1, y, Axis.ROW)} result in
   * {@code [0,4,6;0,4,6]}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return a new matrix
   */
  Matrix mul(double alpha, VectorLike other, double beta, Axis axis);

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
   * @param other the other
   * @return receiver modified
   */
  Matrix muli(Matrix other);

  /**
   * In place element wise <u>m</u>ultiplication.
   *
   * @param scalar the scalar
   * @return receiver multiplied
   */
  Matrix muli(double scalar);

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
   * @param other the array
   * @param axis the extending direction
   * @return receiver modified
   * @see #mul(org.briljantframework.vector.VectorLike, Axis)
   */
  Matrix muli(VectorLike other, Axis axis);

  /**
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #mul(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix muli(double alpha, VectorLike other, double beta, Axis axis);

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
   * Element wise addition. Same as {@code add(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #add(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix add(VectorLike other, Axis axis);

  /**
   * Element wise add, extending {@code other} row or column wise
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]},
   * {@code x.add(1, y, 1, Axis.COLUMN)} extends column-wise, resulting in {@code [2,3,4;3,4,5]}.
   * Instead, using {@code y=[0, 2, 2]} and {@code x.add(1, y, Axis.ROW)} result in
   * {@code [1,4,5;1,4,5]}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return a new matrix
   */
  Matrix add(double alpha, VectorLike other, double beta, Axis axis);

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
   * In place version of {@code add}
   *
   * @param other the array
   * @param axis the extending direction
   * @return reciver modified
   */
  Matrix addi(VectorLike other, Axis axis);

  /**
   * In place version of {@code add}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #add(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix addi(double alpha, VectorLike other, double beta, Axis axis);

  /**
   * In place element wise subtraction.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @see #add(double, Matrix, double)
   * @return a new matrix
   */
  Matrix addi(double alpha, Matrix other, double beta);

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
   * Element wise subtraction. Same as {@code sub(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #sub(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix sub(VectorLike other, Axis axis);

  /**
   * Element wise subtraction, extending {@code other} row or column wise
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]},
   * {@code x.add(1, y, 1, Axis.COLUMN)} extends column-wise, resulting in {@code [0,1,2;-1,0,1]}.
   * Instead, using {@code y=[0, 2, 2]} and {@code x.add(1, y, Axis.ROW)} result in
   * {@code [1,0,1;1,0,1]}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return a new matrix
   */
  Matrix sub(double alpha, VectorLike other, double beta, Axis axis);

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
   * In place version of {@code sub}
   *
   * @param other the array
   * @param axis the extending direction
   * @return reciver modified
   */
  Matrix subi(VectorLike other, Axis axis);

  /**
   * In place version of {@code sub}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #sub(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix subi(double alpha, VectorLike other, double beta, Axis axis);

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
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  Matrix rsub(double scalar);

  /**
   * Element wise subtraction. Same as {@code rsub(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #sub(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix rsub(VectorLike other, Axis axis);

  /**
   * Element wise subtraction, extending {@code other} row or column wise. Inverted, i.e.,
   * {@code other - this}.
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]},
   * {@code x.add(1, y, 1, Axis.COLUMN)} extends column-wise, resulting in {@code [0,-1,-2;1,0,-1]}.
   * Instead, using {@code y=[0, 2, 2]} and {@code x.add(1, y, Axis.ROW)} result in
   * {@code [-1,0,-1;-1,0,-1]}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return a new matrix
   */
  Matrix rsub(double alpha, VectorLike other, double beta, Axis axis);

  /**
   * In place <u>r</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return r r
   */
  Matrix rsubi(double scalar);

  /**
   * In place version of {@code rsub}
   *
   * @param other the array
   * @param axis the extending direction
   * @return reciver modified
   */
  Matrix rsubi(VectorLike other, Axis axis);

  /**
   * In place version of {@code rsub}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #rsub(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix rsubi(double alpha, VectorLike other, double beta, Axis axis);

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
  Matrix div(double other);

  /**
   * Element wise division. Same as {@code add(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #add(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix div(VectorLike other, Axis axis);

  /**
   * Element wise division, extending {@code other} row or column wise
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]},
   * {@code x.add(1, y, 1, Axis.COLUMN)} extends column-wise, resulting in {@code [1,2,3;0.5,1,1.5]}
   * . Instead, using {@code y=[0, 2, 2]} and {@code x.add(1, y, Axis.ROW)} result in
   * {@link java.lang.ArithmeticException}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return a new matrix
   */
  Matrix div(double alpha, VectorLike other, double beta, Axis axis);

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
   * @param other the array
   * @param axis the extending direction
   * @return receiver modified
   * @see #div(org.briljantframework.vector.VectorLike, Axis)
   */
  Matrix divi(VectorLike other, Axis axis);

  /**
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #div(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix divi(double alpha, VectorLike other, double beta, Axis axis);

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  Matrix rdiv(double other);

  /**
   * Element wise division. Same as {@code add(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #add(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix rdiv(VectorLike other, Axis axis);

  /**
   * Element wise division, extending {@code other} row or column wise. Division is <b>reversed</b>,
   * i.e., {@code other / this}
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]},
   * {@code x.add(1, y, 1, Axis.COLUMN)} extends column-wise, resulting in {@code [1,2,3;0.5,1,1.5]}
   * . Instead, using {@code y=[0, 2, 2]} and {@code x.add(1, y, Axis.ROW)} result in
   * {@link java.lang.ArithmeticException}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return a new matrix
   */
  Matrix rdiv(double alpha, VectorLike other, double beta, Axis axis);

  /**
   * In place element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  Matrix rdivi(double other);

  /**
   * @param other the array
   * @param axis the extending direction
   * @return receiver modified
   * @see #divi(org.briljantframework.vector.VectorLike, Axis)
   */
  Matrix rdivi(VectorLike other, Axis axis);

  /**
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #divi(double, org.briljantframework.vector.VectorLike, double, Axis)
   */
  Matrix rdivi(double alpha, VectorLike other, double beta, Axis axis);

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
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value double
   */
  double get(int i, int j);

  /**
   * Puts {@code value} at the linearized position {@code index}. Column major order is strictly
   * enforced.
   * 
   * @param index the index
   * @param value the value
   * @see #get(int)
   */
  void put(int index, double value);

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order. For example, given the following matrix
   * <p>
   *
   * <pre>
   *     1 2 3
   *     4 5 6
   * </pre>
   * <p>
   * this code
   * <p>
   *
   * <pre>
   * for (int i = 0; i &lt; x.size(); i++) {
   *   System.out.print(x.get(i));
   * }
   * </pre>
   * <p>
   * prints
   * <p>
   *
   * <pre>
   * 142536
   * </pre>
   *
   * @param index the index
   * @return the value index
   */
  double get(int index);

  /**
   * Returns the linearized size of this matrix. If {@code rows()} or {@code columns()} return 1,
   * then {@code size()} is intuitive. However, if not size is {@code rows() * columns()} and the
   * end when iterating using {@link #get(int)}. To avoid cache misses,
   * {@code for(int i = 0; i < m.size(); i++) m.put(i, m.get(i) * 2)} should be prefered to
   *
   * <pre>
   *     for(int i = 0; i < m.rows(); i++)
   *       for(int j = 0; j < m.columns(); j++)
   *          m.put(i, j, m.get(i, j) * 2
   * </pre>
   *
   * @return the size
   */
  int size();

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

  /**
   * @return the matrix as a column-major double array
   * @see #isArrayBased()
   */
  double[] asDoubleArray();

  /**
   * @return true if {@link #asDoubleArray()} is {@code O(1)}
   */
  boolean isArrayBased();

  /**
   * Construct a new empty matrix with {@code this.getClass()}
   *
   * @param rows the number of rows
   * @param columns the number of colums
   * @return a new empty matrix (
   */
  Matrix newEmptyMatrix(int rows, int columns);

  /**
   * Create a copy of this matrix.
   *
   * @return the copy
   */
  Matrix copy();
}
