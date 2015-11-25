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

import java.io.IOException;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

import net.mintern.primitive.comparators.IntComparator;

import org.briljantframework.Check;
import org.briljantframework.array.Array;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
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
import org.briljantframework.data.index.ObjectIndex;
import org.briljantframework.data.index.VectorLocationGetter;
import org.briljantframework.data.index.VectorLocationSetter;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.EntryReader;
import org.briljantframework.data.vector.Convert;
import org.briljantframework.data.vector.TypeInferenceVectorBuilder;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.primitive.ArrayAllocations;
import org.briljantframework.primitive.IntList;

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
   * @param index allowed to be {@code null}
   */
  protected AbstractDataFrame(Index columnIndex, Index index) {
    this.columnIndex = columnIndex;
    this.index = index;
  }

  @Override
  public <T> BooleanArray where(Class<T> cls, Predicate<? super T> predicate) {
    BooleanArray array = Arrays.newBooleanArray(rows(), columns());
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        array.set(i, j, predicate.test(loc().get(cls, i, j)));
      }
    }
    return array;
  }

  @Override
  public DataFrame set(Object key, Object value) {
    DataFrame.Builder builder = newCopyBuilder();
    builder.set(key, Vectors.transferableBuilder(Vector.singleton(value, rows())));
    return builder.build();
  }

  @Override
  public DataFrame setRecord(Object key, Object value) {
    DataFrame.Builder builder = newCopyBuilder();
    builder.setRecord(key, Vectors.transferableBuilder(Vector.singleton(value, columns())));
    return builder.build();
  }

  @Override
  public DataFrame setRecord(Object key, Vector vector) {
    return newCopyBuilder().setRecord(key, Vectors.transferableBuilder(vector)).build();
  }

  @Override
  public DataFrame get(BooleanArray array) {
    DataFrame.Builder builder = newBuilder();
    if (array.isMatrix()) { // Select values; setting false values to NA
      Check.argument(array.rows() == rows() && array.columns() == columns(), "Illegal shape");
      for (int j = 0; j < array.columns(); j++) {
        Vector column = loc().get(j);
        Vector.Builder columnBuilder = column.newBuilder();
        for (int i = 0; i < array.rows(); i++) {
          if (array.get(i, j)) {
            columnBuilder.loc().set(i, column, i);
          } else {
            columnBuilder.loc().setNA(i);
          }
        }
        builder.set(getColumnIndex().get(j), columnBuilder);
      }
      builder.setIndex(getIndex());
    } else if (array.isVector()) { // Select rows;
      for (int i = 0; i < array.size(); i++) {
        if (array.get(i)) {
          builder.setRecord(getIndex().get(i), Vectors.transferableBuilder(loc().getRecord(i)));
        }
      }
      builder.setColumnIndex(getColumnIndex());
    } else {
      throw new IllegalArgumentException("Illegal array dimension " + array.dims());
    }

    return builder.build();
  }

  @Override
  public DataFrame set(BooleanArray array, Object value) {
    DataFrame.Builder builder = newBuilder();
    if (array.isMatrix()) { // Select values; setting false values to NA
      Check.argument(array.rows() == rows() && array.columns() == columns(), "Illegal shape");
      for (int j = 0; j < array.columns(); j++) {
        Vector column = loc().get(j);
        Vector.Builder columnBuilder = column.newBuilder();
        for (int i = 0; i < array.rows(); i++) {
          if (array.get(i, j)) {
            columnBuilder.loc().set(i, value);
          } else {
            columnBuilder.loc().set(i, column, i);
          }
        }
        builder.set(getColumnIndex().get(j), columnBuilder);
      }
      builder.setIndex(getIndex());
    } else if (array.isVector()) { // Select rows;
      for (int i = 0; i < array.size(); i++) {
        Object key = getIndex().get(i);
        if (array.get(i)) {
          builder.setRecord(key, Vectors.transferableBuilder(Vector.singleton(value, columns())));
        } else {
          builder.setRecord(key, Vectors.transferableBuilder(loc().getRecord(i)));
        }
      }
      builder.setColumnIndex(getColumnIndex());
    } else {
      throw new IllegalArgumentException("Illegal array dimension " + array.dims());
    }

    return builder.build();
  }

  @Override
  public DataFrame set(Object key, Vector column) {
    DataFrame.Builder builder = newCopyBuilder();
    builder.set(key, Vectors.transferableBuilder(column));
    return builder.build();
  }

  @Override
  public DataFrame set(Map<Object, Vector> columns) {
    DataFrame.Builder builder = newCopyBuilder();
    for (Map.Entry<Object, Vector> entry : columns.entrySet()) {
      builder.set(entry.getKey(), Vectors.transferableBuilder(entry.getValue()));
    }
    return builder.build();
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
  public DataFrame sortColumns(Comparator<Object> comparator) {
    Index.Builder index = getColumnIndex().newCopyBuilder();
    index.sort(comparator);
    return shallowCopy(index.build(), getIndex());
  }

  @Override
  public final DataFrame sortBy(Object key) {
    return sortBy(SortOrder.ASC, key);
  }

  @Override
  public final <T> DataFrame sortBy(Class<? extends T> cls, Comparator<? super T> cmp, Object key) {
    VectorLocationGetter loc = get(key).loc();
    Index.Builder index = getIndex().newCopyBuilder();
    index.sortIterationOrder((a, b) -> cmp.compare(loc.get(cls, a), loc.get(cls, b)));
    return shallowCopy(getColumnIndex(), index.build());
  }

  @Override
  public final DataFrame sortBy(SortOrder order, Object key) {
    VectorLocationGetter loc = get(key).loc();
    boolean asc = order == SortOrder.ASC;
    IntComparator cmp = asc ? loc::compare : (a, b) -> loc.compare(b, a);
    Index.Builder index = getIndex().newCopyBuilder();
    index.sortIterationOrder(cmp);
    return shallowCopy(getColumnIndex(), index.build());
  }

  @Override
  public final DataFrame limit(int n) {
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
    Joiner joiner =
        type.getJoinOperation()
            .createJoiner(JoinUtils.createJoinKeys(getIndex(), other.getIndex()));
    return joiner.join(this, other, Collections.emptyList());
  }

  @Override
  public DataFrame join(JoinType type, DataFrame other, Object key) {
    return doJoin(type, other, Collections.singletonList(key));
  }

  @Override
  public DataFrame join(JoinType type, DataFrame other, Object... keys) {
    return doJoin(type, other, java.util.Arrays.asList(keys));
  }

  private DataFrame doJoin(JoinType type, DataFrame other, Collection<Object> columns) {
    Joiner joiner = type.getJoinOperation().createJoiner(this, other, columns);
    return joiner.join(this, other, columns);
  }

  @Override
  public final <T> DataFrame map(Class<T> cls, Function<? super T, ?> mapper) {
    DataFrame.Builder builder = newBuilder();
    for (int j = 0; j < columns(); j++) {
      Vector column = getAt(j);

      // Not affected by ISSUE#7
      Vector.Builder newColumn = new TypeInferenceVectorBuilder();
      for (int i = 0, size = column.size(); i < size; i++) {
        Object transformed = mapper.apply(column.loc().get(cls, i));
        if (Is.NA(transformed)) {
          newColumn.addNA();
        } else {
          newColumn.add(transformed);
        }
      }
      builder.add(newColumn);
    }
    return builder.setIndex(getIndex()).setColumnIndex(getColumnIndex()).build();
  }

  @Override
  public final <T> Vector reduce(Class<? extends T> cls, T init, BinaryOperator<T> op) {
    Vector.Builder builder = Vector.Builder.of(cls);
    for (Object columnKey : this) {
      Vector column = get(columnKey);
      // if (column.getType().isAssignableTo(cls)) {
      T result = init;
      for (int i = 0, size = column.size(); i < size; i++) {
        result = op.apply(result, column.loc().get(cls, i));
      }
      builder.set(columnKey, result);
      // }
    }
    return builder.build();
  }

  @Override
  public final Vector reduce(Function<Vector, ?> op) {
    // ISSUE#7 use an improved TypeInferenceBuilder here
    Vector.Builder builder = new TypeInferenceVectorBuilder();
    for (Object columnKey : this) {
      Vector column = get(columnKey);
      Object value = op.apply(column);
      if (Is.NA(value)) {
        builder.setNA(columnKey);
      } else {
        builder.set(columnKey, value);
      }
    }
    return builder.build();
  }

  @Override
  public final <T, C> Vector collect(Class<T> cls, Collector<? super T, C, ?> collector) {
    // Affected by ISSUE#7
    Vector.Builder builder = new TypeInferenceVectorBuilder();
    return doCollect(cls, collector, builder);
  }

  private <T, C> Vector doCollect(Class<T> cls, Collector<? super T, C, ?> collector,
      Vector.Builder builder) {

    for (Object columnKey : this) {
      Vector column = get(columnKey);
      // if (column.getType().isAssignableTo(cls)) {
      C accumulator = collector.supplier().get();
      for (int i = 0, size = column.size(); i < size; i++) {
        collector.accumulator().accept(accumulator, column.loc().get(cls, i));
      }
      builder.set(columnKey, collector.finisher().apply(accumulator));
      // }
    }
    return builder.build();
  }

  @Override
  public final DataFrameGroupBy groupBy(Object columnKey) {
    HashMap<Object, IntList> groups = new LinkedHashMap<>();
    Vector column = get(columnKey);
    VectorLocationGetter loc = column.loc();
    for (int i = 0, size = column.size(); i < size; i++) {
      groups.computeIfAbsent(loc.get(Object.class, i), a -> new IntList()).add(i);
    }
    return new HashDataFrameGroupBy(this, groups, columnKey);
  }

  @Override
  public final <T> DataFrameGroupBy groupBy(Class<T> cls, Function<? super T, Object> map,
      Object columnKey) {
    HashMap<Object, IntList> groups = new LinkedHashMap<>();
    Vector column = get(columnKey);
    VectorLocationGetter loc = column.loc();
    for (int i = 0, size = column.size(); i < size; i++) {
      T value = loc.get(cls, i);
      // Ignore NA values (group them separately)
      groups.computeIfAbsent(Is.NA(value) ? value : map.apply(value), a -> new IntList()).add(i);
    }
    return new HashDataFrameGroupBy(this, groups, columnKey);
  }

  @Override
  public final DataFrameGroupBy groupBy(Object key, Object... keys) {
    return groupBy(vector -> vector.toList(Object.class), key, keys);
  }

  @Override
  public DataFrameGroupBy groupBy(Function<? super Vector, Object> combiner, Object key,
      Object... keys) {
    HashMap<Object, IntList> groups = new LinkedHashMap<>();
    keys = ArrayAllocations.prepend(key, keys);
    for (int i = 0, size = rows(); i < size; i++) {
      Vector.Builder cs = new TypeInferenceVectorBuilder();
      for (int j = 0; j < keys.length; j++) {
        cs.loc().set(j, get(keys[j]), i);
      }
      groups.computeIfAbsent(combiner.apply(cs.build()), a -> new IntList()).add(i);
    }
    return new HashDataFrameGroupBy(this, groups, keys);
  }

  @Override
  public final DataFrameGroupBy groupBy(UnaryOperator<Object> keyFunction) {
    return groupBy(Object.class, keyFunction);
  }

  @Override
  public <T> DataFrameGroupBy groupBy(Class<T> cls, Function<? super T, ?> function) {
    HashMap<Object, IntList> groups = new LinkedHashMap<>();

    for (Index.Entry entry : getIndex().indexSet()) {
      T key = Convert.to(cls, entry.getKey());
      groups.computeIfAbsent(Is.NA(key) ? key : function.apply(key), // ignore NA keys
          a -> new IntList()).add(entry.getValue());
    }
    return new HashDataFrameGroupBy(this, groups);
  }

  @Override
  public final DataFrame apply(Function<? super Vector, ? extends Vector> transform) {
    DataFrame.Builder builder = newBuilder();
    for (Object columnKey : this) {
      Vector column = get(columnKey);
      builder.set(columnKey, transform.apply(column));
    }
    return builder.setIndex(getIndex()).build();
  }

  @Override
  public final <T, C> DataFrame apply(Class<T> cls,
      Collector<? super T, C, ? extends Vector> collector) {
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
  public final DataFrame select(Object... keys) {
    return select(java.util.Arrays.asList(keys));
  }

  @Override
  public DataFrame select(List<Object> keys) {
    DataFrame.Builder builder = newBuilder();
    builder.setIndex(getIndex());
    for (Object key : keys) {
      builder.set(key, Vectors.transferableBuilder(get(key)));
    }
    return builder.build();
  }

  @Override
  public final List<Vector> getColumns() {
    if (columnList == null) {
      columnList = new ColumnList();
    }
    return columnList;
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
  public DataFrame drop(Object... keys) {
    return drop(java.util.Arrays.asList(keys));
  }

  @Override
  public DataFrame drop(List<Object> keys) {
    Check.argument(keys.size() > 0, "Can't drop an empty list.");
    DataFrame.Builder builder = newBuilder();
    Set<Object> drop = new HashSet<>(keys);
    getColumnIndex().stream().filter(k -> !drop.contains(k)).forEach(k -> {
      builder.set(k, Vectors.transferableBuilder(get(k)));
    });
    return builder.setIndex(getIndex()).build();
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
  public Vector getDiagonal() {
    Vector.Builder builder = Vector.Builder.of(Object.class);
    for (int i = 0; i < rows(); i++) {
      builder.loc().set(i, loc().get(Object.class, i, i));
    }
    return builder.build();
  }

  @Override
  public final DataFrame selectRecords(Object... keys) {
    DataFrame.Builder builder = newBuilder();
    for (Object recordKey : keys) {
      builder.setRecord(recordKey, Vectors.transferableBuilder(getRecord(recordKey)));
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
  public final DataFrame filter(Predicate<Vector> predicate) {
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
  public DataFrame transpose() {
    DataFrame.Builder builder = newBuilder();
    for (Object columnKey : getColumnIndex().keySet()) {
      builder.setRecord(columnKey, Vectors.transferableBuilder(get(columnKey)));
    }
    builder.setColumnIndex(getIndex());
    return builder.build();
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

  @Override
  public final void setIndex(Index index) {
    Objects.requireNonNull(index);
    Check.size(rows(), index.size());
    this.index = index;
  }

  public final Index getColumnIndex() {
    if (columnIndex == null) {
      columnIndex = new IntIndex(0, columns());
    }
    return columnIndex;
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
  public final <T> Array<T> toArray(Class<T> type) {
    return toArray(type, UnaryOperator.identity());
  }

  @Override
  public <T, R> Array<R> toArray(Class<T> type, Function<? super T, ? extends R> function) {
    Array<R> matrix = Arrays.newArray(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, function.apply(getAt(type, i, j)));
      }
    }
    return matrix;
  }

  @Override
  public final DoubleArray toDoubleArray() {
    return toDoubleArray(DoubleUnaryOperator.identity());
  }

  @Override
  public DoubleArray toDoubleArray(DoubleUnaryOperator operator) {
    DoubleArray matrix = Arrays.newDoubleArray(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, operator.applyAsDouble(getAsDoubleAt(i, j)));
      }
    }
    return matrix;
  }

  @Override
  public final DataFrame resetIndex() {
    Check.state(!getColumnIndex().contains("index"),
        "cannot reset index. Column 'index' already exists");
    DataFrame.Builder builder = newBuilder();
    Vector.Builder indexColumn = new TypeInferenceVectorBuilder();
    getIndex().keySet().forEach(indexColumn::add);
    builder.set("index", indexColumn);
    for (Object columnKey : getColumnIndex().keySet()) {
      builder.set(columnKey, Vectors.transferableBuilder(get(columnKey)));
    }

    return builder.build();
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
      if (o.rows() == rows() && getColumnIndex().equals(o.getColumnIndex())
          && getIndex().equals(o.getIndex())) {
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
   * return {@code NA} as defined by {@link org.briljantframework.data.Na#of(Class)}. The conversion
   * is performed according to the convention found in
   * {@link org.briljantframework.data.vector.Convert#to(Class, Object)}
   *
   * @param <T> the type of the returned value
   * @param cls the class
   * @param row the row
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

  protected DataFrame getAt(IntArray indices) {
    DataFrame.Builder builder = newBuilder();
    Index.Builder columnIndex = getColumnIndex().newBuilder();
    for (int i = 0; i < indices.size(); i++) {
      columnIndex.add(getColumnIndex().get(indices.get(i)));
      builder.loc().set(i, getAt(indices.get(i)));
    }
    return builder.setIndex(getIndex()).setColumnIndex(columnIndex.build()).build();
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
   * @param indexes
   * @return
   */
  protected DataFrame getRecordAt(IntArray indexes) {
    Builder builder = newBuilder();
    for (Integer index : indexes.toList()) {
      builder.setRecord(getIndex().get(index), Vectors.transferableBuilder(getRecordAt(index)));
    }
    builder.setColumnIndex(getColumnIndex());
    return builder.build();
  }

  /**
   * Get value at {@code row} and {@code column} as {@code double}.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  protected abstract double getAsDoubleAt(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as {@code int}.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  protected abstract int getAsIntAt(int row, int column);

  /**
   * Returns string representation of value at {@code row, column}. In most cases this is equivalent
   * to {@code get(Object.class, row, column).toString()}, but it handles {@code NA} values, i.e.
   * the returned {@linkplain String string} is never {@code null}.
   *
   * @param row the row
   * @param column the column
   * @return the representation
   */
  protected abstract String toStringAt(int row, int column);

  /**
   * Returns true if value at {@code row, column} is {@code NA}.
   *
   * @param row the row
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
    Builder newBuilder = newCopyBuilder();
    newBuilder.loc().remove(index);
    return newBuilder.build();
  }

  /**
   * Constructs a new DataFrame by dropping the columns in {@code indexes}.
   *
   * This implementations rely on {@link #newBuilder()} returning a builder and that
   * {@link org.briljantframework.data.dataframe.DataFrame.Builder#add(org.briljantframework.data.vector.Vector)}
   * adds a vector.
   *
   * @param indexes collection of indexes
   * @return a new data frame as created by {@link #newBuilder()}
   */
  protected DataFrame dropAt(IntArray indexes) {
    indexes.sort();
    Builder builder = newBuilder();
    for (int i = 0; i < columns(); i++) {
      if (Arrays.binarySearch(indexes, i) < 0) {
        builder.set(getColumnIndex().get(i), Vectors.transferableBuilder(getAt(i)));
      }
    }

    return builder.setIndex(getIndex()).build();
  }

  protected abstract DataFrame shallowCopy(Index columnIndex, Index index);

  protected abstract VectorType getMostSpecificColumnType();

  /**
   * This class provides a skeletal implementation of the
   * {@link org.briljantframework.data.dataframe.DataFrame.Builder} interface to minimize the effort
   * required to implement this interface, including the handling of key and index-based setters.
   *
   * <p>
   * To implement this builder, the programmer needs to implement the following abstract methods
   * (the documentation for each method provides information on how to implement them):
   * <ul>
   * <li>{@link #setNaAt(int, int)}</li>
   * <li>{@link #setAt(int, int, Object)}</li>
   * <li>{@link #setAt(int, org.briljantframework.data.vector.Vector.Builder)}</li>
   * <li>{@link #setRecordAt(int, org.briljantframework.data.vector.Vector.Builder)}</li>
   * <li>{@link #setAt(int, int, org.briljantframework.data.vector.Vector, int)}</li>
   * <li>{@link #readEntry(org.briljantframework.data.reader.DataEntry)}</li>
   * </ul>
   *
   * and the following methods from the
   * {@link org.briljantframework.data.dataframe.DataFrame.Builder} interface
   * <ul>
   * <li>{@link #rows()}</li>
   * <li>{@link #columns()}</li>
   * <li>{@link #build()}</li>
   * <li>{@link #getTemporaryDataFrame()}</li>
   * </ul>
   *
   * <h3>Notes for object keys</h3>
   * <p>
   * For object keys, the position can be retrieved using {@link #getOrCreateColumnIndex(Object)}
   * and {@link #getOrCreateIndex(Object)}
   *
   * <h3>Notes for the abstract methods</h3>
   * <p>
   * If the indexes are larger than {@link #rows()} and {@link #columns()} respectively additional
   * rows and columns, filled with {@code NA}, are inserted between the specified indexes and the
   * current size.
   */
  protected static abstract class AbstractBuilder implements Builder {

    private final DataFrameLocationSetter loc = new DataFrameLocationSetterImpl();

    /**
     * The column index. When getting the index of a key, prefer
     * {@link #getOrCreateColumnIndex(Object)}.
     */
    private Index.Builder columnIndex;

    /**
     * The record index. When getting the index of a key, prefer {@link #getOrCreateIndex(Object)}.
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
    protected AbstractBuilder() {}

    @Override
    public final DataFrameLocationSetter loc() {
      return loc;
    }

    @Override
    public final Builder set(Object tr, Object tc, DataFrame from, Object fr, Object fc) {
      int r = getOrCreateIndex(tr);
      int c = getOrCreateColumnIndex(tc);
      setAt(r, c, from, from.getIndex().getLocation(fr), from.getColumnIndex().getLocation(fc));
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
    public final Builder remove(Object key) {
      initializeColumnIndexer();
      int index = columnIndex.getLocation(key);
      loc().remove(index);
      return this;
    }

    @Override
    public final Builder removeRecord(Object key) {
      initializeIndexer();
      int index = this.index.getLocation(key);
      loc().removeRecord(index);
      return this;
    }

    @Override
    public final Vector.Builder get(Object key) {
      initializeColumnIndexer();
      int index = columnIndex.getLocation(key);
      return new UnbuildableVectorBuilder(getAt(index));
    }

    @Override
    public final Vector.Builder getRecord(Object key) {
      initializeIndexer();
      int index = this.index.getLocation(key);
      return new UnbuildableVectorBuilder(getRecordAt(index));
    }

    @Override
    public final Builder setIndex(Index index) {
      this.index = index.newCopyBuilder();
      this.index.resize(rows());
      return this;
    }

    @Override
    public final Builder setColumnIndex(Index columnIndex) {
      this.columnIndex = columnIndex.newCopyBuilder();
      this.columnIndex.resize(columns());
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
    public final Builder read(DataEntry entry) {
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
     * contains {@code 3} items and the argument is {@code 5}, index {@code 4} and {@code 5} will be
     * added.
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

    private int getOrCreateIndex(Object key) {
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
     * Set the element at the specified position to the specified value as defined by
     * {@link org.briljantframework.data.vector.Convert#to(Class, Object)}
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

    protected abstract Vector.Builder getAt(int i);

    protected abstract Vector.Builder getRecordAt(int i);

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

  private static class UnbuildableVectorBuilder implements Vector.Builder {

    private final Vector.Builder delegate;

    private UnbuildableVectorBuilder(Vector.Builder delegate) {
      this.delegate = delegate;
    }

    @Override
    public Vector.Builder setNA(Object key) {
      return delegate.setNA(key);
    }

    @Override
    public Vector.Builder addNA() {
      return delegate.addNA();
    }

    @Override
    public Vector.Builder add(Vector from, int fromIndex) {
      return delegate.add(from, fromIndex);
    }

    @Override
    public Vector.Builder add(Vector from, Object key) {
      return delegate.add(from, key);
    }

    @Override
    public Vector.Builder set(Object atKey, Vector from, int fromIndex) {
      return delegate.set(atKey, from, fromIndex);
    }

    @Override
    public Vector.Builder set(Object atKey, Vector from, Object fromIndex) {
      return delegate.set(atKey, from, fromIndex);
    }

    @Override
    public Vector.Builder set(Object key, Object value) {
      return delegate.set(key, value);
    }

    @Override
    public Vector.Builder add(Object value) {
      return delegate.add(value);
    }

    @Override
    public Vector.Builder add(double value) {
      return delegate.add(value);
    }

    @Override
    public Vector.Builder add(int value) {
      return delegate.add(value);
    }

    @Override
    public Vector.Builder addAll(Vector from) {
      return delegate.addAll(from);
    }

    @Override
    public Vector.Builder addAll(Object... objects) {
      return delegate.addAll(objects);
    }

    @Override
    public Vector.Builder addAll(Vector.Builder builder) {
      return delegate.addAll(builder);
    }

    @Override
    public Vector.Builder addAll(Iterable<?> iterable) {
      return delegate.addAll(iterable);
    }

    @Override
    public Vector.Builder remove(Object key) {
      return delegate.remove(key);
    }

    @Override
    public Vector.Builder readAll(DataEntry entry) throws IOException {
      return delegate.readAll(entry);
    }

    @Override
    public VectorLocationSetter loc() {
      return delegate.loc();
    }

    @Override
    public Vector.Builder read(DataEntry entry) {
      return delegate.read(entry);
    }

    @Override
    public int size() {
      return delegate.size();
    }

    @Override
    public Vector getTemporaryVector() {
      return delegate.getTemporaryVector();
    }

    @Override
    public Vector build() {
      throw new IllegalStateException("Can't build this vector");
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
    public DataFrame get(IntArray columns) {
      return getAt(columns);
    }

    @Override
    public DataFrame drop(int index) {
      return dropAt(index);
    }

    @Override
    public DataFrame drop(IntArray columns) {
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

    @Override
    public DataFrame getRecord(IntArray records) {
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
