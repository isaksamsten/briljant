package org.briljantframework.dataframe;


import com.google.common.collect.Iterators;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Isak Karlsson
 */
public class IntIndex implements Index {

  private final int size;

  public IntIndex(int size) {
    this.size = size;
  }

  private NoSuchElementException noSuchElement(Object key) {
    return new NoSuchElementException(String.format("name '%s' not in index", key));
  }

  @Override
  public int get(Object key) {
    if (key instanceof Integer) {
      return (int) key;
    }
    throw noSuchElement(key);
  }

  @Override
  public Object reverse(int index) {
    if (index >= 0 && index < size) {
      return index;
    }
    throw noSuchElement(index);
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
  public Collection<Integer> indices() {
    return new AbstractCollection<Integer>() {
      @Override
      public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
          public int current = 0;

          @Override
          public boolean hasNext() {
            return current < size;
          }

          @Override
          public Integer next() {
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
            return get(keys[current++]);
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
  public Builder newBuilder() {
    return new Builder() {

      private HashIndex.Builder builder;
      private int buffer = 0;

      @Override
      public boolean contains(Object key) {
        if (isMonotonicallyIncreasing(key)) {
          int k = (int) key;
          return k >= 0 && k < buffer;
        } else {
          initializeHashBuilder();
          return builder.contains(key);
        }
      }

      private void initializeHashBuilder() {
        if (builder == null) {
          builder = new HashIndex.Builder();
          for (int i = 0; i < buffer; i++) {
            builder.set(i, i);
          }
        }
      }

      @Override
      public int get(Object key) {
        if (isMonotonicallyIncreasing(key)) {
          return (int) key;
        } else {
          initializeHashBuilder();
          return builder.get(key);
        }
      }

      @Override
      public void add(Object key) {
        if (isMonotonicallyIncreasing(key)) {
          set(key, buffer);
        } else {
          initializeHashBuilder();
          builder.add(key);
        }
      }

      private boolean isMonotonicallyIncreasing(Object key) {
        return key instanceof Integer && builder == null;
      }

      @Override
      public void set(Object key, int index) {
        if (isMonotonicallyIncreasing(key) && key.equals(index)) {
          if (index < buffer) {
            throw new IllegalArgumentException("Duplicate key: " + key);
          }
          buffer++;

        } else {
          initializeHashBuilder();
          builder.set(key, index);
        }
      }

      @Override
      public Index build() {
        if (builder == null) {
          return new IntIndex(buffer);
        } else {
          return builder.build();
        }
      }

      @Override
      public void set(Entry entry) {
        set(entry.key(), entry.index());
      }
    };
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public Index copy() {
    return new IntIndex(size());
  }

  /**
   * Returns an iterator over elements of type {@code T}.
   *
   * @return an Iterator.
   */
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
  public String toString() {
    return Iterators.toString(iterator());
  }
}
