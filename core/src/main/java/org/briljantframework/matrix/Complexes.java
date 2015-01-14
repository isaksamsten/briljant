package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;

/**
 * @author Isak Karlsson
 */
public final class Complexes {
  private Complexes() {}

  public static ComplexMatrix zeros(int size) {
    return fill(size, Complex.ZERO);
  }

  public static ComplexMatrix ones(int size) {
    return fill(size, Complex.ONE);
  }

  private static ComplexMatrix fill(int size, Complex fill) {
    return ArrayComplexMatrix.withDefaultValue(size, 1, fill);
  }
}
