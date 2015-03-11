package org.briljantframework.dataframe;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.briljantframework.complex.Complex;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.VariableVector;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * A mixed (i.e. heterogeneous) data frame contains vectors of possibly different types.
 * <p>
 * Created by Isak Karlsson on 21/11/14.
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
    checkArgument(vectors.size() > 0);

    this.columns = new ArrayList<>(vectors.size());
    int rows = 0, index = 0;
    for (Vector vector : vectors) {
      if (rows == 0) {
        rows = vector.size();
      }
      checkArgument(vector.size() == rows, "Arguments imply different numbers of rows: %s, %s.",
          rows, vector.size());
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
  public MixedDataFrame(Map<String, ? extends Vector> vectors) {
    checkArgument(vectors.size() > 0);
    this.columns = new ArrayList<>(vectors.size());

    int rows = 0;
    int index = 0;
    for (Map.Entry<String, ? extends Vector> kv : vectors.entrySet()) {
      Vector vector = kv.getValue();
      String name = kv.getKey();
      if (rows == 0) {
        rows = vector.size();
      }
      checkArgument(vector.size() == rows, "Arguments imply different numbers of rows: %s, %s.",
          rows, vector.size());
      this.columns.add(vector);
      setColumnName(index++, name);
    }
    this.rows = rows;
  }

  /**
   * Constructs a new mixed data frame from an iterable sequence of of
   * {@link org.briljantframework.vector.VariableVector} treated as rows of equal length
   *
   * @param sequences
   */
  public MixedDataFrame(Iterable<? extends VariableVector> sequences) {
    this.columns = new ArrayList<>();

    List<Vector.Builder> builders = new ArrayList<>();
    int columns = 0, rows = 0;
    for (VariableVector row : sequences) {
      if (columns == 0) {
        columns = row.size();
      }
      checkArgument(row.size() == columns, "Arguments imply different numbers of rows: %s, %s.",
          columns, row.size());
      for (int i = 0; i < row.size(); i++) {
        if (builders.size() <= i) {
          checkArgument(row.getType(i) != VariableVector.TYPE,
              "Can't create untyped vector as column.");
          builders.add(row.getType(i).newBuilder());
        }
        builders.get(i).add(row.getAsValue(i));
      }
      rows++;
    }
    int index = 0;
    for (Vector.Builder builder : builders) {
      this.columns.add(builder.build());
    }

    this.rows = rows;
  }

  /**
   * Unsafe construction of a mixed data frame. Performs no sanity checking (should only be used for
   * performance by checked builder).
   *
   * @param vectors the vectors
   * @param rows the expected size of the vectors (not checked but should be enforced)
   */
  protected MixedDataFrame(NameAttribute columnNames, NameAttribute rowNames, List<Vector> vectors,
      int rows) {
    super(columnNames, rowNames);
    this.columns = vectors;
    this.rows = rows;
  }

  public static DataFrame of(String name, Vector c) {
    return new MixedDataFrame(ImmutableMap.of(name, c));
  }

  public static DataFrame of(String n1, Vector v1, String n2, Vector v2) {
    return new MixedDataFrame(ImmutableMap.of(n1, v1, n2, v2));
  }

  public static DataFrame of(String n1, Vector v1, String n2, Vector v2, String n3, Vector v3) {
    return new MixedDataFrame(ImmutableMap.of(n1, v1, n2, v2, n3, v3));
  }

  public static DataFrame of(String n1, Vector v1, String n2, Vector v2, String n3, Vector v3,
      String n4, Vector v4) {
    return new MixedDataFrame(ImmutableMap.of(n1, v1, n2, v2, n3, v3, n4, v4));
  }

  public static DataFrame of(String n1, Vector v1, String n2, Vector v2, String n3, Vector v3,
      String n4, Vector v4, String n5, Vector v5) {
    return new MixedDataFrame(ImmutableMap.of(n1, v1, n2, v2, n3, v3, n4, v4, n5, v5));
  }

  public static MixedDataFrame read(DataInputStream io) throws IOException {
    return new MixedDataFrame.Builder(io.readColumnNames(), io.readColumnTypes()).read(io).build();
  }

  @Override
  public String getAsString(int row, int column) {
    return columns.get(column).getAsString(row);
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
  public Bit getAsBinary(int row, int column) {
    return columns.get(column).getAsBit(row);
  }

  @Override
  public Complex getAsComplex(int row, int column) {
    return columns.get(column).getAsComplex(row);
  }

  @Override
  public Value getAsValue(int row, int column) {
    return columns.get(column).getAsValue(row);
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
  public VectorType getColumnType(int index) {
    return columns.get(index).getType();
  }

  @Override
  public DataFrame takeColumns(Iterable<Integer> indexes) {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    for (int index : indexes) {
      builder.addColumn(getColumn(index));
      if (getColumnNames().containsKey(index)) {
        builder.getColumnNames().put(index, getColumnName(index));
      }
    }
    return builder.build();
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
    return new Builder(this, false);
  }

  @Override
  public Builder newBuilder(int rows) {
    return new Builder(this, rows, columns());
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(this, true);
  }

  @Override
  public Vector getColumn(int index) {
    return columns.get(index);
  }

  @Override
  public DataFrame dropColumn(int index) {
    checkArgument(index >= 0 && index < columns());
    ArrayList<Vector> columns = new ArrayList<>(this.columns);
    NameAttribute columnNames = new NameAttribute(this.columnNames);

    columnNames.remove(index);
    columns.remove(index);
    return new MixedDataFrame(columnNames, rowNames, columns, rows());
  }

  @Override
  public DataFrame dropColumns(Iterable<Integer> indexes) {
    Set<Integer> set = null;
    if (indexes instanceof Set) {
      set = (Set<Integer>) indexes;
    } else {
      set = Sets.newHashSet(indexes);
    }

    ArrayList<Vector> columns = new ArrayList<>();
    NameAttribute columnNames = new NameAttribute();

    int index = 0;
    for (int i = 0; i < columns(); i++) {
      if (!set.contains(i)) {
        columns.add(getColumn(i));
        String name = getColumnName(i);
        if (name != null) {
          columnNames.put(index, name);
        }
        index += 1;
      }
    }

    return new MixedDataFrame(columnNames, rowNames, columns, rows());
  }

  // @Override
  // public DataFrame takeColumns(Iterable<Integer> indexes) {
  // ArrayList<Vector> columns = new ArrayList<>();
  // NameAttribute columnNames = new NameAttribute();
  // for (Number number : indexes) {
  // int index = number.intValue();
  // checkArgument(index >= 0 && index < columns());
  // columns.add(getColumn(index));
  // String name = getColumnName(index);
  // if (name != null) {
  // columnNames.put(index, name);
  // }
  // }
  //
  // return new MixedDataFrame(columnNames, rowNames, columns, rows());
  // }

  @Override
  public String toString() {
    return DataFrames.toTabularString(this);
  }

  /**
   * <p>
   * Type for constructing a new MixedDataFrame. While for example,
   * {@link org.briljantframework.dataframe.MatrixDataFrame} and
   * {@link org.briljantframework.dataseries.DataSeriesCollection.Builder} can dynamically adapt the
   * number of columns in the constructed DataFrame, this builder can only construct DataFrames with
   * a fixed number of columns due to the fact that each column can be of different types.
   * </p>
   * 
   * <p>
   * To overcome this limitation, {@link #addColumn(org.briljantframework.vector.Vector.Builder)}
   * and {@link #removeColumn(int)} can be used.
   * </p>
   * 
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
     * Construct a builder with {@code types.size()} columns. The column names will be
     * {@code 1 ... types.length}
     * 
     * @param types the column types
     */
    public Builder(Collection<? extends VectorType> types) {
      buffers = new ArrayList<>(types.size());
      types.forEach(type -> buffers.add(type.newBuilder()));
    }

    /**
     * Construct a builder with {@code types.size()} columns with names from {@code colNames}.
     * Asserts that {@code colNames.size() == types.size()}
     * 
     * @param colNames the column names
     * @param types the types
     */
    public Builder(Collection<String> colNames, Collection<? extends VectorType> types) {
      checkArgument(colNames.size() > 0 && colNames.size() == types.size(),
          "Column names and types does not match.");
      this.buffers = new ArrayList<>(types.size());

      Iterator<String> it = colNames.iterator();
      int index = 0;
      for (VectorType type : types) {
        this.columnNames.put(index++, it.next());
        this.buffers.add(type.newBuilder());
      }
    }

    /**
     * <p>
     * Construct a builder using vector builders. Vector builders of different sizes are allowed,
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
     * @param copy copy values or only types
     */
    public Builder(DataFrame frame, boolean copy) {
      buffers = new ArrayList<>(frame.columns());

      for (int i = 0; i < frame.columns(); i++) {
        Vector vector = frame.getColumn(i);
        if (copy) {
          buffers.add(vector.newCopyBuilder());
        } else {
          buffers.add(vector.newBuilder());
        }
        String name = frame.getColumnName(i);
        if (name != null) {
          columnNames.put(i, name);
        }
      }
    }

    private Builder(MixedDataFrame frame, int rows, int columns) {
      buffers = new ArrayList<>(columns);
      for (int i = 0; i < columns; i++) {
        buffers.add(frame.getColumn(i).newBuilder(rows));
      }
    }

    @Override
    public Builder setNA(int row, int column) {
      buffers.get(column).setNA(row);
      return this;
    }

    @Override
    public Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol) {
      buffers.get(toCol).set(toRow, from.getColumn(fromCol), fromRow);
      return this;
    }

    @Override
    public Builder set(int row, int column, Vector from, int index) {
      buffers.get(column).set(row, from, index);
      return this;
    }

    @Override
    public Builder set(int row, int column, Object value) {
      buffers.get(column).set(row, value);
      return this;
    }

    @Override
    public Builder removeColumn(int column) {
      columnNames.remove(column);
      buffers.remove(column);
      return this;
    }

    @Override
    public Builder swapColumns(int a, int b) {
      columnNames.swap(a, b);
      Collections.swap(buffers, a, b);
      return this;
    }

    @Override
    public Builder swapInColumn(int column, int a, int b) {
      buffers.get(column).swap(a, b);
      return this;
    }

    @Override
    public Builder read(DataInputStream inputStream) throws IOException {
      while (inputStream.hasNext()) {
        DataEntry entry = inputStream.next();
        for (int i = 0; i < entry.size(); i++) {
          buffers.get(i).read(entry);
        }
      }

      return this;
    }

    @Override
    public DataFrame.Builder setColumn(int index, Vector.Builder builder) {
      this.buffers.set(index, builder);
      return this;
    }

    @Override
    public DataFrame.Builder setColumn(int index, Vector vector) {
      return setColumn(index, vector.newCopyBuilder());
    }

    @Override
    public DataFrame.Builder addColumn(Vector vector) {
      buffers.add(vector.newCopyBuilder());
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
      return new MixedDataFrame(columnNames, rowNames, vectors, rows);
    }

    @Override
    public Builder addColumn(Vector.Builder builder) {
      buffers.add(builder);
      return this;
    }

    private Vector padVectorWithNA(Vector.Builder builder, int maximumRows) {
      if (builder.size() < maximumRows) {
        builder.setNA(maximumRows - 1);
      }
      return builder.build();
    }
  }
}
