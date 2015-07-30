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

import org.briljantframework.Bj;
import org.briljantframework.array.DoubleArray;

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
public class SingularValueDecomposer {

  public SingularValueDecomposition decompose(DoubleArray x) {
    int m = x.rows(), n = x.columns();
    DoubleArray s = Bj.doubleArray(n);
    DoubleArray u = Bj.doubleArray(m, m);
    DoubleArray vt = Bj.doubleArray(n, n);
    DoubleArray a = x.copy();
    Bj.linalg.gesvd('a', 'a', a, s, u, vt);
    return new SingularValueDecomposition(s, u, vt);
  }
}
