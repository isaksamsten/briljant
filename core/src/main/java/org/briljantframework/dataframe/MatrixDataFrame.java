package org.briljantframework.dataframe;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;

import java.io.IOException;
import java.util.*;

import org.briljantframework.ArrayBuffers;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.*;
import org.briljantframework.vector.Vector;

import com.carrotsearch.hppc.IntDoubleMap;
import com.carrotsearch.hppc.IntDoubleOpenHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Initial implementation of the matrix data frame.
 * 
 * @author Isak Karlsson
 */
public class MatrixDataFrame implements DataFrame {

  private final List<String> colNames;
  private final Matrix matrix;

  public MatrixDataFrame(Matrix matrix) {
    this.matrix = matrix;
    this.colNames = new ArrayList<>(matrix.columns());
    for (int i = 0; i < matrix.columns(); i++) {
      colNames.add(String.valueOf(i));
    }
  }

  protected MatrixDataFrame(Matrix matrix, List<String> colNames) {
    this(matrix, colNames, true);
  }

  protected MatrixDataFrame(Matrix matrix, List<String> colNames, boolean copy) {
    Preconditions.checkArgument(matrix.columns() <= colNames.size());
    this.matrix = matrix;
    if (matrix.columns() == colNames.size()) {
      if (copy) {
        this.colNames = new ArrayList<>(colNames);
      } else {
        this.colNames = colNames;
      }
    } else {
      this.colNames = new ArrayList<>(colNames.size());
      for (int i = 0; i < columns(); i++) {
        this.colNames.add(colNames.get(i));
      }
    }
  }

  @Override
  public String getAsString(int row, int column) {
    double value = matrix.get(row, column);
    return Double.isNaN(value) ? StringVector.NA : String.valueOf(value);
  }

  @Override
  public double getAsDouble(int row, int column) {
    return matrix.get(row, column);
  }

  @Override
  public int getAsInt(int row, int column) {
    return (int) matrix.get(row, column);
  }

  @Override
  public Binary getAsBinary(int row, int column) {
    double value = matrix.get(row, column);
    return Is.NA(value) ? Binary.NA : value == 1 ? Binary.TRUE : Binary.FALSE;
  }

  @Override
  public Complex getAsComplex(int row, int column) {
    double value = matrix.get(row, column);
    return Is.NA(value) ? Complex.NaN : new Complex(value);
  }

  @Override
  public Value getAsValue(int row, int column) {
    double value = matrix.get(row, column);
    return Is.NA(value) ? Undefined.INSTANCE : new DoubleValue(value);
  }

  @Override
  public String toString(int row, int column) {
    double value = matrix.get(row, column);
    return Is.NA(value) ? "NA" : String.valueOf(value);
  }

  @Override
  public boolean isNA(int row, int column) {
    return Is.NA(matrix.get(row, column));
  }

  @Override
  public Vector getColumn(int index) {
    return new MatrixVector(matrix.getColumnView(index));
  }

  @Override
  public DataFrame dropColumn(int index) {
    checkElementIndex(index, columns(), "Column-index out of bounds.");
    List<String> colNames = new ArrayList<>(this.colNames);
    colNames.remove(index);

    Matrix newMatrix = matrix.newEmptyMatrix(rows(), columns() - 1);
    int j = 0;
    for (int k = 0; k < matrix.columns(); k++) {
      if (k != index) {
        for (int i = 0; i < matrix.rows(); i++) {
          newMatrix.put(i, j, matrix.get(i, k));
        }
        j++;
      }
    }

    return new MatrixDataFrame(newMatrix, colNames, false);
  }

  @Override
  public DataFrame dropColumns(Set<Integer> indexes) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  @Override
  public DataFrame takeColumns(Set<Integer> indexes) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  @Override
  public Type getColumnType(int index) {
    checkArgument(index >= 0 && index < columns());
    return DoubleVector.TYPE;
  }

  @Override
  public String getColumnName(int index) {
    return colNames.get(index);
  }

  @Override
  public DataFrame setColumnName(int index, String columnName) {
    colNames.set(index, columnName);
    return this;
  }

  @Override
  public DataFrameRow getRow(int index) {
    return new DataFrameRowView(this, index, DoubleVector.TYPE);
  }

  @Override
  public DataFrame takeRows(Set<Integer> indexes) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  @Override
  public DataFrame dropRows(Set<Integer> indexes) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  @Override
  public int rows() {
    return matrix.rows();
  }

  @Override
  public int columns() {
    return matrix.columns();
  }

  @Override
  public DataFrame.Builder newBuilder() {
    return new HashBuilder(colNames, null);
  }

  @Override
  public DataFrame.Builder newBuilder(int rows) {
    return new ArrayBuilder(rows, colNames);
  }

  @Override
  public DataFrame.Builder newCopyBuilder() {
    double[] array = new double[rows() * columns()];
    System.arraycopy(matrix.asDoubleArray(), 0, array, 0, array.length);
    return new ArrayBuilder(rows(), colNames, array);

  }

  @Override
  public Matrix asMatrix() {
    return matrix;
  }

  @Override
  public Iterator<DataFrameRow> iterator() {
    return new UnmodifiableIterator<DataFrameRow>() {
      private int current = 0;

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

  @Override
  public String toString() {
    return DataFrames.toTabularString(this);
  }

  /**
   * Dynamically allocates a new {@code MatrixDataFrame}.
   * <p>
   * Appending and increasing the size is rather costly (due to reallocation) using this builder. It
   * is therefore recommended to initialize with a size. If linearly adding elements, it's way
   * faster to append backwards, i.e., starting with the bottom right and proceed to the top left
   * corner of the matrix
   * <p>
   * 
   * <pre>
   *     for(int i = 100; i >= 0; i--)
   *       for(int j = 100; i >= 0; j--)
   *         builder.set(i, j, ....);
   * </pre>
   * <p>
   * is faster than starting at {@code i = 0} and {@code j = 0}.
   * <p>
   * Alternatively, if the size is unknown prefer
   * {@link org.briljantframework.dataframe.MatrixDataFrame.HashBuilder}, which is both sparse and
   * fast to incrementally build.
   */
  public static class ArrayBuilder implements DataFrame.Builder {

    private static final int MIN_CAPACITY = 50;
    private final List<String> colNames;
    private double[] buffer;
    private int rows, columns;

    public ArrayBuilder(int rows, int columns) {
      this.rows = rows;
      this.columns = columns;
      this.colNames = null;
      buffer = new double[rows * columns];
      Arrays.fill(buffer, DoubleVector.NA);
    }

    public ArrayBuilder(int initialCapacity) {
      this.rows = 0;
      this.columns = 0;
      this.colNames = null;
      buffer = new double[] {DoubleVector.NA};
    }

    public ArrayBuilder() {
      this(MIN_CAPACITY);
    }

    public ArrayBuilder(Collection<String> colNames, Collection<? extends Type> colTypes) {
      checkArgument(colTypes.size() == colNames.size());
      this.colNames = new ArrayList<>(colNames);
      this.rows = 0;
      this.columns = colNames.size();
      buffer = new double[0];
    }

    protected ArrayBuilder(int rows, List<String> colNames, double[] buffer) {
      this.rows = rows;
      this.columns = colNames.size();
      this.colNames = colNames;
      this.buffer = buffer;
    }

    public ArrayBuilder(int rows, List<String> colNames) {
      this.rows = rows;
      this.colNames = colNames;
      this.columns = colNames.size();
      buffer = new double[rows * columns];
    }

    @Override
    public DataFrame.Builder setNA(int row, int column) {
      set(row, column, DoubleVector.NA);
      return this;
    }

    private int ensureCapacity(int row, int column) {
      if (row >= rows && column >= columns) {
        reInitializeBuffer(row + 1, column + 1);
      } else if (row >= rows) {
        reInitializeBuffer(row + 1, columns);
      } else if (column >= columns) {
        reInitializeBuffer(rows, column + 1);
      } else {
        // buffer = ArrayBuffers.ensureCapacity(buffer, rows * columns);
      }

      return Indexer.columnMajor(row, column, rows, columns);

    }

    private void reInitializeBuffer(int rows, int columns) {
      double[] tmp = ArrayBuffers.reallocate(buffer, rows * columns);
      Arrays.fill(tmp, DoubleVector.NA); // TODO: improve..
      for (int j = 0; j < this.columns; j++) {
        for (int i = 0; i < this.rows; i++) {
          int newIndex = Indexer.columnMajor(i, j, rows, columns);
          int oldIndex = Indexer.columnMajor(i, j, this.rows, this.columns);
          double oldVal = buffer[oldIndex];
          tmp[newIndex] = oldVal;
        }
      }
      this.rows = rows;
      this.columns = columns;
      this.buffer = tmp;
    }

    private DataFrame.Builder set(int row, int column, double dval) {
      int index = ensureCapacity(row, column);
      buffer[index] = dval;
      return this;
    }

    @Override
    public DataFrame.Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol) {
      return set(toRow, toCol, from.getAsDouble(fromRow, fromCol));
    }

    @Override
    public DataFrame.Builder set(int toRow, int toCol, Vector from, int fromRow) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DataFrame.Builder set(int row, int column, Object value) {
      double dval;
      if (value instanceof Value) {
        dval = ((Value) value).getAsDouble();
      } else if (value instanceof Number) {
        dval = ((Number) value).doubleValue();
      } else {
        dval = DoubleVector.NA;
      }
      return set(row, column, dval);
    }

    @Override
    public DataFrame.Builder addColumn(Vector.Builder builder) {
      throw new UnsupportedOperationException("Can't add builder");
    }

    @Override
    public DataFrame.Builder removeColumn(int column) {
      double[] tmp = ArrayBuffers.reallocate(buffer, rows * (columns - 1));
      int newColumns = this.columns - 1;
      for (int j = 0; j < this.columns; j++) {
        for (int i = 0; i < this.rows; i++) {
          if (j != column) {
            tmp[Indexer.columnMajor(i, j, rows, newColumns)] =
                buffer[Indexer.columnMajor(i, j, rows, columns)];
          }
        }
      }
      this.columns = newColumns;
      this.buffer = tmp;
      return this;
    }

    @Override
    public DataFrame.Builder swapColumns(int a, int b) {
      for (int i = 0; i < rows(); i++) {
        int oldIndex = Indexer.columnMajor(i, a, rows(), columns());
        int newIndex = Indexer.columnMajor(i, b, rows(), columns());
        double tmp = buffer[oldIndex];
        buffer[oldIndex] = buffer[newIndex];
        buffer[newIndex] = tmp;
      }
      return this;
    }

    @Override
    public DataFrame.Builder swapInColumn(int column, int a, int b) {
      checkArgument(column >= 0 && column < columns());
      checkArgument(a >= 0 && a < rows() && b >= 0 && b < rows());

      int oldIndex = Indexer.columnMajor(a, column, rows(), columns());
      int newIndex = Indexer.columnMajor(b, column, rows(), columns());

      double tmp = buffer[oldIndex];
      buffer[oldIndex] = buffer[newIndex];
      buffer[newIndex] = tmp;
      return this;
    }

    @Override
    public DataFrame.Builder read(DataInputStream inputStream) throws IOException {
      int row = 0;
      while (inputStream.hasNext()) {
        DataEntry entry = inputStream.next();
        for (int i = 0; i < entry.size(); i++) {
          set(row, i, entry.nextDouble());
        }
        row++;
      }
      return this;
    }

    @Override
    public int columns() {
      return columns;
    }

    @Override
    public int rows() {
      return rows;
    }

    @Override
    public DataFrame build() {
      ArrayMatrix mat = new ArrayMatrix(rows, columns, buffer);
      if (colNames != null) {
        return new MatrixDataFrame(mat, colNames, false);
      } else {
        return new MatrixDataFrame(mat);
      }
    }
  }

  /**
   * TODO(isak): Ensure that HashBuilder, ArrayBuilder and MixedDataFrame are consistent
   * 
   * Incrementally build a {@code MatrixDataFrame}. If the initial size is known, prefer
   * {@link org.briljantframework.dataframe.MatrixDataFrame.ArrayBuilder}.
   */
  public static class HashBuilder implements DataFrame.Builder {

    private final IntObjectMap<IntDoubleMap> buffer = new IntObjectOpenHashMap<>();
    private final List<String> colNames;
    private int rows = 0, columns = 0;

    public HashBuilder(int rows, int columns) {
      this.rows = rows;
      this.columns = columns;
      this.colNames = null;
    }

    public HashBuilder(Collection<String> colNames, Collection<? extends Type> types) {
      // checkArgument(types == null || colNames.size() == types.size(),
      // "Arguments imply different sizes %s != %s.", colNames.size(), types.size());
      this.colNames = new ArrayList<>(colNames);
      this.columns = colNames.size();
    }

    public HashBuilder() {
      this.colNames = null;
    }

    private void ensureCapacity(int rows, int columns) {
      if (rows >= this.rows && columns >= this.columns) {
        this.columns = columns + 1;
        this.rows = rows + 1;
      } else if (rows >= this.rows) {
        this.rows = rows + 1;
      } else if (columns >= this.columns) {
        this.columns = columns + 1;
      }
    }

    public DataFrame.Builder set(int row, int column, double dval) {
      ensureCapacity(row, column);
      IntDoubleMap col = buffer.get(column);
      if (col == null) {
        col = new IntDoubleOpenHashMap();
        buffer.put(column, col);
      }
      col.put(row, dval);
      return this;
    }

    @Override
    public DataFrame.Builder setNA(int row, int column) {
      return set(row, column, DoubleVector.NA);
    }

    @Override
    public DataFrame.Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol) {
      return set(toRow, toCol, from.getAsDouble(fromRow, fromCol));
    }

    @Override
    public DataFrame.Builder set(int toRow, int toCol, Vector from, int fromRow) {
      return set(toRow, toCol, from.getAsDouble(fromRow));
    }

    @Override
    public DataFrame.Builder set(int row, int column, Object value) {
      double dval;
      if (value instanceof Value) {
        dval = ((Value) value).getAsDouble();
      } else if (value instanceof Number) {
        dval = ((Number) value).doubleValue();
      } else {
        dval = DoubleVector.NA;
      }
      return set(row, column, dval);
    }

    @Override
    public DataFrame.Builder addColumn(Vector.Builder builder) {
      throw new UnsupportedOperationException("Can't add vector builder");
    }

    @Override
    public DataFrame.Builder removeColumn(int column) {
      throw new UnsupportedOperationException("Can't remove column");
    }

    @Override
    public DataFrame.Builder swapColumns(int a, int b) {
      IntDoubleMap aMap = buffer.get(a);
      IntDoubleMap bMap = buffer.get(b);
      buffer.put(a, bMap);
      buffer.put(b, aMap);
      return this;
    }

    @Override
    public DataFrame.Builder swapInColumn(int column, int a, int b) {
      IntDoubleMap col = buffer.get(column);
      if (col != null) {
        if (col.containsKey(a) && col.containsKey(b)) {
          double tmp = col.get(a);
          col.put(a, col.get(b));
          col.put(b, tmp);
        } else if (col.containsKey(a)) {
          col.put(b, col.get(a));
          col.remove(a);
        } else if (col.containsKey(b)) {
          col.put(a, col.get(b));
          col.remove(b);
        }
      }
      // col only has NA values and no swapping is needed
      return this;
    }

    @Override
    public DataFrame.Builder read(DataInputStream inputStream) throws IOException {
      int row = 0;
      while (inputStream.hasNext()) {
        DataEntry entry = inputStream.next();
        for (int i = 0; i < entry.size(); i++) {
          set(row, i, entry.nextDouble());
        }
        row++;
      }
      return this;
    }

    @Override
    public int columns() {
      return columns;
    }

    @Override
    public int rows() {
      return rows;
    }


    @Override
    public DataFrame build() {
      double[] values = new double[rows() * columns()];
      for (int j = 0; j < columns(); j++) {
        for (int i = 0; i < rows(); i++) {
          int index = Indexer.columnMajor(i, j, rows(), columns());
          double dval = DoubleVector.NA;
          IntDoubleMap col = buffer.get(j);
          if (col != null) {
            if (col.containsKey(i)) {
              dval = col.get(i);
            }
          }
          values[index] = dval;
        }
      }

      ArrayMatrix mat = new ArrayMatrix(rows(), columns(), values);
      if (colNames != null) {
        return new MatrixDataFrame(mat, colNames, false);
      } else {
        return new MatrixDataFrame(mat);
      }
    }
  }


}
