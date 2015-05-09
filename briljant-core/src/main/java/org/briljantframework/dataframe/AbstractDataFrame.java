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
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.sort.QuickSort;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Implements some default behaviour for DataFrames
 *
 * @author Isak Karlsson
 */
public abstract class AbstractDataFrame implements DataFrame {

  /**
   * Index for the records. Might be null.
   */
  protected Index recordIndex;

  /**
   * Index for columns. Might be null.
   */
  protected Index columnIndex;

  protected AbstractDataFrame() {

  }

  @Override
  public DataFrame sort() {
    Index recordIndex = getRecordIndex();
    // Already sorted.
    if (recordIndex instanceof IntIndex) {
      return copy();
    } else {
      return newCopyBuilder().build()
          .setRecordIndex(HashIndex.sorted(getRecordIndex(), (a, b) -> {
            if (a instanceof Comparable && b instanceof Comparable) {
              @SuppressWarnings("unchecked")
              int cmp = ((Comparable<Object>) a).compareTo(b);
              return cmp;
            } else {
              return 0;
            }
          }))
          .setColumnIndex(getColumnIndex().copy());
    }
  }

  @Override
  public DataFrame sort(SortOrder order) {
    if (recordIndex instanceof IntIndex) {
      return copy();
    } else {
      Comparator<Object> cmp = (a, b) -> {
        if (a instanceof Comparable && b instanceof Comparable) {
          @SuppressWarnings("unchecked")
          int i = ((Comparable<Object>) a).compareTo(b);
          return i;
        } else {
          return 0;
        }
      };
      if (order == SortOrder.DESC) {
        cmp = cmp.reversed();
      }
      return newCopyBuilder().build()
          .setRecordIndex(HashIndex.sorted(getRecordIndex(), cmp))
          .setColumnIndex(getColumnIndex().copy());
    }

  }

  @Override
  public DataFrame sortBy(int column) {
    return sortBy(column, SortOrder.ASC);
  }

  @Override
  public DataFrame sortBy(int column, SortOrder order) {
    DataFrame.Builder builder = newCopyBuilder();
    Vector temporaryVector = builder.getColumn(column);
    IntObjectMap<Object> map = new IntObjectOpenHashMap<>();
    for (Index.Entry entry : getRecordIndex()) {
      map.put(entry.index(), entry.key());
    }
    QuickSort.quickSort(0, rows(), (a, b) -> {
      int compare = temporaryVector.compare(a, b);
      return order == SortOrder.ASC ? compare : -compare;
    }, (a, b) -> {
      builder.swap(a, b);
      Utils.swap(map, a, b);
    });
    Index.Builder recordIndex = getRecordIndex().newBuilder();
    for (IntObjectCursor<Object> i : map) {
      recordIndex.set(i.value, i.key);
    }

    return builder.build()
        .setRecordIndex(recordIndex.build())
        .setColumnIndex(getColumnIndex().copy());
  }

  @Override
  public DataFrame head(int rows) {
    if (rows >= rows()) {
      return this;
    }
    DataFrame.Builder builder = newBuilder();
    Index.Builder recordIndex = getRecordIndex().newBuilder();

    boolean first = true;
    for (int column = 0; column < columns(); column++) {
      builder.addColumnBuilder(getType(column).newBuilder(rows));
      Iterator<Index.Entry> records = getRecordIndex().iterator();
      for (int i = 0; i < rows && records.hasNext(); i++) {
        Index.Entry entry = records.next();
        if (first) {
          recordIndex.add(entry.key());
        }
        builder.set(i, column, this, entry.index(), column);
      }
      first = false;
    }

    return builder.build()
        .setColumnIndex(getColumnIndex())
        .setRecordIndex(recordIndex.build());
  }

  @Override
  public DataFrame indexOn(int col) {
    DataFrame.Builder builder = newCopyBuilder();
    Index.Builder columnIndex = getColumnIndex().newBuilder();
    builder.removeColumn(col);
    for (Index.Entry entry : getColumnIndex()) {
      if (entry.index() != col) {
        columnIndex.set(entry.key(), entry.index() > col ? entry.index() - 1 : entry.index());
      }
    }

    return builder.build()
        .setRecordIndex(HashIndex.from(getColumn(col)))
        .setColumnIndex(columnIndex.build());
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
    for (Index.Entry entry : df.getColumnIndex()) {
      int newIndex;
      int eIdx = entry.index();
      if (eIdx > index) {
        newIndex = eIdx + 1;
      } else {
        newIndex = eIdx;
      }
      columnIndex.set(entry.key(), newIndex);
    }
    return df.setColumnIndex(columnIndex.build()).setRecordIndex(getRecordIndex());
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
            return getColumn(current++);
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
  public Vector getColumn(int index) {
    return new DataFrameColumnView(this, index);
  }

  @Override
  public Vector getColumn(Object key) {
    return getColumn(getColumnIndex().get(key));
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
  public DataFrame removeColumn(int index) {
    return newCopyBuilder().removeColumn(index).build();
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
  public DataFrame removeColumns(Iterable<Integer> indexes) {
    Set<Integer> hash;
    if (!(indexes instanceof Set)) {
      hash = Sets.newHashSet(indexes);
    } else {
      hash = (Set<Integer>) indexes;
    }
    Builder builder = newBuilder();
    for (int i = 0; i < columns(); i++) {
      if (!hash.contains(i)) {
        builder.addColumn(getColumn(i));
      } else {
//        builder.getColumnNames().remove(i);
      }
    }

    return builder.build();
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
  public DataFrame takeColumns(Iterable<Integer> indexes) {
    Builder builder = newBuilder();
    for (int j : indexes) {
      for (int i = 0; i < rows(); i++) {
        builder.set(i, j, this, i, j);
      }
    }
    return builder.build();
  }

  @Override
  public Collection<Record> getRecords() {
    return new AbstractCollection<Record>() {
      @Override
      public Iterator<Record> iterator() {
        return new UnmodifiableIterator<Record>() {
          public int current;

          @Override
          public boolean hasNext() {
            return current < size();
          }

          @Override
          public Record next() {
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
  public Record getRecord(int index) {
    return new RecordView(this, index);
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
  public DataFrame insertRecord(int index, Vector record) {
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
      Check.columnSize(this, dataFrame);
      builder.stack(dataFrame);
    }
    return builder.build();
  }

  @Override
  public DataFrame concat(Iterable<DataFrame> dataFrames) {
    DataFrame.Builder builder = newCopyBuilder();
    for (DataFrame dataFrame : dataFrames) {
      Check.columnSize(this, dataFrame);
      builder.concat(dataFrame);
    }
    return builder.build();
  }

  @Override
  public DataFrame copy() {
    return newCopyBuilder().build()
        .setColumnIndex(getColumnIndex().copy())
        .setRecordIndex(getRecordIndex().copy());
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
  public final DataFrame setRecordIndex(Index index) {
    Preconditions.checkNotNull(index);
    Check.size(rows(), index.size());
    this.recordIndex = index;
    return this;
  }

  @Override
  public final DataFrame setColumnIndex(Index index) {
    Preconditions.checkNotNull(index);
    Check.size(columns(), index.size());
    this.columnIndex = index;
    return this;
  }

  /**
   * Converts the DataFrame to an {@link org.briljantframework.matrix.DoubleMatrix}. This
   * implementation rely on {@link #getAsDouble(int, int)}. Sub-classes are allowed to return any
   * concrete implementation of {@link org.briljantframework.matrix.DoubleMatrix}.
   *
   * @return a new matrix
   */
  @Override
  public Matrix toMatrix() {
    DoubleMatrix matrix = Bj.doubleMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, getAsDouble(i, j));
      }
    }

    return matrix;
  }

  @Override
  public Collection<VectorType> getTypes() {
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
  public Iterator<Record> iterator() {
    return getRecords().iterator();
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

    @Override
    public Builder swapRecords(int a, int b) {
      for (int i = 0; i < columns(); i++) {
        swapInColumn(i, a, b);
      }
      return this;
    }
  }

}
