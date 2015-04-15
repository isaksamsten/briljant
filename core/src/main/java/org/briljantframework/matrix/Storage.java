package org.briljantframework.matrix;

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

  static Storage freeze(Storage storage) {
    return new AbstractStorage(storage.size()) {
      @Override
      public int getInt(int index) {
        return storage.getInt(index);
      }

      @Override
      public void setInt(int index, int value) {
        throw new UnsupportedOperationException();
      }

      @Override
      public long getLong(int index) {
        return storage.getLong(index);
      }

      @Override
      public void setLong(int index, long value) {
        throw new UnsupportedOperationException();
      }

      @Override
      public double getDouble(int index) {
        return storage.getInt(index);
      }

      @Override
      public void setDouble(int index, double value) {
        throw new UnsupportedOperationException();
      }

      @Override
      public Complex getComplex(int index) {
        return storage.getComplex(index);
      }

      @Override
      public void setComplex(int index, Complex complex) {
        throw new UnsupportedOperationException();
      }

      @Override
      public Class<?> getNativeType() {
        return storage.getNativeType();
      }

      @Override
      public Storage copy() {
        return storage.copy().frozen();
      }
    };
  }

  /**
   * Returns a froze representation of this storage container. While it is impossible to modify the
   * storage returned, any references kept of the unfrozen storage can be modified and those
   * modifications are propagated to the frozen instance. To create an independent and immutable
   * storage, use {@code storage.copy().frozen()}. Since {@code as[Type]Array()} is allowed to
   * return an underlying (mutable) representation, a frozen {@code Storage} can never be
   * guaranteed
   * to be immutable. Also, remember that copy is not synchronized, hence, modifications before the
   * call to {@code frozen} can happen in another thread. Hence, {@code synchronize(storage)
   * storage.copy().frozen()} could be preferred.
   *
   * @return a frozen storage instance
   */
  default Storage frozen() {
    return freeze(this);
  }

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
//  /**
//   * Returns this storage as a {@code int} array, with the i:th element set to {@code getInt(i)} .
//   *
//   * If this storage uses a int array, modifications of the returned array propagates.
//   *
//   * @return a int array
//   */
//  int[] intArray();
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
//  /**
//   * Returns this storage as a {@code double} array, with the i:th element set to
//   * {@code getDouble(i)}.
//   *
//   * If this storage uses a double array, modifications of the returned array propagates.
//   *
//   * @return a boolean array
//   */
//  double[] doubleArray();
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
//  /**
//   * @return true if {@code as[Type]Array()} returns an underlying array representation
//   */
//  boolean isArrayBased();

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
