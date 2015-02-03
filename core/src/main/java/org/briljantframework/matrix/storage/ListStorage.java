package org.briljantframework.matrix.storage;

import java.util.ArrayList;
import java.util.List;

import org.briljantframework.complex.Complex;

/**
 * Created by isak on 2/2/15.
 */
public class ListStorage extends AbstractStorage {
  private final List<Number> values;

  protected ListStorage(List<Number> values) {
    super(values.size());
    this.values = values;
  }

  @Override
  public int getAsInt(int index) {
    return values.get(index).intValue();
  }

  @Override
  public void setInt(int index, int value) {
    values.set(index, value);
  }

  @Override
  public long getAsLong(int index) {
    return values.get(index).longValue();
  }

  @Override
  public void setLong(int index, long value) {
    values.set(index, value);
  }

  @Override
  public double getAsDouble(int index) {
    return values.get(index).doubleValue();
  }

  @Override
  public void setDouble(int index, double value) {
    values.set(index, value);
  }

  @Override
  public Complex getComplex(int index) {
    Number value = values.get(index);
    return value instanceof Complex ? (Complex) value : Complex.valueOf(value.doubleValue());
  }

  @Override
  public void setComplex(int index, Complex complex) {
    values.set(index, complex);
  }

  @Override
  public void setNumber(int index, Number value) {
    values.set(index, value);
  }

  @Override
  public Number getNumber(int index) {
    return values.get(index);
  }

  @Override
  public boolean isArrayBased() {
    return false;
  }

  @Override
  public Class<?> getNativeType() {
    return Number.class;
  }

  @Override
  public Storage copy() {
    return new ListStorage(new ArrayList<>(values));
  }
}
