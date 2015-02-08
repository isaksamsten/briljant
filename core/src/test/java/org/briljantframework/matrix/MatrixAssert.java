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

  public static void assertMatrixEquals(IntMatrix m, int expected) {
    for (int i = 0; i < m.size(); i++) {
      assertEquals(expected, m.get(i));
    }
  }

  public static void assertMatrixEquals(LongMatrix l, long value) {
    for (int i = 0; i < l.size(); i++) {
      assertEquals(value, l.get(i));
    }
  }

  public static void assertMatrixEquals(DoubleMatrix matrix, double v, double e) {
    for (int i = 0; i < matrix.size(); i++) {
      assertEquals(v, matrix.get(i), e);
    }
  }

  public static void assertValuesEquals(IntMatrix m, IntMatrix values) {
    assertEquals(values.size(), m.size());
    for (int i = 0; i < m.size(); i++) {
      assertEquals(values.get(i), m.get(i));
    }
  }

  public static void assertMatrixEquals(ComplexMatrix m, Complex value) {
    for (int i = 0; i < m.size(); i++) {
      assertEquals(value, m.get(i));
    }
  }

  public static void assertMatrixEquals(BitMatrix matrix, boolean... values) {
    assertEquals(values.length, matrix.size());
    for (int i = 0; i < matrix.size(); i++) {
      assertEquals(values[i], matrix.get(i));
    }
  }

  public static void assertMatrixEquals(BitMatrix m, boolean b) {
    for (int i = 0; i < m.size(); i++) {
      assertEquals(b, m.get(i));
    }
  }

  public static void assertValuesEquals(LongMatrix actual, LongMatrix expected) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  public static void assertMatrixEquals(ComplexMatrix m, Complex... values) {
    assertEquals(values.length, m.size());
    for (int i = 0; i < m.size(); i++) {
      assertEquals(values[i], m.get(i));
    }
  }
}
