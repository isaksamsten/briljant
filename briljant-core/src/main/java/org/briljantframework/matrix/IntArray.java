package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;
import org.briljantframework.function.IntBiPredicate;
import org.briljantframework.function.ToIntObjIntBiFunction;

import java.util.List;
import java.util.function.DoubleToIntFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongToIntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

/**
 * @author Isak Karlsson
 */
public interface IntArray extends Array<IntArray> {

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  IntArray assign(int value);

  IntArray assign(int[] data);

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.IntSupplier#getAsInt()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  IntArray assign(IntSupplier supplier);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param operator the operator
   * @return receiver modified
   */
  IntArray assign(IntArray matrix, IntUnaryOperator operator);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code combine} to combine the i:th value of
   * {@code this} and {@code matrix}
   *
   * @param matrix  the matrix
   * @param combine the combiner
   * @return receiver modified
   */
  IntArray assign(IntArray matrix, IntBinaryOperator combine);

  IntArray assign(ComplexArray matrix, ToIntFunction<? super Complex> function);

  IntArray assign(DoubleArray matrix, DoubleToIntFunction function);

  IntArray assign(LongArray matrix, LongToIntFunction operator);

  IntArray assign(BitArray matrix, ToIntObjIntBiFunction<Boolean> function);

  IntArray update(IntUnaryOperator operator);

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
  IntArray map(IntUnaryOperator operator);

  LongArray mapToLong(IntToLongFunction function);

  DoubleArray mapToDouble(IntToDoubleFunction function);

  ComplexArray mapToComplex(IntFunction<Complex> function);

  // Filter

  IntArray filter(IntPredicate operator);

  BitArray satisfies(IntPredicate predicate);

  BitArray satisfies(IntArray matrix, IntBiPredicate predicate);

  void forEach(IntConsumer consumer);

  int reduce(int identity, IntBinaryOperator reduce);

  /**
   * Reduces {@code this} into a real value. For example, summing can be implemented as
   * {@code matrix.reduce(0, (a, sumSoFar) -> a + sumSoFar, x -> x)}
   *
   * <p> The operation {@code reduce} takes two parameters the current value and the accumulated
   * value (set to {@code identity} at the first iteration)
   *
   * @param identity the initial value
   * @param reduce   takes two values and reduces them to one
   * @param map      takes a value and possibly transforms it
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
  IntArray reduceColumns(ToIntFunction<? super IntArray> reduce);

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
  IntArray reduceRows(ToIntFunction<? super IntArray> reduce);

  // GET / SET

  /**
   * @param index get int
   * @return int at {@code index}
   */
  int get(int index);

  void set(int index, int value);

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value int
   */
  int get(int i, int j);

  void set(int row, int column, int value);

  void set(int[] ix, int value);

  int get(int... ix);

  void addTo(int index, int value);

  void addTo(int i, int j, int value);

  void update(int index, IntUnaryOperator operator);

  void update(int i, int j, IntUnaryOperator operator);

  IntStream stream();

  List<Integer> flat();

  // Arithmetical operations ///////////

  /**
   * <u>m</u>atrix<u>m</u>ultiplication
   *
   * @param other the other
   * @return r r
   */
  IntArray mmul(IntArray other);

  /**
   * <u>M</u>atrix <u>M</u>atrix <u>M</u>ultiplication. Scaling {@code this} with {@code alpha} and
   * {@code other} with {@code beta}. Hence, it computes
   * {@code this.mul(alpha).mul(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this*other}
   * @param other the other matrix
   * @return a new matrix
   */
  IntArray mmul(int alpha, IntArray other);

  /**
   * Multiplies {@code this} with {@code other}. Transposing {@code this} and/or {@code other}.
   *
   * @param a     transpose for {@code this}
   * @param other the matrix
   * @param b     transpose for {@code other}
   * @return a new matrix
   */
  IntArray mmul(Op a, IntArray other, Op b);

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
  IntArray mmul(int alpha, Op a, IntArray other, Op b);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param other the matrix
   * @return a new matrix
   */
  IntArray mul(IntArray other);

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
  IntArray mul(int alpha, IntArray other, int beta);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  IntArray mul(int scalar);

  /**
   * Element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  IntArray add(IntArray other);

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  IntArray add(int scalar);

  /**
   * Element wise addition. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).add(other.mul(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
   * @return a new matrix
   */
  IntArray add(int alpha, IntArray other, int beta);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  IntArray sub(IntArray other);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param scalar the scalar
   * @return r r
   */
  IntArray sub(int scalar);

  /**
   * Element wise subtraction. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).sub(other.mul(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
   * @return a new matrix
   */
  IntArray sub(int alpha, IntArray other, int beta);

  /**
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  IntArray rsub(int scalar);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the other
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  IntArray div(IntArray other);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  IntArray div(int other);

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  IntArray rdiv(int other);

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  IntArray negate();

  int[] data();
}
