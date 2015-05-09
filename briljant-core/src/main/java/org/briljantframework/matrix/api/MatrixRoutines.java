package org.briljantframework.matrix.api;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Dim;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.T;
import org.briljantframework.sort.IndexComparator;

import java.util.Collection;
import java.util.List;

/**
 * @author Isak Karlsson
 */
public interface MatrixRoutines {

  /**
   * Transposes {@code x} in-place with O(1) storage. Since the dimensions of a matrix cannot
   * change, {@link Matrix#reshape(int, int)} must be subsequently called.
   *
   * <p>For example,
   *
   * <pre>
   * DoubleMatrix a = Bj.matrix(new double[][]{
   *    new double[]{1, 2, 3},
   *    new double[]{1, 2, 3}
   * });
   * Bj.transpose(a)
   * a.reshape(3,2)
   * </pre>
   *
   * <p><b>Note:</b> In most cases {@link org.briljantframework.matrix.Matrix#transpose()} should
   * be preferred. However, this method can useful when memory is a concern.
   *
   * @param x (input/output) the matrix to transpose
   */
  void transpose(DoubleMatrix x);

  /**
   * Computes the mean of {@code x}
   *
   * @param x the matrix
   * @return the mean
   */
  double mean(DoubleMatrix x);

  /**
   * Computes the mean of {@code x} along {@code dim}
   *
   * @param x   the matrix
   * @param dim the dimension
   * @return a matrix of means
   */
  DoubleMatrix mean(DoubleMatrix x, Dim dim);

  /**
   * Computes the (population) variance of {@code x}.
   *
   * @param x the matrix
   * @return the variance
   */
  double var(DoubleMatrix x);

  /**
   * Computes the (population) variance of {@code x} along {@code dim}.
   *
   * @param x   the matrix
   * @param dim the dimension
   * @return a matrix of variances
   */
  DoubleMatrix var(DoubleMatrix x, Dim dim);

  /**
   * Computes the (population) standard deviation of {@code x}.
   *
   * @param x the matrix
   * @return the standard deviation
   */
  double std(DoubleMatrix x);

  /**
   * Computes the (population) standard deviation of {@code x} along {@code dim}.
   *
   * @param x   the matrix
   * @param dim the dimension
   * @return a matrix of standard deviations
   */
  DoubleMatrix std(DoubleMatrix x, Dim dim);

  /**
   * Returns the minimum value of {@code x}.
   *
   * @param x the matrix
   * @return the minimum value
   */
  double min(DoubleMatrix x);

  /**
   * Returns the minimum value of {@code x} along {@code dim}.
   *
   * @param x   the matrix
   * @param dim the dimension
   * @return a matrix of minimum values
   */
  DoubleMatrix min(DoubleMatrix x, Dim dim);

  /**
   * Returns the maximum value of {@code x}.
   *
   * @param x the matrix
   * @return the maximum value
   */
  double max(DoubleMatrix x);

  /**
   * Returns the maximum value of {@code x} along {@code dim}.
   *
   * @param x   the matrix
   * @param dim the dimension
   * @return a matrix of minimum values
   */
  DoubleMatrix max(DoubleMatrix x, Dim dim);

  /**
   * Return the sum of {@code x}.
   *
   * @param x the matrix
   * @return the sum
   */
  double sum(DoubleMatrix x);

  /**
   * Returns the sum of {@code x} along {@code dim}.
   *
   * @param x   the matrix
   * @param dim the dimension
   * @return a matrix of sums
   */
  DoubleMatrix sum(DoubleMatrix x, Dim dim);

  /**
   * Returns the product of {@code x}.
   *
   * @param x the matrix
   * @return the product
   */
  double prod(DoubleMatrix x);

  /**
   * Returns the products of {@code x} along {@code dim}.
   *
   * @param x   the matrix
   * @param dim the dimension
   * @return a matrix of products
   */
  DoubleMatrix prod(DoubleMatrix x, Dim dim);

  DoubleMatrix cumsum(DoubleMatrix x);

  DoubleMatrix cumsum(DoubleMatrix x, Dim dim);

  double dot(DoubleMatrix a, DoubleMatrix b);

  Complex dotu(ComplexMatrix a, ComplexMatrix b);

  Complex dotc(ComplexMatrix a, ComplexMatrix b);

  double nrm2(DoubleMatrix a);

  Complex norm2(ComplexMatrix a);

  double asum(DoubleMatrix a);

  double asum(ComplexMatrix a);

  int iamax(DoubleMatrix x);

  int iamax(ComplexMatrix x);

  void scal(double alpha, DoubleMatrix x);

  /**
   * Compute y <- alpha*x+y
   *
   * @param alpha the scalar
   * @param x     the matrix x
   * @param y     the matrix y
   */
  void axpy(double alpha, DoubleMatrix x, DoubleMatrix y);

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
  void gemv(T transA, double alpha, DoubleMatrix a, DoubleMatrix x, double beta,
            DoubleMatrix y);

  /**
   * Computes a <- alpha*x*y'+a
   *
   * @param alpha a scalar
   * @param x     a {@code m} element vector
   * @param y     a {@code n} element vector
   * @param a     a {@code [m, n]} matrix
   */
  void ger(double alpha, DoubleMatrix x, DoubleMatrix y, DoubleMatrix a);

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
  void gemm(T transA, T transB, double alpha, DoubleMatrix a, DoubleMatrix b,
            double beta, DoubleMatrix c);

  /**
   * Return a matrix containing {@code n} copies of {@code x}.
   *
   * @param x the matrix
   * @param n the repetitions of both rows and columns
   * @return a new matrix
   */
  <T extends Matrix<T>> T repmat(T x, int n);

  <T extends Matrix<T>> T repmat(T x, int r, int c);

  <T extends Matrix<T>> T repeat(T x, int num);

  <T extends Matrix<T>> T take(T x, int num);

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
  <T extends Matrix<T>> List<T> vsplit(T matrix, int parts);

  /**
   * Stacks matrices vertically, i.e. a 2-by-3 matrix vstacked with a 10-by-3 matrix
   * resuls in a 12-by-3 matrix.
   *
   * @param matrices a sequence of matrices; all having the same {@code columns}
   * @param <T>      the matrix type
   * @return a new matrix; {@code shape = [sum-of-rows, columns]}
   */
  <T extends Matrix<T>> T vstack(Collection<T> matrices);

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
  <T extends Matrix<T>> List<T> hsplit(T matrix, int parts);

  /**
   * Stacks matrices horizontally, i.e. a 3-by-2 matrix hstacked with a 3-by-10 matrix
   * results in a 3-by-12 matrix.
   *
   * @param matrices a sequence of matrices; all having the same {@code rows}
   * @param <T>      the matrix type
   * @return a new matrix; {@code shape = [rows, sum-of-columns]}
   */
  <T extends Matrix<T>> T hstack(Collection<T> matrices);

  <T extends Matrix<T>> T shuffle(T x);

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
   * {@link org.briljantframework.complex.Complex} and {@link org.briljantframework.matrix.ComplexMatrix}
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
  <T extends Matrix<T>> T sort(T x, IndexComparator<T> cmp);

  <T extends Matrix<T>> T sort(T x, IndexComparator<T> cmp, Dim dim);

  /**
   * Copy the contents of {@code from} to {@code to}
   *
   * @param from a matrix
   * @param to   a matrix
   * @param <T>  the matrix type
   */
  <T extends Matrix<T>> void copy(T from, T to);

  /**
   * Swap the data of {@code a} and {@code b}
   *
   * @param a   a matrix
   * @param b   a matrix
   * @param <T> the matrix type
   */

  <T extends Matrix<T>> void swap(T a, T b);
}
