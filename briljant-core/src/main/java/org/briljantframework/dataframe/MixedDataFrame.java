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

package org.briljantframework.dataframe;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.io.EntryReader;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A mixed (i.e. heterogeneous) data frame contains vectors of possibly different types.
 *
 * @author Isak Karlsson
 */
public class MixedDataFrame extends AbstractDataFrame {

  private final List<Vector> columns;
  private final int rows;

  /**
   * Constructs a new mixed data frame from balanced vectors
   *
   * @param columns the vectors
   */
  public MixedDataFrame(Vector... columns) {
    this(Arrays.asList(columns));
  }

  /**
   * Construct a new mixed data frame from a collection of balanced vectors
   *
   * @param vectors the collection of vectors
   */
  public MixedDataFrame(Collection<? extends Vector> vectors) {
    Check.argument(vectors.size() > 0);

    this.columns = new ArrayList<>(vectors.size());
    int rows = 0;
    for (Vector vector : vectors) {
      if (rows == 0) {
        rows = vector.size();
      }
      Check.argument(
          vector.size() == rows,
          "Arguments imply different numbers of rows: %s, %s.",
          rows, vector.size()
      );
      this.columns.add(vector);
    }
    this.rows = rows;
  }

  /**
   * Constructs a new mixed data frame from a map of strings (column names) and balanced (i.e. of
   * same size) vectors.
   *
   * @param vectors the map of vectors and names
   */
  public <T> MixedDataFrame(Map<T, ? extends Vector> vectors) {
//    super(null, new HashIndex());
    Check.argument(vectors.size() > 0);
    this.columns = new ArrayList<>(vectors.size());
    int rows = 0;
    List<T> columnIndex = new ArrayList<>();
    for (Map.Entry<T, ? extends Vector> kv : vectors.entrySet()) {
      Vector vector = kv.getValue();
      T key = kv.getKey();
      columnIndex.add(key);
      if (rows == 0) {
        rows = vector.size();
      }
      Check.argument(
          vector.size() == rows,
          "Arguments imply different numbers of rows: %s, %s.",
          rows, vector.size()
      );
      this.columns.add(vector);
    }
    this.rows = rows;
    setColumnIndex(new HashIndex(columnIndex));
  }

  /**
   * Unsafe construction of a mixed data frame. Performs no sanity checking (should only be used
   * for
   * performance by checked builder).
   *
   * @param vectors the vectors
   * @param rows    the expected size of the vectors (not checked but should be enforced)
   */
  protected MixedDataFrame(List<Vector> vectors, int rows) {
    this.columns = vectors;
    this.rows = rows;
  }

  private static Vector padVectorWithNA(Vector.Builder builder, int maximumRows) {
    if (builder.size() < maximumRows) {
      builder.setNA(maximumRows - 1);
    }
    return builder.build();
  }

  public static DataFrame of(Object name, Vector c) {
    HashMap<Object, Vector> map = new HashMap<>();
    map.put(name, c);
    return new MixedDataFrame(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2) {
    HashMap<Object, Vector> map = new HashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    return new MixedDataFrame(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3) {
    HashMap<Object, Vector> map = new HashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    return new MixedDataFrame(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3,
                             Object n4, Vector v4) {
    HashMap<Object, Vector> map = new HashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    map.put(n4, v4);
    return new MixedDataFrame(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3,
                             Object n4, Vector v4, Object n5, Vector v5) {
    HashMap<Object, Vector> map = new HashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    map.put(n4, v4);
    map.put(n5, v5);
    return new MixedDataFrame(map);
  }

  public static DataFrame read(DataInputStream io) throws IOException {
    Collection<Object> index = io.readColumnIndex();
    DataFrame frame = new MixedDataFrame.Builder(io.readColumnTypes()).read(io).build();
    if (index != null) {
      frame.setColumnIndex(HashIndex.from(index));
      return frame;
    } else {
      return frame;
    }
  }

  @Override
  public <T> T get(Class<T> cls, int row, int column) {
    return columns.get(column).get(cls, row);
  }

  @Override
  public double getAsDouble(int row, int column) {
    return columns.get(column).getAsDouble(row);
  }

  @Override
  public int getAsInt(int row, int column) {
    return columns.get(column).getAsInt(row);
  }

  @Override
  public Bit getAsBit(int row, int column) {
    return columns.get(column).getAsBit(row);
  }

  @Override
  public Complex getAsComplex(int row, int column) {
    return columns.get(column).getAsComplex(row);
  }

  @Override
  public String toString(int row, int column) {
    return columns.get(column).toString(row);
  }

  @Override
  public boolean isNA(int row, int column) {
    return columns.get(column).isNA(row);
  }

  @Override
  public VectorType getType(int index) {
    return columns.get(index).getType();
  }

  @Override
  public int rows() {
    return rows;
  }

  @Override
  public int columns() {
    return columns.size();
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(this, true);
  }

  @Override
  public DataFrame add(Vector column) {
    List<Vector> newColumns = new ArrayList<>(columns);
    if (column.size() == rows()) {
      newColumns.add(column);
    } else if (column.size() < rows()) {
      newColumns.add(padVectorWithNA(column.newCopyBuilder(), rows()));
    } else {
      throw new IllegalArgumentException();
    }
    return new MixedDataFrame(newColumns, rows());
  }

  @Override
  public DataFrame insert(int index, Object key, Vector column) {
    Check.elementIndex(index, columns());
    if (getColumnIndex().contains(key)) {
      throw new IllegalArgumentException(key + " already in index");
    }
    List<Vector> newColumns = new ArrayList<>(columns);
    if (column.size() == rows()) {
      newColumns.add(index, column);
    } else if (column.size() < rows()) {
      newColumns.add(index, padVectorWithNA(column.newCopyBuilder(), rows()));
    } else {
      throw new IllegalArgumentException();
    }
    DataFrame df = new MixedDataFrame(newColumns, rows());
    Index.Builder columnIndex = getColumnIndex().newBuilder();
    columnIndex.set(key, index);
    for (Index.Entry entry : getColumnIndex().entrySet()) {
      int newIndex;
      int eIdx = entry.index();
      if (eIdx >= index) {
        newIndex = eIdx + 1;
      } else {
        newIndex = eIdx;
      }
      columnIndex.set(entry.key(), newIndex);
    }
    df.setColumnIndex(columnIndex.build());
    df.setRecordIndex(getRecordIndex());
    return df;
  }

  @Override
  public Vector get(int index) {
    return columns.get(index); // TODO: the index?!
  }

  @Override
  public DataFrame retain(Iterable<Integer> indexes) {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    for (int index : indexes) {
      builder.addColumn(get(index));
      // TODO:
//      if (getColumnNames().containsKey(index)) {
//        builder.getColumnNames().put(index, getColumnName(index));
//      }
    }
    return builder.build();
  }

  @Override
  public String toString() {
    return DataFrames.toTabularString(this);
  }

  /**
   * <p> Type for constructing a new MixedDataFrame. While for example, {@link
   * org.briljantframework.dataframe.MatrixDataFrame} and {@link org.briljantframework.dataseries.DataSeriesCollection.Builder}
   * can dynamically adapt the number of columns in the constructed DataFrame, this builder can
   * only
   * construct DataFrames with a fixed number of columns due to the fact that each column can be of
   * different types. </p>
   *
   * <p> To overcome this limitation, {@link #addColumnBuilder(org.briljantframework.vector.Vector.Builder)}
   * and {@link #removeColumn(int)} can be used. </p>
   */
  public static class Builder extends AbstractBuilder {

    private List<Vector.Builder> buffers = null;

    public Builder() {
      this.buffers = new ArrayList<>();
    }

    /**
     * Construct a builder with {@code types.length} columns.
     *
     * @param types the column types
     */
    public Builder(VectorType... types) {
      this(Arrays.asList(types));
    }

    /**
     * Construct a builder with {@code types.size()} columns. The column names will be {@code 1 ...
     * types.length}
     *
     * @param types the column types
     */
    public Builder(Collection<? extends VectorType> types) {
      buffers = new ArrayList<>(types.size());
      types.forEach(type -> buffers.add(type.newBuilder()));
    }

    /**
     * <p> Construct a builder using vector builders. Vector builders of different sizes are
     * allowed, but padded with NA values until to match the longest. </p>
     *
     * <p> Hence,
     *
     * <pre>
     *     [1 2 3]
     *     [1]
     *     [1,2,3,4]
     * </pre>
     *
     * Added would result in:
     *
     * <pre>
     *     [1,2,3, NA]
     *     [1, NA, NA, NA]
     *     [1,2,3,4]
     * </pre>
     *
     * </p>
     *
     * @param builders the vector builders
     */
    public Builder(Vector.Builder... builders) {
      int rows = Stream.of(builders).mapToInt(Vector.Builder::size).max().getAsInt();
      this.buffers = new ArrayList<>();
      for (Vector.Builder builder : builders) {
        if (builder.size() < rows) {
          builder.setNA(rows - 1);
        }

        buffers.add(builder);
      }
    }

    /**
     * Clones {@code frame}. If {@code copy == true}, the values are copied. Otherwise, only the
     * types and column names are copied.
     *
     * @param frame the DataFrame to clone
     * @param copy  copy values or only types
     */
    public Builder(DataFrame frame, boolean copy) {
      buffers = new ArrayList<>(frame.columns());

      for (int i = 0; i < frame.columns(); i++) {
        Vector vector = frame.get(i);
        if (copy) {
          buffers.add(vector.newCopyBuilder());
        } else {
          buffers.add(vector.newBuilder());
        }
      }
    }

    private Builder(MixedDataFrame frame, int rows, int columns) {
      buffers = new ArrayList<>(columns);
      for (int i = 0; i < columns; i++) {
        buffers.add(frame.get(i).newBuilder(rows));
      }
    }

    @Override
    public Vector getColumn(int col) {
      return buffers.get(col).getTemporaryVector();
    }

    @Override
    public Builder setNA(int row, int column) {
      ensureColumnCapacity(column, Vec.VARIABLE);
      buffers.get(column).setNA(row);
      return this;
    }

    @Override
    public Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol) {
      ensureColumnCapacity(toCol - 1, Vec.VARIABLE);
      ensureColumnCapacity(toCol, from.getType(fromCol));
      buffers.get(toCol).set(toRow, from.get(fromCol), fromRow);
      return this;
    }

    @Override
    public Builder set(int row, int column, Vector from, int index) {
      ensureColumnCapacity(column - 1, Vec.VARIABLE);
      ensureColumnCapacity(column, from.getType(index));
      buffers.get(column).set(row, from, index);
      return this;
    }

    @Override
    public Builder set(int row, int column, Object value) {
      ensureColumnCapacity(column - 1, Vec.VARIABLE);
      ensureColumnCapacity(column, Vec.inferTypeOf(value));
      buffers.get(column).set(row, value);
      return this;
    }

    @Override
    public Builder removeColumn(int column) {
//      columnNames.remove(column);
      buffers.remove(column);
      return this;
    }

    @Override
    public Builder swapColumns(int a, int b) {
      Collections.swap(buffers, a, b);
      return this;
    }

    public Builder swapInColumn(int column, int a, int b) {
      buffers.get(column).swap(a, b);
      return this;
    }

    @Override
    public Builder swapRecords(int a, int b) {
      for (int i = 0; i < columns(); i++) {
        swapInColumn(i, a, b);
      }
      return this;
    }


    @Override
    public Builder read(EntryReader entryReader) throws IOException {
      while (entryReader.hasNext()) {
        DataEntry entry = entryReader.next();
        ensureColumnCapacity(entry.size() - 1, Vec.VARIABLE);
        for (int i = 0; i < entry.size(); i++) {
          buffers.get(i).read(entry);
        }
      }

      return this;
    }

    @Override
    public int columns() {
      return buffers.size();
    }

    /**
     * Returns the vector with most rows
     *
     * @return the number of rows
     */
    @Override
    public int rows() {
      return buffers.stream().mapToInt(Vector.Builder::size).reduce(0, Integer::max);
    }

    /**
     * Constructs a new MixedDataFrame
     *
     * @return a new MixedDataFrame
     */
    @Override
    public MixedDataFrame build() {
      int rows = rows();
      List<Vector> vectors =
          buffers.stream().map(x -> padVectorWithNA(x, rows))
              .collect(Collectors.toCollection(ArrayList::new));
      buffers = null;
      return new MixedDataFrame(vectors, rows);
    }

    private void ensureColumnCapacity(int column, VectorType type) {
      while (column >= buffers.size()) {
        buffers.add(type.newBuilder());
      }
    }

    @Override
    public Builder addColumnBuilder(Vector.Builder builder) {
      buffers.add(builder);
      return this;
    }

    @Override
    public DataFrame.Builder addColumn(Vector vector) {
      buffers.add(vector.newCopyBuilder());
      return this;
    }

    @Override
    public DataFrame.Builder insertColumn(int index, Vector.Builder builder) {
      if (index == buffers.size()) {
        this.buffers.add(builder);
      } else {
        this.buffers.set(index, builder);
      }
      return this;
    }

    @Override
    public DataFrame.Builder insertColumn(int index, Vector vector) {
      return insertColumn(index, vector.newCopyBuilder());
    }
  }
}
