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

import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import java.util.regex.Pattern;

import org.briljantframework.QuickSort;
import org.briljantframework.vector.VectorLike;

import com.github.fommil.netlib.BLAS;


/**
 * Common Basic Linear Algebra Subroutines.
 * <p>
 * Created by Isak Karlsson on 21/06/14.
 */
public class Matrices {

  /**
   * The constant RANDOM.
   */
  public static final Random RANDOM = new Random();

  /**
   * The constant LOG_2.
   */
  public static final double LOG_2 = Math.log(2);
  protected static final BLAS BLAS = com.github.fommil.netlib.BLAS.getInstance();
  private static final Pattern ROW_SEPARATOR = Pattern.compile(";");
  private static final Pattern VALUE_SEPARATOR = Pattern.compile(",");

  /**
   * Parse matrix.
   *
   * @param str the str
   * @return the out
   */
  public static Matrix parseMatrix(String str) {
    checkArgument(str != null && str.length() > 0);

    String[] rows = ROW_SEPARATOR.split(str);
    if (rows.length < 1) {
      throw new NumberFormatException("Illegally formatted Matrix");
    }

    Matrix matrix = null;
    for (int i = 0; i < rows.length; i++) {
      String[] values = VALUE_SEPARATOR.split(rows[i]);
      if (i == 0) {
        matrix = new ArrayMatrix(rows.length, values.length);
      }

      for (int j = 0; j < values.length; j++) {
        matrix.put(i, j, Double.parseDouble(values[j].trim()));
      }
    }

    return matrix;
  }

  /**
   * Zero dense matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the dense matrix
   */
  public static Matrix zeros(int rows, int cols) {
    return new ArrayMatrix(rows, cols);
  }

  /**
   * Ones dense matrix.
   *
   * @param size the size
   * @return the dense matrix
   */
  public static Matrix ones(int size) {
    return ones(size, size);
  }

  /**
   * Ones matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the matrix
   */
  public static Matrix ones(int rows, int cols) {
    return ArrayMatrix.filledWith(rows, cols, 1);
  }

  /**
   * Apply <code>operation</code> to every element in the {@code ArrayBackend} output in
   * <p>
   * 
   * <pre>
   * Tensors.apply(vector, Math::sqrt, output);
   * </pre>
   *
   * @param in input tensorlike
   * @param operator operator to apply
   * @param out the out
   */
  public static void map(Matrix in, DoubleUnaryOperator operator, Matrix out) {
    for (int i = 0; i < in.size(); i++) {
      out.put(i, operator.applyAsDouble(in.get(i)));
    }
  }

  /**
   * N dense matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @param n the n
   * @return the dense matrix
   */
  public static Matrix n(int rows, int cols, double n) {
    return ArrayMatrix.filledWith(rows, cols, n);
  }

  /**
   * Eye diagonal.
   *
   * @param size the size
   * @return the diagonal
   */
  public static Diagonal eye(int size) {
    double[] diagonal = new double[size];
    for (int i = 0; i < size; i++) {
      diagonal[i] = 1;
    }
    return Diagonal.of(size, size, diagonal);
  }


  /**
   * Eye diagonal.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the diagonal
   */
  public static Diagonal eye(int rows, int cols) {
    double[] diagonal = new double[rows * cols];
    for (int i = 0; i < diagonal.length; i++) {
      diagonal[i] = 1;
    }
    return Diagonal.of(rows, cols, diagonal);
  }

  public static Matrix sort(Matrix matrix) {
    Matrix out = matrix.copy();
    QuickSort.quickSort(0, out.size(), (a, b) -> Double.compare(out.get(a), out.get(b)),
        (a, b) -> {
          double tmp = out.get(a);
          out.put(a, out.get(b));
          out.put(b, tmp);
        });
    return out;
  }

  public static Matrix sort(Matrix matrix, Axis axis) {
    Matrix out = matrix.copy();
    if (axis == Axis.ROW) {
      for (int i = 0; i < matrix.rows(); i++) {
        Matrix row = out.getRowView(i);
        QuickSort.quickSort(0, row.size(), (a, b) -> Double.compare(row.get(a), row.get(b)),
            (a, b) -> {
              double tmp = row.get(a);
              row.put(a, out.get(b));
              row.put(b, tmp);
            });

        System.out.println(row);
      }
    } else {
      for (int i = 0; i < matrix.columns(); i++) {
        Matrix col = out.getColumnView(i);
        QuickSort.quickSort(0, col.size(), (a, b) -> Double.compare(col.get(a), col.get(b)),
            (a, b) -> {
              double tmp = col.get(a);
              col.put(a, out.get(b));
              col.put(b, tmp);
            });
      }
    }
    return out;
  }

  /**
   * Sqrt matrix.
   *
   * @param matrix the matrix
   * @return the matrix
   */
  public static Matrix sqrt(Matrix matrix) {
    return map(matrix, Math::sqrt);
  }


  /**
   * <pre>
   * Tensors.apply(vector, Math::sqrt, DenseVector::new)
   * </pre>
   * 
   * @param in the in
   * @param operator the operator
   * @return out out
   */
  public static Matrix map(Matrix in, DoubleUnaryOperator operator) {
    Matrix out = new ArrayMatrix(in.rows(), in.columns());
    map(in, operator, out);
    return out;
  }

  /**
   * Log out.
   *
   * @param in the in
   * @return out out
   */
  public static Matrix log(Matrix in) {
    return map(in, Math::log);
  }

  /**
   * Log 2.
   *
   * @param in the in
   * @return out out
   */
  public static Matrix log2(Matrix in) {
    return map(in, x -> Math.log(x) / LOG_2);
  }

  public static Matrix reshape(VectorLike a, int m, int n) {
    Matrix matrix = new ArrayMatrix(m, n);
    for (int i = 0; i < a.size(); i++) {
      matrix.put(i, a.getAsDouble(i));
    }
    return matrix;
  }

  //
  // /**
  // * Return a new array with the result
  // *
  // * @param m a square matrix with x.rows = d.size
  // * @param d a diagonal matrix
  // * @return a new array with the same dimensions as x
  // */
  // public static Matrix mdmul(Matrix m, Diagonal d) {
  // Shape shape = Shape.of(m.rows(), d.columns());
  // double[] empty = shape.getArrayOfShape();
  // mdmuli(m, d, empty);
  // return new ArrayMatrix(shape, empty);
  // }

  // /**
  // * Multiplying a square matrix X with a symmetric diagonal matrix (i.e. a vector of diagonal
  // * entries) d storing the result in Y.
  // * <p>
  // * Since the result is a new square matrix, inplace multiplication can be performed
  // * <p>
  // *
  // * <pre>
  // * Matrix x = Matrix.of(2, 2, 1, 1, 1, 1);
  // * Vector d = Vector.row(2, 2);
  // * Blas.multiplyByDiagonal(x, d, x);
  // * </pre>
  // * <p>
  // *
  // * <pre>
  // * Y &lt; -Xd
  // * </pre>
  // *
  // * @param x a square matrix with x.headers = d.size
  // * @param d a diagonal matrix
  // * @param y a square matrix with x.shape = out.shape
  // */
  // public static void mdmuli(Matrix x, Diagonal d, double[] y) {
  // if (x.columns() != d.rows()) {
  // throw new NonConformantException(x, d);
  // }
  // int rows = x.rows(), columns = d.columns();
  // for (int column = 0; column < columns; column++) {
  // if (column < x.columns()) {
  // for (int row = 0; row < rows; row++) {
  // double xv = x.get(row, column);
  // double dv = d.get(column);
  // y[column * rows + row] = xv * dv;
  // }
  // } else {
  // break;
  // }
  // }
  // }

  // /**
  // * Multiply by diagonal.
  // *
  // * @param d a diagonal matrix
  // * @param m a square matrix with x.headers = d.size
  // * @return the result
  // */
  // public static Matrix dmmul(Diagonal d, Matrix m) {
  // Shape shape = Shape.of(d.rows(), m.columns());
  // double[] array = shape.getArrayOfShape();
  // dmmuli(d, m, array);
  // return new ArrayMatrix(shape, array);
  // }

  // /**
  // * Multiplying a square symmetric diagonal matrix (i.e. a vector of diagonal entries) d and X,
  // * storing the result in Y
  // * <p>
  // *
  // * <pre>
  // * Y &lt; -dX
  // * </pre>
  // *
  // * @param d a diagonal matrix
  // * @param x a square matrix with x.rows = d.size
  // * @param y a square matrix with x.shape = y.shape
  // */
  // public static void dmmuli(Diagonal d, Matrix x, double[] y) {
  // if (d.columns() != x.rows()) {
  // throw new NonConformantException(d, x);
  // }
  // int rows = d.rows(), columns = x.columns();
  // for (int row = 0; row < rows; row++) {
  // if (row < x.rows()) {
  // for (int column = 0; column < columns; column++) {
  // y[column * rows + row] = x.get(row, column) * d.get(row);
  // }
  // } else {
  // break;
  // }
  // }
  // }

  /**
   * Linspace out.
   *
   * @param limit the limit
   * @param n the n
   * @param base the base
   * @return the out
   */
  public static Matrix linspace(int limit, int n, int base) {
    double[] valyes = new double[n];
    double step = ((double) limit - base) / (n - 1);

    double value = base;
    for (int index = 0; index < n; index++) {
      valyes[index] = value;
      value += step;
    }

    return ArrayMatrix.rowVector(valyes);
  }

  /**
   * Reshape out.
   *
   * @param in the in
   * @param rows the rows
   * @param cols the cols
   * @return the out
   */
  public static Matrix reshape(Matrix in, int rows, int cols) {
    checkArgument(in.hasCompatibleShape(rows, cols), "can't reshape %s tensor into %s tensor",
        in.getShape(), Shape.of(rows, cols));
    return new ArrayMatrix(Shape.of(rows, cols), in);
  }


  /**
   * Std out.
   *
   * @param matrix the matrix
   * @param axis the axis
   * @return the out
   */
  public static Matrix std(Matrix matrix, Axis axis) {
    Matrix mean = mean(matrix, axis);
    int columns = matrix.columns();
    double[] sigmas = new double[columns];

    for (int j = 0; j < columns; j++) {
      double std = 0.0;
      for (int i = 0; i < matrix.rows(); i++) {
        double residual = matrix.get(i, j) - mean.get(j);
        std += residual * residual;
      }
      sigmas[j] = Math.sqrt(std / (matrix.rows() - 1));
    }
    return ArrayMatrix.rowVector(sigmas);
  }

  /**
   * Mean out.
   *
   * @param matrix the matrix
   * @param axis the axis
   * @return the out
   */
  public static Matrix mean(Matrix matrix, Axis axis) {
    int columns = matrix.columns();
    double[] means = new double[matrix.columns()];
    for (int j = 0; j < matrix.columns(); j++) {
      double mean = 0.0;
      for (int i = 0; i < matrix.rows(); i++) {
        mean += matrix.get(i, j);
      }
      means[j] = mean / matrix.rows();
    }

    return ArrayMatrix.rowVector(means);
  }

  /**
   * Randn out.
   *
   * @param rows the rows
   * @param cols the cols
   * @return out out
   */
  public static Matrix randn(int rows, int cols) {
    Shape shape = Shape.of(rows, cols);
    double[] array = shape.getArrayOfShape();
    randn(array);
    return new ArrayMatrix(shape, array);
  }

  /**
   * Randn void.
   *
   * @param array a array to fill
   */
  public static void randn(double[] array) {
    for (int i = 0; i < array.length; i++) {
      array[i] = RANDOM.nextGaussian();
    }
  }

  /**
   * Rand out.
   *
   * @param rows the rows
   * @param cols the cols
   * @return out out
   */
  public static Matrix rand(int rows, int cols) {
    Shape shape = Shape.of(rows, cols);
    double[] array = shape.getArrayOfShape();
    rand(array);
    return new ArrayMatrix(shape, array);
  }

  /**
   * Fill with uniformly random numbers
   *
   * @param array the array
   */
  public static void rand(double[] array) {
    for (int i = 0; i < array.length; i++) {
      array[i] = RANDOM.nextGaussian();
    }
  }



  /**
   * Pow out.
   *
   * @param in the in
   * @param power the power
   * @return out out
   */
  public static Matrix pow(Matrix in, double power) {
    switch ((int) power) {
      case 2:
        return map(in, x -> x * x);
      case 3:
        return map(in, x -> x * x * x);
      case 4:
        return map(in, x -> x * x * x * x);
      default:
        return map(in, x -> Math.pow(x, power));
    }
  }

  /**
   * Log 10.
   *
   * @param in the in
   * @return out out
   */
  public static Matrix log10(Matrix in) {
    return map(in, Math::log10);
  }

  /**
   * Sign out.
   * 
   * @param in the in
   * @return out out
   */
  public static Matrix sign(Matrix in) {
    return map(in, Math::signum);
  }

  // }


  // }

  // }

  // }

  // }

  // }

  /**
   * Simple wrapper around
   * {@link com.github.fommil.netlib.BLAS#dgemm(String, String, int, int, int, double, double[], int, double[], int, double, double[], int)}
   * 
   * Performs no additional error checking.
   * 
   * @param t left hand side
   * @param alpha scaling for lhs
   * @param other right hand side
   * @param beta scaling for rhs
   * @param tmp result is written to {@code tmp}
   */
  public static void mmul(Matrix t, double alpha, Matrix other, double beta, double[] tmp) {
    BLAS.dgemm("n", "n", t.rows(), other.columns(), other.rows(), alpha, t.asDoubleArray(),
        t.rows(), other.asDoubleArray(), other.rows(), beta, tmp, t.rows());
  }


  /**
   * Sum t.
   *
   * @param m the m
   * @param axis the axis
   * @return the t
   */
  public static Matrix sum(Matrix m, Axis axis) {
    switch (axis) {
      case ROW:
        return rowSum(m);
      case COLUMN:
        return columnSum(m);
      default:
        throw new IllegalArgumentException();
    }
  }

  private static ArrayMatrix columnSum(Matrix m) {
    double[] values = new double[m.rows()];
    for (int j = 0; j < m.columns(); j++) {
      for (int i = 0; i < m.rows(); i++) {
        values[i] += m.get(i, j);
      }
    }
    return new ArrayMatrix(m.rows(), 1, values);
  }

  private static ArrayMatrix rowSum(Matrix m) {
    double[] values = new double[m.columns()];
    for (int j = 0; j < m.columns(); j++) {
      for (int i = 0; i < m.rows(); i++) {
        values[j] += m.get(i, j);
      }
    }

    return new ArrayMatrix(1, m.columns(), values);
  }


}
