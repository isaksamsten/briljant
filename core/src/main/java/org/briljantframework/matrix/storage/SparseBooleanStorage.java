package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.AbstractStorage;
import org.briljantframework.matrix.Storage;

import java.util.BitSet;

/**
 * @author Isak Karlsson
 */
public class SparseBooleanStorage extends AbstractStorage {

  private final BitSet values;

  public SparseBooleanStorage(int size) {
    super(size);
    this.values = new BitSet(size);
  }

  public SparseBooleanStorage(int size, BitSet values) {
    super(size);
    this.values = values;
  }

  @Override
  public int getInt(int index) {
    return getBoolean(index) ? 1 : 0;
  }

  @Override
  public void setInt(int index, int value) {
    setBoolean(index, value == 1);
  }

  @Override
  public long getLong(int index) {
    return getInt(index);
  }

  @Override
  public void setLong(int index, long value) {
    setBoolean(index, value == 1);
  }

  @Override
  public double getDouble(int index) {
    return getInt(index);
  }

  @Override
  public void setDouble(int index, double value) {
    setBoolean(index, value == 1);
  }

  @Override
  public Complex getComplex(int index) {
    return getBoolean(index) ? Complex.ONE : Complex.ZERO;
  }

  @Override
  public void setComplex(int index, Complex complex) {
    setBoolean(index, Complex.ONE.equals(complex));
  }

//  @Override
//  public boolean isArrayBased() {
//    return false;
//  }

  @Override
  public Class<?> getNativeType() {
    return Boolean.TYPE;
  }

  @Override
  public Storage copy() {
    return new SparseBooleanStorage(size(), (BitSet) values.clone());
  }
}
