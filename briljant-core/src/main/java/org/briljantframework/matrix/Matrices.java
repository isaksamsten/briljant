package org.briljantframework.matrix;

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.Utils;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Isak Karlsson
 */
public final class Matrices {

  public static final double LOG_2 = Math.log(2);

  private Matrices() {
  }

  // /**
  // * <pre>
  // * > import org.briljantframework.matrix.*;
  // * DoubleMatrix a = Doubles.randn(10, 1)
  // * DoubleMatrix x = Anys.sort(a).asDoubleMatrix()
  // *
  // * -1.8718
  // * -0.8834
  // * -0.6161
  // * -0.0953
  // * 0.0125
  // * 0.3538
  // * 0.4326
  // * 0.4543
  // * 1.0947
  // * 1.1936
  // * shape: 10x1 type: double
  // * </pre>
  // *
  // * @param matrix the source matrix
  // * @return a new matrix; the returned matrix has the same type as {@code a}
  // */
  // public static Matrix sort(Matrix matrix) {
  // return sort(matrix, Matrix::compare);
  // }

  public static long sum(LongMatrix matrix) {
    return matrix.reduce(0, Long::sum);
  }

  public static double sum(DoubleMatrix matrix) {
    return matrix.reduce(0, Double::sum);
  }

  public static int sum(IntMatrix matrix) {
    return matrix.reduce(0, Integer::sum);
  }

  public static int sum(BitMatrix matrix) {
    int sum = 0;
    for (int i = 0; i < matrix.size(); i++) {
      sum += matrix.get(i) ? 1 : 0;
    }
    return sum;
  }

  /**
   * Sum t.
   *
   * @param m   the m
   * @param dim the axis
   * @return the t
   */
  public static DoubleMatrix sum(DoubleMatrix m, Dim dim) {
    switch (dim) {
      case R:
        return m.reduceRows(Matrices::sum);
      case C:
        return m.reduceColumns(Matrices::sum);
      default:
        throw new IllegalArgumentException();
    }
  }

  public static <T extends Matrix> T shuffle(T matrix) {
    Utils.permute(matrix.size(), matrix);
    return matrix;
  }

  /**
   * Computes the mean of the matrix.
   *
   * @param matrix the matrix
   * @return the mean
   */
  public static double mean(DoubleMatrix matrix) {
    return matrix.reduce(0, Double::sum) / matrix.size();
  }

  /**
   * @param matrix the matrix
   * @param dim    the axis
   * @return a mean matrix; if {@code axis == ROW} with shape = {@code [1, columns]}; or
   * {@code axis == COLUMN} with shape {@code [rows, 1]}.
   */
  public static DoubleMatrix mean(DoubleMatrix matrix, Dim dim) {
    if (dim == Dim.R) {
      return matrix.reduceRows(Matrices::mean);
    } else {
      return matrix.reduceColumns(Matrices::mean);
    }
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
   * @param mean   the mean
   * @return the standard deviation
   */
  public static double std(DoubleMatrix vector, double mean) {
    double var = var(vector, mean);
    return Math.sqrt(var / (vector.size() - 1));
  }

  public static DoubleMatrix std(DoubleMatrix matrix, Dim dim) {
    if (dim == Dim.R) {
      return matrix.reduceRows(Matrices::std);
    } else {
      return matrix.reduceColumns(Matrices::std);
    }
  }

  /**
   * @param matrix the vector
   * @param mean   the mean
   * @return the variance
   */
  public static double var(DoubleMatrix matrix, double mean) {
    return matrix.reduce(0, (v, acc) -> acc + (v - mean) * (v - mean));
  }

  /**
   * @param vector the vector
   * @return the variance
   */
  public static double var(DoubleMatrix vector) {
    return var(vector, mean(vector));
  }

  public static DoubleMatrix var(DoubleMatrix matrix, Dim dim) {
    if (dim == Dim.R) {
      return matrix.reduceRows(Matrices::var);
    } else {
      return matrix.reduceColumns(Matrices::var);
    }
  }

}
