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
}
