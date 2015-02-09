package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractStorage implements Storage {

  private final int size;

  protected AbstractStorage(int size) {
    this.size = size;
  }

  @Override
  public boolean getBoolean(int index) {
    return getInt(index) == 1;
  }

  @Override
  public void setBoolean(int index, boolean value) {
    setInt(index, value ? 1 : 0);
  }

  @Override
  public boolean[] asBooleanArray() {
    boolean[] array = new boolean[size()];
    for (int i = 0; i < size(); i++) {
      array[i] = getBoolean(i);
    }
    return array;
  }

  @Override
  public int[] asIntArray() {
    int[] array = new int[size()];
    for (int i = 0; i < size(); i++) {
      array[i] = getInt(i);
    }
    return array;
  }

  @Override
  public long[] asLongArray() {
    long[] array = new long[size()];
    for (int i = 0; i < size(); i++) {
      array[i] = getLong(i);
    }
    return array;
  }

  @Override
  public double[] asDoubleArray() {
    double[] array = new double[size()];
    for (int i = 0; i < size(); i++) {
      array[i] = getDouble(i);
    }
    return array;
  }

  @Override
  public Complex[] asComplexArray() {
    Complex[] array = new Complex[size()];
    for (int i = 0; i < size(); i++) {
      array[i] = getComplex(i);
    }
    return array;
  }

  protected final int arrayIndex(long index) {
    return (int) index;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public String toString() {
    return String.format("Storage for %s of %d values", getNativeType(), size());
  }
}
