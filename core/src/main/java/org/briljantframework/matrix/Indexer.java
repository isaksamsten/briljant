package org.briljantframework.matrix;

/**
 * @author Isak Karlsson
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

  public static int sliceIndex(int step, int index, int n) {
    int i = Math.multiplyExact(step, index);
    if (i >= n || i < 0) {
      throw new IllegalArgumentException(String.format(
              "index out of bounds; value %d out of bound %d", i, n));
    }
    return i;
  }

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
  public static int rowMajor(int row, int col, int nrows, int ncols) {
    if (col >= ncols || col < 0) {
      throw new IllegalArgumentException(String.format(
          "index out of bounds; value %d out of bound %d", col, ncols));
    } else if (row >= nrows || row < 0) {
      throw new IllegalArgumentException(String.format(
          "index out of bounds; value %d out of bound %d", row, nrows));
    } else {
      return row * ncols + col;
    }
  }

  /**
   * Given an {@code index}, compute the linearized column major index in a parent matrix.
   * 
   * @param index the current index
   * @param rows the rows of the view
   * @param colOffset the column offset
   * @param rowOffset the row offset
   * @param parentRows the number of rows in the parent
   * @param parentColumns the number of columns in the parent
   * @return the position {@code index} in a view, transformed to the position in the parent matrix.
   */
  public static int computeLinearIndex(int index, int rows, int colOffset, int rowOffset,
      int parentRows, int parentColumns) {
    int currentColumn = index / rows + colOffset;
    int currentRow = index % rows + rowOffset;
    return columnMajor(currentRow, currentColumn, parentRows, parentColumns);
  }
}
