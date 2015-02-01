package org.briljantframework.matrix.storage;

import java.util.Arrays;

import org.briljantframework.complex.Complex;

/**
 * Created by isak on 1/30/15.
 */
public class BooleanStorage extends AbstractStorage {
  private final boolean[] values;

  public BooleanStorage(boolean[] values) {
    super(values.length);
    this.values = values;
  }

  public static BooleanStorage withSize(int size) {
    return new BooleanStorage(new boolean[size]);
  }

  @Override
  public boolean getBoolean(int index) {
    return values[arrayIndex(index)];
  }

  @Override
  public int getAsInt(int index) {
    return values[arrayIndex(index)] ? 1 : 0;
  }

  @Override
  public void setInt(int index, int value) {
    values[arrayIndex(index)] = value == 1;
  }

  @Override
  public long getAsLong(int index) {
    return getAsInt(index);
  }

  @Override
  public void setLong(int index, long value) {
    values[arrayIndex(index)] = value == 1;
  }

  @Override
  public double getAsDouble(int index) {
    return getAsInt(index);
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
  public void setNumber(int index, Number value) {
    setInt(index, value.intValue());
  }

  @Override
  public Number getNumber(int index) {
    return getAsInt(index);
  }

  @Override
  public boolean isArrayBased() {
    return true;
  }

  @Override
  public boolean[] asBooleanArray() {
    return values;
  }

  @Override
  public Class<?> getNativeType() {
    return Boolean.TYPE;
  }

  @Override
  public Storage copy() {
    return new BooleanStorage(Arrays.copyOf(values, values.length));
  }
}
