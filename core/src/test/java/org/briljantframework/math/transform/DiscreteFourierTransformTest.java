package org.briljantframework.math.transform;

import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Ints;
import org.junit.Test;

public class DiscreteFourierTransformTest {

  @Test
  public void testFft() throws Exception {
    ComplexMatrix mat = Ints.range(1, 10000).asComplexMatrix();
    long n = System.nanoTime();
    ComplexMatrix fft = DiscreteFourierTransform.fft(mat);
    System.out.println((System.nanoTime() - n) / 1e6);
    System.out.println(DiscreteFourierTransform.ifft(fft));
    // System.out.println(fft);
  }
}
