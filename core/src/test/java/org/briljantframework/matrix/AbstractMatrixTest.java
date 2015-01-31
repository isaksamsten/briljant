package org.briljantframework.matrix;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AbstractMatrixTest {

  @Test
  public void testAsReturnThisOnCorrectType() throws Exception {
    IntMatrix a = Matrices.range(0, 10);
    assertTrue(a == a.asDoubleMatrix().asComplexMatrix().asIntMatrix());

    DoubleMatrix b = Doubles.linspace(0, 2, 10);
    assertTrue(b == b.asIntMatrix().asDoubleMatrix());
  }

  @Test
  public void testAsDoubleMatrix() throws Exception {
    IntMatrix a = Matrices.range(0, 10);

    IntMatrix x = a.asDoubleMatrix().asIntMatrix();
    assertTrue(a == x);
  }

  @Test
  public void testAsIntMatrix() throws Exception {

  }

  @Test
  public void testAsBitMatrix() throws Exception {

  }

  @Test
  public void testAsComplexMatrix() throws Exception {

  }
}
