package org.briljantframework.matrix.netlib;

import com.google.common.primitives.Chars;

import com.github.fommil.netlib.LAPACK;

import org.briljantframework.Check;
import org.briljantframework.exceptions.BlasException;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.linalg.api.AbstractLinearAlgebraRoutines;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.Transpose;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.storage.Storage;
import org.netlib.util.intW;

/**
 * @author Isak Karlsson
 */
class NetlibLinearAlgebraRoutines extends AbstractLinearAlgebraRoutines {

  private static final LAPACK lapack = LAPACK.getInstance();

  private static final char[] GESVD_JOB_CHAR = new char[]{'a', 's', 'o', 'n'};
  private static final char[] SYEVR_JOBZ_CHAR = new char[]{'n', 'v'};
  private static final char[] SYEVR_RANGE_CHAR = new char[]{'a', 'v', 'i'};
  public static final char[] SYEVR_UPLO = new char[]{'l', 'u'};
  public static final char[] ORMQR_SIDE = new char[]{'l', 'r'};

  NetlibLinearAlgebraRoutines(
      MatrixFactory matrixFactory) {
    super(matrixFactory);
  }

  @Override
  public void geqrf(DoubleMatrix a, DoubleMatrix tau) {
    int m = a.rows();
    int n = a.columns();
    int lda = Math.max(1, m);
    Check.vectorOfSize(Math.min(m, n), tau);

    Storage sa = a.getStorage();
    Storage st = tau.getStorage();
    double[] aa = sa.doubleArray();
    double[] ta = st.doubleArray();

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
    reassignIfNeeded(a, sa, aa);
    reassignIfNeeded(tau, st, ta);
  }

  @Override
  public void ormqr(char side, Transpose transA, DoubleMatrix a, DoubleMatrix tau, DoubleMatrix c) {
    side = Character.toLowerCase(side);
    if (!Chars.contains(ORMQR_SIDE, side)) {
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
    Check.vectorOfSize(k, tau);

    int ldc = c.rows();
    if (c.columns() != n) {
      throw new IllegalArgumentException();
    }

    Storage as = a.getStorage();
    Storage ts = tau.getStorage();
    Storage cs = c.getStorage();
    double[] aa = as.doubleArray();
    double[] ta = ts.doubleArray();
    double[] ca = cs.doubleArray();

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
    reassignIfNeeded(a, as, aa);
    reassignIfNeeded(tau, ts, ta);
    reassignIfNeeded(c, cs, ca);
  }

  @Override
  public void syev(char jobz, char uplo, DoubleMatrix a, DoubleMatrix w) {
    jobz = Character.toLowerCase(jobz);
    uplo = Character.toLowerCase(uplo);
    if (!Chars.contains(SYEVR_JOBZ_CHAR, jobz)) {
      throw invalidCharacter("jobz", jobz, SYEVR_JOBZ_CHAR);
    }
    if (!Chars.contains(SYEVR_UPLO, uplo)) {
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

    Storage sa = a.getStorage();
    Storage sw = w.getStorage();

    double[] aa = sa.doubleArray();
    double[] wa = sw.doubleArray();

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

    reassignIfNeeded(a, sa, aa);
    reassignIfNeeded(w, sw, wa);
  }

  @Override
  public int syevr(char jobz, char range, char uplo, DoubleMatrix a, double vl, double vu,
                   int il, int iu, double abstol, DoubleMatrix w, DoubleMatrix z,
                   IntMatrix isuppz) {
    jobz = Character.toLowerCase(jobz);
    range = Character.toLowerCase(range);
    uplo = Character.toLowerCase(uplo);
    if (!Chars.contains(SYEVR_JOBZ_CHAR, jobz)) {
      throw invalidCharacter("jobz", jobz, SYEVR_JOBZ_CHAR);
    }
    if (!Chars.contains(SYEVR_RANGE_CHAR, range)) {
      throw invalidCharacter("range", range, SYEVR_RANGE_CHAR);
    }
    if (!Chars.contains(SYEVR_UPLO, uplo)) {
      throw invalidCharacter("uplo", uplo, SYEVR_UPLO);
    }

    if (!a.isSquare()) {
      throw new IllegalArgumentException("a is not square");
    }

    int n = a.rows();
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

    Storage as = a.getStorage();
    Storage ws = w.getStorage();
    Storage zs = z.getStorage();
    Storage is = isuppz.getStorage();

    double[] aa = as.doubleArray();
    double[] wa = ws.doubleArray();
    double[] za = zs.doubleArray();
    int[] ia = is.intArray();

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
    reassignIfNeeded(a, as, aa);
    reassignIfNeeded(w, ws, wa);
    reassignIfNeeded(z, zs, za);
    reassignIfNeeded(isuppz, is, ia);

    return m.val;
  }

  @Override
  public int getrf(DoubleMatrix a, IntMatrix ipiv) {
    Check.all(Matrix::isVector, ipiv);
    Check.size(Math.min(a.rows(), a.columns()), ipiv.size());
    Storage sa = a.getStorage();
    Storage si = ipiv.getStorage();
    double[] aa = sa.doubleArray();
    int[] ia = si.intArray();
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
    reassignIfNeeded(a, sa, aa);
    reassignIfNeeded(ipiv, si, ia);
    return info.val;
  }

  @Override
  public int gelsy(DoubleMatrix a, DoubleMatrix b, IntMatrix jpvt, double rcond) {
    Storage sa = a.getStorage();
    Storage sb = b.getStorage();
    Storage sj = jpvt.getStorage();

    int m = a.rows();
    int n = a.columns();
    int nrhs = b.columns();
    int lda = Math.max(1, m);
    int ldb = Math.max(1, Math.max(m, n));

    int lwork = -1;
    double[] work = new double[1];
    intW rank = new intW(0);
    intW info = new intW(0);
    double[] aa = sa.doubleArray();
    double[] ba = sb.doubleArray();
    int[] ja = sj.intArray();
    lapack.dgelsy(m, n, nrhs, aa, lda, ba, ldb, ja, rcond, rank, work, lwork, info);
    ensureInfo(info);

    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgelsy(m, n, nrhs, aa, lda, ba, ldb, ja, rcond, rank, work, lwork, info);
    ensureInfo(info);

    reassignIfNeeded(a, sa, aa);
    reassignIfNeeded(b, sb, ba);
    reassignIfNeeded(jpvt, sj, ja);
    return rank.val;
  }

  @Override
  public int gesv(DoubleMatrix a, IntMatrix ipiv, DoubleMatrix b) {
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

    Storage as = a.getStorage();
    Storage bs = b.getStorage();
    Storage is = ipiv.getStorage();

    double[] aa = as.doubleArray();
    double[] ba = bs.doubleArray();
    int[] ia = is.intArray();

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

    reassignIfNeeded(a, as, aa);
    reassignIfNeeded(ipiv, is, ia);
    reassignIfNeeded(b, bs, ba);

    return info.val;
  }

  @Override
  public void gesvd(char jobu, char jobvt, DoubleMatrix a, DoubleMatrix s, DoubleMatrix u,
                    DoubleMatrix vt) {
    jobu = Character.toLowerCase(jobu);
    jobvt = Character.toLowerCase(jobvt);
    if (!Chars.contains(GESVD_JOB_CHAR, jobu)) {
      throw invalidCharacter("jobu", jobu, GESVD_JOB_CHAR);
    }

    if (!Chars.contains(GESVD_JOB_CHAR, jobvt)) {
      throw invalidCharacter("jobvt", jobvt, GESVD_JOB_CHAR);
    }

    int m = a.rows();
    int n = a.columns();
    int lda = Math.max(1, m);
    int ldu = u.rows();
    if (!s.isVector() && s.size() != Math.min(m, n)) {
      throw new IllegalArgumentException(String.format("Invalid shape for s (%s)", s.getShape()));
    }

    if (jobu == 'a' && (u.rows() != m || u.columns() != m)) {
      throw new IllegalArgumentException(String.format("Invalid shape for u (%s)", u.getShape()));
    }

    if (jobu == 's' && (u.rows() != m || u.columns() != Math.min(m, n))) {
      throw new IllegalArgumentException(String.format("Invalid shape for u (%s)", u.getShape()));
    }

    if (jobvt == 'a' && (vt.rows() != n || vt.columns() != n)) {
      throw new IllegalArgumentException(String.format("Invalid shape for vt (%s)", vt.getShape()));
    }

    int ldvt = n;
    if (jobvt == 's') {
      ldvt = Math.min(m, n);
    }

    Storage as = a.getStorage();
    Storage us = u.getStorage();
    Storage ss = s.getStorage();
    Storage vts = vt.getStorage();

    double[] aa = as.doubleArray();
    double[] ua = us.doubleArray();
    double[] sa = ss.doubleArray();
    double[] vta = vts.doubleArray();

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
    ensureInfo("Convergence failure. (See errorCode for details).", info);
    reassignIfNeeded(a, as, aa);
    reassignIfNeeded(u, us, ua);
    reassignIfNeeded(s, ss, sa);
    reassignIfNeeded(vt, vts, vta);
  }

  private void ensureInfo(String message, intW info) {
    if (info.val != 0) {
      throw new BlasException(info.val, message);
    }
  }

  private void ensureInfo(intW info) {
    ensureInfo("Internal error.", info);
  }

  private void ensureValidParameterInfo(intW info) {
    if (info.val < 0) {
      throw new BlasException(info.val, "Internal error.");
    }
  }

  private void reassignIfNeeded(DoubleMatrix a, Storage s, double[] data) {
    if (!s.getNativeType().equals(Double.TYPE) || !s.isArrayBased()) {
      a.assign(data);
    }
  }

  private void reassignIfNeeded(IntMatrix a, Storage s, int[] data) {
    if (!s.getNativeType().equals(Integer.TYPE) || !s.isArrayBased()) {
      a.assign(data);
    }
  }

  private IllegalArgumentException invalidCharacter(String parameter, char c, char[] chars) {
    return new IllegalArgumentException(String.format("%s %s not in %s.", parameter, c, chars));
  }
}
