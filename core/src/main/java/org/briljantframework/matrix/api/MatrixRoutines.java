package org.briljantframework.matrix.api;

import org.briljantframework.IndexComparator;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Dim;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.Transpose;

import java.util.Collection;
import java.util.List;

/**
 * @author Isak Karlsson
 */
public interface MatrixRoutines {

  MatrixFactory getMatrixFactory();

  /**
   * Compute the mean of {@code x}
   *
   * @param x the matrix
   * @return the mean
   */
  double mean(DoubleMatrix x);

  DoubleMatrix mean(DoubleMatrix x, Dim dim);

  double var(DoubleMatrix x);

  DoubleMatrix var(DoubleMatrix x, Dim dim);

  double std(DoubleMatrix x);

  DoubleMatrix std(DoubleMatrix x, Dim dim);

  double min(DoubleMatrix x);

  DoubleMatrix min(DoubleMatrix x, Dim dim);

  double max(DoubleMatrix x);

  DoubleMatrix max(DoubleMatrix x, Dim dim);

  double sum(DoubleMatrix x);

  DoubleMatrix sum(DoubleMatrix x, Dim dim);

  double prod(DoubleMatrix x);

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
  void gemv(Transpose transA, double alpha, DoubleMatrix a, DoubleMatrix x, double beta,
            DoubleMatrix y);

  /*
    Compute
   */
  void ger(double alpha, DoubleMatrix x, DoubleMatrix y, DoubleMatrix a);

  /*
    Compute c <- alpha * a * b + beta * c
   */
  void gemm(Transpose transA, Transpose transB, double alpha, DoubleMatrix a, DoubleMatrix b,
            double beta, DoubleMatrix c);


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
