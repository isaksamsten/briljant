package org.briljantframework.matrix.netlib;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.matrix.Dim;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.junit.Test;

public class NetlibMatrixFactoryTest {

  private MatrixFactory bj = new NetlibMatrixFactory();
  private LinearAlgebraRoutines linalg = bj.getLinearAlgebraRoutines();
  private MatrixRoutines blas = bj.getMatrixRoutines();

  @Test
  public void testCreateNewDoubleMatrix() throws Exception {
    DoubleMatrix m = bj.matrix(new double[]{1, 2, 3, 4.0}).reshape(2, 2);
    System.out.println(linalg.gesvd(m).getLeftSingularValues());
    System.out.println(m);
    System.out.println(blas.prod(m, Dim.C));
  }
}