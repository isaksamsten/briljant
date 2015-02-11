package org.briljantframework.distance;

import org.briljantframework.vector.DoubleVector;
import org.junit.Test;

public class EuclideanTest {

  @Test
  public void testBasicDistance() throws Exception {
    Distance e = Euclidean.getInstance();
    DoubleVector a = DoubleVector.wrap(1, 4, 1, 2, 3, 4);
    DoubleVector b = DoubleVector.wrap(1, 4, 2, 3, 3, 4);
    System.out.println(a);
    System.out.println(b);

    System.out.println(e.compute(a, b));
    System.out.println(Manhattan.getInstance().compute(a, b));
  }
}
