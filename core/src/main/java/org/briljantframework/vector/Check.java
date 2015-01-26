package org.briljantframework.vector;

import org.briljantframework.exceptions.SizeMismatchException;
import org.briljantframework.exceptions.TypeConversionException;

/**
 * Created by isak on 1/19/15.
 */
public final class Check {

  private Check() {}

  public static void requireType(VectorType type, Vector vector) throws TypeConversionException {
    if (!type.equals(vector.getType())) {
      throw new TypeConversionException(String.format("Require type %s but got %s", type,
          vector.getType()));
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
   * @throws SizeMismatchException if {@code x.size() != y.size()}
   */
  public static void size(Vector x, Vector y, String message) throws SizeMismatchException {
    if (x.size() != y.size()) {
      throw new SizeMismatchException(message, x.size(), y.size());
    }
  }
}
