package org.briljantframework.array;

import org.briljantframework.Check;

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
    public int get(int i) {
      Check.argument(i >= start() && i <= end(Integer.MAX_VALUE));
      return i;
    }

    @Override
    public int step() {
      return 1;
    }
  },
}
