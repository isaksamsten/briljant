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

import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import java.util.regex.Pattern;

import org.briljantframework.QuickSort;
import org.briljantframework.vector.VectorLike;

import com.github.fommil.netlib.BLAS;
import com.google.common.collect.Lists;


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
   * Parse a matrix in the format
   * <p>
   * 
   * <pre>
   *     row :== double<sub>1</sub>, {double<sub>n</sub>}
   *     matrix :== row<sub>1</sub>; {row<sub>m</sub>}
   * </pre>
   * <p>
   * For example, {@code 1, 2, 3, 4;1,2,3,4;1,2,3,4} is a 3 by 4 matrix with ones in the first
   * column, twos in the second column etc.
   * <p>
   * Returns an {@link org.briljantframework.matrix.ArrayMatrix}.
   *
   * @param str the input matrix as a string
   * @return a matrix
   * @throws java.lang.NumberFormatException
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

  public static Matrix matrix(double[][] values) {
    Matrix m = new ArrayMatrix(values.length, values[0].length);
    for (int i = 0; i < values.length; i++) {
      for (int j = 0; j < values[0].length; j++) {
        m.put(i, j, values[i][j]);
      }
    }
    return m;
  }

  public static Matrix matrix(double... values) {
    return new ArrayMatrix(1, values);
  }

  @SuppressWarnings("unchecked")
  public static Matrix matrix(Iterable<? extends Number> iter) {
    List<? extends Number> numbers;
    if (iter instanceof List) {
      numbers = (List<? extends Number>) iter;
    } else {
      numbers = Lists.newArrayList(iter);
    }
    Matrix m = new ArrayMatrix(numbers.size(), 1);
    for (int i = 0; i < numbers.size(); i++) {
      m.put(i, numbers.get(i).doubleValue());
    }
    return m;
  }

  /**
   * Matrix of zeroes.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the dense matrix
   */
  public static Matrix zeros(int rows, int cols) {
    return new ArrayMatrix(rows, cols);
  }

  /**
   * Square matrix with {@code size x size} consisting of zeroes.
   *
   * @param size
   * @return a new matrix
   */
  public static Matrix zeros(int size) {
    return zeros(size, size);
  }

  /**
   * Square matrix with {@code size x size} consisting of ones.
   *
   * @param size the size
   * @return a new matrix
   */
  public static Matrix ones(int size) {
    return ones(size, size);
  }

  /**
   * Matrix with ones.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the matrix
   */
  public static Matrix ones(int rows, int cols) {
    return ArrayMatrix.filledWith(rows, cols, 1);
  }

  /**
   * N dense matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @param n the n
   * @return the dense matrix
   */
  public static Matrix fill(int rows, int cols, double n) {
    return ArrayMatrix.filledWith(rows, cols, n);
  }

  /**
   * Diagonal identity matrix of {@code size}
   *
   * @param size the size
   * @return the identity matrix
   */
  public static Matrix eye(int size) {
    double[] diagonal = new double[size];
    for (int i = 0; i < size; i++) {
      diagonal[i] = 1;
    }
    return Diagonal.of(size, size, diagonal);
  }

  /**
   * @param in input tensorlike
   * @param operator operator to apply
   * @param out the out
   */
  public static void map(Matrix in, DoubleUnaryOperator operator, Matrix out) {
    out.assign(in, operator);
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
              row.put(a, row.get(b));
              row.put(b, tmp);
            });
      }
    } else {
      for (int i = 0; i < matrix.columns(); i++) {
        Matrix col = out.getColumnView(i);
        QuickSort.quickSort(0, col.size(), (a, b) -> Double.compare(col.get(a), col.get(b)),
            (a, b) -> {
              double tmp = col.get(a);
              col.put(a, col.get(b));
              col.put(b, tmp);
            });
      }
    }
    return out;
  }

  public static Matrix range(int start, int end, int step) {
    int i = end - start;
    double[] values = new double[i / step + (i % step != 0 ? 1 : 0)];
    int index = 0;
    while (index < values.length) {
      values[index++] = start;
      start += step;
    }
    return new ArrayMatrix(1, values);
  }

  /**
   * <pre>
   *
   * </pre>
   *
   * @param in the in
   * @param operator the operator
   * @return out out
   */
  public static Matrix map(Matrix in, DoubleUnaryOperator operator) {
    return in.newEmptyMatrix(in.rows(), in.columns()).assign(in, operator);
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
    return new ArrayMatrix(rows, cols).assign(RANDOM::nextGaussian);
  }

  /**
   * Rand out.
   *
   * @param rows the rows
   * @param cols the cols
   * @return out out
   */
  public static Matrix rand(int rows, int cols) {
    return new ArrayMatrix(rows, cols).assign(RANDOM::nextDouble);
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
  public static Matrix signum(Matrix in) {
    return map(in, Math::signum);
  }

  /**
   * Simple wrapper around
   * {@link com.github.fommil.netlib.BLAS#dgemm(String, String, int, int, int, double, double[], int, double[], int, double, double[], int)}
   * <p>
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
