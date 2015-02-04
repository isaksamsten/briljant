package org.briljantframework.matrix;

import org.junit.Test;

public class AnysTest {

  @Test
  public void testTake() throws Exception {
    DoubleMatrix a = Doubles.newMatrix(1, 2, 3, 4, 5, 6);

    // Matrix taken = Matrices.take(a, Matrices.newIntMatrix(0, 1, 2, 2));
    // assertTrue(taken instanceof DoubleMatrix);
    // assertEquals(Doubles.newMatrix(1, 2, 3, 3), taken);
    //
    // taken =
    // Matrices.take(Matrices.newIntMatrix(1, 2, 3, 4, 5, 6), Matrices.newIntMatrix(0, 1, 2, 2));
    // assertTrue(taken instanceof IntMatrix);
    // assertEquals(Matrices.newIntMatrix(1, 2, 3, 3), taken);
    // assertEquals(Doubles.newMatrix(1, 2, 3, 3), taken.asDoubleMatrix());
    //
    // taken =
    // Matrices.take(Matrices.newBitMatrix(true, true, true, false),
    // Matrices.newIntMatrix(0, 0, 3, 3, 3));
    // assertTrue(taken instanceof BitMatrix);
    // assertEquals(Matrices.newBitMatrix(true, true, false, false, false), taken.asBitMatrix());
    // assertEquals(Matrices.newIntMatrix(1, 1, 0, 0, 0), taken.asIntMatrix());
  }

  @Test
  public void testMask() throws Exception {
    // IntMatrix x = Matrices.range(0, 6).reshape(2, 3);
    // System.out.println(Matrices.mask(x, x.gt(2), x.mul(2)).asDoubleMatrix());

  }
}
