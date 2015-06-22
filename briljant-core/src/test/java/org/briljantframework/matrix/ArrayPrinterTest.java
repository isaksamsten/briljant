package org.briljantframework.matrix;

import org.briljantframework.Bj;
import org.briljantframework.matrix.api.ArrayFactory;
import org.briljantframework.matrix.netlib.NetlibArrayBackend;
import org.junit.Test;

public class ArrayPrinterTest {

  private final ArrayFactory bj = new NetlibArrayBackend().getArrayFactory();

  @Test
  public void testPrint() throws Exception {
    ArrayPrinter.print(Bj.linspace(-1, 1, 1000).reshape(10, 100).asComplexMatrix());
  }

  @Test
  public void testSmall() throws Exception {
    ArrayPrinter.setMinimumTruncateSize(5);
    ArrayPrinter.setVisiblePerSlice(3);
    ArrayPrinter.setPrintSlices(11);
    DoubleArray reshape = Bj.linspace(0, 1, 35).reshape(7, 5);
    System.out.println(reshape);
    System.out.println(reshape.getDiagonal());
  }

  @Test
  public void testBroken() throws Exception {
    DoubleArray b = bj.doubleArray(10).assign(0);
    System.out.println(b);

  }
}