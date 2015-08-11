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

package org.briljantframework.vector;

import org.apache.commons.math3.complex.Complex;

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
    if (index < 0 || index > size()) {
      throw new IndexOutOfBoundsException();
    }
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
  public int compare(int a, int b) {
    return getType().compare(a, this, b, this);
  }

  @Override
  public int compare(int a, Vector other, int b) {
    return getType().compare(a, this, b, other);
  }
}
