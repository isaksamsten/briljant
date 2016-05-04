/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.series;

import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.data.Logical;

/**
 * Provides information of a particular vectors type.
 * 
 * @author Isak Karlsson
 */
public abstract class Type {

  public static final Type STRING = new GenericType(String.class);
  public static final Type LOGICAL = new GenericType(Logical.class);
  public static final Type INT = new IntType();
  public static final Type LONG = new GenericType(Long.class);
  public static final Type COMPLEX = new GenericType(Complex.class);
  public static final Type DOUBLE = new DoubleType();
  public static final Type OBJECT = new GenericType(Object.class);

  private static final Map<Class<?>, Type> CLASS_TO_TYPE;

  static {
    CLASS_TO_TYPE = new IdentityHashMap<>();
    CLASS_TO_TYPE.put(Long.class, LONG);
    CLASS_TO_TYPE.put(Long.TYPE, LONG);
    CLASS_TO_TYPE.put(Integer.class, INT);
    CLASS_TO_TYPE.put(Integer.TYPE, INT);
    CLASS_TO_TYPE.put(Short.class, INT);
    CLASS_TO_TYPE.put(Short.TYPE, INT);
    CLASS_TO_TYPE.put(Byte.class, INT);
    CLASS_TO_TYPE.put(Byte.TYPE, INT);
    CLASS_TO_TYPE.put(Double.class, DOUBLE);
    CLASS_TO_TYPE.put(Double.TYPE, DOUBLE);
    CLASS_TO_TYPE.put(Float.class, DOUBLE);
    CLASS_TO_TYPE.put(Float.TYPE, DOUBLE);
    CLASS_TO_TYPE.put(String.class, STRING);
    CLASS_TO_TYPE.put(Boolean.class, LOGICAL);
    CLASS_TO_TYPE.put(Logical.class, LOGICAL);
    CLASS_TO_TYPE.put(Complex.class, COMPLEX);
    CLASS_TO_TYPE.put(Object.class, OBJECT);
  }

  /**
   * Return a new type from the specified class using the class of the specified value. Returns
   * {@link #OBJECT} if {@code null} is specified.
   *
   * @param value the value
   * @return a new type
   */
  public static Type of(Object value) {
    if (value != null) {
      return of(value.getClass());
    } else {
      return OBJECT;
    }
  }

  /**
   * Create a new type from the specified class
   *
   * @param cls the specified class
   * @return a type
   */
  public static Type of(Class<?> cls) {
    if (cls == null) {
      return OBJECT;
    } else {
      Type type = CLASS_TO_TYPE.get(cls);
      if (type == null) {
        return new GenericType(cls);
      }
      return type;
    }
  }

  /**
   * Creates a new builder able to build vectors of this type
   *
   * @return a new builder
   */
  public abstract Series.Builder newBuilder();

  /**
   * Creates a new builder with the specified initial capacity
   *
   * @param capacity the initial capacity
   * @return a new builder with the specified initial capacity
   */
  public abstract Series.Builder newBuilderWithCapacity(int capacity);

  /**
   * Copy (and perhaps convert) {@code series} to this type
   *
   * @param series the series to copy
   * @return a new series
   */
  public Series copy(Series series) {
    return newBuilder(series.size()).addAll(series).build();
  }

  /**
   * Creates a new builder able to build vectors of this type
   *
   * @param size initial size (the series is padded with NA)
   * @return a new builder
   */
  public abstract Series.Builder newBuilder(int size);

  /**
   * Returns true if the specified type is assignable to the current type.
   * 
   * @param type the type
   * @return true if assignable
   */
  public boolean isAssignableTo(Type type) {
    return isAssignableTo(type.getDataClass());
  }

  /**
   * Returns true if the specified class is assignable to the current type.
   * 
   * @param cls the class
   * @return true if assignable
   */
  public boolean isAssignableTo(Class<?> cls) {
    return cls.isAssignableFrom(getDataClass());
  }

  /**
   * Get the underlying class used to represent values of this series type
   *
   * @return the class
   */
  public abstract Class<?> getDataClass();

  private static class DoubleType extends Type {

    @Override
    public DoubleSeries.Builder newBuilder() {
      return new DoubleSeries.Builder();
    }

    @Override
    public DoubleSeries.Builder newBuilder(int size) {
      return new DoubleSeries.Builder(size);
    }

    @Override
    public Class<?> getDataClass() {
      return Double.class;
    }

    @Override
    public Series.Builder newBuilderWithCapacity(int capacity) {
      return new DoubleSeries.Builder(0, capacity);
    }

    @Override
    public String toString() {
      return "double";
    }
  }

  private static class IntType extends Type {

    @Override
    public IntSeries.Builder newBuilder() {
      return new IntSeries.Builder();
    }

    @Override
    public IntSeries.Builder newBuilder(int size) {
      return new IntSeries.Builder(size, size);
    }

    @Override
    public Class<?> getDataClass() {
      return Integer.class;
    }

    @Override
    public Series.Builder newBuilderWithCapacity(int capacity) {
      return new IntSeries.Builder(0, capacity);
    }

    @Override
    public String toString() {
      return "int";
    }
  }

  /**
   * @author Isak Karlsson
   */
  private static final class GenericType extends Type {
    private Class<?> cls;

    private GenericType(Class<?> cls) {
      this.cls = cls;
    }

    @Override
    public int hashCode() {
      return cls.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof GenericType && ((GenericType) obj).cls.equals(cls);
    }

    @Override
    public Series.Builder newBuilder() {
      return new ObjectSeries.Builder(cls);
    }

    @Override
    public Series.Builder newBuilder(int size) {
      return new ObjectSeries.Builder(cls, size);
    }

    @Override
    public Class<?> getDataClass() {
      return cls;
    }

    @Override
    public Series.Builder newBuilderWithCapacity(int capacity) {
      return new ObjectSeries.Builder(cls);
    }

    @Override
    public String toString() {
      return String.format("%s", cls.getSimpleName());
    }
  }
}
