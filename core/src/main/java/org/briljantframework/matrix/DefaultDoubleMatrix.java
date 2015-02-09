/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static org.briljantframework.matrix.Indexer.columnMajor;

import java.util.Arrays;

import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.storage.DoubleStorage;
import org.briljantframework.matrix.storage.Storage;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * Implementation of {@link DoubleMatrix} using a single {@code double} array. Indexing is
 * calculated in column-major order, hence varying column faster than row is preferred when
 * iterating.
 * 
 * Assuming that {@link com.github.fommil.netlib.BLAS} initializes correctly and that the second
 * operand is {@link #isArrayBased()}, matrix-matrix multiplication is fast.
 * 
 * @author Isak Karlsson
 */
public class DefaultDoubleMatrix extends AbstractDoubleMatrix {

  protected static final String INVALID_SIZE = "Sizes does not match.";
  private final Storage storage;

  public DefaultDoubleMatrix(int size) {
    super(size);
    storage = DoubleStorage.withSize(size);
  }

  public DefaultDoubleMatrix(Storage storage, int size) {
    super(size);
    this.storage = storage;
  }

  public DefaultDoubleMatrix(Storage storage, int rows, int columns) {
    super(rows, columns);
    this.storage = storage;
  }

  /**
   * Create a new matrix from {@code values}. Asserts that {@code rows * columns == values.length}
   * 
   * @param values the values
   * @param rows the rows
   * @param columns the columns
   */
  public DefaultDoubleMatrix(double[] values, int rows, int columns) {
    this(new DoubleStorage(values), rows, columns);
    checkArgument(rows * columns == values.length, "Sizes does not match.");
  }


  /**
   * Creates a new empty matrix with {@code rows} and {@code columns}. Asserts that
   * {@code rows * columns < Integer.MAX_VALUE}.
   * 
   * @param rows in matrix
   * @param columns columns in matrix
   */
  public DefaultDoubleMatrix(int rows, int columns) {
    this(new double[Math.multiplyExact(rows, columns)], rows, columns);
  }

  /**
   * Copy {@code matrix}, retaining the dimensions.
   * 
   * @param matrix the tensor like
   */
  public DefaultDoubleMatrix(Matrix matrix) {
    this(matrix.getStorage(), matrix.rows(), matrix.columns());
  }

  public DefaultDoubleMatrix(Vector vec) {
    this(vec.size(), 1);
    for (int i = 0; i < vec.size(); i++) {
      set(i, vec.getAsDouble(i));
    }
  }

  /**
   * Create a new matrix from a multi-dimensional array. Assumes row-major order in {@code values}.
   *
   * @param values the values
   */
  public DefaultDoubleMatrix(double[][] values) {
    this(values.length, values[0].length);
    for (int i = 0; i < values.length; i++) {
      for (int j = 0; j < values[i].length; j++) {
        set(i, j, values[i][j]);
      }
    }
  }

  public DefaultDoubleMatrix(Storage storage) {
    super(storage.size());
    this.storage = storage;
  }

  public DefaultDoubleMatrix(double... values) {
    this(new DoubleStorage(values));
  }

  /**
   * Construct a new matrix filled with {@code value}.
   *
   * @param rows number of rows
   * @param cols number of columns
   * @param value fill matrix with
   * @return a new matrix filled with <code>value</code>
   */
  public static DefaultDoubleMatrix filledWith(int rows, int cols, double value) {
    double[] values = new double[Math.multiplyExact(rows, cols)];
    Arrays.fill(values, value);
    return new DefaultDoubleMatrix(values, rows, cols);
  }

  public static Builder withSize(int rows, int cols) {
    return new Builder(rows, cols);
  }

  public static Builder withRows(int rows) {
    return new Builder(rows, 1);
  }

  public static Builder withColumns(int columns) {
    return new Builder(1, columns);
  }

  /**
   * Constructs a new {@code ArrayMatrix} from a row-major order double array
   *
   * @param rows the rows
   * @param cols the cols
   * @param args the args
   * @return dense matrix
   */
  public static DefaultDoubleMatrix fromRowOrder(int rows, int cols, double... args) {
    return of(rows, cols, args);
  }

  /**
   * Mostly for convenience when writing matrices in code.
   * <p>
   *
   * <pre>
   * ArrayMatrix.of(2, 3, 1, 2, 3, 4, 5, 6);
   * </pre>
   * <p>
   * Compared to:
   * <p>
   *
   * <pre>
   *     ArrayMatrix.fromColumnOrder(2, 3,
   *          1, 4
   *          2, 5
   *          3, 6
   *    )
   * </pre>
   *
   * @param rows number of rows
   * @param cols number of headers
   * @param data in row-major format
   * @return a matrix
   */
  public static DefaultDoubleMatrix of(int rows, int cols, double... data) {
    Preconditions.checkNotNull(data, "data");
    if (rows * cols != data.length) {
      throw new IllegalArgumentException("rows * headers != data.length");
    }

    // Convert row-major order to column major order
    double[] colOrder = new double[data.length];
    for (int j = 0; j < cols; j++) {
      for (int i = 0; i < rows; i++) {
        colOrder[j * rows + i] = data[i * cols + j];
      }
    }
    return new DefaultDoubleMatrix(colOrder, rows, cols);
  }

  public static DefaultDoubleMatrix fromColumnOrder(int rows, int cols, double... args) {
    return new DefaultDoubleMatrix(args, rows, cols);
  }

  /**
   * Construct a row vector (i.e. a {@code 1 x args.length} matrix)
   * 
   * @param args the double values
   * @return a new matrix
   */
  public static DefaultDoubleMatrix columnVector(double... args) {
    return new DefaultDoubleMatrix(args, args.length, 1);
  }

  /**
   * Construct a column vector (i.e. a {@code args.length x 1} matrix)
   *
   * @param args the double values
   * @return a new matrix
   */
  public static DefaultDoubleMatrix rowVector(double... args) {
    return new DefaultDoubleMatrix(args, 1, args.length);
  }

  @Override
  public DoubleMatrix reshape(int rows, int columns) {
    Preconditions.checkArgument(rows * columns == size(),
        "Total size of new matrix must be unchanged.");
    return new DefaultDoubleMatrix(getStorage(), rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return new DefaultDoubleMatrix(rows, columns);
  }

  @Override
  public double get(int i, int j) {
    return storage.getDouble(columnMajor(i, j, rows(), columns()));
  }

  @Override
  public double get(int index) {
    return storage.getDouble(index);
  }

  @Override
  public boolean isArrayBased() {
    return storage.isArrayBased() && storage.getNativeType() == Double.TYPE;
  }

  /**
   * @return a copy of this matrix
   */
  public DefaultDoubleMatrix copy() {
    return new DefaultDoubleMatrix(storage.copy(), rows(), columns());
  }

  @Override
  public Storage getStorage() {
    return storage;
  }

  @Override
  public DoubleMatrix mmul(double alpha, DoubleMatrix other) {
    if (this.columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }

    if (other.isArrayBased()) {
      double[] tmp = new double[Math.multiplyExact(this.rows(), other.columns())];
      Matrices.mmul(this, alpha, other, 1.0, tmp);
      return new DefaultDoubleMatrix(new DoubleStorage(tmp), this.rows(), other.columns());
    } else {
      return super.mmul(alpha, other);
    }
  }

  @Override
  public DoubleMatrix mmul(double alpha, Transpose a, DoubleMatrix other, Transpose b) {
    int thisRows = rows();
    int thisCols = columns();
    if (a.transpose()) {
      thisRows = columns();
      thisCols = rows();
    }
    int otherRows = other.rows();
    int otherColumns = other.columns();
    if (b.transpose()) {
      otherRows = other.columns();
      otherColumns = other.rows();
    }

    if (thisCols != otherRows) {
      throw new NonConformantException(thisRows, thisCols, otherRows, otherColumns);
    }

    if (other.isArrayBased()) {
      double[] tmp = new double[thisRows * otherColumns];
      Matrices.mmul(this, alpha, a, other, 1.0, b, tmp);
      return new DefaultDoubleMatrix(new DoubleStorage(tmp), thisRows, otherColumns);
    } else {
      return super.mmul(alpha, a, other, b);
    }
  }

  @Override
  public double[] asDoubleArray() {
    return storage.asDoubleArray();
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(storage.asDoubleArray());
  }

  @Override
  public void set(int i, int j, double value) {
    getStorage().setDouble(columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public void set(int index, double value) {
    getStorage().setDouble(index, value);
  }

  /**
   * The type Builder.
   */
  public static class Builder {
    private final int rows, cols;
    private double[][] values;
    private int currentRow = 0;

    /**
     * Instantiates a new Builder.
     *
     * @param rows the rows
     * @param cols the cols
     */
    public Builder(int rows, int cols) {
      this.rows = rows;
      this.cols = cols;
    }

    /**
     * Row builder.
     *
     * @param args the args
     * @return the builder
     */
    public Builder row(double... args) {
      if (args.length != cols) {
        throw new IllegalArgumentException(String.format("Expecting %d rows but got %d", rows,
            args.length));
      }
      initialize();
      if (currentRow < rows) {
        values[currentRow++] = args;
      } else {
        throw new IllegalArgumentException(String.format("To many rows"));
      }
      return this;
    }

    private void initialize() {
      if (values == null) {
        values = new double[rows][cols];
      }
    }

    /**
     * Create dense matrix.
     *
     * @return the dense matrix
     */
    public DefaultDoubleMatrix create() {
      return new DefaultDoubleMatrix(values);
    }

    /**
     * With values.
     *
     * @param args the args
     * @return the dense matrix
     */
    public DefaultDoubleMatrix withValues(double... args) {
      return DefaultDoubleMatrix.of(rows, cols, args);
    }
  }
}
