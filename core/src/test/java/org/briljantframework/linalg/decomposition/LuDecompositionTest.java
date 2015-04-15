package org.briljantframework.linalg.decomposition;

import org.briljantframework.matrix.DoubleMatrix;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class LuDecompositionTest {

  LuDecomposition decomposition;

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testInverse() throws Exception {
    DoubleMatrix inverse = decomposition.inverse();

    assertEquals(inverse.get(0, 0), -0.026, 0.01);
  }

  @Test
  public void testDeterminant() throws Exception {
    System.out.println(decomposition.getDeterminant());

  }

  @Test
  public void testPivot() throws Exception {
    System.out.println(Arrays.toString(decomposition.getPivot()));

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
