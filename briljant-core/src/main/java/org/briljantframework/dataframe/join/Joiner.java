package org.briljantframework.dataframe.join;

import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.NameAttribute;
import org.briljantframework.vector.Vector;

import java.util.Collection;

/**
 * A joiner keeps track of the indexes that will be joined.
 * For example, given a joiner of the left index {@code [1, 2, 3]} the right indexes and {@code
 * [2, 2, 2]}, {@link #join(org.briljantframework.dataframe.DataFrame,
 * org.briljantframework.dataframe.DataFrame, java.util.Collection)} produces a data frame with the
 * rows {@code 1, 2, 3}
 * from {@code a} concatenated with {@code 2, 2, 2} from {@code b}.
 *
 * <p>Joiners are often created using a {@link org.briljantframework.dataframe.join.JoinOperation}.
 *
 * @author Isak Karlsson
 */
public abstract class Joiner {

  /**
   * Combines two dataframes using this joiner.
   *
   * @param a the first data frame. Uses the indexes from {@link #getLeftIndex(int)}
   * @param b the second data frame. Uses the indexes from {@link #getRightIndex(int)}
   * @return a new DataFrame
   */
  public DataFrame.Builder join(DataFrame a, DataFrame b, Collection<String> on) {
    int size = this.size();
    int aCol = a.columns();
    int bCol = b.columns();
    int indexSize = on.size();

    DataFrame.Builder builder = a.newBuilder();
    NameAttribute aColumnNames = a.getColumnNames();
    NameAttribute bColumnNames = b.getColumnNames();
    NameAttribute columnNames = builder.getColumnNames();

    ObjectIntMap<String> indexColumn = new ObjectIntOpenHashMap<>(on.size());

    int m = Math.max(aCol, bCol);
    int index = 0;
    for (int i = 0; i < m && index < indexSize; i++) {
      if (m < aCol && m < bCol) {
        String aColumnName = aColumnNames.get(i);
        String bColumnName = bColumnNames.get(i);
        if ((aColumnName != null && on.contains(aColumnName)) ||
            (bColumnName != null && on.contains(bColumnName))) {
          indexColumn.put(aColumnName, index);
          builder.addColumnBuilder(a.getColumnType(index).newBuilder(size));
          index += 1;
        }
      } else if (m < aCol) {
        String aColumnName = aColumnNames.get(i);
        if (aColumnName != null && on.contains(aColumnName)) {
          indexColumn.put(aColumnName, index);
          builder.addColumnBuilder(a.getColumnType(index).newBuilder(size));
          index += 1;
        }
      } else {
        String bColumnName = bColumnNames.get(i);
        if (bColumnName != null && on.contains(bColumnName)) {
          indexColumn.put(bColumnName, index);
          builder.addColumnBuilder(a.getColumnType(index).newBuilder(size));
          index += 1;
        }
      }
    }

    index = 0;
    for (int j = 0; j < aCol; j++) {
      String columnName = aColumnNames.get(j);
      if (columnName != null) {
        columnNames.put(j, columnName);
      }

      Vector sourceColumn = a.getColumn(j);
      if (columnName != null && on.contains(columnName)) {
        int targetColumn = indexColumn.get(columnName);
        appendColumnFromLeftIndexIgnoreNA(size, builder, targetColumn, sourceColumn);
      } else {
        builder.addColumnBuilder(a.getColumnType(j).newBuilder(size));
        appendColumnFromLeftIndexIgnoreNA(size, builder, j, sourceColumn);
      }
    }

    int columnIndex = aCol;
    for (int j = 0; j < bCol; j++) {
      String columnName = bColumnNames.get(j);
      Vector sourceColumn = b.getColumn(j);
      if (columnName != null && on.contains(columnName)) {
        int targetColumn = indexColumn.get(columnName);
        appendColumnFromRightIndexIgnoreNA(size, builder, targetColumn, sourceColumn);
      } else {
        if (columnName != null) {
          columnNames.put(columnIndex, columnName);
        }
        builder.addColumnBuilder(b.getColumnType(j).newBuilder(size));
        appendColumnFromRightIndexIgnoreNA(size, builder, columnIndex, sourceColumn);
        columnIndex += 1;
      }
    }
    return builder;
  }

  private void appendColumnFromLeftIndex(int size, DataFrame.Builder builder, int targetColumn,
                                         Vector source) {
    for (int i = 0; i < size; i++) {
      int row = this.getLeftIndex(i);
      if (row < 0) {
        builder.setNA(i, targetColumn);
      } else {
        builder.set(i, targetColumn, source, row);
      }
    }
  }

  private void appendColumnFromLeftIndexIgnoreNA(int size, DataFrame.Builder builder,
                                                 int targetColumn, Vector source) {
    for (int i = 0; i < size; i++) {
      int row = this.getLeftIndex(i);
      if (row >= 0) {
        builder.set(i, targetColumn, source, row);
      }
    }
  }

  private void appendColumnFromRightIndex(int size, DataFrame.Builder builder, int targetColumn,
                                          Vector source) {
    for (int i = 0; i < size; i++) {
      int row = this.getRightIndex(i);
      if (row < 0) {
        builder.setNA(i, targetColumn);
      } else {
        builder.set(i, targetColumn, source, row);
      }
    }
  }

  private void appendColumnFromRightIndexIgnoreNA(int size, DataFrame.Builder builder,
                                                  int targetColumn, Vector source) {
    for (int i = 0; i < size; i++) {
      int row = this.getRightIndex(i);
      if (row >= 0) {
        builder.set(i, targetColumn, source, row);
      }
    }
  }

  /**
   * Returns the size of the joiner.
   *
   * @return the size
   */
  public abstract int size();

  /**
   * Get the index for the left side of a join. Returns {@code -1}, if the index should not be
   * included.
   *
   * @param i the index {@code 0 ... size()}
   * @return the index in the resulting container
   */
  public abstract int getLeftIndex(int i);

  /**
   * Get the index for the left side of a join. Returns {@code -1}, if the index should not be
   * included.
   *
   * @param i the index {@code 0 ... size()}
   * @return the index in the resulting container
   */
  public abstract int getRightIndex(int i);

}
