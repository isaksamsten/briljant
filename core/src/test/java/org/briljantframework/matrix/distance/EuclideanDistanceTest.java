package org.briljantframework.matrix.distance;

import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrix;
import org.junit.Test;

public class EuclideanDistanceTest {

  @Test
  public void testBasicDistance() throws Exception {
    Distance e = Distance.EUCLIDEAN;
    Matrix a = DenseMatrix.of(1, 4, 1, 2, 3, 4);
    Matrix b = DenseMatrix.of(1, 4, 2, 3, 3, 4);
    System.out.println(a);
    System.out.println(b);

    System.out.println(e.distance(a, b));
    System.out.println(Distance.MANHATTAN.distance(a, b));
  }
}
