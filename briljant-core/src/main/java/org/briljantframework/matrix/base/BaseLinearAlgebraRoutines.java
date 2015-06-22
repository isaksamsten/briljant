package org.briljantframework.matrix.base;

import org.briljantframework.linalg.api.AbstractLinearAlgebraRoutines;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.matrix.IntArray;
import org.briljantframework.matrix.Op;
import org.briljantframework.matrix.api.ArrayBackend;

/**
 * Created by isak on 27/04/15.
 */
class BaseLinearAlgebraRoutines extends AbstractLinearAlgebraRoutines {

  protected BaseLinearAlgebraRoutines(ArrayBackend matrixFactory) {
    super(matrixFactory);
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
  public void ormqr(char side, Op transA, DoubleArray a, DoubleArray tau, DoubleArray c) {
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
