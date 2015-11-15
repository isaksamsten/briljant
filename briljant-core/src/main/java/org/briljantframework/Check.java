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

package org.briljantframework;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.briljantframework.array.BaseArray;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;
import org.briljantframework.exceptions.IllegalTypeException;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.exceptions.SizeMismatchException;

/**
 * @author Isak Karlsson
 */
public final class Check<T> {

  protected static final String NON_CONFORMAT_VALUE =
      "The value of %s did not conform with the predicate.";

  private final List<T> values;

  private Check(List<T> values) {
    this.values = values;
  }

  @SafeVarargs
  public static <T> Check<T> all(T... t) {
    return new Check<>(Arrays.asList(t));
  }

  public static <T> Check<T> all(List<T> t) {
    return new Check<>(t);
  }

  /**
   * If predicate fails, throws runtime exception
   * 
   * @param predicate the prediate
   */
  public void that(Predicate<? super T> predicate) {
    that(predicate, RuntimeException::new, "Unexpected error.");
  }

  /**
   * Throws an instance of E if the predicate fails
   *
   * @param predicate the predicate
   * @param thrower the exception
   * @param message the message
   * @param args the message arguments
   * @param <E> the type of exception
   * @throws E the exception
   */
  public <E extends Exception> void that(Predicate<? super T> predicate,
      Function<String, ? extends E> thrower, String message, Object... args) throws E {
    E e = thrower.apply(String.format(message, args));
    for (T value : values) {
      if (!predicate.test(value)) {
        throw e;
      }
    }
  }


  /**
   * Throws an instance of E if the predicate fails
   *
   * @param predicate the predicate
   * @param thrower the exception
   * @param <E> the type of exception
   * @throws E the exception
   */
  public <E extends Exception> void that(Predicate<? super T> predicate,
      Supplier<? extends E> thrower) throws E {
    E e = thrower.get();
    for (T value : values) {
      if (!predicate.test(value)) {
        throw e;
      }
    }
  }

  public void argument(Predicate<? super T> predicate, String message, Object... args) {
    that(predicate, IllegalArgumentException::new, message, args);
  }

  public void argument(Predicate<? super T> predicate) {
    that(predicate, IllegalArgumentException::new);
  }

  public void state(Predicate<? super T> predicate, String message, Object... args) {
    that(predicate, IllegalStateException::new, message, args);
  }

  public void state(Predicate<? super T> predicate) {
    that(predicate, IllegalStateException::new);
  }

  /**
   * Throws {@linkplain java.lang.IllegalArgumentException} if {@code value < min || value > max}
   *
   * @param value the value to check
   * @param min the minimum range value
   * @param max the maximum range value
   */
  public static void inRange(double value, double min, double max) {
    if (value < min || value > max) {
      throw new IllegalArgumentException(
          String.format("%f < %f (min) || %f > %f (max)", value, min, value, max));
    }
  }

  /**
   * Throws {@linkplain org.briljantframework.exceptions.NonConformantException} if the shape of the
   * arguments differ.
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
   * Throws {@linkplain org.briljantframework.exceptions.SizeMismatchException} if {@code condition}
   * is {@code false}
   *
   * @param condition the condition
   * @param message the message
   * @param args the arguments to message (formatted as
   *        {@link String#format(java.util.Locale, String, Object...)}
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
   * @param actual the actual size
   * @param expected the expected size
   * @throws SizeMismatchException if {@code actual != expected}
   */
  public static void size(int actual, int expected) throws SizeMismatchException {
    size(actual, expected, "Size does not match. (%d != %d)", actual, expected);
  }

  public static void size(int actual, int expected, String msg, Object... args) {
    size(actual == expected, msg, args);
  }

  /**
   * @see #validIndex(int, int, String, Object...)
   */
  public static void validBoxedIndex(Integer index, int size) {
    Integer i = Objects.requireNonNull(index, "index is null");
    Check.validIndex(i, size);
  }

  /**
   * @see #validIndex(int, int, String, Object...)
   */
  public static void validBoxedIndex(Integer index, int size, String message, Object... args) {
    validIndex(Objects.requireNonNull(index, "index is null"), size, message, args);
  }

  /**
   * @see #validIndex(int, int, String, Object...)
   */
  public static void validIndex(int index, int size) {
    validIndex(index, size, "Index %s out of bounds for axis with size %s", index, size);
  }

  /**
   * Check that an index is valid (index >= 0 && index < size)
   *
   * @param index the index
   * @param size the size
   * @throws IndexOutOfBoundsException
   */
  public static void validIndex(int index, int size, String message, Object... args) {
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
   * @param check the boolean condition
   * @param message the message
   * @param args the arguments to format
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
   * @param test the boolean condition
   * @param message the message
   * @param args the format arguments
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
   * @param test the boolean condition
   * @param message the message
   * @param args the format arguments
   */
  public static void type(boolean test, String message, Object... args) {
    if (!test) {
      throw new IllegalTypeException(String.format(message, args));
    }
  }

  public static void type(Vector vector, VectorType type) throws IllegalTypeException {
    type(vector.getType(), type);
  }

  public static void type(VectorType actual, VectorType expected) throws IllegalTypeException {
    type(actual.equals(expected), "Require type %s but got %s", expected, actual);
  }

  public static void type(Set<VectorType> expected, VectorType actual) throws IllegalTypeException {
    type(actual.equals(expected), "Require any type of %s but got %s", expected, actual);
  }
}
