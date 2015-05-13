package org.briljantframework.vector;

import org.briljantframework.complex.Complex;

/**
 * Utility class for checking value types <p> Created by Isak Karlsson on 26/11/14.
 */
public final class Is {

  private Is() {

  }

  public static boolean nominal(Object value) {
    return !(value instanceof Number);
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(int value) {
    return value == IntVector.NA;
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(Complex value) {
    return ComplexVector.NA.equals(value);
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(String value) {
    return StringVector.TYPE.isNA(value);
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(double value) {
    return Double.isNaN(value)
           && (Double.doubleToRawLongBits(value) & DoubleVector.NA_MASK) == DoubleVector.NA_RES;
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(Bit value) {
    return BitVector.TYPE.isNA(value);
  }

  /**
   * Check if vector is NA-vector
   *
   * @param value the vector
   * @return true if vector is {@code Undefined.INSTANCE}
   */
  public static boolean NA(Vector value) {
    return value.size() == 0;
  }

  public static boolean NA(Object o) {
    if (o == null) {
      return true;
    } else if (o instanceof Double) {
      return Is.NA((double) o);
    } else if (o instanceof Integer) {
      return Is.NA((int) o);
    } else {
      Object na = Na.of(o.getClass());
      return o.equals(na);
    }
  }
}
