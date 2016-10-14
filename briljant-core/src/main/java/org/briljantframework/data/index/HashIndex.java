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

import java.io.Serializable;
import java.util.*;

import org.briljantframework.data.SortOrder;
import org.briljantframework.data.series.Series;
import org.briljantframework.util.primitive.IntList;

import net.mintern.primitive.comparators.IntComparator;

/**
 * Index based on a HashMap.
 *
 * @author Isak Karlsson
 */
public final class HashIndex extends AbstractIndex implements Serializable {

  private final transient IterationOrderKeySet iterationOrderKeySet = new IterationOrderKeySet();
  private final Map<Object, Integer> keys;
  private final ArrayList<Object> locations;
  private final IntList order;

  private HashIndex(Collection<?> coll) {
    keys = new HashMap<>(coll.size());
    locations = new ArrayList<>(coll.size());
    order = new IntList(coll.size());
    Iterator<?> it = coll.iterator();
    for (int i = 0; it.hasNext(); i++) {
      Object next = it.next();
      if (getKeys().put(next, i) != null) {
        throw duplicateKey(next);
      }
      locations.add(next);
      order.add(i);
    }
  }

  protected Map<Object, Integer> getKeys() {
    return keys;
  }

  private HashIndex(Map<Object, Integer> keys, ArrayList<Object> locations, IntList order) {
    this.keys = Collections.unmodifiableMap(keys);
    this.locations = locations;
    this.order = order;
  }

  public static HashIndex of(Series series) {
    return of(series.values());
  }

  public static HashIndex of(Collection<?> coll) {
    return new HashIndex(coll);
  }

  public static HashIndex of(Object... args) {
    return of(Arrays.asList(args));
  }

  @Override
  public int getLocation(Object key) {
    Integer idx = getKeys().get(key);
    if (idx == null) {
      throw noSuchElement(key);
    }
    return idx;
  }

  @Override
  public Set<Object> keySet() {
    if (keys instanceof NavigableMap) {
      return keys.keySet();
    } else {
      return iterationOrderKeySet;
    }
  }

  @Override
  public Collection<Integer> locations() {
    return getKeys().values();
  }

  @Override
  public Set<Entry> indexSet() {
    return new IterationOrderEntrySet();
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Index.Builder newCopyBuilder() {
    return new Builder(getKeys(), locations, order);
  }

  @Override
  public Object get(int location) {
    return locations.get(order.get(location));
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof HashIndex) {
      HashIndex entries = (HashIndex) other;
      return getKeys().keySet().equals(entries.getKeys().keySet());
    } else if (other instanceof Index) {
      Index index = (Index) other;
      for (Object key : getKeys().keySet()) {
        if (!index.contains(key)) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return getKeys().hashCode();
  }

  public static final class Builder implements Index.Builder {

    private final static Comparator<Object> naturalOrdering = NaturalOrdering.ascending();
    private Map<Object, Integer> keys;
    private ArrayList<Object> locations;
    private IntList order;
    private int currentSize = 0;


    public Builder() {
      this.keys = new HashMap<>();
      this.locations = new ArrayList<>();
      this.order = new IntList();
    }

    private Builder(Map<Object, Integer> keys, List<Object> locations, IntList order) {
      this.keys = new HashMap<>(keys);
      this.locations = new ArrayList<>(locations);
      this.order = new IntList(order);
      this.currentSize = this.locations.size();
    }

    @Override
    public boolean contains(Object key) {
      return getKeys().containsKey(key);
    }

    @Override
    public boolean retainAll(Collection<?> keys) {
      for (Object key : getKeys().keySet()) {
        if (!keys.contains(key)) {
          remove(key);
        }
      }
      return true;
    }

    @Override
    public boolean removeAll(Collection<?> keys) {
      for (Object key : keys) {
        remove(key);
      }
      return true;
    }

    @Override
    public Object remove(Object key) {
      int index = getLocation(key);
      removeLocation(index);
      return true;
    }

    @Override
    public void removeLocation(int index) {
      getKeys().remove(locations.remove(index));
      for (int i = index; i < size(); i++) {
        Object key = locations.get(i);
        getKeys().compute(key, (k, v) -> v - 1);
      }

      for (ListIterator<Integer> iterator = order.listIterator(); iterator.hasNext();) {
        int v = iterator.next();
        if (v > index) {
          iterator.set(v - 1);
        } else if (v == index) {
          iterator.remove();
        }
      }
    }

    @Override
    public int getLocation(Object key) {
      Integer v = getKeys().get(key);
      if (v == null) {
        throw noSuchElement(key);
      }
      return v;
    }

    @Override
    public boolean add(Object key) {
      set(key, currentSize);
      return true;
    }

    @Override
    public void add(int key) {
      set(key, currentSize);
    }

    private void set(int key, int index) {
      set((Object) key, index);
    }

    @Override
    public void sortIterationOrder(IntComparator cmp) {
      order.primitiveSort(cmp);
    }

    @Override
    public void sort(Comparator<Object> cmp) {
      sortIterationOrder((a, b) -> cmp.compare(locations.get(a), locations.get(b)));
    }

    @Override
    public void sort(SortOrder order) {
      sort(order.orderComparator(naturalOrdering));
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
    public void resize(int size) {
      if (size < currentSize) {
        for (int i = currentSize - 1; i >= size; i--) {
          removeLocation(i);
        }
      } else {
        extend(size);
      }
    }

    @Override
    public Index build() {
      if (keys == null) {
        throw new IllegalStateException("Can't reuse builder");
      }
      Map<Object, Integer> immutableKeys = getKeys();
      Index index = new HashIndex(immutableKeys, locations, order);
      this.keys = null;
      this.locations = null;
      this.order = null;
      return index;
    }

    @Override
    public int size() {
      return getKeys().size();
    }

    private void set(Object key, int index) {
      if (getKeys().put(key, index) != null) {
        throw duplicateKey(key);
      }
      locations.add(key);
      order.add(index);
      currentSize++;
    }

    protected Map<Object, Integer> getKeys() {
      return keys;
    }

    @Override
    public void swap(int a, int b) {
      Collections.swap(order, a, b);
    }
  }

  private class IterationOrderEntrySet extends AbstractSet<Entry> {

    @Override
    public Iterator<Entry> iterator() {
      return new Iterator<Entry>() {
        int current = 0;

        @Override
        public boolean hasNext() {
          return current < size();
        }

        @Override
        public Entry next() {
          int value = order.get(current);
          Object key = get(current++);
          return new Entry(key, value);
        }
      };
    }

    @Override
    public int size() {
      return HashIndex.this.size();
    }
  }

  private class IterationOrderKeySet extends AbstractSet<Object> {

    @Override
    public Iterator<Object> iterator() {
      return new Iterator<Object>() {
        private int current = 0;

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
    public int size() {
      return HashIndex.this.size();
    }
  }

  @Override
  public boolean contains(Object key) {
    return getKeys().containsKey(key);
  }

  @Override
  public int size() {
    return getKeys().size();
  }

  @Override
  public String toString() {
    return getKeys().toString();
  }
}
