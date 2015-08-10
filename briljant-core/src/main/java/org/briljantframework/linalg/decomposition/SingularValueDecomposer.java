/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
