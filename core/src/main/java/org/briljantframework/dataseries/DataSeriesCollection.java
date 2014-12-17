package org.briljantframework.dataseries;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.briljantframework.dataframe.AbstractDataFrame;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.exceptions.TypeMismatchException;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.vector.*;

import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;

/**
 * <p>
 * A DataSeries collection is collection of data series, i.e., vectors of the same type - usually
 * {@link org.briljantframework.vector.DoubleVector#TYPE}. There are some interesting differences
 * between this implementation and the traditional {@code DataFrame}. It is possible for the data
 * series in the collection to be of different length. Therefore, {@link #columns()} return the
 * maximum data series length and calls to {@code getAs...(n, col)} works as expected only if
 * {@code col < coll.getRow(n).size()}. If not (and {@code index < columns()}), NA is returned.
 * </p>
 *
 * @author Isak Karlsson
 */
public class DataSeriesCollection extends AbstractDataFrame {

  private final IntObjectMap<String> colNames = new IntObjectOpenHashMap<>();
  private final List<Vector> series;
  private final Type type;

  private final int columns;

  public DataSeriesCollection(List<Vector> series, Type type) {
    this(series, type, series.stream().mapToInt(Vector::size).max()
        .orElseThrow(IllegalArgumentException::new));
  }

  protected DataSeriesCollection(List<Vector> series, Type type, int columns) {
    this.type = checkNotNull(type);
    this.series = checkNotNull(series);
    this.columns = columns;
  }

  @Override
  public String getAsString(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.getAsString(column);
    } else if (column >= 0 && column < columns) {
      return StringVector.NA;
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
      return DoubleVector.NA;
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
      return IntVector.NA;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public Binary getAsBinary(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.getAsBinary(column);
    } else if (column >= 0 && column < columns) {
      return BinaryVector.NA;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public Complex getAsComplex(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.getAsComplex(column);
    } else if (column >= 0 && column < columns) {
      return ComplexVector.NA;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public Value getAsValue(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.getAsValue(column);
    } else if (column >= 0 && column < columns) {
      return VariableVector.NA;
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
  public Builder newBuilder(int rows) {
    return new Builder(type);
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(series.stream().map(Vector::newCopyBuilder)
        .collect(Collectors.toCollection(ArrayList::new)), type);
  }

  @Override
  public DataSeries getRow(int index) {
    return new DataSeries(series.get(index));
  }

  public static class Builder extends AbstractBuilder {

    private final Type type;
    private final List<Vector.Builder> builders;

    public Builder(Type type) {
      this(new ArrayList<>(), type);
    }

    protected Builder(List<Vector.Builder> builders, Type type) {
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
      builders.get(toRow).set(toCol, from.getRow(fromRow), fromCol);
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
      checkArgument(column >= 0 && column < columns());

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
    public Builder swapInColumn(int column, int a, int b) {
      Vector.Builder avec = builders.get(a);
      Vector.Builder bvec = builders.get(b);
      // TODO: How can this be supported?
      throw new UnsupportedOperationException();
    }

    @Override
    public DataFrame.Builder swapRows(int a, int b) {
      Collections.swap(builders, a, b);
      return this;
    }

    @Override
    public Builder read(DataInputStream inputStream) throws IOException {
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
      if (vector.getType() != type) {
        throw new TypeMismatchException(type, vector.getType());
      }
      return addRow(vector.newCopyBuilder());
    }

    private void ensureCapacity(int row) {
      while (row >= builders.size()) {
        builders.add(type.newBuilder());
      }
    }
  }
}
