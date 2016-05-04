/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.index;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provides some common methods for indices and lists.
 * 
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
  public Index setLocation(int loc, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Index intersection(Index other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Index union(Index other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Index difference(Index other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Index drop(Collection<?> keys) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Index removeLocation(Collection<?> locations) {
    throw new UnsupportedOperationException();
  }

  @Override public Index removeLocation(int loc) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int[] locations(Collection<?> keys) {
    int[] indicies = new int[keys.size()];
    int i = 0;
    for (Object key : keys) {
      indicies[i++] = getLocation(key);
    }
    return indicies;
  }

  @Override
  public Iterator<Object> iterator() {
    return keySet().iterator();
  }

}
