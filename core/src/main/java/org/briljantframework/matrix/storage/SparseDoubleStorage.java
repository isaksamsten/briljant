package org.briljantframework.matrix.storage;

import com.carrotsearch.hppc.IntDoubleMap;
import com.carrotsearch.hppc.IntDoubleOpenHashMap;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.AbstractStorage;

/**
 * <p>
 * A sparse double storage is implemented using rather efficient hash maps. Although the
 * performance
 * is rather fine in many cases, the {@link DoubleStorage} is
 * several order of magnitudes faster, especially for complex operations such as matrix-matrix
 * multiplication (
 * {@link org.briljantframework.matrix.DoubleMatrix#mmul(org.briljantframework.matrix.DoubleMatrix)}
 * ) and should hence be preferred for all cases except when the number of non-zero elements is
 * <b>very</b> small and the size of the matrix is <b>very</b> large.
 * </p>
 *
 * <p>
 * For this reason, most (all) operations defined in {@link org.briljantframework.matrix.Matrices}
 * return a matrix with a {@link DoubleStorage} if it does not
 * return the type of the receiver.
 * </p>
 *
 * <p>
 * To put the performance differences into perspective, multiplying a {@code 100 x 5000} matrix
 * with
 * a {@code 5000 x 100} matrix takes {@code 5} ms using {@code ArrayMatrix} and {@code 10202} ms,
 * i.e. the {@code HashMatrix} is almost 2000 times slower.
 * </p>
 *
 * @author Isak Karlsson
 */
public class SparseDoubleStorage extends AbstractStorage {

  private final IntDoubleMap values;
  private final double defaultValue;

  public SparseDoubleStorage(int size, double defaultValue) {
    this(size, new IntDoubleOpenHashMap(), defaultValue);
  }

  public SparseDoubleStorage(int size) {
    this(size, 0);
  }

  public SparseDoubleStorage(int size, IntDoubleMap values, double defaultValue) {
    super(size);
    this.values = values;
    this.defaultValue = defaultValue;
  }

  public SparseDoubleStorage withSize(int size) {
    return new SparseDoubleStorage(size, 0);
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
    return values.getOrDefault(index, defaultValue);
  }

  @Override
  public void setDouble(int index, double value) {
    values.put(index, value);
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
  public Class<?> getNativeType() {
    return Double.TYPE;
  }

  @Override
  public Storage copy() {
    return new SparseDoubleStorage(size(), new IntDoubleOpenHashMap(values), defaultValue);
  }
}
