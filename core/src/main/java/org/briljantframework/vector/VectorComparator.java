package org.briljantframework.vector;

/**
 * @author Isak Karlsson
 */
@FunctionalInterface
public interface VectorComparator {

  /**
   * Compare value at index {@code a} to value at index {@code b}.
   * 
   * @param vec the vector
   * @param a the first index
   * @param b the second index
   * @return -1 if value at {@code a} is greater than value at {@code b}, 0 if equal and 1 if
   *         reversed.
   */
  int compare(Vector vec, int a, int b);
}
