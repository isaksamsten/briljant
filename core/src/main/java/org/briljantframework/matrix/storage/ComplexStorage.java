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
  public int getAsInt(int index) {
    return getComplex(index).intValue();
  }

  @Override
  public void setInt(int index, int value) {
    setDouble(index, value);
  }

  @Override
  public long getAsLong(int index) {
    return getComplex(index).longValue();
  }

  @Override
  public void setLong(int index, long value) {
    setDouble(index, value);
  }

  @Override
  public double getAsDouble(int index) {
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
  public void setNumber(int index, Number value) {
    if (value instanceof Complex) {
      setComplex(index, (Complex) value);
    } else {
      setDouble(index, value.doubleValue());
    }
  }

  @Override
  public Number getNumber(int index) {
    return getComplex(index);
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
