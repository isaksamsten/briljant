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

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.array.Array;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.dataframe.join.InnerJoin;
import org.briljantframework.dataframe.join.JoinOperation;
import org.briljantframework.dataframe.join.JoinType;
import org.briljantframework.dataframe.join.JoinUtils;
import org.briljantframework.dataframe.join.Joiner;
import org.briljantframework.dataframe.join.LeftOuterJoin;
import org.briljantframework.dataframe.join.OuterJoin;
import org.briljantframework.index.DataFrameLocationGetter;
import org.briljantframework.index.DataFrameLocationSetter;
import org.briljantframework.index.Index;
import org.briljantframework.index.IntIndex;
import org.briljantframework.index.VectorLocationGetter;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.EntryReader;
import org.briljantframework.sort.QuickSort;
import org.briljantframework.vector.GenericVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.TypeInferenceVectorBuilder;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Implements some default behaviour for DataFrames
 *
 * @author Isak Karlsson
 */
public abstract class AbstractDataFrame implements DataFrame {

  private final DataFrameLocationGetterImpl locationIndexer = new DataFrameLocationGetterImpl();
  private Index recordIndex = null;
  private Index columnIndex = null;

  protected AbstractDataFrame(Index columnIndex, Index recordIndex) {
    this.columnIndex = columnIndex;
    this.recordIndex = recordIndex;
  }

  @Override
  public <T> DataFrame sort(Class<? extends T> cls, Comparator<? super T> cmp, Object key) {
    DataFrame.Builder builder = newCopyBuilder();
    VectorLocationGetter temp = builder.getTemporaryDataFrame().get(key).loc();
    QuickSort.quickSort(
        0, rows(),
        (a, b) -> cmp.compare(temp.get(cls, a), temp.get(cls, b)),
        builder.loc()::swapRecords);
    return builder.build();
  }

  @Override
  public DataFrame sort(Object key) {
    return sort(SortOrder.ASC, key);
  }

  @Override
  public DataFrame sort(SortOrder order, Object key) {
    DataFrame.Builder builder = newCopyBuilder();
    VectorLocationGetter temp = builder.getTemporaryDataFrame().get(key).loc();
    QuickSort.quickSort(0, builder.rows(), (a, b) -> {
      int cmp = temp.compare(a, b);
      return order == SortOrder.ASC ? cmp : -cmp;
    }, builder.loc()::swap);
    return builder.build();
  }

  @Override
  public DataFrame head(int n) {
    if (n >= rows()) {
      return this;
    }
    DataFrame.Builder builder = newBuilder();
    int i = 0;
    for (Object key : getRecordIndex().keySet()) {
      if (i >= n) {
        break;
      }
      builder.setRecord(key, getRecord(key));
      i++;
    }
    DataFrame df = builder.build();
    df.setColumnIndex(getColumnIndex());
    return df;
  }

  @Override
  public DataFrame indexOn(Object key) {
    ObjectIndex index = ObjectIndex.from(get(key)); // fail fast. throws on duplicates
    DataFrame.Builder builder = newCopyBuilder();
    builder.remove(key);
    DataFrame df = builder.build();
    df.setRecordIndex(index);
    return df;
  }

  @Override
  public DataFrame join(JoinType type, DataFrame other) {
    return joinAt(type, other, intersectingIndicies(getColumnIndex(), other.getColumnIndex()));
  }

  protected DataFrame joinAt(JoinType type, DataFrame other, Collection<Integer> columns) {
    return doJoin(type, other, columns);
  }

  private DataFrame doJoin(JoinType type, DataFrame other, Collection<Integer> columns) {
    JoinOperation op;
    DataFrame self = this;
    switch (type) {
      case INNER:
        op = InnerJoin.getInstance();
        break;
      case OUTER:
        op = OuterJoin.getInstance();
        break;
      case LEFT:
        op = LeftOuterJoin.getInstance();
        break;
      case RIGHT:
        op = LeftOuterJoin.getInstance();
        self = other;
        other = this;
        break;
      default:
        throw new UnsupportedOperationException(String.valueOf(type));
    }
    Joiner joiner = op.createJoiner(JoinUtils.createJoinKeys(self, other, columns));
    return joiner.join(self, other, columns);
  }

  private Collection<Integer> intersectingIndicies(Index a, Index b) {
    Set<Object> on = new HashSet<>(a.keySet());
    Set<Object> bCol = new HashSet<>(b.keySet());
    on.retainAll(bCol);

    List<Integer> indices = new ArrayList<>();
    for (Object o : on) {
      if (a.contains(o)) {
        indices.add(a.getLocation(o));
      } else if (b.contains(o)) {
        indices.add(b.getLocation(o));
      }
    }
    return indices;
  }

  @Override
  public <T> DataFrame map(Class<T> cls, Function<? super T, Object> op) {
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
    return transferIndices(builder);
  }

  @Override
  public <T> Vector reduce(Class<? extends T> cls, T init, BinaryOperator<T> op) {
    Vector.Builder builder = findCoherentColumnBuilder();
    for (int j = 0, columns = columns(); j < columns; j++) {
      VectorLocationGetter column = getAt(j).loc();
      T result = init;
      for (int i = 0, size = rows(); i < size; i++) {
        result = op.apply(result, column.get(cls, i));
      }
      builder.loc().set(j, result);
    }
    Vector build = builder.build();
    build.setIndex(getColumnIndex()); // cheaper to reuse the index
    return build;
  }

  private Vector.Builder findCoherentColumnBuilder() {
    Set<VectorType> types = getColumns().stream().map(Vector::getType).collect(Collectors.toSet());
    return types.size() == 1 ? types.iterator().next().newBuilder() : new GenericVector.Builder();
  }

  @Override
  public Vector reduce(Function<Vector, Object> op) {
    Vector.Builder builder = findCoherentColumnBuilder();
    for (int j = 0, columns = columns(); j < columns; j++) {
      builder.set(j, op.apply(getAt(j)));
    }
    Vector v = builder.build();
    v.setIndex(getColumnIndex());
    return v;
  }

  @Override
  public <T, C> Vector collect(Class<T> cls, Collector<? super T, C, ? extends T> collector) {
    return collect(cls, cls, collector);
  }

  @Override
  public <T, R, C> Vector collect(Class<T> in, Class<R> out,
                                  Collector<? super T, C, ? extends R> collector) {
    Vector.Builder builder = VectorType.from(out).newBuilder();

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
  public DataFrameGroupBy groupBy(Object columnKey) {
    HashMap<Object, IntVector.Builder> groups = new LinkedHashMap<>();
    Vector column = get(columnKey);
    VectorLocationGetter loc = column.loc();
    for (int i = 0, size = column.size(); i < size; i++) {
      groups.computeIfAbsent(loc.get(Object.class, i), a -> new IntVector.Builder()).add(i);
    }
    return new HashDataFrameGroupBy(this, groups, columnKey);
  }

  @Override
  public DataFrameGroupBy groupBy(Object... columnKeys) {
    HashMap<Object, IntVector.Builder> groups = new LinkedHashMap<>();
    for (int i = 0, size = rows(); i < size; i++) {
      List<Object> keys = new ArrayList<>(columnKeys.length);
      for (Object columnKey : columnKeys) {
        keys.add(get(columnKey).get(Object.class, i));
      }
      groups.computeIfAbsent(keys, a -> new IntVector.Builder()).add(i);
    }
    return new HashDataFrameGroupBy(this, groups, columnKeys);
  }

  @Override
  public DataFrameGroupBy groupBy(UnaryOperator<Object> keyFunction) {
    HashMap<Object, IntVector.Builder> groups = new LinkedHashMap<>();
    for (Index.Entry entry : getRecordIndex().entrySet()) {
      groups.computeIfAbsent(
          keyFunction.apply(entry.getKey()),
          a -> new IntVector.Builder()).add(entry.getValue()
      );
    }
    return new HashDataFrameGroupBy(this, groups);
  }

  @Override
  public DataFrame apply(Function<? super Vector, ? extends Vector> transform) {
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
  public <T, C> DataFrame apply(
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

  protected DataFrame transferIndices(Builder builder) {
    DataFrame df = builder.build();
    df.setRecordIndex(getRecordIndex());
    df.setColumnIndex(getColumnIndex());
    return df;
  }

  @Override
  public DataFrame add(Vector column) {
    return newCopyBuilder().add(column).build();
  }

  @Override
  public Collection<Vector> getColumns() {
    return new AbstractCollection<Vector>() {
      @Override
      public Iterator<Vector> iterator() {
        return new Iterator<Vector>() {
          private int current = 0;

          @Override
          public boolean hasNext() {
            return current < columns();
          }

          @Override
          public Vector next() {
            return getAt(current++);
          }
        };
      }

      @Override
      public int size() {
        return columns();
      }
    };
  }

  /**
   * Returns the column at {@code index}. This implementation supplies a view into the underlying
   * data frame.
   *
   * @param index the index
   * @return a view of column {@code index}
   */
  protected Vector getAt(int index) {
    return new ColumnView(this, getTypeAt(index), index);
  }

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
    bdf.setRecordIndex(getRecordIndex());
    return bdf;
  }

  public Vector get(Object key) {
    return getAt(getColumnIndex().getLocation(key));
  }

  @Override
  public DataFrame get(Object... keys) {
    return getAt(getColumnIndex().indices(keys));
  }

  @Override
  public final <T> T get(Class<T> cls, Object row, Object col) {
    return getAt(cls, getRecordIndex().getLocation(row), getColumnIndex().getLocation(col));
  }

  @Override
  public final double getAsDouble(Object row, Object col) {
    return getAsDoubleAt(getRecordIndex().getLocation(row), getColumnIndex().getLocation(col));
  }

  @Override
  public final int getAsInt(Object row, Object col) {
    return getAsIntAt(getRecordIndex().getLocation(row), getColumnIndex().getLocation(col));
  }

  @Override
  public final boolean isNA(Object row, Object col) {
    return isNaAt(getRecordIndex().getLocation(row), getColumnIndex().getLocation(col));
  }

  @Override
  public String toString(Object row, Object col) {
    return toStringAt(getRecordIndex().getLocation(row), getColumnIndex().getLocation(col));
  }

  /**
   * Constructs a new DataFrame by dropping {@code index}.
   *
   * @param index the index
   * @return a new data frame as created by {@link #newCopyBuilder()}
   */
  protected DataFrame dropAt(int index) {
//    Index.Builder columnIndex = new HashIndex.Builder();
//    for (int i = 0; i < columns(); i++) {
//      if (index == i) {
//        continue;
//      }
//      if (i > index) {
//        columnIndex.set(getColumnIndex().getKey(i), i - 1);
//      } else {
//        columnIndex.set(getColumnIndex().getKey(i), i);
//      }
//    }
//
//    Builder builder = newCopyBuilder();
//    builder.loc().remove(index);
//
//    DataFrame df = builder.build();
//    df.setRecordIndex(getRecordIndex());
//    df.setColumnIndex(columnIndex.build());
//    return df;
    throw new UnsupportedOperationException();
  }

  /**
   * Constructs a new DataFrame by dropping the columns in {@code indexes}.
   *
   * This implementations rely on {@link #newBuilder()} returning a builder and that {@link
   * org.briljantframework.dataframe.DataFrame.Builder#add(org.briljantframework.vector.Vector)}
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
        builder.add(getAt(i));
      }
    }

    return transferRecordIndex(builder, columnIndex);
  }

  @Override
  public DataFrame dropna() {
    return DataFrames.dropMissingColumns(this);
  }

  @Override
  public DataFrame drop(Object key) {
    if (getColumnIndex().contains(key)) {
      return newCopyBuilder().remove(key).build();
    }
    throw new NoSuchElementException(key.toString());
  }

  @Override
  public DataFrame drop(Object... keys) {
    return dropAt(getColumnIndex().indices(keys));
  }

  @Override
  public DataFrame drop(Predicate<? super Vector> predicate) {
    Builder builder = newBuilder();
    Index.Builder columnIndex = getColumnIndex().newBuilder();
    for (int j = 0; j < columns(); j++) {
      Vector column = getAt(j);
      if (predicate.test(column)) {
        builder.add(column);
        columnIndex.add(getColumnIndex().getKey(j));
      }
    }
    return transferRecordIndex(builder, columnIndex);
  }

  @Override
  public Vector getRecord(Object key) {
    return getRecordAt(getRecordIndex().getLocation(key));
  }

  protected DataFrame transferRecordIndex(Builder builder, Index.Builder columnIndex) {
    DataFrame df = builder.build();
    df.setColumnIndex(columnIndex.build());
    df.setRecordIndex(getRecordIndex());
    return df;
  }

  @Override
  public Collection<Vector> getRecords() {
    return new AbstractCollection<Vector>() {
      @Override
      public Iterator<Vector> iterator() {
        return new Iterator<Vector>() {
          public int current;

          @Override
          public boolean hasNext() {
            return current < size();
          }

          @Override
          public Vector next() {
            return getRecordAt(current++);
          }
        };
      }

      @Override
      public int size() {
        return rows();
      }
    };
  }

  protected Vector getRecordAt(int index) {
    return new RowView(this, index);
  }

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
   * Constructs a new DataFrame by dropping the rows in {@code indexes}
   *
   * @param indexes the indexes to drop
   * @return a new DataFrame as created by {@link #newBuilder()}
   */
  @Override
  public DataFrame removeRecords(Collection<Integer> indexes) {
    Set<Integer> set;
    if (!(indexes instanceof Set)) {
      set = new HashSet<>(indexes);
    } else {
      set = (Set<Integer>) indexes;
    }
    Builder builder = newBuilder();
//    builder.getColumnNames().putAll(getColumnNames());
    DataFrameLocationSetter to = builder.loc();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        if (!set.contains(i)) {
          to.set(i, j, this, i, j);
        }
      }
    }

    return builder.build();
  }

  @Override
  public DataFrame addRecord(Vector record) {
    return newCopyBuilder().addRecord(record).build();
  }

  @Override
  public DataFrame stack(Iterable<DataFrame> dataFrames) {
    DataFrame.Builder builder = newCopyBuilder();
    for (DataFrame dataFrame : dataFrames) {
      Check.size(this.columns(), dataFrame.columns());
      builder.stack(dataFrame);
    }
    return builder.build();
  }

  @Override
  public DataFrame concat(Iterable<DataFrame> dataFrames) {
    DataFrame.Builder builder = newCopyBuilder();
    for (DataFrame dataFrame : dataFrames) {
      Check.size(this.columns(), dataFrame.columns());
      builder.concat(dataFrame);
    }
    return builder.build();
  }

  @Override
  public DataFrame copy() {
    DataFrame df = newCopyBuilder().build();
    df.setColumnIndex(getColumnIndex());
    df.setRecordIndex(getRecordIndex());
    return df;
  }

  protected abstract VectorType getTypeAt(int index);

  @Override
  public final Index getRecordIndex() {
    if (recordIndex == null) {
      recordIndex = new IntIndex(rows());
    }
    return recordIndex;
  }

  public final Index getColumnIndex() {
    if (columnIndex == null) {
      columnIndex = new IntIndex(columns());
    }
    return columnIndex;
  }

  @Override
  public final void setRecordIndex(Index index) {
    Objects.requireNonNull(index);
    Check.size(rows(), index.size());
    this.recordIndex = index;
    getColumns().forEach(v -> v.setIndex(index));
  }

  @Override
  public final void setColumnIndex(Index index) {
    Objects.requireNonNull(index);
    Check.size(columns(), index.size());
    this.columnIndex = index;
    getRecords().forEach(v -> v.setIndex(index));
  }

  @Override
  public DataFrameLocationGetter loc() {
    return locationIndexer;
  }

  /**
   * @return a new matrix
   */
  @Override
  public Array<Object> toArray() {
    Array<Object> matrix = Bj.referenceArray(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, getAt(Object.class, i, j));
      }
    }
    return matrix;
  }

  @Override
  public DoubleArray toDoubleArray() {
    DoubleArray matrix = Bj.doubleArray(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, getAsDoubleAt(i, j));
      }
    }
    return matrix;
  }

  @Override
  public DataFrame resetIndex() {
    DataFrame.Builder builder = newBuilder().add(VectorType.from(Object.class));
    Index.Builder columnIndex = new ObjectIndex.Builder();
    columnIndex.add("index");
    for (int i = 0; i < rows(); i++) {
      builder.loc().set(i, 0, getRecordIndex().getKey(i));
    }
    int column = 1;
    for (int j = 0; j < columns(); j++) {
      columnIndex.add(getColumnIndex().getKey(j));
      for (int i = 0; i < rows(); i++) {
        builder.loc().set(i, column, this, i, j);
      }
      column++;
    }
    DataFrame df = builder.build();
    df.setRecordIndex(new IntIndex(rows()));
    df.setColumnIndex(columnIndex.build());
    return df;
  }

  /**
   * Returns an iterator over the rows of this DataFrame
   *
   * @return a row iterator
   */
  @Override
  public Iterator<Vector> iterator() {
    return getRecords().iterator();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }

    if (obj.getClass().equals(getClass())) {
      DataFrame o = (DataFrame) obj;
      if (o.rows() == rows()) {
        for (int i = 0; i < columns(); i++) {
          Vector a = getAt(i);
          Vector b = o.loc().get(i); // TODO: compare based on index
          if (!a.equals(b)) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Returns a tabular string representation of this DataFrame.
   *
   * @return the string representation
   */
  @Override
  public String toString() {
    return DataFrames.toString(this);
  }

  /**
   * Get value at {@code row} and {@code column} as an instance of {@code T}. If conversion fails,
   * return {@code NA} as defined by {@link org.briljantframework.vector.Na#from(Class)}. The
   * conversion is performed according to the convention found in {@link
   * org.briljantframework.vector.Convert#to(Class, Object)}
   *
   * @param <T>    the type of the returned value
   * @param cls    the class
   * @param row    the row
   * @param column the column
   * @return an instance of {@code T}
   */
  protected abstract <T> T getAt(Class<T> cls, int row, int column);

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

  /**
   * This class provides a skeletal implementation of the {@link org.briljantframework.dataframe.DataFrame.Builder}
   * interface to minimize the effort required to implement this interface, including the handling
   * of key and index-based setters.
   *
   * <p> To implement this builder, the programmer needs to implement the following abstract
   * methods (the documentation for each method provides information on how to implement them):
   * <ul>
   * <li>{@link #setNaAt(int, int)}</li>
   * <li>{@link #setAt(int, int, Object)}</li>
   * <li>{@link #setAt(int, org.briljantframework.vector.Vector.Builder)}</li>
   * <li>{@link #setRecordAt(int, org.briljantframework.vector.Vector.Builder)}</li>
   * <li>{@link #setAt(int, int, org.briljantframework.vector.Vector, int)}</li>
   * <li>{@link #readEntry(org.briljantframework.io.DataEntry)}</li>
   * </ul>
   *
   * and the following methods from the {@link org.briljantframework.dataframe.DataFrame.Builder}
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
   * and {@link #getOrCreateRecordIndex(Object)}
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
     * #getOrCreateRecordIndex(Object)}.
     */
    private Index.Builder recordIndex;

    /**
     * Provide initial indexes
     *
     * @param from the dataframe to copy
     */
    protected AbstractBuilder(DataFrame from) {
      Index ci = from.getColumnIndex();
      Index ri = from.getRecordIndex();
      this.columnIndex = ci instanceof IntIndex ? null : ci.newCopyBuilder();
      this.recordIndex = ri instanceof IntIndex ? null : ri.newCopyBuilder();
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
      int r = getOrCreateRecordIndex(tr);
      int c = getOrCreateColumnIndex(tc);
      setAt(r, c, from, from.getRecordIndex().getLocation(fr),
            from.getColumnIndex().getLocation(fc));
      return this;
    }

    @Override
    public final Builder set(Object row, Object column, Vector from, Object key) {
      int r = getOrCreateRecordIndex(row);
      int c = getOrCreateColumnIndex(column);
      setAt(r, c, from, from.getIndex().getLocation(key));
      return this;
    }

    @Override
    public final Builder set(Object row, Object column, Object value) {
      int r = getOrCreateRecordIndex(row);
      int c = getOrCreateColumnIndex(column);
      setAt(r, c, value);
      return this;
    }

    @Override
    public final Builder set(Object key, Vector.Builder columnBuilder) {
      int columnIndex = getOrCreateColumnIndex(key);
      extendRecordIndex(columnBuilder.size());
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
      int recordIndex = getOrCreateRecordIndex(key);
      extendColumnIndex(recordBuilder.size());
      setRecordAt(recordIndex, recordBuilder);
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
      initializeRecordIndexer();
      int index = recordIndex.getLocation(key);
      loc().removeRecord(index);
      return this;
    }

    @Override
    public final Builder read(EntryReader entryReader) throws IOException {
      int entries = 0;
      while (entryReader.hasNext()) {
        DataEntry entry = entryReader.next();
        extendColumnIndex(entry.size());
        entries++;
        readEntry(entry);
      }
      extendRecordIndex(entries);

      return this;
    }

    private void initializeRecordIndexer() {
      if (recordIndex == null) {
        recordIndex = new IntIndex.Builder(rows());
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
      extendRecordIndex(record);
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
    private void extendRecordIndex(int r) {
      if (recordIndex != null) {
        recordIndex.extend(r);
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

    private final int getOrCreateRecordIndex(Object key) {
      initializeRecordIndexer();
      int index = rows();
      if (recordIndex.contains(key)) {
        index = recordIndex.getLocation(key);
      } else {
        recordIndex.add(key);
      }
      return index;
    }

    protected final Index getRecordIndex(int rows) {
      if (recordIndex == null) {
        return new IntIndex(rows);
      } else {
        return recordIndex.build();
      }
    }

    protected Index getColumnIndex(int columns) {
      if (columnIndex == null) {
        return new IntIndex(columns);
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
     * org.briljantframework.vector.Convert#to(Class, Object)}
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

    protected abstract void readEntry(DataEntry entry) throws IOException;

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
        extendRecordIndex(columnBuilder.size());
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
        extendRecordIndex(r + 1);
        extendColumnIndex(recordBuilder.size());
        setRecordAt(r, recordBuilder);
      }

      @Override
      public void removeRecord(int r) {
        if (recordIndex != null) {
          recordIndex.remove(r);
        }
        removeRecordAt(r);
      }

      @Override
      public void swapRecords(int a, int b) {
        if (recordIndex != null) {
          recordIndex.swap(a, b);
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
}
