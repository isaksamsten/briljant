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

import com.carrotsearch.hppc.IntDoubleMap;
import com.carrotsearch.hppc.IntDoubleOpenHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;

import org.briljantframework.Check;
import org.briljantframework.Utils;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.Indexer;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.EntryReader;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Na;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Initial implementation of the matrix data frame. While the DataFrame interface allows for
 * heterogeneous implementations, the {@code MatrixDataFrame} is homogeneous over {@link
 * org.briljantframework.vector.DoubleVector#TYPE}, i.e. {@code double} values.
 *
 * @author Isak Karlsson
 */
public class MatrixDataFrame extends AbstractDataFrame {

  private final DoubleArray matrix;

  public MatrixDataFrame(DoubleArray matrix) {
    this.matrix = matrix;
  }

  @Override
  public <T> T get(Class<T> cls, int row, int column) {
    return cls.equals(Double.TYPE) || cls.equals(Double.class)
           ? cls.cast(getAsDouble(row, column)) : cls.cast(Na.DOUBLE);
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
  public String toString(int row, int column) {
    double value = matrix.get(row, column);
    return Is.NA(value) ? "NA" : Double.toString(value);
  }

  @Override
  public boolean isNA(int row, int column) {
    return Is.NA(matrix.get(row, column));
  }

  @Override
  public VectorType getType(int index) {
    Check.argument(index >= 0 && index < columns());
    return DoubleVector.TYPE;
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
    return new HashBuilder();
  }

  @Override
  public DataFrame.Builder newCopyBuilder() {
    double[] array = new double[rows() * columns()];
    for (int i = 0; i < matrix.size(); i++) {
      array[i] = matrix.get(i);
    }
    return new ArrayBuilder(rows(), columns(), array);
  }

  /**
   * Returns a
   *
   * @param index the index
   */
  @Override
  public Vector get(int index) {
    return new ColumnView(this, index);
  }

  @Override
  public DataFrame drop(int index) {
    Check.elementIndex(index, columns()/*, "Column-index out of bounds."*/);
//    NameAttribute columnNames = new NameAttribute(this.columnNames);
//    columnNames.remove(index);

    DoubleArray newMatrix = matrix.newEmptyArray(rows(), columns() - 1);
    int j = 0;
    for (int k = 0; k < matrix.columns(); k++) {
      if (k != index) {
        for (int i = 0; i < matrix.rows(); i++) {
          newMatrix.set(i, j, matrix.get(i, k));
        }
        j++;
      }
    }

    return new MatrixDataFrame(newMatrix);
  }

  @Override
  public Vector getRecord(int index) {
    return new RowView(this, index, DoubleVector.TYPE);
  }

  /**
   * Dynamically allocates a new {@code MatrixDataFrame}. <p> Appending and increasing the size is
   * rather costly (due to reallocation) using this builder. It is therefore recommended to
   * initialize with a size. If linearly adding elements, it's way faster to append backwards,
   * i.e.,
   * starting with the bottom right and proceed to the top left corner of the matrix <p>
   *
   * <pre>
   *     for(int i = 100; i >= 0; i--)
   *       for(int j = 100; i >= 0; j--)
   *         builder.set(i, j, ....);
   * </pre>
   * <p> is faster than starting at {@code i = 0} and {@code j = 0}. <p> Alternatively, if the size
   * is unknown prefer {@link org.briljantframework.dataframe.MatrixDataFrame.HashBuilder}, which
   * is
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
      Arrays.fill(buffer, Na.DOUBLE);
    }

    public ArrayBuilder() {
      this(0, 0);
    }

    public ArrayBuilder(Collection<String> colNames, Collection<? extends VectorType> colTypes) {
      Check.argument(colTypes.size() == colNames.size());
      this.rows = 0;
      this.columns = colNames.size();
      buffer = new double[0];
      int index = 0;
      for (String colName : colNames) {
//        columnNames.put(index++, colName);
      }
    }

    protected ArrayBuilder(int rows, int columns, double[] buffer) {
      this.rows = rows;
      this.columns = columns;
      this.buffer = buffer;
    }

    @Override
    public DataFrame.Builder setNA(int row, int column) {
      set(row, column, Na.DOUBLE);
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

      return Indexer.columnMajor(0, row, column, rows, columns);

    }

    /**
     * Reallocates {@code array} to a new array of length {@code minCapacity} if
     * {@code array.length < minCapacity} otherwise return {@code array}.
     *
     * @param array       the array
     * @param minCapacity the minimum capacity
     * @return an array of {@code minCapacity}; might return the input array
     */
    static double[] reallocate(double[] array, int minCapacity) {
      int oldCapacity = array.length;
      double[] newArray;
      if (oldCapacity < minCapacity) {
        newArray = new double[minCapacity];
      } else {
        newArray = array;
      }
      return newArray;
    }

    /*
     * Reinitialize buffer to hold a matrix with rows and columns. Since the indexes must be
     * recalculated, this is rather costly.
     */
    private void reInitializeBuffer(int rows, int columns) {
      double[] tmp = reallocate(buffer, rows * columns);
      Arrays.fill(tmp, Na.DOUBLE);
      for (int j = 0; j < this.columns; j++) {
        for (int i = 0; i < this.rows; i++) {
          int newIndex = Indexer.columnMajor(0, i, j, rows, columns);
          int oldIndex = Indexer.columnMajor(0, i, j, this.rows, this.columns);
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
      if (value instanceof Number) {
        dval = ((Number) value).doubleValue();
      } else {
        dval = Na.DOUBLE;
      }
      return set(row, column, dval);
    }

    @Override
    public DataFrame.Builder removeColumn(int column) {
      double[] tmp = reallocate(buffer, rows * (columns - 1));
      int newColumns = this.columns - 1;
      for (int j = 0; j < this.columns; j++) {
        for (int i = 0; i < this.rows; i++) {
          if (j != column) {
            tmp[Indexer.columnMajor(0, i, j, rows, newColumns)] =
                buffer[Indexer.columnMajor(0, i, j, rows, columns)];
          }
        }
      }
//      this.columnNames.remove(column);
      this.columns = newColumns;
      this.buffer = tmp;
      return this;
    }

    @Override
    public DataFrame.Builder swapColumns(int a, int b) {
      for (int i = 0; i < rows(); i++) {
        int oldIndex = Indexer.columnMajor(0, i, a, rows(), columns());
        int newIndex = Indexer.columnMajor(0, i, b, rows(), columns());
        double tmp = buffer[oldIndex];
        buffer[oldIndex] = buffer[newIndex];
        buffer[newIndex] = tmp;
      }
//      columnNames.swap(a, b);
      return this;
    }

    private DataFrame.Builder swapInColumn(int column, int a, int b) {
      Check.argument(column >= 0 && column < columns());
      Check.argument(a >= 0 && a < rows() && b >= 0 && b < rows());

      int oldIndex = Indexer.columnMajor(0, a, column, rows(), columns());
      int newIndex = Indexer.columnMajor(0, b, column, rows(), columns());

      double tmp = buffer[oldIndex];
      buffer[oldIndex] = buffer[newIndex];
      buffer[newIndex] = tmp;
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
    public DataFrame.Builder read(EntryReader entryReader) throws IOException {
      int row = 0;
      while (entryReader.hasNext()) {
        DataEntry entry = entryReader.next();
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
      throw new UnsupportedOperationException();
      // TODO
//      DefaultDoubleMatrix mat = new DefaultDoubleMatrix(buffer, rows, columns);
//      return new MatrixDataFrame(mat, columnNames, rowNames, false);
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
//        this.columnNames.put(i, columnNames.get(i));
      }
    }

    public HashBuilder(Collection<String> colNames, Collection<? extends VectorType> types) {
      this.columns = colNames.size();
      int index = 0;
      for (String colName : colNames) {
//        this.columnNames.put(index++, colName);
      }
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
      return set(row, column, Na.DOUBLE);
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
      if (value instanceof Number) {
        dval = ((Number) value).doubleValue();
      } else {
        dval = Na.DOUBLE;
      }
      return set(row, column, dval);
    }


    @Override
    public DataFrame.Builder removeColumn(int column) {
      Check.argument(column >= 0 && column < columns());
      buffer.remove(column);
      columns--;
      return this;
    }

    @Override
    public DataFrame.Builder swapColumns(int a, int b) {
      Utils.swap(buffer, a, b);
      return this;
    }

    private DataFrame.Builder swapInColumn(int column, int a, int b) {
      IntDoubleMap col = buffer.get(column);
      if (col != null) {
        Utils.swap(col, a, b);
      }
      // column only has NA values and no swapping is needed
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
    public DataFrame.Builder read(EntryReader entryReader) throws IOException {
      int row = 0;
      while (entryReader.hasNext()) {
        DataEntry entry = entryReader.next();
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
      throw new UnsupportedOperationException();
//      double[] values = new double[rows() * columns()];
//      for (int j = 0; j < columns(); j++) {
//        IntDoubleMap col = buffer.get(j);
//        for (int i = 0; i < rows(); i++) {
//          int index = Indexer.columnMajor(i, j, rows(), columns());
//          double dval = DoubleVector.NA;
//          if (col != null) {
//            if (col.containsKey(i)) {
//              dval = col.get(i);
//            }
//          }
//          values[index] = dval;
//        }
//      }
//      return null; // TODO
//      DefaultDoubleMatrix matrix = new DefaultDoubleMatrix(values, rows(), columns());
//      return new MatrixDataFrame(matrix, columnNames, rowNames, false);
    }
  }


}
