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
import org.briljantframework.index.Index;
import org.briljantframework.index.IntIndex;
import org.briljantframework.dataframe.SortOrder;
import org.briljantframework.exceptions.IllegalTypeException;
import org.briljantframework.function.Aggregates;
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
      collector.accumulator().accept(accumulator, get(in, i));
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
    Vector tmp = builder.getTemporaryVector();
    QuickSort.quickSort(0, tmp.size(), (a, b) -> o * tmp.compare(a, b), builder);
    return builder.build();
  }

  @Override
  public <T> Vector sort(Class<T> cls, Comparator<T> cmp) {
    Vector.Builder builder = newCopyBuilder();
    Vector tmp = builder.getTemporaryVector();
    QuickSort.quickSort(
        0,
        tmp.size(),
        (a, b) -> cmp.compare(tmp.get(cls, a), tmp.get(cls, b)),
        builder
    );
    return builder.build();
  }

  @Override
  public <T extends Comparable<T>> Vector sort(Class<T> cls) {
    Vector.Builder builder = newCopyBuilder();
    Vector t = builder.getTemporaryVector();
    QuickSort.quickSort(0, t.size(), (a, b) -> t.get(cls, a).compareTo(t.get(cls, b)), builder);
    return builder.build();
  }

  protected <T> Vector combineVectors(Class<? extends T> cls, Vector other,
                                      BiFunction<? super T, ? super T, ?> combiner,
                                      Builder builder) {
    int thisSize = this.size();
    int otherSize = other.size();
    int size = Math.max(thisSize, otherSize);
    for (int i = 0; i < size; i++) {
      if (i < thisSize && i < otherSize) {
        builder.add(combiner.apply(get(cls, i), other.get(cls, i)));
      } else {
        if (i < thisSize) {
          builder.add(get(cls, i));
        } else {
          builder.add(other.get(cls, i));
        }
      }
    }
    return builder.build();
  }

  @Override
  public Vector head(int n) {
    Vector.Builder b = newBuilder();
    for (int i = 0; i < n && i < size(); i++) {
      b.add(this, i);
    }
    return b.build();
  }

  @Override
  public Vector tail(int n) {
    Vector.Builder b = newBuilder();
    for (int i = size() - n; i < size(); i++) {
      b.add(this, i);
    }
    return b.build();
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
  public <T> T get(Class<T> cls, Object key) {
    return get(cls, getIndex().getLocation(key));
  }

  @Override
  public <T> T get(Class<T> cls, int index, Supplier<T> defaultValue) {
    T v = get(cls, index);
    return Is.NA(v) ? defaultValue.get() : v;
  }

  @Override
  public double getAsDouble(Object key) {
    return getAsDouble(getIndex().getLocation(key));
  }

  @Override
  public int getAsInt(Object key) {
    return getAsInt(getIndex().getLocation(key));
  }

  @Override
  public String toString(Object key) {
    return toString(getIndex().getLocation(key));
  }

  @Override
  public boolean isTrue(int index) {
    return get(Logical.class, index) == Logical.TRUE;
  }

  @Override
  public boolean hasNA() {
    for (int i = 0; i < size(); i++) {
      if (isNA(i)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Vector select(List<Integer> indexes) {
    Builder builder = newBuilder();
    Index index = getIndex();
    Index.Builder indexBuilder = index.newBuilder();
    for (int idx : indexes) {
      builder.add(this, idx);
      indexBuilder.add(index.get(idx));
    }
    Vector vector = builder.build();
    vector.setIndex(indexBuilder.build());
    return vector;
  }

  @Override
  public Vector select(Vector bits) {
    Check.size(this.size(), bits.size());
    Builder builder = newBuilder();
    if (getIndex() instanceof IntIndex) {
      for (int i = 0; i < size(); i++) {
        Logical b = bits.get(Logical.class, i);
        if (b == Logical.TRUE) {
          builder.add(this, i);
        }
      }
      return builder.build();
    } else {
      Index index = getIndex();
      Index.Builder indexBuilder = index.newBuilder();
      for (Index.Entry entry : index.entrySet()) {
        if (bits.get(Logical.class, entry.key()) == Logical.TRUE) {
          indexBuilder.add(entry.key());
          builder.add(this, entry.index());
        }
      }
      Vector vector = builder.build();
      vector.setIndex(indexBuilder.build());
      return vector;
    }
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
    Array<U> n = Bj.referenceArray(size());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(cls, i));
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
    // TODO: index-based comparision not location based
    for (int i = 0; i < size(); i++) {
      Object a = get(Object.class, i);
      Object b = get(Object.class, i);

      if (!Is.NA(a) && !Is.NA(b) && !a.equals(b)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public int hashCode() {
    return index != null ? index.hashCode() : 0;
  }

  @Override
  public boolean equals(int a, Vector other, int b) {
    return compare(a, other, b) == 0;
  }

  @Override
  public boolean equals(int a, Object other) {
    return get(Object.class, a).equals(other);
  }

  @Override
  public Builder newCopyBuilder() {
    Builder builder = newBuilder(size());
    for (int i = 0; i < size(); i++) {
      builder.set(i, this, i);
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
        return AbstractVector.this.get(cls, index);
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
      String value = toString(i);
      Object o = index.get(i);
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

    protected Index.Builder indexer;

    protected AbstractBuilder(Index.Builder indexer) {
      this.indexer = indexer;
    }

    @Override
    public final Builder setNA(Object key) {
      int index = getOrCreateIndex(key);
      setAt(index, null);
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Builder addNA() {
      setNA(size());
      return this;
    }

    @Override
    public final Builder add(Vector from, int fromIndex) {
      set(size(), from, fromIndex);
      return this;
    }

    @Override
    public final Builder set(int atIndex, Vector from, int fromIndex) {
      setAt(atIndex, from, fromIndex);
      indexer.set(atIndex, atIndex);
      return this;
    }

    @Override
    public final Builder set(int index, Object value) {
      setAt(index, value);
      indexer.set(index, index);
      return this;
    }

    @Override
    public Builder set(int index, int value) {
      set(index, (Integer) value);
      return this;
    }

    @Override
    public Builder set(int index, double value) {
      set(index, (Double) value);
      return this;
    }

    @Override
    public Builder set(Object key, Object value) {
      int index = getOrCreateIndex(key);
      setAt(index, value);
      return this;
    }

    @Override
    public final Vector.Builder add(Object value) {
      return set(size(), value);
    }

    @Override
    public Builder add(int value) {
      return add((Integer) value);
    }

    @Override
    public Builder add(double value) {
      return add((Double) value);
    }

    @Override
    public Vector.Builder add(Vector from, Object key) {
      return set(size(), from, key);
    }

    @Override
    public Vector.Builder set(Object atKey, Vector from, int fromIndex) {
      int index = getOrCreateIndex(atKey);
      setAt(index, from, fromIndex);
      return this;
    }

    @Override
    public Vector.Builder set(Object atKey, Vector from, Object fromIndex) {
      int index = getOrCreateIndex(atKey);
      return set(index, from, fromIndex);
    }


    @Override
    public Builder remove(Object key) {
      if (this.indexer.contains(key)) {
        remove(this.indexer.index(key));
      } else {
        throw new NoSuchElementException(key.toString());
      }
      return this;
    }

    @Override
    public final Vector.Builder read(DataEntry entry) throws IOException {
      return read(size(), entry);
    }

    protected int getOrCreateIndex(Object key) {
      int index = size();
      if (indexer.contains(key)) {
        index = indexer.index(key);
      } else {
        indexer.add(key);
      }
      return index;
    }

    abstract void setAt(int index, Object value);

    abstract void setAt(int atIndex, Vector from, int fromIndex);
  }
}
