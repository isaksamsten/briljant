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
package org.briljantframework;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.briljantframework.array.BaseArray;
import org.briljantframework.exceptions.DimensionMismatchException;
import org.briljantframework.exceptions.MultiDimensionMismatchException;

/**
 * Perform common checks.
 *
 * @author Isak Karlsson
 */
public final class Check<T> {

  protected static final String NON_CONFORMAT_VALUE =
      "The value of %s did not conform with the predicate.";

  private final List<T> values;

  private Check(List<T> values) {
    this.values = values;
  }

  /**
   * Return a checker for all values.
   * 
   * @param values the values
   * @param <T> the value class
   * @return a checker
   */
  @SafeVarargs
  public static <T> Check<T> all(T... values) {
    return new Check<>(Arrays.asList(values));
  }

  /**
   * Return a checker for all values.
   *
   * @param values the values
   * @param <T> the value class
   * @return a checker
   */
  public static <T> Check<T> all(List<T> values) {
    return new Check<>(values);
  }

  /**
   * Throws {@linkplain java.lang.IllegalArgumentException} if {@code value < min || value > max}.
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
   * Throws {@linkplain MultiDimensionMismatchException} if the shape of the arguments differ.
   *
   * @param a an array
   * @param b an array
   */
  public static void dimension(BaseArray a, BaseArray b) throws MultiDimensionMismatchException {
    dimension(a.getShape(), b.getShape());
  }

  /**
   * Throws {@link MultiDimensionMismatchException} if the shapes differ.
   * 
   * @param wrong the wrong shape
   * @param expected the expected shape
   * @throws MultiDimensionMismatchException if shapes differ
   */
  public static void dimension(int[] wrong, int[] expected) throws MultiDimensionMismatchException {
    if (!Arrays.equals(wrong, expected)) {
      throw new MultiDimensionMismatchException(wrong, expected);
    }
  }

  /**
   * Throws {@linkplain DimensionMismatchException} if the (linearized) size of the arguments
   * differ.
   *
   * @param a an array
   * @param b an array
   */
  public static void size(BaseArray a, BaseArray b) throws DimensionMismatchException {
    dimension(a.size(), b.size());
  }

  /**
   * Throws {@linkplain DimensionMismatchException} if the arguments differ.
   *
   * @param actual the actual size
   * @param expected the expected size
   * @throws DimensionMismatchException if {@code actual != expected}
   */
  public static void dimension(int actual, int expected) throws DimensionMismatchException {
    if (actual != expected) {
      throw new DimensionMismatchException(actual, expected);
    }
  }

  public static void index(int index, int size) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(
          String.format("Index %d out of bounds for dimension with size %d", index, size));
    }
  }

  public static void index(int row, int rows, int column, int columns) {
    index(row, rows);
    index(column, columns);
  }

  public static void index(int[] index, int[] shape) {
    if (index.length != shape.length) {
      throw new IndexOutOfBoundsException("to many / to few indexes for dimensions.");
    }
    for (int i = 0; i < index.length; i++) {
      index(index[i], shape[i]);
    }
  }

  /**
   * Check that the index is a valid index.
   * 
   * @see #validIndex(int, int, String, Object...)
   */
  public static void validBoxedIndex(Integer index, int size) {
    Integer i = Objects.requireNonNull(index, "index is null");
    Check.validIndex(i, size);
  }

  /**
   * Check that the index is a valid index.
   *
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
   * Check that the index is a valid index.
   *
   * @see #validIndex(int, int, String, Object...)
   */
  public static void validBoxedIndex(Integer index, int size, String message, Object... args) {
    validIndex(Objects.requireNonNull(index, "index is null"), size, message, args);
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
   * If predicate fails, throws runtime exception.
   * 
   * @param predicate the prediate
   */
  public void that(Predicate<? super T> predicate) {
    that(predicate, RuntimeException::new, "Unexpected error.");
  }

  /**
   * Throws an instance of E if the predicate fails.
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
   * Check that the predicate is true for all values in the checker.
   * 
   * @param predicate the predicate
   * @param message the error message
   * @param args the error message arguments
   * @throws IllegalArgumentException if predicate returns false
   */
  public void argument(Predicate<? super T> predicate, String message, Object... args) {
    that(predicate, IllegalArgumentException::new, message, args);
  }

  /**
   * Check that the predicate return true for all values in the checker.
   * 
   * @param predicate the predicate
   * @throws IllegalArgumentException if predicate return false
   */
  public void argument(Predicate<? super T> predicate) {
    that(predicate, IllegalArgumentException::new);
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

  /**
   * Check that the predicate return true for all values in the checker.
   * 
   * @param predicate the predicate
   * @param message the error message
   * @param args the error message arguments
   * @throws IllegalStateException if predicate returns false
   */
  public void state(Predicate<? super T> predicate, String message, Object... args) {
    that(predicate, IllegalStateException::new, message, args);
  }

  /**
   * Check that the predicate return true for all values in the checker.
   *
   * @param predicate the predicate
   * @throws IllegalStateException if predicate returns false
   */
  public void state(Predicate<? super T> predicate) {
    that(predicate, IllegalStateException::new);
  }
}
