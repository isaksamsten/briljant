package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.AbstractStorage;

import java.util.Arrays;

/**
 * Created by isak on 1/30/15.
 */
public class ComplexStorage extends AbstractStorage {

  private final Complex[] values;

  public ComplexStorage(Complex[] values) {
    super(values.length);
    this.values = values;
  }

  public ComplexStorage(int size) {
    super(size);
    this.values = new Complex[size];
  }

  @Override
  public int getInt(int index) {
    return getComplex(index).intValue();
  }

  @Override
  public void setInt(int index, int value) {
    setDouble(index, value);
  }

  @Override
  public long getLong(int index) {
    return getComplex(index).longValue();
  }

  @Override
  public void setLong(int index, long value) {
    setDouble(index, value);
  }

  @Override
  public double getDouble(int index) {
    return getComplex(index).real();
  }

  @Override
  public void setDouble(int index, double value) {
    setComplex(index, Complex.valueOf(value));
  }

  @Override
  public Complex getComplex(int index) {
    return values[index];
  }

  @Override
  public void setComplex(int index, Complex complex) {
    values[index] = complex;
  }

  @Override
  public Class<?> getNativeType() {
    return Complex.class;
  }

  @Override
  public Storage copy() {
    return new ComplexStorage(Arrays.copyOf(values, values.length));
  }
}
