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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides information of a particular vectors type.
 */
public abstract class VectorType {

  public static final VectorType STRING = new GenericVectorType(String.class);
  public static final VectorType LOGICAL = new GenericVectorType(Logical.class);
  public static final VectorType INT = IntVector.TYPE;
  public static final VectorType COMPLEX = new GenericVectorType(Complex.class);
  public static final VectorType DOUBLE = DoubleVector.TYPE;
  public static final VectorType OBJECT = new GenericVectorType(Object.class);
  public static final Map<Class<?>, VectorType> CLASS_TO_VECTOR_TYPE;
  public static final Set<VectorType> NUMERIC = new HashSet<>();
  public static final Set<VectorType> CATEGORIC = new HashSet<>(); // TODO: unmodifiable

  static {
    NUMERIC.add(VectorType.DOUBLE);
    NUMERIC.add(VectorType.INT);
    NUMERIC.add(VectorType.COMPLEX);

    CATEGORIC.add(VectorType.STRING);
    CATEGORIC.add(VectorType.LOGICAL);

    CLASS_TO_VECTOR_TYPE = new HashMap<>();
    CLASS_TO_VECTOR_TYPE.put(Integer.class, VectorType.INT);
    CLASS_TO_VECTOR_TYPE.put(Integer.TYPE, VectorType.INT);
    CLASS_TO_VECTOR_TYPE.put(Double.class, VectorType.DOUBLE);
    CLASS_TO_VECTOR_TYPE.put(Double.TYPE, VectorType.DOUBLE);
    CLASS_TO_VECTOR_TYPE.put(String.class, VectorType.STRING);
    CLASS_TO_VECTOR_TYPE.put(Boolean.class, VectorType.LOGICAL);
    CLASS_TO_VECTOR_TYPE.put(Logical.class, VectorType.LOGICAL);
    CLASS_TO_VECTOR_TYPE.put(Complex.class, VectorType.COMPLEX);
    CLASS_TO_VECTOR_TYPE.put(Object.class, VectorType.OBJECT);
  }

  public static VectorType from(Class<?> cls) {
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

  public static VectorType from(Object object) {
    if (object != null) {
      return from(object.getClass());
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

}
