package org.briljantframework.matrix;

import org.briljantframework.Utils;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public final class Ints {

  private Ints() {}

  /**
   * Returns a pseudo-random number between min and max, inclusive. The difference between min and
   * max can be at most <code>Integer.MAX_VALUE - 1</code>.
   *
   * @param min Minimum value
   * @param max Maximum value. Must be greater than min.
   * @return Integer between min and max, inclusive.
   * @see java.util.Random#nextInt(int)
   */
  public static int randInt(int min, int max) {
    return Utils.getRandom().nextInt((max - min) + 1) + min;
  }

  public static IntMatrix newMatrix(int... values) {
    return new ArrayIntMatrix(values.length, 1).assign(values);
  }

  public static IntMatrix zeros(int size) {
    return new ArrayIntMatrix(size, 1);
  }

  public static IntMatrix zeros(int rows, int columns) {
    return new ArrayIntMatrix(rows, columns);
  }

  public static IntMatrix range(int start, int end) {
    return range(start, end, 1);
  }

  public static IntMatrix range(int start, int end, int step) {
    int i = end - start;
    int[] values = new int[i / step + (i % step != 0 ? 1 : 0)];
    int index = 0;
    while (index < values.length) {
      values[index++] = start;
      start += step;
    }
    return ArrayIntMatrix.wrap(values);
  }

  public static IntMatrix take(IntMatrix a, IntMatrix b) {
    return Anys.take(a, b).asIntMatrix();
  }
}
