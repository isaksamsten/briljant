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
package org.briljantframework.util.primitive;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilities for allocating, growing and manipulating primitive arrays
 * 
 * @author Isak Karlsson
 */
public final class ArrayAllocations {

  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

  private ArrayAllocations() {}

  /**
   * Alters the current size of the vector if the supplied size is larger than the current.
   */
  public static boolean[] ensureCapacity(final boolean[] buffer, final int newSize) {

    if (newSize - buffer.length > 0) {
      return grow(buffer, newSize);
    }
    return buffer;
  }

  /**
   * From {@link java.util.ArrayList}
   */
  private static boolean[] grow(final boolean[] buffer, int minCapacity) {
    // overflow-conscious code
    int oldCapacity = buffer.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0) {
      newCapacity = minCapacity;
    }
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
      newCapacity = hugeCapacity(minCapacity);
    }
    // minCapacity is usually close to size, so this is a win:
    return Arrays.copyOf(buffer, newCapacity);
  }

  /**
   * From {@link java.util.ArrayList}
   */
  private static int hugeCapacity(int minCapacity) {
    if (minCapacity < 0) { // overflow
      throw new OutOfMemoryError();
    }
    return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
  }

  public static <T> T[] prepend(T element, T[] array) {
    Class<?> type;
    if (array != null) {
      type = array.getClass().getComponentType();
    } else if (element != null) {
      type = element.getClass();
    } else {
      throw new IllegalArgumentException("Arguments cannot both be null");
    }
    @SuppressWarnings("unchecked")
    // type must be T
    final T[] newArray = (T[]) copyArrayGrowMove1(array, type);
    newArray[0] = element;
    return newArray;
  }

  /**
   * Returns a copy of the given array of size 1 greater than the argument. The first value is left
   * to default value.
   *
   * @param array The array to copy, must not be {@code null}.
   * @param newArrayComponentType If {@code array} is {@code null}, create a size 1 array of this
   *        type.
   * @return A new copy of the array of size 1 greater than the input.
   */
  private static Object copyArrayGrowMove1(final Object array, final Class<?> newArrayComponentType) {
    if (array != null) {
      final int arrayLength = Array.getLength(array);
      final Object newArray =
          Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
      System.arraycopy(array, 0, newArray, 1, arrayLength);
      return newArray;
    }
    return Array.newInstance(newArrayComponentType, 1);
  }

  /**
   * Alters the current size of the vector if the supplied size is larger than the current.
   */
  public static long[] ensureCapacity(final long[] buffer, final int newSize) {
    if (newSize - buffer.length > 0) {
      return grow(buffer, newSize);
    }
    return buffer;
  }

  /**
   * From {@link java.util.ArrayList}
   */
  private static long[] grow(final long[] buffer, int minCapacity) {
    // overflow-conscious code
    int oldCapacity = buffer.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0) {
      newCapacity = minCapacity;
    }
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
      newCapacity = hugeCapacity(minCapacity);
    }
    // minCapacity is usually close to size, so this is a win:
    return Arrays.copyOf(buffer, newCapacity);
  }

  /**
   * Alters the current size of the vector if the supplied size is larger than the current.
   */
  public static int[] ensureCapacity(final int[] buffer, final int newSize) {
    if (newSize - buffer.length > 0) {
      return grow(buffer, newSize);
    }
    return buffer;
  }

  /**
   * From {@link java.util.ArrayList}
   */
  private static int[] grow(final int[] buffer, int minCapacity) {
    // overflow-conscious code
    int oldCapacity = buffer.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0) {
      newCapacity = minCapacity;
    }
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
      newCapacity = hugeCapacity(minCapacity);
    }
    // minCapacity is usually close to size, so this is a win:
    return Arrays.copyOf(buffer, newCapacity);
  }

  /**
   * Alters the current size of the vector if the supplied size is larger than the current.
   */
  public static double[] ensureCapacity(final double[] buffer, final int newSize) {
    if (newSize - buffer.length > 0) {
      return grow(buffer, newSize);
    }
    return buffer;
  }

  /**
   * From {@link java.util.ArrayList}
   */
  private static double[] grow(final double[] buffer, int minCapacity) {
    // overflow-conscious code
    int oldCapacity = buffer.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0) {
      newCapacity = minCapacity;
    }
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
      newCapacity = hugeCapacity(minCapacity);
    }
    // minCapacity is usually close to size, so this is a win:
    return Arrays.copyOf(buffer, newCapacity);
  }

  /**
   * The Fisher–Yates shuffle (named after Ronald Fisher and Frank Yates), also known as the Knuth
   * shuffle (after Donald Knuth), is an algorithm for generating a random permutation of a finite
   * set — in plain terms, for randomly shuffling the set.
   * <p>
   * Code from method {@link java.util.Collections#shuffle(java.util.List)}
   *
   * @param array the array
   */
  public static void shuffle(int[] array) {
    Random random = ThreadLocalRandom.current();
    int count = array.length;
    for (int i = count; i > 1; i--) {
      swap(array, i - 1, random.nextInt(i));
    }
  }

  /**
   * Swap values {@code i} and {@code j} in {@code array}
   *
   * @param array the array
   * @param i the i
   * @param j the j
   */
  public static void swap(int[] array, int i, int j) {
    int temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }

  /**
   * Swap values {@code i} and {@code j} in {@code array}
   *
   * @param array the array
   * @param i the i
   * @param j the j
   */
  public static void swap(double[] array, int i, int j) {
    double temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }
}
