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

package org.briljantframework.array;

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.data.index.ObjectComparator;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractArray<T> extends AbstractBaseArray<Array<T>>implements Array<T> {

  private final Comparator<T> comparator = ObjectComparator.getInstance();

  protected AbstractArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
  }

  protected AbstractArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public void set(int toIndex, Array<T> from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, Array<T> from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public void set(int[] toIndex, Array<T> from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public Array<T> slice(BooleanArray bits) {
    Check.size(this, bits);
    List<T> newData = new ArrayList<>();
    for (int i = 0; i < size(); i++) {
      if (bits.get(i)) {
        newData.add(get(i));
      }
    }

    @SuppressWarnings("unchecked")
    T[] arr = (T[]) new Object[newData.size()];
    return getArrayFactory().array(newData.toArray(arr));
  }

  @Override
  public int compare(int a, int b) {
    return comparator.compare(get(a), get(b));
  }

  /**
   * @return a view of {@code this} array as a {@linkplain org.briljantframework.array.DoubleArray}
   * @throws java.lang.ClassCastException if {@code T} is not {@linkplain Double}
   */
  @Override
  @SuppressWarnings("unchecked")
  public DoubleArray asDouble() {
    return asDouble(v -> (Double) v, v -> (T) Double.valueOf(v));
  }

  @Override
  public DoubleArray asDouble(ToDoubleFunction<? super T> to, DoubleFunction<T> from) {
    return new AsDoubleArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      protected void setElement(int i, double value) {
        AbstractArray.this.setElement(i, from.apply(value));
      }

      @Override
      protected double getElement(int i) {
        return to.applyAsDouble(AbstractArray.this.getElement(i));
      }

      @Override
      protected int elementSize() {
        return AbstractArray.this.elementSize();
      }
    };
  }

  /**
   * @return a view
   * @throws java.lang.ClassCastException if {@code T} is not {@linkplain Double}
   */
  @Override
  @SuppressWarnings("unchecked")
  public IntArray asInt() {
    return asInt(v -> (Integer) v, v -> (T) Integer.valueOf(v));
  }

  @Override
  public IntArray asInt(ToIntFunction<? super T> to, IntFunction<T> from) {
    return new AsIntArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      protected void setElement(int i, int value) {
        AbstractArray.this.setElement(i, from.apply(value));
      }

      @Override
      protected int getElement(int i) {
        return to.applyAsInt(AbstractArray.this.getElement(i));
      }

      @Override
      protected int elementSize() {
        return AbstractArray.this.elementSize();
      }
    };
  }

  @Override
  public IntArray asInt(ToIntFunction<? super T> to) {
    return asInt(to, v -> {
      throw new UnsupportedOperationException();
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public LongArray asLong() {
    return asLong(v -> (Long) v, v -> (T) Long.valueOf(v));
  }

  @Override
  public LongArray asLong(ToLongFunction<? super T> to, LongFunction<T> from) {
    return new AsLongArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      protected void setElement(int i, long value) {
        AbstractArray.this.setElement(i, from.apply(value));
      }

      @Override
      protected long getElement(int i) {
        return to.applyAsLong(AbstractArray.this.getElement(i));
      }

      @Override
      protected int elementSize() {
        return AbstractArray.this.elementSize();
      }
    };
  }

  @Override
  public LongArray asLong(ToLongFunction<? super T> to) {
    return asLong(to, v -> {
      throw new UnsupportedOperationException();
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public BooleanArray asBoolean() {
    return asBoolean(v -> (Boolean) v, v -> (T) v);
  }

  @Override
  public BooleanArray asBoolean(Function<? super T, Boolean> to, Function<Boolean, T> from) {
    return new AsBooleanArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      protected void setElement(int i, boolean value) {
        AbstractArray.this.setElement(i, from.apply(value));
      }

      @Override
      protected boolean getElement(int i) {
        return to.apply(AbstractArray.this.getElement(i));
      }

      @Override
      protected int elementSize() {
        return AbstractArray.this.elementSize();
      }
    };
  }

  @Override
  public BooleanArray asBoolean(Function<? super T, Boolean> to) {
    return asBoolean(to, v -> {
      throw new UnsupportedOperationException();
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public ComplexArray asComplex() {
    return asComplex(v -> (Complex) v, v -> (T) v);
  }

  @Override
  public ComplexArray asComplex(Function<? super T, Complex> to, Function<Complex, T> from) {
    return new AsComplexArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      protected void setElement(int i, Complex value) {
        AbstractArray.this.setElement(i, from.apply(value));
      }

      @Override
      protected Complex getElement(int i) {
        return to.apply(AbstractArray.this.getElement(i));
      }

      @Override
      protected int elementSize() {
        return AbstractArray.this.elementSize();
      }
    };
  }

  @Override
  public ComplexArray asComplex(Function<? super T, Complex> to) {
    return asComplex(to, v -> {
      throw new UnsupportedOperationException();
    });
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
        if (!get(i).equals(o.get(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public BooleanArray lt(Array<T> other) {
    return where(other, (a, b) -> comparator.compare(a, b) < 0);
  }

  @Override
  public BooleanArray gt(Array<T> other) {
    return where(other, (a, b) -> comparator.compare(a, b) > 0);
  }

  @Override
  public BooleanArray eq(Array<T> other) {
    return where(other, Object::equals);
  }

  @Override
  public BooleanArray lte(Array<T> other) {
    return where(other, (a, b) -> comparator.compare(a, b) <= 0);
  }

  @Override
  public BooleanArray gte(Array<T> other) {
    return where(other, (a, b) -> comparator.compare(a, b) >= 0);
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
    Check.size(this, other);
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(other.get(i)));
    }
  }

  @Override
  public DoubleArray mapToDouble(ToDoubleFunction<? super T> f) {
    DoubleArray array = getArrayFactory().doubleArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, f.applyAsDouble(get(i)));
    }
    return array;
  }

  @Override
  public LongArray mapToLong(ToLongFunction<? super T> f) {
    LongArray array = getArrayFactory().longArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, f.applyAsLong(get(i)));
    }
    return array;
  }

  @Override
  public IntArray mapToInt(ToIntFunction<? super T> f) {
    IntArray array = getArrayFactory().intArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, f.applyAsInt(get(i)));
    }
    return array;
  }

  @Override
  public ComplexArray mapToComplex(Function<? super T, Complex> f) {
    ComplexArray array = getArrayFactory().complexArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, f.apply(get(i)));
    }
    return array;
  }

  @Override
  public <U> Array<U> map(Function<? super T, ? extends U> f) {
    Array<U> array = getArrayFactory().referenceArray(getShape());
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
  public DoubleArray asDouble(ToDoubleFunction<? super T> to) {
    return asDouble(to, v -> {
      throw new UnsupportedOperationException();
    });
  }

  @Override
  public Array<T> filter(Predicate<T> predicate) {
    List<T> list = new ArrayList<>();
    for (int i = 0; i < size(); i++) {
      T v = get(i);
      if (predicate.test(v)) {
        list.add(v);
      }
    }
    Array<T> array = newEmptyArray(list.size());
    for (int i = 0; i < array.size(); i++) {
      array.set(i, list.get(i));
    }
    return array;
  }

  @Override
  public BooleanArray where(Predicate<T> predicate) {
    BooleanArray array = getArrayFactory().booleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, predicate.test(get(i)));
    }
    return array;
  }

  @Override
  public BooleanArray where(Array<T> other, BiPredicate<T, T> predicate) {
    Check.shape(this, other);
    BooleanArray array = getArrayFactory().booleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, predicate.test(get(i), other.get(i)));
    }
    return array;
  }

  @Override
  public T reduce(T initial, BinaryOperator<T> accumulator) {
    for (int i = 0; i < size(); i++) {
      initial = accumulator.apply(initial, get(i));
    }
    return initial;
  }

  @Override
  public Array<T> reduceVector(int dim, Function<? super Array<T>, T> accumulator) {
    Check.argument(dim < dims(), INVALID_DIMENSION, dim, dims());
    Array<T> reduced = newEmptyArray(Indexer.remove(getShape(), dim));
    int vectors = vectors(dim);
    for (int i = 0; i < vectors; i++) {
      T value = accumulator.apply(getVector(dim, i));
      reduced.set(i, value);
    }
    return reduced;
  }

  @Override
  public T get(int i) {
    return getElement(Indexer.linearized(i, getOffset(), stride, shape));
  }

  @Override
  public void set(int i, T value) {
    setElement(Indexer.linearized(i, getOffset(), stride, shape), value);
  }

  @Override
  public T get(int i, int j) {
    Check.state(isMatrix());
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  @Override
  public void set(int i, int j, T value) {
    Check.state(isMatrix());
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  @Override
  public T get(int... index) {
    Check.argument(index.length == dims());
    return getElement(Indexer.columnMajorStride(index, getOffset(), stride));
  }

  @Override
  public void set(int[] index, T value) {
    Check.argument(index.length == dims());
    setElement(Indexer.columnMajorStride(index, getOffset(), stride), value);
  }

  protected abstract T getElement(int i);

  protected abstract void setElement(int i, T value);

  @Override
  public Stream<T> stream() {
    return toList().stream();
  }

  @Override
  public List<T> toList() {
    return new AbstractList<T>() {

      @Override
      public T set(int index, T element) {
        T oldElement = get(index);
        AbstractArray.this.set(index, element);
        return oldElement;
      }

      @Override
      public T get(int index) {
        return AbstractArray.this.get(index);
      }

      @Override
      public int size() {
        return AbstractArray.this.size();
      }
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  public T[] data() {
    Object[] array = new Object[size()];
    for (int i = 0; i < array.length; i++) {
      array[i] = get(i);
    }
    return (T[]) array;
  }

  @Override
  public void swap(int a, int b) {
    T tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    try {
      ArrayPrinter.print(builder, this);
    } catch (IOException e) {
      return getClass().getSimpleName();
    }
    return builder.toString();
  }
}
