package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;

import com.carrotsearch.hppc.IntDoubleMap;
import com.carrotsearch.hppc.IntDoubleOpenHashMap;

/**
 * <p>
 * A sparse matrix implemented using rather efficient hash maps. Although the performance is rather
 * fine in many cases, the {@link org.briljantframework.matrix.storage.DoubleStorage} is several
 * order of magnitudes faster, especially for complex operations such as matrix-matrix
 * multiplication (
 * {@link org.briljantframework.matrix.DoubleMatrix#mmul(org.briljantframework.matrix.DoubleMatrix)}
 * ) and should hence be preferred for all cases except when the number of non-zero elements is
 * <b>very</b> small and the size of the matrix is <b>very</b> large.
 * </p>
 *
 * <p>
 * For this reason, most (all) operations defined in {@link org.briljantframework.matrix.Matrices}
 * return a matrix with a {@link org.briljantframework.matrix.storage.DoubleStorage} if it does not
 * return the type of the receiver.
 * </p>
 *
 * <p>
 * To put the performance differences into perspective, multiplying a {@code 100 x 5000} matrix with
 * a {@code 5000 x 100} matrix takes {@code 5} ms using {@code ArrayMatrix} and {@code 10202} ms,
 * i.e. the {@code HashMatrix} is almost 2000 times slower.
 * </p>
 * 
 * @author Isak Karlsson
 */
public class DoubleHashStorage extends AbstractStorage {
  private final IntDoubleMap values;
  private final double defaultValue;

  public DoubleHashStorage(int size, double defaultValue) {
    this(size, new IntDoubleOpenHashMap(), defaultValue);
  }

  public DoubleHashStorage(int size) {
    this(size, 0);
  }

  public DoubleHashStorage(int size, IntDoubleMap values, double defaultValue) {
    super(size);
    this.values = values;
    this.defaultValue = defaultValue;
  }

  @Override
  public int getAsInt(int index) {
    return (int) getAsDouble(index);
  }

  @Override
  public void setInt(int index, int value) {
    setDouble(index, value);
  }

  @Override
  public long getAsLong(int index) {
    return (long) getAsDouble(index);
  }

  @Override
  public void setLong(int index, long value) {
    setDouble(index, value);
  }

  @Override
  public double getAsDouble(int index) {
    return values.getOrDefault(index, defaultValue);
  }

  @Override
  public void setDouble(int index, double value) {
    values.put(index, value);
  }

  @Override
  public Complex getComplex(int index) {
    return Complex.valueOf(getAsDouble(index));
  }

  @Override
  public void setComplex(int index, Complex complex) {
    setDouble(index, complex.doubleValue());
  }

  @Override
  public void setNumber(int index, Number value) {
    setDouble(index, value.doubleValue());
  }

  @Override
  public Number getNumber(int index) {
    return getAsDouble(index);
  }

  @Override
  public boolean isArrayBased() {
    return false;
  }

  @Override
  public Class<?> getNativeType() {
    return Double.TYPE;
  }

  @Override
  public Storage copy() {
    return new DoubleHashStorage(size(), new IntDoubleOpenHashMap(values), defaultValue);
  }
}
