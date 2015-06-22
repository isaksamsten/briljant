package org.briljantframework.matrix.netlib;

import com.github.fommil.netlib.BLAS;

import org.briljantframework.Check;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.matrix.Array;
import org.briljantframework.matrix.Op;
import org.briljantframework.matrix.base.BaseArrayRoutines;

/**
 * Created by isak on 13/04/15.
 */
class NetlibArrayRoutines extends BaseArrayRoutines {

  private final static BLAS blas = BLAS.getInstance();

  @Override
  public double dot(DoubleArray a, DoubleArray b) {
    if (!a.isView() && !b.isView()) {
      Check.size(a, b);
      Check.all(Array::isVector, a, b);
      int n = a.size();
      double[] aa = a.data();
      double[] ba = b.data();
      return blas.ddot(n, aa, 1, ba, 1);
    } else {
      return super.dot(a, b);
    }
  }

  @Override
  public double asum(DoubleArray a) {
    if (!a.isView()) {
      return blas.dasum(a.size(), a.data(), 1);
    } else {
      return super.asum(a);
    }
  }

  @Override
  public double norm2(DoubleArray a) {
    if (!a.isView()) {
      return blas.dnrm2(a.size(), a.data(), 1);
    } else {
      return super.norm2(a);
    }
  }

  @Override
  public int iamax(DoubleArray x) {
    if (!x.isView()) {
      return blas.idamax(x.size(), x.data(), 1);
    } else {
      return super.iamax(x);
    }
  }

  @Override
  public void scal(double alpha, DoubleArray x) {
    if (alpha != 1 && (!x.isView())) {
      blas.dscal(x.size(), alpha, x.data(), 1);
    } else {
      super.scal(alpha, x);
    }
  }

  @Override
  public void axpy(double alpha, DoubleArray x, DoubleArray y) {
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
  public void ger(double alpha, DoubleArray x, DoubleArray y, DoubleArray a) {
    if (!x.isView() && !y.isView() && !a.isView()) {
      Check.all(Array::isVector, x, y);
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
  public void gemv(Op transA, double alpha, DoubleArray a,
                   DoubleArray x, double beta, DoubleArray y) {
    Check.argument(a.isMatrix() && x.isMatrix() && y.isMatrix(), "Illegal array shape.");
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
  public void gemm(Op transA, Op transB, double alpha, DoubleArray a, DoubleArray b,
                   double beta, DoubleArray c) {
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

  public boolean isMatrixTransposedView(DoubleArray x) {
    return x.stride(x.dims() - 1) > x.stride(0);
  }

}
