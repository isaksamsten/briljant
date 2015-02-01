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
    return getAsInt(index) == 1;
  }

  @Override
  public void setBoolean(int index, boolean value) {
    setInt(index, value ? 1 : 0);
  }

  @Override
  public boolean[] asBooleanArray() {
    checkIntegerSize();
    boolean[] array = new boolean[(int) size()];
    for (int i = 0; i < size(); i++) {
      array[i] = getBoolean(i);
    }
    return array;
  }

  @Override
  public int[] asIntArray() {
    checkIntegerSize();
    int[] array = new int[(int) size()];
    for (int i = 0; i < size(); i++) {
      array[i] = getAsInt(i);
    }
    return array;
  }

  @Override
  public long[] asLongArray() {
    checkIntegerSize();
    long[] array = new long[(int) size()];
    for (int i = 0; i < size(); i++) {
      array[i] = getAsLong(i);
    }
    return array;
  }

  @Override
  public double[] asDoubleArray() {
    checkIntegerSize();
    double[] array = new double[(int) size()];
    for (int i = 0; i < size(); i++) {
      array[i] = getAsDouble(i);
    }
    return array;
  }

  @Override
  public Complex[] asComplexArray() {
    checkIntegerSize();
    Complex[] array = new Complex[(int) size()];
    for (int i = 0; i < size(); i++) {
      array[i] = getComplex(i);
    }
    return array;
  }

  protected final void checkIntegerSize() throws ArrayStoreException {
    if (size() > Integer.MAX_VALUE) {
      throw new ArrayStoreException();
    }
  }

  protected final int arrayIndex(long index) {
    return (int) index;
  }

  @Override
  public int size() {
    return size;
  }
}
