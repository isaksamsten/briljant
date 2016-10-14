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

import java.util.Collection;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ShapeUtils {

  public static boolean isBroadcastSensible(BaseArray<?> a, BaseArray<?> b) {
    if (!(a.isVector() && b.isVector()))
      if (a.size() == b.size())
        return false;
      else
        return true;
    if (b.size() == 1)
      return true;
    return false;
  }

  /**
   * Broadcast the first argument to the shape of the second argument, or return the original if
   * broadcasting does not make sense (both are vectors).
   * 
   * @param array the array
   * @param newShape the new shape
   * @param <E> the type of array
   * @return the first argument with the shape of the second
   */
  public static <E extends BaseArray<? extends E>> E broadcastToShapeOf(E array,
      BaseArray<?> newShape) {
    if (isBroadcastSensible(newShape, array)) {
      return Arrays.broadcastTo(array, newShape.getShape());
    } else {
      return array;
    }
  }

  public static <A extends BaseArray<? extends A>, B extends BaseArray<? extends B>> Pair<A, B> combinedBroadcast(
      A a, B b) {
    if (isBroadcastSensible(a, b)) {
      int[] combinedShape = findCombinedBroadcastShape(java.util.Arrays.asList(a, b));
      return new ImmutablePair<>(Arrays.broadcastTo(a, combinedShape),
          Arrays.broadcastTo(b, combinedShape));
    } else {
      return new ImmutablePair<>(a, b);
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
   * @param from the old shape
   * @param to the broadcast shape
   * @return the shape of the new broadcast array
   */
  public static int[] findBroadcastShape(int[] from, int[] to) {
    int[] newShape = new int[to.length];
    for (int i = 0; i < newShape.length; i++) {
      int index = newShape.length - 1 - i;
      int dim = from.length - 1 - i;
      int broadcastShape = to[index];
      if (from.length == 1) {
        if (i == 0) {
          if (i < (double) from.length) {
            newShape[index] = Math.max(1, broadcastShape);
          } else {
            newShape[index] = broadcastShape;
          }
        } else {
          if (i < (double) from.length) {
            newShape[index] = Math.max(from[dim], broadcastShape);
          } else {
            newShape[index] = broadcastShape;
          }
        }
      } else {
        if (i < (double) from.length) {
          newShape[index] = Math.max(broadcastShape, from[dim]);
        } else {
          newShape[index] = broadcastShape;
        }
      }
    }
    return newShape;
  }


  public static int[] findCombinedBroadcastShape(Collection<? extends BaseArray<?>> arrays) {
    int dims = arrays.stream().mapToInt(BaseArray::dims).max()
        .orElseThrow(() -> new IllegalArgumentException("no arrays given."));
    int[] shape = new int[dims];
    java.util.Arrays.fill(shape, 1);
    for (BaseArray<?> array : arrays) {
      for (int i = 0; i < shape.length; i++) {
        int shapeIndex = shape.length - 1 - i;
        int arrayIndex = array.dims() - 1 - i;
        if (i < array.dims()) {
          if (shape[shapeIndex] != array.size(arrayIndex)
              && (shape[shapeIndex] != 1 && array.size(arrayIndex) != 1)) {
            throw new IllegalArgumentException("arrays cannot be broadcast to the same shape");
          }
          shape[shapeIndex] = Math.max(shape[shapeIndex], array.size(arrayIndex));
        } else {
          shape[shapeIndex] = Math.max(shape[shapeIndex], 1);
        }
      }
    }
    return shape;
  }

  /**
   * Returns true if both arrays has the exact same shape.
   *
   * @param a an array
   * @param b an array
   * @return true if both arrays has the exact same shape
   */
  public static boolean hasMatchingShape(BaseArray<?> a, BaseArray<?> b) {
    if (a.dims() == b.dims()) {
      for (int i = 0; i < a.dims(); i++) {
        if (a.size(i) != b.size(i)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
