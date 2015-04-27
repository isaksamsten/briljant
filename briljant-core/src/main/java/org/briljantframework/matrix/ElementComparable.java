package org.briljantframework.matrix;

/**
 * @author Isak Karlsson
 */
public interface ElementComparable<T> {

  int compare(int a, T from, int b);

  int compare(int ia, int ja, T from, int ib, int jb);

  int compare(int ia, int ja, int ib, int jb);

  /**
   * Compare value at {@code a} to value at {@code b}.
   *
   * @param a first index
   * @param b second index
   * @return the comparison
   * @see java.lang.Double#compare(double, double)
   * @see java.lang.Integer#compare(int, int)
   * @see java.lang.Boolean#compare(boolean, boolean)
   */
  int compare(int a, int b);
}
