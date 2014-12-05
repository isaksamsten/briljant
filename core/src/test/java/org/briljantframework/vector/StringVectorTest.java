package org.briljantframework.vector;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class StringVectorTest {

  StringVector strings;

  String[] STRING_ARRAY = new String[] {"a", "b", "false", "true", "1", "2.3", null};

  @Before
  public void setUp() throws Exception {
    StringVector.Builder builder = new StringVector.Builder();
    Stream.of(STRING_ARRAY).forEach(builder::add);
    strings = builder.build();
  }

  @Test
  public void testIterator() throws Exception {
    int i = 0;
    for (String str : strings) {
      assertEquals(STRING_ARRAY[i++], str);
    }
  }

  @Test
  public void testGetAsDouble() throws Exception {
    assertEquals(2.3, strings.getAsReal(5), 0);
    assertEquals(1, strings.getAsReal(4), 0);
    assertEquals(RealVector.NA, strings.getAsReal(0), 0);
    assertEquals(RealVector.NA, strings.getAsReal(strings.size() - 1), 0);

  }

  @Test
  public void testGetAsInteger() throws Exception {
    assertEquals(2, strings.getAsInt(5), 0);
    assertEquals(1, strings.getAsInt(4), 0);
    assertEquals(IntVector.NA, strings.getAsInt(0), 0);
    assertEquals(IntVector.NA, strings.getAsInt(strings.size() - 1), 0);
  }

  @Test
  public void testGetAsBinary() throws Exception {
    assertEquals(Binary.TRUE, strings.getAsBinary(3));
    assertEquals(Binary.FALSE, strings.getAsBinary(2));
    assertEquals(Binary.NA, strings.getAsBinary(0));
    assertEquals(Binary.NA, strings.getAsBinary(strings.size() - 1));
  }

  @Test
  public void testGetAsString() throws Exception {
    assertEquals("true", strings.getAsString(3));
    assertEquals("false", strings.getAsString(2));
    assertEquals("a", strings.getAsString(0));
    assertEquals(StringVector.NA, strings.getAsString(strings.size() - 1));
  }

  @Test
  public void testIsNA() throws Exception {
    assertEquals(true, strings.isNA(strings.size() - 1));
    assertEquals(false, strings.isNA(2));
  }

  @Test
  public void testCompare() throws Exception {
    assertEquals(true, strings.compare(0, 1) < 0);
    assertEquals(true, strings.compare(6, 6) == 0);
    assertEquals(false, strings.compare(1, 0) < 0);
  }

  @Test
  public void testSize() throws Exception {
    assertEquals(7, strings.size());
  }

  @Test
  public void testGetType() throws Exception {
    assertEquals(StringVector.TYPE, strings.getType());
  }

  @Test
  public void testNewCopyBuilder() throws Exception {
    StringVector copy = strings.newCopyBuilder().add("hello").build();
    assertEquals("hello", copy.getAsString(copy.size() - 1));
    assertEquals(8, copy.size());
  }

  @Test
  public void testSwap() throws Exception {
    StringVector.Builder builder = StringVector.newBuilderWithInitialValues("a", "b", "c");
    assertEquals("a", builder.build().getAsString(0));
    assertEquals("b", builder.swap(0, 1));

  }

  @Test
  public void testNewBuilder() throws Exception {

  }

  @Test
  public void testNewBuilder1() throws Exception {

  }

  @Test
  public void testTryParseDouble() throws Exception {

  }

  @Test
  public void testTryParseInteger() throws Exception {

  }
}
