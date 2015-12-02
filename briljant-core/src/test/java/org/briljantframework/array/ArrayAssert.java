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
package org.briljantframework.array;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.complex.Complex;
import org.junit.Assert;

/**
 * Created by isak on 2/4/15.
 */
public final class ArrayAssert {

  private static final String ROW_SIZE = "Size of rows does not match.";
  private static final String COL_SIZE = "Size of columns does not match.";
  private static final String VAL_MATCH = "Values does not match.";

  private ArrayAssert() {}

  public static void assertEqualShape(BaseArray expected, BaseArray actual) {
    Assert.assertArrayEquals(expected.getShape(), actual.getShape());
    // assertEquals(ROW_SIZE, expected.rows(), actual.rows());
    // assertEquals(COL_SIZE, expected.columns(), actual.columns());
  }

  public static void assertArrayEquals(IntArray expect, IntArray actual) {
    assertEqualShape(expect, actual);
    for (int i = 0; i < expect.size(); i++) {
      assertEquals(VAL_MATCH, expect.get(i), actual.get(i));
    }
  }

  public static void assertArrayEquals(LongArray expect, LongArray actual) {
    assertEqualShape(expect, actual);
    for (int i = 0; i < expect.size(); i++) {
      assertEquals(VAL_MATCH, expect.get(i), actual.get(i));
    }
  }

  public static void assertArrayEquals(DoubleArray expect, DoubleArray actual, double epsilon) {
    assertEqualShape(expect, actual);
    for (int i = 0; i < expect.size(); i++) {
      try {
        assertEquals(VAL_MATCH, expect.get(i), actual.get(i), epsilon);
      } catch (AssertionError e) {
        throw new AssertionError(String.format(
            "Value mismatch at position %d in \n(expected)\n%s\n(actual)\n%s", i,
            expect.toString(), actual.toString()), e);
      }
    }
  }

  public static void assertValueEquals(DoubleArray actual, DoubleArray expected, double epsilon) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i), epsilon);
    }
  }

  public static void assertArrayEquals(int expected, IntArray actual) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected, actual.get(i));
    }
  }

  public static void assertArrayEquals(LongArray actual, long value) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(value, actual.get(i));
    }
  }

  public static void assertArrayEquals(double expected, DoubleArray actual, double e) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected, actual.get(i), e);
    }
  }

  public static void assertValuesEquals(IntArray expected, IntArray actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertArrayEquals(ComplexArray actual, Complex value) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(value, actual.get(i));
    }
  }

  public static void assertValuesEquals(BooleanArray expected, BooleanArray actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertArrayEquals(BooleanArray actual, boolean expected) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected, actual.get(i));
    }
  }

  public static void assertValuesEquals(LongArray actual, LongArray expected) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertValuesEquals(ComplexArray expected, ComplexArray actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertArrayEquals(ComplexArray expected, ComplexArray actual) {
    assertEqualShape(expected, actual);
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i).getReal(), actual.get(i).getReal(), 1e-4);
      assertEquals(expected.get(i).getImaginary(), actual.get(i).getImaginary(), 1e-4);
    }
  }

  public static void assertArrayEquals(double expected, ComplexArray actual) {
    assertArrayEquals(actual, Complex.valueOf(expected));
  }
}
