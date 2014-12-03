package org.briljantframework.vector;

/**
 * A value is vector of size one.
 * <p>
 * Created by Isak Karlsson on 27/11/14.
 */
public interface Value extends Vector, Comparable<Value> {

  /**
   * Gets the value as double
   *
   * @return the value
   * @see Vector#getAsReal(int)
   */
  default double getAsDouble() {
    return getAsReal(0);
  }

  /**
   * Gets the value as int
   *
   * @return the value
   * @see Vector#getAsInt(int)
   */
  default int getAsInt() {
    return getAsInt(0);
  }

  /**
   * Gets the value as String
   *
   * @return the value
   * @see Vector#getAsString(int)
   */
  default String getAsString() {
    return getAsString(0);
  }

  /**
   * Gets the value as Complex
   *
   * @return the value
   * @see Vector#getAsComplex(int)
   */
  default Complex getAsComplex() {
    return getAsComplex(0);
  }

  /**
   * Gets the value as Binary
   *
   * @return the value
   * @see Vector#getAsBinary(int)
   */
  default Binary getAsBinary() {
    return getAsBinary(0);
  }


  /**
   * Returns this as value
   *
   * @return the value
   */
  default Value getAsValue() {
    return this;
  }

  /**
   * Returns true is this value is NA
   *
   * @return true if this value is NA
   */
  default boolean isNA() {
    return isNA(0);
  }
}
