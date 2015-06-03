package org.briljantframework;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.exceptions.SizeMismatchException;
import org.briljantframework.exceptions.TypeConversionException;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Isak Karlsson
 */
public final class Check {

  private Check() {
  }

  @SafeVarargs
  public static <T> void all(Predicate<? super T> p, T... arr) {
    all(Arrays.asList(arr), p);
  }

  public static <T> void all(Iterable<? extends T> it, Predicate<? super T> p) {
    for (T t : it) {
      if (!p.test(t)) {
        throw new IllegalArgumentException(String.format("Test of %s failed.", t));
      }
    }
  }

  public static void range(double v, double min, double max) {
    if (v < min || v > max) {
      throw new IllegalArgumentException(
          String.format("%f < %f (min) || %f > %f (max)", v, min, v, max));
    }
  }

  public static void vectorOfSize(int actual, Matrix<?> x) {
    if (!x.isVector() || x.size() != actual) {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Ensures that the shape of {@code a} and {@code b} is the same.
   *
   * @param a a matrix
   * @param b a matrix
   * @throws org.briljantframework.exceptions.NonConformantException if
   *                                                                 {@code a.rows() != b.rows() &&
   *                                                                 a.columns() != b.columns()}
   */
  public static void equalShape(Matrix a, Matrix b) throws NonConformantException {
    if (a.rows() != b.rows() && a.columns() != b.columns()) {
      throw new NonConformantException(a, b);
    }
  }

  /**
   * Ensures that the size of {@code a} and {@code b} is the same.
   *
   * @param a a matrix
   * @param b a matrix
   * @throws org.briljantframework.exceptions.SizeMismatchException if {@code a.size() != b.size()}.
   */
  public static void size(Matrix a, Matrix b) throws SizeMismatchException {
    if (a.size() != b.size()) {
      throw new SizeMismatchException(a.size(), b.size());
    }
  }

  /**
   * Ensures that the size of {@code a.size() == b.size()}.
   *
   * @param message format string containing 2 {@code %d}, where the first denote the expected (
   *                {@code a.size()}) and the second the actual ({@code b.size()}).
   * @param a       a matrix
   * @param b       a matrix
   * @throws org.briljantframework.exceptions.SizeMismatchException if {@code a.size() != b.size()}.
   */
  public static void equalSize(String message, Matrix a, Matrix b) throws SizeMismatchException {
    if (a.size() != b.size()) {
      throw new SizeMismatchException(message, a.size(), b.size());
    }
  }

  /**
   * Ensures that the size of {@code a.size() == b.size()}.
   *
   * @param message format string containing 2 {@code %d}, where the first denote the expected (
   *                {@code a.size()}) and the second the actual ({@code size}).
   * @param a       a matrix
   * @param size    a size
   * @throws org.briljantframework.exceptions.SizeMismatchException if {@code a.size() != size}.
   */
  public static void size(String message, int size, Matrix a) throws SizeMismatchException {
    if (a.size() != size) {
      throw new SizeMismatchException(message, a.size(), size);
    }
  }

  /**
   * Ensures that the size of {@code a.size() == b.size()}.
   *
   * @param a    a matrix
   * @param size a size
   * @throws org.briljantframework.exceptions.SizeMismatchException if {@code a.size() != size}.
   */
  public static void size(int size, Matrix a) throws SizeMismatchException {
    if (a.size() != size) {
      throw new SizeMismatchException(a.size(), size);
    }
  }

  public static void size(int actual, int expected) throws SizeMismatchException {
    if (actual != expected) {
      throw new SizeMismatchException(actual, expected);
    }
  }

  public static void requireType(VectorType type, Vector vector) throws TypeConversionException {
    requireType(type, vector.getType());
  }

  public static void requireType(VectorType expected, VectorType actual)
      throws TypeConversionException {
    if (!expected.equals(actual)) {
      throw new TypeConversionException(String.format("Require type %s but got %s", expected,
                                                      actual));
    }
  }

  public static void requireType(Set<VectorType> expected, VectorType actual)
      throws TypeConversionException {
    if (!expected.contains(actual)) {
      throw new TypeConversionException(String.format("Require type %s but got %s", expected,
                                                      actual));
    }
  }

  public static void size(Vector x, Vector y) {
    if (x.size() != y.size()) {
      throw new SizeMismatchException(x.size(), y.size());
    }
  }

  public static void size(int actual, Vector x) {
    if (actual != x.size()) {
      throw new SizeMismatchException(actual, x.size());
    }
  }

  /**
   * @param x       one vector
   * @param y       the other vector
   * @param message the message; 2 {@code %d}
   * @throws org.briljantframework.exceptions.SizeMismatchException if {@code x.size() != y.size()}
   */
  public static void size(Vector x, Vector y, String message) throws SizeMismatchException {
    if (x.size() != y.size()) {
      throw new SizeMismatchException(message, x.size(), y.size());
    }
  }

  public static void size(DataFrame x, Vector y) {
    size(x.rows(), y.size());
  }

  public static void columnSize(DataFrame expected, DataFrame actual) {
    size(expected.columns(), actual.columns());
  }

  public static void isNotView(Matrix<?> m) {
    if (m.isView()) {
      throw new UnsupportedOperationException(
          String.format("Views are unsupported. Please make a copy."));
    }
  }

  public static void argument(boolean check) {
    argument(check, "Invalid argument");
  }

  public static void argument(boolean check, String message, Object... args) {
    if (!check) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }
}
