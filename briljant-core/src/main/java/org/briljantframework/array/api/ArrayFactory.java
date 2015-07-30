package org.briljantframework.array.api;

import org.briljantframework.array.Array;
import org.briljantframework.array.BitArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.LongArray;
import org.briljantframework.array.Range;
import org.briljantframework.complex.Complex;
import org.briljantframework.distribution.Distribution;
import org.briljantframework.distribution.NormalDistribution;
import org.briljantframework.distribution.UniformDistribution;

/**
 * @author Isak Karlsson
 */
public interface ArrayFactory {

  DoubleArray ones(int... shape);

  DoubleArray zero(int... shape);

  <T> Array<T> array(T[] data);

  <T> Array<T> array(T[][] data);

  /**
   * Create an {@code IntMatrix} with the given data
   *
   * @param data the data array
   * @return a new matrix
   */
  IntArray array(int[][] data);

  /**
   * Create a vector with the given data.
   *
   * @param data the data array
   * @return a new matrix
   */
  IntArray array(int[] data);

  /**
   * Create a {@code LongMatrix} with given data
   *
   * @param data the data
   * @return a new matrix
   */
  LongArray array(long[][] data);

  /**
   * Create a vector with the given data.
   *
   * @param data the data array
   * @return a new matrix
   */
  LongArray array(long[] data);

  /**
   * Create a {@code DoubleMatrix} with given data
   *
   * @param data the data
   * @return a new matrix
   */
  DoubleArray array(double[][] data);

  /**
   * Create a vector with the given data.
   *
   * @param data the data array
   * @return a new matrix
   */
  DoubleArray array(double[] data);

  DoubleArray diag(DoubleArray data);

  /**
   * Create a {@code ComplexMatrix} with given data
   *
   * @param data the data
   * @return a new matrix
   */
  ComplexArray array(Complex[][] data);

  ComplexArray array(Complex[] data);

  ComplexArray complexArray(double[] data);

  /**
   * Create a {@code BitMatrix} with given data
   *
   * @param data the data
   * @return a new matrix
   */
  BitArray array(boolean[][] data);

  BitArray array(boolean[] data);

  /**
   * Create an {@code IntMatrix} with designated shape filled with {@code 0}.
   *
   * @param shape the rows
   * @return a new matrix
   */
  IntArray intArray(int... shape);

  /**
   * Create an {@code LongMatrix} with designated shape filled with {@code 0}.
   *
   * @param shape the rows
   * @return a new matrix
   */
  LongArray longArray(int... shape);

  DoubleArray doubleArray(int... shape);

  /**
   * Create an {@code ComplexMatrix} with designated shape filled with {@code 0+0i}.
   *
   * @param shape the rows
   * @return a new matrix
   */
  ComplexArray complexArray(int... shape);

  /**
   * Create an {@code BitMatrix} with designated shape filled with {@code false}.
   *
   * @param shape the rows
   * @return a new matrix
   */
  BitArray booleanArray(int... shape);

  <T> Array<T> referenceArray(int... shape);

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

  Range range();

  /**
   * Return a row vector of linearly spaced values
   *
   * @param start start value
   * @param end   end value
   * @param size  the size of the returned vector
   * @return a new row vector
   */
  DoubleArray linspace(double start, double end, int size);

  DoubleArray eye(int size);

  default DoubleArray rand(int size, Distribution distribution) {
    return doubleArray(size).assign(distribution::sample);
  }

  default DoubleArray randn(int size) {
    return rand(size, new NormalDistribution(0, 1));
  }

  default IntArray randi(int size, Distribution distribution) {
    return intArray(size).assign(() -> (int) Math.round(distribution.sample()));
  }

  default IntArray randi(int size, int l, int u) {
    return randi(size, new UniformDistribution(l, u));
  }
}
