package org.briljantframework.matrix.netlib;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.MatrixPrinter;
import org.briljantframework.matrix.T;
import org.briljantframework.matrix.api.MatrixBackend;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.junit.Test;

import static org.briljantframework.matrix.MatrixAssert.assertMatrixEquals;

public class NetlibMatrixRoutinesTest {

  private MatrixBackend backend = new NetlibMatrixBackend();
  private MatrixFactory bj = backend.getMatrixFactory();
  private MatrixRoutines bjr = backend.getMatrixRoutines();

  private DoubleMatrix a = bj.doubleVector(10000).assign(10);
  private DoubleMatrix b = bj.doubleVector(10000).assign(10);
  private DoubleMatrix c = bj.doubleMatrix(10000, 10000).assign(32);

  static {
    MatrixPrinter.setVisibleRows(3);
    MatrixPrinter.setVisibleColumns(3);
    MatrixPrinter.setMinimumTruncateSize(1000);
  }

  @Test
  public void testGemv() throws Exception {
    DoubleMatrix a = bj.matrix(new double[][]{
        new double[]{1, 2, 3},
        new double[]{1, 2, 3},
        new double[]{1, 2, 3}
    });

    DoubleMatrix x = bj.matrix(new double[]{1, 2, 3});
    DoubleMatrix y = bj.doubleVector(3).assign(3);

    bjr.gemv(T.YES, 1, a, x, 1, y);
    assertMatrixEquals(bj.doubleVector(3).assign(17), y, 0.0);
  }
}