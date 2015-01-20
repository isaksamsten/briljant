package org.briljantframework.vector;

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
}
