package org.briljantframework.math.transform;

import org.briljantframework.Bj;
import org.briljantframework.matrix.ComplexMatrix;
import org.junit.Test;

public class DiscreteFourierTransformTest {

  @Test
  public void testFft() throws Exception {
    ComplexMatrix mat = Bj.range(1, 9).asComplexMatrix();
    System.out.println(mat);
    long n = System.nanoTime();
    ComplexMatrix fft = DiscreteFourierTransform.fft(mat);
    System.out.println((System.nanoTime() - n) / 1e6);
    System.out.println(DiscreteFourierTransform.ifft(fft));
  }
}
