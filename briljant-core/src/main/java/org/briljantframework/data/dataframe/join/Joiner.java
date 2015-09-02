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

package org.briljantframework.data.dataframe.join;

import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.index.DataFrameLocationSetter;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.vector.Vector;

import java.util.Collection;
import java.util.Iterator;

/**
 * A joiner keeps track of the indexes that will be joined.
 * For example, given a joiner of the left index {@code [1, 2, 3]} the right indexes and {@code
 * [2, 2, 2]}, {@link #join(org.briljantframework.data.dataframe.DataFrame,
 * org.briljantframework.data.dataframe.DataFrame, java.util.Collection)} produces a data frame with the
 * rows {@code 1, 2, 3}
 * from {@code a} concatenated with {@code 2, 2, 2} from {@code b}.
 *
 * <p>Joiners are often created using a {@link org.briljantframework.data.dataframe.join.JoinOperation}.
 *
 * @author Isak Karlsson
 */
public abstract class Joiner {

//  // Joined on the
//  public DataFrame join(DataFrame a, DataFrame b) {
//
//
//
//  }

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

      Object key = entry.getKey();
      if (on.contains(entry.getValue())) {
        builder.add(a.loc().get(entry.getValue()).newBuilder(size));
        indexColumn.put(key, currentColumnIndex);
        columnIndexer.add(key);
        currentColumnIndex += 1;
      }
    }

    int columnIndex = on.size();
    for (Index.Entry entry : a.getColumnIndex().entrySet()) {
      int index = entry.getValue();
      Vector sourceColumn = a.loc().get(index);
      Object key = entry.getKey();
      if (on.contains(entry.getValue())) {
        int targetColumn = indexColumn.get(key);
        appendColumnFromLeftIndexIgnoreNA(size, builder, targetColumn, sourceColumn);
      } else {
        columnIndexer.add(key);
        builder.add(a.loc().get(index).newBuilder(size));
        appendColumnFromLeftIndexIgnoreNA(size, builder, columnIndex, sourceColumn);
        columnIndex++;
      }
    }

    for (Index.Entry entry : b.getColumnIndex().entrySet()) {
      int index = entry.getValue();
      Vector sourceColumn = b.loc().get(index);
      Object key = entry.getKey();
      if (on.contains(entry.getValue())) {
        int targetColumn = indexColumn.get(key);
        appendColumnFromRightIndexIgnoreNA(size, builder, targetColumn, sourceColumn);
      } else {
        Object newKey = key;
        if (columnIndexer.contains(key)) {
          newKey = key.toString() + " (right)";
        }
        columnIndexer.add(newKey);
        builder.add(b.loc().get(index).newBuilder(size));
        appendColumnFromRightIndexIgnoreNA(size, builder, columnIndex, sourceColumn);
        columnIndex++;
      }
    }
    DataFrame df = builder.build();
    df.setColumnIndex(columnIndexer.build());
    return df;
  }

  private void appendColumnFromLeftIndexIgnoreNA(int size, DataFrame.Builder builder,
                                                 int targetColumn, Vector source) {
    DataFrameLocationSetter loc = builder.loc();
    for (int i = 0; i < size; i++) {
      int row = getLeftIndex(i);
      if (row >= 0) {
        loc.set(i, targetColumn, source, row);
      }
    }
  }

  private void appendColumnFromRightIndexIgnoreNA(int size, DataFrame.Builder builder,
                                                  int targetColumn, Vector source) {
    DataFrameLocationSetter loc = builder.loc();
    for (int i = 0; i < size; i++) {
      int row = getRightIndex(i);
      if (row >= 0) {
        loc.set(i, targetColumn, source, row);
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
