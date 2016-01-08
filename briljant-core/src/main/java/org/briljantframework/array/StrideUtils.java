/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
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


/**
 * Provide utilities for handling strided arrays.
 * 
 * @author Isak Karlsson
 */
public final class StrideUtils {

  private StrideUtils() {}

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

  /**
   * Compute the stride of an array with the given shape.
   * 
   * @param shape the shape
   * @return the strides of the array
   */
  public static int[] computeStride(int[] shape) {
    return computeStride(1, shape);
  }

  /**
   * Compute the stride of an array with the given shape.
   * 
   * @param st the initial stride
   * @param shape the shape
   * @return the stride of the array
   */
  public static int[] computeStride(int st, int[] shape) {
    int[] stride = new int[shape.length];
    for (int i = 0; i < stride.length; i++) {
      stride[i] = st;
      st *= shape[i];
    }
    return stride;
  }

  /**
   * Compute the stride of the given strides and shape for the new shape.
   * 
   * @param strides the old strides
   * @param shape the old shape
   * @param newShape the new shape
   * @return the new strides
   */
  public static int[] broadcastStrides(int[] strides, int[] shape, int[] newShape) {
    int[] newStrides = new int[newShape.length];
    for (int i = 0; i < newShape.length; i++) {
      int index = newShape.length - 1 - i;
      int dim = shape.length - 1 - i;
      if (i < shape.length && shape[dim] != 1) {
        newStrides[index] = strides[dim];
      }
    }
    return newStrides;
  }

  /**
   * Return the memory location of the row and column coordinates.
   * 
   * @param i the row
   * @param j the column
   * @param offset the offset
   * @param stride the strides
   * @return the memory location
   */
  public static int index(int i, int j, int offset, int[] stride) {
    return offset + i * stride[0] + j * stride[1];
  }

  /**
   * Return the memory location of the index.
   * 
   * @param index the index
   * @param offset the offset
   * @param stride the stride
   * @return the memory location
   */
  public static int index(int[] index, int offset, int[] stride) {
    for (int i = 0; i < index.length; i++) {
      offset += index[i] * stride[i];
    }
    return offset;
  }

  /**
   * Returns the memory (array) location of a given index when traversing a multidimensional array
   * in column major order. This is the inverse of {@link #index(int[], int, int[])}.
   * 
   * @param index the linear index position
   * @param offset the array offset
   * @param stride the strides
   * @param shape the shape
   * @return the memory location
   */
  public static int index(int index, int offset, int[] stride, int[] shape) {
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
}
