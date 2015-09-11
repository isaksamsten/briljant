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

package org.briljantframework;

import org.briljantframework.array.BaseArray;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;
import org.briljantframework.exceptions.IllegalTypeException;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.exceptions.SizeMismatchException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Isak Karlsson
 */
public final class Check {

  protected static final String NON_CONFORMAT_VALUE =
      "The value of %s did not conform with the predicate.";

  private Check() {
  }

  /**
   * Throws {@linkplain java.lang.IllegalArgumentException} if {@code predicate} returns false for
   * any of the values in the supplied list of arguments.
   * <p>
   * <pre>{@code
   * Check.any(x -> x.length() > 3, "a", "b", "c", string);
   * }</pre>
   *
   * @param predicate the predicate to test
   * @param array     the values to check
   */
  @SafeVarargs
  public static <T> void all(Predicate<? super T> predicate, T... array) {
    for (T t : array) {
      if (!predicate.test(t)) {
        throw new IllegalArgumentException(String.format(NON_CONFORMAT_VALUE, t));
      }
    }
  }

  /**
   * @see #all(java.util.function.Predicate, Object[])
   */
  public static <T> void all(Predicate<? super T> predicate, Iterable<? extends T> iterable) {
    for (T t : iterable) {
      if (!predicate.test(t)) {
        throw new IllegalArgumentException(String.format(NON_CONFORMAT_VALUE, t));
      }
    }
  }

  /**
   * Throws {@linkplain java.lang.IllegalArgumentException} if {@code value < min || value > max}
   *
   * @param value the value to check
   * @param min   the minimum range value
   * @param max   the maximum range value
   */
  public static void inRange(double value, double min, double max) {
    if (value < min || value > max) {
      throw new IllegalArgumentException(
          String.format("%f < %f (min) || %f > %f (max)", value, min, value, max));
    }
  }

  /**
   * Throws {@linkplain org.briljantframework.exceptions.NonConformantException} if the shape of
   * the arguments differ.
   *
   * @param a an array
   * @param b an array
   */
  public static void shape(BaseArray a, BaseArray b) throws NonConformantException {
    if (!Arrays.equals(a.getShape(), b.getShape())) {
      throw new NonConformantException(a, b);
    }
  }

  /**
   * Throws {@linkplain org.briljantframework.exceptions.SizeMismatchException} if the (linearized)
   * size of the arguments differ.
   *
   * @param a an array
   * @param b an array
   */
  public static void size(BaseArray a, BaseArray b) throws SizeMismatchException {
    size(a.size(), b.size());
  }

  /**
   * Throws {@linkplain org.briljantframework.exceptions.SizeMismatchException} if {@code
   * condition}
   * is {@code false}
   *
   * @param condition the condition
   * @param message   the message
   * @param args      the arguments to message (formatted as {@link String#format(java.util.Locale,
   *                  String, Object...)}
   */
  public static void size(boolean condition, String message, Object... args) {
    if (!condition) {
      throw new SizeMismatchException(String.format(message, args));
    }
  }

  /**
   * Throws {@linkplain org.briljantframework.exceptions.SizeMismatchException} if the arguments
   * differ.
   *
   * @param actual   the actual size
   * @param expected the expected size
   * @throws SizeMismatchException if {@code actual != expected}
   */
  public static void size(int actual, int expected) throws SizeMismatchException {
    size(actual, expected, "Size does not match. (%d != %d)", actual, expected);
  }

  public static void size(int actual, int expected, String msg, Object... args) {
    size(actual == expected, msg, args);
  }

  public static void boxedIndex(Integer index, int size) {
    int i = Objects.requireNonNull(index, "index is null");
    index(i, size);
  }

  public static void boxedIndex(Integer index, int size, String message, Object... args) {
    index(Objects.requireNonNull(index, "index is null"), size, message, args);
  }

  public static void index(int index, int size) {
    index(index, size, "Index %s out of bounds for axis with size %s", index, size);
  }

  public static void index(int index, int size, String message, Object... args) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(String.format(message, args));
    }
  }

  /**
   * Throws {@linkplain java.lang.IllegalArgumentException} if the boolean condition is false.
   *
   * @param check the boolean condition
   */
  public static void argument(boolean check) {
    if (!check) {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Throws {@linkplain java.lang.IllegalArgumentException} if the boolean condition is false with
   * the message formatted using {@linkplain String#format(String, Object...)}
   *
   * @param check   the boolean condition
   * @param message the message
   * @param args    the arguments to format
   */
  public static void argument(boolean check, String message, Object... args) {
    if (!check) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  /**
   * Throws {@linkplain java.lang.IllegalStateException} if the boolean condition is false.
   *
   * @param test the boolean condition
   */
  public static void state(boolean test) {
    if (!test) {
      throw new IllegalStateException();
    }
  }

  /**
   * Throws {@linkplain java.lang.IllegalStateException} if the boolean condition is false with the
   * message formatted using {@linkplain String#format(String, Object...)}.
   *
   * @param test    the boolean condition
   * @param message the message
   * @param args    the format arguments
   */
  public static void state(boolean test, String message, Object... args) {
    if (!test) {
      throw new IllegalStateException(String.format(message, args));
    }
  }

  /**
   * Throws {@linkplain java.lang.IllegalArgumentException} if the boolean condition is false with
   * the message formatted using {@linkplain String#format(String, Object...)}.
   *
   * @param test    the boolean condition
   * @param message the message
   * @param args    the format arguments
   */
  public static void type(boolean test, String message, Object... args) {
    if (!test) {
      throw new IllegalTypeException(String.format(message, args));
    }
  }

  public static void type(Vector vector, VectorType type) throws IllegalTypeException {
    type(vector.getType(), type);
  }

  public static void type(VectorType actual, VectorType expected)
      throws IllegalTypeException {
    type(actual.equals(expected), "Require type %s but got %s", expected, actual);
  }

  public static void type(Set<VectorType> expected, VectorType actual)
      throws IllegalTypeException {
    type(actual.equals(expected), "Require any type of %s but got %s", expected, actual);
  }

  public static int elementIndex(int index, int size) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(index + " < 0 || " + index + " >= " + size);
    }
    return index;
  }
}
