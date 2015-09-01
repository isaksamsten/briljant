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

package org.briljantframework.data.dataframe;

import org.briljantframework.index.DataFrameLocationSetter;
import org.briljantframework.index.Index;
import org.briljantframework.index.VectorLocationGetter;
import org.briljantframework.data.vector.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

/**
 * @author Isak Karlsson
 */
class HashDataFrameGroupBy implements DataFrameGroupBy {

  /**
   * If no key should be dropped dropKey is assigned this identity.
   *
   * This convention is required since null-keys are allowed
   */
  private static final Object NO_DROP_KEY_IDENTITY = new Object();

  private final HashMap<Object, Vector> groups;
  private final DataFrame dataFrame;
  private final Object dropKey;

  HashDataFrameGroupBy(DataFrame dataFrame, HashMap<Object, ? extends Vector.Builder> groups) {
    this(dataFrame, groups, NO_DROP_KEY_IDENTITY);
  }

  HashDataFrameGroupBy(
      DataFrame dataFrame,
      HashMap<Object, ? extends Vector.Builder> groups,
      Object key) {
    this.dataFrame = dataFrame;
    this.groups = new HashMap<>();
    for (Map.Entry<Object, ? extends Vector.Builder> e : groups.entrySet()) {
      this.groups.put(e.getKey(), e.getValue().build());
    }
    this.dropKey = key;
  }

  @Override
  public Iterator<Group> iterator() {
    return new Iterator<Group>() {
      private final Iterator<Map.Entry<Object, Vector>> it = groups().iterator();

      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public Group next() {
        Map.Entry<Object, Vector> e = it.next();
        Object key = e.getKey();
        Vector indices = e.getValue();
        return new Group(key, createDataFrame(indices));
      }
    };
  }

  @Override
  public Set<Map.Entry<Object, Vector>> groups() {
    return groups.entrySet();
  }

  @Override
  public DataFrame get(Object key) {
    Vector indices = groups.get(key);
    if (indices == null) {
      throw new IllegalArgumentException(String.format("Missing key: %s", key));
    }
    return createDataFrame(indices);
  }

  protected DataFrame createDataFrame(Vector indices) {
    VectorLocationGetter index = indices.loc();
    final int size = indices.size();

    DataFrame.Builder builder = dataFrame.newBuilder();
    DataFrameLocationSetter locationSetter = builder.loc();
    if (indices.size() > 0) {
      for (int j = 0, columns = dataFrame.columns(); j < columns; j++) {
        builder.add(dataFrame.loc().get(j).getType());
        for (int i = 0; i < size; i++) {
          locationSetter.set(i, j, dataFrame, index.getAsInt(i), j);
        }
      }
    }
    Index.Builder recordIndex = dataFrame.getRecordIndex().newBuilder();
    for (int i = 0; i < size; i++) {
      recordIndex.add(dataFrame.getRecordIndex().getKey(index.getAsInt(i)));
    }

    DataFrame df = builder.build();
    df.setColumnIndex(dataFrame.getColumnIndex());
    df.setRecordIndex(recordIndex.build());
    return df;
  }

  @Override
  public DataFrame collect(Function<Vector, Object> function) {
    DataFrame.Builder builder = dataFrame.newBuilder();
    for (Map.Entry<Object, Vector> group : groups()) {
      VectorLocationGetter index = group.getValue().loc();
      for (Object columnKey : dataFrame.getColumnIndex().keySet()) {
        if (dropColumnKey(columnKey)) {
          continue; // do not include the key used for grouping
        }
        Vector column = dataFrame.get(columnKey);
        Vector.Builder groupVector = column.newBuilder();
        for (int i = 0, size = group.getValue().size(); i < size; i++) {
          groupVector.loc().set(i, column, index.getAsInt(i));
        }
        builder.set(group.getKey(), columnKey, function.apply(groupVector.build()));
      }
    }
    return builder.build();
  }

  protected boolean dropColumnKey(Object columnKey) {
    return dropKey != NO_DROP_KEY_IDENTITY &&
           (columnKey == dropKey || // columnKey is null and dropKey is null
            (columnKey != null && columnKey.equals(dropKey))); // columnKey is not null
  }

  @Override
  public <T, C> DataFrame collect(Class<? extends T> cls,
                                  Collector<? super T, C, ? extends T> collector) {
    DataFrame.Builder builder = dataFrame.newBuilder();
    for (Map.Entry<Object, Vector> group : groups.entrySet()) {
      Object groupKey = group.getKey();
      Vector index = group.getValue();
      VectorLocationGetter indexLocation = index.loc();

      for (Object columnKey : dataFrame.getColumnIndex().keySet()) {
        Vector column = dataFrame.get(columnKey);
        if (dropColumnKey(columnKey) || !cls.isAssignableFrom(column.getType().getDataClass())) {
          continue;
        }
        VectorLocationGetter columnLocation = column.loc();
        C accumulator = collector.supplier().get();
        for (int i = 0, size = index.size(); i < size; i++) {
          T value = columnLocation.get(cls, indexLocation.getAsInt(i));
          collector.accumulator().accept(accumulator, value);
        }
        builder.set(groupKey, columnKey, collector.finisher().apply(accumulator));
      }
    }
    return builder.build();
  }

  @Override
  public DataFrame apply(UnaryOperator<Vector> op) {
    DataFrame.Builder builder = dataFrame.newBuilder();
    Index.Builder recordIndex = dataFrame.getRecordIndex().newBuilder();
    int row = 0;
    for (Vector index : groups.values()) {
      for (int j = 0, columns = dataFrame.columns(); j < columns; j++) {
        Vector column = dataFrame.loc().get(j);
        Vector.Builder columnBuilder = column.newBuilder();
        for (int i = 0, size = index.size(); i < size; i++) {
          columnBuilder.loc().set(i, column, index.loc().getAsInt(i));
        }
        Vector transformed = op.apply(columnBuilder.build());
        for (int i = row, from = 0, size = transformed.size(); from < size; i++, from++) {
          builder.loc().set(i, j, transformed, from);
        }
      }

      for (int i = 0, size = index.size(); i < size; i++) {
        recordIndex.add(dataFrame.getRecordIndex().getKey(index.loc().getAsInt(i)));
      }
      row += index.size();
    }

    DataFrame df = builder.build();
    df.setColumnIndex(dataFrame.getColumnIndex());
    df.setRecordIndex(recordIndex.build());
    return df;
  }
}
