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

  NetlibMatrixRoutines(NetlibMatrixBackend backend) {
    super(backend);
  }

  @Override
  public double dot(DoubleMatrix a, DoubleMatrix b) {
    if (!a.isView() && !b.isView()) {
      Check.size(a, b);
      Check.all(Matrix::isVector, a, b);
      int n = a.size();
      double[] aa = a.getStorage().doubleArray();
      double[] ba = b.getStorage().doubleArray();
      return blas.ddot(n, aa, 1, ba, 1);
    } else {
      return super.dot(a, b);
    }
  }

  @Override
  public double asum(DoubleMatrix a) {
    if (!a.isView()) {
      return blas.dasum(a.size(), a.getStorage().doubleArray(), 1);
    } else {
      return super.asum(a);
    }
  }

  @Override
  public double nrm2(DoubleMatrix a) {
    if (!a.isView()) {
      return blas.dnrm2(a.size(), a.getStorage().doubleArray(), 1);
    } else {
      return super.nrm2(a);
    }
  }

  @Override
  public int iamax(DoubleMatrix x) {
    if (!x.isView()) {
      return blas.idamax(x.size(), x.getStorage().doubleArray(), 1);
    } else {
      return super.iamax(x);
    }
  }

  @Override
  public void scal(double alpha, DoubleMatrix x) {
    if (alpha != 1 && (!x.isView())) {
      blas.dscal(x.size(), alpha, x.getStorage().doubleArray(), 1);
    } else {
      super.scal(alpha, x);
    }
  }

  @Override
  public void axpy(double alpha, DoubleMatrix x, DoubleMatrix y) {
    if (alpha != 1 && (!x.isView() && !y.isView())) {
      Check.equalShape(x, y);
      double[] xa = x.getStorage().doubleArray();
      double[] ya = y.getStorage().doubleArray();
      blas.daxpy(x.size(), alpha, xa, 1, ya, 1);
    } else if (alpha != 0) {
      super.axpy(alpha, x, y);
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
    double[] aa = a.getStorage().doubleArray();
    double[] xa = x.getStorage().doubleArray();
    double[] ya = y.getStorage().doubleArray();
    blas.dgemv(ta, am, an, alpha, aa, lda, xa, 1, beta, ya, 1);
    if (y.isView()) {
      y.assign(ya);
    }
  }

  @Override
  public void gemm(Transpose transA, Transpose transB, double alpha, DoubleMatrix a, DoubleMatrix b,
                   double beta, DoubleMatrix c) {
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
    Storage sa = a.getStorage();
    Storage sb = b.getStorage();
    Storage sc = c.getStorage();

    double[] aa = sa.doubleArray();
    double[] ba = sb.doubleArray();
    double[] ca = sc.doubleArray();
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

}
