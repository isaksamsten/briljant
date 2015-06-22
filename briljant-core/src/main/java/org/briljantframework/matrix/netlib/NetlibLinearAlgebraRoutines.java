package org.briljantframework.matrix.netlib;

import com.google.common.primitives.Chars;

import com.github.fommil.netlib.LAPACK;

import org.briljantframework.Check;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.linalg.api.AbstractLinearAlgebraRoutines;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.matrix.IntArray;
import org.briljantframework.matrix.Array;
import org.briljantframework.matrix.Op;
import org.briljantframework.matrix.api.ArrayFactory;
import org.netlib.util.intW;

import java.util.Arrays;

/**
 * @author Isak Karlsson
 */
public class NetlibLinearAlgebraRoutines extends AbstractLinearAlgebraRoutines {

  private static final LAPACK lapack = LAPACK.getInstance();

  public final static double MACHINE_EPSILON = Math.ulp(1);

  private static final char[] GESVD_JOB_CHAR = new char[]{'a', 's', 'o', 'n'};
  private static final char[] SYEVR_JOBZ_CHAR = new char[]{'n', 'v'};
  private static final char[] SYEVR_RANGE_CHAR = new char[]{'a', 'v', 'i'};
  static final char[] SYEVR_UPLO = new char[]{'l', 'u'};
  static final char[] ORMQR_SIDE = new char[]{'l', 'r'};

  protected NetlibLinearAlgebraRoutines(NetlibArrayBackend matrixFactory) {
    super(matrixFactory);
  }

  @Override
  public DoubleArray inv(DoubleArray x) {
    return null;
  }

  @Override
  public DoubleArray pinv(DoubleArray x) {
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
    d = d.slice(bj.range(r1));

//    for (int i = 0; i < v.rows(); i++) {
//      v.getRowView(i).assign(d, (a, b) -> a * b);
//    }
    final int vc = v.columns();
    final int vr = v.rows();
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
    int m = a.rows();
    int n = a.columns();
    int lda = Math.max(1, m);
    Check.vectorOfSize(Math.min(m, n), tau);

    double[] aa = a.data();
    double[] ta = tau.data();

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
    reassignIfNeeded(a, aa);
    reassignIfNeeded(tau, ta);
  }

  @Override
  public void ormqr(char side, Op transA, DoubleArray a, DoubleArray tau, DoubleArray c) {
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

    double[] aa = a.data();
    double[] ta = tau.data();
    double[] ca = c.data();

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
    reassignIfNeeded(a, aa);
    reassignIfNeeded(tau, ta);
    reassignIfNeeded(c, ca);
  }

  @Override
  public void syev(char jobz, char uplo, DoubleArray a, DoubleArray w) {
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

    double[] aa = a.data();
    double[] wa = w.data();

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

    reassignIfNeeded(a, aa);
    reassignIfNeeded(w, wa);
  }

  @Override
  public int syevr(char jobz, char range, char uplo, DoubleArray a, double vl, double vu,
                   int il, int iu, double abstol, DoubleArray w, DoubleArray z,
                   IntArray isuppz) {
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

    double[] aa = a.data();
    double[] wa = w.data();
    double[] za = z.data();
    int[] ia = isuppz.data();

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
    reassignIfNeeded(a, aa);
    reassignIfNeeded(w, wa);
    reassignIfNeeded(z, za);
    reassignIfNeeded(isuppz, ia);

    return m.val;
  }

  @Override
  public int getrf(DoubleArray a, IntArray ipiv) {
    Check.all(Array::isVector, ipiv);
    Check.size(Math.min(a.rows(), a.columns()), ipiv.size());
    double[] aa = a.data();
    int[] ia = ipiv.data();
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
    reassignIfNeeded(a, aa);
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
    double[] aa = a.data();
    double[] ba = b.data();
    int[] ja = jpvt.data();
    lapack.dgelsy(m, n, nrhs, aa, lda, ba, ldb, ja, rcond, rank, work, lwork, info);
    ensureInfo(info);

    lwork = (int) work[0];
    work = new double[lwork];
    lapack.dgelsy(m, n, nrhs, aa, lda, ba, ldb, ja, rcond, rank, work, lwork, info);
    ensureInfo(info);

    reassignIfNeeded(a, aa);
    reassignIfNeeded(b, ba);
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

    double[] aa = a.data();
    double[] ba = b.data();
    int[] ia = ipiv.data();

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

    reassignIfNeeded(a, aa);
    reassignIfNeeded(ipiv, ia);
    reassignIfNeeded(b, ba);

    return info.val;
  }

  @Override
  public void gesvd(char jobu, char jobvt, DoubleArray a, DoubleArray s, DoubleArray u,
                    DoubleArray vt) {
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

    double[] aa = a.data();
    double[] ua = u.data();
    double[] sa = s.data();
    double[] vta = vt.data();

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
    reassignIfNeeded(a, aa);
    reassignIfNeeded(u, ua);
    reassignIfNeeded(s, sa);
    reassignIfNeeded(vt, vta);
  }

  @Override
  public void gesdd(char jobz, DoubleArray a, DoubleArray s, DoubleArray u, DoubleArray vt) {
    jobz = Character.toLowerCase(jobz);
    if (!Chars.contains(GESVD_JOB_CHAR, jobz)) {
      throw invalidCharacter("jobz", jobz, GESVD_JOB_CHAR);
    }

    int m = a.rows();
    int n = a.columns();
    int lda = Math.max(1, m);
    int ldu = u.rows();
    if (!s.isVector() && s.size() != Math.min(m, n)) {
      throw new IllegalArgumentException(String.format("Invalid shape for s (%s)", s.getShape()));
    }

    if (jobz == 'a' && (u.rows() != m || u.columns() != m)) {
      throw new IllegalArgumentException(String.format("Invalid shape for u (%s)", u.getShape()));
    }

    if (jobz == 's' && (u.rows() != m || u.columns() != Math.min(m, n))) {
      throw new IllegalArgumentException(String.format("Invalid shape for u (%s)", u.getShape()));
    }

    if (jobz == 'a' && (vt.rows() != n || vt.columns() != n)) {
      throw new IllegalArgumentException(String.format("Invalid shape for vt (%s)", vt.getShape()));
    }

    int ldvt = n;
    if (jobz == 's') {
      ldvt = Math.min(m, n);
    }

    double[] aa = a.data();
    double[] ua = u.data();
    double[] sa = s.data();
    double[] vta = vt.data();

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

  private void reassignIfNeeded(DoubleArray a, double[] data) {
    if (a.isView()) {
      a.assign(data);
    }
  }

  private void reassignIfNeeded(IntArray a, int[] data) {
    if (a.isView()) {
      a.assign(data);
    }
  }

  private IllegalArgumentException invalidCharacter(String parameter, char c, char[] chars) {
    return new IllegalArgumentException(
        String.format("%s %s not in %s.", parameter, c, Arrays.toString(chars))
    );
  }
}
