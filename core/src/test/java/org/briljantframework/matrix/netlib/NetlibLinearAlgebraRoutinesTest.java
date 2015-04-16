package org.briljantframework.matrix.netlib;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.MatrixAssert;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.junit.Test;

public class NetlibLinearAlgebraRoutinesTest {

  NetlibMatrixFactory bj = new NetlibMatrixFactory();


  @Test
  public void testGetrf() throws Exception {
    DoubleMatrix d = bj.matrix(new double[][]{
        new double[]{1.80, 2.88, 2.05, -0.89},
        new double[]{5.25, -2.95, -0.95, -3.80},
        new double[]{1.58, -2.69, -2.9, -1.4},
        new double[]{-1.11, -0.66, -0.59, 0.8}
    });

    IntMatrix ipiv1 = bj.intVector(4);
    bj.getLinearAlgebraRoutines().getrf(d, ipiv1);
    MatrixAssert.assertMatrixEquals(bj.matrix(new int[]{2, 2, 3, 4}), ipiv1);
  }

  @Test
  public void testGelsy() throws Exception {

  }

  @Test
  public void testGesvd() throws Exception {

  }

  @Test
  public void testSyev() throws Exception {
    LinearAlgebraRoutines linalg = bj.getLinearAlgebraRoutines();
//    MatrixRoutines bjr = bj.getMatrixRoutines();
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
    System.out.println(m);
    System.out.println(w.slice(bj.range(m)));
    System.out.println(w.transpose().slice(bj.range(m)));

    System.out.println(z);
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

    System.out.println(a);

    DoubleMatrix w = bj.doubleVector(a.rows());

    bj.getLinearAlgebraRoutines().syev('v', 'u', a, w);
    System.out.println(w);
    System.out.println(a);
  }
}