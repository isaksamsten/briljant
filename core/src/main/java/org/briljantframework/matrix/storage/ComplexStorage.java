package org.briljantframework.matrix.storage;

import java.util.Arrays;

import org.briljantframework.complex.Complex;

/**
 * Created by isak on 1/30/15.
 */
public class ComplexStorage extends AbstractStorage {

  private final Complex[] values;

  public ComplexStorage(Complex[] values) {
    super(values.length);
    this.values = values;
  }

  public ComplexStorage withSize(int size) {
    return new ComplexStorage(new Complex[size]);
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
    return values[(int) index];
  }

  @Override
  public void setComplex(int index, Complex complex) {
    values[(int) index] = complex;
  }

  @Override
  public boolean isArrayBased() {
    return true;
  }

  @Override
  public Complex[] asComplexArray() {
    return values;
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
