package org.briljantframework.array;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public interface RangeIndexer extends IntIndexer {

  int step();

  int start();

  @Override
  int end(int size);
}
