package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 1/31/15.
 */
public class VectorStorage extends AbstractStorage {
  public VectorStorage(Vector vector) {
    super(vector.size());
  }

  @Override
  public int getInt(int index) {
    return 0;
  }

  @Override
  public void setInt(int index, int value) {

  }

  @Override
  public long getLong(int index) {
    return 0;
  }

  @Override
  public void setLong(int index, long value) {

  }

  @Override
  public double getDouble(int index) {
    return 0;
  }

  @Override
  public void setDouble(int index, double value) {

  }

  @Override
  public Complex getComplex(int index) {
    return null;
  }

  @Override
  public void setComplex(int index, Complex complex) {

  }

  @Override
  public boolean isArrayBased() {
    return false;
  }

  @Override
  public Class<?> getNativeType() {
    return null;
  }

  @Override
  public Storage copy() {
    return null;
  }
}
