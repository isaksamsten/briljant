package org.briljantframework.matrix.netlib;

import com.github.fommil.netlib.BLAS;

import org.briljantframework.Check;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.T;
import org.briljantframework.matrix.base.BaseMatrixRoutines;

/**
 * Created by isak on 13/04/15.
 */
class NetlibMatrixRoutines extends BaseMatrixRoutines {

  private final static BLAS blas = BLAS.getInstance();

  @Override
  public double dot(DoubleMatrix a, DoubleMatrix b) {
    if (!a.isView() && !b.isView()) {
      Check.size(a, b);
      Check.all(Matrix::isVector, a, b);
      int n = a.size();
      double[] aa = a.data();
      double[] ba = b.data();
      return blas.ddot(n, aa, 1, ba, 1);
    } else {
      return super.dot(a, b);
    }
  }

  @Override
  public double asum(DoubleMatrix a) {
    if (!a.isView()) {
      return blas.dasum(a.size(), a.data(), 1);
    } else {
      return super.asum(a);
    }
  }

  @Override
  public double norm2(DoubleMatrix a) {
    if (!a.isView()) {
      return blas.dnrm2(a.size(), a.data(), 1);
    } else {
      return super.norm2(a);
    }
  }

  @Override
  public int iamax(DoubleMatrix x) {
    if (!x.isView()) {
      return blas.idamax(x.size(), x.data(), 1);
    } else {
      return super.iamax(x);
    }
  }

  @Override
  public void scal(double alpha, DoubleMatrix x) {
    if (alpha != 1 && (!x.isView())) {
      blas.dscal(x.size(), alpha, x.data(), 1);
    } else {
      super.scal(alpha, x);
    }
  }

  @Override
  public void axpy(double alpha, DoubleMatrix x, DoubleMatrix y) {
    if (alpha == 0) {
      return;
    }
    if (!x.isView() && !y.isView()) {
      Check.size(x, y);
      double[] xa = x.data();
      double[] ya = y.data();
      blas.daxpy(x.size(), alpha, xa, 1, ya, 1);
    } else {
      super.axpy(alpha, x, y);
    }
  }

  @Override
  public void ger(double alpha, DoubleMatrix x, DoubleMatrix y, DoubleMatrix a) {
    if (!x.isView() && !y.isView() && !a.isView()) {
      Check.all(Matrix::isVector, x, y);
      Check.size(x.size(), a.rows());
      Check.size(y.size(), a.columns());

      int m = a.rows();
      int n = a.columns();

      double[] ax = x.data();
      double[] ay = y.data();
      double[] aa = a.data();
      blas.dger(m, n, alpha, ax, 1, ay, 1, aa, Math.max(1, m));
    } else {
      super.ger(alpha, x, y, a);
    }
  }

  @Override
  public void gemv(T transA, double alpha, DoubleMatrix a,
                   DoubleMatrix x, double beta, DoubleMatrix y) {
    int am = a.rows();
    int an = a.columns();
    String ta = "n";
    if (transA.isTrue()) {
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
    double[] aa = a.data();
    double[] xa = x.data();
    double[] ya = y.data();
    blas.dgemv(ta, am, an, alpha, aa, lda, xa, 1, beta, ya, 1);
    if (y.isView()) {
      y.assign(ya);
    }
  }

  @Override
  public void gemm(T transA, T transB, double alpha, DoubleMatrix a, DoubleMatrix b,
                   double beta, DoubleMatrix c) {
    String ta = "n";
    int am = a.rows();
    int an = a.columns();
    if (transA.isTrue()) {
      am = a.columns();
      an = a.rows();
      ta = "t";
    }

    String tb = "n";
    int bm = b.rows();
    int bn = b.columns();
    if (transB.isTrue()) {
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

    double[] aa = a.data();
    double[] ba = b.data();
    double[] ca = c.data();
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
