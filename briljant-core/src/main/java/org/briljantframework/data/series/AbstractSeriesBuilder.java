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
package org.briljantframework.data.series;

import java.util.Objects;

import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.RangeIndex;
import org.briljantframework.data.reader.DataEntry;

/**
 * The class provides a skeletal implementation of the {@link Series.Builder} interface and handles
 * key-based indexing and location-based indexing.
 *
 * <p>
 * Implementers must define
 * <ul>
 * <li>{@link #setElementNA(int)}</li>
 * <li>{@link #setElementFrom(int, Series, int)}</li>
 * <li>{@link #setElement(int, Object)}</li>
 * <li>{@link #setElement(int, Series, Object)}</li>
 * <li>{@link #removeElement(int)}</li>
 * <li>{@link #swapAt(int, int)}</li>
 * <li>{@link #size()}</li>
 * <li>{@link #build()}</li>
 * </ul>
 *
 * When called for, e.g., when performance is a concern the {@code plus}-operations can also be
 * overridden.
 *
 * <p>
 * Implementing a {@code Series.Builder} backed by an {@link java.util.ArrayList} is as simple as
 *
 * <pre>
 * {@code
 * class ArrayListVectorBuilder extends AbstractBuilder {
 *   private ArrayList<Object> buffer = new ArrayList<>();
 *
 *   private void extend(int index) {
 *    while(index <= buffer.size()) buffer.add(null)
 *   }
 *
 *   &#64;Override
 *   protected void setNaAt(int index) { extend(index); buffer.set(index, null); }
 *
 *   &#64;Override
 *   protected void setAt(int index, Object value) {
 *     extend(index);
 *     buffer.set(index, value);
 *   }
 *
 *   &#64;Override
 *   protected void setAt(int t, Series from, int f) {
 *     extend(index);
 *     buffer.set(index, from.loc().get(Object.class, f));
 *   }
 *
 *   &#64;Override
 *   protected void setAt(int t, Series from, Object f) {
 *     extend(index);
 *     buffer.set(index, from.get(Object.class, f));
 *   }
 *
 *   &#64;Override
 *   protected void readAt(int i, DataEntry entry) throws IOException {
 *     extend(i);
 *     buffer.set(i, entry.next(Object.class));
 *   }
 *
 *   &#64;Override
 *   protected void removeAt(int i) {
 *     buffer.remove(i);
 *   }
 *
 *   &#64;Override
 *   protected void swapAt(int a, int b) {
 *     Collections.swap(buffer, a, b);
 *   }
 *
 *   &#64;Override
 *   public int size() {
 *     return buffer.size();
 *   }
 *
 *   &#64;Override
 *   public Series getTemporaryVector() {
 *     throw new UnsupportedOperationException("need implementation");
 *   }
 *
 *   &#64;Override
 *   public Series build() {
 *     throw new UnsupportedOperationException("need implementation");
 *   }
 * }
 * }
 * </pre>
 */
abstract class AbstractSeriesBuilder implements Series.Builder {

  /**
   * Recommended initial capacity.
   */
  public static final int INITIAL_CAPACITY = 5;

  protected static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

  private final LocationSetterImpl locationSetter = new LocationSetterImpl();

  /**
   * Since null-checks are expensive in micro-benchmarks (increases the cost of creating primitive
   * vectors substantially), this indicator indicates whether or not an indexer is initialized. If
   * the value is true, the indexer cannot be null.
   */
  private boolean hasIndexBuilder;
  private Index.Builder indexBuilder;

  /**
   * Constructs a new builder with a specified indexer. If the supplied indexer is {@code null} an
   * indexer will be initialized when needed.
   *
   * <p>
   * The performance of the builder is improved when avoiding an indexer, however, calls to e.g.,
   * {@link #set(Object, Object)} will require and initializes one. The performance of subsequent
   * calls will decorate.
   *
   * @param indexBuilder the indexer
   */
  protected AbstractSeriesBuilder(Index.Builder indexBuilder) {
    this.indexBuilder = indexBuilder;
    this.hasIndexBuilder = !(indexBuilder == null);
  }

  /**
   * Constructs a default builder
   */
  protected AbstractSeriesBuilder() {
    indexBuilder = null;
    hasIndexBuilder = false;
  }

  /**
   * Provides a default implementation. To improve performance, minus-classes can override.
   *
   * <p>
   * If overridden, the implementor should make sure to extend the index using
   * {@link #extendIndex(int)}, for {@code plus}-operations, this usually amounts to
   * {@code extendIndex(size())}
   */
  @Override
  public Series.Builder addNA() {
    setElementNA(size());
    extendIndex(size());
    return this;
  }

  @Override
  public final Series.Builder setNA(Object key) {
    int index = getOrCreateIndex(key);
    setElement(index, null);
    return this;
  }

  /**
   * Provides a default implementation. To improve performance, minus-classes can override.
   *
   * <p>
   * If overridden, the implementor should make sure to extend the index using
   * {@link #extendIndex(int)}, for {@code plus}-operations, this usually amounts to
   * {@code extendIndex(size())}
   */
  @Override
  public Series.Builder addFrom(Series from, Object key) {
    // loc().set(size(), from, key);
    setElement(size(), from, key);
    extendIndex(size());
    return this;
  }

  @Override
  public Series.Builder addFromLocation(Series from, int pos) {
    setElementFrom(size(), from, pos);
    extendIndex(size());
    return this;
  }

  @Override
  public final Series.Builder setFromLocation(Object atKey, Series from, int fromIndex) {
    int index = getOrCreateIndex(atKey);
    setElementFrom(index, from, fromIndex);
    return this;
  }

  @Override
  public final Series.Builder setFrom(Object atKey, Series from, Object fromIndex) {
    int index = getOrCreateIndex(atKey);
    setElement(index, from, fromIndex);
    return this;
  }

  @Override
  public final Series.Builder set(Object key, Object value) {
    int index = getOrCreateIndex(key);
    setElement(index, value);
    return this;
  }

  @Override
  public Series.Builder setInt(Object key, int value) {
    return set(key, value);
  }

  @Override
  public Series.Builder setDouble(Object key, double value) {
    return set(key, value);
  }

  /**
   * Provides a default implementation. To improve performance, minus-classes can override.
   *
   * <p>
   * If overridden, the implementor should make sure to extend the index using
   * {@link #extendIndex(int)}, for {@code plus}-operations, this usually amounts to
   * {@code extendIndex(size())}
   */
  @Override
  public Series.Builder add(Object value) {
    setElement(size(), value);
    extendIndex(size());
    return this;
  }

  /**
   * Provides a default implementation. To improve performance, minus-classes can override.
   *
   * <p>
   * If overridden, the implementor should make sure to extend the index using
   * {@link #extendIndex(int)}, for {@code plus}-operations, this usually amounts to
   * {@code extendIndex(size())}
   */
  @Override
  public Series.Builder addDouble(double value) {
    setElement(size(), value);
    extendIndex(size());
    return this;
  }

  /**
   * Provides a default implementation. To improve performance, minus-classes can override.
   *
   * <p>
   * If overridden, the implementor should make sure to extend the index using
   * {@link #extendIndex(int)}, for {@code plus}-operations, this usually amounts to
   * {@code extendIndex(size())}
   */
  @Override
  public Series.Builder addInt(int value) {
    setElement(size(), value);
    extendIndex(size());
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * Implementors should override to improve performance.
   *
   * @param from the series
   * @return a modified builder
   */
  @Override
  public Series.Builder setAll(Series from) {
    for (Object key : from.index()) {
      setFrom(key, from, key);
    }
    return this;
  }

  @Override
  public Series.Builder readAll(DataEntry entry) {
    Objects.requireNonNull(entry, "Require non-null entry");
    while (entry.hasNext()) {
      read(entry);
    }
    return this;
  }

  @Override
  public final LocationSetter loc() {
    return locationSetter;
  }

  @Override
  public final Series.Builder read(DataEntry entry) {
    final int size = size();
    readAt(size, entry);
    extendIndex(size);
    return this;
  }

  /**
   * Set value at the specified index to a value read from the supplied entry. Fill with {@code NA}
   * between {@code size()} and {@code index}
   *
   * <p>
   * DO NOT: extend the index
   *
   * @param i the index
   * @param entry the data entry
   */
  protected abstract void readAt(int i, DataEntry entry);

  /**
   * Extend the index to include the supplied index
   *
   * @param i the final index to include
   */
  protected void extendIndex(int i) {
    if (hasIndexBuilder) {
      indexBuilder.extend(i + 1);
    }
  }

  /**
   * Set value at the specified index using the value with the key in the supplied series. Fill with
   * {@code NA} between {@code size()} and {@code t}
   *
   * <p>
   * DO NOT: extend the index
   *
   * @param t the index
   * @param from the supplier
   * @param f the index in the supplier
   */
  protected abstract void setElement(int t, Series from, Object f);

  /**
   * Set value at the specified index using the value at {@code f} in the supplied series. Fill with
   * {@code NA} between {@code size()} and {@code t}
   *
   * <p>
   * DO NOT: extend the index
   *
   * @param t the index
   * @param from the supplier
   * @param f the index in the supplier
   */
  protected abstract void setElementFrom(int t, Series from, int f);

  /**
   * Set the value at the specified index. Fill with {@code NA} between {@code size()} and
   * {@code index}
   *
   * <p>
   * DO NOT: extend the index
   *
   * @param index the index
   */
  protected abstract void setElement(int index, Object value);

  /**
   * Get the location of the element with the supplied key. If no such key exist a new location is
   * created at the end of this series and assocciated with the supplied key.
   *
   * @param key the key
   * @return the location of the key
   */
  private int getOrCreateIndex(Object key) {
    initIndexBuilder();
    return indexBuilder.getOrAdd(key);
  }

  /**
   * Initializes the index if needed
   */
  private void initIndexBuilder() {
    if (!hasIndexBuilder) {
      indexBuilder = new RangeIndex.Builder(size());
      hasIndexBuilder = true;
    }
  }

  /**
   * Set {@code NA} at the specified index. Fill with {@code NA} between {@code size()} and
   * {@code index}
   *
   * <p>
   * DO NOT: extend the index
   *
   * @param index the index
   */
  protected abstract void setElementNA(int index);

  /**
   * Set value at the specified index. Fill with {@code NA} between {@code size()} and {@code index}
   *
   * <p>
   * DO NOT: extend the index
   *
   * @param index the index
   */
  protected void setElement(int index, int value) {
    setElement(index, (Integer) value);
  }

  /**
   * Set value at the specified index. Fill with {@code NA} between {@code size()} and {@code index}
   *
   * <p>
   * DO NOT: extend the index
   *
   * @param index the index
   */
  protected void setElement(int index, double value) {
    setElement(index, (Double) value);
  }

  /**
   * Removes the element at the specified location in this builder. Shifts any subsequent elements
   * to the left (subtracts one from their locations).
   *
   * @param i the index of the element to be removed
   */
  protected abstract void removeElement(int i);

  /**
   * Swap the elements at the specified location.
   *
   * @param a the location of the first element
   * @param b the location of the second element
   */
  protected abstract void swapAt(int a, int b);

  /**
   * Swaps the index at the specified locations
   *
   * @param a the first location
   * @param b the second location
   */
  protected void swapIndex(int a, int b) {
    if (hasIndexBuilder) {
      indexBuilder.swap(a, b);
    }
  }

  /**
   * Remove the index at the specified location
   *
   * @param i the location of the index to remove
   */
  protected void removeIndexLocation(int i) {
    if (hasIndexBuilder) {
      indexBuilder.removeLocation(i);
    }
  }

  /**
   * Get the Index of the completed series
   *
   * @return the index
   */
  protected Index getIndex() {
    if (!hasIndexBuilder) {
      return new RangeIndex(0, size());
    }
    return indexBuilder.build();
  }

  private final class LocationSetterImpl implements LocationSetter {

    @Override
    public void setNA(int i) {
      setElementNA(i);
      extendIndex(i);
    }

    @Override
    public void set(int i, Object value) {
      setElement(i, value);
      extendIndex(i);
    }

    @Override
    public void setDouble(int i, double value) {
      setElement(i, value);
      extendIndex(i);
    }

    @Override
    public void setInt(int i, int value) {
      setElement(i, value);
      extendIndex(i);
    }

    @Override
    public void setFrom(int t, Series from, int f) {
      setElementFrom(t, from, f);
      extendIndex(t);
    }

    @Override
    public void setFromKey(int t, Series from, Object f) {
      setElement(t, from, f);
      extendIndex(t);
    }

    @Override
    public void read(int index, DataEntry entry) {
      readAt(index, entry);
    }

    @Override
    public void remove(int i) {
      removeElement(i);
      removeIndexLocation(i);
    }

    @Override
    public void swap(int a, int b) {
      swapAt(a, b);
      swapIndex(a, b);
    }
  }
}
