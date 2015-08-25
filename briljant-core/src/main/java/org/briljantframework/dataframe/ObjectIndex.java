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

import org.briljantframework.index.HeterogeneousObjectComparator;
import org.briljantframework.index.Index;
import org.briljantframework.vector.Vector;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Isak Karlsson
 */
public final class ObjectIndex implements Index {

  private Map<Object, Integer> keys = new LinkedHashMap<>();
  private List<Object> indexes = new ArrayList<>();

  public ObjectIndex(Collection<?> coll) {
    Iterator<?> it = coll.iterator();
    for (int i = 0; it.hasNext(); i++) {
      Object next = it.next();
      if (keys.put(next, i) != null) {
        throw duplicateKey(next);
      }
      indexes.add(next);
    }
  }

  private ObjectIndex(Map<Object, Integer> keys, List<Object> indexes) {
    this.keys = keys;
    this.indexes = indexes;
  }

  public static ObjectIndex from(Vector vector) {
    return from(vector.asList(Object.class));
  }

  @SafeVarargs
  public static <T> ObjectIndex from(T... args) {
    return from(Arrays.asList(args));
  }

  public static <T> ObjectIndex from(Collection<? extends T> coll) {
    return new ObjectIndex(coll);
  }

  private static UnsupportedOperationException duplicateKey(Object next) {
    return new UnsupportedOperationException(String.format("Duplicate key: %s", next));
  }

  private static NoSuchElementException noSuchElement(Object key) {
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
  public Object getKey(int location) {
    return indexes.get(location);
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
        return ObjectIndex.this.size();
      }
    };
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    ObjectIndex entries = (ObjectIndex) object;
    return indexes.equals(entries.indexes) && keys.equals(entries.keys);
  }

  @Override
  public int hashCode() {
    int result = keys.hashCode();
    result = 31 * result + indexes.hashCode();
    return result;
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

  public static final class Builder implements Index.Builder {

    private final Comparator<Object> objectComparator = new HeterogeneousObjectComparator();
    private Map<Object, Integer> keys;
    private List<Object> indexes;
    private int currentSize = 0;


    public Builder() {
      this.keys = new LinkedHashMap<>();
      this.indexes = new ArrayList<>();
    }

    private Builder(Map<Object, Integer> keys, List<Object> indexes) {
      this.keys = new LinkedHashMap<>(keys);
      this.indexes = new ArrayList<>(indexes);
    }

    @Override
    public boolean contains(Object key) {
      return keys.containsKey(key);
    }

    @Override
    public int getLocation(Object key) {
      return keys.get(key); // Non-null
    }

    @Override
    public Object getKey(int index) {
      return indexes.get(index);
    }

    @Override
    public void add(Object key) {
      set(key, currentSize);
    }

    @Override
    public void add(int key) {
      set(key, currentSize);
    }

    @Override
    public void sort(Comparator<Object> cmp) {
      Map<Object, Integer> treeMap = new TreeMap<>(cmp);
      treeMap.putAll(keys);
      this.keys = treeMap;
    }

    @Override
    public void sort() {
      sort(objectComparator);
    }

    private void set(Object key, int index) {
      if (keys.put(key, index) != null) {
        throw duplicateKey(key);
      }
      indexes.add(key);
      currentSize++;
    }

    private void set(int key, int index) {
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
      Index index = new ObjectIndex(
          Collections.unmodifiableMap(keys),
          Collections.unmodifiableList(indexes)
      );
      this.keys = null;
      this.indexes = null;
      return index;
    }

    @Override
    public void swap(int a, int b) {
      Object keyA = indexes.get(a);
      Object keyB = indexes.get(b);
      indexes.set(a, keyB);
      indexes.set(b, keyA);

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
      keys.remove(indexes.remove(index));
      for (int i = index; i < size(); i++) {
        Object key = indexes.get(i);
        keys.compute(key, (k, v) -> v - 1); // change index back one
      }
    }

  }

}
