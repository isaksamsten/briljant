package org.briljantframework.matrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AnysTest {

  @Test
  public void testTake() throws Exception {
    DoubleMatrix a = Doubles.newMatrix(1, 2, 3, 4, 5, 6);

    AnyMatrix taken = Anys.take(a, Ints.newMatrix(0, 1, 2, 2));
    assertTrue(taken instanceof DoubleMatrix);
    assertEquals(Doubles.newMatrix(1, 2, 3, 3), taken);

    taken = Anys.take(Ints.newMatrix(1, 2, 3, 4, 5, 6), Ints.newMatrix(0, 1, 2, 2));
    assertTrue(taken instanceof IntMatrix);
    assertEquals(Ints.newMatrix(1, 2, 3, 3), taken);
    assertEquals(Doubles.newMatrix(1, 2, 3, 3), taken.asDoubleMatrix());

    taken = Anys.take(Bits.newMatrix(true, true, true, false), Ints.newMatrix(0, 0, 3, 3, 3));
    assertTrue(taken instanceof BitMatrix);
    assertEquals(Bits.newMatrix(true, true, false, false, false), taken.asBitMatrix());
    assertEquals(Ints.newMatrix(1, 1, 0, 0, 0), taken.asIntMatrix());
  }

  @Test
  public void testMask() throws Exception {
    IntMatrix x = Ints.range(0, 6).reshape(2, 3);
    System.out.println(Anys.mask(x, x.greaterThan(2), x.mul(2)).asDoubleMatrix());

  }
}
