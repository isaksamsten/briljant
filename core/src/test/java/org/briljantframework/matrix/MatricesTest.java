package org.briljantframework.matrix;

import static org.briljantframework.matrix.Matrices.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.briljantframework.complex.Complex;
import org.junit.Test;

public class MatricesTest {

  @Test
  public void testNewBitMatrix() throws Exception {
    assertEquals(3, newBitMatrix(true, true, true).size());
    assertEquals(false, newBitMatrix(true, false, true).get(1));
  }

  @Test
  public void testNewBitVector() throws Exception {
    assertEquals(3, newBitVector(3).size());
  }

  @Test
  public void testNewBitMatrix1() throws Exception {
    assertEquals(3, newBitMatrix(3, 2).rows());
    assertEquals(2, newBitMatrix(3, 2).columns());
  }

  @Test
  public void testNewDoubleMatrix() throws Exception {
    assertEquals(3, newDoubleMatrix(1, 2, 3).size());
    assertEquals(3, newDoubleMatrix(1, 2, 3).get(2), 0);
  }

  @Test
  public void testNewDoubleVector() throws Exception {
    assertEquals(3, newDoubleVector(3).size());
  }

  @Test
  public void testNewDoubleMatrix1() throws Exception {
    assertEquals(3, newDoubleMatrix(3, 2).rows());
    assertEquals(2, newDoubleMatrix(3, 2).columns());
  }

  @Test
  public void testNewIntVector() throws Exception {

  }

  @Test
  public void testNewIntMatrix() throws Exception {

  }

  @Test
  public void testNewLongMatrix() throws Exception {

  }

  @Test
  public void testNewLongVector() throws Exception {

  }

  @Test
  public void testZeros() throws Exception {

  }

  @Test
  public void testZeros1() throws Exception {

  }

  @Test
  public void testZeros2() throws Exception {

  }

  @Test
  public void testZeros3() throws Exception {

  }

  @Test
  public void testOnes() throws Exception {


  }

  @Test
  public void testOnes1() throws Exception {

  }

  @Test
  public void testOnes2() throws Exception {
    assertTrue(zeros(3, Integer.class) instanceof IntMatrix);
    assertTrue(zeros(3, Double.class) instanceof DoubleMatrix);
    assertTrue(zeros(3, Long.class) instanceof LongMatrix);
    assertTrue(zeros(3, Complex.class) instanceof ComplexMatrix);
    assertTrue(zeros(3, Boolean.class) instanceof BitMatrix);
  }

  @Test
  public void testFilledWith() throws Exception {
    Matrix a = filledWith(3, Integer.class, 10);
    assertTrue(a instanceof IntMatrix);
    assertEquals(10, a.getAsInt(0));
  }

  @Test
  public void testFilledWith1() throws Exception {

  }

  @Test
  public void testFilledWith2() throws Exception {

  }

  @Test
  public void testFilledWith3() throws Exception {

  }

  @Test
  public void testRandn() throws Exception {

  }

  @Test
  public void testRandn1() throws Exception {

  }

  @Test
  public void testRand() throws Exception {

  }

  @Test
  public void testRand1() throws Exception {

  }

  @Test
  public void testEye() throws Exception {

  }
}
