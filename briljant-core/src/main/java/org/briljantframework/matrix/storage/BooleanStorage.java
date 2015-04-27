package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.AbstractStorage;

import java.util.Arrays;

/**
 * @author Isak Karlsson
 */
public class BooleanStorage extends AbstractStorage {

  private final boolean[] values;

  public BooleanStorage(boolean[] values) {
    super(values.length);
    this.values = values;
  }

  public BooleanStorage(int size) {
    super(size);
    this.values = new boolean[size];
  }

  @Override
  public boolean getBoolean(int index) {
    return values[index];
  }

  @Override
  public int getInt(int index) {
    return values[index] ? 1 : 0;
  }

  @Override
  public void setInt(int index, int value) {
    values[index] = value == 1;
  }

  @Override
  public long getLong(int index) {
    return getInt(index);
  }

  @Override
  public void setLong(int index, long value) {
    values[index] = value == 1;
  }

  @Override
  public double getDouble(int index) {
    return getInt(index);
  }

  @Override
  public void setDouble(int index, double value) {
    setLong(index, (long) value);
  }

  @Override
  public Complex getComplex(int index) {
    return getBoolean(index) ? Complex.ONE : Complex.ZERO;
  }

  @Override
  public void setComplex(int index, Complex complex) {
    setLong(index, complex.longValue());
  }

  @Override
  public Class<?> getNativeType() {
    return Boolean.class;
  }

  @Override
  public Storage copy() {
    return new BooleanStorage(Arrays.copyOf(values, values.length));
  }
}
