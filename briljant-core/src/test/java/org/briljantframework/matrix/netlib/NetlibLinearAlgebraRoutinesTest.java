package org.briljantframework.matrix.netlib;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.api.MatrixBackend;
import org.briljantframework.matrix.api.MatrixFactory;
import org.junit.Test;

import static org.briljantframework.matrix.MatrixAssert.assertMatrixEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NetlibLinearAlgebraRoutinesTest {

  MatrixBackend b = new NetlibMatrixBackend();
  MatrixFactory bj = b.getMatrixFactory();
  LinearAlgebraRoutines linalg = b.getLinearAlgebraRoutines();


  @Test
  public void testGetrf() throws Exception {
    DoubleMatrix d = bj.matrix(new double[][]{
        new double[]{1.80, 2.88, 2.05, -0.89},
        new double[]{5.25, -2.95, -0.95, -3.80},
        new double[]{1.58, -2.69, -2.9, -1.4},
        new double[]{-1.11, -0.66, -0.59, 0.8}
    });

    IntMatrix ipiv1 = bj.intVector(4);
    linalg.getrf(d, ipiv1);
    assertMatrixEquals(bj.matrix(new int[]{2, 2, 3, 4}), ipiv1);
  }

  @Test
  public void testGelsy() throws Exception {

  }

  @Test
  public void testGesvd() throws Exception {

  }

  @Test
  public void testSyev() throws Exception {
    DoubleMatrix a = bj.matrix(new double[]{
        0.67, 0.00, 0.00, 0.00, 0.00,
        -0.20, 3.82, 0.00, 0.00, 0.00,
        0.19, -0.13, 3.27, 0.00, 0.00,
        -1.06, 1.06, 0.11, 5.86, 0.00,
        0.46, -0.48, 1.10, -0.98, 3.54
    }).reshape(5, 5);
    double abstol = -1;
    int il = 1;
    int ul = 3;
    double vl = 0;
    double vu = 0;
    int n = a.rows();
    DoubleMatrix w = bj.doubleVector(n);
    DoubleMatrix z = bj.doubleMatrix(n, 3);
    IntMatrix isuppz = bj.intVector(n);
    int m = linalg.syevr('v', 'i', 'u', a, vl, vu, il, ul, abstol, w, z, isuppz);
//    System.out.println(w);
    assertEquals(3, m);
    assertMatrixEquals(bj.matrix(new double[]{0.433, 2.145, 3.368}), w.slice(bj.range(3)), 0.001);
    assertMatrixEquals(bj.matrix(new double[][]{
        new double[]{3.292, 0.507, 0.876, 0.176, -0.177},
        new double[]{0, 0.891, -1.111, 0.082, 0.185},
        new double[]{0, 0, 4.561, 1.671, -0.424},
        new double[]{0, 0, 0, 4.877, 1.616},
        new double[]{0, 0, 0, 0, 3.54}}), a, 0.001);

  }

  @Test
  public void testSyevr() throws Exception {
    DoubleMatrix a = bj.matrix(new double[]{
        1.96, 0.00, 0.00, 0.00, 0.00,
        -6.49, 3.80, 0.00, 0.00, 0.00,
        -0.47, -6.39, 4.17, 0.00, 0.00,
        -7.20, 1.50, -1.51, 5.70, 0.00,
        -0.65, -6.34, 2.67, 1.80, -7.10
    }).reshape(5, 5);

//    System.out.println(a);

    DoubleMatrix w = bj.doubleVector(a.rows());

    linalg.syev('v', 'u', a, w);
//    System.out.println(w);
//    System.out.println(a);
  }

  @Test
  public void testGesv() throws Exception {
    DoubleMatrix a = bj.matrix(new double[]{
        6.80, -2.11, 5.66, 5.97, 8.23,
        -6.05, -3.30, 5.36, -4.44, 1.08,
        -0.45, 2.58, -2.70, 0.27, 9.04,
        8.32, 2.71, 4.35, -7.17, 2.14,
        -9.67, -5.14, -7.26, 6.08, -6.87
    }).reshape(5, 5);

    DoubleMatrix b = bj.matrix(new double[]{
        4.02, 6.19, -8.22, -7.57, -3.03,
        -1.56, 4.00, -8.67, 1.75, 2.86,
        9.81, -4.09, -4.57, -8.61, 8.99
    }).reshape(5, 3);

    IntMatrix ipiv = bj.intVector(5);
    linalg.gesv(a, ipiv, b);

    assertMatrixEquals(bj.matrix(new int[]{5, 5, 3, 4, 5}), ipiv);
    assertMatrixEquals(bj.matrix(new double[][]{
        new double[]{-0.80, -0.39, 0.96},
        new double[]{-0.70, -0.55, 0.22},
        new double[]{0.59, 0.84, 1.90},
        new double[]{1.32, -0.10, 5.36},
        new double[]{0.57, 0.11, 4.04}
    }), b, 0.01);
    assertMatrixEquals(bj.matrix(new double[][]{
        new double[]{8.23, 1.08, 9.04, 2.14, -6.87},
        new double[]{0.83, -6.94, -7.92, 6.55, -3.99},
        new double[]{0.69, -0.67, -14.18, 7.24, -5.19},
        new double[]{0.73, 0.75, 0.02, -13.82, 14.19},
        new double[]{-0.26, 0.44, -0.59, -0.34, -3.43}
    }), a, 0.01);
  }

  @Test
  public void testGeqrf() throws Exception {
    DoubleMatrix a = bj.matrix(new double[]{
        0.000000, 2.000000,
        2.000000, -1.000000,
        2.000000, -1.000000,
        0.000000, 1.500000,
        2.000000, -1.000000,
        2.000000, -1.000000
    }).reshape(2, 6).transpose();
    DoubleMatrix tau = bj.doubleVector(2);
    linalg.geqrf(a, tau);
    assertMatrixEquals(bj.matrix(new double[]{1, 1.4}), tau, 0.01);

    assertMatrixEquals(bj.matrix(new double[][]{
        new double[]{-4, 2},
        new double[]{0.5, 2.5},
        new double[]{0.5, 0.286},
        new double[]{0, -0.429},
        new double[]{0.5, 0.286},
        new double[]{0.5, 0.286}}), a, 0.01);
  }

  @Test
  public void testOrmqr() throws Exception {
    DoubleMatrix a = bj.matrix(new double[]{
        -0.5, -1.2, -0.3, 0.2,
        -1.9, 1.0, -0.3, -2.1,
        2.3, 0.2, 0.4, -0.3,
        -1.9, 0.6, -0.6, 0.0,
        0.1, 0.3, 0.1, -2.1,
        -0.0, 1.0, -1.4, 0.50
    }).reshape(4, 6).transpose();

    DoubleMatrix b = bj.matrix(new double[]{
        -3.1, 2.1,
        -0.1, -3.6,
        1.9, 0.5,
        -2.7, 8.2,
        0.2, -6.3,
        4.5, -1.48
    }).reshape(6, 2).transpose();


  }

  @Test
  public void testInv() throws Exception {

  }

  @Test
  public void testPinv() throws Exception {
    DoubleMatrix x = bj.matrix(new double[][]{
        new double[]{1, 2, 3},
        new double[]{1, 2, 3}
    });
    DoubleMatrix p = linalg.pinv(x.transpose());
    assertArrayEquals(
        new double[]{0.035714285714285705, 0.03571428571428572, 0.07142857142857141,
                     0.07142857142857144, 0.10714285714285711, 0.10714285714285715},
        p.data(), 1e-6);
  }

  @Test
  public void testSvd() throws Exception {
    DoubleMatrix x = bj.matrix(new double[][]{
        new double[]{1, 2, 3},
        new double[]{2, 3, 8},
        new double[]{9, 7, 1}
    });
    SingularValueDecomposition svd = linalg.svd(x);
//    System.out.println(svd);

  }
}