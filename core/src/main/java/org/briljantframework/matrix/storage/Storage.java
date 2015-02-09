package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;

/**
 * Represent a mutable storage unit. For example, an {@code Array}, {@link java.util.List} or memory
 * mapped file.
 * 
 * Values of different types coerces without exceptions, but precision might be lost.
 * 
 * To support large storage containers, indexes are {@code long}. In theory this might impact
 * performance slightly, however, in practice this is rarely a problem.
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
   * @param index the index
   * @param complex the value
   */
  void setComplex(int index, Complex complex);

  /**
   * Returns this storage as a {@code boolean} array, with the i:th element set to
   * {@code getBoolean(i)}.
   *
   * It this storage uses a boolean array, modifications of the returned array propagates.
   *
   * @return a boolean array
   */
  boolean[] asBooleanArray();

  /**
   * Returns this storage as a {@code int} array, with the i:th element set to {@code getInt(i)} .
   *
   * It this storage uses a boolean array, modifications of the returned array propagates.
   *
   * @return a int array
   */
  int[] asIntArray();

  /**
   * Returns this storage as a {@code long} array, with the i:th element set to {@code getLong(i)}.
   *
   * It this storage uses a boolean array, modifications of the returned array propagates.
   *
   * @return a long array
   */
  long[] asLongArray();

  /**
   * Returns this storage as a {@code double} array, with the i:th element set to
   * {@code getDouble(i)}.
   *
   * It this storage uses a boolean array, modifications of the returned array propagates.
   *
   * @return a boolean array
   */
  double[] asDoubleArray();

  /**
   * Returns this storage as a {@code Complex} array, with the i:th element set to
   * {@code getComplex(i)}.
   *
   * It this storage uses a boolean array, modifications of the returned array propagates.
   *
   * @return a complex array
   */
  Complex[] asComplexArray();

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
