package org.briljantframework.matrix.netlib;

import com.github.fommil.netlib.BLAS;

import org.briljantframework.Check;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.Array;
import org.briljantframework.matrix.DoubleArray;
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
    Check.argument(a.dims() == 2, "'a' has %s dims", a.dims());
    Check.argument(b.dims() == 2, "'b' has %s dims", a.dims());
    Check.argument(c.dims() == 2, "'c' has %s dims", a.dims());

    if (a.stride(0) == 1 && b.stride(0) == 1 && c.stride(0) == 1 &&
        a.stride(1) >= a.size(1) && b.stride(1) >= b.size(1) && c.stride(1) >= c.size(1)) {

      int m = a.size(transA == Op.KEEP ? 0 : 1);
      int n = b.size(transB == Op.KEEP ? 1 : 0);
      int k = a.size(transA == Op.KEEP ? 1 : 0);

      if (m != c.size(0) || n != c.size(1)) {
        throw new NonConformantException(String.format(
            "a has size (%d,%d), b has size (%d,%d), c has size (%d, %d)",
            m, k, k, n, c.size(0), c.size(1)));
      }

      double[] ca = c.data();
      blas.dgemm(
          transA.asString(),
          transB.asString(),
          m,
          n,
          k,
          alpha,
          a.data(),
          a.getOffset(),
          Math.max(1, a.stride(1)),
          b.data(),
          b.getOffset(),
          Math.max(1, b.stride(1)),
          beta,
          ca,
          c.getOffset(),
          Math.max(1, c.stride(1))
      );
//
      if (c.isView()) {
        c.assign(ca);
      }
    } else {
      super.gemm(transA, transB, alpha, a, b, beta, c);
    }


  }

  public boolean isMatrixTransposedView(DoubleArray x) {
    return x.stride(x.dims() - 1) > x.stride(0);
  }

}
