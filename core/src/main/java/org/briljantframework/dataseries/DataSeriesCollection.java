package org.briljantframework.dataseries;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrameColumnView;
import org.briljantframework.dataframe.DataFrameRow;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.DataFrameInputStream;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.*;

import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.google.common.collect.UnmodifiableIterator;

/**
 * <p>
 * A DataSeries collection is collection of data series, i.e., vectors of the same type - usually
 * {@link org.briljantframework.vector.DoubleVector#TYPE}. There are some interesting differences
 * between this implementation and the traditional {@code DataFrame}. It is possible for the data
 * series in the collection to be of different length. Therefore, {@link #columns()} return the
 * maximum data series length and calls to {@code getAs...(n, col)} works as expected only if
 * {@code col < coll.getRow(n).size()}. If not, NA is returned.
 * </p>
 * 
 * @author Isak Karlsson
 */
public class DataSeriesCollection implements DataFrame {

  private final IntObjectMap<String> colNames = new IntObjectOpenHashMap<>();
  private final List<Vector> series;
  private final Type type;

  private final int maxColumns;

  public DataSeriesCollection(List<Vector> series, Type type) {
    this(series, type, series.stream().mapToInt(Vector::size).max()
        .orElseThrow(IllegalArgumentException::new));
  }

  protected DataSeriesCollection(List<Vector> series, Type type, int maxColumns) {
    this.type = checkNotNull(type);
    this.series = checkNotNull(series);
    this.maxColumns = maxColumns;
  }

  @Override
  public String getAsString(int row, int column) {
    Vector rvec = series.get(row);
    if (column < rvec.size()) {
      return rvec.getAsString(column);
    } else if (column < maxColumns) {
      return StringVector.NA;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public double getAsDouble(int row, int column) {
    Vector rvec = series.get(row);
    if (column < rvec.size()) {
      return rvec.getAsDouble(column);
    } else if (column < maxColumns) {
      return DoubleVector.NA;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public int getAsInt(int row, int column) {
    Vector rvec = series.get(row);
    if (column < rvec.size()) {
      return rvec.getAsInt(column);
    } else if (column < maxColumns) {
      return IntVector.NA;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public Binary getAsBinary(int row, int column) {
    Vector rvec = series.get(row);
    if (column < rvec.size()) {
      return rvec.getAsBinary(column);
    } else if (column < maxColumns) {
      return BinaryVector.NA;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public Complex getAsComplex(int row, int column) {
    Vector rvec = series.get(row);
    if (column < rvec.size()) {
      return rvec.getAsComplex(column);
    } else if (column < maxColumns) {
      return ComplexVector.NA;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public Value getAsValue(int row, int column) {
    Vector rvec = series.get(row);
    if (column < rvec.size()) {
      return rvec.getAsValue(column);
    } else if (column < maxColumns) {
      return VariableVector.NA;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public String toString(int row, int column) {
    Vector rvec = series.get(row);
    if (column < rvec.size()) {
      return rvec.toString(column);
    } else if (column < maxColumns) {
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
  public Vector getColumn(int index) {
    return new DataFrameColumnView(this, index);
  }

  @Override
  public DataFrame dropColumn(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DataFrame dropColumns(Set<Integer> indexes) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DataFrame takeColumns(Set<Integer> indexes) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Type getColumnType(int index) {
    return type;
  }

  @Override
  public String getColumnName(int index) {
    String name = colNames.get(index);
    return name == null ? String.valueOf(index) : name;
  }

  @Override
  public DataFrame setColumnName(int index, String columnName) {
    colNames.put(index, columnName);
    return this;
  }

  @Override
  public DataFrameRow getRow(int index) {
    return new DataSeries(series.get(index));
  }

  @Override
  public DataFrame takeRows(Set<Integer> indexes) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DataFrame dropRows(Set<Integer> indexes) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int rows() {
    return series.size();
  }

  @Override
  public int columns() {
    return maxColumns;
  }

  @Override
  public Builder newBuilder() {
    return new Builder(type);
  }

  @Override
  public Builder newBuilder(int rows) {
    return new Builder(type);
  }

  @Override
  public Builder newCopyBuilder() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Matrix asMatrix() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return DataFrames.toTabularString(this);
  }

  @Override
  public Iterator<DataFrameRow> iterator() {
    return new UnmodifiableIterator<DataFrameRow>() {
      int current = 0;

      @Override
      public boolean hasNext() {
        return current < rows();
      }

      @Override
      public DataFrameRow next() {
        return getRow(current++);
      }
    };
  }

  public static class Builder implements DataFrame.Builder {

    private final Type type;
    private final List<Vector.Builder> builders;


    public Builder(Type type) {
      this.type = type;
      this.builders = new ArrayList<>();
    }

    @Override
    public Builder setNA(int row, int column) {
      ensureCapacity(row);
      builders.get(row).setNA(column);
      return this;
    }

    @Override
    public Builder addNA(int column) {
      return setNA(rows(), column);
    }

    @Override
    public Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol) {
      ensureCapacity(toRow);
      builders.get(toRow).set(toCol, from.getRow(fromRow), fromCol);
      return this;
    }

    @Override
    public Builder add(int toCol, DataFrame from, int fromRow, int fromCol) {
      return set(rows(), toCol, from, fromRow, fromCol);
    }

    @Override
    public Builder add(int toCol, Vector from, int fromRow) {
      ensureCapacity(rows());
      builders.get(rows()).set(toCol, from, fromRow);
      return this;
    }

    @Override
    public Builder set(int row, int column, Object value) {
      ensureCapacity(row);
      builders.get(row).set(column, value);
      return this;
    }

    @Override
    public Builder add(int col, Object value) {
      return set(rows(), col, value);
    }

    @Override
    public Builder addColumn(Vector.Builder builder) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Builder removeColumn(int column) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Builder swapColumns(int a, int b) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Builder swapInColumn(int column, int a, int b) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Builder read(DataFrameInputStream inputStream) throws IOException {
      int row = 0;
      while (inputStream.hasNext()) {
        ensureCapacity(row);
        DataEntry entry = inputStream.next();
        for (int i = 0; i < entry.size() && entry.hasNext(); i++) {
          builders.get(row).read(i, entry);
        }
        row++;
      }
      return this;
    }

    @Override
    public int columns() {
      return builders.stream().mapToInt(Vector.Builder::size).max()
          .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public int rows() {
      return builders.size();
    }

    @Override
    public DataSeriesCollection build() {
      return new DataSeriesCollection(builders.stream().map(Vector.Builder::build)
          .collect(Collectors.toCollection(ArrayList::new)), type);
    }

    public Builder addRow(Vector.Builder row) {
      builders.add(row);
      return this;
    }

    public Builder addRow(Vector vector) {
      return addRow(vector.newCopyBuilder());
    }

    private void ensureCapacity(int row) {
      while (row >= builders.size()) {
        builders.add(type.newBuilder());
      }
    }
  }
}
