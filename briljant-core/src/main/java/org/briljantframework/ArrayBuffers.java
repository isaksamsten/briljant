package org.briljantframework;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;

import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.IntVector;

/**
 * Utility class for handling array buffers
 * 
 * @author Isak Karlsson
 */
public final class ArrayBuffers {
  private ArrayBuffers() {}

  /**
   * Removes element at {@code index} from {@code array}, shifting (similar to
   * {@link java.util.List#remove(int)}
   * 
   * @param array double array
   * @param index the index
   * @return array modified
   */
  public static double[] remove(double[] array, int index) {
    checkArgument(index >= 0 && index < array.length);
    int numMoved = array.length - index - 1;
    if (numMoved > 0)
      System.arraycopy(array, index + 1, array, index, numMoved);
    array[array.length - 1] = DoubleVector.NA;
    return array;
  }

  /**
   * Removes element at {@code index} from {@code array}, shifting (similar to
   * {@link java.util.List#remove(int)}
   *
   * @param array double array
   * @param index the index
   * @return array modified
   */
  public static int[] remove(int[] array, int index) {
    checkArgument(index >= 0 && index < array.length);
    int numMoved = array.length - index - 1;
    if (numMoved > 0)
      System.arraycopy(array, index + 1, array, index, numMoved);
    array[array.length - 1] = IntVector.NA;
    return array;
  }

  /**
   * Ensures that {@code array.length} is at least {@code minCapacity}. If
   * {@code minCapacity < array.length}, {@code array} is returned.
   *
   * @param array the array
   * @param minCapacity the minimum capacity
   * @return {@code array} if {@code array.length < minCapacity} otherwise a new array with
   *         {@code array.length > minCapacity}
   */
  public static double[] ensureCapacity(double[] array, int minCapacity) {
    int oldCapacity = array.length;
    double[] newArray;
    if (oldCapacity < minCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }
      newArray = Arrays.copyOf(array, newCapacity);
    } else {
      newArray = array;
    }
    return newArray;
  }

  /**
   * Reallocates {@code array} to a new array of length {@code minCapacity} if
   * {@code array.length < minCapacity} otherwise return {@code array}.
   * 
   * @param array the array
   * @param minCapacity the minimum capacity
   * @return an array of {@code minCapacity}; might return the input array
   */
  public static double[] reallocate(double[] array, int minCapacity) {
    int oldCapacity = array.length;
    double[] newArray;
    if (oldCapacity < minCapacity) {
      newArray = new double[minCapacity];
    } else {
      newArray = array;
    }
    return newArray;
  }

  /**
   * Ensures that {@code array.length} is at least {@code minCapacity}. If
   * {@code minCapacity < array.length}, {@code array} is returned.
   *
   * @param array the array
   * @param minCapacity the minimum capacity
   * @return {@code array} if {@code array.length < minCapacity} otherwise a new array with
   *         {@code array.length > minCapacity}
   */
  public static int[] ensureCapacity(int[] array, int minCapacity) {
    int oldCapacity = array.length;
    int[] newArray;
    if (oldCapacity < minCapacity) {
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }
      newArray = Arrays.copyOf(array, newCapacity);
    } else {
      newArray = array;
    }
    return newArray;
  }


}
