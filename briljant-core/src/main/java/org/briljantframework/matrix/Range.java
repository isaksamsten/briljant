package org.briljantframework.matrix;

/**
 * <p> {@linkplain #copy()} returns a mutable {@code IntMatrix}
 *
 * @author Isak Karlsson
 */
public interface Range extends IntMatrix {

  int start();

  int end();

  int step();

  boolean contains(int value);
}
