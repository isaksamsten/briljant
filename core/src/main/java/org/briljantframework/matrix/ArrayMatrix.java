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
import org.briljantframework.vector.VectorLike;

import com.google.common.base.Preconditions;

/**
 * Implementation of {@link org.briljantframework.matrix.Matrix} using a single {@code double}
 * array. Indexing is calculated in column-major order, hence varying column faster than row is
 * preferred when iterating.
 * 
 * Assuming that {@link com.github.fommil.netlib.BLAS} initializes correctly and that the second
 * operand is {@link #isArrayBased()}, matrix-matrix multiplication is fast.
 * 
 * @author Isak Karlsson
 */
public class ArrayMatrix extends AbstractMatrix {

  protected static final String INVALID_SIZE = "Sizes does not match.";
  private final double[] values;

  /**
   * Create a new matrix from {@code values} with {@code column} columns. {@code values.length} must
   * be evenly dividable by {@code columns}.
   * 
   * @param columns the columns
   * @param values the values
   */
  public ArrayMatrix(int columns, double[] values) {
    super(values.length / columns, columns);
    this.values = values; // new double[Math.multiplyExact(rows, columns)];
    checkArgument(values.length % columns == 0, INVALID_SIZE);
  }

  /**
   * Create a new matrix from {@code values}. Asserts that {@code rows * columns == values.length}
   *
   * @param rows the rows
   * @param columns the columns
   * @param values the values
   */
  public ArrayMatrix(int rows, int columns, double[] values) {
    super(rows, columns);
    checkArgument(rows * columns == values.length, "Sizes does not match.");
    this.values = values;
  }

  /**
   * Creates a new empty matrix with a shape defined by {@code shape}
   *
   * @param shape the shape
   */
  public ArrayMatrix(Shape shape) {
    this(shape.rows, shape.columns);
  }

  /**
   * Creates a new empty matrix with {@code rows} and {@code columns}. Asserts that
   * {@code rows * columns < Integer.MAX_VALUE}.
   * 
   * @param rows in matrix
   * @param columns columns in matrix
   */
  public ArrayMatrix(int rows, int columns) {
    this(rows, columns, new double[Math.multiplyExact(rows, columns)]);
  }

  /**
   * @param shape the shape
   * @param values the values
   * @see #ArrayMatrix(int, int, double[])
   */
  public ArrayMatrix(Shape shape, double[] values) {
    this(shape.rows, shape.columns, values);
  }

  /**
   * Copy {@code matrix}, retaining the dimensions.
   * 
   * @param matrix the tensor like
   */
  public ArrayMatrix(Matrix matrix) {
    this(matrix.getShape(), matrix);
  }

  /**
   * Copy {@code matrix}, changing the dimensions to {@code shape}. Asserts that
   * {@code shape.size() == matrix.size()}
   *
   * @param shape the shape
   * @param matrix to copy
   */
  public ArrayMatrix(Shape shape, Matrix matrix) {
    super(shape.rows, shape.columns);
    if (!hasCompatibleShape(matrix.getShape())) {
      throw new IllegalArgumentException("matrix can't fit");
    }

    values = new double[Math.multiplyExact(rows, cols)];
    if (matrix instanceof ArrayMatrix) {
      System.arraycopy(((ArrayMatrix) matrix).values, 0, values, 0, this.cols * this.rows);
    } else {
      for (int i = 0; i < matrix.size(); i++) {
        values[i] = matrix.get(i);
      }
    }
  }

  public ArrayMatrix(VectorLike vec) {
    this(vec.size(), 1);
    for (int i = 0; i < vec.size(); i++) {
      put(i, vec.getAsDouble(i));
    }
  }

  /**
   * Create a new matrix from a multi-dimensional array. Assumes row-major order in {@code values}.
   *
   * @param values the values
   */
  public ArrayMatrix(double[][] values) {
    this(values.length, values[0].length);
    for (int i = 0; i < values.length; i++) {
      for (int j = 0; j < values[i].length; j++) {
        put(i, j, values[i][j]);
      }
    }
  }

  /**
   * Construct a new matrix filled with {@code value}.
   *
   * @param rows number of rows
   * @param cols number of columns
   * @param value fill matrix with
   * @return a new matrix filled with <code>value</code>
   */
  public static ArrayMatrix filledWith(int rows, int cols, double value) {
    ArrayMatrix m = new ArrayMatrix(rows, cols);
    Arrays.fill(m.values, value);
    return m;
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
  public static ArrayMatrix fromRowOrder(int rows, int cols, double... args) {
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
  public static ArrayMatrix of(int rows, int cols, double... data) {
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
    return new ArrayMatrix(rows, cols, colOrder);
  }

  public static ArrayMatrix fromColumnOrder(int rows, int cols, double... args) {
    return new ArrayMatrix(rows, cols, args);
  }

  /**
   * Construct a row vector (i.e. a {@code 1 x args.length} matrix)
   * 
   * @param args the double values
   * @return a new matrix
   */
  public static ArrayMatrix columnVector(double... args) {
    return new ArrayMatrix(args.length, 1, args);
  }

  /**
   * Construct a column vector (i.e. a {@code args.length x 1} matrix)
   *
   * @param args the double values
   * @return a new matrix
   */
  public static ArrayMatrix rowVector(double... args) {
    return new ArrayMatrix(1, args.length, args);
  }

  @Override
  public void put(int i, int j, double value) {
    values[columnMajor(i, j, rows(), columns())] = value;
  }

  @Override
  public double get(int i, int j) {
    return values[columnMajor(i, j, rows(), columns())];
  }

  @Override
  public void put(int index, double value) {
    values[index] = value;
  }

  @Override
  public double get(int index) {
    return values[index];
  }

  @Override
  public int size() {
    return rows() * columns();
  }

  @Override
  public boolean isArrayBased() {
    return true;
  }

  @Override
  public Matrix newEmptyMatrix(int rows, int columns) {
    return new ArrayMatrix(rows, columns);
  }

  /**
   * @return a copy of this matrix
   */
  public ArrayMatrix copy() {
    ArrayMatrix m = new ArrayMatrix(this.rows(), this.columns());
    System.arraycopy(values, 0, m.values, 0, values.length);
    return m;
  }

  @Override
  public Matrix mmul(double alpha, Matrix other, double beta) {
    if (this.columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }

    if (other.isArrayBased()) {
      double[] tmp = new double[this.rows() * other.columns()];
      Matrices.mmul(this, alpha, other, beta, tmp);
      return new ArrayMatrix(other.columns(), tmp);
    } else {
      return super.mmul(alpha, other, beta);
    }
  }

  @Override
  public double[] asDoubleArray() {
    return values;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(values);
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
    public ArrayMatrix create() {
      return new ArrayMatrix(values);
    }

    /**
     * With values.
     *
     * @param args the args
     * @return the dense matrix
     */
    public ArrayMatrix withValues(double... args) {
      return ArrayMatrix.of(rows, cols, args);
    }
  }
}
