package org.briljantframework.distance;

import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Matrix;
import org.junit.Test;

public class EuclideanDistanceTest {

  @Test
  public void testBasicDistance() throws Exception {
    Distance e = Distance.EUCLIDEAN;
    Matrix a = ArrayMatrix.of(1, 4, 1, 2, 3, 4);
    Matrix b = ArrayMatrix.of(1, 4, 2, 3, 3, 4);
    System.out.println(a);
    System.out.println(b);

    System.out.println(e.distance(a, b));
    System.out.println(Distance.MANHATTAN.distance(a, b));
  }
}
