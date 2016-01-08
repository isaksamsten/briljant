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

import java.util.function.BiFunction;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.data.Na;

/**
 * Provide functions for combining vectors.
 * 
 * @author Isak Karlsson
 */
public final class Combine {

  private Combine() {}

  /**
   * @return an adder that ignores {@code NA}
   */
  public static BiFunction<Object, Object, Object> add() {
    return Na.ignore(Combine::plusNumber);
  }

  /**
   * @return an adder that ignores {@code NA} and defaults to {@code fillValue}
   * @param fillValue the value to fill
   * @see Na#ignore(java.util.function.BiFunction, Object)
   */
  public static BiFunction<Object, Object, Object> add(Object fillValue) {
    return Na.ignore(Combine::plusNumber, fillValue);
  }

  public static BiFunction<Object, Object, Object> mul() {
    return Na.ignore(Combine::multiplyNumber);
  }

  public static BiFunction<Object, Object, Object> multiply(Object fillValue) {
    return Na.ignore(Combine::multiplyNumber, fillValue);
  }

  public static BiFunction<Object, Object, Object> div() {
    return Na.ignore(Combine::divNumber);
  }

  public static BiFunction<Object, Object, Object> div(Object fillValue) {
    return Na.ignore(Combine::divNumber, fillValue);
  }

  public static BiFunction<Object, Object, Object> sub() {
    return Na.ignore(Combine::minusNumber);
  }

  public static BiFunction<Object, Object, Object> sub(Object fillValue) {
    return Na.ignore(Combine::minusNumber, fillValue);
  }

  // TODO: propagate aptly. e.g., int / double => double, int / int => int, long + int => long
  // TODO: follow the spec
  // ISSUE#15
  private static Object plusNumber(Object a, Object b) {
    if (a instanceof Integer && b instanceof Integer || a instanceof Short && b instanceof Short) {
      return ((Number) a).intValue() + ((Number) a).intValue();
    } else if (a instanceof Complex && b instanceof Complex) {
      return ((Complex) a).add((Complex) b);
    } else if (a instanceof Long && b instanceof Long) {
      return ((Number) a).longValue() + ((Number) b).longValue();
    } else if (a instanceof Number) {
      return ((Number) a).doubleValue() + ((Number) b).doubleValue();
    } else {
      return null; // NA
    }
  }

  private static Object minusNumber(Object a, Object b) {
    if (a instanceof Integer && b instanceof Integer || a instanceof Short && b instanceof Short) {
      return ((Number) a).intValue() - ((Number) a).intValue();
    } else if (a instanceof Complex && b instanceof Complex) {
      return ((Complex) a).subtract((Complex) b);
    } else if (a instanceof Long && b instanceof Long) {
      return ((Number) a).longValue() - ((Number) b).longValue();
    } else if (a instanceof Number) {
      return ((Number) a).doubleValue() - ((Number) b).doubleValue();
    } else {
      return null; // NA
    }
  }

  private static Object multiplyNumber(Object a, Object b) {
    if (a instanceof Integer && b instanceof Integer || a instanceof Short && b instanceof Short) {
      return ((Number) a).intValue() * ((Number) b).intValue();
    } else if (a instanceof Complex && b instanceof Complex) {
      return ((Complex) a).multiply((Complex) b);
    } else if (a instanceof Long && b instanceof Long) {
      return ((Number) a).longValue() * ((Number) b).longValue();
    } else if (a instanceof Number) {
      return ((Number) a).doubleValue() * ((Number) b).doubleValue();
    } else {
      return null; // NA
    }
  }

  private static Object divNumber(Object a, Object b) {
    if (a instanceof Integer && b instanceof Integer || a instanceof Short && b instanceof Short) {
      return ((Number) a).intValue() / ((Number) a).intValue();
    } else if (a instanceof Complex && b instanceof Complex) {
      return ((Complex) a).divide((Complex) b);
    } else if (a instanceof Long && b instanceof Long) {
      return ((Number) a).longValue() / ((Number) b).longValue();
    } else if (a instanceof Number) {
      return ((Number) a).doubleValue() / ((Number) b).doubleValue();
    } else {
      return null; // NA
    }
  }
}
