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

public class ComplexVectorTest {

  private ComplexVector vector;

  @Before
  public void setUp() throws Exception {
    vector = new ComplexVector.Builder().add(1).add(2).add(Complex.NaN).add(Complex.ONE).build();
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
