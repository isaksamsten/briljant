package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;

/**
 * Created by Isak Karlsson on 13/01/15.
 */
public final class Complexes {
  private Complexes() {}

  public static ComplexMatrix zeros(int size) {
    return ArrayComplexMatrix.withDefaultValue(size, 1, Complex.ZERO);
  }
}
