/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.array.Array;
import org.briljantframework.array.BaseArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.LongArray;
import org.briljantframework.array.Op;
import org.briljantframework.sort.IndexComparator;

/**
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
   * Returns the minimum value of {@code x}.
   *
   * @param x the matrix
   * @return the minimum value
   */
  double min(DoubleArray x);

  int min(IntArray x);

  long min(LongArray x);

  <T extends Comparable<T>> T min(Array<T> x);

  <T> T min(Array<T> x, Comparator<T> cmp);

  /**
   * Returns the minimum value of {@code x} along {@code dim}.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of minimum values
   */
  DoubleArray min(int dim, DoubleArray x);

  IntArray min(int dim, IntArray x);

  LongArray min(int dim, LongArray x);

  <T extends Comparable<T>> Array<T> min(int dim, Array<T> x);

  <T> Array<T> min(int dim, Array<T> x, Comparator<T> cmp);


  /**
   * Returns the maximum value of {@code x}.
   *
   * @param x the matrix
   * @return the maximum value
   */
  double max(DoubleArray x);

  int max(IntArray x);

  long max(LongArray x);

  <T extends Comparable<T>> T max(Array<T> x);

  <T> T max(Array<T> x, Comparator<T> cmp);

  /**
   * Returns the maximum value of {@code x} along {@code dim}.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of minimum values
   */
  DoubleArray max(int dim, DoubleArray x);

  IntArray max(int dim, IntArray x);

  LongArray max(int dim, LongArray x);

  <T extends Comparable<T>> Array<T> max(int dim, Array<T> x);

  <T> Array<T> max(int dim, Array<T> x, Comparator<T> cmp);

  /**
   * Return the sum of {@code x}.
   *
   * @param x the matrix
   * @return the sum
   */
  double sum(DoubleArray x);

  int sum(IntArray x);

  /**
   * Returns the sum of {@code x} along {@code dim}.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of sums
   */
  DoubleArray sum(int dim, DoubleArray x);

  IntArray sum(int dim, IntArray x);

  /**
   * Returns the product of {@code x}.
   *
   * @param x the matrix
   * @return the product
   */
  double prod(DoubleArray x);

  /**
   * Returns the products of {@code x} along {@code dim}.
   *
   * @param dim the dimension
   * @param x the matrix
   * @return a matrix of products
   */
  DoubleArray prod(int dim, DoubleArray x);

  DoubleArray cumsum(DoubleArray x);

  DoubleArray cumsum(int dim, DoubleArray x);

  double dot(DoubleArray a, DoubleArray b);

  Complex dotu(ComplexArray a, ComplexArray b);

  Complex dotc(ComplexArray a, ComplexArray b);

  double norm2(DoubleArray a);

  Complex norm2(ComplexArray a);

  double asum(DoubleArray a);

  double asum(ComplexArray a);

  int iamax(DoubleArray x);

  int iamax(ComplexArray x);

  void scal(double alpha, DoubleArray x);

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
   * Compute y <- alpha*op(a)*x + beta * y (general matrix vector multiplication)
   *
   * @param transA the operation op(.)
   * @param alpha the scalar alpha
   * @param a the matrix a
   * @param x the vector x
   * @param beta the scalar beta
   * @param y the vector y
   */
  void gemv(Op transA, double alpha, DoubleArray a, DoubleArray x, double beta, DoubleArray y);

  /**
   * Computes a <- alpha*x*y'+a
   *
   * @param alpha a scalar
   * @param x a {@code m} element vector
   * @param y a {@code n} element vector
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
  void gemm(Op transA, Op transB, double alpha, DoubleArray a, DoubleArray b, double beta,
      DoubleArray c);

  /**
   * Return a matrix containing {@code n} copies of {@code x}.
   *
   * @param x the matrix
   * @param n the repetitions of both rows and columns
   * @return a new matrix
   */
  <T extends BaseArray<T>> T repmat(T x, int n);

  <T extends BaseArray<T>> T repmat(T x, int r, int c);

  <T extends BaseArray<T>> T repeat(T x, int num);

  <T extends BaseArray<T>> T take(T x, int num);

  /**
   * Split array vertically (i.e. row-wise). A 3-by-3 array hsplit into 3 parts return a (lazy) list
   * of 3 1-by-3 matrices.
   *
   * <p>
   * The returned list is lazy, i.e. no splitting is done before {@link List#get(int)} is called. To
   * get a computed list, use {@code new ArrayList<>(Matrices.hsplit(m, 3))}. This is useful when
   * {@link List#get(int)} is used multiple times.
   *
   * @param array array to be split
   * @param parts parts to split array (must evenly divide {@code array.columns()})
   * @param <T> the array type
   * @return a (lazy) list of {@code part} elements
   */
  <T extends BaseArray<T>> List<T> vsplit(T array, int parts);

  /**
   * Stacks arrays vertically, i.e. a 2-by-3 matrix vstacked with a 10-by-3 matrix resuls in a
   * 12-by-3 matrix.
   *
   * @param arrays a sequence of arrays; all having the same {@code columns}
   * @param <T> the matrix type
   * @return a new matrix; {@code shape = [sum-of-rows, columns]}
   */
  <T extends BaseArray<T>> T vstack(Collection<T> arrays);

  /**
   * Split array horizontally (i.e. column-wise). A 3-by-3 array hsplit into 3 parts return a (lazy)
   * list of 3 3-by-1 matrices.
   *
   * <p>
   * The returned list is lazy, i.e. no splitting is done before {@link List#get(int)} is called. To
   * get a computed list, use {@code new ArrayList<>(Arrays.hsplit(m, 3))}. This is useful when
   * {@link List#get(int)} is used multiple times.
   *
   * @param array array to be split
   * @param parts parts to split array (must evenly divide {@code array.columns()})
   * @param <T> the array type
   * @return a (lazy) list of {@code part} elements
   */
  <T extends BaseArray<T>> List<T> hsplit(T array, int parts);

  /**
   * Stacks arrays horizontally, i.e. a 3-by-2 matrix hstacked with a 3-by-10 matrix results in a
   * 3-by-12 matrix.
   *
   * @param arrays a sequence of arrays; all having the same {@code rows}
   * @param <T> the matrix type
   * @return a new matrix; {@code shape = [rows, sum-of-columns]}
   */
  <T extends BaseArray<T>> T hstack(Collection<T> arrays);

  <T extends BaseArray<T>> T shuffle(T x);

  default <T extends BaseArray<T>> T sort(T array) {
    return sort(array, (t, a, b) -> {
      return t.compare(a, b);
    });
  }

  default <T extends BaseArray<T>> T sort(int dim, T array) {
    return sort(dim, array, (t, a, b) -> {
      return t.compare(a, b);
    });
  }

  /**
   * <p>
   * Sorts the source matrix {@code a} in the order specified by {@code comparator}.
   * </p>
   *
   * @param x the source matrix
   * @param cmp the comparator; first argument is the container, and the next are indexes
   * @return a new sorted matrix; the returned matrix has the same type as {@code a}
   */
  <T extends BaseArray<T>> T sort(T x, IndexComparator<T> cmp);

  <T extends BaseArray<T>> T sort(int dim, T x, IndexComparator<T> cmp);

  default <T extends Comparable<T>> Array<T> sort(Array<T> array) {
    return sort(array, (a, i, j) -> a.get(i).compareTo(a.get(j)));
  }

  default <T extends Comparable<T>> Array<T> sort(int dim, Array<T> array) {
    return sort(dim, array, (a, i, j) -> a.get(i).compareTo(a.get(j)));
  }

  default <T> Array<T> sort(Array<T> array, Comparator<T> cmp) {
    return sort(array, (a, i, j) -> cmp.compare(a.get(i), a.get(j)));
  }

  default <T> Array<T> sort(int dim, Array<T> array, Comparator<T> cmp) {
    return sort(dim, array, (a, i, j) -> cmp.compare(a.get(i), a.get(j)));
  }

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
   * Rounds the number to the next largest integer, rounding is applied separately to the real and
   * the imaginary parts.
   *
   * @param array the array to ceil
   * @return a new array
   * @see Math#ceil(double)
   */
  ComplexArray ceil(ComplexArray array);

  /**
   * @see Math#floor(double)
   */
  DoubleArray floor(DoubleArray array);

  /**
   * /** Rounds the number to the next smallest integer, rounding is applied separately to the real
   * and the imaginary parts.
   *
   * @param array the array to ceil
   * @return a new array
   * @see Math#floor(double)
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
