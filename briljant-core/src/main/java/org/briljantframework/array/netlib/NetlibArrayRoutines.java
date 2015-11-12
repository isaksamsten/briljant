/*
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

package org.briljantframework.array.netlib;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.Op;
import org.briljantframework.array.base.BaseArrayRoutines;
import org.briljantframework.exceptions.NonConformantException;

import com.github.fommil.netlib.BLAS;

/**
 * @author Isak Karlsson
 */
class NetlibArrayRoutines extends BaseArrayRoutines {

  protected static final String VECTOR_REQUIRED = "vector required";
  private final static BLAS blas = BLAS.getInstance();

  @Override
  public double inner(DoubleArray a, DoubleArray b) {
    if (a instanceof NetlibDoubleArray && b instanceof NetlibDoubleArray) {
      Check.argument(a.isVector() && b.isVector(), VECTOR_REQUIRED);
      Check.size(a, b);
      int n = a.size();
      return blas.ddot(n, a.data(), a.getOffset(), a.getMajorStride(), b.data(), b.getOffset(),
          b.getMajorStride());
    } else {
      return super.inner(a, b);
    }
  }

  @Override
  public double asum(DoubleArray a) {
    if (a instanceof NetlibDoubleArray) {
      Check.argument(a.isVector(), VECTOR_REQUIRED);
      return blas.dasum(a.size(), a.data(), a.getOffset(), a.getMajorStride());
    } else {
      return super.asum(a);
    }
  }

  @Override
  public double norm2(DoubleArray a) {
    if (a instanceof NetlibDoubleArray) {
      Check.argument(a.isVector(), VECTOR_REQUIRED);
      return blas.dnrm2(a.size(), a.data(), a.getOffset(), a.getMajorStride());
    } else {
      return super.norm2(a);
    }
  }

  @Override
  public int iamax(DoubleArray a) {
    if (a instanceof NetlibDoubleArray) {
      Check.argument(a.isVector(), VECTOR_REQUIRED);
      return blas.idamax(a.size(), a.data(), a.getOffset(), a.getMajorStride());
    } else {
      return super.iamax(a);
    }
  }

  @Override
  public void scal(double alpha, DoubleArray a) {
    if (a instanceof NetlibDoubleArray && alpha != 1) {
      Check.argument(a.isVector(), VECTOR_REQUIRED);
      blas.dscal(a.size(), alpha, a.data(), a.getOffset(), a.getMajorStride());
    } else {
      super.scal(alpha, a);
    }
  }

  @Override
  public void axpy(double alpha, DoubleArray x, DoubleArray y) {
    if (alpha == 0) {
      return;
    }
    if (x instanceof NetlibDoubleArray && y instanceof NetlibDoubleArray) {
      Check.argument(x.isVector() && y.isVector());
      Check.size(x, y);
      blas.daxpy(x.size(), alpha, x.data(), x.getOffset(), x.getMajorStride(), y.data(),
          y.getOffset(), y.getMajorStride());
    } else {
      super.axpy(alpha, x, y);
    }
  }

  @Override
  public void ger(double alpha, DoubleArray x, DoubleArray y, DoubleArray a) {
    Check.argument(a.isMatrix() && x.isVector() && y.isVector());
    Check.size(x.size(), a.rows());
    Check.size(y.size(), a.columns());
    if (x instanceof NetlibDoubleArray && y instanceof NetlibDoubleArray
        && a instanceof NetlibDoubleArray && a.stride(0) == 1 && a.stride(1) >= a.size(1)) {
      blas.dger(a.rows(), a.columns(), alpha, x.data(), x.getOffset(), x.getMajorStride(),
          y.data(), y.getOffset(), y.getMajorStride(), a.data(), a.getOffset(),
          Math.max(1, a.stride(1)));
    } else {
      super.ger(alpha, x, y, a);
    }
  }

  @Override
  public void gemv(Op transA, double alpha, DoubleArray a, DoubleArray x, double beta, DoubleArray y) {
    Check.argument(a.isMatrix());
    Check.argument(x.isVector());
    Check.argument(y.isVector());

    if (a instanceof NetlibDoubleArray && a.stride(0) == 1 && a.stride(1) >= a.size(1)
        && x instanceof NetlibDoubleArray && y instanceof NetlibDoubleArray) {

      int m = a.size(transA == Op.KEEP ? 0 : 1);
      int n = a.size(transA == Op.KEEP ? 1 : 0);
      // TODO: sanity checks

      blas.dgemv(transA.asString(), m, n, alpha, a.data(), a.getOffset(), Math.max(1, a.stride(1)),
          x.data(), x.getOffset(), x.getMajorStride(), beta, y.data(), y.getOffset(),
          y.getMajorStride());
    } else {
      super.gemv(transA, alpha, a, x, beta, y);
    }
  }

  @Override
  public void gemm(Op transA, Op transB, double alpha, DoubleArray a, DoubleArray b, double beta,
      DoubleArray c) {
    Check.argument(a.dims() == 2, "'a' has %s dims", a.dims());
    Check.argument(b.dims() == 2, "'b' has %s dims", a.dims());
    Check.argument(c.dims() == 2, "'c' has %s dims", a.dims());

    if (b.size(transB == Op.KEEP ? 0 : 1) != a.size(transA == Op.KEEP ? 1 : 0)) {
      boolean ta = transA == Op.KEEP;
      boolean tb = transB == Op.KEEP;
      throw new NonConformantException(String.format("a has size (%d, %d), b has size(%d, %d)",
          a.size(ta ? 0 : 1), a.size(ta ? 1 : 0), b.size(tb ? 0 : 1), b.size(tb ? 1 : 0)));
    }
    int m = a.size(transA == Op.KEEP ? 0 : 1);
    int n = b.size(transB == Op.KEEP ? 1 : 0);
    int k = a.size(transA == Op.KEEP ? 1 : 0);

    if (m != c.size(0) || n != c.size(1)) {
      throw new NonConformantException(String.format(
          "a has size (%d,%d), b has size (%d,%d), c has size (%d, %d)", m, k, k, n,
              c.size(0), c.size(1)));
    }

    // Issue: is a or b is non-netlib arrays it might be beneficial to copy here if
    // the array is a small view of a large array since the view performs a copy of
    // the large array and, while the copy here might be small.
    a = a.isContiguous() && a.stride(0) == 1 ? a : a.copy();
    b = b.isContiguous() && b.stride(0) == 1 ? b : b.copy();
    DoubleArray maybeC =
        c instanceof NetlibDoubleArray && c.isContiguous() && c.stride(0) == 1 ? c : c.copy();

    double[] ca = maybeC.data();
    blas.dgemm(transA.asString(), transB.asString(), m, n, k, alpha, a.data(), a.getOffset(),
        Math.max(1, a.stride(1)), b.data(), b.getOffset(), Math.max(1, b.stride(1)), beta, ca,
        maybeC.getOffset(), Math.max(1, maybeC.stride(1)));

    // If c was copied, maybeC and c won't be the same instance.
    // To simulate an out parameter, c is assigned the new data if this is the case.
    if (maybeC != c) {
      c.assign(ca);
    }
  }
}
