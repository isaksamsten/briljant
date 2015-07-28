package org.briljantframework.array.api;

import org.briljantframework.complex.Complex;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.BaseArray;
import org.briljantframework.array.Op;
import org.briljantframework.sort.IndexComparator;

import java.util.Collection;
import java.util.List;

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
   * @param x   the matrix
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
   * @param x   the matrix
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
   * @param x   the matrix
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

  /**
   * Returns the minimum value of {@code x} along {@code dim}.
   *
   * @param dim the dimension
   * @param x   the matrix
   * @return a matrix of minimum values
   */
  DoubleArray min(int dim, DoubleArray x);

  /**
   * Returns the maximum value of {@code x}.
   *
   * @param x the matrix
   * @return the maximum value
   */
  double max(DoubleArray x);

  /**
   * Returns the maximum value of {@code x} along {@code dim}.
   *
   * @param dim the dimension
   * @param x   the matrix
   * @return a matrix of minimum values
   */
  DoubleArray max(int dim, DoubleArray x);

  /**
   * Return the sum of {@code x}.
   *
   * @param x the matrix
   * @return the sum
   */
  double sum(DoubleArray x);

  /**
   * Returns the sum of {@code x} along {@code dim}.
   *
   * @param dim the dimension
   * @param x   the matrix
   * @return a matrix of sums
   */
  DoubleArray sum(int dim, DoubleArray x);

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
   * @param x   the matrix
   * @return a matrix of products
   */
  DoubleArray prod(int dim, DoubleArray x);

  DoubleArray cumsum(DoubleArray x);

  DoubleArray cumsum(DoubleArray x, int dim);

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
   * @param x     the matrix x
   * @param y     the matrix y
   */
  void axpy(double alpha, DoubleArray x, DoubleArray y);

  /**
   * Compute y <- alpha*op(a)*x + beta * y (general matrix vector multiplication)
   *
   * @param transA the operation op(.)
   * @param alpha  the scalar alpha
   * @param a      the matrix a
   * @param x      the vector x
   * @param beta   the scalar beta
   * @param y      the vector y
   */
  void gemv(Op transA, double alpha, DoubleArray a, DoubleArray x, double beta,
            DoubleArray y);

  /**
   * Computes a <- alpha*x*y'+a
   *
   * @param alpha a scalar
   * @param x     a {@code m} element vector
   * @param y     a {@code n} element vector
   * @param a     a {@code [m, n]} matrix
   */
  void ger(double alpha, DoubleArray x, DoubleArray y, DoubleArray a);

  /**
   * Computes c <- alpha * transA(a) * transB(b) + beta * c
   *
   * @param transA transpose of a
   * @param transB transpose of b
   * @param alpha  the scalar for a
   * @param a      the matrix a
   * @param b      the matrix b
   * @param beta   the scalar for c
   * @param c      the result matrix c
   */
  void gemm(Op transA, Op transB, double alpha, DoubleArray a, DoubleArray b,
            double beta, DoubleArray c);

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
   * Split matrix vertically (i.e. row-wise). A 3-by-3 matrix hsplit into 3 parts
   * return a (lazy) list of 3 1-by-3 matrices.
   *
   * <p>The returned list is lazy, i.e. no splitting is done before {@link List#get(int)} is
   * called. To get a computed list, use {@code new ArrayList<>(Matrices.hsplit(m, 3))}.
   * This is useful when {@link List#get(int)} is used multiple times.
   *
   * @param matrix matrix to be split
   * @param parts  parts to split matrix (must evenly devide {@code matrix.columns()})
   * @param <T>    the matrix type
   * @return a (lazy) list of {@code part} elements
   */
  <T extends BaseArray<T>> List<T> vsplit(T matrix, int parts);

  /**
   * Stacks matrices vertically, i.e. a 2-by-3 matrix vstacked with a 10-by-3 matrix
   * resuls in a 12-by-3 matrix.
   *
   * @param matrices a sequence of matrices; all having the same {@code columns}
   * @param <T>      the matrix type
   * @return a new matrix; {@code shape = [sum-of-rows, columns]}
   */
  <T extends BaseArray<T>> T vstack(Collection<T> matrices);

  /**
   * Split matrix horizontally (i.e. column-wise). A 3-by-3 matrix hsplit into 3 parts
   * return a (lazy) list of 3 3-by-1 matrices.
   *
   * <p>The returned list is lazy, i.e. no splitting is done before {@link List#get(int)} is
   * called. To get a computed list, use {@code new ArrayList<>(Matrices.hsplit(m, 3))}.
   * This is useful when {@link List#get(int)} is used multiple times.
   *
   * @param matrix matrix to be split
   * @param parts  parts to split matrix (must evenly divide {@code matrix.columns()})
   * @param <T>    the matrix type
   * @return a (lazy) list of {@code part} elements
   */
  <T extends BaseArray<T>> List<T> hsplit(T matrix, int parts);

  /**
   * Stacks matrices horizontally, i.e. a 3-by-2 matrix hstacked with a 3-by-10 matrix
   * results in a 3-by-12 matrix.
   *
   * @param matrices a sequence of matrices; all having the same {@code rows}
   * @param <T>      the matrix type
   * @return a new matrix; {@code shape = [rows, sum-of-columns]}
   */
  <T extends BaseArray<T>> T hstack(Collection<T> matrices);

  <T extends BaseArray<T>> T shuffle(T x);

  /**
   * <p>
   * Sorts the source matrix {@code a} in the order specified by {@code comparator}. For example,
   * reversed sorted
   * </p>
   *
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    DoubleMatrix a = Matrices.randn(12, 1)
   *    DoubleMatrix x = Matrices.sort(a, (c, i, j) -> -c.compare(a, b));
   * </pre>
   * <p>
   * {@link org.briljantframework.complex.Complex} and {@link org.briljantframework.array.ComplexArray}
   * do not have a natural
   * sort order.
   * </p>
   *
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    ComplexMatrix a = randn(12, 1).asComplexMatrix().map(Complex::sqrt)
   *    ComplexMatrix x = sort(a, (c, i, j) -> Double.compare(c.get(i).abs(), c.get(j).abs());
   *
   *    0.1499 + 0.0000i
   *    0.5478 + 0.0000i
   *    0.5725 + 0.0000i
   *    0.0000 + 0.5916i
   *    0.0000 + 0.6856i
   *    0.0000 + 0.8922i
   *    0.0000 + 0.9139i
   *    0.0000 + 1.0130i
   *    0.0000 + 1.1572i
   *    1.1912 + 0.0000i
   *    1.2493 + 0.0000i
   *    1.2746 + 0.0000i
   *    shape: 12x1 type: complex
   * </pre>
   *
   * @param x   the source matrix
   * @param cmp the comparator; first argument is the container, and the next are indexes
   * @return a new sorted matrix; the returned matrix has the same type as {@code a}
   */
  <T extends BaseArray<T>> T sort(T x, IndexComparator<T> cmp);

  <T extends BaseArray<T>> T sort(T x, IndexComparator<T> cmp, int dim);

  /**
   * Copy the contents of {@code from} to {@code to}
   *
   * @param from a matrix
   * @param to   a matrix
   * @param <T>  the matrix type
   */
  <T extends BaseArray<T>> void copy(T from, T to);

  /**
   * Swap the data of {@code a} and {@code b}
   *
   * @param a   a matrix
   * @param b   a matrix
   * @param <T> the matrix type
   */

  <T extends BaseArray<T>> void swap(T a, T b);
}
