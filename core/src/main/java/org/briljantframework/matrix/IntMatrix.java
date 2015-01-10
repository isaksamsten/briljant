package org.briljantframework.matrix;

import java.util.function.*;

import org.briljantframework.complex.Complex;
import org.briljantframework.vector.VectorLike;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public interface IntMatrix extends AnyMatrix, Iterable<Integer> {

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.IntSupplier#getAsInt()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  IntMatrix assign(IntSupplier supplier);

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  IntMatrix assign(int value);

  /**
   * Assign {@code vector} extending row or column wise
   *
   * Note: {@code vector.size()} must equal {@code matrix.rows()} or {@code matrix.columns()}
   *
   * @param vector the vector
   * @param axis the extending direction
   * @return receiver modified
   */
  IntMatrix assign(VectorLike vector, Axis axis);

  /**
   * Assign {@code vector} and apply operator to every element extending row or column wise
   *
   * @param vector the vector
   * @param operator the operator
   * @param axis the extending direction
   * @return receiver modified
   */
  IntMatrix assign(VectorLike vector, IntBinaryOperator operator, Axis axis);

  /**
   * Assign {@code matrix} to {@code this}. Requires {@code matrix.getShape()} to equal
   * {@code this.getShape()}.
   *
   * @param matrix the matrix
   * @return receiver modified
   */
  IntMatrix assign(IntMatrix matrix);

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
   * @param matrix the matrix
   * @param operator the operator
   * @return receiver modified
   */
  IntMatrix assign(IntMatrix matrix, IntUnaryOperator operator);

  /**
   *
   * @param matrix
   * @param function
   * @return
   */
  IntMatrix assign(ComplexMatrix matrix, ToIntFunction<? super Complex> function);

  /**
   *
   * @param matrix
   * @param function
   * @return
   */
  IntMatrix assign(DoubleMatrix matrix, DoubleToIntFunction function);

  /**
   * Assigns values in {@code numbers}.
   *
   * @param numbers iterable of numbers
   * @return receiver modified
   */
  IntMatrix assignStream(Iterable<? extends Number> numbers);

  /**
   * Assigns elements from {@code iterable} to this matrix added in the order implemented by
   * {@link #set(int, double)} and transformed to double precision using {@code function}.
   *
   * @param iterable the iterable
   * @param function the function, transforming {@code T} to double
   * @param <T> the type
   * @return receiver modified
   */
  <T> IntMatrix assignStream(Iterable<T> iterable, ToIntFunction<? super T> function);

  /**
   * Assign the values in {@code values} to this matrix. The {@code length} of {@code value} must
   * equal {@code this.size()}. The array is assumed to be in column major order, hence
   * {@code [1,2,3,4]} assigned to a matrix will result in {@code [1 3; 2 4]} and not
   * {@code [1,2; 3,4]}, similar to R.
   *
   * @param values the column major array
   * @return receiver modified
   */
  IntMatrix assign(int[] values);

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
  IntMatrix map(IntUnaryOperator operator);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   * @return receiver modified
   */
  IntMatrix mapi(IntUnaryOperator operator);

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
  int reduce(int identity, IntBinaryOperator reduce, IntUnaryOperator map);

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
  IntMatrix reduceColumns(ToIntFunction<? super IntMatrix> reduce);

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
  IntMatrix reduceRows(ToIntFunction<? super IntMatrix> reduce);

  /**
   * Get row vector at {@code i}. Modifications will change to original matrix.
   *
   * @param i row
   * @return a vector
   */
  IntMatrix getRowView(int i);

  /**
   * Gets vector at {@code index}. Modifications will change the original matrix.
   *
   * @param index the index
   * @return the column
   */
  IntMatrix getColumnView(int index);

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
  IntMatrix getView(int rowOffset, int colOffset, int rows, int columns);

  /**
   * Transpose matrix like.
   *
   * @return the matrix like
   */
  IntMatrix transpose();

  /**
   * {@inheritDoc}
   */
  @Override
  IntMatrix reshape(int rows, int columns);

  // Arithmetical operations ///////////

  /**
   * Create a copy of this matrix.
   *
   * @return the copy
   */
  @Override
  IntMatrix copy();

  /**
   * Construct a new empty matrix with {@code this.getClass()}
   *
   * @param rows the number of rows
   * @param columns the number of colums
   * @return a new empty matrix (
   */
  IntMatrix newEmptyMatrix(int rows, int columns);

  /**
   * <u>m</u>atrix<u>m</u>ultiplication
   *
   * @param other the other
   * @return r r
   */
  IntMatrix mmul(IntMatrix other);

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
  IntMatrix mmul(int alpha, IntMatrix other, int beta);

  /**
   * Multiplies {@code this} with {@code other}. Transposing {@code this} and/or {@code other}.
   *
   * @param a transpose for {@code this}
   * @param other the matrix
   * @param b transpose for {@code other}
   * @return a new matrix
   */
  IntMatrix mmul(Transpose a, IntMatrix other, Transpose b);

  /**
   * Multiplies {@code this} with {@code other}. Transposing {@code this} and/or {@code other}
   * scaling by {@code alpha} {@code beta}.
   *
   * @param alpha scaling factor for {@code this}
   * @param a transpose for {@code this}
   * @param other the matrix
   * @param beta scaling factor for {@code other}
   * @param b transpose for {@code other}
   * @return a new matrix
   */
  IntMatrix mmul(int alpha, Transpose a, IntMatrix other, int beta, Transpose b);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param other the matrix
   * @return a new matrix
   */
  IntMatrix mul(IntMatrix other);

  /**
   * Element wise multiplication. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).mul(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  IntMatrix mul(int alpha, IntMatrix other, int beta);

  /**
   * Element wise multiplication, extending {@code other} row or column wise (determined by
   * {@code axis})
   *
   * @param other the vector
   * @param axis the extending direction
   * @return a new matrix
   * @see #mul(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix mul(VectorLike other, Axis axis);

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
  IntMatrix mul(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  IntMatrix mul(int scalar);

  /**
   * In place element wise <u>m</u>ultiplication.
   *
   * @param other the other
   * @return receiver modified
   */
  IntMatrix muli(IntMatrix other);

  /**
   * In place element wise <u>m</u>ultiplication.
   *
   * @param scalar the scalar
   * @return receiver multiplied
   */
  IntMatrix muli(int scalar);

  /**
   * In place Element wise subtraction.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @see #mul(int, IntMatrix, int)
   * @return a new matrix
   */
  IntMatrix muli(int alpha, IntMatrix other, int beta);

  /**
   * @param other the array
   * @param axis the extending direction
   * @return receiver modified
   * @see #mul(org.briljantframework.vector.VectorLike, Axis)
   */
  IntMatrix muli(VectorLike other, Axis axis);

  /**
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #mul(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix muli(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * Element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  IntMatrix add(IntMatrix other);

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  IntMatrix add(int scalar);

  /**
   * Element wise addition. Same as {@code add(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #add(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix add(VectorLike other, Axis axis);

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
  IntMatrix add(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * Element wise addition. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).add(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  IntMatrix add(int alpha, IntMatrix other, int beta);

  /**
   * In place element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  IntMatrix addi(IntMatrix other);

  /**
   * In place element wise addition.
   *
   * @param scalar the scalar
   * @return receiver modified
   */
  IntMatrix addi(int scalar);

  /**
   * In place version of {@code add}
   *
   * @param other the array
   * @param axis the extending direction
   * @return reciver modified
   */
  IntMatrix addi(VectorLike other, Axis axis);

  /**
   * In place version of {@code add}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #add(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix addi(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * In place element wise subtraction.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @see #add(int, IntMatrix, int)
   * @return a new matrix
   */
  IntMatrix addi(int alpha, IntMatrix other, int beta);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  IntMatrix sub(IntMatrix other);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param scalar the scalar
   * @return r r
   */
  IntMatrix sub(int scalar);

  /**
   * Element wise subtraction. Same as {@code sub(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #sub(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix sub(VectorLike other, Axis axis);

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
  IntMatrix sub(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * Element wise subtraction. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).sub(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  IntMatrix sub(int alpha, IntMatrix other, int beta);

  /**
   * In place element wise subtraction.
   *
   * @param other the other matrix
   * @return receiver modified
   */
  IntMatrix subi(IntMatrix other);

  /**
   * In place element wise subtraction.
   *
   * @param scalar the scalar
   * @return receiver modified
   */
  IntMatrix subi(int scalar);

  /**
   * In place version of {@code sub}
   *
   * @param other the array
   * @param axis the extending direction
   * @return reciver modified
   */
  IntMatrix subi(VectorLike other, Axis axis);

  /**
   * In place version of {@code sub}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #sub(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix subi(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * In place Element wise subtraction.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @see #sub(int, IntMatrix, int)
   * @return a new matrix
   */
  IntMatrix subi(int alpha, IntMatrix other, int beta);

  /**
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  IntMatrix rsub(int scalar);

  /**
   * Element wise subtraction. Same as {@code rsub(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #sub(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix rsub(VectorLike other, Axis axis);

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
  IntMatrix rsub(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * In place <u>r</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return r r
   */
  IntMatrix rsubi(int scalar);

  /**
   * In place version of {@code rsub}
   *
   * @param other the array
   * @param axis the extending direction
   * @return reciver modified
   */
  IntMatrix rsubi(VectorLike other, Axis axis);

  /**
   * In place version of {@code rsub}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #rsub(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix rsubi(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the other
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  IntMatrix div(IntMatrix other);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  IntMatrix div(int other);

  /**
   * Element wise division. Same as {@code add(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #add(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix div(VectorLike other, Axis axis);

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
  IntMatrix div(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * In place element wise division.
   *
   * @param other the other matrix
   * @return receiver modified
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  IntMatrix divi(IntMatrix other);

  /**
   * In place element wise division.
   *
   * @param other the other
   * @return receiver modified
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  IntMatrix divi(int other);

  /**
   * @param other the array
   * @param axis the extending direction
   * @return receiver modified
   * @see #div(org.briljantframework.vector.VectorLike, Axis)
   */
  IntMatrix divi(VectorLike other, Axis axis);

  /**
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #div(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix divi(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  IntMatrix rdiv(int other);

  /**
   * Element wise division. Same as {@code add(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #add(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix rdiv(VectorLike other, Axis axis);

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
  IntMatrix rdiv(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * In place element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  IntMatrix rdivi(int other);

  /**
   * @param other the array
   * @param axis the extending direction
   * @return receiver modified
   * @see #divi(org.briljantframework.vector.VectorLike, Axis)
   */
  IntMatrix rdivi(VectorLike other, Axis axis);

  /**
   * @param alpha scaling factor for {@code this}
   * @param other the array
   * @param beta scaling factor for {@code other}
   * @param axis the extending direction
   * @return receiver modified
   * @see #divi(int, org.briljantframework.vector.VectorLike, int, Axis)
   */
  IntMatrix rdivi(int alpha, VectorLike other, int beta, Axis axis);

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  IntMatrix negate();

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value int
   */
  int get(int i, int j);

  /**
   * @param index get int
   * @return int at {@code index}
   * @see #getAsInt(int)
   */
  int get(int index);

  /**
   * Equal shape (i.e.
   *
   * @param other the other
   * @return the boolean
   */
  default boolean hasEqualShape(IntMatrix other) {
    return rows() == other.rows() && columns() == other.columns();
  }

  /**
   * @return the matrix as a column-major int array
   * @see #isArrayBased()
   */
  int[] asIntArray();

  /**
   * @return true if {@link #asIntArray()} is {@code O(1)}
   */
  boolean isArrayBased();
}
