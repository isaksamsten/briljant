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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DoubleVectorTest {

  public static final double[] DOUBLE_ARRAY = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
  private DoubleVector vector;
  private DoubleVector hasNA = new DoubleVector.Builder().addNA().addNA().add(1).add(2).build();


  @Before
  public void setUp() throws Exception {
    DoubleVector.Builder builder = new DoubleVector.Builder();
    for (int i = 0; i < 10; i++) {
      builder.add(i);
    }
    vector = builder.build();
  }

  @Test
  public void testAddAtIndex() throws Exception {
    DoubleVector.Builder builder = new DoubleVector.Builder();
    builder.set(3, 10);
    builder.set(4, Double.NaN);
    builder.set(10, 10);
    Vector vec = builder.build();
    assertFalse(vec.isNA(4));
  }

  @Test
  public void testIterator() throws Exception {
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
    assertEquals(DoubleVector.TYPE, vector.getType());
  }

  @Test
  public void testNewCopyBuilder() throws Exception {
    DoubleVector copy = vector.newCopyBuilder().add(10).build();
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
    DoubleVector.Builder builder = vector.newBuilder();

    builder.add(hasNA, 0);
    builder.add(vector, 0);
    builder.add(vector, 9);
  }

  @Test
  public void testNewBuilder1() throws Exception {

  }

}
