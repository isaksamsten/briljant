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
public final class Na {

  /**
   * The integer NA.
   */
  public static final int INT = Integer.MIN_VALUE;
  private static final Integer BOXED_INT_NA = INT;

  /**
   * The value denoting {@code NA} for a {@code double}. The value lies in
   * a valid IEEE 754 floating point range for {@code NaN} values. Since no floating point
   * operations can distinguish between values in the {@code NaN} range (see {@link
   * Double#longBitsToDouble(long)}, a mask {@link #DOUBLE_NA_MASK} in conjunction with an expected
   * return value {@link #DOUBLE_NA_RES} can be used to find if a particular {@code NaN} value is
   * also
   * {@code NA}. The most straight forward way is
   * <pre>{@code
   *  Double.isNaN(value) && Double.doubleToRawLongBits(value) & NA_MASK == NA_RES
   * }</pre>
   *
   * <p>This implementation is provided by {@link org.briljantframework.vector.Is#NA(double)}.
   *
   * <p>TL;DR; DO NOT {@code v == NA.DOUBLE}. DO {@code Is.NA(v);}
   */
  public static final double DOUBLE = Double.longBitsToDouble(0x7ff0000000000009L);

  /**
   * The mask used in conjunction with {@link #DOUBLE} and and {@link #DOUBLE_NA_RES} to recognize
   * a {@code NA} value from {@link Double#NaN}.
   */
  public static final long DOUBLE_NA_MASK = 0x000000000000000FL;
  public static final int DOUBLE_NA_RES = 9;

  private static final Double BOXED_DOUBLE_NA = DOUBLE;

  private static final Long BOXED_LONG_NA = Long.MAX_VALUE;

  public static final Complex COMPLEX = new Complex(DOUBLE, DOUBLE);

  private Na() {
  }

  /**
   * Returns the {@code NA} value for the class {@code T}. For reference types (excluding {@link
   * Complex} and {@link Logical}) {@code NA} is represented as {@code null}, but for primitive
   * types a
   * special convention is used.
   *
   * <ul>
   * <li>{@code double}: {@link Na#DOUBLE}</li>
   * <li>{@code int}: {@link Na#INT}</li>
   * <li>{@code long}: {@link Long#MAX_VALUE}</li>
   * <li>{@link Logical}: {@link Logical#NA}</li>
   * </ul>
   *
   * @param cls the class
   * @param <T> the type of {@code cls}
   * @return a {@code NA} value of type {@code T}
   */
  @SuppressWarnings("unchecked")
  public static <T> T from(Class<T> cls) {
    if (cls == null) {
      return null;
    } else if (Integer.class.equals(cls) || Integer.TYPE.equals(cls)) {
      return (T) BOXED_INT_NA;
    } else if (Double.class.equals(cls) || Double.TYPE.equals(cls)) {
      return (T) BOXED_DOUBLE_NA;
    } else if (Long.class.equals(cls) || Long.TYPE.equals(cls)) {
      return (T) BOXED_LONG_NA;
    } else if (Logical.class.equals(cls)) {
      return (T) Logical.NA;
    } else if (Complex.class.equals(cls)) {
      return (T) COMPLEX;
    } else {
      return null;
    }
  }
}
