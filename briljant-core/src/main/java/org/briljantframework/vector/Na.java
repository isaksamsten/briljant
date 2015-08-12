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

  public static final Integer BOXED_INT_NA = IntVector.NA;
  public static final Double BOXED_DOUBLE_NA = DoubleVector.NA;
  public static final Long BOXED_LONG_NA = Long.MAX_VALUE;

  private Na() {
  }

  /**
   * Returns the {@code NA} value for the class {@code T}. For reference types (excluding {@link
   * Complex} and {@link Bit}) {@code NA} is represented as {@code null}, but for primitive types a
   * special convention is used.
   *
   * <ul>
   * <li>{@code double}: {@link org.briljantframework.vector.DoubleVector#NA}</li>
   * <li>{@code int}: {@link IntVector#NA}</li>
   * <li>{@code long}: {@link Long#MAX_VALUE}</li>
   * <li>{@link Bit}: {@link Bit#NA}</li>
   * <li>{@link Complex}: {@link ComplexVector#NA}</li>
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
    } else if (Bit.class.equals(cls)) {
      return (T) Bit.NA;
    } else if (Complex.class.equals(cls)) {
      return (T) ComplexVector.NA;
    } else {
      return null;
    }
  }
}
