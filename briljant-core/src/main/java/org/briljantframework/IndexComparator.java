package org.briljantframework;

/**
 * Comparator to compare values in containers of unknown values.
 *
 * @author Isak Karlsson
 */
public interface IndexComparator<T> {

  int compare(T c, int a, int b);
}
