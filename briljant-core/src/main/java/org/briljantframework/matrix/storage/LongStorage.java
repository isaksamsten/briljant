package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.AbstractStorage;

import java.util.Arrays;

/**
 * Created by isak on 1/30/15.
 */
public class LongStorage extends AbstractStorage {

  private final long[] values;

  public LongStorage(long[] values) {
    super(values.length);
    this.values = values;
  }

  public static LongStorage withSize(int size) {
    return new LongStorage(new long[size]);
  }

  @Override
  public int getInt(int index) {
    return (int) getLong(index);
  }

  @Override
  public void setInt(int index, int value) {
    setLong(index, value);
  }

  @Override
  public long getLong(int index) {
    return values[index];
  }

  @Override
  public void setLong(int index, long value) {
    values[index] = value;
  }

  @Override
  public double getDouble(int index) {
    return getLong(index);
  }

  @Override
  public void setDouble(int index, double value) {
    setLong(index, (long) value);
  }

  @Override
  public Complex getComplex(int index) {
    return Complex.valueOf(getDouble(index));
  }

  @Override
  public void setComplex(int index, Complex complex) {
    setLong(index, complex.longValue());
  }

  @Override
  public Class<?> getNativeType() {
    return Long.TYPE;
  }

  @Override
  public Storage copy() {
    return new LongStorage(Arrays.copyOf(values, values.length));
  }
}
