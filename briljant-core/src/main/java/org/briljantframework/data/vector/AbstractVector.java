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

package org.briljantframework.data.vector;

import net.mintern.primitive.comparators.IntComparator;

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.array.Array;
import org.briljantframework.data.Collectors;
import org.briljantframework.data.Is;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.IntIndex;
import org.briljantframework.data.index.VectorLocationGetter;
import org.briljantframework.data.index.VectorLocationSetter;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.exceptions.IllegalTypeException;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractVector implements Vector {

  private static final int SUPPRESS_OUTPUT_AFTER = 4;
  private static final int PER_OUTPUT = 8;

  private Index index = null;
  private final VectorLocationGetter locationGetter = new VectorLocationGetterImpl();

  protected AbstractVector(Index index) {
    this.index = index;
  }

  protected AbstractVector() {
    this.index = null;
  }

  @Override
  public <T> Vector satisfies(Class<T> cls, Vector other, BiPredicate<T, T> predicate) {
    return combine(cls, Boolean.class, other, predicate::test);
  }

  @Override
  public <T> Vector satisfies(Class<T> cls, Predicate<? super T> predicate) {
    return collect(cls, Collectors.test(predicate));
  }

  @Override
  public <T> Vector filter(Class<T> cls, Predicate<? super T> predicate) {
    return collect(cls, Collectors.filter(this::newBuilder, predicate));
  }

  @Override
  public <T, O> Vector map(Class<T> in, Class<O> out,
                           Function<? super T, ? extends O> operator) {
    Collector<T, ?, Vector> transform = Collectors.map(
        () -> VectorType.of(out).newBuilder(), operator
    );
    return collect(in, transform);
  }

  @Override
  public <T> Vector map(Class<T> cls, UnaryOperator<T> operator) {
    return collect(cls, Collectors.map(this::newBuilder, operator));
  }

  @Override
  public <T, R, C> R collect(Class<T> in,
                             Collector<? super T, C, ? extends R> collector) {
    C accumulator = collector.supplier().get();
    for (int i = 0; i < size(); i++) {
      collector.accumulator().accept(accumulator, loc().get(in, i));
    }
    return collector.finisher().apply(accumulator);
  }

  @Override
  public <R> R collect(Collector<? super Object, ?, R> collector) {
    return collect(getType().getDataClass(), collector);
  }

  @Override
  public <T, R> Vector combine(Class<T> in, Class<R> out, Vector other,
                               BiFunction<? super T, ? super T, ? extends R> combiner) {
    Vector.Builder builder = VectorType.of(out).newBuilder();
    return combineVectors(in, other, combiner, builder);
  }

  @Override
  public <T> Vector combine(Class<T> cls, Vector other,
                            BiFunction<? super T, ? super T, ? extends T> combiner) {
    return combineVectors(cls, other, combiner, newBuilder());
  }

  @Override
  public Vector sort(SortOrder order) {
//    if (getIndex() instanceof IntIndex && order == SortOrder.ASC) {
//      return this;
//    }

    IntComparator cmp = order == SortOrder.ASC ? loc()::compare : (a, b) -> loc().compare(b, a);
    Index.Builder index = getIndex().newCopyBuilder();
    index.sortOrder(cmp);
    return shallowCopy(index.build());
  }

  @Override
  public <T> Vector sort(Class<T> cls, Comparator<T> cmp) {
    Index.Builder index = getIndex().newCopyBuilder();
    VectorLocationGetter loc = loc();
    index.sortOrder((a, b) -> cmp.compare(loc.get(cls, a), loc.get(cls, b)));
    return shallowCopy(index.build());
  }

  @Override
  public <T extends Comparable<T>> Vector sort(Class<T> cls) {
    return sort(cls, Comparable::compareTo);
  }

  protected <T> Vector combineVectors(Class<? extends T> cls, Vector other,
                                      BiFunction<? super T, ? super T, ?> combiner,
                                      Builder builder) {
    int thisSize = this.size();
    int otherSize = other.size();
    int size = Math.max(thisSize, otherSize);
    for (int i = 0; i < size; i++) {
      if (i < thisSize && i < otherSize) {
        builder.add(combiner.apply(loc().get(cls, i), other.loc().get(cls, i)));
      } else {
        if (i < thisSize) {
          builder.add(loc().get(cls, i));
        } else {
          builder.add(other.loc().get(cls, i));
        }
      }
    }
    return builder.build();
  }

  @Override
  public final Vector head(int n) {
    Vector.Builder b = newBuilder();
    int i = 0;
    for (Object key : getIndex().keySet()) {
      if (i >= n) {
        break;
      }
      i++;
      b.set(key, this, key);
    }
    return b.build();
  }

  @Override
  public final Vector tail(int n) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final Index getIndex() {
    if (index == null) {
      index = new IntIndex(0, size());
    }
    return index;
  }

  @Override
  public final void setIndex(Index index) {
    Objects.requireNonNull(index);
    Check.size(size(), index.size());
    this.index = index;
  }

  @Override
  public final <T> T get(Class<T> cls, Object key) {
    return loc().get(cls, getIndex().getLocation(key));
  }

  @Override
  public final double getAsDouble(Object key) {
    return getAsDoubleAt(getIndex().getLocation(key));
  }

  @Override
  public final int getAsInt(Object key) {
    return getAsIntAt(getIndex().getLocation(key));
  }

  @Override
  public final String toString(Object key) {
    return toStringAt(getIndex().getLocation(key));
  }

  @Override
  public final boolean isNA(Object key) {
    return isNaAt(getIndex().getLocation(key));
  }

  @Override
  public boolean isTrue(Object key) {
    return isTrueAt(getIndex().getLocation(key));
  }

  @Override
  public boolean hasNA() {
    for (int i = 0; i < size(); i++) {
      if (loc().isNA(i)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Vector select(Vector bits) {
    Check.size(this.size(), bits.size());
    Builder builder = newBuilder();
    getIndex().keySet().stream()
        .filter(bits::isTrue)
        .forEach(key -> builder.set(key, this, key));
    return builder.build();
  }

  @Override
  public int compare(Object a, Object b) {
    return compareAt(getIndex().getLocation(a), this, getIndex().getLocation(b));
  }

  @Override
  public Vector copy() {
    return shallowCopy(index);
  }

  @Override
  public <U> Array<U> toArray(Class<U> cls) throws IllegalTypeException {
    final VectorLocationGetter get = loc();
    Array<U> n = Bj.referenceArray(size());
    for (int i = 0; i < size(); i++) {
      n.set(i, get.get(cls, i));
    }
    return n;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || !(object instanceof Vector)) {
      return false;
    }

    Vector that = (Vector) object;
    if (size() != that.size()) {
      return false;
    }
    if (!getIndex().equals(that.getIndex())) {
      return false;
    }
    for (Object key : getIndex().keySet()) {
      Object a = get(Object.class, key);
      Object b = get(Object.class, key);
      if (!Is.NA(a) && !Is.NA(b) && !a.equals(b)) {
        return false;
      }

    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 1;
    for (int i = 0, size = size(); i < size; i++) {
      Object o = loc().get(Object.class, i);
      result += 31 * result + (!Is.NA(o) ? o.hashCode() : 0);
    }
    return result;
  }

  @Override
  public VectorLocationGetter loc() {
    return locationGetter;
  }

  protected abstract <T> T getAt(Class<T> cls, int index);

  protected abstract double getAsDoubleAt(int i);

  protected abstract int getAsIntAt(int i);

  protected abstract boolean isNaAt(int index);

  protected abstract String toStringAt(int index);

  protected boolean isTrueAt(int index) {
    return getAsIntAt(index) == 1;
  }

  protected abstract int compareAt(int a, Vector other, int b);

  protected abstract Vector shallowCopy(Index index);

  @Override
  public Builder newCopyBuilder() {
    Builder builder = newBuilder(size());
    for (int i = 0; i < size(); i++) {
      builder.loc().set(i, this, i);
    }
    return builder;
  }

  @Override
  public Builder newBuilder() {
    return getType().newBuilder();
  }

  @Override
  public Builder newBuilder(int size) {
    return getType().newBuilder(size);
  }

  @Override
  public <T> List<T> asList(Class<T> cls) {
    return new AbstractList<T>() {
      @Override
      public T get(int index) {
        return loc().get(cls, index);
      }

      @Override
      public int size() {
        return AbstractVector.this.size();
      }
    };
  }

  @Override
  public <T> Stream<T> stream(Class<T> cls) {
    return asList(cls).stream();
  }

  @Override
  public <T> Stream<T> parallelStream(Class<T> cls) {
    return asList(cls).parallelStream();
  }

  @Override
  public IntStream intStream() {
    return stream(Number.class).mapToInt(Number::intValue);
  }

  @Override
  public DoubleStream doubleStream() {
    return stream(Number.class).mapToDouble(Number::doubleValue);
  }

  @Override
  public LongStream longStream() {
    return stream(Number.class).mapToLong(Number::longValue);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    Index index = getIndex();
    int longestKey = String.valueOf(index.size()).length();
    if (!(index instanceof IntIndex)) {
      longestKey = index.keySet().stream()
          .mapToInt(key -> Is.NA(key) ? 2 : key.toString().length())
          .max()
          .orElse(0);
    }

    int max = size() < SUPPRESS_OUTPUT_AFTER ? size() : PER_OUTPUT;
    int i = 0;
    for (Object key : getIndex().keySet()) {
      String value = toString(key);
      String keyString = Is.NA(key) ? "NA" : key.toString();
      int keyPad = longestKey - keyString.length();
      builder.append(keyString).append("  ");
      for (int j = 0; j < keyPad; j++) {
        builder.append(" ");
      }
      builder.append(value).append("\n");
      if (i >= max) {
        int left = size() - i - 1;
        if (left > max) {
          i += left - max - 1;
          builder.append("...\n");
        }
      }
      i++;
    }
    builder.append("type: ").append(getType().toString());
    return builder.toString();
  }

  /**
   * The class provides a skeletal implementation of the {@link org.briljantframework.data.vector.Vector.Builder}
   * interface and handles key-based indexing and location-based indexing.
   *
   * <p> Implementers must define
   * <ul>
   * <li>{@link #setNaAt(int)}</li>
   * <li>{@link #setAt(int, Vector, int)}</li>
   * <li>{@link #setAt(int, Object)} </li>
   * <li>{@link #setAt(int, Vector, Object)} </li>
   * <li>{@link #removeAt(int)} </li>
   * <li>{@link #swapAt(int, int)} </li>
   * <li>{@link #size()}</li>
   * <li>{@link #getTemporaryVector()}</li>
   * <li>{@link #build()}</li>
   * </ul>
   *
   * When called for, e.g., when performance is a concern the {@code add}-operations can also
   * be overridden.
   *
   * <p> Implementing a {@code Vector.Builder} backed by an {@link java.util.ArrayList} is as
   * simple
   * as
   *
   * <pre>{@code
   * class ArrayListVectorBuilder extends AbstractBuilder {
   *   private ArrayList<Object> buffer = new ArrayList<>();
   *
   *   private void extend(int index) {
   *    while(index <= buffer.size()) buffer.add(null)
   *   }
   *
   *   @Override
   *   protected void setNaAt(int index) { extend(index); buffer.set(index, null); }
   *
   *   @Override
   *   protected void setAt(int index, Object value) {
   *     extend(index);
   *     buffer.set(index, value);
   *   }
   *
   *   @Override
   *   protected void setAt(int t, Vector from, int f) {
   *     extend(index);
   *     buffer.set(index, from.loc().get(Object.class, f));
   *   }
   *
   *   @Override
   *   protected void setAt(int t, Vector from, Object f) {
   *     extend(index);
   *     buffer.set(index, from.get(Object.class, f));
   *   }
   *
   *   @Override
   *   protected void readAt(int i, DataEntry entry) throws IOException {
   *     extend(i);
   *     buffer.set(i, entry.next(Object.class));
   *   }
   *
   *   @Override
   *   protected void removeAt(int i) {
   *     buffer.remove(i);
   *   }
   *
   *   @Override
   *   protected void swapAt(int a, int b) {
   *     Collections.swap(buffer, a, b);
   *   }
   *
   *   @Override
   *   public int size() {
   *     return buffer.size();
   *   }
   *
   *   @Override
   *   public Vector getTemporaryVector() {
   *     throw new UnsupportedOperationException("need implementation");
   *   }
   *
   *   @Override
   *   public Vector build() {
   *     throw new UnsupportedOperationException("need implementation");
   *   }
   * }
   * }</pre>
   */
  protected static abstract class AbstractBuilder implements Vector.Builder {

    protected static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private final VectorLocationSetterImpl locationSetter = new VectorLocationSetterImpl();

    /**
     * Since null-checks are expensive in micro-benchmarks (increases the cost of creating
     * primitive vectors substantially), this indicator indicates whether or not an indexer is
     * initialized. If the value is true, the indexer cannot be null.
     */
    private boolean hasIndexer;
    private Index.Builder indexer;

    /**
     * Constructs a new builder with a specified indexer. If the supplied indexer is {@code null}
     * an indexer will be initialized when needed.
     *
     * <p> The performance of the builder is improved when avoiding an indexer, however,
     * calls to e.g., {@link #set(Object, Object)} will require and initializes one. The
     * performance of subsequent calls will decorate.
     *
     * @param indexer the indexer
     */
    protected AbstractBuilder(Index.Builder indexer) {
      this.indexer = indexer;
      this.hasIndexer = !(indexer == null);
    }

    /**
     * Constructs a default builder
     */
    protected AbstractBuilder() {
      indexer = null;
      hasIndexer = false;
    }

    /**
     * Provides a default implementation. To improve performance, sub-classes can override.
     *
     * <p> If overridden, the implementor should make sure to extend the index using {@link
     * #extendIndex(int)}, for {@code add}-operations, this usually amounts to {@code
     * extendIndex(size())}
     */
    @Override
    public Builder addNA() {
      loc().setNA(size());
      return this;
    }

    /**
     * Provides a default implementation. To improve performance, sub-classes can override.
     *
     * <p> If overridden, the implementor should make sure to extend the index using {@link
     * #extendIndex(int)}, for {@code add}-operations, this usually amounts to {@code
     * extendIndex(size())}
     */
    @Override
    public Builder add(Vector from, int fromIndex) {
      loc().set(size(), from, fromIndex);
      return this;
    }

    /**
     * Provides a default implementation. To improve performance, sub-classes can override.
     *
     * <p> If overridden, the implementor should make sure to extend the index using {@link
     * #extendIndex(int)}, for {@code add}-operations, this usually amounts to {@code
     * extendIndex(size())}
     */
    @Override
    public Vector.Builder add(Object value) {
      loc().set(size(), value);
      return this;
    }

    /**
     * Provides a default implementation. To improve performance, sub-classes can override.
     *
     * <p> If overridden, the implementor should make sure to extend the index using {@link
     * #extendIndex(int)}, for {@code add}-operations, this usually amounts to {@code
     * extendIndex(size())}
     */
    @Override
    public Builder add(int value) {
      loc().set(size(), value);
      return this;
    }

    /**
     * Provides a default implementation. To improve performance, sub-classes can override.
     *
     * <p> If overridden, the implementor should make sure to extend the index using {@link
     * #extendIndex(int)}, for {@code add}-operations, this usually amounts to {@code
     * extendIndex(size())}
     */
    @Override
    public Builder add(double value) {
      loc().set(size(), value);
      return this;
    }

    /**
     * Provides a default implementation. To improve performance, sub-classes can override.
     *
     * <p> If overridden, the implementor should make sure to extend the index using {@link
     * #extendIndex(int)}, for {@code add}-operations, this usually amounts to {@code
     * extendIndex(size())}
     */
    @Override
    public Vector.Builder add(Vector from, Object key) {
      loc().set(size(), from, key);
      return this;
    }

    @Override
    public final Builder setNA(Object key) {
      int index = getOrCreateIndex(key);
      setAt(index, null);
      return this;
    }

    @Override
    public final Builder set(Object key, Object value) {
      int index = getOrCreateIndex(key);
      setAt(index, value);
      return this;
    }

    @Override
    public final Vector.Builder set(Object atKey, Vector from, int fromIndex) {
      int index = getOrCreateIndex(atKey);
      setAt(index, from, fromIndex);
      return this;
    }

    @Override
    public final Vector.Builder set(Object atKey, Vector from, Object fromIndex) {
      int index = getOrCreateIndex(atKey);
      setAt(index, from, fromIndex);
      return this;
    }

    @Override
    public final Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        add(from, i);
      }
      return this;
    }

    @Override
    public final VectorLocationSetter loc() {
      return locationSetter;
    }

    @Override
    public final Builder remove(Object key) {
      initializeIndexer();
      int location = indexer.getLocation(key);
      loc().remove(location);
      indexer.remove(location);
      return this;
    }

    @Override
    public Builder readAll(DataEntry entry) throws IOException {
      Objects.requireNonNull(entry, "Require non-null entry");
      while (entry.hasNext()) {
        read(entry);
      }
      return this;
    }

    @Override
    public final Vector.Builder read(DataEntry entry) {
      final int size = size();
      readAt(size, entry);
      extendIndex(size);
      return this;
    }

    /**
     * Set {@code NA} at the specified index. Fill with {@code NA} between {@code size()} and
     * {@code index}
     *
     * <p> DO NOT: extend the index
     *
     * @param index the index
     */
    protected abstract void setNaAt(int index);

    /**
     * Set the value at the specified index. Fill with {@code NA} between {@code size()} and
     * {@code index}
     *
     * <p> DO NOT: extend the index
     *
     * @param index the index
     */
    protected abstract void setAt(int index, Object value);

    /**
     * Set value at the specified index. Fill with {@code NA} between {@code size()} and
     * {@code index}
     *
     * <p> DO NOT: extend the index
     *
     * @param index the index
     */
    protected void setAt(int index, int value) {
      setAt(index, (Integer) value);
    }

    /**
     * Set value at the specified index. Fill with {@code NA} between {@code size()} and
     * {@code index}
     *
     * <p> DO NOT: extend the index
     *
     * @param index the index
     */
    protected void setAt(int index, double value) {
      setAt(index, (Double) value);
    }

    /**
     * Set value at the specified index using the value at {@code f} in the supplied
     * vector.
     * Fill with {@code NA} between {@code size()} and {@code t}
     *
     * <p> DO NOT: extend the index
     *
     * @param t    the index
     * @param from the supplier
     * @param f    the index in the supplier
     */
    protected abstract void setAt(int t, Vector from, int f);

    /**
     * Set value at the specified index using the value with the key in the supplied
     * vector. Fill with {@code NA} between {@code size()} and {@code t}
     *
     * <p> DO NOT: extend the index
     *
     * @param t    the index
     * @param from the supplier
     * @param f    the index in the supplier
     */
    protected abstract void setAt(int t, Vector from, Object f);

    /**
     * Set value at the specified index to a value read from the supplied entry. Fill with {@code
     * NA} between {@code size()} and {@code index}
     *
     * <p> DO NOT: extend the index
     *
     * @param i     the index
     * @param entry the data entry
     */
    protected abstract void readAt(int i, DataEntry entry);

    /**
     * Removes the element at the specified location in this builder.
     * Shifts any subsequent elements to the left (subtracts one from their
     * locations).
     *
     * @param i the index of the element to be removed
     */
    protected abstract void removeAt(int i);

    /**
     * Swap the elements at the specified location.
     *
     * @param a the location of the first element
     * @param b the location of the second element
     */
    protected abstract void swapAt(int a, int b);

    ;

    /**
     * Get the location of the element with the supplied key. If no such key exist
     * a new location is created at the end of this vector and assocciated with the supplied key.
     *
     * @param key the key
     * @return the location of the key
     */
    private int getOrCreateIndex(Object key) {
      initializeIndexer();
      int index = size();
      if (indexer.contains(key)) {
        index = indexer.getLocation(key);
      } else {
        indexer.add(key);
      }
      return index;
    }

    /**
     * Initializes the index if needed
     */
    private void initializeIndexer() {
      if (!hasIndexer) {
        indexer = new IntIndex.Builder(size());
        hasIndexer = true;
      }
    }

    /**
     * Swaps the index at the specified locations
     *
     * @param a the first location
     * @param b the second location
     */
    protected void swapIndex(int a, int b) {
      if (hasIndexer) {
        indexer.swap(a, b);
      }
    }

    /**
     * Remove the index at the specified location
     *
     * @param i the location of the index to remove
     */
    protected void removeIndex(int i) {
      if (hasIndexer) {
        indexer.remove(i);
      }
    }

    /**
     * Extend the index to include the supplied index
     *
     * @param i the final index to include
     */
    protected void extendIndex(int i) {
      if (hasIndexer) {
        indexer.extend(i + 1);
      }
    }

    /**
     * Get the Index of the completed vector
     *
     * @return the index
     */
    protected Index getIndex() {
      if (!hasIndexer) {
        return new IntIndex(0, size());
      }
      return indexer.build();
    }

    private final class VectorLocationSetterImpl implements VectorLocationSetter {

      @Override
      public void setNA(int i) {
        setNaAt(i);
        extendIndex(i);
      }

      @Override
      public void set(int i, Object value) {
        setAt(i, value);
        extendIndex(i);
      }

      @Override
      public void set(int i, double value) {
        setAt(i, value);
        extendIndex(i);
      }

      @Override
      public void set(int i, int value) {
        setAt(i, value);
        extendIndex(i);
      }

      @Override
      public void set(int t, Vector from, int f) {
        setAt(t, from, f);
        extendIndex(t);
      }

      @Override
      public void set(int t, Vector from, Object f) {
        setAt(t, from, f);
        extendIndex(t);
      }

      @Override
      public void read(int index, DataEntry entry) {
        readAt(index, entry);
      }

      @Override
      public void remove(int i) {
        removeAt(i);
        removeIndex(i);
      }

      @Override
      public void swap(int a, int b) {
        swapAt(a, b);
        swapIndex(a, b);
      }
    }
  }

  private final class VectorLocationGetterImpl implements VectorLocationGetter {

    @Override
    public double getAsDouble(int i) {
      return getAsDoubleAt(i);
    }

    @Override
    public int getAsInt(int i) {
      return getAsIntAt(i);
    }

    @Override
    public <T> T get(Class<T> cls, int i) {
      return getAt(cls, i);
    }

    @Override
    public <T> T get(Class<T> cls, int i, Supplier<T> defaultValue) {
      T v = get(cls, i);
      return Is.NA(v) ? defaultValue.get() : v;
    }

    @Override
    public boolean isNA(int i) {
      return isNaAt(i);
    }

    @Override
    public boolean isTrue(int index) {
      return isTrueAt(index);
    }

    @Override
    public String toString(int index) {
      return toStringAt(index);
    }

    @Override
    public Vector get(int... locations) {
      Builder builder = newBuilder(locations.length);
      Index index = getIndex();
      for (int location : locations) {
        builder.set(index.getKey(location), AbstractVector.this, location);
      }
      return builder.build();
    }

    @Override
    public int compare(int a, int b) {
      return compareAt(a, AbstractVector.this, b);
    }

    @Override
    public boolean equals(int a, Vector other, int b) {
      return compareAt(a, other, b) == 0;
    }

    @Override
    public int compare(int a, Vector other, int b) {
      return compareAt(a, other, b);
    }
  }
}
