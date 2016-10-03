/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.dataframe;

import static org.briljantframework.array.Arrays.unmodifiableArray;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

import org.briljantframework.Check;
import org.briljantframework.array.IntArray;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.series.Series;
import org.briljantframework.data.series.LocationGetter;
import org.briljantframework.util.primitive.IntList;

/**
 * Grouped data frame index by a HashMap.
 * 
 * @author Isak Karlsson
 */
class HashDataFrameGroupBy implements DataFrameGroupBy {

  /**
   * If no key should be dropped dropKey is assigned this identity.
   *
   * This convention is required since null-keys are allowed
   */
  private static final Object NO_DROP_KEY_IDENTITY = new Object();

  private final HashMap<Object, IntArray> groups;
  private final DataFrame dataFrame;
  private final Object[] dropKeys;

  HashDataFrameGroupBy(DataFrame dataFrame, HashMap<Object, IntList> groups) {
    this(dataFrame, groups, new Object[0]);
  }

  HashDataFrameGroupBy(DataFrame dataFrame, HashMap<Object, IntList> groups, Object... keys) {
    this.dataFrame = dataFrame;
    this.groups = new HashMap<>();
    for (Map.Entry<Object, IntList> e : groups.entrySet()) {
      this.groups.put(e.getKey(), e.getValue().toIntArray());
    }
    this.dropKeys = keys;
  }

  @Override
  public Iterator<Group> iterator() {
    return new Iterator<Group>() {
      private final Iterator<Map.Entry<Object, IntArray>> it = groups().iterator();

      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public Group next() {
        Map.Entry<Object, IntArray> e = it.next();
        return new Group(e.getKey(), createDataFrame(e.getValue()));
      }
    };
  }

  @Override
  public Set<Map.Entry<Object, IntArray>> groups() {
    return new AbstractSet<Map.Entry<Object, IntArray>>() {
      @Override
      public Iterator<Map.Entry<Object, IntArray>> iterator() {
        return new Iterator<Map.Entry<Object, IntArray>>() {
          Iterator<Map.Entry<Object, IntArray>> it = groups.entrySet().iterator();

          @Override
          public boolean hasNext() {
            return it.hasNext();
          }

          @Override
          public Map.Entry<Object, IntArray> next() {
            Map.Entry<Object, IntArray> entry = it.next();
            return new AbstractMap.SimpleEntry<>(entry.getKey(),
                unmodifiableArray(entry.getValue()));
          }
        };
      }

      @Override
      public int size() {
        return groups.size();
      }
    };
  }

  @Override
  public DataFrame get(Object key) {
    IntArray indices = groups.get(key);
    if (indices == null) {
      throw new IllegalArgumentException(String.format("Missing key: %s", key));
    }
    return createDataFrame(indices);
  }

  @Override
  public DataFrame collect(Function<Series, Object> function) {
    DataFrame.Builder builder = dataFrame.newEmptyBuilder();
    for (Map.Entry<Object, IntArray> group : groups()) {
      IntArray index = group.getValue();
      for (Object columnKey : dataFrame.getColumnIndex().keySet()) {
        if (dropColumnKey(columnKey)) {
          continue; // do not include the key used for grouping
        }
        Series column = dataFrame.get(columnKey);
        Series.Builder groupVector = column.newBuilder();
        for (int i = 0, size = group.getValue().size(); i < size; i++) {
          groupVector.addFromLocation(column, index.get(i));
        }
        builder.set(group.getKey(), columnKey, function.apply(groupVector.build()));
      }
    }
    return builder.build();
  }

  private boolean dropColumnKey(Object columnKey) {
    if (dropKeys.length != 0) {
      for (Object dropKey : dropKeys) {
        if (columnKey == dropKey || (columnKey != null && columnKey.equals(dropKey))) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public <T, C> DataFrame collect(Class<? extends T> cls,
      Collector<? super T, C, ? extends T> collector) {
    DataFrame.Builder builder = dataFrame.newEmptyBuilder();
    for (Map.Entry<Object, IntArray> group : groups.entrySet()) {
      Object groupKey = group.getKey();
      IntArray index = group.getValue();

      for (Object columnKey : dataFrame.getColumnIndex().keySet()) {
        Series column = dataFrame.get(columnKey);
        if (dropColumnKey(columnKey) || !cls.isAssignableFrom(column.getType().getDataClass())) {
          continue;
        }
        LocationGetter columnLocation = column.loc();
        C accumulator = collector.supplier().get();
        for (int i = 0, size = index.size(); i < size; i++) {
          T value = columnLocation.get(cls, index.get(i));
          collector.accumulator().accept(accumulator, value);
        }
        builder.set(groupKey, columnKey, collector.finisher().apply(accumulator));
      }
    }
    return builder.build();
  }

  @Override
  public DataFrame apply(UnaryOperator<Series> op) {
    DataFrame.Builder builder = dataFrame.newEmptyBuilder();
    for (Object dropKey : dropKeys) {
      builder.setColumn(dropKey, dataFrame.get(dropKey).newCopyBuilder());
    }
    for (Object columnKey : dataFrame.getColumnIndex()) {
      if (dropColumnKey(columnKey)) {
        continue;
      }
      Series column = dataFrame.get(columnKey);
      Series.Builder columnBuilder = column.newBuilder();
      for (IntArray index : groups.values()) {
        Series selectedColumn = column.loc().get(index);
        Series transformed = op.apply(selectedColumn);
        Check.state(selectedColumn.size() == transformed.size(), "transformation must retain size");
        for (int i = 0; i < index.size(); i++) {
          int id = index.get(i);
          columnBuilder.loc().setFrom(id, transformed, i);
        }
      }
      builder.setColumn(columnKey, columnBuilder);
    }
    DataFrame df = builder.build();
    df.setIndex(dataFrame.getIndex());
    return df;
  }

  private DataFrame createDataFrame(IntArray indices) {
    final int indexSize = indices.size();
    DataFrame.Builder builder = dataFrame.newEmptyBuilder();
    if (indexSize > 0) {
      Index labels = dataFrame.getIndex();
      for (Object column : dataFrame.getColumnIndex()) {
        for (int i = 0; i < indexSize; i++) {
          Object row = labels.get(indices.get(i));
          builder.setFrom(row, column, dataFrame, row, column);
        }
      }
    }
   return builder.build();
  }
}
