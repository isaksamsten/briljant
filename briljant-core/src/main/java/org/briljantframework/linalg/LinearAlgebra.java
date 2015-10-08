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

package org.briljantframework.linalg;

import org.briljantframework.Check;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.linalg.decomposition.LuDecomposer;
import org.briljantframework.linalg.decomposition.LuDecomposition;
import org.briljantframework.linalg.decomposition.SingularValueDecomposer;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.linalg.solve.LeastLinearSquaresSolver;

/**
 * Created by isak on 23/06/14.
 */
public class LinearAlgebra {

  /**
   * The constant MACHINE_EPSILON.
   */
  public final static double MACHINE_EPSILON = Math.ulp(1);

  /**
   * In statistics and mathematics, linear least squares is an approach fitting a mathematical or
   * statistical model to data in cases where the idealized value provided by the model for any data
   * point is expressed linearly in terms of the unknown parameters of the model. The resulting
   * fitted model can be used to summarize the data, to predict unobserved values from the same
   * system, and to understand the mechanisms that may underlie the system.
   * <p>
   * Find the <code>minimum_x || b - Ax ||_2</code>. For example when
   *
   * <pre>
   *     |  -0.09   0.14  -0.46   0.68   1.29 |          |  7.4 |
   *     |  -1.56   0.20   0.29   1.09   0.51 |          |  4.2 |
   * A = |  -1.48  -0.43   0.89  -0.71  -0.96 |, and b = | -8.3 |
   *     |  -1.09   0.84   0.77   2.11  -1.27 |          |  1.8 |
   *     |   0.08   0.55  -1.13   0.14   1.74 |          |  8.6 |
   *     |  -1.59  -0.72   1.06   1.24   0.34 |          |  2.1 |
   * </pre>
   * <p>
   * the solution is,
   * <p>
   *
   * <pre>
   *      0.6344, 0.9699, -1.4402, 3.3678, 3.3992
   * </pre>
   *
   * @param a the matrix A
   * @param b the matrix b
   * @return the solution vector <code>x</code>
   */
  public static DoubleArray leastLinearSquares(DoubleArray a, DoubleArray b) {
    return new LeastLinearSquaresSolver(a).solve(b);
  }

  /**
   * Lu lu decomposition.
   *
   * @param matrix the matrix
   * @return lu decomposition
   */
  public static LuDecomposition lu(DoubleArray matrix) {
    return new LuDecomposer().decompose(matrix);
  }


  /**
   * Pinv out.
   *
   * @param matrix the matrix
   * @return the out
   */
  public static DoubleArray pinv(DoubleArray matrix) {
    Check.argument(matrix.isMatrix());
    double[] array = new double[matrix.size()];
    pinvi(matrix, array);
    return Arrays.of(array).reshape(matrix.columns(), matrix.rows());
  }

  /**
   * Pinvi void.
   *
   * @param matrix the tensor
   * @param copy the copy
   */
  public static void pinvi(DoubleArray matrix, double[] copy) {
    SingularValueDecomposition svd = svd(matrix);
    DoubleArray diagonal = svd.getDiagonal();
    DoubleArray rightSingularValues = svd.getRightSingularValues();
    DoubleArray leftSingularValues = svd.getLeftSingularValues();

    diagonal.update(x -> x < MACHINE_EPSILON ? 0 : 1 / x);
    diagonal.transpose();
    DoubleArray s = rightSingularValues.mmul(diagonal);
    throw new UnsupportedOperationException("must be implemented");
    // Matrices.mmuli(s, Transpose.NO, leftSingularValues, Transpose.YES, copy);
  }

  /**
   * Formally, the singular value decomposition of an m×n real or complex matrix M is a
   * factorization of the form \mathbf{M} = \mathbf{U} \boldsymbol{\Sigma} \mathbf{V}^* where U is a
   * m×m real or complex unitary matrix, \Sigma is an m×n rectangular diagonal matrix with
   * nonnegative real numbers on the diagonal, and V* (the conjugate transpose of V, or simply the
   * transpose of V if V is real) is an n×n real or complex unitary matrix. The diagonal entries
   * \Sigma_{i,i} of \Sigma are known as the singular values of M. The m headers of U and the n
   * headers of V are called the left-singular vectors and right-singular vectors of M,
   * respectively.
   *
   * @param matrix the matrix
   * @return the singular value decomposition
   * @throws IllegalArgumentException if SVD fail to converge
   */
  public static SingularValueDecomposition svd(DoubleArray matrix) {
    return new SingularValueDecomposer().decompose(matrix);
  }

  /**
   * In linear algebra, the determinant is a value associated with a square matrix. It can be
   * computed from the entries of the matrix by a specific arithmetic expression, while other ways
   * to determine its value exist as well. The determinant provides important information about a
   * matrix of coefficients of a system of linear equations, or about a matrix that corresponds to a
   * linear transformation of a vector space.
   *
   * @param x a square mutable array
   * @return the determinant
   */
  public static double det(DoubleArray x) {
    if (x.isSquare()) {
      return new LuDecomposer().decompose(x).getDeterminant();
    } else {
      throw new IllegalArgumentException("argument must be a square array");
    }
  }

  /**
   * In linear algebra, the rank of a matrix A is the size of the largest collection of linearly
   * independent columns of A (the column rank) or the size of the largest collection of linearly
   * independent rows of A (the row rank). For every matrix, the column rank is equal to the row
   * rank.[1] It is a measure of the "nondegenerateness" of the system of linear equations and
   * linear transformation encoded by A. There are multiple definitions of rank. The rank is one of
   * the fundamental pieces of data associated with a matrix.
   *
   * @param x a matrix
   * @return the rank
   */
  public static double rank(DoubleArray x) {
    SingularValueDecomposition svd = new SingularValueDecomposer().decompose(x);
    DoubleArray singular = svd.getDiagonal();
    // int rank = 0;
    // for (int i = 0; i < singular.diagonalSize(); i++) {
    // if (singular.getDiagonal(i) > 0) {
    // rank += 1;
    // }
    // }
    // return rank;
    return Double.NaN;
  }

}
