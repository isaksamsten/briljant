package org.briljantframework.dataframe.join;

/**
 * Created by Isak on 2015-01-08.
 */
public interface Joiner {

  /**
   * Returns the size of the joiner.
   * 
   * @return the size
   */
  int size();

  /**
   * Get the index for the left side of a join. Returns {@code -1}, if the index should not be
   * included.
   * 
   * @param i the index {@code 0 ... size()}
   * @return the index in the resulting container
   */
  int getLeftIndex(int i);

  /**
   * Get the index for the left side of a join. Returns {@code -1}, if the index should not be
   * included.
   *
   * @param i the index {@code 0 ... size()}
   * @return the index in the resulting container
   */
  int getRightIndex(int i);
}
