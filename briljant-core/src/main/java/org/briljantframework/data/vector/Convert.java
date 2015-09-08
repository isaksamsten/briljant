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

/**
 * @author Isak Karlsson
 */
public final class Convert {

  private Convert() {
  }

  public static <T> T to(Class<T> cls, Object value) {
    if (!cls.isInstance(value)) {
      if (Is.NA(value)) {
        return Na.of(cls);
      } else if (cls.equals(String.class)) {
        return cls.cast(value.toString());
      } else if (value instanceof Number) {
        Number num = (Number) value;
        if (cls.equals(Double.class)) {
          return cls.cast(num.doubleValue());
        } else if (cls.equals(Integer.class)) {
          return cls.cast(num.intValue());
        } else if (Complex.class.equals(cls)) {
          return cls.cast(Complex.valueOf(num.doubleValue()));
        } else if (Logical.class.equals(cls)) {
          return cls.cast(num.intValue() == 1 ? Logical.TRUE : Logical.FALSE);
        }
      } else if (value instanceof Logical) {
        Logical log = (Logical) value;
        if (cls.equals(Double.class)) {
          return cls.cast(log == Logical.TRUE ? 1.0 : 0.0);
        } else if (cls.equals(Integer.class)) {
          return cls.cast(log == Logical.TRUE ? 1 : 0);
        } else if (Complex.class.equals(cls)) {
          return cls.cast(log == Logical.TRUE ? Complex.ONE : Complex.ZERO);
        }
      } else {
        return Na.of(cls);
      }
    }
    return cls.cast(value);
  }

  /**
   * Convert {@code vector} to a StringVector.
   *
   * @param vector the vector
   * @return a new StringVector
   */
  public static Vector toStringVector(Vector vector) {
    if (vector instanceof GenericVector && vector.getType().getDataClass().equals(String.class)) {
      return vector;
    }
    return new GenericVector.Builder(String.class).addAll(vector).build();
  }
}
