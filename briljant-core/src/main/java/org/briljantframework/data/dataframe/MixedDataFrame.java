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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.briljantframework.Check;
import org.briljantframework.data.Na;
import org.briljantframework.data.index.HashIndex;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.series.Series;
import org.briljantframework.data.series.Type;
import org.briljantframework.data.series.TypeInferenceBuilder;
import org.briljantframework.data.series.Types;

/**
 * A mixed (i.e. heterogeneous) data frame contains vectors of possibly different types.
 *
 * @author Isak Karlsson
 */
public class MixedDataFrame extends AbstractDataFrame {

  private final List<Series> columns;
  private final Type mostSpecificColumnType;
  private final int rows;

  /**
   * Constructs a new mixed data frame from balanced vectors
   *
   * @param columns the vectors
   */
  private MixedDataFrame(Series... columns) {
    this(Arrays.asList(columns));
  }

  /**
   * Construct a new mixed data frame from a collection of balanced vectors
   *
   * @param vectors the collection of vectors
   */
  private MixedDataFrame(Collection<? extends Series> vectors) {
    super(null, null);
    Check.argument(vectors.size() > 0);

    this.columns = new ArrayList<>(vectors.size());
    int rows = 0;
    Set<Type> typeSet = new HashSet<>();
    for (Series series : vectors) {
      if (rows == 0) {
        rows = series.size();
      }
      Check.argument(series.size() == rows, "Arguments imply different numbers of rows: %s, %s.",
          rows, series.size());
      typeSet.add(series.getType());
      this.columns.add(series);
    }
    this.mostSpecificColumnType = typeSet.size() == 1 ? typeSet.iterator().next() : Types.OBJECT;
    this.rows = rows;
  }

  /**
   * Constructs a new mixed data frame from a map of strings (column names) and balanced (i.e. of
   * same size) vectors.
   *
   * @param vectors the map of vectors and names
   */
  private <T> MixedDataFrame(Map<T, ? extends Series> vectors) {
    super(null, null); // TODO: fix me
    Check.argument(vectors.size() > 0);
    this.columns = new ArrayList<>(vectors.size());
    int rows = 0;
    List<T> columnIndex = new ArrayList<>();
    Set<Type> typeSet = new HashSet<>();
    for (Map.Entry<T, ? extends Series> kv : vectors.entrySet()) {
      Series series = kv.getValue();
      T key = kv.getKey();
      columnIndex.add(key);
      if (rows == 0) {
        rows = series.size();
      }
      Check.argument(series.size() == rows, "Arguments imply different numbers of rows: %s, %s.",
          rows, series.size());
      this.columns.add(series);
      typeSet.add(series.getType());

    }
    this.rows = rows;
    this.mostSpecificColumnType = typeSet.size() == 1 ? typeSet.iterator().next() : Types.OBJECT;
    setColumnIndex(HashIndex.of(columnIndex));
  }

  /**
   * Unsafe construction of a mixed data frame. Performs no sanity checking (should only be used for
   * performance by checked builder).
   *
   * @param columns the vectors
   * @param rows the expected size of the vectors (not checked but should be enforced)
   */
  private MixedDataFrame(List<Series> columns, int rows) {
    super(null, null);
    this.columns = columns;
    this.rows = rows;
    Set<Type> typeSet = columns.stream().map(Series::getType).collect(Collectors.toSet());
    this.mostSpecificColumnType = typeSet.size() == 1 ? typeSet.iterator().next() : Types.OBJECT;
  }

  private MixedDataFrame(List<Series> columns, int rows, Index columnIndex, Index index) {
    super(columnIndex, index);
    Check.argument(columns.size() == columnIndex.size());
    Check.argument(index.size() == rows);
    Set<Type> typeSet = columns.stream().map(Series::getType).collect(Collectors.toSet());
    this.mostSpecificColumnType = typeSet.size() == 1 ? typeSet.iterator().next() : Types.OBJECT;
    this.columns = columns;
    this.rows = rows;

  }

  private static Series.Builder padVectorWithNA(Series.Builder builder, int maximumRows) {
    if (builder.size() < maximumRows) {
      builder.loc().setNA(maximumRows - 1);
    }
    return builder;
  }

  public static MixedDataFrame create(Collection<? extends Series> vectors) {
    return new MixedDataFrame(vectors);
  }

  public static MixedDataFrame create(Series... columns) {
    return new MixedDataFrame(columns);
  }

  static MixedDataFrame of(Object name, Series c) {
    HashMap<Object, Series> map = new LinkedHashMap<>();
    map.put(name, c);
    if (map.size() != 1) {
      throw new IllegalArgumentException("duplicate elements");
    }
    return MixedDataFrame.create(map);
  }

  public static <T> MixedDataFrame create(Map<T, ? extends Series> vectors) {
    return new MixedDataFrame(vectors);
  }

  static MixedDataFrame of(Object n1, Series v1, Object n2, Series v2) {
    HashMap<Object, Series> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    if (map.size() != 2) {
      throw new IllegalArgumentException("duplicate elements");
    }
    return MixedDataFrame.create(map);
  }

  static MixedDataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3) {
    HashMap<Object, Series> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    if (map.size() != 3) {
      throw new IllegalArgumentException("duplicate elements");
    }
    return MixedDataFrame.create(map);
  }

  static MixedDataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3,
      Object n4, Series v4) {
    HashMap<Object, Series> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    map.put(n4, v4);
    if (map.size() != 4) {
      throw new IllegalArgumentException("duplicate elements");
    }
    return MixedDataFrame.create(map);
  }

  static MixedDataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3,
      Object n4, Series v4, Object n5, Series v5) {
    HashMap<Object, Series> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    map.put(n4, v4);
    map.put(n5, v5);
    if (map.size() != 5) {
      throw new IllegalArgumentException("duplicate elements");
    }
    return MixedDataFrame.create(map);
  }

  static MixedDataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3,
      Object n4, Series v4, Object n5, Series v5, Object n6, Series v6) {
    HashMap<Object, Series> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    map.put(n4, v4);
    map.put(n5, v5);
    map.put(n6, v6);
    if (map.size() != 6) {
      throw new IllegalArgumentException("duplicate elements");
    }
    return MixedDataFrame.create(map);
  }

  static MixedDataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3,
      Object n4, Series v4, Object n5, Series v5, Object n6, Series v6, Object n7, Series v7) {
    HashMap<Object, Series> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    map.put(n4, v4);
    map.put(n5, v5);
    map.put(n6, v6);
    map.put(n7, v7);
    if (map.size() != 7) {
      throw new IllegalArgumentException("duplicate elements");
    }
    return MixedDataFrame.create(map);
  }

  static MixedDataFrame of(Object n1, Series v1, Object n2, Series v2, Object n3, Series v3,
      Object n4, Series v4, Object n5, Series v5, Object n6, Series v6, Object n7, Series v7,
      Object n8, Series v8) {
    HashMap<Object, Series> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    map.put(n4, v4);
    map.put(n5, v5);
    map.put(n6, v6);
    map.put(n7, v7);
    map.put(n8, v8);
    if (map.size() != 8) {
      throw new IllegalArgumentException("duplicate elements");
    }
    return MixedDataFrame.create(map);
  }

  /**
   * Create a mixed data frame containing the keys and values from the given entries.
   *
   * @param entries the keys and values with which the dataframe is populated
   * @return a newly created {@code MixedDataFrame}
   */
  @SafeVarargs
  public static MixedDataFrame fromEntries(Map.Entry<Object, ? extends Series> entry,
      Map.Entry<Object, ? extends Series>... entries) {
    Map<Object, Series> map = new LinkedHashMap<>();
    map.put(entry.getKey(), entry.getValue());
    for (Map.Entry<Object, ? extends Series> e : entries) {
      map.put(e.getKey(), e.getValue());
    }
    return new MixedDataFrame(map);
  }

  public static MixedDataFrame.Builder builder() {
    return new MixedDataFrame.Builder();
  }

  @Override
  public boolean isElementNa(int row, int column) {
    return columns.get(column).loc().isNA(row);
  }

  @Override
  public int getIntElement(int row, int column) {
    return columns.get(column).loc().getInt(row);
  }

  @Override
  public double getDoubleElement(int row, int column) {
    return columns.get(column).loc().getDouble(row);
  }

  @Override
  public <T> T getElement(Class<T> cls, int row, int column) {
    return columns.get(column).loc().get(cls, row);
  }

  @Override
  protected Series getRowElement(int index) {
    Check.validIndex(index, rows());
    return new RowView(this, index, mostSpecificColumnType);
  }

  @Override
  public Builder newEmptyBuilder() {
    return new Builder();
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(this);
  }

  @Override
  public Series getColumnElement(int index) {
    return columns.get(index);
  }

  @Override
  protected void setColumnElement(int pos, Series column) {
    if (columns.size() == pos) {
      columns.add(pos, column);
    } else {
      columns.set(pos, column);
    }
  }

  @Override
  protected void setRowElement(int pos, Series row) {
    Index columnIndex = getColumnIndex();
    for (Object columnKey : columnIndex) {
      Series column = get(columnKey);
      if (row.getIndex().contains(columnKey)) {
        column.loc().set(pos, row.get(columnKey));
      } else {
        column.loc().set(pos, Na.ANY);
      }
    }
  }

  @Override
  protected void setElement(int r, int c, Object element) {
    columns.get(c).loc().set(r, element);
  }

  @Override
  protected DataFrame dropColumnElement(int index) {
    List<Series> newColumns = new ArrayList<>(columns);
    newColumns.remove(index);
    Index.Builder columnIndex = getColumnIndex().newCopyBuilder();
    columnIndex.removeLocation(index);
    return new MixedDataFrame(newColumns, rows, columnIndex.build(), getIndex());
  }

  @Override
  public DataFrame reindex(Index columnIndex, Index index) {
    return new MixedDataFrame(columns, rows, columnIndex, index);
  }

  @Override
  public int rows() {
    return rows;
  }

  @Override
  public int columns() {
    return columns.size();
  }

  public static final class Builder extends AbstractBuilder {

    private List<Series.Builder> buffers = null;

    public Builder() {
      this.buffers = new ArrayList<>();
    }

    /**
     * Construct a builder with {@code types.length} columns.
     *
     * @param types the column types
     */
    public Builder(Type... types) {
      this(Arrays.asList(types));
    }

    /**
     * Construct a builder with {@code types.size()} columns. The column names will be {@code 1 ...
     * types.length}
     *
     * @param types the column types
     */
    public Builder(Collection<? extends Type> types) {
      this(types.stream().map(Type::newBuilder).toArray(Series.Builder[]::new));
    }

    /**
     * <p>
     * Construct a builder using series builders. Series builders of different sizes are allowed,
     * but padded with NA values until to match the longest.
     * </p>
     *
     * <p>
     * Hence,
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
     * @param builders the series builders
     */
    public Builder(Series.Builder... builders) {
      int rows = Stream.of(builders).mapToInt(Series.Builder::size).max().getAsInt();
      this.buffers = new ArrayList<>();
      for (Series.Builder builder : builders) {
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
      buffers = frame.columns.stream().map(Series::newCopyBuilder)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    protected void setNaAt(int row, int column) {
      ensureColumnCapacity(column);
      buffers.get(column).loc().setNA(row);
    }

    @Override
    public void setElement(int r, int c, Object value) {
      ensureColumnCapacity(c - 1);
      ensureColumnCapacity(c, Types.inferFrom(value));
      buffers.get(c).loc().set(r, value);
    }

    @Override
    public void setColumnElement(int c, Series.Builder builder) {
      if (c == buffers.size()) {
        this.buffers.add(builder);
      } else {
        this.buffers.set(c, builder);
      }
    }

    @Override
    protected void setRowElement(int index, Series.Builder builder) {
      // ensureColumnCapacity(builder.size() - 1);
      final int columns = columns();
      final int size = builder.size();
      final Series series = builder.build();
      for (int j = 0; j < Math.max(size, columns); j++) {
        if (j < size) {
          Object value = series.loc().get(Object.class, j);
          ensureColumnCapacity(j, Types.inferFrom(value));
          setElement(index, j, value);
          // setAt(index, j, series, j);
        } else {
          setNaAt(index, j);
        }
      }
    }

    @Override
    public void setElementFromLocation(int r, int c, Series from, int i) {
      ensureColumnCapacity(c - 1);
      ensureColumnCapacity(c, from.getType());
      buffers.get(c).loc().setFrom(r, from, i);
    }

    @Override
    public void removeAt(int column) {
      buffers.remove(column);
    }

    @Override
    protected void removeRowElement(int r) {
      for (Series.Builder buffer : buffers) {
        buffer.loc().remove(r);
      }
    }

    @Override
    public void swapAt(int a, int b) {
      Collections.swap(buffers, a, b);
    }

    Builder swapInColumn(int column, int a, int b) {
      buffers.get(column).loc().swap(a, b);
      return this;
    }

    @Override
    public void swapRowElement(int a, int b) {
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

    public int rows() {
      return buffers.stream().mapToInt(Series.Builder::size).reduce(0, Integer::max);
    }

    public int columns() {
      return buffers.size();
    }

    @Override
    public MixedDataFrame build() {
      int rows = rows();
      List<Series> series = buffers.stream().map(x -> padVectorWithNA(x, rows).build())
          .collect(Collectors.toCollection(ArrayList::new));
      MixedDataFrame df = new MixedDataFrame(series, rows, getColumnIndex(columns()), getIndex(rows));
      buffers = null;
      return df;
    }

    private void ensureColumnCapacity(int index, Type type) {
      int i = buffers.size();
      while (i <= index) {
        buffers.add(type.newBuilder());
        i++;
      }
    }

    private void ensureColumnCapacity(int index) {
      int i = buffers.size();
      while (i <= index) {
        buffers.add(new TypeInferenceBuilder());
        i++;
      }
    }
  }
}
