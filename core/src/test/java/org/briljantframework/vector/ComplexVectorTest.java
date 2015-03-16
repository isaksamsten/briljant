package org.briljantframework.vector;

import org.briljantframework.complex.Complex;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ComplexVectorTest {

  private ComplexVector vector;

  @Before
  public void setUp() throws Exception {
    vector = new ComplexVector.Builder().add(1).add(2).add(Complex.NaN).add(Complex.ONE).build();
  }

  @Test
  public void testBuilderAdd() throws Exception {
    Vector.Builder builder = new ComplexVector.Builder();
    builder.addAll("1+10i", "1+11i", "3-10i");
    System.out.println(builder.build());

  }

  @Test
  public void testAddAtIndex() throws Exception {
    ComplexVector.Builder builder = new ComplexVector.Builder();
    builder.set(3, 10);
    builder.set(10, new Complex(10, 2));
    System.out.println(builder.size());

    System.out.println(builder.build());
  }

  @Test
  public void testGetAsDouble() throws Exception {
    assertEquals(1, vector.getAsDouble(0), 0);
    assertEquals(2, vector.getAsDouble(1), 0);
    assertEquals(DoubleVector.NA, vector.getAsDouble(2), 0);
  }

  @Test
  public void testGetAsInt() throws Exception {
    assertEquals(1, vector.getAsInt(0));
    assertEquals(2, vector.getAsInt(1));
    assertEquals(IntVector.NA, vector.getAsInt(2));
  }

  @Test
  public void testGetAsBinary() throws Exception {
    assertEquals(Bit.TRUE, vector.getAsBit(0));
    assertEquals(Bit.NA, vector.getAsBit(1));
    assertEquals(BitVector.NA, vector.getAsBit(2));
  }

  @Test
  public void testGetAsString() throws Exception {
    assertEquals("1.0000 + 0.0000i", vector.getAsString(0));
    assertEquals("2.0000 + 0.0000i", vector.getAsString(1));
    assertEquals(StringVector.NA, vector.getAsString(2));
  }

  @Test
  public void testGetAsComplex() throws Exception {

  }

  @Test
  public void testIsNA() throws Exception {

  }

  @Test
  public void testCompare() throws Exception {

  }

  @Test
  public void testSize() throws Exception {

  }

  @Test
  public void testGetType() throws Exception {

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
  public void testIterator() throws Exception {

  }

  @Test
  public void testToDoubleArray() throws Exception {

  }

  @Test
  public void testAsDoubleArray() throws Exception {

  }
}
