/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.matrix.natives;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Created by Isak Karlsson on 26/06/14.
 */
public class Blas {

    /**
     * Optional major-order in calls to {@code cblas_*}
     */
    public static final int CblasRowMajor = 101;

    /**
     * Default major-order for the {@code org.adeb.matrix.Matrix}
     */
    public static final int CblasColMajor = 102;

    /**
     * Don't transpose
     */
    public static final int CblasNoTrans = 111;

    /**
     * Transpose
     */
    public static final int CblasTrans = 112;

    /**
     * Conjugate transpose - not supported yet (complex matrices not supported)
     */
    public static final int CblasConjTrans = 113;

    private static final Logger logger = Logger.getLogger(Blas.class.getSimpleName());

    /*
        This is a rather ad-hoc search for the correct library to load. This must be tested on a
        wide array of configurations.
     */
    static {
        try {
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
                Native.register("blas");
            }

        } catch (NoClassDefFoundError e) {
            logger.info("JNA not found. Native methods will be disabled.");
        } catch (UnsatisfiedLinkError e) {
            logger.info("Unable to link OpenBLAS. Native methods will be disabled.");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }


    /**
     * Multiplies each element of a vector by a constant (double-precision).
     *
     * @param N     The number of elements in vector x.
     * @param alpha The constant scaling factor to multiply by.
     * @param X     Vector x.
     * @param incX  Stride within X. For example, if incX is 7, every 7th element is used.
     */

    public static native void cblas_dscal(int N, double alpha, double[] X, int incX);

    /**
     * Computes the dot product of two vectors (double-precision).
     *
     * @param N    The number of elements in the vectors.
     * @param X    Vector X.
     * @param incX Stride within X. For example, if incX is 7, every 7th element is used.
     * @param Y    Vector Y.
     * @param incY Stride within Y. For example, if incY is 7, every 7th element is used.
     * @return dot product
     */
    public static native double cblas_ddot(int N, double[] X, int incX, double[] Y, int incY);

    /**
     * Computes the L2 norm (Euclidian length) of a vector (double precision).
     *
     * @param N    Length of vector X.
     * @param X    Vector X.
     * @param incX Stride within X. For example, if incX is 7, every 7th element is used.
     * @return euclidian distance
     */
    public static native double cblas_dnrm2(int N, double[] X, int incX);

    /**
     * Computes the sum of the absolute values of elements in a vector (double-precision).
     *
     * @param N    The number of elements in vector x.
     * @param X    Vector x.
     * @param incX Stride within X. For example, if incX is 7, every 7th element is used.
     * @return the absolute sum
     */
    public static native double cblas_dasum(int N, double[] X, int incX);

    /**
     * Cblas _ idamax.
     *
     * @param N    the n
     * @param X    the x
     * @param incX the inc x
     * @return the int
     */
    public static native int cblas_idamax(int N, double[] X, int incX);

    /**
     * Computes a constant times a vector plus a vector (double-precision).
     * <p>
     * On return, the contents of vector Y are replaced with the result. The value computed is (alpha * X[i]) + Y[i].
     *
     * @param N     Number of elements in the vectors.
     * @param alpha Scaling factor for the values in X.
     * @param X     Input vector X.
     * @param incX  Stride within X. For example, if incX is 7, every 7th element is used.
     * @param Y     Input vector Y.
     * @param incY  Stride within Y. For example, if incY is 7, every 7th element is used.
     */
    public static native void cblas_daxpy(int N, double alpha, double[] X, int incX, double[] Y, int incY);

    /**
     * Cblas _ dswap.
     *
     * @param N    the n
     * @param X    the x
     * @param incX the inc x
     * @param Y    the y
     * @param incY the inc y
     */
    public static native void cblas_dswap(int N, double[] X, int incX, double[] Y, int incY);

    /**
     * Cblas _ dcopy.
     *
     * @param N    the n
     * @param X    the x
     * @param incX the inc x
     * @param Y    the y
     * @param incY the inc y
     */
    public static native void cblas_dcopy(int N, double[] X, int incX, double[] Y, int incY);

    /**
     * Cblas _ dgemv.
     *
     * @param Order  the order
     * @param TransA the trans a
     * @param M      the m
     * @param N      the n
     * @param alpha  the alpha
     * @param A      the a
     * @param lda    the lda
     * @param X      the x
     * @param incX   the inc x
     * @param beta   the beta
     * @param Y      the y
     * @param incY   the inc y
     */
    public static native void cblas_dgemv(int Order, int TransA, int M, int N, double alpha, double[] A, int lda,
                                          double[] X, int incX, double beta, double[] Y, int incY);

    /**
     * Cblas _ dgemm.
     *
     * @param Order  the order
     * @param TransA the trans a
     * @param TransB the trans b
     * @param M      the m
     * @param N      the n
     * @param K      the k
     * @param alpha  the alpha
     * @param A      the a
     * @param lda    the lda
     * @param B      the b
     * @param ldb    the ldb
     * @param beta   the beta
     * @param C      the c
     * @param ldc    the ldc
     */
    public static native void cblas_dgemm(int Order, int TransA, int TransB, int M, int N, int K, double alpha,
                                          double[] A, int lda, double[] B, int ldb, double beta, double[] C, int ldc);


}
