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

package org.briljantframework.dataframe;

import org.briljantframework.sort.Swappable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Immutable index. {@link #iterator()} is guaranteed to return the keys in the index according
 * to the order which a similar list would, i.e. from position {@code 1} to position {@code
 * size()}.
 *
 * @author Isak Karlsson
 */
public interface Index extends List<Object> {

  int index(Object key);

  Object get(int index);

  Set<Object> keySet();

  Collection<Integer> indices();

  Set<Index.Entry> entrySet();

  Collection<Integer> indices(Object[] keys);

  Map<Integer, Object> indexMap();

  Builder newBuilder();

  Builder newCopyBuilder();

  int size();

  public final class Entry {

    private final Object key;
    private final int index;

    protected Entry(Object key, int index) {
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
    int index(Object key);

    Object get(int index);

    void add(Object key);

    void set(Object key, int index);

    Index build();

    void set(Entry entry);

    void putAll(Set<Entry> entries);

    int size();
  }
}
