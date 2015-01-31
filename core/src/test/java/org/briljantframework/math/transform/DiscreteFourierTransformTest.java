package org.briljantframework.math.transform;

import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Matrices;
import org.junit.Test;

public class DiscreteFourierTransformTest {

  @Test
  public void testFft() throws Exception {
    ComplexMatrix mat = Matrices.range(1, 9).asComplexMatrix();
    long n = System.nanoTime();
    ComplexMatrix fft = DiscreteFourierTransform.fft(mat);
    System.out.println((System.nanoTime() - n) / 1e6);
    System.out.println(DiscreteFourierTransform.ifft(fft));
    // System.out.println(fft);
  }
}
