package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.ImmutableModificationException;
import org.briljantframework.matrix.AbstractStorage;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 1/31/15.
 */
public class VectorStorage extends AbstractStorage {
  private final Vector vector;

  public VectorStorage(Vector vector) {
    super(vector.size());
    this.vector = vector;
  }

  @Override
  public int getInt(int index) {
    return vector.getAsInt(index);
  }

  @Override
  public void setInt(int index, int value) {
    throw new ImmutableModificationException();
  }

  @Override
  public long getLong(int index) {
    return vector.getAsInt(index);
  }

  @Override
  public void setLong(int index, long value) {
    throw new ImmutableModificationException();

  }

  @Override
  public double getDouble(int index) {
    return vector.getAsDouble(index);
  }

  @Override
  public void setDouble(int index, double value) {
    throw new ImmutableModificationException();

  }

  @Override
  public Complex getComplex(int index) {
    return vector.getAsComplex(index);
  }

  @Override
  public void setComplex(int index, Complex complex) {
    throw new ImmutableModificationException();
  }

  @Override
  public Class<?> getNativeType() {
    return Value.class;
  }

  @Override
  public Storage copy() {
    return this; // Safe: this is immutable
  }
}
