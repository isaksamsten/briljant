package org.briljantframework.matrix;

import org.junit.Assert;
import org.junit.Test;

public class MatricesTest {

  @Test
  public void testArgMax() throws Exception {
    DoubleMatrix v = Matrices.newDoubleVector(1, 2, 3, 9, 5, 1, 2);
    Assert.assertEquals(3, Matrices.argMax(v));
  }
}
