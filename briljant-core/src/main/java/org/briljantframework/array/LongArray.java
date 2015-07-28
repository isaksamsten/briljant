package org.briljantframework.array;

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
public interface LongArray extends BaseArray<LongArray>, Iterable<Long> {

  // Assignments

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  LongArray assign(long value);

  /**
   * Assign value returned by {@link #size()} successive calls to {@link
   * java.util.function.LongSupplier#getAsLong()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  LongArray assign(LongSupplier supplier);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param matrix   the matrix
   * @param operator the operator
   * @return receiver modified
   */
  LongArray assign(LongArray matrix, LongUnaryOperator operator);

  LongArray assign(LongArray matrix, LongBinaryOperator combine);

  LongArray assign(ComplexArray matrix, ToLongFunction<? super Complex> function);

  LongArray assign(IntArray matrix, IntToLongFunction operator);

  LongArray assign(DoubleArray matrix, DoubleToLongFunction function);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   * @return receiver modified
   */
  LongArray update(LongUnaryOperator operator);

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
  LongArray map(LongUnaryOperator operator);

  IntArray mapToInt(LongToIntFunction map);

  DoubleArray mapToDouble(LongToDoubleFunction map);

  ComplexArray mapToComplex(LongFunction<Complex> map);

  BitArray satisfies(LongPredicate predicate);

  BitArray satisfies(LongArray matrix, LongBiPredicate predicate);

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
  LongArray reduceColumns(ToLongFunction<? super LongArray> reduce);

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
  LongArray reduceRows(ToLongFunction<? super LongArray> reduce);

  // Filter

  LongArray filter(LongPredicate operator);

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

  void set(int[] ix, long value);

  long get(int... ix);

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
  LongArray mmul(LongArray other);

  /**
   * <u>M</u>atrix <u>M</u>atrix <u>M</u>ultiplication. Scaling {@code this} with {@code alpha} and
   * {@code other} with {@code beta}. Hence, it computes {@code this.mul(alpha).mul(other.mul(beta))},
   * but in one pass.
   *
   * @param alpha scaling for {@code this * other}
   * @param other the other matrix
   * @return a new matrix
   */
  LongArray mmul(long alpha, LongArray other);

  /**
   * Multiplies {@code this} with {@code other}. Transposing {@code this} and/or {@code other}.
   *
   * @param a     transpose for {@code this}
   * @param other the matrix
   * @param b     transpose for {@code other}
   * @return a new matrix
   */
  LongArray mmul(Op a, LongArray other, Op b);

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
  LongArray mmul(long alpha, Op a, LongArray other, Op b);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param other the matrix
   * @return a new matrix
   */
  LongArray mul(LongArray other);

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
  LongArray mul(long alpha, LongArray other, long beta);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  LongArray mul(long scalar);

  /**
   * Element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  LongArray add(LongArray other);

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  LongArray add(long scalar);

  /**
   * Element wise addition. Scaling {@code this} with {@code alpha} and {@code other} with {@code
   * beta}. Hence, it computes {@code this.mul(alpha).add(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
   * @return a new matrix
   */
  LongArray add(long alpha, LongArray other, long beta);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  LongArray sub(LongArray other);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param scalar the scalar
   * @return r r
   */
  LongArray sub(long scalar);

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
  LongArray sub(long alpha, LongArray other, long beta);

  /**
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  LongArray rsub(long scalar);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the other
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  LongArray div(LongArray other);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  LongArray div(long other);

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  LongArray rdiv(long other);

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  LongArray negate();
}
