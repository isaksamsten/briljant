package org.briljantframework.matrix;

/**
 * @author Isak Karlsson
 */
public final class Indexer {

  private Indexer() {
  }

  public static int[] reverse(int[] arr) {
    int[] copy = new int[arr.length];
    for (int i = 0; i < arr.length; i++) {
      copy[i] = arr[arr.length - i - 1];
    }
    return copy;
  }

  /**
   * Returns the flattened index for a column-major indexed array given {@code row}, {@code column}
   * and the size {@code nrows} and {@code ncols}
   *
   * @param row   the row
   * @param col   the col
   * @param nrows the number or rows
   * @param ncols the number of columns
   * @return the linearized index
   */
  public static int columnMajor(int offset, int row, int col, int nrows, int ncols) {
    if (col >= ncols || col < 0) {
      throw new IllegalArgumentException(String.format(
          "index out of bounds; value %d out of bound %d", col, ncols));
    } else if (row >= nrows || row < 0) {
      throw new IllegalArgumentException(String.format(
          "index out of bounds; value %d out of bound %d", row, nrows));
    } else {
      return offset + col * nrows + row;
    }
  }

  public static int[] computeStride(int st, int[] shape) {
    int[] stride = new int[shape.length];
    for (int i = 0; i < stride.length; i++) {
      stride[i] = st;
      st *= shape[i];
    }
    return stride;
  }

  public static int size(int[] shape) throws ArithmeticException {
    int size = shape[0];
    for (int i = 1; i < shape.length; i++) {
      size = Math.multiplyExact(size, shape[i]);
    }
    return size;
  }

  public static int[] remove(int[] array, int index) {
    int[] result = new int[array.length - 1];
    System.arraycopy(array, 0, result, 0, index);
    if (index < array.length - 1) {
      System.arraycopy(array, index + 1, result, index, array.length - index - 1);
    }

    return result;
  }

  public static int columnMajorStride(int offset, int[] index, int[] stride) {
    for (int i = 0; i < index.length; i++) {
      offset += index[i] * stride[i];
    }
    return offset;
  }

  public static int sub2ind(int[] dims, int... i) {
    int n = i.length - 1;
    int idx = i[n];
    for (int j = n - 1; j > 0; j--) {
      idx = i[j] + dims[j] * idx;
    }
    return idx;
  }

  /**
   * Returns the index of {@code indexe}, if the stride were {@code step}.
   *
   * @param step  the step size
   * @param index the index
   * @param n     the end
   * @return a new index; {@code step * index}, guaranteed to be {@code < n}
   */
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
   * @param row   the row
   * @param col   the col
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
   * @param index         the current index
   * @param rows          the rows of the view
   * @param colOffset     the column offset
   * @param rowOffset     the row offset
   * @param parentRows    the number of rows in the parent
   * @param parentColumns the number of columns in the parent
   * @return the position {@code index} in a view, transformed to the position in the parent matrix.
   */
  public static int computeLinearIndex(int index, int rows, int colOffset, int rowOffset,
                                       int parentRows, int parentColumns) {
    int currentColumn = index / rows + colOffset;
    int currentRow = index % rows + rowOffset;
    return columnMajor(0, currentRow, currentColumn, parentRows, parentColumns);
  }

  protected static int sub2ind(int index, int offset, int[] stride, int[] shape) {
    if (stride.length == 1) {
      return offset + index * stride[0];
    }
    for (int i = 0; i < stride.length; i++) {
      int size = shape[i];
      int sub2 = index / size;
      offset += (index - size * sub2) * stride[i];
      index = sub2;
    }
    return offset;
  }
}
