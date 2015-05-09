package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;
import org.briljantframework.function.LongBiPredicate;

import java.util.List;
import java.util.function.DoubleToLongFunction;
import java.util.function.IntToLongFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public interface LongMatrix extends Matrix<LongMatrix>, Iterable<Long> {

  // Assignments

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  LongMatrix assign(long value);

  /**
   * Assign value returned by {@link #size()} successive calls to {@link
   * java.util.function.LongSupplier#getAsLong()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  LongMatrix assign(LongSupplier supplier);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param matrix   the matrix
   * @param operator the operator
   * @return receiver modified
   */
  LongMatrix assign(LongMatrix matrix, LongUnaryOperator operator);

  LongMatrix assign(LongMatrix matrix, LongBinaryOperator combine);

  LongMatrix assign(ComplexMatrix matrix, ToLongFunction<? super Complex> function);

  LongMatrix assign(IntMatrix matrix, IntToLongFunction operator);

  LongMatrix assign(DoubleMatrix matrix, DoubleToLongFunction function);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   * @return receiver modified
   */
  LongMatrix update(LongUnaryOperator operator);

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
   * Reduces {@code this} longo a real value. For example, summing can be implemented as {@code
   * matrix.reduce(0, (a, b) -> a + b, x -> x)}
   *
   * The first value of {@code reduce} is the current value and the second value is the
   * accumulator.
   *
   * @param identity the initial value
   * @param reduce   takes two values and reduces them to one
   * @param map      takes a value and possibly transforms it
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

  LongStream stream();

  List<Long> asList();

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
   * {@code other} with {@code beta}. Hence, it computes {@code this.mul(alpha).mul(other.mul(beta))},
   * but in one pass.
   *
   * @param alpha scaling for {@code this * other}
   * @param other the other matrix
   * @return a new matrix
   */
  LongMatrix mmul(long alpha, LongMatrix other);

  /**
   * Multiplies {@code this} with {@code other}. Transposing {@code this} and/or {@code other}.
   *
   * @param a     transpose for {@code this}
   * @param other the matrix
   * @param b     transpose for {@code other}
   * @return a new matrix
   */
  LongMatrix mmul(T a, LongMatrix other, T b);

  /**
   * Multiplies {@code this} with {@code other}. Transposing {@code this} and/or {@code other}
   * scaling by {@code alpha} {@code beta}.
   *
   * @param alpha scaling factor for {@code this * other}
   * @param a     transpose for {@code this}
   * @param other the matrix
   * @param b     transpose for {@code other}
   * @return a new matrix
   */
  LongMatrix mmul(long alpha, T a, LongMatrix other, T b);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param other the matrix
   * @return a new matrix
   */
  LongMatrix mul(LongMatrix other);

  /**
   * Element wise multiplication. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).mul(other.mul(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
   * @return a new matrix
   */
  LongMatrix mul(long alpha, LongMatrix other, long beta);

  /**
   * Element wise multiplication, extending {@code other} row or column wise (determined by {@code
   * axis})
   *
   * @param other the vector
   * @param dim   the extending direction
   * @return a new matrix
   * @see #mul(long, org.briljantframework.matrix.LongMatrix, long, Dim)
   */
  LongMatrix mul(LongMatrix other, Dim dim);

  /**
   * Element wise multiplication, extending {@code other} row or column wise
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]}, {@code x.mul(1, y, 1,
   *Axis.COLUMN)} extends column-wise, resulting in {@code [1, 2, 3;2,4,6]}. Instead, using {@code
   * y=[0, 2, 2]} and {@code x.mul(1, y, Axis.ROW)} result in {@code [0, 4, 6;0,4,6]}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta  scaling factor for {@code other}
   * @param dim   the extending direction
   * @return a new matrix
   */
  LongMatrix mul(long alpha, LongMatrix other, long beta, Dim dim);

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
   * @param dim   the extending direction
   * @return a new matrix
   * @see #add(long, org.briljantframework.matrix.LongMatrix, long, Dim)
   */
  LongMatrix add(LongMatrix other, Dim dim);

  /**
   * Element wise add, extending {@code other} row or column wise
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]}, {@code x.add(1, y, 1,
   *Axis.COLUMN)} extends column-wise, resulting in {@code [2, 3, 4;3,4,5]}. Instead, using {@code
   * y=[0, 2, 2]} and {@code x.add(1, y, Axis.ROW)} result in {@code [1, 4, 5;1,4,5]}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta  scaling factor for {@code other}
   * @param dim   the extending direction
   * @return a new matrix
   */
  LongMatrix add(long alpha, LongMatrix other, long beta, Dim dim);

  /**
   * Element wise addition. Scaling {@code this} with {@code alpha} and {@code other} with {@code
   * beta}. Hence, it computes {@code this.mul(alpha).add(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
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
   * @param dim   the extending direction
   * @return a new matrix
   * @see #sub(long, org.briljantframework.matrix.LongMatrix, long, Dim)
   */
  LongMatrix sub(LongMatrix other, Dim dim);

  /**
   * Element wise subtraction, extending {@code other} row or column wise
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]}, {@code x.add(1, y, 1,
   *Axis.COLUMN)} extends column-wise, resulting in {@code [0, 1, 2;-1,0,1]}. Instead, using
   * {@code
   * y=[0, 2, 2]} and {@code x.add(1, y, Axis.ROW)} result in {@code [1, 0, 1;1,0,1]}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta  scaling factor for {@code other}
   * @param dim   the extending direction
   * @return a new matrix
   */
  LongMatrix sub(long alpha, LongMatrix other, long beta, Dim dim);

  /**
   * Element wise subtraction. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code
   * beta}. Hence, it computes {@code this.mul(alpha).sub(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
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
   * @param dim   the extending direction
   * @return a new matrix
   * @see #sub(long, org.briljantframework.matrix.LongMatrix, long, Dim)
   */
  LongMatrix rsub(LongMatrix other, Dim dim);

  /**
   * Element wise subtraction, extending {@code other} row or column wise. Inverted, i.e., {@code
   * other - this}.
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]}, {@code x.add(1, y, 1,
   *Axis.COLUMN)} extends column-wise, resulting in {@code [0,-1,-2;1,0,-1]}. Instead, using
   * {@code
   * y=[0, 2, 2]} and {@code x.add(1, y, Axis.ROW)} result in {@code [-1,0,-1;-1,0,-1]}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta  scaling factor for {@code other}
   * @param dim   the extending direction
   * @return a new matrix
   */
  LongMatrix rsub(long alpha, LongMatrix other, long beta, Dim dim);

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
   * @param dim   the extending direction
   * @return a new matrix
   * @see #add(long, org.briljantframework.matrix.LongMatrix, long, Dim)
   */
  LongMatrix div(LongMatrix other, Dim dim);

  /**
   * Element wise division, extending {@code other} row or column wise
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]}, {@code x.add(1, y, 1,
   *Axis.COLUMN)} extends column-wise, resulting in {@code [1, 2, 3;0.5,1,1.5]} . Instead, using
   * {@code y=[0, 2, 2]} and {@code x.add(1, y, Axis.ROW)} result in {@link
   * java.lang.ArithmeticException}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta  scaling factor for {@code other}
   * @param dim   the extending direction
   * @return a new matrix
   */
  LongMatrix div(long alpha, LongMatrix other, long beta, Dim dim);

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
   * @param dim   the extending direction
   * @return a new matrix
   * @see #add(long, org.briljantframework.matrix.LongMatrix, long, Dim)
   */
  LongMatrix rdiv(LongMatrix other, Dim dim);

  /**
   * Element wise division, extending {@code other} row or column wise. Division is
   * <b>reversed</b>,
   * i.e., {@code other / this}
   *
   * For example, given {@code y = [1,2]} and {@code x = [1,2,3;1,2,3]}, {@code x.add(1, y, 1,
   *Axis.COLUMN)} extends column-wise, resulting in {@code [1, 2, 3;0.5,1,1.5]} . Instead, using
   * {@code y=[0, 2, 2]} and {@code x.add(1, y, Axis.ROW)} result in {@link
   * java.lang.ArithmeticException}.
   *
   * @param alpha scaling factor for {@code this}
   * @param other the vector
   * @param beta  scaling factor for {@code other}
   * @param dim   the extending direction
   * @return a new matrix
   */
  LongMatrix rdiv(long alpha, LongMatrix other, long beta, Dim dim);

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  LongMatrix negate();
}
