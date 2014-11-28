package org.briljantframework.matrix.natives;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import com.sun.jna.ptr.IntByReference;

/**
 * Created by Isak Karlsson on 15/10/14.
 */
public class Lapack {

  /**
   * LAPACKE_* row-major order
   */
  public static final int LAPACK_ROW_MAJOR = 101;
  /**
   * LAPACKE_* column major order
   */
  public static final int LAPACK_COL_MAJOR = 102;
  private static final Logger logger = Logger.getLogger(Blas.class.getSimpleName());
  static {
    try {
      /*
       * On one hand, the integrated Lapack-library included in OpenBLAS is for the windows and mac
       * platform. On the other hand, a separate lapack (liblapack) and BLAS libraries are used on
       * other unixes (e.g., Debian).
       */
      if (Platform.isMac()) {
        if (Files.exists(Paths.get("/opt", "local", "lib"))) {
          NativeLibrary.addSearchPath("openblas", "/opt/local/lib/");
        } else if (Files.exists(Paths.get("/usr", "local", "opt", "openblas", "lib"))) {
          NativeLibrary.addSearchPath("openblas", "/usr/local/opt/openblas/lib");
        } else {
          logger.severe("Unable to find MacPorts or Homebrew installed");
          logger.severe("Openblas might or might not work.");
          logger.severe("Consider installing MacPorts or Homebrew and then install openblas");
        }
        Native.register("openblas");
      } else if (Platform.isWindows()) {
        Native.register("openblas");
      } else {
        Native.register("lapack");
      }
    } catch (NoClassDefFoundError e) {
      logger.info("JNA not found. Native methods will be disabled.");
    } catch (UnsatisfiedLinkError e) {
      logger.info("Unable to link to LAPACK. Native methods will be disabled.");
      logger.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  /**
   * LAPACKE _ dgetrf.
   *
   * @param matrix_order the matrix _ order
   * @param m the m
   * @param n the n
   * @param a the a
   * @param lda the lda
   * @param ipiv the ipiv
   * @return the int
   */
  public static native int LAPACKE_dgetrf(int matrix_order, int m, int n, double[] a, int lda,
      int[] ipiv);

  /**
   * LAPACKE _ dgetri.
   *
   * @param matrix_order the matrix _ order
   * @param n the n
   * @param a the a
   * @param lda the lda
   * @param ipiv the ipiv
   * @return the int
   */
  public static native int LAPACKE_dgetri(int matrix_order, int n, double[] a, int lda, int[] ipiv);

  /**
   * LAPACKE _ dgesvd.
   *
   * @param matrix_order the matrix _ order
   * @param jobu the jobu
   * @param jobvt the jobvt
   * @param M the m
   * @param N the n
   * @param A the a
   * @param lda the lda
   * @param S the s
   * @param U the u
   * @param ldu the ldu
   * @param VT the vT
   * @param ldvt the ldvt
   * @param work the work
   * @return the int
   */
  public static native int LAPACKE_dgesvd(int matrix_order, char jobu, char jobvt, int M, int N,
      double[] A, int lda, double[] S, double[] U, int ldu, double[] VT, int ldvt, double[] work);

  /**
   * LAPACKE _ dgelsy.
   *
   * @param matrix_order the matrix _ order
   * @param m the m
   * @param n the n
   * @param nrhs the nrhs
   * @param a the a
   * @param lda the lda
   * @param b the b
   * @param ldb the ldb
   * @param jpvt the jpvt
   * @param rcond the rcond
   * @param rank the rank
   * @return the int
   */
  public static native int LAPACKE_dgelsy(int matrix_order, int m, int n, int nrhs, double[] a,
      int lda, double[] b, int ldb, int[] jpvt, double rcond, IntByReference rank);
}
