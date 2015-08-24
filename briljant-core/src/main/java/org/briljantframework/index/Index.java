/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.index;

import org.briljantframework.sort.Swappable;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Immutable index.
 *
 * @author Isak Karlsson
 */
public interface Index extends Iterable<Index.Entry> {

  int getLocation(Object key);

  Object getKey(int index);

  boolean contains(Object key);

  Set<Object> keySet();

  Collection<Integer> locations();

  Set<Index.Entry> entrySet();

  int[] indices(Object[] keys);

  Builder newBuilder();

  Builder newCopyBuilder();

  @Override
  default Iterator<Entry> iterator() {
    return entrySet().iterator();
  }

  default List<Object> asList() {
    return new AbstractList<Object>() {
      @Override
      public Object get(int index) {
        return getKey(index);
      }

      @Override
      public int size() {
        return Index.this.size();
      }
    };
  }

  int size();

  public final class Entry {

    private final Object key;
    private final int index;

    public Entry(Object key, int index) {
      this.key = key;
      this.index = index;
    }

    public Object key() {
      return key;
    }

    public int index() {
      return index;
    }

    @Override
    public String toString() {
      return "Entry{" +
             "key=" + key +
             ", index=" + index +
             '}';
    }
  }

  public interface Builder extends Swappable {

    boolean contains(Object key);

    /**
     * Returns the value associated with {@code key}
     *
     * @param key the key
     * @return value {@code > 0} if {@code key} exists or {@code -1} otherwise.
     */
    int getLocation(Object key);

    Object getKey(int index);

    void add(Object key);

    void add(int key);

    void sort(Comparator<Object> cmp);

    void sort();

    void extend(int size);

    Index build();

    int size();

    void remove(int index);
  }
}
