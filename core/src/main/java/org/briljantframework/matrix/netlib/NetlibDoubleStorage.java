package org.briljantframework.matrix.netlib;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.AbstractStorage;
import org.briljantframework.matrix.Storage;

/**
 * Created by isak on 14/04/15.
 */
class NetlibDoubleStorage extends AbstractStorage {

  private final double[] values;

  NetlibDoubleStorage(double[] values) {
    super(values.length);
    this.values = values;
  }

  NetlibDoubleStorage(int size) {
    super(size);
    this.values = new double[size];
  }

  @Override
  public int getInt(int index) {
    return (int) values[index];
  }

  @Override
  public void setInt(int index, int value) {
    values[index] = value;
  }

  @Override
  public long getLong(int index) {
    return (long) values[index];
  }

  @Override
  public void setLong(int index, long value) {
    values[index] = value;
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
    values[index] = complex.doubleValue();
  }

  @Override
  public Class<?> getNativeType() {
    return Double.TYPE;
  }

  @Override
  public Storage copy() {
    return new NetlibDoubleStorage(values.clone());
  }

  public double[] doubleArray() {
    return values;
  }
}
