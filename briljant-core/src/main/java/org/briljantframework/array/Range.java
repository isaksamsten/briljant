package org.briljantframework.array;

/**
 * <p> {@linkplain #copy()} returns a mutable {@code IntMatrix}
 *
 * @author Isak Karlsson
 */
public interface Range extends IntArray {

  int start();

  int end();

  int step();

  boolean contains(int value);
}
