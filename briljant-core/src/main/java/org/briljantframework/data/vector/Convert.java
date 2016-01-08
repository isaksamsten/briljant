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
package org.briljantframework.data.vector;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.data.Is;
import org.briljantframework.data.Logical;
import org.briljantframework.data.Na;

/**
 * Utilities for converting values between different types.
 * 
 * @author Isak Karlsson
 */
public final class Convert {

  private Convert() {}

  /**
   * Convert the specified value to an instance of the specified class. If {@link Is#NA(Object)}
   * return {@code true} for the specified value or the value cannot be converted into the specified
   * class, {@code NA} of the specified type is returned.
   *
   * <p/>
   * The following conversions are available:
   * <ul>
   * <li>Any primitive of reference value can be converted into a {@link String} (the values
   * {@link Object#toString()} method is used)</li>
   * <li>Any {@link Number number} in {@code java.lang} can be converted into any other other
   * {@link Number number} in {@code java.lang}</li>
   * <li>Any {@link Number number} in {@code java.lang} can be converted into {@link Complex}</li>
   * <li>Any {@link Number number} in {@code java.lang} can be converted into {@link Logical}</li>
   * </ul>
   *
   * <p/>
   * This method is guaranteed to not throw {@link ClassCastException} but instead returns
   * {@code NA}.
   *
   * <p/>
   * Read {@link Na#of(Class)} for information on how to deal with {@code NA} values
   *
   * @param cls the class to convert <em>to</em>
   * @param value the value to convert into the specified class
   * @param <T> the type of the return value
   * @return an instance of the specified class
   * @throws NullPointerException if the specified class is {@code null}
   */
  public static <T> T to(Class<T> cls, Object value) {
    if (!cls.isInstance(value)) {
      if (Is.NA(value)) {
        return Na.of(cls);
      } else if (cls.equals(String.class)) {
        return cls.cast(value.toString());
      } else if (value instanceof Logical) {
        return convertLogical(cls, (Logical) value);
      } else if (value instanceof Number) {
        return convertNumber(cls, (Number) value);
      } else if (value instanceof Complex) {
        return convertNumber(cls, ((Complex) value).getReal());
      } else if (value instanceof Boolean) {
        return convertNumber(cls, (boolean) value ? 1 : 0);
      } else {
        return Na.of(cls);
      }
    } else {
      return cls.cast(value);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> T convertNumber(Class<T> cls, Number num) {
    if (cls.equals(Double.class) || cls.equals(Double.TYPE)) {
      return (T) (Double) num.doubleValue();
    } else if (cls.equals(Float.class) || cls.equals(Float.TYPE)) {
      return (T) (Float) num.floatValue();
    } else if (cls.equals(Long.class) || cls.equals(Long.TYPE)) {
      return (T) (Long) num.longValue();
    } else if (cls.equals(Integer.class) || cls.equals(Integer.TYPE)) {
      return (T) (Integer) num.intValue();
    } else if (cls.equals(Short.class) || cls.equals(Short.TYPE)) {
      return (T) (Short) num.shortValue();
    } else if (cls.equals(Byte.class) || cls.equals(Byte.TYPE)) {
      return (T) (Byte) num.byteValue();
    } else if (Complex.class.equals(cls)) {
      return cls.cast(Complex.valueOf(num.doubleValue()));
    } else if (Logical.class.equals(cls)) {
      return cls.cast(num.intValue() == 1 ? Logical.TRUE : Logical.FALSE);
    } else if (Boolean.class.equals(cls)) {
      return cls.cast(num.intValue() == 1);
    } else {
      return Na.of(cls);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> T convertLogical(Class<T> cls, Logical log) {
    if (cls.equals(Double.class) || cls.equals(Double.TYPE)) {
      return (T) (Double) (log == Logical.TRUE ? 1.0 : 0.0);
    } else if (cls.equals(Float.class) || cls.equals(Float.TYPE)) {
      return (T) (Float) (log == Logical.TRUE ? 1.0f : 0.0f);
    } else if (cls.equals(Long.class) || cls.equals(Long.TYPE)) {
      return (T) (Long) (log == Logical.TRUE ? 1l : 0l);
    } else if (cls.equals(Integer.class) || cls.equals(Integer.TYPE)) {
      return (T) (Integer) (log == Logical.TRUE ? 1 : 0);
    } else if (cls.equals(Short.class) || cls.equals(Short.TYPE)) {
      return (T) (Short) (log == Logical.TRUE ? (short) 1 : (short) 0);
    } else if (cls.equals(Byte.class) || cls.equals(Byte.TYPE)) {
      return (T) (Byte) (log == Logical.TRUE ? (byte) 1 : (byte) 0);
    } else if (Complex.class.equals(cls)) {
      return cls.cast(log == Logical.TRUE ? Complex.ONE : Complex.ZERO);
    } else if (Boolean.class.equals(cls)) {
      return cls.cast(log == Logical.TRUE);
    } else {
      return Na.of(cls);
    }
  }

}
