/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array.api;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.array.Array;
import org.briljantframework.array.BaseArray;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.LongArray;
import org.briljantframework.array.Range;


/**
 * An array factory creates new arrays.
 * 
 * @author Isak Karlsson
 */
public interface ArrayFactory {

  /**
   * Create a new array from the given data.
   * 
   * @param data the data
   * @param <T> the class
   * @return a new array
   */
  <T> Array<T> newVector(T... data);

  /**
   * Create a new array from the given data.
   *
   * @param data the data
   * @param <T> the class
   * @return a new array
   */
  <T> Array<T> newMatrix(T[][] data);

  <T> Array<T> newArray(int... shape);

  /**
   * Create a {@code BitMatrix} with given data
   *
   * @param data the data
   * @return a new matrix
   */
  BooleanArray newBooleanMatrix(boolean[][] data);

  BooleanArray newBooleanVector(boolean... data);

  /**
   * Create an {@code BitMatrix} with designated shape filled with {@code false}.
   *
   * @param shape the rows
   * @return a new matrix
   */
  BooleanArray newBooleanArray(int... shape);

  /**
   * Create an array with the given data
   *
   * @param data the data array
   * @return a new matrix
   */
  IntArray newIntMatrix(int[][] data);

  /**
   * Create a vector with the given data.
   *
   * @param data the data array
   * @return a new matrix
   */
  IntArray newIntVector(int... data);

  /**
   * Create an {@code IntMatrix} with designated shape filled with {@code 0}.
   *
   * @param shape the rows
   * @return a new matrix
   */
  IntArray newIntArray(int... shape);

  /**
   * Create an array with the given data
   *
   * @param data the data
   * @return a new matrix
   */
  LongArray newLongMatrix(long[][] data);

  /**
   * Create a vector with the given data.
   *
   * @param data the data array
   * @return a new matrix
   */
  LongArray newLongVector(long... data);

  /**
   * Create an {@code LongMatrix} with designated shape filled with {@code 0}.
   *
   * @param shape the rows
   * @return a new matrix
   */
  LongArray newLongArray(int... shape);

  /**
   * Create a matrix with given data in row-major order.
   *
   * <p>
   * Example
   *
   * <pre>
   * {@code
   * > double[][] data = {
   *     {1, 2, 3},
   *     {1, 2 ,3}
   *   };
   * > f.array(data);
   * 
   * array([[1, 2, 3],
   *        [1, 2, 3]] type: double)
   * }
   * </pre>
   *
   * @param data the data
   * @return a new matrix
   */
  DoubleArray newDoubleMatrix(double[][] data);

  /**
   * Create a vector with the given data.
   *
   * @param data the data array
   * @return a new matrix
   */
  DoubleArray newDoubleVector(double... data);

  /**
   * Construct an empty {@code double} are with the given shape. Note that for most implementations
   * the resulting array is initialized with {@code 0}. This is however no guarantee.
   *
   * @param shape the shape
   * @return a new array
   */
  DoubleArray newDoubleArray(int... shape);

  /**
   * Create a matrix with given data
   *
   * @param data the data
   * @return a new matrix
   * @see #newDoubleMatrix(double[][])
   */
  ComplexArray newComplexMatrix(Complex[][] data);

  ComplexArray newComplexVector(Complex... data);

  ComplexArray newComplexVector(double... data);

  /**
   * Create an {@code ComplexMatrix} with designated shape filled with {@code 0+0i}.
   *
   * @param shape the rows
   * @return a new matrix
   */
  ComplexArray newComplexArray(int... shape);

  DoubleArray ones(int... shape);

  DoubleArray zeros(int... shape);

  /**
   * Extract or create a diagonal matrix
   *
   * <p>
   * If the argument is a 2d-array (matrix), a view of the diagonal entries will be
   * {@linkplain org.briljantframework.array.BaseArray#getDiagonal() extracted}. If the argument is
   * a 1d-array (vector) of size {@code n}, a 2d-array {@code n x n} with the vector as the diagonal
   * will be returned. Note that a {@code 1 x n} or {@code m x 1} 2d-array will be considered a
   * 1d-array.
   *
   * <p>
   * Example
   * 
   * <pre>
   * {@code
   * > IntArray x = Arrays.range(3)
   * array([0, 1, 2] type: int)
   * 
   * > IntArray y = Arrays.diag(x)
   * array([[0, 0, 0],
   *        [0, 1, 0],
   *        [0, 0, 2]] type: int)
   * 
   * > Arrays.diag(y)
   * array([0, 1, 2] type: int)
   * }
   * </pre>
   *
   * @param data the data
   * @return a 2d-array or a 1d-view
   * @throws java.lang.IllegalArgumentException if the array has more than 2 dimensions
   */
  <T extends BaseArray<T>> T diag(T data);

  /**
   * Return a row vector of evenly spaced values
   *
   * @param start start value
   * @param end end value
   * @param step step size
   * @return a new row vector
   */
  Range range(int start, int end, int step);

  /**
   * Return a row vector of evenly spaced values (step = 1)
   *
   * @param start start value
   * @param end end value
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
   * @param end end value
   * @param size the size of the returned vector
   * @return a new row vector
   */
  DoubleArray linspace(double start, double end, int size);

  DoubleArray eye(int size);
}
