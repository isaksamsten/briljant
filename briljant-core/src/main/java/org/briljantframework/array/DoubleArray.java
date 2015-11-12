/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.array;

import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Listable;
import org.briljantframework.function.DoubleBiPredicate;

/**
 * A matrix is a n-dimensional array.
 *
 * <p>
 * Every implementation have to ensure that {@link #set(int, double)}, {@link #get(int)} and work in
 * <b>column-major</b> order as fortran and not in <b>row-major</b> order as in e.g., c.
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
 * operations one should prefer {@link #get(int)} and {@link #set(int, double)} to
 * {@link #get(int, int)} and {@link #set(int, int, double)}.
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
 * In the example above, prefer <b>Option 2</b> (or simply {@code m.plusAssign(2)}). <b>Option 3</b>
 * can also be an alternative option, that for many implementations preserves cache locality and
 * might be more readable in some cases.
 *
 * @author Isak Karlsson
 */
public interface DoubleArray extends BaseArray<DoubleArray>, Iterable<Double>, Listable<Double> {

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   */
  void assign(double value);

  void assign(double[] array);

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.DoubleSupplier#getAsDouble()}
   *
   * @param supplier the supplier
   */
  void assign(DoubleSupplier supplier);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param matrix the matrix
   * @param operator the operator
   */
  void assign(DoubleArray matrix, DoubleUnaryOperator operator);

  void assign(DoubleArray matrix, DoubleBinaryOperator combine);

  void assign(IntArray matrix, IntToDoubleFunction function);

  void assign(LongArray matrix, LongToDoubleFunction function);

  void assign(ComplexArray matrix, ToDoubleFunction<? super Complex> function);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   */
  void update(DoubleUnaryOperator operator);

  <R, C> R collect(Collector<? super Double, C, R> collector);

  <E> E collect(Supplier<E> supplier, ObjDoubleConsumer<E> consumer);

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
  DoubleArray map(DoubleUnaryOperator operator);

  /**
   * Map each value of this double array to an int.
   *
   * @param function function for transforming double to int
   * @return an int array
   */
  IntArray mapToInt(DoubleToIntFunction function);

  LongArray mapToLong(DoubleToLongFunction function);

  ComplexArray mapToComplex(DoubleFunction<Complex> function);

  // Filter

  DoubleArray filter(DoublePredicate predicate);

  BooleanArray where(DoublePredicate predicate);

  BooleanArray where(DoubleArray matrix, DoubleBiPredicate predicate);

  void forEachDouble(DoubleConsumer consumer);

  // Reduce

  double reduce(double identity, DoubleBinaryOperator reduce);

  /**
   * Reduces {@code this} into a real value. For example, summing can be implemented as
   * {@code matrix.reduce(0, (a, b) -> a + b, x -> x)}
   *
   * @param identity the initial value
   * @param reduce takes two values and reduces them to one
   * @param map takes a value and possibly transforms it
   * @return the result
   */
  double reduce(double identity, DoubleBinaryOperator reduce, DoubleUnaryOperator map);

  DoubleArray reduceVectors(int dim, ToDoubleFunction<? super DoubleArray> reduce);

  // GET SET

  void addTo(int i, double value);

  void addTo(int i, int j, double value);

  void set(int index, double value);

  void set(int i, int j, double value);

  void set(int[] ix, double value);

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order. For example, given the following matrix
   * <p>
   * <p>
   *
   * <pre>
   *     1 2 3
   *     4 5 6
   * </pre>
   * <p>
   * this code
   * <p>
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
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value double
   */
  double get(int i, int j);

  double get(int... ix);

  DoubleStream stream();

  List<Double> toList();

  /**
   * Provides a lazy view of this {@code double} array as it's boxed counterpart.
   *
   * @return a boxed view
   */
  Array<Double> boxed();

  // Arithmetical operations ///////////

  DoubleArray times(DoubleArray other);

  /**
   * Element wise multiplication. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.times(alpha).times(other.times(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  DoubleArray times(double alpha, DoubleArray other, double beta);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  DoubleArray times(double scalar);

  DoubleArray plus(DoubleArray other);

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  DoubleArray plus(double scalar);

  default DoubleArray plusAssign(DoubleArray other) {
    assign(other, (a, b) -> a + b);
    return this;
  }

  default DoubleArray plusAssign(double scalar) {
    update(v -> v + scalar);
    return this;
  }

  /**
   * Element wise addition. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.times(alpha).plus(other.times(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  DoubleArray plus(double alpha, DoubleArray other, double beta);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param scalar the scalar
   * @return r r
   */
  DoubleArray minus(double scalar);

  DoubleArray minus(DoubleArray other);

  /**
   * Element wise subtraction. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.times(alpha).minus(other.times(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  DoubleArray minus(double alpha, DoubleArray other, double beta);

  /**
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  DoubleArray reverseMinus(double scalar);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  DoubleArray div(double other);

  DoubleArray div(DoubleArray other);

  default DoubleArray divAssign(DoubleArray other) {
    assign(other, (x, y) -> x / y);
    return this;
  }

  default DoubleArray divAssign(double value) {
    update(v -> v / value);
    return this;
  }

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  DoubleArray reverseDiv(double other);

  default DoubleArray reverseDivAssign(double other) {
    update(v -> other / v);
    return this;
  }

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  DoubleArray negate();

  default BooleanArray gt(double v) {
    return where(x -> x > v);
  }

  default BooleanArray gte(double v) {
    return where(x -> x >= v);
  }

  default BooleanArray lt(double v) {
    return where(x -> x < v);
  }

  default BooleanArray lte(double v) {
    return where(x -> x <= v);
  }

  default BooleanArray eq(double v) {
    return where(x -> x == v);
  }

  default BooleanArray neq(double v) {
    return where(x -> x != v);
  }

  /**
   * Returns a double array representation of this matrix. If {@linkplain #isView()} is {@code true}
   * , a copy is returned.
   *
   * @return a double array
   */
  double[] data();
}
