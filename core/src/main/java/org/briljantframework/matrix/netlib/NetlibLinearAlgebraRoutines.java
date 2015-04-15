package org.briljantframework.matrix.netlib;

import com.github.fommil.netlib.LAPACK;

import org.briljantframework.Check;
import org.briljantframework.exceptions.BlasException;
import org.briljantframework.linalg.api.AbstractLinearAlgebraRoutines;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Storage;
import org.briljantframework.matrix.api.MatrixFactory;
import org.netlib.util.intW;

import java.util.Arrays;

/**
 * @author Isak Karlsson
 */
class NetlibLinearAlgebraRoutines extends AbstractLinearAlgebraRoutines {

  private static final LAPACK lapack = LAPACK.getInstance();

  NetlibLinearAlgebraRoutines(
      MatrixFactory matrixFactory) {
    super(matrixFactory);
  }

  private void ensureCompatibleStorage(Storage... storages) {
    for (Storage storage : storages) {
      if (!(storage instanceof NetlibDoubleStorage)) {
        throw new IllegalArgumentException(String.format(
            "Unsupported storage unit !(%s instanceof NetlibDoubleStorage)",
            storage.getClass().getSimpleName()));
      }
    }
  }

  @Override
  public int getrf(DoubleMatrix a, int[] ipiv) {
    Check.size(Math.min(a.rows(), a.columns()), ipiv.length);
    Check.isNotView(a);
    ensureCompatibleStorage(a.getStorage());

    double[] data = ((NetlibDoubleStorage) a.getStorage()).doubleArray();
    intW error = new intW(0);
    LAPACK.getInstance().dgetrf(
        a.rows(),
        a.columns(),
        data,
        a.columns(),
        ipiv,
        error
    );
    if (error.val < 0) {
      throw new BlasException("dgtref", error.val, "LU decomposition failed");
    }

    return error.val; //getMatrixFactory().matrix(data).reshape(a.rows(), a.columns());
  }

  @Override
  public int dgelsy(DoubleMatrix a, DoubleMatrix b, int[] jpvt, double rcond) {
    int m = a.rows();
    int n = a.columns();
    int nrhs = b.columns();
    int lwork = -1;
    double[] work = new double[1];

    intW rank = new intW(0);
    intW info = new intW(0);
    double[] arr = ((NetlibDoubleStorage) a.getStorage()).doubleArray();
    double[] brr = ((NetlibDoubleStorage) b.getStorage()).doubleArray();
    lapack.dgelsy(m, n, nrhs, arr, m, brr, m, jpvt, rcond, rank, work, lwork, info);
    if (info.val != 0) {
      return info.val;
    }

    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgelsy(m, n, nrhs, arr, m, brr, m, jpvt, 0.01, rank, work,
                  lwork, info);

    double[] array = Arrays.copyOf(brr, n);
    return info.val;
  }

  @Override
  public SingularValueDecomposition gesvd(DoubleMatrix x) {
    int m = x.rows(), n = x.columns();
    double[] sigma = new double[n];
    double[] u = new double[m * m];
    double[] vt = new double[n * n];
    double[] data = ((NetlibDoubleStorage) x.getStorage().copy()).doubleArray();

    int lwork = -1;
    double[] work = new double[1];

    intW info = new intW(0);
    lapack.dgesvd(
        "a",
        "a",
        m,
        n,
        data,
        m,
        sigma,
        u,
        m,
        vt,
        n,
        work,
        lwork,
        info
    );

    if (info.val != 0) {
      throw new BlasException("LAPACKE_dgesvd", info.val, "SVD failed to converge.");
    }

    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgesvd(
        "a",
        "a",
        m,
        n,
        data,
        m,
        sigma,
        u,
        m,
        vt,
        n,
        work,
        lwork,
        info
    );

    if (info.val != 0) {
      throw new BlasException("LAPACKE_dgesvd", info.val, "SVD failed to converge.");
    }

    DoubleMatrix sv = getMatrixFactory().diag(sigma).reshape(m, n);
    DoubleMatrix um = getMatrixFactory().matrix(u).reshape(m, m);
    DoubleMatrix vtm = getMatrixFactory().matrix(vt).reshape(n, n).transpose();

    return new SingularValueDecomposition(sv, um, vtm);
  }
}
