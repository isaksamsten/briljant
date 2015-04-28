package org.briljantframework.matrix.base;

import org.briljantframework.linalg.api.AbstractLinearAlgebraRoutines;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.Transpose;
import org.briljantframework.matrix.api.MatrixBackend;

/**
 * Created by isak on 27/04/15.
 */
class BaseLinearAlgebraRoutines extends AbstractLinearAlgebraRoutines {

  protected BaseLinearAlgebraRoutines(MatrixBackend matrixFactory) {
    super(matrixFactory);
  }

  @Override
  public DoubleMatrix inv(DoubleMatrix x) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DoubleMatrix pinv(DoubleMatrix x) {
    throw new UnsupportedOperationException();
  }

  @Override
  public SingularValueDecomposition svd(DoubleMatrix x) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void ormqr(char side, Transpose transA, DoubleMatrix a, DoubleMatrix tau, DoubleMatrix c) {
    throw new UnsupportedOperationException();

  }

  @Override
  public void geqrf(DoubleMatrix a, DoubleMatrix tau) {
    throw new UnsupportedOperationException();

  }

  @Override
  public void syev(char jobz, char uplo, DoubleMatrix a, DoubleMatrix w) {
    throw new UnsupportedOperationException();

  }

  @Override
  public int syevr(char jobz, char range, char uplo, DoubleMatrix a, double vl, double vu, int il,
                   int iu, double abstol, DoubleMatrix w, DoubleMatrix z, IntMatrix isuppz) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getrf(DoubleMatrix a, IntMatrix ipiv) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int gelsy(DoubleMatrix a, DoubleMatrix b, IntMatrix jpvt, double rcond) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int gesv(DoubleMatrix a, IntMatrix ipiv, DoubleMatrix b) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void gesvd(char jobu, char jobvt, DoubleMatrix a, DoubleMatrix s, DoubleMatrix u,
                    DoubleMatrix vt) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void gesdd(char jobz, DoubleMatrix a, DoubleMatrix s, DoubleMatrix u, DoubleMatrix vt) {
    throw new UnsupportedOperationException();
  }
}
