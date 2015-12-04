/**
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
package org.briljantframework.data.vector;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.Listable;
import org.briljantframework.array.Array;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.data.Collectors;
import org.briljantframework.data.Na;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.VectorLocationGetter;
import org.briljantframework.data.index.VectorLocationSetter;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.exceptions.IllegalTypeException;

/**
 * A vector is an homogeneous (i.e. with values of only one (minus)type) and immutable (i.e. the
 * contents cannot change) list/map (with O(1) location-based access and key-based access) of values
 * supporting missing entries (i.e. NA).
 *
 * <p/>
 * Since NA value are implemented differently depending value type, checking for NA-values are done
 * via the {@link #isNA(Object)} method or via the static
 * {@link org.briljantframework.data.Is#NA(java.lang.Object)} method.
 *
 * <p/>
 * <strong>Note:</strong>
 *
 * <p>
 * Implementers must ensure that:
 *
 * <ul>
 * <li>{@link #hashCode()} and {@link #equals(Object)} are based on the values and the index.</li>
 * <li>The vector cannot be changed, i.e. a vector cannot expose it's underlying implementation and
 * be mutated. This simplifies parallel algorithms.</li>
 * </ul>
 *
 * @author Isak Karlsson
 */
public interface Vector extends Serializable, Listable<Object> {

  /**
   * Construct a vector of values. The type of vector is inferred from the values.
   *
   * @param array the values
   * @return a new vector
   */
  @SafeVarargs
  static <T> Vector of(T... array) {
    Vector.Builder builder = Vector.Builder.of(array.getClass().getComponentType());
    builder.addAll(array);
    return builder.build();
  }

  /**
   * Creates an empty vector
   *
   * @return an empty vector
   */
  static Vector empty() {
    return SingletonVector.empty();
  }

  static Vector fromIterable(Iterable<Object> values) {
    return fromIterable(Object.class, values);
  }

  /**
   * Creates a vector of the values in the iterable
   *
   * @param values the specified values
   * @return a new vector with the specified values
   */
  static <T> Vector fromIterable(Class<T> cls, Iterable<T> values) {
    Iterator<T> it = values.iterator();
    if (!it.hasNext()) {
      return singleton(null);
    }

    Vector.Builder builder = new GenericVector.Builder(cls);
    while (it.hasNext()) {
      builder.add(it.next());
    }
    return builder.build();
  }

  /**
   * Creates a one element vector with the specified value
   *
   * @param value the value
   * @return a one element vector
   */
  static Vector singleton(Object value) {
    return singleton(value, 1);
  }

  /**
   * Creates a vector with the specified value and size
   *
   * @param value the value
   * @param size the size of the vector
   * @return a new vector
   */
  static Vector singleton(Object value, int size) {
    return new SingletonVector(value, size);
  }

  /**
   * Creates a vector with the specified size consisting of the values given by the supplier
   *
   * <pre>
   * {
   *   &#064;code
   *   Vector.Builder b;
   *   for (int i = 0; i &lt; size; i++) {
   *     b.plus(supplier.get());
   *   }
   * }
   * </pre>
   */
  static Vector fromSupplier(Supplier<Object> supplier, int size) {
    if (size < 1) {
      throw new UnsupportedOperationException();
    }
    Object value = supplier.get();
    Vector.Builder builder = VectorType.of(value).newBuilder().add(value);
    for (int i = 1; i < size; i++) {
      builder.add(supplier.get());
    }
    return builder.build();
  }

  /**
   * Returns a boolean array of the elements for which the predicate return {@code true}.
   * 
   * @param predicate the predicate
   * @return a boolean vector
   * @see #where(Class, Predicate)
   */
  default BooleanArray where(Predicate<? super Object> predicate) {
    return where(Object.class, predicate);
  }

  /**
   * Return a boolean array of the elements for which the predicate returns {@code true}
   * 
   * @param <T> the type
   * @param cls the type
   * @param predicate the predicate
   * @return a boolean vector
   */
  <T> BooleanArray where(Class<T> cls, Predicate<? super T> predicate);

  default Vector filter(Predicate<? super Object> predicate) {
    return filter(Object.class, predicate);
  }

  /**
   * Filter values in this vector, treating each value as {@code cls} (or NA), using the supplied
   * predicate.
   *
   * @param <T> the type
   * @param cls the class
   * @param predicate the predicate
   * @return a new vector with only values for which {@code predicate} returns true
   */
  <T> Vector filter(Class<T> cls, Predicate<? super T> predicate);

  <T> Vector filterWithIndex(Class<T> cls, BiPredicate<Object, ? super T> predicate);

  default Vector map(Function<Object, ?> operator) {
    return map(Object.class, operator);
  }

  /**
   * Transform each value (as a value of {@code T}) in the vector using {@code operator}, producing
   * a new vector with the type inferred from the first value returned by {@code operator}.
   *
   * <p>
   * Example:
   * 
   * <pre>
   * {@code
   * > Random rand = new Random();
   * > Vector a = Vector.of(rand::nextGaussian, 3);
   * 0     -0.862
   * 1     0.653
   * 2     0.836
   * 3     0.196
   * 4     0.554
   * 
   * > Vector b = a.map(Double.class, Math::round);
   * 0     -1
   * 1     1
   * 2     1
   * 3     1
   * 4     1
   * }
   * </pre>
   *
   * @param <T> the input type
   * @param cls the input type
   * @param operator the operator
   * @return a new vector of type inferred by {@code operator}
   */
  <T> Vector map(Class<T> cls, Function<? super T, ?> operator);

  <T> Vector mapWithIndex(Class<T> cls, BiFunction<Object, ? super T, ?> operator);

  Vector combine(Vector other, BiFunction<? super Object, ? super Object, ?> combiner);

  default Vector plus(Object other) {
    return plus(singleton(other, size()));
  }

  default Vector plus(Vector other) {
    return combine(Object.class, other, Combine.add());
  }

  /**
   * Combine two vectors using the specified combination function. For example, concatenating two
   * string vectors, or adding two numerical vectors.
   *
   * <pre>
   * Vector a = Vector.of(1, 2, 3, 4);
   * Vector b = Vector.of(1, 2, 3, 4);
   * a.combine(String.class, b, (x, y) -&gt; x + y).map(String.class, String::length);
   * </pre>
   *
   * @param cls the class
   * @param other the other vector
   * @param combiner the combiner
   * @param <T> a type
   * @return a new vector
   */
  <T> Vector combine(Class<T> cls, Vector other,
      BiFunction<? super T, ? super T, ? extends T> combiner);

  /**
   * Returns the size of the vector
   *
   * @return size
   */
  int size();

  default Vector times(Number other) {
    return times(singleton(other, size()));
  }

  default Vector times(Vector other) {
    return combine(Object.class, other, Combine.mul());
  }

  default Vector div(Number other) {
    return div(singleton(other, size()));
  }

  default Vector div(Vector other) {
    return combine(Object.class, other, Combine.div());
  }

  default Vector reverseDiv(Number other) {
    return singleton(other, size()).combine(Object.class, this, Combine.div());
  }

  default Vector minus(Number other) {
    return minus(singleton(other, size()));
  }

  default Vector minus(Vector other) {
    return combine(Object.class, other, Combine.sub());
  }

  default Vector reverseMinus(Number other) {
    return singleton(other, size()).combine(Object.class, this, Combine.sub());
  }

  /**
   * Sort the vector in its <i>natural order</i> in ascending or descending order
   *
   * <p>
   * The sort order is specified by the implementation
   *
   * @param order the specified order
   * @return the vector sorted
   */
  Vector sort(SortOrder order);

  /**
   * Sort the vector using the the specified comparator and the defined type
   *
   * @param cls the type of elements
   * @param cmp the comparator
   * @return a new sorted vector
   */
  <T> Vector sort(Class<T> cls, Comparator<T> cmp);

  /**
   * Sort the vector according to the natural sort order of the specified comparable
   *
   * @param cls the comparable type
   * @return a new vector sorted
   */
  <T extends Comparable<T>> Vector sort(Class<T> cls);

  /**
   * Return a vector of the {@code n} first elements
   *
   * <p>
   * Example
   * 
   * <pre>
   * {@code
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
   * }
   * </pre>
   *
   * @param n the first elements
   * @return the n first elements
   */
  Vector head(int n);

  /**
   * Returns a vector of the {@code n} last elements
   * <p>
   * Example
   * 
   * <pre>
   * {@code
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
   * }
   * </pre>
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

  /**
   * Get the object with the specified key as an object
   * 
   * @param key the key
   * @return an object
   */
  default Object get(Object key) {
    return get(Object.class, key);
  }

  /**
   * Get the value with the given key as an instance of the specified type
   * 
   * @param cls the type
   * @param key the key
   * @param <T> the type
   * @return the value with the given key
   * @see Convert#to(Class, Object)
   */
  <T> T get(Class<T> cls, Object key);

  /**
   * Get the value with the given key as a double
   * 
   * @param key the key
   * @return a double
   */
  double getAsDouble(Object key);

  /**
   * Get the value witht the given key as an int
   * 
   * @param key the key
   * @return an int
   */
  int getAsInt(Object key);

  double getAsDouble(Object key, double defaultValue);

  /**
   * Select a subset of this vector for which the keys of this vector is true in the given vector
   *
   * @param select a vector of truth values
   * @return a subset of this vector
   */
  Vector get(BooleanArray select);

  /**
   * Return a new vector with the specified element set to the specified value
   * 
   * @param key the key
   * @param value the valye
   * @return a new vector
   */
  Vector set(Object key, Object value);

  /**
   * Set the positions for which this array return true to the specified value
   * 
   * @param array the array of boolean values
   * @param value the value
   * @return a new vector
   */
  Vector set(BooleanArray array, Object value);

  /**
   * Returns true if there are any NA values
   *
   * @return true or false
   */
  boolean hasNA();

  /**
   * Return true if the value with the given key is {@code NA}
   *
   * @param key the key
   * @return true if the value is {@code NA}
   */
  boolean isNA(Object key);

  int compare(Object a, Object b);

  /**
   * Returns true if the vector is empty
   * 
   * @return true if the vector is empty
   */
  default boolean isEmpty() {
    return size() == 0;
  }

  /**
   * Get the type of the vector.
   *
   * @return the type
   */
  VectorType getType();

  <T> Set<Pair<Object, T>> indexSet(Class<T> cls);

  Vector copy();

  default <T> Listable<T> toListable(Class<T> cls) {
    return () -> toList(cls);
  }

  <T> List<T> toList(Class<T> cls);

  default List<Object> toList() {
    return toList(Object.class);
  }

  <T> Stream<T> stream(Class<T> cls);

  IntStream intStream();

  DoubleStream doubleStream();

  LongStream longStream();

  /**
   * Copies the contents of this vector to the given array.
   * 
   * @param cls the type
   * @param array the array
   * @param <U> the type
   */
  <U> void toArray(Class<U> cls, Array<U> array);

  /**
   * Copies the contents of this vector to the given array.
   *
   * @param array the array
   */
  void toArray(Array<Object> array);

  /**
   * Copies the contents of this vector to the given array.
   *
   * @param array the array
   */
  void toArray(DoubleArray array);

  /**
   * Copies the contents of this vector to the given array.
   *
   * @param array the array
   */
  void toArray(IntArray array);

  /**
   * Copies the contents of this vector to the given array.
   *
   * @param array the array
   */
  void toArray(ComplexArray array);

  /**
   * The default implementation is equivalent to calling {@code toArray(Double.class).asDouble()}.
   *
   * @see #toArray(Class)
   */
  default DoubleArray toDoubleArray() throws IllegalTypeException {
    return toArray(Double.class).asDouble();
  }

  /**
   * <p>
   * Copies this vector to a {@link org.briljantframework.array.Array}. An appropriate
   * specialization of the {@link org.briljantframework.array.BaseArray} interface should be
   * preferred. For example, a {@link org.briljantframework.data.vector.DoubleVector} should return
   * a {@link org.briljantframework.array.DoubleArray} implementation.
   *
   * <pre>
   * Vector a = new DoubleVector(1, 2, 3, 4, 5);
   * DoubleMatrix mat = a.toArray(Double.class).asDouble();
   * double sum = mat.reduce(0, Double::sum);
   * </pre>
   *
   * @return this vector as an {@linkplain org.briljantframework.array.Array array}
   */
  <U> Array<U> toArray(Class<U> cls);

  /**
   * The default implementation is equivalent to calling {@code toArray(Integer.class).asInt()}.
   *
   * @see #toArray(Class)
   */
  default IntArray toIntArray() throws IllegalTypeException {
    return toArray(Integer.class).asInt();
  }

  /**
   * The default implementation is equivalent to calling {@code toArray(Complex.class).asComplex()}.
   *
   * @see #toArray(Class)
   */
  default ComplexArray toComplexArray() throws IllegalTypeException {
    return toArray(Complex.class).asComplex();
  }

  default Vector abs() {
    return map(Double.class, Na.ignore(v -> Math.abs(v)));
  }

  default Object argmax() {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  default Object argmin() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the sum of the values in this vector, or {@code NA}.
   *
   * @return the sum
   */
  default double sum() {
    return collect(Double.class, Collectors.sum());
  }

  /**
   * Performs a mutable aggregation of the values in this vector, similar to
   * {@linkplain Stream#collect(java.util.stream.Collector)}. A mutable aggregation performs its
   * aggregation by mutating and adding values to an aggregation container such as a
   * {@linkplain List list}.
   *
   * <p>
   * The result produced is equivalent to:
   *
   * <pre>
   * {
   *   &#064;code
   *   T container = collector.supplier();
   *   for (int i = 0; i &lt; size(); i++) {
   *     collector.accumulator().accept(container, get(in, i));
   *   }
   *   return collector.finisher().apply(container);
   * }
   * </pre>
   *
   * <p>
   * Example:
   *
   * <pre>
   * {@code
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
   * mean  0.029
   * sum   28.714
   * std   1.008
   * var   1.016
   * min   -3.056
   * max   3.589
   * n     1000.000
   * type: double
   * }
   * </pre>
   *
   * <pre>
   * Vector names = Vector.of("Mary", "Bob", "Lisa");
   * 0  Mary
   * 1  Bob
   * 2  Lisa
   * type: string
   * 
   * names.collect(Collectors.repeat(2));
   * 0  Mary
   * 1  Bob
   * 2  Lisa
   * 3  Mary
   * 4  Bob
   * 5  Lisa
   * type: string
   * </pre>
   *
   * @param <T> the type of the input value to the mutable aggregation
   * @param <R> the type of the mutable collector
   * @param <C> the type of the return type of the aggregation
   * @param in the input type
   * @param collector the collector
   * @return a value of type {@code R} (i.e. the result of the aggregation)
   * @see java.util.stream.Collector
   * @see java.util.stream.Stream#collect(java.util.stream.Collector)
   */
  <T, R, C> R collect(Class<T> in, Collector<? super T, C, ? extends R> collector);

  /**
   * Returns the mean of the values in this vector or {@code NA}.
   *
   * @return the mean
   */
  default double mean() {
    return collect(Double.class, Collectors.mean());
  }

  /**
   * Returns the standard deviation of the values in this vector or {@code NA}.
   *
   * @return the standard deviation
   */
  default double std() {
    return collect(Double.class, Collectors.std());
  }

  /**
   * Return the variance of the values in this vector or {@code NA}.
   *
   * @return the variance
   */
  default double var() {
    return collect(Double.class, Collectors.var());
  }

  /**
   * Return the number of unique elements in this vector.
   * 
   * <pre>
   * {@code
   * > Vector.of(1, 2, 1, 2).nunique()
   * 2
   * }
   * </pre>
   *
   * @return the number of unique values
   */
  default int nunique() {
    return collect(Collectors.nunique());
  }

  <R> R collect(Collector<? super Object, ?, R> collector);

  /**
   * Return a vector of value and their counts.
   *
   * <pre>
   * {@code
   * > Vector.of(1, 1, 2, 2).valueCounts();
   * 1   2
   * 2   2
   * type: int
   * }
   * </pre>
   *
   * @return a vector of value counts
   */
  default Vector valueCounts() {
    return collect(Collectors.valueCounts());
  }

  default <T extends Comparable<T>> T min(Class<T> cls) {
    return collect(cls, Collectors.withFinisher(
        java.util.stream.Collectors.minBy(Comparable::compareTo), Optional::get));
  }

  default <T extends Comparable<T>> T max(Class<T> cls) {
    return collect(cls, Collectors.withFinisher(
        java.util.stream.Collectors.minBy(Comparable::compareTo), Optional::get));
  }

  /**
   * Return a vector of only {@code non-NA} values
   *
   * @return a vector without {@code NA} values
   */
  default Vector nonNA() {
    return collect(Collectors.nonNA());
  }

  default StatisticalSummary statisticalSummary() {
    return Vectors.statisticalSummary(this);
  }

  default boolean all(Predicate<Object> predicate) {
    return all(Object.class, predicate);
  }

  <T> boolean all(Class<T> cls, Predicate<? super T> predicate);

  default boolean any(Predicate<Object> predicate) {
    return any(Object.class, predicate);
  }

  <T> boolean any(Class<T> cls, Predicate<? super T> predicate);

  /**
   * Return an indexer that provides pure location based indexing.
   *
   * <p>
   * Location based indexing closely follows the indexing rules of other java containers such as
   * lists and arrays
   *
   * @return a location indexer
   */
  VectorLocationGetter loc();

  /**
   * Creates a new builder able to build new vectors of this type, initialized with the values in
   * this builder.
   *
   * <pre>
   * {@code
   * Vector vec = vector.newCopyBuilder().plus("Hello world")
   * assert vec.size() == vector.size() + 1
   * assert vec.getAsString(0) == vector.getAsString(0)}
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

  /**
   * <p>
   * Builds a new vector. A builder can incrementally grow, but not allow gaps. For example, if a
   * builder is initialized with size {@code 8}, {@link #add(Object)} (et. al.) adds a value at
   * index {@code 8} and indexes {@code 0-7} have the value {@code NA}. If the value at index
   * {@code 11} is set, values {@code 9, 10} are set to {@code NA}.
   * </p>
   *
   * <p>
   * When transferring values between vectors, prefer {@link #set(Object, Vector, int)} to
   * {@link #set(Object, Object)}. For example, {@code Vector.Builder a; Vector b; a.set(0, b, 10)}
   * sets the value of {@code a} at index {@code 0} to the value at index {@code 10} in {@code b}.
   * This avoids unboxing values from one vector to another. For the numerical vectors, values are
   * coerced, e.g. {@code 1} from an int-vector becomes {@code 1.0} in a double vector or
   * {@code Logical.TRUE} in a logical-vector.
   * </p>
   */
  interface Builder {

    /**
     * Recommended initial capacity
     */
    int INITIAL_CAPACITY = 50;

    /**
     * Construct a builder for the specified type
     *
     * @param cls the class of the builder
     * @return a primitive or reference builder
     * @see org.briljantframework.data.vector.VectorType#of(Class)
     */
    static Builder of(Class<?> cls) {
      return VectorType.of(cls).newBuilder();
    }

    /**
     * Construct a builder of the specified type and size filled with {@code NA}
     *
     * @param cls the type
     * @param size the size
     * @return a new builder with the specified size filled with {@code NA}
     */
    static Builder withSize(Class<?> cls, int size) {
      Vector.Builder builder = withCapacity(cls, size);
      for (int i = 0; i < size; i++) {
        builder.addNA();
      }
      return builder;
    }

    static Builder withCapacity(Class<?> cls, int capacity) {
      return VectorType.of(cls).newBuilderWithCapacity(capacity);
    }

    /**
     * @return a modified builder
     */
    Builder addNA();

    Builder setNA(Object key);

    /**
     * Same as {@code plus(size(), from, fromIndex)}
     *
     * @param from the vector to take the value from
     * @param fromIndex the index
     * @return a modified builder
     */
    Builder add(Vector from, int fromIndex);

    Builder add(Vector from, Object key);

    Builder set(Object atKey, Vector from, int fromIndex);

    Builder set(Object atKey, Vector from, Object fromIndex);

    Builder set(Object key, Object value);

    /**
     * Same as {@code plus(size(), value)}
     *
     * @param value the value
     * @return a modified builder
     */
    Builder add(Object value);

    Builder add(double value);

    Builder add(int value);

    default Builder addAll(Object... objects) {
      return addAll(Arrays.asList(objects));
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

    default Builder addAll(Vector.Builder builder) {
      return addAll(builder.getView());
    }

    /**
     * Add all values in {@code from} to this builder.
     *
     * @param from the vector
     * @return a modified builder
     */
    Builder addAll(Vector from);

    /**
     * Returns a view of this builder as a vector. Modifications to the builder is propagated to the
     * vector, allowing changes to be tracked within the builder.
     * 
     * <p/>
     * Note, however, that the view does NOT track any indexes (hence, only integer based (location)
     * indexing is supported).
     *
     * @return a view of this builder as a vector
     */
    Vector getView();

    Builder remove(Object key);

    Builder readAll(DataEntry entry) throws IOException;

    VectorLocationSetter loc();

    /**
     * Reads a value from the input stream and appends it to the builder (after the last value).
     *
     * @param entry the input stream
     * @return receiver modified
     */
    Builder read(DataEntry entry);

    /**
     * Returns the size of the resulting vector
     *
     * @return the size
     */
    int size();

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
