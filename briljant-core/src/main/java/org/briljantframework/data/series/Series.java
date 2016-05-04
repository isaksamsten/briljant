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
package org.briljantframework.data.series;

import java.io.Serializable;
import java.util.*;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.Check;
import org.briljantframework.array.*;
import org.briljantframework.data.Collectors;
import org.briljantframework.data.Na;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.reader.DataEntry;

/**
 * A <tt>Series</tt> is an homogeneous combined list and map implementation with O(1) location-based
 * and key-based access of values and supporting missing entries (i.e. NA). <tt>Series</tt> allows
 * duplicate entries, but require unique indexes. Inserting new key/value pairs is supported, but
 * discouraged. Using an appropriate builder is often much faster.
 * <p/>
 * 
 * The <tt>equals</tt> and <tt>hashCode</tt> methods should be based on both the values and the
 * indices. That is, given two series they are only considered equal if each key is associated with
 * equal elements (similar to the {@link Map Map} interface.
 * 
 * <p/>
 * 
 * A <tt>Series</tt> implements the <tt>BaseArray</tt> interface and can thus be used by many
 * operations in {@link org.briljantframework.array.Arrays Arrays}. Note, however, that a view
 * returned by any of the operations in the <tt>BaseArray</tt> interface <i>looses</i> the index of
 * the original (and replaces it with a range index from <tt>[0...size()]</tt>). For example:
 *
 * <pre>
 * Series a = Series.of("A", "B", "C", "D");
 * a.setIndex(Index.of(10, 20, 30, 40));
 * b = a.reshape(2, 2);
 * b.get(10); // throws NoSuchKeyException
 * b.get(0); // => "A"
 * b.loc().get(0, 0); // => "A"
 * </pre>
 *
 * Location based indexing works as expected (in column-major order).
 *
 * <p/>
 * Since NA value are implemented differently depending value type, checking for NA-values are done
 * via the {@link #isNA(Object)} method or via the static
 * {@link org.briljantframework.data.Is#NA(java.lang.Object)} method.
 *
 * @author Isak Karlsson
 */
public interface Series extends BaseArray<Series>, Collection<Object>, Serializable {

  /**
   * Construct a series of values. The type of series is inferred from the values.
   *
   * @param array the values
   * @return a new series
   */
  static Series of(Object first, Object... array) {
    Series.Builder builder = Series.Builder.of(first.getClass());
    builder.add(first);
    builder.addAll(Arrays.asList(array));
    return builder.build();
  }

  /**
   * Creates an empty series
   *
   * @return an empty series
   */
  static Series of() {
    return SingletonSeries.empty();
  }

  /**
   * Return a one element series with the specified value
   *
   * @param value the value
   * @return a one element series
   */
  static Series of(Object value) {
    return repeat(value, 1);
  }

  /**
   * Creates a series with the specified value and size
   *
   * @param value the value
   * @param size the size of the series
   * @return a new series
   */
  static Series repeat(Object value, int size) {
    Check.argument(size > 0, "illegal size");
    return new SingletonSeries(value, size);
  }

  /**
   * Return a series with the specified size consisting of the values given by the supplier
   *
   * <pre>
   * Series.Builder b;
   * for (int i = 0; i &lt; size; i++) {
   *   b.plus(supplier.get());
   * }
   * </pre>
   */
  static Series generate(Supplier<Object> supplier, int size) {
    Check.argument(size > 0, "illegal size");
    if (size == 1) {
      return of();
    }
    Object value = supplier.get();
    Series.Builder builder = Type.of(value).newBuilder().add(value);
    for (int i = 1; i < size; i++) {
      builder.add(supplier.get());
    }
    return builder.build();
  }

  /**
   * Returns a series with the type inferred.
   * 
   * @param values the elements
   * @return a new series
   */
  @SuppressWarnings("unchecked")
  static Series copyOf(Iterable<?> values) {
    if (values instanceof Series) {
      return (Series) values;
    }

    Iterator<?> it = values.iterator();
    if (!it.hasNext()) {
      return of();
    }

    TypeInferenceBuilder builder = new TypeInferenceBuilder();
    while (it.hasNext()) {
      builder.add(it.next());
    }
    return builder.build();
  }

  /**
   * Returns a series with {@code T}-type containing the given elements.
   *
   * @param values the specified values
   * @return a new series with the specified values
   */
  static <T> Series copyOf(Class<T> cls, Iterable<T> values) {
    Iterator<T> it = values.iterator();
    if (!it.hasNext()) {
      return of();
    }

    Series.Builder builder = new ObjectSeries.Builder(cls);
    while (it.hasNext()) {
      builder.add(it.next());
    }
    return builder.build();
  }

  /**
   * {@inheritDoc}
   * 
   * In addition to the requirements imposed by the <tt>BaseArray</tt> interface, for
   * <tt>Series</tt> the indexing of the view is no longer related to the original. The returned
   * view should be indexed with a {@link org.briljantframework.data.index.RangeIndex RangeIndex}.
   * 
   * @param offset the offset (where indexing starts)
   * @param shape the shape of the view
   * @param stride the stride of the view
   * @return
   */
  @Override
  Series asView(int offset, int[] shape, int[] stride);

  /**
   * Returns a boolean array of the elements for which the predicate returns {@code true}.
   *
   * @param predicate the predicate
   * @return a boolean array
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
   * @return a boolean array
   */
  <T> BooleanArray where(Class<T> cls, Predicate<? super T> predicate);

  /**
   * Returns a series consisting of only the elements for which the predicate returns true.
   * 
   * @param predicate the predicate
   * @return a series
   */
  default Series filter(Predicate<? super Object> predicate) {
    return filter(Object.class, predicate);
  }

  /**
   * Filter values in this series, treating each value as {@code cls} (or NA), using the supplied
   * predicate.
   *
   * @param <T> the type
   * @param cls the class
   * @param predicate the predicate
   * @return a new series with only values for which {@code predicate} returns true
   */
  <T> Series filter(Class<T> cls, Predicate<? super T> predicate);

  /**
   * Transform each value in this series.
   * 
   * @param operator the operator
   * @return a new series
   */
  default Series map(Function<Object, ?> operator) {
    return map(Object.class, operator);
  }

  /**
   * Transform each value (as a value of {@code T}) in the series using {@code operator}, producing
   * a new series with the type inferred from the first value returned by {@code operator}.
   *
   * <p>
   * Example:
   *
   * <pre>
   * {@code
   * > Random rand = new Random();
   * > Series a = Series.of(rand::nextGaussian, 3);
   * 0     -0.862
   * 1     0.653
   * 2     0.836
   * 3     0.196
   * 4     0.554
   *
   * > Series b = a.map(Double.class, Math::round);
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
   * @return a new series of type inferred by {@code operator}
   */
  <T> Series map(Class<T> cls, Function<? super T, ?> operator);

  Series zipWith(Series other, BiFunction<? super Object, ? super Object, ?> combiner);

  /**
   * Combine two vectors using the specified combination function. For example, concatenating two
   * string vectors, or adding two numerical vectors.
   *
   * <pre>
   * Series a = Series.of(1, 2, 3, 4);
   * Series b = Series.of(1, 2, 3, 4);
   * a.combine(String.class, b, (x, y) -&gt; x + y).map(String.class, String::length);
   * </pre>
   *
   * @param cls the class
   * @param other the other series
   * @param combiner the combiner
   * @param <T> a type
   * @return a new series
   */
  <T> Series zipWith(Class<T> cls, Series other,
      BiFunction<? super T, ? super T, ? extends T> combiner);

  default Series plus(Object other) {
    return plus(repeat(other, size()));
  }

  default Series plus(Series other) {
    return zipWith(Object.class, other, Combine.add());
  }

  default Series times(Number other) {
    return times(repeat(other, size()));
  }

  default Series times(Series other) {
    return zipWith(Object.class, other, Combine.mul());
  }

  default Series div(Number other) {
    return div(repeat(other, size()));
  }

  default Series div(Series other) {
    return zipWith(Object.class, other, Combine.div());
  }

  default Series reverseDiv(Number other) {
    return repeat(other, size()).zipWith(Object.class, this, Combine.div());
  }

  default Series minus(Number other) {
    return minus(repeat(other, size()));
  }

  default Series minus(Series other) {
    return zipWith(Object.class, other, Combine.sub());
  }

  default Series reverseMinus(Number other) {
    return repeat(other, size()).zipWith(Object.class, this, Combine.sub());
  }

  /**
   * Sort the series in its <i>natural order</i> in ascending or descending order
   *
   * <p>
   * The sort order is specified by the implementation
   *
   * @param order the specified order
   * @return the series sorted
   */
  Series sort(SortOrder order); // TODO: 4/28/16 this should sort the index

  /**
   * Sort the series according to the natural sort order of the specified comparable
   *
   * @param cls the comparable type
   * @return a new series sorted
   */
  <T extends Comparable<T>> Series sort(Class<T> cls); // this should take a comparator

  /**
   * Sort the series using the the specified comparator and the defined type
   *
   * @param cls the type of elements
   * @param cmp the comparator
   * @return a new sorted series
   */ // TODO(isak) rename sortBy (sort values)
  <T> Series sort(Class<T> cls, Comparator<? super T> cmp);

  /**
   * Return a series of the {@code n} first elements
   *
   * <p>
   * Example
   * 
   * <pre>
   * {@code
   * > Series v = Series.of(1, 2, 3, 4);
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
  Series limit(int n);

  /**
   * Returns a series of the {@code n} last elements
   * <p>
   * Example
   * 
   * <pre>
   * {@code
   * > Series v = Series.of(1, 2, 3, 4);
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
  Series tail(int n);

  // Index operations

  /**
   * Get the index of this series
   *
   * @return the index of this series
   */
  Index getIndex();

  /**
   * Set the index of this series. The size of the index must equal the size of the series.
   *
   * @param index the index
   */
  void setIndex(Index index);

  /// Query operations

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
   * Set (replace or add) the value at the specified key with the given value.
   *
   * @param key the key
   * @param value the value
   */
  void set(Object key, Object value);

  /**
   * Get the value with the given key as a double
   * 
   * @param key the key
   * @return a double
   */
  double getDouble(Object key);

  /**
   * Set (replace or add) the value at the specified key with the given value.
   *
   * @param key the key
   * @param value the value
   */
  void setDouble(Object key, double value);

  /**
   * Get the value witht the given key as an int
   * 
   * @param key the key
   * @return an int
   */
  int getInt(Object key);

  /**
   * Set (replace or add) the value at the specified key with the given value.
   *
   * @param key the key
   * @param value the value
   */
  void setInt(Object key, int value);

  /**
   * Select a subset of this series for which the keys of this series is true in the given series
   *
   * @param select a series of truth values
   * @return a subset of this series
   */
  Series get(BooleanArray select);

  /**
   * Set the positions for which this array return true to the specified value
   * 
   * @param array the array of boolean values
   * @param value the value
   */
  void set(BooleanArray array, Object value);

  /**
   * Return true if the value with the given key is {@code NA}
   *
   * @param key the key
   * @return true if the value is {@code NA}
   */
  boolean isNA(Object key);

  /**
   * Returns true if there are any NA values
   *
   * @return true or false
   */
  boolean hasNA();

  /**
   * Get the type of the series.
   *
   * @return the type
   */
  Type getType();

  default Set<Map.Entry<Object, Object>> entrySet() {
    return entrySet(Object.class);
  }

  <T> Set<Map.Entry<Object, T>> entrySet(Class<T> cls);

  /**
   * {@inheritDoc}
   * 
   * This methods copies both the index and the values.
   * 
   * @return a new identical copy of this series
   */
  Series copy();

  // View operations

  /**
   * Return this view as a <tt>DoubleArray</tt>.
   *
   * @return a double array view
   */
  DoubleArray asDoubleArray();

  /**
   * Return this view as an <tt>IntArray</tt>.
   *
   * @return an int array view
   */
  IntArray asIntArray();

  LongArray asLongArray();

  BooleanArray asBooleanArray();

  ComplexArray asComplexArray();

  /**
   * Return a view of this series as an array.
   *
   * @return an array view
   */
  Array<Object> asArray();

  /**
   * Return a view of this series as a list.
   *
   * @return a list view
   */
  List<Object> asList();

  /**
   * Return a view of this <tt>Series</tt> as a list.
   * 
   * @param cls the element type
   * @param <T> the element type
   * @return a list view
   */
  <T> List<T> asList(Class<T> cls);

  /**
   * Return a stream of the elements in this series.
   *
   * @param cls the element type
   * @return a stream
   */
  <T> Stream<T> stream(Class<T> cls);

  /**
   * Returns a stream of the elements in this series as ints.
   *
   * @return a stream of the elements in this series as ints.
   */
  IntStream intStream();

  /**
   * Returns a stream of elements in this series as doubles.
   *
   * @return a stream of doubles
   */
  DoubleStream doubleStream();

  // Numerical operations

  default Series abs() {
    return map(Double.class, Na.ignore(v -> Math.abs(v)));
  }

  default Object argmax() {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  default Object argmin() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the sum of the values in this series, or {@code NA}.
   *
   * @return the sum
   */
  default double sum() {
    return collect(Double.class, Collectors.sum());
  }

  /**
   * Performs a mutable aggregation of the values in this series, similar to
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
   * > Series series = Series.of(normal::sample, 1000)
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
   * > double mean = series.collect(Double.class, Aggregates.mean());
   * > Series summary = series.collect(Double.class, Aggregate.summary());
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
   * Series names = Series.of("Mary", "Bob", "Lisa");
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
   * Returns the mean of the values in this series or {@code NA}.
   *
   * @return the mean
   */
  default double mean() {
    return collect(Double.class, Collectors.mean());
  }

  /**
   * Returns the standard deviation of the values in this series or {@code NA}.
   *
   * @return the standard deviation
   */
  default double std() {
    return collect(Double.class, Collectors.std());
  }

  /**
   * Return the variance of the values in this series or {@code NA}.
   *
   * @return the variance
   */
  default double var() {
    return collect(Double.class, Collectors.var());
  }

  /**
   * Return the number of unique elements in this series.
   * 
   * <pre>
   * {@code
   * > Series.of(1, 2, 1, 2).nunique()
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
   * Return a series of value and their counts.
   *
   * <pre>
   * {@code
   * > Series.of(1, 1, 2, 2).valueCounts();
   * 1   2
   * 2   2
   * type: int
   * }
   * </pre>
   *
   * @return a series of value counts
   */
  default Series valueCounts() {
    return collect(Collectors.valueCounts());
  }

  default <T extends Comparable<T>> T min(Class<T> cls) {
    return collect(cls, Collectors
        .withFinisher(java.util.stream.Collectors.minBy(Comparable::compareTo), Optional::get));
  }

  default <T extends Comparable<T>> T max(Class<T> cls) {
    return collect(cls, Collectors
        .withFinisher(java.util.stream.Collectors.minBy(Comparable::compareTo), Optional::get));
  }

  /**
   * Return a series of only {@code non-NA} values
   *
   * @return a series without {@code NA} values
   */
  default Series nonNA() {
    return collect(Collectors.nonNA());
  }

  default StatisticalSummary statisticalSummary() {
    return Vectors.statisticalSummary(this);
  }

  default boolean allMatch(Predicate<Object> predicate) {
    return allMatch(Object.class, predicate);
  }

  <T> boolean allMatch(Class<T> cls, Predicate<? super T> predicate);

  default boolean anyMatch(Predicate<Object> predicate) {
    return anyMatch(Object.class, predicate);
  }

  <T> boolean anyMatch(Class<T> cls, Predicate<? super T> predicate);

  // TODO: 5/4/16 Add noneMatch

  /**
   * Return an indexer that provides pure location based indexing.
   *
   * <p />
   * Location based indexing closely follows the indexing rules of other java containers such as
   * lists and arrays.
   *
   * <p />
   *
   * @return a location indexer
   */
  LocationGetter loc();

  Series reindex(Index index);

  /**
   * Creates a new builder able to build new vectors of this type, initialized with the values in
   * this builder.
   *
   * <pre>
   * Series vec = series.newCopyBuilder().add("Hello world").build();
   * assert vec.size() == series.size() + 1;
   * assert vec.loc().get(0) == series.loc().get(0);
   * </pre>
   *
   * @return a new builder
   */
  Builder newCopyBuilder();

  /**
   * Creates a new builder.
   *
   * @return a new builder
   */
  Builder newBuilder();

  /**
   * Creates a new builder filled with {@code NA}.
   *
   * @param size the initial size
   * @return a new builder
   */
  Builder newBuilder(int size);

  /**
   * <p>
   * Builds a new series. A builder can incrementally grow, but not allow gaps. For example, if a
   * builder is initialized with size {@code 8}, {@link #add(Object)} (et. al.) adds a value at
   * index {@code 8} and indexes {@code 0-7} have the value {@code NA}. If the value at index
   * {@code 11} is set, values {@code 9, 10} are set to {@code NA}.
   * </p>
   *
   * <p>
   * When transferring values between series, prefer {@link #set(Object, Series, int)} to
   * {@link #set(Object, Object)}. For example, {@code Series.Builder a; Series b; a.set(0, b, 10)}
   * sets the value of {@code a} at index {@code 0} to the value at index {@code 10} in {@code b}.
   * This avoids unboxing values from one series to another. For the numerical vectors, values are
   * coerced, e.g. {@code 1} from an int-series becomes {@code 1.0} in a double series or
   * {@code Logical.TRUE} in a logical-series.
   * </p>
   */
  interface Builder {

    /**
     * Recommended initial capacity.
     */
    int INITIAL_CAPACITY = 5;

    /**
     * Construct a builder of the specified type.
     *
     * @param cls the class of the builder
     * @return a primitive or reference builder
     * @see Type#of(Class)
     */
    static Builder of(Class<?> cls) {
      return Type.of(cls).newBuilder();
    }

    /**
     * Construct a builder of the specified type and size filled with {@code NA}
     *
     * @param cls the type
     * @param size the size
     * @return a new builder with the specified size filled with {@code NA}
     */
    static Builder withSize(Class<?> cls, int size) {
      Series.Builder builder = withCapacity(cls, size);
      for (int i = 0; i < size; i++) {
        builder.addNA();
      }
      return builder;
    }

    /**
     * Construct a new builder with the specified type and capacity.
     * 
     * @param cls the type
     * @param capacity the initial capacity
     * @return a new builder
     */
    static Builder withCapacity(Class<?> cls, int capacity) {
      return Type.of(cls).newBuilderWithCapacity(capacity);
    }

    /**
     * Add a new NA value.
     * 
     * @return a modified builder
     */
    Builder addNA();

    /**
     * Set the value associated with the specified key to NA.
     * 
     * @param key the key
     * @return a modified builder
     */
    Builder setNA(Object key);

    /**
     * Add the value from the specified series and location.
     *
     * @param from the series to take the value from
     * @param fromIndex the index
     * @return a modified builder
     */
    Builder add(Series from, int fromIndex);

    /**
     * Add the value from the specified series and key.
     * 
     * @param from the series to take the value from
     * @param key the key in from
     * @return a modified builder
     */
    Builder add(Series from, Object key);

    /**
     * Associate the specified key with the value from the specified series and location.
     * 
     * @param atKey the key to set
     * @param from the source
     * @param fromIndex the index in from
     * @return a modified builder
     */
    Builder set(Object atKey, Series from, int fromIndex);

    /**
     * Associate the specified key with the value from from the specified series and key
     * 
     * @param atKey the key to set
     * @param from the source
     * @param fromIndex the key in from
     * @return a modified builder
     */
    Builder set(Object atKey, Series from, Object fromIndex);

    /**
     * Associate the specified key with the specified value.
     * 
     * @param key the key
     * @param value the value
     * @return a modified builder
     */
    Builder set(Object key, Object value);

    /**
     * Add the specified value.
     *
     * @param value the value
     * @return a modified builder
     */
    Builder add(Object value);

    /**
     * Add the specified value.
     *
     * @param value the value
     * @return a modified builder
     */
    Builder add(double value);

    /**
     * Add the specified value.
     *
     * @param value the value
     * @return a modified builder
     */
    Builder add(int value);

    /**
     * Add all values in iterable
     *
     * @param iterable the collection of values
     */
    default Builder addAll(Iterable<?> iterable) {
      iterable.forEach(this::add);
      return this;
    }

    /**
     * Add all values in to this builder.
     *
     * @param from the series
     * @return a modified builder
     */
    Builder addAll(Series from);

    /**
     * Remove the value associated with the specified key. This shifts the location of all elements
     * with a location larger than the element associated with the specified key on index to the
     * left.
     * 
     * @param key the key
     * @return a modified builder
     */
    Builder remove(Object key);

    /**
     * Read all elements from the specified entry.
     * 
     * @param entry the entry
     * @return a modified builder
     */
    Builder readAll(DataEntry entry);

    /**
     * Returns a
     * @return
     */
    LocationSetter loc();

    /**
     * Reads a value from the input stream and appends it to the builder (after the last value).
     *
     * @param entry the input stream
     * @return receiver modified
     */
    Builder read(DataEntry entry);

    /**
     * Returns the size of the resulting series
     *
     * @return the size
     */
    int size();

    /**
     * Create a new series of suitable type. This interface does not provide any guarantees to
     * whether or not it is possible to construct several vectors using the same builder. Most
     * commonly, once {@code build()} has been called, subsequent calls on the builder will fail.
     *
     * @return a new series
     */
    Series build();
  }


}
