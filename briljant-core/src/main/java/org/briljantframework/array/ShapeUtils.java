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

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ShapeUtils {

  public static boolean isBroadcastSensible(BaseArray<?> a, BaseArray<?> b) {
    return !(a.isVector() && b.isVector());
  }

  public static <S extends BaseArray<S>> S broadcastIfSensible(BaseArray<?> targetShape,
      S toBroadcast) {
    if (isBroadcastSensible(targetShape, toBroadcast)) {
      return Arrays.broadcast(toBroadcast, targetShape.getShape());
    } else {
      return toBroadcast;
    }
  }

  /**
   * Checks if the given shapes are broadcast compatible.
   * 
   * <p/>
   * An array can be broadcast to a specified shape if the dimensions are compatible. The shapes are
   * by compared element-wise starting with the trailing dimension. Two dimensions are compatible
   * if:
   * <ul>
   * <li>they are equal; or</li>
   * <li>one of them is equal to {@code 1}</li>
   * </ul>
   * 
   * @param a the first shape
   * @param b the second shape
   * @return true if the shapes are broadcast compatible
   */
  public static boolean isBroadcastCompatible(int[] a, int[] b) {
    int ac = b.length - 1;
    int bc = a.length - 1;
    for (int i = 0; i < b.length; i++) {
      if (i >= a.length) {
        break;
      }
      if (b[ac - i] != a[bc - i] && a[bc - i] != 1) {
        return false;
      }
    }
    return true;
  }

  /**
   * Compute the size of an array with the given shape.
   * 
   * @param shape the shape
   * @return the size
   * @throws ArithmeticException if the size is larger than an int
   */
  public static int size(int[] shape) throws ArithmeticException {
    int size = shape[0];
    for (int i = 1; i < shape.length; i++) {
      size = Math.multiplyExact(size, shape[i]);
    }
    return size;
  }

  /**
   * Return the shape of the given array brodcasted to the specified shape.
   *
   * @param oldShape the old shape
   * @param shape the broadcast shape
   * @return the shape of the new broadcast array
   */
  public static int[] broadcast(int[] oldShape, int[] shape) {
    int[] newShape = new int[shape.length];
    for (int i = 0; i < newShape.length; i++) {
      int index = newShape.length - 1 - i;
      int dim = oldShape.length - 1 - i;
      int broadcastShape = shape[index];
      if (oldShape.length == 1) {
        if (i == 0) {
          if (i < (double) oldShape.length) {
            newShape[index] = Math.max(1, broadcastShape);
          } else {
            newShape[index] = broadcastShape;
          }
        } else {
          if (i < (double) oldShape.length) {
            newShape[index] = Math.max(oldShape[dim], broadcastShape);
          } else {
            newShape[index] = broadcastShape;
          }
        }
      } else {
        if (i < (double) oldShape.length) {
          newShape[index] = Math.max(broadcastShape, oldShape[dim]);
        } else {
          newShape[index] = broadcastShape;
        }
      }
    }
    return newShape;
  }
}
