package org.briljantframework.matrix.storage;

import org.briljantframework.complex.Complex;

/**
 * Represent a mutable storage unit. For example, an {@code Array}, {@link java.util.List} or memory
 * mapped file.
 * 
 * Values of different types coerces without exceptions, but precision might be lost.
 * 
 * To support large storage containers, indexes are {@code long}. In theory this might impact
 * performance slightly, however, in practice this is rarely a problem.
 * 
 * @author Isak Karlsson
 */
public interface Storage {

  boolean getBoolean(int index);

  void setBoolean(int index, boolean value);

  int getAsInt(int index);

  void setInt(int index, int value);

  long getAsLong(int index);

  void setLong(int index, long value);

  double getAsDouble(int index);

  void setDouble(int index, double value);

  Complex getComplex(int index);

  void setComplex(int index, Complex complex);

  void setNumber(int index, Number value);

  Number getNumber(int index);

  boolean[] asBooleanArray();

  int[] asIntArray();

  long[] asLongArray();

  double[] asDoubleArray();

  Complex[] asComplexArray();

  boolean isArrayBased();

  Class<?> getNativeType();

  Storage copy();

  int size();
}
