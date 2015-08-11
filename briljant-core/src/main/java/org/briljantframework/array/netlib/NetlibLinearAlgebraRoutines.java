/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.array.netlib;

import com.github.fommil.netlib.LAPACK;

import org.briljantframework.Check;
import org.briljantframework.array.BaseArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.Op;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.linalg.api.AbstractLinearAlgebraRoutines;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.netlib.util.intW;

import java.util.Arrays;
import java.util.List;

/**
 * This class implements the linear algebra routines (commonly LAPACK) using the netlib-java
 * fortran
 * wrappers.
 *
 * <p> Since java does not allow for taking a pointer to a slice of an input array, the routines
 * implemented here copies the values of array-slices into new arrays and then re-inserts them.
 * The array must be copied if: {@code a.stride(0) != 1} or {@code a.getOffset() > 0} or {@code
 * a.isView() == true}.
 *
 * @author Isak Karlsson
 */
public class NetlibLinearAlgebraRoutines extends AbstractLinearAlgebraRoutines {

  private static final LAPACK lapack = LAPACK.getInstance();

  public final static double MACHINE_EPSILON = Math.ulp(1);

  private static final List<Character> GESVD_JOB_CHAR = Arrays.asList('a', 's', 'o', 'n');
  private static final List<Character> SYEVR_JOBZ_CHAR = Arrays.asList('n', 'v');
  private static final List<Character> SYEVR_RANGE_CHAR = Arrays.asList('a', 'v', 'i');
  static final List<Character> SYEVR_UPLO = Arrays.asList('l', 'u');
  static final List<Character> ORMQR_SIDE = Arrays.asList('l', 'r');
  protected static final String REQUIRE_2D_ARRAY = "require 2d-array";

  protected NetlibLinearAlgebraRoutines(NetlibArrayBackend matrixFactory) {
    super(matrixFactory);
  }

  @Override
  public DoubleArray inv(DoubleArray x) {
    return null;
  }

  @Override
  public DoubleArray pinv(DoubleArray x) {
    Check.argument(x.isMatrix(), REQUIRE_2D_ARRAY);
    ArrayFactory bj = getArrayBackend().getArrayFactory();
    SingularValueDecomposition svd = svd(x);
    DoubleArray d = svd.getDiagonal();
    int r1 = 0;
    for (int i = 0; i < d.size(); i++) {
      if (d.get(i) > MACHINE_EPSILON) {
        d.set(i, 1 / d.get(i));
        r1++;
      }
    }

    DoubleArray u = svd.getLeftSingularValues();
    DoubleArray v = svd.getRightSingularValues();
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

    DoubleArray pinv = bj.doubleArray(x.columns(), x.rows());
    getArrayBackend().getArrayRoutines()
        .gemm(Op.KEEP, Op.TRANSPOSE, 1, v, u, 1, pinv);
    return pinv;
  }

  @Override
  public SingularValueDecomposition svd(DoubleArray x) {
    Check.argument(x.isMatrix(), REQUIRE_2D_ARRAY);
    ArrayFactory bj = getArrayBackend().getArrayFactory();
    int m = x.rows();
    int n = x.columns();
    DoubleArray s = bj.doubleArray(n);
    DoubleArray u = bj.doubleArray(m, m);
    DoubleArray vt = bj.doubleArray(n, n);
    DoubleArray a = x.copy();
    if (m > n) {
      gesdd('a', a, s, u, vt);
    } else {
      gesdd('a', a, s, u, vt);
    }
    return new SingularValueDecomposition(s, u, vt.transpose());
  }

  @Override
  public void geqrf(DoubleArray a, DoubleArray tau) {
    Check.argument(a.isMatrix(), REQUIRE_2D_ARRAY);
    int m = a.rows();
    int n = a.columns();
    int lda = Math.max(1, m);
    Check.argument(tau.isVector() && tau.size() == Math.min(m, n));

    double[] aa = getData(a);
    double[] ta = getData(tau);

    double[] work = new double[1];
    int lwork = -1;

    intW info = new intW(0);
    lapack.dgeqrf(
        m,
        n,
        aa,
        lda,
        ta,
        work,
        lwork,
        info
    );
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];

    lapack.dgeqrf(
        m,
        n,
        aa,
        lda,
        ta,
        work,
        lwork,
        info
    );
    ensureInfo(info);
    assignIfNeeded(a, aa);
    assignIfNeeded(tau, ta);
  }

  @Override
  public void ormqr(char side, Op transA, DoubleArray a, DoubleArray tau, DoubleArray c) {
    side = Character.toLowerCase(side);
    if (!ORMQR_SIDE.contains(side)) {
      throw invalidCharacter("side", side, ORMQR_SIDE);
    }
    int m = c.rows();
    int n = c.columns();
    int k = m;
    if (side == 'r') {
      k = n;
    }
    int lda = Math.max(1, m);
    if (side == 'r') {
      lda = Math.max(1, n);
    }
    Check.argument(tau.isVector() && tau.size() == k);

    int ldc = c.rows();
    if (c.columns() != n) {
      throw new IllegalArgumentException();
    }

    double[] aa = getData(a);
    double[] ta = getData(tau);
    double[] ca = getData(c);

    double[] work = new double[1];
    int lwork = -1;
    intW info = new intW(0);
    lapack.dormqr(
        String.valueOf(side),
        transA.asString(),
        m,
        n,
        k,
        aa,
        lda,
        ta,
        ca,
        ldc,
        work,
        lwork,
        info
    );
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dormqr(
        String.valueOf(side),
        transA.asString(),
        m,
        n,
        k,
        aa,
        lda,
        ta,
        ca,
        ldc,
        work,
        lwork,
        info
    );
    ensureInfo(info);
    assignIfNeeded(a, aa);
    assignIfNeeded(tau, ta);
    assignIfNeeded(c, ca);
  }

  @Override
  public void syev(char jobz, char uplo, DoubleArray a, DoubleArray w) {
    jobz = Character.toLowerCase(jobz);
    uplo = Character.toLowerCase(uplo);
    if (!SYEVR_JOBZ_CHAR.contains(jobz)) {
      throw invalidCharacter("jobz", jobz, SYEVR_JOBZ_CHAR);
    }
    if (!SYEVR_UPLO.contains(uplo)) {
      throw invalidCharacter("uplo", uplo, SYEVR_UPLO);
    }

    if (!a.isSquare()) {
      throw new IllegalArgumentException("a is not square");
    }
    int n = a.rows();
    int lda = Math.max(1, n);
    if (!w.isVector() && w.size() != n) {
      throw new IllegalArgumentException();
    }

    double[] aa = getData(a);
    double[] wa = getData(w);

    intW info = new intW(0);
    int lwork = -1;
    double[] work = new double[1];
    lapack.dsyev(
        String.valueOf(jobz),
        String.valueOf(uplo),
        n,
        aa,
        lda,
        wa,
        work,
        lwork,
        info
    );
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dsyev(
        String.valueOf(jobz),
        String.valueOf(uplo),
        n,
        aa,
        lda,
        wa,
        work,
        lwork,
        info
    );
    ensureInfo(info);

    assignIfNeeded(a, aa);
    assignIfNeeded(w, wa);
  }

  @Override
  public int syevr(char jobz, char range, char uplo, DoubleArray a, double vl, double vu,
                   int il, int iu, double abstol, DoubleArray w, DoubleArray z,
                   IntArray isuppz) {
    Check.argument(a.isMatrix(), "a must be a 2d-array");
    Check.argument(a.isSquare(), "a is not square.");
    Check.argument(z.isMatrix(), "z must be a 2d-array");
    Check.argument(w.isVector(), "v must be a 1d-array");

    jobz = Character.toLowerCase(jobz);
    range = Character.toLowerCase(range);
    uplo = Character.toLowerCase(uplo);
    if (!SYEVR_JOBZ_CHAR.contains(jobz)) {
      throw invalidCharacter("jobz", jobz, SYEVR_JOBZ_CHAR);
    }
    if (!SYEVR_RANGE_CHAR.contains(range)) {
      throw invalidCharacter("range", range, SYEVR_RANGE_CHAR);
    }
    if (!SYEVR_UPLO.contains(uplo)) {
      throw invalidCharacter("uplo", uplo, SYEVR_UPLO);
    }

    int n = a.size(0);
    int lda = Math.max(1, n);

    if (!w.isVector() || w.size() != n) {
      throw new IllegalArgumentException();
    }

    if (jobz == 'n' || (z.rows() != n || z.columns() > n)) {
      throw new IllegalArgumentException();
    }

    if ((range == 'a' || range == 'n') &&
        (!isuppz.isVector() || isuppz.size() != 2 * Math.max(1, n))) {
      throw new IllegalArgumentException();
    }

    int ldz = 1;
    if (jobz == 'v') {
      ldz = Math.max(1, n);
    }

    double[] aa = getData(a);
    double[] wa = getData(w);
    double[] za = getData(z);
    int[] ia = getData(isuppz);

    intW info = new intW(0);
    intW m = new intW(0);
    // Workspace query
    double[] work = new double[1];
    int[] iwork = new int[1];
    int lwork = -1;
    int liwork = -1;

    lapack.dsyevr(
        String.valueOf(jobz),
        String.valueOf(range),
        String.valueOf(uplo),
        n,
        aa,
        lda,
        vl,
        vu,
        il,
        iu,
        abstol,
        m,
        wa,
        za,
        ldz,
        ia,
        work,
        lwork,
        iwork,
        liwork,
        info
    );
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];
    liwork = iwork[0];
    iwork = new int[liwork];

    lapack.dsyevr(
        String.valueOf(jobz),
        String.valueOf(range),
        String.valueOf(uplo),
        n,
        aa,
        lda,
        vl,
        vu,
        il,
        iu,
        abstol,
        m,
        wa,
        za,
        ldz,
        ia,
        work,
        lwork,
        iwork,
        liwork,
        info
    );
    ensureInfo(info);
    assignIfNeeded(a, aa);
    assignIfNeeded(w, wa);
    assignIfNeeded(z, za);
    reassignIfNeeded(isuppz, ia);

    return m.val;
  }

  @Override
  public int getrf(DoubleArray a, IntArray ipiv) {
    Check.all(BaseArray::isVector, ipiv);
    Check.size(Math.min(a.rows(), a.columns()), ipiv.size());
    double[] aa = getData(a);
    int[] ia = getData(ipiv);
    intW info = new intW(0);
    LAPACK.getInstance().dgetrf(
        a.rows(),
        a.columns(),
        aa,
        a.rows(),
        ia,
        info
    );
    ensureValidParameterInfo(info);
    assignIfNeeded(a, aa);
    reassignIfNeeded(ipiv, ia);
    return info.val;
  }

  @Override
  public int gelsy(DoubleArray a, DoubleArray b, IntArray jpvt, double rcond) {
    int m = a.rows();
    int n = a.columns();
    int nrhs = b.columns();
    int lda = Math.max(1, m);
    int ldb = Math.max(1, Math.max(m, n));

    int lwork = -1;
    double[] work = new double[1];
    intW rank = new intW(0);
    intW info = new intW(0);
    double[] aa = getData(a);
    double[] ba = getData(b);
    int[] ja = getData(jpvt);
    lapack.dgelsy(m, n, nrhs, aa, lda, ba, ldb, ja, rcond, rank, work, lwork, info);
    ensureInfo(info);

    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgelsy(m, n, nrhs, aa, lda, ba, ldb, ja, rcond, rank, work, lwork, info);
    ensureInfo(info);

    assignIfNeeded(a, aa);
    assignIfNeeded(b, ba);
    reassignIfNeeded(jpvt, ja);
    return rank.val;
  }

  @Override
  public int gesv(DoubleArray a, IntArray ipiv, DoubleArray b) {
    if (!a.isSquare()) {
      throw new IllegalArgumentException();
    }

    if (a.rows() != b.rows()) {
      throw new NonConformantException(a, b);
    }

    if (!ipiv.isVector() || a.rows() != ipiv.size()) {
      throw new IllegalArgumentException();
    }

    int n = a.rows();
    int nrhs = b.columns();
    int lda = Math.max(1, n);
    int ldb = Math.max(1, n);

    double[] aa = getData(a);
    double[] ba = getData(b);
    int[] ia = getData(ipiv);

    intW info = new intW(0);
    lapack.dgesv(
        n,
        nrhs,
        aa,
        lda,
        ia,
        ba,
        ldb,
        info
    );
    ensureValidParameterInfo(info);

    assignIfNeeded(a, aa);
    reassignIfNeeded(ipiv, ia);
    assignIfNeeded(b, ba);

    return info.val;
  }

  @Override
  public void gesvd(char jobu, char jobvt, DoubleArray a, DoubleArray s, DoubleArray u,
                    DoubleArray vt) {
    jobu = Character.toLowerCase(jobu);
    jobvt = Character.toLowerCase(jobvt);
    if (!GESVD_JOB_CHAR.contains(jobu)) {
      throw invalidCharacter("jobu", jobu, GESVD_JOB_CHAR);
    }

    if (!GESVD_JOB_CHAR.contains(jobvt)) {
      throw invalidCharacter("jobvt", jobvt, GESVD_JOB_CHAR);
    }

    int m = a.rows();
    int n = a.columns();
    int lda = Math.max(1, m);
    int ldu = u.rows();
    if (!s.isVector() && s.size() != Math.min(m, n)) {
      throw new IllegalArgumentException(
          String.format("Invalid shape for s (%s)", Arrays.toString(s.getShape())));
    }

    if (jobu == 'a' && (u.rows() != m || u.columns() != m)) {
      throw new IllegalArgumentException(
          String.format("Invalid shape for u (%s)", Arrays.toString(u.getShape())));
    }

    if (jobu == 's' && (u.rows() != m || u.columns() != Math.min(m, n))) {
      throw new IllegalArgumentException(
          String.format("Invalid shape for u (%s)", Arrays.toString(u.getShape())));
    }

    if (jobvt == 'a' && (vt.rows() != n || vt.columns() != n)) {
      throw new IllegalArgumentException(
          String.format("Invalid shape for vt (%s)", Arrays.toString(vt.getShape())));
    }

    int ldvt = n;
    if (jobvt == 's') {
      ldvt = Math.min(m, n);
    }

    double[] aa = getData(a);
    double[] ua = getData(u);
    double[] sa = getData(s);
    double[] vta = getData(vt);

    int lwork = -1;
    double[] work = new double[1];
    intW info = new intW(0);
    // Find the optimal work array size
    lapack.dgesvd(
        String.valueOf(jobu),
        String.valueOf(jobvt),
        m,
        n,
        aa,
        lda,
        sa,
        ua,
        ldu,
        vta,
        ldvt,
        work,
        lwork,
        info
    );
    ensureInfo("Failed to allocate workspace. (See error code for details)", info);

    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgesvd(
        String.valueOf(jobu),
        String.valueOf(jobvt),
        m,
        n,
        aa,
        lda,
        sa,
        ua,
        ldu,
        vta,
        ldvt,
        work,
        lwork,
        info
    );
    ensureInfo("Convergence failure", info);
    assignIfNeeded(a, aa);
    assignIfNeeded(u, ua);
    assignIfNeeded(s, sa);
    assignIfNeeded(vt, vta);
  }

  @Override
  public void gesdd(char jobz, DoubleArray a, DoubleArray s, DoubleArray u, DoubleArray vt) {
    jobz = Character.toLowerCase(jobz);
    if (!GESVD_JOB_CHAR.contains(jobz)) {
      throw invalidCharacter("jobz", jobz, GESVD_JOB_CHAR);
    }

    int m = a.rows();
    int n = a.columns();
    int lda = Math.max(1, m);
    int ldu = u.rows();
    if (!s.isVector() && s.size() != Math.min(m, n)) {
      throw new IllegalArgumentException(
          String.format("Invalid shape for s (%s)", Arrays.toString(s.getShape())));
    }

    if (jobz == 'a' && (u.rows() != m || u.columns() != m)) {
      throw new IllegalArgumentException(
          String.format("Invalid shape for u (%s)", Arrays.toString(u.getShape())));
    }

    if (jobz == 's' && (u.rows() != m || u.columns() != Math.min(m, n))) {
      throw new IllegalArgumentException(
          String.format("Invalid shape for u (%s)", Arrays.toString(u.getShape())));
    }

    if (jobz == 'a' && (vt.rows() != n || vt.columns() != n)) {
      throw new IllegalArgumentException(
          String.format("Invalid shape for vt (%s)", Arrays.toString(vt.getShape())));
    }

    int ldvt = n;
    if (jobz == 's') {
      ldvt = Math.min(m, n);
    }

    double[] aa = getData(a);
    double[] ua = getData(u);
    double[] sa = getData(s);
    double[] vta = getData(vt);

    int lwork = -1;
    double[] work = new double[1];
    int[] iwork = new int[8 * Math.min(m, n)];
    intW info = new intW(0);
    lapack.dgesdd(
        String.valueOf(jobz),
        m,
        n,
        aa,
        lda,
        sa,
        ua,
        ldu,
        vta,
        ldvt,
        work,
        lwork,
        iwork,
        info
    );
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgesdd(
        String.valueOf(jobz),
        m,
        n,
        aa,
        lda,
        sa,
        ua,
        ldu,
        vta,
        ldvt,
        work,
        lwork,
        iwork,
        info
    );
    ensureInfo(info);
  }

  /**
   * Returns the data of the double array. If a is a view (as defined above), a copy is returned.
   */
  private double[] getData(DoubleArray a) {
    if (a.getOffset() > 0 || a.stride(0) != 1) {
      return a.copy().data();
    } else {
      return a.data();
    }
  }

  private int[] getData(IntArray ipiv) {
    if (ipiv.getOffset() > 0 || ipiv.stride(0) != 1) {
      return ipiv.copy().data();
    } else {
      return ipiv.data();
    }
  }


  /**
   * Assigns the {@code data} to {@code a} if {@code a} is a view (as defined above).
   *
   * <p> The data is assigned to simulate out-parameters
   */
  private void assignIfNeeded(DoubleArray a, double[] data) {
    if (!(a instanceof NetlibDoubleArray) || a.getOffset() > 0 || a.stride(0) != 1) {
      a.assign(data);
    }
  }

  private void reassignIfNeeded(IntArray a, int[] data) {
    if (a.isView() || a.getOffset() > 0 || a.stride(0) != 1) {
      a.assign(data);
    }
  }

  private void ensureInfo(String message, intW info) {
    if (info.val != 0) {
      throw new NetlibLapackException(info.val, message);
    }
  }

  private void ensureInfo(intW info) {
    ensureInfo("Internal error.", info);
  }

  private void ensureValidParameterInfo(intW info) {
    if (info.val < 0) {
      throw new NetlibLapackException(info.val, "Internal error.");
    }
  }

  private IllegalArgumentException invalidCharacter(String parameter, char c,
                                                    List<Character> chars) {
    return new IllegalArgumentException(
        String.format("%s %s not in %s.", parameter, c, chars)
    );
  }
}
