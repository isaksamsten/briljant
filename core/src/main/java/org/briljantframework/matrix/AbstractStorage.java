package org.briljantframework.matrix;

import org.briljantframework.matrix.storage.Storage;

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
  public int[] intArray() {
    int[] a = new int[size()];
    for (int i = 0; i < a.length; i++) {
      a[i] = getInt(i);
    }
    return a;
  }

  @Override
  public double[] doubleArray() {
    double[] a = new double[size()];
    for (int i = 0; i < a.length; i++) {
      a[i] = getDouble(i);
    }
    return a;
  }

  @Override
  public boolean isArrayBased() {
    return false;
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
