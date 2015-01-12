package org.briljantframework.matrix;

/**
 * Created by Isak Karlsson on 12/01/15.
 */
public final class Bits {
  private Bits() {}

  public static BitMatrix newMatrix(int rows, int cols, boolean... values) {
    return new ArrayBitMatrix(rows, cols, values);
  }

  public static BitMatrix newMatrix(int rows, int cols) {
    return new ArrayBitMatrix(rows, cols);
  }

  public static BitMatrix newMatrix(boolean... values) {
    return new ArrayBitMatrix(values);
  }
}
