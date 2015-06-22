package org.briljantframework.matrix.netlib;

import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.matrix.ArrayPrinter;
import org.briljantframework.matrix.Op;
import org.briljantframework.matrix.api.ArrayBackend;
import org.briljantframework.matrix.api.ArrayFactory;
import org.briljantframework.matrix.api.ArrayRoutines;
import org.junit.Test;

import static org.briljantframework.matrix.MatrixAssert.assertMatrixEquals;

public class NetlibArrayRoutinesTest {

  private ArrayBackend backend = new NetlibArrayBackend();
  private ArrayFactory bj = backend.getArrayFactory();
  private ArrayRoutines bjr = backend.getArrayRoutines();

  private DoubleArray a = bj.doubleArray(10000).assign(10);
  private DoubleArray b = bj.doubleArray(10000).assign(10);
  private DoubleArray c = bj.doubleArray(10000, 10000).assign(32);

  static {
    ArrayPrinter.setPrintSlices(3);
    ArrayPrinter.setVisiblePerSlice(3);
    ArrayPrinter.setMinimumTruncateSize(1000);
  }

  @Test
  public void testGemv() throws Exception {
    DoubleArray a = bj.array(new double[][]{
        new double[]{1, 2, 3},
        new double[]{1, 2, 3},
        new double[]{1, 2, 3}
    });

    DoubleArray x = bj.array(new double[]{1, 2, 3});
    DoubleArray y = bj.doubleArray(3).assign(3);

    bjr.gemv(Op.TRANSPOSE, 1, a, x, 1, y);
    assertMatrixEquals(bj.doubleArray(3).assign(17), y, 0.0);
  }
}