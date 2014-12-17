/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.linalg;

import org.briljantframework.dataframe.transform.InverseTransformation;
import org.briljantframework.linalg.analysis.PrincipalComponentAnalysis;
import org.briljantframework.linalg.analysis.PrincipalComponentAnalyzer;
import org.briljantframework.linalg.decomposition.LuDecomposer;
import org.briljantframework.linalg.decomposition.LuDecomposition;
import org.briljantframework.linalg.decomposition.SingularValueDecomposer;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.linalg.solve.LeastLinearSquaresSolver;
import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Diagonal;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.Shape;

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
  public static Matrix leastLinearSquares(Matrix a, Matrix b) {
    return new LeastLinearSquaresSolver(a).solve(b);
  }

  /**
   * Lu lu decomposition.
   *
   * @param matrix the matrix
   * @return lu decomposition
   */
  public static LuDecomposition lu(Matrix matrix) {
    return new LuDecomposer().decompose(matrix);
  }

  /**
   * Return the inverse of a matrix a.
   * <p>
   * 
   * <pre>
   * Matrix a = ArrayMatrix.of(2, 2, 1, 1, 1, 2);
   * Matrix inverse = Linalg.inverse(a);
   * // Result:
   * // 2 -1
   * // -1 1
   * </pre>
   *
   * @param a the matrix to inverse
   * @return the inverse of a
   * @throws IllegalArgumentException if matrix is not square
   * @throws RuntimeException if the decomposition fail (i.e. the matrix is singular)
   */
  public static Matrix inv(Matrix a) {
    return new InverseTransformation().transform(a);
  }


  /**
   * Pinv out.
   *
   * @param matrix the matrix
   * @return the out
   */
  public static ArrayMatrix pinv(Matrix matrix) {
    Shape shape = Shape.of(matrix.columns(), matrix.rows());
    double[] array = shape.getArrayOfShape();
    pinvi(matrix, array);
    return new ArrayMatrix(shape, array);
  }

  /**
   * Pinvi void.
   * 
   * @param matrix the tensor
   * @param copy the copy
   */
  public static void pinvi(Matrix matrix, double[] copy) {
    SingularValueDecomposition svd = svd(matrix);
    Diagonal diagonal = svd.getDiagonal();
    ArrayMatrix rightSingularValues = svd.getRightSingularValues();
    ArrayMatrix leftSingularValues = svd.getLeftSingularValues();

    diagonal.apply(x -> x < MACHINE_EPSILON ? 0 : 1 / x);
    diagonal.transposei();
    Matrix s = rightSingularValues.mmul(diagonal);
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
  public static SingularValueDecomposition svd(Matrix matrix) {
    return new SingularValueDecomposer().decompose(matrix);
  }

  /**
   * Principal component analysis (PCA) is a statistical procedure that uses an orthogonal
   * transformation to convert a set of observations of possibly correlated variables into a set of
   * values of linearly uncorrelated variables called principal components. The number of principal
   * components is less than or equal to the number of original variables. This transformation is
   * defined in such a way that the first principal component has the largest possible variance
   * (that is, accounts for as much of the variability in the data as possible), and each succeeding
   * component in turn has the highest variance possible under the constraint that it is orthogonal
   * to (i.e., uncorrelated with) the preceding components. Principal components are guaranteed to
   * be independent if the data set is jointly normally distributed. PCA is sensitive to the
   * relative scaling of the original variables.
   *
   * @param x the array
   * @return the principal components of x
   */
  public static PrincipalComponentAnalysis pca(Matrix x) {
    return new PrincipalComponentAnalyzer().analyze(x);
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
  public static double det(Matrix x) {
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
  public static double rank(Matrix x) {
    SingularValueDecomposition svd = new SingularValueDecomposer().decompose(x);
    Diagonal singular = svd.getDiagonal();
    int rank = 0;
    for (int i = 0; i < singular.size(); i++) {
      if (singular.getAsDouble(i) > 0) {
        rank += 1;
      }
    }
    return rank;
  }

}
