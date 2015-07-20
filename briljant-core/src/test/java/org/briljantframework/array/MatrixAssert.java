package org.briljantframework.array;

import org.briljantframework.complex.Complex;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by isak on 2/4/15.
 */
public final class MatrixAssert {

  private static final String ROW_SIZE = "Size of rows does not match.";
  private static final String COL_SIZE = "Size of columns does not match.";
  private static final String VAL_MATCH = "Values does not match.";

  private MatrixAssert() {
  }

  public static void assertEqualShape(Array expected, Array actual) {
    assertArrayEquals(expected.getShape(), actual.getShape());
//    assertEquals(ROW_SIZE, expected.rows(), actual.rows());
//    assertEquals(COL_SIZE, expected.columns(), actual.columns());
  }

  public static void assertMatrixEquals(IntArray expect, IntArray actual) {
    assertEqualShape(expect, actual);
    for (int i = 0; i < expect.size(); i++) {
      assertEquals(VAL_MATCH, expect.get(i), actual.get(i));
    }
  }

  public static void assertMatrixEquals(LongArray expect, LongArray actual) {
    assertEqualShape(expect, actual);
    for (int i = 0; i < expect.size(); i++) {
      assertEquals(VAL_MATCH, expect.get(i), actual.get(i));
    }
  }

  public static void assertMatrixEquals(DoubleArray expect, DoubleArray actual, double epsilon) {
    assertEqualShape(expect, actual);
    for (int i = 0; i < expect.size(); i++) {
      assertEquals(VAL_MATCH, expect.get(i), actual.get(i), epsilon);
    }
  }

  public static void assertValueEquals(DoubleArray actual, DoubleArray expected, double epsilon) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i), epsilon);
    }
  }

  public static void assertMatrixEquals(int expected, IntArray actual) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected, actual.get(i));
    }
  }

  public static void assertMatrixEquals(LongArray actual, long value) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(value, actual.get(i));
    }
  }

  public static void assertMatrixEquals(double expected, DoubleArray actual, double e) {
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

  public static void assertMatrixEquals(ComplexArray actual, Complex value) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(value, actual.get(i));
    }
  }

  public static void assertValuesEquals(BitArray expected, BitArray actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertMatrixEquals(BitArray actual, boolean expected) {
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

  public static void assertMatrixEquals(ComplexArray expected, ComplexArray actual) {
    assertEqualShape(expected, actual);
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertMatrixEquals(double expected, ComplexArray actual) {
    assertMatrixEquals(actual, Complex.valueOf(expected));
  }
}
