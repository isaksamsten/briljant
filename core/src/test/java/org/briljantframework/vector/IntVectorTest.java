package org.briljantframework.vector;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class IntVectorTest {

  public static final int[] INT_ARRAY = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
  private IntVector vector;
  private IntVector hasNA = new IntVector.Builder().addNA().addNA().add(1).add(2).build();


  @Before
  public void setUp() throws Exception {
    IntVector.Builder builder = new IntVector.Builder();
    for (int i = 0; i < 10; i++) {
      builder.add(i);
    }
    vector = builder.build();
  }

  @Test
  public void testAddAtIndex() throws Exception {
    IntVector.Builder builder = new IntVector.Builder();
    builder.set(3, 10);
    builder.set(10, 10);
    System.out.println(builder.size());
    System.out.println(builder.build().getAsInt(0));
  }

  @Test
  public void testEquals() throws Exception {
    IntVector a = new IntVector(1, 2, 3);
    IntVector b = new IntVector(1, 2, 3);
    assertEquals(a.asMatrix(), b.asMatrix());

    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void testAsIntArray() throws Exception {
    assertArrayEquals(INT_ARRAY, vector.asIntArray());
  }

  @Test
  public void testToIntArray() throws Exception {
    assertArrayEquals(INT_ARRAY, vector.toIntArray());
  }

  @Test
  public void testIterator() throws Exception {
    // for (Integer integer : vector) {
    // assertEquals((int) integer, INT_ARRAY[integer]);
    // }
  }

  @Test
  public void testGetAsInteger() throws Exception {
    assertEquals(2, vector.getAsInt(2));
    assertEquals(9, vector.getAsInt(9));
  }

  @Test
  public void testHasNA() throws Exception {
    assertEquals(false, vector.hasNA());
    assertEquals(true, hasNA.hasNA());
  }

  @Test
  public void testCompare() throws Exception {
    assertEquals(true, vector.compare(0, 2) < 0);
    assertEquals(true, vector.compare(2, 1) > 0);
    assertEquals(true, vector.compare(2, 2) == 0);
  }

  @Test
  public void testSize() throws Exception {
    assertEquals(10, vector.size());
    assertEquals(4, hasNA.size());
  }

  @Test
  public void testGetType() throws Exception {
    assertEquals(IntVector.TYPE, vector.getType());
  }

  @Test
  public void testNewCopyBuilder() throws Exception {
    IntVector copy = vector.newCopyBuilder().add(10).build();
    assertEquals(11, copy.size());
    assertEquals(copy.getAsInt(2), vector.getAsInt(2));
  }

  @Test
  public void testGetAsDouble() throws Exception {
    assertEquals(2.0, vector.getAsDouble(2), 0);
  }

  @Test
  public void testGetAsBinary() throws Exception {
    assertEquals(Bit.TRUE, vector.getAsBit(1));
    assertEquals(Bit.FALSE, vector.getAsBit(0));
    assertEquals(Bit.NA, hasNA.getAsBit(0));
  }

  @Test
  public void testGetAsString() throws Exception {
    assertEquals("9", vector.getAsString(9));
  }

  @Test
  public void testIsTrue() throws Exception {
    assertEquals(true, vector.isTrue(1));
    assertEquals(false, vector.isTrue(0));
  }

  @Test
  public void testIsNA() throws Exception {
    assertEquals(true, hasNA.isNA(0));
    assertEquals(false, hasNA.isNA(2));
  }

  @Test
  public void testNewBuilder() throws Exception {
    IntVector.Builder builder = vector.newBuilder();

    builder.add(hasNA, 0);
    builder.add(vector, 0);
    builder.add(vector, 9);

    assertArrayEquals(new int[]{IntVector.NA, 0, 9}, builder.build().asIntArray());

  }

  @Test
  public void testNewBuilder1() throws Exception {

  }
}
