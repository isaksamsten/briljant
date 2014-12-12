package org.briljantframework;

import java.util.Arrays;

/**
 * Created by Isak Karlsson on 09/12/14.
 */
public final class ArrayBuffers {
  private ArrayBuffers() {}

  // public static long allocations = 0;

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

  public static double[] reallocate(double[] array, int minCapacity) {
    int oldCapacity = array.length;
    double[] newArray;
    if (oldCapacity < minCapacity) {
      // int newCapacity = (oldCapacity * 3) / 2 + 1;
      // if (newCapacity < minCapacity) {
      // newCapacity = minCapacity;
      // }
      newArray = new double[minCapacity];
    } else {
      newArray = new double[array.length];
    }
    // System.out.printf("Allocate new array with %d elements%n", newArray.length);
    // allocations++;
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
