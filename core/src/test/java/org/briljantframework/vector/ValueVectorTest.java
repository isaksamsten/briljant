package org.briljantframework.vector;

import org.briljantframework.complex.Complex;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValueVectorTest {

  ValueVector sequence;

  @Before
  public void setUp() throws Exception {
    sequence = new ValueVector.Builder().add(1).add(2).add("hello").add("next").addNA().build();
  }

  @Test
  public void testGetAsDouble() throws Exception {
    assertEquals(1, sequence.getAsDouble(0), 0);
    assertEquals(2, sequence.getAsDouble(1), 0);
    assertEquals(DoubleVector.NA, sequence.getAsDouble(3), 0);
  }

  @Test
  public void testGetAsInt() throws Exception {
    assertEquals(1, sequence.getAsInt(0));
    assertEquals(2, sequence.getAsInt(1));
    assertEquals(IntVector.NA, sequence.getAsInt(3));
  }

  @Test
  public void testGetAsBinary() throws Exception {
    assertEquals(Bit.TRUE, sequence.getAsBit(0));
    assertEquals(Bit.NA, sequence.getAsBit(1));
    assertEquals(BitVector.NA, sequence.getAsBit(3));
  }

  @Test
  public void testGetAsString() throws Exception {
    assertEquals("1", sequence.getAsString(0));
    assertEquals("2", sequence.getAsString(1));
    assertEquals("hello", sequence.getAsString(2));
    assertEquals(StringVector.NA, sequence.getAsString(4));
  }

  @Test
  public void testGetAsComplex() throws Exception {
    assertEquals(new Complex(1, 0), sequence.getAsComplex(0));
    assertEquals(new Complex(2, 0), sequence.getAsComplex(1));
    assertEquals(ComplexVector.NA, sequence.getAsComplex(2));
    assertEquals(ComplexVector.NA, sequence.getAsComplex(3));
  }

  @Test
  public void testToString() throws Exception {
    assertEquals("1", sequence.toString(0));
    assertEquals("2", sequence.toString(1));
    assertEquals("hello", sequence.toString(2));
    assertEquals("NA", sequence.toString(4));
  }

  @Test
  public void testIsNA() throws Exception {
    assertEquals(true, sequence.isNA(4));

  }

  @Test
  public void testSize() throws Exception {
    assertEquals(5, sequence.size());
  }

  @Test
  public void testNewCopyBuilder() throws Exception {
    ValueVector.Builder builder = sequence.newCopyBuilder();
    builder.add("hello");
    builder.add(null);

    ValueVector copy = builder.build();
    assertEquals(7, copy.size());
    assertEquals("hello", copy.getAsString(5));
    assertEquals(VariableVector.NA, copy.get(6));

  }

  @Test
  public void testNewBuilder() throws Exception {

  }

  @Test
  public void testNewBuilder1() throws Exception {

  }

  public void testCompare() throws Exception {
    sequence.compare(0, 1);
  }

  @Test
  public void testGetAsObject() throws Exception {
    assertEquals("1", sequence.get(0).getAsString());
    assertEquals("2", sequence.get(1).getAsString());
    assertEquals("hello", sequence.get(2).getAsString());
    assertEquals(VariableVector.NA, sequence.get(4));
  }

  @Test
  public void testGetType() throws Exception {
    assertEquals(VariableVector.TYPE, sequence.getType());
  }
}
