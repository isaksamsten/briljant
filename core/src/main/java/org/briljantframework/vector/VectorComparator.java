package org.briljantframework.vector;

/**
 * @author Isak Karlsson
 */
@FunctionalInterface
public interface VectorComparator {

  /**
   * <p>
   * Returns the comparison of value at index {@code a} and value at {@code b} in {@code v}.
   * </p>
   * 
   * <p>
   * Not {@link org.briljantframework.vector.VectorLike} might throw
   * {@link java.lang.UnsupportedOperationException} or return strange values if a particular method
   * is not implemented.
   * </p>
   * 
   * @param v the vector
   * @param a the index
   * @param b the index
   * @return the comparison
   */
  int compare(VectorLike v, int a, int b);
}
