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
package org.briljantframework.array.netlib;

import java.util.Arrays;
import java.util.List;

import org.briljantframework.Check;
import org.briljantframework.array.*;
import org.briljantframework.array.api.AbstractLinearAlgebraRoutines;
import org.briljantframework.exceptions.MultiDimensionMismatchException;
import org.netlib.util.intW;

import com.github.fommil.netlib.LAPACK;

/**
 * This class implements the linear algebra routines (commonly LAPACK) using the netlib-java fortran
 * wrappers.
 *
 * <p>
 * Since java does not allow for taking a pointer to a slice of an input array, the routines
 * implemented here copies the values of array-slices into new arrays and then re-inserts them. The
 * array must be copied if: {@code a.stride(0) != 1} or {@code a.getOffset() > 0} or
 * {@code a.isView() == true}.
 *
 * @author Isak Karlsson
 */
class NetlibLinearAlgebraRoutines extends AbstractLinearAlgebraRoutines {

  private static final String REQUIRE_2D_ARRAY = "require 2d-array";
  static final List<Character> UPLO_CHAR = Arrays.asList('l', 'u');
  static final List<Character> ORMQR_SIDE = Arrays.asList('l', 'r');
  private static final LAPACK lapack = LAPACK.getInstance();
  private static final List<Character> GESVD_JOB_CHAR = Arrays.asList('a', 's', 'o', 'n');
  private static final List<Character> JOBZ_CHAR = Arrays.asList('n', 'v');
  private static final List<Character> SYEVR_RANGE_CHAR = Arrays.asList('a', 'v', 'i');

  NetlibLinearAlgebraRoutines(NetlibArrayBackend matrixFactory) {
    super(matrixFactory);
  }

  @Override
  public double rank(DoubleArray x) {
    return super.rank(x); // TODO: improve by only computing the singular values
  }

  private double[] getData(ComplexArray array) {
    return array.data();
  }

  private void assignIfNeeded(ComplexArray a, double[] data) {
    a.assign(data);
  }

  @Override
  public void geev(char jobvl, char jobvr, DoubleArray a, DoubleArray wr, DoubleArray wi,
      DoubleArray vl, DoubleArray vr) {
    Check.argument(a.isMatrix(), REQUIRE_2D_ARRAY);
    Check.argument(a.rows() == a.columns(), "Require square 2d-array");
    int n = a.size(1);

    Check.argument(wr.isVector() && wr.size() == n);
    Check.argument(wi.isVector() && wi.size() == n);

    int ldvl = 1;
    if (jobvl == 'v') {
      ldvl = n;
      Check.argument(vl.isMatrix() && vl.rows() == vl.columns() && vl.rows() == ldvl);
    }

    int ldvr = 1;
    if (jobvr == 'v') {
      ldvr = n;
      Check.argument(vr.isMatrix() && vr.rows() == vr.columns() && vr.rows() == ldvr,
          "Illegal 'vr' 2d-array");
    }

    double[] aa = getData(a);
    double[] wra = getData(wr);
    double[] wia = getData(wi);
    double[] vla = getData(vl);
    double[] vra = getData(vr);

    double[] work = new double[1];
    int lwork = -1;
    intW info = new intW(0);
    lapack.dgeev(String.valueOf(jobvl), String.valueOf(jobvr), n, aa, Math.max(1, n), wra, wia, vla,
        ldvl, vra, ldvr, work, lwork, info);
    ensureInfo(info);
    lwork = (int) work[0];

    lapack.dgeev(String.valueOf(jobvl), String.valueOf(jobvr), n, aa, Math.max(1, n), wra, wia, vla,
        Math.max(1, ldvl), vra, Math.max(1, ldvr), work, lwork, info);

    ensureInfo(info);
    assignIfNeeded(a, aa);
    assignIfNeeded(wr, wra);
    assignIfNeeded(wi, wia);
    assignIfNeeded(vl, vla);
    assignIfNeeded(vr, vra);
  }

  @Override
  public void ormqr(char side, ArrayOperation transA, DoubleArray a, DoubleArray tau,
      DoubleArray c) {
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
    lapack.dormqr(String.valueOf(side), transA.getCblasString(), m, n, k, aa, lda, ta, ca, ldc,
        work, lwork, info);
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dormqr(String.valueOf(side), transA.getCblasString(), m, n, k, aa, lda, ta, ca, ldc,
        work, lwork, info);
    ensureInfo(info);
    assignIfNeeded(a, aa);
    assignIfNeeded(tau, ta);
    assignIfNeeded(c, ca);
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
    lapack.dgeqrf(m, n, aa, lda, ta, work, lwork, info);
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];

    lapack.dgeqrf(m, n, aa, lda, ta, work, lwork, info);
    ensureInfo(info);
    assignIfNeeded(a, aa);
    assignIfNeeded(tau, ta);
  }

  @Override
  public void syev(char jobz, char uplo, DoubleArray a, DoubleArray w) {
    jobz = Character.toLowerCase(jobz);
    uplo = Character.toLowerCase(uplo);
    if (!JOBZ_CHAR.contains(jobz)) {
      throw invalidCharacter("jobz", jobz, JOBZ_CHAR);
    }
    if (!UPLO_CHAR.contains(uplo)) {
      throw invalidCharacter("uplo", uplo, UPLO_CHAR);
    }

    if (!a.isSquare()) {
      throw new IllegalArgumentException("a is not square");
    }
    int n = a.rows();
    int lda = Math.max(1, n);
    if (!w.isVector() && w.size() != n) {
      throw new IllegalArgumentException();
    }
    DoubleArray safeA = copyIfView(a);
    DoubleArray safeW = copyIfView(w);

    intW info = new intW(0);
    int lwork = -1;
    double[] work = new double[1];
    lapack.dsyev(String.valueOf(jobz), String.valueOf(uplo), n, getBackingArray(safeA),
        Math.max(1, safeA.stride(1)), getBackingArray(safeW), work, lwork, info);
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dsyev(String.valueOf(jobz), String.valueOf(uplo), n, getBackingArray(safeA),
        Math.max(1, safeA.stride(1)), getBackingArray(safeW), work, lwork, info);
    ensureInfo(info);


    if (safeA != a) {
      a.assign(safeA);
    }
    if (safeW != w) {
      w.assign(safeW);
    }
  }

  private boolean isView(BaseArray<?> array) {
    return array.isView()
        || (!array.isContiguous() || array.getOffset() > 0 || array.stride(0) != 1);
  }

  private <S extends BaseArray<S>> S copyIfView(S array) {
    return isView(array) ? array.copy() : array;
  }

  @Override
  public void syevd(char jobz, char uplo, DoubleArray a, DoubleArray w) {
    jobz = Character.toLowerCase(jobz);
    uplo = Character.toLowerCase(uplo);
    if (!JOBZ_CHAR.contains(jobz)) {
      throw invalidCharacter("jobz", jobz, JOBZ_CHAR);
    }
    if (!UPLO_CHAR.contains(uplo)) {
      throw invalidCharacter("uplo", uplo, UPLO_CHAR);
    }
    Check.argument(a.isSquare(), "a must be square");

    int n = a.rows();
    int lda = Math.max(1, n);
    Check.argument(w.isVector() && w.size() == n, "illegal output size");

    double[] aa = getData(a);
    double[] wa = getData(w);
    intW info = new intW(0);
    int lwork = -1;
    int liwork = -1;
    double[] work = new double[1];
    int[] iwork = new int[1];
    lapack.dsyevd(String.valueOf(jobz), String.valueOf(uplo), n, aa, lda, wa, work, lwork, iwork,
        liwork, info);
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];
    liwork = iwork[0];
    iwork = new int[liwork];
    lapack.dsyevd(String.valueOf(jobz), String.valueOf(uplo), n, aa, lda, wa, work, lwork, iwork,
        liwork, info);
    ensureInfo(info);
    assignIfNeeded(a, aa);
    assignIfNeeded(w, wa);
  }

  @Override
  public int syevr(char jobz, char range, char uplo, DoubleArray a, double vl, double vu, int il,
      int iu, double abstol, DoubleArray w, DoubleArray z, IntArray isuppz) {
    Check.argument(a.isMatrix(), "a must be a 2d-array");
    Check.argument(a.isSquare(), "a is not square.");
    Check.argument(z.isMatrix(), "z must be a 2d-array");
    Check.argument(w.isVector(), "v must be a 1d-array");

    jobz = Character.toLowerCase(jobz);
    range = Character.toLowerCase(range);
    uplo = Character.toLowerCase(uplo);
    if (!JOBZ_CHAR.contains(jobz)) {
      throw invalidCharacter("jobz", jobz, JOBZ_CHAR);
    }
    if (!SYEVR_RANGE_CHAR.contains(range)) {
      throw invalidCharacter("range", range, SYEVR_RANGE_CHAR);
    }
    if (!UPLO_CHAR.contains(uplo)) {
      throw invalidCharacter("uplo", uplo, UPLO_CHAR);
    }

    int n = a.size(0);
    int lda = Math.max(1, n);

    if (!w.isVector() || w.size() != n) {
      throw new IllegalArgumentException();
    }

    if (jobz == 'n' || (z.rows() != n || z.columns() > n)) {
      throw new IllegalArgumentException();
    }

    if ((range == 'a' || range == 'n')
        && (!isuppz.isVector() || isuppz.size() != 2 * Math.max(1, n))) {
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

    lapack.dsyevr(String.valueOf(jobz), String.valueOf(range), String.valueOf(uplo), n, aa, lda, vl,
        vu, il, iu, abstol, m, wa, za, ldz, ia, work, lwork, iwork, liwork, info);
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];
    liwork = iwork[0];
    iwork = new int[liwork];

    lapack.dsyevr(String.valueOf(jobz), String.valueOf(range), String.valueOf(uplo), n, aa, lda, vl,
        vu, il, iu, abstol, m, wa, za, ldz, ia, work, lwork, iwork, liwork, info);
    ensureInfo(info);
    assignIfNeeded(a, aa);
    assignIfNeeded(w, wa);
    assignIfNeeded(z, za);
    assignIfNeeded(isuppz, ia);

    return m.val;
  }

  @Override
  public int getrf(DoubleArray a, IntArray ipiv) {
    Check.argument(ipiv.isVector(), "ipiv must be a series");
    Check.argument(a.isMatrix(), "a must be a 2d-array");
    Check.dimension(Math.min(a.rows(), a.columns()), ipiv.size());
    DoubleArray aCopy = copyIfView(a);
    IntArray ipivCopy = copyIfView(ipiv);
    intW info = new intW(0);
    lapack.dgetrf(a.rows(), a.columns(), getBackingArray(aCopy), a.rows(), getBackingArray(ipivCopy), info);
    ensureValidParameterInfo(info);
    copyToIfNeeded(a, aCopy);
    copyToIfNeeded(ipiv, ipivCopy);
    return info.val;
  }

  private double[] getBackingArray(DoubleArray doubleArray) {
    return ((NetlibDoubleArray) doubleArray).getBackingArray();
  }

  private int[] getBackingArray(IntArray intArray) {
    return ((NetlibIntArray)intArray).getBackingArray();
  }

  @Override
  public int getri(DoubleArray a, IntArray ipiv) {
    Check.argument(ipiv.isVector(), "ipiv must be a series");
    int n = a.size(1);
    Check.dimension(ipiv.size(), n);

    int lda = Math.max(1, a.size(0));
    int lwork = -1;
    double[] work = new double[1];
    DoubleArray aCopy = copyIfView(a);
    IntArray ipivCopy = copyIfView(ipiv);

    intW info = new intW(0);
    lapack.dgetri(n, getBackingArray(aCopy), lda, getBackingArray(ipivCopy), work, lwork, info);
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgetri(n, getBackingArray(aCopy), lda, getBackingArray(ipivCopy), work, lwork, info);

    copyToIfNeeded(ipiv, ipivCopy);
    copyToIfNeeded(a, aCopy);
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
    assignIfNeeded(jpvt, ja);
    return rank.val;
  }

  @Override
  public int gesv(DoubleArray a, IntArray ipiv, DoubleArray b) {
    if (!a.isSquare()) {
      throw new IllegalArgumentException();
    }

    if (a.rows() != b.rows()) {
      throw new MultiDimensionMismatchException(a, b);
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
    lapack.dgesv(n, nrhs, aa, lda, ia, ba, ldb, info);
    ensureValidParameterInfo(info);

    assignIfNeeded(a, aa);
    assignIfNeeded(ipiv, ia);
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
    lapack.dgesvd(String.valueOf(jobu), String.valueOf(jobvt), m, n, aa, lda, sa, ua, ldu, vta,
        ldvt, work, lwork, info);
    ensureInfo("Failed to allocate workspace. (See error code for details)", info);

    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgesvd(String.valueOf(jobu), String.valueOf(jobvt), m, n, aa, lda, sa, ua, ldu, vta,
        ldvt, work, lwork, info);
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
    lapack.dgesdd(String.valueOf(jobz), m, n, aa, lda, sa, ua, ldu, vta, ldvt, work, lwork, iwork,
        info);
    ensureInfo(info);
    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgesdd(String.valueOf(jobz), m, n, aa, lda, sa, ua, ldu, vta, ldvt, work, lwork, iwork,
        info);
    ensureInfo(info);
  }

  private void ensureValidParameterInfo(intW info) {
    if (info.val < 0) {
      throw new NetlibLapackException(info.val, "Internal error.");
    }
  }

  private int[] getData(IntArray ipiv) {
    if (ipiv.getOffset() > 0 || ipiv.stride(0) != 1) {
      return getBackingArray(ipiv.copy());
    } else {
      return getBackingArray(ipiv);
    }
  }

  private void assignIfNeeded(IntArray a, int[] data) {
    if (a.isView() || a.getOffset() > 0 || a.stride(0) != 1) {
      a.assign(data);
    }
  }

  private <S extends BaseArray<S>> void copyToIfNeeded(S to, S from) {
    if (to != from) {
      to.assign(from);
    }
  }

  private IllegalArgumentException invalidCharacter(String parameter, char c,
      List<Character> chars) {
    return new IllegalArgumentException(String.format("%s %s not in %s.", parameter, c, chars));
  }

  /**
   * Returns the data of the double array. If a is a view (as defined above), a copy is returned.
   */
  private double[] getData(DoubleArray a) {
    if (!a.isContiguous() || a.getOffset() > 0 || a.stride(0) != 1) {
      return getBackingArray(a.copy());
    } else {
      return getBackingArray(a);
    }
  }

  /**
   * Assigns the {@code data} to {@code a} if {@code a} is a view (as defined above).
   *
   * <p/>
   * The data is assigned to simulate out-parameters
   */
  private void assignIfNeeded(DoubleArray a, double[] data) {
    if (!(a instanceof NetlibDoubleArray) || a.getOffset() > 0 || a.stride(0) != 1) {
      a.assign(data);
    }
  }

  private void ensureInfo(intW info) {
    ensureInfo("Internal error.", info);
  }

  private void ensureInfo(String message, intW info) {
    if (info.val != 0) {
      throw new NetlibLapackException(info.val, message);
    }
  }
}
