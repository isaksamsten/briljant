package org.briljantframework.array;

import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.complex.Complex;

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractArray<T> extends AbstractBaseArray<Array<T>> implements Array<T> {

  private final Comparator<T> comparator = null;

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
  public int compare(int a, int b) {
    return comparator.compare(get(a), get(b));
  }

  @Override
  public Array<T> slice(BitArray bits) {
    return null;
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
  public BitArray asBit() {
    return asBit(v -> (Boolean) v, v -> (T) v);
  }

  @Override
  public BitArray asBit(Function<? super T, Boolean> to, Function<Boolean, T> from) {
    return new AsBitArray(getArrayFactory(), getOffset(), getShape(), getStride(),
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
  public BitArray asBit(Function<? super T, Boolean> to) {
    return asBit(to, v -> {
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
      set(i, get(i));
    }
    return array;
  }

  @Override
  public BitArray lt(Array<T> other) {
    return satisfies(other, (a, b) -> comparator.compare(a, b) < 0);
  }

  @Override
  public BitArray gt(Array<T> other) {
    return satisfies(other, (a, b) -> comparator.compare(a, b) > 0);
  }

  @Override
  public BitArray eq(Array<T> other) {
    return satisfies(other, Object::equals);
  }

  @Override
  public BitArray lte(Array<T> other) {
    return satisfies(other, (a, b) -> comparator.compare(a, b) <= 0);
  }

  @Override
  public BitArray gte(Array<T> other) {
    return satisfies(other, (a, b) -> comparator.compare(a, b) >= 0);
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
  public BitArray satisfies(Predicate<T> predicate) {
    BitArray array = getArrayFactory().booleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, predicate.test(get(i)));
    }
    return array;
  }

  @Override
  public BitArray satisfies(Array<T> other, BiPredicate<T, T> predicate) {
    Check.shape(this, other);
    BitArray array = getArrayFactory().booleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, predicate.test(get(i), other.get(i)));
    }
    return array;
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
    return list().stream();
  }

  @Override
  public List<T> list() {
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
