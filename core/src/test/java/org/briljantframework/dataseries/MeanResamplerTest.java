package org.briljantframework.dataseries;

import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class MeanResamplerTest {

  @Test
  public void testResample() throws Exception {
    Resampler r = new LinearResampler(5);
    Vector vector = DoubleVector.wrap(1, 3, 5, 7, 8, 9, 15, 17, 18);

    System.out.println(r.mutableTransform(vector));
  }
}
