package org.briljantframework.array;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public enum ArraySelector implements RangeIndexer {
  ALL {
    @Override
    public int start() {
      return 0;
    }

    @Override
    public int end(int size) {
      return size;
    }

    @Override
    public int step() {
      return 1;
    }
  },
}
