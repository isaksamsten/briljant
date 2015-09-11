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

package org.briljantframework.data.dataframe;

import org.briljantframework.array.Array;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.BoundType;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.dataframe.join.JoinType;
import org.briljantframework.data.index.DataFrameLocationGetter;
import org.briljantframework.data.index.DataFrameLocationSetter;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.EntryReader;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
   * <p/> For example,
   * <pre>{@code
   * DataFrame df = MixedDataFrame.of(
   *    "a", Vector.of(3,2,1),
   *    "b", Vector.of(2,9,0));
   * df.setIndex(ObjectIndex.of("F", "Q", "A"));
   * df.sort(SortOrder.ASC);
   * }</pre>
   *
   * produces
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
   * Return a <em>shallow copy</em> of the data frame with the index sorted in the order
   * specified by the supplied {@link Comparator}.
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
   * Return a <em>shallow copy</em> of the data frame sorted in the order specified using the
   * values
   * of the specified column.
   *
   * <p/> Generally, the index is sorted which makes the iteration order of {@linkplain
   * #getIndex()} sorted by the values in the specified column but leaves the values in
   * their original order. Hence, the location of rows are unchanged.
   *
   * @param order the order
   * @param key   the column
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
   * {@linkplain org.briljantframework.data.dataframe.join.JoinType type}. For example, given
   * two data frames with the column-index {@code ["a", "c", "id"]} and {@code ["c", "f", "id"]},
   * the common indicies {@code ["a", "id"]} will be used as join-keys.
   *
   * <pre>{@code
   *
   *
   * }</pre>
   *
   * @param type  the type of join to perform
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

  default DataFrame join(JoinType type, DataFrame other, Object... keys) {
    return join(type, other, getColumnIndex().locations(keys));
  }

  /**
   * <p> Apply {@code op} to value. If {@code op} returns {@code NA}, the old value is kept.
   *
   * @param <T> the type
   * @param cls the type of values to transform
   * @param op  the operation
   * @return a new data frame
   */
  <T> DataFrame map(Class<T> cls, Function<? super T, Object> op);

  DataFrame apply(Function<? super Vector, ? extends Vector> transform);

  <T, C> DataFrame apply(Class<T> cls, Collector<? super T, C, ? extends Vector> collector);

  /**
   * <p> Reduce all columns, applying {@code op} with the initial value {@code init}.
   *
   * <pre>{@code
   * for(Object colKey : getColumnIndex().keySet()) {
   *   Vector column = get(colKey);
   *   T value = init;
   *   for(int i = 0; i < column.size(); i++){
   *     value = op.apply(value, column.loc().get(cls, i));
   *   }
   *   newVector.set(colKey, value);
   * }
   * }</pre>
   *
   * @param <T>  the type
   * @param cls  the class
   * @param init the initial value
   * @param op   the operation
   * @return a record with the reduced values
   */
  <T> Vector reduce(Class<? extends T> cls, T init, BinaryOperator<T> op);

  /**
   * <p> Reduce every column by applying a function.
   *
   * <pre>{@code
   *  df.reduce(Vec::mode);
   * }</pre>
   *
   * <p> Returns a record with the most frequent value of each column
   *
   * @param op the operation to apply
   * @return a new record with the reduced values
   */
  Vector reduce(Function<Vector, Object> op);

  /**
   * <p> Aggregate every column which is an instance of {@code cls} using the supplied collector.
   *
   * <pre>{@code
   *  df.aggregate(Double.class, Aggregate.of(
   *    RunningStatistics::new, RunningStatistics::add, RunningStatistics::getMean))
   * }</pre>
   *
   * <p> Returns a series consisting of the mean of the {@code Double} columns in {@code this}
   * dataframe.
   *
   * <p> Note that {@link org.briljantframework.data.Collectors} implement several convenient
   * aggregates, for example {@code df.collect(Number.class, Aggregate.median())}.
   *
   * @param <T>       the type of value to be aggregated
   * @param <C>       the type of the mutable collector
   * @param cls       the class
   * @param collector the collector
   * @return a vector of aggregated values
   */
  <T, C> Vector collect(Class<T> cls, Collector<? super T, C, ? extends T> collector);

  <T, R, C> Vector collect(Class<T> in, Class<R> out,
                           Collector<? super T, C, ? extends R> collector);


  DataFrameGroupBy groupBy(Object column);

  <T> DataFrameGroupBy groupBy(Class<T> cls, Object column, Function<? super T, Object> map);

  DataFrameGroupBy groupBy(Object[] columns);

  /**
   * Group data frame based on the value returned by {@code keyFunction}. Each record in the
   * data frame is used for grouping.
   *
   * <p> Example, given a {@link java.time.LocalDate}-index {@code DataFrame}, the data frame can
   * be grouped based on year:
   *
   * <pre>{@code
   * df.groupBy(k -> LocalDate.class.cast(v).getYear());
   * }</pre>
   *
   * @param keyFunction the key function
   * @return a group by data frame
   */
  DataFrameGroupBy groupBy(UnaryOperator<Object> keyFunction);

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

  DataFrame select(Object from, Object to);

  DataFrame select(Object from, BoundType fromBound, Object to, BoundType toBound);

  DataFrame select(Vector bits);

  DataFrame select(Predicate<Vector> predicate);

  DataFrame selectColumns(Object first, Object last);

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
   * Creates a new builder, initialized with a copy of this data frame, i.e. {@code
   * c.newCopyBuilder().build()} creates a new copy.
   *
   * @return a new builder
   */
  Builder newCopyBuilder();

  /**
   * Returns {@code this} DataFrame as an {@linkplain org.briljantframework.array.Array array}.
   *
   * @return this data frame as a matrix
   */
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
     * Adds a new vector builder as an additional column using {@link org.briljantframework.data.vector.VectorType#newBuilder()}
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
     * Read all records ({@linkplain org.briljantframework.data.reader.DataEntry}) from the
     * supplied
     * reader.
     *
     * @param entryReader the entry reader
     * @return a modified builder
     */
    public Builder readAll(EntryReader entryReader);

    /**
     * Append the entry as a record.
     *
     * @param entry the data entry
     * @return a modified builder
     */
    public Builder read(DataEntry entry);

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

}
