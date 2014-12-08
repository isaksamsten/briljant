package org.briljantframework.vector;

/**
 * Utility class for checking value types
 * <p>
 * Created by Isak Karlsson on 26/11/14.
 */
public final class Is {
  private Is() {

  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(int value) {
    return IntVector.TYPE.isNA(value);
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(Complex value) {
    return ComplexVector.TYPE.isNA(value);
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
    return RealVector.TYPE.isNA(value);
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(Binary value) {
    return BinaryVector.TYPE.isNA(value);
  }

  /**
   * Check if vector is NA-vector
   *
   * @param value the vector
   * @return true if vector is {@code Undefined.INSTANCE}
   */
  public static boolean NA(Vector value) {
    return value == Undefined.INSTANCE;
  }

}