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

package org.briljantframework.vector;

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.array.Array;
import org.briljantframework.dataframe.SortOrder;
import org.briljantframework.exceptions.IllegalTypeException;
import org.briljantframework.function.Aggregates;
import org.briljantframework.index.Index;
import org.briljantframework.index.IntIndex;
import org.briljantframework.index.VectorLocationGetter;
import org.briljantframework.index.VectorLocationSetter;
import org.briljantframework.io.DataEntry;
import org.briljantframework.sort.QuickSort;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;
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
  public <T> Vector satisfies(Class<? extends T> cls, Predicate<? super T> predicate) {
    return collect(cls, Aggregates.test(predicate));
  }

  @Override
  public <T> Vector filter(Class<T> cls, Predicate<T> predicate) {
    return collect(cls, Aggregates.filter(this::newBuilder, predicate));
  }

  @Override
  public <T, O> Vector transform(Class<T> in, Class<O> out,
                                 Function<? super T, ? extends O> operator) {
    Collector<T, ?, Vector> transform = Aggregates.transform(
        () -> VectorType.from(out).newBuilder(), operator
    );
    return collect(in, transform);
  }

  @Override
  public <T> Vector transform(Class<T> cls, UnaryOperator<T> operator) {
    return collect(cls, Aggregates.transform(this::newBuilder, operator));
  }

  @Override
  public <T, R, C> R collect(Class<? extends T> in,
                             Collector<? super T, C, ? extends R> collector) {
    C accumulator = collector.supplier().get();
    for (int i = 0; i < size(); i++) {
      collector.accumulator().accept(accumulator, loc().get(in, i));
    }
    return collector.finisher().apply(accumulator);
  }

  @Override
  public <T, R> R collect(Class<? extends T> in, Supplier<R> supplier,
                          BiConsumer<R, ? super T> consumer) {
    return collect(in, Collector.of(
        supplier,
        consumer,
        (left, right) -> {
          throw new UnsupportedOperationException();
        },
        Function.identity(),
        Collector.Characteristics.IDENTITY_FINISH
    ));
  }

  @Override
  public <R> R collect(Collector<? super Object, ?, R> collector) {
    return collect(getType().getDataClass(), collector);
  }

  @Override
  public <T, R> Vector combine(Class<? extends T> in, Class<? extends R> out, Vector other,
                               BiFunction<? super T, ? super T, ? extends R> combiner) {
    Vector.Builder builder = VectorType.from(out).newBuilder();
    return combineVectors(in, other, combiner, builder);
  }

  @Override
  public <T> Vector combine(Class<T> cls, Vector other,
                            BiFunction<? super T, ? super T, ? extends T> combiner) {
    return combineVectors(cls, other, combiner, newBuilder());
  }

  @Override
  public Vector sort(SortOrder order) {
    int o = order == SortOrder.DESC ? -1 : 1;
    Vector.Builder builder = newCopyBuilder();
    VectorLocationGetter get = builder.getTemporaryVector().loc();
    VectorLocationSetter set = builder.loc();
    QuickSort.quickSort(0, builder.size(), (a, b) -> o * get.compare(a, b), set::swap);
    return builder.build();
  }

  @Override
  public <T> Vector sort(Class<T> cls, Comparator<T> cmp) {
    Vector.Builder builder = newCopyBuilder();
    VectorLocationGetter get = builder.getTemporaryVector().loc();

    VectorLocationSetter set = builder.loc();
    QuickSort.quickSort(
        0,
        builder.size(),
        (a, b) -> cmp.compare(get.get(cls, a), get.get(cls, b)),
        set::swap
    );
    return builder.build();
  }

  @Override
  public <T extends Comparable<T>> Vector sort(Class<T> cls) {
    Vector.Builder b = newCopyBuilder();
    VectorLocationGetter t = b.getTemporaryVector().loc();
    VectorLocationSetter set = b.loc();

    QuickSort.quickSort(0, b.size(), (i, j) -> t.get(cls, i).compareTo(t.get(cls, j)), set::swap);
    return b.build();
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
      index = new IntIndex(size());
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
  public VectorType getType(int index) {
    return getType();
  }

  @Override
  public Scale getScale() {
    return getType().getScale();
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
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    Vector that = (Vector) object;
    if (size() != that.size()) {
      return false;
    }
    // TODO: equality should take into account the keys
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
        return AbstractVector.this.loc().get(cls, index);
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
    for (int i = 0; i < size(); i++) {
      String value = toStringAt(i);
      Object o = index.getKey(i);
      String key = Is.NA(o) ? "NA" : o.toString();
      int keyPad = longestKey - key.length();
      builder.append(key).append("  ");
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
    }
    builder.append("type: ").append(getType().toString());
    return builder.toString();
  }

  protected static abstract class AbstractBuilder implements Vector.Builder {

    private final VectorLocationSetterImpl locationSetter = new VectorLocationSetterImpl();
    private Index.Builder indexer;

    protected AbstractBuilder(Index.Builder indexer) {
      this.indexer = indexer;
    }

    @Override
    public final Builder setNA(Object key) {
      int index = getOrCreateIndex(key);
      this.indexer.extend(index);
      setAt(index, null);
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Builder addNA() {
      loc().setNA(size());
      return this;
    }

    @Override
    public final Builder add(Vector from, int fromIndex) {
      loc().set(size(), from, fromIndex);
      return this;
    }

    @Override
    public final Builder set(Object key, Object value) {
      int index = getOrCreateIndex(key);
      setAt(index, value);
      return this;
    }

    @Override
    public final Vector.Builder add(Object value) {
      loc().set(size(), value);
      return this;
    }

    @Override
    public final Builder add(int value) {
      loc().set(size(), value);
      return this;
    }

    @Override
    public final Builder add(double value) {
      loc().set(size(), value);
      return this;
    }

    @Override
    public final Vector.Builder add(Vector from, Object key) {
      loc().set(size(), from, key);
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
      if (indexer.contains(key)) {
        int location = indexer.getLocation(key);
        loc().remove(location);
        indexer.remove(location);
      } else {
        throw new NoSuchElementException(key.toString());
      }
      return this;
    }

    @Override
    public final Vector.Builder read(DataEntry entry) throws IOException {
      return read(size(), entry);
    }

    protected final int getOrCreateIndex(Object key) {
      int index = size();
      if (indexer.contains(key)) {
        index = indexer.getLocation(key);
      } else {
        indexer.add(key);
      }
      return index;
    }

    protected abstract void setNaAt(int index);

    protected abstract void setAt(int index, Object value);

    protected void setAt(int index, int value) {
      setAt(index, (Integer) value);
    }

    protected void setAt(int index, double value) {
      setAt(index, (Double) value);
    }

    protected abstract void setAt(int atIndex, Vector from, int fromIndex);

    protected abstract void setAt(int t, Vector from, Object fromIndex);

    protected abstract void removeAt(int i);

    protected abstract void swapAt(int a, int b);

    protected Index getIndex() {
      Index index = indexer.build();
      indexer = null;
      return index;
    }

    private class VectorLocationSetterImpl implements VectorLocationSetter {

      @Override
      public void setNA(int i) {
        setNaAt(i);
        indexer.extend(i + 1);
      }

      @Override
      public void set(int i, Object value) {
        setAt(i, value);
        indexer.extend(i + 1);
      }

      @Override
      public void set(int i, double value) {
        setAt(i, value);
        indexer.extend(i + 1);
      }

      @Override
      public void set(int i, int value) {
        setAt(i, value);
        indexer.extend(i + 1);
      }

      @Override
      public void set(int t, Vector from, int f) {
        setAt(t, from, f);
        indexer.extend(t + 1);
      }

      @Override
      public void set(int t, Vector from, Object f) {
        setAt(t, from, f);
        indexer.extend(t + 1);
      }

      @Override
      public void remove(int i) {
        removeAt(i);
        indexer.remove(i);
      }

      @Override
      public void swap(int a, int b) {
        swapAt(a, b);
        indexer.swap(a, b);
      }
    }
  }

  private class VectorLocationGetterImpl implements VectorLocationGetter {

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
