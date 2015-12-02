/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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

package org.briljantframework.data;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.math3.complex.Complex;

/**
 * @author Isak Karlsson
 */
public final class Na {

  /**
   * The value denoting {@code NA} for a {@code double}. The value lies in a valid IEEE 754 floating
   * point range for {@code NaN} values. Since no floating point operations can distinguish between
   * values in the {@code NaN} range (see {@link Double#longBitsToDouble(long)}, a mask
   * {@link #DOUBLE_NA_MASK} in conjunction with an expected return value {@link #DOUBLE_NA_RES} can
   * be used to find if a particular {@code NaN} value is also {@code NA}. The most straight forward
   * way is
   * 
   * <pre>
   * {@code
   *  Double.isNaN(value) && Double.doubleToRawLongBits(value) & NA_MASK == NA_RES
   * }
   * </pre>
   *
   * <p>
   * This implementation is provided by {@link Is#NA(double)}.
   *
   * <p>
   * TL;DR; DO NOT {@code v == NA.DOUBLE}. DO {@code Is.NA(v);}
   */
  public static final double DOUBLE = Double.longBitsToDouble(0x7ff0000000000009L);

  /**
   * The mask used in conjunction with {@link #DOUBLE} and and {@link #DOUBLE_NA_RES} to recognize a
   * {@code NA} value from {@link Double#NaN}.
   */
  public static final long DOUBLE_NA_MASK = 0x000000000000000FL;
  public static final int DOUBLE_NA_RES = 9;

  public static final long LONG = Long.MIN_VALUE;

  public static final int INT = Integer.MIN_VALUE;

  public static final Complex COMPLEX = new Complex(DOUBLE, DOUBLE);

  public static final byte BYTE = Byte.MIN_VALUE;

  public static final short SHORT = Short.MIN_VALUE;

  public static final float FLOAT = Float.intBitsToFloat(0xff800009);
  public static final long FLOAT_NA_MASK = 0x0000000F;
  public static final int FLOAT_NA_RES = 9;

  public static final char CHAR = '\0';

  public static final Integer BOXED_INT = INT;
  public static final Double BOXED_DOUBLE = DOUBLE;
  public static final Long BOXED_LONG = LONG;
  public static final Byte BOXED_BYTE = BYTE;
  public static final Short BOXED_SHORT = SHORT;
  public static final Float BOXED_FLOAT = FLOAT;
  public static final Character BOXED_CHAR = CHAR;

  private Na() {}

  /**
   * Returns a string representation of either NA or the supplied value
   *
   * @param v the value (possible null or NA)
   * @return a string representation
   */
  public static String toString(Object v) {
    return Is.NA(v) ? "NA" : v.toString();
  }

  /**
   * Returns the {@code NA} value for the class {@code T}. For reference types (excluding
   * {@link Complex} and {@link Logical}) {@code NA} is represented as {@code null}, but for
   * primitive types a special convention is used.
   *
   * <ul>
   * <li>{@code double}: {@link Na#DOUBLE}</li>
   * <li>{@code int}: {@link Na#INT}</li>
   * <li>{@code long}: {@link Long#MAX_VALUE}</li>
   * <li>{@link Logical}: {@link Logical#NA}</li>
   * <li>{@link Complex}: {@link Na#COMPLEX}</li>
   * </ul>
   *
   * @param cls the class
   * @param <T> the type of {@code cls}
   * @return a {@code NA} value of type {@code T}
   */
  @SuppressWarnings("unchecked")
  public static <T> T of(Class<T> cls) {
    if (cls == null) {
      return null;
    } else if (Double.class.equals(cls) || Double.TYPE.equals(cls)) {
      return (T) BOXED_DOUBLE;
    } else if (Float.class.equals(cls) || Float.TYPE.equals(cls)) {
      return (T) BOXED_FLOAT;
    } else if (Long.class.equals(cls) || Long.TYPE.equals(cls)) {
      return (T) BOXED_LONG;
    } else if (Integer.class.equals(cls) || Integer.TYPE.equals(cls)) {
      return (T) BOXED_INT;
    } else if (Short.class.equals(cls) || Short.TYPE.equals(cls)) {
      return (T) BOXED_SHORT;
    } else if (Byte.class.equals(cls) || Byte.TYPE.equals(cls)) {
      return (T) BOXED_BYTE;
    } else if (Character.class.equals(cls) || Character.TYPE.equals(cls)) {
      return (T) BOXED_CHAR;
    } else if (Logical.class.equals(cls)) {
      return (T) Logical.NA;
    } else if (Complex.class.equals(cls)) {
      return (T) COMPLEX;
    } else {
      return null;
    }
  }

  /**
   * Only apply the supplied operator to {@code non-NA} values, effectively ensuring that any value
   * delegated to the operator is non-null.
   *
   * <p>
   * Example:
   * 
   * <pre>
   * {
   *   &#064;code
   *   UnaryOperator&lt;String&gt; toUpper = Na.ignore(String::toUpperCase);
   *   toUpper(&quot;hello&quot;); // &quot;HELLO&quot;
   *   toUpper(null); // null (notice, no NullPointerException)
   * }
   * </pre>
   *
   * @param operator the operator
   * @param <T> the type
   * @return a new operator which ignores {@code NA}
   */
  public static <T> Function<T, ?> ignore(Function<? super T, ?> operator) {
    return v -> Is.NA(v) ? v : operator.apply(v);
  }

  /**
   * Returns a {@code BiFunction} that ignores {@code NA} values by returning {@code NA} when either
   * the {@code left} or {@code right} value is {@code NA}.
   *
   * <p>
   * Example:
   * 
   * <pre>
   * {
   *   &#064;code
   *   BiFunction&lt;Integer, Integer, Integer&gt; adder = Na.ignore(Integer::sum);
   *   adder(null, 1); // null
   *   adder(null, null); // null
   *   adder(1, null); // null
   *   adder(1, 1); // 2
   * }
   * </pre>
   *
   * @param combine the function to apply for non-{@code NA} values
   * @param <T> the input type
   * @param <R> the output type
   * @return a {@code NA} safe {@code BiFunction}
   */
  public static <T, R> BiFunction<T, T, R> ignore(BiFunction<T, T, R> combine) {
    return (a, b) -> {
      boolean aNA = Is.NA(a);
      boolean bNA = Is.NA(b);
      if (aNA || bNA) {
        return null;
      } else {
        return combine.apply(a, b);
      }
    };
  }

  /**
   * Returns a {@code BiFunction} that ignores {@code NA} values by returning {@code NA} when both
   * the {@code left} or {@code right} value is {@code NA} and fill the {@code left} or
   * {@code right} value when either is {@code NA}.
   *
   * <p>
   * For example:
   * 
   * <pre>
   * {@code
   *  BiFunction<Integer, Integer, Integer> adder = Na.ignore((a, b) -> a + b), 10);
   *  adder(null, 1);    // 11
   *  adder(1, null);    // 11
   *  adder(null, null); // NA
   *  adder(1, 1);       // 2
   * }
   * </pre>
   *
   * @param combine the function to apply for non-{@code NA} values
   * @param <T> the input type
   * @param <R> the output type
   * @return a {@code NA} safe {@code BiFunction}
   */
  public static <T, R> BiFunction<T, T, R> ignore(BiFunction<T, T, R> combine, T fillValue) {
    return (a, b) -> {
      boolean aNA = Is.NA(a);
      boolean bNA = Is.NA(b);
      if (aNA && bNA) {
        return null;
      } else {
        if (aNA) {
          return combine.apply(fillValue, b);
        } else if (bNA) {
          return combine.apply(a, fillValue);
        } else {
          return combine.apply(a, b);
        }
      }
    };
  }
}
