package org.briljantframework.vector;

import org.junit.Test;

import static org.junit.Assert.*;

public class IsTest {

  @Test
  public void testNA() throws Exception {
    Double v = DoubleVector.NA;
    Double o = Double.NaN;

    assertTrue(Is.NA(v));
    assertFalse(Is.NA(o));
  }
}