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
package org.briljantframework.array.jcuda;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.array.*;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class JCudaArrayFactory implements ArrayFactory {

  private final ArrayBackend backend;

  protected JCudaArrayFactory(ArrayBackend backend) {
    this.backend = backend;
  }

  @Override
  public <T> Array<T> newVector(T[] data) {
    return null;
  }

  @Override
  public <T> Array<T> newMatrix(T[][] data) {
    return null;
  }

  @Override
  public <T> Array<T> newArray(int... shape) {
    return null;
  }

  @Override
  public BooleanArray newBooleanMatrix(boolean[][] data) {
    return null;
  }

  @Override
  public BooleanArray newBooleanVector(boolean... data) {
    return null;
  }

  @Override
  public BooleanArray newBooleanArray(int... shape) {
    return null;
  }

  @Override
  public IntArray newIntMatrix(int[][] data) {
    return null;
  }

  @Override
  public IntArray newIntVector(int... data) {
    return null;
  }

  @Override
  public IntArray newIntArray(int... shape) {
    return null;
  }

  @Override
  public LongArray newLongMatrix(long[][] data) {
    return null;
  }

  @Override
  public LongArray newLongVector(long... data) {
    return null;
  }

  @Override
  public LongArray newLongArray(int... shape) {
    return null;
  }

  @Override
  public DoubleArray newDoubleMatrix(double[][] data) {
    return null;
  }

  @Override
  public DoubleArray newDoubleVector(double... data) {
    return null;
  }

  @Override
  public DoubleArray newDoubleArray(int... shape) {
    return new JCudaDoubleArray(backend, shape);
  }

  @Override
  public ComplexArray newComplexMatrix(Complex[][] data) {
    return null;
  }

  @Override
  public ComplexArray newComplexVector(Complex... data) {
    return null;
  }

  @Override
  public ComplexArray newComplexVector(double... data) {
    return null;
  }

  @Override
  public ComplexArray newComplexArray(int... shape) {
    return null;
  }

  @Override
  public DoubleArray randn(int size) {
    return null;
  }

  @Override
  public DoubleArray rand(int size) {
    return null;
  }

  @Override
  public DoubleArray ones(int... shape) {
    return null;
  }

  @Override
  public Range range(int start, int end, int step) {
    return null;
  }

  @Override
  public Range range(int start, int end) {
    return null;
  }

  @Override
  public Range range(int end) {
    return null;
  }

  @Override
  public DoubleArray linspace(double start, double end, int size) {
    return null;
  }

  @Override
  public DoubleArray eye(int size) {
    return null;
  }
}
