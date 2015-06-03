package org.briljantframework.matrix;

import org.briljantframework.Bj;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.netlib.NetlibMatrixBackend;
import org.junit.Test;

public class MatrixPrinterTest {

  private final MatrixFactory bj = new NetlibMatrixBackend().getMatrixFactory();

  @Test
  public void testPrint() throws Exception {
    MatrixPrinter.print(Bj.linspace(-1, 1, 1000).reshape(10, 100).asComplexMatrix());
  }

  @Test
  public void testSmall() throws Exception {
    MatrixPrinter.setMinimumTruncateSize(5);
    MatrixPrinter.setVisibleColumns(3);
    MatrixPrinter.setVisibleRows(11);
    DoubleMatrix reshape = Bj.linspace(0, 1, 35).reshape(7, 5);
    System.out.println(reshape);
    System.out.println(reshape.getDiagonal());
  }

  @Test
  public void testBroken() throws Exception {
    DoubleMatrix b = bj.doubleVector(10).assign(0);
    System.out.println(b);

  }
}