package org.briljantframework.matrix;

import java.util.Collection;
import java.util.function.*;

import org.briljantframework.complex.Complex;
import org.briljantframework.function.IntBiPredicate;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public interface IntMatrix extends Matrix, Iterable<Integer> {

  // Assignments

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  IntMatrix assign(int value);

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.IntSupplier#getAsInt()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  IntMatrix assign(IntSupplier supplier);


  IntMatrix assign(IntUnaryOperator operator);

  /**
   * Assign {@code matrix} to {@code this}. Requires {@code matrix.getShape()} to equal
   * {@code this.getShape()}.
   *
   * @param matrix the matrix
   * @return receiver modified
   */
  IntMatrix assign(IntMatrix matrix);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param operator the operator
   * @return receiver modified
   */
  IntMatrix assign(IntMatrix matrix, IntUnaryOperator operator);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code combine} to combine the i:th value of
   * {@code this} and {@code matrix}
   * 
   * @param matrix the matrix
   * @param combine the combiner
   * @return receiver modified
   */
  IntMatrix assign(IntMatrix matrix, IntBinaryOperator combine);

  IntMatrix assign(ComplexMatrix matrix, ToIntFunction<? super Complex> function);

  IntMatrix assign(DoubleMatrix matrix, DoubleToIntFunction function);

  IntMatrix assign(LongMatrix matrix, LongToIntFunction operator);

  // Transform

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

  LongMatrix mapToLong(IntToLongFunction function);

  DoubleMatrix mapToDouble(IntToDoubleFunction function);

  ComplexMatrix mapToComplex(IntFunction<Complex> function);

  // Filter

  IntMatrix filter(IntPredicate operator);

  BitMatrix satisfies(IntPredicate predicate);

  BitMatrix satisfies(IntMatrix matrix, IntBiPredicate predicate);

  int reduce(int identity, IntBinaryOperator reduce);

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
   * {@inheritDoc}
   * 
   * @param rows
   * @param columns
   */
  @Override
  IntMatrix reshape(int rows, int columns);

  // GET / SET

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
   */
  int get(int index);

  void set(int index, int value);

  void set(int row, int column, int value);

  void setRow(int index, IntMatrix matrix);

  void setColumn(int index, IntMatrix matrix);

  void addTo(int index, int value);

  void addTo(int i, int j, int value);

  void update(int index, IntUnaryOperator operator);

  void update(int i, int j, IntUnaryOperator operator);

  /**
   * {@inheritDoc}
   * 
   * @param i
   */
  IntMatrix getRowView(int i);

  /**
   * {@inheritDoc}
   * 
   * @param index
   */
  IntMatrix getColumnView(int index);

  /**
   * {@inheritDoc}
   */
  IntMatrix getDiagonalView();

  /**
   * {@inheritDoc}
   */
  IntMatrix getView(int rowOffset, int colOffset, int rows, int columns);

  @Override
  IntMatrix slice(Range rows, Range columns);

  @Override
  IntMatrix slice(Range range);

  @Override
  IntMatrix slice(Range range, Axis axis);

  @Override
  IntMatrix slice(Collection<Integer> rows, Collection<Integer> columns);

  @Override
  IntMatrix slice(Collection<Integer> indexes);

  @Override
  IntMatrix slice(Collection<Integer> indexes, Axis axis);

  @Override
  IntMatrix slice(BitMatrix bits);

  @Override
  IntMatrix slice(BitMatrix indexes, Axis axis);

  /**
   * {@inheritDoc}
   */
  IntMatrix transpose();

  /**
   * {@inheritDoc}
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

  IntMatrix newEmptyVector(int size);

  // Arithmetical operations ///////////

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
   * @see #mul(int, org.briljantframework.matrix.IntMatrix, int, Axis)
   */
  IntMatrix mul(IntMatrix other, Axis axis);

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
  IntMatrix mul(int alpha, IntMatrix other, int beta, Axis axis);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  IntMatrix mul(int scalar);

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
   * @see #add(int, org.briljantframework.matrix.IntMatrix, int, Axis)
   */
  IntMatrix add(IntMatrix other, Axis axis);

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
  IntMatrix add(int alpha, IntMatrix other, int beta, Axis axis);

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
   * @see #sub(int, org.briljantframework.matrix.IntMatrix, int, Axis)
   */
  IntMatrix sub(IntMatrix other, Axis axis);

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
  IntMatrix sub(int alpha, IntMatrix other, int beta, Axis axis);

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
   * @see #sub(int, org.briljantframework.matrix.IntMatrix, int, Axis)
   */
  IntMatrix rsub(IntMatrix other, Axis axis);

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
  IntMatrix rsub(int alpha, IntMatrix other, int beta, Axis axis);

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
   * @see #add(int, org.briljantframework.matrix.IntMatrix, int, Axis)
   */
  IntMatrix div(IntMatrix other, Axis axis);

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
  IntMatrix div(int alpha, IntMatrix other, int beta, Axis axis);

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
   * @see #add(int, org.briljantframework.matrix.IntMatrix, int, Axis)
   */
  IntMatrix rdiv(IntMatrix other, Axis axis);

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
  IntMatrix rdiv(int alpha, IntMatrix other, int beta, Axis axis);

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  IntMatrix negate();

}
