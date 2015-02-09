package org.briljantframework.matrix;

import static org.briljantframework.matrix.Matrices.newBitVector;
import static org.briljantframework.matrix.MatrixAssert.assertValuesEquals;

import org.junit.Test;

public class AbstractBitMatrixTest {

  @Test
  public void testGetRowView() throws Exception {
    BitMatrix m =
        newBitVector(true, false, false, true, false, true, true, false, false).reshape(3, 3);
    assertValuesEquals(newBitVector(true, true, true), m.getRowView(0));
    assertValuesEquals(newBitVector(false, false, false), m.getRowView(1));
  }

  @Test
  public void testGetColumnView() throws Exception {
    BitMatrix m =
        newBitVector(true, false, false, true, false, true, true, false, false).reshape(3, 3);
    assertValuesEquals(newBitVector(true, false, false), m.getColumnView(0));
    assertValuesEquals(newBitVector(true, false, true), m.getColumnView(1));
  }

  @Test
  public void testGetView() throws Exception {
    BitMatrix m =
        newBitVector(true, false, false, true, false, true, true, false, false).reshape(3, 3);
    BitMatrix view = m.getView(0, 0, 2, 2);
    assertValuesEquals(newBitVector(true, false), view.getColumnView(0));
    assertValuesEquals(newBitVector(true, false), view.getColumnView(1));
  }

  @Test
  public void testSlice() throws Exception {

  }

  @Test
  public void testSlice1() throws Exception {

  }

  @Test
  public void testSlice2() throws Exception {

  }

  @Test
  public void testSlice3() throws Exception {

  }

  @Test
  public void testSlice4() throws Exception {

  }

  @Test
  public void testSlice5() throws Exception {

  }

  @Test
  public void testSlice6() throws Exception {

  }

  @Test
  public void testSlice7() throws Exception {

  }

  @Test
  public void testStream() throws Exception {

  }

  @Test
  public void testSetRow() throws Exception {

  }

  @Test
  public void testSetColumn() throws Exception {

  }

  @Test
  public void testTranspose() throws Exception {

  }

  @Test
  public void testCopy() throws Exception {

  }

  @Test
  public void testHashCode() throws Exception {

  }

  @Test
  public void testEquals() throws Exception {

  }

  @Test
  public void testToString() throws Exception {

  }

  @Test
  public void testIterator() throws Exception {

  }

  @Test
  public void testSwap() throws Exception {

  }

  @Test
  public void testXor() throws Exception {

  }

  @Test
  public void testOr() throws Exception {

  }

  @Test
  public void testOrNot() throws Exception {

  }

  @Test
  public void testAnd() throws Exception {

  }

  @Test
  public void testAndNot() throws Exception {

  }

  @Test
  public void testNot() throws Exception {

  }

  @Test
  public void testNewEmptyVector() throws Exception {

  }
}
