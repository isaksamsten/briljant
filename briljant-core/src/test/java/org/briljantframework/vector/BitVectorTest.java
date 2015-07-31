package org.briljantframework.vector;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BitVectorTest {

  private BitVector vector;
  private int[] trueArray = new int[]{0, 1, IntVector.NA, 0};

  @Before
  public void setUp() throws Exception {
    vector = new BitVector.Builder().add(0).add(1).add(Bit.NA).add(Bit.FALSE).build();
  }

  @Test
  public void testGetAsDouble() throws Exception {
    assertEquals(0, vector.getAsDouble(0), 0);
    assertEquals(1, vector.getAsDouble(1), 0);
    assertEquals(DoubleVector.NA, vector.getAsDouble(2), 0);
  }

  @Test
  public void testGetAsInt() throws Exception {
    assertEquals(0, vector.getAsInt(0));
    assertEquals(1, vector.getAsInt(1));
    assertEquals(IntVector.NA, vector.getAsInt(2));
  }

  @Test
  public void testGetAsBinary() throws Exception {
    assertEquals(Bit.FALSE, vector.getAsBit(0));
    assertEquals(Bit.TRUE, vector.getAsBit(1));
    assertEquals(BitVector.NA, vector.getAsBit(2));
  }

  @Test
  public void testIsNA() throws Exception {
    assertEquals(true, vector.isNA(2));
    assertEquals(false, vector.isNA(0));
  }

  @Test
  public void testCompare() throws Exception {
    assertEquals(true, vector.compare(0, 1) < 0);
    assertEquals(true, vector.compare(1, 0) > 0);
    assertEquals(true, vector.compare(0, 3) == 0);
  }

  @Test
  public void testSize() throws Exception {
    assertEquals(4, vector.size());
    assertEquals(5, vector.newCopyBuilder().add(1).build().size());
  }

  @Test
  public void testGetType() throws Exception {
    assertEquals(BitVector.TYPE, vector.getType());
    assertEquals(Bit.class, vector.getType().getDataClass());
  }

  @Test
  public void testNewCopyBuilder() throws Exception {

  }

  @Test
  public void testNewBuilder() throws Exception {

  }

  @Test
  public void testNewBuilder1() throws Exception {

  }

  @Test
  public void testToIntArray() throws Exception {
    assertArrayEquals(trueArray, vector.toIntArray());
  }

}
