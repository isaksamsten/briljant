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

import org.briljantframework.Check;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.vector.TypeInferenceVectorBuilder;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A mixed (i.e. heterogeneous) data frame contains vectors of possibly different types.
 *
 * @author Isak Karlsson
 */
public class MixedDataFrame extends AbstractDataFrame {

  private final List<Vector> columns;
  private final VectorType mostSpecificColumnType;
  private final int rows;

  /**
   * Constructs a new mixed data frame from balanced vectors
   *
   * @param columns the vectors
   */
  private MixedDataFrame(Vector... columns) {
    this(Arrays.asList(columns));
  }

  /**
   * Construct a new mixed data frame from a collection of balanced vectors
   *
   * @param vectors the collection of vectors
   */
  private MixedDataFrame(Collection<? extends Vector> vectors) {
    super(null, null);
    Check.argument(vectors.size() > 0);

    this.columns = new ArrayList<>(vectors.size());
    int rows = 0;
    Set<VectorType> typeSet = new HashSet<>();
    for (Vector vector : vectors) {
      if (rows == 0) {
        rows = vector.size();
      }
      Check.argument(
          vector.size() == rows,
          "Arguments imply different numbers of rows: %s, %s.",
          rows, vector.size()
      );
      typeSet.add(vector.getType());
      this.columns.add(vector);
    }
    this.mostSpecificColumnType = typeSet.size() == 1 ?
                                  typeSet.iterator().next() :
                                  VectorType.OBJECT;
    this.rows = rows;
  }

  /**
   * Constructs a new mixed data frame from a map of strings (column names) and balanced (i.e. of
   * same size) vectors.
   *
   * @param vectors the map of vectors and names
   */
  private <T> MixedDataFrame(Map<T, ? extends Vector> vectors) {
    super(null, null); // TODO: fix me
    Check.argument(vectors.size() > 0);
    this.columns = new ArrayList<>(vectors.size());
    int rows = 0;
    List<T> columnIndex = new ArrayList<>();
    Set<VectorType> typeSet = new HashSet<>();
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
      typeSet.add(vector.getType());

    }
    this.rows = rows;
    this.mostSpecificColumnType = typeSet.size() == 1 ?
                                  typeSet.iterator().next() :
                                  VectorType.OBJECT;
    setColumnIndex(new ObjectIndex(columnIndex));
  }

  /**
   * Unsafe construction of a mixed data frame. Performs no sanity checking (should only be used
   * for
   * performance by checked builder).
   *
   * @param columns the vectors
   * @param rows    the expected size of the vectors (not checked but should be enforced)
   */
  private MixedDataFrame(List<Vector> columns, int rows) {
    super(null, null);
    this.columns = columns;
    this.rows = rows;
    Set<VectorType> typeSet = columns.stream().map(Vector::getType).collect(Collectors.toSet());
    this.mostSpecificColumnType = typeSet.size() == 1 ?
                                  typeSet.iterator().next() :
                                  VectorType.OBJECT;
  }

  private MixedDataFrame(List<Vector> columns, int rows, Index columnIndex, Index index) {
    super(columnIndex, index);
    Check.argument(columns.size() == columnIndex.size());
    Check.argument(index.size() == rows);
    Set<VectorType> typeSet = columns.stream().map(Vector::getType).collect(Collectors.toSet());
    this.mostSpecificColumnType = typeSet.size() == 1 ?
                                  typeSet.iterator().next() :
                                  VectorType.OBJECT;
    this.columns = columns;
    this.rows = rows;

  }

  private static Vector.Builder padVectorWithNA(Vector.Builder builder, int maximumRows) {
    if (builder.size() < maximumRows) {
      builder.loc().setNA(maximumRows - 1);
    }
    return builder;
  }

  public static MixedDataFrame create(Collection<? extends Vector> vectors) {
    return new MixedDataFrame(vectors);
  }

  public static MixedDataFrame create(Vector... columns) {
    return new MixedDataFrame(columns);
  }

  public static <T> MixedDataFrame create(Map<T, ? extends Vector> vectors) {
    return new MixedDataFrame(vectors);
  }

  public static DataFrame of(Object name, Vector c) {
    HashMap<Object, Vector> map = new LinkedHashMap<>();
    map.put(name, c);
    return create(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2) {
    HashMap<Object, Vector> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    return create(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3) {
    HashMap<Object, Vector> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    return create(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3,
                             Object n4, Vector v4) {
    HashMap<Object, Vector> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    map.put(n4, v4);
    return create(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3,
                             Object n4, Vector v4, Object n5, Vector v5) {
    HashMap<Object, Vector> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    map.put(n4, v4);
    map.put(n5, v5);
    return create(map);
  }

  @Override
  public <T> T getAt(Class<T> cls, int row, int column) {
    return columns.get(column).loc().get(cls, row);
  }

  @Override
  public double getAsDoubleAt(int row, int column) {
    return columns.get(column).loc().getAsDouble(row);
  }

  @Override
  protected Vector getRecordAt(int index) {
    Check.elementIndex(index, rows());
    return new RecordView(this, index, mostSpecificColumnType);
  }

  @Override
  public int getAsIntAt(int row, int column) {
    return columns.get(column).loc().getAsInt(row);
  }

  @Override
  public String toStringAt(int row, int column) {
    return columns.get(column).loc().toString(row);
  }

  @Override
  public boolean isNaAt(int row, int column) {
    return columns.get(column).loc().isNA(row);
  }

  @Override
  protected DataFrame shallowCopy(Index columnIndex, Index index) {
    return new MixedDataFrame(columns, rows, columnIndex, index);
  }

  @Override
  protected VectorType getMostSpecificColumnType() {
    return mostSpecificColumnType;
  }

  @Override
  public VectorType getTypeAt(int index) {
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
    return new Builder(this);
  }

  @Override
  public Vector getAt(int index) {
    Vector vector = columns.get(index);
    vector.setIndex(getIndex());
    return vector;
  }

  public static final class Builder extends AbstractBuilder {

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
      this(types.stream().map(VectorType::newBuilder).toArray(Vector.Builder[]::new));
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
          builder.loc().setNA(rows - 1);
        }
        buffers.add(builder);
      }
    }

    /**
     * Clones {@code frame}. If {@code copy == true}, the values are copied. Otherwise, only the
     * types and column names are copied.
     *
     * @param frame the DataFrame to clone
     */
    public Builder(MixedDataFrame frame) {
      super(frame);
      buffers = frame.columns.stream()
          .map(Vector::newCopyBuilder)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    protected void setNaAt(int row, int column) {
      ensureColumnCapacity(column);
      buffers.get(column).loc().setNA(row);
    }

    @Override
    public void setAt(int r, int c, Object value) {
      ensureColumnCapacity(c - 1);
      ensureColumnCapacity(c, VectorType.of(value));
      buffers.get(c).loc().set(r, value);
    }

    @Override
    public void setAt(int c, Vector.Builder builder) {
      if (c == buffers.size()) {
        this.buffers.add(builder);
      } else {
        this.buffers.set(c, builder);
      }
    }

    @Override
    protected void setRecordAt(int index, Vector.Builder builder) {
//      ensureColumnCapacity(builder.size() - 1);
      final int columns = columns();
      final int size = builder.size();
      final Vector vector = builder.getTemporaryVector();
      for (int j = 0; j < Math.max(size, columns); j++) {
        if (j < size) {
          Object value = vector.loc().get(Object.class, j);
          ensureColumnCapacity(j, VectorType.of(value));
          setAt(index, j, value);
//          setAt(index, j, vector, j);
        } else {
          setNaAt(index, j);
        }
      }
    }

    @Override
    public void setAt(int r, int c, Vector from, int i) {
      ensureColumnCapacity(c - 1);
      ensureColumnCapacity(c, from.getType());
      buffers.get(c).loc().set(r, from, i);
    }

    @Override
    public void removeAt(int column) {
      buffers.remove(column);
    }

    @Override
    protected void removeRecordAt(int r) {
      for (int i = 0, buffersSize = buffers.size(); i < buffersSize; i++) {
        Vector.Builder buffer = buffers.get(i);
        buffer.loc().remove(r);
      }
    }

    @Override
    public void swapAt(int a, int b) {
      Collections.swap(buffers, a, b);
    }

    public Builder swapInColumn(int column, int a, int b) {
      buffers.get(column).loc().swap(a, b);
      return this;
    }

    @Override
    public void swapRecordsAt(int a, int b) {
      for (int i = 0; i < columns(); i++) {
        swapInColumn(i, a, b);
      }
    }

    @Override
    protected void readEntry(DataEntry entry) {
      ensureColumnCapacity(entry.size() - 1);
      for (int i = 0; i < entry.size(); i++) {
        buffers.get(i).read(entry);
      }
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

    @Override
    public DataFrame getTemporaryDataFrame() {
      int rows = rows();
      List<Vector> vectors = buffers.stream()
          .map((builder) -> padVectorWithNA(builder, rows).getTemporaryVector())
          .collect(Collectors.toCollection(ArrayList::new));
      return new MixedDataFrame(vectors, rows) {
        @Override
        public Builder newCopyBuilder() {
          return Builder.this;
        }
      };
    }

    @Override
    public MixedDataFrame build() {
      int rows = rows();
      List<Vector> vectors = buffers.stream()
          .map(x -> padVectorWithNA(x, rows).build())
          .collect(Collectors.toCollection(ArrayList::new));
      MixedDataFrame df = new MixedDataFrame(
          vectors,
          rows,
          getColumnIndex(columns()),
          getIndex(rows)
      );
      buffers = null;
      return df;
    }

    private void ensureColumnCapacity(int index, VectorType type) {
      int i = buffers.size();
      while (i <= index) {
        buffers.add(type.newBuilder());
        i++;
      }
    }

    private void ensureColumnCapacity(int index) {
      int i = buffers.size();
      while (i <= index) {
        buffers.add(new TypeInferenceVectorBuilder());
        i++;
      }
    }
  }
}
