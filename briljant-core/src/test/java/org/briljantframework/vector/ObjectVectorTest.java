/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.vector;

import org.apache.commons.math3.complex.Complex;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ObjectVectorTest {

  Vector sequence;

  @Before
  public void setUp() throws Exception {
    sequence = new GenericVector.Builder(Object.class)
        .add(1)
        .add(2)
        .add("hello")
        .add("next")
        .addNA()
        .build();
  }

  @Test
  public void testGetAsDouble() throws Exception {
    assertEquals(1, sequence.getAsDouble(0), 0);
    assertEquals(2, sequence.getAsDouble(1), 0);
    assertEquals(Na.DOUBLE, sequence.getAsDouble(3), 0);
  }

  @Test
  public void testGetAsInt() throws Exception {
    assertEquals(1, sequence.getAsInt(0));
    assertEquals(2, sequence.getAsInt(1));
    assertEquals(Na.INT, sequence.getAsInt(3));
  }

  @Test
  public void testGetAsBinary() throws Exception {
    assertEquals(Logical.TRUE, sequence.get(Logical.class, 0));
    assertEquals(Logical.FALSE, sequence.get(Logical.class, 1));
    assertEquals(Logical.NA, sequence.get(Logical.class, 3));
  }

  @Test
  public void testGetAsComplex() throws Exception {
    assertEquals(new Complex(1, 0), sequence.get(Complex.class, 0));
    assertEquals(new Complex(2, 0), sequence.get(Complex.class, 1));
    assertTrue(Is.NA(sequence.get(Complex.class, 2)));
    assertTrue(Is.NA(sequence.get(Complex.class, 3)));
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
//    ObjectVector.Builder builder = sequence.newCopyBuilder();
//    builder.add("hello");
//    builder.add(null);
//
//    ObjectVector copy = builder.build();
//    assertEquals(7, copy.size());
//    assertEquals("hello", copy.getAsString(5));
//    assertEquals(VariableVector.NA, copy.getAsValue(6));

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
//    assertEquals("1", sequence.getAsValue(0).getAsString());
//    assertEquals("2", sequence.getAsValue(1).getAsString());
//    assertEquals("hello", sequence.getAsValue(2).getAsString());
//    assertEquals(VariableVector.NA, sequence.getAsValue(4));
  }

  @Test
  public void testGetType() throws Exception {
    assertEquals(VectorType.from(Object.class), sequence.getType());
  }
}
