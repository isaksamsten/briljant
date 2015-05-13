package org.briljantframework.dataframe;

import org.briljantframework.vector.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author Isak Karlsson
 */
class HashDataFrameGroupBy implements DataFrameGroupBy {

  private final HashMap<Object, Vector> groups;
  private final DataFrame dataFrame;

  HashDataFrameGroupBy(DataFrame dataFrame, HashMap<Object, ? extends Vector.Builder> groups) {
    this.dataFrame = dataFrame;
    this.groups = new HashMap<>();
    for (Map.Entry<Object, ? extends Vector.Builder> e : groups.entrySet()) {
      this.groups.put(e.getKey(), e.getValue().build());
    }
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
        DataFrame.Builder builder = dataFrame.newBuilder();
        if (indices.size() > 0) {
          for (int j = 0; j < dataFrame.columns(); j++) {
            builder.addColumnBuilder(dataFrame.getType(j));
            for (int i = 0; i < indices.size(); i++) {
              builder.set(i, j, dataFrame, indices.getAsInt(i), j);
            }
          }
        }
        return new Group(
            key, builder.build().setColumnIndex(dataFrame.getColumnIndex().copy())
        );
      }
    };
  }

  @Override
  public Set<Map.Entry<Object, Vector>> groups() {
    return groups.entrySet();
  }

  @Override
  public DataFrame aggregate(Function<Vector, Object> function) {
    DataFrame.Builder builder = dataFrame.newBuilder();
    Index.Builder recordIndex = new HashIndex.Builder();
    Index.Builder columnIndex = new HashIndex.Builder();
    for (int j = 0; j < dataFrame.columns(); j++) {
      columnIndex.add(dataFrame.getColumnIndex().get(j));
    }
    int row = 0;
    for (Map.Entry<Object, Vector> e : groups()) {
      recordIndex.add(e.getKey());
      Vector groupIndex = e.getValue();
      int column = 0;
      for (int j = 0; j < dataFrame.columns(); j++) {
        Vector col = dataFrame.get(j);
        Vector.Builder reduce = col.newBuilder(groupIndex.size());
        for (int i = 0; i < groupIndex.size(); i++) {
          reduce.set(i, col, groupIndex.getAsInt(i));
        }
        builder.set(row, column++, function.apply(reduce.build()));
      }
      row += 1;
    }

    return finalizeDataFrame(builder, recordIndex, columnIndex);
  }

  @Override
  public <T, C> DataFrame aggregate(Class<? extends T> cls, Aggregator<? super T, ? extends T, C> aggregator) {
    DataFrame.Builder builder = dataFrame.newBuilder();
    Index.Builder recordIndex = new HashIndex.Builder();
    Index.Builder columnIndex = new HashIndex.Builder();
    for (int j = 0; j < dataFrame.columns(); j++) {
      if (cls.isAssignableFrom(dataFrame.getType(j).getDataClass())) {
        columnIndex.add(dataFrame.getColumnIndex().get(j));
      }
    }

    int row = 0;
    for (Map.Entry<Object, Vector> e : groups.entrySet()) {
      recordIndex.add(e.getKey());
      int colIndex = 0;
      for (int j = 0; j < dataFrame.columns(); j++) {
        if (cls.isAssignableFrom(dataFrame.getType(j).getDataClass())) {
          Vector groupIndex = e.getValue();
          Vector col = dataFrame.get(j);
          C c = aggregator.supplier().get();
          for (int i = 0; i < groupIndex.size(); i++) {
            aggregator.accumulator().accept(c, col.get(cls, groupIndex.getAsInt(i)));
          }
          T t = aggregator.finisher().apply(c);
          builder.set(row, colIndex++, t);
        }
      }
      row++;
    }
    return finalizeDataFrame(builder, recordIndex, columnIndex);
  }

  @Override
  public <T> DataFrame transform(Class<T> cls, UnaryOperator<T> op) {
    return null;
  }

  protected DataFrame finalizeDataFrame(DataFrame.Builder builder, Index.Builder recordIndex,
                                        Index.Builder columnIndex) {
    return builder.build()
        .setRecordIndex(recordIndex.build())
        .setColumnIndex(columnIndex.build());
  }
}