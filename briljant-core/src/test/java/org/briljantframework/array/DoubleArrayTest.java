/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Isak Karlsson
 */
public class DoubleArrayTest {

  @Test
  public void testRange() throws Exception {
    DoubleArray actual = DoubleArray.range(0, 1, 0.1);
    DoubleArray expected = DoubleArray.of(0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
    assertEquals(expected, actual);
  }

  @Test
  public void testGetRange_nonNegative_start() throws Exception {
    DoubleArray array = DoubleArray.range(0, 10, 1);
    DoubleArray expected = DoubleArray.of(5, 6, 7, 8, 9);

    System.out.println(Range.of(5, 10));

    DoubleArray actual = array.get(Range.of(5, 10));
    System.out.println(actual);
    assertEquals(expected, actual);
  }
}
