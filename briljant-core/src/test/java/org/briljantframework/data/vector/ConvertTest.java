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

package org.briljantframework.data.vector;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConvertTest {

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

    assertEquals(Vector.of(1, 2, 3), Convert.to(Vector.class, Vector.of(1.0, 2.0, 3.0)));
  }
}