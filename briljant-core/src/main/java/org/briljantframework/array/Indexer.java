/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array;


import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * Provide utilities for handling strided arrays.
 * 
 * @author Isak Karlsson
 */
public final class Indexer {

  private Indexer() {}

  /**
   * Returns a reversed copy of the argument.
   *
   * @param arr the array
   * @return a reversed copy of {@code arr}
   */
  public static int[] reverse(int[] arr) {
    int[] copy = new int[arr.length];
    for (int i = 0; i < arr.length; i++) {
      copy[i] = arr[arr.length - i - 1];
    }
    return copy;
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

  public static int columnMajorStride(int[] index, int offset, int[] stride) {
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
   * @param step the step size
   * @param index the index
   * @param n the end
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
    return columnMajor(0, currentRow, currentColumn, parentRows, parentColumns);
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

  protected static int linearized(int index, int offset, int[] stride, int[] shape) {
    if (stride.length == 1) {
      return offset + index * stride[0];
    } else if (stride.length == 2) {
      int shape0 = shape[0];
      int shape1 = shape[1];
      int sub0 = index / shape0;
      int sub1 = sub0 / shape1;
      return offset + (index - shape0 * sub0) * stride[0] + (sub0 - shape1 * sub1) * stride[1];
    } else {
      for (int i = 0; i < stride.length; i++) {
        int size = shape[i];
        int sub2 = index / size;
        offset += (index - size * sub2) * stride[i];
        index = sub2;
      }
      return offset;
    }
  }

  public static List<Integer> asList(int[] index) {
    Objects.requireNonNull(index);
    return new AbstractList<Integer>() {
      @Override
      public Integer get(int i) {
        return index[i];
      }

      @Override
      public int size() {
        return index.length;
      }
    };
  }
}
