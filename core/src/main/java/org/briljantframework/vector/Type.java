package org.briljantframework.vector;

/**
 * Provides information of a particular vectors type. The common choice is that referential equality
 * is used when comparing types.
 */
public interface Type {

  /**
   * Creates a new builder able to build vectors of this type
   *
   * @return a new builder
   */
  Vector.Builder newBuilder();

  /**
   * Creates a new builder able to build vectors of this type
   *
   * @param size initial size (the vector is padded with NA)
   * @return a new builder
   */
  Vector.Builder newBuilder(int size);

  /**
   * Copy (and perhaps convert) {@code vector} to this type
   *
   * @param vector the vector to copy
   * @return a new vector
   */
  default Vector copy(Vector vector) {
    return newBuilder(vector.size()).addAll(vector).build();
  }

  /**
   * Get the underlying class used to represent values of this vector type
   *
   * @return the class
   */
  Class<?> getDataClass();

  /**
   * Returns true if this object is NA for this value type
   *
   * @param value the value
   * @return true if value is NA
   */
  boolean isNA(Object value);

  /**
   * Compare value at position {@code a} from {@code va} to value at position {@code b} from
   * {@code va}.
   *
   * @param a the index in va
   * @param va the vector
   * @param b the index in ba
   * @param ba the vector
   * @return the comparison
   */
  int compare(int a, Vector va, int b, Vector ba);

  /**
   * @param a the index in {@code va}
   * @param va the vector
   * @param ba the value
   * @return the comparison
   * @see #compare(int, Vector, int, Vector)
   */
  default int compare(int a, Vector va, Value ba) {
    return compare(a, va, 0, ba);
  }

  /**
   * @param va the value
   * @param b the index in ba
   * @param ba the vector
   * @return the comparison
   * @see #compare(int, Vector, int, Vector)
   */
  default int compare(Value va, int b, Vector ba) {
    return compare(0, va, b, ba);
  }

  /**
   * Compares two values.
   *
   * @param va the first value
   * @param ba the second value
   * @return the comparison
   * @see #compare(int, Vector, int, Vector)
   */
  default int compare(Value va, Value ba) {
    return compare(0, va, 0, ba);
  }

  /**
   * Returns the scale of this type. If the scale is {@link Scale#CATEGORICAL}, the
   * {@link Vector#getAsString(int)} is expected to return a meaningful value. On the other hand, if
   * the value is {@link Scale#NUMERICAL} {@link Vector#getAsReal(int)} is expected to return a
   * meaning ful value (or NA).
   *
   * @return the scale
   */
  Scale getScale();

  /**
   * Check if value at position {@code a} from {@code va} and value at position {@code b} from
   * {@code va} are equal.
   *
   * @param a the index in va
   * @param va the vector
   * @param b the index in ba
   * @param ba the vector
   * @return true if equal false otherwise
   */
  default boolean equals(int a, Vector va, int b, Vector ba) {
    return compare(a, va, b, ba) == 0;
  }

  /**
   * Check if value {@code va} and {@code ba} are equal.
   *
   * @param va the value
   * @param ba the value
   * @return true if equal false otherwise
   */
  default boolean equals(Value va, Value ba) {
    return equals(0, va, 0, ba);
  }

  /**
   * Check if value {@code va} is equal to {@code ba.getValue(b)}
   * 
   * @param va the value
   * @param b the index in ba
   * @param ba the vector
   * @return true if equal false otherwise
   */
  default boolean equals(Value va, int b, Vector ba) {
    return equals(0, va, b, ba);
  }

  /**
   * The expectations on the data stored within this type. If the data is
   */
  public enum Scale {
    /**
     * If the scale is categorical
     */
    CATEGORICAL,

    /**
     * If the scale is numerical
     */
    NUMERICAL
  }
}
