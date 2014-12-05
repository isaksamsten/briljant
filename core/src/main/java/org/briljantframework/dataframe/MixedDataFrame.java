package org.briljantframework.dataframe;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.briljantframework.io.DataFrameInputStream;
import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.*;
import org.briljantframework.vector.Vector;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

/**
 * A mixed (i.e. heterogeneous) data frame contains vectors of possibly different types.
 * <p>
 * Created by Isak Karlsson on 21/11/14.
 */
public class MixedDataFrame implements DataFrame {

  private final List<String> names;
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

    this.names = Lists.newArrayList("0");
    this.columns = new ArrayList<>(vectors.size());
    int rows = 0, index = 0;
    for (Vector vector : vectors) {
      if (rows == 0) {
        rows = vector.size();
      }
      checkArgument(vector.size() == rows, "Arguments imply different numbers of rows: %s, %s.",
          rows, vector.size());
      this.columns.add(vector);
      this.names.add(String.valueOf(index++));
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
    this.names = new ArrayList<>(vectors.size());
    this.columns = new ArrayList<>(vectors.size());

    int rows = 0;
    for (Map.Entry<String, ? extends Vector> kv : vectors.entrySet()) {
      Vector vector = kv.getValue();
      String name = kv.getKey();
      if (rows == 0) {
        rows = vector.size();
      }

      checkArgument(vector.size() == rows, "Arguments imply different numbers of rows: %s, %s.",
          rows, vector.size());
      this.names.add(name);
      this.columns.add(vector);
    }
    this.rows = rows;
  }

  /**
   * Constructs a new mixed data frame from an iterable sequence of of
   * {@link org.briljantframework.vector.CompoundVector} treated as rows of equal length
   * 
   * @param sequences
   */
  public MixedDataFrame(Iterable<? extends CompoundVector> sequences) {
    this.names = new ArrayList<>();
    this.columns = new ArrayList<>();

    List<Vector.Builder> builders = new ArrayList<>();
    int columns = 0, rows = 0;
    for (CompoundVector row : sequences) {
      if (columns == 0) {
        columns = row.size();
      }
      checkArgument(row.size() == columns, "Arguments imply different numbers of rows: %s, %s.",
          columns, row.size());
      for (int i = 0; i < row.size(); i++) {
        if (builders.size() <= i) {
          checkArgument(row.getType(i) != CompoundVector.TYPE,
              "Can't create untyped vector as column.");
          builders.add(row.getType(i).newBuilder());
        }
        builders.get(i).add(row.getAsValue(i));
      }
      rows++;
    }
    int index = 0;
    for (Vector.Builder builder : builders) {
      this.names.add(String.valueOf(index++));
      this.columns.add(builder.build());
    }

    this.rows = rows;
  }

  /**
   * Unsafe construction of a mixed data frame. Performs no sanity checking (should only be used for
   * performance by checked builder).
   *
   * @param names the names
   * @param vectors the vectors
   * @param rows the expected size of the vectors (not checked but should be enforced)
   */
  protected MixedDataFrame(List<String> names, List<Vector> vectors, int rows) {
    checkArgument(names.size() == vectors.size());
    this.names = names;
    this.columns = vectors;
    this.rows = rows;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsString(int row, int column) {
    return columns.get(column).getAsString(row);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getAsDouble(int row, int column) {
    return columns.get(column).getAsReal(row);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getAsInt(int row, int column) {
    return columns.get(column).getAsInt(row);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Binary getAsBinary(int row, int column) {
    return columns.get(column).getAsBinary(row);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Complex getAsComplex(int row, int column) {
    return columns.get(column).getAsComplex(row);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Value getAsValue(int row, int column) {
    return columns.get(column).getAsValue(row);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString(int row, int column) {
    return columns.get(column).toString(row);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isNA(int row, int column) {
    return columns.get(column).isNA(row);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Vector getColumn(int index) {
    return columns.get(index);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DataFrame dropColumn(int index) {
    checkArgument(index >= 0 && index < columns());
    ArrayList<Vector> columns = new ArrayList<>(this.columns);
    ArrayList<String> names = new ArrayList<>(this.names);

    columns.remove(index);
    names.remove(index);
    return new MixedDataFrame(names, columns, rows());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DataFrame dropColumns(Set<Integer> indexes) {
    ArrayList<Vector> columns = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    for (int i = 0; i < columns(); i++) {
      if (!indexes.contains(i)) {
        columns.add(getColumn(i));
        names.add(getColumnName(i));
      }
    }

    return new MixedDataFrame(names, columns, rows());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DataFrame takeColumns(Set<Integer> indexes) {
    ArrayList<Vector> columns = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    for (int index : indexes) {
      checkArgument(index >= 0 && index < columns());
      columns.add(getColumn(index));
      names.add(getColumnName(index));
    }

    return new MixedDataFrame(names, columns, rows());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Type getColumnType(int index) {
    return columns.get(index).getType();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnName(int index) {
    return names.get(index);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CompoundVector getRow(int index) {
    return new MixedDataFrameRow(this, index);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DataFrame takeRows(Set<Integer> indexes) {
    DataFrame.Builder builder = newBuilder();
    for (int i : indexes) {
      for (int j = 0; j < columns(); j++) {
        builder.add(j, this, i, j);
      }
    }

    return builder.build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DataFrame dropRows(Set<Integer> indexes) {
    DataFrame.Builder builder = newBuilder();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        if (!indexes.contains(i)) {
          builder.add(j, this, i, j);
        }
      }
    }

    return builder.build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int rows() {
    return rows;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int columns() {
    return columns.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Builder newBuilder() {
    return new Builder(this, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Builder newBuilder(int rows) {
    return new Builder(this, rows, columns());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Builder newCopyBuilder() {
    return new Builder(this, true);
  }

  @Override
  public Matrix asMatrix() {
    Matrix matrix = new ArrayMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(i, j, getAsDouble(i, j));
      }
    }

    return matrix;
  }

  @Override
  public String toString() {
    return DataFrames.toTabularString(this, 100);
  }

  @Override
  public Iterator<CompoundVector> iterator() {
    return new UnmodifiableIterator<CompoundVector>() {
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < rows();
      }

      @Override
      public CompoundVector next() {
        return getRow(index++);
      }
    };
  }

  /**
   * Type for constructing a new MixedDataFrame by mutation.
   */
  public static class Builder implements DataFrame.Builder {

    private List<Vector.Builder> buffers = null;
    private List<String> colNames = null;

    public Builder(Type... types) {
      this(Arrays.asList(types));
    }

    public Builder(Collection<? extends Type> types) {
      buffers = new ArrayList<>(types.size());
      colNames = new ArrayList<>(types.size());
      int index = 0;
      for (Type type : types) {
        colNames.add(String.valueOf(index++));
        buffers.add(type.newBuilder());
      }
    }

    public Builder(Collection<String> colNames, Collection<? extends Type> types) {
      checkArgument(colNames.size() > 0 && colNames.size() == types.size(),
          "Column names and types does not match.");
      this.buffers = new ArrayList<>(types.size());
      this.colNames = new ArrayList<>(colNames);
      for (Type type : types) {
        buffers.add(type.newBuilder());
      }
    }

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

    protected Builder(MixedDataFrame frame, boolean copy) {
      buffers = new ArrayList<>(frame.columns());
      colNames = new ArrayList<>(frame.columns());

      ArrayList<Vector> columns = new ArrayList<>(frame.columns);
      for (int i = 0; i < columns.size(); i++) {
        Vector vector = columns.get(i);
        if (copy) {
          buffers.add(vector.newCopyBuilder());
        } else {
          buffers.add(vector.newBuilder());
        }
        colNames.add(frame.getColumnName(i));
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
    public Builder addNA(int column) {
      buffers.get(column).addNA();
      return this;
    }

    @Override
    public Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol) {
      buffers.get(toCol).set(toRow, from.getColumn(fromCol), fromRow);
      return this;
    }

    @Override
    public Builder add(int toCol, DataFrame from, int fromRow, int fromCol) {
      buffers.get(toCol).add(from.getColumn(fromCol), fromRow);
      return this;
    }

    @Override
    public Builder add(int toCol, Vector from, int fromRow) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Builder set(int row, int column, Object value) {
      buffers.get(column).set(row, value);
      return this;
    }

    @Override
    public DataFrame.Builder add(int col, Object value) {
      buffers.get(col).add(value);
      return this;
    }

    @Override
    public Builder addColumn(Vector.Builder builder) {
      if (colNames != null) {
        colNames.add(String.valueOf(colNames.size()));
      }
      buffers.add(builder);
      return this;
    }

    @Override
    public Builder removeColumn(int column) {
      if (colNames != null) {
        colNames.remove(column);
      }
      buffers.remove(column);
      return this;
    }

    @Override
    public Builder swapColumns(int a, int b) {
      if (colNames != null) {
        Collections.swap(colNames, a, b);
      }
      Collections.swap(buffers, a, b);
      return this;
    }

    @Override
    public DataFrame.Builder swapInColumn(int column, int a, int b) {
      buffers.get(column).swap(a, b);
      return this;
    }

    @Override
    public DataFrame.Builder read(DataFrameInputStream inputStream) throws IOException {
      while (inputStream.hasNext()) {
        for (int i = 0; i < columns() && inputStream.hasNext(); i++) {
          buffers.get(i).read(inputStream);
        }
      }

      return this;
    }

    @Override
    public int columns() {
      return buffers.size();
    }

    @Override
    public int rows() {
      return buffers.stream().mapToInt(Vector.Builder::size).reduce(0, Integer::max);
    }

    @Override
    public DataFrame build() {
      int rows = rows();
      List<Vector> vectors =
          buffers.stream().map(x -> padVectorWithNA(x, rows))
              .collect(Collectors.toCollection(ArrayList::new));
      buffers = null;
      return new MixedDataFrame(colNames, vectors, rows);
    }

    private Vector padVectorWithNA(Vector.Builder builder, int maximumRows) {
      if (builder.size() < maximumRows) {
        builder.setNA(maximumRows - 1);
      }
      return builder.build();
    }
  }
}
