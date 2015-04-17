package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.AbstractStorage;

import java.util.Arrays;

/**
 * @author Isak Karlsson
 */
public class DoubleStorage extends AbstractStorage implements DoubleArrayStorage {

  private final double[] values;

  public DoubleStorage(double[] values) {
    super(values.length);
    this.values = values;
  }

  public DoubleStorage(int size) {
    super(size);
    this.values = new double[size];
  }

  @Override
  public int getInt(int index) {
    return (int) getDouble(index);
  }

  @Override
  public void setInt(int index, int value) {
    setDouble(index, value);
  }

  @Override
  public long getLong(int index) {
    return (long) getDouble(index);
  }

  @Override
  public void setLong(int index, long value) {
    setDouble(index, value);
  }

  @Override
  public double getDouble(int index) {
    return values[index];
  }

  @Override
  public void setDouble(int index, double value) {
    values[index] = value;
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
  public double[] doubleArray() {
    return values;
  }

  @Override
  public boolean isArrayBased() {
    return true;
  }

  @Override
  public Class<?> getNativeType() {
    return Double.TYPE;
  }

  @Override
  public Storage copy() {
    return new DoubleStorage(Arrays.copyOf(values, values.length));
  }
}
