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
package org.briljantframework.data.dataframe;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.briljantframework.array.BooleanArray;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.EntryReader;
import org.briljantframework.data.series.Series;
import org.briljantframework.data.series.Type;

/**
 * A DataFrame is a 2-dimensional storage of data consisting of (indexed) <em>columns</em> and
 * (indexed) <em>rows</em>. Rows are denoted as records and columns as columns.
 * 
 * <p/>
 * Columns and records can either be accessed by their intrinsic location using {@link #loc()} or by
 * their index e.g, using {@link #get(Object)} and {@link #ix()}
 *
 * @author Isak Karlsson
 */
public interface DataFrame {

  static DataFrame of(Object name, Series c) {
    return ColumnDataFrame.of(name, c);
  }

  static DataFrame of(Object n1, Series v1, Object n2, Series v2) {
    return ColumnDataFrame.of(n1, v1, n2, v2);
  }

  static DataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3) {
    return ColumnDataFrame.of(n1, v1, n2, v2, n3, v3);
  }

  static DataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3, Object n4,
      Series v4) {
    return ColumnDataFrame.of(n1, v1, n2, v2, n3, v3, n4, v4);
  }

  static DataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3, Object n4,
      Series v4, Object n5, Series v5) {
    return ColumnDataFrame.of(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5);
  }

  static DataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3, Object n4,
      Series v4, Object n5, Series v5, Object n6, Series v6) {
    return ColumnDataFrame.of(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5, n6, v6);
  }

  static DataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3, Object n4,
      Series v4, Object n5, Series v5, Object n6, Series v6, Object n7, Series v7) {
    return ColumnDataFrame.of(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5, n6, v6, n7, v7);
  }

  static DataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3, Object n4,
      Series v4, Object n5, Series v5, Object n6, Series v6, Object n7, Series v7, Object n8,
      Series v8) {
    return ColumnDataFrame.of(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5, n6, v6, n7, v7, n8, v8);
  }

  /**
   * Creates a data frame from the given keys and values.
   *
   * @param entries the keys and values with which the dataframe is populated
   * @return a newly created {@code DataFrame}
   */
  @SafeVarargs
  @SuppressWarnings("unchecked")
  static DataFrame fromEntries(Map.Entry<Object, ? extends Series> entry,
      Map.Entry<Object, ? extends Series>... entries) {
    return ColumnDataFrame.fromEntries(entry, entries);
  }

  static DataFrame.Builder newBuilder(List<Class<?>> types) {
    DataFrame.Builder builder = newBuilder();
    for (Class<?> type : types) {
      builder.addColumn(Series.Builder.of(type));
    }
    return builder;
  }

  static DataFrame.Builder newBuilder(Class... cls) {
    DataFrame.Builder builder = newBuilder();
    for (Class c : cls) {
      builder.addColumn(Series.Builder.of(c));
    }
    return builder;
  }

  /**
   * Create a new builder.
   *
   * @return a new data frame builder
   */
  static DataFrame.Builder newBuilder() {
    return ColumnDataFrame.builder();
  }

  /**
   * Create a new entry
   *
   * @param key the column key
   * @param series the column
   * @return a new key series holder
   */
  static KeyVectorHolder entry(Object key, Series series) {
    return new KeyVectorHolder(key, series);
  }

  /**
   * Return a new data frame indexed on the values of the column specified by {@code key}.
   * <p/>
   * An exception is raised if the specified column contains duplicates.
   *
   * @param key the column
   * @return a new data frame index on the specified column
   */
  DataFrame indexOn(Object key);

  DataFrame reindex(Index columnIndex, Index index);

  /**
   * Apply the supplied operation to each value in the data frame.
   *
   * <pre>
   * DataFrame df = DataFrame.of("A", Series.of(1,2,3), "B", Series.of("a","b","c");
   * df.map(o -> {
   *   if(o instanceof String) {
   *     return ((String)o).toUpperCase();
   *   } else if(o instanceof Integer){
   *     return (Integer)o * 2
   *   } else {
   *     return o;
   *   }
   * });
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
   * @param operator the function to apply
   * @return a new data frame
   */
  default DataFrame map(UnaryOperator<Object> operator) {
    return map(Object.class, operator);
  }

  /**
   * Apply {@code mapper} to each value, where the value is an instance of the supplied class, in
   * the data frame.
   *
   * <pre>
   * DataFrame df = DataFrame.of("A", Series.of(1,2,3),
   *                             "B", Series.of("a","b","c");
   * df.map(Integer.class, i -> i * 2);
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
   * For each column, perform the specified transformation.
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(1, 2, 3), &quot;B&quot;, Series.of(1, 2, 3));
   * df.apply(a -&gt; a.filter(Integer.class, i -&gt; i &gt;= 2).map(Integer.class, i -&gt; i * 2));
   * </pre>
   *
   * produces
   *
   * <pre>
   *    A  B
   * 0  4  4
   * 1  6  6
   * 
   * [2 rows x 2 columns]
   * </pre>
   *
   * @param transform the transformation
   * @return a new dataframe
   * @see #apply(Class, Collector) a more efficient implementation
   */
  DataFrame apply(Function<? super Series, ? extends Series> transform);

  /**
   * For each column, perform the specified collector transformation using each element.
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(1, 2, 3), &quot;B&quot;, Series.of(1, 2, 3));
   * df.apply(Integer.class, Collectors.each(2));
   * </pre>
   *
   * produces,
   *
   * <pre>
   *    A  B
   * 0  1  1
   * 1  1  1
   * 2  2  2
   * 3  2  2
   * 4  3  3
   * 5  3  3
   *
   * [6 rows x 2 columns]
   * </pre>
   *
   * @param cls the type of value to transform
   * @param collector the collector
   * @param <T> the type of the input to the collector
   * @param <C> the the mutable aggregate
   * @return a new dataframe with each column transformed
   */
  <T, C> DataFrame apply(Class<T> cls, Collector<? super T, C, ? extends Series> collector);

  /**
   * Select only the rows for which he predicate returns true
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(1, 2, null), &quot;B&quot;, Series.of(1, null, 3));
   * df.filter(Series::hasNA);
   * </pre>
   *
   * produces,
   *
   * <pre>
   *    A   B
   * 1  2   NA
   * 2  NA  3
   *
   * [2 rows x 2 columns]
   * </pre>
   *
   * @param predicate the predicate indicating inclusion
   * @return a dataframe with only the records for which the predicate returns true is present
   */
  DataFrame filter(Predicate<? super Series> predicate);

  DataFrame filter(Collection<?> keys);

  /**
   * Return a boolean array where the value of the predicate applied to each value is stored
   *
   * @param predicate the predicate
   * @return a boolean array
   */
  default BooleanArray where(Predicate<Object> predicate) {
    return where(Object.class, predicate);
  }

  /**
   * Return a boolean array ({@code [n-rows, n-columns]}, where the value of the predicate applied
   * to each value is stored
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(1, 2, 3), &quot;B&quot;, Series.of(1, 2, 3));
   * df.where(Integer.class, i -&gt; i &gt; 2);
   * </pre>
   *
   * produces,
   *
   * <pre>
   * array([[0, 0],
   *        [0, 0],
   *        [1, 1]])
   * </pre>
   *
   * @param cls type of element
   * @param predicate the predicate (receives {@code NA}, if conversion to the specified class
   *        fails)
   * @param <T> the type
   * @return a boolean array where each value denotes the truth of the predicate for a value
   */
  <T> BooleanArray where(Class<T> cls, Predicate<? super T> predicate);

  /**
   * Reduce all columns, applying {@code op} with the initial value {@code init}.
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(1, 2, 3), &quot;B&quot;, Series.of(1, 2, 3));
   * df.reduce(Integer.class, 0, Integer::sum);
   * </pre>
   *
   * produces,
   *
   * <pre>
   * A 6
   * B 6
   * </pre>
   *
   * @param <T> the type
   * @param cls the class
   * @param init the initial value
   * @param op the operation
   * @return a record with the reduced values
   */
  <T> Series reduce(Class<? extends T> cls, T init, BinaryOperator<T> op);

  /**
   * Group this data frame based on the values of specified column.
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(1, 2, 1, 2), &quot;B&quot;, Series.of(30, 2, 33, 6));
   * DataFrameGroupBy groups = df.groupBy(&quot;A&quot;);
   * groups.apply(v -&gt; v.minus(v.mean()));
   * groups.get(1);
   * </pre>
   *
   * produces,
   *
   * <pre>
   *     A  B
   *  0  1  -1.5
   *  1  2  -2.0
   *  2  1  1.5
   *  3  2  2.0
   *
   *  [4 rows x 2 columns]
   *
   *     A  B
   *  0  1  30.0
   *  2  1  33.0
   *
   * [2 rows x 2 columns]
   * </pre>
   *
   *
   * @param column the specified column
   * @return a grouped dataframe
   */
  DataFrameGroupBy groupBy(Object column);

  /**
   * Group this data frame based on the values in the specified column as instances of the specified
   * class transformed using the specified function.
   *
   * <pre>
   *   DataFrame df = DataFrame.of("A", Series.of(1, 2, 10, 20), "B", Series.of("a", "b", "c", "d"));
   *   df.groupBy(String.class, "A", String::length).get(2)
   * </pre>
   *
   * produces,
   *
   * <pre>
   *    A   B
   * 2  10  c
   * 3  20  d
   *
   * [2 rows x 2 columns]
   * </pre>
   *
   * @param <T> the type
   * @param cls the class
   * @param map the mapper
   * @param column the column
   * @return a grouped data frame
   */
  <T> DataFrameGroupBy groupBy(Class<T> cls, Function<? super T, Object> map, Object column);

  /**
   * Group the data frame based on the concatenation of the values in the specified columns. This is
   * equivalent to:
   *
   * <pre>
   * df.groupBy(Series::asList, &quot;A&quot;, &quot;B&quot;);
   * </pre>
   *
   * @param keys the specified columns
   * @return a grouped data frame
   */
  DataFrameGroupBy groupBy(Collection<?> keys);

  /**
   * Group the data frame based on the function application over the combination of the values in
   * the specified columns.
   *
   * <pre>
   * // @formatter:off
   *  DataFrame df = DataFrame.fromEntries(
   *      entry(&quot;A&quot;, Series.of(1, 2, 3, 4)),
   *      entry(&quot;B&quot;, Series.of(1, 1, 0, 4)),
   *      entry(&quot;C&quot;, Series.of(1, 1, 0, 4))
   *  );
   *
   * for (Group group : df.groupBy(Series::mean, &quot;A&quot;, &quot;B&quot;)) {
   *   System.out.println(&quot;key: &quot; + group.getKey());
   *   System.out.println(group.getData());
   * }
   * // @formatter:on
   * </pre>
   *
   * produces,
   *
   * <pre>
   * key: 1.0
   *    A  B  C
   * 0  1  1  1
   *
   * [1 rows x 3 columns]
   *
   * key: 4.0
   *    A  B  C
   * 3  4  4  4
   *
   * [1 rows x 3 columns]
   *
   * key: 1.5
   *    A  B  C
   * 1  2  1  1
   * 2  3  0  0
   *
   * [2 rows x 3 columns]
   * </pre>
   *
   * @param combiner the function to combine the column values
   * @param keys the columns to select
   * @return a grouped data frame
   */
  DataFrameGroupBy groupBy(Function<? super Series, Object> combiner, Collection<?> keys);

  /**
   * Group data frame based on the value of the index returned by {@code keyFunction}. Each record
   * in the data frame is used for grouping.
   *
   * <p>
   * Example, given a {@link java.time.LocalDate}-indexed {@code DataFrame}, the data frame can be
   * grouped based on year:
   *
   * <pre>
   * df.groupBy(k -&gt; LocalDate.class.cast(v).getYear());
   * </pre>
   *
   * @param keyFunction the key function
   * @return a group by data frame
   */
  DataFrameGroupBy groupBy(UnaryOperator<Object> keyFunction);

  /**
   * Group data frame based on the value returned by the specified function given a value of the
   * specified class. {@code NA}-values are partitioned separately.
   *
   * <pre>
   * df.groupBy(LocalData.class, LocalData::getYear);
   * </pre>
   *
   * @param cls the class to cast the index keys to
   * @param keyFunction the function to transform the index keys
   * @param <T> the type
   * @return a grouped data frame
   */
  <T> DataFrameGroupBy groupBy(Class<T> cls, Function<? super T, ?> keyFunction);

  /**
   * Set the specified column to the given series
   *
   * @param key the key
   * @param column the column
   * @return a new data frame
   */
  void set(Object key, Series column);

  /**
   * Set the specified column(s) to the specified vectors
   *
   * @param columns the map of keys to vectors
   */
  void setAll(Map<?, Series> columns);

  /**
   * Get the specified column
   *
   * @param key the column key
   * @return the column
   * @throws java.util.NoSuchElementException if key is not found
   */
  Series get(Object key);

  /**
   * Select the specified columns
   *
   * @param keys the keys
   * @return a new data frame
   */
  DataFrame getAll(Collection<?> keys);

  /**
   * Drop the columns with the specified keys
   *
   * @param key the keys
   * @return a new data frame
   */
  DataFrame drop(Object key);

  /**
   * Drop the columns with the specified keys
   *
   * @param keys the keys
   * @return the keys
   */
  DataFrame dropAll(Collection<?> keys);

  /**
   * Drop the columns for which the specified predicate returns true.
   *
   * <pre>
   * DataFrame df =
   *     DataFrame.of(&quot;A&quot;, Series.of(1, 2, 3), &quot;B&quot;, Series.of(1, null, 3)).drop(Series::hasNA);
   * </pre>
   *
   * produces,
   *
   * <pre>
   *    A
   * 0  1
   * 1  2
   * 2  3
   *
   * [1 rows x 3 columns]
   * </pre>
   *
   * @param predicate the predicate
   * @return a new data frame
   */
  DataFrame dropIf(Predicate<? super Series> predicate);

  /**
   * @return a series with the diagonal entries
   */
  Series getDiagonal();

  /**
   * The result is different depending on the shape of the input array. For vectors of shape
   * {@code [n-rows]}, the rows for which the series is true are selected. For matrices of shape
   * {@code [n-rows, n-columns]}, the values for which the array is false are changed to {@code NA}.
   *
   * For example,
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(1, 2, 3), &quot;B&quot;, Series.of(1, 2, 3));
   * df.get(df.where(Integer.class, i -&gt; i &gt; 1));
   * </pre>
   *
   * produces
   *
   * <pre>
   *    A   B
   * 0  NA  NA
   * 1  2   2
   * 2  3   3
   * </pre>
   *
   * whereas
   *
   * <pre>
   * df.get(BooleanArray.of(false, true, true);
   * </pre>
   *
   * produces,
   *
   * <pre>
   *    A   B
   * 1  2   2
   * 2  3   3
   * </pre>
   *
   *
   * @param array the boolean array
   * @return a new data frame
   */
  DataFrame get(BooleanArray array);

  /**
   * Set the values for which the boolean array is true (the boolean array has the same shape as for
   * {@linkplain #get(BooleanArray)}).
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(1, 2, 3), &quot;B&quot;, Series.of(1, 2, 3));
   * df.set(df.where(Integer.class, i -> i > 1), 30)
   * </pre>
   *
   * produces,
   *
   * <pre>
   *    A   B
   * 0  1   1
   * 1  30  30
   * 2  30  30
   *
   * [3 rows x 2 columns]
   * </pre>
   *
   * @param array the array
   * @param value the value
   * @return a new data frame where the new value is set
   */
  void set(BooleanArray array, Object value);

  /**
   * Return a new data frame with the first {@code n} records
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(1, 2, 3, 4));
   * df.limit(2)
   * </pre>
   *
   * produces,
   *
   * <pre>
   *    A
   * 0  1
   * 1  2
   * </pre>
   *
   * Note that the first {@code n} rows in the order of the index are returned.
   *
   * @param n the number of rows
   * @return a new data frame
   */
  DataFrame limit(int n);

  /**
   * Transpose the data frame, i.e. turning its columns into rows and its rows into columns.
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(1, 2, 3, 4));
   * df.setIndex(Index.of(&quot;a&quot;, &quot;b&quot;, &quot;c&quot;, &quot;d&quot;));
   * df.transpose();
   * </pre>
   *
   * produces,
   *
   * <pre>
   *    a  b  c  d
   * A  1  2  3  4
   *
   * [1 rows x 4 columns]
   * </pre>
   *
   * @return a new transposed data frame
   */
  DataFrame transpose();

  /**
   * Get the element with the specified row and column index as an instance of {@link Object}.
   *
   * @param row the row index
   * @param col the column index
   * @return the specified value
   */
  default Object get(Object row, Object col) {
    return get(Object.class, row, col);
  }

  void set(Object row, Object col, Object value);

  /**
   * Get the element with the specified row and column index as an instance of the specified class
   * or {@code NA} if the conversion fails. For information on how values are converted see
   * {@link org.briljantframework.data.series.Convert#to(Class, Object)}.
   *
   * @param cls the class of the returned values
   * @param row the row index
   * @param col the column index
   * @param <T> the type of the returned value
   * @return the specified value
   */
  <T> T get(Class<T> cls, Object row, Object col);

  /**
   * Get the element at the specified position as a primitive {@code double} value. With the right
   * support in the data frame this avoids unboxing.
   *
   * @param row the row index
   * @param col the column index
   * @return the specified element
   */
  double getDouble(Object row, Object col);

  /**
   * Get the element at the specified position as a primitive {@code in} value. With the right
   * support in the data frame this avoids unboxing.
   *
   * @param row the row index
   * @param col the column index
   * @return the specified element
   */
  int getInt(Object row, Object col);

  /**
   * Return {@code true} if the element at the specified position is {@code NA}. This is in some
   * case more efficient than {@code Is.NA(df.get(row, col));} (e.g., when dealing with primitive
   * values)
   *
   * @param row the row index
   * @param col the colum index
   * @return {@code true} if the specified position contains {@code NA}
   */
  boolean isNA(Object row, Object col);

  int rows();

  int columns();

  /**
   * Returns a copy of this data frame.
   *
   * @return a copy
   */
  DataFrame copy();

  // / Indexing

  /**
   * Reset the index of this data frame. This will create a new data frame with an additional column
   * named {@code index} with the values of the current index. If such column already exists, an
   * exception is raised.
   *
   * <pre>
   * DataFrame df = DataFrame.builder().setRecord("a", Series.of(1, 2)).setRecord("b", Series.of(1, 2)).build();
   * df.resetIndex()
   * </pre>
   *
   * produces,
   *
   * <pre>
   *    index  0  1
   * 0  a      1  2
   * 1  b      1  2
   *
   * [2 rows x 3 columns]
   * </pre>
   *
   * @return a new data frame where the index is reset
   */
  DataFrame resetIndex();

  /**
   * Get the index of the records of this data frame.
   *
   * <pre>
   * DataFrame df =
   *     DataFrame.builds().setRecord(&quot;A&quot;, Series.of(1, 2)).setRecord(&quot;B&quot;, Series.of(1, 2)).build();
   * df.getIndex().getLocation(&quot;A&quot;);
   * </pre>
   *
   * produces, {@code 0}.
   *
   * @return the records index
   */
  Index getIndex();

  /**
   * Set the index of the records of this data frame. This won't change the data in the data frame
   * only the indexing. As a consequence, it is unadvised to change the index in a multithreaded
   * application.
   *
   * @param index the record index
   */
  void setIndex(Index index);

  /**
   * Get the index for the columns of this data frame
   *
   * @return the column index
   */
  Index getColumnIndex();

  /**
   * Set the index of the columns of this data frame. This won't change the data in the data frame
   * only the indexing. As a consequence, it is unadvised to change the index in a multithreaded
   * application.
   *
   * @param index the column index
   */
  void setColumnIndex(Index index);

  /**
   * Return an indexer that allows integer based location indexing of the data frame irrespective of
   * the current index assigned.
   *
   * @return a location getter
   */
  LocationIndexer loc();

  LabelIndexer ix();

  /**
   * <p>
   * Reduce every column by applying a (summarizing) function over the series. The reduction
   * function must return a value which is an instance of the series, i.e. if the i:th series is a
   * double-series it must return a {@code double}. The type of the returned value will is
   * determined of the columns. If all columns share the same type, the returned series will have
   * that type; otherwise the most general series will be returned.
   *
   * <pre>
   * DataFrame df = ColumnDataFrame.of(&quot;a&quot;, Series.of(1, 2, 3), &quot;b&quot;, Series.of(10, 20, 30));
   * Series means = df.reduce(Vectors::mean);
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
  Series reduce(Function<Series, ?> op);

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
   * @return a series of aggregated values
   */
  <T, C> Series collect(Class<T> cls, Collector<? super T, C, ?> collector);

  /**
   * Return a list of columns.
   *
   * @return an (immutable) collection of columns
   */
  List<Series> getColumns();

  /**
   * Return a list of rows.
   *
   * @return an (immutable) collection of rows
   */
  List<Series> getRows();

  /**
   * Return a stream over the records in this data frame. For example, we could filter the records
   * with missing values
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(&quot;a&quot;, &quot;b&quot;, &quot;c&quot;), &quot;B&quot;, Series.of(1, null, 3));
   * df.stream().filter(Series::hasNA).collect(Collectors.toDataFrame())
   * </pre>
   *
   * produces,
   *
   * <pre>
   *    0   1
   * 0  b   NA
   * 1  NA  3
   *
   * [2 rows x 2 columns]
   * </pre>
   *
   * Note that the index information is lost, to retain such information use e.g.,
   * {@link #filter(Predicate)}.
   *
   * @return a stream of the rows of this data frame
   */
  default Stream<Series> stream() {
    return StreamSupport.stream(getRows().spliterator(), false);
  }

  /**
   * Return a parallel stream of the rows of this data frame
   *
   * @return a parallel stream of the rows of this data frame
   */
  default Stream<Series> parallelStream() {
    return StreamSupport.stream(getRows().spliterator(), true);
  }

  /// builder

  /**
   * Creates a new builder for creating new data frames which produces the concrete implementation
   * of {@code this}
   *
   * @return a new builder
   */
  default Builder newEmptyBuilder() {
    return newEmptyBuilder(false);
  }

  /**
   * Creates a new builder for creating new data frames which produces the concrete implementation
   * of {@code this}
   *
   * @return a new builder
   * @param includeHeader include the header
   */
  Builder newEmptyBuilder(boolean includeHeader);

  /**
   * Creates a new builder, initialized with a copy of this data frame, i.e.
   * {@code c.newCopyBuilder().build()} creates a new copy.
   *
   * @return a new builder
   */
  Builder newCopyBuilder();

  /**
   * Since DataFrames are immutable, this builder allows for the creation of new data frames
   */
  interface Builder {

    /**
     * Set the element at the specified position to the value of specified data frame from the
     * specified position
     *
     * @param tr the row index to set
     * @param tc the colum index to set
     * @param from the data frame to get values from
     * @param fr the row index to get from
     * @param fc the column index to get from
     * @return this modified
     */
    Builder setFrom(Object tr, Object tc, DataFrame from, Object fr, Object fc);

    /**
     * Set the element at the specified position to the value of the specified series from the
     * specified position.
     *
     * @param row the row index to set
     * @param column the colum index to set
     * @param from the series to get values from
     * @param key the index to get from
     * @return this modified
     */
    Builder setFrom(Object row, Object column, Series from, Object key);

    /**
     * Set the element at the specified index to the specified value
     *
     * @param row the row index
     * @param column the colum index
     * @param value the value
     * @return this modified
     */
    Builder set(Object row, Object column, Object value);

    /**
     * Set the column at the specified index to the specified series.
     *
     * @param columnKey the column index
     * @param column the series
     * @return this modified
     */
    default Builder setColumn(Object columnKey, Series column) {
      for (Object rowKey : column.index()) {
        setFrom(rowKey, columnKey, column, rowKey);
      }
      return this;
    }

    default Builder setColumn(Object key, Collection<?> column) {
      int i = 0;
      for (Object value : column) {
        set(i++, key, value);
      }
      return this;
    }

    default Builder setAll(Map<?, ? extends Series> columns) {
      for (Map.Entry<?, ? extends Series> e : columns.entrySet()) {
        setColumn(e.getKey(), e.getValue());
      }
      return this;
    }

    /**
     * Set the column at the specified index to the specified series builder. Ignores indexing.
     *
     * @param key the column index
     * @param columnBuilder the series
     * @return this modified
     */
    Builder setColumn(Object key, Series.Builder columnBuilder);

    /**
     * Set the column at the specified index to a new empty series builder of the specified type.
     *
     * @param key the column index
     * @param columnType the type of series
     * @return this modified
     */
    default Builder newColumn(Object key, Type columnType) {
      return setColumn(key, columnType.newBuilder());
    }

    /**
     * Adds a new series builder as an additional column using {@link Type#newBuilder()}
     *
     * @param columnType the type
     * @return receiver modified
     */
    default Builder newColumn(Type columnType) {
      return addColumn(columnType.newBuilder());
    }

    default Builder newRow(Object key, Type rowType) {
      setColumn(key, rowType.newBuilder());
      return this;
    }

    default Builder newRow(Type rowType) {
      return addRow(rowType.newBuilder());
    }

    /**
     * Add a new series. If the {@code series.size() < rows()}, the resulting series is padded with
     * NA.
     *
     * @param column the series
     * @return a modified builder
     */
    default Builder addColumn(Series column) {
      return addColumn(column.newCopyBuilder());
    }

    default Builder addColumn(Collection<?> column) {
      return addColumn(Series.copyOf(column));
    }

    /**
     * Add a new series builder as an additional column. If {@code builder.size() < rows()} the
     * added builder is padded with NA.
     *
     * @param columnBuilder builder to plus
     * @return a modified builder
     */
    Builder addColumn(Series.Builder columnBuilder);

    /**
     * Add all series builders
     *
     * @param vectors add all the series builders
     * @return a modified builder
     */
    default Builder addColumns(Collection<? extends Series.Builder> vectors) {
      for (Series.Builder vector : vectors) {
        addColumn(vector);
      }
      return this;
    }

    /**
     * Set the row at the specified position to the given series.
     *
     * @param key the row key
     * @param row the series
     * @return this modified
     */
    default Builder setRow(Object key, Series row) {
      return setRow(key, row.newCopyBuilder());
    }

    default Builder setRow(Object key, Collection<?> values) {
      return setRow(key, Series.copyOf(values));
    }

    /**
     * Set the row at the specified position to the given series builder
     *
     * @param key the row key
     * @param rowBuilder the row
     * @return this modified
     */
    Builder setRow(Object key, Series.Builder rowBuilder);

    /**
     * Adds a new row. If {@code series.size() < columns()}, left-over columns are padded with NA.
     *
     * @param series the series
     * @return this modified
     */
    default Builder addRow(Series series) {
      return addRow(series.newCopyBuilder());
    }

    default Builder addRow(Collection<?> values) {
      return addRow(Series.copyOf(values));
    }


    /**
     * Adds a new row. If {@code builder.size() < columns()}, left-over columns are padded with NA.
     *
     * @param builder the builder
     * @return receiver modified
     */
    Builder addRow(Series.Builder builder);

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

    int rows();

    int columns();

    /**
     * Create a new DataFrame.
     *
     * @return a new data frame
     */
    DataFrame build();
  }

  final class KeyVectorHolder implements Map.Entry<Object, Series> {

    private final Object key;
    private final Series value;

    KeyVectorHolder(Object key, Series value) {
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
    public Series getValue() {
      return value;
    }

    /**
     * Throws {@link UnsupportedOperationException}.
     *
     * @param value ignored
     * @return never returns normally
     */
    @Override
    public Series setValue(Series value) {
      throw new UnsupportedOperationException();
    }
  }

}
