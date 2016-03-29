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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import net.mintern.primitive.comparators.IntComparator;

import org.briljantframework.data.SortOrder;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.util.primitive.IntList;

/**
 * Index based on a HashMap.
 *
 * @author Isak Karlsson
 */
public final class ObjectIndex extends AbstractIndex {

  private final IterationOrderKeySet iterationOrderKeySet = new IterationOrderKeySet();
  private final Map<Object, Integer> keys;
  private final List<Object> locations;
  private final IntList order;

  private ObjectIndex(Collection<?> coll) {
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

  private ObjectIndex(Map<Object, Integer> keys, List<Object> locations, IntList order) {
    this.keys = Collections.unmodifiableMap(keys);
    this.locations = locations;
    this.order = order;
  }

  public static ObjectIndex of(Vector vector) {
    return of(vector.toList(Object.class));
  }

  public static <T> ObjectIndex of(Collection<? extends T> coll) {
    return new ObjectIndex(coll);
  }

  public static ObjectIndex of(Object... args) {
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

  protected boolean shareComparableSupertype(Object a, Object b) {
    return a instanceof Comparable && b instanceof Comparable && shareSupertype(a, b);
  }

  private boolean shareSupertype(Object a, Object b) {
    Class<?> clsA = a.getClass();
    Class<?> clsB = b.getClass();
    return clsA.equals(clsB) || clsA.isAssignableFrom(clsB) || clsB.isAssignableFrom(clsA);
  }

  @SuppressWarnings("unchecked")
  private int compare(Object a, Object b) {
    return ((Comparable) a).compareTo(b);
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
    if (other instanceof ObjectIndex) {
      ObjectIndex entries = (ObjectIndex) other;
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

    private final static Comparator<Object> objectComparator = ObjectComparator.getInstance();
    private Map<Object, Integer> keys;
    private List<Object> locations;
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
    public int getLocation(Object key) {
      Integer v = getKeys().get(key);
      if (v == null) {
        throw noSuchElement(key);
      }
      return v;
    }

    @Override
    public Object get(int index) {
      return locations.get(order.get(index));
    }

    @Override
    public void add(Object key) {
      set(key, currentSize);
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
      sort(order.orderComparator(objectComparator));
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
          remove(i);
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
      Map<Object, Integer> immutableKeys = Collections.unmodifiableMap(getKeys());
      Index index = new ObjectIndex(immutableKeys, Collections.unmodifiableList(locations), order);
      this.keys = null;
      this.locations = null;
      this.order = null;
      return index;
    }

    @Override
    public int size() {
      return getKeys().size();
    }

    @Override
    public void remove(int index) {
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
      return ObjectIndex.this.size();
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
      return ObjectIndex.this.size();
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
