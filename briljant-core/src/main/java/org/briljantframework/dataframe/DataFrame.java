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

package org.briljantframework.dataframe;

import org.briljantframework.array.Array;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.dataframe.join.JoinType;
import org.briljantframework.index.DataFrameLocationGetter;
import org.briljantframework.index.DataFrameLocationSetter;
import org.briljantframework.index.Index;
import org.briljantframework.index.Ix;
import org.briljantframework.io.EntryReader;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p> A DataFrame is a heterogeneous storage of data. </p>
 *
 * @author Isak Karlsson
 */
public interface DataFrame extends Iterable<Vector> {

  /**
   * Sort the data frame according to the comparator and the vector at the specified position.
   *
   * @param cls the type of values to sort on
   * @param cmp the comparator
   * @param key the column
   * @param <T> the type
   * @return a new data frame sorted according to the specified comparator
   */
  <T> DataFrame sort(Class<? extends T> cls, Comparator<? super T> cmp, Object key);

  /**
   * Sort the data frame in the order specified by {@code order} using the specified column.
   *
   * @param order the order
   * @param key   the column
   * @return a new sorted data frame
   */
  DataFrame sort(SortOrder order, Object key);

  /**
   * Equivalent to {@code sort(SortOrder.ASC, key)}
   *
   * @see #sort(SortOrder, Object)
   */
  DataFrame sort(Object key);

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

  default DataFrame join(JoinType type, DataFrame other, Object key) {
    return join(type, other, getColumnIndex().getLocation(key));
  }

  default DataFrame join(JoinType type, DataFrame other, Object... keys) {
    return join(type, other, getColumnIndex().indices(keys));
  }

  default <T> DataFrame apply(Class<? extends T> cls, UnaryOperator<T> op, Object key) {
    return apply(cls, op, getColumnIndex().getLocation(key));
  }

  default <T> DataFrame apply(Class<? extends T> cls, UnaryOperator<T> op, Object... keys) {
    return apply(cls, op, getColumnIndex().indices(keys));
  }

  /**
   * <p> Apply {@code op} to value. If {@code op} returns {@code NA}, the old value is kept.
   *
   * @param cls the type of values to transform
   * @param op  the operation
   * @param <T> the type
   * @return a new data frame
   */
  <T> DataFrame apply(Class<? extends T> cls, UnaryOperator<T> op);

  /**
   * <p> Reduce all columns, applying {@code op} with the initial value {@code init}.
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
   * <p> Note that {@link org.briljantframework.function.Aggregates} implement several convenient
   * aggregates, for example {@code df.aggregate(Number.class, Aggregate.median())}.
   *
   * @param <T>       the type of value to be aggregated
   * @param <C>       the type of the mutable collector
   * @param cls       the class
   * @param collector the collector
   * @return a vector of aggregated values
   */
  <T, C> Vector collector(Class<T> cls, Collector<? super T, C, ? extends T> collector);

  <T, R, C> Vector collect(Class<T> in, Class<R> out,
                           Collector<? super T, C, ? extends R> collector);

  default DataFrameGroupBy groupBy(Object key) {
    return groupBy(getColumnIndex().getLocation(key));
  }

  /**
   * <p> Group data frame based on the value returned by {@code keyFunction}. Each record in the
   * data frame is used for grouping.
   *
   * <p> The result of {@link #groupBy(Object);} can be implemented as {@code groupBy(v ->
   * v.get(Object.class, index))}
   *
   * @param keyFunction the key function
   * @return a group by data frame
   */
  DataFrameGroupBy groupBy(Function<? super Vector, Object> keyFunction);

  DataFrame transform(Function<? super Vector, ? extends Vector> transform);

  DataFrame add(Vector column);

  /**
   * Uses the column name to lookup a specified column.
   *
   * @param key the column name
   * @return the column
   * @throws java.lang.IllegalArgumentException if key is not found
   */
  Vector get(Object key);

  DataFrame get(Object... keys);

  DataFrame dropna();

  DataFrame drop(Object key);

  DataFrame drop(Object... keys);

  DataFrame drop(Predicate<? super Vector> predicate);

  Vector getRecord(Object key);

  /**
   * Return a collection of columns
   *
   * @return an (immutable) collection of columns
   */
  Collection<Vector> getColumns();

  /**
   * Returns a collection of records.
   *
   * @return an (immutable) collection of rows
   */
  Collection<Vector> getRecords();

  /**
   * Drop rows in {@code indexes} and return a new DataFrame
   *
   * @param indexes the indexes to drop
   * @return a new data frame
   */
  DataFrame removeRecords(Collection<Integer> indexes);

  DataFrame addRecord(Vector record);

  /**
   * {@code stack} {@code DataFrames} on top of each other.  All DataFrames in {@code dataFrames}
   * must have the same number of columns.
   *
   * @param dataFrames the data frames to stack.
   * @return a new data data frame
   */
  DataFrame stack(Iterable<DataFrame> dataFrames);

  /**
   * {@code concat}enate {@code DataFrames} side-by-side.
   *
   * @param dataFrames the data frames to stack.
   * @return a new data frame
   */
  DataFrame concat(Iterable<DataFrame> dataFrames);

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
   * Returns {@code this} DataFrame as a real valued matrix.
   *
   * @return this data frame as a matrix
   */
  Array<Object> toArray();

  DoubleArray toDoubleArray();

  default Stream<Vector> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  default Stream<Vector> parallelStream() {
    return StreamSupport.stream(spliterator(), true);
  }

  DataFrame resetIndex();

  Index getRecordIndex();

  Index getColumnIndex();

  void setRecordIndex(Index index);

  void setColumnIndex(Index index);

  default Ix getIx() {
    return new IxImpl(this);
  }

  DataFrameLocationGetter loc();

  /**
   * Since DataFrames are immutable, this builder allows for the creation of new data frames
   */
  interface Builder {

    DataFrameLocationSetter loc();

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
     * Adds a new vector builder as an additional column using {@link org.briljantframework.vector.VectorType#newBuilder()}
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

    /**
     * Concatenates the row at {@code toRow} with {@code vector} starting at {@code startCol}. If
     * {@code startCol < columns()}, values will be overwritten.
     *
     * @param toRow    the row to concat {@code vector} with
     * @param startCol the starting index in {@code toRow}
     * @param vector   the vector to concat
     * @return receiver modified
     */
    default Builder concat(int toRow, int startCol, Vector vector) {
      if (startCol > columns() || startCol < 0 || toRow > rows() || toRow < 0) {
        throw new IndexOutOfBoundsException();
      }

      for (int i = 0; i < vector.size(); i++) {
        if (startCol == columns()) {
          add(vector.getType(i).newBuilder());
        }

        loc().set(toRow, startCol++, vector, i);
      }
      return this;
    }

    /**
     * Same as {@code concat(toRow, columns(), vector)}
     *
     * @param toRow  the row concat {@code vector} with
     * @param vector the vector to concat
     * @return receiver modified
     */
    default Builder concat(int toRow, Vector vector) {
      return concat(toRow, columns(), vector);
    }

    /**
     * Concatenates {@code this} builder with {@code frame}. If {@code startCol < columns()} values
     * will be overwritten. <p> For example, a builder representing:
     * <pre>
     * a   b
     * 2   2
     * 3   5
     * 3   5</pre>
     *
     * concatenated with
     * <pre>
     * c   d
     * a   b
     * c   d</pre>
     *
     * results in
     * <pre>
     * a   b   c   d
     * 2   2   a   b
     * 3   5   c   d
     * 3   5   NA  NA</pre>
     *
     * (assuming that startCol = columns())
     * </pre>
     *
     * @param startCol the starting column
     * @param frame    the data frame to concatenate
     * @return receiver modified
     */
    default Builder concat(int startCol, DataFrame frame) {
      for (int i = 0; i < frame.columns(); i++) {
//        if (frame.getColumnNames().containsKey(i)) {
//          getColumnNames().put(startCol + i, frame.getColumnName(i));
//        }
      }
      for (int i = 0; i < rows(); i++) {
        concat(i, startCol, frame.loc().getRecord(i));
      }
      return this;
    }

    /**
     * @param frame the data frame
     * @return receiver modified
     */
    default Builder concat(DataFrame frame) {
      return concat(columns(), frame);
    }

    default Builder stack(int toCol, Vector vector) {
      return stack(rows(), toCol, vector);
    }

    /**
     * Add all values in {@code vector} to column {@code toCol}, starting at {@code startRow}. If
     * {@code startRow < rows()}, values are overwritten.
     *
     * @param startRow the start row
     * @param toCol    the index
     * @param vector   the vector
     * @return a modified builder
     */
    default Builder stack(int startRow, int toCol, Vector vector) {
      for (int i = 0; i < vector.size(); i++) {
        loc().set(startRow++, toCol, vector, i);
      }
      return this;
    }

    default Builder stack(DataFrame frame) {
      return stack(rows(), frame);
    }

    /**
     * Add all values from frame (from column 0 until column()) starting at {@code startRow}. If
     * {@code startRow < rows()}, values are overwritten.
     *
     * @param frame the frame
     * @return a modified builder
     */
    default Builder stack(int startRow, DataFrame frame) {
      for (int i = 0; i < frame.columns(); i++) {
        stack(startRow, i, frame.loc().get(i));
      }
      return this;
    }

    /**
     * Read values from the {@code inputStream} and add the values to the correct column.
     *
     * @param entryReader the input stream
     * @return a modified builder
     */
    public Builder read(EntryReader entryReader) throws IOException;

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
