/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
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
package org.briljantframework.array.api;

import java.util.Objects;

import org.apache.commons.math3.util.Precision;
import org.briljantframework.Check;
import org.briljantframework.array.ArrayOperation;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.linalg.decomposition.EigenDecomposition;
import org.briljantframework.array.linalg.decomposition.LuDecomposition;
import org.briljantframework.array.linalg.decomposition.SingularValueDecomposition;

/**
 * Skeletal implementation of linear algebra routines in terms of the LAPACK definitions.
 * 
 * @author Isak Karlsson
 */
public abstract class AbstractLinearAlgebraRoutines implements LinearAlgebraRoutines {
  public static final double EPS = 1e-10;
  protected final static double MACHINE_EPSILON = Math.ulp(1);
  private final ArrayBackend arrayBackend;

  protected AbstractLinearAlgebraRoutines(ArrayBackend matrixFactory) {
    this.arrayBackend = Objects.requireNonNull(matrixFactory);
  }

  protected final ArrayFactory getArrayFactory() {
    return getArrayBackend().getArrayFactory();
  }

  protected final ArrayBackend getArrayBackend() {
    return arrayBackend;
  }

  @Override
  public EigenDecomposition eig(DoubleArray x) {
    Check.argument(x.isMatrix() && x.isSquare(), "require square 2d-array.");
    final int n = x.size(0);
    DoubleArray wr = getArrayFactory().newDoubleArray(n);
    DoubleArray wi = getArrayFactory().newDoubleArray(n);
    DoubleArray vl = getArrayFactory().newDoubleArray(n, n);
    DoubleArray vr = getArrayFactory().newDoubleArray(n, n);
    DoubleArray a = x.copy();
    geev('v', 'v', a, wr, wi, vl, vr);
    return new GeeVEigenDecomposition(wr, wi, vl);
  }

  @Override
  public LuDecomposition lu(DoubleArray array) {
    Check.argument(array.isMatrix(), "require square 2d-array");
    int m = array.size(0);
    int n = array.size(1);
    IntArray pivots = getArrayFactory().newIntArray(Math.min(m, n));
    DoubleArray lu = array.copy();
    getrf(lu, pivots);
    return new LuDecomposition(lu, pivots);
  }

  @Override
  public SingularValueDecomposition svd(DoubleArray x) {
    Check.argument(x.isMatrix(), "require 2d-array");
    int m = x.rows();
    int n = x.columns();
    DoubleArray s = getArrayFactory().newDoubleArray(n);
    DoubleArray u = getArrayFactory().newDoubleArray(m, m);
    DoubleArray vt = getArrayFactory().newDoubleArray(n, n);
    DoubleArray a = x.copy();
    if (m > n) {
      gesdd('a', a, s, u, vt);
    } else {
      gesdd('a', a, s, u, vt);
    }
    return new GesddSingularValueDecomposition(s, u, vt.transpose());
  }

  @Override
  public DoubleArray inv(DoubleArray x) {
    LuDecomposition lu = lu(x);
    DoubleArray out = lu.getDecomposition();
    getri(out, lu.getPivot());
    return out;
  }

  @Override
  public DoubleArray pinv(DoubleArray x) {
    Check.argument(x.isMatrix(), "require 2d-array");
    ArrayFactory bj = getArrayBackend().getArrayFactory();
    SingularValueDecomposition svd = svd(x);
    DoubleArray d = svd.getSingularValues().copy();
    int r1 = 0;
    for (int i = 0; i < d.size(); i++) {
      if (d.get(i) > MACHINE_EPSILON) {
        d.set(i, 1 / d.get(i));
        r1++;
      }
    }

    DoubleArray u = svd.getLeftSingularValues();
    DoubleArray v = svd.getRightSingularValues().copy();
    u = u.getView(0, 0, u.rows(), r1);
    v = v.getView(0, 0, v.rows(), r1);
    d = d.get(bj.range(r1));

    final int vc = v.size(1);
    final int vr = v.size(0);
    for (int j = 0; j < vc; j++) {
      double dv = d.get(j);
      for (int i = 0; i < vr; i++) {
        v.set(i, j, dv * v.get(i, j));
      }
    }

    DoubleArray pinv = bj.newDoubleArray(x.columns(), x.rows());
    getArrayBackend().getArrayRoutines().gemm(ArrayOperation.KEEP, ArrayOperation.TRANSPOSE, 1, v,
        u, 1, pinv);
    return pinv;
  }

  /**
   * In linear algebra, the determinant is a value associated with a square matrix. It can be
   * computed from the entries of the matrix by a specific arithmetic expression, while other ways
   * to determine its value exist as well. The determinant provides important information about a
   * matrix of coefficients of a system of linear equations, or about a matrix that corresponds to a
   * linear transformation of a series space.
   *
   * @param x a square mutable array
   * @return the determinant
   */
  @Override
  public double det(DoubleArray x) {
    if (x.isSquare()) {
      return lu(x).getDeterminant();
    } else {
      throw new IllegalArgumentException("argument must be a square array");
    }
  }

  @Override
  public double rank(DoubleArray x) {
    SingularValueDecomposition svd = svd(x);
    DoubleArray singular = svd.getSingularValues();
    return singular.reduce(0, (acc, v) -> Precision.compareTo(v, 0, EPS) > 0 ? acc + 1 : acc);
  }

  private static class GeeVEigenDecomposition extends EigenDecomposition {
    private final DoubleArray wr;
    private final DoubleArray wi;
    private final DoubleArray vl;

    GeeVEigenDecomposition(DoubleArray wr, DoubleArray wi, DoubleArray vl) {
      this.wr = Arrays.unmodifiableArray(wr);
      this.wi = Arrays.unmodifiableArray(wi);
      this.vl = Arrays.unmodifiableArray(vl);
    }

    @Override
    public DoubleArray getRealEigenvalues() {
      return wr;
    }

    @Override
    public DoubleArray getImagEigenvalues() {
      return wi;
    }

    @Override
    public DoubleArray getEigenVectors() {
      return vl;
    }
  }

  private static class GesddSingularValueDecomposition extends SingularValueDecomposition {
    private final DoubleArray s;
    private final DoubleArray u;
    private final DoubleArray vt;

    public GesddSingularValueDecomposition(DoubleArray s, DoubleArray u, DoubleArray vt) {
      this.s = Arrays.unmodifiableArray(s);
      this.u = Arrays.unmodifiableArray(u);
      this.vt = Arrays.unmodifiableArray(vt);
    }

    @Override
    public DoubleArray getSingularValues() {
      return s;
    }

    @Override
    public DoubleArray getLeftSingularValues() {
      return u;
    }

    @Override
    public DoubleArray getRightSingularValues() {
      return vt;
    }
  }
}
