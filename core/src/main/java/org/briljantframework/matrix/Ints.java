package org.briljantframework.matrix;

import org.briljantframework.Utils;

/**
 * @author Isak Karlsson
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

}
