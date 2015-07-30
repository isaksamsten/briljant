package org.briljantframework.array;

/**
 * @author Isak Karlsson
 */
public interface Range extends IntArray {

  /**
   * The start value of this range
   *
   * @return the start value
   */
  int start();

  /**
   * The end value of this range
   *
   * @return the end value
   */
  int end();

  /**
   * The step size of this range
   *
   * @return the step size
   */
  int step();

  /**
   * Determines if the given value is included in the range
   *
   * @param value the value
   * @return true if the given value is included
   */
  boolean contains(int value);
}
