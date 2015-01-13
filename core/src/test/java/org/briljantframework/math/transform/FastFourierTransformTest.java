package org.briljantframework.math.transform;

import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Ints;
import org.junit.Test;

public class FastFourierTransformTest {

  @Test
  public void testFft() throws Exception {
    ComplexMatrix mat = Ints.range(1, 8).asComplexMatrix().copy();
    System.out.println(mat);
    ComplexMatrix fft = FastFourierTransform.fft(mat);
    System.out.println(fft);
  }
}
