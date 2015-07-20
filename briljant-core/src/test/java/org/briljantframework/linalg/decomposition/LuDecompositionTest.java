package org.briljantframework.linalg.decomposition;

import org.briljantframework.array.DoubleArray;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LuDecompositionTest {

  LuDecomposition decomposition;

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testInverse() throws Exception {
    DoubleArray inverse = decomposition.inverse();

    assertEquals(inverse.get(0, 0), -0.026, 0.01);
  }

  @Test
  public void testDeterminant() throws Exception {
    System.out.println(decomposition.getDeterminant());

  }

  @Test
  public void testPivot() throws Exception {
    System.out.println(decomposition.getPivot());

  }

  @Test
  public void testIsNonSingular() throws Exception {

  }

  @Test
  public void testLower() throws Exception {
    System.out.println(decomposition.getLower());
  }

  @Test
  public void testUpper() throws Exception {
    System.out.println(decomposition.getUpper());
  }
}
