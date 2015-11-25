/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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

package org.briljantframework.data.dataseries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.briljantframework.Check;
import org.briljantframework.array.IntArray;
import org.briljantframework.data.Na;
import org.briljantframework.data.dataframe.AbstractDataFrame;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;

/**
 * <p>
 * A DataSeries collection is collection of data series, i.e., vectors of the same type - usually
 * {@link Double}. There are some interesting differences between this implementation and the
 * traditional {@code DataFrame}. It is possible for the data series in the collection to be of
 * different length. Therefore, {@link #columns()} return the maximum data series length and calls
 * to {@code getAs...(n, col)} works as expected only if {@code col < col.getRecord(n).size()}. If
 * not (and {@code index < columns()}), NA is returned.
 * </p>
 *
 * @author Isak Karlsson
 */
public class DataSeriesCollection extends AbstractDataFrame {

  private final List<Vector> series;
  private final VectorType type;

  private final int columns;

  private DataSeriesCollection(List<Vector> series, VectorType type, int columns,
      Index columnIndex, Index index) {
    super(columnIndex, index);
    Check.argument(series.size() == index.size());
    Check.argument(columnIndex.size() == columns);
    this.series = series;
    this.type = type;
    this.columns = columns;
  }

  @Override
  public <T> T getAt(Class<T> cls, int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.loc().get(cls, column);
    } else if (column >= 0 && column < columns) {
      return Na.of(cls);
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public double getAsDoubleAt(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.loc().getAsDouble(column);
    } else if (column >= 0 && column < columns) {
      return Na.DOUBLE;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public int getAsIntAt(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.loc().getAsInt(column);
    } else if (column >= 0 && column < columns) {
      return Na.INT;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public String toStringAt(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.loc().toString(column);
    } else if (column >= 0 && column < columns) {
      return "NA";
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public boolean isNaAt(int row, int column) {
    return series.get(row).loc().isNA(column);
  }

  @Override
  protected DataFrame shallowCopy(Index columnIndex, Index index) {
    return new DataSeriesCollection(series, type, columns, columnIndex, index);
  }

  @Override
  protected VectorType getMostSpecificColumnType() {
    return type;
  }

  @Override
  public VectorType getTypeAt(int index) {
    return type;
  }

  @Override
  public int rows() {
    return series.size();
  }

  @Override
  public int columns() {
    return columns;
  }

  @Override
  public Builder newBuilder() {
    return new Builder(type);
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(this, type);
  }

  @Override
  protected Vector getAt(int index) {
    return new ColumnView(this, type, index);
  }

  @Override
  protected DataFrame dropAt(IntArray indexes) {
    indexes.sort();
    Builder builder = newBuilder();
    for (int i = 0; i < rows(); i++) {
      Vector row = getRecordAt(i);
      Vector.Builder vecBuilder = row.newBuilder();
      for (int j = 0; j < row.size(); j++) {
        if (org.briljantframework.array.Arrays.binarySearch(indexes, j) < 0) {
          vecBuilder.add(row, j);
        }
      }
      builder.addRecord(vecBuilder);
    }
    Index.Builder columnIndex = getColumnIndex().newBuilder();
    for (int i = 0; i < columns(); i++) {
      if (org.briljantframework.array.Arrays.binarySearch(indexes, i) < 0) {
        columnIndex.add(getColumnIndex().get(i));
      }
    }

    return builder.setColumnIndex(columnIndex.build()).setIndex(getIndex()).build();
  }

  @Override
  public Vector getRecordAt(int index) {
    Vector vector = series.get(index);
    // vector.setIndex(getColumnIndex());
    return vector; // TODO: rethink indexing?
  }

  /**
   * Returns the type of data series in this DataSeries collection
   *
   * @return the type
   */
  public VectorType getType() {
    return type;
  }

  public static class Builder extends AbstractBuilder {

    private final VectorType type;
    private List<Vector.Builder> builders;

    public Builder(VectorType type) {
      this.type = type;
      this.builders = new ArrayList<>();
    }

    public Builder(Class<?> cls) {
      this(VectorType.of(cls));
    }

    private Builder(DataSeriesCollection df, VectorType type) {
      super(df);
      this.type = type;
      this.builders =
          df.series.stream().map(Vector::newCopyBuilder)
              .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    protected void setNaAt(int row, int column) {
      ensureCapacity(row);
      builders.get(row).loc().setNA(column);
    }

    @Override
    public void setAt(int fr, int fc, DataFrame from, int tr, int tc) {
      ensureCapacity(fr);
      // If the source row does not contain the source column requested
      // silently ignore the value. This is the case since data series
      // can be of unequal lengths.
      Vector row = from.loc().getRecord(tr);
      if (tc < row.size()) {
        builders.get(fr).loc().set(fc, row, tc);
      }
    }

    @Override
    protected Vector.Builder getAt(int i) {
      throw new UnsupportedOperationException();
    }

    @Override
    protected Vector.Builder getRecordAt(int i) {
      return builders.get(i);
    }

    @Override
    protected void setAt(int r, int c, Vector from, int i) {
      ensureCapacity(r);
      builders.get(r).loc().set(c, from, i);
    }

    @Override
    public void setAt(int row, int column, Object value) {
      ensureCapacity(row);
      builders.get(row).loc().set(column, value);
    }

    @Override
    protected void setAt(int c, Vector.Builder builder) {
      final int size = builder.size();
      final Vector temporaryVector = builder.getTemporaryVector();
      for (int i = 0; i < size; i++) {
        setAt(i, c, temporaryVector, i);
      }

      final int rows = rows();
      for (int i = size; i < rows; i++) {
        setNaAt(i, c);
      }
    }

    @Override
    public void removeAt(int column) {
      Check.validIndex(column, columns());
      for (int i = 0; i < rows(); i++) {
        Vector.Builder colb = builders.get(i);
        if (column < colb.size()) { // TODO: check?
          colb.loc().remove(column);
        }
      }
    }

    @Override
    protected void setRecordAt(int index, Vector.Builder builder) {
      ensureCapacity(index);
      builders.set(index, builder);
    }

    @Override
    protected void removeRecordAt(int r) {
      builders.remove(r);
    }

    @Override
    public void swapAt(int a, int b) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void swapRecordsAt(int a, int b) {
      Collections.swap(builders, a, b);
    }

    @Override
    protected void readEntry(DataEntry entry) {
      int row = rows();
      ensureCapacity(row);
      for (int i = 0; i < entry.size() && entry.hasNext(); i++) {
        builders.get(row).loc().read(i, entry);
      }
    }

    @Override
    public int columns() {
      return builders.stream().mapToInt(Vector.Builder::size).max().orElse(0);
    }

    @Override
    public int rows() {
      return builders.size();
    }

    @Override
    public DataFrame getTemporaryDataFrame() {
      int columns = columns();
      ArrayList<Vector> series =
          builders.stream().map(Vector.Builder::getTemporaryVector)
              .collect(Collectors.toCollection(ArrayList::new));
      Index index = getIndex(rows());
      Index columnIndex = getColumnIndex(columns);
      return new DataSeriesCollection(series, type, columns, columnIndex, index) {
        @Override
        public Builder newCopyBuilder() {
          return Builder.this;
        }
      };
    }

    @Override
    public DataSeriesCollection build() {
      int columns = columns();
      DataSeriesCollection collection =
          new DataSeriesCollection(builders.stream().map(Vector.Builder::build)
              .collect(Collectors.toCollection(ArrayList::new)), type, columns,
              getColumnIndex(columns), getIndex(rows()));
      builders = null;
      return collection;
    }

    private void ensureCapacity(int row) {
      while (row >= builders.size()) {
        builders.add(type.newBuilder());
      }
    }
  }
}
