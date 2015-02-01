package org.briljantframework.matrix.storage;

import java.util.Arrays;

import org.briljantframework.complex.Complex;

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
  public int getAsInt(int index) {
    return (int) getAsLong(index);
  }

  @Override
  public void setInt(int index, int value) {
    setLong(index, value);
  }

  @Override
  public long getAsLong(int index) {
    return values[arrayIndex(index)];
  }

  @Override
  public void setLong(int index, long value) {
    values[arrayIndex(index)] = value;
  }

  @Override
  public double getAsDouble(int index) {
    return getAsLong(index);
  }

  @Override
  public void setDouble(int index, double value) {
    setLong(index, (long) value);
  }

  @Override
  public Complex getComplex(int index) {
    return Complex.valueOf(getAsDouble(index));
  }

  @Override
  public void setComplex(int index, Complex complex) {
    setLong(index, complex.longValue());
  }

  @Override
  public void setNumber(int index, Number value) {
    setLong(index, value.longValue());
  }

  @Override
  public Number getNumber(int index) {
    return getAsLong(index);
  }

  @Override
  public boolean isArrayBased() {
    return true;
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
