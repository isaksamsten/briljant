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
import java.util.function.DoubleUnaryOperator;

import org.briljantframework.exception.MismatchException;

import com.google.common.base.Preconditions;

/**
 * Created by Isak Karlsson on 13/06/14.
 */
public class ArrayMatrix extends AbstractMatrix {

  private final double[] values;

  /**
   * Instantiates a new Dense matrix.
   *
   * @param columns the columns
   * @param values the values
   */
  public ArrayMatrix(int columns, double[] values) {
    this(values.length / columns, columns, values);
  }

  /**
   * Instantiates a new Dense matrix.
   *
   * @param rows the rows
   * @param columns the columns
   * @param values the values
   */
  public ArrayMatrix(int rows, int columns, double[] values) {
    super(rows, columns);
    this.values = values;
  }

  /**
   * Instantiates a new Dense matrix.
   *
   * @param shape the shape
   */
  public ArrayMatrix(Shape shape) {
    this(shape.rows, shape.columns);
  }

  /**
   * Instantiates a new Dense matrix.
   *
   * @param rows in matrix
   * @param cols columns in matrix
   */
  public ArrayMatrix(int rows, int cols) {
    this(rows, cols, new double[Math.multiplyExact(rows, cols)]);
  }

  /**
   * Instantiates a new Dense matrix.
   *
   * @param shape the shape
   * @param values the values
   */
  public ArrayMatrix(Shape shape, double[] values) {
    this(shape.rows, shape.columns, values);
  }

  /**
   * Instantiates a new Dense matrix.
   *
   * @param matrix the tensor like
   */
  public ArrayMatrix(MatrixLike matrix) {
    this(matrix.getShape(), matrix);
  }

  /**
   * Copy the tensor (as a Matrix)
   *
   * @param shape the shape
   * @param matrix to copy
   */
  public ArrayMatrix(Shape shape, MatrixLike matrix) {
    this(shape.rows, shape.columns);
    if (!hasCompatibleShape(matrix.getShape())) {
      throw new MismatchException("DenseMatrix", "cant fit tensor");
    }
    if (matrix instanceof ArrayMatrix) {
      System.arraycopy(((Matrix) matrix).asDoubleArray(), 0, values, 0, this.cols * this.rows);
    } else {
      for (int i = 0; i < matrix.size(); i++) {
        values[i] = matrix.get(i);
      }
    }
  }

  /**
   * Instantiates a new Dense matrix.
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
   * Filled with.
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

  /**
   * With size.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the builder
   */
  public static Builder withSize(int rows, int cols) {
    return new Builder(rows, cols);
  }

  /**
   * With rows.
   *
   * @param rows the rows
   * @return the builder
   */
  public static Builder withRows(int rows) {
    return new Builder(rows, 1);
  }

  /**
   * With columns.
   *
   * @param columns the columns
   * @return the builder
   */
  public static Builder withColumns(int columns) {
    return new Builder(1, columns);
  }

  /**
   * From row order.
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
   * DenseMatrix.of(2, 3, 1, 2, 3, 4, 5, 6);
   * </pre>
   * <p>
   * Compared to:
   * <p>
   *
   * <pre>
   *     DenseMatrix.fromColumnOrder(2, 3,
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

  /**
   * Return a new DenseMatrix
   *
   * @param rows with rows
   * @param cols with columns
   * @param args with values
   * @return the new DenseMatrix
   */
  public static ArrayMatrix fromColumnOrder(int rows, int cols, double... args) {
    return new ArrayMatrix(rows, cols, args);
  }

  /**
   * Row vector.
   *
   * @param args the args
   * @return the dense matrix
   */
  public static ArrayMatrix rowVector(double... args) {
    return new ArrayMatrix(args.length, 1, args);
  }

  /**
   * Column vector.
   *
   * @param args the args
   * @return the dense matrix
   */
  public static ArrayMatrix columnVector(double... args) {
    return new ArrayMatrix(1, args.length, args);
  }

  @Override
  protected Matrix newEmptyMatrix(int rows, int columns) {
    return new ArrayMatrix(rows, columns);
  }

  @Override
  public double get(int i, int j) {
    return values[columnMajor(i, j, rows(), columns())];
  }

  @Override
  public double get(int index) {
    checkArgument(index >= 0 && index < values.length);
    return values[index];
  }

  @Override
  public int size() {
    return rows() * columns();
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
  public void put(int i, int j, double value) {
    values[columnMajor(i, j, rows(), columns())] = value;
  }

  @Override
  public void put(int index, double value) {
    checkArgument(index >= 0 && index < values.length);
    values[index] = value;
  }

  @Override
  public double[] asDoubleArray() {
    return new double[0];
  }

  /**
   * Fill void.
   *
   * @param value the value
   */
  public void fill(double value) {
    Arrays.fill(values, value);
  }

  /**
   * Map matrix.
   *
   * @param operator the operator
   * @return the matrix
   */
  public Matrix map(DoubleUnaryOperator operator) {
    ArrayMatrix n = new ArrayMatrix(this.rows(), this.columns());
    double[] values = n.asDoubleArray(), array = asDoubleArray();
    for (int i = 0; i < array.length; i++) {
      values[i] = operator.applyAsDouble(array[i]);
    }

    return n;
  }

  /**
   * Reshape inplace.
   *
   * @param rows the rows
   * @param cols the cols
   */
  public void reshapei(int rows, int cols) {
    if (rows * cols != rows() * columns()) {
      throw new MismatchException("reshapeInplace", String.format(
          "can't reshape %s tensor into %s tensor", getShape(), Shape.of(rows, cols)));
    }
    this.rows = rows;
    this.cols = cols;
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
