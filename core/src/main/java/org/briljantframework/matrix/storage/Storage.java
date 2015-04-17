package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;

/**
 * Represent a mutable storage unit. For example, an {@code Array}, {@link java.util.List}, memory
 * mapped file or any other internal or external resource.
 *
 * <p>For external resources, {@link #finalize()} shall be overridden
 *
 * <p>Values of different types coerces without exceptions, but precision might be lost.
 *
 * @author Isak Karlsson
 */
public interface Storage {

  /**
   * Get value as boolean
   *
   * @param index the index
   * @return the boolean
   */
  boolean getBoolean(int index);

  /**
   * Set value as boolean
   *
   * @param index the index
   * @param value the value
   */
  void setBoolean(int index, boolean value);

  /**
   * Get value as int
   *
   * @param index the index
   * @return the int
   */
  int getInt(int index);

  /**
   * Set value as int
   *
   * @param index the index
   * @param value the value
   */
  void setInt(int index, int value);

  /**
   * Get value as long
   *
   * @param index the index
   * @return the long
   */
  long getLong(int index);

  /**
   * Set value as long
   *
   * @param index the index
   * @param value the value
   */
  void setLong(int index, long value);

  /**
   * Get value as double
   *
   * @param index the index
   * @return the double
   */
  double getDouble(int index);

  /**
   * Set value as double
   *
   * @param index the index
   * @param value the value
   */
  void setDouble(int index, double value);

  /**
   * Get value as complex
   *
   * @param index the index
   * @return the complex
   */
  Complex getComplex(int index);

  /**
   * Set value as complex
   *
   * @param index   the index
   * @param complex the value
   */
  void setComplex(int index, Complex complex);

//  /**
//   * Returns this storage as a {@code boolean} array, with the i:th element set to
//   * {@code getBoolean(i)}.
//   *
//   * If this storage uses a boolean array, modifications of the returned array propagates.
//   *
//   * @return a boolean array
//   */
//  boolean[] booleanArray();
//

  /**
   * Returns this storage as a {@code int} array, with the i:th element set to {@code getInt(i)} .
   *
   * If this storage uses a int array, modifications of the returned array propagates.
   *
   * @return a int array
   */
  int[] intArray();
//
//  /**
//   * Returns this storage as a {@code long} array, with the i:th element set to {@code getLong(i)}.
//   *
//   * If this storage uses a long array, modifications of the returned array propagates.
//   *
//   * @return a long array
//   */
//  long[] longArray();
//
  /**
   * Returns this storage as a {@code double} array, with the i:th element set to
   * {@code getDouble(i)}.
   *
   * If this storage uses a double array, modifications of the returned array propagates.
   *
   * @return a boolean array
   */
  double[] doubleArray();
//
//  /**
//   * Returns this storage as a {@code Complex} array, with the i:th element set to
//   * {@code getComplex(i)}.
//   *
//   * If this storage uses a complex array, modifications of the returned array propagates.
//   *
//   * @return a complex array
//   */
//  Complex[] complexArray();
//
  /**
   * @return true if {@code as[Type]Array()} returns an underlying array representation
   */
  boolean isArrayBased();

  /**
   * @return the native type covered by this storage.
   */
  Class<?> getNativeType();

  /**
   * Creates an independent copy of this storage.
   *
   * @return a copy
   */
  Storage copy();

  /**
   * @return the number of elements in this storage
   */
  int size();
}
