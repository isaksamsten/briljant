package org.briljantframework.dataframe.join;

import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.Index;
import org.briljantframework.vector.Vector;

import java.util.Collection;
import java.util.Iterator;

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
   * Combines two data frames using this joiner.
   *
   * @param a the first data frame. Uses the indexes from {@link #getLeftIndex(int)}
   * @param b the second data frame. Uses the indexes from {@link #getRightIndex(int)}
   * @return a new DataFrame
   */
  public DataFrame join(DataFrame a, DataFrame b, Collection<Integer> on) {
    int size = this.size();
    int indexSize = on.size();

    DataFrame.Builder builder = a.newBuilder();
    Index.Builder columnIndexer = a.getColumnIndex().newBuilder();
    ObjectIntMap<Object> indexColumn = new ObjectIntOpenHashMap<>(on.size());
    Iterator<Index.Entry> aIt = a.getColumnIndex().entrySet().iterator();
    Iterator<Index.Entry> bIt = b.getColumnIndex().entrySet().iterator();
    int currentColumnIndex = 0;
    while (currentColumnIndex < indexSize && (aIt.hasNext() || bIt.hasNext())) {
      Index.Entry entry;
      if (aIt.hasNext()) {
        entry = aIt.next();
      } else {
        entry = bIt.next();
      }

      Object key = entry.key();
      if (on.contains(entry.index())) {
        builder.addColumnBuilder(a.getType(entry.index()).newBuilder(size));
        indexColumn.put(key, currentColumnIndex);
        columnIndexer.add(key);
        currentColumnIndex += 1;
      }
    }

    int columnIndex = on.size();
    for (Index.Entry entry : a.getColumnIndex().entrySet()) {
      int index = entry.index();
      Vector sourceColumn = a.get(index);
      Object key = entry.key();
      if (on.contains(entry.index())) {
        int targetColumn = indexColumn.get(key);
        appendColumnFromLeftIndexIgnoreNA(size, builder, targetColumn, sourceColumn);
      } else {
        columnIndexer.add(key);
        builder.addColumnBuilder(a.getType(index).newBuilder(size));
        appendColumnFromLeftIndexIgnoreNA(size, builder, columnIndex, sourceColumn);
        columnIndex++;
      }
    }

    for (Index.Entry entry : b.getColumnIndex().entrySet()) {
      int index = entry.index();
      Vector sourceColumn = b.get(index);
      Object key = entry.key();
      if (on.contains(entry.index())) {
        int targetColumn = indexColumn.get(key);
        appendColumnFromRightIndexIgnoreNA(size, builder, targetColumn, sourceColumn);
      } else {
        Object newKey = key;
        if (columnIndexer.contains(key)) {
          newKey = key.toString() + " (right)";
        }
        columnIndexer.add(newKey);
        builder.addColumnBuilder(b.getType(index).newBuilder(size));
        appendColumnFromRightIndexIgnoreNA(size, builder, columnIndex, sourceColumn);
        columnIndex++;
      }
    }
    return builder.build().setColumnIndex(columnIndexer.build());
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
