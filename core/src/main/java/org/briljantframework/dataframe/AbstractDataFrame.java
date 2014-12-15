package org.briljantframework.dataframe;

import java.util.Iterator;
import java.util.Set;

import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Matrix;

import com.google.common.collect.UnmodifiableIterator;

/**
 * 
 * 
 * @author Isak Karlsson
 */
public abstract class AbstractDataFrame implements DataFrame {

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
  public DataFrame dropColumns(Set<Integer> indexes) {
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
  public DataFrame takeColumns(Set<Integer> indexes) {
    Builder builder = newBuilder();
    for (int i : indexes) {
      for (int j = 0; j < columns(); j++) {
        builder.set(i, j, this, i, j);
      }
    }
    return builder.build();
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
  public DataFrame takeRows(Set<Integer> indexes) {
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
  public DataFrame dropRows(Set<Integer> indexes) {
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
   * {@link #getAsDouble(int, int)} and returns a {@link org.briljantframework.matrix.ArrayMatrix}.
   * Sub-classes are of course allowed to return any concrete implementation of {@link Matrix}.
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

  @Override
  public String toString() {
    return DataFrames.toTabularString(this);
  }
}
