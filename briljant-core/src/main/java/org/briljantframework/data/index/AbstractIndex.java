package org.briljantframework.data.index;

import java.util.AbstractList;
import java.util.Iterator;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
abstract class AbstractIndex extends AbstractList<Object> implements Index {

  @Override
  public Iterator<Object> iterator() {
    return keySet().iterator();
  }

}
