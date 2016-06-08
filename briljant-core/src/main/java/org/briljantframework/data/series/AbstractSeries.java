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

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.*;
import org.briljantframework.array.Arrays;
import org.briljantframework.data.Collectors;
import org.briljantframework.data.Is;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.NaturalOrdering;
import org.briljantframework.data.index.RangeIndex;

import net.mintern.primitive.comparators.IntComparator;

/**
 * Provide a skeletal implementation of a series.
 * 
 * @author Isak Karlsson
 */
public abstract class AbstractSeries extends AbstractBaseArray<Series> implements Series {

  private final LocationGetter locationGetter = new LocationGetterImpl();

  // The index is initialized when accessed (or in the constructor)
  private Index index = null;

  /**
   * Create a new abstract series with the specified index (must be the same size as the product of
   * the shape), offset shape and stride.
   * 
   * @param index the index (or null if a default range index shall be used)
   * @param offset the offset
   * @param shape the shape
   * @param stride the stride.
   */
  protected AbstractSeries(Index index, int offset, int[] shape, int[] stride) {
    super(Arrays.getArrayBackend(), offset, shape, stride);
    this.index = index;
  }

  /**
   * Create a new abstract series with a default index and the given offset, shape and stride.
   * 
   * @param offset the offset
   * @param shape the shape
   * @param stride the stride
   */
  protected AbstractSeries(int offset, int[] shape, int[] stride) {
    this(null, offset, shape, stride);
  }

  @Override
  public void set(int toIndex, Series from, int fromIndex) {
    loc().set(toIndex, from.loc().get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, Series from, int fromRow, int fromColumn) {
    loc().set(toRow, fromColumn, from.loc().get(fromRow, fromColumn));
  }

  @Override
  public void set(int[] toIndex, Series from, int[] fromIndex) {
    loc().set(toIndex, from.loc().get(fromIndex));
  }

  @Override
  public void set(int[] toIndex, Series from, int fromIndex) {
    loc().set(toIndex, from.loc().get(fromIndex));
  }

  @Override
  public void set(int toIndex, Series from, int[] fromIndex) {
    loc().set(toIndex, from.loc().get(fromIndex));
  }

  @Override
  public Series reverse() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void forEach(int dim, Consumer<Series> consumer) {
    int vectors = vectors(dim);
    for (int i = 0; i < vectors; i++) {
      consumer.accept(select(dim, i));
    }
  }

  @Override
  public DoubleArray asDoubleArray() {
    return new AsDoubleArray(null, getOffset(), getShape(), getStride()) {
      @Override
      protected double getElement(int i) {
        return loc().getDouble(i);
      }

      @Override
      protected void setElement(int i, double value) {
        throw new UnsupportedOperationException();
      }

      @Override
      protected int elementSize() {
        return size();
      }
    };
  }

  @Override
  public IntArray asIntArray() {
    return new AsIntArray(this) {
      @Override
      protected int getElement(int i) {
        return getIntElement(i);
      }

      @Override
      protected void setElement(int i, int value) {
        setIntElement(i, value);
      }

      @Override
      protected int elementSize() {
        return AbstractSeries.this.elementSize();
      }
    };
  }

  @Override
  public LongArray asLongArray() {
    return new AsLongArray(this) {
      @Override
      protected void setElement(int i, long value) {
        setDoubleElement(i, value);
      }

      @Override
      protected long getElement(int i) {
        return (long) getDoubleElement(i);
      }

      @Override
      protected int elementSize() {
        return AbstractSeries.this.elementSize();
      }
    };
  }

  @Override
  public BooleanArray asBooleanArray() {
    return new AsBooleanArray(this) {
      @Override
      protected boolean getElement(int i) {
        return getIntElement(i) == 1;
      }

      @Override
      protected void setElement(int i, boolean value) {
        setIntElement(i, value ? 1 : 0);
      }

      @Override
      protected int elementSize() {
        return AbstractSeries.this.elementSize();
      }
    };
  }

  @Override
  public ComplexArray asComplexArray() {
    return new AsComplexArray(this) {
      @Override
      protected Complex getElement(int i) {
        return AbstractSeries.this.getElement(Complex.class, i);
      }

      @Override
      protected void setElement(int i, Complex value) {
        AbstractSeries.this.setElement(i, value);
      }

      @Override
      protected int elementSize() {
        return AbstractSeries.this.elementSize();
      }
    };
  }

  @Override
  public Array<Object> asArray() {
    return new AsArray<Object>(this) {
      @Override
      protected void setElement(int i, Object value) {
        AbstractSeries.this.setElement(i, value);
      }

      @Override
      protected Object getElement(int i) {
        return AbstractSeries.this.getElement(Object.class, i);
      }

      @Override
      protected int elementSize() {
        return AbstractSeries.this.elementSize();
      }
    };
  }

  @Override
  public List<Object> asList() {
    return asList(Object.class);
  }

  @Override
  public void swap(int a, int b) {
    Object tmp = loc().get(a);
    loc().set(a, loc().get(b));
    loc().set(b, tmp);
  }

  @Override
  public <T> BooleanArray where(Class<T> cls, Predicate<? super T> predicate) {
    BooleanArray array = Arrays.booleanArray(size());
    for (int i = 0; i < size(); i++) {
      array.set(i, predicate.test(loc().get(cls, i)));
    }
    return array;
  }

  @Override
  public <T> Series retainIf(Class<T> cls, Predicate<? super T> predicate) {
    return collect(cls, Collectors.filter(this::newBuilder, predicate));
  }

  @Override
  public <T> Series map(Class<T> cls, Function<? super T, ?> operator) {
    Series.Builder builder = new TypeInferenceBuilder();
    for (Object key : this.getIndex()) {
      builder.set(key, operator.apply(get(cls, key)));
    }
    return builder.build();
  }

  @Override
  public Series merge(Series other, BiFunction<? super Object, ? super Object, ?> combiner) {
    return merge(Object.class, other, combiner, new TypeInferenceBuilder());
  }

  @Override
  public <T> Series merge(Class<T> cls, Series other,
                          BiFunction<? super T, ? super T, ? extends T> combiner) {
    return merge(cls, other, combiner, new TypeInferenceBuilder());
  }

  @Override
  public Series drop(Object key) {
    Series.Builder builder = newCopyBuilder();
    builder.remove(key);
    return builder.build();
  }

  @Override
  public Series dropAll(Collection<?> keys) {
    Series.Builder builder = newBuilder();
    for (Object key : getIndex()) {
      if (!keys.contains(key)) {
        builder.setFrom(key, this, key);
      }
    }
    return builder.build();
  }

  @Override
  public Series getAll(Collection<?> keys) {
    Series.Builder builder = newBuilder();
    for (Object key : keys) {
      builder.setFrom(key, this, key);
    }
    return builder.build();
  }

  @Override
  public <T> Series dropIf(Class<T> cls, Predicate<? super T> predicate) {
    Series.Builder builder = newBuilder();
    for (Object key : getIndex()) {
      if (!predicate.test(get(cls, key))) {
        builder.setFrom(key, this, key);
      }
    }
    return builder.build();
  }

  @Override
  public Series sort(SortOrder order) {
    IntComparator cmp = order == SortOrder.ASC ? loc()::compare : (a, b) -> loc().compare(b, a);
    Index.Builder index = getIndex().newCopyBuilder();
    index.sortIterationOrder(cmp);
    return reindex(index.build());
  }

  @Override
  public <T> Series sort(Class<T> cls, Comparator<? super T> cmp) {
    Index.Builder index = getIndex().newCopyBuilder();
    LocationGetter loc = loc();
    index.sortIterationOrder((a, b) -> cmp.compare(loc.get(cls, a), loc.get(cls, b)));
    return reindex(index.build());
  }

  @Override
  public <T extends Comparable<T>> Series sort(Class<T> cls) {
    return sort(cls, Comparable::compareTo);
  }

  @Override
  public final Series limit(int n) {
    Series.Builder b = newBuilder();
    int i = 0;
    for (Object key : getIndex().keySet()) {
      if (i >= n) {
        break;
      }
      i++;
      b.setFrom(key, this, key);
    }
    return b.build();
  }

  @Override
  public final Series tail(int n) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final Index getIndex() {
    if (index == null) {
      index = new RangeIndex(0, size());
    }
    return index;
  }

  @Override
  public void setIndex(Index index) {
    Objects.requireNonNull(index);
    Check.dimension(size(), index.size());
    this.index = index;
  }

  @Override
  public final <T> T get(Class<T> cls, Object key) {
    return getElement(cls, resolveIndex(getIndex().getLocation(key)));
  }

  @Override
  public final void set(Object key, Object value) {
    setElement(resolveIndex(getIndex().getLocation(key)), value);
  }

  @Override
  public final double getDouble(Object key) {
    return getDoubleElement(resolveIndex(getIndex().getLocation(key)));
  }

  @Override
  public final void setDouble(Object key, double value) {
    setDoubleElement(resolveIndex(getIndex().getLocation(key)), value);
  }

  @Override
  public final int getInt(Object key) {
    return getIntElement(resolveIndex(getIndex().getLocation(key)));
  }

  @Override
  public final void setInt(Object key, int value) {
    setIntElement(resolveIndex(getIndex().getLocation(key)), value);
  }


  @Override
  public Series get(BooleanArray array) {
    Check.dimension(this.size(), array.size());
    Builder builder = newBuilder();
    for (int i = 0; i < array.size(); i++) {
      if (array.get(i)) {
        builder.setFromLocation(getIndex().get(i), this, i);
      }
    }
    return builder.build();
  }

  @Override
  public void set(BooleanArray array, Object value) {
    Check.dimension(size(), array.size());
    for (int i = 0; i < array.size(); i++) {
      if (array.get(i)) {
        loc().set(i, value);
      } else {
        set(i, this, i);
      }
    }
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
  public final boolean isNA(Object key) {
    return isElementNA(resolveIndex(getIndex().getLocation(key)));
  }

  @Override
  public <T> Set<Map.Entry<Object, T>> entrySet(Class<T> cls) {
    return new AbstractSet<Map.Entry<Object, T>>() {
      @Override
      public Iterator<Map.Entry<Object, T>> iterator() {
        return new Iterator<Map.Entry<Object, T>>() {
          Iterator<Object> keys = getIndex().iterator();

          @Override
          public boolean hasNext() {
            return keys.hasNext();
          }

          @Override
          public Map.Entry<Object, T> next() {
            Object key = keys.next();
            return new AbstractMap.SimpleImmutableEntry<>(key, get(cls, key));
          }
        };
      }

      @Override
      public int size() {
        return AbstractSeries.this.size();
      }
    };
  }

  @Override
  public Series copy() {
    // TODO: 5/3/16 this needs fixing
    return reindex(index);
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
        return AbstractSeries.this.size();
      }
    };
  }

  @Override
  public <T> Stream<T> stream(Class<T> cls) {
    return asList(cls).stream();
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
  public <T, R, C> R collect(Class<T> in, Collector<? super T, C, R> collector) {
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
  public <T> boolean allMatch(Class<T> cls, Predicate<? super T> predicate) {
    LocationGetter getter = loc();
    for (int i = 0, size = size(); i < size; i++) {
      if (!predicate.test(getter.get(cls, i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public <T> boolean anyMatch(Class<T> cls, Predicate<? super T> predicate) {
    for (int i = 0; i < size(); i++) {
      if (predicate.test(loc().get(cls, i))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public LocationGetter loc() {
    return locationGetter;
  }

  /**
   * {@inheritDoc}
   *
   * The copy builder returned here provides a lazy builder, which, if not used for modifying the
   * contents return {@code this} when built.
   *
   * <p/>
   * If your series is backed by a heavy data structure (e.g., a view from a data frame), consider
   * overriding.
   *
   * @return a new lazy series builder
   */
  @Override
  public Builder newCopyBuilder() {
    return new LazySeriesBuilder(this);
  }

  @Override
  public Builder newBuilder() {
    return getType().newBuilder();
  }

  @Override
  public Builder newBuilder(int size) {
    return getType().newBuilder(size);
  }

  /**
   * Compare the element at the specified positions.
   *
   * @param a the position in this series
   * @param other the other series
   * @param b the position in other
   * @return true if the elements are equal
   */
  protected boolean equalsElement(int a, Series other, int b) {
    return Is.equal(getElement(Object.class, a), other.loc().get(b));
  }

  protected int compareElement(int a, Series other, int b) {
    Object ca = getElement(Object.class, a);
    Object cb = other.loc().get(Object.class, b);
    return NaturalOrdering.ascending().compare(ca, cb);
  }

  /**
   * Return true if the value at the intrinsic position is {@code NA}.
   *
   * @param i the intrinsic position
   * @return true if the value is na
   */
  protected abstract boolean isElementNA(int i);

  /**
   * Return the value at the intrinsic position.
   *
   * @param index the intrinsic position
   * @return the value
   */
  protected abstract <T> T getElement(Class<T> cls, int index);

  /**
   * Return the value at the intrinsic position.
   *
   * @param i the intrinsic position
   * @return the value
   */
  protected abstract double getDoubleElement(int i);

  /**
   * Return the value at the intrinsic position.
   *
   * @param i the intrinsic position
   * @return the value
   */
  protected abstract int getIntElement(int i);

  protected abstract String getStringElement(int index);

  /**
   * Set the specified element at the specified location in the series. The default implementation
   * throws <tt>UnsupportedOperationExceptuon</tt>.
   *
   * @param index the index (in the underlying storage container)
   * @param value the value
   */
  protected void setElement(int index, Object value) {
    throw new UnsupportedOperationException();
  }

  protected void setDoubleElement(int index, double value) {
    setElement(index, value);
  }

  protected void setIntElement(int index, int value) {
    setElement(index, value);
  }

  protected <T> Series merge(Class<? extends T> cls, Series other,
                             BiFunction<? super T, ? super T, ?> combiner, Builder builder) {
    Index thisIndex = getIndex();
    Index otherIndex = Objects.requireNonNull(other, "require other series").getIndex();
    if (otherIndex instanceof RangeIndex) {
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
          builder.setFrom(key, this, key);
        } else {
          builder.setFrom(key, other, key);
        }
      }
    }
    return builder.build();
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean contains(Object o) {
    return false;
  }

  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      private final Iterator<Object> keys = getIndex().iterator();

      @Override
      public boolean hasNext() {
        return keys.hasNext();
      }

      @Override
      public Object next() {
        if (!keys.hasNext()) {
          throw new NoSuchElementException();
        }
        return get(keys.next());
      }
    };
  }

  @Override
  public Object[] toArray() {
    return asList().toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return asList().toArray(a);
  }

  @Override
  public boolean add(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeIf(Predicate<? super Object> filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
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
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || !(object instanceof Series)) {
      return false;
    }

    Series that = (Series) object;
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
  public String toString() {
    return SeriesUtils.toString(this, 100);
  }

  /**
   * Resolves the index given the stride and shape.
   * 
   * @param index the index
   * @return the memory location
   */
  private int resolveIndex(int index) {
    return StrideUtils.index(index, getOffset(), stride, shape);
  }

  /**
   * Resolves the index given the stride and shape.
   *
   * @param i the row
   * @param j the column
   * @return the memory location
   */
  private int resolveIndex(int i, int j) {
    return StrideUtils.index(i, j, getOffset(), stride);
  }

  /**
   * Resolves the index given the stride and shape.
   *
   * @param index the index
   * @return the memory location
   */
  private int resolveIndex(int... index) {
    return StrideUtils.index(index, getOffset(), stride);
  }

  private final class LocationGetterImpl extends AbstractList<Object> implements LocationGetter {

    @Override
    public Object set(int index, Object element) {
      Check.index(index, size());
      Object oldValue = get(index);
      setElement(resolveIndex(index), element);
      return oldValue;
    }

    @Override
    public Object set(int i, int j, Object element) {
      Check.index(i, rows(), j, columns());
      Object oldValue = get(i, j);
      setElement(resolveIndex(i, j), element);
      return oldValue;
    }

    @Override
    public Object set(int[] index, Object element) {
      Check.index(index, shape);
      Object oldValue = get(index);
      setElement(resolveIndex(index), element);
      return oldValue;
    }

    @Override
    public <T> T get(Class<T> cls, int i) {
      Check.index(i, size());
      return getElement(cls, resolveIndex(i));
    }

    @Override
    public <T> T get(Class<T> cls, int i, int j) {
      Check.index(i, rows(), j, columns());
      return getElement(cls, resolveIndex(i, j));
    }

    @Override
    public <T> T get(Class<T> cls, int... index) {
      Check.index(index, shape);
      return getElement(cls, resolveIndex(index));
    }

    @Override
    public int getInt(int i) {
      return getIntElement(resolveIndex(i));
    }

    @Override
    public int getInt(int i, int j) {
      return getIntElement(resolveIndex(i, j));
    }

    @Override
    public int getInt(int... index) {
      return getIntElement(resolveIndex(index));
    }

    @Override
    public double getDouble(int i) {
      return getDoubleElement(resolveIndex(i));
    }

    @Override
    public double getDouble(int i, int j) {
      return getDoubleElement(resolveIndex(i, j));
    }

    @Override
    public double getDouble(int... index) {
      return getDoubleElement(resolveIndex(index));
    }

    @Override
    public double setDouble(int index, double value) {
      double oldValue = getDouble(index);
      setDoubleElement(resolveIndex(index), value);
      return oldValue;
    }

    @Override
    public double setDouble(int i, int j, double value) {
      double oldValue = getDouble(i, j);
      setDoubleElement(resolveIndex(i, j), value);
      return oldValue;
    }

    @Override
    public double setDouble(int[] index, double value) {
      double oldValue = getDouble(index);
      setDoubleElement(resolveIndex(index), value);
      return oldValue;
    }

    @Override
    public int setInt(int index, int value) {
      int oldValue = getInt(index);
      setDoubleElement(resolveIndex(index), value);
      return oldValue;
    }

    @Override
    public int setInt(int i, int j, int value) {
      int oldValue = getInt(i, j);
      setIntElement(resolveIndex(i, j), value);
      return oldValue;
    }

    @Override
    public int setInt(int[] index, int value) {
      int oldValue = getInt(index);
      setIntElement(resolveIndex(index), value);
      return oldValue;
    }

    @Override
    public <T> T get(Class<T> cls, int i, Supplier<T> defaultValue) {
      T v = get(cls, i);
      return Is.NA(v) ? defaultValue.get() : v;
    }

    @Override
    public boolean isNA(int i) {
      return isElementNA(resolveIndex(i));
    }

    @Override
    public String toString(int index) {
      return getStringElement(resolveIndex(index));
    }

    @Override
    public Object get(int index) {
      return get(Object.class, index);
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
    public Series get(IntArray locations) {
      Builder builder = newBuilder();
      Index index = getIndex();
      for (int i = 0; i < locations.size(); i++) {
        int location = locations.get(i);
        builder.setFromLocation(index.get(location), AbstractSeries.this, location);
      }
      return builder.build();
    }

    @Override
    public int compare(int a, int b) {
      return compareElement(a, AbstractSeries.this, b);
    }

    @Override
    public boolean equals(int a, Series other, int b) {
      return equalsElement(resolveIndex(a), other, b);
    }

    @Override
    public int compare(int a, Series other, int b) {
      return compareElement(resolveIndex(a), other, b);
    }

    @Override
    public int size() {
      return AbstractSeries.this.size();
    }
  }
}
