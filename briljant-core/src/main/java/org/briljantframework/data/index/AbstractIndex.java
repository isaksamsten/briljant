package org.briljantframework.data.index;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
abstract class AbstractIndex extends AbstractList<Object> implements Index {

  protected static UnsupportedOperationException duplicateKey(Object next) {
    return new UnsupportedOperationException(String.format("Duplicate key: %s", next));
  }

  protected static NoSuchElementException noSuchElement(Object key) {
    return new NoSuchElementException(String.format("name '%s' not in index", key));
  }


  @Override
  public int[] locations(Object[] keys) {
    int[] indicies = new int[keys.length];
    for (int i = 0; i < keys.length; i++) {
      indicies[i] = getLocation(keys[i]);
    }
    return indicies;
  }

  @Override
  public Iterator<Object> iterator() {
    return keySet().iterator();
  }

}
