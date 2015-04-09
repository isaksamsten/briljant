package org.briljantframework.dataframe;

import com.carrotsearch.hppc.IntDoubleMap;
import com.carrotsearch.hppc.IntDoubleOpenHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;

import org.briljantframework.ArrayBuffers;
import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.matrix.DefaultDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Convert;
import org.briljantframework.vector.DoubleValue;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Undefined;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.primitives.Ints.checkedCast;

/**
 * Initial implementation of the matrix data frame. While the DataFrame interface allows for
 * heterogeneous implementations, the {@code MatrixDataFrame} is homogeneous over {@link
 * org.briljantframework.vector.DoubleVector#TYPE}, i.e. {@code double} values.
 *
 * @author Isak Karlsson
 */
public class MatrixDataFrame extends AbstractDataFrame {

  private final DoubleMatrix matrix;

  public MatrixDataFrame(DoubleMatrix matrix) {
    this.matrix = matrix;
  }

  protected MatrixDataFrame(DoubleMatrix matrix, NameAttribute columnNames, NameAttribute rowNames,
                            boolean copy) {
    super(columnNames, rowNames, copy);
    this.matrix = matrix;
  }

  /**
   * Returns a {@link org.briljantframework.vector.DoubleValue} or {@link
   * org.briljantframework.vector.Undefined#INSTANCE}
   *
   * @param row    the row
   * @param column the column
   * @return the value
   */
  @Override
  public Value getAsValue(int row, int column) {
    double value = matrix.get(row, column);
    return Is.NA(value) ? Undefined.INSTANCE : new DoubleValue(value);
  }

  @Override
  public <T> T get(Class<T> cls, int row, int column) {
    return cls.equals(Double.TYPE) || cls.equals(Double.class)
           ? cls.cast(getAsDouble(row, column)) : cls.cast(DoubleVector.NA);
  }

  /**
   * Returns the double value as a string. Returns {@code null}, if value is missing
   *
   * @param row    the row
   * @param column the column
   * @return the string representation (as returned by {@link String#valueOf(double)} or {@code
   * null}
   */
  @Override
  public String getAsString(int row, int column) {
    double value = matrix.get(row, column);
    return Is.NA(value) ? StringVector.NA : String.valueOf(value);
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
  public Bit getAsBit(int row, int column) {
    double value = matrix.get(row, column);
    return Is.NA(value) ? Bit.NA : value == 1 ? Bit.TRUE : Bit.FALSE;
  }

  /**
   * Returns a {@link org.briljantframework.complex.Complex} usign the double as the real part.
   *
   * @param row    the row
   * @param column the column
   * @return a complex
   */
  @Override
  public Complex getAsComplex(int row, int column) {
    double value = matrix.get(row, column);
    return Is.NA(value) ? Complex.NaN : new Complex(value);
  }

  @Override
  public String toString(int row, int column) {
    double value = matrix.get(row, column);
    return Is.NA(value) ? "NA" : Double.toString(value);
  }

  @Override
  public boolean isNA(int row, int column) {
    return Is.NA(matrix.get(row, column));
  }

  @Override
  public VectorType getColumnType(int index) {
    checkArgument(index >= 0 && index < columns());
    return DoubleVector.TYPE;
  }

  @Override
  public int rows() {
    return checkedCast(matrix.rows());
  }

  @Override
  public int columns() {
    return checkedCast(matrix.columns());
  }

  @Override
  public DataFrame.Builder newBuilder() {
    return new HashBuilder(columnNames, rowNames);
  }

  @Override
  public DataFrame.Builder newCopyBuilder() {
    double[] array = new double[rows() * columns()];
    System.arraycopy(matrix.asDoubleArray(), 0, array, 0, array.length);
    return new ArrayBuilder(columnNames, rowNames, rows(), columns(), array);

  }

  /**
   * Returns a
   *
   * @param index the index
   */
  @Override
  public Vector getColumn(int index) {
    return Convert.toAdapter(matrix.getColumnView(index));
  }

  @Override
  public DataFrame removeColumn(int index) {
    checkElementIndex(index, columns(), "Column-index out of bounds.");
    NameAttribute columnNames = new NameAttribute(this.columnNames);
    columnNames.remove(index);

    DoubleMatrix newMatrix = matrix.newEmptyMatrix(rows(), columns() - 1);
    int j = 0;
    for (int k = 0; k < matrix.columns(); k++) {
      if (k != index) {
        for (int i = 0; i < matrix.rows(); i++) {
          newMatrix.set(i, j, matrix.get(i, k));
        }
        j++;
      }
    }

    return new MatrixDataFrame(newMatrix, columnNames, rowNames, false);
  }

  @Override
  public Record getRecord(int index) {
    return new RecordView(this, index, DoubleVector.TYPE);
  }

  /**
   * Dynamically allocates a new {@code MatrixDataFrame}. <p> Appending and increasing the size is
   * rather costly (due to reallocation) using this builder. It is therefore recommended to
   * initialize with a size. If linearly adding elements, it's way faster to append backwards, i.e.,
   * starting with the bottom right and proceed to the top left corner of the matrix <p>
   *
   * <pre>
   *     for(int i = 100; i >= 0; i--)
   *       for(int j = 100; i >= 0; j--)
   *         builder.set(i, j, ....);
   * </pre>
   * <p> is faster than starting at {@code i = 0} and {@code j = 0}. <p> Alternatively, if the size
   * is unknown prefer {@link org.briljantframework.dataframe.MatrixDataFrame.HashBuilder}, which is
   * both sparse and fast to incrementally build.
   */
  public static class ArrayBuilder extends AbstractBuilder {

    private static final int MIN_CAPACITY = 50;
    private double[] buffer;
    private int rows, columns;

    public ArrayBuilder(int rows, int columns) {
      this.rows = rows;
      this.columns = columns;

      buffer = new double[rows * columns];
      Arrays.fill(buffer, DoubleVector.NA);
    }

    public ArrayBuilder() {
      this(0, 0);
    }

    public ArrayBuilder(Collection<String> colNames, Collection<? extends VectorType> colTypes) {
      checkArgument(colTypes.size() == colNames.size());
      this.rows = 0;
      this.columns = colNames.size();
      buffer = new double[0];
      int index = 0;
      for (String colName : colNames) {
        columnNames.put(index++, colName);
      }
    }

    protected ArrayBuilder(NameAttribute columnNames, NameAttribute rowNames, int rows,
                           int columns, double[] buffer) {
      super(columnNames, rowNames);
      this.rows = rows;
      this.columns = columns;
      this.buffer = buffer;
    }

    protected ArrayBuilder(NameAttribute columnNames, NameAttribute rowNames, int rows,
                           int columns) {
      this(columnNames, rowNames, rows, columns, new double[rows * columns]);
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
      }

      return Indexer.columnMajor(row, column, rows, columns);

    }

    /*
     * Reinitialize buffer to hold a matrix with rows and columns. Since the indexes must be
     * recalculated, this is rather costly.
     */
    private void reInitializeBuffer(int rows, int columns) {
      double[] tmp = ArrayBuffers.reallocate(buffer, rows * columns);
      Arrays.fill(tmp, DoubleVector.NA);
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
    public DataFrame.Builder set(int row, int column, Vector from, int index) {
      return set(row, column, from.getAsDouble(index));
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
      this.columnNames.remove(column);
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
      columnNames.swap(a, b);
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
      DefaultDoubleMatrix mat = new DefaultDoubleMatrix(buffer, rows, columns);
      return new MatrixDataFrame(mat, columnNames, rowNames, false);
    }
  }

  /**
   * Incrementally build a {@code MatrixDataFrame}. If the initial size is known, prefer {@link
   * org.briljantframework.dataframe.MatrixDataFrame.ArrayBuilder}.
   */
  public static class HashBuilder extends AbstractBuilder {

    /*
     * The buffer stores values in column based maps
     */
    private final IntObjectMap<IntDoubleMap> buffer = new IntObjectOpenHashMap<>();

    private int rows = 0, columns = 0;

    public HashBuilder(int rows, int columns) {
      this.rows = rows;
      this.columns = columns;
    }

    public HashBuilder(String... columnNames) {
      this(Arrays.asList(columnNames));
    }

    public HashBuilder(List<String> columnNames) {
      for (int i = 0; i < columnNames.size(); i++) {
        this.columnNames.put(i, columnNames.get(i));
      }
    }

    public HashBuilder(Collection<String> colNames, Collection<? extends VectorType> types) {
      this.columns = colNames.size();
      int index = 0;
      for (String colName : colNames) {
        this.columnNames.put(index++, colName);
      }
    }

    protected HashBuilder(NameAttribute columnNames, NameAttribute rowNames) {
      super(columnNames, rowNames);
    }

    public HashBuilder() {
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
    public DataFrame.Builder addColumnBuilder(Vector.Builder builder) {
      Vector vector = builder.build();
      int column = columns();
      for (int i = 0; i < vector.size(); i++) {
        set(i, column, vector, i);
      }
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
    public DataFrame.Builder set(int row, int column, Vector from, int index) {
      return set(row, column, from.getAsDouble(index));
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
    public DataFrame.Builder removeColumn(int column) {
      checkArgument(column >= 0 && column < columns());

      columnNames.remove(column);
      buffer.remove(column);
      columns--;
      return this;
    }

    @Override
    public DataFrame.Builder swapColumns(int a, int b) {
      Utils.swap(buffer, a, b);
      columnNames.swap(a, b);
      return this;
    }

    @Override
    public DataFrame.Builder swapInColumn(int column, int a, int b) {
      IntDoubleMap col = buffer.get(column);
      if (col != null) {
        // boolean colContainsA = col.containsKey(a);
        // boolean colContainsB = col.containsKey(b);
        // if (colContainsA && colContainsB) {
        // double tmp = col.get(a);
        // col.put(a, col.get(b));
        // col.put(b, tmp);
        // } else if (colContainsA) {
        // col.put(b, col.get(a));
        // col.remove(a);
        // } else if (colContainsB) {
        // col.put(a, col.get(b));
        // col.remove(b);
        // }

        Utils.swap(col, a, b);
        rowNames.swap(a, b);
      }
      // column only has NA values and no swapping is needed
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
        IntDoubleMap col = buffer.get(j);
        for (int i = 0; i < rows(); i++) {
          int index = Indexer.columnMajor(i, j, rows(), columns());
          double dval = DoubleVector.NA;
          if (col != null) {
            if (col.containsKey(i)) {
              dval = col.get(i);
            }
          }
          values[index] = dval;
        }
      }

      DefaultDoubleMatrix matrix = new DefaultDoubleMatrix(values, rows(), columns());
      return new MatrixDataFrame(matrix, columnNames, rowNames, false);
    }
  }


}
