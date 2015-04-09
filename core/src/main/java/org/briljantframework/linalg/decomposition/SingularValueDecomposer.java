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

package org.briljantframework.linalg.decomposition;

import com.github.fommil.netlib.LAPACK;

import org.briljantframework.exceptions.BlasException;
import org.briljantframework.matrix.DefaultDoubleMatrix;
import org.briljantframework.matrix.Diagonal;
import org.briljantframework.matrix.DoubleMatrix;
import org.netlib.util.intW;

/**
 * Formally, the singular value decomposition of an m×n real or complex matrix M is a factorization
 * of the form \mathbf{M} = \mathbf{U} \boldsymbol{\Sigma} \mathbf{V}^* where U is a m×m real or
 * complex unitary matrix, \Sigma is an m×n rectangular diagonal matrix with nonnegative real
 * numbers on the diagonal, and V* (the conjugate transpose of V, or simply the transpose of V if V
 * is real) is an n×n real or complex unitary matrix. The diagonal entries \Sigma_{i,i} of \Sigma
 * are known as the singular values of M. The m headers of U and the n headers of V are called the
 * left-singular vectors and right-singular vectors of M, respectively.
 * <p>
 * Created by Isak Karlsson on 11/08/14.
 */
public class SingularValueDecomposer implements Decomposer<SingularValueDecomposition> {

  protected static final LAPACK lapack = LAPACK.getInstance();

  @Override
  public SingularValueDecomposition decompose(DoubleMatrix matrix) {
    int m = matrix.rows(), n = matrix.columns();
    double[] sigma = new double[n];
    double[] u = new double[m * m];
    double[] vt = new double[n * n];
    DoubleMatrix copy = matrix.copy();

    int lwork = -1;
    double[] work = new double[1];

    intW info = new intW(0);
    lapack.dgesvd("a", "a", m, n, copy.asDoubleArray(), m, sigma, u, m, vt, n, work, lwork, info);

    if (info.val != 0) {
      throw new BlasException("LAPACKE_dgesvd", info.val, "SVD failed to converge.");
    }

    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgesvd("a", "a", m, n, copy.asDoubleArray(), m, sigma, u, m, vt, n, work, lwork, info);

    if (info.val != 0) {
      throw new BlasException("LAPACKE_dgesvd", info.val, "SVD failed to converge.");
    }

    Diagonal sv = Diagonal.of(m, n, sigma);
    DoubleMatrix um = DefaultDoubleMatrix.fromColumnOrder(m, m, u);
    DoubleMatrix vtm = DefaultDoubleMatrix.fromRowOrder(n, n, vt);

    return new SingularValueDecomposition(sv, um, vtm);
  }
}
