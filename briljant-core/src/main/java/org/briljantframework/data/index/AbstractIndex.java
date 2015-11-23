package org.briljantframework.data.index;

import java.util.Iterator;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
abstract class AbstractIndex implements Index {

  @Override
  public Iterator<Object> reverseIterator() {
    return new Iterator<Object>() {
      private int current = size() - 1;

      @Override
      public boolean hasNext() {
        return current >= 0;
      }

      @Override
      public Object next() {
        return getKey(current--);
      }
    };
  }
}
