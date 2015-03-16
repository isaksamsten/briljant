package org.briljantframework.dataseries;

import com.google.common.collect.Sets;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.dataframe.AbstractDataFrame;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.NameAttribute;
import org.briljantframework.dataframe.Record;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.BitVector;
import org.briljantframework.vector.ComplexVector;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.VariableVector;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;
import org.briljantframework.vector.Vectors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p> A DataSeries collection is collection of data series, i.e., vectors of the same type -
 * usually {@link org.briljantframework.vector.DoubleVector#TYPE}. There are some interesting
 * differences between this implementation and the traditional {@code DataFrame}. It is possible for
 * the data series in the collection to be of different length. Therefore, {@link #columns()} return
 * the maximum data series length and calls to {@code getAs...(n, col)} works as expected only if
 * {@code col < coll.getRow(n).size()}. If not (and {@code index < columns()}), NA is returned.
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

  protected DataSeriesCollection(NameAttribute columnNames, NameAttribute rowNames,
                                 List<Vector> series, VectorType type, int columns) {
    super(columnNames, rowNames);
    this.type = checkNotNull(type);
    this.series = checkNotNull(series);
    this.columns = columns;
  }

  @Override
  public Value get(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.get(column);
    } else if (column >= 0 && column < columns) {
      return VariableVector.NA;
    } else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public <T> T get(Class<T> cls, int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.get(cls, column);
    } else if (column >= 0 && column < columns) {
      return Vectors.naValue(cls);
    } else {
      throw new IndexOutOfBoundsException();
    }
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
  public Bit getAsBit(int row, int column) {
    Vector rvec = series.get(row);
    if (column >= 0 && column < rvec.size()) {
      return rvec.getAsBit(column);
    } else if (column >= 0 && column < columns) {
      return BitVector.NA;
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
  public VectorType getColumnType(int index) {
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
    return new Builder(columnNames, rowNames, series.stream().map(Vector::newCopyBuilder)
        .collect(Collectors.toCollection(ArrayList::new)), type);
  }

  @Override
  public DataFrame removeColumns(Iterable<Integer> indexes) {
    Set<Integer> set = Sets.newHashSet(indexes);
    Builder builder = newBuilder();
    builder.getColumnNames().putAll(getColumnNames());
    for (int i = 0; i < rows(); i++) {
      Vector row = getRecord(i);
      Vector.Builder vecBuilder = row.newBuilder();
      for (int j = 0; j < row.size(); j++) {
        if (!set.contains(j)) {
          vecBuilder.add(row, j);
        } else {
          builder.getColumnNames().remove(j);
        }
      }
      builder.addRecord(vecBuilder);
    }
    return builder.build();
  }

  @Override
  public DataSeries getRecord(int index) {
    return new DataSeries(series.get(index));
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

    public Builder(NameAttribute columnNames, NameAttribute rowNames, VectorType type) {
      super(columnNames, rowNames);
      this.type = type;
      this.builders = new ArrayList<>();
    }

    public Builder(VectorType type) {
      this.type = type;
      this.builders = new ArrayList<>();
    }

    protected Builder(NameAttribute columnNames, NameAttribute rowNames,
                      List<Vector.Builder> builders, VectorType type) {
      super(columnNames, rowNames);
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
      Record row = from.getRecord(fromRow);
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
      checkElementIndex(column, columns());
      columnNames.remove(column);
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
    public Builder read(DataInputStream is) throws IOException {
      int row = 0;
      while (is.hasNext()) {
        ensureCapacity(row);
        DataEntry entry = is.next();
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
      return new DataSeriesCollection(columnNames, rowNames, builders.stream()
          .map(Vector.Builder::build).collect(Collectors.toCollection(ArrayList::new)), type,
                                      columns());
    }

    public Builder removeRow(int index) {
      checkElementIndex(index, rows());
      rowNames.remove(index);
      builders.remove(index);
      return this;
    }

    @Override
    public Builder addRecord(Vector.Builder row) {
      builders.add(row);
      return this;
    }

    @Override
    public Builder addRecord(Vector vector) {
      Check.requireType(type, vector);
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
      rowNames.swap(a, b);
      return this;
    }

    private void ensureCapacity(int row) {
      while (row >= builders.size()) {
        builders.add(type.newBuilder());
      }
    }
  }
}
