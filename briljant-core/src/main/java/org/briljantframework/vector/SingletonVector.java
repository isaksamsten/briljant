package org.briljantframework.vector;

import com.google.common.base.Preconditions;

import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.IllegalTypeException;
import org.briljantframework.matrix.Array;

/**
 * @author Isak Karlsson
 */
class SingletonVector extends AbstractVector {

  private final Class<?> cls;
  private final Object value;
  private final VectorType type;
  private final int size;

  SingletonVector(Object value, int size) {
    this.cls = value != null ? value.getClass() : Object.class;
    this.value = value;
    this.size = size;
    type = Vec.typeOf(cls);
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    checkElementIndex(index);
    Object obj = value;
    if (Is.NA(obj)) {
      return Na.of(cls);
    }
    if (!cls.isInstance(obj)) {
      if (cls.equals(String.class)) {
        return cls.cast(obj.toString());
      } else {
        if (this.cls.equals(Number.class)) {
          Number num = Number.class.cast(obj);
          if (cls.equals(Double.class)) {
            return cls.cast(num.doubleValue());
          } else if (cls.equals(Integer.class)) {
            return cls.cast(num.intValue());
          }
        }
      }
      return Na.of(cls);
    }
    return cls.cast(obj);
  }

  private void checkElementIndex(int index) {
    Preconditions.checkElementIndex(index, size());
  }

  @Override
  public String toString(int index) {
    checkElementIndex(index);
    return value != null ? value.toString() : "NA";
  }

  @Override
  public boolean isNA(int index) {
    checkElementIndex(index);
    return value == null;
  }

  @Override
  public double getAsDouble(int index) {
    checkElementIndex(index);
    return value instanceof Number ? ((Number) value).doubleValue() : DoubleVector.NA;
  }

  @Override
  public int getAsInt(int index) {
    checkElementIndex(index);
    return value instanceof Number ? ((Number) value).intValue() : IntVector.NA;
  }

  @Override
  public Bit getAsBit(int index) {
    checkElementIndex(index);
    return value instanceof Number ? Bit.valueOf(((Number) value).intValue())
                                   : value instanceof Bit ? (Bit) value :
                                     value instanceof Boolean ? Bit.valueOf((boolean) value) :
                                     Bit.NA;
  }

  @Override
  public Complex getAsComplex(int index) {
    checkElementIndex(index);
    return value instanceof Complex ? (Complex) value :
           value instanceof Number ? Complex.valueOf(((Number) value).doubleValue())
                                   : Na.of(Complex.class);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public VectorType getType() {
    return type;
  }

  @Override
  public Array toMatrix() throws IllegalTypeException {
    throw new IllegalTypeException("not compatible");
  }

  @Override
  public int compare(int a, int b) {
    return getType().compare(a, this, b, this);
  }

  @Override
  public int compare(int a, Vector other, int b) {
    return getType().compare(a, this, b, other);
  }
}
