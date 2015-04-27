package org.briljantframework.matrix;

import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.netlib.NetlibMatrixFactory;
import org.junit.Before;
import org.junit.Test;

import static org.briljantframework.matrix.MatrixAssert.assertValuesEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractBitMatrixTest {

  BitMatrix a, b;
  private final MatrixFactory bj = NetlibMatrixFactory.getInstance();

  @Test
  public void testGetRowView() throws Exception {
    BitMatrix m =
        bj.matrix(new boolean[]{true, false, false, true, false, true, true, false, false})
            .reshape(3, 3);
    assertValuesEquals(bj.matrix(new boolean[]{true, true, true}), m.getRowView(0));
    assertValuesEquals(bj.matrix(new boolean[]{false, false, false}), m.getRowView(1));
  }

  @Test
  public void testGetColumnView() throws Exception {
    BitMatrix m =
        bj.matrix(new boolean[]{true, false, false, true, false, true, true, false, false})
            .reshape(3, 3);
    assertValuesEquals(bj.matrix(new boolean[]{true, false, false}), m.getColumnView(0));
    assertValuesEquals(bj.matrix(new boolean[]{true, false, true}), m.getColumnView(1));
  }

  @Test
  public void testGetView() throws Exception {
    BitMatrix m =
        bj.matrix(new boolean[]{true, false, false, true, false, true, true, false, false})
            .reshape(3, 3);
    BitMatrix view = m.getView(0, 0, 2, 2);
    assertValuesEquals(bj.matrix(new boolean[]{true, false}), view.getColumnView(0));
    assertValuesEquals(bj.matrix(new boolean[]{true, false}), view.getColumnView(1));
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

  @Before
  public void setUp() throws Exception {
    a = bj.matrix(new boolean[]{true, true, true, false, false, true}).reshape(3, 2);
    b = bj.matrix(new boolean[]{true, false, true, false, true, true}).reshape(3, 2);
  }


  @Test
  public void testCopy() throws Exception {
    BitMatrix copy = a.copy();
    for (int i = 0; i < a.size(); i++) {
      assertEquals(a.get(i), copy.get(i));
    }
  }

  @Test
  public void testHas() throws Exception {
    assertTrue(a.get(1));
    assertTrue(a.get(0));
    assertTrue(a.get(2));
    assertTrue(a.get(1, 0));
    assertFalse(a.get(1, 1));
  }

  @Test
  public void testAnd() throws Exception {
    BitMatrix and = a.and(b);
    boolean[] actual = new boolean[]{true, false, true, false, false, true};
    for (int i = 0; i < and.size(); i++) {
      assertEquals(actual[i], and.get(i));
    }
  }

  @Test
  public void testOr() throws Exception {
    boolean[] actual = new boolean[]{true, true, true, false, true, true};
    BitMatrix or = a.or(b);
    for (int i = 0; i < or.size(); i++) {
      assertEquals(actual[i], or.get(i));
    }
  }

  @Test
  public void testXor() throws Exception {
    boolean[] actual = new boolean[]{false, true, false, false, true, false};
    BitMatrix xor = a.xor(b);
    for (int i = 0; i < xor.size(); i++) {
      assertEquals(actual[i], xor.get(i));
    }
  }

  @Test
  public void testOrNot() throws Exception {
    boolean[] actual = new boolean[]{true, true, true, true, false, true};
    BitMatrix orNot = a.orNot(b);
    for (int i = 0; i < orNot.size(); i++) {
      assertEquals(actual[i], orNot.get(i));
    }
  }

  @Test
  public void testAndNot() throws Exception {
    boolean[] actual = new boolean[]{false, true, false, false, false, false};
    BitMatrix andNot = a.andNot(b);
    for (int i = 0; i < andNot.size(); i++) {
      assertEquals(actual[i], andNot.get(i));
    }
  }

  @Test
  public void testNot() throws Exception {
    boolean[] actual = new boolean[]{false, false, false, true, true, false};
    BitMatrix not = a.not();
    for (int i = 0; i < a.size(); i++) {
      assertEquals(actual[i], not.get(i));
    }
  }

  @Test
  public void testSet() throws Exception {
    a.set(0, true);
    assertTrue(a.get(0));
    a.set(0, false);
    assertFalse(a.get(0));
  }

  @Test
  public void testSet1() throws Exception {
    a.set(0, 0, true);
    assertTrue(a.get(0, 0));
    a.set(2, 1, false);
    assertFalse(a.get(2, 1));
  }

  @Test
  public void testTranspose() throws Exception {
    BitMatrix aTranspose = a.transpose();
    assertEquals(2, aTranspose.rows());
    assertEquals(3, aTranspose.columns());
  }

  @Test
  public void testReshape() throws Exception {
    BitMatrix reshape = a.reshape(6, 1);
    assertEquals(6, reshape.rows());
    assertEquals(1, reshape.columns());
    assertEquals(a.get(0), reshape.get(0));

    // Mutation
    reshape.set(0, false);
    assertEquals(false, a.get(0));
  }

  @Test
  public void testNewEmptyMatrix() throws Exception {
    BitMatrix newMatrix = a.newEmptyMatrix(2, 20);
    assertEquals(2, newMatrix.rows());
    assertEquals(20, newMatrix.columns());
    assertEquals(false, newMatrix.get(12));
  }
}