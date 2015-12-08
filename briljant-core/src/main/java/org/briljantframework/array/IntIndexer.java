package org.briljantframework.array;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public interface IntIndexer {

  /**
   * Return the end value of this indexer.
   * 
   * @return the end of this indexer
   */
  int end(int end);

  /**
   * Get the index at the position
   * 
   * @param i the position
   * @return an index
   */
  int get(int i);
}
