/**
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
package org.briljantframework.array.base;

import org.briljantframework.array.ArrayOperation;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.linalg.api.AbstractLinearAlgebraRoutines;
import org.briljantframework.linalg.decomposition.LuDecomposition;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;

/**
 * Created by isak on 27/04/15.
 */
class BaseLinearAlgebraRoutines extends AbstractLinearAlgebraRoutines {

  protected BaseLinearAlgebraRoutines(ArrayBackend matrixFactory) {
    super(matrixFactory);
  }

  @Override
  public LuDecomposition lu(DoubleArray x) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DoubleArray inv(DoubleArray x) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DoubleArray pinv(DoubleArray x) {
    throw new UnsupportedOperationException();
  }

  @Override
  public SingularValueDecomposition svd(DoubleArray x) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void geev(char jobvl, char jobvr, DoubleArray a, DoubleArray wr, DoubleArray wi,
      DoubleArray vl, DoubleArray vr) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void ormqr(char side, ArrayOperation transA, DoubleArray a, DoubleArray tau, DoubleArray c) {
    throw new UnsupportedOperationException();

  }

  @Override
  public void geqrf(DoubleArray a, DoubleArray tau) {
    throw new UnsupportedOperationException();

  }

  @Override
  public void syev(char jobz, char uplo, DoubleArray a, DoubleArray w) {
    throw new UnsupportedOperationException();

  }

  @Override
  public int syevr(char jobz, char range, char uplo, DoubleArray a, double vl, double vu, int il,
      int iu, double abstol, DoubleArray w, DoubleArray z, IntArray isuppz) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getrf(DoubleArray a, IntArray ipiv) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getri(DoubleArray a, IntArray ipiv) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int gelsy(DoubleArray a, DoubleArray b, IntArray jpvt, double rcond) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int gesv(DoubleArray a, IntArray ipiv, DoubleArray b) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void gesvd(char jobu, char jobvt, DoubleArray a, DoubleArray s, DoubleArray u,
      DoubleArray vt) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void gesdd(char jobz, DoubleArray a, DoubleArray s, DoubleArray u, DoubleArray vt) {
    throw new UnsupportedOperationException();
  }
}
