package org.briljantframework.dataframe;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;

import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.Utils;
import org.briljantframework.array.Array;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.dataframe.join.InnerJoin;
import org.briljantframework.dataframe.join.JoinOperation;
import org.briljantframework.dataframe.join.JoinType;
import org.briljantframework.dataframe.join.JoinUtils;
import org.briljantframework.dataframe.join.LeftOuterJoin;
import org.briljantframework.dataframe.join.OuterJoin;
import org.briljantframework.function.Aggregator;
import org.briljantframework.sort.QuickSort;
import org.briljantframework.vector.GenericVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Implements some default behaviour for DataFrames
 *
 * @author Isak Karlsson
 */
public abstract class AbstractDataFrame implements DataFrame {

  private Index recordIndex;
  private Index columnIndex;

  protected AbstractDataFrame() {

  }

  @Override
  public <T> DataFrame sort(Class<? extends T> cls, Comparator<? super T> cmp, int column) {
    DataFrame.Builder builder = newCopyBuilder();
    Vector tmp = builder.getColumn(column);
    IntObjectMap<Object> map = new IntObjectOpenHashMap<>();
    for (Index.Entry entry : getRecordIndex().entrySet()) {
      map.put(entry.index(), entry.key());
    }
    QuickSort.quickSort(
        0, rows(),
        (a, b) -> cmp.compare(tmp.get(cls, a), tmp.get(cls, b)),
        (a, b) -> {
          builder.swap(a, b);
          Utils.swap(map, a, b);
        });

    Index.Builder recordIndex = new HashIndex.Builder();
    for (IntObjectCursor<Object> i : map) {
      recordIndex.set(i.value, i.key);
    }
    DataFrame df = builder.build();
    df.setRecordIndex(recordIndex.build());
    df.setColumnIndex(getColumnIndex());
    return df;
  }

  @Override
  public DataFrame sort(int column) {
    return sort(SortOrder.ASC, column);
  }

  @Override
  public DataFrame sort(SortOrder order, int column) {
    DataFrame.Builder builder = newCopyBuilder();
    Vector temporaryVector = builder.getColumn(column);
    IntObjectMap<Object> map = new IntObjectOpenHashMap<>();
    for (Index.Entry entry : getRecordIndex().entrySet()) {
      map.put(entry.index(), entry.key());
    }
    QuickSort.quickSort(0, rows(), (a, b) -> {
      int compare = temporaryVector.compare(a, b);
      return order == SortOrder.ASC ? compare : -compare;
    }, (a, b) -> {
      builder.swap(a, b);
      Utils.swap(map, a, b);
    });
    Index.Builder recordIndex = new HashIndex.Builder();
    for (IntObjectCursor<Object> i : map) {
      recordIndex.set(i.value, i.key);
    }

    DataFrame df = builder.build();
    df.setRecordIndex(recordIndex.build());
    df.setColumnIndex(getColumnIndex());
    return df;
  }

  @Override
  public DataFrame head(int n) {
    if (n >= rows()) {
      return this;
    }
    DataFrame.Builder builder = newBuilder();
    Index.Builder recordIndex = getRecordIndex().newBuilder();

    boolean first = true;
    for (int column = 0; column < columns(); column++) {
      builder.addColumnBuilder(getType(column).newBuilder(n));
      for (int i = 0; i < n; i++) {
        if (first) {
          recordIndex.add(getRecordIndex().get(i));
        }
        builder.set(i, column, this, i, column);
      }
      first = false;
    }

    DataFrame df = builder.build();
    df.setColumnIndex(getColumnIndex());
    df.setRecordIndex(recordIndex.build());
    return df;
  }

  @Override
  public DataFrame indexOn(int col) {
    DataFrame.Builder builder = newCopyBuilder();
    Index.Builder columnIndex = getColumnIndex().newBuilder();
    builder.removeColumn(col);
    for (Index.Entry entry : getColumnIndex().entrySet()) {
      if (entry.index() != col) {
        columnIndex.set(entry.key(), entry.index() > col ? entry.index() - 1 : entry.index());
      }
    }

    DataFrame df = builder.build();
    df.setRecordIndex(HashIndex.from(get(col)));
    df.setColumnIndex(columnIndex.build());
    return df;
  }

  @Override
  public DataFrame join(JoinType type, DataFrame other) {
    return join(type, other, intersectingIndicies(getColumnIndex(), other.getColumnIndex()));
  }

  @Override
  public DataFrame join(JoinType type, DataFrame other, Collection<Integer> columns) {
    return doJoin(type, other, columns);
  }

  @Override
  public <T> DataFrame apply(Class<? extends T> cls, UnaryOperator<T> op,
                             Collection<Integer> columns) {
    DataFrame.Builder builder = newBuilder();
    for (int j = 0; j < columns(); j++) {
      Vector column = get(j);
      builder.addColumnBuilder(column.newBuilder());
      if (columns.contains(j)) {
        for (int i = 0; i < column.size(); i++) {
          T value = column.get(cls, i);
          T transformed = op.apply(value);
          if (Is.NA(transformed)) {
            builder.set(i, j, this, i, j);
          } else {
            builder.set(i, j, transformed);
          }
        }
      } else {
        for (int i = 0; i < column.size(); i++) {
          builder.set(i, j, column, i);
        }
      }
    }
    return transferIndices(builder);
  }

  @Override
  public <T> DataFrame apply(Class<? extends T> cls, UnaryOperator<T> op) {
    DataFrame.Builder builder = newBuilder();
    for (int j = 0; j < columns(); j++) {
      Vector column = get(j);
      builder.addColumnBuilder(column.newBuilder());
      for (int i = 0; i < column.size(); i++) {
        T value = column.get(cls, i);
        T transformed = op.apply(value);
        if (Is.NA(transformed)) {
          builder.set(i, j, this, i, j);
        } else {
          builder.set(i, j, transformed);
        }
      }
    }
    return transferIndices(builder);
  }

  @Override
  public <T> Vector reduce(Class<? extends T> cls, T init, BinaryOperator<T> op) {
    Set<VectorType> types = getColumns().stream().map(Vector::getType).collect(Collectors.toSet());
    Vector.Builder builder;
    if (types.size() == 1) {
      builder = types.iterator().next().newBuilder();
    } else {
      builder = new GenericVector.Builder(Object.class);
    }
    for (int j = 0; j < columns(); j++) {
      Vector col = get(j);
      T val = init;
      for (int i = 0; i < col.size(); i++) {
        val = op.apply(col.get(cls, i), val);
      }
      builder.set(j, val);
    }
    Vector build = builder.build();
    build.setIndex(getColumnIndex());
    return build;
  }

  @Override
  public Vector reduce(Function<Vector, Object> op) {
    Vector.Builder builder = new GenericVector.Builder(Object.class);
    for (int j = 0; j < columns(); j++) {
      builder.set(j, op.apply(get(j)));
    }
    Vector v = builder.build();
    v.setIndex(getColumnIndex());
    return v;
  }

  @Override
  public <T, C> Vector aggregate(Class<T> cls,
                                 Aggregator<? super T, ? extends T, C> aggregator) {
    return aggregate(cls, cls, aggregator);
  }

  @Override
  public <T, R, C> Vector aggregate(Class<T> in, Class<R> out,
                                    Aggregator<? super T, ? extends R, C> aggregator) {
    Vector.Builder builder = Vec.typeOf(out).newBuilder();
    Index.Builder columnIndex = new HashIndex.Builder();

    int column = 0;
    for (int j = 0; j < columns(); j++) {
      Vector vec = get(j);
      if (in.isAssignableFrom(vec.getType().getDataClass())) {
        columnIndex.add(getColumnIndex().get(j));
        C accumulator = aggregator.supplier().get();
        for (int i = 0; i < rows(); i++) {
          aggregator.accumulator().accept(accumulator, vec.get(in, i));
        }
        builder.set(column++, aggregator.finisher().apply(accumulator));
      }
    }
    Vector v = builder.build();
    v.setIndex(columnIndex.build());
    return v;
  }

  @Override
  public DataFrameGroupBy groupBy(Function<? super Vector, Object> keyFunction) {
    HashMap<Object, IntVector.Builder> groups = new HashMap<>();
    for (int i = 0; i < rows(); i++) {
      Vector record = getRecord(i);
      Object key = keyFunction.apply(record);
      groups.computeIfAbsent(key, a -> new IntVector.Builder()).add(i);
    }
    return new HashDataFrameGroupBy(this, groups);
  }

  @Override
  public DataFrame transform(Function<? super Vector, ? extends Vector> transform) {
    DataFrame.Builder builder = newBuilder();
    for (int j = 0; j < columns(); j++) {
      Vector col = get(j);
      Vector transformed = transform.apply(col);
      Check.size(rows(), transformed.size());
      builder.addColumnBuilder(transformed.getType());
      for (int i = 0; i < rows(); i++) {
        builder.set(i, j, transformed, i);
      }
    }
    return transferIndices(builder);
  }

  protected DataFrame transferIndices(Builder builder) {
    DataFrame df = builder.build();
    df.setRecordIndex(getRecordIndex());
    df.setColumnIndex(getColumnIndex());
    return df;
  }

  private DataFrame doJoin(JoinType type, DataFrame other, Collection<Integer> columns) {
    JoinOperation op;
    DataFrame t = this;
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
        t = other;
        other = this;
        break;
      default:
        throw new UnsupportedOperationException(String.valueOf(type));
    }
    return op.createJoiner(JoinUtils.createJoinKeys(t, other, columns)).join(t, other, columns);
  }

  private Collection<Integer> intersectingIndicies(Index a, Index b) {
    Set<Object> on = new HashSet<>(a.keySet());
    Set<Object> bCol = new HashSet<>(b.keySet());
    on.retainAll(bCol);

    List<Integer> indices = new ArrayList<>();
    for (Object o : on) {
      if (a.contains(o)) {
        indices.add(a.index(o));
      } else if (b.contains(o)) {
        indices.add(b.index(o));
      }
    }
    return indices;
  }

  @Override
  public DataFrame add(Vector column) {
    return newCopyBuilder().addColumn(column).build();
  }

  @Override
  public DataFrame insert(int index, Object key, Vector column) {
    DataFrame df = newCopyBuilder().insertColumn(index, column).build();
    Index.Builder columnIndex = getColumnIndex().newBuilder();
    columnIndex.set(key, index);
    for (Index.Entry entry : df.getColumnIndex().entrySet()) {
      int newIndex;
      int eIdx = entry.index();
      if (eIdx > index) {
        newIndex = eIdx + 1;
      } else {
        newIndex = eIdx;
      }
      columnIndex.set(entry.key(), newIndex);
    }
    df.setColumnIndex(columnIndex.build());
    df.setRecordIndex(getRecordIndex());
    return df;
  }

  @Override
  public Collection<Vector> getColumns() {
    return new AbstractCollection<Vector>() {
      @Override
      public Iterator<Vector> iterator() {
        return new UnmodifiableIterator<Vector>() {
          private int current = 0;

          @Override
          public boolean hasNext() {
            return current < columns();
          }

          @Override
          public Vector next() {
            return get(current++);
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
  @Override
  public Vector get(int index) {
    return new ColumnView(this, index);
  }

  @Override
  public Vector get(Object key) {
    return get(getColumnIndex().index(key));
  }

  @Override
  public DataFrame dropna() {
    return DataFrames.dropMissingColumns(this);
  }

  /**
   * Constructs a new DataFrame by dropping {@code index}.
   *
   * This implementations rely on {@link #newCopyBuilder()} returning a builder and that {@link
   * org.briljantframework.dataframe.DataFrame.Builder#removeColumn(int)}.
   *
   * @param index the index
   * @return a new data frame as created by {@link #newCopyBuilder()}
   */
  @Override
  public DataFrame drop(int index) {
    Index.Builder builder = new HashIndex.Builder();
    for (int i = 0; i < columns(); i++) {
      if (index == i) {
        continue;
      }
      if (i > index) {
        builder.set(getColumnIndex().get(i), i - 1);
      } else {
        builder.set(getColumnIndex().get(i), i);
      }
    }

    DataFrame df = newCopyBuilder().removeColumn(index).build();
    df.setRecordIndex(getRecordIndex());
    df.setColumnIndex(builder.build());
    return df;
  }

  /**
   * Constructs a new DataFrame by dropping the columns in {@code indexes}.
   *
   * This implementations rely on {@link #newBuilder()} returning a builder and that {@link
   * org.briljantframework.dataframe.DataFrame.Builder#addColumn(org.briljantframework.vector.Vector)}
   * adds a vector.
   *
   * @param indexes collection of indexes
   * @return a new data frame as created by {@link #newBuilder()}
   */
  @Override
  public DataFrame drop(Iterable<Integer> indexes) {
    Set<Integer> hash;
    if (!(indexes instanceof Set)) {
      hash = Sets.newHashSet(indexes);
    } else {
      hash = (Set<Integer>) indexes;
    }
    Builder builder = newBuilder();
    Index.Builder columnIndex = getColumnIndex().newBuilder();
    for (int i = 0; i < columns(); i++) {
      if (!hash.contains(i)) {
        columnIndex.add(getColumnIndex().get(i));
        builder.addColumn(get(i));
      }
    }

    return transferRecordIndex(builder, columnIndex);
  }

  @Override
  public DataFrame drop(Predicate<? super Vector> predicate) {
    Builder builder = newBuilder();
    Index.Builder columnIndex = getColumnIndex().newBuilder();
    for (int j = 0; j < columns(); j++) {
      Vector column = get(j);
      if (predicate.test(column)) {
        builder.addColumn(column);
        columnIndex.add(getColumnIndex().get(j));
      }
    }
    return transferRecordIndex(builder, columnIndex);
  }

  protected DataFrame transferRecordIndex(Builder builder, Index.Builder columnIndex) {
    DataFrame df = builder.build();
    df.setColumnIndex(columnIndex.build());
    df.setRecordIndex(getRecordIndex());
    return df;
  }

  /**
   * Constructs a new DataFrame by including the rows in {@code indexes}.
   *
   * This implementation rely on {@link #newBuilder()} and {@link Builder#set(int, int, DataFrame,
   * int, int)}.
   *
   * @param indexes collection of indexes
   * @return a new data frame as created by {@link #newBuilder()}
   */
  @Override
  public DataFrame retain(Iterable<Integer> indexes) {
    Builder builder = newBuilder();
    for (int j : indexes) {
      for (int i = 0; i < rows(); i++) {
        builder.set(i, j, this, i, j);
      }
    }
    return builder.build();
  }

  @Override
  public Collection<Vector> getRecords() {
    return new AbstractCollection<Vector>() {
      @Override
      public Iterator<Vector> iterator() {
        return new UnmodifiableIterator<Vector>() {
          public int current;

          @Override
          public boolean hasNext() {
            return current < size();
          }

          @Override
          public Vector next() {
            return getRecord(current++);
          }
        };
      }

      @Override
      public int size() {
        return rows();
      }
    };
  }

  /**
   * Returns the row at {@code index}. This implementation supplies a view into the underlying data
   * frame.
   *
   * @param index the index
   * @return a view of the row at {@code index}
   */
  @Override
  public Vector getRecord(int index) {
    return new RowView(this, index);
  }

  /**
   * Constructs a new DataFrame by including the rows in {@code indexes}
   *
   * This implementation rely on {@link #newBuilder()} and {@link Builder#set(int, int, DataFrame,
   * int, int)}.
   *
   * @param indexes the indexes to take
   * @return a new data frame as created by {@link #newBuilder()}
   */
  @Override
  public DataFrame getRecords(Iterable<Integer> indexes) {
    Builder builder = newBuilder();
//    builder.getColumnNames().putAll(getColumnNames());
    for (Number num : indexes) {
      int i = num.intValue();
      for (int j = 0; j < columns(); j++) {
        builder.set(i, j, this, i, j);
      }
    }

    return builder.build();
  }

  /**
   * Constructs a new DataFrame by dropping the rows in {@code indexes}
   *
   * This implementation rely on {@link #newBuilder()} and {@link Builder#set(int, int, DataFrame,
   * int, int)}
   *
   * @param indexes the indexes to drop
   * @return a new DataFrame as created by {@link #newBuilder()}
   */
  @Override
  public DataFrame removeRecords(Iterable<Integer> indexes) {
    Set<Integer> set;
    if (!(indexes instanceof Set)) {
      set = Sets.newHashSet(indexes);
    } else {
      set = (Set<Integer>) indexes;
    }
    Builder builder = newBuilder();
//    builder.getColumnNames().putAll(getColumnNames());
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        if (!set.contains(i)) {
          builder.set(i, j, this, i, j);
        }
      }
    }

    return builder.build();
  }

  @Override
  public DataFrame insertRecord(int index, Object key, Vector record) {
    return newCopyBuilder().insertRecord(index, record).build();
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
    Preconditions.checkNotNull(index);
    Check.size(rows(), index.size());
    this.recordIndex = index;
    getColumns().forEach(v -> v.setIndex(index));
  }

  @Override
  public final void setColumnIndex(Index index) {
    Preconditions.checkNotNull(index);
    Check.size(columns(), index.size());
    this.columnIndex = index;
    getRecords().forEach(v -> v.setIndex(index));
  }

  /**
   * Converts the DataFrame to an {@link org.briljantframework.array.DoubleArray}. This
   * implementation rely on {@link #getAsDouble(int, int)}. Sub-classes are allowed to return any
   * concrete implementation of {@link org.briljantframework.array.DoubleArray}.
   *
   * @return a new matrix
   */
  @Override
  public Array<Object> toArray() {
    Array<Object> matrix = Bj.referenceArray(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(Object.class, i, j));
      }
    }
    return matrix;
  }

  @Override
  public DoubleArray toDoubleArray() {
    DoubleArray matrix = Bj.doubleArray(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, getAsDouble(i, j));
      }
    }
    return matrix;
  }

  @Override
  public DataFrame resetIndex() {
    DataFrame.Builder builder = newBuilder().addColumnBuilder(Vec.typeOf(Object.class));
    Index.Builder columnIndex = new HashIndex.Builder();
    columnIndex.add("index");
    for (int i = 0; i < rows(); i++) {
      builder.set(i, 0, getRecordIndex().get(i));
    }
    int column = 1;
    for (int j = 0; j < columns(); j++) {
      columnIndex.add(getColumnIndex().get(j));
      for (int i = 0; i < rows(); i++) {
        builder.set(i, column, this, i, j);
      }
      column++;
    }
    DataFrame df = builder.build();
    df.setRecordIndex(new IntIndex(rows()));
    df.setColumnIndex(columnIndex.build());
    return df;
  }

  @Override
  public List<VectorType> getTypes() {
    List<VectorType> types = new ArrayList<>();
    for (int i = 0; i < columns(); i++) {
      types.add(getType(i));
    }
    return types;
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
    if (this == obj) {
      return true;
    }

    if (obj instanceof DataFrame) {
      DataFrame o = (DataFrame) obj;
      if (o.rows() == rows()) {
        for (int i = 0; i < columns(); i++) {
          Vector a = get(i);
          Vector b = o.get(i);
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
    return DataFrames.toTabularString(this);
  }

  protected static abstract class AbstractBuilder implements Builder {

    protected AbstractBuilder() {
    }

    @Override
    public Vector getColumn(int col) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector getRecord(int row) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DataFrame.Builder addColumnBuilder(Vector.Builder builder) {
      Vector vector = builder.build();
      return addColumn(vector);
    }

    @Override
    public Builder addColumnBuilder(VectorType type) {
      return addColumnBuilder(type.newBuilder());
    }

    @Override
    public Builder addColumn(Vector vector) {
      return insertColumn(columns(), vector);
    }

    @Override
    public Builder insertColumn(int index, Vector.Builder builder) {
      return insertColumn(index, builder.build());
    }

    @Override
    public Builder insertColumn(int index, Vector vector) {
      final int size = vector.size();
      for (int i = 0; i < size; i++) {
        set(i, index, vector, i);
      }
      for (int i = size; i < rows(); i++) {
        setNA(i, index);
      }
      return this;
    }

    @Override
    public Builder addRecord(Vector.Builder builder) {
      return addRecord(builder.build());
    }

    @Override
    public Builder addRecord(Vector vector) {
      return insertRecord(rows(), vector);
    }

    @Override
    public Builder insertRecord(int index, Vector.Builder builder) {
      return insertRecord(index, builder.build());
    }

    @Override
    public Builder insertRecord(int index, Vector vector) {
      final int columns = columns();
      final int size = vector.size();
      for (int j = 0; j < Math.max(size, columns); j++) {
        if (j < size) {
          set(index, j, vector, j);
        } else {
          setNA(index, j);
        }
      }
      return this;
    }
  }

}
