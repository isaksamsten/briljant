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

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.array.Array;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.BoundType;
import org.briljantframework.data.Is;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.dataframe.join.JoinType;
import org.briljantframework.data.dataframe.join.JoinUtils;
import org.briljantframework.data.dataframe.join.Joiner;
import org.briljantframework.data.index.DataFrameLocationGetter;
import org.briljantframework.data.index.DataFrameLocationSetter;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.IntIndex;
import org.briljantframework.data.index.ObjectComparator;
import org.briljantframework.data.index.VectorLocationGetter;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.EntryReader;
import org.briljantframework.data.vector.TypeInferenceVectorBuilder;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.sort.QuickSort;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

/**
 * Implements some default behaviour for DataFrames
 *
 * @author Isak Karlsson
 */
public abstract class AbstractDataFrame implements DataFrame {

  private final DataFrameLocationGetterImpl locationIndexer = new DataFrameLocationGetterImpl();
  private ColumnList columnList = null; // Lazy initialization
  private RecordList recordList = null; // Lazy initialization
  private Index index = null;
  private Index columnIndex = null;

  /**
   * @param columnIndex allowed to be {@code null}
   * @param index       allowed to be {@code null}
   */
  protected AbstractDataFrame(Index columnIndex, Index index) {
    this.columnIndex = columnIndex;
    this.index = index;
  }

  @Override
  public final DataFrame sort(SortOrder order) {
    Index.Builder index = getIndex().newCopyBuilder();
    index.sort(order.orderComparator(ObjectComparator.getInstance()));
    return shallowCopy(getColumnIndex(), index.build());
  }

  @Override
  public final DataFrame sort(Comparator<Object> comparator) {
    Index.Builder index = getIndex().newCopyBuilder();
    index.sort(comparator);
    return shallowCopy(getColumnIndex(), index.build());
  }

  @Override
  public final DataFrame sort(Object key) {
    return sort(SortOrder.ASC, key);
  }

  @Override
  public final <T> DataFrame sort(Class<? extends T> cls, Comparator<? super T> cmp, Object key) {
    Vector column = get(key);
    Index.Builder index = getIndex().newCopyBuilder();
    QuickSort.quickSort(
        0, index.size(),
        (a, b) -> cmp.compare(column.get(cls, index.getKey(a)),
                              column.get(cls, index.getKey(b))),
        index::swap);
    return shallowCopy(getColumnIndex(), index.build());
  }

  @Override
  public final DataFrame sort(SortOrder order, Object key) {
    Vector column = get(key);
    Index.Builder index = getIndex().newCopyBuilder();

    // Sort the record index based on the values in the comparator
    QuickSort.quickSort(0, index.size(), (a, b) -> {
      int cmp = column.compare(index.getKey(a), index.getKey(b));
      return order == SortOrder.ASC ? cmp : -cmp;
    }, index::swap);
    return shallowCopy(getColumnIndex(), index.build());
  }

  @Override
  public final DataFrame head(int n) {
    Check.argument(n >= 0 && n <= rows(), "Illegal number of records");
    if (n == rows()) {
      return this;
    }
    DataFrame.Builder builder = newBuilder();
    int i = 0;
    for (Object key : getIndex().keySet()) {
      if (i >= n) {
        break;
      }
      builder.setRecord(key, Vectors.transferableBuilder(getRecord(key)));
      i++;
    }
    DataFrame df = builder.build();
    df.setColumnIndex(getColumnIndex());
    return df;
  }

  @Override
  public final DataFrame indexOn(Object key) {
    ObjectIndex index = ObjectIndex.of(get(key)); // fail fast. throws on duplicates
    DataFrame.Builder builder = newCopyBuilder();
    builder.remove(key);
    DataFrame df = builder.build();
    df.setIndex(index);
    return df;
  }

  @Override
  public DataFrame join(JoinType type, DataFrame other) {
    Joiner joiner = type.getJoinOperation().createJoiner(
        JoinUtils.createJoinKeys(getIndex(), other.getIndex()));
    return joiner.join(this, other, Collections.emptyList());
  }

  @Override
  public DataFrame join(JoinType type, DataFrame other, Object key) {
    return doJoin(type, other, Arrays.asList(key));
  }

  private DataFrame doJoin(JoinType type, DataFrame other, Collection<Object> columns) {
    Joiner joiner = type.getJoinOperation().createJoiner(this, other, columns);
    return joiner.join(this, other, columns);
  }

  @Override
  public final <T> DataFrame map(Class<T> cls, Function<? super T, Object> op) {
    DataFrame.Builder builder = newBuilder();
    for (int j = 0; j < columns(); j++) {
      Vector column = getAt(j);
      VectorLocationGetter loc = column.loc();
      builder.add(new TypeInferenceVectorBuilder());
      for (int i = 0, size = column.size(); i < size; i++) {
        T value = loc.get(cls, i);
        Object transformed = op.apply(value);
        if (Is.NA(transformed)) { // TODO: fix?
          builder.loc().set(i, j, this, i, j);
        } else {
          builder.loc().set(i, j, transformed);
        }
      }
    }
    return builder.setIndex(getIndex()).setColumnIndex(getColumnIndex()).build();
  }

  @Override
  public final <T> Vector reduce(Class<? extends T> cls, T init, BinaryOperator<T> op) {
    Vector.Builder builder = Vector.Builder.of(cls);
    for (int j = 0, columns = columns(); j < columns; j++) {
      VectorLocationGetter column = getAt(j).loc();
      T result = init; // TODO: only include columns whose type is instance of cls
      for (int i = 0, size = rows(); i < size; i++) {
        result = op.apply(result, column.get(cls, i));
      }
      builder.loc().set(j, result);
    }
    Vector build = builder.build();
    build.setIndex(getColumnIndex());
    return build;
  }

  @Override
  public final Vector reduce(Function<Vector, Object> op) {
    Vector.Builder builder = getMostSpecificColumnType().newBuilder();
    for (int j = 0, columns = columns(); j < columns; j++) {
      builder.set(j, op.apply(getAt(j)));
    }
    Vector v = builder.build();
    v.setIndex(getColumnIndex());
    return v;
  }

  @Override
  public final <T, C> Vector collect(Class<T> cls, Collector<? super T, C, ? extends T> collector) {
    return collect(cls, cls, collector);
  }

  @Override
  public final <T, R, C> Vector collect(
      Class<T> in, Class<R> out, Collector<? super T, C, ? extends R> collector) {
    Vector.Builder builder = VectorType.of(out).newBuilder();

    int column = 0;
    for (int j = 0; j < columns(); j++) {
      Vector vec = getAt(j);
      C accumulator = collector.supplier().get();
      for (int i = 0; i < rows(); i++) {
        collector.accumulator().accept(accumulator, vec.loc().get(in, i));
      }
      builder.loc().set(column++, collector.finisher().apply(accumulator));
    }
    Vector v = builder.build();
    v.setIndex(getColumnIndex());
    return v;
  }

  @Override
  public final DataFrameGroupBy groupBy(Object columnKey) {
    HashMap<Object, Vector.Builder> groups = new LinkedHashMap<>();
    Vector column = get(columnKey);
    VectorLocationGetter loc = column.loc();
    for (int i = 0, size = column.size(); i < size; i++) {
      groups.computeIfAbsent(loc.get(Object.class, i),
                             a -> Vector.Builder.of(Integer.class)).add(i);
    }
    return new HashDataFrameGroupBy(this, groups, columnKey);
  }

  @Override
  public final <T> DataFrameGroupBy groupBy(Class<T> cls, Object columnKey,
                                            Function<? super T, Object> map) {
    HashMap<Object, Vector.Builder> groups = new LinkedHashMap<>();
    Vector column = get(columnKey);
    VectorLocationGetter loc = column.loc();
    for (int i = 0, size = column.size(); i < size; i++) {
      groups.computeIfAbsent(map.apply(loc.get(cls, i)),
                             a -> Vector.Builder.of(Integer.class)).add(i);
    }
    return new HashDataFrameGroupBy(this, groups, columnKey);
  }

  @Override
  public final DataFrameGroupBy groupBy(Object... columnKeys) {
    HashMap<Object, Vector.Builder> groups = new LinkedHashMap<>();
    for (int i = 0, size = rows(); i < size; i++) {
      List<Object> keys = new ArrayList<>(columnKeys.length);
      for (Object columnKey : columnKeys) {
        keys.add(get(columnKey).get(Object.class, i));
      }
      groups.computeIfAbsent(keys, a -> Vector.Builder.of(Integer.class)).add(i);
    }
    return new HashDataFrameGroupBy(this, groups, columnKeys);
  }

  @Override
  public final DataFrameGroupBy groupBy(UnaryOperator<Object> keyFunction) {
    HashMap<Object, Vector.Builder> groups = new LinkedHashMap<>();
    for (Index.Entry entry : getIndex().entrySet()) {
      groups.computeIfAbsent(
          keyFunction.apply(entry.getKey()),
          a -> Vector.Builder.of(Integer.class)
      ).add(entry.getValue());
    }
    return new HashDataFrameGroupBy(this, groups);
  }

  @Override
  public final DataFrame apply(Function<? super Vector, ? extends Vector> transform) {
    DataFrame.Builder builder = newBuilder();
    for (int j = 0; j < columns(); j++) {
      Vector col = getAt(j);
      Vector transformed = transform.apply(col);
      builder.add(transformed.getType());
      for (int i = 0, size = transformed.size(); i < size; i++) {
        builder.loc().set(i, j, transformed, i);
      }
    }
    DataFrame df = builder.build();
    setColumnIndex(getColumnIndex());
    return df;
  }

  @Override
  public final <T, C> DataFrame apply(
      Class<T> cls, Collector<? super T, C, ? extends Vector> collector) {
    DataFrame.Builder builder = newBuilder();
    for (Object columnKey : getColumnIndex().keySet()) {
      Vector column = get(columnKey);
      C accumulator = collector.supplier().get();
      for (int i = 0, size = column.size(); i < size; i++) {
        collector.accumulator().accept(accumulator, column.loc().get(cls, i));
      }
      Vector transformed = collector.finisher().apply(accumulator);
      builder.set(columnKey, transformed);
    }
    return builder.build();
  }

  public final Vector get(Object key) {
    return getAt(getColumnIndex().getLocation(key));
  }

  @Override
  public final DataFrame get(Object... keys) {
    return getAt(getColumnIndex().locations(keys));
  }

  @Override
  public final List<Vector> getColumns() {
    if (columnList == null) {
      columnList = new ColumnList();
    }
    return columnList;
  }

  @Override
  public final DataFrame selectColumns(Object first, Object last) {
    DataFrame.Builder builder = newBuilder();
    Set<Object> selectedRange = getColumnIndex().selectRange(
        first, BoundType.INCLUSIVE, last, BoundType.EXCLUSIVE);
    for (Object columnKey : selectedRange) {
      builder.set(columnKey, Vectors.transferableBuilder(get(columnKey)));
    }
    DataFrame df = builder.build();
    df.setIndex(getIndex());
    return df;
  }

  @Override
  public final <T> T get(Class<T> cls, Object row, Object col) {
    return getAt(cls, getIndex().getLocation(row), getColumnIndex().getLocation(col));
  }

  @Override
  public final double getAsDouble(Object row, Object col) {
    return getAsDoubleAt(getIndex().getLocation(row), getColumnIndex().getLocation(col));
  }

  @Override
  public final int getAsInt(Object row, Object col) {
    return getAsIntAt(getIndex().getLocation(row), getColumnIndex().getLocation(col));
  }

  @Override
  public final boolean isNA(Object row, Object col) {
    return isNaAt(getIndex().getLocation(row), getColumnIndex().getLocation(col));
  }

  @Override
  public final String toString(Object row, Object col) {
    return toStringAt(getIndex().getLocation(row), getColumnIndex().getLocation(col));
  }

  @Override
  public final DataFrame dropna() {
    return DataFrames.dropMissingColumns(this);
  }

  @Override
  public final DataFrame drop(Object key) {
    return dropAt(getColumnIndex().getLocation(key));
  }

  @Override
  public DataFrame drop(Object... keys) {
    return dropAt(getColumnIndex().locations(keys));
  }

  @Override
  public DataFrame drop(Predicate<Vector> predicate) {
    Builder builder = newBuilder();
    for (Object columnKey : getColumnIndex().keySet()) {
      Vector column = get(columnKey);
      if (predicate.test(column)) {
        builder.set(columnKey, Vectors.transferableBuilder(column));
      }
    }
    DataFrame df = builder.build();
    df.setIndex(getIndex());
    return df;
  }

  @Override
  public final Vector getRecord(Object key) {
    return getRecordAt(getIndex().getLocation(key));
  }

  @Override
  public final DataFrame getRecord(Object... keys) {
    DataFrame.Builder builder = newBuilder();
    for (Object key : keys) {
      builder.setRecord(key, Vectors.transferableBuilder(getRecord(key)));
    }
    DataFrame df = builder.build();
    df.setColumnIndex(getColumnIndex());
    return df;
  }

  @Override
  public final List<Vector> getRecords() {
    if (recordList == null) {
      recordList = new RecordList();
    }
    return recordList;
  }

  @Override
  public final DataFrame select(Object from, Object to) {
    return select(from, BoundType.INCLUSIVE, to, BoundType.EXCLUSIVE);
  }

  @Override
  public final DataFrame select(Object from, BoundType fromBound, Object to, BoundType toBound) {
    DataFrame.Builder builder = newBuilder();
    for (Object record : getIndex().selectRange(from, fromBound, to, toBound)) {
      builder.setRecord(record, Vectors.transferableBuilder(getRecord(record)));
    }
    DataFrame df = builder.build();
    df.setColumnIndex(getColumnIndex());
    return df;
  }

  @Override
  public final DataFrame select(Vector bits) {
    DataFrame.Builder builder = newBuilder();
    getIndex().keySet().stream()
        .filter(bits::isTrue)
        .forEach(recordKey -> {
          builder.setRecord(recordKey, Vectors.transferableBuilder(getRecord(recordKey)));
        });
    DataFrame df = builder.build();
    df.setColumnIndex(getColumnIndex());
    return df;
  }

  @Override
  public final DataFrame select(Predicate<Vector> predicate) {
    DataFrame.Builder builder = newBuilder();
    for (Object recordKey : getIndex().keySet()) {
      Vector record = getRecord(recordKey);
      if (predicate.test(record)) {
        builder.setRecord(recordKey, Vectors.transferableBuilder(record));
      }
    }

    DataFrame df = builder.build();
    df.setColumnIndex(getColumnIndex());
    return df;
  }

  @Override
  public final DataFrame copy() {
    return shallowCopy(getColumnIndex(), getIndex());
  }

  @Override
  public final Index getIndex() {
    if (index == null) {
      index = new IntIndex(0, rows());
    }
    return index;
  }

  public final Index getColumnIndex() {
    if (columnIndex == null) {
      columnIndex = new IntIndex(0, columns());
    }
    return columnIndex;
  }

  @Override
  public final void setIndex(Index index) {
    Objects.requireNonNull(index);
    Check.size(rows(), index.size());
    this.index = index;
  }

  @Override
  public final void setColumnIndex(Index index) {
    Objects.requireNonNull(index);
    Check.size(columns(), index.size());
    this.columnIndex = index;
  }

  @Override
  public final DataFrameLocationGetter loc() {
    return locationIndexer;
  }

  @Override
  public final Array<Object> toArray() {
    Array<Object> matrix = Bj.referenceArray(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, getAt(Object.class, i, j));
      }
    }
    return matrix;
  }

  @Override
  public final DoubleArray toDoubleArray() {
    DoubleArray matrix = Bj.doubleArray(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, getAsDoubleAt(i, j));
      }
    }
    return matrix;
  }

  @Override
  public final DataFrame resetIndex() {
    Check.state(!getColumnIndex().contains("index"), "cannot insert 'index', already exists");
    DataFrame.Builder builder = newBuilder();
    Vector.Builder indexColumn = new TypeInferenceVectorBuilder();
    getIndex().keySet().forEach(indexColumn::add);
    builder.set("index", indexColumn);
    for (Object columnKey : getColumnIndex().keySet()) {
      builder.set(columnKey, Vectors.transferableBuilder(get(columnKey)));
    }

    DataFrame df = builder.build();
    df.setIndex(new IntIndex(0, rows()));
    return df;
  }

  /**
   * Returns an iterator over the column indexes in this data frame
   *
   * @return a row iterator
   */
  @Override
  public final Iterator<Object> iterator() {
    return getColumnIndex().keySet().iterator();
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }

    if (obj.getClass().equals(getClass())) {
      DataFrame o = (DataFrame) obj;
      if (o.rows() == rows() &&
          getColumnIndex().equals(o.getColumnIndex()) &&
          getIndex().equals(o.getIndex())) {
        for (Object columnKey : this) {
          if (!get(columnKey).equals(o.get(columnKey))) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(getColumnIndex(), getIndex(), getColumns());
  }

  /**
   * Returns a tabular string representation of this DataFrame.
   *
   * @return the string representation
   */
  @Override
  public final String toString() {
    return DataFrames.toString(this);
  }

  /**
   * Get value at {@code row} and {@code column} as an instance of {@code T}. If conversion fails,
   * return {@code NA} as defined by {@link org.briljantframework.data.Na#of(Class)}. The
   * conversion is performed according to the convention found in {@link
   * org.briljantframework.data.vector.Convert#to(Class, Object)}
   *
   * @param <T>    the type of the returned value
   * @param cls    the class
   * @param row    the row
   * @param column the column
   * @return an instance of {@code T}
   */
  protected abstract <T> T getAt(Class<T> cls, int row, int column);

  /**
   * Returns the column at {@code index}. This implementation supplies a view into the underlying
   * data frame.
   *
   * @param index the index
   * @return a view of column {@code index}
   */
  protected abstract Vector getAt(int index);

  protected DataFrame getAt(int... indices) {
    DataFrame.Builder df = newBuilder();
    Index.Builder columnIndex = new ObjectIndex.Builder();
    int newColumn = 0;
    for (int index : indices) {
      columnIndex.add(getColumnIndex().getKey(index));
      df.add(getTypeAt(index));
      for (int i = 0; i < rows(); i++) {
        df.loc().set(i, newColumn, this, i, index);
      }
      newColumn++;
    }

    DataFrame bdf = df.build();
    bdf.setColumnIndex(columnIndex.build());
    bdf.setIndex(getIndex());
    return bdf;
  }

  protected abstract Vector getRecordAt(int index);

  /**
   * Constructs a new DataFrame by including the rows in {@code indexes}
   *
   * @param indexes the indexes to take
   * @return a new data frame as created by {@link #newBuilder()}
   */
  protected DataFrame getRecordAt(int... indexes) {
    Builder builder = newBuilder();
    for (int i : indexes) {
      for (int j = 0; j < columns(); j++) {
        builder.loc().set(i, j, this, i, j);
      }
    }

    return builder.build();
  }

  /**
   * Get value at {@code row} and {@code column} as {@code double}.
   *
   * @param row    the row
   * @param column the column
   * @return the value
   */
  protected abstract double getAsDoubleAt(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as {@code int}.
   *
   * @param row    the row
   * @param column the column
   * @return the value
   */
  protected abstract int getAsIntAt(int row, int column);

  /**
   * Returns string representation of value at {@code row, column}. In most cases this is
   * equivalent to {@code get(Object.class, row, column).toString()}, but it handles {@code NA}
   * values, i.e. the returned {@linkplain String string} is never {@code null}.
   *
   * @param row    the row
   * @param column the column
   * @return the representation
   */
  protected abstract String toStringAt(int row, int column);

  /**
   * Returns true if value at {@code row, column} is {@code NA}.
   *
   * @param row    the row
   * @param column the column
   * @return true or false
   */
  protected abstract boolean isNaAt(int row, int column);

  protected abstract VectorType getTypeAt(int index);

  /**
   * Constructs a new DataFrame by dropping {@code index}.
   *
   * @param index the index
   * @return a new data frame as created by {@link #newCopyBuilder()}
   */
  protected DataFrame dropAt(int index) {
    // TODO: implement me
    throw new UnsupportedOperationException();
  }

  /**
   * Constructs a new DataFrame by dropping the columns in {@code indexes}.
   *
   * This implementations rely on {@link #newBuilder()} returning a builder and that {@link
   * org.briljantframework.data.dataframe.DataFrame.Builder#add(org.briljantframework.data.vector.Vector)}
   * adds a vector.
   *
   * @param indexes collection of indexes
   * @return a new data frame as created by {@link #newBuilder()}
   */
  protected DataFrame dropAt(int[] indexes) {
    Arrays.sort(indexes);

    Builder builder = newBuilder();
    Index.Builder columnIndex = getColumnIndex().newBuilder();
    for (int i = 0; i < columns(); i++) {
      if (Arrays.binarySearch(indexes, i) < 0) {
        columnIndex.add(getColumnIndex().getKey(i));
        builder.add(Vectors.transferableBuilder(getAt(i)));
      }
    }

    return builder.setColumnIndex(columnIndex.build()).setIndex(getIndex()).build();
  }

  protected abstract DataFrame shallowCopy(Index columnIndex, Index index);

  protected abstract VectorType getMostSpecificColumnType();

  /**
   * This class provides a skeletal implementation of the {@link org.briljantframework.data.dataframe.DataFrame.Builder}
   * interface to minimize the effort required to implement this interface, including the handling
   * of key and index-based setters.
   *
   * <p> To implement this builder, the programmer needs to implement the following abstract
   * methods (the documentation for each method provides information on how to implement them):
   * <ul>
   * <li>{@link #setNaAt(int, int)}</li>
   * <li>{@link #setAt(int, int, Object)}</li>
   * <li>{@link #setAt(int, org.briljantframework.data.vector.Vector.Builder)}</li>
   * <li>{@link #setRecordAt(int, org.briljantframework.data.vector.Vector.Builder)}</li>
   * <li>{@link #setAt(int, int, org.briljantframework.data.vector.Vector, int)}</li>
   * <li>{@link #readEntry(org.briljantframework.data.reader.DataEntry)}</li>
   * </ul>
   *
   * and the following methods from the {@link org.briljantframework.data.dataframe.DataFrame.Builder}
   * interface
   * <ul>
   * <li>{@link #rows()}</li>
   * <li>{@link #columns()}</li>
   * <li>{@link #build()}</li>
   * <li>{@link #getTemporaryDataFrame()}</li>
   * </ul>
   *
   * <h3>Notes for object keys</h3>
   * <p> For object keys, the position can be retrieved using {@link #getOrCreateColumnIndex(Object)}
   * and {@link #getOrCreateIndex(Object)}
   *
   * <h3>Notes for the abstract methods</h3>
   * <p> If the indexes are larger than {@link #rows()} and {@link #columns()} respectively
   * additional rows and columns, filled with {@code NA}, are inserted between the specified
   * indexes and the current size.
   */
  protected static abstract class AbstractBuilder implements Builder {

    private final DataFrameLocationSetter loc = new DataFrameLocationSetterImpl();

    /**
     * The column index. When getting the index of a key, prefer {@link
     * #getOrCreateColumnIndex(Object)}.
     */
    private Index.Builder columnIndex;

    /**
     * The record index. When getting the index of a key, prefer {@link
     * #getOrCreateIndex(Object)}.
     */
    private Index.Builder index;

    /**
     * Provide initial indexes
     *
     * @param from the dataframe to copy
     */
    protected AbstractBuilder(DataFrame from) {
      Index ci = from.getColumnIndex();
      Index ri = from.getIndex();
      this.columnIndex = ci instanceof IntIndex ? null : ci.newCopyBuilder();
      this.index = ri instanceof IntIndex ? null : ri.newCopyBuilder();
    }

    /**
     * Implicit default constructor for constructing default {@code int}-based indexes
     */
    protected AbstractBuilder() {
    }

    @Override
    public final DataFrameLocationSetter loc() {
      return loc;
    }

    @Override
    public final Builder set(Object tr, Object tc, DataFrame from, Object fr, Object fc) {
      int r = getOrCreateIndex(tr);
      int c = getOrCreateColumnIndex(tc);
      setAt(r, c, from, from.getIndex().getLocation(fr),
            from.getColumnIndex().getLocation(fc));
      return this;
    }

    @Override
    public final Builder set(Object row, Object column, Vector from, Object key) {
      int r = getOrCreateIndex(row);
      int c = getOrCreateColumnIndex(column);
      setAt(r, c, from, from.getIndex().getLocation(key));
      return this;
    }

    @Override
    public final Builder set(Object row, Object column, Object value) {
      int r = getOrCreateIndex(row);
      int c = getOrCreateColumnIndex(column);
      setAt(r, c, value);
      return this;
    }

    @Override
    public final Builder set(Object key, Vector.Builder columnBuilder) {
      int columnIndex = getOrCreateColumnIndex(key);
      extendIndex(columnBuilder.size());
      setAt(columnIndex, columnBuilder);
      return this;
    }

    @Override
    public final DataFrame.Builder add(Vector.Builder columnBuilder) {
      loc().set(columns(), columnBuilder);
      return this;
    }

    @Override
    public final Builder setRecord(Object key, Vector.Builder recordBuilder) {
      int index = getOrCreateIndex(key);
      extendColumnIndex(recordBuilder.size());
      setRecordAt(index, recordBuilder);
      return this;
    }

    @Override
    public final Builder addRecord(Vector.Builder recordBuilder) {
      loc().setRecord(rows(), recordBuilder);
      return this;
    }

    @Override
    public Builder remove(Object key) {
      initializeColumnIndexer();
      int index = columnIndex.getLocation(key);
      loc().remove(index);
      return this;
    }

    @Override
    public Builder removeRecord(Object key) {
      initializeIndexer();
      int index = this.index.getLocation(key);
      loc().removeRecord(index);
      return this;
    }

    @Override
    public Builder setIndex(Index index) {
      this.index = index.newCopyBuilder();
      this.index.extend(rows());
      return this;
    }

    @Override
    public Builder setColumnIndex(Index columnIndex) {
      this.columnIndex = columnIndex.newCopyBuilder();
      this.columnIndex.extend(columns());
      return this;
    }

    @Override
    public final Builder readAll(EntryReader entryReader) {
      int entries = rows();
      while (entryReader.hasNext()) {
        DataEntry entry = entryReader.next();
        extendColumnIndex(entry.size());
        entries++;
        readEntry(entry);
      }
      extendIndex(entries);

      return this;
    }

    @Override
    public Builder read(DataEntry entry) {
      int rows = rows();
      readEntry(entry);
      extendIndex(rows + 1);
      return this;
    }

    private void initializeIndexer() {
      if (index == null) {
        index = new IntIndex.Builder(rows());
      }
    }

    private void initializeColumnIndexer() {
      if (columnIndex == null) {
        columnIndex = new IntIndex.Builder(columns());
      }
    }

    /**
     * Extend both column and record index with the specified number of entries.
     */
    private void extendIndex(int record, int columns) {
      extendIndex(record);
      extendColumnIndex(columns);
    }

    /**
     * Extend the column index with the specified number of entries. For example, if the index
     * contains {@code 3} items and the argument is {@code 5}, index {@code 4} and {@code 5} will
     * be added.
     */
    private void extendColumnIndex(int c) {
      if (columnIndex != null) {
        columnIndex.extend(c);
      }
    }

    /**
     * See explanation of {@link #extendColumnIndex(int)}
     */
    private void extendIndex(int r) {
      if (index != null) {
        index.extend(r);
      }
    }

    private int getOrCreateColumnIndex(Object key) {
      initializeColumnIndexer();
      int index = columns();
      if (columnIndex.contains(key)) {
        index = columnIndex.getLocation(key);
      } else {
        columnIndex.add(key);
      }
      return index;
    }

    private final int getOrCreateIndex(Object key) {
      initializeIndexer();
      int index = rows();
      if (this.index.contains(key)) {
        index = this.index.getLocation(key);
      } else {
        this.index.add(key);
      }
      return index;
    }

    protected final Index getIndex(int rows) {
      if (index == null) {
        return new IntIndex(0, rows);
      } else {
        return index.build();
      }
    }

    protected Index getColumnIndex(int columns) {
      if (columnIndex == null) {
        return new IntIndex(0, columns);
      } else {
        return columnIndex.build();
      }
    }

    /**
     * Set the element at the specified position to {@code NA}.
     *
     * @param r the row position
     * @param c the column position
     */
    protected abstract void setNaAt(int r, int c);

    /**
     * Set the element at the specified position to the specified value as defined by {@link
     * org.briljantframework.data.vector.Convert#to(Class, Object)}
     *
     * @param r the row location
     * @param c the column location
     */
    protected abstract void setAt(int r, int c, Object value);

    protected abstract void setAt(int c, Vector.Builder builder);

    protected abstract void setRecordAt(int index, Vector.Builder builder);

    protected abstract void setAt(int r, int c, Vector from, int i);

    protected void setAt(int tr, int tc, DataFrame from, int fr, int fc) {
      setAt(tr, tc, from.loc().get(fc), fr);
    }

    protected abstract void removeAt(int c);

    protected abstract void removeRecordAt(int r);

    protected abstract void swapAt(int a, int b);

    protected abstract void swapRecordsAt(int a, int b);

    protected abstract void readEntry(DataEntry entry);

    private class DataFrameLocationSetterImpl implements DataFrameLocationSetter {

      @Override
      public void setNA(int r, int c) {
        extendIndex(r + 1, c + 1);
        setNaAt(r, c);
      }

      @Override
      public void set(int r, int c, Object value) {
        extendIndex(r + 1, c + 1);
        setAt(r, c, value);
      }

      @Override
      public void set(int tr, int tc, DataFrame df, int fr, int fc) {
        extendIndex(tr + 1, tc + 1);
        setAt(tr, tc, df, fr, fc);
      }

      @Override
      public void set(int tr, int tc, Vector v, int i) {
        extendIndex(tr + 1, tc + 1);
        setAt(tr, tc, v, i);
      }

      @Override
      public void set(int c, Vector.Builder columnBuilder) {
        extendColumnIndex(c + 1);
        extendIndex(columnBuilder.size());
        setAt(c, columnBuilder);
      }

      @Override
      public void remove(int c) {
        if (columnIndex != null) {
          columnIndex.remove(c);
        }
        removeAt(c);
      }

      @Override
      public void swap(int a, int b) {
        if (columnIndex != null) {
          columnIndex.swap(a, b);
        }
        swapAt(a, b);
      }

      @Override
      public void setRecord(int r, Vector.Builder recordBuilder) {
        extendIndex(r + 1);
        extendColumnIndex(recordBuilder.size());
        setRecordAt(r, recordBuilder);
      }

      @Override
      public void removeRecord(int r) {
        if (index != null) {
          index.remove(r);
        }
        removeRecordAt(r);
      }

      @Override
      public void swapRecords(int a, int b) {
        if (index != null) {
          index.swap(a, b);
        }
        swapRecordsAt(a, b);
      }
    }
  }

  private class DataFrameLocationGetterImpl implements DataFrameLocationGetter {

    @Override
    public <T> T get(Class<T> cls, int r, int c) {
      return getAt(cls, r, c);
    }

    @Override
    public double getAsDouble(int r, int c) {
      return getAsDoubleAt(r, c);
    }

    @Override
    public int getAsInt(int r, int c) {
      return getAsIntAt(r, c);
    }

    @Override
    public String toString(int r, int c) {
      return toStringAt(r, c);
    }

    @Override
    public boolean isNA(int r, int c) {
      return isNaAt(r, c);
    }

    @Override
    public Vector get(int c) {
      return getAt(c);
    }

    @Override
    public DataFrame get(int... columns) {
      return getAt(columns);
    }

    @Override
    public DataFrame drop(int index) {
      return dropAt(index);
    }

    @Override
    public DataFrame drop(int... columns) {
      return dropAt(columns);
    }

    @Override
    public Vector getRecord(int r) {
      return getRecordAt(r);
    }

    @Override
    public DataFrame getRecord(int... records) {
      return getRecordAt(records);
    }
  }

  private class ColumnList extends AbstractList<Vector> {

    @Override
    public Vector get(int index) {
      return getAt(index);
    }

    @Override
    public int size() {
      return columns();
    }
  }

  private class RecordList extends AbstractList<Vector> {

    @Override
    public Vector get(int index) {
      return getRecordAt(index);
    }

    @Override
    public int size() {
      return rows();
    }
  }
}
