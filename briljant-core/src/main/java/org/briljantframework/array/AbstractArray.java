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
package org.briljantframework.array;

import java.util.*;
import java.util.Arrays;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.util.sort.QuickSort;

/**
 * Provide a skeletal implementation of an {@link Array} to minimize the effort required to
 * implement.
 *
 * @author Isak Karlsson
 */
public abstract class AbstractArray<T> extends AbstractBaseArray<Array<T>> implements Array<T> {

  protected AbstractArray(ArrayBackend backend, int[] shape) {
    super(backend, shape);
  }

  protected AbstractArray(ArrayBackend backend, int offset, int[] shape, int[] stride) {
    super(backend, offset, shape, stride);
  }

  @Override
  public void setFrom(int toIndex, Array<T> from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int toRow, int toColumn, Array<T> from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public void setFrom(int[] toIndex, Array<T> from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int[] toIndex, Array<T> from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int toIndex, Array<T> from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }


  @Override
  public Array<T> copy() {
    Array<T> array = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, get(i));
    }
    return array;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Array) {
      Array<?> o = (Array<?>) obj;
      if (!Arrays.equals(shape, o.getShape())) {
        return false;
      }
      for (int i = 0; i < size(); i++) {
        if (!Objects.equals(get(i), o.get(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return ArrayPrinter.toString(this);
  }

  @Override
  public void assign(T value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
  }

  @Override
  public void assign(Supplier<T> supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.get());
    }
  }

  @Override
  public <U> void assign(Array<U> other, Function<? super U, ? extends T> operator) {
    org.briljantframework.array.Arrays.broadcast(this).with(other, (t, o) -> {
      for (int i = 0, size = o.size(); i < size; i++) {
        t.set(i, operator.apply(o.get(i)));
      }
    });
  }

  @Override
  public DoubleArray mapToDouble(ToDoubleFunction<? super T> f) {
    DoubleArray array = getArrayBackend().getArrayFactory().newDoubleArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, f.applyAsDouble(get(i)));
    }
    return array;
  }

  @Override
  public LongArray mapToLong(ToLongFunction<? super T> f) {
    LongArray array = getArrayBackend().getArrayFactory().newLongArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, f.applyAsLong(get(i)));
    }
    return array;
  }

  @Override
  public IntArray mapToInt(ToIntFunction<? super T> f) {
    IntArray array = getArrayBackend().getArrayFactory().newIntArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, f.applyAsInt(get(i)));
    }
    return array;
  }

  @Override
  public ComplexArray mapToComplex(Function<? super T, Complex> f) {
    ComplexArray array = getArrayBackend().getArrayFactory().newComplexArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, f.apply(get(i)));
    }
    return array;
  }

  public BooleanArray mapToBoolean(Function<? super T, Boolean> f) {
    BooleanArray array = getArrayBackend().getArrayFactory().newBooleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, f.apply(get(i)));
    }
    return array;
  }

  @Override
  public <U> Array<U> map(Function<? super T, ? extends U> f) {
    Array<U> array = getArrayBackend().getArrayFactory().newArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, f.apply(get(i)));
    }
    return array;
  }

  @Override
  public void apply(UnaryOperator<T> operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(get(i)));
    }
  }

  @Override
  public Array<T> filter(Predicate<? super T> predicate) {
    List<T> elements = new ArrayList<>();
    for (int i = 0; i < size(); i++) {
      T v = get(i);
      if (predicate.test(v)) {
        elements.add(v);
      }
    }
    return convertToArray(elements);
  }

  @Override
  public BooleanArray where(Predicate<? super T> predicate) {
    BooleanArray array = getArrayBackend().getArrayFactory().newBooleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, predicate.test(get(i)));
    }
    return array;
  }

  @Override
  public BooleanArray where(Array<? extends T> other, BiPredicate<? super T, ? super T> predicate) {
    return org.briljantframework.array.Arrays.broadcast(this).combine(other, (x, y) -> {
      BooleanArray array = getArrayBackend().getArrayFactory().newBooleanArray(x.getShape());
      for (int i = 0; i < size(); i++) {
        array.set(i, predicate.test(x.get(i), y.get(i)));
      }
      return array;
    });
  }

  @Override
  public int indexOf(Object v) {
    if (v == null) {
      for (int i = 0; i < size(); i++) {
        if (get(i) == null) {
          return i;
        }
      }
    } else {
      for (int i = 0; i < size(); i++) {
        if (v.equals(get(i))) {
          return i;
        }
      }
    }
    return -1;
  }

  @Override
  public IntArray indexOf(int dim, Object v) {
    Check.argument(dim >= 0 && dim < dims(), INVALID_DIMENSION, dim, dims());
    int vectors = vectors(dim);
    IntArray out = getArrayBackend().getArrayFactory().newIntArray(vectors);
    for (int i = 0; i < vectors; i++) {
      out.set(i, getVector(dim, i).indexOf(v));
    }
    return out;
  }

  @Override
  public T reduce(T initial, BinaryOperator<T> accumulator) {
    for (int i = 0; i < size(); i++) {
      initial = accumulator.apply(initial, get(i));
    }
    return initial;
  }

  @Override
  public Array<T> reduceVector(int dim, Function<? super Array<T>, ? extends T> accumulator) {
    Check.argument(dim >= 0 && dim < dims(), INVALID_DIMENSION, dim, dims());
    Array<T> reduced = newEmptyArray(ArrayUtils.remove(getShape(), dim));
    int vectors = vectors(dim);
    for (int i = 0; i < vectors; i++) {
      T value = accumulator.apply(getVector(dim, i));
      reduced.set(i, value);
    }
    return reduced;
  }

  @Override
  public T get(int i) {
    Check.index(i, size());
    return getElement(StrideUtils.index(i, getOffset(), stride, shape));
  }

  @Override
  public void set(int i, T value) {
    Check.index(i, size());
    setElement(StrideUtils.index(i, getOffset(), stride, shape), value);
  }

  @Override
  public T get(int i, int j) {
    Check.index(i, rows(), j, columns());
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  @Override
  public void set(int i, int j, T value) {
    Check.index(i, rows(), j, columns());
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  @Override
  public T get(int... index) {
    Check.index(index, shape);
    return getElement(StrideUtils.index(index, getOffset(), stride));
  }

  @Override
  public void set(int[] index, T value) {
    Check.index(index, shape);
    setElement(StrideUtils.index(index, getOffset(), stride), value);
  }

  @Override
  public void set(BooleanArray array, T value) {
    Check.dimension(array, this);
    for (int i = 0; i < this.size(); i++) {
      this.set(i, array.get(i) ? value : this.get(i));
    }
  }

  @Override
  public Array<T> get(BooleanArray array) {
    Check.dimension(array, this);
    List<T> elements = new ArrayList<>();
    for (int i = 0; i < this.size(); i++) {
      if (array.get(i)) {
        elements.add(get(i));
      }
    }
    return convertToArray(elements);
  }

  /**
   * 
   * Converts a list to an array.
   *
   * @param l the list
   * @return an array (as created by {@link #newEmptyArray(int...)})
   */
  private Array<T> convertToArray(List<T> l) {
    Array<T> array = newEmptyArray(l.size());
    for (int i = 0; i < array.size(); i++) {
      array.set(i, l.get(i));
    }
    return array;
  }

  @Override
  public Stream<T> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Override
  public void sort(Comparator<? super T> comparator) {
    QuickSort.quickSort(0, size(), (i, j) -> comparator.compare(get(i), get(j)), this);
  }

  @Override
  public Stream<T> parallelStream() {
    return StreamSupport.stream(spliterator(), true);
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      public int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public T next() {
        if (current >= size()) {
          throw new NoSuchElementException();
        }
        return get(current++);
      }
    };
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean contains(Object o) {
    for (int i = 0; i < size(); i++) {
      if (Objects.equals(get(i), o)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Object[] toArray() {
    Object[] data = new Object[size()];
    for (int i = 0; i < size(); i++) {
      data[i] = get(i);
    }
    return data;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E[] toArray(E[] a) {
    int size = size();
    E[] r = a.length >= size ? a
        : (E[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
    for (int i = 0; i < size(); i++) {
      r[i] = (E) get(i);
    }
    return r;
  }

  @Override
  public final void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean add(T integer) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean addAll(Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  protected abstract void setElement(int i, T value);

  protected abstract T getElement(int i);

  @Override
  public void swap(int a, int b) {
    T tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }
}
