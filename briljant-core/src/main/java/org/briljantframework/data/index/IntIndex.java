/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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


import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import net.mintern.primitive.comparators.IntComparator;

import org.briljantframework.data.SortOrder;

/**
 * @author Isak Karlsson
 */
public final class IntIndex extends AbstractIndex {

  private final int start;
  private final int size;

  public IntIndex(int start, int size) {
    this.start = start;
    this.size = size;
  }

  public int getStart() {
    return start;
  }

  public int getSize() {
    return size;
  }

  @Override
  public int getLocation(Object key) {
    if (key instanceof Integer) {
      int k = (int) key;
      if (k < size && k >= 0) {
        return k;
      }
    }
    throw noSuchElement(key);
  }

  @Override
  public Set<Object> keySet() {
    return new AbstractSet<Object>() {
      @Override
      public Iterator<Object> iterator() {
        return new Iterator<Object>() {
          public int current = 0;

          @Override
          public boolean hasNext() {
            return current < size;
          }

          @Override
          public Object next() {
            if (current >= size) {
              throw new NoSuchElementException();
            }
            return current++;
          }
        };
      }

      @Override
      public int size() {
        return size;
      }
    };
  }

  @Override
  public Collection<Integer> locations() {
    return new AbstractCollection<Integer>() {
      @Override
      public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
          public int current = start;

          @Override
          public boolean hasNext() {
            return current < size;
          }

          @Override
          public Integer next() {
            if (current >= size) {
              throw new NoSuchElementException();
            }
            return current++;
          }
        };
      }

      @Override
      public int size() {
        return size;
      }
    };
  }

  @Override
  public Set<Entry> indexSet() {
    return new AbstractSet<Entry>() {

      @Override
      public Iterator<Entry> iterator() {
        return new Iterator<Entry>() {
          private int current = 0;

          @Override
          public boolean hasNext() {
            return current < size();
          }

          @Override
          public Entry next() {
            int i = current++;
            return new Entry(i, i);
          }
        };
      }

      @Override
      public int size() {
        return IntIndex.this.size();
      }
    };
  }

  @Override
  public Index.Builder newBuilder() {
    return new Builder(0);
  }

  @Override
  public Index.Builder newCopyBuilder() {
    return new Builder(size());
  }

  @Override
  public Object get(int location) {
    if (location >= 0 && location < size) {
      return location;
    }
    throw noSuchElement(location);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof IntIndex) {
      IntIndex other = (IntIndex) obj;
      return size() == other.size();
    } else if (obj instanceof Index) {
      Index other = (Index) obj;
      for (int i = 0; i < size(); i++) {
        if (!other.contains(i)) {
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
    return size;
  }

  public static final class Builder implements Index.Builder {

    private ObjectIndex.Builder builder;
    private int currentSize;

    public Builder(int i) {
      this.currentSize = i;
    }

    @Override
    public boolean contains(Object key) {
      if (isIntegerKey(key)) {
        int k = (int) key;
        return k >= 0 && k < currentSize;
      } else {
        initializeHashBuilder();
        return builder.contains(key);
      }
    }

    private void initializeHashBuilder() {
      if (builder == null) {
        builder = new ObjectIndex.Builder();
        for (int i = 0; i < currentSize; i++) {
          builder.add(i);
        }
      }
    }

    @Override
    public int getLocation(Object key) {
      if (isIntegerKey(key)) {
        return (int) key;
      } else {
        initializeHashBuilder();
        return builder.getLocation(key);
      }
    }

    @Override
    public Object get(int index) {
      if (index > currentSize) {
        throw noSuchElement(index);
      }
      return builder == null ? index : builder.get(index);
    }

    @Override
    public void add(Object key) {
      set(key, currentSize);
    }

    @Override
    public void add(int key) {
      if (key > currentSize) {
        throw nonMonotonicallyIncreasingIndex(key);
      }

      if (!isMonotonicallyIncreasing(key)) {
        initializeHashBuilder();
        builder.add(key);
      }
      currentSize++;
      // set(key, currentSize);
    }

    private boolean isMonotonicallyIncreasing(int key) {
      return builder == null;
    }

    @Override
    public void sortIterationOrder(IntComparator cmp) {
      initializeHashBuilder();
      builder.sortIterationOrder(cmp);
    }

    @Override
    public void sort(Comparator<Object> cmp) {
      initializeHashBuilder();
      builder.sort(cmp);
    }

    @Override
    public void sort(SortOrder order) {
      if (builder != null && order == SortOrder.ASC) {
        builder.sort();
      } else {
        initializeHashBuilder();
        builder.sort(order);
      }
    }

    @Override
    public void extend(int size) {
      if (builder != null) {
        initializeHashBuilder();
        builder.extend(size);
      } else {
        if (size > currentSize) {
          currentSize = size;
        }
      }
    }

    @Override
    public void resize(int size) {
      if (builder != null) {
        initializeHashBuilder();
        builder.resize(size);
      } else {
        currentSize = size;
      }
    }

    @Override
    public Index build() {
      if (builder == null) {
        return new IntIndex(0, currentSize);
      } else {
        return builder.build();
      }
    }

    @Override
    public int size() {
      return builder == null ? currentSize : builder.size();
    }

    @Override
    public void remove(int index) {
      initializeHashBuilder();
      builder.remove(index);
    }

    private void set(Object key, int index) {
      if (index > currentSize) {
        throw nonMonotonicallyIncreasingIndex(index);
      }
      if (!isIntegerKey(key) || !key.equals(index)) {
        initializeHashBuilder();
        builder.add(key);
      }
      if (index == currentSize) {
        currentSize++;
      }
    }

    private RuntimeException nonMonotonicallyIncreasingIndex(int index) {
      return new UnsupportedOperationException(getMessage(index));
    }

    private String getMessage(int index) {
      return "Creating gap in index. current != index " + currentSize + " != " + index;
    }

    private boolean isIntegerKey(Object key) {
      return key instanceof Integer && builder == null;
    }

    @Override
    public void swap(int a, int b) {
      if (a != b) {
        initializeHashBuilder();
        builder.swap(a, b);
      }
    }
  }

  private static class SelectSet extends AbstractSet<Object> {

    private final int s;
    private final int e;

    public SelectSet(int s, int e) {
      this.s = s;
      this.e = e;
    }

    @Override
    public Iterator<Object> iterator() {
      return new Iterator<Object>() {
        private int current = s;

        @Override
        public boolean hasNext() {
          return current < e;
        }

        @Override
        public Object next() {
          return current++;
        }
      };
    }

    @Override
    public int size() {
      return e - s;
    }
  }

  @Override
  public boolean contains(Object key) {
    if (key instanceof Integer) {
      int k = (int) key;
      if (k >= 0 && k < size) {
        return true;
      }
    }
    return false;
  }



  @Override
  public int size() {
    return size;
  }



  @Override
  public String toString() {
    return indexSet().toString();
  }



}
