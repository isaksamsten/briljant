package org.briljantframework.matrix.api;

import org.briljantframework.complex.Complex;
import org.briljantframework.distribution.Distribution;
import org.briljantframework.distribution.NormalDistribution;
import org.briljantframework.distribution.UniformDistribution;
import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.LongMatrix;
import org.briljantframework.matrix.Range;

/**
 * @author Isak Karlsson
 */
public interface MatrixFactory {

  /**
   * Create an {@code IntMatrix} with the given data
   *
   * @param data the data array
   * @return a new matrix
   */
  IntMatrix matrix(int[][] data);

  /**
   * Create a vector with the given data.
   *
   * @param data the data array
   * @return a new matrix
   */
  IntMatrix matrix(int[] data);

  /**
   * Create a {@code LongMatrix} with given data
   *
   * @param data the data
   * @return a new matrix
   */
  LongMatrix matrix(long[][] data);

  /**
   * Create a vector with the given data.
   *
   * @param data the data array
   * @return a new matrix
   */
  LongMatrix matrix(long[] data);

  /**
   * Create a {@code DoubleMatrix} with given data
   *
   * @param data the data
   * @return a new matrix
   */
  DoubleMatrix matrix(double[][] data);

  /**
   * Create a vector with the given data.
   *
   * @param data the data array
   * @return a new matrix
   */
  DoubleMatrix matrix(double[] data);

  DoubleMatrix diag(double[] data);

  /**
   * Create a {@code ComplexMatrix} with given data
   *
   * @param data the data
   * @return a new matrix
   */
  ComplexMatrix matrix(Complex[][] data);

  ComplexMatrix matrix(Complex[] data);

  ComplexMatrix complexMatrix(double[] data);

  /**
   * Create a {@code BitMatrix} with given data
   *
   * @param data the data
   * @return a new matrix
   */
  BitMatrix matrix(boolean[][] data);

  BitMatrix matrix(boolean[] data);

  /**
   * Create an {@code IntMatrix} with designated shape filled with {@code 0}.
   *
   * @param rows    the rows
   * @param columns the columns
   * @return a new matrix
   */
  IntMatrix intMatrix(int rows, int columns);

  /**
   * Create an {@code IntMatrix} with designated shape filled with {@code 0}.
   *
   * @param size the columns
   * @return a new matrix
   */
  IntMatrix intVector(int size);

  /**
   * Create an {@code LongMatrix} with designated shape filled with {@code 0}.
   *
   * @param rows    the rows
   * @param columns the columns
   * @return a new matrix
   */
  LongMatrix longMatrix(int rows, int columns);

  /**
   * Create an {@code LongMatrix} with designated shape filled with {@code 0}.
   *
   * @param size the size
   * @return a new matrix
   */
  LongMatrix longVector(int size);

  /**
   * Create an {@code DoubleMatrix} with designated shape filled with {@code 0}.
   *
   * @param rows    the rows
   * @param columns the columns
   * @return a new matrix
   */
  DoubleMatrix doubleMatrix(int rows, int columns);

  /**
   * Create an {@code DoubleMatrix} with designated shape filled with {@code 0}.
   *
   * @param size the size
   * @return a new matrix
   */
  DoubleMatrix doubleVector(int size);

  /**
   * Create an {@code ComplexMatrix} with designated shape filled with {@code 0+0i}.
   *
   * @param rows    the rows
   * @param columns the columns
   * @return a new matrix
   */
  ComplexMatrix complexMatrix(int rows, int columns);

  /**
   * Create an {@code ComplexMatrix} with designated shape filled with {@code 0+0i}.
   *
   * @param size the size
   * @return a new matrix
   */
  ComplexMatrix complexVector(int size);

  /**
   * Create an {@code BitMatrix} with designated shape filled with {@code false}.
   *
   * @param rows    the rows
   * @param columns the columns
   * @return a new matrix
   */
  BitMatrix booleanMatrix(int rows, int columns);

  /**
   * Create an {@code BitMatrix} with designated shape filled with {@code false}.
   *
   * @param size the size
   * @return a new matrix
   */
  BitMatrix booleanVector(int size);

  /**
   * Return a row vector of evenly spaced values
   *
   * @param start start value
   * @param end   end value
   * @param step  step size
   * @return a new row vector
   */
  Range range(int start, int end, int step);

  /**
   * Return a row vector of evenly spaced values (step = 1)
   *
   * @param start start value
   * @param end   end value
   * @return a new row vector
   */
  Range range(int start, int end);

  /**
   * Return a row vector of evenly spaced values (start = 0, step = 1)
   *
   * @param end end value
   * @return a new row vector
   */
  Range range(int end);

  /**
   * Return a row vector of linearly spaced values
   *
   * @param start start value
   * @param end   end value
   * @param size  the size of the returned vector
   * @return a new row vector
   */
  DoubleMatrix linspace(double start, double end, int size);

  DoubleMatrix eye(int size);

  default DoubleMatrix rand(int size, Distribution distribution) {
    return doubleVector(size).assign(distribution::sample);
  }

  default DoubleMatrix randn(int size) {
    return rand(size, new NormalDistribution(0, 1));
  }

  default IntMatrix randi(int size, Distribution distribution) {
    return intVector(size).assign(() -> (int) Math.round(distribution.sample()));
  }

  default IntMatrix randi(int size, int l, int u) {
    return randi(size, new UniformDistribution(l, u));
  }

}
