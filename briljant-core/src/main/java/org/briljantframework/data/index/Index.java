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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.mintern.primitive.comparators.IntComparator;

import org.briljantframework.Listable;
import org.briljantframework.array.Range;
import org.briljantframework.data.SortOrder;
import org.briljantframework.sort.Swappable;

/**
 * Immutable index.
 *
 * @author Isak Karlsson
 */
public interface Index extends List<Object>, Iterable<Object> {

  /**
   * Return an index from 0 to end
   * 
   * @param end the end of the range
   * @return a new index
   */
  static Index range(int end) {
    return range(0, end);
  }

  /**
   * Return an index from start (inclusive) to end (exclusive)
   * 
   * @param start the start
   * @param end the end
   * @return a new index
   */
  static Index range(int start, int end) {
    return new IntIndex(start, end);
  }

  /**
   * Return an index from the supplied values
   * 
   * @param values the values
   * @return a new index
   */
  static Index of(Object... values) {
    return ObjectIndex.of(values);
  }

  /**
   * Return an index from the value in the given collection (iteration order is defined by the
   * iteration order of the collection)
   * 
   * @param collection a collection
   * @return a new index
   */
  static Index of(Collection<Object> collection) {
    return ObjectIndex.of(collection);
  }

  /**
   * Return an index from the given listable collection
   * 
   * @param listable a listable collection (i.e. a collection which can be transformed into a list)
   * @return a new index
   */
  static Index of(Listable<?> listable) {
    if (listable instanceof Range && ((Range) listable).step() == 1) {
      return new IntIndex(((Range) listable).start(), ((Range) listable).end());
    }
    return of(listable.toList());
  }

  /**
   * Get the index location of the supplied key.
   *
   * @param key the key
   * @return the location
   * @throws java.util.NoSuchElementException if key does not exist
   */
  int getLocation(Object key);

  @Override
  Object get(int index);

  /**
   * Get the keys in this index as a set of keys
   *
   * @return a new key set (with specified iteration order)
   */
  Set<Object> keySet();

  /**
   * Get a collection of locations include in this index
   *
   * @return a collection of locations
   */
  Collection<Integer> locations();

  /**
   * Get a set of index entries in this index
   *
   * @return a new index set
   */
  Set<Index.Entry> indexSet();

  /**
   * Get the locations associated with the given keys
   *
   * @param keys the keys
   * @return the locations
   * @throws java.util.NoSuchElementException if any key is missing
   */
  int[] locations(Object[] keys);

  /**
   * Construct a new index builder
   *
   * @return a new builder
   */
  Builder newBuilder();

  /**
   * Copy this index into a new builder
   *
   * @return a new builder
   */
  Builder newCopyBuilder();

  interface Builder extends Swappable {

    /**
     * Returns {@code true} if the index builder contains the specified key.
     *
     * @param key the key
     * @return a boolean indicator
     */
    boolean contains(Object key);

    /**
     * Returns the value associated with {@code key}
     *
     * @param key the key
     * @return value {@code > 0} if {@code key} exists or {@code -1} otherwise.
     */
    int getLocation(Object key);

    Object get(int index);

    void add(Object key);

    void add(int key);

    /**
     * Sort the iteration order of this index based on some external value.
     * 
     * <pre>
     * int[] order = {20, 10, 30};
     * Index.Builder ib = new ObjectIndex.Builder();
     * ib.add(&quot;A&quot;);
     * ib.add(&quot;B&quot;);
     * ib.add(&quot;C&quot;);
     * ib.sortIterationOrder((a, b) -&gt; order[a] - order[b]);
     * for (Object key : ib.build()) {
     *   System.out.println(key);
     * }
     * // Prints
     * // B
     * // A
     * // C
     * </pre>
     * 
     * @param cmp an external comparison function
     */
    void sortIterationOrder(IntComparator cmp);

    void sort(Comparator<Object> cmp);

    void sort(SortOrder order);

    default void sort() {
      sort(SortOrder.ASC);
    }

    void extend(int size);

    void resize(int size);

    Index build();

    int size();

    void remove(int index);
  }

  final class Entry {

    private final Object key;
    private final int index;

    public Entry(Object key, int index) {
      this.key = key;
      this.index = index;
    }

    public Object getKey() {
      return key;
    }

    public int getValue() {
      return index;
    }

    @Override
    public String toString() {
      return "Entry{" + "key=" + key + ", index=" + index + '}';
    }
  }
}
