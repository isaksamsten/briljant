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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.briljantframework.Check;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.data.Is;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.RangeIndex;

import net.mintern.primitive.comparators.IntComparator;

/**
 * Provide a skeletal implementation of a series.
 * 
 * @author Isak Karlsson
 */
public abstract class AbstractSeries implements Series {

  transient volatile protected Storage storage;

  @Override
  public <T> BooleanArray where(Class<T> cls, Predicate<? super T> predicate) {
    BooleanArray array = Arrays.booleanArray(size());
    for (int i = 0; i < size(); i++) {
      array.set(i, predicate.test(values().get(cls, i)));
    }
    return array;
  }

  @Override
  public <T> Series map(Class<T> cls, Function<? super T, ?> operator) {
    Series.Builder builder = new TypeInferenceBuilder();
    for (Object key : this.index()) {
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
  public final Series drop(Object key) {
    if (!index().contains(key)) {
      throw new NoSuchElementException();
    }

    Series.Builder copy = newBuilder();
    for (Object k : index()) {
      if (!Is.equal(k, key)) {
        copy.setFrom(k, this, k);
      }
    }

    return copy.build();
  }

  @Override
  public Series dropAll(Collection<?> keys) {
    if (!index().containsAll(keys)) {
      throw new NoSuchElementException();
    }

    Series.Builder builder = newBuilder();
    for (Object key : index()) {
      if (!keys.contains(key)) {
        builder.setFrom(key, this, key);
      }
    }
    return builder.build();
  }

  @Override
  public Series getAll(Collection<?> keys) {
    if (!index().containsAll(keys)) {
      throw new NoSuchElementException();
    }

    Series.Builder builder = newBuilder();
    for (Object key : keys) {
      builder.setFrom(key, this, key);
    }
    return builder.build();
  }

  @Override
  public <T> Series dropIf(Class<T> cls, Predicate<? super T> predicate) {
    Series.Builder builder = newBuilder();
    for (Object key : index()) {
      if (!predicate.test(get(cls, key))) {
        builder.setFrom(key, this, key);
      }
    }
    return builder.build();
  }

  @Override
  public Series sort(SortOrder order) {
    IntComparator cmp =
        order == SortOrder.ASC ? values()::compare : (a, b) -> values().compare(b, a);
    Index.Builder index = index().newCopyBuilder();
    index.sortIterationOrder(cmp);
    return reindex(index.build());
  }

  @Override
  public <T> Series sortBy(Class<T> cls, Comparator<? super T> cmp) {
    Index.Builder index = index().newCopyBuilder();
    Storage loc = values();
    index.sortIterationOrder((a, b) -> cmp.compare(loc.get(cls, a), loc.get(cls, b)));
    return reindex(index.build());
  }

  @Override
  public <T extends Comparable<T>> Series sortBy(Class<T> cls) {
    return sortBy(cls, Comparable::compareTo);
  }

  @Override
  public final Series limit(int n) {
    Series.Builder b = newBuilder();
    int i = 0;
    for (Object key : index().keySet()) {
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
  public <T> T get(Class<T> cls, Object key) {
    return Convert.to(cls, get(key));
  }

  @Override
  public double getDouble(Object key) {
    return get(Double.class, key);
  }

  @Override
  public void setDouble(Object key, double value) {
    set(key, value);
  }

  @Override
  public int getInt(Object key) {
    return get(Integer.class, key);
  }

  @Override
  public void setInt(Object key, int value) {
    set(key, value);
  }

  @Override
  public Series get(BooleanArray array) {
    Check.dimension(this.size(), array.size());
    Builder builder = newBuilder();
    for (int i = 0; i < array.size(); i++) {
      if (array.get(i)) {
        builder.setFromLocation(index().get(i), this, i);
      }
    }
    return builder.build();
  }

  @Override
  public void set(BooleanArray array, Object value) {
    Check.dimension(size(), array.size());
    for (int i = 0; i < array.size(); i++) {
      if (array.get(i)) {
        values().set(i, value);
      } else {
        values().setFrom(i, this.values(), i);
      }
    }
  }

  @Override
  public boolean hasNA() {
    for (int i = 0; i < size(); i++) {
      if (values().isNA(i)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isNA(Object key) {
    return Is.NA(get(key));
  }

  @Override
  public Set<Map.Entry<Object, Object>> entrySet() {
    return new AbstractSet<Map.Entry<Object, Object>>() {
      @Override
      public Iterator<Map.Entry<Object, Object>> iterator() {
        return new Iterator<Map.Entry<Object, Object>>() {
          Iterator<Object> keys = index().iterator();

          @Override
          public boolean hasNext() {
            return keys.hasNext();
          }

          @Override
          public Map.Entry<Object, Object> next() {
            Object key = keys.next();
            return new AbstractMap.SimpleImmutableEntry<>(key, get(key));
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
    return newCopyBuilder().build();
  }

  @Override
  public <T> List<T> values(Class<T> cls) {
    return new AbstractList<T>() {
      @Override
      public T get(int index) {
        return values().get(cls, index);
      }

      @Override
      public int size() {
        return AbstractSeries.this.size();
      }
    };
  }

  @Override
  public IntStream intStream() {
    return values().stream().map(o -> Convert.to(Number.class, o)).mapToInt(Number::intValue);
  }

  @Override
  public DoubleStream doubleStream() {
    return values().stream().map(o -> Convert.to(Number.class, o)).mapToDouble(Number::doubleValue);
  }

  @Override
  public <T, R, C> R collect(Class<T> in, Collector<? super T, C, R> collector) {
    C accumulator = collector.supplier().get();
    for (int i = 0; i < size(); i++) {
      collector.accumulator().accept(accumulator, values().get(in, i));
    }
    return collector.finisher().apply(accumulator);
  }

  @Override
  public <R> R collect(Collector<? super Object, ?, R> collector) {
    return collect(getType().getDataClass(), collector);
  }

  @Override
  public <T> boolean allMatch(Class<T> cls, Predicate<? super T> predicate) {
    Storage getter = values();
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
      if (predicate.test(values().get(cls, i))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Storage values() {
    Storage lg;
    return (lg = storage) == null ? storage = new StorageImpl() : lg;
  }

  @Override
  public Builder newBuilder() {
    return getType().newBuilder();
  }

  @Override
  public Builder newBuilder(int size) {
    return getType().newBuilder(size);
  }

  // /**
  // * Return the value at the intrinsic position.
  // *
  // * @param index the intrinsic position
  // * @return the value
  // */
  // protected abstract Object getElement(int index);
  //
  // /**
  // * Return the value at the intrinsic position.
  // *
  // * @param i the intrinsic position
  // * @return the value
  // */
  // protected abstract double getDoubleElement(int i);
  //
  // /**
  // * Return the value at the intrinsic position.
  // *
  // * @param i the intrinsic position
  // * @return the value
  // */
  // protected abstract int getIntElement(int i);
  //
  // /**
  // * Set the specified element at the specified location in the series. The default implementation
  // * throws <tt>UnsupportedOperationExceptuon</tt>.
  // *
  // * @param index the index (in the underlying storage container)
  // * @param value the value
  // */
  // protected void setElement(int index, Object value) {
  // throw new UnsupportedOperationException();
  // }
  //
  // protected void addElement(Object value) {
  // throw new UnsupportedOperationException();
  // }
  //
  // protected void setDoubleElement(int index, double value) {
  // setElement(index, value);
  // }
  //
  // protected void setIntElement(int index, int value) {
  // setElement(index, value);
  // }

  protected <T> Series merge(Class<? extends T> cls, Series other,
      BiFunction<? super T, ? super T, ?> combiner, Builder builder) {
    Index thisIndex = index();
    Index otherIndex = Objects.requireNonNull(other, "require other series").index();
    if (otherIndex instanceof RangeIndex) {
      int size = Math.min(size(), other.size());
      for (int i = 0; i < size; i++) {
        builder.set(thisIndex.get(i),
            combiner.apply(values().get(cls, i), other.values().get(cls, i)));
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
    return size() == 0;
  }

  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      private final Iterator<Object> keys = index().iterator();
      private Object lastKey = null;

      @Override
      public void remove() {

      }

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
  public int hashCode() {
    int result = 1;
    for (int i = 0, size = size(); i < size; i++) {
      Object o = values().get(Object.class, i);
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
    if (!index().equals(that.index())) {
      return false;
    }
    for (Object key : index().keySet()) {
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

  private final class StorageImpl extends AbstractStorage {
    @Override
    public Object get(int index) {
      return AbstractSeries.this.get(index().get(index));
    }

    @Override
    public Object set(int index, Object element) {
      Object oldValue = get(index);
      AbstractSeries.this.set(index().get(index), element);
      return oldValue;
    }

    @Override
    public int size() {
      return AbstractSeries.this.size();
    }
  }
}
