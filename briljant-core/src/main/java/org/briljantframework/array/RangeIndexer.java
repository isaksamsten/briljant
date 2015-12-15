package org.briljantframework.array;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public interface RangeIndexer {

  int step();

  int start();

  int end(int size);
}
