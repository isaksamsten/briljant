package org.briljantframework.dataframe;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Isak Karlsson
 */
public interface Index extends Iterable<Index.Entry> {

  int get(Object key);

  Object reverse(int index);

  boolean contains(Object key);

  Set<Object> keySet();

  Collection<Integer> indices();

  Collection<Integer> indices(Object[] keys);

  default Stream<Entry> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  Builder newBuilder();

  int size();

  Index copy();

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

  public interface Builder {

    boolean contains(Object key);

    /**
     * Returns the value associated with {@code key}
     *
     * @param key the key
     * @return value {@code > 0} if {@code key} exists or {@code -1} otherwise.
     */
    int get(Object key);

    void add(Object key);

    void set(Object key, int index);

    Index build();

    void set(Entry entry);
  }
}
