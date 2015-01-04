package org.briljantframework.vector;

import static org.junit.Assert.assertTrue;

import org.briljantframework.complex.Complex;
import org.junit.Test;

public class ComplexTest {

  private final Complex nanReal = new Complex(Double.NaN, 1);
  private final Complex nanImag = new Complex(1, Double.NaN);
  private final Complex oneInf = new Complex(1, Double.POSITIVE_INFINITY);
  private final Complex infOne = new Complex(Double.POSITIVE_INFINITY, 1);

  @Test
  public void testConstruct() throws Exception {
    assertTrue(nanReal.isNaN());
    assertTrue(nanImag.isNaN());
    assertTrue(oneInf.isInfinite());
    assertTrue(infOne.isInfinite());
  }

  @Test
  public void testPlus() throws Exception {

  }

  @Test
  public void testMinus() throws Exception {

  }

  @Test
  public void testMultiply() throws Exception {

  }

  @Test
  public void testDivide() throws Exception {

  }

  @Test
  public void testIsNaN() throws Exception {

  }

  @Test
  public void testIsInfinite() throws Exception {

  }
}
