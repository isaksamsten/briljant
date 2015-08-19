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

import org.briljantframework.index.Index;
import org.briljantframework.vector.Vector;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Isak Karlsson
 */
public class HashIndex implements Index {

  Map<Object, Integer> keys = new HashMap<>();
  Map<Integer, Object> indexes = new HashMap<>();

  public HashIndex(Collection<?> coll) {
    Iterator<?> it = coll.iterator();
    for (int i = 0; it.hasNext(); i++) {
      Object next = it.next();
      if (keys.put(next, i) != null) {
        throw duplicateKey(next);
      }
      indexes.put(i, next);
    }
  }

  private HashIndex(Map<Object, Integer> keys, Map<Integer, Object> indexes) {
    this.keys = keys;
    this.indexes = indexes;
  }

  public static HashIndex from(Vector vector) {
    return from(vector.asList(Object.class));
  }

  @SafeVarargs
  public static <T> HashIndex from(T... args) {
    return from(Arrays.asList(args));
  }

  public static <T> HashIndex from(Collection<? extends T> coll) {
    return new HashIndex(coll);
  }

  private static UnsupportedOperationException duplicateKey(Object next) {
    return new UnsupportedOperationException(String.format("Duplicate key: %s", next));
  }

  private NoSuchElementException noSuchElement(Object key) {
    return new NoSuchElementException(String.format("name '%s' not in index", key));
  }

  @Override
  public int getLocation(Object key) {
    Integer idx = keys.get(key);
    if (idx == null) {
      throw noSuchElement(key);
    }
    return idx;
  }

  @Override
  public Object get(int index) {
    Object key = indexes.get(index);
    if (key == null) {
      if (indexes.containsKey(index)) {
        return null;
      }
      throw noSuchElement(index);
    }
    return key;
  }

  @Override
  public boolean contains(Object key) {
    return keys.containsKey(key);
  }

  @Override
  public Collection<Integer> locations() {
    return keys.values();
  }

  @Override
  public Set<Entry> entrySet() {
    return new AbstractSet<Entry>() {
      @Override
      public Iterator<Entry> iterator() {
        return new Iterator<Entry>() {

          Iterator<Map.Entry<Object, Integer>> it = keys.entrySet().iterator();

          @Override
          public boolean hasNext() {
            return it.hasNext();
          }

          @Override
          public Entry next() {
            Map.Entry<Object, Integer> next = it.next();
            return new Entry(next.getKey(), next.getValue());
          }
        };
      }

      @Override
      public int size() {
        return HashIndex.this.size();
      }
    };
  }

  @Override
  public Set<Object> keySet() {
    return keys.keySet();
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Index.Builder newCopyBuilder() {
    return new Builder(keys, indexes);
  }

  @Override
  public int size() {
    return keys.size();
  }

  @Override
  public int[] indices(Object[] keys) {
    int[] indicies = new int[keys.length];
    for (int i = 0; i < keys.length; i++) {
      indicies[i] = this.keys.get(keys[i]);
    }
    return indicies;
  }

  @Override
  public String toString() {
    return keys.toString();
  }

  public static class Builder implements Index.Builder {

    private Map<Object, Integer> keys;
    private Map<Integer, Object> indexes;
    private int currentSize = 0;


    public Builder() {
      this.keys = new HashMap<>();
      this.indexes = new HashMap<>();
    }

    private Builder(Map<Object, Integer> keys, Map<Integer, Object> indexes) {
      this.keys = new HashMap<>(keys);
      this.indexes = new HashMap<>(indexes);
    }

    @Override
    public boolean contains(Object key) {
      return keys.containsKey(key);//buffer.containsKey(key);
    }

    @Override
    public int index(Object key) {
      Integer val = keys.get(key);
      return val == null ? -1 : val;
    }

    @Override
    public Object get(int index) {
      return indexes.get(index);
    }

    @Override
    public void add(Object key) {
      set(key, currentSize);
    }

    @Override
    public void set(Object key, int index) {
      if (keys.put(key, index) != null) {
        throw duplicateKey(key);
      }
      indexes.put(index, key);
      currentSize++;
    }

    @Override
    public void set(int key, int index) {
      set((Object) key, index);
    }

    @Override
    public void extend(int size) {
      if (size > currentSize) {
        for (int i = currentSize; i < size; i++) {
          set(i, i);
        }
        currentSize = size;
      }
    }

    @Override
    public Index build() {
      Index index = new HashIndex(keys, indexes);
      this.keys = null;
      this.indexes = null;
      return index;
    }

    @Override
    public void set(Entry entry) {
      set(entry.key(), entry.index());
    }

    @Override
    public void putAll(Set<Entry> entries) {
      entries.forEach(this::set);
    }

    @Override
    public void swap(int a, int b) {
      Object keyA = indexes.get(a);
      Object keyB = indexes.get(b);
      indexes.put(a, keyB);
      indexes.put(b, keyA);

      Integer tmp = keys.get(keyA);
      keys.put(keyA, keys.get(keyB));
      keys.put(keyB, tmp);
    }

    @Override
    public int size() {
      return keys.size();
    }

    @Override
    public void remove(int index) {
      keys.remove(indexes.get(index));
      indexes.remove(index);
    }
  }
}
