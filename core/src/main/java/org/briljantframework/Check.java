package org.briljantframework;

import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.exceptions.SizeMismatchException;
import org.briljantframework.matrix.AnyMatrix;

/**
 * Created by Isak Karlsson on 12/01/15.
 */
public final class Check {
  private Check() {}


  /**
   * Ensures that the shape of {@code a} and {@code b} is the same.
   *
   * @param a a matrix
   * @param b a matrix
   * @throws org.briljantframework.exceptions.NonConformantException if
   *         {@code a.rows() != b.rows() && a.columns() != b.columns()}
   */
  public static void equalShape(AnyMatrix a, AnyMatrix b) throws NonConformantException {
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
  public static void equalSize(AnyMatrix a, AnyMatrix b) throws SizeMismatchException {
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
  public static void equalSize(String message, AnyMatrix a, AnyMatrix b)
      throws SizeMismatchException {
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
  public static void size(String message, int size, AnyMatrix a) throws SizeMismatchException {
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
  public static void size(int size, AnyMatrix a) throws SizeMismatchException {
    if (a.size() != size) {
      throw new SizeMismatchException(a.size(), size);
    }
  }
}
