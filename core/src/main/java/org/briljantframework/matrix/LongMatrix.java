package org.briljantframework.matrix;

import java.util.Collection;
import java.util.function.*;
import java.util.stream.LongStream;

import org.briljantframework.complex.Complex;
import org.briljantframework.function.LongBiPredicate;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public interface LongMatrix extends Matrix, Iterable<Long> {

  // Assignments
  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  LongMatrix assign(long value);

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.LongSupplier#getAsLong()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  LongMatrix assign(LongSupplier supplier);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   * @return receiver modified
   */
  LongMatrix assign(LongUnaryOperator operator);

  /**
   * Assign {@code matrix} to {@code this}. Requires {@code matrix.getShape()} to equal
   * {@code this.getShape()}.
   *
   * @param matrix the matrix
   * @return receiver modified
   */
  LongMatrix assign(LongMatrix matrix);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param matrix the matrix
   * @param operator the operator
   * @return receiver modified
   */
  LongMatrix assign(LongMatrix matrix, LongUnaryOperator operator);

  LongMatrix assign(LongMatrix matrix, LongBinaryOperator combine);

  LongMatrix assign(ComplexMatrix matrix, ToLongFunction<? super Complex> function);

  LongMatrix assign(IntMatrix matrix, IntToLongFunction operator);

  LongMatrix assign(DoubleMatrix matrix, DoubleToLongFunction function);

  // Transform

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * For example, {@code m.map(Math::sqrt)} is equal to
   *
   * <pre>
   *     Matrix n = m.copy();
   *     for(long i = 0; i < n.size(); i++)
   *        n.put(i, Math.sqrt(n.get(i));
   * </pre>
   *
   * To perform the operation in place, modifying {@code m}, use {@code m.assign(m, Math::sqrt)} or
   * more verbosely
   *
   * <pre>
   *     for(long i = 0; i < m.size(); i++)
   *       m.put(i, Math.sqrt(m.get(i));
   * </pre>
   *
   * @param operator the operator to apply to each element
   * @return a new matrix
   */
  LongMatrix map(LongUnaryOperator operator);

  IntMatrix mapToInt(LongToIntFunction map);

  DoubleMatrix mapToDouble(LongToDoubleFunction map);

  ComplexMatrix mapToComplex(LongFunction<Complex> map);

  BitMatrix satisfies(LongPredicate predicate);

  BitMatrix satisfies(LongMatrix matrix, LongBiPredicate predicate);

  long reduce(long identity, LongBinaryOperator reduce);

  /**
   * Reduces {@code this} longo a real value. For example, summing can be implemented as
   * {@code matrix.reduce(0, (a,b) -> a + b, x -> x)}
   *
   *
   * @param identity the initial value
   * @param reduce takes two values and reduces them to one
   * @param map takes a value and possibly transforms it
   * @return the result
   */
  long reduce(long identity, LongBinaryOperator reduce, LongUnaryOperator map);

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
  LongMatrix reduceColumns(ToLongFunction<? super LongMatrix> reduce);

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
  LongMatrix reduceRows(ToLongFunction<? super LongMatrix> reduce);


  // Filter

  LongMatrix filter(LongPredicate operator);

  /**
   * {@inheritDoc}
   * 
   * @param rows
   * @param columns
   */
  @Override
  LongMatrix reshape(int rows, int columns);

  // Get / set

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value long
   */
  long get(int i, int j);

  /**
   * @param index get long
   * @return long at {@code index}
   */
  long get(int index);

  void set(int index, long value);

  void set(int row, int column, long value);

  void setRow(int index, LongMatrix row);

  void setColumn(int index, LongMatrix column);

  /**
   * {@inheritDoc}
   *
   * @param i
   */
  LongMatrix getRowView(int i);

  /**
   * {@inheritDoc}
   *
   * @param index
   */
  LongMatrix getColumnView(int index);

  /**
   * {@inheritDoc}
   */
  LongMatrix getDiagonalView();

  /**
   * {@inheritDoc}
   * 
   * @param rowOffset
   * @param colOffset
   * @param rows
   * @param columns
   */
  LongMatrix getView(int rowOffset, int colOffset, int rows, int columns);

  @Override
  LongMatrix slice(Range rows, Range columns);

  @Override
  LongMatrix slice(Range range);

  @Override
  LongMatrix slice(Range range, Axis axis);

  @Override
  LongMatrix slice(Collection<Integer> rows, Collection<Integer> columns);

  @Override
  LongMatrix slice(Collection<Integer> indexes);

  @Override
  LongMatrix slice(Collection<Integer> indexes, Axis axis);

  @Override
  LongMatrix slice(BitMatrix bits);

  @Override
  LongMatrix slice(BitMatrix indexes, Axis axis);

  LongStream stream();

  /**
   * Transpose matrix like.
   *
   * @return the matrix like
   */
  LongMatrix transpose();

  /**
   * Construct a new empty matrix with {@code this.getClass()}
   *
   * @param rows the number of rows
   * @param columns the number of colums
   * @return a new empty matrix (
   */
  LongMatrix newEmptyMatrix(int rows, int columns);

  /**
   * Construct a new empty (column) vector.
   * 
   * @param size the size
   * @return the new vector
   */
  LongMatrix newEmptyVector(int size);

  /**
   * Create a copy of this matrix.
   *
   * @return the copy
   */
  @Override
  LongMatrix copy();

  // Arithmetical operations ///////////

  /**
   * <u>m</u>atrix<u>m</u>ultiplication
   *
   * @param other the other
   * @return r r
   */
  LongMatrix mmul(LongMatrix other);

  /**
   * <u>M</u>atrix <u>M</u>atrix <u>M</u>ultiplication. Scaling {@code this} with {@code alpha} and
   * {@code other} with {@code beta}. Hence, it computes
   * {@code this.mul(alpha).mul(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this * other}
   * @param other the other matrix
   * @return a new matrix
   */
  LongMatrix mmul(long alpha, LongMatrix other);

  /**
   * Multiplies {@code this} with {@code other}. Transposing {@code this} and/or {@code other}.
   *
   * @param a transpose for {@code this}
   * @param other the matrix
   * @param b transpose for {@code other}
   * @return a new matrix
   */
  LongMatrix mmul(Transpose a, LongMatrix other, Transpose b);

  /**
   * Multiplies {@code this} with {@code other}. Transposing {@code this} and/or {@code other}
   * scaling by {@code alpha} {@code beta}.
   *
   * @param alpha scaling factor for {@code this * other}
   * @param a transpose for {@code this}
   * @param other the matrix
   * @param b transpose for {@code other}
   * @return a new matrix
   */
  LongMatrix mmul(long alpha, Transpose a, LongMatrix other, Transpose b);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param other the matrix
   * @return a new matrix
   */
  LongMatrix mul(LongMatrix other);

  /**
   * Element wise multiplication. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).mul(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  LongMatrix mul(long alpha, LongMatrix other, long beta);

  /**
   * Element wise multiplication, extending {@code other} row or column wise (determined by
   * {@code axis})
   *
   * @param other the vector
   * @param axis the extending direction
   * @return a new matrix
   * @see #mul(long, org.briljantframework.matrix.LongMatrix, long, Axis)
   */
  LongMatrix mul(LongMatrix other, Axis axis);

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
  LongMatrix mul(long alpha, LongMatrix other, long beta, Axis axis);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  LongMatrix mul(long scalar);

  /**
   * Element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  LongMatrix add(LongMatrix other);

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  LongMatrix add(long scalar);

  /**
   * Element wise addition. Same as {@code add(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #add(long, org.briljantframework.matrix.LongMatrix, long, Axis)
   */
  LongMatrix add(LongMatrix other, Axis axis);

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
  LongMatrix add(long alpha, LongMatrix other, long beta, Axis axis);

  /**
   * Element wise addition. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).add(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  LongMatrix add(long alpha, LongMatrix other, long beta);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  LongMatrix sub(LongMatrix other);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param scalar the scalar
   * @return r r
   */
  LongMatrix sub(long scalar);

  /**
   * Element wise subtraction. Same as {@code sub(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #sub(long, org.briljantframework.matrix.LongMatrix, long, Axis)
   */
  LongMatrix sub(LongMatrix other, Axis axis);

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
  LongMatrix sub(long alpha, LongMatrix other, long beta, Axis axis);

  /**
   * Element wise subtraction. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).sub(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  LongMatrix sub(long alpha, LongMatrix other, long beta);

  /**
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  LongMatrix rsub(long scalar);

  /**
   * Element wise subtraction. Same as {@code rsub(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #sub(long, org.briljantframework.matrix.LongMatrix, long, Axis)
   */
  LongMatrix rsub(LongMatrix other, Axis axis);

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
  LongMatrix rsub(long alpha, LongMatrix other, long beta, Axis axis);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the other
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  LongMatrix div(LongMatrix other);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  LongMatrix div(long other);

  /**
   * Element wise division. Same as {@code add(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #add(long, org.briljantframework.matrix.LongMatrix, long, Axis)
   */
  LongMatrix div(LongMatrix other, Axis axis);

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
  LongMatrix div(long alpha, LongMatrix other, long beta, Axis axis);

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  LongMatrix rdiv(long other);

  /**
   * Element wise division. Same as {@code add(1, other, 1, axis)}.
   *
   * @param other the array
   * @param axis the extending direction
   * @return a new matrix
   * @see #add(long, org.briljantframework.matrix.LongMatrix, long, Axis)
   */
  LongMatrix rdiv(LongMatrix other, Axis axis);

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
  LongMatrix rdiv(long alpha, LongMatrix other, long beta, Axis axis);

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  LongMatrix negate();
}
