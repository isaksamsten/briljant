package org.briljantframework;

/**
 * @author Isak Karlsson
 */
public interface Swappable {

  /**
   * Swaps, in for example a list, the value at position {@code a} and {@code b}.
   *
   * @param a the first index
   * @param b the second index
   */
  void swap(int a, int b);
}
