/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
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

import java.util.Collection;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;

import net.mintern.primitive.comparators.DoubleComparator;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.DoubleSequence;
import org.briljantframework.function.DoubleBiPredicate;

/**
 * A n-dimensional array of double values.
 *
 * @author Isak Karlsson
 */
public interface DoubleArray
    extends BaseArray<Double, DoubleArray>, Iterable<Double>, DoubleSequence {

  static DoubleArray ones(int... shape) {
    return Arrays.ones(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newDoubleVector(double[])
   */
  static DoubleArray of(double... data) {
    return Arrays.doubleVector(data);
  }

  /**
   * Returns a double array consisting of the given elements, in order.
   *
   * @param elements the elements
   * @return a new double array
   */
  static DoubleArray copyOf(Collection<? extends Number> elements) {
    DoubleArray a = DoubleArray.zeros(elements.size());
    int i = 0;
    for (Number element : elements) {
      a.set(i++, element.doubleValue());
    }
    return a;
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
    return Arrays.doubleArray(shape);
  }

  /**
   * @see Arrays#linspace(double, double, int)
   */
  static DoubleArray linspace(double start, double end, int size) {
    return Arrays.linspace(start, end, size);
  }

  void set(int index, double value);

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
   * @param array the matrix
   * @param operator the operator
   */
  void assign(DoubleArray array, DoubleUnaryOperator operator);

  void assign(IntArray array, IntToDoubleFunction function);

  void assign(LongArray array, LongToDoubleFunction function);

  void assign(ComplexArray array, ToDoubleFunction<? super Complex> function);

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
   * Set the elements where the given array is {@code true} to the given value
   * <p/>
   * Example
   *
   * <pre>
   * DoubleArray a = Arrays.range(3 * 3).reshape(3, 3).asDouble();
   * DoubleArray b = Arrays.newDoubleArray(3, 3);
   * b.assign(0.1);
   * b.set(a.where(i -&gt; i &gt; 2), 10);
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([[0.1, 10.0, 10.0],
   *        [0.1, 10.0, 10.0],
   *        [0.1, 10.0, 10.0]])
   * </pre>
   *
   * @param array the array
   * @param value the value
   */
  void set(BooleanArray array, double value);

  /**
   * Get the elements where the given arra is {@code true}.
   *
   * <p/>
   * Example
   *
   * <pre>
   * DoubleArray a = Arrays.range(3 * 3).reshape(3, 3).asDouble()
   * a.get(a.where(i -&gt; i &gt; 2));
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([3.0, 4.0, 5.0, 6.0, 7.0, 8.0])
   * </pre>
   *
   * @param array the array
   * @return a new array
   */
  DoubleArray get(BooleanArray array);

  default void sort() {
    sort(Double::compare);
  }

  void sort(DoubleComparator comparator);

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

  DoubleArray times(DoubleArray other);

  // Arithmetical operations ///////////

  /**
   * Element wise multiplication.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @return a new matrix
   */
  DoubleArray times(double alpha, DoubleArray other);

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
   * Element wise addition.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @return a new matrix
   */
  DoubleArray plus(double alpha, DoubleArray other);

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
   * @return a new matrix
   */
  DoubleArray minus(double alpha, DoubleArray other);

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

  @Override
  default double getAsDouble(int index) {
    return get(index);
  }

  /**
   * Returns a double array representation of this matrix. If {@linkplain #isView()} is {@code true}
   * , a copy is returned.
   *
   * @return a double array
   */
  double[] data();
}
