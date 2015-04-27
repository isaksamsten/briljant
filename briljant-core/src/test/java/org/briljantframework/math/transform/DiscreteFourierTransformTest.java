package org.briljantframework.math.transform;

import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.netlib.NetlibMatrixFactory;
import org.junit.Test;

public class DiscreteFourierTransformTest {

  @Test
  public void testFft() throws Exception {
    MatrixFactory bj = NetlibMatrixFactory.getInstance();
    ComplexMatrix mat = bj.range(1, 9).asComplexMatrix();
    System.out.println(mat);
    long n = System.nanoTime();
    ComplexMatrix fft = DiscreteFourierTransform.fft(mat);
    System.out.println((System.nanoTime() - n) / 1e6);
    System.out.println(DiscreteFourierTransform.ifft(fft));
  }
}
