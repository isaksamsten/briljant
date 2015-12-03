/**
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
package org.briljantframework.data.vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.data.Is;
import org.briljantframework.data.Logical;
import org.briljantframework.data.Na;
import org.junit.Test;

public class ConvertTest {

  @Test
  public void testTo_convertToDouble() throws Exception {
    assertEquals(10.0, Convert.to(Double.class, 10), 0);
    assertEquals(10.0, Convert.to(Double.class, 10.0), 0);
    assertEquals(10.0, Convert.to(Double.class, 10.0f), 0);
    assertEquals(10.0, Convert.to(Double.class, 10l), 0);
    assertEquals(10.0, Convert.to(Double.class, (short) 10), 0);
    assertEquals(10.0, Convert.to(Double.class, (byte) 10), 0);
    assertEquals(10.0, Convert.to(Double.class, Complex.valueOf(10)), 0);
    assertEquals(1.0, Convert.to(Double.class, Logical.TRUE), 0);
    assertEquals(1.0, Convert.to(Double.class, true), 0);

    assertEquals(10.0, Convert.to(Double.TYPE, 10), 0);
    assertEquals(10.0, Convert.to(Double.TYPE, 10.0), 0);
    assertEquals(10.0, Convert.to(Double.TYPE, 10.0f), 0);
    assertEquals(10.0, Convert.to(Double.TYPE, 10l), 0);
    assertEquals(10.0, Convert.to(Double.TYPE, (short) 10), 0);
    assertEquals(10.0, Convert.to(Double.TYPE, (byte) 10), 0);
    assertEquals(10.0, Convert.to(Double.TYPE, Complex.valueOf(10)), 0);
    assertEquals(1.0, Convert.to(Double.TYPE, Logical.TRUE), 0);
    assertEquals(1.0, Convert.to(Double.TYPE, true), 0);
  }

  @Test
  public void testTo_convertToFloat() throws Exception {
    assertEquals(10.0, Convert.to(Float.class, 10), 0);
    assertEquals(10.0, Convert.to(Float.class, 10.0), 0);
    assertEquals(10.0, Convert.to(Float.class, 10.0f), 0);
    assertEquals(10.0, Convert.to(Float.class, 10l), 0);
    assertEquals(10.0, Convert.to(Float.class, (short) 10), 0);
    assertEquals(10.0, Convert.to(Float.class, (byte) 10), 0);
    assertEquals(10.0, Convert.to(Float.class, Complex.valueOf(10)), 0);
    assertEquals(1.0, Convert.to(Float.class, Logical.TRUE), 0);
    assertEquals(1.0, Convert.to(Float.class, true), 0);

    assertEquals(10.0, Convert.to(Float.TYPE, 10), 0);
    assertEquals(10.0, Convert.to(Float.TYPE, 10.0), 0);
    assertEquals(10.0, Convert.to(Float.TYPE, 10.0f), 0);
    assertEquals(10.0, Convert.to(Float.TYPE, 10l), 0);
    assertEquals(10.0, Convert.to(Float.TYPE, (short) 10), 0);
    assertEquals(10.0, Convert.to(Float.TYPE, (byte) 10), 0);
    assertEquals(10.0, Convert.to(Float.TYPE, Complex.valueOf(10)), 0);
    assertEquals(1.0, Convert.to(Float.TYPE, Logical.TRUE), 0);
    assertEquals(1.0, Convert.to(Float.TYPE, true), 0);
  }

  @Test
  public void testTo_convertToLong() throws Exception {
    assertEquals(10, (long) Convert.to(Long.class, 10));
    assertEquals(10, (long) Convert.to(Long.class, 10.0));
    assertEquals(10, (long) Convert.to(Long.class, 10.0f));
    assertEquals(10, (long) Convert.to(Long.class, 10l));
    assertEquals(10, (long) Convert.to(Long.class, (short) 10));
    assertEquals(10, (long) Convert.to(Long.class, (byte) 10));
    assertEquals(10, (long) Convert.to(Long.class, Complex.valueOf(10)));
    assertEquals(1, (long) Convert.to(Long.class, Logical.TRUE));
    assertEquals(1, (long) Convert.to(Long.class, true));

    assertEquals(10, (long) Convert.to(Long.TYPE, 10));
    assertEquals(10, (long) Convert.to(Long.TYPE, 10.0));
    assertEquals(10, (long) Convert.to(Long.TYPE, 10.0f));
    assertEquals(10, (long) Convert.to(Long.TYPE, 10l));
    assertEquals(10, (long) Convert.to(Long.TYPE, (short) 10));
    assertEquals(10, (long) Convert.to(Long.TYPE, (byte) 10));
    assertEquals(10, (long) Convert.to(Long.TYPE, Complex.valueOf(10)));
    assertEquals(1, (long) Convert.to(Long.TYPE, Logical.TRUE));
    assertEquals(1, (long) Convert.to(Long.TYPE, true));
  }

  @Test
  public void testTo_convertToInt() throws Exception {
    assertEquals(10, (int) Convert.to(Integer.class, 10));
    assertEquals(10, (int) Convert.to(Integer.class, 10.0));
    assertEquals(10, (int) Convert.to(Integer.class, 10.0f));
    assertEquals(10, (int) Convert.to(Integer.class, 10l));
    assertEquals(10, (int) Convert.to(Integer.class, (short) 10));
    assertEquals(10, (int) Convert.to(Integer.class, (byte) 10));
    assertEquals(10, (int) Convert.to(Integer.class, Complex.valueOf(10)));
    assertEquals(1, (int) Convert.to(Integer.class, Logical.TRUE));
    assertEquals(1, (int) Convert.to(Integer.class, true));

    assertEquals(10, (int) Convert.to(Integer.TYPE, 10));
    assertEquals(10, (int) Convert.to(Integer.TYPE, 10.0));
    assertEquals(10, (int) Convert.to(Integer.TYPE, 10.0f));
    assertEquals(10, (int) Convert.to(Integer.TYPE, 10l));
    assertEquals(10, (int) Convert.to(Integer.TYPE, (short) 10));
    assertEquals(10, (int) Convert.to(Integer.TYPE, (byte) 10));
    assertEquals(10, (int) Convert.to(Integer.TYPE, Complex.valueOf(10)));
    assertEquals(1, (int) Convert.to(Integer.TYPE, Logical.TRUE));
    assertEquals(1, (int) Convert.to(Integer.TYPE, true));
  }

  @Test
  public void testTo_convertToShort() throws Exception {
    assertEquals(10, (short) Convert.to(Short.class, 10));
    assertEquals(10, (short) Convert.to(Short.class, 10.0));
    assertEquals(10, (short) Convert.to(Short.class, 10.0f));
    assertEquals(10, (short) Convert.to(Short.class, 10l));
    assertEquals(10, (short) Convert.to(Short.class, (short) 10));
    assertEquals(10, (short) Convert.to(Short.class, (byte) 10));
    assertEquals(10, (short) Convert.to(Short.class, Complex.valueOf(10)));
    assertEquals(1, (short) Convert.to(Short.class, Logical.TRUE));
    assertEquals(1, (short) Convert.to(Short.class, true));

    assertEquals(10, (short) Convert.to(Short.TYPE, 10));
    assertEquals(10, (short) Convert.to(Short.TYPE, 10.0));
    assertEquals(10, (short) Convert.to(Short.TYPE, 10.0f));
    assertEquals(10, (short) Convert.to(Short.TYPE, 10l));
    assertEquals(10, (short) Convert.to(Short.TYPE, (short) 10));
    assertEquals(10, (short) Convert.to(Short.TYPE, (byte) 10));
    assertEquals(10, (short) Convert.to(Short.TYPE, Complex.valueOf(10)));
    assertEquals(1, (short) Convert.to(Short.TYPE, Logical.TRUE));
    assertEquals(1, (short) Convert.to(Short.TYPE, true));
  }

  @Test
  public void testTo_convertToByte() throws Exception {
    assertEquals(10, (byte) Convert.to(Byte.class, 10));
    assertEquals(10, (byte) Convert.to(Byte.class, 10.0));
    assertEquals(10, (byte) Convert.to(Byte.class, 10.0f));
    assertEquals(10, (byte) Convert.to(Byte.class, 10l));
    assertEquals(10, (byte) Convert.to(Byte.class, (short) 10));
    assertEquals(10, (byte) Convert.to(Byte.class, (byte) 10));
    assertEquals(10, (byte) Convert.to(Byte.class, Complex.valueOf(10)));
    assertEquals(1, (byte) Convert.to(Byte.class, Logical.TRUE));
    assertEquals(1, (byte) Convert.to(Byte.class, true));

    assertEquals(10, (byte) Convert.to(Byte.TYPE, 10));
    assertEquals(10, (byte) Convert.to(Byte.TYPE, 10.0));
    assertEquals(10, (byte) Convert.to(Byte.TYPE, 10.0f));
    assertEquals(10, (byte) Convert.to(Byte.TYPE, 10l));
    assertEquals(10, (byte) Convert.to(Byte.TYPE, (short) 10));
    assertEquals(10, (byte) Convert.to(Byte.TYPE, (byte) 10));
    assertEquals(10, (byte) Convert.to(Byte.TYPE, Complex.valueOf(10)));
    assertEquals(1, (byte) Convert.to(Byte.TYPE, Logical.TRUE));
    assertEquals(1, (byte) Convert.to(Byte.TYPE, true));
  }

  @Test
  public void testTo_convertToComplex() throws Exception {

    System.out.println(Na.of(Integer.TYPE));

    assertEquals(Complex.valueOf(10), Convert.to(Complex.class, 10));
    assertEquals(Complex.valueOf(10), Convert.to(Complex.class, 10.0));
    assertEquals(Complex.valueOf(10), Convert.to(Complex.class, 10.0f));
    assertEquals(Complex.valueOf(10), Convert.to(Complex.class, 10l));
    assertEquals(Complex.valueOf(10), Convert.to(Complex.class, (short) 10));
    assertEquals(Complex.valueOf(10), Convert.to(Complex.class, (byte) 10));
    assertEquals(Complex.valueOf(10), Convert.to(Complex.class, Complex.valueOf(10)));
    assertEquals(Complex.valueOf(1), Convert.to(Complex.class, Logical.TRUE));
    assertEquals(Complex.valueOf(1), Convert.to(Complex.class, true));
  }

  @Test
  public void testTo() throws Exception {
    assertEquals(10, Convert.to(Double.class, 10), 0);
    assertEquals(1, Convert.to(Integer.class, 1.0), 0);
    assertEquals("100", Convert.to(String.class, 100));

    assertEquals(1, Convert.to(Double.class, Logical.TRUE), 0);
    assertEquals(0, Convert.to(Double.class, Logical.FALSE), 0);
    assertTrue(Is.NA(Convert.to(Double.class, Logical.NA)));

    assertEquals(1, (int) Convert.to(Integer.class, Logical.TRUE));
    assertEquals(0, (int) Convert.to(Integer.class, Logical.FALSE));
    assertTrue(Is.NA(Convert.to(Integer.class, Logical.NA)));

    assertEquals(Complex.ONE, Convert.to(Complex.class, Logical.TRUE));
    assertEquals(Complex.ZERO, Convert.to(Complex.class, Logical.FALSE));
    assertTrue(Is.NA(Convert.to(Complex.class, Logical.NA)));

    assertEquals(Complex.valueOf(10), Convert.to(Complex.class, 10));
    assertTrue(Is.NA(Convert.to(Complex.class, Na.DOUBLE)));
    assertTrue(Is.NA(Convert.to(Complex.class, Na.INT)));

    assertEquals(Logical.TRUE, Convert.to(Logical.class, 1));
    assertEquals(Logical.FALSE, Convert.to(Logical.class, 0));
    assertEquals(Logical.NA, Convert.to(Logical.class, "100"));

    assertTrue(Is.NA(Convert.to(Complex.class, Na.DOUBLE)));
    assertTrue(Is.NA(Convert.to(Complex.class, Na.INT)));

    assertEquals(10, Convert.to(Double.class, 10.0), 0);
    assertEquals(10, (int) Convert.to(Integer.class, 10));

    assertEquals(DoubleVector.of(1, 2, 3), Convert.to(Vector.class, Vector.of(1.0, 2.0, 3.0)));
  }
}
