package org.briljantframework.dataframe;

import com.google.common.collect.Iterators;

import org.briljantframework.vector.Vector;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Isak Karlsson
 */
public class HashIndex extends AbstractList<Object> implements Index {

  private final Map<Object, Integer> hash;
  private final Map<Integer, Object> reverse;

  private HashIndex(Map<Object, Integer> hash, Map<Integer, Object> reverse) {
    this.hash = hash;
    this.reverse = reverse;
  }

  public static HashIndex from(Vector vector) {
    return from(vector.asList(Object.class));
  }

  public static <T extends Comparable<T>> HashIndex sorted(Iterable<? extends T> coll) {
    Map<Object, Integer> hash = new TreeMap<>();
    Map<Integer, Object> reverse = new HashMap<>();
    Iterator<? extends T> it = coll.iterator();
    for (int i = 0; it.hasNext(); i++) {
      T next = it.next();
      if (hash.put(next, i) != null) {
        throw duplicateKey(next);
      }
      reverse.put(i, next);
    }
    return new HashIndex(hash, reverse);
  }

  public static HashIndex sorted(Index index, Comparator<Object> order) {
    Map<Object, Integer> hash = new TreeMap<>(order);
    Map<Integer, Object> reverse = new HashMap<>();
    for (Entry entry : index.entrySet()) {
      hash.put(entry.key(), entry.index());
      reverse.put(entry.index(), entry.key());
    }
    return new HashIndex(hash, reverse);
  }

  @SafeVarargs
  public static <T extends Comparable<T>> HashIndex sorted(T... values) {
    return sorted(Arrays.asList(values));
  }

  public static HashIndex sorted(Vector vector) {
    return sorted(vector.asList(Comparable.class));
  }

  @SafeVarargs
  public static <T> HashIndex from(T... args) {
    return from(Arrays.asList(args));
  }

  public static <T> HashIndex from(Collection<? extends T> coll) {
    Map<Object, Integer> hash = new HashMap<>();
    Map<Integer, Object> reverse = new HashMap<>();
    Iterator<?> it = coll.iterator();
    for (int i = 0; it.hasNext(); i++) {
      Object next = it.next();
      if (hash.put(next, i) != null) {
        throw duplicateKey(next);
      }
      reverse.put(i, next);
    }
    return new HashIndex(hash, reverse);
  }

  private static UnsupportedOperationException duplicateKey(Object next) {
    return new UnsupportedOperationException(String.format("Duplicate key: %s", next));
  }

  private NoSuchElementException noSuchElement(Object key) {
    return new NoSuchElementException(String.format("name '%s' not in index", key));
  }

  @Override
  public int index(Object key) {
    Integer idx = hash.get(key);
    if (idx == null) {
      throw noSuchElement(key);
    }
    return idx;
  }

  @Override
  public Object get(int index) {
    Object key = reverse.get(index);
    if (key == null) {
      if (reverse.containsKey(index)) {
        return null;
      }
      throw noSuchElement(index);
    }
    return key;
  }

  @Override
  public boolean contains(Object key) {
    return hash.containsKey(key);
  }

  @Override
  public Iterator<Object> iterator() {
    return hash.keySet().iterator();
  }

  @Override
  public Collection<Integer> indices() {
    return hash.values();
  }

  @Override
  public Set<Entry> entrySet() {
    return new AbstractSet<Entry>() {
      @Override
      public Iterator<Entry> iterator() {
        return new Iterator<Entry>() {

          Iterator<Map.Entry<Object, Integer>> it = hash.entrySet().iterator();

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
    return hash.keySet();
  }

  @Override
  public Builder newBuilder() {
    return new Builder(hash instanceof TreeMap);
  }

  @Override
  public int size() {
    return hash.size();
  }

  @Override
  public Index copy() {
    return new HashIndex(hash instanceof TreeMap ? new TreeMap<>(hash) : new HashMap<>(hash),
                         reverse);
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
            return hash.get(keys[current++]);
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
    return Collections.unmodifiableMap(reverse);
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

    private final Map<Object, Integer> buffer;
    private final Map<Integer, Object> reverse;
    private int current = 0;

    /**
     * @param sorted if true creates a sorted index builder
     */
    public Builder(boolean sorted) {
      this.buffer = sorted ? new TreeMap<>() : new HashMap<>();
      this.reverse = new HashMap<>();
    }

    public Builder() {
      this(false);
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
      return reverse.get(index);
    }

    @Override
    public void add(Object key) {
      set(key, current++);
    }

    @Override
    public void set(Object key, int index) {
      if (buffer.put(key, index) != null) {
        throw duplicateKey(key);
      }
      reverse.put(index, key);
    }

    @Override
    public Index build() {
      return new HashIndex(buffer, reverse);
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
