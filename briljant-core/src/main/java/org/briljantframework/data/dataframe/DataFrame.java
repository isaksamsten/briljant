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

import org.briljantframework.array.BooleanArray;
import org.briljantframework.data.Is;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.dataframe.join.JoinType;
import org.briljantframework.data.index.DataFrameLocationGetter;
import org.briljantframework.data.index.DataFrameLocationSetter;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.ObjectComparator;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.EntryReader;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Type;
import org.briljantframework.primitive.ArrayAllocations;

/**
 * A DataFrame is a 2-dimensional storage of data consisting of (indexed) <em>columns</em> and
 * (indexed) <em>rows</em>. Rows are denoted as records and columns as columns.
 * 
 * <p/>
 * Columns and records can either be accessed by their intrinsic location using {@link #loc()} or by
 * their index e.g, using {@link #get(Object)} and {@link #getRecord(Object)}.
 *
 * @author Isak Karlsson
 */
public interface DataFrame extends Iterable<Object> {

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
  @SafeVarargs
  @SuppressWarnings("unchecked")
  static DataFrame fromEntries(Map.Entry<Object, ? extends Vector> entry,
      Map.Entry<Object, ? extends Vector>... entries) {
    return MixedDataFrame.fromEntries(ArrayAllocations.prepend(entry, entries));
  }

  static DataFrame.Builder builder(Class... cls) {
    DataFrame.Builder builder = builder();
    for (Class c : cls) {
      builder.add(Vector.Builder.of(c));
    }
    return builder;
  }

  static DataFrame.Builder builder() {
    return MixedDataFrame.builder();
  }

  static KeyVectorHolder entry(Object key, Vector vector) {
    return new KeyVectorHolder(key, vector);
  }

  /**
   * Return a <em>shallow copy</em> of the data frame with the index sorted in the specified order.
   *
   * <p/>
   * For example,
   *
   * <pre>
   * {
   *   &#064;code
   *   DataFrame df = DataFrame.of(&quot;a&quot;, Vector.of(3, 2, 1), &quot;b&quot;, Vector.of(2, 9, 0));
   *   df.setIndex(Index.of(&quot;F&quot;, &quot;Q&quot;, &quot;A&quot;));
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

  DataFrame sortColumns(Comparator<Object> comparator);

  /**
   * Equivalent to {@code sort(SortOrder.ASC, key)}
   *
   * @see #sortBy(SortOrder, Object)
   */
  DataFrame sortBy(Object key);

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
  DataFrame sortBy(SortOrder order, Object key);

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
  <T> DataFrame sortBy(Class<? extends T> cls, Comparator<? super T> cmp, Object key);

  /**
   * Return a new data frame indexed on the values of the column specified by {@code key}.
   * <p/>
   * An exception is raised if the specified column contains duplicates.
   *
   * @param key the column
   * @return a new data frame index on the specified column
   */
  DataFrame indexOn(Object key);

  default DataFrame join(DataFrame other) {
    return join(JoinType.INNER, other);
  }

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

  default DataFrame join(DataFrame other, Object key) {
    return join(JoinType.INNER, other, key);
  }

  DataFrame join(JoinType type, DataFrame other, Object key);

  default DataFrame join(DataFrame other, Object... keys) {
    return join(JoinType.INNER, other, keys);
  }

  DataFrame join(JoinType type, DataFrame other, Object... keys);

  /**
   * Apply the supplied operation to each value in the data frame elementwise.
   *
   * <pre>
   * DataFrame df = DataFrame.of("A", Vector.of(1,2,3), "B", Vector.of("a","b","c");
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
   * For each column, perform the specified transformation.
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Vector.of(1, 2, 3), &quot;B&quot;, Vector.of(1, 2, 3));
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
  DataFrame apply(Function<? super Vector, ? extends Vector> transform);

  /**
   * For each column, perform the specified collector transformation using each element.
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Vector.of(1, 2, 3), &quot;B&quot;, Vector.of(1, 2, 3));
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
  <T, C> DataFrame apply(Class<T> cls, Collector<? super T, C, ? extends Vector> collector);

  /**
   * Reduce all columns, applying {@code op} with the initial value {@code init}.
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Vector.of(1, 2, 3), &quot;B&quot;, Vector.of(1, 2, 3));
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
  <T> Vector reduce(Class<? extends T> cls, T init, BinaryOperator<T> op);

  /**
   * Group this data frame based on the values of specified column.
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Vector.of(1, 2, 1, 2), &quot;B&quot;, Vector.of(30, 2, 33, 6));
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
   *   DataFrame df = DataFrame.of("A", Vector.of(1, 2, 10, 20), "B", Vector.of("a", "b", "c", "d"));
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
   * df.groupBy(Vector::asList, &quot;A&quot;, &quot;B&quot;);
   * </pre>
   *
   * @param key the first column
   * @param keys the specified columns
   * @return a grouped data frame
   */
  DataFrameGroupBy groupBy(Object key, Object... keys);

  /**
   * Group the data frame based on the function application over the combination of the values in
   * the specified columns.
   *
   * <pre>
   * // @formatter:off
   *  DataFrame df = DataFrame.fromEntries(
   *      entry(&quot;A&quot;, Vector.of(1, 2, 3, 4)),
   *      entry(&quot;B&quot;, Vector.of(1, 1, 0, 4)),
   *      entry(&quot;C&quot;, Vector.of(1, 1, 0, 4))
   *  );
   * 
   * for (Group group : df.groupBy(Vector::mean, &quot;A&quot;, &quot;B&quot;)) {
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
   * @param key the first column
   * @param keys the columns to select
   * @return a grouped data frame
   */
  DataFrameGroupBy groupBy(Function<? super Vector, Object> combiner, Object key, Object... keys);

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
   * specified class, or {@code NA}, if the index key cannot be cast to the specified class.
   *
   * <pre>
   * df.groupBy(LocalData.class, k -&gt; Is.NA(k) ? 2000 : k.getYear());
   * 
   * // or, if we allow the possibility of error
   * df.groupBy(LocalData.class, LocalData::getYear);
   * </pre>
   *
   * @param cls the class to cast the index keys to
   * @param function the function to transform the index keys (receives {@code NA} if conversion
   *        fails)
   * @param <T> the type
   * @return a grouped data frame
   */
  <T> DataFrameGroupBy groupBy(Class<T> cls, Function<? super T, ?> function);

  /**
   * Get the specified column
   *
   * @param key the column key
   * @return the column
   * @throws java.util.NoSuchElementException if key is not found
   */
  Vector get(Object key);

  /**
   * Return a new data frame where the values of the specificed column is replaced with (a vector)
   * of value
   *
   * @param key the key
   * @param value the value
   * @return a new data frame
   */
  DataFrame set(Object key, Object value);

  /**
   * Set the specified column to the given vector
   *
   * @param key the key
   * @param column the column
   * @return a new data frame
   */
  DataFrame set(Object key, Vector column);

  /**
   * Set the specified column(s) to the specified vectors
   *
   * @param columns the map of keys to vectors
   * @return a new data frame
   */
  DataFrame set(Map<Object, Vector> columns);

  /**
   * Select the specified columns
   *
   * @param keys the column keys
   * @return a new data frame
   */
  DataFrame select(Object... keys);

  /**
   * Select the specified columns
   *
   * @param keys the keys
   * @return a new data frame
   */
  DataFrame select(List<Object> keys);

  /**
   * Drop the columns with the specified keys
   *
   * @param keys the keys
   * @return a new data frame
   */
  DataFrame drop(Object... keys);

  /**
   * Drop the columns with the specified keys
   *
   * @param keys the keys
   * @return the keys
   */
  DataFrame drop(List<Object> keys);

  /**
   * Drop the columns for which the specified predicat returns true.
   *
   * <pre>
   * DataFrame df =
   *     DataFrame.of(&quot;A&quot;, Vector.of(1, 2, 3), &quot;B&quot;, Vector.of(1, null, 3)).drop(Vector::hasNA);
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
  DataFrame drop(Predicate<Vector> predicate);

  /**
   * Get the row with the specified key
   *
   * @param key the key
   * @return a record
   */
  Vector getRecord(Object key);

  DataFrame selectRecords(Object... keys);

  /**
   * Set the specified record to a the specified value (all columns)
   *
   * @param key the record
   * @param value the value
   * @return a new data frame
   */
  DataFrame setRecord(Object key, Object value);

  /**
   * Set the specified record to the specified vector
   *
   * @param key the key
   * @param vector the vector
   * @return a new data frame
   */
  DataFrame setRecord(Object key, Vector vector);

  /**
   * @return a vector with the diagonal entries
   */
  Vector getDiagonal();

  /**
   * @return a boolean array indicating if the value is NA
   */
  default BooleanArray isNA() {
    return where(Is::NA);
  }

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
   * DataFrame df = DataFrame.of(&quot;A&quot;, Vector.of(1, 2, 3), &quot;B&quot;, Vector.of(1, 2, 3));
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
   * The result is different depending on the shape of the input array. For vectors of shape
   * {@code [n-rows]}, the rows for which the vector is true are selected. For matrices of shape
   * {@code [n-rows, n-columns]}, the values for which the array is false are changed to {@code NA}.
   *
   * For example,
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Vector.of(1, 2, 3), &quot;B&quot;, Vector.of(1, 2, 3));
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
   * df.get(Array.of(new boolean[] {false, true, true}));
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
   * DataFrame df = DataFrame.of(&quot;A&quot;, Vector.of(1, 2, 3), &quot;B&quot;, Vector.of(1, 2, 3));
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
  DataFrame set(BooleanArray array, Object value);

  /**
   * Equivalent to {@code limit(10)}
   *
   * @see #limit(int)
   */
  default DataFrame limit() {
    return limit(10);
  }

  /**
   * Return a new data frame with the first {@code n} records
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Vector.of(1, 2, 3, 4));
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
   * DataFrame df = DataFrame.of(&quot;A&quot;, Vector.of(1, 2, 3, 4));
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
   * Select only the records for which he predicate returns true
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Vector.of(1, 2, null), &quot;B&quot;, Vector.of(1, null, 3));
   * df.filter(Vector::hasNA);
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
  DataFrame filter(Predicate<Vector> predicate);

  /**
   * Return a collection of columns
   *
   * @return an (immutable) collection of columns
   */
  List<Vector> getColumns();

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

  /**
   * Get the element with the specified row and column index as an instance of the specified class
   * or {@code NA} if the conversion fails. For information on how values are converted see
   * {@link org.briljantframework.data.vector.Convert#to(Class, Object)}.
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
  double getAsDouble(Object row, Object col);

  /**
   * Get the element at the specified position as a primitive {@code in} value. With the right
   * support in the data frame this avoids unboxing.
   *
   * @param row the row index
   * @param col the column index
   * @return the specified element
   */
  int getAsInt(Object row, Object col);

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
   * Return a stream over the records in this data frame. For example, we could filter the records
   * with missing values
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Vector.of(&quot;a&quot;, &quot;b&quot;, &quot;c&quot;), &quot;B&quot;, Vector.of(1, null, 3));
   * df.stream().filter(Vector::hasNA).collect(Collectors.toDataFrame())
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
   * @return a stream of the records of this data frame
   */
  default Stream<Vector> stream() {
    return StreamSupport.stream(getRecords().spliterator(), false);
  }

  /**
   * Return a parallel stream of the records of this data frame
   *
   * @return a parallel stream of the records of this data frame
   */
  default Stream<Vector> parallelStream() {
    return StreamSupport.stream(getRecords().spliterator(), true);
  }

  /**
   * Return a collection of records.
   *
   * @return an (immutable) collection of rows
   */
  List<Vector> getRecords();

  /**
   * Reset the index of this data frame. This will create a new data frame with an additional column
   * named {@code index} with the values of the current index. If such column already exists, an
   * exception is raised.
   *
   * <pre>
   * DataFrame df = DataFrame.builder().setRecord("a", Vector.of(1, 2)).setRecord("b", Vector.of(1, 2)).build();
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
   *     DataFrame.builds().setRecord(&quot;A&quot;, Vector.of(1, 2)).setRecord(&quot;B&quot;, Vector.of(1, 2)).build();
   * df.getIndex().getLocation(&quot;A&quot;);
   * </pre>
   *
   * produces, {@code 0}.
   *
   * @return the records index
   */
  Index getIndex();

  // / Indexing

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
  DataFrameLocationGetter loc();

  /**
   * Returns a vector of the means of each column in the data frame
   *
   * @return a vector of the means of each column in the data frame
   */
  default Vector mean() {
    return reduce(Vector::mean);
  }

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
   * Returns a vector of the sums of each column in the data frame
   *
   * @return a vector of the sums of each column in the data frame
   */
  default Vector sum() {
    return reduce(Vector::sum);
  }

  /**
   * Returns a vector of the minimum value of each column in the data frame
   *
   * @return a vector of the minimum value of each column in the data frame
   */
  default Vector min() {
    return collect(Object.class, org.briljantframework.data.Collectors
        .withFinisher(Collectors.minBy(ObjectComparator.getInstance()), Optional::get));
  }

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

  /**
   * Returns a vector of the maximum value of each column in the data frame
   *
   * @return a vector of the maximum value of each column in the data frame
   */
  default Vector max() {
    return collect(Object.class, org.briljantframework.data.Collectors
        .withFinisher(Collectors.maxBy(ObjectComparator.getInstance()), Optional::get));
  }

  /**
   * Since DataFrames are immutable, this builder allows for the creation of new data frames
   */
  interface Builder {

    DataFrameLocationSetter loc();

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
    Builder set(Object tr, Object tc, DataFrame from, Object fr, Object fc);

    /**
     * Set the element at the specified position to the value of the specified vector from the
     * specified position.
     *
     * @param row the row index to set
     * @param column the colum index to set
     * @param from the vector to get values from
     * @param key the index to get from
     * @return this modified
     */
    Builder set(Object row, Object column, Vector from, Object key);

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
     * Set the column at the specified index to the specified vector
     *
     * Note that the default implementation copies the given vector to allow the builder to expand.
     * If the number of columns are known use {@linkplain #set(Object, Vector.Builder)} and wrap the
     * vector using {@link org.briljantframework.data.vector.Vectors#transferableBuilder(Vector)}.
     *
     * @param key the column index
     * @param column the vector
     * @return this modified
     */
    default Builder set(Object key, Vector column) {
      return set(key, column.newCopyBuilder());
    }

    /**
     * Set the column at the specified index to the specified vector builder
     *
     * @param key the column index
     * @param columnBuilder the vector
     * @return this modified
     */
    Builder set(Object key, Vector.Builder columnBuilder);

    /**
     * Set the column at the specified index to a new empty vector builder of the specified type.
     *
     * @param key the column index
     * @param columnType the type of vector
     * @return this modified
     */
    default Builder set(Object key, Type columnType) {
      return set(key, columnType.newBuilder());
    }

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
     * Add a new vector builder as an additional column. If {@code builder.size() < rows()} the
     * added builder is padded with NA.
     *
     * @param columnBuilder builder to plus
     * @return a modified builder
     */
    Builder add(Vector.Builder columnBuilder);

    /**
     * Adds a new vector builder as an additional column using
     * {@link Type#newBuilder()}
     *
     * @param columnType the type
     * @return receiver modified
     */
    default Builder add(Type columnType) {
      return add(columnType.newBuilder());
    }

    /**
     * Set the record at the specified position to the given vector. If the affected positions are
     * not affected, {@link org.briljantframework.data.vector.Vectors#transferableBuilder(Vector)}
     * can be used to avoid copying values.
     *
     * @param key the record key
     * @param record the vector
     * @return this modified
     */
    default Builder setRecord(Object key, Vector record) {
      return setRecord(key, record.newCopyBuilder());
    }

    /**
     * Set the record at the specified position to the given vector builder
     *
     * @param key the record key
     * @param recordBuilder the record
     * @return this modified
     */
    Builder setRecord(Object key, Vector.Builder recordBuilder);

    /**
     * Set the record at the specified position to an empty vector of the specified type
     *
     * @param key the record key
     * @param recordType the vector type
     * @return this modified
     */
    default Builder setRecord(Object key, Type recordType) {
      return setRecord(key, recordType.newBuilder());
    }

    /**
     * Adds a new record. If {@code vector.size() < columns()}, left-over columns are padded with
     * NA.
     *
     * @param vector the vector
     * @return this modified
     */
    default Builder addRecord(Vector vector) {
      return addRecord(vector.newCopyBuilder());
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
     * Adds a new empty record of the specified type
     *
     * @param recordType the vector type
     * @return this modified
     */
    default Builder addRecord(Type recordType) {
      return addRecord(recordType.newBuilder());
    }

    /**
     * Remove the column at the specified position
     *
     * @param key the column key
     * @return this modified
     */
    Builder remove(Object key);

    /**
     * Remove the record at the specified position
     *
     * @param key the record key
     * @return this modified
     */
    Builder removeRecord(Object key);

    Vector.Builder get(Object key);

    Vector.Builder getRecord(Object key);

    /**
     * Set the column index
     *
     * @param key the first key
     * @param keys the rest of the keys
     * @return this modified
     */
    default Builder setColumnIndex(Object key, Object... keys) {
      return setColumnIndex(Index.of(ArrayAllocations.prepend(key, keys)));
    }

    /**
     * Set the column index
     *
     * @param columnIndex the column index
     * @return this modified
     */
    Builder setColumnIndex(Index columnIndex);

    /**
     * Set the index
     *
     * @param key the first key
     * @param keys the rest of the keys
     * @return this modified
     */
    default Builder setIndex(Object key, Object... keys) {
      return setIndex(Index.of(ArrayAllocations.prepend(key, keys)));
    }

    /**
     * Set the record index. The index will be resized to match the builder current size.
     *
     * @param index the index
     * @return this modified
     */
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
