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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterators;

import org.briljantframework.vector.Vector;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Isak Karlsson
 */
public class HashIndex extends AbstractList<Object> implements Index {

  BiMap<Object, Integer> biMap;

  public HashIndex(Collection<?> coll) {
    biMap = HashBiMap.create(coll.size());
    Iterator<?> it = coll.iterator();
    for (int i = 0; it.hasNext(); i++) {
      Object next = it.next();
      if (biMap.put(next, i) != null) {
        throw duplicateKey(next);
      }
    }
  }

  public HashIndex(BiMap<Object, Integer> biMap) {
    this.biMap = HashBiMap.create(biMap);
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
  public int index(Object key) {
    Integer idx = biMap.get(key);
    if (idx == null) {
      throw noSuchElement(key);
    }
    return idx;
  }

  @Override
  public Object get(int index) {
    Object key = biMap.inverse().get(index);
    if (key == null) {
      if (biMap.inverse().containsKey(index)) {
        return null;
      }
      throw noSuchElement(index);
    }
    return key;
  }

  @Override
  public boolean contains(Object key) {
    return biMap.containsKey(key);
  }

  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      public int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Object next() {
        return get(current++);
      }
    };
  }

  @Override
  public Collection<Integer> indices() {
    return biMap.values();
  }

  @Override
  public Set<Entry> entrySet() {
    return new AbstractSet<Entry>() {
      @Override
      public Iterator<Entry> iterator() {
        return new Iterator<Entry>() {

          Iterator<Map.Entry<Object, Integer>> it = biMap.entrySet().iterator();

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
    return biMap.keySet();
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public int size() {
    return biMap.size();
  }

  @Override
  public Collection<Integer> indices(Object[] keys) {
    return new AbstractCollection<Integer>() {
      @Override
      public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
          private int current = 0;

          @Override
          public boolean hasNext() {
            return current < size();
          }

          @Override
          public Integer next() {
            return biMap.get(keys[current++]);
          }
        };
      }

      @Override
      public int size() {
        return keys.length;
      }
    };
  }

  @Override
  public Map<Integer, Object> indexMap() {
    return Collections.unmodifiableMap(biMap.inverse());
  }

//  /**
//   * Returns an iterator over elements of type {@code T}.
//   *
//   * @return an Iterator.
//   */
//  @Override
//  public Iterator<Index.Entry> iterator() {
//    return new Iterator<Entry>() {
//
//      Iterator<Map.Entry<Object, Integer>> it = hash.entrySet().iterator();
//
//      @Override
//      public boolean hasNext() {
//        return it.hasNext();
//      }
//
//      @Override
//      public Entry next() {
//        Map.Entry<Object, Integer> next = it.next();
//        return new Entry(next.getKey(), next.getValue());
//      }
//    };
//  }

  @Override
  public String toString() {
    return Iterators.toString(iterator());
  }

  public static class Builder implements Index.Builder {

    private BiMap<Object, Integer> buffer;
    private int currentSize = 0;

    public Builder() {
      this.buffer = HashBiMap.create();
    }

    @Override
    public boolean contains(Object key) {
      return buffer.containsKey(key);
    }

    @Override
    public int index(Object key) {
      Integer val = buffer.get(key);
      return val == null ? -1 : val;
    }

    @Override
    public Object get(int index) {
      return buffer.inverse().get(index);
    }

    @Override
    public void add(Object key) {
      set(key, currentSize);
    }

    @Override
    public void set(Object key, int index) {
      if (buffer.put(key, index) != null) {
        throw duplicateKey(key);
      }
      currentSize++;
    }

    @Override
    public Index build() {
      return new HashIndex(buffer);
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
      BiMap<Integer, Object> reverse = buffer.inverse();
      Object keyA = reverse.get(a);
      Object keyB = reverse.get(b);
      reverse.put(a, keyB);
      reverse.put(b, keyA);
      Integer tmp = buffer.get(keyA);
      buffer.put(keyA, buffer.get(keyB));
      buffer.put(keyB, tmp);
    }

    @Override
    public int size() {
      return buffer.size();
    }
  }
}
