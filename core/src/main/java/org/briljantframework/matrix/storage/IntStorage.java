package org.briljantframework.matrix.storage;

import java.util.Arrays;

import org.briljantframework.complex.Complex;

/**
 * Created by isak on 1/30/15.
 */
public class IntStorage extends AbstractStorage {
  private final int[] values;

  public IntStorage(int[] values) {
    super(values.length);
    this.values = values;
  }

  public static IntStorage withSize(int size) {
    return new IntStorage(new int[size]);
  }

  @Override
  public int getInt(int index) {
    return values[arrayIndex(index)];
  }

  @Override
  public void setInt(int index, int value) {
    values[arrayIndex(index)] = value;
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
    setInt(index, complex.intValue());
  }

  @Override
  public boolean isArrayBased() {
    return true;
  }

  @Override
  public int[] asIntArray() {
    return values;
  }

  @Override
  public Class<?> getNativeType() {
    return Integer.TYPE;
  }

  @Override
  public Storage copy() {
    return new IntStorage(Arrays.copyOf(values, values.length));
  }
}
