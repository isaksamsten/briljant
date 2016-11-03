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

import java.util.*;
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
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
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
 * equal elements (similar to the {@link Map Map} interface).
 * 
 * <p/>
 * Since NA value are implemented differently depending value type, checking for NA-values are done
 * via the {@link #isNA(Object)} method or via the static
 * {@link org.briljantframework.data.Is#NA(java.lang.Object)} method.
 *
 * @author Isak Karlsson
 */
public interface Series extends Iterable<Object> {

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
  static Series generate(Supplier<?> supplier, int size) {
    Check.argument(size > 0, "illegal size");
    if (size == 1) {
      return of();
    }
    Object value = supplier.get();
    Series.Builder builder = Types.inferFrom(value).newBuilder().add(value);
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
  static Series copyOf(Collection<?> values) {
    if (values instanceof Series) {
      return (Series) values;
    }

    Iterator<?> it = values.iterator();
    if (!it.hasNext()) {
      return of();
    }

    Builder builder;
    if (values instanceof DoubleArray) {
      builder = new DoubleSeries.Builder();
    } else if (values instanceof IntArray) {
      builder = new IntSeries.Builder();
    } else {
      builder = new TypeInferenceBuilder();
    }
    while (it.hasNext()) {
      builder.add(it.next());
    }
    return builder.build();
  }

  static Series copyOf(char[] values) {
    Series.Builder b = new IntSeries.Builder(0, values.length);
    for (char value : values) {
      b.addInt(value);
    }
    return b.build();
  }

  static Series copyOf(int[] values) {
    Series.Builder b = new IntSeries.Builder(0, values.length);
    for (int value : values) {
      b.addInt(value);
    }
    return b.build();
  }

  static Series copyOf(double[] values) {
    Series.Builder b = new DoubleSeries.Builder(0, values.length);
    for (double value : values) {
      b.addDouble(value);
    }
    return b.build();
  }

  static Series copyOf(long[] values) {
    Series.Builder b = new DoubleSeries.Builder(0, values.length);
    for (long value : values) {
      b.addDouble(value);
    }
    return b.build();
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
   * Returns the number of key-value pairs in the series.
   * 
   * @return the number of key-value pairs in the series.
   */
  int size();

  /**
   * Returns true if there are no key-value pairs in the series
   * 
   * @return true if there are no key-value pairs in the series
   */
  boolean isEmpty();

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

  default Series dropIf(Predicate<? super Object> predicate) {
    return dropIf(Object.class, predicate);
  }

  <T> Series dropIf(Class<T> cls, Predicate<? super T> predicate);

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

  Series merge(Series other, BiFunction<? super Object, ? super Object, ?> combiner);

  /**
   * Merge two vectors using the specified combination function. For example, concatenating two
   * string vectors, or adding two numerical vectors.
   *
   * <pre>
   * Series a = Series.of(1, 2, 3, 4);
   * Series b = Series.of(1, 2, 3, 4);
   * a.merge(String.class, b, (x, y) -&gt; x + y).map(String.class, String::length);
   * </pre>
   *
   * @param cls the class
   * @param other the other series
   * @param combiner the combiner
   * @param <T> a type
   * @return a new series
   */
  <T> Series merge(Class<T> cls, Series other,
      BiFunction<? super T, ? super T, ? extends T> combiner);

  /**
   * Return a new series with the specified key-value removed.
   *
   * @param key the key
   * @return a new series
   */
  Series drop(Object key);

  /**
   * Return a new series with the specified key-values removed.
   * 
   * @param keys the keys to remove
   * @return a new series
   */
  Series dropAll(Collection<?> keys);

  /**
   * Return a new series with the specified keys retained.
   *
   * @param keys the keys to retain
   * @return a new series
   */
  Series getAll(Collection<?> keys);

  /**
   * Sort the series in its <i>natural order</i> in ascending or descending order (according to its
   * index)
   *
   * @param order the specified order
   * @return the series sorted
   */
  Series sort(SortOrder order);

  /**
   * Sort the series according to the natural sort order of the values (converted to the specified
   * comparable)
   *
   * @param cls the comparable type
   * @return a new series sorted
   */
  <T extends Comparable<T>> Series sortBy(Class<T> cls);

  /**
   * Sort the series using the the specified comparator and the defined type
   *
   * @param cls the type of elements
   * @param cmp the comparator
   * @return a new sorted series
   */
  <T> Series sortBy(Class<T> cls, Comparator<? super T> cmp);

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
   * > Series v = Series.of(1, 2, 3, 4);
   * 0  1
   * 1  2
   * 2  3
   * 3  4
   * 
   * > v.tail(3);
   * 0  2
   * 1  3
   * 2  4
   * </pre>
   */
  Series tail(int n);

  // Index operations

  /**
   * Get the index of this series
   *
   * @return the index of this series
   */
  Index index();

  /// Query operations

  /**
   * Get the object with the specified key as an object
   *
   * @param key the key
   * @return an object
   */
  Object get(Object key);

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

  Set<Map.Entry<Object, Object>> entrySet();

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
   * Return a view of this <tt>Series</tt> as a list.
   *
   * @param cls the element type
   * @param <T> the element type
   * @return a list view
   */
  <T> List<T> values(Class<T> cls);

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
   * @param <A> the type of the return type of the aggregation
   * @param in the input type
   * @param collector the collector
   * @return a value of type {@code R} (i.e. the result of the aggregation)
   * @see java.util.stream.Collector
   * @see java.util.stream.Stream#collect(java.util.stream.Collector)
   */
  <T, R, A> R collect(Class<T> in, Collector<? super T, A, R> collector);

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
    return SeriesUtils.statisticalSummary(this);
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
  Storage values(); // TODO: rename to values()

  /**
   * Return a shallow copy of the specified series indexed using the given indexer.
   *
   * @param index the index
   * @return a new series
   * @see #setIndex(Index)
   */
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
   * When transferring values between series, prefer {@link #setFromLocation(Object, Series, int)}
   * to {@link #set(Object, Object)}. For example,
   * {@code Series.Builder a; Series b; a.set(0, b, 10)} sets the value of {@code a} at index
   * {@code 0} to the value at index {@code 10} in {@code b}. This avoids unboxing values from one
   * series to another. For the numerical vectors, values are coerced, e.g. {@code 1} from an
   * int-series becomes {@code 1.0} in a double series or {@code Logical.TRUE} in a logical-series.
   * </p>
   */
  interface Builder {

    /**
     * Construct a builder of the specified type.
     *
     * @param cls the class of the builder
     * @return a primitive or reference builder
     * @see Types#from(Class)
     */
    static Builder of(Class<?> cls) {
      return Types.from(cls).newBuilder();
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
     * Add the value from the specified series and key.
     * 
     * @param from the series to take the value from
     * @param key the key in from
     * @return a modified builder
     */
    Builder addFrom(Series from, Object key);

    Builder addFromLocation(Series from, int pos);

    /**
     * Associate the specified key with the value from the specified series and location.
     *
     * @param atKey the key to set
     * @param from the source
     * @param fromIndex the index in from
     * @return a modified builder
     */
    Builder setFromLocation(Object atKey, Series from, int fromIndex);

    /**
     * Associate the specified key with the value from from the specified series and key
     * 
     * @param atKey the key to set
     * @param from the source
     * @param fromIndex the key in from
     * @return a modified builder
     */
    Builder setFrom(Object atKey, Series from, Object fromIndex);

    /**
     * Associate the specified key with the specified value.
     * 
     * @param key the key
     * @param value the value
     * @return a modified builder
     */
    Builder set(Object key, Object value);

    Builder setInt(Object key, int value);

    Builder setDouble(Object key, double value);

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
    Builder addDouble(double value);

    /**
     * Add the specified value.
     *
     * @param value the value
     * @return a modified builder
     */
    Builder addInt(int value);

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
     * Set all values in to this builder (key-value pair) to the corresponding key-value pair from
     * the given series.
     *
     * @param from the series
     * @return a modified builder
     */
    Builder setAll(Series from);

    /**
     * Read all elements from the specified entry.
     * 
     * @param entry the entry
     * @return a modified builder
     */
    Builder readAll(DataEntry entry);

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
