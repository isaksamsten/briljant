/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
import org.briljantframework.Check;
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

  static DoubleArray ones(int... shape) {
    return Arrays.ones(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newVector(double[])
   */
  static DoubleArray of(double... data) {
    return Arrays.newDoubleVector(data);
  }

  /**
   * Constructs a double array of values in the range [start, end[ with the specified step-value
   * between consecutive values
   *
   * @param start start value (inclusive)
   * @param end end value (exclusive)
   * @param step step size
   * @return a new double array
   */
  static DoubleArray range(double start, double end, double step) {
    Check.argument(step > 0, "Illegal step size");
    Check.argument(start < end, "Illegal start");
    int size = (int) Math.round((end - start) / step);
    Check.argument(size >= 0, "Illegal range");
    DoubleArray array = zeros(size);
    double v = start;
    for (int i = 0; i < size; i++) {
      array.set(i, v);
      v += step;
    }
    return array;
  }

  static DoubleArray zeros(int... shape) {
    return Arrays.newDoubleArray(shape);
  }

  void set(int index, double value);

  /**
   * @see Arrays#linspace(double, double, int)
   */
  static DoubleArray linspace(double start, double end, int size) {
    return Arrays.linspace(start, end, size);
  }

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   */
  void assign(double value);

  /**
   * Assign the array
   *
   * @param array the array
   */
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

  void assign(IntArray matrix, IntToDoubleFunction function);

  void assign(LongArray matrix, LongToDoubleFunction function);

  void assign(ComplexArray matrix, ToDoubleFunction<? super Complex> function);

  /**
   * Combine this with the given array assigning the results
   *
   * @param array the other array
   * @param combine the combiner
   */
  void combineAssign(DoubleArray array, DoubleBinaryOperator combine);

  /**
   * Combine this with the given array resulting in a new array
   *
   * @param array the array
   * @param combine the combiner
   * @return a new array
   */
  DoubleArray combine(DoubleArray array, DoubleBinaryOperator combine);

  /**
   * Collect the array
   *
   * @param collector the collector
   * @param <R> the return type
   * @param <C> the mutable reduction container
   * @return an instance of R
   */
  <R, C> R collect(Collector<? super Double, C, R> collector);

  // Transform

  /**
   * Collect the array
   *
   * @param supplier the supplier
   * @param consumer the consumer
   * @param <R> the return type
   * @return an instance of R
   */
  <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> consumer);

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

  /**
   * Map each value to a long
   *
   * @param function the mapper
   * @return a long array
   */
  LongArray mapToLong(DoubleToLongFunction function);

  /**
   * Map each value to a complex
   *
   * @param function the mapper
   * @return a complex array
   */
  ComplexArray mapToComplex(DoubleFunction<Complex> function);

  /**
   * Map each value to an object
   *
   * @param mapper the mapper
   * @param <T> the type
   * @return an array
   */
  <T> Array<T> mapToObj(DoubleFunction<? extends T> mapper);

  // Filter

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   */
  void apply(DoubleUnaryOperator operator);

  /**
   * Return the value for which the predicate returns true
   *
   * @param predicate the predicate
   * @return a new double array
   */
  DoubleArray filter(DoublePredicate predicate);

  /**
   * Return a boolean array of indicator values for joining this with the given array and the
   * predicate
   * 
   * @param array the array
   * @param predicate the predicate
   * @return a boolean array
   */
  BooleanArray where(DoubleArray array, DoubleBiPredicate predicate);

  /**
   * For each double perform the side-effect
   * 
   * @param consumer the consumer
   */
  void forEachDouble(DoubleConsumer consumer);

  // Reduce

  /**
   * Successively apply the given function over the identity and each value
   * 
   * <pre>
   * DoubleArray.of(1,2,3).reduce(0, Double::sum));
   * </pre>
   * 
   * The first argument to the reduce operator is the initial value (and then the accumulator)
   * 
   * @param identity the initial value
   * @param reduce the operator
   * @return a single value
   */
  double reduce(double identity, DoubleBinaryOperator reduce);

  /**
   * Perform a reduction over all vectors along the given dimension
   * 
   * <pre>
   * DoubleArray.of(1,2,3,4).reshape(2,2).reduceVectors(0, Double::sum));
   * </pre>
   * 
   * sums each row
   * 
   * @param dim the dimension
   * @param reduce the reduction
   * @return a new array
   */
  DoubleArray reduceVectors(int dim, ToDoubleFunction<? super DoubleArray> reduce);

  // GET SET

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

  /**
   * Return a double stream
   *
   * @return a double stream
   */
  DoubleStream stream();

  /**
   * Convert this array to a (mutable) list.
   *
   * @return a list
   */
  List<Double> toList();

  /**
   * Provides a lazy view of this {@code double} array as it's boxed counterpart.
   *
   * @return a boxed view
   */
  Array<Double> boxed();

  DoubleArray times(DoubleArray other);

  // Arithmetical operations ///////////

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

  void timesAssign(double scalar);

  void timesAssign(DoubleArray array);

  DoubleArray plus(DoubleArray other);

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  DoubleArray plus(double scalar);

  void plusAssign(DoubleArray other);

  void plusAssign(double scalar);

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

  void minusAssign(double scalar);

  void minusAssign(DoubleArray scalar);

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

  void reverseMinusAssign(double scalar);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  DoubleArray div(double other);

  DoubleArray div(DoubleArray other);

  void divAssign(DoubleArray other);

  void divAssign(double value);

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  DoubleArray reverseDiv(double other);

  void reverseDivAssign(double other);

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  DoubleArray negate();

  default BooleanArray gt(double v) {
    return where(x -> x > v);
  }

  /**
   * Return a boolean array of indicator values using the given predicate
   *
   * @param predicate the predicate
   * @return a boolean array
   */
  BooleanArray where(DoublePredicate predicate);

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
