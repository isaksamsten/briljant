package org.briljantframework.matrix;

import static org.junit.Assert.assertEquals;

import org.briljantframework.complex.Complex;

/**
 * Created by isak on 2/4/15.
 */
public final class MatrixAssert {
  private static final String ROW_SIZE = "Size of rows does not match.";
  private static final String COL_SIZE = "Size of columns does not match.";
  private static final String VAL_MATCH = "Values does not match.";

  private MatrixAssert() {}

  public static void assertEqualShape(Matrix expected, Matrix actual) {
    assertEquals(ROW_SIZE, expected.rows(), actual.rows());
    assertEquals(COL_SIZE, expected.columns(), actual.columns());
  }

  public static void assertMatrixEquals(IntMatrix expect, IntMatrix actual) {
    assertEqualShape(expect, actual);
    for (int i = 0; i < expect.size(); i++) {
      assertEquals(VAL_MATCH, expect.get(i), actual.get(i));
    }
  }

  public static void assertMatrixEquals(LongMatrix expect, LongMatrix actual) {
    assertEqualShape(expect, actual);
    for (int i = 0; i < expect.size(); i++) {
      assertEquals(VAL_MATCH, expect.get(i), actual.get(i));
    }
  }

  public static void assertMatrixEquals(DoubleMatrix expect, DoubleMatrix actual, double epsilon) {
    assertEqualShape(expect, actual);
    for (int i = 0; i < expect.size(); i++) {
      assertEquals(VAL_MATCH, expect.get(i), actual.get(i), epsilon);
    }
  }

  public static void assertValueEquals(DoubleMatrix actual, DoubleMatrix expected, double epsilon) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i), epsilon);
    }
  }

  public static void assertMatrixEquals(int expected, IntMatrix actual) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected, actual.get(i));
    }
  }

  public static void assertMatrixEquals(LongMatrix actual, long value) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(value, actual.get(i));
    }
  }

  public static void assertMatrixEquals(DoubleMatrix actual, double expected, double e) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected, actual.get(i), e);
    }
  }

  public static void assertValuesEquals(IntMatrix expected, IntMatrix actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertMatrixEquals(ComplexMatrix actual, Complex value) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(value, actual.get(i));
    }
  }

  public static void assertValuesEquals(BitMatrix expected, BitMatrix actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertMatrixEquals(BitMatrix actual, boolean expected) {
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected, actual.get(i));
    }
  }

  public static void assertValuesEquals(LongMatrix actual, LongMatrix expected) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertValuesEquals(ComplexMatrix expected, ComplexMatrix actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertMatrixEquals(ComplexMatrix expected, ComplexMatrix actual) {
    assertEqualShape(expected, actual);
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertMatrixEquals(double expected, ComplexMatrix actual) {
    assertMatrixEquals(actual, Complex.valueOf(expected));
  }
}
