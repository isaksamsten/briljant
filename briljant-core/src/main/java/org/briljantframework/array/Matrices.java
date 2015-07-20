package org.briljantframework.array;

import org.briljantframework.Utils;

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

  public static long sum(LongArray matrix) {
    return matrix.reduce(0, Long::sum);
  }

  public static double sum(DoubleArray matrix) {
    return matrix.reduce(0, Double::sum);
  }

  public static int sum(IntArray matrix) {
    return matrix.reduce(0, Integer::sum);
  }

  public static int sum(BitArray matrix) {
    int sum = 0;
    for (int i = 0; i < matrix.size(); i++) {
      sum += matrix.get(i) ? 1 : 0;
    }
    return sum;
  }

  /**
   * Sum t.
   *
   * @param matrix the m
   * @param dim    the axis
   * @return the t
   */
  public static DoubleArray sum(int dim, DoubleArray matrix) {
    return matrix.reduceVectors(dim, Matrices::sum);
  }

  public static <T extends Array> T shuffle(T matrix) {
    Utils.permute(matrix.size(), matrix);
    return matrix;
  }

  /**
   * Computes the mean of the matrix.
   *
   * @param matrix the matrix
   * @return the mean
   */
  public static double mean(DoubleArray matrix) {
    return matrix.reduce(0, Double::sum) / matrix.size();
  }

  /**
   * @param dim    the axis
   * @param matrix the matrix
   * @return a mean matrix; if {@code axis == ROW} with shape = {@code [1, columns]}; or
   * {@code axis == COLUMN} with shape {@code [rows, 1]}.
   */
  public static DoubleArray mean(int dim, DoubleArray matrix) {
    return matrix.reduceVectors(dim, Matrices::mean);
  }

  /**
   * @param vector the vector
   * @return the standard deviation
   */
  public static double std(DoubleArray vector) {
    return std(vector, mean(vector));
  }

  /**
   * @param vector the vector
   * @param mean   the mean
   * @return the standard deviation
   */
  public static double std(DoubleArray vector, double mean) {
    double var = var(vector, mean);
    return Math.sqrt(var / (vector.size() - 1));
  }

  public static DoubleArray std(int dim, DoubleArray matrix) {
    return matrix.reduceVectors(dim, Matrices::std);
  }

  /**
   * @param matrix the vector
   * @param mean   the mean
   * @return the variance
   */
  public static double var(DoubleArray matrix, double mean) {
    return matrix.reduce(0, (v, acc) -> acc + (v - mean) * (v - mean));
  }

  /**
   * @param vector the vector
   * @return the variance
   */
  public static double var(DoubleArray vector) {
    return var(vector, mean(vector));
  }

  public static DoubleArray var(int dim, DoubleArray matrix) {
    return matrix.reduceVectors(dim, Matrices::var);
  }

}
