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
import static com.google.common.primitives.Ints.checkedCast;

import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.regex.Pattern;

import org.briljantframework.QuickSort;
import org.briljantframework.matrix.storage.DoubleStorage;

import com.github.fommil.netlib.BLAS;
import com.google.common.collect.Lists;


/**
 * Common Basic Linear Algebra Subroutines.
 * <p>
 * Created by Isak Karlsson on 21/06/14.
 */
public class Doubles {

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
   * Returns an {@link DefaultDoubleMatrix}.
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
        matrix = new DefaultDoubleMatrix(rows.length, values.length);
      }

      for (int j = 0; j < values.length; j++) {
        matrix.set(i, j, Double.parseDouble(values[j].trim()));
      }
    }

    return matrix;
  }

  public static DoubleMatrix newMatrix(int size, DoubleSupplier supplier) {
    return Matrices.zeros(size, 1).assign(supplier);
  }

  public static DoubleMatrix newMatrix(double[][] values) {
    DoubleMatrix m = new DefaultDoubleMatrix(values.length, values[0].length);
    for (int i = 0; i < values.length; i++) {
      for (int j = 0; j < values[0].length; j++) {
        m.set(i, j, values[i][j]);
      }
    }
    return m;
  }

  public static DoubleMatrix newMatrix(double... values) {
    return new DefaultDoubleMatrix(new DoubleStorage(values));
  }

  @SuppressWarnings("unchecked")
  public static DoubleMatrix newMatrix(Iterable<? extends Number> iter) {
    List<? extends Number> numbers;
    if (iter instanceof List) {
      numbers = (List<? extends Number>) iter;
    } else {
      numbers = Lists.newArrayList(iter);
    }
    DoubleMatrix m = new DefaultDoubleMatrix(numbers.size(), 1);
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
    return new DefaultDoubleMatrix(rows, cols);
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
    return DefaultDoubleMatrix.filledWith(rows, cols, 1);
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
    return DefaultDoubleMatrix.filledWith(rows, cols, n);
  }

  public static DoubleMatrix fill(int size, double n) {
    return DefaultDoubleMatrix.filledWith(size, 1, n);
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
   * Std out.
   *
   * @param matrix the matrix
   * @param axis the axis
   * @return the out
   */
  public static DoubleMatrix std(DoubleMatrix matrix, Axis axis) {
    DoubleMatrix mean = mean(matrix, axis);
    long columns = matrix.columns();
    DoubleMatrix sigmas = Matrices.newDoubleVector(matrix.columns());


    for (int j = 0; j < columns; j++) {
      double std = 0.0;
      for (int i = 0; i < matrix.rows(); i++) {
        double residual = matrix.get(i, j) - mean.get(j);
        std += residual * residual;
      }
      sigmas.set(j, Math.sqrt(std / (matrix.rows() - 1)));
    }
    return sigmas;
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
    DoubleMatrix means = Matrices.newDoubleVector(columns);
    for (int j = 0; j < matrix.columns(); j++) {
      double mean = 0.0;
      for (int i = 0; i < matrix.rows(); i++) {
        mean += matrix.get(i, j);
      }
      means.set(j, mean / matrix.rows());
    }

    return means;
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
    BLAS.dgemm("n", "n", checkedCast(t.rows()), checkedCast(other.columns()),
        checkedCast(other.rows()), alpha, t.asDoubleArray(), checkedCast(t.rows()),
        other.asDoubleArray(), checkedCast(other.rows()), beta, tmp, checkedCast(t.rows()));
  }

  public static void mmul(DoubleMatrix t, double alpha, Transpose a, DoubleMatrix other,
      double beta, Transpose b, double[] tmp) {
    String transA = "n";
    int thisRows = checkedCast(t.rows());
    if (a.transpose()) {
      thisRows = checkedCast(t.columns());
      transA = "t";
    }

    String transB = "n";
    int otherRows = checkedCast(other.rows());
    int otherColumns = checkedCast(other.columns());
    if (b.transpose()) {
      otherRows = checkedCast(other.columns());
      otherColumns = checkedCast(other.rows());
      transB = "t";
    }
    BLAS.dgemm(transA, transB, thisRows, otherColumns, otherRows, alpha, t.asDoubleArray(),
        checkedCast(t.rows()), other.asDoubleArray(), checkedCast(other.rows()), beta, tmp,
        thisRows);
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


}
