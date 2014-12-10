package org.briljantframework.matrix;

/**
 * Created by Isak Karlsson on 02/12/14.
 */
public final class Indexer {
  private Indexer() {}

  /**
   * Returns the flattened index for a column-major indexed array given {@code row}, {@code column}
   * and the size {@code nrows} and {@code ncols}
   *
   * @param row the row
   * @param col the col
   * @param nrows the number or rows
   * @param ncols the number of columns
   * @return the linearized index
   */
  public static int columnMajor(int row, int col, int nrows, int ncols) {
    if (col >= ncols || col < 0) {
      throw new IllegalArgumentException(String.format(
          "index out of bounds; value %d out of bound %d", col, ncols));
    } else if (row >= nrows || row < 0) {
      throw new IllegalArgumentException(String.format(
          "index out of bounds; value %d out of bound %d", row, nrows));
    } else {
      return col * nrows + row;
    }
  }

}
