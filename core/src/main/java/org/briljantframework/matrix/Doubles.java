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
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.regex.Pattern;

import org.briljantframework.QuickSort;
import org.briljantframework.Utils;
import org.briljantframework.vector.VectorLike;

import com.github.fommil.netlib.BLAS;
import com.google.common.collect.Lists;


/**
 * Common Basic Linear Algebra Subroutines.
 * <p>
 * Created by Isak Karlsson on 21/06/14.
 */
public class Doubles {

  /**
   * The constant RANDOM.
   */
  public static final Random RANDOM = Utils.getRandom();

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
   * <p>
   * 
   * <pre>
   *     row :== double<sub>1</sub>, {double<sub>n</sub>}
   *     matrix :== row<sub>1</sub>; {row<sub>m</sub>}
   * </pre>
   * <p>
   * For example, {@code 1, 2, 3, 4; 1, 2, 3, 4;1, 2, 3, 4} is a 3-by-4 matrix with ones in the
   * first column, twos in the second column etc.
   * <p>
   * Returns an {@link ArrayDoubleMatrix}.
   *
   * @param str the input matrix as a string
   * @return a matrix
   * @throws java.lang.NumberFormatException
   */
  public static DoubleMatrix parseMatrix(String str) {
    checkArgument(str != null && str.length() > 0);

    String[] rows = ROW_SEPARATOR.split(str);
    if (rows.length < 1) {
      throw new NumberFormatException("Illegally formatted Matrix");
    }

    DoubleMatrix matrix = null;
    for (int i = 0; i < rows.length; i++) {
      String[] values = VALUE_SEPARATOR.split(rows[i]);
      if (i == 0) {
        matrix = new ArrayDoubleMatrix(rows.length, values.length);
      }

      for (int j = 0; j < values.length; j++) {
        matrix.set(i, j, Double.parseDouble(values[j].trim()));
      }
    }

    return matrix;
  }

  public static DoubleMatrix newMatrix(int size, DoubleSupplier supplier) {
    return zeros(size, 1).assign(supplier);
  }

  public static DoubleMatrix newMatrix(double[][] values) {
    DoubleMatrix m = new ArrayDoubleMatrix(values.length, values[0].length);
    for (int i = 0; i < values.length; i++) {
      for (int j = 0; j < values[0].length; j++) {
        m.set(i, j, values[i][j]);
      }
    }
    return m;
  }

  public static DoubleMatrix newMatrix(double... values) {
    return new ArrayDoubleMatrix(1, values);
  }

  @SuppressWarnings("unchecked")
  public static DoubleMatrix newMatrix(Iterable<? extends Number> iter) {
    List<? extends Number> numbers;
    if (iter instanceof List) {
      numbers = (List<? extends Number>) iter;
    } else {
      numbers = Lists.newArrayList(iter);
    }
    DoubleMatrix m = new ArrayDoubleMatrix(numbers.size(), 1);
    for (int i = 0; i < numbers.size(); i++) {
      m.set(i, numbers.get(i).doubleValue());
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
  public static DoubleMatrix zeros(int rows, int cols) {
    return new ArrayDoubleMatrix(rows, cols);
  }

  /**
   * Square matrix with {@code size x size} consisting of zeroes.
   *
   * @param size
   * @return a new matrix
   */
  public static DoubleMatrix zeros(int size) {
    return zeros(size, 1);
  }

  /**
   * Square matrix with {@code size x size} consisting of ones.
   *
   * @param size the size
   * @return a new matrix
   */
  public static DoubleMatrix ones(int size) {
    return ones(size, size);
  }

  /**
   * Matrix with ones.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the matrix
   */
  public static DoubleMatrix ones(int rows, int cols) {
    return ArrayDoubleMatrix.filledWith(rows, cols, 1);
  }

  /**
   * N dense matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @param n the n
   * @return the dense matrix
   */
  public static DoubleMatrix fill(int rows, int cols, double n) {
    return ArrayDoubleMatrix.filledWith(rows, cols, n);
  }

  public static DoubleMatrix fill(int size, double n) {
    return ArrayDoubleMatrix.filledWith(size, 1, n);
  }

  /**
   * Diagonal identity matrix of {@code size}
   *
   * @param size the size
   * @return the identity matrix
   */
  public static DoubleMatrix eye(int size) {
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
  public static void map(DoubleMatrix in, DoubleUnaryOperator operator, DoubleMatrix out) {
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

  public static DoubleMatrix sort(DoubleMatrix matrix) {
    DoubleMatrix out = matrix.copy();
    QuickSort.quickSort(0, out.size(), (a, b) -> Double.compare(out.get(a), out.get(b)),
        (a, b) -> {
          double tmp = out.get(a);
          out.set(a, out.get(b));
          out.set(b, tmp);
        });
    return out;
  }

  public static DoubleMatrix sort(DoubleMatrix matrix, Axis axis) {
    DoubleMatrix out = matrix.copy();
    if (axis == Axis.ROW) {
      for (int i = 0; i < matrix.rows(); i++) {
        DoubleMatrix row = out.getRowView(i);
        QuickSort.quickSort(0, row.size(), (a, b) -> Double.compare(row.get(a), row.get(b)),
            (a, b) -> {
              double tmp = row.get(a);
              row.set(a, row.get(b));
              row.set(b, tmp);
            });
      }
    } else {
      for (int i = 0; i < matrix.columns(); i++) {
        DoubleMatrix col = out.getColumnView(i);
        QuickSort.quickSort(0, col.size(), (a, b) -> Double.compare(col.get(a), col.get(b)),
            (a, b) -> {
              double tmp = col.get(a);
              col.set(a, col.get(b));
              col.set(b, tmp);
            });
      }
    }
    return out;
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
  public static DoubleMatrix map(DoubleMatrix in, DoubleUnaryOperator operator) {
    return in.newEmptyMatrix(in.rows(), in.columns()).assign(in, operator);
  }


  /**
   * Sqrt matrix.
   *
   * @param matrix the matrix
   * @return the matrix
   */
  public static DoubleMatrix sqrt(DoubleMatrix matrix) {
    return map(matrix, Math::sqrt);
  }

  /**
   * Log out.
   *
   * @param in the in
   * @return out out
   */
  public static DoubleMatrix log(DoubleMatrix in) {
    return map(in, Math::log);
  }

  /**
   * Log 2.
   *
   * @param in the in
   * @return out out
   */
  public static DoubleMatrix log2(DoubleMatrix in) {
    return map(in, x -> Math.log(x) / LOG_2);
  }

  public static DoubleMatrix reshape(VectorLike a, int m, int n) {
    DoubleMatrix matrix = new ArrayDoubleMatrix(m, n);
    for (int i = 0; i < a.size(); i++) {
      matrix.set(i, a.getAsDouble(i));
    }
    return matrix;
  }

  /**
   * <p>
   * Create a vector of length {@code num} with evenly spaced values between {@code start} and
   * {@code end}.
   * </p>
   *
   * @param start the start value
   * @param stop the end value
   * @param num the number of steps (i.e. intermediate values)
   * @return a vector
   */
  public static DoubleMatrix linspace(double start, double stop, int num) {
    double[] builder = new double[num];
    double step = (stop - start) / (num - 1);
    double value = start;
    for (int index = 0; index < num; index++) {
      builder[index] = value;
      value += step;
    }

    return new ArrayDoubleMatrix(1, builder);
  }

  /**
   * Std out.
   *
   * @param matrix the matrix
   * @param axis the axis
   * @return the out
   */
  public static DoubleMatrix std(DoubleMatrix matrix, Axis axis) {
    DoubleMatrix mean = mean(matrix, axis);
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
    return ArrayDoubleMatrix.rowVector(sigmas);
  }

  /**
   * Mean out.
   *
   * @param matrix the matrix
   * @param axis the axis
   * @return the out
   */
  public static DoubleMatrix mean(DoubleMatrix matrix, Axis axis) {
    int columns = matrix.columns();
    double[] means = new double[matrix.columns()];
    for (int j = 0; j < matrix.columns(); j++) {
      double mean = 0.0;
      for (int i = 0; i < matrix.rows(); i++) {
        mean += matrix.get(i, j);
      }
      means[j] = mean / matrix.rows();
    }

    return ArrayDoubleMatrix.rowVector(means);
  }

  /**
   * Randn out.
   *
   * @param rows the rows
   * @param cols the cols
   * @return out out
   */
  public static DoubleMatrix randn(int rows, int cols) {
    return new ArrayDoubleMatrix(rows, cols).assign(RANDOM::nextGaussian);
  }

  /**
   * Rand out.
   *
   * @param rows the rows
   * @param cols the cols
   * @return out out
   */
  public static DoubleMatrix rand(int rows, int cols) {
    return new ArrayDoubleMatrix(rows, cols).assign(RANDOM::nextDouble);
  }

  /**
   * Pow out.
   *
   * @param in the in
   * @param power the power
   * @return out out
   */
  public static DoubleMatrix pow(DoubleMatrix in, double power) {
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
  public static DoubleMatrix log10(DoubleMatrix in) {
    return map(in, Math::log10);
  }

  /**
   * Sign out.
   *
   * @param in the in
   * @return out out
   */
  public static DoubleMatrix signum(DoubleMatrix in) {
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
  public static void mmul(DoubleMatrix t, double alpha, DoubleMatrix other, double beta,
      double[] tmp) {
    BLAS.dgemm("n", "n", t.rows(), other.columns(), other.rows(), alpha, t.asDoubleArray(),
        t.rows(), other.asDoubleArray(), other.rows(), beta, tmp, t.rows());
  }

  public static void mmul(DoubleMatrix t, double alpha, Transpose a, DoubleMatrix other,
      double beta, Transpose b, double[] tmp) {
    String transA = "n";
    int thisRows = t.rows();
    if (a.transpose()) {
      thisRows = t.columns();
      transA = "t";
    }

    String transB = "n";
    int otherRows = other.rows();
    int otherColumns = other.columns();
    if (b.transpose()) {
      otherRows = other.columns();
      otherColumns = other.rows();
      transB = "t";
    }
    BLAS.dgemm(transA, transB, thisRows, otherColumns, otherRows, alpha, t.asDoubleArray(),
        t.rows(), other.asDoubleArray(), other.rows(), beta, tmp, thisRows);
  }

  /**
   *
   * @param matrix
   * @return
   */
  public static double trace(DoubleMatrix matrix) {
    int min = Math.min(matrix.rows(), matrix.columns());
    double sum = 0;
    for (int i = 0; i < min; i++) {
      sum += matrix.get(i, i);
    }
    return sum;
  }

  /**
   * @param vector the vector
   * @return the standard deviation
   */
  public static double std(DoubleMatrix vector) {
    return std(vector, mean(vector));
  }

  /**
   * @param vector the vector
   * @param mean the mean
   * @return the standard deviation
   */
  public static double std(DoubleMatrix vector, double mean) {
    double var = var(vector, mean);
    return Math.sqrt(var / (vector.size() - 1));
  }

  /**
   * @param vector the vector
   * @return the mean
   */
  public static double mean(DoubleMatrix vector) {
    double mean = 0;
    for (int i = 0; i < vector.size(); i++) {
      mean += vector.get(i);
    }

    return mean / vector.size();
  }

  /**
   * @param vector the vector
   * @param mean the mean
   * @return the variance
   */
  public static double var(DoubleMatrix vector, double mean) {
    double var = 0;
    for (int i = 0; i < vector.size(); i++) {
      double residual = vector.get(i) - mean;
      var += residual * residual;
    }
    return var;
  }

  /**
   * @param vector the vector
   * @return the variance
   */
  public static double var(DoubleMatrix vector) {
    return var(vector, mean(vector));
  }


  /**
   * Sum t.
   *
   * @param m the m
   * @param axis the axis
   * @return the t
   */
  public static DoubleMatrix sum(DoubleMatrix m, Axis axis) {
    switch (axis) {
      case ROW:
        return rowSum(m);
      case COLUMN:
        return columnSum(m);
      default:
        throw new IllegalArgumentException();
    }
  }

  private static ArrayDoubleMatrix columnSum(DoubleMatrix m) {
    double[] values = new double[m.rows()];
    for (int j = 0; j < m.columns(); j++) {
      for (int i = 0; i < m.rows(); i++) {
        values[i] += m.get(i, j);
      }
    }
    return new ArrayDoubleMatrix(m.rows(), 1, values);
  }


  private static ArrayDoubleMatrix rowSum(DoubleMatrix m) {
    double[] values = new double[m.columns()];
    for (int j = 0; j < m.columns(); j++) {
      for (int i = 0; i < m.rows(); i++) {
        values[j] += m.get(i, j);
      }
    }

    return new ArrayDoubleMatrix(1, m.columns(), values);
  }
}
