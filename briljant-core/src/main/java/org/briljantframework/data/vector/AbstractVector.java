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

package org.briljantframework.data.vector;

import java.io.IOException;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import net.mintern.primitive.comparators.IntComparator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.Array;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.data.Collectors;
import org.briljantframework.data.Is;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.IntIndex;
import org.briljantframework.data.index.ObjectComparator;
import org.briljantframework.data.index.VectorLocationGetter;
import org.briljantframework.data.index.VectorLocationSetter;
import org.briljantframework.data.reader.DataEntry;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractVector implements Vector {

  private static final int SUPPRESS_OUTPUT_AFTER = 4;
  private static final int PER_OUTPUT = 2;
  private final VectorLocationGetter locationGetter = new VectorLocationGetterImpl();
  private Index index = null;

  protected AbstractVector(Index index) {
    this.index = index;
  }

  protected AbstractVector() {
    this.index = null;
  }

  // @Override
  // public <T> BooleanArray where(Class<T> cls, Vector other, BiPredicate<T, T> predicate) {
  // return combine(cls, Boolean.class, other, predicate::test);
  // }

  @Override
  public <T> BooleanArray where(Class<T> cls, Predicate<? super T> predicate) {
    BooleanArray array = Arrays.newBooleanArray(size());
    for (int i = 0; i < size(); i++) {
      array.set(i, predicate.test(loc().get(cls, i)));
    }
    return array;
  }

  @Override
  public <T> Vector filter(Class<T> cls, Predicate<? super T> predicate) {
    return collect(cls, Collectors.filter(this::newBuilder, predicate));
  }

  @Override
  public <T> Vector filterWithIndex(Class<T> cls, BiPredicate<Object, ? super T> predicate) {
    Vector.Builder builder = newBuilder();
    for (Object key : getIndex()) {
      T value = get(cls, key);
      if (predicate.test(key, value)) {
        builder.set(key, value);
      }
    }
    return builder.build();
  }

  @Override
  public <T> Vector map(Class<T> cls, Function<? super T, ?> operator) {
    Vector.Builder builder = new TypeInferenceVectorBuilder();
    for (Object key : this.getIndex()) {
      builder.set(key, operator.apply(get(cls, key)));
    }
    return builder.build();
  }

  @Override
  public <T> Vector mapWithIndex(Class<T> cls, BiFunction<Object, ? super T, ?> operator) {
    Vector.Builder builder = new TypeInferenceVectorBuilder();
    for (Object key : this.getIndex()) {
      builder.set(key, operator.apply(key, get(cls, key)));
    }
    return builder.build();
  }

  @Override
  public <T, R, C> R collect(Class<T> in, Collector<? super T, C, ? extends R> collector) {
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
  public <T> Vector combine(Class<T> cls, Vector other,
      BiFunction<? super T, ? super T, ? extends T> combiner) {
    return combineVectors(cls, other, combiner, new TypeInferenceVectorBuilder());
  }

  @Override
  public Vector combine(Vector other, BiFunction<? super Object, ? super Object, ?> combiner) {
    return combineVectors(Object.class, other, combiner, new TypeInferenceVectorBuilder());
  }

  @Override
  public Vector sort(SortOrder order) {
    IntComparator cmp = order == SortOrder.ASC ? loc()::compare : (a, b) -> loc().compare(b, a);
    Index.Builder index = getIndex().newCopyBuilder();
    index.sortIterationOrder(cmp::compare);
    return shallowCopy(index.build());
  }

  @Override
  public <T> Vector sort(Class<T> cls, Comparator<T> cmp) {
    Index.Builder index = getIndex().newCopyBuilder();
    VectorLocationGetter loc = loc();
    index.sortIterationOrder((a, b) -> cmp.compare(loc.get(cls, a), loc.get(cls, b)));
    return shallowCopy(index.build());
  }

  @Override
  public <T extends Comparable<T>> Vector sort(Class<T> cls) {
    return sort(cls, Comparable::compareTo);
  }

  protected <T> Vector combineVectors(Class<? extends T> cls, Vector other,
      BiFunction<? super T, ? super T, ?> combiner, Builder builder) {
    Index thisIndex = getIndex();
    Index otherIndex = Objects.requireNonNull(other, "require other vector").getIndex();
    if (otherIndex instanceof IntIndex) {
      int size = Math.min(size(), other.size());
      for (int i = 0; i < size; i++) {
        builder.set(thisIndex.get(i), combiner.apply(loc().get(cls, i), other.loc().get(cls, i)));
      }
    } else {
      HashSet<Object> keys = new HashSet<>();
      keys.addAll(thisIndex.keySet());
      keys.addAll(otherIndex.keySet());
      for (Object key : keys) {
        boolean thisIndexContainsKey = thisIndex.contains(key);
        boolean otherIndexContainsKey = otherIndex.contains(key);
        if (thisIndexContainsKey && otherIndexContainsKey) {
          builder.set(key, combiner.apply(get(cls, key), other.get(cls, key)));
        } else if (thisIndexContainsKey) {
          builder.set(key, this, key);
        } else {
          builder.set(key, other, key);
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
  public double getAsDouble(Object key, double defaultValue) {
    if (!getIndex().contains(key)) {
      return defaultValue;
    }
    double v = getAsDouble(key);
    if (Is.NA(v)) {
      return defaultValue;
    }
    return v;
  }

  @Override
  public final int getAsInt(Object key) {
    return getAsIntAt(getIndex().getLocation(key));
  }

  @Override
  public Vector get(BooleanArray array) {
    Check.argument(array.isVector(), "1d-array required");
    Check.size(this.size(), array.size());
    Builder builder = newBuilder();
    for (int i = 0; i < array.size(); i++) {
      if (array.get(i)) {
        builder.set(getIndex().get(i), this, i);
      }
    }
    return builder.build();
  }

  @Override
  public Vector set(Object key, Object value) {
    if (!getIndex().contains(key)) {
      throw new NoSuchElementException();
    }
    return newCopyBuilder().set(key, value).build();
  }

  @Override
  public Vector set(BooleanArray array, Object value) {
    Check.argument(array.isVector(), "1d-array required");
    Check.size(size(), array.size());
    Builder builder = newBuilder();
    for (int i = 0; i < array.size(); i++) {
      Object key = getIndex().get(i);
      if (array.get(i)) {
        builder.set(key, value);
      } else {
        builder.set(key, this, i);
      }
    }
    return builder.build();
  }

  @Override
  public final boolean isNA(Object key) {
    return isNaAt(getIndex().getLocation(key));
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

  public Iterator<Object> iterator() {
    return getIndex().keySet().iterator();
  }

  @Override
  public int compare(Object a, Object b) {
    return compareAt(getIndex().getLocation(a), this, getIndex().getLocation(b));
  }

  @Override
  public <T> Set<Pair<Object, T>> indexSet(Class<T> cls) {
    return new AbstractSet<Pair<Object, T>>() {
      @Override
      public Iterator<Pair<Object, T>> iterator() {
        return new Iterator<Pair<Object, T>>() {
          Iterator<Object> keys = getIndex().iterator();

          @Override
          public boolean hasNext() {
            return keys.hasNext();
          }

          @Override
          public Pair<Object, T> next() {
            Object key = keys.next();
            return new ImmutablePair<>(key, get(cls, key));
          }
        };
      }

      @Override
      public int size() {
        return AbstractVector.this.size();
      }
    };
  }

  @Override
  public Vector copy() {
    return shallowCopy(index);
  }

  @Override
  public <U> Array<U> toArray(Class<U> cls) {
    final VectorLocationGetter get = loc();
    Array<U> n = Arrays.newArray(size());
    for (int i = 0; i < size(); i++) {
      n.set(i, get.get(cls, i));
    }
    return n;
  }

  @Override
  public <U> void toArray(Class<U> cls, Array<U> array) {
    final VectorLocationGetter get = loc();
    int size = Math.min(size(), array.size());
    for (int i = 0; i < size; i++) {
      array.set(i, get.get(cls, i));
    }
  }

  @Override
  public void toArray(Array<Object> array) {
    toArray(Object.class, array);
  }

  @Override
  public void toArray(DoubleArray array) {
    final VectorLocationGetter getter = loc();
    int size = Math.min(size(), array.size());
    for (int i = 0; i < size; i++) {
      array.set(i, getter.getAsDouble(i));
    }
  }

  @Override
  public void toArray(IntArray array) {
    final VectorLocationGetter getter = loc();
    int size = Math.min(size(), array.size());
    for (int i = 0; i < size; i++) {
      array.set(i, getter.getAsInt(i));
    }
  }

  @Override
  public void toArray(ComplexArray array) {
    final VectorLocationGetter getter = loc();
    int size = Math.min(size(), array.size());
    for (int i = 0; i < size; i++) {
      array.set(i, getter.get(Complex.class, i));
    }
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
      Object b = that.get(Object.class, key);
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

  protected int compareAt(int a, Vector other, int b) {
    Object ca = loc().get(Object.class, a);
    Object cb = other.loc().get(Object.class, b);
    return ObjectComparator.getInstance().compare(ca, cb);
  }

  protected boolean equalsAt(int a, Vector other, int b) {
    return Is.equal(getAt(Object.class, a), other.loc().get(b));
  }

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
  public <T> boolean all(Class<T> cls, Predicate<? super T> predicate) {
    VectorLocationGetter getter = loc();
    for (int i = 0, size = size(); i < size; i++) {
      if (!predicate.test(getter.get(cls, i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public <T> boolean any(Class<T> cls, Predicate<? super T> predicate) {
    for (int i = 0; i < size(); i++) {
      if (predicate.test(loc().get(cls, i))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public <T> List<T> toList(Class<T> cls) {
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
    return toList(cls).stream();
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
    return Vectors.toString(this, 100);
  }

  /**
   * The class provides a skeletal implementation of the
   * {@link org.briljantframework.data.vector.Vector.Builder} interface and handles key-based
   * indexing and location-based indexing.
   *
   * <p>
   * Implementers must define
   * <ul>
   * <li>{@link #setNaAt(int)}</li>
   * <li>{@link #setAt(int, Vector, int)}</li>
   * <li>{@link #setAt(int, Object)}</li>
   * <li>{@link #setAt(int, Vector, Object)}</li>
   * <li>{@link #removeAt(int)}</li>
   * <li>{@link #swapAt(int, int)}</li>
   * <li>{@link #size()}</li>
   * <li>{@link #getTemporaryVector()}</li>
   * <li>{@link #build()}</li>
   * </ul>
   *
   * When called for, e.g., when performance is a concern the {@code plus}-operations can also be
   * overridden.
   *
   * <p>
   * Implementing a {@code Vector.Builder} backed by an {@link java.util.ArrayList} is as simple as
   *
   * <pre>
   * {@code
   * class ArrayListVectorBuilder extends AbstractBuilder {
   *   private ArrayList<Object> buffer = new ArrayList<>();
   * 
   *   private void extend(int index) {
   *    while(index <= buffer.size()) buffer.plus(null)
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
   *   protected void setAt(int t, Vector from, int f) {
   *     extend(index);
   *     buffer.set(index, from.loc().get(Object.class, f));
   *   }
   * 
   *   &#64;Override
   *   protected void setAt(int t, Vector from, Object f) {
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
   *   public Vector getTemporaryVector() {
   *     throw new UnsupportedOperationException("need implementation");
   *   }
   * 
   *   &#64;Override
   *   public Vector build() {
   *     throw new UnsupportedOperationException("need implementation");
   *   }
   * }
   * }
   * </pre>
   */
  protected static abstract class AbstractBuilder implements Vector.Builder {

    protected static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private final VectorLocationSetterImpl locationSetter = new VectorLocationSetterImpl();

    /**
     * Since null-checks are expensive in micro-benchmarks (increases the cost of creating primitive
     * vectors substantially), this indicator indicates whether or not an indexer is initialized. If
     * the value is true, the indexer cannot be null.
     */
    private boolean hasIndexer;
    private Index.Builder indexer;

    /**
     * Constructs a new builder with a specified indexer. If the supplied indexer is {@code null} an
     * indexer will be initialized when needed.
     *
     * <p>
     * The performance of the builder is improved when avoiding an indexer, however, calls to e.g.,
     * {@link #set(Object, Object)} will require and initializes one. The performance of subsequent
     * calls will decorate.
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
     * Provides a default implementation. To improve performance, minus-classes can override.
     *
     * <p>
     * If overridden, the implementor should make sure to extend the index using
     * {@link #extendIndex(int)}, for {@code plus}-operations, this usually amounts to
     * {@code extendIndex(size())}
     */
    @Override
    public Builder addNA() {
      loc().setNA(size());
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
    public Builder add(Vector from, int fromIndex) {
      loc().set(size(), from, fromIndex);
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
    public Vector.Builder add(Object value) {
      loc().set(size(), value);
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
    public Builder add(int value) {
      loc().set(size(), value);
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
    public Builder add(double value) {
      loc().set(size(), value);
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
     * <p>
     * DO NOT: extend the index
     *
     * @param index the index
     */
    protected abstract void setNaAt(int index);

    /**
     * Set the value at the specified index. Fill with {@code NA} between {@code size()} and
     * {@code index}
     *
     * <p>
     * DO NOT: extend the index
     *
     * @param index the index
     */
    protected abstract void setAt(int index, Object value);

    /**
     * Set value at the specified index. Fill with {@code NA} between {@code size()} and
     * {@code index}
     *
     * <p>
     * DO NOT: extend the index
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
     * <p>
     * DO NOT: extend the index
     *
     * @param index the index
     */
    protected void setAt(int index, double value) {
      setAt(index, (Double) value);
    }

    /**
     * Set value at the specified index using the value at {@code f} in the supplied vector. Fill
     * with {@code NA} between {@code size()} and {@code t}
     *
     * <p>
     * DO NOT: extend the index
     *
     * @param t the index
     * @param from the supplier
     * @param f the index in the supplier
     */
    protected abstract void setAt(int t, Vector from, int f);

    /**
     * Set value at the specified index using the value with the key in the supplied vector. Fill
     * with {@code NA} between {@code size()} and {@code t}
     *
     * <p>
     * DO NOT: extend the index
     *
     * @param t the index
     * @param from the supplier
     * @param f the index in the supplier
     */
    protected abstract void setAt(int t, Vector from, Object f);

    /**
     * Set value at the specified index to a value read from the supplied entry. Fill with
     * {@code NA} between {@code size()} and {@code index}
     *
     * <p>
     * DO NOT: extend the index
     *
     * @param i the index
     * @param entry the data entry
     */
    protected abstract void readAt(int i, DataEntry entry);

    /**
     * Removes the element at the specified location in this builder. Shifts any subsequent elements
     * to the left (subtracts one from their locations).
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

    /**
     * Get the location of the element with the supplied key. If no such key exist a new location is
     * created at the end of this vector and assocciated with the supplied key.
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
    public String toString(int index) {
      return toStringAt(index);
    }

    @Override
    public int indexOf(Object o) {
      for (int i = 0; i < size(); i++) {
        if (Is.equal(o, get(i))) {
          return i;
        }
      }
      return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
      for (int i = size() - 1; i >= 0; i--) {
        if (Is.equal(o, get(i))) {
          return i;
        }
      }
      return -1;
    }

    @Override
    public Vector get(IntArray locations) {
      Builder builder = newBuilder();
      Index index = getIndex();
      for (int i = 0; i < locations.size(); i++) {
        int location = locations.get(i);
        builder.set(index.get(location), AbstractVector.this, location);
      }
      return builder.build();
    }

    @Override
    public int compare(int a, int b) {
      return compareAt(a, AbstractVector.this, b);
    }

    @Override
    public boolean equals(int a, Vector other, int b) {
      return equalsAt(a, other, b);
    }

    @Override
    public int compare(int a, Vector other, int b) {
      return compareAt(a, other, b);
    }
  }
}
