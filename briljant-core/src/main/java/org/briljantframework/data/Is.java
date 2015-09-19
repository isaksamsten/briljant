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

package org.briljantframework.data;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.data.vector.Vector;

/**
 * Utility class for checking value types
 *
 * @author Isak Karlsson
 */
public final class Is {

  private Is() {

  }

  public static boolean nominal(Object value) {
    return !(value instanceof Number);
  }

  public static boolean nominal(Vector vector) {
    return !Number.class.isAssignableFrom(vector.getType().getDataClass());
  }

  /**
   * Check if vector is NA-vector
   *
   * @param vector the vector
   * @return true/false
   */
  public static boolean NA(Vector vector) {
    return vector.size() == 0 || vector.stream(Logical.class).allMatch(Is::NA);
  }

  public static boolean NA(char value) {
    return value == Na.CHAR;
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(long value) {
    return value == Na.LONG;
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(int value) {
    return value == Na.INT;
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(short value) {
    return value == Na.SHORT;
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(byte value) {
    return value == Na.BYTE;
  }


  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(Complex value) {
    if (value == null) {
      return true;
    } else {
      return Is.NA(value.getImaginary()) || Is.NA(value.getReal());
    }
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(double value) {
    return Double.isNaN(value)
           && (Double.doubleToRawLongBits(value) & Na.DOUBLE_NA_MASK) == Na.DOUBLE_NA_RES;
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(float value) {
    return Float.isNaN(value)
           && (Float.floatToRawIntBits(value) & Na.FLOAT_NA_MASK) == Na.FLOAT_NA_RES;
  }

  /**
   * Check if value is NA
   *
   * @param value the value
   * @return true if value is NA
   */
  public static boolean NA(Logical value) {
    return value == null || value == Logical.NA;
  }

  public static boolean NA(Object o) {
    if (o == null) {
      return true;
    } else if (o instanceof Double) {
      return Is.NA((double) o);
    } else if (o instanceof Float) {
      return Is.NA((float) o);
    } else if (o instanceof Long) {
      return Is.NA((long) o);
    } else if (o instanceof Integer) {
      return Is.NA((int) o);
    } else if (o instanceof Short) {
      return Is.NA((short) o);
    } else if (o instanceof Byte) {
      return Is.NA((byte) o);
    } else if (o instanceof Character) {
      return Is.NA((char) o);
    } else if (o instanceof Complex) {
      return Is.NA((Complex) o);
    } else {
      Object na = Na.of(o.getClass());
      return o.equals(na);
    }
  }
}
