package org.briljantframework.matrix.storage;

import com.google.common.base.Preconditions;

import com.carrotsearch.hppc.IntIntMap;
import com.carrotsearch.hppc.IntIntOpenHashMap;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.AbstractStorage;

/**
 * Created by isak on 26/03/15.
 */
public class SparseIntStorage extends AbstractStorage {

  private final IntIntMap values;

  public SparseIntStorage(int size) {
    super(size);
    this.values = new IntIntOpenHashMap();
  }

  private SparseIntStorage(int size, IntIntMap values) {
    super(size);
    this.values = new IntIntOpenHashMap(values);
  }

  @Override
  public int getInt(int index) {
    Preconditions.checkElementIndex(index, size());
    return values.getOrDefault(index, 0);
  }

  @Override
  public void setInt(int index, int value) {
    Preconditions.checkElementIndex(index, size());
    values.put(index, value);
  }

  @Override
  public long getLong(int index) {
    return getInt(index);
  }

  @Override
  public void setLong(int index, long value) {
    setInt(index, (int) value);
  }

  @Override
  public double getDouble(int index) {
    return getInt(index);
  }

  @Override
  public void setDouble(int index, double value) {
    setInt(index, (int) value);
  }

  @Override
  public Complex getComplex(int index) {
    return Complex.valueOf(getDouble(index));
  }

  @Override
  public void setComplex(int index, Complex complex) {
    setDouble(index, complex.doubleValue());
  }

  @Override
  public Class<?> getNativeType() {
    return Integer.TYPE;
  }

  @Override
  public Storage copy() {
    return new SparseIntStorage(size(), values);
  }
}
