package org.briljantframework.matrix.netlib;

import com.github.fommil.netlib.BLAS;

import org.briljantframework.Check;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.Transpose;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.base.BaseMatrixRoutines;
import org.briljantframework.matrix.storage.DoubleArrayStorage;
import org.briljantframework.matrix.storage.Storage;

/**
 * Created by isak on 13/04/15.
 */
class NetlibMatrixRoutines extends BaseMatrixRoutines {

  private final static BLAS blas = BLAS.getInstance();

  NetlibMatrixRoutines(MatrixFactory matrixFactory) {
    super(matrixFactory);
  }

  @Override
  public double dot(DoubleMatrix a, DoubleMatrix b) {
    Check.size(a, b);
    Check.all(Matrix::isVector, a, b);
    Storage sa = a.getStorage();
    Storage sb = b.getStorage();
    TypeChecks.ensureInstanceOf(DoubleArrayStorage.class, sa, sb);
    int n = a.size();
    double[] aa = ((DoubleArrayStorage) sa).array();
    double[] ba = ((DoubleArrayStorage) sb).array();
    return blas.ddot(n, aa, 1, ba, 1);
  }

  @Override
  public double asum(DoubleMatrix a) {
    Storage sa = a.getStorage();
    if (sa instanceof DoubleArrayStorage) {
      return blas.dasum(a.size(), ((DoubleArrayStorage) sa).array(), 1);
    } else {
      throw unsupportedStorage(sa);
    }
  }

  @Override
  public double nrm2(DoubleMatrix a) {
    Storage sa = a.getStorage();
    if (sa instanceof DoubleArrayStorage) {
      return blas.dnrm2(a.size(), ((DoubleArrayStorage) sa).array(), 1);
    } else {
      throw unsupportedStorage(sa);
    }
  }

  @Override
  public int iamax(DoubleMatrix x) {
    Storage s = x.getStorage();
    if (s instanceof DoubleArrayStorage) {
      double[] values = ((DoubleArrayStorage) s).array();
      return blas.idamax(s.size(), values, 1);
    } else {
      throw unsupportedStorage(s);
    }
  }

  @Override
  public void axpy(double alpha, DoubleMatrix x, DoubleMatrix y) {
    Check.equalShape(x, y);
    if (alpha != 0) {
      Storage sx = x.getStorage();
      Storage sy = y.getStorage();
      TypeChecks.ensureInstanceOf(DoubleArrayStorage.class, sx, sy);
      int n = x.size();

      double[] xa = ((DoubleArrayStorage) sx).array();
      double[] ya = ((DoubleArrayStorage) sy).array();
      blas.daxpy(n, alpha, xa, 1, ya, 1);

      if (y.isView()) {
        y.assign(ya);
      }
    }
  }

  @Override
  public void gemv(Transpose transA, double alpha, DoubleMatrix a,
                   DoubleMatrix x, double beta, DoubleMatrix y) {
    Storage sa = a.getStorage();
    Storage sb = x.getStorage();
    Storage sc = y.getStorage();
    TypeChecks.ensureInstanceOf(DoubleArrayStorage.class, sa, sb, sc);

    int am = a.rows();
    int an = a.columns();
    String ta = "n";
    if (transA.transpose()) {
      am = a.columns();
      an = a.rows();
    }
    if (!x.isVector() || x.size() != am) {
      throw new NonConformantException(am, an, x.rows(), x.columns());
    }
    if (!x.isVector() || !y.isVector() || y.size() != x.size()) {
      throw new IllegalArgumentException("...");
    }

    int lda = a.rows();
    double[] aa = ((DoubleArrayStorage) a.getStorage()).array();
    double[] xa = ((DoubleArrayStorage) x.getStorage()).array();
    double[] ya = ((DoubleArrayStorage) y.getStorage()).array();
    blas.dgemv(ta, am, an, alpha, aa, lda, xa, 1, beta, ya, 1);
    if (y.isView()) {
      y.assign(ya);
    }
  }

  @Override
  public void gemm(Transpose transA, Transpose transB, double alpha, DoubleMatrix a, DoubleMatrix b,
                   double beta, DoubleMatrix c) {
    Storage sa = a.getStorage();
    Storage sb = b.getStorage();
    Storage sc = c.getStorage();
    TypeChecks.ensureInstanceOf(DoubleArrayStorage.class, sa, sb, sc);
    String ta = "n";
    int am = a.rows();
    int an = a.columns();
    if (transA.transpose()) {
      am = a.columns();
      an = a.rows();
      ta = "t";
    }

    String tb = "n";
    int bm = b.rows();
    int bn = b.columns();
    if (transB.transpose()) {
      bm = b.columns();
      bn = b.rows();
      tb = "t";
    }

    if (an != bm) {
      throw new NonConformantException(a, b);
    }

    if (c.rows() != am || c.columns() != bn) {
      throw new NonConformantException(am, an, c.rows(), c.columns());
    }

    double[] aa = ((DoubleArrayStorage) a.getStorage()).array();
    double[] ba = ((DoubleArrayStorage) b.getStorage()).array();
    double[] ca = ((DoubleArrayStorage) c.getStorage()).array();
    blas.dgemm(
        ta,
        tb,
        am,
        bn,
        bm,
        alpha,
        aa,
        a.rows(),
        ba,
        b.rows(),
        beta,
        ca,
        am);

    if (c.isView()) {
      c.assign(ca);
    }
  }

  private IllegalArgumentException unsupportedStorage(Storage sa) {
    return new IllegalArgumentException(
        String.format("Unsupported storage unit !(%s instanceof %s)",
                      sa.getClass().getSimpleName(), DoubleArrayStorage.class.getSimpleName()));
  }
}
