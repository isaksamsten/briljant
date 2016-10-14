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

import java.util.Comparator;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.array.*;

/**
 * Array routines perform different operations on arrays.
 * 
 * @author Isak Karlsson
 */
public interface ArrayRoutines {

  /**
   * Computes the mean of {@code x}
   *
   * @param x the matrix
   * @return the mean
   */
  double mean(DoubleArray x);

  /**
   * Computes the mean of {@code x} along {@code dim}
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of means
   */
  DoubleArray mean(int dim, DoubleArray x);

  /**
   * Computes the (population) variance of {@code x}.
   *
   * @param x the matrix
   * @return the variance
   */
  double var(DoubleArray x);

  /**
   * Computes the (population) variance of {@code x} along {@code dim}.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of variances
   */
  DoubleArray var(int dim, DoubleArray x);

  /**
   * Computes the (population) standard deviation of {@code x}.
   *
   * @param x the matrix
   * @return the standard deviation
   */
  double std(DoubleArray x);

  /**
   * Computes the (population) standard deviation of {@code x} along {@code dim}.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of standard deviations
   */
  DoubleArray std(int dim, DoubleArray x);

  /**
   * Returns the minimum value.
   *
   * @param x the matrix
   * @return the minimum value
   */
  double min(DoubleArray x);

  /**
   * Returns the minimum value.
   *
   * @param x the matrix
   * @return the minimum value
   */
  int min(IntArray x);

  /**
   * Returns the minimum value.
   *
   * @param x the matrix
   * @return the minimum value
   */
  long min(LongArray x);

  /**
   * Returns the minimum value.
   *
   * @param x the matrix
   * @return the minimum value
   */
  <T extends Comparable<T>> T min(Array<T> x);

  /**
   * Returns the minimum value.
   *
   * @param x the matrix
   * @param cmp the comparator
   * @return the minimum value
   */
  <T> T min(Array<T> x, Comparator<T> cmp);

  /**
   * Returns the minimum value along the specified dimension.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of minimum values
   */
  DoubleArray min(int dim, DoubleArray x);

  /**
   * Returns the minimum value along the specified dimension.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of minimum values
   */
  IntArray min(int dim, IntArray x);

  /**
   * Returns the minimum value along the specified dimension.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of minimum values
   */
  LongArray min(int dim, LongArray x);

  /**
   * Returns the minimum value along the specified dimension.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of minimum values
   */
  <T extends Comparable<T>> Array<T> min(int dim, Array<T> x);

  /**
   * Returns the minimum value along the specified dimension.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of minimum values
   */
  <T> Array<T> min(int dim, Array<T> x, Comparator<T> cmp);

  /**
   * Returns the maximum value.
   *
   * @param x the matrix
   * @return the maximum value
   */
  double max(DoubleArray x);

  /**
   * Returns the maximum value.
   *
   * @param x the matrix
   * @return the maximum value
   */
  int max(IntArray x);

  /**
   * Returns the maximum value.
   *
   * @param x the matrix
   * @return the maximum value
   */
  long max(LongArray x);

  /**
   * Returns the maximum value.
   *
   * @param x the matrix
   * @return the maximum value
   */
  <T extends Comparable<T>> T max(Array<T> x);

  /**
   * Returns the maximum value.
   *
   * @param x the matrix
   * @param cmp the comparator
   * @return the maximum value
   */
  <T> T max(Array<T> x, Comparator<T> cmp);

  /**
   * Returns the maximum value along the specified dimension.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of minimum values
   */
  DoubleArray max(int dim, DoubleArray x);

  /**
   * Returns the maximum value along the specified dimension.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of minimum values
   */
  IntArray max(int dim, IntArray x);

  /**
   * Returns the maximum value along the specified dimension.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of minimum values
   */
  LongArray max(int dim, LongArray x);

  /**
   * Returns the maximum value along the specified dimension.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of minimum values
   */
  <T extends Comparable<T>> Array<T> max(int dim, Array<T> x);

  /**
   * Returns the maximum value along the specified dimension.
   *
   * @param dim the dimension
   * @param x the matrix
   * @param cmp the comparator
   * @return a matrix of minimum values
   */
  <T> Array<T> max(int dim, Array<T> x, Comparator<T> cmp);

  /**
   * Return the sum.
   *
   * @param x the array
   * @return the sum
   */
  double sum(DoubleArray x);

  /**
   * Return the sum.
   *
   * @param x the array
   * @return the sum
   */
  int sum(IntArray x);

  /**
   * Return the sum.
   *
   * @param x the array
   * @return the sum
   */
  long sum(LongArray x);

  /**
   * Return the sum.
   *
   * @param x the array
   * @return the sum
   */
  Complex sum(ComplexArray x);

  /**
   * Returns the sum along the specified dimension.
   *
   * @param dim the dimension
   * @param x the array
   * @return an array of sums
   */
  DoubleArray sum(int dim, DoubleArray x);

  /**
   * Returns the sum along the specified dimension.
   *
   * @param dim the dimension
   * @param x the array
   * @return an array of sums
   */
  IntArray sum(int dim, IntArray x);

  /**
   * Returns the sum along the specified dimension.
   *
   * @param dim the dimension
   * @param x the array
   * @return an array of sums
   */
  LongArray sum(int dim, LongArray x);

  /**
   * Returns the sum along the specified dimension.
   *
   * @param dim the dimension
   * @param x the array
   * @return an array of sums
   */
  ComplexArray sum(int dim, ComplexArray x);

  /**
   * Returns the product.
   *
   * @param x the array
   * @return the product
   */
  double prod(DoubleArray x);

  /**
   * Returns the products along the specified dimension.
   *
   * @param dim the dimension
   * @param x the array
   * @return an array of products
   */
  DoubleArray prod(int dim, DoubleArray x);

  /**
   * Return the cumulative sum.
   * 
   * @param x the array
   * @return an array of cumulative sums
   */
  DoubleArray cumsum(DoubleArray x);


  /**
   * Return the cumulative sum along the specified dimension.
   * 
   * @param x the array
   * @return an array of cumulative sums
   */
  DoubleArray cumsum(int dim, DoubleArray x);

  /**
   * Return the inner product of two vectors.
   * 
   * @param a the first series
   * @param b the second series
   * @return the inner product
   * @see org.briljantframework.array.Arrays#inner(DoubleArray, DoubleArray)
   */
  double inner(DoubleArray a, DoubleArray b);

  /**
   * Return the inner product of two vectors.
   *
   * @param a the first series
   * @param b the second series
   * @return the inner product
   * @see org.briljantframework.array.Arrays#inner(ComplexArray, ComplexArray)
   * @see #conjugateInner(ComplexArray, ComplexArray)
   */

  Complex inner(ComplexArray a, ComplexArray b);

  /**
   * Return the dot product of two vectors. The complex conjugate of the first argument is used for
   * the calculation of the dot product.
   * 
   * @param a the first series
   * @param b the second series
   * @return the inner product
   * @see org.briljantframework.array.Arrays#conjugateInner(ComplexArray, ComplexArray)
   */
  Complex conjugateInner(ComplexArray a, ComplexArray b);

  /**
   * Return the square norm.
   * 
   * @param a the array
   * @return the norm
   */
  double norm2(DoubleArray a);

  DoubleArray norm2(int dim, DoubleArray a);

  /**
   * Return the square norm.
   *
   * @param a the array
   * @return the norm
   */
  Complex norm2(ComplexArray a);

  /**
   * Return sum of absolute values.
   * 
   * @param a the array
   * @return the sum
   */
  double asum(DoubleArray a);

  /**
   * Return sum of absolute values.
   * 
   * @param a the array
   * @return the sum
   */
  double asum(ComplexArray a);

  /**
   * Return the index of the value with the largest absolute value.
   * 
   * @param x the array
   * @return the index
   */
  int iamax(DoubleArray x);

  /**
   * Return the index of the value with the largest absolute value.
   *
   * @param x the array
   * @return the index
   */
  int iamax(ComplexArray x);

  /**
   * Scale the array with the specified scalar value.
   * 
   * @param alpha the scalar
   * @param x the array
   */
  void scal(double alpha, DoubleArray x);

  /**
   * Return the trace (diagonal sum) of the array.
   * 
   * @param x the array
   * @return the diagonal sum
   */
  double trace(DoubleArray x);

  /**
   * Compute y <- alpha*x+y
   *
   * @param alpha the scalar
   * @param x the matrix x
   * @param y the matrix y
   */
  void axpy(double alpha, DoubleArray x, DoubleArray y);

  /**
   * Compute y <- alpha*op(a)*x + beta * y (general matrix series multiplication)
   *
   * @param transA the operation op(.)
   * @param alpha the scalar alpha
   * @param a the matrix a
   * @param x the series x
   * @param beta the scalar beta
   * @param y the series y
   */
  void gemv(ArrayOperation transA, double alpha, DoubleArray a, DoubleArray x, double beta,
      DoubleArray y);

  /**
   * Computes a <- alpha*x*y'+a
   *
   * @param alpha a scalar
   * @param x a {@code m} element series
   * @param y a {@code n} element series
   * @param a a {@code [m, n]} matrix
   */
  void ger(double alpha, DoubleArray x, DoubleArray y, DoubleArray a);

  /**
   * Computes c <- alpha * transA(a) * transB(b) + beta * c
   *
   * @param transA transpose of a
   * @param transB transpose of b
   * @param alpha the scalar for a
   * @param a the matrix a
   * @param b the matrix b
   * @param beta the scalar for c
   * @param c the result matrix c
   */
  void gemm(ArrayOperation transA, ArrayOperation transB, double alpha, DoubleArray a,
      DoubleArray b, double beta, DoubleArray c);

  /**
   * Copy the contents of {@code from} to {@code to}
   *
   * @param from a matrix
   * @param to a matrix
   * @param <T> the matrix type
   */
  <T extends BaseArray<T>> void copy(T from, T to);

  /**
   * Swap the data of {@code a} and {@code b}
   *
   * @param a a matrix
   * @param b a matrix
   * @param <T> the matrix type
   */

  <T extends BaseArray<T>> void swap(T a, T b);

  DoubleArray plus(DoubleArray a, DoubleArray b);

  /**
   * out <- a + out
   * 
   * @param a
   * @param b
   * @param out
   */
  void plus(DoubleArray a, DoubleArray b, DoubleArray out);

  DoubleArray minus(DoubleArray a, DoubleArray b);

  /**
   * out <- a - out
   *  @param a
   * @param b
   * @param out
   */
  void minus(DoubleArray a, DoubleArray b, DoubleArray out);

  DoubleArray times(DoubleArray a, DoubleArray b);

  /**
   * out <- a * out
   * 
   * @param a
   * @param out
   */
  void times(DoubleArray a, DoubleArray b, DoubleArray out);

  DoubleArray div(DoubleArray nominator, DoubleArray denominator);

  /**
   * out <- a / b
   *  @param a
   * @param b
   */
  void div(DoubleArray a, DoubleArray b, DoubleArray out);

  IntArray plus(IntArray a, IntArray b);

  void plusAssign(IntArray a, IntArray out);

  IntArray minus(IntArray a, IntArray b);

  void minusAssign(IntArray a, IntArray out);

  IntArray times(IntArray a, IntArray b);

  void timesAssign(IntArray a, IntArray out);

  IntArray div(IntArray a, IntArray b);

  void divAssign(IntArray a, IntArray out);

  LongArray plus(LongArray a, LongArray b);

  void plusAssign(LongArray a, LongArray out);

  LongArray minus(LongArray a, LongArray b);

  void minusAssign(LongArray a, LongArray out);

  LongArray times(LongArray a, LongArray b);

  void timesAssign(LongArray a, LongArray out);

  LongArray div(LongArray a, LongArray b);

  void divAssign(LongArray a, LongArray out);

  ComplexArray plus(ComplexArray a, ComplexArray b);

  void plusAssign(ComplexArray a, ComplexArray out);

  ComplexArray minus(ComplexArray a, ComplexArray b);

  void minusAssign(ComplexArray a, ComplexArray out);

  ComplexArray times(ComplexArray a, ComplexArray b);

  void timesAssign(ComplexArray a, ComplexArray out);

  ComplexArray div(ComplexArray a, ComplexArray b);

  void divAssign(ComplexArray a, ComplexArray out);

  BooleanArray and(BooleanArray a, BooleanArray b);

  BooleanArray or(BooleanArray a, BooleanArray b);

  BooleanArray xor(BooleanArray a, BooleanArray b);

  /**
   * @see Math#sin(double)
   */
  DoubleArray sin(DoubleArray array);

  /**
   * @see Complex#sin()
   */
  ComplexArray sin(ComplexArray array);

  /**
   * @see Math#cos(double)
   */
  DoubleArray cos(DoubleArray array);

  /**
   * @see Complex#cos()
   */
  ComplexArray cos(ComplexArray array);

  /**
   * @see Math#tan(double)
   */
  DoubleArray tan(DoubleArray array);

  /**
   * @see Complex#tan()
   */
  ComplexArray tan(ComplexArray array);

  /**
   * @see Math#asin(double)
   */
  DoubleArray asin(DoubleArray array);

  /**
   * @see Complex#asin()
   */
  ComplexArray asin(ComplexArray array);

  /**
   * @see Math#acos(double)
   */
  DoubleArray acos(DoubleArray array);

  /**
   * @see Complex#acos()
   */
  ComplexArray acos(ComplexArray array);

  /**
   * @see Math#atan(double)
   */
  DoubleArray atan(DoubleArray array);

  /**
   * @see Complex#atan()
   */
  ComplexArray atan(ComplexArray array);

  /**
   * @see Math#sinh(double)
   */
  DoubleArray sinh(DoubleArray array);

  /**
   * @see Complex#sinh()
   */
  ComplexArray sinh(ComplexArray array);

  /**
   * @see Math#cosh(double)
   */
  DoubleArray cosh(DoubleArray array);

  /**
   * @see Complex#cosh()
   */
  ComplexArray cosh(ComplexArray array);

  /**
   * @see Math#tanh(double)
   */
  DoubleArray tanh(DoubleArray array);

  /**
   * @see Complex#tanh()
   */
  ComplexArray tanh(ComplexArray array);

  /**
   * @see Math#exp(double)
   */
  DoubleArray exp(DoubleArray array);

  /**
   * @see Complex#exp()
   */
  ComplexArray exp(ComplexArray array);

  /**
   * @see Math#cbrt(double)
   */
  DoubleArray cbrt(DoubleArray array);

  /**
   * @see Math#ceil(double)
   */
  DoubleArray ceil(DoubleArray array);

  /**
   * Rounds the number to the next smallest integer, rounding is applied separately to the real and
   * the imaginary parts.
   *
   * @param array the array
   * @return a new array
   * @see Math#floor(double)
   */
  ComplexArray ceil(ComplexArray array);

  /**
   * @see Math#floor(double)
   */
  DoubleArray floor(DoubleArray array);

  /**
   * Rounds the number to the next largest integer, rounding is applied separately to the real and
   * the imaginary parts.
   *
   * @param array the array to ceil
   * @return a new array
   * @see Math#floor(double) (double)
   */
  ComplexArray floor(ComplexArray array);

  /**
   * @see Math#abs(double)
   */
  IntArray abs(IntArray array);

  /**
   * @see Math#abs(double)
   */
  LongArray abs(LongArray array);

  /**
   * @see Math#abs(double)
   */
  DoubleArray abs(DoubleArray array);

  /**
   * @see Math#cos(double)
   */
  DoubleArray abs(ComplexArray array);

  /**
   * @see Math#scalb(double, int)
   */
  DoubleArray scalb(DoubleArray array, int scaleFactor);

  /**
   * @see Math#sqrt(double)
   */
  DoubleArray sqrt(DoubleArray array);

  /**
   * @see Complex#sqrt()
   */
  ComplexArray sqrt(ComplexArray array);

  /**
   * @see Math#log(double)
   */
  DoubleArray log(DoubleArray array);

  ComplexArray log(ComplexArray array);

  /**
   * @see Math#log(double)
   */
  DoubleArray log2(DoubleArray array);

  /**
   * @see Math#pow(double, double)
   */
  DoubleArray pow(DoubleArray in, double power);

  /**
   * @see Math#log10(double)
   */
  DoubleArray log10(DoubleArray in);

  /**
   * @see Math#signum(double)
   */
  DoubleArray signum(DoubleArray in);

  /**
   * @see Math#round(double)
   */
  LongArray round(DoubleArray in);
}
