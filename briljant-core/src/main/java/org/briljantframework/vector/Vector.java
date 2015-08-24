/*
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

package org.briljantframework.vector;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.array.Array;
import org.briljantframework.array.BitArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.LongArray;
import org.briljantframework.dataframe.SortOrder;
import org.briljantframework.exceptions.IllegalTypeException;
import org.briljantframework.index.Index;
import org.briljantframework.index.VectorLocationGetter;
import org.briljantframework.index.VectorLocationSetter;
import org.briljantframework.io.DataEntry;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * <p> A vector is an homogeneous (i.e. with values of only one (sub)type) and immutable (i.e. the
 * contents cannot change) list (with O(1) access) of values supporting missing entries (i.e. NA).
 *
 * <p> Since NA value are implemented differently depending value type, checking for NA-values are
 * done via the {@link #isNA(int)} method. For the default types, the {@link Is#NA} is available.
 *
 * <p> Implementers must ensure that:
 *
 * <ul>
 * <li>{@link #hashCode()} and {@link #equals(Object)} work as expected.</li>
 * <li>The vector cannot be changed, i.e. a vector cannot expose it's underlying
 * implementation and be mutated. This simplifies parallel algorithms.</li>
 * </ul>
 *
 * @author Isak Karlsson
 */
public interface Vector extends Serializable {

  /**
   * Construct a vector of values. The type of vector is inferred from the values.
   *
   * @param array the values
   * @param <T>   the type
   * @return a new vector, inferred from {@code T}
   */
  @SafeVarargs
  static <T> Vector of(T... array) {
    return of(Arrays.asList(array));
  }

  static <T> Vector of(Iterable<T> values) {
    Iterator<T> it = values.iterator();
    if (!it.hasNext()) {
      return singleton(null);
    }
    T t = it.next();
    Builder builder = VectorType.from(t).newBuilder().add(t);
    while (it.hasNext()) {
      builder.add(it.next());
    }
    return builder.build();
  }

  static <T> Vector of(Supplier<T> supplier, int size) {
    if (size < 1) {
      throw new UnsupportedOperationException();
    }
    T value = supplier.get();
    Vector.Builder builder = VectorType.from(value).newBuilder().add(value);
    for (int i = 1; i < size; i++) {
      builder.add(supplier.get());
    }
    return builder.build();
  }

  static Vector singleton(Object value, int size) {
    return new SingletonVector(value, size);
  }

  static Vector singleton(Object value) {
    return singleton(value, 1);
  }

  static Vector empty() {
    return SingletonVector.empty();
  }

  /**
   * Construct a new vector of truth values based on the supplied predicate.
   *
   * <p>
   * <pre>{@code
   *  Vector a = ..;
   *  Vector b = ..;
   *  Vector.Builder builder = Vector.Builder.of(Boolean.class);
   *  for(int i = 0; Math.min(a.size(), b.size()); i++){
   *     builder.add(predicate.test(a.get(cls, i), b.get(cls, i));
   *  }
   *  Vector c = builder.build();
   * }</pre>
   *
   * @param cls       the type
   * @param other     other vector
   * @param predicate the predicate to test
   * @return a new {@code BitVector} of values
   * @see #combine(Class, Class, Vector, java.util.function.BiFunction)
   */
  <T> Vector satisfies(Class<T> cls, Vector other, BiPredicate<T, T> predicate);

  <T> Vector satisfies(Class<? extends T> cls, Predicate<? super T> predicate);

  /**
   * Filter values in this vector, treating each value as {@code cls} (or NA), using the supplied
   * predicate.
   *
   * @param cls       the class
   * @param predicate the predicate
   * @param <T>       the type
   * @return a new vector with only values for which {@code predicate} returns true
   */
  <T> Vector filter(Class<T> cls, Predicate<T> predicate);

  /**
   * <p> Transform each value (as a value of T) in the vector using {@code operator}, producing a
   * new vector with values of type {@code O}.
   *
   * <p> Example:
   * <pre>{@code
   *  Random rand = new Random(123);
   *  > Vector a = Vector.of(rand::nextGaussian, 10);
   *  > Vector b = a.transform(Double.class, Long.class, Math::round);
   *  [-1, 1, 0, 0, 0, 0, 1, 0, 0, 1] type: long
   * }</pre>
   *
   * <p> Please note that transformations can be implemented in terms of aggregation operations.
   * For example, this method can be implemented as:
   *
   * <pre>{@code
   *  a.aggregate(in, Aggregates.transform(() -> Vec.typeOf(out).newBuilder, operator));
   * }</pre>
   *
   * @param in       the input type (if the vector cannot coerce values to {@code T}, NA is used)
   * @param out      the output type
   * @param operator the operator to apply
   * @param <T>      the input type (i.e. the type of values stored in {@code this})
   * @param <O>      the output type (i.e. the type of values in the resulting vector)
   * @return a new vector of type {@code O}
   */
  <T, O> Vector transform(Class<T> in, Class<O> out, Function<? super T, ? extends O> operator);

  /**
   * Transform each value (as a value of {@code T}) in the vector using {@code operator}, producing
   * a new vector with the type inferred from the first value returned by {@code operator}.
   *
   * <p> Example:
   * <pre>{@code
   * > Random rand = new Random();
   * > Vector a = Vector.of(rand::nextGaussian, 3);
   * 0     -0.862
   * 1     0.653
   * 2     0.836
   * 3     0.196
   * 4     0.554
   *
   * > Vector b = a.transform(Double.class, Math::round);
   * 0     -1.000
   * 1     1.000
   * 2     1.000
   * 3     1.000
   * 4     1.000
   * }</pre>
   *
   * @param cls      the input type
   * @param operator the operator
   * @param <T>      the input type
   * @return a new vector of type inferred by {@code operator}
   */
  <T> Vector transform(Class<T> cls, UnaryOperator<T> operator);

  /**
   * Performs a mutable aggregation of the values in this vector, similar to {@linkplain
   * Stream#collect(java.util.stream.Collector)}. A mutable aggregation performs its aggregation
   * by mutating and adding values to an aggregation container such as a {@linkplain List list}.
   *
   * <p> The result produced is equivalent to:
   *
   * <pre>{@code
   * T container = collector.supplier();
   * for(int i = 0; i < size(); i++) {
   *  collector.accumulator().accept(container, get(in, i));
   * }
   * return collector.finisher().apply(container);
   * }</pre>
   *
   * <p> Example:
   *
   * <pre>{@code
   * > RealDistribution normal = new NormalDistribution()
   * > Vector vector = Vector.of(normal::sample, 1000)
   * 0     -0.862
   * 1     0.653
   * 2     0.836
   * 3     0.196
   * 4     0.554
   * 5     1.388
   * 6     -0.992
   * 7     -0.453
   * 8     0.283
   * ...
   * 991   -0.778
   * 992   -0.043
   * 993   -0.288
   * 994   0.184
   * 995   -0.524
   * 996   -0.391
   * 997   0.553
   * 998   -0.856
   * 999   -0.055
   * type: double
   *
   * > double mean = vector.collect(Double.class, Aggregates.mean());
   * > Vector summary = vector.collect(Double.class, Aggregate.summary());
   * type: double
   * mean  0.029
   * sum   28.714
   * std   1.008
   * var   1.016
   * min   -3.056
   * max   3.589
   * n     1000.000
   * type: double
   * }</pre>
   *
   * <pre>{@code
   * > Vector names = Vector.of("Mary", "Bob", "Lisa");
   * 0  Mary
   * 1  Bob
   * 2  Lisa
   * type: string
   *
   * > names.repeat(Collectors.repeat(2));
   * 0  Mary
   * 1  Bob
   * 2  Lisa
   * 3  Mary
   * 4  Bob
   * 5  Lisa
   * type: string
   * }</pre>
   *
   * @param <T>       the type of the input value to the mutable aggregation
   * @param <R>       the type of the mutable collector
   * @param <C>       the type of the return type of the aggregation
   * @param in        the input type
   * @param collector the collector
   * @return a value of type {@code R} (i.e. the result of the aggregation)
   * @see java.util.stream.Collector
   * @see java.util.stream.Stream#collect(java.util.stream.Collector)
   */
  <T, R, C> R collect(Class<? extends T> in, Collector<? super T, C, ? extends R> collector);

  /**
   * Example:
   *
   * <pre>{@code
   *  ArrayList<Double> list = Vector.of(1,2,3).aggregate(Double.class,
   *    ArrayList::new, ArrayList::add);
   * }</pre>
   *
   * @param in       the input class
   * @param supplier the mutable container
   * @param consumer the update function
   * @param <T>      the input type
   * @param <R>      the result type
   * @return a value of type {@code R}
   */
  <T, R> R collect(Class<? extends T> in, Supplier<R> supplier, BiConsumer<R, ? super T> consumer);

  <R> R collect(Collector<? super Object, ?, R> collector);

  <T, R> Vector combine(Class<? extends T> in, Class<? extends R> out, Vector other,
                        BiFunction<? super T, ? super T, ? extends R> combiner);

  <T> Vector combine(Class<T> cls, Vector other,
                     BiFunction<? super T, ? super T, ? extends T> combiner);

  default Vector add(Vector other) {
    return combine(Object.class, other, Combine.add());
  }

  default Vector add(Object other) {
    return add(singleton(other, size()));
  }

  default Vector mul(Vector other) {
    return combine(Object.class, other, Combine.mul());
  }

  default Vector mul(Number other) {
    return mul(singleton(other, size()));
  }

  default Vector div(Vector other) {
    return combine(Object.class, other, Combine.div());
  }

  default Vector div(Number other) {
    return div(singleton(other, size()));
  }

  default Vector rdiv(Number other) {
    return singleton(other, size()).combine(Object.class, this, Combine.div());
  }

  default Vector sub(Vector other) {
    return combine(Object.class, other, Combine.sub());
  }

  default Vector sub(Number other) {
    return sub(singleton(other, size()));
  }

  default Vector rsub(Number other) {
    return singleton(other, size()).combine(Object.class, this, Combine.sub());
  }

  Vector sort(SortOrder order);

  <T> Vector sort(Class<T> cls, Comparator<T> cmp);

  <T extends Comparable<T>> Vector sort(Class<T> cls);

  /**
   * Return a vector of the {@code n} first elements
   *
   * <p> Example
   * <pre>{@code
   * > Vector v = Vector.of(1, 2, 3, 4);
   * 0  1
   * 1  2
   * 2  3
   * 3  4
   * type: int
   *
   * > v.head(2);
   * 0  1
   * 1  2
   * }</pre>
   *
   * @param n the first elements
   * @return the n first elements
   */
  Vector head(int n);

  /**
   * Returns a vector of the {@code n} last elements
   * <p>Example
   * <pre>{@code
   * > Vector v = Vector.of(1, 2, 3, 4);
   * 0  1
   * 1  2
   * 2  3
   * 3  4
   *
   * > v.tail(1);
   * 0  2
   * 1  3
   * 2  4
   * }</pre>
   */
  Vector tail(int n);

  /**
   * Get the index of this vector
   *
   * @return the index of this vector
   */
  Index getIndex();

  /**
   * Set the index of this vector. The size of the index must equal the size of the vector.
   *
   * @param index the index
   */
  void setIndex(Index index);

//  <T> T get(Class<T> cls, int index);

  /**
   * Same as {@code loc().get(cls, getIndex().getLocation(key))}
   *
   * @see org.briljantframework.index.VectorLocationGetter#get(Class, int)
   */
  <T> T get(Class<T> cls, Object key);

  double getAsDouble(Object key);

  int getAsInt(Object key);

  String toString(Object key);

  /**
   * Returns true there ara any NA values
   *
   * @return true or false
   */
  boolean hasNA();

  boolean isNA(Object key);

  boolean isTrue(Object key);

  Vector select(Vector bits);

  /**
   * Returns the size of the vector
   *
   * @return size
   */
  int size();

  /**
   * Get the type of the vector.
   *
   * @return the type
   */
  VectorType getType();

  /**
   * Get type of value at {@code index}
   *
   * @param index the index
   * @return the type of value
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > size()}
   */
  VectorType getType(int index);

  /**
   * For values implementing {@link java.lang.Comparable}, {@link org.briljantframework.vector.Scale#NUMERICAL}
   * should be returned; otherwise {@link org.briljantframework.vector.Scale#NOMINAL}.
   */
  Scale getScale();

  /**
   * Creates a new builder able to build new vectors of this type, initialized with the values in
   * this builder. <p>
   *
   * <pre>
   * Vector vec = vector.newCopyBuilder().add("Hello world")
   * assert vec.size() == vector.size() + 1
   * assert vec.getAsString(0) == vector.getAsString(0)
   * </pre>
   *
   * @return a new builder
   */
  Builder newCopyBuilder();

  /**
   * Creates a new builder able to build vectors of this type
   *
   * @return a new builder
   */
  Builder newBuilder();

  /**
   * Creates a new builder able to build vectors of this type. The constructed builder produces a
   * vector of length {@code size}, filled with NA.
   *
   * @param size the initial size
   * @return a new builder
   */
  Builder newBuilder(int size);

  <T> List<T> asList(Class<T> cls);

  <T> Stream<T> stream(Class<T> cls);

  <T> Stream<T> parallelStream(Class<T> cls);

  IntStream intStream();

  DoubleStream doubleStream();

  LongStream longStream();

  /**
   * <p>Copies this vector to a {@link org.briljantframework.array.Array}. An appropriate
   * specialization of the {@link org.briljantframework.array.BaseArray} interface should be
   * preferred. For example, a {@link org.briljantframework.vector.DoubleVector} should return a
   * {@link org.briljantframework.array.DoubleArray} implementation.
   *
   * <pre>{@code
   * Vector a = new DoubleVector(1, 2, 3, 4, 5);
   * DoubleMatrix mat = a.toArray(Double.class).asDouble();
   * double sum = mat.reduce(0, Double::sum);
   * }</pre>
   *
   * @return this vector as an {@linkplain org.briljantframework.array.Array array}
   */
  <U> Array<U> toArray(Class<U> cls) throws IllegalTypeException;

  /**
   * The default implementation is equivalent to calling {@code toArray(Double.class).asDouble()}.
   *
   * @see #toArray(Class)
   */
  default DoubleArray toDoubleArray() throws IllegalTypeException {
    return toArray(Double.class).asDouble();
  }

  /**
   * The default implementation is equivalent to calling {@code toArray(Complex.class).asComplex()}.
   *
   * @see #toArray(Class)
   */
  default ComplexArray toComplexArray() throws IllegalTypeException {
    return toArray(Complex.class).asComplex();
  }

  /**
   * The default implementation is equivalent to calling {@code toArray(Long.class).asLong()}.
   *
   * @see #toArray(Class)
   */
  default LongArray toLongArray() throws IllegalTypeException {
    return toArray(Long.class).asLong();
  }

  /**
   * The default implementation is equivalent to calling {@code toArray(Boolean.class).asBit()}.
   *
   * @see #toArray(Class)
   */
  default BitArray toBitArray() throws IllegalTypeException {
    return toArray(Boolean.class).asBit();
  }

  /**
   * The default implementation is equivalent to calling {@code toArray(Integer.class).asInt()}.
   *
   * @see #toArray(Class)
   */
  default IntArray toIntArray() throws IllegalTypeException {
    return toArray(Integer.class).asInt();
  }

  VectorLocationGetter loc();

  /**
   * <p> Builds a new vector. A builder can incrementally grow, but not allow gaps. For example, if
   * a builder is initialized with size {@code 8}, {@link #add(Object)} (et. al.) adds a value at
   * index {@code 8} and indexes {@code 0-7} have the value {@code NA}. If the value at index
   * {@code 11} is set, values {@code 9, 10} are set to {@code NA}. </p>
   *
   * <p> When transferring values between vectors, prefer {@link #set(Object, Vector, int)} to
   * {@link
   * #set(Object, Object)}. For example, {@code Vector.Builder a; Vector b; a.set(0, b, 10)} sets
   * the
   * value of {@code a} at index {@code 0} to the value at index {@code 10} in {@code b}. This
   * avoids unboxing values from one vector to another. For the numerical vectors, values are
   * coerced, e.g. {@code 1} from an int-vector becomes {@code 1.0} in a double vector or {@code
   * Bit.TRUE} in a bit-vector. </p>
   */
  public static interface Builder {

    /**
     * Recommended initial capacity
     */
    int INITIAL_CAPACITY = 50;

    Builder setNA(Object key);

    /**
     * @return a modified builder
     */
    Builder addNA();

    /**
     * Same as {@code add(size(), from, fromIndex)}
     *
     * @param from      the vector to take the value from
     * @param fromIndex the index
     * @return a modified builder
     */
    Builder add(Vector from, int fromIndex);

    Builder add(Vector from, Object key);

    Builder set(Object atKey, Vector from, int fromIndex);

    Builder set(Object atKey, Vector from, Object fromIndex);

    Builder set(Object key, Object value);

    /**
     * Same as {@code add(size(), value)}
     *
     * @param value the value
     * @return a modified builder
     */
    Builder add(Object value);

    Builder add(double value);

    Builder add(int value);

    /**
     * Add all values in {@code from} to this builder.
     *
     * @param from the vector
     * @return a modified builder
     */
    Builder addAll(Vector from);

    default Builder addAll(Object... objects) {
      return addAll(Arrays.asList(objects));
    }

    default Builder addAll(Vector.Builder builder) {
      return addAll(builder.getTemporaryVector());
    }

    /**
     * Add all values in iterable
     *
     * @param iterable the collection of values
     */
    default Builder addAll(Iterable<?> iterable) {
      iterable.forEach(this::add);
      return this;
    }

    Builder remove(Object key);

    default Builder readAll(DataEntry entry) throws IOException {
      while (entry.hasNext()) {
        read(entry);
      }
      return this;
    }

    VectorLocationSetter loc();

    /**
     * Reads a value from the input stream and appends it to the builder (after the last value).
     *
     * @param entry the input stream
     * @return receiver modified
     * @throws IOException if {@code inputStream} fail
     */
    Builder read(DataEntry entry) throws IOException;

    /**
     * Reads a value from the input stream and set {@code index} to the next value in the stream.
     *
     * @param index the index
     * @param entry the input stream
     * @return receiver modified
     * @throws IOException if {@code inputStream} fail
     */
    Builder read(int index, DataEntry entry) throws IOException;

    /**
     * Returns the size of the resulting vector
     *
     * @return the size
     */
    int size();

    /**
     * Returns a temporary vector. Modifications to the builder is propagated to the temporary
     * vector, allowing changes to be tracked within the builder.
     *
     * @return the temporary vector.
     */
    Vector getTemporaryVector();

    /**
     * Create a new vector of suitable type. This interface does not provide any guarantees to
     * whether or not it is possible to construct several vectors using the same builder. Most
     * commonly, once {@code build()} has been called, subsequent calls on the builder will fail.
     *
     * @return a new vector
     */
    Vector build();
  }


}
