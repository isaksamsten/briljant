package org.briljantframework.matrix;

/**
 * Created by isak on 15/04/15.
 */
public interface Range extends IntMatrix {

  int start();

  int end();

  int step();

  boolean contains(int value);
}
