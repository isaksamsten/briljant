package org.briljantframework.dataframe;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.*;

import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.Type;
import org.briljantframework.vector.Vector;

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

  private final AbstractCollection<Vector> rowCollection = new AbstractCollection<Vector>() {
    @Override
    public Iterator<Vector> iterator() {
      return iterator();
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
   * This implementations rely on {@link #newCopyBuilder()} returning a builder and that
   * {@link org.briljantframework.dataframe.DataFrame.Builder#removeColumn(int)}.
   * 
   * @param indexes collection of indexes
   * @return a new data frame as created by {@link #newCopyBuilder()}
   */
  @Override
  public DataFrame dropColumns(Collection<Integer> indexes) {
    if (!(indexes instanceof Set)) {
      indexes = new HashSet<>(indexes);
    }

    Builder builder = newCopyBuilder();
    for (int i = 0; i < builder.columns(); i++) {
      if (indexes.contains(i)) {
        builder.removeColumn(i);
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
  public DataFrame takeColumns(Collection<Integer> indexes) {
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
  public String getRowName(int index) {
    checkArgument(index >= 0 && index < rows());
    return rowNames.getOrDefault(index, () -> String.valueOf(index));
  }

  @Override
  public DataFrame setRowName(int index, String rowName) {
    checkArgument(index >= 0 && index < rows());
    rowNames.put(index, rowName);
    return this;
  }

  @Override
  public DataFrame setRowNames(List<String> names) {
    for (int i = 0; i < names.size(); i++) {
      setRowName(i, names.get(i));
    }
    return this;
  }

  @Override
  public Type getRowType(int index) {
    return getRow(index).getType();
  }

  @Override
  public Collection<Vector> getRows() {
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
  public DataFrameRow getRow(int index) {
    return new DataFrameRowView(this, index);
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
  public DataFrame takeRows(Collection<Integer> indexes) {
    Builder builder = newBuilder();
    for (int i : indexes) {
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
  public DataFrame dropRows(Collection<Integer> indexes) {
    if (!(indexes instanceof Set)) {
      indexes = new HashSet<>(indexes);
    }

    Builder builder = newBuilder();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        if (!indexes.contains(i)) {
          builder.set(i, j, this, i, j);
        }
      }
    }

    return builder.build();
  }

  /**
   * Converts the DataFrame to an {@link Matrix}. This implementation rely on
   * {@link #getAsDouble(int, int)} and returns an {@link org.briljantframework.matrix.ArrayMatrix}.
   * Sub-classes are allowed to return any concrete implementation of {@link Matrix}.
   * 
   * @return a new matrix
   */
  @Override
  public Matrix asMatrix() {
    Matrix matrix = new ArrayMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, getAsDouble(i, j));
      }
    }

    return matrix;
  }

  /**
   * Returns an iterator over the rows of this DataFrame
   * 
   * @return a row iterator
   */
  @Override
  public Iterator<DataFrameRow> iterator() {
    return new UnmodifiableIterator<DataFrameRow>() {
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < rows();
      }

      @Override
      public DataFrameRow next() {
        return getRow(index++);
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

    protected AbstractBuilder(NameAttribute columnNames, NameAttribute rowNames) {
      this.columnNames = new NameAttribute(columnNames);
      this.rowNames = new NameAttribute(rowNames);
    }

    public AbstractBuilder() {
      this.columnNames = new NameAttribute();
      this.rowNames = new NameAttribute();
    }

    @Override
    public DataFrame.Builder addColumn(Vector.Builder builder) {
      Vector vector = builder.build();
      int j = columns();
      for (int i = 0; i < vector.size(); i++) {
        set(i, j, vector, i);
      }
      return this;
    }

    @Override
    public Builder swapRows(int a, int b) {
      for (int i = 0; i < columns(); i++) {
        swapInColumn(i, a, b);
      }
      rowNames.swap(a, b);
      return this;
    }
  }

}
