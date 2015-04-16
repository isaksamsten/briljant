package org.briljantframework.matrix.netlib;

import com.google.common.primitives.Chars;

import com.github.fommil.netlib.LAPACK;

import org.briljantframework.Check;
import org.briljantframework.exceptions.BlasException;
import org.briljantframework.linalg.api.AbstractLinearAlgebraRoutines;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.storage.DoubleArrayStorage;
import org.briljantframework.matrix.storage.IntArrayStorage;
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

  NetlibLinearAlgebraRoutines(
      MatrixFactory matrixFactory) {
    super(matrixFactory);
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

    TypeChecks.ensureInstanceOf(DoubleArrayStorage.class, sa, sw);

    double[] aa = ((DoubleArrayStorage) sa).array();
    double[] wa = ((DoubleArrayStorage) sw).array();

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
    if (info.val != 0) {
      throw new BlasException(info.val, "...");
    }
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

    TypeChecks.ensureInstanceOf(DoubleArrayStorage.class, as, ws, zs);
    TypeChecks.ensureInstanceOf(IntArrayStorage.class, is);

    double[] aa = ((DoubleArrayStorage) as).array();
    double[] wa = ((DoubleArrayStorage) ws).array();
    double[] za = ((DoubleArrayStorage) zs).array();
    int[] ia = ((IntArrayStorage) is).array();

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

    if (info.val != 0) {
      throw new BlasException(info.val, "...");
    }

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
    if (info.val != 0) {
      throw new BlasException(info.val, "...");
    }

    if (a.isView()) {
      a.assign(aa);
    }

    if (isuppz.isView()) {
      isuppz.assign(ia);
    }

    if (w.isView()) {
      w.assign(wa);
    }

    if (z.isView()) {
      z.assign(za);
    }

    return m.val;
  }

  @Override
  public int getrf(DoubleMatrix a, IntMatrix ipiv) {
    Check.all(Matrix::isVector, ipiv);
    Check.size(Math.min(a.rows(), a.columns()), ipiv.size());
    Storage sa = a.getStorage();
    Storage si = ipiv.getStorage();
    if (sa instanceof DoubleArrayStorage && si instanceof IntArrayStorage) {
      double[] aa = ((DoubleArrayStorage) sa).array();
      int[] ia = ((IntArrayStorage) si).array();
      intW info = new intW(0);
      LAPACK.getInstance().dgetrf(
          a.rows(),
          a.columns(),
          aa,
          a.rows(),
          ia,
          info
      );
      if (info.val < 0) {
        throw new BlasException(info.val, "LU decomposition failed");
      }

      if (a.isView()) {
        a.assign(aa);
      }
      if (ipiv.isView()) {
        ipiv.assign(ia);
      }
      return info.val;
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public int gelsy(DoubleMatrix a, DoubleMatrix b, IntMatrix jpvt, double rcond) {
    Storage sa = a.getStorage();
    Storage sb = b.getStorage();
    Storage sj = jpvt.getStorage();

    TypeChecks.ensureInstanceOf(DoubleArrayStorage.class, sa, sb);
    TypeChecks.ensureInstanceOf(IntArrayStorage.class, sj);

    int m = a.rows();
    int n = a.columns();
    int nrhs = b.columns();
    int lda = Math.max(1, m);
    int ldb = Math.max(1, Math.max(m, n));

    int lwork = -1;
    double[] work = new double[1];
    intW rank = new intW(0);
    intW info = new intW(0);
    double[] aa = ((DoubleArrayStorage) sa).array();
    double[] ba = ((DoubleArrayStorage) sb).array();
    int[] ja = ((IntArrayStorage) sj).array();
    lapack.dgelsy(m, n, nrhs, aa, lda, ba, ldb, ja, rcond, rank, work, lwork, info);
    if (info.val != 0) {
      throw new BlasException(info.val, "");
    }

    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgelsy(m, n, nrhs, aa, lda, ba, ldb, ja, rcond, rank, work, lwork, info);
    if (info.val != 0) {
      throw new BlasException(info.val, "");
    }

    if (a.isView()) {
      a.assign(aa);
    }

    if (b.isView()) {
      b.assign(ba);
    }

    if (jpvt.isView()) {
      jpvt.assign(ja);
    }

    return rank.val;
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
    TypeChecks.ensureInstanceOf(DoubleArrayStorage.class, as, ss, us, vts);

    double[] aa = ((DoubleArrayStorage) as).array();
    double[] ua = ((DoubleArrayStorage) us).array();
    double[] sa = ((DoubleArrayStorage) ss).array();
    double[] vta = ((DoubleArrayStorage) vts).array();

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
    if (info.val != 0) {
      throw new BlasException(
          info.val, "Failed to allocate workspace. (See error code for details)"
      );
    }

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
    if (info.val != 0) {
      throw new BlasException(
          info.val, "Convergence failure. (See errorCode for details)."
      );
    }
  }

  private IllegalArgumentException invalidCharacter(String parameter, char c, char[] chars) {
    return new IllegalArgumentException(String.format("%s %s not in %s.", parameter, c, chars));
  }
}
