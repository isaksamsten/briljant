package org.briljantframework.matrix;

import static org.junit.Assert.assertEquals;

import org.briljantframework.complex.Complex;

/**
 * Created by isak on 2/4/15.
 */
public final class MatrixAssert {
  private MatrixAssert() {}

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

  public static void assertMatrixEquals(IntMatrix m, int... values) {
    assertEquals(values.length, m.size());
    for (int i = 0; i < m.size(); i++) {
      assertEquals(values[i], m.get(i));
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

  public static void assertMatrixEquals(DoubleMatrix m, double epsilon, double... values) {
    assertEquals(values.length, m.size());
    for (int i = 0; i < m.size(); i++) {
      assertEquals(values[i], m.get(i), epsilon);
    }
  }

  public static void assertMatrixEquals(LongMatrix m, long... values) {
    assertEquals(values.length, m.size());
    for (int i = 0; i < m.size(); i++) {
      assertEquals(values[i], m.get(i));
    }
  }

  public static void assertMatrixEquals(ComplexMatrix m, Complex... values) {
    assertEquals(values.length, m.size());
    for (int i = 0; i < m.size(); i++) {
      assertEquals(values[i], m.get(i));
    }
  }
}
