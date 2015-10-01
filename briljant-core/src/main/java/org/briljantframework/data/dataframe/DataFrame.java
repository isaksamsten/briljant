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

package org.briljantframework.data.dataframe;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.briljantframework.array.Array;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.BoundType;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.dataframe.join.JoinType;
import org.briljantframework.data.index.DataFrameLocationGetter;
import org.briljantframework.data.index.DataFrameLocationSetter;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.ObjectComparator;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.EntryReader;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;

/**
 * A DataFrame is a 2-dimensional storage of data consisting of (index) <em>columns</em> and
 * <em>rows</em>.
 *
 * @author Isak Karlsson
 */
public interface DataFrame extends Iterable<Object> {

  /**
   * Return a <em>shallow copy</em> of the data frame with the index sorted in the specified order.
   *
   * <p/>
   * For example,
   * 
   * <pre>
   * {
   *   &#064;code
   *   DataFrame df = MixedDataFrame.of(&quot;a&quot;, Vector.of(3, 2, 1), &quot;b&quot;, Vector.of(2, 9, 0));
   *   df.setIndex(ObjectIndex.of(&quot;F&quot;, &quot;Q&quot;, &quot;A&quot;));
   *   df.sort(SortOrder.ASC);
   * }
   * </pre>
   *
   * produces
   * 
   * <pre>
   *    a  b
   * A  1  0
   * F  3  2
   * Q  2  9
   * </pre>
   *
   * @param order the sort order
   * @return a shallow copy
   */
  DataFrame sort(SortOrder order);

  /**
   * Return a <em>shallow copy</em> of the data frame with the index sorted in the order specified
   * by the supplied {@link Comparator}.
   *
   * @param comparator the comparator
   * @return a shallow copy
   * @see #sort(org.briljantframework.data.SortOrder)
   */
  DataFrame sort(Comparator<Object> comparator);

  /**
   * Equivalent to {@code sort(SortOrder.ASC, key)}
   *
   * @see #sort(SortOrder, Object)
   */
  DataFrame sort(Object key);

  /**
   * Return a <em>shallow copy</em> of the data frame sorted in the order specified using the values
   * of the specified column.
   *
   * <p/>
   * Generally, the index is sorted which makes the iteration order of {@linkplain #getIndex()}
   * sorted by the values in the specified column but leaves the values in their original order.
   * Hence, the location of rows are unchanged.
   *
   * @param order the order
   * @param key the column
   * @return a new sorted data frame
   */
  DataFrame sort(SortOrder order, Object key);

  /**
   * Return a <em>shallow copy</em> of the data frame sorted according to the values and the
   * comparator in the specified column.
   *
   * @param cls the type of values to sort on
   * @param cmp the comparator
   * @param key the column
   * @param <T> the type
   * @return a new data frame sorted according to the specified comparator
   */
  <T> DataFrame sort(Class<? extends T> cls, Comparator<? super T> cmp, Object key);

  /**
   * Return a new data frame with the first {@code n} rows
   *
   * @param n the number of rows
   * @return a new data frame
   */
  DataFrame head(int n);

  /**
   * Equivalent to {@code head(10)}
   */
  default DataFrame head() {
    return head(10);
  }

  /**
   * Return a new data frame indexed on the values of the column specified by {@code key}.
   * <p>
   * An exception is raised if the specified column contains duplicates.
   *
   * @param key the column
   * @return a new data frame index on the specified column
   */
  DataFrame indexOn(Object key);

  /**
   * Join this data frame with the supplied data frame on common indicies using the specified
   * {@linkplain org.briljantframework.data.dataframe.join.JoinType type}. For example, given two
   * data frames with the column-index {@code ["a", "c", "id"]} and {@code ["c", "f", "id"]}, the
   * common indicies {@code ["a", "id"]} will be used as join-keys.
   *
   * <pre>
   * {@code
   * 
   * 
   * }
   * </pre>
   *
   * @param type the type of join to perform
   * @param other the data frame to join
   * @return the data frames joined
   */
  DataFrame join(JoinType type, DataFrame other);

  default DataFrame join(DataFrame other) {
    return join(JoinType.INNER, other);
  }

  default DataFrame join(DataFrame other, Object key) {
    return join(JoinType.INNER, other, key);
  }

  default DataFrame join(DataFrame other, Object... keys) {
    return join(JoinType.INNER, other, keys);
  }

  DataFrame join(JoinType type, DataFrame other, Object key);

  DataFrame join(JoinType type, DataFrame other, Object... keys);

  /**
   * Apply {@code mapper} to each value, where the value is an instance of the supplied class, in
   * the data frame elementwise .
   *
   * <pre>
   * {@code
   * DataFrame df = MixedDataFrame.of("A", Vector.of(1,2,3),
   *                                  "B", Vector.of("a","b","c");
   * df.map(Integer.class, i -> i * 2);
   * }
   * </pre>
   *
   * result in the data frame:
   *
   * <pre>
   *    A  B
   * 0  1  a
   * 1  4  b
   * 2  6  c
   * [3 rows x 2 columns]
   * </pre>
   *
   * @param <T> the type
   * @param cls the type of values to transform
   * @param mapper the operation
   * @return a new data frame
   */
  <T> DataFrame map(Class<T> cls, Function<? super T, ?> mapper);

  /**
   * Apply the supplied operation to each value in the data frame elementwise.
   *
   * <pre>
   * {@code
   * DataFrame df = MixedDataFrame.of("A", Vector.of(1,2,3),
   *                                  "B", Vector.of("a","b","c");
   * df.map(o -> {
   *   if(o instanceof String) {
   *     return ((String)o).toUpperCase();
   *   } else if(o instanceof Integer){
   *     return (Integer)o * 2
   *   } else {
   *     return o;
   *   }
   * });
   * }
   * </pre>
   *
   * <pre>
   *    A  B
   * 0  1  A
   * 1  4  B
   * 2  6  C
   * [3 rows x 2 columns]
   * </pre>
   *
   * @param function the function to apply
   * @return a new data frame
   */
  default DataFrame map(Function<Object, ?> function) {
    return map(Object.class, function);
  }

  DataFrame apply(Function<? super Vector, ? extends Vector> transform);

  <T, C> DataFrame apply(Class<T> cls, Collector<? super T, C, ? extends Vector> collector);

  /**
   * <p>
   * Reduce all columns, applying {@code op} with the initial value {@code init}.
   *
   * @param <T> the type
   * @param cls the class
   * @param init the initial value
   * @param op the operation
   * @return a record with the reduced values
   */
  <T> Vector reduce(Class<? extends T> cls, T init, BinaryOperator<T> op);

  /**
   * <p>
   * Reduce every column by applying a (summarizing) function over the vector. The reduction
   * function must return a value which is an instance of the vector, i.e. if the i:th vector is a
   * double-vector it must return a {@code double}. The type of the returned value will is
   * determined of the columns. If all columns share the same type, the returned vector will have
   * that type; otherwise the most general vector will be returned.
   *
   * <pre>
   * {
   *   &#064;code
   *   DataFrame df = MixedDataFrame.of(&quot;a&quot;, Vector.of(1, 2, 3), &quot;b&quot;, Vector.of(10, 20, 30));
   *   Vector means = df.reduce(Vector::mean);
   * }
   * </pre>
   *
   * produces
   *
   * <pre>
   * a  2
   * b  20
   * type: int
   * </pre>
   *
   * @param op the operation to apply
   * @return a new record with the reduced values
   */
  Vector reduce(Function<Vector, ?> op);

  /**
   * <p>
   * Aggregate every column which is an instance of {@code cls} using the supplied collector.
   *
   * <pre>
   * {@code
   * df.collect(Double.class, Collectors.mean())
   * }
   * </pre>
   *
   * <p>
   * Returns a series consisting of the mean of the {@code Double} columns in {@code this}
   * dataframe.
   *
   * <p>
   * Note that {@link org.briljantframework.data.Collectors} implement several convenient
   * collectors, for example {@code df.collect(Number.class, Collectors.median())}.
   *
   * @param <T> the type of value to be aggregated
   * @param <C> the type of the mutable collector
   * @param cls the class
   * @param collector the collector
   * @return a vector of aggregated values
   */
  <T, C> Vector collect(Class<T> cls, Collector<? super T, C, ?> collector);

  // TODO: remove when ISSUE#7 is resolved
  <T, R, C> Vector collect(Class<T> in, Class<R> out, Collector<? super T, C, ? extends R> collector);

  DataFrameGroupBy groupBy(Object column);

  <T> DataFrameGroupBy groupBy(Class<T> cls, Object column, Function<? super T, Object> map);

  DataFrameGroupBy groupBy(Object[] columns);

  /**
   * Group data frame based on the value returned by {@code keyFunction}. Each record in the data
   * frame is used for grouping.
   *
   * <p>
   * Example, given a {@link java.time.LocalDate}-index {@code DataFrame}, the data frame can be
   * grouped based on year:
   *
   * <pre>
   * {@code
   * df.groupBy(k -> LocalDate.class.cast(v).getYear());
   * }
   * </pre>
   *
   * @param keyFunction the key function
   * @return a group by data frame
   */
  DataFrameGroupBy groupBy(UnaryOperator<Object> keyFunction);

  <T> DataFrameGroupBy groupBy(Class<T> cls, Function<? super T, ?> function);

  /**
   * Uses the column index to find the specified column
   *
   * @param key the column name
   * @return the column
   * @throws java.util.NoSuchElementException if key is not found
   */
  Vector get(Object key);

  DataFrame get(Object... keys);

  DataFrame dropna();

  DataFrame drop(Object key);

  DataFrame drop(Object... keys);

  DataFrame drop(Predicate<Vector> predicate);

  Vector getRecord(Object key);

  DataFrame getRecord(Object... keys);

  /**
   * Limit the <em>records</em> of this dataframe to those with an {@code index} in the given range.
   *
   * @param from from inclusive
   * @param to to exclusive
   * @return a newly created {@code DataFrame}
   */
  DataFrame limit(Object from, Object to);

  /**
   * Limit the <em>records</em> of this dataframe to those with an {@code index} in the given range.
   *
   * @param from from object
   * @param fromBound the bound of from
   * @param to to object
   * @param toBound the bound of to
   * @return a newly created {@code DataFrame}
   */
  DataFrame limit(Object from, BoundType fromBound, Object to, BoundType toBound);

  DataFrame transpose();

  /**
   *
   * @param bits
   * @return
   */
  DataFrame select(Vector bits);

  DataFrame select(Predicate<Vector> predicate);

  DataFrame select(Object first, Object last);

  /**
   * Return a collection of columns
   *
   * @return an (immutable) collection of columns
   */
  List<Vector> getColumns();

  /**
   * Returns a collection of records.
   *
   * @return an (immutable) collection of rows
   */
  List<Vector> getRecords();

  <T> T get(Class<T> cls, Object row, Object col);

  double getAsDouble(Object row, Object col);

  int getAsInt(Object row, Object col);

  boolean isNA(Object row, Object col);

  String toString(Object row, Object col);

  /**
   * Returns the number of rows in this data frame
   *
   * @return the number of rows
   */
  int rows();

  /**
   * Returns the number of columns in this data frame
   *
   * @return the number of columns
   */
  int columns();

  /**
   * Returns a copy of this data frame.
   *
   * @return a copy
   */
  DataFrame copy();

  /**
   * Creates a new builder for creating new data frames which produces the concrete implementation
   * of {@code this}
   *
   * @return a new builder
   */
  Builder newBuilder();

  /**
   * Creates a new builder, initialized with a copy of this data frame, i.e.
   * {@code c.newCopyBuilder().build()} creates a new copy.
   *
   * @return a new builder
   */
  Builder newCopyBuilder();

  /**
   * Returns {@code this} DataFrame as an {@linkplain org.briljantframework.array.Array array}.
   *
   * @return this data frame as a matrix
   */
  // TODO: perhaps
  Array<Object> toArray();

  DoubleArray toDoubleArray();

  default Stream<Vector> stream() {
    return StreamSupport.stream(getRecords().spliterator(), false);
  }

  default Stream<Vector> parallelStream() {
    return StreamSupport.stream(getRecords().spliterator(), true);
  }

  DataFrame resetIndex();

  Index getIndex();

  Index getColumnIndex();

  void setIndex(Index index);

  void setColumnIndex(Index index);

  DataFrameLocationGetter loc();

  // Operations

  default Vector mean() {
    return reduce(Vector::mean);
  }

  default Vector min() {
    return collect(
        Object.class,
        org.briljantframework.data.Collectors.withFinisher(
            Collectors.minBy(ObjectComparator.getInstance()), Optional::get));
  }

  default Vector max() {
    return collect(
        Object.class,
        org.briljantframework.data.Collectors.withFinisher(
            Collectors.maxBy(ObjectComparator.getInstance()), Optional::get));
  }

  /**
   * Since DataFrames are immutable, this builder allows for the creation of new data frames
   */
  interface Builder {

    DataFrameLocationSetter loc();

    Builder set(Object tr, Object tc, DataFrame from, Object fr, Object fc);

    Builder set(Object row, Object column, Vector from, Object key);

    Builder set(Object row, Object column, Object value);

    Builder set(Object key, Vector.Builder columnBuilder);

    default Builder set(Object key, Vector column) {
      return set(key, column.newCopyBuilder());
    }

    default Builder set(Object key, VectorType columnType) {
      return set(key, columnType.newBuilder());
    }

    /**
     * Add a new vector builder as an additional column. If {@code builder.size() < rows()} the
     * added builder is padded with NA.
     *
     * @param columnBuilder builder to add
     * @return a modified builder
     */
    Builder add(Vector.Builder columnBuilder);

    /**
     * Add a new vector. If the {@code vector.size() < rows()}, the resulting vector is padded with
     * NA.
     *
     * @param column the vector
     * @return a modified builder
     */
    default Builder add(Vector column) {
      return add(column.newCopyBuilder());
    }

    /**
     * Adds a new vector builder as an additional column using
     * {@link org.briljantframework.data.vector.VectorType#newBuilder()}
     *
     * @param columnType the type
     * @return receiver modified
     */
    default Builder add(VectorType columnType) {
      return add(columnType.newBuilder());
    }

    Builder setRecord(Object key, Vector.Builder recordBuilder);

    default Builder setRecord(Object key, Vector record) {
      return setRecord(key, record.newCopyBuilder());
    }

    default Builder setRecord(Object key, VectorType recordType) {
      return setRecord(key, recordType.newBuilder());
    }

    /**
     * Adds a new record. If {@code builder.size() < columns()}, left-over columns are padded with
     * NA.
     *
     * @param builder the builder
     * @return receiver modified
     */
    Builder addRecord(Vector.Builder builder);

    /**
     * Adds a new record. If {@code vector.size() < columns()}, left-over columns are padded with
     * NA.
     *
     * @param vector the vector
     * @return receiver modified
     */
    default Builder addRecord(Vector vector) {
      return addRecord(vector.newCopyBuilder());
    }

    default Builder addRecord(VectorType recordType) {
      return addRecord(recordType.newBuilder());
    }

    Builder remove(Object key);

    Builder removeRecord(Object key);

    default Builder setColumnIndex(Object... keys) {
      return setColumnIndex(ObjectIndex.of(keys));
    }

    Builder setColumnIndex(Index columnIndex);

    default Builder setIndex(Object... keys) {
      return setIndex(ObjectIndex.of(keys));
    }

    Builder setIndex(Index index);

    /**
     * Read all records ({@linkplain org.briljantframework.data.reader.DataEntry}) from the supplied
     * reader.
     *
     * @param entryReader the entry reader
     * @return a modified builder
     */
    Builder readAll(EntryReader entryReader);

    /**
     * Append the entry as a record.
     *
     * @param entry the data entry
     * @return a modified builder
     */
    Builder read(DataEntry entry);

    /**
     * Returns the number of columns in the resulting data frame
     *
     * @return the number of columns
     */
    int columns();

    /**
     * Returns the number of rows in the resulting data frame
     *
     * @return the number of rows
     */
    int rows();

    DataFrame getTemporaryDataFrame();

    /**
     * Create a new DataFrame.
     *
     * @return a new data frame
     */
    DataFrame build();
  }

  static DataFrame of(Object name, Vector c) {
    return MixedDataFrame.of(name, c);
  }

  static DataFrame of(Object n1, Vector v1, Object n2, Vector v2) {
    return MixedDataFrame.of(n1, v1, n2, v2);
  }

  static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3) {
    return MixedDataFrame.of(n1, v1, n2, v2, n3, v3);
  }

  static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3, Object n4,
      Vector v4) {
    return MixedDataFrame.of(n1, v1, n2, v2, n3, v3, n4, v4);
  }

  static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3, Object n4,
      Vector v4, Object n5, Vector v5) {
    return MixedDataFrame.of(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5);
  }

  static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3, Object n4,
      Vector v4, Object n5, Vector v5, Object n6, Vector v6) {
    return MixedDataFrame.of(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5, n6, v6);
  }

  static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3, Object n4,
      Vector v4, Object n5, Vector v5, Object n6, Vector v6, Object n7, Vector v7) {
    return MixedDataFrame.of(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5, n6, v6, n7, v7);
  }

  static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3, Object n4,
      Vector v4, Object n5, Vector v5, Object n6, Vector v6, Object n7, Vector v7, Object n8,
      Vector v8) {
    return MixedDataFrame.of(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5, n6, v6, n7, v7, n8, v8);
  }

  /**
   * Creates a data frame from the given keys and values.
   *
   * @param entries the keys and values with which the dataframe is populated
   * @return a newly created {@code DataFrame}
   */
  static DataFrame fromEntries(Map.Entry<Object, ? extends Vector>... entries) {
    return MixedDataFrame.fromEntries(entries);
  }

  static DataFrame.Builder builder() {
    return MixedDataFrame.builder();
  }

  static KeyVectorHolder entry(Object key, Vector vector) {
    return new KeyVectorHolder(key, vector);
  }

  final class KeyVectorHolder implements Map.Entry<Object, Vector> {

    private final Object key;
    private final Vector value;

    public KeyVectorHolder(Object key, Vector value) {
      this.key = key;
      this.value = Objects.requireNonNull(value);
    }

    /**
     * Gets the key from this holder
     *
     * @return the key
     */
    @Override
    public Object getKey() {
      return key;
    }

    /**
     * Gets the value from this holder
     *
     * @return the value
     */
    @Override
    public Vector getValue() {
      return value;
    }

    /**
     * Throws {@link UnsupportedOperationException}.
     *
     * @param value ignored
     * @return never returns normally
     */
    @Override
    public Vector setValue(Vector value) {
      throw new UnsupportedOperationException();
    }
  }

}
