package org.briljantframework.math.transform;

import org.briljantframework.Bj;
import org.briljantframework.array.ComplexArray;
import org.junit.Test;

public class DiscreteFourierTransformTest {

  @Test
  public void testFft() throws Exception {
    ComplexArray mat = Bj.range(1, 9).asComplex();
    System.out.println(mat);
    long n = System.nanoTime();
    ComplexArray fft = DiscreteFourierTransform.fft(mat);
    System.out.println((System.nanoTime() - n) / 1e6);
    System.out.println(DiscreteFourierTransform.ifft(fft));
  }
}
