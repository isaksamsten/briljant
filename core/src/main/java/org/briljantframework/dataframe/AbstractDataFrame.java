package org.briljantframework.dataframe;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.*;

import org.briljantframework.matrix.DefaultDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Implements some default behaviour for DataFrames
 * 
 * @author Isak Karlsson
 */
public abstract class AbstractDataFrame implements DataFrame {

  /**
   * The column names. Subclasses should preserve the name, e.g., in {@link #newBuilder()}
   */
  protected final NameAttribute columnNames;

  /**
   * The row names. Subclasses should preserve the name, e.g., in {@link #newBuilder()}
   */
  protected final NameAttribute rowNames;

  private final AbstractCollection<Record> rowCollection = new AbstractCollection<Record>() {
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
  private final AbstractCollection<Vector> columnCollection = new AbstractCollection<Vector>() {
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

  protected AbstractDataFrame(NameAttribute columnNames, NameAttribute rowNames) {
    this.columnNames = columnNames;
    this.rowNames = rowNames;
  }

  protected AbstractDataFrame(NameAttribute columnNames, NameAttribute rowNames, boolean copy) {
    if (copy) {
      this.columnNames = new NameAttribute(columnNames);
      this.rowNames = new NameAttribute(rowNames);
    } else {
      this.columnNames = columnNames;
      this.rowNames = rowNames;
    }
  }

  protected AbstractDataFrame() {
    this(new NameAttribute(), new NameAttribute());
  }

  @Override
  public NameAttribute getColumnNames() {
    return columnNames;
  }

  @Override
  public Collection<Vector> getColumns() {
    return columnCollection;
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
  public Vector getColumn(String name) {
    return getColumn(columnNames.getOrThrow(name, IllegalArgumentException::new));
  }

  /**
   * Constructs a new DataFrame by dropping {@code index}.
   * 
   * This implementations rely on {@link #newCopyBuilder()} returning a builder and that
   * {@link org.briljantframework.dataframe.DataFrame.Builder#removeColumn(int)}.
   * 
   * @param index the index
   * @return a new data frame as created by {@link #newCopyBuilder()}
   */
  @Override
  public DataFrame dropColumn(int index) {
    return newCopyBuilder().removeColumn(index).build();
  }

  /**
   * Constructs a new DataFrame by dropping the columns in {@code indexes}.
   * 
   * This implementations rely on {@link #newBuilder()} returning a builder and that
   * {@link org.briljantframework.dataframe.DataFrame.Builder#addColumn(org.briljantframework.vector.Vector)}
   * adds a vector.
   * 
   * @param indexes collection of indexes
   * @return a new data frame as created by {@link #newBuilder()}
   */
  @Override
  public DataFrame dropColumns(Iterable<Integer> indexes) {
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
        builder.getColumnNames().remove(i);
      }
    }

    return builder.build();
  }

  /**
   * Constructs a new DataFrame by including the rows in {@code indexes}.
   * 
   * This implementation rely on {@link #newBuilder()} and
   * {@link Builder#set(int, int, DataFrame, int, int)}.
   * 
   * @param indexes collection of indexes
   * @return a new data frame as created by {@link #newBuilder()}
   */
  @Override
  public DataFrame takeColumns(Iterable<Integer> indexes) {
    Builder builder = newBuilder();
    for (int i : indexes) {
      for (int j = 0; j < columns(); j++) {
        builder.set(i, j, this, i, j);
      }
    }
    return builder.build();
  }

  @Override
  public String getColumnName(int index) {
    checkArgument(index >= 0 && index < columns());
    return columnNames.getOrDefault(index, () -> String.valueOf(index));
  }

  @Override
  public DataFrame setColumnName(int index, String columnName) {
    checkArgument(index >= 0 && index < columns());
    columnNames.put(index, columnName);
    return this;
  }

  @Override
  public DataFrame setColumnNames(List<String> names) {
    for (int i = 0; i < names.size(); i++) {
      setColumnName(i, names.get(i));
    }
    return this;
  }

  @Override
  public String getRecordName(int index) {
    checkArgument(index >= 0 && index < rows());
    return rowNames.getOrDefault(index, () -> String.valueOf(index));
  }

  @Override
  public DataFrame setRecordName(int index, String rowName) {
    checkArgument(index >= 0 && index < rows());
    rowNames.put(index, rowName);
    return this;
  }

  @Override
  public DataFrame setRecordNames(List<String> names) {
    for (int i = 0; i < names.size(); i++) {
      setRecordName(i, names.get(i));
    }
    return this;
  }

  @Override
  public VectorType getRecordType(int index) {
    return getRecord(index).getType();
  }

  @Override
  public Collection<Record> getRecords() {
    return rowCollection;
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
   * This implementation rely on {@link #newBuilder()} and
   * {@link Builder#set(int, int, DataFrame, int, int)}.
   * 
   * @param indexes the indexes to take
   * @return a new data frame as created by {@link #newBuilder()}
   */
  @Override
  public DataFrame takeRecords(Iterable<Integer> indexes) {
    Builder builder = newBuilder();
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
   * This implementation rely on {@link #newBuilder()} and
   * {@link Builder#set(int, int, DataFrame, int, int)}
   * 
   * @param indexes the indexes to drop
   * @return a new DataFrame as created by {@link #newBuilder()}
   */
  @Override
  public DataFrame dropRecords(Iterable<Integer> indexes) {
    Set<Integer> set;
    if (!(indexes instanceof Set)) {
      set = Sets.newHashSet(indexes);
    } else {
      set = (Set<Integer>) indexes;
    }
    Builder builder = newBuilder();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        if (!set.contains(i)) {
          builder.set(i, j, this, i, j);
        }
      }
    }

    return builder.build();
  }

  /**
   * Converts the DataFrame to an {@link org.briljantframework.matrix.DoubleMatrix}. This
   * implementation rely on {@link #getAsDouble(int, int)} and returns an
   * {@link org.briljantframework.matrix.DefaultDoubleMatrix}. Sub-classes are allowed to return any
   * concrete implementation of {@link org.briljantframework.matrix.DoubleMatrix}.
   * 
   * @return a new matrix
   */
  @Override
  public DoubleMatrix asMatrix() {
    DoubleMatrix matrix = new DefaultDoubleMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, getAsDouble(i, j));
      }
    }

    return matrix;
  }

  @Override
  public Collection<VectorType> getColumnTypes() {
    List<VectorType> types = new ArrayList<>();
    for (int i = 0; i < columns(); i++) {
      types.add(getColumnType(i));
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
    return new UnmodifiableIterator<Record>() {
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < rows();
      }

      @Override
      public Record next() {
        return getRecord(index++);
      }
    };
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

    protected final NameAttribute columnNames;
    protected final NameAttribute rowNames;

    /**
     * Construct a new Abstract builder with {@link org.briljantframework.dataframe.NameAttribute}s.
     *
     * The attribute containers are copied on construction.
     *
     * @param columnNames the column names
     * @param rowNames the row names
     */
    protected AbstractBuilder(NameAttribute columnNames, NameAttribute rowNames) {
      this.columnNames = new NameAttribute(columnNames);
      this.rowNames = new NameAttribute(rowNames);
    }

    public AbstractBuilder() {
      this.columnNames = new NameAttribute();
      this.rowNames = new NameAttribute();
    }

    @Override
    public NameAttribute getColumnNames() {
      return columnNames;
    }

    @Override
    public NameAttribute getRecordNames() {
      return rowNames;
    }

    @Override
    public DataFrame.Builder addColumn(Vector.Builder builder) {
      Vector vector = builder.build();
      return addColumn(vector);
    }

    @Override
    public Builder addColumn(Vector vector) {
      return setColumn(columns(), vector);
    }

    @Override
    public Builder setColumn(int index, Vector.Builder builder) {
      return setColumn(index, builder.build());
    }

    @Override
    public Builder setColumn(int index, Vector vector) {
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
      return setRecord(rows(), vector);
    }

    @Override
    public Builder setRecord(int index, Vector.Builder builder) {
      return setRecord(index, builder.build());
    }

    @Override
    public Builder setRecord(int index, Vector vector) {
      final int columns = columns();
      final int size = vector.size();
      for (int j = 0; j < columns; j++) {
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
      rowNames.swap(a, b);
      return this;
    }
  }

}
