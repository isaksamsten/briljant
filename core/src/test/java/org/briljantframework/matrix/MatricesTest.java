package org.briljantframework.matrix;

import org.junit.Assert;
import org.junit.Test;

public class MatricesTest {

  @Test
  public void testArgMax() throws Exception {
    DoubleMatrix v = DoubleMatrix.of(1, 2, 3, 9, 5, 1, 2);
    Assert.assertEquals(3, Matrices.argmax(v));

    System.out.println(Matrices.sort(v, (m, i, j) -> -Double.compare(m.get(i), m.get(j))));
  }
}
