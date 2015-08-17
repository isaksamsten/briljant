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

package org.briljantframework.dataseries;

import org.briljantframework.Check;
import org.briljantframework.dataframe.AbstractDataFrame;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.EntryReader;
import org.briljantframework.vector.Na;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p> A DataSeries collection is collection of data series, i.e., vectors of the same type -
 * usually {@link org.briljantframework.vector.DoubleVector#TYPE}. There are some interesting
 * differences between this implementation and the traditional {@code DataFrame}. It is possible
 * for
 * the data series in the collection to be of different length. Therefore, {@link #columns()}
 * return
 * the maximum data series length and calls to {@code getAs...(n, col)} works as expected only if
 * {@code col < col.getRecord(n).size()}. If not (and {@code index < columns()}), NA is returned.
 * </p>
 *
 * @author Isak Karlsson
 */
public class DataSeriesCollection extends AbstractDataFrame {

  private final List<Vector> series;
  private final VectorType type;

  private final int columns;

  public DataSeriesCollection(List<Vector> series, VectorType type) {
    this.series = new ArrayList<>(series);
    this.type = type;
    this.columns = series.stream().mapToInt(Vector::size).max().orElse(0);
  }

  protected DataSeriesCollection(List<Vector> series, VectorType type, int columns) {
    this.type = Objects.requireNonNull(type);
    this.series = Objects.requireNonNull(series);
    this.columns = columns;
  }

  @Override
  public <T> T get(Class<T> cls, int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.get(cls, column);
    } else if (column >= 0 && column < columns) {
      return Na.from(cls);
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public double getAsDouble(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.getAsDouble(column);
    } else if (column >= 0 && column < columns) {
      return Na.DOUBLE;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public int getAsInt(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.getAsInt(column);
    } else if (column >= 0 && column < columns) {
      return Na.INT;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public String toString(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.toString(column);
    } else if (column >= 0 && column < columns) {
      return "NA";
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public boolean isNA(int row, int column) {
    return series.get(row).isNA(column);
  }

  @Override
  public VectorType getType(int index) {
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
    List<Vector.Builder> collect = series.stream()
        .map(Vector::newCopyBuilder)
        .collect(Collectors.toList());
    return new Builder(collect, type);
  }

  @Override
  public DataFrame drop(Collection<Integer> indexes) {
    Set<Integer> set = new HashSet<>(indexes);
    Builder builder = newBuilder();
    for (int i = 0; i < rows(); i++) {
      Vector row = getRecord(i);
      Vector.Builder vecBuilder = row.newBuilder();
      for (int j = 0; j < row.size(); j++) {
        if (!set.contains(j)) {
          vecBuilder.add(row, j);
        }
      }
      builder.addRecord(vecBuilder);
    }
    return builder.build();
  }

  @Override
  public Vector getRecord(int index) {
    return series.get(index); // TODO: the index
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
    private final List<Vector.Builder> builders;

    public Builder(VectorType type) {
      this.type = type;
      this.builders = new ArrayList<>();
    }

    public Builder(Class<?> cls) {
      this(Vec.typeOf(cls));
    }

    protected Builder(List<Vector.Builder> builders, VectorType type) {
      this.type = type;
      this.builders = builders;
    }

    @Override
    public Builder setNA(int row, int column) {
      ensureCapacity(row);
      builders.get(row).setNA(column);
      return this;
    }

    @Override
    public Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol) {
      ensureCapacity(toRow);
      // If the source row does not contain the source column requested
      // silently ignore the value. This is the case since data series
      // can be of unequal lengths.
      Vector row = from.getRecord(fromRow);
      if (fromCol < row.size()) {
        builders.get(toRow).set(toCol, row, fromCol);
      }
      return this;
    }

    @Override
    public Builder set(int row, int column, Vector from, int index) {
      ensureCapacity(row);
      builders.get(row).set(column, from, index);
      return this;
    }

    @Override
    public Builder set(int row, int column, Object value) {
      ensureCapacity(row);
      builders.get(row).set(column, value);
      return this;
    }

    @Override
    public Builder removeColumn(int column) {
      Check.elementIndex(column, columns());
//      columnNames.remove(column);
      for (int i = 0; i < rows(); i++) {
        Vector.Builder colb = builders.get(i);
        if (column < colb.size()) { // TODO: check?
          colb.remove(column);
        }
      }

      return this;
    }

    @Override
    public Builder swapColumns(int a, int b) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Builder read(EntryReader entryReader) throws IOException {
      int row = rows();
      while (entryReader.hasNext()) {
        ensureCapacity(row);
        DataEntry entry = entryReader.next();
        for (int i = 0; i < entry.size() && entry.hasNext(); i++) {
          builders.get(row).read(i, entry);
        }
        row++;
      }
      return this;
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
    public DataSeriesCollection build() {
      int p = columns();
      return new DataSeriesCollection(
          builders.stream()
              .map(Vector.Builder::build)
              .collect(Collectors.toCollection(ArrayList::new)),
          type,
          p);
    }

    @Override
    public Builder addRecord(Vector.Builder row) {
      builders.add(row);
      return this;
    }

    @Override
    public Builder addRecord(Vector vector) {
      Check.type(vector, type);
      return addRecord(vector.newCopyBuilder());
    }

    @Override
    public Builder insertRecord(int index, Vector.Builder builder) {
      ensureCapacity(index);
      builders.set(index, builder);
      return this;
    }

    @Override
    public DataFrame.Builder insertRecord(int index, Vector vector) {
      return insertRecord(index, vector.newCopyBuilder());
    }

    @Override
    public DataFrame.Builder swapRecords(int a, int b) {
      Collections.swap(builders, a, b);
      return this;
    }

    private void ensureCapacity(int row) {
      while (row >= builders.size()) {
        builders.add(type.newBuilder());
      }
    }
  }
}
