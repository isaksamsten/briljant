package org.briljantframework.matrix.netlib;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.MatrixPrinter;
import org.briljantframework.matrix.Transpose;
import org.briljantframework.matrix.api.MatrixBackend;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.junit.Test;

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
  }

  @Test
  public void testDot() throws Exception {
    long n = System.nanoTime();
    for (int i = 0; i < 1; i++) {
      bjr.gemm(Transpose.NO, Transpose.YES, 1, a, b, 1, c);
    }
//    c = a.mmul(Transpose.NO, b, Transpose.YES);
    System.out.println((System.nanoTime() - n) / 1e6);

    n = System.nanoTime();
    System.out.println(c);
    System.out.println((System.nanoTime() - n) / 1e6);

  }
}