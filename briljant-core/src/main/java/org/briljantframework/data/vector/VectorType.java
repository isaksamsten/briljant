/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.data.vector;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.data.Is;
import org.briljantframework.data.Logical;
import org.briljantframework.data.Na;
import org.briljantframework.data.Scale;
import org.briljantframework.data.index.ObjectComparator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides information of a particular vectors type.
 */
public abstract class VectorType {

  public static final VectorType STRING = new GenericVectorType(String.class);
  public static final VectorType LOGICAL = new GenericVectorType(Logical.class);
  public static final VectorType INT = new IntVectorType();
  public static final VectorType COMPLEX = new GenericVectorType(Complex.class);
  public static final VectorType DOUBLE = new DoubleVectorType();
  public static final VectorType OBJECT = new GenericVectorType(Object.class);

  private static final Map<Class<?>, VectorType> CLASS_TO_VECTOR_TYPE;
  private static final Set<VectorType> NUMERIC = new HashSet<>();
  private static final Set<VectorType> CATEGORIC = new HashSet<>();

  static {
    NUMERIC.add(VectorType.DOUBLE);
    NUMERIC.add(VectorType.INT);
    NUMERIC.add(VectorType.COMPLEX);

    CATEGORIC.add(VectorType.STRING);
    CATEGORIC.add(VectorType.LOGICAL);

    CLASS_TO_VECTOR_TYPE = new IdentityHashMap<>();
    CLASS_TO_VECTOR_TYPE.put(Integer.class, INT);
    CLASS_TO_VECTOR_TYPE.put(Integer.TYPE, INT);
    CLASS_TO_VECTOR_TYPE.put(Double.class, DOUBLE);
    CLASS_TO_VECTOR_TYPE.put(Double.TYPE, DOUBLE);
    CLASS_TO_VECTOR_TYPE.put(String.class, STRING);
    CLASS_TO_VECTOR_TYPE.put(Boolean.class, LOGICAL);
    CLASS_TO_VECTOR_TYPE.put(Logical.class, LOGICAL);
    CLASS_TO_VECTOR_TYPE.put(Complex.class, COMPLEX);
    CLASS_TO_VECTOR_TYPE.put(Object.class, OBJECT);
  }

  public static VectorType of(Class<?> cls) {
    if (cls == null) {
      return OBJECT;
    } else {
      VectorType type = CLASS_TO_VECTOR_TYPE.get(cls);
      if (type == null) {
        return new GenericVectorType(cls);
      }
      return type;
    }
  }

  public static VectorType of(Object object) {
    if (object != null) {
      return of(object.getClass());
    } else {
      return OBJECT;
    }
  }

  /**
   * Creates a new builder able to build vectors of this type
   *
   * @return a new builder
   */
  public abstract Vector.Builder newBuilder();

  /**
   * Creates a new builder able to build vectors of this type
   *
   * @param size initial size (the vector is padded with NA)
   * @return a new builder
   */
  public abstract Vector.Builder newBuilder(int size);

  /**
   * Copy (and perhaps convert) {@code vector} to this type
   *
   * @param vector the vector to copy
   * @return a new vector
   */
  public Vector copy(Vector vector) {
    return newBuilder(vector.size()).addAll(vector).build();
  }

  /**
   * Get the underlying class used to represent values of this vector type
   *
   * @return the class
   */
  public abstract Class<?> getDataClass();

  /**
   * Returns true if this object is NA for this value type
   *
   * @param value the value
   * @return true if value is NA
   */
  public abstract boolean isNA(Object value);

  /**
   * Compare value at position {@code a} from {@code va} to value at position {@code b} from {@code
   * ba}.
   *
   * @param a  the index in va
   * @param va the vector
   * @param b  the index in ba
   * @param ba the vector
   * @return the comparison
   */
  public abstract int compare(int a, Vector va, int b, Vector ba);

  /**
   * @return the scale
   */
  public abstract Scale getScale();

  /**
   * Check if value at position {@code a} from {@code va} and value at position {@code b} from
   * {@code va} are equal.
   *
   * @param a  the index in va
   * @param va the vector
   * @param b  the index in ba
   * @param ba the vector
   * @return true if equal false otherwise
   */
  public boolean equals(int a, Vector va, int b, Vector ba) {
    return compare(a, va, b, ba) == 0;
  }

  public abstract Vector.Builder newBuilderWithCapacity(int capacity);

  private static class DoubleVectorType extends VectorType {

    @Override
    public DoubleVector.Builder newBuilder() {
      return new DoubleVector.Builder();
    }

    @Override
    public DoubleVector.Builder newBuilder(int size) {
      return new DoubleVector.Builder(size);
    }

    @Override
    public Class<?> getDataClass() {
      return Double.class;
    }

    @Override
    public boolean isNA(Object value) {
      return Is.NA(value);
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      double dva = va.loc().getAsDouble(a);
      double dba = ba.loc().getAsDouble(b);

      return !Is.NA(dva) && !Is.NA(dba) ? Double.compare(dva, dba) : 0;
    }

    @Override
    public Scale getScale() {
      return Scale.NUMERICAL;
    }

    @Override
    public Vector.Builder newBuilderWithCapacity(int capacity) {
      return new DoubleVector.Builder(0, capacity);
    }

    @Override
    public String toString() {
      return "double";
    }
  }

  private static class IntVectorType extends VectorType {

    @Override
    public IntVector.Builder newBuilder() {
      return new IntVector.Builder();
    }

    @Override
    public IntVector.Builder newBuilder(int size) {
      return new IntVector.Builder(size, size);
    }

    @Override
    public Class<?> getDataClass() {
      return Integer.class;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null || (value instanceof Integer && (int) value == Na.INT);
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      int x = va.loc().getAsInt(a);
      int y = ba.loc().getAsInt(b);
      boolean aIsNa = Is.NA(x);
      boolean bIsNa = Is.NA(y);
      if (aIsNa && !bIsNa) {
        return -1;
      } else if (!aIsNa && bIsNa) {
        return 1;
      } else {
        return Integer.compare(x, y);
      }
    }

    @Override
    public Scale getScale() {
      return Scale.NUMERICAL;
    }

    @Override
    public Vector.Builder newBuilderWithCapacity(int capacity) {
      return new IntVector.Builder(0, capacity);
    }

    @Override
    public String toString() {
      return "int";
    }
  }

  /**
   * @author Isak Karlsson
   */
  private static class GenericVectorType extends VectorType {

    private final static Comparator<Object> CMP = ObjectComparator.getInstance();
    private Class<?> cls;

    public GenericVectorType(Class<?> cls) {
      this.cls = cls;
    }

    @Override
    public Vector.Builder newBuilder() {
      return new GenericVector.Builder(cls);
    }

    @Override
    public Vector.Builder newBuilder(int size) {
      return new GenericVector.Builder(cls, size);
    }

    @Override
    public Class<?> getDataClass() {
      return cls;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null;
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      Object ca = va.loc().get(Object.class, a);
      Object cb = ba.loc().get(Object.class, b);
      return CMP.compare(ca, cb);
    }

    @Override
    public Scale getScale() {
      return Number.class.isAssignableFrom(cls) ? Scale.NUMERICAL : Scale.NOMINAL;
    }

    @Override
    public Vector.Builder newBuilderWithCapacity(int capacity) {
      return new GenericVector.Builder(cls);
    }

    @Override
    public int hashCode() {
      return cls.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof GenericVectorType && ((GenericVectorType) obj).cls.equals(cls);
    }

    @Override
    public String toString() {
      return String.format("%s", cls.getSimpleName());
    }
  }
}
