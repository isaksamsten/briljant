package org.briljantframework;

import java.util.Set;

import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.exceptions.SizeMismatchException;
import org.briljantframework.exceptions.TypeConversionException;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

/**
 * @author Isak Karlsson
 */
public final class Check {
  private Check() {}

  public static void range(double v, double min, double max) {
    if (v < min || v > max) {
      throw new IllegalArgumentException(String.format("%f < %f (min) || %f > %f (max)", v, min, v,
          max));
    }
  }


  /**
   * Ensures that the shape of {@code a} and {@code b} is the same.
   *
   * @param a a matrix
   * @param b a matrix
   * @throws org.briljantframework.exceptions.NonConformantException if
   *         {@code a.rows() != b.rows() && a.columns() != b.columns()}
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
   *        {@code a.size()}) and the second the actual ({@code b.size()}).
   * @param a a matrix
   * @param b a matrix
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
   *        {@code a.size()}) and the second the actual ({@code size}).
   * @param a a matrix
   * @param size a size
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
   * @param a a matrix
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
   * @param x one vector
   * @param y the other vector
   * @param message the message; 2 {@code %d}
   * @throws org.briljantframework.exceptions.SizeMismatchException if {@code x.size() != y.size()}
   */
  public static void size(Vector x, Vector y, String message) throws SizeMismatchException {
    if (x.size() != y.size()) {
      throw new SizeMismatchException(message, x.size(), y.size());
    }
  }
}
